package com.ta.truckmap;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.ta.truckmap.bean.EventBean;
import com.ta.truckmap.util.Constant;
import com.ta.truckmap.util.Utility;

public class EventDetails extends BaseActivity implements OnClickListener
{

	private TextView mBackTextView, mEventNameTextView, mDetailsTextView, mPlaceTextView, mDateTextView, mStartTimeTextView;
	private ImageView /*mAddnewImageView*/mHomeImageView, mEditImageView, mPhotoImageView, mVideoImageView, mDocImageView;
	private EventBean event;
	private TextView mEventDetail, mDetailInfoText, mAtInfoText, mOnInfoText, mFromInfoText;

	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private Dialog mDialogShow;
	private ImageView mCrossPhotoImageView;
	private ImageView mPhotoDisplayImageView;
	final int REQUEST_CODE_UPDATE = 201;
	String mType = "";

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_detail_layout);
		initializeImageLoader();
		initUI();
		setFont();
		Intent intent = getIntent();
		int postion = intent.getIntExtra("position", -1);
		if (postion != -1)
		{
			event = Constant.EVENTLIST.get(postion);
			mType = event.getmType();

			showDetails();
		}

	}

	private void setFont()
	{
		Utility.textViewFontRobotoLight(mBackTextView, getAssets());
		Utility.textViewFontRobotoLight(mAtInfoText, getAssets());
		Utility.textViewFontRobotoLight(mEventNameTextView, getAssets());
		Utility.textViewFontRobotoLight(mEventDetail, getAssets());
		Utility.textViewFontRobotoLight(mDetailInfoText, getAssets());
		Utility.textViewFontRobotoLight(mDetailsTextView, getAssets());
		Utility.textViewFontRobotoLight(mPlaceTextView, getAssets());
		Utility.textViewFontRobotoLight(mOnInfoText, getAssets());
		Utility.textViewFontRobotoLight(mDateTextView, getAssets());
		Utility.textViewFontRobotoLight(mFromInfoText, getAssets());
		Utility.textViewFontRobotoLight(mStartTimeTextView, getAssets());

	}

	private void showDetails()
	{
		if (mType.equalsIgnoreCase("2"))
			mBackTextView.setText("Business Ad");
		else
			mBackTextView.setText("Event");
		try
		{
			if (event != null)
			{
				mEventNameTextView.setText(event.getEventname());
				mDetailsTextView.setText(event.getDetails());
				mPlaceTextView.setText(event.getPlace());

				if (event.getmType().equalsIgnoreCase("1"))
				{

					mDateTextView.setText(event.getDate() + " To " + event.getmEnddate());
					mStartTimeTextView.setText((Utility.getLocalTimeFromUTC(event.getStarttime())) + " to " + Utility.getLocalTimeFromUTC(event.getEndtime()));
				}
				else
				{

					((LinearLayout) findViewById(R.id.datelyt)).setVisibility(View.GONE);
					((LinearLayout) findViewById(R.id.timelyt)).setVisibility(View.GONE);
					/*	mDateTextView.setVisibility(View.GONE);
						mStartTimeTextView.setVisibility(View.GONE);
						mOnInfoText.setVisibility(View.GONE);
						mFromInfoText.setVisibility(View.GONE);*/

				}

				String a = Utility.getSharedPrefStringData(EventDetails.this, "userId");
				System.out.println(a);
				if (!event.getUserId().equals(Utility.getSharedPrefStringData(EventDetails.this, "userId")))
				{
					mEditImageView.setVisibility(View.GONE);
				}

				if (event.getImagePathList().size() > 0)
				{
					String imageUrl = event.getImagePathList().get(0);
					if (imageUrl != null && imageUrl.length() > 0)
					{
						imageLoader.displayImage(imageUrl, mHomeImageView, options);
						//imageLoader.displayImage(imageUrl, mPhotoImageView, options);
						mPhotoImageView.setImageDrawable(getResources().getDrawable(R.drawable.photo_icon_select));
					}
				}

				if (event.getVideoPathList().size() > 0)
				{
					String videoThumbnail = event.getVideoPathList().get(0).get("thumbnail");
					if (videoThumbnail != null && videoThumbnail.length() > 0)
					{
						//imageLoader.displayImage(videoThumbnail, mVideoImageView, options);
						mVideoImageView.setImageDrawable(getResources().getDrawable(R.drawable.video_icon_select));
					}
				}
				if (event.getDocPathList().size() > 0)
				{

					String docUrl = event.getDocPathList().get(0);
					if (docUrl != null && docUrl.length() > 0)
					{
						//imageLoader.displayImage(videoThumbnail, mVideoImageView, options);
						mDocImageView.setImageDrawable(getResources().getDrawable(R.drawable.check_select));
					}
				}

			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public void updateDetails(int postion)
	{

	}

	private void initUI()
	{
		mFromInfoText = (TextView) findViewById(R.id.from_info_text);
		mOnInfoText = (TextView) findViewById(R.id.on_info_text);
		mAtInfoText = (TextView) findViewById(R.id.at_info_text_view);
		mDetailInfoText = (TextView) findViewById(R.id.detail_text);
		mEventDetail = (TextView) findViewById(R.id.event_detail_text_view);
		mBackTextView = (TextView) findViewById(R.id.evetn_details_layout_back_textview);
		mEventNameTextView = (TextView) findViewById(R.id.evetn_details_layout_textview_eventname);
		mDetailsTextView = (TextView) findViewById(R.id.evetn_details_layout_textview_eventdetails);
		mPlaceTextView = (TextView) findViewById(R.id.evetn_details_layout_textview_place);
		mDateTextView = (TextView) findViewById(R.id.evetn_details_layout_textview_date);
		mStartTimeTextView = (TextView) findViewById(R.id.evetn_details_layout_textview_starttime);

		//mAddnewImageView = (ImageView) findViewById(R.id.evetn_details_layout_addnew_textview);
		mHomeImageView = (ImageView) findViewById(R.id.evetn_details_layout_imageview_homeimage);
		mEditImageView = (ImageView) findViewById(R.id.evetn_details_layout_imageview_edit);
		mPhotoImageView = (ImageView) findViewById(R.id.evetn_details_layout_imageview_photo);
		mVideoImageView = (ImageView) findViewById(R.id.evetn_details_layout_imageview_video);
		mDocImageView = (ImageView) findViewById(R.id.evetn_details_layout_imageview_doc);

		mBackTextView.setOnClickListener(this);
		//mAddnewImageView.setOnClickListener(this);
		mEditImageView.setOnClickListener(this);
		mPhotoImageView.setOnClickListener(this);
		mVideoImageView.setOnClickListener(this);
		mDocImageView.setOnClickListener(this);

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

	/*private String changeTimeToString1(String timeInmili)
	{

		try
		{
			long time = Long.parseLong(timeInmili);
			Calendar cal = Calendar.getInstance();
			cal.setTimeZone(TimeZone.getTimeZone("UTC"));
			cal.setTimeInMillis(time);
			String changedtime = cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE);
			if (cal.get(Calendar.MINUTE) == 0)
				changedtime = changedtime + " AM";
			else
				changedtime = changedtime + " PM";

			return changedtime;

		}
		catch (Exception e)
		{
			e.printStackTrace();
			return timeInmili;
		}

	}
	*/
	private void getPhotoDialog()
	{

		//Add New Trip Screen

		/**
		 * @author ravind maurya
		 *
		 */
		mDialogShow = new Dialog(this);
		mDialogShow.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mDialogShow.setContentView(R.layout.photo_display_dialog);
		mDialogShow.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		mDialogShow.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		mDialogShow.setCancelable(false);

		mCrossPhotoImageView = (ImageView) mDialogShow.findViewById(R.id.cross_imgvw);
		mPhotoDisplayImageView = (ImageView) mDialogShow.findViewById(R.id.main_imgvw);
		//		mPhotoDisplayImageView.setBackgroundDrawable(drawable);
		Log.e("url", event.getImagePathList().get(0));
		mDialogShow.show();
		imageLoader.displayImage(event.getImagePathList().get(0), mPhotoDisplayImageView, Utility.getFitXYDisplayOption(), Utility.getImageLoaderListner(EventDetails.this));

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

	@Override
	public void onClick(View v)
	{
		if (v == mBackTextView)
		{
			finish();
		}
		/*else
			if (v == mAddnewImageView)
			{
				Intent intent = new Intent(this, AddNewEventActivity.class);
				startActivity(intent);
			}*/
		else
			if (v == mEditImageView)
			{

				if (isValidForUpdate(event.getStarttime()))

				{
					Intent intent = new Intent(this, AddNewEventActivity.class);
					Bundle data = new Bundle();
					data.putString("action", "update");
					data.putString("eventId", event.getEventId());
					intent.putExtra("data", data);
					startActivityForResult(intent, REQUEST_CODE_UPDATE);

				}
				else

				{
					if (mType.equalsIgnoreCase("1"))
						Utility.showMsgDialog(this, "Can't update, event start time remain less than 6 hr.");
					else
						Utility.showMsgDialog(this, "Can't update, The editable time has been passed away");

				}

			}
			else
				if (v == mPhotoImageView)
				{
					if (event.getImagePathList().size() > 0 && event.getImagePathList().get(0).length() > 0)
					{
						getPhotoDialog();
					}
					else
					{
						Utility.showToastMessage(EventDetails.this, "No Image Found");
					}

				}
				else
					if (v == mVideoImageView)
					{

						if (event.getVideoPathList().size() > 0 && event.getVideoPathList().get(0).get("videofile").toString().length() > 0)
						{
							try
							{
								Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
								Uri uri = Uri.parse(event.getVideoPathList().get(0).get("videofile").toString());
								intent.setDataAndType(uri, "video/*");
								startActivity(intent);
							}
							catch (Exception e)
							{
								// TODO: handle exception
								Toast.makeText(EventDetails.this, "Error connecting", Toast.LENGTH_SHORT).show();
							}
						}
						else
						{
							Utility.showToastMessage(EventDetails.this, "No Video Found");
						}

					}
					else
						if (v == mDocImageView)
						{
							if (event.getDocPathList().size() > 0 && event.getDocPathList().get(0).length() > 0)
							{
								WebView mWebView = new WebView(EventDetails.this);
								mWebView.getSettings().setJavaScriptEnabled(true);
								mWebView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + event.getDocPathList().get(0));
								setContentView(mWebView);
							}
							else
							{
								Utility.showToastMessage(EventDetails.this, "No Pdf/Doc Found");
							}

						}

	}

	private boolean isValidForUpdate(String startTime)
	{
		//"starttime":"09/01/2014 13:02",

		DateFormat utcFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");
		utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date date;
		try
		{
			date = utcFormat.parse(startTime);
			System.out.println(date);

			Calendar startTimeCal = Calendar.getInstance();
			//startTimeCal.setTimeZone(TimeZone.getTimeZone("UTC"));
			startTimeCal.setTime(date);

			System.out.println(startTimeCal.getTime());

			Calendar currenttimeCal = Calendar.getInstance();
			currenttimeCal.setTimeZone(TimeZone.getTimeZone("UTC"));
			currenttimeCal.setTime(new Date());

			System.out.println(currenttimeCal.getTime());

			if (mType.equalsIgnoreCase("2"))
			{
				if (currenttimeCal.getTimeInMillis() - startTimeCal.getTimeInMillis() < (24 * 60 * 60 * 1000))
					return true;

			}
			else
			{
				if (startTimeCal.getTimeInMillis() - currenttimeCal.getTimeInMillis() > (6 * 60 * 60 * 1000))
				{
					return true;
				}

			}

		}

		catch (Exception e)
		{
			e.printStackTrace();
			//return false;
		}

		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null && requestCode == REQUEST_CODE_UPDATE)
		{

			int position = data.getIntExtra("position", -1);
			if (position != -1)
			{
				event = Constant.EVENTLIST.get(position);
				showDetails();
			}
		}
	}

}
