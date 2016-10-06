package com.ta.truckmap;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
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
import com.ta.truckmap.gpstracking.GMapV2Direction;
import com.ta.truckmap.gpstracking.GPSTracker;
import com.ta.truckmap.rest.api.APIUtils;
import com.ta.truckmap.util.Constant;
import com.ta.truckmap.util.CustomProgressDialog;
import com.ta.truckmap.util.LoadAdViewInEveryActivity;
import com.ta.truckmap.util.Utility;
import com.truckmap.model.FinishedTripDataBean;
import com.truckmap.parsers.FinishTripParser;
import com.truckmap.parsers.Parser;

public class NotificationsDetail extends BaseActivity implements OnClickListener
{
	// for drawline map

	GPSTracker gpsTracker;
	GoogleMap mMap;
	private MarkerOptions markerSource, markerDestin, mNewMarkersource, mNewMarkerDestination;
	Document doc;

	Polyline poly;
	GMapV2Direction gmapv = new GMapV2Direction();
	Handler mMapHandler = new Handler();
	Handler mPinHandler = new Handler();
	private LatLng latlngSource, latlngDestination, mNewLatLngSource, mNewLatLngDestination;

	ImageView mNotificationsDetailBackImgView, mNotificationsDetailProfileImgView;
	TextView mNotificationsDetailAboutTripTextView, mNotificationsDetailsLogoTextView, mNotificationsDetailLocationNameTextView;
	String mNotificationId, mNotificationType;
	Bundle mNotificationDataBundle;
	Handler mNotificationHandler = new Handler();
	LinearLayout mInflaterRowLayout, mRowMapLinearLayout, mAddMobsLinearLayout;
	private ImageLoader imageLoader;
	Parser<?> parser;
	ProgressDialog mProgressDialog;
	private CustomProgressDialog mCustomProgressDialog;
	protected Handler mCancelHandler = new Handler();
	private LatLng mCurrentTripLatLngDestination, mCurrentTripLatLngSource;
	protected Handler mMapHandler1 = new Handler();;
	private ArrayList<Address> mNewLatLongSource, mNewLatLongDesination;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notifications_detail_ui);
		initiViewReference();
		this.imageLoader = imageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(getBaseContext()));
		if (getIntent().getExtras() != null)
		{
			mNotificationDataBundle = getIntent().getExtras();
			mNotificationId = mNotificationDataBundle.getString("NotificationId");
			mNotificationType = mNotificationDataBundle.getString("Type");
		}
		if (mNotificationType.equals("1"))
		{
			initiViewReferenceForSharedScreen();
			setDataForSharedScreen();
		}
		else
		{
			initiViewReferenceForDrawLine();
			setDataForDrawLineScreen();
		}

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

	private void initiViewReferenceForDrawLine()
	{
		mRowMapLinearLayout = (LinearLayout) findViewById(R.id.map_linear);
		mRowMapLinearLayout.setVisibility(View.VISIBLE);
	}

	private void checkgps()
	{
		gpsTracker = new GPSTracker(NotificationsDetail.this);

		if (gpsTracker.canGetLocation())
		{

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
			latlngSource = new LatLng(Double.parseDouble(Constant.mDrawSharedLineDataBean.getTripArray().get(0).getStartLatitude()), Double.parseDouble(Constant.mDrawSharedLineDataBean.getTripArray()
					.get(0).getStartLongitude()));

			latlngDestination = new LatLng(Double.parseDouble(Constant.mDrawSharedLineDataBean.getTripArray().get(0).getEndLatitude()), Double.parseDouble(Constant.mDrawSharedLineDataBean
					.getTripArray().get(0).getEndLongitude()));

			// create marker
			markerSource = new MarkerOptions().position(latlngSource).title(Constant.mDrawSharedLineDataBean.getTripArray().get(0).getStartPointName())
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin));
			markerDestin = new MarkerOptions().position(latlngDestination).title(Constant.mDrawSharedLineDataBean.getTripArray().get(0).getEndPointName())
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin));
			// adding marker
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlngSource, 15));
			mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

			mMap.addMarker(markerSource);
			mMap.addMarker(markerDestin);

			mProgressDialog = new ProgressDialog(NotificationsDetail.this);
			mProgressDialog.setTitle("MO-BIA");
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();

			new Thread(new Runnable()
			{

				@Override
				public void run()
				{
					final PolylineOptions rectLine = new PolylineOptions();

					ArrayList<LatLng> directionPoint = null;
					try
					{
						doc = gmapv.getDocument(latlngSource, latlngDestination, GMapV2Direction.MODE_DRIVING);
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

					mMapHandler.post(new Runnable()
					{

						@Override
						public void run()
						{
							mProgressDialog.dismiss();
							poly = mMap.addPolyline(rectLine);

							if (Constant.mDrawSharedLineDataBean.getTripArray().get(0).getmSndPtLat().length() > 0)
							{

								getAnotherCurrentMap();

							}
							else
							{
								if (Constant.mDrawSharedLineDataBean.getTripArray().get(0).getDrawLineArray().size() > 0)
								{

									for (int i = 0; i < Constant.mDrawSharedLineDataBean.getTripArray().get(0).getDrawLineArray().size(); i++)
									{

										mNewLatLngSource = new LatLng(Double.parseDouble(Constant.mDrawSharedLineDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawLineStartLatitude()),
												Double.parseDouble(Constant.mDrawSharedLineDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawLineStartLongitude()));

										mNewLatLngDestination = new LatLng(Double.parseDouble(Constant.mDrawSharedLineDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawEndLatitude()),
												Double.parseDouble(Constant.mDrawSharedLineDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawLineEndLongitude()));

										if (i == Constant.mDrawSharedLineDataBean.getTripArray().get(0).getDrawLineArray().size() - 1)
										{
											mNewMarkersource = new MarkerOptions()
													.position(mNewLatLngSource)
													.title(Constant.mDrawSharedLineDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawLineStartName())
													.snippet(
															"Category: " + Constant.mDrawSharedLineDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmCategory() + ",\t \n" + "Details: "
																	+ Constant.mDrawSharedLineDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawComment())
													.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_one));
											mNewMarkerDestination = new MarkerOptions()
													.position(mNewLatLngDestination)
													.title(Constant.mDrawSharedLineDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawLineEndName())
													.snippet(
															"Category: " + Constant.mDrawSharedLineDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmCategory() + ",\t \n" + "Details: "
																	+ Constant.mDrawSharedLineDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawComment())
													.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_one));
										}
										else
										{
											mNewMarkersource = new MarkerOptions()
													.position(mNewLatLngSource)
													.title(Constant.mDrawSharedLineDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawLineStartName())
													.snippet(
															"Category: " + Constant.mDrawSharedLineDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmCategory() + ",\t \n" + "Details: "
																	+ Constant.mDrawSharedLineDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawComment())
													.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_pin));
											mNewMarkerDestination = new MarkerOptions()
													.position(mNewLatLngDestination)
													.title(Constant.mDrawSharedLineDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawLineEndName())
													.snippet(
															"Category: " + Constant.mDrawSharedLineDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmCategory() + ",\t \n" + "Details: "
																	+ Constant.mDrawSharedLineDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawComment())
													.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_pin));

										}
										mMap.addMarker(mNewMarkerDestination);
										mMap.addMarker(mNewMarkersource);
										String mColor = Constant.mDrawSharedLineDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawColor();

										getNewDrawLine(mNewLatLngSource, mNewLatLngDestination, mColor);
									}

									try
									{
										if (Constant.mDrawSharedLineDataBean.getTripArray().get(0).getShareScreenArray().size() > 0)
										{
											for (int j = 0; j < Constant.mDrawSharedLineDataBean.getTripArray().get(0).getShareScreenArray().size(); j++)
											{
												putNewPointonMap(Constant.mDrawSharedLineDataBean.getTripArray().get(0).getShareScreenArray().get(j).getShareScreenId(),
														Double.parseDouble(Constant.mDrawSharedLineDataBean.getTripArray().get(0).getShareScreenArray().get(j).getShareScreenCurrentLatitude()),
														Double.parseDouble(Constant.mDrawSharedLineDataBean.getTripArray().get(0).getShareScreenArray().get(j).getShareScreenCurrentLongitude()));
											}

										}

									}
									catch (Exception e2)
									{
										e2.printStackTrace();
									}

								}
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

	private void getAnotherCurrentMap()
	{

		mCurrentTripLatLngDestination = new LatLng(Double.parseDouble(Constant.mDrawSharedLineDataBean.getTripArray().get(0).getmSndPtLat()), Double.parseDouble(Constant.mDrawSharedLineDataBean
				.getTripArray().get(0).getmSndPtLog()));

		mCurrentTripLatLngSource = new LatLng(Double.parseDouble(Constant.mDrawSharedLineDataBean.getTripArray().get(0).getEndLatitude()), Double.parseDouble(Constant.mDrawSharedLineDataBean
				.getTripArray().get(0).getEndLongitude()));

		/*mCurrentTripLatLngDestination = new LatLng(Double.parseDouble(Constant.mCurrentTripDataBean.getTripArray().get(0).getmSndPtLat()), Double.parseDouble(Constant.mCurrentTripDataBean
				.getTripArray().get(0).getmSndPtLog()));*/

		markerDestin = new MarkerOptions().position(mCurrentTripLatLngDestination).title(Constant.mDrawSharedLineDataBean.getTripArray().get(0).getmSndPtName())
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin));

		markerSource = new MarkerOptions().position(mCurrentTripLatLngSource).title(Constant.mDrawSharedLineDataBean.getTripArray().get(0).getEndPointName())
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

						if (Constant.mDrawSharedLineDataBean.getTripArray().get(0).getDrawLineArray().size() > 0)
						{

							for (int i = 0; i < Constant.mDrawSharedLineDataBean.getTripArray().get(0).getDrawLineArray().size(); i++)
							{

								mNewLatLngSource = new LatLng(Double.parseDouble(Constant.mDrawSharedLineDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawLineStartLatitude()), Double
										.parseDouble(Constant.mDrawSharedLineDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawLineStartLongitude()));

								mNewLatLngDestination = new LatLng(Double.parseDouble(Constant.mDrawSharedLineDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawEndLatitude()), Double
										.parseDouble(Constant.mDrawSharedLineDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawLineEndLongitude()));
								mNewLatLongSource = new ArrayList<Address>();
								mNewLatLongDesination = new ArrayList<Address>();

								mNewMarkersource = new MarkerOptions()
										.position(mNewLatLngSource)
										.title(Constant.mDrawSharedLineDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawLineStartName()/*mNewLatLongSource.get(0).getAddressLine(2) + mNewLatLongSource.get(0).getAddressLine(1)*/)
										.snippet(
												"Category: " + Constant.mDrawSharedLineDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmCategory() + ",\t \n" + "Details: "
														+ Constant.mDrawSharedLineDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawComment())
										.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_pin));
								mNewMarkerDestination = new MarkerOptions()
										.position(mNewLatLngDestination)
										.title(Constant.mDrawSharedLineDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawLineEndName())
										.snippet(
												"Category: " + Constant.mDrawSharedLineDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmCategory() + ",\t \n" + "Details: "
														+ Constant.mDrawSharedLineDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawComment())
										.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_pin));
								String mColor = Constant.mDrawSharedLineDataBean.getTripArray().get(0).getDrawLineArray().get(i).getmDrawColor();

								mMap.addMarker(mNewMarkersource);
								mMap.addMarker(mNewMarkerDestination);

								getNewDrawLine(mNewLatLngSource, mNewLatLngDestination, mColor);
							}

						}
						try
						{
							if (Constant.mDrawSharedLineDataBean.getTripArray().get(0).getShareScreenArray().size() > 0)
							{
								for (int j = 0; j < Constant.mDrawSharedLineDataBean.getTripArray().get(0).getShareScreenArray().size(); j++)
								{
									putNewPointonMap(Constant.mDrawSharedLineDataBean.getTripArray().get(0).getShareScreenArray().get(j).getShareScreenId(),
											Double.parseDouble(Constant.mDrawSharedLineDataBean.getTripArray().get(0).getShareScreenArray().get(j).getShareScreenCurrentLatitude()),
											Double.parseDouble(Constant.mDrawSharedLineDataBean.getTripArray().get(0).getShareScreenArray().get(j).getShareScreenCurrentLongitude()));
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

		mCustomProgressDialog = new CustomProgressDialog(NotificationsDetail.this, "");
		mCustomProgressDialog.setCancelable(false);
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
									Utility.showMsgDialog(NotificationsDetail.this, msg);
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
			final Dialog dialog = new Dialog(NotificationsDetail.this);
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
		imageLoader.displayImage(imageurl, mPhotoDisplayImageView, Utility.getFitXYDisplayOption(), Utility.getImageLoaderListner(NotificationsDetail.this));

		

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
					}
				});

			}
		}).start();

	}

	private void initiViewReferenceForSharedScreen()
	{

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.row_shared_screen_notification, null);

		mNotificationsDetailProfileImgView = (ImageView) view.findViewById(R.id.notificationsDetail_profile_placeholder);
		mNotificationsDetailAboutTripTextView = (TextView) view.findViewById(R.id.notificationsDetail_aboutTripTextView);
		mNotificationsDetailLocationNameTextView = (TextView) view.findViewById(R.id.notificationsDetail_LocationNameTextView);

		Utility.textViewFontRobotoLight(mNotificationsDetailLocationNameTextView, getAssets());
		Utility.textViewFontRobotoLight(mNotificationsDetailAboutTripTextView, getAssets());

		mInflaterRowLayout.addView(view);

	}

	private void setDataForDrawLineScreen()
	{
		mProgressDialog = new ProgressDialog(NotificationsDetail.this);
		mProgressDialog.setTitle("MO-BIA");
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();

		new Thread(new Runnable()
		{
			String response = "";

			@Override
			public void run()
			{

				JSONObject FinishTripJsonObject = new JSONObject();
				try
				{
					FinishTripJsonObject.put("UserId", Utility.getSharedPrefString(NotificationsDetail.this, "userId"));
					FinishTripJsonObject.put("NotificationId", mNotificationId);
					FinishTripJsonObject.put("Type", mNotificationType);

				}
				catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.e("params for first", FinishTripJsonObject.toString());
				String url = APIUtils.BASE_URL + APIUtils.DRAWLINEDETAIL;
				response = Utility.POST(url, FinishTripJsonObject.toString(), APIUtils.DRAWLINEDETAIL);
				Log.e("response", response.toString());

				mNotificationHandler.post(new Runnable()
				{

					private String msg;

					@Override
					public void run()
					{
						mProgressDialog.dismiss();

						if (response != null && response.length() > 0)
						{
							Log.e("msg", "is:" + response);

							try
							{
								JSONObject responseJsonObject = new JSONObject(response);
								if (responseJsonObject.optBoolean("Success"))
								{
									parser = new FinishTripParser();
									Constant.mDrawSharedLineDataBean = (FinishedTripDataBean) parser.parse(response);
									setUpMapIfNeeded();
								}
								else
								{
									msg = responseJsonObject.optString("Message");
									Utility.showMsgDialog(NotificationsDetail.this, msg);
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

	private void setDataForSharedScreen()
	{
		mProgressDialog = new ProgressDialog(NotificationsDetail.this);
		mProgressDialog.setTitle("MO-BIA");
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();

		new Thread(new Runnable()
		{
			String response = "";

			@Override
			public void run()
			{

				JSONObject FinishTripJsonObject = new JSONObject();
				try
				{
					FinishTripJsonObject.put("UserId", Utility.getSharedPrefString(NotificationsDetail.this, "userId"));
					FinishTripJsonObject.put("NotificationId", mNotificationId);
					FinishTripJsonObject.put("Type", mNotificationType);

				}
				catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.e("params for first", FinishTripJsonObject.toString());
				String url = APIUtils.BASE_URL + APIUtils.SHARESCREENDETAIL;
				response = Utility.POST(url, FinishTripJsonObject.toString(), APIUtils.SHARESCREENDETAIL);
				Log.e("response", response.toString());

				mNotificationHandler.post(new Runnable()
				{

					private String msg;

					@Override
					public void run()
					{
						mProgressDialog.dismiss();
						if (response != null && response.length() > 0)
						{
							Log.e("msg", "is:" + response);

							try
							{
								JSONObject responseJsonObject = new JSONObject(response);
								if (responseJsonObject.optBoolean("Success"))
								{
									JSONObject resultObject = responseJsonObject.getJSONObject("Result");

									mNotificationsDetailAboutTripTextView.setText(resultObject.optString("Comment"));
									mNotificationsDetailLocationNameTextView.setText(resultObject.optString("LocationName"));
									String imgUrl = resultObject.optString("ImageUrl");
									if (imgUrl.length() > 0 && !imgUrl.isEmpty())
									{
										imageLoader.displayImage(resultObject.optString("ImageUrl"), mNotificationsDetailProfileImgView);
									}

								}
								else
								{
									msg = responseJsonObject.optString("Message");
									Utility.showMsgDialog(NotificationsDetail.this, msg);
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
		Utility.textViewFontRobotoLight(mNotificationsDetailsLogoTextView, getAssets());
	}

	private void initiViewReference()
	{
		mNotificationsDetailsLogoTextView = (TextView) findViewById(R.id.notificationsDetails_ui_Logo_tv);
		mNotificationsDetailBackImgView = (ImageView) findViewById(R.id.notificDetails_ui_backImgview);
		mInflaterRowLayout = (LinearLayout) findViewById(R.id.layout_inflater_linear);
		mNotificationsDetailBackImgView.setOnClickListener(this);
		mAddMobsLinearLayout = (LinearLayout) findViewById(R.id.add_mobs_lnrlyt);
		LoadAdViewInEveryActivity.loadAdds(this, mAddMobsLinearLayout);
	}

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub

		if (v == mNotificationsDetailBackImgView)
		{
			if (getIntent().getExtras().containsKey("pushcall"))
			{
				Intent homeIntent = new Intent(NotificationsDetail.this, HomeActivity.class);
				startActivity(homeIntent);
				finish();

			}
			else
			{
				finish();
			}

		}

	}
}
