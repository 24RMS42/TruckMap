package com.ta.truckmap;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.ta.inapp.util.IabHelper;
import com.ta.inapp.util.IabResult;
import com.ta.inapp.util.Inventory;
import com.ta.inapp.util.Purchase;
import com.ta.truckmap.gpstracking.GMapV2Direction;
import com.ta.truckmap.gpstracking.GPSTracker;
import com.ta.truckmap.rest.api.APIUtils;
import com.ta.truckmap.util.Constant;
import com.ta.truckmap.util.CustomDialog;
import com.ta.truckmap.util.CustomDialog.DIALOG_TYPE;
import com.ta.truckmap.util.CustomProgressDialog;
import com.ta.truckmap.util.IActionOKCancel;
import com.ta.truckmap.util.LoadAdViewInEveryActivity;
import com.ta.truckmap.util.PhoneNumberTextWatcher;
import com.ta.truckmap.util.Utility;
import com.truckmap.model.FinishedTripDataBean;
import com.truckmap.model.UserDataBean;
import com.truckmap.parsers.FinishTripParser;
import com.truckmap.parsers.Parser;

public class HomeActivity extends BaseActivity implements OnClickListener, IActionOKCancel
{
	UserDataBean userDataBean;
	LinearLayout mAddNewTripLinearLayout, mSeeNotification, mSeeCurrentTrip, mSeeFinishTrip;
	Dialog mDialogShow;

	Handler mEnterMapScreenHandler = new Handler();
	Handler mCurrentMapScreenHandler = new Handler();
	Handler mHomeMapHandler = new Handler();
	Handler mMapNewHandler = new Handler();

	private CheckBox driverCheckBox;
	private LinearLayout belowDriverCheckLinearLayout;
	public static LatLng latlng_Source, latlng_Dest, latlng_moreDest;
	public static double mDestLatitude, mDestLongitude, mMoreDestLatitude, mMoreDestLongitude;
	ArrayList<String> resultList = null;
	public static String mTripId = "";
	GPSTracker gpsTracker;

	Address location, locationMore;
	EditText addnewtrip_ed_DestName;
	EditText mDest_Contct_Details;
	private EditText mDestTripEdBelowAddMore, mDestSecCntDetailEditText;
	protected Parser<?> parser;
	int locationDetailCount = 0;
	public int flagEnterDest = 0;
	// inn app billing process
	static final String TAG = "sampleBilling";
	// static final String SKU_GAS = "android.test.purchased";
	static final String SKU_GAS = "com.ta.mobia.truckapp.product.299";
	// com.ta.mobia.truckapp.product.299

	static final int RC_REQUEST = 10001;
	// The helper object
	IabHelper mHelper;
	boolean mcheckgps;
	LinearLayout mDropDownLinearLayout;
	protected Handler mPurchaseRenewalHandler = new Handler();
	protected Handler mPurchaseRenewalUpdateHandler = new Handler();
	ImageView home_ui_logoutImgview, closeAddnewtrip;
	TextView home_ui_tv_logo, home_ui_tv_addnewtrip, home_ui_tv_notification, home_ui_tv_currenttrip, home_ui_tv_finishtrip;

	EditText addnewtrip_ed_TripName;
	Button mAddnewtripBtnSend;
	private EditText mHeightEditText, mLengthEditText, mWidthEditText;
	private TextView mLengthUnitTv, mHeightUnitTv, mWidthUnitTv;

	public static ArrayList<ArrayList> mDrawLineBigArrayList;
	ArrayList<String> mDrawLineArrayList;
	String mTripIdCurrent, mNameCurrent, mStartLongitudeCurrent, mStartLatitudeCurrent, mEndLongitudeCurrent, mEndLatitudeCurrent, mStartPointNameCurrent, mEndPointNameCurrent;
	String mStartLongitudeDrawLine, mStartLatitudeDrawLine, mEndLatitudeDrawLine, mEndLongitudeDrawLine, mCommentDrawLine, mColorDrawLine;
	int destLng = 0, destMoreLng = 0;
	LinearLayout mAddMobsLinearLayout;
	LinearLayout mAddMorebelowDropDownLayout;
	public static int drawlineArrayLength;

	Handler mSourceInfoHandler = new Handler();
	public static String location_string = "", locationStringDestination = "", locationStringMoreDestination = "";
	private static IntentFilter s_intentFilter;

	public String mLastSearchString = "", isCancel = "false";

	public boolean proceed = false;
	protected Object mParentId;
	GoogleMap mMap;
	// Added in phase-2
	private ImageView mEventImageView;
	MarkerOptions markerSource;
	private String mCuurentAddress = "", destContactDetail = "", mSecondContatctDetail = "";

