package com.ta.truckmap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.ta.truckmap.adapter.FinishTripImportentInfoListAdapter;
import com.ta.truckmap.gpstracking.GMapV2Direction;
import com.ta.truckmap.gpstracking.GPSTracker;
import com.ta.truckmap.rest.api.APIUtils;
import com.ta.truckmap.util.Constant;
import com.ta.truckmap.util.CustomDialog;
import com.ta.truckmap.util.CustomProgressDialog;
import com.ta.truckmap.util.ImageCaputureUtility;
import com.ta.truckmap.util.LoadAdViewInEveryActivity;
import com.ta.truckmap.util.SetVideoFromCamera;
import com.ta.truckmap.util.Utility;
import com.truckmap.model.FinishedTripDataBean;
import com.truckmap.parsers.FinishTripParser;
import com.truckmap.parsers.Parser;

public class MapScreenActivity extends BaseActivity implements OnClickListener, OnMapClickListener, CallBack
{

	ImageView mapScreen_ui_backImgview1, mCancelColorPopupImageView, mMapScreenEditImageView, mapScreen_ui_shareImgview1, mapScreen_ui_closeImgview1, mapScreen_ui_refreshImgview1,
			mfinishTrip_ImageViewCancel, mMapScreenCancelTripImageView;
	EditText mDrowLineCommentEditText /*mfinishTripdialog_comment*/;
	TextView mDrowLineShareTextView, mCategoryTextView, mColorOneTextView, mColorTwoTextView, mColorThreeTextView, mColorFourTextView, mColorFiveTextView, mColorSixTextView, mColorSevenTextView,
			mColorEightTextView, mfinishTripDialogTextView1, mfinishTripDialogTextView2, mfinishTripDialogYesBtn, mColor_dialogTextViewComment;

	Handler mExitTripHandler = new Handler();
	Handler mRefreshMapHandler = new Handler();
	Handler mRefreshMapHandlermarker = new Handler();

	GoogleMap mMap;
	Polyline poly;
	GPSTracker gpsTracker;
	Handler mMapHandler = new Handler();
	Handler mMapHandler1 = new Handler();
	Handler mMapHandlerAdd = new Handler();

	GMapV2Direction gmapv = new GMapV2Direction();
	List<Address> mLatLongSource, mLatLongDesination, mNewLatLongSource, mNewLatLongDesination;

	MarkerOptions markerSource, markerDestin, mNewMarkersource, mNewMarkerDestination, mSndSrsMarker;
	ArrayList<LatLng> mRoutListLatLngs = new ArrayList<LatLng>();

	ArrayList<LatLng> mRoutListLatLngsCurrentLine = new ArrayList<LatLng>();
	Document doc;
	Location getCurrentLocation;
	private int i = 0;
	LatLng mNewLatLngSource, mNewLatLngDestination, mPtLatLngSource, mPtLatLngDestination;
	private int mapMarkerCount = 0;
	Dialog mDialogShow;
	Handler mDrawLineHandler = new Handler();
	Handler mCategotyHandler = new Handler();
	Handler mCurrentTripMapHandler = new Handler();
	ProgressDialog mDrawLineProgressBar, mCategotyProgressDialog, mRefreshProgressDialog;
	List<HashMap<String, String>> mCategotyHashMaps;
	JSONArray mCategoryDataJsonArray = null;
	private String[] mCategoryArray = new String[0];
	protected String mSelectedCategoryId;
	Bundle mBundle;
	Marker now;
	LinearLayout mAddMobsLinearLayout;
	String mTripId, mName, mStartLongitude, mStartLatitude, mEndLongitude, mEndLatitude, mStartPointName, mEndPointName, mTripIdFinish;
	public LatLng mlatlng_Source, mlatlng_Dest, mCurrentTripLatLngSource, mCurrentTripLatLngDestination;
	ArrayList<String> mDrawLineArrayList;
	String mColorCode = "", mNewPinSourceString, mNewPinDestination, mReuseTrip = "0";
	Timer timer = new Timer();
	private CustomProgressDialog mCustomProgressDialog;
	Handler mPinHandler = new Handler();
	LatLng ponts;
	protected Parser<?> parser;
	Handler mCancelHandler = new Handler();
	Marker marker, mMoveingMarker;

	ProgressDialog cancelProgressDialog;
	private BitmapDescriptor mBitmapDescriptor;
	private boolean mCancelFlag = false;

	private ImageCaputureUtility imageCaptureRefrence = new ImageCaputureUtility();
	private SetVideoFromCamera mVidFromCamera;
	private TextView mTextViewPhoto, mTextViewVideo, closedialog, mTextViewEnterPost;
	private ImageView mImageViewPhoto, mImageViewPhotoCross, mImageViewVideo, mImageViewVideoCross;
	private RelativeLayout mRelLayoutPhoto, mRelLayoutVideo;
	private EditText mEditTextTitle, mEditTextComment;
	private String mImagePath = null, mSavePath, mVideoPath = "", mAudiopath = "";
	private ExifInterface exif;
	final int REQUEST_CODE_AUDIO = 1001;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;

	double prevLattitude, prevLongitude;
	double curLattitude, curLongitude;
	//private String totaldistance;
	private LatLng mCurrentLatLng, mPrevLatLng;
	//private Circle firstCircle,secondCircle;
	Marker markertemp1, markertemp2;
	private int count = 0;
	private boolean isCurrentUploadPoint = false, isRoutOkforTruck = false;
	//final String[] finishInfoItems = { "Call before arrival for store to make room parking", "No truck alloweds but ok", "Use 1st Entrance", "Use 2nd Entrance", "Front of building/store" };
	final String[] finishInfoItems = { "CALL BEFORE ARRIVAL FOR STORE TO MAKE ROOM PARKING", "NO TRUCKS ALLOWED BUT OK", "USE 1ST ENTRANCE", "USE 2ND ENTRANCE", "FRONT OF BUILDING/STORE",
			"SIDE OF BUILDING/STORE", "REAR OF BUILDING/STORE", "ALLEYWAY", "HAND OFF LOAD", "STREET UNLOADING", "COUNT", "POWER JACK/FORKLIFT", "HIGH RISK AREA/VERY TIGHT SPOT", "DOCK",
			"ON A HILL/AN ELEVATION", "BACKING FROM THE STREET BLINDSIDE", "BACKING FROM THE STREET SEESIDE", "BACKING INTO UNDERGROUND/BASEMENT.",
			"WATCHOUT FOR OVERHEAD TRAIN TRACKS /WIRES/DOORS/HANGERS", "DOUBLE PARKING OK", "ROOF TOP PARKING", "SEE GUARD", "DROP & HOOK", "DROP & GO/PULL OFF", "USE SERVICE RD",
			"GO UNDER TRAIN TRACKS AND USE SERVICE RD.( WATCHOUT FOR ARCH ).", "DO NOT GO UNDER TRAIN TRACKS AND JUST USE SERVICE RD. (WATCHOUT FOR ARCH).",
			"DO NOT GO UNDER TRAIN TRACKS, JUST PARK ON SIDE OF BUILDING/STORE", "LIFTGATE / SHORTEST TRAILER RECOMMENDED", "GET HELP BEFORE PARKING/EXITING", "RIGHT TURN BEFORE STORE/BUILDING",
			"RIGHT TURN AFTER STORE/BUILDING", "USE ONEWAY", "MUST HAVE POWER JACK TO TRAVEL WITH LOAD", "STORE/BULDING HAS POWER JACK/FORKLIFT", "SWING WIDE TO TURN/PARK",
			"DO NOT GO INTO PARKING LOT", "GO INTO PARKING LOT" };

	private String selectInfoText = "", finishComment = "";
	private ProgressDialog mProgressDialogCurrentTrip;
	protected Handler mCurrentMapScreenHandler = new Handler();
	private CustomDialog dailog;
	private Bitmap bmpOriginal;

	/*private SensorEventListener mSensorListener;
	private float mOrientation;*/

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		mCancelFlag = false;

		//bmpOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.truck_icon/* R.drawable.suvcar */);
		if (savedInstanceState == null)
		{

		}
		else
		{
			getCurrentTripService();
		}

		setContentView(R.layout.mapscreen_ui);

		mAddMobsLinearLayout = (LinearLayout) findViewById(R.id.add_mobs_lnrlyt);
		LoadAdViewInEveryActivity.loadAdds(this, mAddMobsLinearLayout);
		mapScreen_ui_backImgview1 = (ImageView) findViewById(R.id.mapscreen_ui_backImgview);
		mapScreen_ui_closeImgview1 = (ImageView) findViewById(R.id.mapscreen_ui_closeImgview);
		mapScreen_ui_refreshImgview1 = (ImageView) findViewById(R.id.mapscreen_ui_refreshImgview);
		mapScreen_ui_shareImgview1 = (ImageView) findViewById(R.id.mapscreen_ui_shareImgview);
		mMapScreenEditImageView = (ImageView) findViewById(R.id.mapscreen_ui_editImgview);
		mMapScreenCancelTripImageView = (ImageView) findViewById(R.id.mapscreen_ui_cancelImgview);

		if (Utility.getSharedPrefStringData(getApplicationContext(), "driverrtype").equalsIgnoreCase("1"))
		{
			mMapScreenEditImageView.setVisibility(View.VISIBLE);
			mapScreen_ui_shareImgview1.setVisibility(View.VISIBLE);
			mapScreen_ui_refreshImgview1.setVisibility(View.VISIBLE);

		}
		else
		{
			mMapScreenEditImageView.setVisibility(View.INVISIBLE);
			mapScreen_ui_shareImgview1.setVisibility(View.INVISIBLE);
			mapScreen_ui_refreshImgview1.setVisibility(View.INVISIBLE);
		}

		mMapScreenEditImageView.setOnClickListener(this);
		mapScreen_ui_backImgview1.setOnClickListener(this);
		mapScreen_ui_closeImgview1.setOnClickListener(this);
		mapScreen_ui_refreshImgview1.setOnClickListener(this);
		mapScreen_ui_shareImgview1.setOnClickListener(this);
		mMapScreenCancelTripImageView.setOnClickListener(this);
		initializeImageLoader();
		checkgps(savedInstanceState);

		/*	mSensorListener = new SensorEventListener()
			{

				@Override
				public void onSensorChanged(SensorEvent event)
				{
					mOrientation = event.values[0];
					draw(mOrientation);
				}

				@Override
				public void onAccuracyChanged(Sensor sensor, int accuracy)
				{
				}
			};
			setupSensorManager();*/

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

