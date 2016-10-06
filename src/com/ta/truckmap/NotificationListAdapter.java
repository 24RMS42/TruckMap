package com.ta.truckmap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.ta.truckmap.bean.EventBean;
import com.ta.truckmap.rest.api.APIUtils;
import com.ta.truckmap.util.Constant;
import com.ta.truckmap.util.CustomProgressDialog;
import com.ta.truckmap.util.Utility;

public class NotificationListAdapter extends BaseAdapter
{
	private ImageLoader imageLoader;
	private LayoutInflater inflater;
	private Context context;
	List<HashMap<String, String>> mNotificationListMaps;
	private CustomProgressDialog mCustomProgressDialog;
	protected Handler mNotificationHandler = new Handler();

	public NotificationListAdapter(Context context, List<HashMap<String, String>> mNotificationListMaps)
	{
		this.context = context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.imageLoader = imageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		this.mNotificationListMaps = mNotificationListMaps;
	}

	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return mNotificationListMaps.size();
	}

	@Override
	public Object getItem(int position)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View v = convertView;
		NotificationViewHolder mNotificationViewHolder = null;
		if (convertView == null)
		{
			v = inflater.inflate(R.layout.row_notification, null);
			mNotificationViewHolder = new NotificationViewHolder();
			mNotificationViewHolder.profileImage = (ImageView) v.findViewById(R.id.notification_ui_profile_placeholder);
			mNotificationViewHolder.userName = (TextView) v.findViewById(R.id.notification_ui_UserName);
			mNotificationViewHolder.comment = (TextView) v.findViewById(R.id.notification_ui_tv_aboutTrip);
			mNotificationViewHolder.catloc = (TextView) v.findViewById(R.id.notification_ui_tv_catloc);
			mNotificationViewHolder.type = (TextView) v.findViewById(R.id.notification_ui_tv_type);
			mNotificationViewHolder.categoryLocation = (TextView) v.findViewById(R.id.notification_ui_tv_category);
			mNotificationViewHolder.mNotificationRow = (RelativeLayout) v.findViewById(R.id.notification_ui_relative);

			v.setTag(mNotificationViewHolder);
		}
		else
		{
			mNotificationViewHolder = (NotificationViewHolder) v.getTag();
		}

		mNotificationViewHolder.userName.setText(mNotificationListMaps.get(position).get("UserName"));
		mNotificationViewHolder.comment.setText(mNotificationListMaps.get(position).get("Comment"));
		mNotificationViewHolder.catloc.setText(mNotificationListMaps.get(position).get("CatLoc"));

		if (mNotificationListMaps.get(position).get("Type").equalsIgnoreCase("1"))
		{
			mNotificationViewHolder.type.setText("Share Screen");
			mNotificationViewHolder.categoryLocation.setText("Location :");
		}
		else
			if (mNotificationListMaps.get(position).get("Type").equalsIgnoreCase("2"))
			{
				mNotificationViewHolder.type.setText("DrawLine Screen");
				mNotificationViewHolder.categoryLocation.setText("Category :");
			}
			else
			{
				mNotificationViewHolder.type.setText("Event: ");
				mNotificationViewHolder.categoryLocation.setText("Location: ");
			}
		mNotificationViewHolder.mNotificationRow.setTag(position);
		//		if (mNotificationListMaps.get(position).get("UserImageUrl").length() > 0)
		//			imageLoader.displayImage(mNotificationListMaps.get(position).get("UserImageUrl"), mNotificationViewHolder.profileImage);

		imageLoader.displayImage(mNotificationListMaps.get(position).get("UserImageUrl"), mNotificationViewHolder.profileImage, Utility.getFitXYDisplayOption(), null);

		mNotificationViewHolder.mNotificationRow.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

				if (mNotificationListMaps.get((Integer) v.getTag()).get("Type").equalsIgnoreCase("1"))
				{
					setDataForSharedScreen(mNotificationListMaps.get((Integer) v.getTag()).get("NotificationId"), mNotificationListMaps.get((Integer) v.getTag()).get("Type"));
				}
				else
					if (mNotificationListMaps.get((Integer) v.getTag()).get("Type").equalsIgnoreCase("2"))
					{
						Intent notificationDetailsIntent = new Intent(context.getApplicationContext(), NotificationsDetail.class);
						notificationDetailsIntent.putExtra("NotificationId", mNotificationListMaps.get((Integer) v.getTag()).get("NotificationId"));
						notificationDetailsIntent.putExtra("Type", mNotificationListMaps.get((Integer) v.getTag()).get("Type"));
						context.startActivity(notificationDetailsIntent);
					}
					else
					{

						setDataForEventDetails(mNotificationListMaps.get((Integer) v.getTag()).get("NotificationId"));
						/*Intent notificationDetailsIntent = new Intent(context.getApplicationContext(), EventsActivity.class);
						notificationDetailsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						context.startActivity(notificationDetailsIntent);*/
					}

			}
		});
		return v;
	}

	protected void setDataForEventDetails(final String id)
	{

		mCustomProgressDialog = new CustomProgressDialog(context, "");
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
					//FinishTripJsonObject.put("UserId", Utility.getSharedPrefString(context, "userId"));
					FinishTripJsonObject.put("id", id);

				}
				catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.e("params for first", FinishTripJsonObject.toString());
				String url = APIUtils.BASE_URL + APIUtils.GETEVENTDETAILSBYID;
				response = Utility.POST(url, FinishTripJsonObject.toString(), APIUtils.GETEVENTDETAILSBYID);
				Log.e("response", response.toString());

				mNotificationHandler.post(new Runnable()
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
									JSONArray result = responseJsonObject.getJSONArray("Result");

									parseResultJson(result);

									//showInfoDialog(resultObject);

									/*	mNotificationsDetailAboutTripTextView.setText(resultObject.optString("Comment"));
										mNotificationsDetailLocationNameTextView.setText(resultObject.optString("LocationName"));
										String imgUrl = resultObject.optString("ImageUrl");
										if (imgUrl.length() > 0 && !imgUrl.isEmpty())
											imageLoader.displayImage(resultObject.optString("ImageUrl"), mNotificationsDetailProfileImgView);*/

								}
								else
								{
									msg = responseJsonObject.optString("Message");
									Utility.showMsgDialog(context, msg);
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

	private void parseResultJson(JSONArray result)
	{
		Constant.EVENTLIST.clear();
		for (int i = 0; i < result.length(); i++)
		{
			try
			{
				//System.out.println("In parser");
				JSONObject eventjson = result.getJSONObject(i);
				EventBean event = new EventBean();
				event.setEventname(eventjson.getString("eventname"));
				event.setEventId(eventjson.getString("eventId"));

				//System.out.println("Eventid:- "+eventjson.getString("eventId"));

				event.setDetails(eventjson.getString("details"));
				event.setPlace(eventjson.getString("place"));
				event.setStarttime(eventjson.getString("starttime"));
				event.setEndtime(eventjson.getString("endtime"));
				event.setDate(eventjson.getString("date"));
				event.setmEnddate(eventjson.optString("enddate"));
				event.setUserId(eventjson.getString("userId"));
				event.setmType(eventjson.getString("type"));

				//System.out.println("date:- "+eventjson.getString("date"));

				JSONArray jsonArrayImage = eventjson.getJSONArray("image");
				for (int j = 0; j < jsonArrayImage.length(); j++)
				{
					event.addImagePathInList(jsonArrayImage.getString(j));
					//event.addImagePathInList("http://staging10.techaheadcorp.com/ecourier/Images/Profile/de6d4b0e7e1a47e083736b29f33115a9.jpg");
				}

				JSONArray jsonArrayVideo = eventjson.getJSONArray("video");
				//ArrayList<Map<String, String>> videoList= new ArrayList<Map<String,String>>();
				for (int j = 0; j < jsonArrayVideo.length(); j++)
				{
					JSONObject videoObject = jsonArrayVideo.getJSONObject(j);
					Map<String, String> map = new HashMap<String, String>();
					map.put("videofile", videoObject.getString("videoFileName"));
					map.put("thumbnail", videoObject.getString("thumbnailFileName"));
					//videoList.add(map);
					event.addVideoPathInList(map);
				}

				JSONArray jsonArrayDoc = eventjson.getJSONArray("doc");
				for (int j = 0; j < jsonArrayDoc.length(); j++)
				{
					event.addDocPathInList(jsonArrayDoc.getString(j));
				}
				Constant.EVENTLIST.add(event);

				Intent intent = new Intent(context, EventDetails.class);
				intent.putExtra("position", 0);
				context.startActivity(intent);

			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

		}

	}

	class NotificationViewHolder
	{
		ImageView profileImage;
		TextView userName, comment, catloc, type, categoryLocation;
		RelativeLayout mNotificationRow;
	}

	private void setDataForSharedScreen(final String id, final String type)
	{
		mCustomProgressDialog = new CustomProgressDialog(context, "");
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
					FinishTripJsonObject.put("UserId", Utility.getSharedPrefString(context, "userId"));
					FinishTripJsonObject.put("NotificationId", id);
					FinishTripJsonObject.put("Type", type);

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
						mCustomProgressDialog.dismissDialog();
						if (response != null && response.length() > 0)
						{
							Log.e("msg", "is:" + response);

							try
							{
								JSONObject responseJsonObject = new JSONObject(response);
								if (responseJsonObject.optBoolean("Success"))
								{
									JSONObject resultObject = responseJsonObject.getJSONObject("Result");

									showInfoDialog(resultObject);

									/*	mNotificationsDetailAboutTripTextView.setText(resultObject.optString("Comment"));
										mNotificationsDetailLocationNameTextView.setText(resultObject.optString("LocationName"));
										String imgUrl = resultObject.optString("ImageUrl");
										if (imgUrl.length() > 0 && !imgUrl.isEmpty())
											imageLoader.displayImage(resultObject.optString("ImageUrl"), mNotificationsDetailProfileImgView);*/

								}
								else
								{
									msg = responseJsonObject.optString("Message");
									Utility.showMsgDialog(context, msg);
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
			final Dialog dialog = new Dialog(context);
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

			Utility.textViewFontRobotoLight(mTextViewPlaceName, context.getAssets());
			Utility.textViewFontRobotoLight(mTextViewTitle, context.getAssets());
			Utility.textViewFontRobotoLight(mTextViewDescData, context.getAssets());
			Utility.textViewFontRobotoLight(mTextViewOk, context.getAssets());
			Utility.textViewFontRobotoLight((TextView) dialog.findViewById(R.id.dialog_shared_post_details_textview_name), context.getAssets());
			Utility.textViewFontRobotoLight((TextView) dialog.findViewById(R.id.dialog_shared_post_details_textview_description), context.getAssets());

			mImageViewPhoto = (ImageView) dialog.findViewById(R.id.dialog_shared_post_details_imageview_photo);
			mImageViewVideo = (ImageView) dialog.findViewById(R.id.dialog_shared_post_details_imageview_video);
			mImageviewClose = (ImageView) dialog.findViewById(R.id.dialog_shared_post_details_imageview_close_dialog);

			mTextViewPlaceName.setText(resultJsonObject.optString("LocationName") + "");
			mTextViewTitle.setText(resultJsonObject.optString("Title") + "");
			mTextViewDescData.setText(resultJsonObject.optString("Comment") + "");

			final String imageUrl = resultJsonObject.optString("ImageUrl");
			final String videoUrl = resultJsonObject.optString("VideoUrl");
			final String audioUrl = resultJsonObject.optString("AudioUrl");

			if (imageUrl != null && imageUrl.length() > 0)
			{
				mImageViewPhoto.setImageDrawable(context.getResources().getDrawable(R.drawable.photo_icon_select));
			}

			if ((videoUrl != null && videoUrl.length() > 0) || (audioUrl != null && audioUrl.length() > 0))
			{
				mImageViewVideo.setImageDrawable(context.getResources().getDrawable(R.drawable.video_icon_select));
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
						else
							Utility.showToastMessage(context, "No data found");

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

		final Dialog mDialogShow = new Dialog(context);
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
		imageLoader.displayImage(imageurl, mPhotoDisplayImageView, Utility.getFitXYDisplayOption(), Utility.getImageLoaderListner(context));

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

	void openVideo(String videourl)
	{
		try
		{
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
			Uri uri = Uri.parse(videourl);
			intent.setDataAndType(uri, "video/*");
			context.startActivity(intent);
		}
		catch (Exception e)
		{
			// TODO: handle exception
			Toast.makeText(context, "Error connecting", Toast.LENGTH_SHORT).show();
		}
	}

	void openAudio(String audiourl)
	{
		try
		{
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
			Uri uri = Uri.parse(audiourl);
			intent.setDataAndType(uri, "audio/*");
			context.startActivity(intent);
		}
		catch (Exception e)
		{
			// TODO: handle exception
			Toast.makeText(context, "Error connecting", Toast.LENGTH_SHORT).show();
		}
	}

}
