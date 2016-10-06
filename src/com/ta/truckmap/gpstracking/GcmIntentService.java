/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ta.truckmap.gpstracking;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.ta.truckmap.BaseActivity;
import com.ta.truckmap.R;
import com.ta.truckmap.SplashActivity;
import com.ta.truckmap.util.Utility;

public class GcmIntentService extends IntentService
{
	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;
	String realMsg;
	String myMsg = "";
	private Handler mBaseActivityPushHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			if (BaseActivity.mBaseActivity != null)
			{
				BaseActivity.mBaseActivity.setMessage(new String[] { myMsg });
			}
		}
	};

	public GcmIntentService()
	{
		super("GcmIntentService");
	}

	public static final String TAG = "MoBia";

	@Override
	protected void onHandleIntent(Intent intent)
	{
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty())
		{
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType))
			{
				sendNotification("Send error: " + extras.toString());
			}
			else
				if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType))
				{
					sendNotification("Deleted messages on server: " + extras.toString());
					// If it's a regular GCM message, do some work.
				}
				else
					if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType))
					{
						// This loop represents the service doing some work.

						for (int i = 0; i < 1; i++)
						{
							Log.i(TAG, "Working... " + (i + 1) + "/5 @ " + SystemClock.elapsedRealtime());
							try
							{
								Thread.sleep(5000);
							}
							catch (InterruptedException e)
							{
							}
						}

						Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
						// Post notification of received message.

						realMsg = extras.getString("payload");

						/*if (Utility.getSharedPrefStringData(getApplicationContext(), "driverrtype").equalsIgnoreCase("1"))
						{*/
						displayMsg(realMsg);
						/*}*/

						Log.i(TAG, "Received: " + extras.toString());

					}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void displayMsg(String extras)
	{
		try
		{
			JSONObject jsonObject = new JSONObject(extras.toString());
			Log.e("my message", jsonObject.optString("message"));
			myMsg = jsonObject.optString("message");
			sendNotification(jsonObject.optString("message"), jsonObject.optString("id"), jsonObject.optString("type"));
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}

	}

	private void sendNotification(String msg)
	{
		mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.appicon).setContentTitle("TruckMap")
				.setStyle(new NotificationCompat.BigTextStyle().bigText(msg)).setContentText(msg);

		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

	}

	private void sendNotification(String msg, String id, String type)
	{
		if (Utility.getSharedPrefStringData(getApplicationContext(), "IsLogin").equalsIgnoreCase("true"))
		{

			if (BaseActivity.mBaseActivity != null && BaseActivity.isActivityVisible())
			{
				mBaseActivityPushHandler.sendEmptyMessage(0);
			}
			else
			{

				startT2TIntent(id, type);

			}
			vibrateDevice();

		}
	}

	private void vibrateDevice()
	{
	}

	private void startT2TIntent(String id, String type)
	{
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Intent pushIntent = startUserCreatePushIntent(id, type);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, pushIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		Notification notification = createNotificationConstraints(contentIntent, 1);
		nm.notify(TAG, (NOTIFICATION_ID), notification);

	}

	private Intent startUserCreatePushIntent(String id, String type)
	{
		Utility.setSharedPrefBooleanData(getApplicationContext(), "isNotification", true);
		Utility.setSharedPrefStringData(getApplicationContext(), "notification", myMsg);
		Intent pushIntent = new Intent(getApplicationContext(), SplashActivity.class);
		pushIntent.putExtra("id", id);
		pushIntent.putExtra("type", type);

		pushIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		return pushIntent;
	}

	@SuppressWarnings("deprecation")
	private Notification createNotificationConstraints(PendingIntent contentIntent, int type)
	{
		Notification notification = new Notification(R.drawable.appicon, TAG, System.currentTimeMillis());
		notification.setLatestEventInfo(this, getResources().getString(R.string.app_name), myMsg, contentIntent);
		notification.defaults |= Notification.DEFAULT_SOUND;
		long[] vibrate = { 0, 100, 200, 300 };
		notification.vibrate = vibrate;
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		return notification;
	}

}
