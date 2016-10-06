package com.ta.truckmap.gpstracking;

import org.w3c.dom.Document;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;
import com.ta.truckmap.CallBack;
import com.ta.truckmap.MapScreenActivity;

public class GPSTracker implements LocationListener
{

	private final Context mContext;

	// flag for GPS status
	boolean isGPSEnabled = false;

	// flag for network status
	boolean isNetworkEnabled = false;
	private LocationRequest locationRequest;
	// flag for GPS status
	boolean canGetLocation = false;

	Location location; // location
	double latitude; // latitude
	double longitude; // longitude

	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 1 meters

	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 1000 * 1 * 1; // 1 seconds

	// Declaring a Location Manager
	protected LocationManager locationManager;

	private LatLng mCurrentLatLng;

	private GMapV2Direction md;
	Document doc = null;
	private LatLng mFromServerLatLng;

	CallBack callBack;

	public GPSTracker(Context context, CallBack callBc)
	{
		this.mContext = context;
		callBack = callBc;
		getLocation();

	}

	public GPSTracker(Context context)
	{
		this.mContext = context;
		getLocation();

	}

	public Location getLocation()
	{
		try
		{
			locationRequest = LocationRequest.create();
			locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			locationManager = (LocationManager) mContext.getSystemService(mContext.LOCATION_SERVICE);

			// getting GPS status
			isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled)
			{
				// no network provider is enabled
			}
			else
			{
				this.canGetLocation = true;
				if (isGPSEnabled)
				{
					if (location == null)
					{
						locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						Log.d("GPS Enabled", "GPS Enabled");
						if (locationManager != null)
						{
							location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (location != null)
							{
								latitude = location.getLatitude();
								longitude = location.getLongitude();
							}
						}
					}
				}
				else
					if (isNetworkEnabled)
					{
						locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						Log.d("Network", "Network");
						if (locationManager != null)
						{
							location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
							if (location != null)
							{
								latitude = location.getLatitude();
								longitude = location.getLongitude();
							}
						}
					}
				// if GPS Enabled get lat/long using GPS Services

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return location;
	}

	/**
	 * Stop using GPS listener
	 * Calling this function will stop using GPS in your app
	 * */
	public void stopUsingGPS()
	{
		if (locationManager != null)
		{
			locationManager.removeUpdates(GPSTracker.this);
		}
	}

	/**
	 * Function to get latitude
	 * */
	public double getLatitude()
	{
		if (location != null)
		{
			latitude = location.getLatitude();
		}

		// return latitude
		return latitude;
	}

	/**
	 * Function to get longitude
	 * */
	public double getLongitude()
	{
		if (location != null)
		{
			longitude = location.getLongitude();
		}

		// return longitude
		return longitude;
	}

	/**
	 * Function to check GPS/wifi enabled
	 * @return boolean
	 * */
	public boolean canGetLocation()
	{
		return this.canGetLocation;
	}

	public String getDistanceInXML(double prevLattitude, double prevLongitude, double curLattitude, double curLongitude)
	{
		// check if GPS enabled
		if (canGetLocation())
		{
			//			mCurrentLatitude = (getLatitude());
			//			mCurrentLongitude = (getLongitude());
			mCurrentLatLng = new LatLng(curLattitude, curLongitude);
			// what you want after the click on info window

			md = new GMapV2Direction();

			mFromServerLatLng = new LatLng(prevLattitude, prevLongitude);

			return md.getJsonDoc(mFromServerLatLng, mCurrentLatLng, GMapV2Direction.MODE_WALKING);
		}
		return "0";
	}

	/**
	 * Function to show settings alert dialog
	 * On pressing Settings button will lauch Settings Options
	 * */
	public void showSettingsAlert()
	{
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

		// Setting Dialog Title
		alertDialog.setTitle("GPS is settings");

		// Setting Dialog Message
		alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

		// On pressing Settings button
		alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				mContext.startActivity(intent);
			}
		});

		// on pressing cancel button
		alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.cancel();
			}
		});

		// Showing Alert Message
		alertDialog.show();
	}

	@Override
	public void onLocationChanged(Location location)
	{
		if (location != null)
		{
			this.location = location;
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			if (callBack != null && callBack instanceof MapScreenActivity)
				callBack.onChngedLoc(location);
			Log.e("onLocationChanged", "latitude " + latitude + " ,longitude " + longitude);
		}
	}

	@Override
	public void onProviderDisabled(String provider)
	{
	}

	@Override
	public void onProviderEnabled(String provider)
	{
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{
	}
}

