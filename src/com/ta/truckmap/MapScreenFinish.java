package com.ta.truckmap;

import java.io.IOException;
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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.ta.truckmap.gpstracking.GMapV2Direction;
import com.ta.truckmap.gpstracking.GPSTracker;
import com.ta.truckmap.rest.api.APIUtils;
import com.ta.truckmap.util.Constant;
import com.ta.truckmap.util.CustomDialog;
import com.ta.truckmap.util.CustomDialog.DIALOG_TYPE;
import com.ta.truckmap.util.CustomProgressDialog;
import com.ta.truckmap.util.IActionOKCancel;
import com.ta.truckmap.util.LoadAdViewInEveryActivity;
import com.ta.truckmap.util.Utility;
import com.truckmap.model.FinishedTripDataBean;
import com.truckmap.parsers.FinishTripParser;

public class MapScreenFinish extends BaseActivity implements OnClickListener, IActionOKCancel
{

	ImageView mMapScreenFinishBackImgview;
	GPSTracker gpsTracker;
	GoogleMap mMap;
	Bundle mGetPossitionBundle;
	int mItemPossion;
	private MarkerOptions markerSource, markerDestin, mNewMarkersource, mNewMarkerDestination, mSecondPointMarker;
	Document doc;

	Polyline poly;
	GMapV2Direction gmapv = new GMapV2Direction();
	Handler mMapHandler = new Handler();
	LinearLayout mAddMobsLinearLayout;
	private LatLng latlngSource, latlngDestination, mNewLatLngSource, mLatLongSecondPoint, mNewLatLngDestination;
	CustomProgressDialog mCustomProgressDialog;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private TextView mResuseTextView;
	protected String tripName;
	private FinishTripParser parser;
	private CustomDialog dailog;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapscreenfinish);
		if (getIntent().getExtras() != null)
		{
			mGetPossitionBundle = getIntent().getExtras();
			mItemPossion = mGetPossitionBundle.getInt("position");
		}
		initializeImageLoader();
		initiViewReference();
		checkgps();

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

	private void checkgps()
	{
		gpsTracker = new GPSTracker(MapScreenFinish.this);

		if (gpsTracker.canGetLocation())
		{

			setUpMapIfNeeded();
		}
		else
			gpsTracker.showSettingsAlert();
	}

	private void setUpMapIfNeeded()
	{
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null)
		{
			mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_relative_fragment)).getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null)
			{

				setUpMap();
			}
		}
	}

	private void setUpMap()
	{
		try
		{

			latlngSource = new LatLng(Double.parseDouble(Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getStartLatitude()), Double.parseDouble(Constant.mFinishedTripDataBean
					.getTripArray().get(mItemPossion).getStartLongitude()));

			latlngDestination = new LatLng(Double.parseDouble(Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getEndLatitude()), Double.parseDouble(Constant.mFinishedTripDataBean
					.getTripArray().get(mItemPossion).getEndLongitude()));

			//			=new LatLng(Double.parseDouble(Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).get), Double.parseDouble(Constant.mFinishedTripDataBean
			//					.getTripArray().get(mItemPossion).getEndLongitude()));

			//			if (Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getmSndPtLat().length() > 0)
			//			{
			//
			//				mLatLongSecondPoint = new LatLng(Double.parseDouble(Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getmSndPtLat()), Double.parseDouble(Constant.mFinishedTripDataBean
			//						.getTripArray().get(mItemPossion).getmSndPtLog()));
			//
			//				mSecondPointMarker = new MarkerOptions().position(mLatLongSecondPoint).title(Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getmSndPtName())
			//						.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin));
			//				mMap.addMarker(mSecondPointMarker);
			//			}

			// create marker

			markerSource = new MarkerOptions().position(latlngSource).title(Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getStartPointName())
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin));
			markerDestin = new MarkerOptions().position(latlngDestination).title(Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getEndPointName())
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin));
			// adding marker
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlngSource, 15));
			mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

			mMap.addMarker(markerSource);
			mMap.addMarker(markerDestin);

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

			mCustomProgressDialog = new CustomProgressDialog(MapScreenFinish.this, "");
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

						String routPointArray = Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getmRoutPointLatLongArryString();
						System.out.println(routPointArray);

						JSONArray jsonArray = new JSONArray(routPointArray);
						for (int i = 0; i < jsonArray.length(); i++)
						{
							JSONObject obj = jsonArray.getJSONObject(i);
							LatLng latlong = new LatLng(Double.parseDouble(obj.getString("latitude")), Double.parseDouble(obj.getString("longtitude")));

							Log.e("latlong", "" + latlong);
							if (!directionPoint.contains(latlong))
								directionPoint.add(latlong);
						}

						//						if (mLatLongSecondPoint != null)
						//							doc = gmapv.getDocument(latlngSource, mLatLongSecondPoint, GMapV2Direction.MODE_DRIVING);
						//						else
						doc = gmapv.getDocument(latlngSource, latlngDestination, GMapV2Direction.MODE_DRIVING);

						directionPointestimatedSourceDest = gmapv.getDirection(doc);
						estimatedSourceDestination.width(15).color(Color.MAGENTA);
						for (int k = 0; k < directionPointestimatedSourceDest.size(); k++)
						{
							estimatedSourceDestination.add(directionPointestimatedSourceDest.get(k));
						}

						Log.i("size points", "size" + directionPoint.size());
						rectLine.width(15).color(Color.GREEN);
						for (int j = 0; j < directionPoint.size(); j++)
						{

							rectLine.add(directionPoint.get(j));

						}

					}

					catch (Exception e)
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
							mMap.addPolyline(estimatedSourceDestination);

							if (directionPoint.size() >= 2)
							{
								MarkerOptions finaluplodadingPoint = new MarkerOptions().position(directionPoint.get(directionPoint.size() - 1))
										.title(Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getmSndPtName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.check_icon));
								mMap.addMarker(finaluplodadingPoint);
							}

							if (Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getDrawLineArray().size() > 0)
							{

								for (int i = 0; i < Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getDrawLineArray().size(); i++)
								{

									mNewLatLngSource = new LatLng(Double.parseDouble(Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getDrawLineArray().get(i)
											.getmDrawLineStartLatitude()), Double.parseDouble(Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getDrawLineArray().get(i)
											.getmDrawLineStartLongitude()));

									mNewLatLngDestination = new LatLng(Double.parseDouble(Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getDrawLineArray().get(i)
											.getmDrawEndLatitude()), Double.parseDouble(Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getDrawLineArray().get(i)
											.getmDrawLineEndLongitude()));

									mNewMarkersource = new MarkerOptions()
											.position(mNewLatLngSource)
											.title(Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getDrawLineArray().get(i).getmDrawLineStartName())
											.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_pin))
											.snippet(
													"Category: " + Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getDrawLineArray().get(i).getmCategory() + ",\t \n" + "Details: "
															+ Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getDrawLineArray().get(i).getmDrawComment());

									mNewMarkerDestination = new MarkerOptions()
											.position(mNewLatLngDestination)
											.title(Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getDrawLineArray().get(i).getmDrawLineEndName())
											.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_pin))
											.snippet(
													"Category: " + Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getDrawLineArray().get(i).getmCategory() + ",\t \n" + "Details: "
															+ Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getDrawLineArray().get(i).getmDrawComment());

									String mColor = Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getDrawLineArray().get(i).getmDrawColor();

									mMap.addMarker(mNewMarkerDestination);
									mMap.addMarker(mNewMarkersource);

									getNewDrawLine(mNewLatLngSource, mNewLatLngDestination, mColor);
								}

							}
							//for share screen

							if (Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getShareScreenArray().size() > 0)
							{

								for (int i = 0; i < Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getShareScreenArray().size(); i++)
								{

									LatLng sharedPointLatlong = new LatLng(Double.parseDouble(Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getShareScreenArray().get(i)
											.getShareScreenCurrentLatitude()), Double.parseDouble(Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getShareScreenArray().get(i)
											.getShareScreenCurrentLongitude()));

									//									mNewLatLngDestination = new LatLng(Double.parseDouble(Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getDrawLineArray().get(i)
									//											.getmDrawEndLatitude()), Double.parseDouble(Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getDrawLineArray().get(i)
									//											.getmDrawLineEndLongitude()));

									MarkerOptions shareMarker = new MarkerOptions().position(sharedPointLatlong)
											.title(Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getShareScreenArray().get(i).getShareScreenLocationName())
											.snippet(Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getShareScreenArray().get(i).getShareScreenId());
									mMap.addMarker(shareMarker);

								}

							}

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

	private void initiViewReference()
	{
		mMapScreenFinishBackImgview = (ImageView) findViewById(R.id.mapscreenfinish_ui_backImgview);
		mResuseTextView = (TextView) findViewById(R.id.mapscreenfinish_ui_textview_reuse);

		mMapScreenFinishBackImgview.setOnClickListener(this);
		mResuseTextView.setOnClickListener(this);
		mAddMobsLinearLayout = (LinearLayout) findViewById(R.id.add_mobs_lnrlyt);
		LoadAdViewInEveryActivity.loadAdds(this, mAddMobsLinearLayout);

	}

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub

		if (v == mMapScreenFinishBackImgview)
		{
			finish();
		}
		else
			if (v == mResuseTextView)
			{
				getpopupReuseTrip();
			}

	}

	private void getInfoLayout(final String id)
	{

		final ProgressDialog mCategotyProgressDialog = new ProgressDialog(MapScreenFinish.this);
		mCategotyProgressDialog.setTitle("Please Wait..");
		mCategotyProgressDialog.show();
		final Handler mCancelHandler = new Handler();

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
						mCategotyProgressDialog.dismiss();

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
									Utility.showMsgDialog(MapScreenFinish.this, msg);
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
			final Dialog dialog = new Dialog(MapScreenFinish.this);
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
		mDialogShow.show();
		imageLoader.displayImage(imageurl, mPhotoDisplayImageView, Utility.getFitXYDisplayOption(), Utility.getImageLoaderListner(MapScreenFinish.this));

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

	/**
	 * Method to show dialog on reuse button click
	 */

	private void showReuseDialog1()
	{

		/*final EditText inputFiled = new EditText(this);
		//inputFiled.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		//inputFiled.setFilters( new InputFilter[] { new InputFilter.LengthFilter(6) } );
		inputFiled.setSingleLine(true);
		inputFiled.setText(Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getName());*/

		LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflator.inflate(R.layout.reuse_trip_dialog, null);

		final EditText inputFiled = (EditText) view.findViewById(R.id.reuse_trip_dialog_ed_tripname);
		final TextView info = (TextView) view.findViewById(R.id.reuse_trip_dialog_info);

		inputFiled.setText(Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getName());
		info.setText(Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getmExtraInformation());

		new AlertDialog.Builder(this).setTitle("Reuse Trip").setCancelable(false).setView(view)

		.setPositiveButton("Ok", new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{

				CallReuseTripAPI();

			}
		}).setNegativeButton("Cancel", new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();

			}
		}).create().show();
	}

	private void getpopupReuseTrip()
	{
		String msg = "", height = "", width = "", length = "";
		msg = "Trip Info:" + System.getProperty("line.separator");
		msg = msg + System.getProperty("line.separator") + "TRIP #/NAME: " + Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getName() + System.getProperty("line.separator");
		if (!Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getmTruckHeight().equalsIgnoreCase(""))
		{
			height = Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getmTruckHeight();
			width = Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getmTruckWidth();
			length = Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getmTruckLength();

			msg = msg + System.getProperty("line.separator") + "TRAILER INFO:" + System.getProperty("line.separator") + "TruckHeight: " + height + "FT" + System.getProperty("line.separator")
					+ "TruckWidth: " + width + "FT" + System.getProperty("line.separator") + "TruckLength: " + length + "FT" + System.getProperty("line.separator");

		}

		if (!Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getmCotactDetail().equalsIgnoreCase(""))
		{

			msg = msg + System.getProperty("line.separator") + "Destination Phone: " + Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getmCotactDetail()
					+ System.getProperty("line.separator");
		}

		if (!Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getStartPointName().equalsIgnoreCase(""))
		{

			msg = msg + System.getProperty("line.separator") + "Start Point/Warehouse/Store: " + Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getStartPointName()
					+ System.getProperty("line.separator");
		}

		if (!Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getEndPointName().equalsIgnoreCase(""))
		{

			msg = msg + System.getProperty("line.separator") + "Destination Address: " + Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getEndPointName()
					+ System.getProperty("line.separator");
		}

		msg = msg + System.getProperty("line.separator") + "Route Safe for Truck/Trailer: " + Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getmIsrouteOk()
				+ System.getProperty("line.separator");

		if (!Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getmExtraInformation().equalsIgnoreCase(""))
		{
			msg = msg + System.getProperty("line.separator") + "Important safety Info List: " + System.getProperty("line.separator")
					+ Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getmExtraInformation() + System.getProperty("line.separator");
		}

		if (!Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getmSndPtName().equalsIgnoreCase(""))
		{

			msg = msg + System.getProperty("line.separator") + "Unloading Address: " + Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getmSndPtName()
					+ System.getProperty("line.separator");
		}

		if (!Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getmComment().equalsIgnoreCase(""))
		{

			msg = msg + System.getProperty("line.separator") + "Comments: " + Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getmComment() + System.getProperty("line.separator");
		}

		dailog = CustomDialog.getInstance(MapScreenFinish.this, this, msg + System.getProperty("line.separator") + "This Trip Already exist! Do you want to Reuse it? ", "Mo-Bia",
				DIALOG_TYPE.OK_CANCEL, 1);
		dailog.show();
	}

	void CallReuseTripAPI()
	{

		final Handler mFinishTripHandler = new Handler();
		final ProgressDialog mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle("MO-BIA");
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
		new Thread(new Runnable()
		{

			String response = "";
			String location_string = "";

			@Override
			public void run()
			{
				JSONObject ret = Utility.getLocationInfo(gpsTracker.getLatitude(), gpsTracker.getLongitude());
				JSONObject location;

				JSONObject resueTrip = new JSONObject();
				try
				{
					location = ret.getJSONArray("results").getJSONObject(0);
					location_string = location.getString("formatted_address");

					resueTrip.put("Userid", Utility.getSharedPrefString(MapScreenFinish.this, "userId"));
					resueTrip.put("tripId", Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getTripId());
					resueTrip.put("tripName", Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion).getName());
					resueTrip.put("startLatitude", gpsTracker.getLatitude());
					resueTrip.put("startLongitude", gpsTracker.getLongitude());
					resueTrip.put("startPointName", location_string);

				}
				catch (JSONException e)
				{

					e.printStackTrace();
				}
				Log.e("params for first", resueTrip.toString());
				String url = APIUtils.BASE_URL + APIUtils.REUSE_TRIP;
				response = Utility.POST(url, resueTrip.toString(), APIUtils.REUSE_TRIP);
				Log.e("response", response.toString());

				mFinishTripHandler.post(new Runnable()
				{

					private String msg;

					@Override
					public void run()
					{
						mProgressDialog.dismiss();
						if (response != null && response.length() > 0)
						{
							Log.e("msg", "is:" + response);
							parser = new FinishTripParser();

							try
							{

								JSONObject responseJsonObject = new JSONObject(response);
								msg = responseJsonObject.optString("Message");

								if (responseJsonObject.getString("Success").equalsIgnoreCase("true"))
								{

									if (responseJsonObject.getJSONArray("Result") != null && responseJsonObject.getJSONArray("Result").length() > 0)
									{

										Constant.mCurrentTripDataBean = (FinishedTripDataBean) parser.parse(response);
										Utility.setSharedPrefStringData(MapScreenFinish.this, "TripId", Constant.mCurrentTripDataBean.getTripArray().get(0).getTripId());

										Log.e("trip id from current trip", "is:" + Constant.mCurrentTripDataBean.getTripArray().get(0).getTripId());

										/*	JSONObject result = responseJsonObject.getJSONObject("Result");
											String tripid = result.getString("TripId");*/
										onReuseTripCreated();
									}
								}

								else
								{

									Utility.showMsgDialog(MapScreenFinish.this, msg);
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

	void onReuseTripCreated()
	{

		/*	Constant.mCurrentTripDataBean = Constant.mFinishedTripDataBean.getTripArray().get(mItemPossion);
			Constant.mCurrentTripDataBean.setTripId(tripId);
			Constant.mCurrentTripDataBean.setName(tripName);
			Constant.mCurrentTripDataBean.setStartLatitude(String.valueOf(gpsTracker.getLatitude()));
			Constant.mCurrentTripDataBean.setStartLongitude(String.valueOf(gpsTracker.getLongitude()));
			Constant.mCurrentTripDataBean.setStartPointName(StartPointName);

			Utility.setSharedPrefStringData(this, "TripId", Constant.mCurrentTripDataBean.getTripArray().get(0).getTripId());*/

		Intent currentTripIntent = new Intent(this, MapScreenActivity.class);
		currentTripIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		currentTripIntent.putExtra("currentTrip", "1");
		currentTripIntent.putExtra("reuseTrip", "2");
		currentTripIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(currentTripIntent);
		finish();

	}

	@Override
	public void onActionOk(int requestCode)
	{
		CallReuseTripAPI();
	}

	@Override
	public void onActionCancel(int requestCode)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onActionNeutral(int requestCode)
	{
		// TODO Auto-generated method stub

	}

}