	private void initializeImageLoader()
	{
		options = new DisplayImageOptions.Builder().resetViewBeforeLoading()
		/* .showImageForEmptyUri(R.drawable.opacity) */.cacheInMemory().cacheOnDisc().build();
		imageLoader = ImageLoader.getInstance();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).threadPoolSize(1).threadPriority(1).memoryCacheSize(1500000) // 1.5 Mb
				.discCacheSize(50000000) // 50 Mb
				.denyCacheImageMultipleSizesInMemory().build();
		imageLoader.init(config);
		imageLoader.clearMemoryCache();
		imageLoader.clearDiscCache();
	}

	private void checkgps(Bundle savedInstanceState)
	{
		gpsTracker = new GPSTracker(MapScreenActivity.this, this);

		if (gpsTracker.canGetLocation())
		{

			setUpMapIfNeeded(savedInstanceState);

		}
		else
			gpsTracker.showSettingsAlert();
	}

	private void setUpMapIfNeeded(Bundle savedInstanceState)
	{
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null)
		{
			mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_relative_fragment)).getMap();
			mMap.isTrafficEnabled();
			mMap.isBuildingsEnabled();
			mMap.isIndoorEnabled();
			// Check if we were successful in obtaining the map.
			if (mMap != null)
			{

				if (getIntent().getExtras() != null)
				{
					if (savedInstanceState == null)
					{

						if (getIntent().getExtras().containsKey("reuseTrip"))
						{
							mReuseTrip = getIntent().getExtras().getString("reuseTrip");
							Utility.setSharedPrefStringData(MapScreenActivity.this, "reuse", "yes");
						}

						mTripIdFinish = Constant.mCurrentTripDataBean.getTripArray().get(0).getTripId();
						Utility.setSharedPrefStringData(MapScreenActivity.this, "currentTripId", mTripIdFinish);
						setUpCurrentMap();
					}

					else
						mTripIdFinish = Utility.getSharedPrefStringData(MapScreenActivity.this, "currentTripId");

					mMap.setOnMapClickListener(MapScreenActivity.this);

				}
				else
				{
					mTripIdFinish = Utility.getSharedPrefStringData(MapScreenActivity.this, "TripId");
					mMap.setOnMapClickListener(MapScreenActivity.this);
					try
					{
						mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(HomeActivity.latlng_Source, 15));
						mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
						setUpMap();

					}
					catch (Exception e)
					{

						e.printStackTrace();

					}
					catch (Throwable e)
					{
						e.printStackTrace();
					}

				}

			}
		}
	}

	public void getservice()
	{

	}

	private void getTimer()
	{

		prevLattitude = gpsTracker.getLatitude();
		prevLongitude = gpsTracker.getLongitude();

		timer.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				Log.e("start the timer", "1111");
				String response = "";
				JSONObject jsonObj = new JSONObject();
				try
				{

					if (gpsTracker.canGetLocation())
					{
						jsonObj.put("Userid", Utility.getSharedPrefString(MapScreenActivity.this, "userId"));
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

				curLattitude = gpsTracker.getLatitude();
				curLongitude = gpsTracker.getLongitude();
				Log.e("current latlong", curLattitude + " " + curLongitude);
				Log.e("previous latlong", prevLattitude + " " + prevLongitude);
				/*totaldistance = gpsTracker.getDistanceInXML(prevLattitude, prevLongitude, curLattitude, curLongitude);*/
				/*Log.e("distance", totaldistance);*/

				if (response != null && response.length() > 0)
				{
					Log.e("response", response.toString());

					try
					{
						JSONObject jsonObject = new JSONObject(response);
						if (jsonObject.optString("Success").equalsIgnoreCase("true"))
						{

							//							getNewthread();/*if (Integer.parseInt(totaldistance) > 0)
							//											{
							//											setUpMap();
							//											}
							//											*/
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

		}, 0, 10000);

		/*if (Utility.getSharedPrefStringData(getApplicationContext(), "childStatus").equalsIgnoreCase("pickedup"))
		{
			timer.cancel();
			Log.e("stop the timer", "1111");
		}*/

	}

	private void getNewthread(final LatLng prevLtg, final LatLng currentLtg)
	{
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				//test = mMap.addMarker(new MarkerOptions().position(new LatLng(curLattitude, curLongitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin)));

				// TODO Auto-generated method stub

				try
				{
					try
					{
						if (bmpOriginal != null)
						{
							Log.e("Bitmap recycled", "Hurre!!!! Bitmap recycled....");
							bmpOriginal.recycle();
							bmpOriginal = null;
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					bmpOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.truck_icon/* R.drawable.suvcar */);
					double rotationAngle = Math.atan2(currentLtg.longitude - prevLtg.longitude, currentLtg.latitude - prevLtg.latitude);

					Matrix matrix = new Matrix();
					matrix.postRotate((float) Math.toDegrees(rotationAngle));
					//					Bitmap bmpOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.truck_icon/* R.drawable.suvcar */);
					bmpOriginal = Bitmap.createBitmap(bmpOriginal, 0, 0, bmpOriginal.getWidth(), bmpOriginal.getHeight(), matrix, true);
					mBitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bmpOriginal);
				}
				catch (OutOfMemoryError e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				catch (Throwable e)
				{
					e.printStackTrace();
				}

				mRefreshMapHandlermarker.post(new Runnable()
				{

					@Override
					public void run()
					{
						if (mMoveingMarker != null)
						{
							mMoveingMarker.remove();
						}

						mMoveingMarker = mMap.addMarker(new MarkerOptions().position(currentLtg).icon(mBitmapDescriptor));
						if (mRoutListLatLngsCurrentLine != null && mRoutListLatLngsCurrentLine.size() > 0)
						{
							try
							{
								//								if(mRoutListLatLngsCurrentLine.size()>=2){
								//									LatLng previcousLatlong=mRoutListLatLngsCurrentLine.get(mRoutListLatLngsCurrentLine.size() - 2);
								//									
								//									
								//								}

								animateMarker(mMoveingMarker, mRoutListLatLngsCurrentLine.get(mRoutListLatLngsCurrentLine.size() - 1), false);
							}
							catch (Exception e)
							{
								// TODO: handle exception
							}

						}
					}
				});

			}
		}).start();

	}

	private void getCurrentTripService()
	{
		mCustomProgressDialog = new CustomProgressDialog(MapScreenActivity.this, "");

		final String id = Utility.getSharedPrefString(MapScreenActivity.this, "userId");
		new Thread(new Runnable()
		{

			String response = "";

			@Override
			public void run()
			{

				JSONObject CurrentScreenJsonObject = new JSONObject();
				try
				{
					CurrentScreenJsonObject.put("UserId", id);
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
						try
						{
							if (mCustomProgressDialog.isVisible())
								mCustomProgressDialog.dismissDialog();
						}
						catch (Exception e1)
						{
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						catch (Throwable e)
						{
							e.printStackTrace();
						}

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
										Utility.setSharedPrefStringData(MapScreenActivity.this, "TripId", Constant.mCurrentTripDataBean.getTripArray().get(0).getTripId());

										Log.e("trip id from current trip", "is:" + Constant.mCurrentTripDataBean.getTripArray().get(0).getTripId());
										/*Intent currentTripIntent = new Intent(MapScreenActivity.this, MapScreenActivity.class);
										currentTripIntent.putExtra("currentTrip", "1");
										// currentTripIntent.putExtra(name,
										// value)
										startActivity(currentTripIntent);*/

										setUpCurrentMap();
									}
									else
									{

										Utility.showMsgDialog(MapScreenActivity.this, "Currently! You do not have any pending trip.");
									}

								}
								else
								{
									msg = responseJsonObject.optString("Message");
									Utility.showMsgDialog(MapScreenActivity.this, msg);
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

	private void setUpCurrentMap()
	{
		try
		{

			if (Constant.RoutListLatLngsCurrentLine != null && Constant.RoutListLatLngsCurrentLine.size() > 0)
			{

				mRoutListLatLngsCurrentLine.addAll(Constant.RoutListLatLngsCurrentLine);

			}
			else
			{

				Gson gson = new Gson();
				java.lang.reflect.Type type = new TypeToken<ArrayList<LatLng>>()
				{
				}.getType();

				String arrayName = Utility.LoadArrayListObject("objectarraylist", MapScreenActivity.this);
				/*objectArrayList = gson.fromJson(arrayName, type);*/
				if (arrayName != null && !arrayName.equalsIgnoreCase("null"))
				{
					ArrayList<LatLng> test = new ArrayList<LatLng>();
					test = gson.fromJson(arrayName, type);
					/*test.remove(1)*/;

					mRoutListLatLngsCurrentLine.addAll(test);
					Log.e("mRoutListLatLngsCurrentLine", "is " + mRoutListLatLngsCurrentLine);
					Log.e("arrayName", "is" + arrayName);
				}

			}

			mCurrentTripLatLngSource = new LatLng(Double.parseDouble(Constant.mCurrentTripDataBean.getTripArray().get(0).getStartLatitude()), Double.parseDouble(Constant.mCurrentTripDataBean
					.getTripArray().get(0).getStartLongitude()));

			mCurrentTripLatLngDestination = new LatLng(Double.parseDouble(Constant.mCurrentTripDataBean.getTripArray().get(0).getEndLatitude()), Double.parseDouble(Constant.mCurrentTripDataBean
					.getTripArray().get(0).getEndLongitude()));

			// create marker
			markerSource = new MarkerOptions().position(mCurrentTripLatLngSource).title(Constant.mCurrentTripDataBean.getTripArray().get(0).getStartPointName())
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin));
			markerDestin = new MarkerOptions().position(mCurrentTripLatLngDestination).title(Constant.mCurrentTripDataBean.getTripArray().get(0).getEndPointName())
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin));

			// adding marker

			mMoveingMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude())).icon(
					BitmapDescriptorFactory.fromResource(R.drawable.truck_icon)));
			mMoveingMarker.setRotation(0);

			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()), 15));
			mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

			mMap.addMarker(markerSource);
			mMap.addMarker(markerDestin);

			mCustomProgressDialog = new CustomProgressDialog(MapScreenActivity.this, "");
			mCustomProgressDialog.setCancelable(false);
			new Thread(new Runnable()
			{

				@Override
				public void run()
				{
					final PolylineOptions rectLine = new PolylineOptions();
					final ArrayList<LatLng> directionPoint = new ArrayList<LatLng>();

					final PolylineOptions estimatedSourceDestination = new PolylineOptions();
					final ArrayList<LatLng> directionPointestimatedSourceDest;
					try
					{
						if (mReuseTrip.equalsIgnoreCase("2") || Utility.getSharedPrefStringData(MapScreenActivity.this, "reuse").equalsIgnoreCase("yes"))
						{
							String routPointArray = Constant.mCurrentTripDataBean.getTripArray().get(0).getmRoutPointLatLongArryString();
							System.out.println(routPointArray);

							JSONArray jsonArray;

							jsonArray = new JSONArray(routPointArray);
							for (int i = 0; i < jsonArray.length(); i++)
							{
								JSONObject obj = jsonArray.getJSONObject(i);
								System.out.println("dd");

								LatLng latlong = new LatLng(Double.parseDouble(obj.getString("latitude")), Double.parseDouble(obj.getString("longtitude")));
								if (!directionPoint.contains(latlong))
									directionPoint.add(latlong);
							}

							rectLine.width(15).color(Color.GREEN);//actual green
							for (int j = 0; j < directionPoint.size(); j++)
							{

								rectLine.add(directionPoint.get(j));

							}
						}
						else
						{
							rectLine.width(15).color(Color.GREEN);
							for (int j = 0; j < mRoutListLatLngsCurrentLine.size(); j++)
							{

								rectLine.add(mRoutListLatLngsCurrentLine.get(j));

							}
						}

						doc = gmapv.getDocument(mCurrentTripLatLngSource, mCurrentTripLatLngDestination, GMapV2Direction.MODE_DRIVING);
						directionPointestimatedSourceDest = gmapv.getDirection(doc);
						estimatedSourceDestination.width(15).color(Color.MAGENTA);//estimated
						for (int k = 0; k < directionPointestimatedSourceDest.size(); k++)
						{
							estimatedSourceDestination.add(directionPointestimatedSourceDest.get(k));
						}

						Log.i("size points", "size" + directionPoint.size());

					}

					catch (Exception e)
					{

						e.printStackTrace();
					}
					mCurrentTripMapHandler.post(new Runnable()
					{

						@Override
						public void run()
						{
							mCustomProgressDialog.dismissDialog();
							if (mReuseTrip.equalsIgnoreCase("2") || Utility.getSharedPrefStringData(MapScreenActivity.this, "reuse").equalsIgnoreCase("yes"))
							{
								poly = mMap.addPolyline(rectLine);
								if (directionPoint.size() >= 2)
								{
									MarkerOptions finaluplodadingPoint = new MarkerOptions().position(directionPoint.get(directionPoint.size() - 1))
											.title(Constant.mCurrentTripDataBean.getTripArray().get(0).getmSndPtName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.check_icon));
									mMap.addMarker(finaluplodadingPoint);
								}
							}
							else
							{
								poly = mMap.addPolyline(rectLine);
							}

							mMap.addPolyline(estimatedSourceDestination);

							//							if (Constant.mCurrentTripDataBean.getTripArray().get(0).getmSndPtLat().length() > 0)
							//							{
							//
							//								getAnotherCurrentMap();
							//
							//							}
							//							else
							//							{
							if (Constant.mCurrentTripDataBean.getTripArray().get(0).getDrawLineArray().size() > 0)
							{

								for (int i = 0; i < Constant.mCurrentTripDataBean.getTripArray().get(0).getDrawLineArray().size(); i++)
								{

									mNewLatLngSource = new LatLng(Double.parseDouble(Constant.mCurrentTripDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawLineStartLatitude()), Double
											.parseDouble(Constant.mCurrentTripDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawLineStartLongitude()));

									mNewLatLngDestination = new LatLng(Double.parseDouble(Constant.mCurrentTripDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawEndLatitude()), Double
											.parseDouble(Constant.mCurrentTripDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawLineEndLongitude()));
									mNewLatLongSource = new ArrayList<Address>();
									mNewLatLongDesination = new ArrayList<Address>();

									mNewMarkersource = new MarkerOptions()
											.position(mNewLatLngSource)
											.title(Constant.mCurrentTripDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawLineStartName()/*mNewLatLongSource.get(0).getAddressLine(2) + mNewLatLongSource.get(0).getAddressLine(1)*/)
											.snippet(
													"Category: " + Constant.mCurrentTripDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmCategory() + ",\t \n" + "Details: "
															+ Constant.mCurrentTripDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawComment())
											.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_pin));
									mNewMarkerDestination = new MarkerOptions()
											.position(mNewLatLngDestination)
											.title(Constant.mCurrentTripDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawLineEndName())
											.snippet(
													"Category: " + Constant.mCurrentTripDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmCategory() + ",\t \n" + "Details: "
															+ Constant.mCurrentTripDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawComment())
											.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_pin));
									String mColor = Constant.mCurrentTripDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawColor();

									mMap.addMarker(mNewMarkersource);
									mMap.addMarker(mNewMarkerDestination);

									getNewDrawLine(mNewLatLngSource, mNewLatLngDestination, mColor);
								}

							}
							try
							{
								if (Constant.mCurrentTripDataBean.getTripArray().get(0).getShareScreenArray().size() > 0)
								{
									for (int j = 0; j < Constant.mCurrentTripDataBean.getTripArray().get(0).getShareScreenArray().size(); j++)
									{
										putNewPointonMap(Constant.mCurrentTripDataBean.getTripArray().get(0).getShareScreenArray().get(j).getShareScreenId(),
												Double.parseDouble(Constant.mCurrentTripDataBean.getTripArray().get(0).getShareScreenArray().get(j).getShareScreenCurrentLatitude()),
												Double.parseDouble(Constant.mCurrentTripDataBean.getTripArray().get(0).getShareScreenArray().get(j).getShareScreenCurrentLongitude()));
									}

								}

							}
							catch (Exception e2)
							{
								// TODO: handle exception
							}
							//}

						}

					});

				}
			}).start();

		}
		catch (Exception ee)
		{
			Log.e("LatLong error", "" + ee);

			ee.printStackTrace();
		}

	}

	private void getAnotherCurrentMap()
	{

		mCurrentTripLatLngDestination = new LatLng(Double.parseDouble(Constant.mCurrentTripDataBean.getTripArray().get(0).getmSndPtLat()), Double.parseDouble(Constant.mCurrentTripDataBean
				.getTripArray().get(0).getmSndPtLog()));

		mCurrentTripLatLngSource = new LatLng(Double.parseDouble(Constant.mCurrentTripDataBean.getTripArray().get(0).getEndLatitude()), Double.parseDouble(Constant.mCurrentTripDataBean.getTripArray()
				.get(0).getEndLongitude()));

		/*mCurrentTripLatLngDestination = new LatLng(Double.parseDouble(Constant.mCurrentTripDataBean.getTripArray().get(0).getmSndPtLat()), Double.parseDouble(Constant.mCurrentTripDataBean
				.getTripArray().get(0).getmSndPtLog()));*/

		markerDestin = new MarkerOptions().position(mCurrentTripLatLngDestination).title(Constant.mCurrentTripDataBean.getTripArray().get(0).getmSndPtName())
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin));

		markerSource = new MarkerOptions().position(mCurrentTripLatLngSource).title(Constant.mCurrentTripDataBean.getTripArray().get(0).getEndPointName())
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin));

		/*
		markerSource = new MarkerOptions().position(mCurrentTripLatLngSource)
				.title(mLatLongSource.get(0).getAddressLine(2) + mLatLongSource.get(0).getAddressLine(1)HomeActivity.locationStringDestination)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin));*/
		/*markerDestin = new MarkerOptions().position(mCurrentTripLatLngDestination)
				.title(HomeActivity.locationStringMoreDestinationmLatLongDesination.get(0).getAddressLine(2) + mLatLongDesination.get(0).getAddressLine(1))
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin));*/

		mMap.addMarker(markerSource);

		mMap.addMarker(markerDestin);

		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				final PolylineOptions rectLine = new PolylineOptions();

				ArrayList<LatLng> directionPoint = null;
				try
				{
					doc = gmapv.getDocument(mCurrentTripLatLngSource, mCurrentTripLatLngDestination, GMapV2Direction.MODE_DRIVING);
					directionPoint = gmapv.getDirection(doc);
					rectLine.width(15).color(Color.GREEN);
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

				mMapHandler1.post(new Runnable()
				{

					@Override
					public void run()
					{

						poly = mMap.addPolyline(rectLine);
						mRoutListLatLngs.addAll(gmapv.getDirection(doc));
						Log.e("rout array lis", mRoutListLatLngs.toString());

						if (Constant.mCurrentTripDataBean.getTripArray().get(0).getDrawLineArray().size() > 0)
						{

							for (int i = 0; i < Constant.mCurrentTripDataBean.getTripArray().get(0).getDrawLineArray().size(); i++)
							{

								mNewLatLngSource = new LatLng(Double.parseDouble(Constant.mCurrentTripDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawLineStartLatitude()), Double
										.parseDouble(Constant.mCurrentTripDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawLineStartLongitude()));

								mNewLatLngDestination = new LatLng(Double.parseDouble(Constant.mCurrentTripDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawEndLatitude()), Double
										.parseDouble(Constant.mCurrentTripDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawLineEndLongitude()));
								mNewLatLongSource = new ArrayList<Address>();
								mNewLatLongDesination = new ArrayList<Address>();

								mNewMarkersource = new MarkerOptions()
										.position(mNewLatLngSource)
										.title(Constant.mCurrentTripDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawLineStartName()/*mNewLatLongSource.get(0).getAddressLine(2) + mNewLatLongSource.get(0).getAddressLine(1)*/)
										.snippet(
												"Category: " + Constant.mCurrentTripDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmCategory() + ",\t \n" + "Details: "
														+ Constant.mCurrentTripDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawComment())
										.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_pin));
								mNewMarkerDestination = new MarkerOptions()
										.position(mNewLatLngDestination)
										.title(Constant.mCurrentTripDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawLineEndName())
										.snippet(
												"Category: " + Constant.mCurrentTripDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmCategory() + ",\t \n" + "Details: "
														+ Constant.mCurrentTripDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawComment())
										.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_pin));
								String mColor = Constant.mCurrentTripDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawColor();

								mMap.addMarker(mNewMarkersource);
								mMap.addMarker(mNewMarkerDestination);

								getNewDrawLine(mNewLatLngSource, mNewLatLngDestination, mColor);
							}

						}
						try
						{
							if (Constant.mCurrentTripDataBean.getTripArray().get(0).getShareScreenArray().size() > 0)
							{
								for (int j = 0; j < Constant.mCurrentTripDataBean.getTripArray().get(0).getShareScreenArray().size(); j++)
								{
									putNewPointonMap(Constant.mCurrentTripDataBean.getTripArray().get(0).getShareScreenArray().get(j).getShareScreenId(),
											Double.parseDouble(Constant.mCurrentTripDataBean.getTripArray().get(0).getShareScreenArray().get(j).getShareScreenCurrentLatitude()),
											Double.parseDouble(Constant.mCurrentTripDataBean.getTripArray().get(0).getShareScreenArray().get(j).getShareScreenCurrentLongitude()));
								}

							}

						}
						catch (Exception e2)
						{
							// TODO: handle exception
						}
						//animateMarker(marker, mRoutListLatLngs.get(1), false);

					}

				});

			}
		}).start();

	}

	private void getNewDrawLine(final LatLng mNewLatLngSource, final LatLng mNewLatLngDestination, final String color)
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
						//mRoutListLatLngs = gmapv.getDirection(doc);

					}
				});

			}
		}).start();

	}

	private void setUpMap()
	{
		try
		{

			mLatLongSource = new ArrayList<Address>();
			mLatLongDesination = new ArrayList<Address>();
			getCurrentLocation = gpsTracker.getLocation();

			/*mLatLongSource = coder.getFromLocation(getCurrentLocation.getLatitude(), getCurrentLocation.getLongitude(), 1);
			mLatLongDesination = coder.getFromLocation(HomeActivity.mDestLatitude, HomeActivity.mDestLongitude, 1);*/
			// create marker
			markerSource = new MarkerOptions().position(HomeActivity.latlng_Source/*new LatLng(curLattitude, curLongitude)*/).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin))
					.title(HomeActivity.location_string);
			markerDestin = new MarkerOptions().position(HomeActivity.latlng_Dest)
					.title(HomeActivity.locationStringDestination/*mLatLongDesination.get(0).getAddressLine(2) + mLatLongDesination.get(0).getAddressLine(1)*/)
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin));
			// adding marker

			/*marker = mMap.addMarker(new MarkerOptions().position(new LatLng(curLattitude, curLongitude)));*/
			/*marker =*/
			MarkerOptions movingSource = new MarkerOptions().position(HomeActivity.latlng_Source).icon(BitmapDescriptorFactory.fromResource(R.drawable.truck_icon));
			mMoveingMarker = mMap.addMarker(movingSource);

			mMap.addMarker(markerSource);
			mMap.addMarker(markerDestin);

			mCustomProgressDialog = new CustomProgressDialog(MapScreenActivity.this, "");
			mCustomProgressDialog.setCancelable(false);

			getTimer();
			new Thread(new Runnable()
			{

				@Override
				public void run()
				{
					final PolylineOptions rectLine = new PolylineOptions();

					ArrayList<LatLng> directionPoint = null;
					try
					{
						doc = gmapv.getDocument(HomeActivity.latlng_Source, HomeActivity.latlng_Dest, GMapV2Direction.MODE_DRIVING);
						directionPoint = gmapv.getDirection(doc);
						rectLine.width(15).color(Color.MAGENTA);
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
							mCustomProgressDialog.dismissDialog();
							poly = mMap.addPolyline(rectLine);
							mRoutListLatLngs = gmapv.getDirection(doc);
							Log.e("rout array lis", mRoutListLatLngs.toString());
							if (HomeActivity.latlng_moreDest != null)
								getAnotherMap();

						}

					});

				}
			}).start();

		}
		catch (Exception ee)
		{
			ee.printStackTrace();
		}

	}

	private void getAnotherMap()
	{
		markerSource = new MarkerOptions().position(HomeActivity.latlng_Dest)
				.title(/*mLatLongSource.get(0).getAddressLine(2) + mLatLongSource.get(0).getAddressLine(1)*/HomeActivity.locationStringDestination)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin));
		markerDestin = new MarkerOptions().position(HomeActivity.latlng_moreDest)
				.title(HomeActivity.locationStringMoreDestination/*mLatLongDesination.get(0).getAddressLine(2) + mLatLongDesination.get(0).getAddressLine(1)*/)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin));

		mMap.addMarker(markerSource);
		if (markerDestin != null)
			mMap.addMarker(markerDestin);

		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				final PolylineOptions rectLine = new PolylineOptions();

				ArrayList<LatLng> directionPoint = null;
				try
				{
					doc = gmapv.getDocument(HomeActivity.latlng_Dest, HomeActivity.latlng_moreDest, GMapV2Direction.MODE_DRIVING);
					directionPoint = gmapv.getDirection(doc);
					rectLine.width(15).color(Color.GREEN);
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

				mMapHandler1.post(new Runnable()
				{

					@Override
					public void run()
					{

						poly = mMap.addPolyline(rectLine);
						mRoutListLatLngs.addAll(gmapv.getDirection(doc));
						Log.e("rout array lis", mRoutListLatLngs.toString());
						//animateMarker(marker, mRoutListLatLngs.get(1), false);

					}

				});

			}
		}).start();

	}

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub

		if (v == mapScreen_ui_backImgview1)
		{
			Intent intent = new Intent(this, HomeActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();

		}
		else
			if (v == mapScreen_ui_refreshImgview1)
			{
				//Refresh Map

				/**
				 * author nipun
				 */
				mCustomProgressDialog = new CustomProgressDialog(MapScreenActivity.this, "");
				new Thread(new Runnable()
				{
					String response = "";

					@Override
					public void run()
					{
						JSONObject refreshJsonObject = new JSONObject();
						try
						{
							refreshJsonObject.put("Userid", Utility.getSharedPrefString(MapScreenActivity.this, "userId"));
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

						mRefreshMapHandler.post(new Runnable()
						{
							private String msg;

							@Override
							public void run()
							{
								mCustomProgressDialog.dismissDialog();

								if (response != null && response.length() > 0)
								{
									parser = new FinishTripParser();

									try
									{

										JSONObject responseJSONObject = new JSONObject(response);

										if (responseJSONObject.optBoolean("Success"))
										{
											Constant.mRefreshTripDataBean = (FinishedTripDataBean) parser.parse(response);

											setUpRefreshMap();
										}
										else
										{
											Utility.showToastMessage(MapScreenActivity.this, responseJSONObject.optString("Message"));

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
				//mRefreshProgressDialog.dismiss();
			}
			else
				if (v == mapScreen_ui_closeImgview1)
				{

					if (mReuseTrip.equalsIgnoreCase("2") || Utility.getSharedPrefStringData(MapScreenActivity.this, "reuse").equalsIgnoreCase("yes")
					/*|| !Utility.getSharedPrefStringData(MapScreenActivity.this, "driverrtype").equalsIgnoreCase("1")*/)
						finishTripDialog();
					else

						finishTripDialogCurrentUploadPoint();
				}

				else
					if (v == mapScreen_ui_shareImgview1)
					{
						/*startActivity(new Intent(MapScreenActivity.this, ShareActivity.class));*/
						showScreenShareDialog();
					}
					else
						if (v == mMapScreenCancelTripImageView)
						{

							AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MapScreenActivity.this);

							alertDialogBuilder.setTitle(this.getResources().getString(R.string.app_name));
							alertDialogBuilder.setCancelable(false);
							alertDialogBuilder.setMessage(getApplicationContext().getResources().getString(R.string.MapScreenAlert));

							// set positive button: Yes message
							alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
							{
								public void onClick(DialogInterface dialog, int id)
								{

									getCancelTrip();
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

						}
						else
							if (v == mMapScreenEditImageView)
							{
								if (mPtLatLngSource != null && mPtLatLngDestination != null)

									getCategories();

								else
								{
									Utility.showMsgDialog(MapScreenActivity.this, "Please Select the start or end point");
								}

							}
							else
								if (v == mColorOneTextView)
								{
									if (mColorOneTextView.getTag().equals("0"))
									{
										mColorOneTextView.setTag("1");
										mColorTwoTextView.setTag("0");
										mColorThreeTextView.setTag("0");
										mColorFourTextView.setTag("0");
										mColorFiveTextView.setTag("0");
										mColorSixTextView.setTag("0");
										mColorSevenTextView.setTag("0");
										mColorEightTextView.setTag("0");
										mColorOneTextView.setBackgroundResource(R.drawable.code_one_selected);
										mColorTwoTextView.setBackgroundResource(R.drawable.code_two);
										mColorThreeTextView.setBackgroundResource(R.drawable.code_three);
										mColorFourTextView.setBackgroundResource(R.drawable.code_four);
										mColorFiveTextView.setBackgroundResource(R.drawable.code_five);
										mColorSixTextView.setBackgroundResource(R.drawable.code_six);
										mColorSevenTextView.setBackgroundResource(R.drawable.code_seven);
										mColorEightTextView.setBackgroundResource(R.drawable.code_eight);

									}

								}
								else
									if (v == mColorTwoTextView)
									{
										if (mColorTwoTextView.getTag().equals("0"))
										{
											mColorOneTextView.setTag("0");
											mColorTwoTextView.setTag("1");
											mColorThreeTextView.setTag("0");
											mColorFourTextView.setTag("0");
											mColorFiveTextView.setTag("0");
											mColorSixTextView.setTag("0");
											mColorSevenTextView.setTag("0");
											mColorEightTextView.setTag("0");
											mColorOneTextView.setBackgroundResource(R.drawable.code_one);
											mColorTwoTextView.setBackgroundResource(R.drawable.code_two_selected);
											mColorThreeTextView.setBackgroundResource(R.drawable.code_three);
											mColorFourTextView.setBackgroundResource(R.drawable.code_four);
											mColorFiveTextView.setBackgroundResource(R.drawable.code_five);
											mColorSixTextView.setBackgroundResource(R.drawable.code_six);
											mColorSevenTextView.setBackgroundResource(R.drawable.code_seven);
											mColorEightTextView.setBackgroundResource(R.drawable.code_eight);

										}

									}
									else
										if (v == mColorThreeTextView)
										{

											if (mColorThreeTextView.getTag().equals("0"))
											{
												mColorOneTextView.setTag("0");
												mColorTwoTextView.setTag("0");
												mColorThreeTextView.setTag("1");
												mColorFourTextView.setTag("0");
												mColorFiveTextView.setTag("0");
												mColorSixTextView.setTag("0");
												mColorSevenTextView.setTag("0");
												mColorEightTextView.setTag("0");
												mColorOneTextView.setBackgroundResource(R.drawable.code_one);
												mColorTwoTextView.setBackgroundResource(R.drawable.code_two);
												mColorThreeTextView.setBackgroundResource(R.drawable.code_three_selected);
												mColorFourTextView.setBackgroundResource(R.drawable.code_four);
												mColorFiveTextView.setBackgroundResource(R.drawable.code_five);
												mColorSixTextView.setBackgroundResource(R.drawable.code_six);
												mColorSevenTextView.setBackgroundResource(R.drawable.code_seven);
												mColorEightTextView.setBackgroundResource(R.drawable.code_eight);

											}

										}
										else
											if (v == mColorFourTextView)
											{

												if (mColorFourTextView.getTag().equals("0"))
												{
													mColorOneTextView.setTag("0");
													mColorTwoTextView.setTag("0");
													mColorThreeTextView.setTag("0");
													mColorFourTextView.setTag("1");
													mColorFiveTextView.setTag("0");
													mColorSixTextView.setTag("0");
													mColorSevenTextView.setTag("0");
													mColorEightTextView.setTag("0");
													mColorOneTextView.setBackgroundResource(R.drawable.code_one);
													mColorTwoTextView.setBackgroundResource(R.drawable.code_two);
													mColorThreeTextView.setBackgroundResource(R.drawable.code_three);
													mColorFourTextView.setBackgroundResource(R.drawable.code_four_selected);
													mColorFiveTextView.setBackgroundResource(R.drawable.code_five);
													mColorSixTextView.setBackgroundResource(R.drawable.code_six);
													mColorSevenTextView.setBackgroundResource(R.drawable.code_seven);
													mColorEightTextView.setBackgroundResource(R.drawable.code_eight);

												}

											}
											else
												if (v == mColorFiveTextView)
												{

													if (mColorFiveTextView.getTag().equals("0"))
													{
														mColorOneTextView.setTag("0");
														mColorTwoTextView.setTag("0");
														mColorThreeTextView.setTag("0");
														mColorFourTextView.setTag("0");
														mColorFiveTextView.setTag("1");
														mColorSixTextView.setTag("0");
														mColorSevenTextView.setTag("0");
														mColorEightTextView.setTag("0");
														mColorOneTextView.setBackgroundResource(R.drawable.code_one);
														mColorTwoTextView.setBackgroundResource(R.drawable.code_two);
														mColorThreeTextView.setBackgroundResource(R.drawable.code_three);
														mColorFourTextView.setBackgroundResource(R.drawable.code_four);
														mColorFiveTextView.setBackgroundResource(R.drawable.code_five_selected);
														mColorSixTextView.setBackgroundResource(R.drawable.code_six);
														mColorSevenTextView.setBackgroundResource(R.drawable.code_seven);
														mColorEightTextView.setBackgroundResource(R.drawable.code_eight);

													}

												}
												else
													if (v == mColorSixTextView)
													{

														if (mColorSixTextView.getTag().equals("0"))
														{
															mColorOneTextView.setTag("0");
															mColorTwoTextView.setTag("0");
															mColorThreeTextView.setTag("0");
															mColorFourTextView.setTag("0");
															mColorFiveTextView.setTag("0");
															mColorSixTextView.setTag("1");
															mColorSevenTextView.setTag("0");
															mColorEightTextView.setTag("0");
															mColorOneTextView.setBackgroundResource(R.drawable.code_one);
															mColorTwoTextView.setBackgroundResource(R.drawable.code_two);
															mColorThreeTextView.setBackgroundResource(R.drawable.code_three);
															mColorFourTextView.setBackgroundResource(R.drawable.code_four);
															mColorFiveTextView.setBackgroundResource(R.drawable.code_five);
															mColorSixTextView.setBackgroundResource(R.drawable.code_six_selected);
															mColorSevenTextView.setBackgroundResource(R.drawable.code_seven);
															mColorEightTextView.setBackgroundResource(R.drawable.code_eight);

														}

													}
													else
														if (v == mColorSevenTextView)
														{

															if (mColorSevenTextView.getTag().equals("0"))
															{
																mColorOneTextView.setTag("0");
																mColorTwoTextView.setTag("0");
																mColorThreeTextView.setTag("0");
																mColorFourTextView.setTag("0");
																mColorFiveTextView.setTag("0");
																mColorSixTextView.setTag("0");
																mColorSevenTextView.setTag("1");
																mColorEightTextView.setTag("0");
																mColorOneTextView.setBackgroundResource(R.drawable.code_one);
																mColorTwoTextView.setBackgroundResource(R.drawable.code_two);
																mColorThreeTextView.setBackgroundResource(R.drawable.code_three);
																mColorFourTextView.setBackgroundResource(R.drawable.code_four);
																mColorFiveTextView.setBackgroundResource(R.drawable.code_five);
																mColorSixTextView.setBackgroundResource(R.drawable.code_six);
																mColorSevenTextView.setBackgroundResource(R.drawable.code_seven_selected);
																mColorEightTextView.setBackgroundResource(R.drawable.code_eight);
															}

														}
														else
															if (v == mColorEightTextView)
															{

																if (mColorEightTextView.getTag().equals("0"))
																{
																	mColorOneTextView.setTag("0");
																	mColorTwoTextView.setTag("0");
																	mColorThreeTextView.setTag("0");
																	mColorFourTextView.setTag("0");
																	mColorFiveTextView.setTag("0");
																	mColorSixTextView.setTag("0");
																	mColorSevenTextView.setTag("0");
																	mColorEightTextView.setTag("1");
																	mColorOneTextView.setBackgroundResource(R.drawable.code_one);
																	mColorTwoTextView.setBackgroundResource(R.drawable.code_two);
																	mColorThreeTextView.setBackgroundResource(R.drawable.code_three);
																	mColorFourTextView.setBackgroundResource(R.drawable.code_four);
																	mColorFiveTextView.setBackgroundResource(R.drawable.code_five);
																	mColorSixTextView.setBackgroundResource(R.drawable.code_six);
																	mColorSevenTextView.setBackgroundResource(R.drawable.code_seven);
																	mColorEightTextView.setBackgroundResource(R.drawable.code_eight_selected);

																}

															}
															else
																if (v == mCategoryTextView)
																{
																	showCategoryDropDown();
																}
																else
																	if (v == mCancelColorPopupImageView)
																	{
																		mDialogShow.dismiss();
																	}

	}

	/**TripId
	 * @author nipun
	 *@deprecated
	 */

	private void finishTripDialog()
	{

		//Finish Trip Dialog Screen

		mDialogShow = new Dialog(MapScreenActivity.this);

		mDialogShow.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mDialogShow.setContentView(R.layout.finishtripdialog_ui);
		mDialogShow.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		mDialogShow.setCancelable(false);
		mfinishTripDialogTextView1 = (TextView) mDialogShow.findViewById(R.id.finishTripdialog_tv1);

		mfinishTripDialogTextView2 = (TextView) mDialogShow.findViewById(R.id.finishTripdialog_tv2);

		mfinishTripDialogYesBtn = (Button) mDialogShow.findViewById(R.id.finishTripDialogYesBtn);
		//mfinishTripdialog_comment = (EditText) mDialogShow.findViewById(R.id.finishTripdialog_comment);

		Utility.textViewFontRobotoLight(mfinishTripDialogTextView1, getAssets());
		Utility.textViewFontRobotoLight(mfinishTripDialogTextView2, getAssets());

		Utility.textViewFontRobotoLight(mfinishTripDialogYesBtn, getAssets());
		//	Utility.textViewFontRobotoLight(mfinishTripdialog_comment, getAssets());
		mDialogShow.show();

		//Click on Forget Password Button

		mfinishTripDialogYesBtn.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (i == 0)
				{
					i = i + 1;
					getFinishService();
				}

			}

		});

		// Finish Trip Dialog Screen

		mfinishTrip_ImageViewCancel = (ImageView) mDialogShow.findViewById(R.id.finishTrip_relative_ImageViewCancel);

		mfinishTrip_ImageViewCancel.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{

				mDialogShow.dismiss();
			}
		});

	}

	private void finishTripDialogCurrentUploadPoint()
	{

		//Finish Trip Dialog Screen

		final Dialog mDialogShow = new Dialog(MapScreenActivity.this);

		mDialogShow.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mDialogShow.setContentView(R.layout.finish_trip_current_upload_point);
		mDialogShow.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		mDialogShow.setCancelable(false);
		TextView mTextViewOk = (TextView) mDialogShow.findViewById(R.id.current_unloading_point_textview_ok);
		TextView mTextViewClose = (TextView) mDialogShow.findViewById(R.id.current_unloading_point_textview_close);
		final CheckBox mCheckIsCurrentPoint = (CheckBox) mDialogShow.findViewById(R.id.current_unloading_point_check_box);

		Utility.textViewFontRobotoLight(mTextViewOk, getAssets());
		Utility.textViewFontRobotoLight(mTextViewClose, getAssets());
		Utility.textViewFontRobotoLight(mCheckIsCurrentPoint, getAssets());

		mDialogShow.show();
		//isCurrentUploadPoint
		mTextViewOk.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{

				mDialogShow.dismiss();
				isCurrentUploadPoint = mCheckIsCurrentPoint.isChecked();
				finishTripDialogImportentInfo();
			}
		});
		mTextViewClose.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				mDialogShow.dismiss();
				isCurrentUploadPoint = false;
				finishTripDialogImportentInfo();

			}
		});

	}

	private void finishTripDialogImportentInfo()
	{

		//Finish Trip Dialog Screen

		final Dialog mDialogShow = new Dialog(MapScreenActivity.this);

		mDialogShow.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mDialogShow.setContentView(R.layout.finish_trip_important_info_dialog);
		mDialogShow.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		mDialogShow.setCancelable(false);
		TextView mTextViewTitle = (TextView) mDialogShow.findViewById(R.id.important_info_textview_title);
		TextView mTextViewComment = (TextView) mDialogShow.findViewById(R.id.important_info_textview_comment);
		final EditText mEditTextComment = (EditText) mDialogShow.findViewById(R.id.important_info_edittext_comment);
		TextView mTextViewNext = (TextView) mDialogShow.findViewById(R.id.important_info_textview_next);
		TextView mTextViewClose = (TextView) mDialogShow.findViewById(R.id.important_info_textView_close);

		final ListView mListViewInfo = (ListView) mDialogShow.findViewById(R.id.important_info_listview);

		//final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.checkbox_row_layout, R.id.checkBox1, finishInfoItems);
		final FinishTripImportentInfoListAdapter adapter = new FinishTripImportentInfoListAdapter(this, finishInfoItems);
		mListViewInfo.setAdapter(adapter);

		Utility.textViewFontRobotoLight(mTextViewTitle, getAssets());
		Utility.textViewFontRobotoLight(mTextViewComment, getAssets());
		Utility.textViewFontRobotoLight(mTextViewNext, getAssets());
		Utility.textViewFontRobotoLight(mTextViewClose, getAssets());

		mDialogShow.show();
		//isCurrentUploadPoint
		mTextViewClose.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{

				mDialogShow.dismiss();
				selectInfoText = "";
				finishComment = "";
				finishTripDialogDone();
			}
		});

		mTextViewNext.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{

				selectInfoText = adapter.getSelectedTexts();
				finishComment = mEditTextComment.getText().toString().trim();
				mDialogShow.dismiss();
				finishTripDialogDone();

			}
		});
	}

	private void finishTripDialogDone()
	{

		final Dialog mDialogShow = new Dialog(MapScreenActivity.this);

		mDialogShow.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mDialogShow.setContentView(R.layout.finish_trip_done_dialog);
		mDialogShow.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		mDialogShow.setCancelable(false);

		TextView mTextViewTitle = (TextView) mDialogShow.findViewById(R.id.finish_trip_done_textview_title);
		final RadioButton radiYes = (RadioButton) mDialogShow.findViewById(R.id.finish_trip_done_checkbox_yes);
		RadioButton radiNo = (RadioButton) mDialogShow.findViewById(R.id.finish_trip_done_checkbox_no);

		TextView mTextViewDone = (TextView) mDialogShow.findViewById(R.id.finish_trip_done_textview_done);
		ImageView mTextViewClose = (ImageView) mDialogShow.findViewById(R.id.finish_trip_done_textview_close);

		Utility.textViewFontRobotoLight(mTextViewTitle, getAssets());
		Utility.textViewFontRobotoLight(radiYes, getAssets());
		Utility.textViewFontRobotoLight(radiNo, getAssets());
		Utility.textViewFontRobotoLight(mTextViewDone, getAssets());

		mDialogShow.show();
		//isCurrentUploadPoint
		mTextViewDone.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{

				mDialogShow.dismiss();
				isRoutOkforTruck = radiYes.isChecked();
				System.out.println("iscurrent poitn:-" + isCurrentUploadPoint + ", \nselected textInfo:-" + selectInfoText + ", \nisRoutOk forTruck " + isRoutOkforTruck);

				getFinishServiceNew();

				//Clear on service success
				/*isCurrentUploadPoint=false;
				selectInfoText="";
				isRoutOkforTruck=false;
				finishComment="";*/

			}
		});

		mTextViewClose.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				mDialogShow.dismiss();
				isCurrentUploadPoint = false;
				selectInfoText = "";
				isRoutOkforTruck = false;
				finishComment = "";

			}
		});
	}

	private void getFinishServiceNew()
	{
		mCustomProgressDialog = new CustomProgressDialog(MapScreenActivity.this, "");
		new Thread(new Runnable()
		{

			String response = "";

			@Override
			public void run()
			{

				JSONObject exitJsonObject = new JSONObject();
				try
				{
					exitJsonObject.put("UserId", Utility.getSharedPrefString(MapScreenActivity.this, "userId"));
					exitJsonObject.put("TripId", mTripIdFinish);
					exitJsonObject.put("Comment", finishComment);

					if (isCurrentUploadPoint)
					{
						JSONObject getLocationName = Utility.getLocationInfo(gpsTracker.getLatitude(), gpsTracker.getLongitude());
						JSONObject Currentlocation = getLocationName.getJSONArray("results").getJSONObject(0);
						String location_string = Currentlocation.getString("formatted_address");

						exitJsonObject.put("endPointLatitude", gpsTracker.getLatitude());
						exitJsonObject.put("endPointLogtitude", gpsTracker.getLongitude());
						exitJsonObject.put("endPointName", location_string);
					}
					else
					{
						if (getIntent().getExtras() != null)
						{
							exitJsonObject.put("endPointLatitude", Constant.mCurrentTripDataBean.getTripArray().get(0).getEndLatitude());
							exitJsonObject.put("endPointLogtitude", Constant.mCurrentTripDataBean.getTripArray().get(0).getEndLongitude());
							exitJsonObject.put("endPointName", Constant.mCurrentTripDataBean.getTripArray().get(0).getEndPointName());
						}
						else
						{
							exitJsonObject.put("endPointLatitude", HomeActivity.latlng_Dest.latitude);
							exitJsonObject.put("endPointLogtitude", HomeActivity.latlng_Dest.longitude);
							exitJsonObject.put("endPointName", HomeActivity.locationStringDestination);
						}

					}
					exitJsonObject.put("extraInformation", selectInfoText);
					exitJsonObject.put("isRouteOk", isRoutOkforTruck);
					JSONArray routePointsInformationArray = new JSONArray();

					if (mRoutListLatLngsCurrentLine != null)
					{
						for (int i = 0; i < mRoutListLatLngsCurrentLine.size(); i++)
						{
							JSONObject point = new JSONObject();
							point.put("latitude", mRoutListLatLngsCurrentLine.get(i).latitude);
							point.put("longtitude", mRoutListLatLngsCurrentLine.get(i).longitude);

							routePointsInformationArray.put(point);

						}
					}
					exitJsonObject.put("routePointsInformation", routePointsInformationArray.toString());

					//latlng_Dest
				}
				catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.e("params for first", exitJsonObject.toString());
				String url = APIUtils.BASE_URL + APIUtils.FINISH_TRIP_REQUEST;
				response = Utility.POST(url, exitJsonObject.toString(), APIUtils.FINISH_TRIP_REQUEST);
				Log.e("response", response.toString());
				mExitTripHandler.post(new Runnable()
				{
					private String msg;

					@Override
					public void run()
					{

						if (response != null && response.length() > 0)
						{
							Log.e("msg", "is:" + response);
							mCustomProgressDialog.dismissDialog();

							try
							{
								JSONObject responseJsonObject = new JSONObject(response);
								if (responseJsonObject.optBoolean("Success"))
								{
									mCancelFlag = true;
									Constant.RoutListLatLngsCurrentLine = null;
									Utility.SaveArraylistObject(Constant.RoutListLatLngsCurrentLine, "objectarraylist", MapScreenActivity.this);

									msg = responseJsonObject.optString("Message");

									Intent startFinishTripActivity = new Intent(MapScreenActivity.this, FinishTripActivity.class);
									startActivity(startFinishTripActivity);
									//timer.cancel();
									finish();

								}
								else
								{
									msg = responseJsonObject.optString("Message");
									Utility.showMsgDialog(MapScreenActivity.this, msg);
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

	/**
	 * @deprecated
	 */

	private void getFinishService()
	{

		new Thread(new Runnable()
		{

			String response = "";

			@Override
			public void run()
			{

				JSONObject exitJsonObject = new JSONObject();
				try
				{
					exitJsonObject.put("UserId", Utility.getSharedPrefString(MapScreenActivity.this, "userId"));
					exitJsonObject.put("TripId", mTripIdFinish);
					//exitJsonObject.put("Comment", mfinishTripdialog_comment.getText().toString().trim());
				}
				catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.e("params for first", exitJsonObject.toString());
				String url = APIUtils.BASE_URL + APIUtils.FINISHTRIP;
				response = Utility.POST(url, exitJsonObject.toString(), APIUtils.FINISHTRIP);
				Log.e("response", response.toString());
				mExitTripHandler.post(new Runnable()
				{
					private String msg;

					@Override
					public void run()
					{

						if (response != null && response.length() > 0)
						{
							Log.e("msg", "is:" + response);

							try
							{
								JSONObject responseJsonObject = new JSONObject(response);
								if (responseJsonObject.optBoolean("Success"))
								{
									msg = responseJsonObject.optString("Message");
									Utility.setSharedPrefStringData(MapScreenActivity.this, "reuse", "no");
									Intent startFinishTripActivity = new Intent(MapScreenActivity.this, FinishTripActivity.class);
									startActivity(startFinishTripActivity);
									timer.cancel();
									finish();

								}
								else
								{
									msg = responseJsonObject.optString("Message");
									Utility.showMsgDialog(MapScreenActivity.this, msg);
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

	private void showCategoryDropDown()
	{

		if (mCategoryArray != null && mCategoryArray.length > 0)
		{

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(MapScreenActivity.this, R.layout.select_dialog_item, mCategoryArray);
			AlertDialog.Builder builder = new AlertDialog.Builder(MapScreenActivity.this);

			builder.setTitle("Select Option");
			builder.setAdapter(adapter, new DialogInterface.OnClickListener()
			{

				public void onClick(DialogInterface dialog, int which)
				{
					//Utility.textViewFontRobotoLight(mCategoryTextView, getAssets());
					mCategoryTextView.setText(mCategotyHashMaps.get(which).get("name"));
					mSelectedCategoryId = mCategotyHashMaps.get(which).get("id");

				}
			});
			final AlertDialog dialog = builder.create();

			dialog.show();
		}
		else
		{
			Toast.makeText(MapScreenActivity.this, "No Category", Toast.LENGTH_SHORT).show();
		}

	}

	private void getCancelTrip()
	{

		mCustomProgressDialog = new CustomProgressDialog(MapScreenActivity.this, "");

		new Thread(new Runnable()
		{

			String response = "";

			@Override
			public void run()
			{

				JSONObject cancelJsonObject = new JSONObject();
				try
				{

					cancelJsonObject.put("TripId", Utility.getSharedPrefStringData(MapScreenActivity.this, "TripId"));
					cancelJsonObject.put("UserId", Utility.getSharedPrefString(MapScreenActivity.this, "userId"));
					cancelJsonObject.put("DeviceToken", SplashActivity.regId);

				}
				catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.e("params for first", cancelJsonObject.toString());
				String url = APIUtils.BASE_URL + APIUtils.CANCELTRIP;
				response = Utility.POST(url, cancelJsonObject.toString(), APIUtils.CANCELTRIP);
				Log.e("response", response.toString());
				mCancelHandler.post(new Runnable()
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
									mCancelFlag = true;
									Constant.RoutListLatLngsCurrentLine = null;
									Utility.SaveArraylistObject(Constant.RoutListLatLngsCurrentLine, "objectarraylist", MapScreenActivity.this);

									msg = responseJsonObject.optString("Message");
									Utility.setSharedPrefStringData(MapScreenActivity.this, "reuse", "no");
									finish();

								}
								else
								{
									msg = responseJsonObject.optString("Message");

									Utility.showMsgDialog(MapScreenActivity.this, msg);

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

	private void getCategories()
	{

		mCustomProgressDialog = new CustomProgressDialog(MapScreenActivity.this, "");

		new Thread(new Runnable()
		{

			String response = "";

			@Override
			public void run()
			{
				JSONObject jsonObj = new JSONObject();
				try
				{

					jsonObj.put("deviceToken", SplashActivity.regId);

				}
				catch (Exception e)
				{
					// TODO: handle exception
				}

				Log.e("params for first", jsonObj.toString());
				String url = APIUtils.BASE_URL + APIUtils.GETCATEGORY;
				response = Utility.POST(url, jsonObj.toString(), APIUtils.GETCATEGORY);

				mCategotyHandler.post(new Runnable()
				{

					@Override
					public void run()
					{
						if (response != null && response.length() > 0)
						{
							mCustomProgressDialog.dismissDialog();
							Log.e("msg", "is:" + response);

							try
							{
								JSONObject responseJsonObject = new JSONObject(response);
								if (responseJsonObject.optBoolean("Success"))
								{
									mCategoryDataJsonArray = responseJsonObject.getJSONArray("Result");
									mCategotyHashMaps = new ArrayList<HashMap<String, String>>();

									for (int i = 0; i < mCategoryDataJsonArray.length(); i++)
									{
										HashMap<String, String> hashMap = new HashMap<String, String>();
										JSONObject jsonObject = mCategoryDataJsonArray.getJSONObject(i);
										hashMap.put("id", jsonObject.getString("id"));
										hashMap.put("name", jsonObject.getString("name"));
										mCategotyHashMaps.add(hashMap);

									}
									mCategoryArray = new String[mCategotyHashMaps.size()];
									for (int i = 0; i < mCategoryArray.length; i++)
									{
										mCategoryArray[i] = mCategotyHashMaps.get(i).get("name");
									}

									getCustomDialogForColorLines();

								}
								else
								{

									Utility.showMsgDialog(MapScreenActivity.this, responseJsonObject.optString("Message"));
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

	@Override
	public void onMapClick(LatLng point)
	{
		ponts = point;
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				mapMarkerCount = mapMarkerCount + 1;
				if ((mapMarkerCount % 2) == 0)
				{

					mPtLatLngDestination = new LatLng(ponts.latitude, ponts.longitude);
					mNewLatLongDesination = new ArrayList<Address>();
					JSONObject ret = Utility.getLocationInfo(ponts.latitude, ponts.longitude);
					Log.e("address string", ret.toString());

					JSONObject location;

					try
					{
						location = ret.getJSONArray("results").getJSONObject(0);
						mNewPinSourceString = location.getString("formatted_address");
						Log.d("test", "formattted address:" + mNewPinSourceString);
					}
					catch (JSONException e1)
					{
						e1.printStackTrace();

					}
					mNewMarkerDestination = new MarkerOptions().position(ponts).title(mNewPinSourceString).icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_pin));

				}
				else
				{
					mPtLatLngSource = new LatLng(ponts.latitude, ponts.longitude);
					mNewLatLongSource = new ArrayList<Address>();
					JSONObject ret = Utility.getLocationInfo(ponts.latitude, ponts.longitude);
					JSONObject location;

					try
					{
						location = ret.getJSONArray("results").getJSONObject(0);
						mNewPinDestination = location.getString("formatted_address");
						Log.d("test", "formattted address:" + mNewPinSourceString);
					}
					catch (JSONException e1)
					{
						e1.printStackTrace();

					}
					mNewMarkersource = new MarkerOptions().position(ponts).title(mNewPinDestination).icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_pin));

				}

				mPinHandler.post(new Runnable()
				{

					@Override
					public void run()
					{
						try
						{
							if ((mapMarkerCount % 2) == 0)
							{
								markertemp1 = mMap.addMarker(mNewMarkerDestination);
								//							.center(point)   //set center
								//							  .radius(500)   //set radius in meters
								//							  .fillColor(0x40ff0000)  //semi-transparent
								//							  .strokeColor(Color.BLUE)
								//							  .strokeWidth(5);

								//firstCircle=mMap.addCircle(new CircleOptions().center(mNewLatLngDestination).radius(300).fillColor(0x40ff0000).strokeColor(Color.BLUE).strokeWidth(2));

							}
							else
							{
								markertemp2 = mMap.addMarker(mNewMarkersource);

								//secondCircle=mMap.addCircle(new CircleOptions().center(mNewLatLngSource).radius(300).fillColor(0x40ff0000).strokeColor(Color.BLUE).strokeWidth(2));

							}
						}
						catch (Exception e)
						{
							// TODO Auto-generated catch block
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

					}
				});

			}
		}).start();

		/*new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				mapMarkerCount = mapMarkerCount + 1;
				if ((mapMarkerCount % 2) == 0)
				{

					mNewLatLngDestination = new LatLng(ponts.latitude, ponts.longitude);
					mNewLatLongDesination = new ArrayList<Address>();
					JSONObject ret = Utility.getLocationInfo(ponts.latitude, ponts.longitude);
					Log.e("address string", ret.toString());

					JSONObject location;

					try
					{
						location = ret.getJSONArray("results").getJSONObject(0);
						mNewPinSourceString = location.getString("formatted_address");
						Log.d("test", "formattted address:" + mNewPinSourceString);
					}
					catch (JSONException e1)
					{
						e1.printStackTrace();

					}
					mNewMarkerDestination = new MarkerOptions().position(ponts).title(mNewPinSourceString).icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_pin));

				}
				else
				{
					mNewLatLngSource = new LatLng(ponts.latitude, ponts.longitude);
					mNewLatLongSource = new ArrayList<Address>();
					JSONObject ret = Utility.getLocationInfo(ponts.latitude, ponts.longitude);

					JSONObject location;

					try
					{
						location = ret.getJSONArray("results").getJSONObject(0);
						mNewPinDestination = location.getString("formatted_address");
						Log.d("test", "formattted address:" + mNewPinSourceString);
					}
					catch (JSONException e1)
					{
						e1.printStackTrace();

					}
					mNewMarkersource = new MarkerOptions().position(ponts).title(mNewPinDestination).icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_pin));
					
					

				}

			}
		}).start();

		showDialogOnMapClick();*/

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
		mCustomProgressDialog = new CustomProgressDialog(MapScreenActivity.this, "");

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
						mCustomProgressDialog.dismissDialog();

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
									Utility.showMsgDialog(MapScreenActivity.this, msg);
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
			final Dialog dialog = new Dialog(MapScreenActivity.this);
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
		//imageLoader.displayImage(imageurl, mPhotoDisplayImageView, Utility.getFitXYDisplayOption(), null);
		mDialogShow.show();
		imageLoader.displayImage(imageurl, mPhotoDisplayImageView, Utility.getFitXYDisplayOption(), Utility.getImageLoaderListner(MapScreenActivity.this));

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

	private void showScreenShareDialog()
	{
		mImagePath = "";
		mVideoPath = "";
		mAudiopath = "";

		final Dialog dialog = new Dialog(MapScreenActivity.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.post_audio_video_dialog);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.setCancelable(false);

		mTextViewPhoto = (TextView) dialog.findViewById(R.id.post_audio_video_dialog_textview_photo);
		mTextViewVideo = (TextView) dialog.findViewById(R.id.post_audio_video_dialog_textview_video);
		mImageViewPhoto = (ImageView) dialog.findViewById(R.id.post_audio_video_dialog_imageview_photo);
		mImageViewPhotoCross = (ImageView) dialog.findViewById(R.id.post_audio_video_dialog_imageview_photo_cross);
		mImageViewVideo = (ImageView) dialog.findViewById(R.id.post_audio_video_dialog_imageview_video);
		mImageViewVideoCross = (ImageView) dialog.findViewById(R.id.post_audio_video_dialog_imageview_video_cross);
		mRelLayoutPhoto = (RelativeLayout) dialog.findViewById(R.id.post_audio_video_dialog_rel_layout_photo);
		mRelLayoutVideo = (RelativeLayout) dialog.findViewById(R.id.post_audio_video_dialog_rel_layout_video);
		mEditTextTitle = (EditText) dialog.findViewById(R.id.post_audio_video_dialog_edittext_title);
		mEditTextComment = (EditText) dialog.findViewById(R.id.post_audio_video_dialog_edittext_comment);
		mTextViewEnterPost = (TextView) dialog.findViewById(R.id.post_audio_video_dialog_textview_enterpost);

		closedialog = (TextView) (TextView) dialog.findViewById(R.id.post_audio_video_dialog_textview_close_dialog);

		final Dialog _dialog = dialog;

		closedialog.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				_dialog.dismiss();

			}
		});

		mTextViewPhoto.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				getImageFromGalleryCamera();

			}
		});

		mTextViewVideo.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				chooseAudioOrVideo();

			}
		});

		mImageViewPhotoCross.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				mImagePath = "";
				mTextViewPhoto.setVisibility(View.VISIBLE);
				mRelLayoutPhoto.setVisibility(View.GONE);

			}
		});

		mImageViewVideoCross.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				mVideoPath = "";
				mAudiopath = "";
				mRelLayoutVideo.setVisibility(View.GONE);
				mTextViewVideo.setVisibility(View.VISIBLE);

			}
		});

		mTextViewEnterPost.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (isValid())
				{
					CallWebApi();
					dialog.dismiss();
				}
			}
		});

		dialog.show();

	}

	private void getImageFromGalleryCamera()
	{
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle(R.string.app_name);
		adb.setMessage("Choose Photo From");
		adb.setIcon(R.drawable.appicon);

		// adb.setIcon(android.R.drawable.ic_dialog_alert);

		adb.setPositiveButton(getResources().getString(R.string.lbl_camera), new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				imageCaptureRefrence.captureImage(MapScreenActivity.this);

			}
		});

		adb.setNegativeButton(getResources().getString(R.string.lbl_gallery), new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				imageCaptureRefrence.selectFromGalley(MapScreenActivity.this);

			}
		});
		adb.show();

	}

	private void chooseAudioOrVideo()
	{
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle(R.string.app_name);
		adb.setMessage("Choose Audio or Video");
		adb.setIcon(R.drawable.appicon);

		//adb.setIcon(android.R.drawable.ic_dialog_alert);

		adb.setPositiveButton("Audio", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				Intent intent = new Intent();
				intent.setType("audio/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(Intent.createChooser(intent, "Select Audio "), REQUEST_CODE_AUDIO);

			}
		});

		adb.setNegativeButton("Video", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				getVideo();

			}
		});
		adb.show();

	}

	private void getVideo()
	{

		if (mVidFromCamera == null)
			mVidFromCamera = new SetVideoFromCamera(this);
		mVidFromCamera.showVideoDialog();

	}

	public void setVideoPreview(String mVideoPath, Bitmap image)
	{
		this.mVideoPath = mVideoPath;
		mImageViewVideo.setImageBitmap(image);
		mRelLayoutVideo.setVisibility(View.VISIBLE);
		mTextViewVideo.setVisibility(View.GONE);
	}

	@Override
	protected void onActivityResult(int req, int result, Intent data)
	{
		super.onActivityResult(req, result, data);

		if (result == RESULT_OK)
		{

			switch (req)
			{
				case Constant.REQUEST_CODE_IMAGE_CAPTURED:
					mImagePath = Utility.onPhotoTaken(data, this);

					try
					{
						exif = new ExifInterface(mImagePath);
					}
					catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (mImagePath != null)
					{
						try
						{
							mSavePath = Utility.getCacheFilePath(this);
							Log.e("path is ", mSavePath);

							mImageViewPhoto.setImageBitmap(Utility.getCroppedBitmap(BitmapFactory.decodeFile(mImagePath), Integer.parseInt(exif.getAttribute(ExifInterface.TAG_ORIENTATION))));
							mRelLayoutPhoto.setVisibility(View.VISIBLE);
							mTextViewPhoto.setVisibility(View.GONE);
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

					}
					break;

				case Constant.REQUEST_CODE_IMAGE_GALLERY:
					mImagePath = Utility.onPhotoTakenFromgallery(data, this);
					try
					{
						exif = new ExifInterface(mImagePath);
					}
					catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (mImagePath != null)
					{
						mSavePath = Utility.getCacheFilePath(this);
						Log.e("path is gallery ", mSavePath);

						mImageViewPhoto.setImageBitmap(Utility.getCroppedBitmap(BitmapFactory.decodeFile(mImagePath), Integer.parseInt(exif.getAttribute(ExifInterface.TAG_ORIENTATION))));

						mRelLayoutPhoto.setVisibility(View.VISIBLE);
						mTextViewPhoto.setVisibility(View.GONE);
					}
					break;

				case Constant.REQUEST_CODE_VIDEO_CAPTURED:
					mVidFromCamera.onVideoTakenResult(req, result, data);
					break;

				case Constant.REQUEST_CODE_VIDEO_GALLERY:
					mVidFromCamera.onVideoTakenResult(req, result, data);

					break;

				case REQUEST_CODE_AUDIO:
					//					if(Utility.onAudioTake(data, this)!=null){
					//						mAudiopath=Utility.onAudioTake(data, this);
					//						
					//						mImageViewVideo.setImageResource(R.drawable.check_icon);
					//						mRelLayoutVideo.setVisibility(View.VISIBLE);
					//						mTextViewVideo.setVisibility(View.GONE);
					//					}
					Uri fileuri = data.getData();
					if (fileuri != null)
					{
						try
						{
							mAudiopath = Utility.getFileNameByUri(this, fileuri);
						}
						catch (Exception e)
						{
							mAudiopath = "";
						}

						if (mAudiopath != null && !mAudiopath.equals(""))
						{
							mImageViewVideo.setImageResource(R.drawable.code_five_selected);
							mRelLayoutVideo.setVisibility(View.VISIBLE);
							mTextViewVideo.setVisibility(View.GONE);
						}
					}

					break;

				default:
					break;
			}

		}
	}

	private void CallWebApi()
	{
		mCustomProgressDialog = new CustomProgressDialog(MapScreenActivity.this, "");
		new Thread(new Runnable()
		{

			String response = "";
			private Handler mShareHandler = new Handler();

			@Override
			public void run()
			{

				JSONObject jsonRequest = new JSONObject();

				JSONObject getLocationName = Utility.getLocationInfo(gpsTracker.getLatitude(), gpsTracker.getLongitude());
				JSONObject Currentlocation;

				{

					try
					{
						Currentlocation = getLocationName.getJSONArray("results").getJSONObject(0);
						Log.e("RESULT STRING ", Currentlocation.toString());
						String location_string = Currentlocation.getString("formatted_address");

						jsonRequest.put("Title", mEditTextTitle.getText().toString().trim());
						jsonRequest.put("LocationName", location_string);
						jsonRequest.put("Comment", mEditTextComment.getText().toString().trim());
						jsonRequest.put("CurrentLongitude", gpsTracker.getLongitude());
						jsonRequest.put("CurrentLatitude", gpsTracker.getLatitude());
						jsonRequest.put("TripId", Utility.getSharedPrefStringData(MapScreenActivity.this, "TripId"));
						jsonRequest.put("Userid", Utility.getSharedPrefString(MapScreenActivity.this, "userId"));

					}
					catch (JSONException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Log.e("params for first", jsonRequest.toString());
					String url = APIUtils.BASE_URL + APIUtils.SHARESCREEN;

					response = Utility.sendJsonWithFileOnShareScreen(url, mImagePath, mVideoPath, mAudiopath, jsonRequest.toString(), APIUtils.SHARESCREEN);
					Log.e("response", response.toString());
					mShareHandler.post(new Runnable()
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
										Utility.showToastMessage(MapScreenActivity.this, "Shared successfully");
										JSONObject resultJsonObject = responseJsonObject.getJSONObject("Result");
										//Utility.showMsgDialogForFinish(ShareActivity.this, getResources().getString(R.string.drawlinesuccess));
										putNewPointonMap(resultJsonObject.getString("Response"), gpsTracker.getLatitude(), gpsTracker.getLongitude());
									}
									else
									{
										msg = responseJsonObject.optString("Message");
										Utility.showToastMessage(MapScreenActivity.this, "Unable to Share");
										//Utility.showMsgDialog(ShareActivity.this, msg);
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
			}
		}).start();

	}

	boolean isValid()
	{
		if (mEditTextTitle.getText().toString().trim().length() <= 0)
		{
			Utility.showMsgDialog(this, "Please enter title !");
			return false;
		}
		//		else
		//			if (mEditTextComment.getText().toString().trim().length() <= 0)
		//			{
		//				Utility.showMsgDialog(this, "Please enter comments");
		//				return false;
		//			}

		return true;
	}

	private void getCustomDialogForColorLines()
	{

		mDialogShow = new Dialog(MapScreenActivity.this);
		mDialogShow.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mDialogShow.setContentView(R.layout.color_dialog);
		mDialogShow.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		mDialogShow.setCancelable(false);

		mColor_dialogTextViewComment = (TextView) mDialogShow.findViewById(R.id.color_dialog_comment_edittext);

		mCategoryTextView = (TextView) mDialogShow.findViewById(R.id.category_dropdown_textview);
		mColorOneTextView = (TextView) mDialogShow.findViewById(R.id.code_one);
		mColorTwoTextView = (TextView) mDialogShow.findViewById(R.id.code_two);
		mColorThreeTextView = (TextView) mDialogShow.findViewById(R.id.code_three);
		mColorFourTextView = (TextView) mDialogShow.findViewById(R.id.code_four);
		mColorFiveTextView = (TextView) mDialogShow.findViewById(R.id.code_five);
		mColorSixTextView = (TextView) mDialogShow.findViewById(R.id.code_six);
		mColorSevenTextView = (TextView) mDialogShow.findViewById(R.id.code_seven);
		mColorEightTextView = (TextView) mDialogShow.findViewById(R.id.code_eight);
		mCancelColorPopupImageView = (ImageView) mDialogShow.findViewById(R.id.color_pop_cancel_imgview);
		mColorOneTextView.setTag("0");
		mColorTwoTextView.setTag("0");
		mColorThreeTextView.setTag("0");
		mColorFourTextView.setTag("0");
		mColorFiveTextView.setTag("0");
		mColorSixTextView.setTag("0");
		mColorSevenTextView.setTag("0");
		mColorEightTextView.setTag("0");
		mColorOneTextView.setOnClickListener(this);
		mColorTwoTextView.setOnClickListener(this);
		mColorOneTextView.setOnClickListener(this);
		mColorThreeTextView.setOnClickListener(this);
		mColorFourTextView.setOnClickListener(this);
		mColorFiveTextView.setOnClickListener(this);
		mColorSixTextView.setOnClickListener(this);
		mColorSevenTextView.setOnClickListener(this);
		mColorEightTextView.setOnClickListener(this);
		mCategoryTextView.setOnClickListener(this);
		mCancelColorPopupImageView.setOnClickListener(this);

		mDrowLineCommentEditText = (EditText) mDialogShow.findViewById(R.id.color_dialog_comment_edittext);
		mDrowLineShareTextView = (TextView) mDialogShow.findViewById(R.id.color_dialog_share_textview);

		Utility.textViewFontRobotoLight(mColor_dialogTextViewComment, getAssets());
		Utility.textViewFontRobotoLight(mDrowLineShareTextView, getAssets());
		Utility.textViewFontRobotoLight(mDrowLineCommentEditText, getAssets());

		mDialogShow.show();
		mDrowLineShareTextView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

				if ((mColorOneTextView.getTag().equals("0")) && (mColorTwoTextView.getTag().equals("0")) && (mColorThreeTextView.getTag().equals("0")) && (mColorFourTextView.getTag().equals("0"))
						&& (mColorFiveTextView.getTag().equals("0")) && (mColorSixTextView.getTag().equals("0")) && (mColorSevenTextView.getTag().equals("0"))
						&& (mColorEightTextView.getTag().equals("0")))
				{
					Utility.showMsgDialog(MapScreenActivity.this, "Please select one color.");

				}
				else
					if (Utility.isFieldEmpty(mCategoryTextView.getText().toString().trim()))
					{
						Utility.showMsgDialog(MapScreenActivity.this, "Please Select one category.");
					}
					else
					{
						mDialogShow.dismiss();

						mDrawLineProgressBar = new ProgressDialog(MapScreenActivity.this);
						mDrawLineProgressBar.setTitle("Please Wait..");
						mDrawLineProgressBar.show();

						new Thread(new Runnable()
						{
							String response = "";

							@Override
							public void run()
							{
								JSONObject jsonObj = new JSONObject();
								try
								{
									checkColorCode();

									jsonObj.put("StartLongitude", mPtLatLngSource.longitude);
									jsonObj.put("StartLatitude", mPtLatLngSource.latitude);
									jsonObj.put("EndLongitude", mPtLatLngDestination.longitude);
									jsonObj.put("EndLatitude", mPtLatLngDestination.latitude);
									if (Utility.isFieldEmpty(mDrowLineCommentEditText.getText().toString().trim()))
									{
										jsonObj.put("Comment", "No Comment Entered.");
									}
									else
									{
										jsonObj.put("Comment", mDrowLineCommentEditText.getText().toString().trim());
									}
									jsonObj.put("Color", mColorCode);
									jsonObj.put("TripId", mTripIdFinish);
									jsonObj.put("CategoryId", mSelectedCategoryId);
									jsonObj.put("Userid", Utility.getSharedPrefString(MapScreenActivity.this, "userId"));
									jsonObj.put("StartPointName", mNewPinDestination);
									jsonObj.put("EndPointName", mNewPinSourceString);
									//jsonObj.put("deviceToken",SplashActivity.regId);

									/*"StartPointName": "sample string 9",
									  "": "sample string 10",*/

								}
								catch (Exception e)
								{
									// TODO: handle exception
								}

								Log.e("params for first", jsonObj.toString());
								String url = APIUtils.BASE_URL + APIUtils.DRAWLINE;
								response = Utility.POST(url, jsonObj.toString(), APIUtils.DRAWLINE);

								mDrawLineHandler.post(new Runnable()
								{
									@Override
									public void run()
									{
										mDrawLineProgressBar.dismiss();

										if (response != null && response.length() > 0)
										{
											Log.e("msg", "is:" + response);

											try
											{
												JSONObject responseJsonObject = new JSONObject(response);
												if (responseJsonObject.optBoolean("Success"))
												{

													getNewDrawLine();
													Utility.showToastMessage(MapScreenActivity.this, getString(R.string.share_ui_ed_alert_success));
													//Utility.showMsgDialog(MapScreenActivity.this, getString(R.string.share_ui_ed_alert_success));

												}
												else
												{
													Utility.showToastMessage(MapScreenActivity.this, getString(R.string.share_ui_ed_alert_notsuccess));
													//Utility.showMsgDialog(MapScreenActivity.this, getString(R.string.share_ui_ed_alert_notsuccess));
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

			}
		});

	}

	private void checkColorCode()
	{
		if (mColorOneTextView.getTag().equals("1"))
		{
			mColorCode = getResources().getString(R.color.color_One);
		}
		else
			if (mColorTwoTextView.getTag().equals("1"))
			{
				mColorCode = getResources().getString(R.color.color_Two);

			}
			else
				if (mColorThreeTextView.getTag().equals("1"))
				{
					mColorCode = getResources().getString(R.color.color_Three);

				}
				else
					if (mColorFourTextView.getTag().equals("1"))
					{
						mColorCode = getResources().getString(R.color.color_Four);

					}
					else
						if (mColorFiveTextView.getTag().equals("1"))
						{
							mColorCode = getResources().getString(R.color.color_Five);

						}
						else
							if (mColorSixTextView.getTag().equals("1"))
							{
								mColorCode = getResources().getString(R.color.color_Six);

							}
							else
								if (mColorSevenTextView.getTag().equals("1"))
								{
									mColorCode = getResources().getString(R.color.color_Seven);

								}
								else
									if (mColorEightTextView.getTag().equals("1"))
									{
										mColorCode = getResources().getString(R.color.color_Eight);

									}

	}

	private void getNewDrawLine()
	{

		new Thread(new Runnable()
		{
			Handler mtempHendler = new Handler();

			@Override
			public void run()
			{
				final PolylineOptions rectLine = new PolylineOptions();

				ArrayList<LatLng> directionPoint = null;
				try
				{
					doc = gmapv.getDocument(mPtLatLngSource, mPtLatLngDestination, GMapV2Direction.MODE_DRIVING);
					directionPoint = gmapv.getDirection(doc);

					if (mColorCode.equals(getResources().getString(R.color.color_One)))
					{

						rectLine.width(15).color(getResources().getColor(R.color.color_One));
					}
					else
						if (mColorCode.equals(getResources().getString(R.color.color_Two)))
						{

							rectLine.width(15).color(getResources().getColor(R.color.color_Two));
						}
						else
							if (mColorCode.equals(getResources().getString(R.color.color_Three)))
							{

								rectLine.width(15).color(getResources().getColor(R.color.color_Three));
							}
							else
								if (mColorCode.equals(getResources().getString(R.color.color_Four)))
								{

									rectLine.width(15).color(getResources().getColor(R.color.color_Four));
								}
								else
									if (mColorCode.equals(getResources().getString(R.color.color_Five)))
									{

										rectLine.width(15).color(getResources().getColor(R.color.color_Five));
									}
									else
										if (mColorCode.equals(getResources().getString(R.color.color_Six)))
										{

											rectLine.width(15).color(getResources().getColor(R.color.color_Six));
										}
										else
											if (mColorCode.equals(getResources().getString(R.color.color_Seven)))
											{

												rectLine.width(15).color(getResources().getColor(R.color.color_Seven));
											}
											else
												if (mColorCode.equals(getResources().getString(R.color.color_Eight)))
												{

													rectLine.width(15).color(getResources().getColor(R.color.color_Eight));
												}

					Log.i("size points", "size" + directionPoint.size());
					for (int j = 0; j < directionPoint.size(); j++)
					{

						rectLine.add(directionPoint.get(j));

					}
					mtempHendler.post(new Runnable()
					{

						@Override
						public void run()
						{
							//							jsonObj.put("StartLongitude", mNewLatLngSource.longitude);
							//							jsonObj.put("StartLatitude", mNewLatLngSource.latitude);
							//							jsonObj.put("EndLongitude", mNewLatLngDestination.longitude);
							//							jsonObj.put("EndLatitude", mNewLatLngDestination.latitude);

							//firstCircle.remove();
							//secondCircle.remove();

							// mNewMarkerDestination .title(mNewPinSourceString)
							markertemp1.setSnippet("Category: " + mCategoryTextView.getText().toString() + ",\t\n Details: " + mDrowLineCommentEditText.getText().toString().trim());
							//.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_pin));

							// mNewMarkersource.title(mNewPinDestination)
							markertemp2.setSnippet("Category: " + mCategoryTextView.getText().toString() + ",\t\n Details: " + mDrowLineCommentEditText.getText().toString().trim());
							//.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_pin));

							//mMap.addMarker(mNewMarkerDestination);
							//mMap.addMarker(mNewMarkersource);
						}
					});

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
						//mRoutListLatLngs = gmapv.getDirection(doc);
						mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mPtLatLngSource, 15));
						Log.e("rout array lis", mRoutListLatLngs.toString());
						mPtLatLngSource = null;
						mPtLatLngDestination = null;

					}
				});

			}
		}).start();

	}

	/*@Override
	public void onLocationChanged(Location location)
	{
		if (now != null)
		{
			mPrevLatLng = new LatLng(now.getPosition().latitude, now.getPosition().longitude);

			now.remove();

		}
		//
		//		markerSource = new MarkerOptions().position(mCurrentTripLatLngSource).title(Constant.mCurrentTripDataBean.getTripArray().get(0).getStartPointName())
		//				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
		//		
		// Getting latitude of the current location
		double latitude = location.getLatitude();

		// Getting longitude of the current location
		double longitude = location.getLongitude();

		Log.e("location", "location");

		// Creating a LatLng object for the current location
		LatLng latLng = new LatLng(latitude, longitude);
		mCurrentLatLng = latLng;
		now = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin)));
		// Showing the current location in Google Map
		//		mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		//
		//		// Zoom in the Google Map
		//		mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

		//		animateMarker(now, mCurrentLatLng, false);

	}*/

	private void setUpRefreshMap()
	{

		try
		{

			mCurrentTripLatLngSource = new LatLng(Double.parseDouble(Constant.mRefreshTripDataBean.getTripArray().get(0).getStartLatitude()), Double.parseDouble(Constant.mRefreshTripDataBean
					.getTripArray().get(0).getStartLongitude()));

			/*	mCurrentTripLatLngDestination = new LatLng(Double.parseDouble(Constant.mRefreshTripDataBean.getTripArray().get(0).getEndLatitude()), Double.parseDouble(Constant.mRefreshTripDataBean
						.getTripArray().get(0).getEndLongitude()));*/

			// create marker
			//	markerSource = new MarkerOptions().position(mCurrentTripLatLngSource).icon(BitmapDescriptorFactory.fromResource(R.drawable.truck_icon));
			/*	markerDestin = new MarkerOptions().position(mCurrentTripLatLngDestination).title(Constant.mRefreshTripDataBean.getTripArray().get(0).getEndPointName())
						.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin));*/
			// adding marker
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrentTripLatLngSource, 15));
			mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

			//marker for second destination

			/*if (Constant.mRefreshTripDataBean.getTripArray().get(0).getmSndPtLat().length() > 0)
			{

				mCurrentTripLatLngDestination = new LatLng(Double.parseDouble(Constant.mRefreshTripDataBean.getTripArray().get(0).getmSndPtLat()), Double.parseDouble(Constant.mRefreshTripDataBean
						.getTripArray().get(0).getmSndPtLog()));

				mSndSrsMarker = new MarkerOptions().position(mCurrentTripLatLngDestination).title(Constant.mRefreshTripDataBean.getTripArray().get(0).getmSndPtName())
						.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin));
			}*/
			//			markerSource = new MarkerOptions().position(HomeActivity.latlng_Source/*new LatLng(curLattitude, curLongitude)*/)
			//					.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin))
			//					.title(HomeActivity.location_string);

			//mMap.addMarker(markerSource);
			/*mMap.addMarker(markerDestin);
			mMap.addMarker(mSndSrsMarker);*/
			mCustomProgressDialog = new CustomProgressDialog(MapScreenActivity.this, "");
			mCustomProgressDialog.setCancelable(false);
			new Thread(new Runnable()
			{

				@Override
				public void run()
				{
					final PolylineOptions rectLine = new PolylineOptions();

					ArrayList<LatLng> directionPoint = null;
					//					try
					//					{
					//						doc = gmapv.getDocument(mCurrentTripLatLngSource, mCurrentTripLatLngDestination, GMapV2Direction.MODE_DRIVING);
					//						directionPoint = gmapv.getDirection(doc);
					//						rectLine.width(15).color(Color.GREEN);
					//						Log.i("size points", "size" + directionPoint.size());
					//						for (int j = 0; j < directionPoint.size(); j++)
					//						{
					//
					//							rectLine.add(directionPoint.get(j));
					//
					//						}
					//
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

					mCurrentTripMapHandler.post(new Runnable()
					{

						@Override
						public void run()
						{
							mCustomProgressDialog.dismissDialog();
							//	poly = mMap.addPolyline(rectLine);

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

									getNewDrawLine(mNewLatLngSource, mNewLatLngDestination, mColor);
								}

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
								e2.printStackTrace();
							}

						}
					});

				}
			}).start();

		}
		catch (Exception ee)
		{

		}

	}

	public void animateMarker(final Marker marker, final LatLng toPosition, final boolean hideMarker)
	{
		final Handler handler = new Handler();
		final long start = SystemClock.uptimeMillis();
		Projection proj = mMap.getProjection();
		Point startPoint = proj.toScreenLocation(marker.getPosition());
		final LatLng startLatLng = proj.fromScreenLocation(startPoint);
		final long duration = 50;

		final Interpolator interpolator = new LinearInterpolator();

		handler.post(new Runnable()
		{
			@Override
			public void run()
			{
				long elapsed = SystemClock.uptimeMillis() - start;

				float t = interpolator.getInterpolation((float) elapsed / duration);
				Log.e("t value", "" + t);
				double lng = t * toPosition.longitude + (1 - t) * startLatLng.longitude;
				double lat = t * toPosition.latitude + (1 - t) * startLatLng.latitude;
				marker.setPosition(new LatLng(lat, lng));

				if (t < 1.0)
				{
					// Post again 16ms later.
					handler.postDelayed(this, 16);
				}
				else
				{
					if (hideMarker)
					{
						marker.setVisible(false);
					}
					else
					{
						marker.setVisible(true);
					}
				}

				try
				{
					getNewPolyLine(toPosition);

				}
				catch (Exception e)
				{

					e.printStackTrace();
				}

				catch (Throwable e)
				{

					e.printStackTrace();
				}

			}

		});
	}

	private void getNewPolyLine(final LatLng toPosition)
	{

		new Thread(new Runnable()
		{
			PolylineOptions rectLine = new PolylineOptions();;

			@Override
			public void run()
			{
				try
				{
					rectLine = new PolylineOptions();
					if (mReuseTrip.equalsIgnoreCase("2") || Utility.getSharedPrefStringData(MapScreenActivity.this, "reuse").equalsIgnoreCase("yes"))
					{
						rectLine.width(15).color(Color.YELLOW);
					}
					else
					{
						rectLine.width(15).color(Color.GREEN);
					}

					try
					{
						rectLine.addAll(mRoutListLatLngsCurrentLine);

						/*Iterator<LatLng> iterator = mRoutListLatLngsCurrentLine.iterator();
						while (iterator.hasNext())
						{
							rectLine.add(iterator.next());
						}*/
					}
					catch (Exception e1)
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					catch (OutOfMemoryError e)
					{
						e.printStackTrace();
					}
					catch (Throwable e)
					{
						e.printStackTrace();
					}

					//rectLine.addAll(/*HomeActivity.latlng_Source, new LatLng(Double.parseDouble("28.6629"), Double.parseDouble("77.2100"))*/mRoutListLatLngsCurrentLine);

					mMapHandler1.post(new Runnable()
					{

						@Override
						public void run()
						{

							try
							{
								poly = mMap.addPolyline(rectLine);
							}
							catch (Exception e)
							{
								// TODO Auto-generated catch block
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

							//animateMarker(marker, mRoutListLatLngs.get(1), false);

						}

					});
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
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

			}
		}).start();

	}

	@Override
	protected void onDestroy()
	{

		super.onDestroy();
	}

	@Override
	public void onStop()
	{
		if (timer != null)
			timer.cancel();
		if (!mCancelFlag)
		{
			if (mRoutListLatLngsCurrentLine != null && mRoutListLatLngsCurrentLine.size() > 0)
			{

				Utility.SaveArraylistObject(mRoutListLatLngsCurrentLine, "objectarraylist", MapScreenActivity.this);

				Constant.RoutListLatLngsCurrentLine = mRoutListLatLngsCurrentLine;

				//Utility.saveArrayList(mRoutListLatLngsCurrentLine, "currentroute", MapScreenActivity.this);
			}
		}

		super.onStop();
	}

	@Override
	public void onChngedLoc(final Location object)
	{
		LatLng prev = null;
		double distance = 0.0;
		if (mRoutListLatLngsCurrentLine.size() > 2)
			distance = CalculationByDistance(mRoutListLatLngsCurrentLine.get(mRoutListLatLngsCurrentLine.size() - 1), new LatLng(object.getLatitude(), object.getLongitude()));
		Log.e("distance", "is" + distance);
		if (distance > 500)
		{
			final LatLng prevNew = prev;
			new Thread(new Runnable()
			{
				LatLng prevNew1 = prevNew;

				@Override
				public void run()
				{

					addlatLong(mRoutListLatLngsCurrentLine.get(mRoutListLatLngsCurrentLine.size() - 1), new LatLng(object.getLatitude(), object.getLongitude()));
					mMapHandlerAdd.post(new Runnable()
					{

						@Override
						public void run()
						{

							if (mRoutListLatLngsCurrentLine.size() >= 2)
								prevNew1 = mRoutListLatLngsCurrentLine.get(mRoutListLatLngsCurrentLine.size() - 2);
							else
								prevNew1 = mRoutListLatLngsCurrentLine.get(mRoutListLatLngsCurrentLine.size() - 1);

							try
							{
								getNewthread(prevNew1, new LatLng(object.getLatitude(), object.getLongitude()));

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

						}
					});
				}
			}).start();

		}
		else
		{
			if (!mRoutListLatLngsCurrentLine.contains(new LatLng(object.getLatitude(), object.getLongitude())))
				mRoutListLatLngsCurrentLine.add(new LatLng(object.getLatitude(), object.getLongitude()));
			if (mRoutListLatLngsCurrentLine.size() >= 2)
				prev = mRoutListLatLngsCurrentLine.get(mRoutListLatLngsCurrentLine.size() - 2);
			else
				prev = mRoutListLatLngsCurrentLine.get(mRoutListLatLngsCurrentLine.size() - 1);

			try
			{
				getNewthread(prev, new LatLng(object.getLatitude(), object.getLongitude()));

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

		}

	}

	private void addlatLong(LatLng source, LatLng dest)
	{

		try
		{
			doc = gmapv.getDocument(source, dest, GMapV2Direction.MODE_DRIVING);
			ArrayList<LatLng> directionPoint = gmapv.getDirection(doc);
			Log.i("size points", "size" + directionPoint.size());
			for (int j = 1; j < directionPoint.size(); j++)
			{
				if (!mRoutListLatLngsCurrentLine.contains(directionPoint.get(j)))
					mRoutListLatLngsCurrentLine.add(directionPoint.get(j));

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

	}

	private double CalculationByDistance(LatLng StartP, LatLng EndP)
	{

		double distance;

		Location locationA = new Location("point A");

		locationA.setLatitude(StartP.latitude);

		locationA.setLongitude(StartP.longitude);

		Location locationB = new Location("point B");

		locationB.setLatitude(EndP.latitude);

		locationB.setLongitude(EndP.longitude);

		distance = locationA.distanceTo(locationB);

		/*double Radius = 6371;
		double lat1 = StartP.getLatitudeE6() / 1E6;
		double lat2 = EndP.getLatitudeE6() / 1E6;
		double lon1 = StartP.getLongitudeE6() / 1E6;
		double lon2 = EndP.getLongitudeE6() / 1E6;
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double temp = Radius * c;
		temp = temp * 0.621;*/
		return distance;
	}

	/*private void setupSensorManager()
	{

		SensorManager mSensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
		mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_NORMAL);

		//Log.d(TAG, "SensorManager setup");
	}*/

	public void draw1(float angle)
	{
		// Take the relevant Marker from the marker list where available in map

		if (mMoveingMarker == null)
		{
			return;
		}
		mMoveingMarker.setRotation(angle); // set the orientation value returned from the senserManager
	}

}