//package com.ta.fitnessapp.utils;
//
//import java.io.IOException;
//
//import javax.xml.parsers.ParserConfigurationException;
//
//import org.apache.http.client.ClientProtocolException;
//import org.w3c.dom.Document;
//import org.xml.sax.SAXException;
//
//import android.app.AlertDialog;
//import android.app.Service;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.IBinder;
//import android.provider.Settings;
//import android.util.Log;
//
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.maps.model.LatLng;
//
//public class GPSTracker extends Service implements LocationListener
//{
//
//	private final Context mContext;
//
//	// flag for GPS status
//	boolean isGPSEnabled = false;
//
//	// flag for network status
//	boolean isNetworkEnabled = false;
//	private LocationRequest locationRequest;
//	// flag for GPS status
//	boolean canGetLocation = false;
//
//	Location location; // location
//	double latitude; // latitude
//	double longitude; // longitude
//
//	// The minimum distance to change Updates in meters
//	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 1 meters
//
//	// The minimum time between updates in milliseconds
//	private static final long MIN_TIME_BW_UPDATES = 1000 * 1 * 1; // 1 seconds
//
//	// Declaring a Location Manager
//	protected LocationManager locationManager;
//
//	private double mCurrentLatitude;
//
//	private double mCurrentLongitude;
//
//	private LatLng mCurrentLatLng;
//
//	private com.ta.fitnessapp.utils.GMapV2Direction md;
//	Document doc = null;
//	private LatLng mFromServerLatLng;
//
//	protected Handler handler = new Handler();
//	String dist = "";
//
//	public GPSTracker(Context context)
//	{
//		this.mContext = context;
//		getLocation();
//	}
//
//	public Location getLocation()
//	{
//		try
//		{
//			locationRequest = LocationRequest.create();
//			locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//			locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
//
//			// getting GPS status
//			isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//
//			// getting network status
//			isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//
//			if (!isGPSEnabled && !isNetworkEnabled)
//			{
//				// no network provider is enabled
//			}
//			else
//			{
//				this.canGetLocation = true;
//				if (isNetworkEnabled)
//				{
//					locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
//							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
//					Log.d("Network", "Network");
//					if (locationManager != null)
//					{
//						location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//						if (location != null)
//						{
//							latitude = location.getLatitude();
//							longitude = location.getLongitude();
//						}
//					}
//				}
//				// if GPS Enabled get lat/long using GPS Services
//				if (isGPSEnabled)
//				{
//					if (location == null)
//					{
//						locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
//								MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
//						Log.d("GPS Enabled", "GPS Enabled");
//						if (locationManager != null)
//						{
//							location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//							if (location != null)
//							{
//								latitude = location.getLatitude();
//								longitude = location.getLongitude();
//							}
//						}
//					}
//				}
//			}
//
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//
//		return location;
//	}
//
//	/**
//	 * Stop using GPS listener
//	 * Calling this function will stop using GPS in your app
//	 * */
//	public void stopUsingGPS()
//	{
//		if (locationManager != null)
//		{
//			locationManager.removeUpdates(GPSTracker.this);
//		}
//	}
//
//	/**
//	 * Function to get latitude
//	 * */
//	public double getLatitude()
//	{
//		if (location != null)
//		{
//			latitude = location.getLatitude();
//		}
//
//		// return latitude
//		return latitude;
//	}
//
//	/**
//	 * Function to get longitude
//	 * */
//	public double getLongitude()
//	{
//		if (location != null)
//		{
//			longitude = location.getLongitude();
//		}
//
//		// return longitude
//		return longitude;
//	}
//
//	/**
//	 * Function to check GPS/wifi enabled
//	 * @return boolean
//	 * */
//	public boolean canGetLocation()
//	{
//		return this.canGetLocation;
//	}
//
//	public String getDistanceInXML(double prevLattitude, double prevLongitude, double curLattitude, double curLongitude)
//	{
//		// check if GPS enabled
//		if (canGetLocation())
//		{
//			//			mCurrentLatitude = (getLatitude());
//			//			mCurrentLongitude = (getLongitude());
//			Log.e("current ", "is:" + mCurrentLatitude);
//			mCurrentLatLng = new LatLng(curLattitude, curLongitude);
//			// what you want after the click on info window
//
//			md = new GMapV2Direction();
//
//			mFromServerLatLng = new LatLng(prevLattitude, prevLongitude);
//
//			return md.getJsonDoc(mFromServerLatLng, mCurrentLatLng, GMapV2Direction.MODE_DRIVING);
//		}
//		return "0";
//	}
//
//	public String getDistance(final String latitude, final String longitude)
//	{
//		// check if GPS enabled
//		if (canGetLocation())
//		{
//			mCurrentLatitude = (getLatitude());
//			mCurrentLongitude = (getLongitude());
//			Log.e("current ", "is:" + mCurrentLatitude);
//			mCurrentLatLng = new LatLng(mCurrentLatitude, mCurrentLongitude);
//			// what you want after the click on info window
//
//			md = new GMapV2Direction();
//
//			mFromServerLatLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
//
//			new Thread(new Runnable()
//			{
//
//				@Override
//				public void run()
//				{
//					try
//					{
//						doc = md.getDocument(mCurrentLatLng, mFromServerLatLng, GMapV2Direction.MODE_DRIVING);
//					}
//					catch (ClientProtocolException e)
//					{
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					catch (IOException e)
//					{
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					catch (ParserConfigurationException e)
//					{
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					catch (SAXException e)
//					{
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					handler.post(new Runnable()
//					{
//
//						@Override
//						public void run()
//						{
//
//							if (md.getDistanceText(doc) != null)
//
//								dist = md.getDistanceText(doc);
//							else
//								dist = "0";
//
//						}
//					});
//
//				}
//			}).start();
//			return dist;
//		}
//		return "0";
//	}
//
//	public String getCustomDistance(final double latitude, final double longitude)
//	{
//		// check if GPS enabled
//		if (canGetLocation())
//		{
//			mCurrentLatitude = (getLocation().getLatitude());
//			mCurrentLongitude = (getLocation().getLongitude());
//			Log.e("current ", "is:" + mCurrentLatitude);
//			mCurrentLatLng = new LatLng(mCurrentLatitude, mCurrentLongitude);
//			// what you want after the click on info window
//
//			md = new GMapV2Direction();
//
//			mFromServerLatLng = new LatLng(latitude, longitude);
//
//			try
//			{
//				doc = md.getDocument(mCurrentLatLng, mFromServerLatLng, GMapV2Direction.MODE_WALKING);
//			}
//			catch (ClientProtocolException e)
//			{
//				e.printStackTrace();
//			}
//			catch (IOException e)
//			{
//				e.printStackTrace();
//			}
//			catch (ParserConfigurationException e)
//			{
//				e.printStackTrace();
//			}
//			catch (SAXException e)
//			{
//				e.printStackTrace();
//			}
//
//			dist = md.getDistanceValue(doc) + "";
//			Log.e("inside fetch distance", "inside fetch");
//
//			return dist;
//		}
//		return "0";
//	}
//
//	/**
//	 * Function to show settings alert dialog
//	 * On pressing Settings button will lauch Settings Options
//	 * */
//	public void showSettingsAlert()
//	{
//		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
//
//		// Setting Dialog Title
//		alertDialog.setTitle("GPS is settings");
//
//		// Setting Dialog Message
//		alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
//
//		// On pressing Settings button
//		alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener()
//		{
//			public void onClick(DialogInterface dialog, int which)
//			{
//				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//				mContext.startActivity(intent);
//			}
//		});
//
//		// on pressing cancel button
//		alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
//		{
//			public void onClick(DialogInterface dialog, int which)
//			{
//				dialog.cancel();
//			}
//		});
//
//		// Showing Alert Message
//		alertDialog.show();
//	}
//
//	@Override
//	public void onLocationChanged(Location location)
//	{
//		if (location != null)
//		{
//			this.location=location;
//			latitude = location.getLatitude();
//			longitude = location.getLongitude();
//			Log.e("onLocationChanged", "latitude "+latitude+" ,longitude "+longitude);
//		}
//	}
//
//	@Override
//	public void onProviderDisabled(String provider)
//	{
//	}
//
//	@Override
//	public void onProviderEnabled(String provider)
//	{
//	}
//
//	@Override
//	public void onStatusChanged(String provider, int status, Bundle extras)
//	{
//	}
//
//	@Override
//	public IBinder onBind(Intent arg0)
//	{
//		return null;
//	}
//
//	public String getCustomDistance(double prevLattitude, double prevLongitude, double curLattitude, double curLongitude)
//	{
//		if (canGetLocation())
//		{
//			//			mCurrentLatitude = curLattitude;
//			//			mCurrentLongitude = curLongitude;
//			//			Log.e("current ", "is:" + mCurrentLatitude);
//			mCurrentLatLng = new LatLng(curLattitude, curLongitude);
//			// what you want after the click on info window
//
//			md = new GMapV2Direction();
//
//			mFromServerLatLng = new LatLng(prevLattitude, prevLongitude);
//
//			try
//			{
//				doc = md.getDocument(mFromServerLatLng, mCurrentLatLng, GMapV2Direction.MODE_WALKING);
//			}
//			catch (ClientProtocolException e)
//			{
//				e.printStackTrace();
//			}
//			catch (IOException e)
//			{
//				e.printStackTrace();
//			}
//			catch (ParserConfigurationException e)
//			{
//				e.printStackTrace();
//			}
//			catch (SAXException e)
//			{
//				e.printStackTrace();
//			}
//
//			dist = md.getDistanceValue(doc) + "";
//			Log.e("inside fetch distance", "inside fetch");
//
//			return dist;
//		}
//		return "0";
//	}
//
//}
