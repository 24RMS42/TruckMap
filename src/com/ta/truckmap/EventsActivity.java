package com.ta.truckmap;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ta.truckmap.adapter.EventsListAdapter;
import com.ta.truckmap.bean.EventBean;
import com.ta.truckmap.rest.api.APIUtils;
import com.ta.truckmap.util.Constant;
import com.ta.truckmap.util.CustomProgressDialog;
import com.ta.truckmap.util.Utility;

public class EventsActivity extends BaseActivity implements OnClickListener
{

	private ImageView mAddEventImageView;
	private TextView mBackTextView;
	private PullToRefreshListView mEventList;
	private EventsListAdapter mListAdapter;

	AsyncTask<Void, Void, String> eventListAsynTask;
	private CustomProgressDialog mCustomProgressDialog;
	private Handler mEventListHandler = new Handler();
	private final int REQUEST_CODE_ADD_EVENT = 1000;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.events_activity);
		initUI();
		callEventListService();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		//callEventListService();
	}

	void initUI()
	{
		mAddEventImageView = (ImageView) findViewById(R.id.event_activity_addnew_imageview);
		mBackTextView = (TextView) findViewById(R.id.event_activity_back_textview);
		mEventList = (PullToRefreshListView) findViewById(R.id.event_activity_listview);
		mEventList.setMode(Mode.PULL_UP_TO_REFRESH); // mode refresh for top and bottom
		mEventList.setShowIndicator(false); //disable indicator
		mEventList.setPullLabel("Loading");
		mEventList.setOnRefreshListener(new OnRefreshListener<ListView>()
		{

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView)
			{
				Log.e("setOnRefreshListener...", "onRefresh...");
				callEventListService();

			}
		});

		mEventList.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				//position start from 1
				System.out.println("clicked on item " + arg2);

				//EventBean event =(EventBean)arg0.getItemAtPosition(arg2);
				//System.out.println(event.getEventname());
				Intent intent = new Intent(EventsActivity.this, EventDetails.class);
				intent.putExtra("position", arg2 - 1);
				startActivity(intent);

			}
		});

		mBackTextView.setOnClickListener(this);
		mAddEventImageView.setOnClickListener(this);

		fillListData();
	}

	private void fillListData()
	{
		mEventList.onRefreshComplete();
		if (mListAdapter == null)
		{
			mListAdapter = new EventsListAdapter(this);
			mEventList.setAdapter(mListAdapter);
		}
		else
		{
			//mListAdapter.mEventList=eventList;
			mListAdapter.notifyDataSetChanged();
		}

	}

	@Override
	public void onClick(View v)
	{
		if (v == mBackTextView)
		{
			finish();
		}
		else
			if (v == mAddEventImageView)
			{

				getPopEventAdd();

			}

	}

	private void getPopEventAdd()
	{

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EventsActivity.this);

		alertDialogBuilder.setTitle(" " + this.getResources().getString(R.string.app_name));
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setMessage("Create Business Add or Event");

		// set positive button: Yes message
		alertDialogBuilder.setPositiveButton("Business Add", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				// go to a new activity of the app

				Intent intent = new Intent(EventsActivity.this, AddNewEventActivity.class);
				intent.putExtra("type", "2");// 2 for bussiness add 
				startActivityForResult(intent, REQUEST_CODE_ADD_EVENT);
			}
		});

		// set negative button: No message
		alertDialogBuilder.setNegativeButton("Event", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{

				Intent intent = new Intent(EventsActivity.this, AddNewEventActivity.class);
				intent.putExtra("type", "1");//1 for normal events
				startActivityForResult(intent, REQUEST_CODE_ADD_EVENT);

			}
		});

		AlertDialog alertDialog = alertDialogBuilder.create();
		// show alert
		alertDialog.show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_ADD_EVENT && resultCode == RESULT_OK)
		{
			callEventListService();
		}

	}

	void callEventListService()
	{
		mCustomProgressDialog = new CustomProgressDialog(EventsActivity.this, "Please wait..");
		mCustomProgressDialog.setCancelable(false);
		new Thread(new Runnable()
		{

			String response = "";

			@Override
			public void run()
			{

				JSONObject evntlistJsonObject = new JSONObject();
				//				try {
				//
				//					loginJsonObject.put("Email", mlogin_email.getText()
				//							.toString().trim());
				//					loginJsonObject.put("Password", mlogin_password.getText()
				//							.toString().trim());
				//					loginJsonObject.put("DeviceToken", SplashActivity.regId);

				//				} catch (JSONException e) {
				//					// TODO Auto-generated catch block
				//					e.printStackTrace();
				//				}
				//				Log.e("params for first", loginJsonObject.toString());
				String url = APIUtils.BASE_URL + APIUtils.GET_EVENT_LIST;
				response = Utility.POST(url, evntlistJsonObject.toString(), APIUtils.LOGIN);
				Log.e("response", response.toString());
				mEventListHandler.post(new Runnable()
				{

					private String msg = "", success = "";
					private JSONObject responseJson = null;

					@Override
					public void run()
					{
						mCustomProgressDialog.dismissDialog();

						if (response != null && response.length() > 0)
						{
							//Log.e("msg", "response:" + response);
							try
							{
								responseJson = new JSONObject(response);
								success = responseJson.getString("Success");
								msg = responseJson.getString("Message");
								if (success.equalsIgnoreCase("true"))
								{
									JSONArray result = responseJson.getJSONArray("Result");
									parseResultJson(result);
									fillListData();
								}

							}
							catch (JSONException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}

					}
				});

			}
		}).start();

	}

	private void parseResultJson(JSONArray result)
	{
		Constant.EVENTLIST.clear();
		for (int i = 0; i < result.length(); i++)
		{
			try
			{
				//System.out.println("In parser");
				JSONObject eventjson = result.getJSONObject(i);
				EventBean event = new EventBean();
				event.setEventname(eventjson.getString("eventname"));
				event.setEventId(eventjson.getString("eventId"));

				//System.out.println("Eventid:- "+eventjson.getString("eventId"));

				event.setDetails(eventjson.getString("details"));
				event.setPlace(eventjson.getString("place"));
				event.setStarttime(eventjson.getString("starttime"));
				event.setEndtime(eventjson.getString("endtime"));
				event.setDate(eventjson.getString("date"));
				event.setmEnddate(eventjson.optString("enddate"));
				event.setUserId(eventjson.getString("userId"));
				event.setmType(eventjson.getString("type"));

				//System.out.println("date:- "+eventjson.getString("date"));

				JSONArray jsonArrayImage = eventjson.getJSONArray("image");
				for (int j = 0; j < jsonArrayImage.length(); j++)
				{
					event.addImagePathInList(jsonArrayImage.getString(j));
					//event.addImagePathInList("http://staging10.techaheadcorp.com/ecourier/Images/Profile/de6d4b0e7e1a47e083736b29f33115a9.jpg");
				}

				JSONArray jsonArrayVideo = eventjson.getJSONArray("video");
				//ArrayList<Map<String, String>> videoList= new ArrayList<Map<String,String>>();
				for (int j = 0; j < jsonArrayVideo.length(); j++)
				{
					JSONObject videoObject = jsonArrayVideo.getJSONObject(j);
					Map<String, String> map = new HashMap<String, String>();
					map.put("videofile", videoObject.getString("videoFileName"));
					map.put("thumbnail", videoObject.getString("thumbnailFileName"));
					//videoList.add(map);
					event.addVideoPathInList(map);
				}

				JSONArray jsonArrayDoc = eventjson.getJSONArray("doc");
				for (int j = 0; j < jsonArrayDoc.length(); j++)
				{
					event.addDocPathInList(jsonArrayDoc.getString(j));
				}
				Constant.EVENTLIST.add(event);

			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

		}

	}

}
