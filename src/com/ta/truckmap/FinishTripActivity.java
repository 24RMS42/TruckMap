package com.ta.truckmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.ta.truckmap.rest.api.APIUtils;
import com.ta.truckmap.util.Constant;
import com.ta.truckmap.util.CustomProgressDialog;
import com.ta.truckmap.util.LoadAdViewInEveryActivity;
import com.ta.truckmap.util.Utility;
import com.truckmap.model.FinishedTripDataBean;
import com.truckmap.parsers.FinishTripParser;
import com.truckmap.parsers.Parser;

public class FinishTripActivity extends BaseActivity implements OnItemClickListener, OnClickListener
{
	ImageView mfinishTripBackImgView;
	TextView mfinishTripNameTextView, mfinishTripDestinationTextView, mfinishTripDestination2TextView;
	ListView mfinishTripListView;

	String url = APIUtils.BASE_URL + APIUtils.NEWTRIP;
	LinearLayout mAddMobsLinearLayout;
	CustomProgressDialog customProgressDialog;
	// JSON Node names
	private static final String NAME = "Name";
	private static final String START_LONGITUDE = "StartLongitude";
	private static final String START_LATITUDE = "StartLatitude";
	private static final String END_LONGITUDE = "EndLongitude";
	private static final String END_LATITUDE = "EndLatitude";
	Handler mFinishTripHandler = new Handler();
	Parser<?> parser;
	// contacts JSONArray
	JSONArray getTrip = null;

	// Hashmap for ListView
	List<HashMap<String, String>> mTripList = new ArrayList<HashMap<String, String>>();

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.finishtrip_ui);
		initiViewReference();
		getfinishTripData();
		setFontFamily();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		setActivityVisible(true);
	}

	@Override
	protected void onPause()
	{

		super.onPause();
		setActivityVisible(false);
	}

	public void getfinishTripData()
	{

		customProgressDialog = new CustomProgressDialog(FinishTripActivity.this, "Please wait");
		customProgressDialog.setCancelable(false);
		new Thread(new Runnable()
		{

			String response = "";

			@Override
			public void run()
			{

				JSONObject FinishTripJsonObject = new JSONObject();
				try
				{
					FinishTripJsonObject.put("UserId", Utility.getSharedPrefString(FinishTripActivity.this, "userId"));
					FinishTripJsonObject.put("DeviceToken", SplashActivity.regId);
				}
				catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				Log.e("params for first", FinishTripJsonObject.toString());
				String url = APIUtils.BASE_URL + APIUtils.EARLIESTTRIP;
				response = Utility.POST(url, FinishTripJsonObject.toString(), APIUtils.EARLIESTTRIP);
				Log.e("response", response.toString());

				mFinishTripHandler.post(new Runnable()
				{

					private String msg;

					@Override
					public void run()
					{
						customProgressDialog.dismissDialog();
						if (response != null && response.length() > 0)
						{
							Log.e("msg", "is:" + response);

							parser = new FinishTripParser();

							try
							{
								Constant.mFinishedTripDataBean = (FinishedTripDataBean) parser.parse(response);
								JSONObject responseJsonObject = new JSONObject(response);
								if (responseJsonObject.optBoolean("Success"))
								{
									msg = responseJsonObject.optString("Message");
									getTrip = responseJsonObject.getJSONArray("Result");

									for (int i = 0; i < getTrip.length(); i++)
									{
										JSONObject c = getTrip.getJSONObject(i);

										String tripName = c.getString(NAME);
										String startPointName = c.getString("StartPointName");
										String endPointName = c.getString("EndPointName");

										String otherDestination = c.getString("EndFirstPointName");
										String isRootOk = c.getString("isRootOk");
										String importentInfo = c.getString("extraInformation");

										/*if(c.getString("EndFirstPointName")!=null && c.getString("EndFirstPointName").length()>0){
											endPointName= endPointName+" - "+c.getString("EndFirstPointName");
										}*/

										String comment_FinishTrip = "";
										if (c.getString("Comment").equals("null") || c.getString("Comment").equals(""))
										{
											comment_FinishTrip = "Comment Not Entered";
										}
										else
										{
											comment_FinishTrip = c.getString("Comment");
										}
										HashMap<String, String> trips = new HashMap<String, String>();

										trips.put(NAME, tripName);
										trips.put("StartPointName", startPointName);
										trips.put("EndPointName", endPointName);
										trips.put("Comment", comment_FinishTrip);

										trips.put("otherDestination", otherDestination);
										trips.put("isRootOk", isRootOk);
										trips.put("importentInfo", importentInfo);

										mTripList.add(trips);

										String[] from = new String[] { NAME, "EndPointName", "StartPointName", "Comment", "isRootOk", "importentInfo" };

										int[] to = { R.id.finishTrip_ui_TripName, R.id.finishTrip_ui_tv_destinationname, R.id.finishTrip_ui_tv_sourcename, R.id.TextView02,
												R.id.finishTrip_ui_tv_isrout_ok, R.id.finishTrip_ui_tv_importentinfo };

										SimpleAdapter adapter = new SimpleAdapter(FinishTripActivity.this, mTripList, R.layout.finishtriprow, from, to);
										mfinishTripListView.setAdapter(adapter);

									}
								}
								else
								{
									msg = responseJsonObject.optString("Message");
									Utility.showMsgDialog(FinishTripActivity.this, msg);
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

	private void setFontFamily()
	{
	}

	private void initiViewReference()
	{
		mfinishTripBackImgView = (ImageView) findViewById(R.id.finishTrip_ui_backImgView);
		mfinishTripListView = (ListView) findViewById(R.id.listv);

		mfinishTripBackImgView.setOnClickListener(this);
		mfinishTripListView.setOnItemClickListener(this);
		mAddMobsLinearLayout = (LinearLayout) findViewById(R.id.add_mobs_lnrlyt);
		LoadAdViewInEveryActivity.loadAdds(this, mAddMobsLinearLayout);

	}

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub

		if (v == mfinishTripBackImgView)
		{
			this.finish();
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View arg1, int position, long id)
	{
		Intent mapFinishIntent = new Intent(FinishTripActivity.this, MapScreenFinish.class);
		mapFinishIntent.putExtra("position", position);
		startActivity(mapFinishIntent);

	}
}