	private LatLng mNewLatLngSource, mNewLatLngDestination;
	private ArrayList<Address> mNewLatLongSource, mNewLatLongDesination;
	MarkerOptions mNewMarkersource, mNewMarkerDestination;
	Document doc;
	GMapV2Direction gmapv = new GMapV2Direction();
	protected Handler mMapHandler = new Handler();//
	Polyline poly;
	protected Handler mPinHandler = new Handler();
	protected Handler mCancelHandler = new Handler();
	private ImageLoader imageLoader;
	private com.ta.truckmap.util.CustomProgressDialog mCustomDialog;
	private Handler mLocationUpdate = new Handler();
	private CustomDialog dailog;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		gpsTracker = new GPSTracker(HomeActivity.this);
		this.imageLoader = imageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(this));

		SplashActivity.regId = Utility.getSharedPrefStringData(HomeActivity.this, "regId");
		initiViewReference();
		setFontFamily();

		if (Utility.getSharedPrefString(HomeActivity.this, "usertype").equalsIgnoreCase("1"))
		{

			if (Utility.getSharedPrefString(HomeActivity.this, "driverrtype").equalsIgnoreCase("1"))
			{
				if (Utility.getSharedPrefString(HomeActivity.this, "isPurchase").equalsIgnoreCase("") || Utility.getSharedPrefString(HomeActivity.this, "isPurchase").equalsIgnoreCase("false"))
				{
					if (Utility.getSharedPrefStringData(HomeActivity.this, "day").equalsIgnoreCase("1"))
					{
						registerReceiver(m_timeChangedReceiver, s_intentFilter);
						checkRenewalPurchase();
					}

				}

			}

		}

	}

	static
	{
		s_intentFilter = new IntentFilter();
		s_intentFilter.addAction(Intent.ACTION_TIME_TICK);
		s_intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
		s_intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
	}

	private final BroadcastReceiver m_timeChangedReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			final String action = intent.getAction();

			if (action.equals(Intent.ACTION_TIME_CHANGED) || action.equals(Intent.ACTION_TIMEZONE_CHANGED))
			{
				checkRenewalPurchase();
			}
		}
	};

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		try
		{
			if (m_timeChangedReceiver != null)
				unregisterReceiver(m_timeChangedReceiver);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

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

	/*
	 * check in parent status of in-app purchase through web service
	 */
	private void checkRenewalPurchase()
	{

		new Thread(new Runnable()
		{
			String response = "";

			@Override
			public void run()
			{
				JSONObject jsonObj = new JSONObject();
				try
				{
					mParentId = Utility.getSharedPrefStringData(HomeActivity.this, "username");
					jsonObj.put("Email", mParentId);
					jsonObj.put("Userid", Utility.getSharedPrefString(HomeActivity.this, "userId") /*
																									* Utility
																									* .
																									* getSharedPrefStringData
																									* (
																									* HomeActivity
																									* .
																									* this
																									* ,
																									* "mUserId"
																									* )
																									*/);

				}
				catch (Exception e)
				{
					// TODO: handle exception
				}

				Log.e("params for first", jsonObj.toString());
				String url = APIUtils.BASE_URL + APIUtils.CHECKPURCHASED;
				Log.e("login url", url);
				response = Utility.POST(url, jsonObj.toString(), APIUtils.CHECKPURCHASED);
				Log.e("login url", response.toString());
				mPurchaseRenewalHandler.post(new Runnable()
				{

					@Override
					public void run()
					{

						if (response != null && response.length() > 0)
						{
							Log.e("response", response.toString());

							try
							{
								JSONObject jsonObject = new JSONObject(response);
								if (jsonObject.optString("Success").equalsIgnoreCase("true"))
								{

									if (jsonObject.optString("Result").equalsIgnoreCase("1"))
									{
										Utility.setSharedPrefStringData(HomeActivity.this, "isPurchase", "true");
									}
									else
									{
										getInAppProcess();
									}

								}
								else
								{

									Utility.showToastMessage(HomeActivity.this, jsonObject.optString("Message"));

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

	private void getInAppProcess()
	{

		AlertDialog.Builder inAppDialogBuilder = new AlertDialog.Builder(this);
		inAppDialogBuilder.setTitle("MO-BIA");
		inAppDialogBuilder.setCancelable(false);
		inAppDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
		inAppDialogBuilder.setMessage("Your subcription has been expired ! Do you want to Purchase the app ?");
		inAppDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				Log.d(TAG, "Buy gas button clicked.");

				// setWaitScreen(true);
				Log.d(TAG, "Launching purchase flow for gas.");

				String payload = "";
				checkInAppVlaidation();

			}
		});

		inAppDialogBuilder.show();

	}

	// Callback for when a purchase is finished
	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener()
	{
		public void onIabPurchaseFinished(IabResult result, Purchase purchase)
		{
			Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

			// if we were disposed of in the meantime, quit.
			if (mHelper == null)
				return;

			if (result.isFailure())
			{
				complain("Error purchasing: " + result);
				// setWaitScreen(false);
				return;
			}
			if (!verifyDeveloperPayload(purchase))
			{
				complain("Error purchasing. Authenticity verification failed.");
				// setWaitScreen(false);
				return;
			}

			Log.d(TAG, "Purchase successful.");

			if (purchase.getSku().equals(SKU_GAS))
			{
				// bought 1/4 tank of gas. So consume it.
				Log.d(TAG, "Purchase is gas. Starting gas consumption.");
				mHelper.consumeAsync(purchase, mConsumeFinishedListener);

				//

			}

		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (mHelper == null)
			return;

		// Pass on the activity result to the helper for handling
		if (!mHelper.handleActivityResult(requestCode, resultCode, data))
		{
			// not handled, so handle it ourselves (here's where you'd
			// perform any handling of activity results not related to in-app
			// billing...
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	/** Verifies the developer payload of a purchase. */
	boolean verifyDeveloperPayload(Purchase p)
	{
		String payload = p.getDeveloperPayload();

		return true;
	}

	// Called when consumption is complete
	IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener()
	{
		public void onConsumeFinished(Purchase purchase, IabResult result)
		{
			Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);

			// if we were disposed of in the meantime, quit.
			if (mHelper == null)
				return;

			// We know this is the "gas" sku because it's the only one we
			// consume,
			// so we don't check which sku was consumed. If you have more than
			// one
			// sku, you probably should check...
			if (result.isSuccess())
			{
				// successfully consumed, so we apply the effects of the item in
				// our
				// game world's logic, which in our case means filling the gas
				// tank a bit
				Log.d(TAG, "Consumption successful. Provisioning.");

				saveData(purchase.getOrderId());

				// alert("Now You are successfully purchase it!");
			}
			else
			{
				complain("Error while consuming: " + result);
			}
			// updateUi();
			// setWaitScreen(false);
			Log.d(TAG, "End consumption flow.");
		}

	};

	/**
	 * this method update the status of parent for In-app purchase
	 */
	private void saveData(final String inAppTransactionId)
	{

		mCustomDialog = new CustomProgressDialog(HomeActivity.this, "Please Wait..");

		new Thread(new Runnable()
		{

			String response = "";

			@Override
			public void run()
			{
				JSONObject jsonObj = new JSONObject();
				try
				{

					/*
					 * jsonObj.put("Email", mParentId); jsonObj.put("Userid",
					 * Utility.getSharedPrefStringData(HomeActivity.this,
					 * "mUserId"));
					 */

					jsonObj.put("Email", mParentId);
					jsonObj.put("Userid", Utility.getSharedPrefString(HomeActivity.this, "userId"));
					jsonObj.put("IsPurchased", "1");
					/* jsonObj.put("inAppTransactionId", inAppTransactionId); */

				}
				catch (Exception e)
				{
					// TODO: handle exception
				}

				Log.e("params for first", jsonObj.toString());
				String url = APIUtils.BASE_URL + APIUtils.UPDATEPURCHASE;

				Log.e("login url", url);
				response = Utility.POST(url, jsonObj.toString(), APIUtils.UPDATEPURCHASE);

				mPurchaseRenewalUpdateHandler.post(new Runnable()
				{
					@Override
					public void run()
					{
						mCustomDialog.dismissDialog();

						if (response != null && response.length() > 0)
						{
							Log.e("response", response.toString());

							try
							{
								JSONObject jsonObject = new JSONObject(response);
								if (jsonObject.optString("Success").equalsIgnoreCase("true"))
								{
									Utility.setSharedPrefStringData(HomeActivity.this, "isPurchase", "true");
									/*
									 * Utility.showToastMessage(HomeActivity.this
									 * , jsonObject.optString("Message"));
									 */
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

	void complain(String message)
	{
		/*
		 * Log.e(TAG, "**** PikMykid InApp Error: " + message); alert("Error: "
		 * + message);
		 */
		getInAppProcess();

	}

	void alert(String message)
	{
		AlertDialog.Builder bld = new AlertDialog.Builder(this);
		bld.setMessage(message);
		bld.setNeutralButton("OK", null);
		Log.d(TAG, "Showing alert dialog: " + message);
		bld.create().show();
	}

	private void checkInAppVlaidation()
	{
		String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqF60w9J5ssh45cs2TKN2zMuD7/yMKkjZCS9MPGcrLJEZnlv8b2oKxQg5GLGoAxu1Dk1IW0ed1ruFsyXmXdYKnahPqAvNVyFxUh2cPl/c/BiH0/0Fnnm+WpgLiYqhNlkiP4kVu8ZnvKHfTlSEHPLmQfFYQgYEbnPUtkWwThx+AjaVNsazzZd+wM+WLMsv0zWDv+Ul8FzYul87rwfXKE2h4kdMnhcMx7bK8IM3sKR0e6qvaKR+m6EKIlmh7vjPrxLOks+KkeetLA39BwaAlKyvCu6/Jf8lVxjDiCurxlrmICMiPY98xAsLHO9ZAzqr7fB9gFuTQySiUKTsQb229nbT9wIDAQAB";

		// Create the helper, passing it our context and the public key to
		// verify signatures with
		Log.d(TAG, "Creating IAB helper.");
		mHelper = new IabHelper(this, base64EncodedPublicKey);
		// enable debug logging (for a production application, you should set
		// this to false).
		mHelper.enableDebugLogging(false);
		// showWaitIndicator();

		// Start setup. This is asynchronous and the specified listener
		// will be called once setup completes.
		Log.d(TAG, "Starting setup.");
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener()
		{
			public void onIabSetupFinished(IabResult result)
			{
				Log.d(TAG, "Setup finished.");

				if (!result.isSuccess())
				{
					// Oh noes, there was a problem.
					complain("Problem setting up in-app billing: " + result);
					return;
				}

				// Have we been disposed of in the meantime? If so, quit.
				if (mHelper == null)
					return;
				// dismissWaitIndicatior();
				// IAB is fully set up. Now, let's get an inventory of stuff we
				// own.
				Log.d(TAG, "Setup successful. Querying inventory.");
				mHelper.queryInventoryAsync(mGotInventoryListener);
			}
		});

	}

	// Listener that's called when we finish querying the items and
	// subscriptions we own
	IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener()
	{
		public void onQueryInventoryFinished(IabResult result, Inventory inventory)
		{
			Log.d(TAG, "Query inventory finished.");

			// Have we been disposed of in the meantime? If so, quit.
			if (mHelper == null)
				return;

			// Is it a failure?
			if (result.isFailure())
			{
				complain("Failed to query inventory: " + result);
				return;
			}

			Log.d(TAG, "Query inventory was successful.");

			/*
			 * Check for items we own. Notice that for each purchase, we check
			 * the developer payload to see if it's correct! See
			 * verifyDeveloperPayload().
			 */

			/*
			 * // Do we have the premium upgrade? Purchase premiumPurchase =
			 * inventory.getPurchase(SKU_PREMIUM); mIsPremium = (premiumPurchase
			 * != null && verifyDeveloperPayload(premiumPurchase)); Log.d(TAG,
			 * "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));
			 */

			/*
			 * // Do we have the infinite gas plan? Purchase infiniteGasPurchase
			 * = inventory.getPurchase(SKU_INFINITE_GAS);
			 * mSubscribedToInfiniteGas = (infiniteGasPurchase != null &&
			 * verifyDeveloperPayload(infiniteGasPurchase)); Log.d(TAG, "User "
			 * + (mSubscribedToInfiniteGas ? "HAS" : "DOES NOT HAVE") +
			 * " infinite gas subscription."); if (mSubscribedToInfiniteGas)
			 * mTank = TANK_MAX;
			 */

			// Check for gas delivery -- if we own gas, we should fill up the
			// tank immediately
			/*Purchase gasPurchase = inventory.getPurchase(SKU_GAS);
			if (gasPurchase != null && verifyDeveloperPayload(gasPurchase))
			{
				Log.d(TAG, "We have gas. Consuming it.");
				mHelper.consumeAsync(inventory.getPurchase(SKU_GAS), mConsumeFinishedListener);

				return;
			}*/
			String payload = "";
			mHelper.launchPurchaseFlow(HomeActivity.this, SKU_GAS, RC_REQUEST, mPurchaseFinishedListener, payload);
			// updateUi();
			// setWaitScreen(false);
			Log.d(TAG, "Initial inventory query finished; enabling main UI.");
		}
	};
	private ProgressDialog mCategotyProgressDialog;

	private void initiViewReference()
	{

		mAddNewTripLinearLayout = (LinearLayout) findViewById(R.id.home_ui_addnewTripLayout);
		mSeeCurrentTrip = (LinearLayout) findViewById(R.id.home_ui_currenttrips);
		mSeeNotification = (LinearLayout) findViewById(R.id.home_ui_notification);
		mSeeFinishTrip = (LinearLayout) findViewById(R.id.home_ui_finishedtrips);

		home_ui_logoutImgview = (ImageView) findViewById(R.id.home_ui_logoutImgview);

		home_ui_tv_logo = (TextView) findViewById(R.id.home_ui_tv_logo);
		home_ui_tv_addnewtrip = (TextView) findViewById(R.id.home_ui_tv_Addnewtrip);
		home_ui_tv_currenttrip = (TextView) findViewById(R.id.home_ui_tv_currenttrip);
		home_ui_tv_finishtrip = (TextView) findViewById(R.id.home_ui_tv_finishtrip);
		home_ui_tv_notification = (TextView) findViewById(R.id.home_ui_tv_notification);

		// Added
		mEventImageView = (ImageView) findViewById(R.id.event_btn_imageview);

		mAddMobsLinearLayout = (LinearLayout) findViewById(R.id.add_mobs_lnrlyt);
		LoadAdViewInEveryActivity.loadAdds(this, mAddMobsLinearLayout);
		mAddNewTripLinearLayout.setOnClickListener(this);
		mSeeCurrentTrip.setOnClickListener(this);
		mSeeFinishTrip.setOnClickListener(this);
		mSeeNotification.setOnClickListener(this);

		home_ui_logoutImgview.setOnClickListener(this);
		mEventImageView.setOnClickListener(this);
		LocationUpdater();

	}

	private void LocationUpdater()
	{

		new Thread(new Runnable()
		{
			String response = "";

			@Override
			public void run()
			{

				JSONObject jsonObj = new JSONObject();
				try
				{

					if (gpsTracker.canGetLocation())
					{
						jsonObj.put("Userid", Utility.getSharedPrefString(HomeActivity.this, "userId"));
						jsonObj.put("latitude", gpsTracker.getLatitude());
						jsonObj.put("longitude", gpsTracker.getLongitude());
					}

				}
				catch (Exception e)
				{
					// TODO: handle exception
				}
				Log.e("params for first", jsonObj.toString());
				String url = APIUtils.BASE_URL + APIUtils.UPDATELOCATION;

				response = Utility.POST(url, jsonObj.toString(), APIUtils.UPDATELOCATION);

				/*totaldistance = gpsTracker.getDistanceInXML(prevLattitude, prevLongitude, curLattitude, curLongitude);*/
				/*Log.e("distance", totaldistance);*/

				mLocationUpdate.post(new Runnable()
				{

					@Override
					public void run()
					{

						if (response != null && response.length() > 0)
						{
							Log.e("response", response.toString());

							try
							{
								JSONObject jsonObject = new JSONObject(response);
								if (jsonObject.optString("Success").equalsIgnoreCase("true"))
								{
									getRefreshData();
								}
								else
								{
									//Utility.showToastMessage(MapScreenActivity.this, jsonObject.optString("Message"));
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

		/*if (Utility.getSharedPrefStringData(getApplicationContext(), "childStatus").equalsIgnoreCase("pickedup"))
		{
			timer.cancel();
			Log.e("stop the timer", "1111");
		}*/

	}

	public void refreshMap()
	{
		try
		{
			getRefreshData();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	private void getRefreshData()
	{
		mCustomDialog = new com.ta.truckmap.util.CustomProgressDialog(HomeActivity.this, "Please Wait");
		new Thread(new Runnable()
		{
			String response = "";

			@Override
			public void run()
			{

				JSONObject refreshJsonObject = new JSONObject();
				try
				{
					refreshJsonObject.put("Userid", Utility.getSharedPrefString(HomeActivity.this, "userId"));
					refreshJsonObject.put("DeviceToken", SplashActivity.regId);
				}
				catch (JSONException e)
				{
					e.printStackTrace();
				}

				Log.e("params for refresh map", refreshJsonObject.toString());

				String url = APIUtils.BASE_URL + APIUtils.REFRESHMAP;
				response = Utility.POST(url, refreshJsonObject.toString(), APIUtils.REFRESHMAP);
				Log.e("response for refresh map", response.toString());

				mHomeMapHandler.post(new Runnable()
				{

					@Override
					public void run()
					{

						if (response != null && response.length() > 0)
						{

							parser = new FinishTripParser();

							try
							{

								JSONObject responseJSONObject = new JSONObject(response);

								if (responseJSONObject.optBoolean("Success"))
								{
									Constant.mRefreshTripDataBean = (FinishedTripDataBean) parser.parse(response);

									try
									{
										mapInitiView(mCustomDialog);
									}
									catch (Exception e)
									{
										e.printStackTrace();
									}

								}
								else
								{
									mCustomDialog.dismissDialog();
									Utility.showToastMessage(HomeActivity.this, responseJSONObject.optString("Message"));

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

	private void mapInitiView(CustomProgressDialog mCustomDialog)
	{

		if (gpsTracker.canGetLocation())
		{
			try
			{
				setUpMapIfNeeded(mCustomDialog);

			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

		}
		else
			gpsTracker.showSettingsAlert();

	}

	private void setUpMapIfNeeded(CustomProgressDialog mCustomDialog)
	{
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null)
		{
			mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_relative_fragment)).getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null)
			{
				try
				{
					setUpMap(mCustomDialog);
				}
				catch (Exception e)
				{
					// TODO: handle exception
				}

			}
		}
		else
		{
			setUpMap(mCustomDialog);
		}
	}

	private void setFontFamily()
	{
		Utility.textViewFontRobotoLight(home_ui_tv_addnewtrip, getAssets());
		Utility.textViewFontRobotoLight(home_ui_tv_currenttrip, getAssets());
		Utility.textViewFontRobotoLight(home_ui_tv_finishtrip, getAssets());
		Utility.textViewFontRobotoLight(home_ui_tv_notification, getAssets());
		Utility.textViewFontRobotoLight(home_ui_tv_logo, getAssets());

	}

	private boolean checkgps()
	{
		gpsTracker = new GPSTracker(HomeActivity.this);

		if (gpsTracker.canGetLocation())
		{
			try
			{
				return getSourceLatLng();
			}
			catch (Exception e)
			{
				gpsTracker.showSettingsAlert();
				return false;
			}
		}
		else
		{
			gpsTracker.showSettingsAlert();
			return false;
		}
	}

	private boolean getSourceLatLng()
	{

		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				JSONObject ret = Utility.getLocationInfo(gpsTracker.getLatitude(), gpsTracker.getLongitude());

				JSONObject location;

				try
				{
					location = ret.getJSONArray("results").getJSONObject(0);
					location_string = location.getString("formatted_address");
					Log.d("test", "formattted address:" + location_string);
				}
				catch (JSONException e1)
				{
					e1.printStackTrace();

				}

				mSourceInfoHandler.post(new Runnable()
				{

					@Override
					public void run()
					{
						getDestLatLng();

					}
				});

			}
		}).start();

		/*
		 * coder = new Geocoder(HomeActivity.this, Locale.getDefault());
		 * 
		 * mSourceAddress = new ArrayList<Address>();
		 */
		return true;
	}

	private void getDestLatLng()
	{

		new Thread(new Runnable()
		{

			@Override
			public void run()
			{

				try
				{

					String[] addressSearchStrings = addnewtrip_ed_DestName.getText().toString().split(" ");
					String mAddress = "";
					for (int i = 0; i < addressSearchStrings.length; i++)
					{
						mAddress = mAddress.concat(addressSearchStrings[i]);
					}

					JSONObject ret = Utility.getLatLngFromAddress(mAddress);
					Log.e("response", ret.toString());
					JSONObject address;
					address = ret.getJSONArray("results").getJSONObject(0);
					if (address.length() > 0)
					{
						JSONObject geometry = address.getJSONObject("geometry");
						locationStringDestination = address.getString("formatted_address");
						JSONObject location = geometry.getJSONObject("location");

						mDestLatitude = Double.parseDouble(location.getString("lat"));
						mDestLongitude = Double.parseDouble(location.getString("lng"));

						Log.d("test", "mDestLatitude:" + mDestLatitude);
						latlng_Dest = new LatLng(mDestLatitude, mDestLongitude);
						destLng = 1;
					}
					else
					{
						mDialogShow.dismiss();
						Utility.showMsgDialog(HomeActivity.this, "Please Enter valid address");
						destLng = 0;
					}

					String[] addressMoreStrings = mDestTripEdBelowAddMore.getText().toString().split(" ");
					String mMoreAddress = "";
					for (int i = 0; i < addressMoreStrings.length; i++)
					{
						mMoreAddress = mMoreAddress.concat(addressMoreStrings[i]);
					}

					JSONObject resultResponce = Utility.getLatLngFromAddress(mMoreAddress);
					Log.e("response", resultResponce.toString());
					JSONObject addressMore;
					addressMore = resultResponce.getJSONArray("results").getJSONObject(0);
					if (addressMore.length() > 0)
					{
						JSONObject geometry = addressMore.getJSONObject("geometry");
						locationStringMoreDestination = addressMore.getString("formatted_address");
						JSONObject location = geometry.getJSONObject("location");

						mMoreDestLatitude = Double.parseDouble(location.getString("lat"));
						mMoreDestLongitude = Double.parseDouble(location.getString("lng"));

						Log.d("test", "mMoreDestLatitude:" + mMoreDestLatitude);
						latlng_moreDest = new LatLng(mMoreDestLatitude, mMoreDestLongitude);
						destMoreLng = 1;
					}
					else
					{
						mDialogShow.dismiss();
						Utility.showMsgDialog(HomeActivity.this, "Please Enter valid address");
						destMoreLng = 0;
					}

				}
				catch (JSONException e1)
				{
					e1.printStackTrace();

				}

				mMapNewHandler.post(new Runnable()
				{

					@Override
					public void run()
					{
						getMapScreen();

					}
				});

			}
		}).start();

	}

	private void setUpMap(final CustomProgressDialog mCustomDialog)
	{

		new Thread(new Runnable()
		{

			@Override
			public void run()
			{

				latlng_Source = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
				JSONObject ret = Utility.getLocationInfo(latlng_Source.latitude, latlng_Source.longitude);
				Log.e("address string", ret.toString());

				JSONObject location;

				try
				{
					location = ret.getJSONArray("results").getJSONObject(0);
					mCuurentAddress = location.getString("formatted_address");
					Log.d("test", "formattted address:" + mCuurentAddress);
				}
				catch (JSONException e1)
				{
					e1.printStackTrace();

				}

				mHomeMapHandler.post(new Runnable()
				{

					@Override
					public void run()
					{

						markerSource = new MarkerOptions().position(latlng_Source).title(mCuurentAddress).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin));
						mMap.addMarker(markerSource);
						mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng_Source, 15));
						mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

						if (Constant.mRefreshTripDataBean.getTripArray().get(0).getDrawLineArray().size() > 0)
						{

							for (int i = 0; i < Constant.mRefreshTripDataBean.getTripArray().get(0).getDrawLineArray().size(); i++)
							{

								mNewLatLngSource = new LatLng(Double.parseDouble(Constant.mRefreshTripDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawLineStartLatitude()), Double
										.parseDouble(Constant.mRefreshTripDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawLineStartLongitude()));

								mNewLatLngDestination = new LatLng(Double.parseDouble(Constant.mRefreshTripDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawEndLatitude()), Double
										.parseDouble(Constant.mRefreshTripDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawLineEndLongitude()));
								mNewLatLongSource = new ArrayList<Address>();
								mNewLatLongDesination = new ArrayList<Address>();
								String location_string;

								mNewMarkersource = new MarkerOptions()
										.position(mNewLatLngSource)
										.title(Constant.mRefreshTripDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawLineStartName()/*mNewLatLongSource.get(0).getAddressLine(2) + mNewLatLongSource.get(0).getAddressLine(1)*/)
										.snippet(
												"Category: " + Constant.mRefreshTripDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmCategory() + ",\t \n" + "Details: "
														+ Constant.mRefreshTripDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawComment())
										.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_pin));
								mNewMarkerDestination = new MarkerOptions()
										.position(mNewLatLngDestination)
										.title(Constant.mRefreshTripDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawLineEndName())
										.snippet(
												"Category: " + Constant.mRefreshTripDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmCategory() + ",\t \n" + "Details: "
														+ Constant.mRefreshTripDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawComment())
										.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_pin));
								String mColor = Constant.mRefreshTripDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawColor();

								mMap.addMarker(mNewMarkersource);
								mMap.addMarker(mNewMarkerDestination);

								getNewDrawLine(mNewLatLngSource, mNewLatLngDestination, mColor, mCustomDialog);
							}

						}
						else
						{
							mCustomDialog.dismissDialog();
						}

						try
						{
							if (Constant.mRefreshTripDataBean.getTripArray().get(0).getShareScreenArray().size() > 0)
							{
								for (int j = 0; j < Constant.mRefreshTripDataBean.getTripArray().get(0).getShareScreenArray().size(); j++)
								{
									putNewPointonMap(Constant.mRefreshTripDataBean.getTripArray().get(0).getShareScreenArray().get(j).getShareScreenId(),
											Double.parseDouble(Constant.mRefreshTripDataBean.getTripArray().get(0).getShareScreenArray().get(j).getShareScreenCurrentLatitude()),
											Double.parseDouble(Constant.mRefreshTripDataBean.getTripArray().get(0).getShareScreenArray().get(j).getShareScreenCurrentLongitude()));
								}

							}

						}
						catch (Exception e2)
						{
							// TODO: handle exception
						}

					}
				});

			}
		}).start();

		/*
		try
		{

			new Thread(new Runnable()
			{

				@Override
				public void run()
				{
					adsd

					mMapNewHandler.post(new Runnable()
					{

						@Override
						public void run()
						{
							markerSource = new MarkerOptions().position(latlng_Source).title(mCuurentAddress).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin));
							mMap.addMarker(markerSource);
							mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng_Source, 15));
							mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

						}
					});

				}
			}).start();

		}
		catch (Exception ee)
		{
			ee.printStackTrace();
		}

		*/}

	@Override
	public void onClick(View view)
	{

		if (view == mAddNewTripLinearLayout)
		{

			BaseActivity.createEvent("Adding Trip", "Button Press", "Add trip", null);
			/*easyTracker).send(MapBuilder.createEvent("your_action", "envet_name", "button_name/id", null).build());*/

			// Add New Trip Screen

			try
			{
				showCustomDialogInitvar();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			catch (OutOfMemoryError e)
			{
				e.printStackTrace();
			}
			catch (Throwable e)
			{
				e.printStackTrace();
			}

			driverCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener()
			{

				@Override
				public void onCheckedChanged(CompoundButton arg0, boolean arg1)
				{
					if (driverCheckBox.isChecked())
					{
						belowDriverCheckLinearLayout.setVisibility(View.VISIBLE);
						Utility.setSharedPrefBooleanData(HomeActivity.this, Constant.IS_PROFESSIONAL_DRIVER, true);
					}
					else
					{
						belowDriverCheckLinearLayout.setVisibility(View.GONE);
						Utility.setSharedPrefBooleanData(HomeActivity.this, Constant.IS_PROFESSIONAL_DRIVER, false);
					}
				}
			});

			addnewtrip_ed_DestName.addTextChangedListener(new TextWatcher()
			{

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count)
				{

					proceed = false;
					Log.e("s......", s.toString());
					Log.e("mLastSearchString.....", mLastSearchString);

					if (s.toString().length() >= 3)
					{
						if (!s.toString().equalsIgnoreCase(mLastSearchString))
						{
							new SearchLocataionNameAsyncTask(s.toString().trim()).execute();

						}
					}
					else
						if (s.toString().length() == 0)
						{
							mDropDownLinearLayout.setVisibility(View.GONE);
						}
					// mToDropDownLinearLayout.setVisibility(View.GONE);

				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after)
				{
				}

				@Override
				public void afterTextChanged(Editable s)
				{
				}
			});
			mDestTripEdBelowAddMore.addTextChangedListener(new TextWatcher()
			{

				@Override
				public void onTextChanged(CharSequence character, int arg1, int arg2, int arg3)
				{

					proceed = true;
					if (character.toString().length() >= 3)
					{
						if (!character.toString().equalsIgnoreCase(mLastSearchString))
						{
							new SearchLocataionNameAsyncTask(character.toString().trim()).execute();
						}
					}
					else
						if (character.toString().length() == 0)
						{
							mAddMorebelowDropDownLayout.setVisibility(View.GONE);
							mMoreDestLongitude = 0.0;
							mMoreDestLatitude = 0.0;
							locationStringMoreDestination = "";

						}
					// mToDropDownLinearLayout.setVisibility(View.GONE);

				}

				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
				{
					// TODO Auto-generated method stub

				}

				@Override
				public void afterTextChanged(Editable arg0)
				{
					// TODO Auto-generated method stub

				}
			});
			// Enter Map Screen

			mAddnewtripBtnSend.setOnClickListener(new OnClickListener()
			{
				String tripname = "", destname = "";

				@Override
				public void onClick(View v)
				{
					//					if (proceed == false)
					//					{
					//						// Utility.showErrorDialog(HomeActivity.this,
					//						// getApplicationContext().getResources().getString(R.string.app_name),
					//						// "", mDialogShow);
					//						//Utility.showMsgDialog(HomeActivity.this, "Please Select the Destination Name from the list.");
					//
					//					}
					//					else
					//					{

					if (mDestTripEdBelowAddMore.getVisibility() == View.VISIBLE)
					{
						if (mDestTripEdBelowAddMore.getText().toString().trim().length() > 0)
						{

						}
						else
						{
							HomeActivity.latlng_moreDest = null;
						}

					}

					tripname = addnewtrip_ed_TripName.getText().toString();
					// destname
					destname = addnewtrip_ed_DestName.getText().toString();
					destContactDetail = mDest_Contct_Details.getText().toString();
					/*if (mDestSecCntDetailEditText.getVisibility() == View.VISIBLE)
						mSecondContatctDetail = mDestSecCntDetailEditText.getText().toString();*/

					if (driverCheckBox.isChecked())
					{
						/*loginJsonObject.put("Height", mHeightEditText.getText().toString().trim());
						loginJsonObject.put("Width", mWidthEditText.getText().toString().trim());
						loginJsonObject.put("length", mLengthEditText.getText().toString().trim());*/
						if (!(mHeightEditText.getText().toString().trim().length() > 0))
						{

							Utility.showMsgDialog(HomeActivity.this, "Please enter the Truck height");

						}
						else
							if (!(mWidthEditText.getText().toString().trim().length() > 0))
							{
								Utility.showMsgDialog(HomeActivity.this, "Please enter the Truck Width");

							}
							else
								if (!(mLengthEditText.getText().toString().trim().length() > 0))
									Utility.showMsgDialog(HomeActivity.this, "Please enter the Truck Length");
								else
								{
									if (checkifValid(tripname, destname, destContactDetail, mSecondContatctDetail))
									// Also validate below field.
									//driverCheckBox
									//mHeightEditText
									//mLengthEditText
									//mWidthEditText

									{
										if (checkgps())
										{

											mDialogShow.dismiss();
										}

									}
								}

					}
					else
					{

						if (checkifValid(tripname, destname, destContactDetail, mSecondContatctDetail))
						// Also validate below field.
						//driverCheckBox
						//mHeightEditText
						//mLengthEditText
						//mWidthEditText

						{
							if (checkgps())
							{

								mDialogShow.dismiss();
							}

						}

					}

					//					}

				}

			});

			// Close Add New Trip Screen

			closeAddnewtrip = (ImageView) mDialogShow.findViewById(R.id.rate_courier_layout_corner_cancel_textview);

			closeAddnewtrip.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					mDialogShow.dismiss();
				}
			});
		}
		else
			if (view == mSeeCurrentTrip)
			{

				getCurrentTripService();

			}
			else
				if (view == mSeeFinishTrip)
				{

					startActivity(new Intent(HomeActivity.this, FinishTripActivity.class));

				}
				else
					if (view == mSeeNotification)
					{
						startActivity(new Intent(HomeActivity.this, NotificationActivity.class));

					}
					else
						if (view == home_ui_logoutImgview)
						{/*

							Utility.setSharedPrefStringData(HomeActivity.this, "isPurchase", "false");
							Utility.setSharedPrefStringData(HomeActivity.this, "day", "2");
							AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeActivity.this);

							alertDialogBuilder.setTitle(" " + this.getResources().getString(R.string.app_name));
							alertDialogBuilder.setCancelable(false);
							alertDialogBuilder.setMessage("Are you sure you want to log out?");

							// set positive button: Yes message
							alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
							{
								public void onClick(DialogInterface dialog, int id)
								{
									// go to a new activity of the app

									Utility.setSharedPrefStringData(HomeActivity.this, "username", "");
									Utility.setSharedPrefStringData(HomeActivity.this, "password", "");
									Utility.setSharedPrefStringData(HomeActivity.this, "IsLogin", "");
									Intent positveActivity = new Intent(HomeActivity.this, LoginActivity.class);

									startActivity(positveActivity);
									HomeActivity.this.finish();
								}
							});

							// set negative button: No message
							alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener()
							{
								public void onClick(DialogInterface dialog, int id)
								{
									// cancel the alert box and put a Toast to the user
									dialog.cancel();

								}
							});

							AlertDialog alertDialog = alertDialogBuilder.create();
							// show alert
							alertDialog.show();

							*/
							Intent eventsIntent = new Intent(HomeActivity.this, SettingActivity.class);
							startActivity(eventsIntent);

						}

						else
							if (view == mEventImageView)
							{
								Intent eventsIntent = new Intent(HomeActivity.this, EventsActivity.class);
								startActivity(eventsIntent);

							}

	}

	private void showCustomDialogInitvar()
	{
		if (mDialogShow != null && mDialogShow.isShowing())
			mDialogShow.dismiss();
		mDialogShow = new Dialog(HomeActivity.this);
		mDialogShow.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mDialogShow.setContentView(R.layout.add_new_trip_dialog);
		mDialogShow.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		mDialogShow.setCancelable(false);

		TextView tvLine1 = (TextView) mDialogShow.findViewById(R.id.add_trip_text_view);
		TextView tripNameTextView = (TextView) mDialogShow.findViewById(R.id.trip_name_text_view);
		TextView destEndPointTextView = (TextView) mDialogShow.findViewById(R.id.dest_trip_text_view);
		TextView destContctDetailtextview = (TextView) mDialogShow.findViewById(R.id.dest_contact_detail_text_view);
		TextView addMoreDesCrptn = (TextView) mDialogShow.findViewById(R.id.add_more_desc_text_view);
		TextView heightTextView = (TextView) mDialogShow.findViewById(R.id.height_text_view);
		TextView lengthTextView = (TextView) mDialogShow.findViewById(R.id.length_text_view);
		TextView widthTextView = (TextView) mDialogShow.findViewById(R.id.width_text_view);
		addMoreDesCrptn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.cross_icon_popup, 0, 0, 0);
		TextView destTripTextViewBelowAddMore = (TextView) mDialogShow.findViewById(R.id.des_trip_text_view_below_add_more);
		belowDriverCheckLinearLayout = (LinearLayout) mDialogShow.findViewById(R.id.below_driver_check_linear_layout);
		addnewtrip_ed_TripName = (EditText) mDialogShow.findViewById(R.id.dialog_trip_name_editText_name);
		mDestTripEdBelowAddMore = (EditText) mDialogShow.findViewById(R.id.dilog_destination_trip_edit_text_below_add_more);

		mDestSecCntDetailEditText = (EditText) mDialogShow.findViewById(R.id.dialog_destination_secn_details_edit_text);

		addnewtrip_ed_DestName = (EditText) mDialogShow.findViewById(R.id.dilog_destination_trip_edit_text);
		mDest_Contct_Details = (EditText) mDialogShow.findViewById(R.id.dialog_destination_details_edit_text);

		mDest_Contct_Details.addTextChangedListener(new PhoneNumberTextWatcher(mDest_Contct_Details));
		//mDest_Contct_Details.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

		driverCheckBox = (CheckBox) mDialogShow.findViewById(R.id.dialog_with_height_profesnl_driver_check_box);
		// id for below check box acc proffesional driver
		mHeightEditText = (EditText) mDialogShow.findViewById(R.id.dialog_with_height_edit_text);
		mLengthEditText = (EditText) mDialogShow.findViewById(R.id.dialog_with_length_edit_text);
		mWidthEditText = (EditText) mDialogShow.findViewById(R.id.dialog_with_width_edit_text);
		if (Utility.getSharedPrefStringData(HomeActivity.this, "driverrtype").equalsIgnoreCase("1"))
		{

			driverCheckBox.setChecked(true);
			belowDriverCheckLinearLayout.setVisibility(View.VISIBLE);
			/*Utility.setSharedPrefStringData(mContext, "height", jObject.optString("TruckHeight"));
					Utility.setSharedPrefStringData(mContext, "width", jObject.optString("TruckWidth"));
					Utility.setSharedPrefStringData(mContext, "length", jObject.optString("TruckLength"));*/

			mHeightEditText.setText(Utility.getSharedPrefString(HomeActivity.this, "height"));
			mLengthEditText.setText(Utility.getSharedPrefString(HomeActivity.this, "length"));
			mWidthEditText.setText(Utility.getSharedPrefString(HomeActivity.this, "width"));

		}
		else
		{
			driverCheckBox.setChecked(false);
			belowDriverCheckLinearLayout.setVisibility(View.GONE);
		}

		mWidthUnitTv = (TextView) mDialogShow.findViewById(R.id.dialog_with_width_unit_text_view);
		mHeightUnitTv = (TextView) mDialogShow.findViewById(R.id.dialog_with_height_unit_text_view);
		mLengthUnitTv = (TextView) mDialogShow.findViewById(R.id.dialog_with_length_unit_text_view);
		LinearLayout add_description_linear_layout = (LinearLayout) mDialogShow.findViewById(R.id.dialog_with_add_more_destinton_layout);
		mDropDownLinearLayout = (LinearLayout) mDialogShow.findViewById(R.id.form_dropdown_container_layout);
		mAddMorebelowDropDownLayout = (LinearLayout) mDialogShow.findViewById(R.id.form_dropdown_container_add_more_below_layout);
		LinearLayout below_add_des_linear_layout = (LinearLayout) mDialogShow.findViewById(R.id.below_add_descrptn_linear_layout);
		showBelowAddlayout(below_add_des_linear_layout, add_description_linear_layout, addMoreDesCrptn);
		mAddnewtripBtnSend = (Button) mDialogShow.findViewById(R.id.dialog_with_enter_text_view);
		Utility.textViewFontRobotoLight(tvLine1, getAssets());
		Utility.textViewFontRobotoLight(destTripTextViewBelowAddMore, getAssets());
		Utility.textViewFontRobotoLight(heightTextView, getAssets());
		Utility.textViewFontRobotoLight(lengthTextView, getAssets());
		Utility.textViewFontRobotoLight(widthTextView, getAssets());
		Utility.textViewFontRobotoLight(tripNameTextView, getAssets());
		Utility.textViewFontRobotoLight(destEndPointTextView, getAssets());
		Utility.textViewFontRobotoLight(destContctDetailtextview, getAssets());
		Utility.textViewFontRobotoLight(addMoreDesCrptn, getAssets());
		Utility.textViewFontRobotoLight(mAddnewtripBtnSend, getAssets());
		driverCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				if (isChecked)
					belowDriverCheckLinearLayout.setVisibility(View.VISIBLE);
				else
					belowDriverCheckLinearLayout.setVisibility(View.GONE);

			}
		});
		mDialogShow.show();

	}

	private void showBelowAddlayout(final LinearLayout below_add_des_linear_layout, final LinearLayout mDropDownLinearLayout, final TextView addMoreDesCrptn)
	{
		mDropDownLinearLayout.setTag(0);
		mDropDownLinearLayout.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				int value = (Integer) mDropDownLinearLayout.getTag();
				if (value == 0)
				{
					addMoreDesCrptn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.minus_icon, 0, 0, 0);
					below_add_des_linear_layout.setVisibility(View.VISIBLE);
					mDropDownLinearLayout.setTag(1);
				}
				else
				{
					addMoreDesCrptn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.cross_icon_popup, 0, 0, 0);
					below_add_des_linear_layout.setVisibility(View.GONE);
					mDropDownLinearLayout.setTag(0);
				}
			}
		});
	}

	/*	private void checkProfesonalDriverTrueOrfalse(CheckBox driverCheckBox, LinearLayout belowDriverCheckLinearLayout)
		{
			if (driverCheckBox.isChecked())
			{
				belowDriverCheckLinearLayout.setVisibility(View.VISIBLE);
			}
			else
			{
				belowDriverCheckLinearLayout.setVisibility(View.GONE);
			}
		}*/

	public class SearchLocataionNameAsyncTask extends AsyncTask<Void, Void, ArrayList<String>>
	{
		String inputString = "";

		public SearchLocataionNameAsyncTask(String input)
		{
			inputString = input;
		}

		@Override
		protected void onPreExecute()
		{

		}

		@Override
		protected ArrayList<String> doInBackground(Void... params)
		{
			return autocomplete(inputString);
		}

		@Override
		protected void onPostExecute(ArrayList<String> result)
		{

			// if (mShowLocationDialog != null &&
			// mShowLocationDialog.isShowing())
			// {
			// mShowLocationDialog.dismiss();
			// }
			if (resultList != null && resultList.size() > 0)
			{
				// mShowLocationDialog.dismiss();
				// by viash
				if (proceed)
				{
					mAddMorebelowDropDownLayout.setVisibility(View.VISIBLE);
					mAddMorebelowDropDownLayout.removeAllViews();
					makeCustomReservationListView(mAddMorebelowDropDownLayout, resultList);
				}
				else
				{
					mDropDownLinearLayout.setVisibility(View.VISIBLE);
					mDropDownLinearLayout.removeAllViews();
					makeCustomReservationListView(mDropDownLinearLayout, resultList);
				}
			}
			else
			{
				if (proceed)
				{
					mAddMorebelowDropDownLayout.removeAllViews();
					mAddMorebelowDropDownLayout.setVisibility(View.GONE);
				}
				else
				{
					mDropDownLinearLayout.removeAllViews();
					mDropDownLinearLayout.setVisibility(View.GONE);
				}
			}

			super.onPostExecute(result);
		}

		/**
		 * @param dropDownLinearLayout
		 * @param dropDownLayout
		 * @param resultList
		 */
		private void makeCustomReservationListView(LinearLayout dropDownLinearLayout, final ArrayList<String> resultList)
		{
			LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (resultList.size() >= 3)
			{
				locationDetailCount = 3;
			}
			else
			{
				locationDetailCount = resultList.size();
			}
			try
			{
				showlist(inflater, dropDownLinearLayout);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

		}

		private void showlist(LayoutInflater inflater, LinearLayout dropDownLinearLayout)
		{
			for (int i = 0; i < locationDetailCount; i++)
			{
				View vw = inflater.inflate(R.layout.vehicle_type_row, null);
				LinearLayout layout = (LinearLayout) vw.findViewById(R.id.vehicleType_LLayout);
				TextView placeTextView = (TextView) vw.findViewById(R.id.vehicleName_textView);
				placeTextView.setText(resultList.get(i));
				placeTextView.setTextSize(15);
				placeTextView.setTextColor(getApplicationContext().getResources().getColor(R.color.black));
				placeTextView.setTag(i);
				dropDownLinearLayout.addView(layout);

				placeTextView.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						try
						{
							int position = (Integer) v.getTag();
							mLastSearchString = resultList.get(position); // BUG
							Log.e("mLastSearchString.....", mLastSearchString);
							if (proceed)
							{
								mDestTripEdBelowAddMore.setText(mLastSearchString);
								proceed = false;
								mAddMorebelowDropDownLayout.setVisibility(View.GONE);
							}
							else
							{
								addnewtrip_ed_DestName.setText(mLastSearchString);
								proceed = true;
								mDropDownLinearLayout.setVisibility(View.GONE);
							}

						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				});
			}

		}
	}

	// Method for getting the location search data from google service...
	private ArrayList<String> autocomplete(String input)
	{
		if (resultList != null)
		{
			resultList.clear();
		}
		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		try
		{
			if (input.contains("&"))
			{
				input = input.replace("&", "and");
			}

			StringBuilder sb = new StringBuilder(APIUtils.PLACES_API_BASE + APIUtils.TYPE_AUTOCOMPLETE + APIUtils.OUT_JSON);
			sb.append("?sensor=true&key=" + "AIzaSyDW6oap94w-64puqB0FxroyoeaN7ood9zo");
			sb.append("&language=en");
			sb.append("&input=" + URLEncoder.encode(input, "utf8"));
			Log.e("place api url...", sb.toString());

			URL url = new URL(sb.toString());
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());

			// Load the results into a StringBuilder
			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1)
			{
				jsonResults.append(buff, 0, read);
			}
		}
		catch (MalformedURLException e)
		{
			Log.e("LOG_TAG", "Error processing Places API URL", e);
			return resultList;
		}
		catch (Exception e)
		{
			Log.e("LOG_TAG", "Error connecting to Places API", e);
			return resultList;
		}
		finally
		{
			if (conn != null)
			{
				conn.disconnect();
			}
		}
		try
		{
			Log.e("jsonResults", jsonResults + "");
			JSONObject jsonObj = new JSONObject(jsonResults.toString());
			JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");// results

			// Extract the Place descriptions from the results
			resultList = new ArrayList<String>();
			if (predsJsonArray.length() > 0)
			{
				for (int i = 0; i < predsJsonArray.length(); i++)
				{
					JSONObject jsonObjadd = predsJsonArray.getJSONObject(i);
					String name = jsonObjadd.getString("description");
					resultList.add(name);
				}
			}
			else
			{
				resultList.clear();
			}

		}
		catch (JSONException e)
		{
			Log.e("LOG_TAG", "Cannot process JSON results", e);

		}
		Log.e("resultList", resultList + "");
		return resultList;
	}

	private boolean checkifValid(String tripname, String destname, String destContactDetail, String destSndDtl)
	{

		//driverCheckBox
		//mHeightEditText
		//mLengthEditText
		//mWidthEditText

		if (driverCheckBox.isChecked())
		{

			if (mHeightEditText.equals(""))
			{
				Utility.showMsgDialog(HomeActivity.this, "Please enter height!");
				return false;
			}
			else
				if (mLengthEditText.equals(""))
				{
					Utility.showMsgDialog(HomeActivity.this, "Please enter length!");
					return false;
				}
				else
					if (mWidthEditText.equals(""))
					{
						Utility.showMsgDialog(HomeActivity.this, "Please enter width!");
						return false;
					}

		}

		if (tripname.equals(""))
		{

			Utility.showMsgDialog(HomeActivity.this, "Please enter trip name!");
			return false;
		}
		else
			if (destname.equals(""))
			{
				Utility.showMsgDialog(HomeActivity.this, "Please enter destination name!");
				return false;
			}
			else
				if (destContactDetail.equals(""))
				{
					Utility.showMsgDialog(HomeActivity.this, "Please enter destination contact detail!");
					return false;

				}
				else
					if (!tripname.matches("^.{2,}$"))
					{
						Utility.showMsgDialog(HomeActivity.this, "Trip name is too short!");
						return false;
					}
					else
						if (!destname.matches("^.{2,}$"))
						{
							Utility.showMsgDialog(HomeActivity.this, "Destination name is too short!");
							return false;
						}
						/*						else
													if (locationStringMoreDestination != null && locationStringMoreDestination.length() > 0)
													{
														if (destSndDtl.length() > 0)
														{
															return true;
														}
														else
														{
															Utility.showMsgDialog(HomeActivity.this, "Please enter destination contact detail!");
															return false;
														}
													}*/

						else
							return true;
	}

	private void getCurrentTripService()
	{
		mCustomDialog = new CustomProgressDialog(HomeActivity.this, "Please wait");
		mCustomDialog.setCancelable(false);
		new Thread(new Runnable()
		{

			String response = "";

			@Override
			public void run()
			{

				JSONObject CurrentScreenJsonObject = new JSONObject();
				try
				{
					CurrentScreenJsonObject.put("UserId", Utility.getSharedPrefString(HomeActivity.this, "userId"));
					CurrentScreenJsonObject.put("DeviceToken", SplashActivity.regId);

				}
				catch (JSONException e)
				{
					e.printStackTrace();
				}
				Log.e("params for first", CurrentScreenJsonObject.toString());
				String url = APIUtils.BASE_URL + APIUtils.CURRENTRIP;
				response = Utility.POST(url, CurrentScreenJsonObject.toString(), APIUtils.CURRENTRIP);
				Log.e("response", response.toString());
				mCurrentMapScreenHandler.post(new Runnable()
				{
					private String msg;

					@Override
					public void run()
					{
						mCustomDialog.dismissDialog();

						if (response != null && response.length() > 0)
						{
							Log.e("msg", "is:" + response);

							parser = new FinishTripParser();

							try
							{

								JSONObject responseJsonObject = new JSONObject(response);
								if (responseJsonObject.optBoolean("Success"))
								{
									if (responseJsonObject.getJSONArray("Result") != null && responseJsonObject.getJSONArray("Result").length() > 0)
									{

										Constant.mCurrentTripDataBean = (FinishedTripDataBean) parser.parse(response);
										Utility.setSharedPrefStringData(HomeActivity.this, "TripId", Constant.mCurrentTripDataBean.getTripArray().get(0).getTripId());

										Log.e("trip id from current trip", "is:" + Constant.mCurrentTripDataBean.getTripArray().get(0).getTripId());
										Intent currentTripIntent = new Intent(HomeActivity.this, MapScreenActivity.class);
										currentTripIntent.putExtra("currentTrip", "1");
										// currentTripIntent.putExtra(name,
										// value)
										startActivity(currentTripIntent);
									}
									else
									{

										Utility.showMsgDialog(HomeActivity.this, "Currently! You do not have any pending trip.");
									}

								}
								else
								{
									msg = responseJsonObject.optString("Message");
									Utility.showMsgDialog(HomeActivity.this, msg);
								}

							}
							catch (JSONException e)
							{
								e.printStackTrace();
							}

						}
						else
						{

						}

					}
				});

			}
		}).start();
	}

	private void getMapScreen()
	{

		mCustomDialog = new CustomProgressDialog(HomeActivity.this, "");
		gpsTracker = new GPSTracker(HomeActivity.this);

		latlng_Source = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());

		Log.e("location", "is:" + latlng_Source);
		new Thread(new Runnable()
		{

			String response = "";

			@Override
			public void run()
			{

				/*"UserId": "sample string 1",
				  "Name": "sample string 2",
				  "UserType": "sample string 3",
				  "DeviceToken": "sample string 4",
				  "StartLongitude": "sample string 5",
				  "StartLatitude": "sample string 6",
				  "StartPointName": "sample string 7",
				  "EndLongitude": "sample string 11",
				  "EndLatitude": "sample string 12",
				  "EndPointName": "sample string 13",
				  
				  "EndFirstLongitude": "sample string 8",
				  "EndFirstLatitude": "sample string 9",
				  "EndFirstPointName": "sample string 10",
				  
				  "Type": "sample string 14",
				  "Height": "sample string 15",
				  "Width": "sample string 16",
				  "length": "sample string 17"*/

				JSONObject loginJsonObject = new JSONObject();
				try
				{
					loginJsonObject.put("UserId", Utility.getSharedPrefStringData(HomeActivity.this, "userId"));
					loginJsonObject.put("Name", addnewtrip_ed_TripName.getText().toString().trim());
					loginJsonObject.put("StartLongitude", String.valueOf(gpsTracker.getLongitude()));
					loginJsonObject.put("StartLatitude", String.valueOf(gpsTracker.getLatitude()));
					loginJsonObject.put("EndLongitude", String.valueOf(mDestLongitude));
					loginJsonObject.put("EndLatitude", String.valueOf(mDestLatitude));
					loginJsonObject.put("StartPointName", location_string);
					loginJsonObject.put("EndPointName", locationStringDestination);
					loginJsonObject.put("DeviceToken", SplashActivity.regId);

					loginJsonObject.put("EndFirstCntDetails ", mSecondContatctDetail);
					loginJsonObject.put("EndCntDetails", destContactDetail);
					loginJsonObject.put("IsCancel", isCancel);

					if (driverCheckBox.isChecked())
					{
						loginJsonObject.put("DriverType", "1");
						loginJsonObject.put("Height", mHeightEditText.getText().toString().trim());
						loginJsonObject.put("Width", mWidthEditText.getText().toString().trim());
						loginJsonObject.put("length", mLengthEditText.getText().toString().trim());
					}
					else
					{
						if (!Utility.getSharedPrefString(HomeActivity.this, "driverrtype").equalsIgnoreCase("1"))
							loginJsonObject.put("DriverType", "2");
						loginJsonObject.put("Height", "");
						loginJsonObject.put("Width", "");
						loginJsonObject.put("length", "");
					}

					if (locationStringMoreDestination.trim().equals(""))
					{
						loginJsonObject.put("EndFirstLongitude", "");
						loginJsonObject.put("EndFirstLatitude", "");
					}
					else
					{
						loginJsonObject.put("EndFirstLongitude", String.valueOf(mMoreDestLongitude));
						loginJsonObject.put("EndFirstLatitude", String.valueOf(mMoreDestLatitude));
					}
					loginJsonObject.put("EndFirstPointName", locationStringMoreDestination);

					/*
					 * "StartPointName": "sample string 7", "EndPointName":
					 * "sample string 8"
					 */

				}
				catch (JSONException e)
				{
					e.printStackTrace();
				}
				Log.e("params for first", loginJsonObject.toString());
				String url = APIUtils.BASE_URL + APIUtils.NEWTRIP;
				response = Utility.POST(url, loginJsonObject.toString(), APIUtils.NEWTRIP);
				Log.e("response", response.toString());
				mEnterMapScreenHandler.post(new Runnable()
				{
					private String msg;

					@Override
					public void run()
					{
						mCustomDialog.dismissDialog();

						if (response != null && response.length() > 0)
						{
							Log.e("msg", "is:" + response);

							try
							{
								JSONObject responseJsonObject = new JSONObject(response);
								if (responseJsonObject.optBoolean("Success"))
								{
									JSONObject resultJsonObject = responseJsonObject.getJSONObject("Result");
									mTripId = resultJsonObject.optString("TripId");
									Utility.setSharedPrefStringData(HomeActivity.this, "TripId", mTripId);
									if (resultJsonObject.optString("Isexisting").equalsIgnoreCase("true"))
									{
										//put the code if isexisting true
										getpopupReuseTrip(resultJsonObject);
									}
									else
									{

										isCancel = "false";
										if (driverCheckBox.isChecked())
										{

											Utility.setSharedPrefStringData(HomeActivity.this, "driverrtype", "1");
											Utility.setSharedPrefStringData(HomeActivity.this, "height", mHeightEditText.getText().toString().trim());
											Utility.setSharedPrefStringData(HomeActivity.this, "width", mWidthEditText.getText().toString().trim());
											Utility.setSharedPrefStringData(HomeActivity.this, "length", mLengthEditText.getText().toString().trim());
										}

										msg = responseJsonObject.optString("Message");

										Intent startProfileActivity = new Intent(HomeActivity.this, MapScreenActivity.class);
										startActivity(startProfileActivity);

									}

								}
								else
								{
									msg = responseJsonObject.optString("Message");
									Utility.showMsgDialog(HomeActivity.this, msg);
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

	private void getpopupReuseTrip(JSONObject responseJsonObject)
	{
		String msg = "", height = "", width = "", length = "";
		msg = "Trip Info:" + System.getProperty("line.separator");
		msg = msg + "Trip Name: " + responseJsonObject.optString("TripName") + System.getProperty("line.separator");
		if (!responseJsonObject.optString("TruckHeight").equalsIgnoreCase(""))
		{
			height = responseJsonObject.optString("TruckHeight");
			width = responseJsonObject.optString("TruckWidth");
			length = responseJsonObject.optString("TruckLength");

			msg = msg + System.getProperty("line.separator") + "TruckHeight: " + height + "FT" + System.getProperty("line.separator") + "TruckWidth: " + width + "FT"
					+ System.getProperty("line.separator") + "TruckLength: " + length + "FT" + System.getProperty("line.separator");

		}

		if (!responseJsonObject.optString("DestinationPhoneNo").equalsIgnoreCase(""))
		{

			msg = msg + System.getProperty("line.separator") + "Destination Phone: " + responseJsonObject.optString("DestinationPhoneNo") + System.getProperty("line.separator");
		}

		if (!responseJsonObject.optString("StartPointName").equalsIgnoreCase(""))
		{

			msg = msg + System.getProperty("line.separator") + "Start Point/Warehouse/Store: " + responseJsonObject.optString("StartPointName") + System.getProperty("line.separator");
		}

		if (!responseJsonObject.optString("DestinationPointName").equalsIgnoreCase(""))
		{

			msg = msg + System.getProperty("line.separator") + "Destination Address: " + responseJsonObject.optString("DestinationPointName") + System.getProperty("line.separator");
		}

		msg = msg + System.getProperty("line.separator") + "Route Safe for Truck/Trailer: " + responseJsonObject.optString("Status") + System.getProperty("line.separator");

		if (!responseJsonObject.optString("ImpInformation").equalsIgnoreCase(""))
		{
			msg = msg + System.getProperty("line.separator") + "Important safety Info List: " + System.getProperty("line.separator") + responseJsonObject.optString("ImpInformation")
					+ System.getProperty("line.separator");
		}

		if (!responseJsonObject.optString("UnloadingAddress").equalsIgnoreCase(""))
		{

			msg = msg + System.getProperty("line.separator") + "Unloading Address: " + responseJsonObject.optString("UnloadingAddress") + System.getProperty("line.separator");
		}

		if (!responseJsonObject.optString("Comments").equalsIgnoreCase(""))
		{

			msg = msg + System.getProperty("line.separator") + "Comments: " + responseJsonObject.optString("Comments") + System.getProperty("line.separator");
		}

		dailog = CustomDialog.getInstance(HomeActivity.this, this, msg + System.getProperty("line.separator") + "This Trip Already exist! Do you want to Reuse it? ", "Mo-Bia", DIALOG_TYPE.OK_CANCEL,
				1);
		dailog.show();
	}

	private void getNewDrawLine(final LatLng mNewLatLngSource, final LatLng mNewLatLngDestination, final String color, final CustomProgressDialog mCustomDialog)
	{

		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				final PolylineOptions rectLine = new PolylineOptions();

				ArrayList<LatLng> directionPoint = null;
				try
				{
					doc = gmapv.getDocument(mNewLatLngSource, mNewLatLngDestination, GMapV2Direction.MODE_DRIVING);
					directionPoint = gmapv.getDirection(doc);

					rectLine.width(15).color(Color.parseColor(color));

					Log.i("size points", "size" + directionPoint.size());
					for (int j = 0; j < directionPoint.size(); j++)
					{

						rectLine.add(directionPoint.get(j));

					}

				}
				catch (ClientProtocolException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (ParserConfigurationException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (SAXException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				mMapHandler.post(new Runnable()
				{

					@Override
					public void run()
					{
						poly = mMap.addPolyline(rectLine);
						mCustomDialog.dismissDialog();
						//mRoutListLatLngs = gmapv.getDirection(doc);

					}
				});

			}
		}).start();

	}

	void putNewPointonMap(final String id, final double lat, final double log)
	{
		mPinHandler.post(new Runnable()
		{

			@Override
			public void run()
			{
				/*if ((mapMarkerCount % 2) == 0)
				{*/
				mMap.addMarker(new MarkerOptions().position(new LatLng(lat, log)).snippet(id));
				mMap.setOnMarkerClickListener(new OnMarkerClickListener()
				{

					@Override
					public boolean onMarkerClick(Marker arg0)
					{

						if (arg0.getSnippet() != null && arg0.getSnippet().length() > 0 && !arg0.getSnippet().contains("Category"))
						{
							getInfoLayout(arg0.getSnippet());
							return true;
						}
						return false;
					}

				});
				/*}
				else
				{
					mMap.addMarker(mNewMarkersource);
				}*/
			}
		});
	}

	private void getInfoLayout(final String id)
	{
		mCustomDialog = new CustomProgressDialog(HomeActivity.this, "Please Wait..");
		mCustomDialog.setCancelable(false);
		new Thread(new Runnable()
		{

			String response = "";

			@Override
			public void run()
			{

				JSONObject cancelJsonObject = new JSONObject();
				try
				{

					cancelJsonObject.put("id", id);

				}
				catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.e("params for first", cancelJsonObject.toString());
				String url = APIUtils.BASE_URL + APIUtils.GET_PLACE_INFO;
				response = Utility.POST(url, cancelJsonObject.toString(), APIUtils.GET_PLACE_INFO);
				Log.e("response", response.toString());
				mCancelHandler.post(new Runnable()
				{

					private String msg;

					@Override
					public void run()
					{
						mCustomDialog.dismissDialog();

						if (response != null && response.length() > 0)
						{
							Log.e("msg", "is:" + response);

							try
							{
								JSONObject responseJsonObject = new JSONObject(response);
								if (responseJsonObject.optBoolean("Success"))
								{

									JSONObject resultJsonObject = responseJsonObject.getJSONObject("Result");
									showInfoDialog(resultJsonObject);

								}
								else
								{
									msg = responseJsonObject.optString("Message");
									Utility.showMsgDialog(HomeActivity.this, msg);
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

	private void showInfoDialog(JSONObject resultJsonObject)
	{
		try
		{
			final Dialog dialog = new Dialog(HomeActivity.this);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.dialog_shared_post_details);
			dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
			dialog.setCancelable(false);

			TextView mTextViewPlaceName, mTextViewTitle, mTextViewDescData, mTextViewOk;
			ImageView mImageViewPhoto, mImageViewVideo, mImageviewClose;

			mTextViewPlaceName = (TextView) dialog.findViewById(R.id.dialog_shared_post_details_textview_placename);
			mTextViewTitle = (TextView) dialog.findViewById(R.id.dialog_shared_post_details_textview_title);
			mTextViewDescData = (TextView) dialog.findViewById(R.id.dialog_shared_post_details_textview_description_data);
			mTextViewOk = (TextView) dialog.findViewById(R.id.dialog_shared_post_details_textView_ok);

			Utility.textViewFontRobotoLight(mTextViewPlaceName, getAssets());
			Utility.textViewFontRobotoLight(mTextViewTitle, getAssets());
			Utility.textViewFontRobotoLight(mTextViewDescData, getAssets());
			Utility.textViewFontRobotoLight(mTextViewOk, getAssets());
			Utility.textViewFontRobotoLight((TextView) dialog.findViewById(R.id.dialog_shared_post_details_textview_name), getAssets());
			Utility.textViewFontRobotoLight((TextView) dialog.findViewById(R.id.dialog_shared_post_details_textview_description), getAssets());

			mImageViewPhoto = (ImageView) dialog.findViewById(R.id.dialog_shared_post_details_imageview_photo);
			mImageViewVideo = (ImageView) dialog.findViewById(R.id.dialog_shared_post_details_imageview_video);
			mImageviewClose = (ImageView) dialog.findViewById(R.id.dialog_shared_post_details_imageview_close_dialog);

			mTextViewPlaceName.setText(resultJsonObject.optString("locationName") + "");
			mTextViewTitle.setText(resultJsonObject.optString("Title") + "");
			mTextViewDescData.setText(resultJsonObject.optString("comment") + "");

			final String imageUrl = resultJsonObject.optString("image");
			final String videoUrl = resultJsonObject.optString("video");
			final String audioUrl = resultJsonObject.optString("audio");

			if (imageUrl != null && imageUrl.length() > 0)
			{
				mImageViewPhoto.setImageDrawable(getResources().getDrawable(R.drawable.photo_icon_select));
			}

			if ((videoUrl != null && videoUrl.length() > 0) || (audioUrl != null && audioUrl.length() > 0))
			{
				mImageViewVideo.setImageDrawable(getResources().getDrawable(R.drawable.video_icon_select));
			}

			mTextViewOk.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					dialog.dismiss();

				}
			});
			mImageviewClose.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					dialog.dismiss();

				}
			});

			mImageViewPhoto.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					if (imageUrl.length() > 0)
						getPhotoDialog(imageUrl);

				}
			});

			mImageViewVideo.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					if (videoUrl.length() > 0)
					{
						openVideo(videoUrl);
					}
					else
						if (audioUrl.length() > 0)
						{
							openAudio(audioUrl);
						}

				}
			});

			dialog.show();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	private void getPhotoDialog(String imageurl)
	{

		final Dialog mDialogShow = new Dialog(this);
		mDialogShow.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mDialogShow.setContentView(R.layout.photo_display_dialog);
		mDialogShow.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		mDialogShow.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		mDialogShow.setCancelable(false);

		ImageView mCrossPhotoImageView = (ImageView) mDialogShow.findViewById(R.id.cross_imgvw);
		ImageView mPhotoDisplayImageView = (ImageView) mDialogShow.findViewById(R.id.main_imgvw);
		//		mPhotoDisplayImageView.setBackgroundDrawable(drawable);
		Log.d("Imageurl", imageurl);
		mDialogShow.show();
		imageLoader.displayImage(imageurl, mPhotoDisplayImageView, Utility.getFitXYDisplayOption(), Utility.getImageLoaderListner(HomeActivity.this));

		// Close Add New Trip Screen

		mCrossPhotoImageView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				mDialogShow.dismiss();
			}
		});

	}

	void openAudio(String audiourl)
	{
		try
		{
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
			Uri uri = Uri.parse(audiourl);
			intent.setDataAndType(uri, "audio/*");
			startActivity(intent);
		}
		catch (Exception e)
		{
			// TODO: handle exception
			Toast.makeText(this, "Error connecting", Toast.LENGTH_SHORT).show();
		}
	}

	void openVideo(String videourl)
	{
		try
		{
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
			Uri uri = Uri.parse(videourl);
			intent.setDataAndType(uri, "video/*");
			startActivity(intent);
		}
		catch (Exception e)
		{
			// TODO: handle exception
			Toast.makeText(this, "Error connecting", Toast.LENGTH_SHORT).show();
		}
	}

	private void getReuseTrip()
	{
		mCustomDialog = new CustomProgressDialog(HomeActivity.this, "Please wait..");
		mCustomDialog.setCancelable(false);
		new Thread(new Runnable()
		{
			String response = "";

			@Override
			public void run()
			{

				JSONObject jsonObj = new JSONObject();
				try
				{
					jsonObj.put("Userid", Utility.getSharedPrefString(HomeActivity.this, "userId"));
					jsonObj.put("tripId", mTripId);
					//trip id

				}
				catch (Exception e)
				{
					// TODO: handle exception
				}

				Log.e("params for first", jsonObj.toString());
				String url = APIUtils.BASE_URL + APIUtils.REUSE_TRIP;
				Log.e("login url", url);
				response = Utility.POST(url, jsonObj.toString(), APIUtils.REUSE_TRIP);
				Log.e("login url", response.toString());

				mCancelHandler.post(new Runnable()
				{
					private String msg;

					@Override
					public void run()
					{
						mCustomDialog.dismissDialog();

						if (response != null && response.length() > 0)
						{
							Log.e("msg", "is:" + response);

							parser = new FinishTripParser();

							try
							{

								JSONObject responseJsonObject = new JSONObject(response);
								if (responseJsonObject.optBoolean("Success"))
								{
									if (responseJsonObject.getJSONArray("Result") != null && responseJsonObject.getJSONArray("Result").length() > 0)
									{

										Constant.mCurrentTripDataBean = (FinishedTripDataBean) parser.parse(response);
										Utility.setSharedPrefStringData(HomeActivity.this, "TripId", Constant.mCurrentTripDataBean.getTripArray().get(0).getTripId());

										Log.e("trip id from current trip", "is:" + Constant.mCurrentTripDataBean.getTripArray().get(0).getTripId());
										Intent currentTripIntent = new Intent(HomeActivity.this, MapScreenActivity.class);
										currentTripIntent.putExtra("currentTrip", "1");
										currentTripIntent.putExtra("reuseTrip", "2");
										// currentTripIntent.putExtra(name,
										// value)
										startActivity(currentTripIntent);
									}
									else
									{

										Utility.showMsgDialog(HomeActivity.this, "Currently! You do not have any pending trip.");
									}

								}
								else
								{
									msg = responseJsonObject.optString("Message");
									Utility.showMsgDialog(HomeActivity.this, msg);
								}

							}
							catch (JSONException e)
							{
								e.printStackTrace();
							}

						}
						else
						{

						}

					}
				});

			}
		}).start();

	}

	@Override
	public void onActionOk(int requestCode)
	{
		if (Utility.isNetworkAvailable(HomeActivity.this))
			getReuseTrip();
		else
			Utility.showToastMessage(HomeActivity.this, getResources().getString(R.string.msg_netork_error));

	}

	@Override
	public void onActionCancel(int requestCode)
	{
		// create a new trip service 
		isCancel = "true";
		getMapScreen();

	}

	@Override
	public void onActionNeutral(int requestCode)
	{
		// TODO Auto-generated method stub

	}

}