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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ta.truckmap.rest.api.APIUtils;
import com.ta.truckmap.util.CustomProgressDialog;
import com.ta.truckmap.util.LoadAdViewInEveryActivity;
import com.ta.truckmap.util.Utility;
import com.truckmap.parsers.Parser;

public class NotificationActivity extends BaseActivity implements OnClickListener
{
	TextView mNotificationLogoTextView, mNotificationUserNameTextView, mNotificationAboutTripTextView;
	ImageView mNotificationProfilePic_ImgView, mNotificationBackImgView;
	RelativeLayout mNotificationRelativeLayout;
	ListView mNotificationListView;
	Handler mNotificationListHandler = new Handler();
	Parser<?> parser;
	private NotificationListAdapter mListAdapter;
	// contacts JSONArray
	JSONArray getNotificationListJsonArray = null;
	LinearLayout mAddMobsLinearLayout;
	CustomProgressDialog mCustomProgressDialog;
	// Hashmap for ListView
	List<HashMap<String, String>> mNotificationListMaps = new ArrayList<HashMap<String, String>>();

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification_ui);
		initiViewReference();
		getNotificattionListData();
	}

	private void getNotificattionListData()
	{

		mCustomProgressDialog = new CustomProgressDialog(NotificationActivity.this, "");
		mCustomProgressDialog.setCancelable(false);
		new Thread(new Runnable()
		{

			String response = "";

			@Override
			public void run()
			{

				JSONObject FinishTripJsonObject = new JSONObject();
				try
				{
					FinishTripJsonObject.put("UserId", Utility.getSharedPrefString(NotificationActivity.this, "userId"));
				}
				catch (JSONException e)
				{
					e.printStackTrace();
				}
				Log.e("params for first", FinishTripJsonObject.toString());
				String url = APIUtils.BASE_URL + APIUtils.NOTIFICATIONS;
				response = Utility.POST(url, FinishTripJsonObject.toString(), APIUtils.NOTIFICATIONS);
				Log.e("response", response.toString());

				mNotificationListHandler.post(new Runnable()
				{

					private String msg;

					@Override
					public void run()
					{
						mCustomProgressDialog.dismissDialog();
						if (response != null && response.length() > 0)
						{
							Log.e("msg", "is:" + response);

							try
							{
								JSONObject responseJsonObject = new JSONObject(response);
								if (responseJsonObject.optBoolean("Success"))
								{
									msg = responseJsonObject.optString("Message");
									getNotificationListJsonArray = responseJsonObject.getJSONArray("Result");

									for (int i = 0; i < getNotificationListJsonArray.length(); i++)
									{
										JSONObject c = getNotificationListJsonArray.getJSONObject(i);

										String notificationId = c.getString("NotificationId");
										String type = c.getString("Type");
										String userName = c.getString("UserName");
										String userImageUrl = c.optString("UserImageUrl");
										String comment = c.getString("Comment");
										String catLoc = c.getString("CatLoc");
										HashMap<String, String> notifications = new HashMap<String, String>();
										notifications.put("NotificationId", notificationId);
										notifications.put("Type", type);
										notifications.put("UserName", userName);
										notifications.put("UserImageUrl", userImageUrl);
										notifications.put("Comment", comment);
										notifications.put("CatLoc", catLoc);
										mNotificationListMaps.add(notifications);

									}
									if (mListAdapter == null)
									{
										mListAdapter = new NotificationListAdapter(NotificationActivity.this, mNotificationListMaps);
										mNotificationListView.setAdapter(mListAdapter);
									}
									else
									{
										mListAdapter.notifyDataSetChanged();
									}
								}
								else
								{
									msg = responseJsonObject.optString("Message");
									Utility.showMsgDialog(NotificationActivity.this, msg);
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
		Utility.textViewFontRobotoLight(mNotificationAboutTripTextView, getAssets());
		Utility.textViewFontRobotoLight(mNotificationLogoTextView, getAssets());
		Utility.textViewFontRobotoLight(mNotificationUserNameTextView, getAssets());
	}

	private void initiViewReference()
	{
		mNotificationListView = (ListView) findViewById(R.id.notification_list_view);

		mNotificationLogoTextView = (TextView) findViewById(R.id.notification_ui_Logo_tv);
		mNotificationBackImgView = (ImageView) findViewById(R.id.notific_ui_backImgview);

		mNotificationBackImgView.setOnClickListener(this);
		mAddMobsLinearLayout = (LinearLayout) findViewById(R.id.add_mobs_lnrlyt);
		LoadAdViewInEveryActivity.loadAdds(this, mAddMobsLinearLayout);
	}

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub

		if (v == mNotificationBackImgView)
		{

			finish();
		}

		else
			if (v == mNotificationRelativeLayout)
			{

				startActivity(new Intent(NotificationActivity.this, NotificationsDetail.class));
				finish();

			}

	}
}
