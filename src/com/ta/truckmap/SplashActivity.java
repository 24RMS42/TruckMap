package com.ta.truckmap;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.ta.truckmap.rest.api.APIUtils;
import com.ta.truckmap.util.Utility;
import com.truckmap.parsers.LoginParser;
import com.truckmap.parsers.Parser;

public class SplashActivity extends Activity
{
	public static String regId = "";
	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	String SENDER_ID = "634494442335";
	/**
	 * Tag used on log messages.
	 */
	static final String TAG = "GCM Demo";

	TextView mDisplay;
	GoogleCloudMessaging gcm;
	AtomicInteger msgId = new AtomicInteger();
	Handler mLoginHandler = new Handler();
	private Parser<?> parser;
	Bundle mNotificationDataBundle;
	Context mContext;
	String mNotificationId = "", mNotificationType = "";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Crashlytics.start(this);
		setContentView(R.layout.splash);
		mContext = this;
		if (getIntent().getExtras() != null)
		{
			mNotificationDataBundle = getIntent().getExtras();
			mNotificationId = mNotificationDataBundle.getString("id");
			mNotificationType = mNotificationDataBundle.getString("type");
		}
		// Check device for Play Services APK. If check succeeds, proceed with GCM registration.

		if (checkPlayServices())
		{
			gcm = GoogleCloudMessaging.getInstance(this);
			regId = getRegistrationId(this);
			Log.e("Registration Id", "is blank:" + regId);
			if (regId.isEmpty())
			{
				registerInBackground();

				Log.e("Registration Id", regId);
			}
			else
			{
				Utility.setSharedPrefStringData(SplashActivity.this, "regId", regId);
			}
		}
		else
		{
			Log.i(TAG, "No valid Google Play Services APK found.");
		}

	}

	private boolean checkPlayServices()
	{
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS)
		{
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
			{
				GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
			}
			else
			{
				Log.i(TAG, "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}

	private String getRegistrationId(Context context)
	{
		final SharedPreferences prefs = getGcmPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.length() > 0)
		{
			Log.i(TAG, "Registration not found.");
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(SplashActivity.this);
		if (registeredVersion != currentVersion)
		{
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context)
	{
		try
		{
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		}
		catch (NameNotFoundException e)
		{
			// should never happenNotificationFragmentActivity
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and the app versionCode in the application's
	 * shared preferences.
	 */
	private void registerInBackground()
	{
		new AsyncTask<Void, Void, String>()
		{
			@Override
			protected String doInBackground(Void... params)
			{
				String msg = "";
				try
				{
					if (gcm == null)
					{
						gcm = GoogleCloudMessaging.getInstance(SplashActivity.this);
					}

					while (regId.isEmpty())
					{
						regId = gcm.register(SENDER_ID);
					}

					msg = "Device registered, registration ID=" + regId;

					Log.e("msg", "is:" + msg);
					// You should send the registration ID to your server over HTTP, so it
					// can use GCM/HTTP or CCS to send messages to your app.
					sendRegistrationIdToBackend();

					// For this demo: we don't need to send it because the device will send
					// upstream messages to a server that echo back the message using the
					// 'from' address in the message.

					// Persist the regID - no need to register again.
					storeRegistrationId(SplashActivity.this, regId);
				}
				catch (IOException ex)
				{
					msg = "Error :" + ex.getMessage();
					// If there is an error, don't just keep trying to register.
					// Require the user to click a button again, or perform
					// exponential back-off.
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg)
			{
				if (!isTaskRoot())
				{
					finish();
					return;
				}
				else
				{
					getSpalshCheck();
				}

				if (!regId.isEmpty())
					Utility.setSharedPrefStringData(SplashActivity.this, "regId", regId);

			}

		}.execute(null, null, null);
	}

	private void sendRegistrationIdToBackend()
	{
		// Your implementation here. sent the device token to server for identify
	}

	/**
	 * Stores the registration ID and the app versionCode in the application's
	 * {@code SharedPreferences}.
	 *
	 * @param context application's context.
	 * @param regId registration ID
	 */
	private void storeRegistrationId(Context context, String regId)
	{
		final SharedPreferences prefs = getGcmPreferences(context);
		int appVersion = getAppVersion(context);
		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}

	private void getSpalshCheck()
	{
		new Handler().postDelayed(new Runnable()
		{

			@Override
			public void run()
			{
				if (Utility.isNetworkAvailable(SplashActivity.this))
				{

					if (!Utility.getSharedPrefStringData(SplashActivity.this, "username").equals(""))
					{

						getLogin(Utility.getSharedPrefStringData(SplashActivity.this, "username"), Utility.getSharedPrefStringData(SplashActivity.this, "password"));

					}

					else
					{
						Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
						startActivity(intent);
						finish();
					}
				}
				else
				{
					Utility.showToastMessage(SplashActivity.this, getResources().getString(R.string.msg_netork_error));
				}
			}
		}, 3000);
	}

	private void getLogin(final String username, final String password)
	{

		new Thread(new Runnable()
		{

			String response = "";

			@Override
			public void run()
			{

				JSONObject loginJsonObject = new JSONObject();
				try
				{

					loginJsonObject.put("Email", username);
					loginJsonObject.put("Password", password);
					loginJsonObject.put("DeviceToken", SplashActivity.regId);

				}
				catch (JSONException e)
				{
					e.printStackTrace();
				}
				Log.e("params for first", loginJsonObject.toString());
				String url = APIUtils.BASE_URL + APIUtils.LOGIN;
				response = Utility.POST(url, loginJsonObject.toString(), APIUtils.LOGIN);
				Log.e("response", response.toString());
				mLoginHandler.post(new Runnable()
				{

					private String msg;

					@Override
					public void run()
					{

						if (response != null && response.length() > 0)
						{
							Log.e("msg", "is:" + response);

							parser = new LoginParser();
							try
							{
								parser.parse(new JSONObject(response));
							}
							catch (JSONException e1)
							{
								e1.printStackTrace();
							}

							try
							{
								JSONObject responseJsonObject = new JSONObject(response);
								if (responseJsonObject.optBoolean("Success"))
								{
									msg = responseJsonObject.optString("Message");
									// via vikash
									Utility.setResponseDataInshrdPref(mContext, responseJsonObject);

									Utility.setSharedPrefStringData(SplashActivity.this, "IsLogin", "true");

									if (mNotificationId.length() > 0 && mNotificationId != null)
									{
										Intent intent = new Intent(SplashActivity.this, NotificationActivity.class);
										intent.putExtra("NotificationId", mNotificationId);
										intent.putExtra("Type", mNotificationType);
										intent.putExtra("pushcall", "pushcall");
										startActivity(intent);
										finish();
									}
									else
									{

										Intent startProfileActivity = new Intent(SplashActivity.this, HomeActivity.class);
										startActivity(startProfileActivity);
										finish();
									}

								}
								else
								{
									msg = responseJsonObject.optString("Message");
									Utility.showMsgDialog(SplashActivity.this, msg);
								}

							}
							catch (JSONException e)
							{
								e.printStackTrace();
							}

						}

					}
				});

			}
		}).start();

	}

	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGcmPreferences(Context context)
	{
		// This sample app persists the registration ID in shared preferences, but
		// how you store the regID in your app is up to you.
		return getSharedPreferences(SplashActivity.class.getSimpleName(), Context.MODE_PRIVATE);
	}

}
