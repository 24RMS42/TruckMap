package com.ta.truckmap;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ta.inapp.util.IabHelper;
import com.ta.inapp.util.IabResult;
import com.ta.inapp.util.Inventory;
import com.ta.inapp.util.Purchase;
import com.ta.truckmap.asyntask.CreateEventAsynTask;
import com.ta.truckmap.asyntask.UpdateEventAsynTask;
import com.ta.truckmap.bean.EventBean;
import com.ta.truckmap.rest.api.APIUtils;
import com.ta.truckmap.util.Constant;
import com.ta.truckmap.util.ImageCaputureUtility;
import com.ta.truckmap.util.MyDatePickerDialog;
import com.ta.truckmap.util.SetVideoFromCamera;
import com.ta.truckmap.util.Utility;

public class AddNewEventActivity extends BaseActivity implements OnClickListener
{
	private Calendar /*cal*/now;
	//private int year, month, day;
	private boolean fired;
	private TextView mBackTextView, mStartDateTextView, mStartTimeTextView, mEndTimeTextView, mEndDateTextView;
	private EditText mNameEditText, mDetailsEditText, mWhereEditText;
	private ImageView mPhotoImageView, mVideoImageView, mDocImageView;
	private TextView mPayTextView;
	private TextView mPayText;
	private ArrayList<String> resultList = null;
	private int locationDetailCount = 0;
	private LinearLayout mDropDownLinearLayout;
	private String mLastSearchString = "";
	private boolean proceed = false;
	private String mImagePath = null, mSavePath;
	private String docFilePath = "";
	private ExifInterface exif;
	private final int REQUEST_CODE_DOC = 5;
	private ImageCaputureUtility imageCaptureRefrence = new ImageCaputureUtility();
	private SetVideoFromCamera mVidFromCamera;

	private String mAction = "add";
	private EventBean mUpdateEvent;

	Calendar mcurrentTime, newTime;
	private Calendar mEventDate, mEventStarttime, mEventEndTime;
	public String mToDate, mStartDateUtc = "", mEndDateUtc = "";
	TextWatcher textWatcher;
	private String mCurrentDate;
	//in app
	static final String TAG = "sampleBilling";
	//static final String SKU_GAS_ADD = "android.test.purchased";
	static final String SKU_GAS_ADD = "com.ta.mobia.truckapp.product.399";
	// com.ta.mobia.truckapp.product.299
	// The helper object
	IabHelper mHelper;
	String mType = "";

	static final int RC_REQUEST = 10001;
	private int isImageDelete = 0, isVideoDelete = 0, isDocDelete = 0;

