package com.ta.truckmap.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.ta.truckmap.R;

public class Utility
{
	public static DecimalFormat mDecimalFormat = new DecimalFormat("#.##");
	public static Handler mHandler = new Handler();
	public static int posstion = 0;

	public final static DisplayImageOptions getFitXYDisplayOption()
	{
		return new DisplayImageOptions.Builder().showStubImage(R.drawable.profile_placeholder).showImageForEmptyUri(R.drawable.profile_placeholder).showImageOnFail(R.drawable.profile_placeholder)
				.bitmapConfig(Config.RGB_565).cacheInMemory(true).cacheOnDisc(true).displayer(new SimpleBitmapDisplayer()).imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();
	}

	/*
	 * public static String getElapsedTimeInMin(long ms) { return
	 * String.format("%d", TimeUnit.MILLISECONDS.toMinutes(ms)); }
	 * 
	 * public static String getElapsedTimeInSec(long ms) { return String.format(
	 * "%d", TimeUnit.MILLISECONDS.toSeconds(ms) -
	 * TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS .toMinutes(ms)));
	 * 
	 * }
	 * 
	 * public static String getElapsedTimeInMinSec(long ms) { return
	 * String.format( "%d.%d", TimeUnit.MILLISECONDS.toMinutes(ms),
	 * TimeUnit.MILLISECONDS.toSeconds(ms) -
	 * TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS .toMinutes(ms))); }
	 */

	public static LatLng getLastElement(final Collection c)
	{
		final Iterator itr = c.iterator();
		LatLng lastElement = (LatLng) itr.next();
		while (itr.hasNext())
		{
			lastElement = (LatLng) itr.next();
		}
		return lastElement;
	}

	public static ImageLoadingListener getImageLoaderListner(final Context context)
	{
		ImageLoadingListener imageLoadingListener;
		final CustomProgressDialog mCustomProgressDialog = new CustomProgressDialog(context, "");
		mCustomProgressDialog.setCancelable(false);

		imageLoadingListener = new ImageLoadingListener()
		{

			@Override
			public void onLoadingStarted(String imageUri, View view)
			{

			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason)
			{

				Toast.makeText(context, "Loading failed due to " + failReason, Toast.LENGTH_SHORT).show();
				mCustomProgressDialog.dismissDialog();

			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
			{
				//view.setImportantForAccessibility(mode)
				System.out.println("Image Loaded");
				mCustomProgressDialog.dismissDialog();

			}

			@Override
			public void onLoadingCancelled(String imageUri, View view)
			{
				Toast.makeText(context, "Canceled", Toast.LENGTH_SHORT).show();
				mCustomProgressDialog.dismissDialog();

			}
		};

		return imageLoadingListener;

	}

	public static ImageLoader getImageLoader(Context context)
	{
		ImageLoader imageLoader = ImageLoader.getInstance();
		if (imageLoader.isInited())
			return imageLoader;
		else
		{
			File cacheDir = new File(context.getCacheDir(), "FitnessApp");
			if (!cacheDir.exists())
				cacheDir.mkdir();

			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).threadPoolSize(1).threadPriority(1).memoryCacheSize(1048576 * 5)
			// 1.5 Mb
					.discCacheSize(50000000)
					// 50 Mb
					.discCache(new UnlimitedDiscCache(cacheDir)).denyCacheImageMultipleSizesInMemory().build();
			imageLoader.init(config);
			return imageLoader;
		}
	}

	public static void setCascadeAnimation(final View view, long duration)
	{
		AnimationSet set = new AnimationSet(true);

		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(duration);
		set.addAnimation(animation);
		animation.setAnimationListener(new AnimationListener()
		{

			@Override
			public void onAnimationStart(Animation animation)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation)
			{
				// TODO Auto-generated method stub

			}
		});

		animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		animation.setDuration(100);
		set.addAnimation(animation);

