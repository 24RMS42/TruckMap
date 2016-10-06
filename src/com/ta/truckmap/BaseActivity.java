package com.ta.truckmap;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

public class BaseActivity extends FragmentActivity
{

	public static BaseActivity mBaseActivity;
	public static boolean activityVisible;
	AlertDialog mDialog;
	public static EasyTracker easyTracker = null;

	public BaseActivity getBaseActivity()
	{
		return mBaseActivity;
	}

	public static void setmActivityObject(BaseActivity mActivityObject)
	{
		BaseActivity.mBaseActivity = mActivityObject;
	}

	public BaseActivity()
	{
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		easyTracker = EasyTracker.getInstance(this);

		setmActivityObject(this);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		setActivityVisible(true);
		setmActivityObject(this);
	}

	public static void createEvent(String action, String name, String id, String value)
	{
		easyTracker.send(MapBuilder.createEvent(action, name, id, null).build());
	}

	@Override
	protected void onPause()
	{

		super.onPause();
		setActivityVisible(false);
	}

	public static void setActivityVisible(boolean activityVisible)
	{
		BaseActivity.activityVisible = activityVisible;
	}

	public void setMessage(String[] message)
	{
		try
		{
			setPushMessage(message, mBaseActivity);
			clearNotificationStatus();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	void setPushMessage(String[] strings, Context context)
	{
		try
		{
			parseMessage(strings, context);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	void parseMessage(String[] message, Context context)
	{
		if (message.length > 0)
		{
			createCustomOKPushDialog(message, context, context.getAssets());
		}
	}

	void createCustomOKPushDialog(final String[] message, final Context context, AssetManager mAssetManager)
	{

		showMsgDialog(message[0], context);

	}

	void showMsgDialog(final String msg, final Context context)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getBaseActivity())
		//Utility.setSharedPrefStringData(this, "notification", realMsg);
				.setMessage(msg).setIcon(R.drawable.ic_launcher).setCancelable(false).setPositiveButton(this.getResources().getString(R.string.ok), new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int whichButton)
					{
						Intent notificationIntent = null;
						if (context instanceof HomeActivity && !(msg.contains("event") || msg.contains("Event") || msg.contains("Bussiness") || msg.contains("Birthday") || msg.contains("birthday")))
						{
							((HomeActivity) context).refreshMap();

						}
						else
							if (msg.contains("event") || msg.contains("Event") || msg.contains("Bussiness"))
							{
								notificationIntent = new Intent(getBaseActivity(), EventsActivity.class);
								notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(notificationIntent);

							}
							else
								if (msg.contains("Birthday") || msg.contains("birthday"))
								{

								}
								else
								{
									notificationIntent = new Intent(getBaseActivity(), NotificationActivity.class);
									notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									startActivity(notificationIntent);
								}

						dialog.dismiss();

					}
				});
		mDialog = builder.create();

		if (!isFinishing())
		{
			mDialog.show();
		}

	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		// TODO Auto-generated method stub
		if (mDialog != null && mDialog.isShowing())
		{
			mDialog.dismiss();
		}
	}

	@Override
	public void onStart()
	{
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	public void onStop()
	{
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);
	}

	public void clearNotificationStatus()
	{
		try
		{
			NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			nm.cancelAll();
		}
		catch (Exception e)
		{
		}
	}

	public static boolean isActivityVisible()
	{
		return activityVisible;
	}

	public static void activityResumed()
	{
		activityVisible = true;
	}

	public static void activityPaused()
	{
		activityVisible = false;
	}

}