	//

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.company_ads_layout);
		isImageDelete = 0;
		isVideoDelete = 0;
		isDocDelete = 0;
		initUI();
		setFont();
		Intent intent = getIntent();
		Bundle bundle = intent.getBundleExtra("data");
		if (bundle != null)
		{
			mAction = bundle.getString("action");
			if (mAction != null && mAction.equalsIgnoreCase("update"))
			{
				String eventid = bundle.getString("eventId");
				for (int i = 0; i < Constant.EVENTLIST.size(); i++)
				{
					if (eventid.equalsIgnoreCase(Constant.EVENTLIST.get(i).getEventId()))
					{
						mUpdateEvent = Constant.EVENTLIST.get(i);
						updateDetails();
					}
				}
			}
		}
		if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("type"))
		{
			mType = getIntent().getExtras().getString("type");

			if (mType.equalsIgnoreCase("2"))
			{
				mStartTimeTextView.setVisibility(View.GONE);
				mEndTimeTextView.setVisibility(View.GONE);
				mEndDateTextView.setVisibility(View.GONE);
				mStartDateTextView.setVisibility(View.GONE);
			}
			else
			{
				mStartTimeTextView.setVisibility(View.VISIBLE);
				mEndTimeTextView.setVisibility(View.VISIBLE);
				mEndDateTextView.setVisibility(View.VISIBLE);
				mStartDateTextView.setVisibility(View.VISIBLE);
			}

		}

		mWhereEditText.addTextChangedListener(textWatcher);

	}

	private void setFont()
	{
		Utility.textViewFontRobotoLight(mNameEditText, getAssets());
		Utility.textViewFontRobotoLight(mDetailsEditText, getAssets());
		Utility.textViewFontRobotoLight(mBackTextView, getAssets());
		Utility.textViewFontRobotoLight(mWhereEditText, getAssets());
		Utility.textViewFontRobotoLight(mStartDateTextView, getAssets());
		Utility.textViewFontRobotoLight(mEndDateTextView, getAssets());
		Utility.textViewFontRobotoLight(mStartTimeTextView, getAssets());
		Utility.textViewFontRobotoLight(mEndTimeTextView, getAssets());
		Utility.textViewFontRobotoLight(mPayTextView, getAssets());
		Utility.textViewFontRobotoLight(mPayText, getAssets());

	}

	private void initUI()
	{
		mPayText = (TextView) findViewById(R.id.company_ads_textview_value);
		mBackTextView = (TextView) findViewById(R.id.company_ads_back_textview);
		mNameEditText = (EditText) findViewById(R.id.company_ads_editText_name);
		mDetailsEditText = (EditText) findViewById(R.id.company_ads_editText_details);
		mWhereEditText = (EditText) findViewById(R.id.company_ads_where_edittext);
		mStartDateTextView = (TextView) findViewById(R.id.company_ads_srtdt_textview);
		mEndDateTextView = (TextView) findViewById(R.id.company_ads_enddt__textview);

		mStartTimeTextView = (TextView) findViewById(R.id.company_ads_textview_startTime);
		mEndTimeTextView = (TextView) findViewById(R.id.company_ads_textview_end_time);
		mPhotoImageView = (ImageView) findViewById(R.id.company_ads_image_imageview);
		mVideoImageView = (ImageView) findViewById(R.id.company_ads_video_imageview);
		mDocImageView = (ImageView) findViewById(R.id.company_ads_doc_imageview);
		mPayTextView = (TextView) findViewById(R.id.company_ads_textview_pay);
		mDropDownLinearLayout = (LinearLayout) findViewById(R.id.company_ads_dropdown_container_layout);

		mBackTextView.setOnClickListener(this);
		mStartDateTextView.setOnClickListener(this);
		mPhotoImageView.setOnClickListener(this);
		mVideoImageView.setOnClickListener(this);
		mDocImageView.setOnClickListener(this);
		mPayTextView.setOnClickListener(this);
		mStartTimeTextView.setOnClickListener(this);
		mEndTimeTextView.setOnClickListener(this);
		mEndDateTextView.setOnClickListener(this);

		mStartTimeTextView.setEnabled(false);
		mEndTimeTextView.setEnabled(false);

		textWatcher = new TextWatcher()
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
				//	mToDropDownLinearLayout.setVisibility(View.GONE);

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{
			}

			@Override
			public void afterTextChanged(Editable s)
			{
			}
		};

	}

	//Method for update existing event
	private void updateDetails()
	{
		findViewById(R.id.company_ads_textview_value).setVisibility(View.GONE);
		mPhotoImageView.setVisibility(View.VISIBLE);
		mPhotoImageView.setTag(0);
		mVideoImageView.setVisibility(View.VISIBLE);
		mVideoImageView.setTag(0);
		mDocImageView.setVisibility(View.VISIBLE);
		mDocImageView.setTag(0);

		mDropDownLinearLayout.setVisibility(View.GONE);
		mPayTextView.setText("Update");
		if (mUpdateEvent != null)
		{

			if (mUpdateEvent.getImagePathList().size() > 0)
			{
				String imageUrl = mUpdateEvent.getImagePathList().get(0);
				if (imageUrl != null && imageUrl.length() > 0)
				{
					mPhotoImageView.setImageDrawable(getResources().getDrawable(R.drawable.photo_icon_select));
					mPhotoImageView.setVisibility(View.VISIBLE);
					mPhotoImageView.setTag(1);
				}
			}
			if (mUpdateEvent.getVideoPathList().size() > 0)
			{

				String videoThumbnail = mUpdateEvent.getVideoPathList().get(0).get("thumbnail");
				if (videoThumbnail != null && videoThumbnail.length() > 0)
				{
					mVideoImageView.setImageDrawable(getResources().getDrawable(R.drawable.video_icon_select));
					mVideoImageView.setVisibility(View.VISIBLE);
					mVideoImageView.setTag(1);
				}
			}

			if (mUpdateEvent.getDocPathList().size() > 0)
			{
				String docUrl = mUpdateEvent.getDocPathList().get(0);
				if (docUrl != null && docUrl.length() > 0)
				{
					mDocImageView.setImageDrawable(getResources().getDrawable(R.drawable.check_select));
					mDocImageView.setVisibility(View.VISIBLE);
					mDocImageView.setTag(1);
				}
			}

			mNameEditText.setText(mUpdateEvent.getEventname());
			mDetailsEditText.setText(mUpdateEvent.getDetails());
			mWhereEditText.setText(mUpdateEvent.getPlace());

			if (mUpdateEvent.getmType().equalsIgnoreCase("2"))
			{

				mStartTimeTextView.setVisibility(View.GONE);
				mEndTimeTextView.setVisibility(View.GONE);
				mStartDateTextView.setVisibility(View.GONE);
				mEndDateTextView.setVisibility(View.GONE);
			}
			else
			{
				mStartDateTextView.setText(mUpdateEvent.getDate());
				mStartDateUtc = mUpdateEvent.getStarttime();
				mEndDateUtc = mUpdateEvent.getEndtime();
				mEndDateTextView.setText(mUpdateEvent.getmEnddate());
				mStartTimeTextView.setText(Utility.getLocalTimeFromUTC(mUpdateEvent.getStarttime()));
				mEndTimeTextView.setText(Utility.getLocalTimeFromUTC(mUpdateEvent.getEndtime()));
			}

		}

	}

	//	private String changeTimeToString(String timeInmili)
	//	{
	//
	//		try
	//		{
	//			long time = Long.parseLong(timeInmili);
	//			Calendar cal = Calendar.getInstance();
	//			cal.setTimeZone(TimeZone.getTimeZone("UTC"));
	//			cal.setTimeInMillis(time);
	//			String changedtime = cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE);
	//			if (cal.get(Calendar.MINUTE) == 0)
	//				changedtime = changedtime + " AM";
	//			else
	//				changedtime = changedtime + " PM";
	//
	//			return changedtime;
	//
	//		}
	//		catch (Exception e)
	//		{
	//			e.printStackTrace();
	//			return timeInmili;
	//		}
	//
	//	}

	@Override
	public void onClick(View v)
	{
		if (v == mEndDateTextView)
		{
			if (mCurrentDate != null && mCurrentDate.length() > 0)
			{
				showDatePickerToDate();
			}

		}
		else
			if (v == mStartDateTextView)
			{
				mEndDateTextView.setText("");
				showDatePicker();
				mStartTimeTextView.setEnabled(true);
				mEndTimeTextView.setEnabled(false);

				mStartTimeTextView.setText("");
				mEndTimeTextView.setText("");
				/*//showDatePicker();
				fired = false;
				now = Calendar.getInstance();
				showDialog(0);*/
			}
			else
				if (v == mStartTimeTextView)
				{

					mEndTimeTextView.setText("");
					mEndTimeTextView.setEnabled(true);
					/*fired = false;
					showDialog(1);*/

					mcurrentTime = Calendar.getInstance();
					final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
					final int minute = mcurrentTime.get(Calendar.MINUTE);
					TimePickerDialog mTimePicker;
					mTimePicker = new TimePickerDialog(AddNewEventActivity.this, new TimePickerDialog.OnTimeSetListener()
					{
						@Override
						public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute)
						{
							String date = mStartDateTextView.getText().toString();
							String selMinute = "";
							if (selectedMinute < 10)
								selMinute = "0" + selectedMinute;
							else
								selMinute = selectedMinute + "";
							String[] dateArray = date.split("/");
							String year = dateArray[2];
							String month = dateArray[1];
							String day = dateArray[0];

							//mcurrentTime.set(Calendar., selectedHour);
							mcurrentTime.set(Calendar.MONTH, Integer.parseInt(month) - 1);
							mcurrentTime.set(Calendar.YEAR, Integer.parseInt(year));
							mcurrentTime.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
							mcurrentTime.set(Calendar.HOUR_OF_DAY, selectedHour);
							mcurrentTime.set(Calendar.MINUTE, selectedMinute);
							mcurrentTime.set(Calendar.SECOND, 0);

							DateFormat localFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

							String localDateTime = localFormat.format(mcurrentTime.getTime());
							Log.e("localDateTime", localDateTime);
							mStartDateUtc = Utility.LocalToUTC(localDateTime);
							Log.e("mStartDateUtc", mStartDateUtc);

							if (mcurrentTime.getTimeInMillis() < System.currentTimeMillis())
							{
								Toast.makeText(AddNewEventActivity.this, "Selected time has already passed", Toast.LENGTH_SHORT).show();
								((TextView) mStartTimeTextView).setText(hour + ":" + minute);
							}
							else
							{
								((TextView) mStartTimeTextView).setText(selectedHour + ":" + selMinute);
							}
						}
					}, hour, minute, false);
					mTimePicker.setTitle("Select Time");
					mTimePicker.show();

				}
				else
					if (v == mEndTimeTextView)
					{
						/*fired = false;
						showDialog(2);*/

						newTime = Calendar.getInstance();
						final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
						final int minute = mcurrentTime.get(Calendar.MINUTE);
						Log.e("hour", "" + hour);
						Log.e("minute", "" + minute);

						TimePickerDialog mTimePicker;
						mTimePicker = new TimePickerDialog(AddNewEventActivity.this, new TimePickerDialog.OnTimeSetListener()
						{
							@Override
							public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute)
							{
								String date = mEndDateTextView.getText().toString();
								String selMinute = "";
								if (selectedMinute < 10)
									selMinute = "0" + selectedMinute;
								else
									selMinute = selectedMinute + "";
								String[] dateArray = date.split("/");
								String year = dateArray[2];
								String month = dateArray[1];
								String day = dateArray[0];

								//mcurrentTime.set(Calendar., selectedHour);
								newTime.set(Calendar.MONTH, Integer.parseInt(month) - 1);
								newTime.set(Calendar.YEAR, Integer.parseInt(year));
								newTime.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
								newTime.set(Calendar.HOUR_OF_DAY, selectedHour);
								newTime.set(Calendar.MINUTE, selectedMinute);
								newTime.set(Calendar.SECOND, 0);
								DateFormat localFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
								String localDateTime = localFormat.format(newTime.getTime());
								Log.e("localDateTime", localDateTime);
								mEndDateUtc = Utility.LocalToUTC(localDateTime);
								Log.e("mStartDateUtc", mStartDateUtc);
								if (newTime.getTimeInMillis() < mcurrentTime.getTimeInMillis())
								{
									Toast.makeText(AddNewEventActivity.this, "Selected time has already passed", Toast.LENGTH_SHORT).show();
									((TextView) mEndTimeTextView).setText("");
								}
								else
								{
									((TextView) mEndTimeTextView).setText(selectedHour + ":" + selMinute);
								}
							}

						}, hour, minute, false);
						mTimePicker.setTitle("Select Time");
						mTimePicker.show();

					}

					else
						if (v == mBackTextView)
						{

							//						Intent intent = new Intent();
							//						intent.putExtra("position", -1);
							//						setResult(0, intent);
							finish();
						}
						else
							if (v == mPhotoImageView)
							{
								if (mAction != null && mAction.equalsIgnoreCase("update"))
								{
									updateOrRemoveDialog(1);
								}
								else
								{
									getImageFromGalleryCamera();
								}
							}
							else
								if (v == mVideoImageView)
								{
									if (mAction != null && mAction.equalsIgnoreCase("update"))
									{
										updateOrRemoveDialog(2);
									}
									else
									{
										getVideo();
									}
									//
								}
								else
									if (v == mDocImageView)
									{

										if (mAction != null && mAction.equalsIgnoreCase("update"))
										{
											updateOrRemoveDialog(3);
										}
										else
										{
											getDocument();
										}
									}

									else
										if (v == mPayTextView)
										{
											if (inputFieldValidation())
											{
												if (mAction != null && mAction.equalsIgnoreCase("update"))
												{
													callService(false);
												}
												else
												{

													getInAppProcess();
												}

											}

										}

	}

	private void showDatePickerToDate()
	{
		String[] separated = mStartDateTextView.getText().toString().split("/");

		int mDay = Integer.parseInt(separated[0]);//c.get(Calendar.DAY_OF_MONTH);
		int mMonth = Integer.parseInt(separated[1]);//c.get(Calendar.MONTH);
		int mYear = Integer.parseInt(separated[2]);//c.get(Calendar.YEAR);

		System.out.println("the selected " + mDay);
		MyDatePickerDialog dialog = new MyDatePickerDialog(AddNewEventActivity.this, new mDateSetListener1(), mYear, mMonth - 1, mDay, false);
		dialog.show();

	}

	private void showDatePicker()
	{

		int mYear, mMonth, mDay;
		if (mStartDateTextView.getText().toString().length() > 0)
		{
			String[] separated = mStartDateTextView.getText().toString().split("/");

			mDay = Integer.parseInt(separated[0]);//c.get(Calendar.DAY_OF_MONTH);
			mMonth = Integer.parseInt(separated[1]);//c.get(Calendar.MONTH);
			mYear = Integer.parseInt(separated[2]);//c.get(Calendar.YEAR);
			System.out.println("the selected " + mDay);
		}
		else
		{
			Calendar c = Calendar.getInstance();
			mYear = c.get(Calendar.YEAR);
			mMonth = c.get(Calendar.MONTH) + 1;
			mDay = c.get(Calendar.DAY_OF_MONTH);
		}
		System.out.println("the selected " + mDay);
		MyDatePickerDialog dialog = new MyDatePickerDialog(AddNewEventActivity.this, new mDateSetListener(), mYear, mMonth - 1, mDay, false);
		dialog.show();

		/*
		Calendar c = Calendar.getInstance();
		int mDay = c.get(Calendar.DAY_OF_MONTH);
		int mMonth = c.get(Calendar.MONTH);
		int mYear = c.get(Calendar.YEAR);

		System.out.println("the selected " + mDay);
		MyDatePickerDialog dialog = new MyDatePickerDialog(AddNewEventActivity.this, new mDateSetListener1(), mYear, mMonth, mDay, false);
		dialog.show();*/

	}

	class mDateSetListener implements DatePickerDialog.OnDateSetListener
	{

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
		{
			// getCalender();
			int mYear = year;
			int mMonth = monthOfYear + 1;
			int mDay = dayOfMonth;
			String day = mDay + "", month = mMonth + "";
			if (mDay < 10)
			{
				day = "0" + mDay;
			}
			if (mMonth < 10)
			{
				month = "0" + mMonth;
			}
			mCurrentDate = day + "/" + month + "/" + year;
			mStartDateTextView.setText(/*
													* new StringBuilder() // Month
													* is 0 based so add 1
													* .append(mMonth +
													* 1).append("/"
													* ).append(mDay).append("/")
													* .append(mYear).append(" ")
													*/mCurrentDate);
			System.out.println(mStartDateTextView.getText().toString());
		}
	}

	class mDateSetListener1 implements DatePickerDialog.OnDateSetListener
	{

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
		{
			// getCalender();
			int mYear = year;
			int mMonth = monthOfYear + 1;
			int mDay = dayOfMonth;
			String day = mDay + "", month = mMonth + "";
			if (mDay < 10)
			{
				day = "0" + mDay;
			}
			if (mMonth < 10)
			{
				month = "0" + mMonth;
			}
			mToDate = day + "/" + month + "/" + mYear;
			mEndDateTextView.setText(/*
													* new StringBuilder() // Month
													* is 0 based so add 1
													* .append(mMonth +
													* 1).append("/"
													* ).append(mDay).append("/")
													* .append(mYear).append(" ")
													*/mToDate);
			System.out.println(mEndDateTextView.getText().toString());

		}
	}

	boolean inputFieldValidation()
	{

		if (mNameEditText.getText().toString().trim().length() <= 0)
		{
			Utility.showMsgDialog(this, getResources().getString(R.string.alt_event_name));
			return false;
		}
		else
			if (mDetailsEditText.getText().toString().trim().length() <= 0)
			{
				Utility.showMsgDialog(this, getResources().getString(R.string.alt_details));
				return false;
			}
			else
				if (mWhereEditText.getText().toString().trim().length() <= 0)
				{
					Utility.showMsgDialog(this, getResources().getString(R.string.alt_where));
					return false;
				}

				else

					if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("type"))
					{

						if (mType.equalsIgnoreCase("1"))
						{
							if (mStartDateTextView.getText().toString().trim().length() <= 0)
							{
								Utility.showMsgDialog(this, getResources().getString(R.string.alt_when));
								return false;
							}
							else

								if (mStartTimeTextView.getText().toString().trim().length() <= 0)
								{
									Utility.showMsgDialog(this, getResources().getString(R.string.alt_start_time));
									return false;
								}
								else
									if (mEndTimeTextView.getText().toString().trim().length() <= 0)
									{
										Utility.showMsgDialog(this, getResources().getString(R.string.alt_end_time));
										return false;
									}

						}
						else
						{
							return true;

						}

					}

		//		else if(mSavePath!=null && mSavePath.trim().length() <= 0){
		//			Utility.showMsgDialog(this,getResources().getString(R.string.alt_upload));
		//			return false;
		//		}

		return true;
	}

	void callService(boolean isCreateNew)
	{
		String eventname = mNameEditText.getText().toString().trim();
		String details = mDetailsEditText.getText().toString().trim();
		String where = mWhereEditText.getText().toString().trim();
		String when = mStartDateTextView.getText().toString().trim();
		String endDate = mEndDateTextView.getText().toString().trim();
		DateFormat Format = new SimpleDateFormat("MM/dd/yyyy");
		String finalString = "", endDateString = "";
		Date date, endDt;
		try
		{
			date = Format.parse(when);
			endDt = Format.parse(endDate);
			DateFormat localFormat = new SimpleDateFormat("MM/dd/yyyy");
			finalString = localFormat.format(date);
			endDateString = localFormat.format(endDt);
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String amt = "0.99";

		if (Utility.isNetworkAvailable(this))
		{

			if (isCreateNew)
			{
				new CreateEventAsynTask(this, mType, eventname, details, where, finalString, endDateString, mStartDateUtc, mEndDateUtc, amt, mImagePath, mVideoPath, docFilePath).execute();
			}
			else
			{
				new UpdateEventAsynTask(this, mUpdateEvent.getEventId(), eventname, details, where, finalString, endDateString, mStartDateUtc, mEndDateUtc, amt, mImagePath, mVideoPath, docFilePath,
						isImageDelete, isVideoDelete, isDocDelete).execute();

			}
		}

		else
		{
			Utility.showMsgDialog(this, this.getString(R.string.msg_netork_error));
		}

	}

	private void getDocument()
	{
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("application/msword,application/pdf");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		// Only the system receives the ACTION_OPEN_DOCUMENT, so no need to test.
		startActivityForResult(intent, REQUEST_CODE_DOC);
	}

	public void onUpdateResult(int position)
	{
		isImageDelete = 0;
		isVideoDelete = 0;
		isDocDelete = 0;
		Intent intent = new Intent(this, EventsActivity.class);
		//intent.putExtra("position", position);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		//setResult(0, intent);
		//setResult(RESULT_OK);
		finish();

	}

	/**
	 * Method to ba called from CreateEventAsynTask
	 * @param message
	 */
	public void onEventCreated(String message)
	{
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
		setResult(RESULT_OK);

		finish();
	}

	@Override
	public void onBackPressed()
	{

		Intent intent = new Intent();
		intent.putExtra("position", -1);
		setResult(0, intent);
		super.onBackPressed();

	}

	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id)
	{

		switch (id)
		{
			case 0:
				DatePickerDialog _date = new DatePickerDialog(this, datePickerListener, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));

				return _date;

			case 1:
				TimePickerDialog _starttime = new TimePickerDialog(this, startTimeListner, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), false);
				return _starttime;

			case 2:
				TimePickerDialog _endtime = new TimePickerDialog(this, endTimeListner, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), false);
				return _endtime;

		}
		return null;

	}

	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener()
	{

		public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay)
		{
			if (selectedYear < now.get(Calendar.YEAR) || selectedMonth < now.get(Calendar.MONTH) && selectedYear == now.get(Calendar.YEAR) || selectedMonth < now.get(Calendar.MONTH)
					&& selectedYear == now.get(Calendar.YEAR) && selectedMonth == now.get(Calendar.MONTH))
			{

				if (fired == true)
				{
					return;
				}
				else
				{
					fired = true;
					Utility.showMsgDialog(AddNewEventActivity.this, "Invalid  Date.");
					mStartDateTextView.setText("");
					view.invalidate();

				}

			}

			else
			{
				fired = true;
				mStartDateTextView.setText((selectedMonth + 1) + " / " + selectedDay + " / " + selectedYear);
				mEventDate = Calendar.getInstance();
				mEventDate.set(selectedYear, selectedMonth, selectedDay);
				mEventDate.setTimeZone(TimeZone.getTimeZone("UTC"));

				mStartTimeTextView.setEnabled(true);
				mEndTimeTextView.setEnabled(false);
				mStartTimeTextView.setText("");
				mEndTimeTextView.setText("");

			}

		}

	};

	private TimePickerDialog.OnTimeSetListener startTimeListner = new TimePickerDialog.OnTimeSetListener()
	{
		//boolean check=false;
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute)
		{
			if (!fired)
			{
				fired = true;
				return;
			}

			mEndTimeTextView.setText("");
			mEndTimeTextView.setEnabled(true);
			mEventStarttime = Calendar.getInstance();
			mEventStarttime.set(mEventDate.get(Calendar.YEAR), mEventDate.get(Calendar.MONTH), mEventDate.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
			mEventStarttime.setTimeZone(TimeZone.getTimeZone("UTC"));

			mStartTimeTextView.setText(updateTime(hourOfDay, minute));

			mStartTimeTextView.setTag(mEventStarttime.getTimeInMillis() + "");

			//mStartTimeTextView.setTag(hourOfDay+":"+minute);

			//			System.out.println("fired= "+fired+", hour= "+hourOfDay+", minuts="+minute);
			//			
			//			
			//			Calendar calendar = Calendar.getInstance();
			//			if(!fired)
			//			{
			//				fired=true;
			//				return;			
			//			}
			//			
			//			System.out.println("Check validation" + calendar.get(Calendar.HOUR_OF_DAY) +":"+ calendar.get(Calendar.MINUTE));
			//			if(hourOfDay > calendar.get(Calendar.HOUR_OF_DAY) ){
			//				mStartTimeTextView.setText(updateTime(hourOfDay, minute));
			//			}
			//			else if(minute > calendar.get(Calendar.MINUTE) ){
			//				mStartTimeTextView.setText(updateTime(hourOfDay, minute));
			//			}
			//			
			//			else
			//			{
			//				fired=true;
			//				Utility.showMsgDialog(AddNewEventActivity.this,"Invalid start time");
			//				mStartTimeTextView.setText("");
			//				
			//			}
		}

	};

	private TimePickerDialog.OnTimeSetListener endTimeListner = new TimePickerDialog.OnTimeSetListener()
	{

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute)
		{
			mEndTimeTextView.setText(updateTime(hourOfDay, minute));
			mEventEndTime = Calendar.getInstance();
			mEventEndTime.set(mEventDate.get(Calendar.YEAR), mEventDate.get(Calendar.MONTH), mEventDate.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
			mEventEndTime.setTimeZone(TimeZone.getTimeZone("UTC"));

			mEndTimeTextView.setTag(mEventEndTime.getTimeInMillis() + "");

			//			if(!fired)
			//			{
			//				fired=true;
			//				return;			
			//			}
			//			
			//			System.out.println("Check validation");
			//			if(calendar.get(Calendar.HOUR_OF_DAY)< hourOfDay){
			//				mEndTimeTextView.setText(updateTime(hourOfDay, minute));
			//			}
			//			else
			//			{
			//				fired=true;
			//				Utility.showMsgDialog(AddNewEventActivity.this,"Invalid end time");
			//				mEndTimeTextView.setText("");
			//				
			//			}

		}

	};
	private String mVideoPath;

	// Used to convert 24hr format to 12hr format with AM/PM values
	private String updateTime(int hours, int mins)
	{

		String timeSet = "";
		if (hours > 12)
		{
			hours -= 12;
			timeSet = "PM";
		}
		else
			if (hours == 0)
			{
				hours += 12;
				timeSet = "AM";
			}
			else
				if (hours == 12)
					timeSet = "PM";
				else
					timeSet = "AM";

		String minutes = "";
		if (mins < 10)
			minutes = "0" + mins;
		else
			minutes = String.valueOf(mins);

		// Append in a StringBuilder
		String aTime = new StringBuilder().append(hours).append(':').append(minutes).append(" ").append(timeSet).toString();

		return aTime;
	}

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

			//			if (mShowLocationDialog != null && mShowLocationDialog.isShowing())
			//			{
			//				mShowLocationDialog.dismiss();
			//			}
			if (resultList != null && resultList.size() > 0)
			{
				//mShowLocationDialog.dismiss();

				mDropDownLinearLayout.setVisibility(View.VISIBLE);
				mDropDownLinearLayout.removeAllViews();
				makeCustomReservationListView(mDropDownLinearLayout, resultList);

			}
			else
			{
				mDropDownLinearLayout.removeAllViews();
				mDropDownLinearLayout.setVisibility(View.GONE);

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
							mLastSearchString = resultList.get(position); //BUG
							Log.e("mLastSearchString.....", mLastSearchString);
							mWhereEditText.setText(mLastSearchString);
							mDropDownLinearLayout.setVisibility(View.GONE);
							proceed = true;
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

	private boolean shouldShowUpdateDialog(final int whichFile)
	{
		if (whichFile == 1)
		{
			if (mPhotoImageView.getTag().toString().equalsIgnoreCase("1"))
			{
				return true;

			}
		}
		else
			if (whichFile == 2)
			{
				if (mVideoImageView.getTag().toString().equalsIgnoreCase("1"))
				{
					return true;

				}

				//			if(mUpdateEvent.getVideoPathList().size()>0){
				//
				//				String videoThumbnail = mUpdateEvent.getVideoPathList().get(0).get("thumbnail");
				//				if (videoThumbnail != null && videoThumbnail.length() > 0)
				//				{
				//					return true;
				//				}
				//				return false;
				//			}
			}
			else
				if (whichFile == 3)
				{
					if (mDocImageView.getTag().toString().equalsIgnoreCase("1"))
					{
						return true;

					}
					//			if(mUpdateEvent.getDocPathList().size()>0){
					//				String docUrl = mUpdateEvent.getDocPathList().get(0);
					//				if (docUrl != null && docUrl.length() > 0)
					//				{
					//					return true;
					//				}
					//				return false;
					//			}
				}

		return false;

	}

	/**
	 * 
	 * @param whichFile
	 * 1 for Image, 2 for video, 3 for doc
	 */
	private void updateOrRemoveDialog(final int whichFile)
	{
		if (shouldShowUpdateDialog(whichFile))
		{
			AlertDialog.Builder adb = new AlertDialog.Builder(this);
			//adb.setTitle(R.string.app_name);
			adb.setMessage("What do you want ?");
			//adb.setIcon(R.drawable.cooltext1499324584);

			// adb.setIcon(android.R.drawable.ic_dialog_alert);

			adb.setPositiveButton("Update", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which)
				{
					switch (whichFile)
					{
						case 1:
							getImageFromGalleryCamera();
							break;
						case 2:
							getVideo();
							break;
						case 3:
							getDocument();
							break;

						default:
							break;
					}

				}
			});

			adb.setNegativeButton("Remove", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which)
				{
					switch (whichFile)
					{
						case 1:
							mImagePath = null;
							isImageDelete = 1;
							mPhotoImageView.setImageDrawable(getResources().getDrawable(R.drawable.photo_icon));
							mPhotoImageView.setTag(0);
							break;
						case 2:
							mVideoPath = null;
							isVideoDelete = 1;
							mVideoImageView.setImageDrawable(getResources().getDrawable(R.drawable.video_icon));
							mVideoImageView.setTag(0);
							break;
						case 3:
							docFilePath = null;
							isDocDelete = 1;
							mDocImageView.setImageDrawable(getResources().getDrawable(R.drawable.note_icon));
							mDocImageView.setTag(0);
							break;

						default:
							break;
					}

				}
			});
			adb.show();
		}
		else
		{
			switch (whichFile)
			{
				case 1:
					getImageFromGalleryCamera();
					break;
				case 2:
					getVideo();
					break;
				case 3:
					getDocument();
					break;
			}
		}
	}

	private void getImageFromGalleryCamera()
	{
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle(R.string.app_name);
		adb.setMessage("Choose Photo From");
		adb.setIcon(R.drawable.appicon);

		// adb.setIcon(android.R.drawable.ic_dialog_alert);

		adb.setPositiveButton(getResources().getString(R.string.lbl_gallery), new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{

				imageCaptureRefrence.selectFromGalley(AddNewEventActivity.this);
			}
		});

		adb.setNegativeButton(getResources().getString(R.string.lbl_camera), new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				imageCaptureRefrence.captureImage(AddNewEventActivity.this);

			}
		});
		adb.show();

	}

	private void getVideo()
	{

		if (mVidFromCamera == null)
			mVidFromCamera = new SetVideoFromCamera(AddNewEventActivity.this);
		mVidFromCamera.showVideoDialog();

	}

	// code for getting the imagepath of image taken via Camera to imageView
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
	/*public static String onVideoTaken(Intent data, Context context)
	{
		try
		{
			String[] projection = { MediaStore.Video.Media.DATA };
			Cursor cursor = context.getContentResolver().query(Constant.URI_VIDEO_CAPTURED, projection, null, null,
					null);
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
	}*/

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

	// code for getting the videopath of video taken from Gallery 
	/*public static String onVideoTakenFromgallery(Intent data, Context context)
	{
		try
		{
			Uri _uri = data.getData();
			if (_uri != null)
			{
				Cursor cursor = context.getContentResolver().query(
						_uri,
						new String[] { android.provider.MediaStore.Video.VideoColumns.DATA,
								MediaStore.Video.Media.MINI_THUMB_MAGIC }, null, null, null);
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
	}*/

	private String getFileNameByUri(Context context, Uri uri)
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

	public void setVideoPreview(String mVideoPath, Bitmap image)
	{
		this.mVideoPath = mVideoPath;
		mVideoImageView.setImageBitmap(image);
		mVideoImageView.setTag(1);
	}

	@Override
	protected void onActivityResult(int req, int result, Intent data)
	{
		// TODO Auto-generated method stub
		super.onActivityResult(req, result, data);

		if (result == RESULT_OK)
		{

			switch (req)
			{
				case Constant.REQUEST_CODE_IMAGE_CAPTURED:
					mImagePath = onPhotoTaken(data, AddNewEventActivity.this);

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
						mSavePath = Utility.getCacheFilePath(AddNewEventActivity.this);
						Log.e("path is ", mSavePath);

						mPhotoImageView.setImageBitmap(Utility.getCroppedBitmap(BitmapFactory.decodeFile(mImagePath), Integer.parseInt(exif.getAttribute(ExifInterface.TAG_ORIENTATION))));
						mPhotoImageView.setTag(1);

					}
					break;

				case Constant.REQUEST_CODE_IMAGE_GALLERY:
					mImagePath = onPhotoTakenFromgallery(data, AddNewEventActivity.this);
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
						mSavePath = Utility.getCacheFilePath(AddNewEventActivity.this);
						Log.e("path is gallery ", mSavePath);

						mPhotoImageView.setImageBitmap(Utility.getCroppedBitmap(BitmapFactory.decodeFile(mImagePath), Integer.parseInt(exif.getAttribute(ExifInterface.TAG_ORIENTATION))));
						mPhotoImageView.setTag(1);
					}
					break;

				case Constant.REQUEST_CODE_VIDEO_CAPTURED:
					mVidFromCamera.onVideoTakenResult(req, result, data);
					break;

				case Constant.REQUEST_CODE_VIDEO_GALLERY:
					mVidFromCamera.onVideoTakenResult(req, result, data);

					break;

				case REQUEST_CODE_DOC:
					Uri fileuri = data.getData();
					docFilePath = getFileNameByUri(this, fileuri);
					System.out.println(docFilePath);
					mDocImageView.setImageDrawable(getResources().getDrawable(R.drawable.check_select));
					mDocImageView.setTag(1);

					break;

				case RC_REQUEST:
					if (mHelper == null)
						return;

					// Pass on the activity result to the helper for handling
					if (!mHelper.handleActivityResult(req, result, data))
					{
						// not handled, so handle it ourselves (here's where you'd
						// perform any handling of activity results not related to in-app
						// billing...
						super.onActivityResult(req, result, data);
					}

					break;

				default:
					break;
			}

		}

	}

	/*@Override
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
	}*/

	private void getInAppProcess()
	{

		AlertDialog.Builder inAppDialogBuilder = new AlertDialog.Builder(this);
		inAppDialogBuilder.setTitle("MO-BIA");
		inAppDialogBuilder.setCancelable(false);
		inAppDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
		inAppDialogBuilder.setMessage("This post requires the payment of $ 1.99.Do you want to post?");
		inAppDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				Log.d(TAG, "Buy gas button clicked.");

				// setWaitScreen(true);
				Log.d(TAG, "Launching purchase flow for gas.");

				checkInAppVlaidation();

			}
		});

		inAppDialogBuilder.show();

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

			if (purchase.getSku().equals(SKU_GAS_ADD))
			{
				// bought 1/4 tank of gas. So consume it.
				Log.d(TAG, "Purchase is gas. Starting gas consumption.");
				mHelper.consumeAsync(purchase, mConsumeFinishedListener);

				//

			}

		}
	};

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

	private void saveData(String orderId)
	{
		callService(true);
		Log.e("inapp", "inapp");
	}

	void complain(String message)
	{
		/*
		 * Log.e(TAG, "**** PikMykid InApp Error: " + message); alert("Error: "
		 * + message);
		 */
		getInAppProcess();

	}

	/** Verifies the developer payload of a purchase. */
	boolean verifyDeveloperPayload(Purchase p)
	{
		String payload = p.getDeveloperPayload();

		return true;
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

			String payload = "";
			mHelper.launchPurchaseFlow(AddNewEventActivity.this, SKU_GAS_ADD, RC_REQUEST, mPurchaseFinishedListener, payload);

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
			/*Purchase gasPurchase = inventory.getPurchase(SKU_GAS_ADD);
			if (gasPurchase != null && verifyDeveloperPayload(gasPurchase))
			{
				Log.d(TAG, "We have gas. Consuming it.");
				mHelper.consumeAsync(inventory.getPurchase(SKU_GAS_ADD), mConsumeFinishedListener);

				return;
			}
			*/
			// updateUi();
			// setWaitScreen(false);
			Log.d(TAG, "Initial inventory query finished; enabling main UI.");
		}
	};

}