		LayoutAnimationController controller = new LayoutAnimationController(set, 0.5f);
		((ViewGroup) view).setLayoutAnimation(controller);

	}

	public static final String getLocalTimeFromUTC(String utcDateTime)
	{

		if (utcDateTime.length() > 0)
		{
			DateFormat utcFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");
			utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			Date date;
			try
			{
				date = utcFormat.parse(utcDateTime);

				DateFormat format = new SimpleDateFormat("hh:mm aa");
				format.setTimeZone(TimeZone.getDefault());
				Date convertedDate = format.parse(format.format(date));
				//System.out.println(pstFormat.format(date));

				return format.format(convertedDate);
			}
			catch (Exception e)
			{
				Log.e("exception", " " + e);
			}
		}
		return "";
	}

	public static void setAlphaAnimation(final View view, long duration)
	{
		AnimationSet set = new AnimationSet(true);

		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(duration);
		set.addAnimation(animation);
		animation.setAnimationListener(new AnimationListener()
		{

			@Override
			public void onAnimationStart(Animation animation)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation)
			{
				// TODO Auto-generated method stub

			}
		});

		LayoutAnimationController controller = new LayoutAnimationController(set, 0.3f);
		((ViewGroup) view).setLayoutAnimation(controller);
	}

	public static void animateHeaderToSlideUp(final View view)
	{
		Animation animation = new TranslateAnimation(0, 0, 0, -(view.getHeight()));
		animation.setDuration(500);
		animation.setFillAfter(true);
		animation.setAnimationListener(new AnimationListener()
		{

			@Override
			public void onAnimationStart(Animation animation)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation)
			{
				// TODO Auto-generated method stub
				view.setVisibility(View.GONE);
			}
		});
		view.startAnimation(animation);

	}

	public static JSONObject getLocationInfo(double lat, double lng)
	{

		HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?latlng=" + lat + "," + lng + "&sensor=true");
		HttpClient client = new DefaultHttpClient();
		HttpResponse response;
		StringBuilder stringBuilder = new StringBuilder();

		try
		{
			response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();
			InputStream stream = entity.getContent();
			int b;
			while ((b = stream.read()) != -1)
			{
				stringBuilder.append((char) b);
			}
		}
		catch (ClientProtocolException e)
		{
		}
		catch (IOException e)
		{
		}

		JSONObject jsonObject = new JSONObject();
		try
		{
			jsonObject = new JSONObject(stringBuilder.toString());
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return jsonObject;
	}

	public static JSONObject getLatLngFromAddress(String address)
	{

		HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?address=" + address + "&sensor=true");
		HttpClient client = new DefaultHttpClient();
		HttpResponse response;
		StringBuilder stringBuilder = new StringBuilder();

		try
		{
			response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();
			InputStream stream = entity.getContent();
			int b;
			while ((b = stream.read()) != -1)
			{
				stringBuilder.append((char) b);
			}
		}
		catch (ClientProtocolException e)
		{
		}
		catch (IOException e)
		{
		}

		JSONObject jsonObject = new JSONObject();
		try
		{
			jsonObject = new JSONObject(stringBuilder.toString());
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return jsonObject;
	}

	public static String getFileExt(String FileName)
	{
		return FileName.substring((FileName.lastIndexOf(".") + 1), FileName.length());
	}

	public static void animateFooterSlideDown(final View view)
	{

		Animation animation = new TranslateAnimation(0, 0, 0, view.getHeight());
		animation.setDuration(500);
		animation.setFillAfter(true);
		animation.setAnimationListener(new AnimationListener()
		{

			@Override
			public void onAnimationStart(Animation animation)
			{
				// TODO Auto-generated method stub

				view.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation)
			{
				// TODO Auto-generated method stub

			}
		});
		view.startAnimation(animation);

	}

	public static final String LocalToUTC(String localDateTime)
	{

		if (localDateTime.length() > 0)
		{
			//		       "CreatedOn":,"2013-11-21 12:14:39"));
			DateFormat localFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			//localFormat.setTimeZone(TimeZone.getDefault());
			Date date;
			try
			{
				date = localFormat.parse(localDateTime);
				DateFormat pstFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
				pstFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

				return pstFormat.format(date);
				//
			}
			catch (Exception e)
			{
				System.out.print(e);
			}
		}
		return "";
	}

	public static void animateFooterSlideUp(final View view)
	{

		Animation animation = new TranslateAnimation(0, 0, view.getHeight(), 0);
		animation.setDuration(500);
		animation.setFillAfter(true);
		animation.setAnimationListener(new AnimationListener()
		{

			@Override
			public void onAnimationStart(Animation animation)
			{
				// TODO Auto-generated method stub
			}

			@Override
			public void onAnimationRepeat(Animation animation)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation)
			{
				// TODO Auto-generated method stub

				view.setVisibility(View.GONE);

			}
		});
		view.startAnimation(animation);

	}

	public static void animateHeaderToSlideDown(final View view)
	{
		Animation animation = new TranslateAnimation(0, 0, -(view.getHeight()), 0);
		animation.setDuration(500);
		animation.setFillAfter(true);
		animation.setAnimationListener(new AnimationListener()
		{

			@Override
			public void onAnimationStart(Animation animation)
			{
				// TODO Auto-generated method stub
				view.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation)
			{
				// TODO Auto-generated method stub

			}
		});
		view.startAnimation(animation);

	}

	@TargetApi(11)
	public static void enableStrictMode()
	{
		if (Utility.hasGingerbread())
		{
			StrictMode.ThreadPolicy.Builder threadPolicyBuilder = new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog();
			StrictMode.VmPolicy.Builder vmPolicyBuilder = new StrictMode.VmPolicy.Builder().detectAll().penaltyLog();

			if (Utility.hasHoneycomb())
			{
			}
			StrictMode.setThreadPolicy(threadPolicyBuilder.build());
			StrictMode.setVmPolicy(vmPolicyBuilder.build());
		}
	}

	public static boolean hasFroyo()
	{
		// Can use static final constants like FROYO, declared in later versions
		// of the OS since they are inlined at compile time. This is guaranteed
		// behavior.
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
	}

	public static boolean hasGingerbread()
	{
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
	}

	public static boolean hasHoneycomb()
	{
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	public static boolean hasHoneycombMR1()
	{
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
	}

	/*
	 * public static boolean hasJellyBean() { return Build.VERSION.SDK_INT >=
	 * Build.VERSION_CODES.JELLY_BEAN; }
	 */

	public static int copy(InputStream input, OutputStream output) throws IOException
	{
		byte[] buffer = new byte[1024];
		int count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer)))
		{
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	// public static boolean isEmailValid(String email)
	// {
	// return Constants.EMAIL_ADDRESS_PATTERN.matcher(email).matches();
	// }

	/**
	 * Method for checking empty input fields
	 * 
	 * @param field
	 * @return
	 */
	public static boolean isFieldEmpty(String field)
	{
		if (field.trim().length() > 0)
		{
			return false;
		}
		return true;
	}

	/**
	 * Method for checking valid email-id fields
	 * 
	 * @param mail
	 * @return
	 */
	public static boolean isEmailValid(String mail)
	{
		String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		Pattern p = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);

		Matcher m = p.matcher(mail);
		if (m.matches() && mail.trim().length() > 0)
		{
			return true;
		}
		return false;
	}

	@SuppressWarnings("resource")
	public static void copyFile(File sourceFile, File destFile) throws IOException
	{
		if (!sourceFile.exists())
		{
			return;
		}
		FileChannel source = null;
		FileChannel destination = null;
		source = new FileInputStream(sourceFile).getChannel();
		destination = new FileOutputStream(destFile).getChannel();
		if (destination != null && source != null)
		{
			destination.transferFrom(source, 0, source.size());
		}
		if (source != null)
		{
			// Toast.makeText(context, "deleted "+sourceFile.getPath(),
			// 1).show();
			source.close();
		}
		if (destination != null)
		{
			destination.close();
		}
	}

	public static String getFromAssets(Context context, String fileName)
	{
		try
		{
			InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
			BufferedReader bufReader = new BufferedReader(inputReader);
			String line = "";
			String Result = "";
			while ((line = bufReader.readLine()) != null)
			{
				if (line.trim().equals(""))
					continue;
				Result += line + "\r\n";
			}
			return Result;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return "";
	}

	public static boolean isPlayerServiceRunning(Context context)
	{
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		// for (RunningServiceInfo service :
		// manager.getRunningServices(Integer.MAX_VALUE))
		// {
		// if (MPService.class.getName().equals(service.service.getClassName()))
		// {
		// return true;
		// }
		// }
		return false;
	}

	public static void setWindowFlagPref(Context context)
	{

		// ((Activity) context).getWindow().addFlags(
		// WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
		// WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

		((Activity) context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
	}

	public static String getDeviceId(Context context)
	{
		String str = null;
		str = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		return str;
	}

	public static long getSharedPrefLongData(Context context, String key)
	{

		SharedPreferences userAcountPreference = context.getSharedPreferences(com.ta.truckmap.util.Constant.SHARED_PREF_NAME, Context.MODE_MULTI_PROCESS);

		return userAcountPreference.getLong(key, 0l);

	}

	public static boolean getSharedPrefBooleanData(Context context, String key)
	{

		SharedPreferences userAcountPreference = context.getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_MULTI_PROCESS);

		return userAcountPreference.getBoolean(key, false);

	}

	public static void setSharedPrefBooleanData(Context context, String key, boolean value)
	{
		SharedPreferences appInstallInfoSharedPref = context.getSharedPreferences(Constant.SHARED_PREF_NAME, context.MODE_MULTI_PROCESS);
		Editor appInstallInfoEditor = appInstallInfoSharedPref.edit();
		appInstallInfoEditor.putBoolean(key, value);
		appInstallInfoEditor.commit();
	}

	public static void setSharedPrefLongData(Context context, String key, long value)
	{
		SharedPreferences appInstallInfoSharedPref = context.getSharedPreferences(Constant.SHARED_PREF_NAME, context.MODE_MULTI_PROCESS);
		Editor appInstallInfoEditor = appInstallInfoSharedPref.edit();
		appInstallInfoEditor.putLong(key, value);
		appInstallInfoEditor.commit();
	}

	public static int getSharedPrefIntData(Context context, String key)
	{

		SharedPreferences userAcountPreference = context.getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_MULTI_PROCESS);

		return userAcountPreference.getInt(key, 0);

	}

	public static void setSharedPrefIntData(Context context, String key, int value)
	{
		SharedPreferences appInstallInfoSharedPref = context.getSharedPreferences(Constant.SHARED_PREF_NAME, context.MODE_MULTI_PROCESS);
		Editor appInstallInfoEditor = appInstallInfoSharedPref.edit();
		appInstallInfoEditor.putInt(key, value);
		appInstallInfoEditor.commit();
	}

	public static String getSharedPrefString(Context context, String key)
	{

		SharedPreferences userAcountPreference = context.getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_MULTI_PROCESS);

		return userAcountPreference.getString(key, "");

	}

	public static String getSharedPrefLoginData(Context context, String key)
	{

		SharedPreferences userAcountPreference = context.getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_MULTI_PROCESS);

		return userAcountPreference.getString(key, "");

	}

	public static void setSharedPrefAddressStringData(Context context, String key, String value)
	{
		SharedPreferences appInstallInfoSharedPref = context.getSharedPreferences(Constant.SHARED_PREF_NAME, context.MODE_MULTI_PROCESS);
		Editor appInstallInfoEditor = appInstallInfoSharedPref.edit();
		appInstallInfoEditor.putString(key, value);
		appInstallInfoEditor.commit();
	}

	public static void setSharedPrefPaymentStringData(Context context, String key, String value)
	{
		SharedPreferences appInstallInfoSharedPref = context.getSharedPreferences(Constant.SHARED_PREF_NAME, context.MODE_MULTI_PROCESS);
		Editor appInstallInfoEditor = appInstallInfoSharedPref.edit();
		appInstallInfoEditor.putString(key, value);
		appInstallInfoEditor.commit();
	}

	public static String getSharedPrefAddressStringData(Context context, String key)
	{

		SharedPreferences userAcountPreference = context.getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_MULTI_PROCESS);

		return userAcountPreference.getString(key, "");

	}

	public static void setSharedPrefStringData(Context context, String key, String value)
	{
		SharedPreferences appInstallInfoSharedPref = context.getSharedPreferences(Constant.SHARED_PREF_NAME, context.MODE_MULTI_PROCESS);
		Editor appInstallInfoEditor = appInstallInfoSharedPref.edit();
		appInstallInfoEditor.putString(key, value);
		appInstallInfoEditor.commit();
	}

	public static String getSharedPrefStringData(Context context, String key)
	{

		SharedPreferences userAcountPreference = context.getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_MULTI_PROCESS);

		return userAcountPreference.getString(key, "");

	}

	// save arraylist<object>

	public static void SaveArraylistObject(ArrayList<LatLng> mRoutListLatLngsCurrentLine, String name, Context mcContext)
	{

		ArrayList<LatLng> test = new ArrayList<LatLng>();
		test = mRoutListLatLngsCurrentLine;
		SharedPreferences appInstallInfoSharedPref = mcContext.getSharedPreferences(Constant.SHARED_PREF_NAME, mcContext.MODE_MULTI_PROCESS);
		Editor appInstallInfoEditor = appInstallInfoSharedPref.edit();
		Gson gson = new Gson();

		Log.e("object array", "is:" + gson.toJson(test));
		appInstallInfoEditor.putString(name, gson.toJson(test));
		appInstallInfoEditor.commit();

	}

	//load arraylist<object>

	public static String LoadArrayListObject(String arrayName, Context mContext)
	{
		SharedPreferences appInstallInfoSharedPref = mContext.getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_MULTI_PROCESS);

		return appInstallInfoSharedPref.getString(arrayName, "");
	}

	public static boolean saveArrayList(ArrayList<LatLng> array, String arrayName, Context mContext)
	{
		SharedPreferences prefs = mContext.getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_MULTI_PROCESS);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(arrayName + "_size", array.size());
		for (int i = 0; i < array.size(); i++)
			editor.putString(arrayName + "_" + i, "" + array.get(i));
		return editor.commit();
	}

	public static String[] loadArrayList(String arrayName, Context mContext)
	{
		SharedPreferences prefs = mContext.getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_MULTI_PROCESS);
		int size = prefs.getInt(arrayName + "_size", 0);
		String array[] = new String[size];
		for (int i = 0; i < size; i++)
			array[i] = prefs.getString(arrayName + "_" + i, null);
		return array;
	}

	// for array

	public static boolean saveArray(String[] array, String arrayName, Context mContext)
	{
		SharedPreferences prefs = mContext.getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_MULTI_PROCESS);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(arrayName + "_size", array.length);
		for (int i = 0; i < array.length; i++)
			editor.putString(arrayName + "_" + i, array[i]);
		return editor.commit();
	}

	public static String[] loadArray(String arrayName, Context mContext)
	{
		SharedPreferences prefs = mContext.getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_MULTI_PROCESS);
		int size = prefs.getInt(arrayName + "_size", 0);
		String array[] = new String[size];
		for (int i = 0; i < size; i++)
			array[i] = prefs.getString(arrayName + "_" + i, null);
		return array;
	}

	public static String getSharedPrefPaymentStringData(Context context, String key)
	{

		SharedPreferences userAcountPreference = context.getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_MULTI_PROCESS);

		return userAcountPreference.getString(key, "");

	}

	public static void removeSharedPrefStringData(Context context, String key)
	{

		SharedPreferences appInstallInfoSharedPref = context.getSharedPreferences(Constant.SHARED_PREF_NAME, context.MODE_MULTI_PROCESS);

		Editor appInstallInfoEditor = appInstallInfoSharedPref.edit();
		appInstallInfoEditor.remove(key);
		appInstallInfoEditor.commit();

	}

	public static void setSharedPreStringData(Context context, String key, String value)
	{
		SharedPreferences appInstallInfoSharedPref = context.getSharedPreferences(Constant.SHARED_PREF_NAME, context.MODE_MULTI_PROCESS);

		Editor appInstallInfoEditor = appInstallInfoSharedPref.edit();
		appInstallInfoEditor.putString(key, value);
		appInstallInfoEditor.commit();
	}

	public static void clearSharedPrefData(Context context, String key)
	{
		SharedPreferences appInstallInfoSharedPref = context.getSharedPreferences(Constant.SHARED_PREF_NAME, context.MODE_MULTI_PROCESS);
		Editor appInstallInfoEditor = appInstallInfoSharedPref.edit();
		appInstallInfoEditor.remove(key);
		appInstallInfoEditor.commit();
	}

	public static boolean isNetworkAvailable(Context context)
	{
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED
				|| connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTING)
		{
			return true;
		}
		else
			if (connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
					|| connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTING)
			{

				return true;
			}
			else
				return false;

	}

	public static void CopyStream(InputStream is, OutputStream os)
	{
		final int buffer_size = 1024;
		try
		{
			byte[] bytes = new byte[buffer_size];
			for (;;)
			{
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		}
		catch (Exception ex)
		{

			/* Log.e("Exception", ex + ""); */

		}
	}

	public static Bitmap decodeFile(File f)
	{
		try
		{
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, options);

			final int REQUIRED_SIZE = 300;

			// Find the correct scale value. It should be the power of 2.
			int width_tmp = options.outWidth, height_tmp = options.outHeight;
			int scale = 1;
			while (true)
			{
				if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inPreferredConfig = Bitmap.Config.ARGB_8888;
			o2.inSampleSize = scale;
			// o2.inPurgeable = true;

			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] getBitmapToByteArray(Bitmap bitmap)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 100 /* ignored for PNG */, bos);
		return bos.toByteArray();
	}

	public static String getDateInFormatFromMilisecond(long timeInMs) throws Exception
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

		Date dateObj = new Date();
		dateObj.setTime(timeInMs);

		return dateFormat.format(dateObj);

	}

	public static String getTimeInFormatFromDTString(String date) throws Exception
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aaa");

		Date dateObj = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);

		return dateFormat.format(dateObj);

	}

	public static String getTimeInFormatLong(long date) throws Exception
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm aaa");

		// Date dateObj = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse();
		Date dateObj = new Date(date);
		return dateFormat.format(dateObj);

	}

	public static String getTimeInFormatFromMS(long ms) throws Exception
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm");

		Date dateObj = new Date(ms);

		return dateFormat.format(dateObj);

	}

	public static String getAMPMInFormatFromMS(long ms) throws Exception
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("a");

		Date dateObj = new Date(ms);

		return dateFormat.format(dateObj);

	}

	public static String getDayInFormatFromMS(long ms) throws Exception
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d");

		Date dateObj = new Date(ms);

		return dateFormat.format(dateObj);

	}

	public static String httpGetRequestToServer(String Url)
	{
		String content = null;
		/* Log.w("Url", Url + "  "); */

		HttpClient httpclient = null;
		try
		{
			// URLEncoder url =
			String urlStr = Url;
			URL url = new URL(urlStr);
			URI uri = null;
			try
			{
				uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), URLDecoder.decode(url.getQuery(), "UTF-8"), url.getRef());

				url = uri.toURL();

			}
			catch (URISyntaxException e)
			{

				e.printStackTrace();
				return "false";
			}

			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, Constant.CONNECTION_TIME_OUT);

			httpclient = new DefaultHttpClient(httpParameters);

			HttpResponse response = httpclient.execute(new HttpGet(uri));
			Log.w("uri", uri + " ");
			content = EntityUtils.toString(response.getEntity());
		}
		catch (ParseException e)
		{

			e.printStackTrace();
		}
		catch (IOException e)
		{

			e.printStackTrace();
		}
		finally
		{
			if (httpclient != null)
			{
				httpclient.getConnectionManager().shutdown();
			}
		}

		Log.w("response", content + " ");
		return content;
	}

	public static void showKeyboard(Context context)
	{
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null)
		{
			imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
		}
	}

	public static void hideKeyboard(Context context)
	{
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
		if (imm != null)
		{
			imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
		}
		// imm.hideSoftInputFromWindow(((Activity)
		// context).getCurrentFocus().getWindowToken(),
		// InputMethodManager.HIDE_NOT_ALWAYS);
		/*
		 * context.getWindow().setSoftInputMode(
		 * WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		 */
	}

	public static boolean isDebuggable(Context ctx)
	{
		boolean debuggable = false;

		PackageManager pm = ctx.getPackageManager();
		try
		{
			ApplicationInfo appinfo = pm.getApplicationInfo(ctx.getPackageName(), 0);
			debuggable = (0 != (appinfo.flags &= ApplicationInfo.FLAG_DEBUGGABLE));
		}
		catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}

		return debuggable;
	}

	public static String POST(String url, String paramsList, String serviceName)
	{
		InputStream inputStream = null;
		String result = "";
		try
		{
			Log.w("URL...", url + " .");
			Log.w("params", paramsList + " .");
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			httpPost.addHeader("Content-Type", "application/json");

			/*long timestamp = System.currentTimeMillis() / 1000;*/
			Calendar cal1 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			long timestamp = cal1.getTimeInMillis() / 1000;
			Log.e("timestamp...", timestamp + "");

			String authenticationKey = AESAlgo.Encrypt(serviceName + "_" + timestamp, "TruckApp_0314");
			httpPost.addHeader("Authentication", authenticationKey);
			httpPost.addHeader("version", "1");
			if (paramsList != null)
			{
				StringEntity se = new StringEntity(paramsList);
				se.setContentEncoding("UTF-8");
				// se.setContentType("application/x-www-form-urlencoded");
				httpPost.setEntity(se);
			}

			HttpResponse httpResponse = httpclient.execute(httpPost);
			inputStream = httpResponse.getEntity().getContent();

			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
			else
				result = "Did not work!";

		}
		catch (Exception e)
		{
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return result;
	}

	private static String convertInputStreamToString(InputStream inputStream) throws IOException
	{
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		return result;

	}

	public static String httpPostRequestToServer(String URL, Object paramsList)
	{

		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, Constant.CONNECTION_TIME_OUT);

		DefaultHttpClient client = new DefaultHttpClient(httpParameters);

		HttpPost httpPost = new HttpPost(URL);

		// StringEntity se = new StringEntity( jsonString);
		// post.setHeader("Content-type", "application/json");
		//
		// se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
		// "application/json"));
		// post.setEntity(se);
		// // response = client.execute(post);
		// httpresponse = client.execute(post);
		// response = EntityUtils.toString(httpresponse.getEntity()).trim();

		httpPost.addHeader("Content-Type", "application/json");

		Log.w("url", URL);
		try
		{
			if (paramsList != null)
			{
				Log.w("params", paramsList + " .");
				StringEntity entity = new StringEntity(paramsList.toString());

				entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
				httpPost.setEntity(entity);
			}
			HttpResponse response = client.execute(httpPost);

			String responseEntity = EntityUtils.toString(response.getEntity());
			responseEntity = responseEntity.trim();
			System.out.println(responseEntity + ".");
			Log.e("server resp:", responseEntity);
			return responseEntity;
		}
		catch (Exception e)
		{

			return null;
		}
		finally
		{
			if (client != null)
			{
				client.getConnectionManager().shutdown();
			}
		}
	}

	public static void makeCall(Context context, String number)
	{
		Intent intent = new Intent(Intent.ACTION_CALL);
		intent.setData(Uri.parse("tel:" + number.trim()));
		context.startActivity(intent);
	}

	public static void showErrorDialog(Context context, String title, String msg, final View mEdTextToFocus)
	{
		// title = context.getResources().getString(R.string.label_message);
		new AlertDialog.Builder(context).setTitle(title).setMessage(msg).setIcon(R.drawable.ic_launcher).setCancelable(false)
				.setPositiveButton(context.getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int whichButton)
					{
						dialog.dismiss();

						if (mEdTextToFocus != null && mEdTextToFocus instanceof EditText)
						{
							mEdTextToFocus.requestFocus();
						}
						else
						{
							// do nothing
						}
					}
				}).create().show();
	}

	public static int showCustomPopupList(Context mContext, final TextView v, final ArrayList<String> mCompany)
	{

		ArrayAdapter<String> adapter = new ArrayAdapter<String>((Activity) mContext, android.R.layout.select_dialog_item, mCompany);
		AlertDialog.Builder builder = new AlertDialog.Builder((Activity) mContext);
		builder.setTitle("Select Option");
		builder.setCancelable(true);
		builder.setAdapter(adapter, new DialogInterface.OnClickListener()
		{

			public void onClick(DialogInterface dialog, int arg2)
			{
				Log.e("poss", "is:" + arg2);
				v.setText(mCompany.get(arg2));
				posstion = arg2;

			}
		});
		final AlertDialog dialog = builder.create();
		dialog.show();

		return posstion;
	}

	public static void showMsgDialog(Context context, String msg)
	{
		new AlertDialog.Builder(context)

		.setMessage(msg).setIcon(R.drawable.ic_launcher).setCancelable(false).setPositiveButton(context.getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
				dialog.dismiss();

			}
		}).create().show();
	}

	public static void showMsgDialogForFinish(final Context context, String msg)
	{
		new AlertDialog.Builder(context)

		.setMessage(msg).setIcon(R.drawable.ic_launcher).setCancelable(false).setPositiveButton(context.getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
				dialog.dismiss();
				((Activity) context).finish();

			}
		}).create().show();
	}

	public static Animation expandCollapse(final View v, final boolean expand)
	{

		try
		{
			Method m = v.getClass().getDeclaredMethod("onMeasure", int.class, int.class);
			m.setAccessible(true);
			m.invoke(v, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(((View) v.getParent()).getMeasuredWidth(), MeasureSpec.AT_MOST));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		final int initialHeight = v.getMeasuredHeight();

		if (expand)
		{
			v.getLayoutParams().height = 0;
		}
		else
		{
			v.getLayoutParams().height = initialHeight;
		}
		v.setVisibility(View.VISIBLE);

		Animation a = new Animation()
		{
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t)
			{
				int newHeight = 0;
				if (expand)
				{
					newHeight = (int) (initialHeight * interpolatedTime);
				}
				else
				{
					newHeight = (int) (initialHeight * (1 - interpolatedTime));
				}
				v.getLayoutParams().height = newHeight;
				v.requestLayout();

				if (interpolatedTime == 1 && !expand)
					v.setVisibility(View.GONE);
			}

			@Override
			public boolean willChangeBounds()
			{
				return true;
			}
		};
		a.setDuration(400);
		return a;
	}

	public static void showMessageDevelopment(Context ctx)
	{
		Toast.makeText(ctx, "Under Development", Toast.LENGTH_SHORT).show();
	}

	public static String getUniqueSDCardId()
	{
		UUID sdCardId = UUID.randomUUID();
		return String.valueOf(sdCardId);
	}

	public static void showKey(Context instance, EditText user)
	{
		InputMethodManager imm = (InputMethodManager) instance.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
	}

	public static void hideKey(Context instance, EditText user)
	{
		InputMethodManager imm = (InputMethodManager) instance.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(user.getWindowToken(), 0);
	}

	public static String capitalizeSentence(String projectName)
	{
		char[] stringArray = projectName.toCharArray();
		stringArray[0] = Character.toUpperCase(stringArray[0]);
		projectName = new String(stringArray);
		return projectName;
	}

	public static Bitmap setProfileImageFromFacebookURL(String profileImgID)
	{
		URL img_value = null;
		try
		{
			img_value = new URL("http://graph.facebook.com/" + profileImgID + "/picture?type=large");
			Bitmap mIcon1 = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
			return mIcon1;
		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static final boolean isEmailValid(CharSequence email)
	{
		if (email == null)
		{
			return false;
		}
		else
		{
			return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
		}
	}

	public static final void showNetworkNotAvailToast(Context context)
	{
		Toast.makeText(context, context.getResources().getString(R.string.msg_netork_error), Toast.LENGTH_SHORT).show();

	}

	public static final void showToastMessage(Context context, String Message)
	{
		Toast.makeText(context, Message, Toast.LENGTH_SHORT).show();

	}

	public static Bitmap getImageFromURL(String profileImageURL2)
	{
		try
		{
			return BitmapFactory.decodeStream((InputStream) new URL(profileImageURL2).getContent());
		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// public static String saveOutput(Bitmap croppedImage, String fileName,
	// Context context)
	// {
	// File file = new File(context.getCacheDir(), fileName);
	// if (fileName != null)
	// {
	// OutputStream outputStream = null;
	// try
	// {
	// outputStream = new FileOutputStream(file);
	// if (outputStream != null)
	// {
	// croppedImage.compress(CompressFormat.PNG, 100, outputStream);
	// }
	// }
	// catch (IOException ex)
	// {
	//
	// return null;
	// }
	// finally
	// {
	// Util.closeSilently(outputStream);
	// }
	//
	// }
	// return file.getAbsolutePath();
	// }

	// shared preferences data for view cuisine selected list

	/*
	 * public static void setSharedViewStringData(Context context, String key,
	 * String value) { SharedPreferences selectedViewSharedPref =
	 * context.getSharedPreferences(Constants.SHARED_PREF_NAME,
	 * context.MODE_MULTI_PROCESS); Editor appInstallInfoEditor =
	 * selectedViewSharedPref.edit(); appInstallInfoEditor.putString(key,
	 * value); appInstallInfoEditor.commit(); }
	 * 
	 * public static String getSharedPrefViewStringData(Context context, String
	 * key) {
	 * 
	 * SharedPreferences userAcountPreference =
	 * context.getSharedPreferences(Constants.SHARED_PREF_NAME,
	 * Context.MODE_MULTI_PROCESS);
	 * 
	 * return userAcountPreference.getString(key, "");
	 * 
	 * }
	 */

	public static String getCacheFilePath(Context context)
	{
		File f = new File(context.getCacheDir(), System.currentTimeMillis() + ".png");

		return f.getAbsolutePath();

	}

	public static void translateAnimation(float fromXDelta, float toXDelta, final float fromYDelta, float toYDelta, final View view, final View mHeaderViewHidden)
	{
		TranslateAnimation anim = new TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);
		anim.setDuration(500);

		anim.setAnimationListener(new TranslateAnimation.AnimationListener()
		{

			@Override
			public void onAnimationStart(Animation animation)
			{
			}

			@Override
			public void onAnimationRepeat(Animation animation)
			{
			}

			@Override
			public void onAnimationEnd(Animation animation)
			{

			}
		});

		view.startAnimation(anim);
	}

	public static void textViewFontRobotoLight(final TextView textView, final AssetManager assetManager)
	{

		Typeface face = Typeface.createFromAsset(assetManager, "fonts/Roboto-Light.ttf");
		textView.setTypeface(face);

	}

	public static void textViewFontFuturastdMediumBook(final TextView textView, final AssetManager assetManager)
	{
		mHandler.post(new Runnable()
		{

			@Override
			public void run()
			{

				Typeface face = Typeface.createFromAsset(assetManager, "fonts/futurastdbook.ttf");
				textView.setTypeface(face);

			}
		});

	}

	public static void textViewFontArial(final TextView textView, final AssetManager assetManager, final String path)
	{
		mHandler.post(new Runnable()
		{

			@Override
			public void run()
			{

				Typeface face = Typeface.createFromAsset(assetManager, path);
				textView.setTypeface(face);

			}
		});

	}

	public static void scaleAnimation(float fromXDelta, float toXDelta, final float fromYDelta, float toYDelta, final View view, final View mHeaderViewHidden)
	{
		ScaleAnimation anim = new ScaleAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);

		anim.setDuration(500);
		anim.setFillEnabled(true);
		anim.setFillAfter(true);
		anim.setAnimationListener(new TranslateAnimation.AnimationListener()
		{

			@Override
			public void onAnimationStart(Animation animation)
			{
			}

			@Override
			public void onAnimationRepeat(Animation animation)
			{
			}

			@Override
			public void onAnimationEnd(Animation animation)
			{

				// view.setLayoutParams(params);
			}
		});

		view.startAnimation(anim);
	}

	// Method for sending files using multiparting......
	public static String sendJsonWithFile(String url, String imagePath, String jsonString, String serviceName)
	{
		//imagePath = "image1";
		Log.e("image path", imagePath + "--->");
		Log.e("URL", url);

		Log.e("json params", jsonString);
		String res = "";
		try
		{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);

			Calendar cal1 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			long timestamp = cal1.getTimeInMillis() / 1000;
			Log.e("timestamp...", timestamp + "");

			String authenticationKey = AESAlgo.Encrypt(serviceName + "_" + timestamp, "TruckApp_0314");
			httppost.addHeader("Authentication", authenticationKey);
			httppost.addHeader("version", "1");

			MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			if (imagePath != null && imagePath.length() > 0)
			{
				File imageFile = new File(imagePath);
				if (imageFile.exists())
				{
					FileBody filebodyImage = new FileBody(imageFile, "image/jpg");
					Log.e("file path=", filebodyImage.toString());
					reqEntity.addPart("image1", filebodyImage);
				}

			}

			reqEntity.addPart("json", new StringBody(jsonString));

			httppost.setEntity(reqEntity);
			Log.e("String Body", reqEntity.getContentType().toString());
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity resEntity = response.getEntity();
			if (resEntity != null)
			{
				res = EntityUtils.toString(resEntity);
				Log.e("Response", res);
				//System.out.println(res);

			}

			if (resEntity != null)
			{
				resEntity.consumeContent();
			}
			httpclient.getConnectionManager().shutdown();
		}
		catch (UnsupportedEncodingException e)
		{
			res = "UnsupportedEncodingException";
			e.printStackTrace();
		}
		catch (ClientProtocolException e)
		{
			res = "ClientProtocolException";
			e.printStackTrace();
		}
		catch (FileNotFoundException e)
		{
			res = "FileNotFoundException";
			e.printStackTrace();
		}
		catch (IOException e)
		{
			res = "IOException";
			e.printStackTrace();
		}
		catch (Exception e)
		{
			res = "Exception";
			e.printStackTrace();
		}
		if (res.contains("﻿"))
		{
			res = res.replace("﻿", "");
		}
		return res;
	}

	public static String sendJsonWithFile(String url, String imagePath, String video, String doc, String jsonString, String serviceName)
	{
		//imagePath = "image1";
		Log.e("image path", imagePath + "--->");

		Log.e("URL", url);
		String res = "";
		try
		{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);

			Calendar cal1 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			long timestamp = cal1.getTimeInMillis() / 1000;
			Log.e("timestamp...", timestamp + "");

			String authenticationKey = AESAlgo.Encrypt(serviceName + "_" + timestamp, "TruckApp_0314");
			httppost.addHeader("Authentication", authenticationKey);
			httppost.addHeader("version", "1");

			MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			if (imagePath != null && imagePath.length() > 0)
			{
				File imageFile = new File(imagePath);
				if (imageFile.exists())
				{
					FileBody filebodyImage = new FileBody(imageFile, "image/*");

					Log.e("image file path=", filebodyImage.toString());
					reqEntity.addPart("image", filebodyImage);
				}

			}

			if (video != null && video.length() > 0)
			{
				File videofile = new File(video);
				if (videofile.exists())
				{
					FileBody filebodyImage = new FileBody(videofile, "video/*");

					Log.e("video file path=", filebodyImage.toString());
					reqEntity.addPart("video", filebodyImage);
				}

			}

			if (doc != null && doc.length() > 0)
			{
				File docFile = new File(doc);
				if (docFile.exists())
				{
					FileBody filebodyImage = new FileBody(docFile, "application/*");

					Log.e("doc file path=", filebodyImage.toString());
					reqEntity.addPart("doc", filebodyImage);
				}

			}

			reqEntity.addPart("json", new StringBody(jsonString));

			Log.e("json params:\n", jsonString);

			httppost.setEntity(reqEntity);
			Log.e("String Body", reqEntity.getContentType().toString());
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity resEntity = response.getEntity();
			if (resEntity != null)
			{
				res = EntityUtils.toString(resEntity);
				Log.e("Response", res);
				//System.out.println(res);

			}

			if (resEntity != null)
			{
				resEntity.consumeContent();
			}
			httpclient.getConnectionManager().shutdown();
		}
		catch (UnsupportedEncodingException e)
		{
			res = "UnsupportedEncodingException";
			e.printStackTrace();
		}
		catch (ClientProtocolException e)
		{
			res = "ClientProtocolException";
			e.printStackTrace();
		}
		catch (FileNotFoundException e)
		{
			res = "FileNotFoundException";
			e.printStackTrace();
		}
		catch (IOException e)
		{
			res = "IOException";
			e.printStackTrace();
		}
		catch (Exception e)
		{
			res = "Exception";
			e.printStackTrace();
		}
		if (res.contains("﻿"))
		{
			res = res.replace("﻿", "");
		}
		return res;
	}

	public static String sendJsonWithFileOnShareScreen(String url, String imagePath, String video, String audiofile, String jsonString, String serviceName)
	{
		//imagePath = "image1";
		Log.e("image path", imagePath + "--->");

		Log.e("URL", url);
		String res = "";
		try
		{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);

			Calendar cal1 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			long timestamp = cal1.getTimeInMillis() / 1000;
			Log.e("timestamp...", timestamp + "");

			String authenticationKey = AESAlgo.Encrypt(serviceName + "_" + timestamp, "TruckApp_0314");
			httppost.addHeader("Authentication", authenticationKey);
			httppost.addHeader("version", "1");

			MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			if (imagePath != null && imagePath.length() > 0)
			{
				File imageFile = new File(imagePath);
				if (imageFile.exists())
				{
					FileBody filebodyImage = new FileBody(imageFile, "image/*");

					Log.e("image file path=", filebodyImage.toString());
					reqEntity.addPart("image", filebodyImage);
				}

			}

			if (video != null && video.length() > 0)
			{
				File videofile = new File(video);
				if (videofile.exists())
				{
					FileBody filebodyImage = new FileBody(videofile, "video/*");

					Log.e("video file path=", filebodyImage.toString());
					reqEntity.addPart("video", filebodyImage);
				}

			}

			if (audiofile != null && audiofile.length() > 0)
			{
				File audioFile = new File(audiofile);
				if (audioFile.exists())
				{
					FileBody filebody = new FileBody(audioFile, "audio/*");

					Log.e("audio file path=", filebody.toString());
					reqEntity.addPart("audio", filebody);
				}

			}

			reqEntity.addPart("json", new StringBody(jsonString));

			Log.e("json params:\n", jsonString);

			httppost.setEntity(reqEntity);
			Log.e("String Body", reqEntity.getContentType().toString());
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity resEntity = response.getEntity();
			if (resEntity != null)
			{
				res = EntityUtils.toString(resEntity);
				Log.e("Response", res);
				//System.out.println(res);

			}

			if (resEntity != null)
			{
				resEntity.consumeContent();
			}
			httpclient.getConnectionManager().shutdown();
		}
		catch (UnsupportedEncodingException e)
		{
			res = "UnsupportedEncodingException";
			e.printStackTrace();
		}
		catch (ClientProtocolException e)
		{
			res = "ClientProtocolException";
			e.printStackTrace();
		}
		catch (FileNotFoundException e)
		{
			res = "FileNotFoundException";
			e.printStackTrace();
		}
		catch (IOException e)
		{
			res = "IOException";
			e.printStackTrace();
		}
		catch (Exception e)
		{
			res = "Exception";
			e.printStackTrace();
		}
		if (res.contains("﻿"))
		{
			res = res.replace("﻿", "");
		}
		return res;
	}

	/**
	 * read text files saved in assets
	 * 
	 * @param ctx
	 * @param resourceName
	 * @return
	 * @throws IOException
	 */
	public static String readTextFileFromAssets(Context ctx, String resourceName) throws IOException
	{
		InputStream in = ctx.getAssets().open(resourceName);
		BufferedReader bin = new BufferedReader(new InputStreamReader(in));
		StringBuffer sb = new StringBuffer("");
		String line = "";
		String NL = System.getProperty("line.separator");
		while ((line = bin.readLine()) != null)
		{
			sb.append(line + NL);
		}
		in.close();
		return sb.toString();
	}

	public static Bitmap getCroppedBitmap(Bitmap bitmapOrg, int exifOrientation)
	{
		Bitmap resizedBitmap = null;
		try
		{
			int width = bitmapOrg.getWidth(); /* 140 */
			int height = bitmapOrg.getHeight();
			int newWidth = width;
			int newHeight = height;

			double ratio = (double) width / height;
			newHeight = (int) (newWidth / ratio);
			float scaleWidth = ((float) newWidth) / width;
			float scaleHeight = ((float) newHeight) / height;

			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);
			if (exifOrientation != 0)
			{
				if (exifOrientation == 6)
				{
					// if (width > height)
					{
						matrix.postRotate(90);
					}
				}
				else
					if (exifOrientation == 3)
					{
						matrix.postRotate(180);
					}
					else
						if (exifOrientation == 8)
						{
							matrix.postRotate(270);
						}
			}
			resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, width, height, matrix, true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return resizedBitmap;
	}

	public static void setResponseDataInshrdPref(Context mContext, JSONObject responseJsonObject)
	{
		if (responseJsonObject.optBoolean("Success"))
		{
			try
			{
				JSONObject jObject = responseJsonObject.getJSONObject("Result");
				Utility.setSharedPrefAddressStringData(mContext, "name", jObject.getString("Name"));
				Utility.setSharedPrefAddressStringData(mContext, "birthday", jObject.getString("DateOfBirth"));
				Utility.setSharedPrefAddressStringData(mContext, "company", jObject.getString("CompanyName"));
				Utility.setSharedPrefAddressStringData(mContext, "address", jObject.getString("Address"));
				Utility.setSharedPrefAddressStringData(mContext, "Image", jObject.getString("ImageUrl"));
				Utility.setSharedPrefStringData(mContext, "usertype", jObject.getString("UserType"));
				Utility.setSharedPrefStringData(mContext, "drivertype", jObject.getString("DriverType"));
				if (jObject.getString("UserType").equalsIgnoreCase("1") && jObject.getString("DriverType").equalsIgnoreCase("1"))
				{
					Utility.setSharedPrefStringData(mContext, "height", jObject.optString("TruckHeight"));
					Utility.setSharedPrefStringData(mContext, "width", jObject.optString("TruckWidth"));
					Utility.setSharedPrefStringData(mContext, "length", jObject.optString("TruckLength"));
				}

				Utility.setSharedPrefStringData(mContext, "userId", jObject.getString("Userid"));
				Utility.setSharedPrefStringData(mContext, "compantId", jObject.getString("CompanyID"));

			}
			catch (JSONException e)
			{

				e.printStackTrace();
			}
		}
	}

	public static ImageLoader getImageLoader1(Context context)
	{
		ImageLoader instance = ImageLoader.getInstance();

		if (!instance.isInited())
		{
			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).memoryCacheExtraOptions(480, 800)
			// default = device screen dimensions
					.discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75, new BitmapProcessor()
					{

						@Override
						public Bitmap process(Bitmap bitmap)
						{
							return bitmap;
						}
					}).threadPoolSize(3)
					// default
					.threadPriority(Thread.NORM_PRIORITY - 1)
					// default
					.tasksProcessingOrder(QueueProcessingType.FIFO)
					// default
					.denyCacheImageMultipleSizesInMemory().memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
					// default
					.memoryCacheSize(2 * 1024 * 1024)
					// default
					.imageDownloader(new BaseImageDownloader(context)) // default
					.defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
					.build();

			instance.init(config);
		}

		return instance;
	}

	public static void requestImage(Context context, ImageView view, String profileImageId)
	{
		if (!TextUtils.isEmpty(profileImageId) && !profileImageId.equals("null"))
		{
			view.setTag(profileImageId);

			String uri = profileImageId;
			if (profileImageId.startsWith("http:") || profileImageId.startsWith("https:"))
			{
				uri = profileImageId;
			}

			DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnFail(R.drawable.signup_placeholder).cacheInMemory(true).displayer(new SimpleBitmapDisplayer()).build();

			ImageLoader instance = Utility.getImageLoader(context);
			instance.displayImage(uri, view, options);
		}
	}

	public static String onPhotoTaken(Intent data, Context context)
	{
		try
		{
			String[] projection = { MediaStore.Images.Media.DATA };
			Cursor cursor = context.getContentResolver().query(Constant.URI_IMAGE_CAPTURED, projection, null, null, null);
			int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			String mImagePath = cursor.getString(column_index_data);
			cursor.close();
			Log.e("Image path", mImagePath);

			Log.w("imagePath", mImagePath);
			// Utility.decodeFile(new File(mImagePath));
			return mImagePath;

		}
		catch (Exception e)
		{
			e.printStackTrace();
			Log.e("imagePath", "onactivity result");
		}
		return null;
	}

	// code for getting the videopath of video taken via Camera 
	public static String onVideoTaken(Intent data, Context context)
	{
		try
		{
			String[] projection = { MediaStore.Video.Media.DATA };
			Cursor cursor = context.getContentResolver().query(Constant.URI_VIDEO_CAPTURED, projection, null, null, null);
			int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
			cursor.moveToFirst();
			String mImagePath = cursor.getString(column_index_data);
			cursor.close();
			Log.e("Video path", mImagePath);

			Log.w("Video path", mImagePath);
			return mImagePath;

		}
		catch (Exception e)
		{
			e.printStackTrace();
			Log.e("videopath", "onactivity result");
		}
		return null;
	}

	// code for getting the imagepath of image taken from Gallery to imageView
	public static String onPhotoTakenFromgallery(Intent data, Context context)
	{
		try
		{
			Uri _uri = data.getData();
			if (_uri != null)
			{
				Cursor cursor = context.getContentResolver().query(_uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA, MediaStore.Images.Media.ORIENTATION }, null, null, null);
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

				cursor.moveToFirst();

				String mImagePath = cursor.getString(column_index);
				cursor.close();
				return mImagePath;

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			Log.e("imagePath", "onactivity result");
		}
		return null;
	}

	public static String onAudioTake(Context context, Uri uri)
	{
		String filepath = "";//default fileName
		//Uri filePathUri = uri;
		File file;
		if (uri.getScheme().toString().compareTo("content") == 0)
		{
			Cursor cursor = context.getContentResolver().query(uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA, MediaStore.Images.Media.ORIENTATION }, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

			cursor.moveToFirst();

			String mImagePath = cursor.getString(column_index);
			cursor.close();
			filepath = mImagePath;

		}
		else
			if (uri.getScheme().compareTo("file") == 0)
			{
				try
				{
					file = new File(new URI(uri.toString()));
					if (file.exists())
						filepath = file.getAbsolutePath();

				}
				catch (URISyntaxException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
			{
				filepath = uri.getPath();
			}
		return filepath;
	}

	// code for getting the videopath of video taken from Gallery 
	public static String onVideoTakenFromgallery(Intent data, Context context)
	{
		try
		{
			Uri _uri = data.getData();
			if (_uri != null)
			{
				Cursor cursor = context.getContentResolver().query(_uri, new String[] { android.provider.MediaStore.Video.VideoColumns.DATA, MediaStore.Video.Media.MINI_THUMB_MAGIC }, null, null,
						null);
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);

				cursor.moveToFirst();

				String mImagePath = cursor.getString(column_index);
				cursor.close();
				return mImagePath;

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			Log.e("videopath", "onactivity result");
		}
		return null;
	}

	public static String getFileNameByUri(Context context, Uri uri)
	{
		String filepath = "";//default fileName
		//Uri filePathUri = uri;
		File file;
		if (uri.getScheme().toString().compareTo("content") == 0)
		{
			Cursor cursor = context.getContentResolver().query(uri, new String[] { android.provider.MediaStore.Audio.AudioColumns.DATA }, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA);

			cursor.moveToFirst();

			String mImagePath = cursor.getString(column_index);
			cursor.close();
			filepath = mImagePath;

		}
		else
			if (uri.getScheme().compareTo("file") == 0)
			{
				try
				{
					file = new File(new URI(uri.toString()));
					if (file.exists())
						filepath = file.getAbsolutePath();

				}
				catch (URISyntaxException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
			{
				filepath = uri.getPath();
			}
		return filepath;
	}

	//	public static void getInfoLayout(final String id, final  Context context)
	//	{
	//
	//		final ProgressDialog mCategotyProgressDialog = new ProgressDialog(context);
	//		mCategotyProgressDialog.setTitle("Please Wait..");
	//		mCategotyProgressDialog.show();
	//		final Handler mCancelHandler= new Handler();
	//
	//		new Thread(new Runnable()
	//		{
	//
	//			String response = "";
	//
	//			@Override
	//			public void run()
	//			{
	//
	//				JSONObject cancelJsonObject = new JSONObject();
	//				try
	//				{
	//
	//					cancelJsonObject.put("id", id);
	//
	//				}
	//				catch (JSONException e)
	//				{
	//					// TODO Auto-generated catch block
	//					e.printStackTrace();
	//				}
	//				Log.e("params for first", cancelJsonObject.toString());
	//				String url = APIUtils.BASE_URL + APIUtils.GET_PLACE_INFO;
	//				response = Utility.POST(url, cancelJsonObject.toString(), APIUtils.GET_PLACE_INFO);
	//				Log.e("response", response.toString());
	//				mCancelHandler.post(new Runnable()
	//				{
	//
	//					private String msg;
	//
	//					@Override
	//					public void run()
	//					{
	//						mCategotyProgressDialog.dismiss();
	//
	//						if (response != null && response.length() > 0)
	//						{
	//							Log.e("msg", "is:" + response);
	//
	//							try
	//							{
	//								JSONObject responseJsonObject = new JSONObject(response);
	//								if (responseJsonObject.optBoolean("Success"))
	//								{
	//
	//									JSONObject resultJsonObject = responseJsonObject.getJSONObject("Result");
	//									showInfoDialog(resultJsonObject);
	//
	//								}
	//								else
	//								{
	//									msg = responseJsonObject.optString("Message");
	//									Utility.showMsgDialog(context, msg);
	//								}
	//
	//							}
	//							catch (JSONException e)
	//							{
	//								// TODO Auto-generated catch block
	//								e.printStackTrace();
	//							}
	//
	//						}
	//
	//					}
	//
	//				});
	//
	//			}
	//		}).start();
	//
	//	}
}
