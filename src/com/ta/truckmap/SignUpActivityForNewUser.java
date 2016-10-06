package com.ta.truckmap;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ta.truckmap.rest.api.APIUtils;
import com.ta.truckmap.util.Constant;
import com.ta.truckmap.util.CustomProgressDialog;
import com.ta.truckmap.util.ImageCaputureUtility;
import com.ta.truckmap.util.Utility;
import com.truckmap.parsers.LoginParser;
import com.truckmap.parsers.Parser;

public class SignUpActivityForNewUser extends Activity implements android.view.View.OnClickListener
{
	private EditText mNameEditText, mEmailEditText, mPasswordEditText, mConfirmPasswordEditText, mTruckAddressEditText, mTxtVwHeight, mTxtVwWidth, mTxtVwlength;
	private ImageView mBackImageView, mProfilePicImageView, mUploadImageView;
	public TextView mHeadingTextView, mBirthDayTextView, mTruckCompanyNameTextView;
	private Button mSignUpBtn;
	Handler mSignUpHandler = new Handler();
	private Calendar cal, now;
	private CheckBox mProfnalDriverCheckBox;
	private CustomProgressDialog mCustomProgressDialog;
	private ImageCaputureUtility imageCaptureRefrence = new ImageCaputureUtility();
	String mImagePath = null, mSavePath;
	boolean isCamera = true;
	protected Parser<?> parser;
	private int year, month, day;
	ExifInterface exif;
	boolean fired;
	ArrayList<HashMap<String, String>> mCompanyList;
	ArrayList<String> mCompany;
	private String mCompanyId = "", mHeigth = "", mWidth = "", mLength = "", mDriverType = "";
	private CheckBox mProfesionalCheckBox;
	private LinearLayout mProfessionalLnrLayoutl;
	boolean checkEditpfle;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup_ui_for_new_user);
		initiViewReference();
		checkIntentData();
		setFont();

	}

	/**
	 * use for get intent data from setting screen for edit profile via vikash
	 */
	private void checkIntentData()
	{
		checkEditpfle = getIntent().hasExtra("from_setting");
		try
		{
			if (checkEditpfle)
			{
				//mEmailEditText.setVisibility(View.GONE);
				mPasswordEditText.setVisibility(View.GONE);
				mConfirmPasswordEditText.setVisibility(View.GONE);

				mHeadingTextView.setText("Edit Profile");
				mSignUpBtn.setText("Update");
				String name = Utility.getSharedPrefStringData(SignUpActivityForNewUser.this, "name");
				String birthday = Utility.getSharedPrefStringData(SignUpActivityForNewUser.this, "birthday");
				String company = Utility.getSharedPrefStringData(SignUpActivityForNewUser.this, "company");
				String address = Utility.getSharedPrefStringData(SignUpActivityForNewUser.this, "address");
				String image = Utility.getSharedPrefStringData(SignUpActivityForNewUser.this, "Image");
				/*String password = Utility.getSharedPrefStringData(SignUpActivityForNewUser.this, "password");*/
				String emailId = Utility.getSharedPrefStringData(SignUpActivityForNewUser.this, "username");
				mEmailEditText.setText(emailId);
				/*mConfirmPasswordEditText.setText(password);
				mPasswordEditText.setText(password);*/
				mNameEditText.setText(name);
				birthday = setdate(birthday);
				mBirthDayTextView.setText(birthday);
				mTruckCompanyNameTextView.setText(company);
				mTruckAddressEditText.setText(address);
				mEmailEditText.setEnabled(false);
				mTruckAddressEditText.setEnabled(false);
				mBirthDayTextView.setEnabled(false);
				mNameEditText.setEnabled(false);

				if (Utility.getSharedPrefStringData(SignUpActivityForNewUser.this, "driverrtype").equalsIgnoreCase("1"))
				{

					mProfesionalCheckBox.setChecked(true);
					mProfesionalCheckBox.setClickable(false);
					mProfessionalLnrLayoutl.setVisibility(View.GONE);

				}

				/*mPasswordEditText.setEnabled(false);
				mConfirmPasswordEditText.setEnabled(false);*/
				String orientation = Utility.getSharedPrefStringData(SignUpActivityForNewUser.this, "orientation");
				if (orientation != null && orientation.length() > 0)
				{
					int orientnForImage = Integer.valueOf(orientation);
					mProfilePicImageView.setImageBitmap(getCroppedBitmap(BitmapFactory.decodeFile(image), orientnForImage));
				}
				Utility.requestImage(SignUpActivityForNewUser.this, mProfilePicImageView, image);
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String setdate(String birthday)
	{
		try
		{
			if (birthday != null && birthday.length() > 0)
			{
				SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.US);
				Date date = format.parse(birthday);
				format = new SimpleDateFormat("MM/dd/yyyy");
				String dateString = format.format(date);
				return dateString;
			}
			else
			{
				return "";
			}

		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		return birthday;
	}

	private void setFont()
	{

		Utility.textViewFontRobotoLight(mNameEditText, getAssets());
		Utility.textViewFontRobotoLight(mEmailEditText, getAssets());
		Utility.textViewFontRobotoLight(mPasswordEditText, getAssets());
		Utility.textViewFontRobotoLight(mConfirmPasswordEditText, getAssets());
		Utility.textViewFontRobotoLight(mSignUpBtn, getAssets());
		Utility.textViewFontRobotoLight(mTruckCompanyNameTextView, getAssets());
		Utility.textViewFontRobotoLight(mTruckAddressEditText, getAssets());
		Utility.textViewFontRobotoLight(mHeadingTextView, getAssets());
		Utility.textViewFontRobotoLight(mBirthDayTextView, getAssets());

	}

	private void initiViewReference()
	{

		mHeadingTextView = (TextView) findViewById(R.id.signup_ui_new_user_heading_textview);
		mNameEditText = (EditText) findViewById(R.id.name_edit_text_signup);
		mEmailEditText = (EditText) findViewById(R.id.email_edittext_SignupScreen);
		mPasswordEditText = (EditText) findViewById(R.id.password_edittext_SignupScreen);
		mConfirmPasswordEditText = (EditText) findViewById(R.id.confirmPassword_edittext_SignupScreen);
		mTruckCompanyNameTextView = (TextView) findViewById(R.id.company_name_edittext_signup);
		mTruckAddressEditText = (EditText) findViewById(R.id.address_edittext_signup);
		mBirthDayTextView = (TextView) findViewById(R.id.birthday_edittext_signup);
		mBackImageView = (ImageView) findViewById(R.id.signup_ui_new_user_backBtn);
		mProfilePicImageView = (ImageView) findViewById(R.id.signup_ui_new_user_profileImageView);
		mUploadImageView = (ImageView) findViewById(R.id.signup_ui_new_user_uploadImageView);
		mSignUpBtn = (Button) findViewById(R.id.btn_Signup_SignUpScreen);
		mProfesionalCheckBox = (CheckBox) findViewById(R.id.signup_professional_driver_check_box);

		mTxtVwHeight = (EditText) findViewById(R.id.height_edittext_signup);
		mTxtVwlength = (EditText) findViewById(R.id.length_edittext_signup);
		mTxtVwWidth = (EditText) findViewById(R.id.width_edittext_signup);
		mProfessionalLnrLayoutl = (LinearLayout) findViewById(R.id.signup_prf_lnrlyt);

		mNameEditText.setFilters(new InputFilter[] { filter });
		mSignUpBtn.setOnClickListener(this);
		mBackImageView.setOnClickListener(this);
		mUploadImageView.setOnClickListener(this);
		mBirthDayTextView.setOnClickListener(this);
		mTruckCompanyNameTextView.setOnClickListener(this);

		final Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);

		if (mProfesionalCheckBox.isChecked())
		{
			mProfessionalLnrLayoutl.setVisibility(View.VISIBLE);
			mTruckCompanyNameTextView.setVisibility(View.VISIBLE);
		}

		else
		{
			mProfessionalLnrLayoutl.setVisibility(View.GONE);
			mTruckCompanyNameTextView.setVisibility(View.GONE);
		}

		mProfesionalCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				if (isChecked)
				{
					mProfessionalLnrLayoutl.setVisibility(View.VISIBLE);
					mTruckCompanyNameTextView.setVisibility(View.VISIBLE);

				}

				else
				{
					mProfessionalLnrLayoutl.setVisibility(View.GONE);
					mTruckCompanyNameTextView.setVisibility(View.GONE);
				}

			}
		});

	}

	InputFilter filter = new InputFilter()
	{
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
		{
			for (int i = start; i < end; i++)
			{
				if (Character.isDigit(source.charAt(i)))
				{
					return "";
				}
			}
			return null;
		}
	};

	private void showDatePicker()
	{

		cal = Calendar.getInstance();
		day = cal.get(Calendar.DAY_OF_MONTH);
		month = cal.get(Calendar.MONTH);
		year = cal.get(Calendar.YEAR);
		fired = false;
		showDialog(0);

	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		fired = false;
	}

	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id)
	{
		now = Calendar.getInstance();

		switch (id)
		{
			case 0:
				DatePickerDialog _date = new DatePickerDialog(this, datePickerListener, year, month, day);

				return _date;

		}
		return null;

	}

	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener()
	{

		public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay)
		{
			if (selectedYear < now.get(Calendar.YEAR) || selectedMonth < now.get(Calendar.MONTH) && selectedYear == now.get(Calendar.YEAR) || selectedMonth < now.get(Calendar.MONTH)
					&& year == now.get(Calendar.YEAR) && selectedMonth == now.get(Calendar.MONTH))
			{
				mBirthDayTextView.setText((selectedMonth + 1) + " / " + selectedDay + " / " + selectedYear);
			}
			/*
						if (fired == true)
						{
							return;
						}*/

			else
			{
				//	fired = true;
				if (fired)
				{
					Utility.showMsgDialog(SignUpActivityForNewUser.this, "Invalid Birthday Date.");
					fired = false;
				}
				else
				{
					fired = true;
				}

			}

			// }

			/*
			 * else { fired = true; mBirthDayTextView.setText((selectedMonth +
			 * 1) + " / " + selectedDay + " / " + selectedYear); }
			 */

		}

	};

	@Override
	public void onClick(View v)
	{

		if (v == mSignUpBtn)
		{
			if (getValidation())
			{
				getSignUp();
			}

		}
		else
			if (v == mUploadImageView)
			{

				getImageFromGalleryCamera();
			}
			else
				if (v == mBackImageView)
				{
					finish();
				}
				// else if (v == mDriverTypeTextView) {
				// showDropDownDriverTypeList();
				// }

				else
					if (v == mBirthDayTextView)
					{
						showDatePicker();
					}
					else
						if (v == mTruckCompanyNameTextView)
						{
							if (mCompanyList != null && mCompanyList.size() > 0)
								getcompanyDialog();
							else
								getCompanyList();
						}

	}

	private void getCompanyList()
	{
		mCustomProgressDialog = new CustomProgressDialog(SignUpActivityForNewUser.this, "");
		mCustomProgressDialog.setCancelable(false);
		new Thread(new Runnable()
		{

			String response = "";

			@Override
			public void run()
			{

				String url = APIUtils.BASE_URL + APIUtils.SIGNUP;
				response = Utility.POST(APIUtils.BASE_URL + "GetCompanyName", "", "GetCompanyName");
				Log.e("response", response.toString());
				mSignUpHandler.post(new Runnable()
				{

					private String msg;

					@Override
					public void run()
					{
						mCustomProgressDialog.dismissDialog();

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
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							try
							{
								//{"Success":true,"Result":[{"CompanyId":"1","CompanyName":"Girjesh"},    
								JSONObject responseJsonObject = new JSONObject(response);
								if (responseJsonObject.optBoolean("Success"))
								{
									JSONArray jsonArray = responseJsonObject.getJSONArray("Result");
									mCompanyList = new ArrayList<HashMap<String, String>>();
									mCompany = new ArrayList<String>();
									for (int i = 0; i < jsonArray.length(); i++)
									{
										JSONObject jsonObject = jsonArray.getJSONObject(i);
										HashMap<String, String> map = new HashMap<String, String>();
										map.put("id", jsonObject.getString("CompanyId"));
										map.put("CompanyName", jsonObject.getString("CompanyName"));
										mCompany.add(jsonObject.getString("CompanyName"));
										mCompanyList.add(map);
									}

									getcompanyDialog();

								}
								else
								{
									msg = responseJsonObject.optString("Message");
									Utility.showMsgDialog(SignUpActivityForNewUser.this, msg);
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

	private void getcompanyDialog()
	{
		if (mCompany != null && mCompany.size() > 0)
		{
			int poss = Utility.showCustomPopupList(SignUpActivityForNewUser.this, mTruckCompanyNameTextView, mCompany);
			mCompanyId = mCompanyList.get(poss).get("id");
		}
		else
			Utility.showToastMessage(SignUpActivityForNewUser.this, getResources().getString(R.string.alt_no_rcd));

	}

	private boolean getValidation()
	{
		if (checkEditpfle)
		{
			if (Utility.isFieldEmpty(mTruckCompanyNameTextView.getText().toString()))
			{
				Utility.showMsgDialog(this, getResources().getString(R.string.alt_company_name));
			}

			else
			{
				if (mProfessionalLnrLayoutl.getVisibility() == View.VISIBLE)
				{
					if (Utility.isFieldEmpty(mTxtVwHeight.getText().toString()))
					{
						Utility.showMsgDialog(this, getResources().getString(R.string.alt_heigth));
					}
					else
					{
						if (Utility.isFieldEmpty(mTxtVwWidth.getText().toString()))
						{
							Utility.showMsgDialog(this, getResources().getString(R.string.alt_wigth));
						}
						else
						{
							if (Utility.isFieldEmpty(mTxtVwlength.getText().toString()))
							{
								Utility.showMsgDialog(this, getResources().getString(R.string.alt_lenght));
							}
							else
								return true;
						}
					}

				}
				else
				{
					if (Utility.isFieldEmpty(mTruckAddressEditText.getText().toString()))
					{
						Utility.showMsgDialog(this, getResources().getString(R.string.alt_company));

					}
					else
						return true;
				}

			}

			return false;

		}
		else
		{

			if (Utility.isFieldEmpty(mNameEditText.getText().toString()))
			{
				Utility.showMsgDialog(this, getResources().getString(R.string.alt_name));
			}
			else
			{
				if (Utility.isFieldEmpty(mEmailEditText.getText().toString()))
				{
					Utility.showMsgDialog(this, getResources().getString(R.string.alt_email));
				}
				else
				{
					if (Utility.isEmailValid(mEmailEditText.getText().toString()))
					{

						if (Utility.isFieldEmpty(mPasswordEditText.getText().toString()))
						{
							Utility.showMsgDialog(this, getResources().getString(R.string.alt_password));
						}
						else
						{
							if (Utility.isFieldEmpty(mConfirmPasswordEditText.getText().toString()))
							{
								Utility.showMsgDialog(this, getResources().getString(R.string.alt_retype));
							}
							else
							{
								if (mConfirmPasswordEditText.getText().toString().compareTo(mPasswordEditText.getText().toString()) == 0)
								{
									if (Utility.isFieldEmpty(mBirthDayTextView.getText().toString()))
									{
										Utility.showMsgDialog(this, getResources().getString(R.string.birthday_type));

									}
									else
									{
										if (Utility.isFieldEmpty(mTruckCompanyNameTextView.getText().toString()) && mProfesionalCheckBox.isChecked())
										{
											Utility.showMsgDialog(this, getResources().getString(R.string.alt_company_name));
										}

										else
										{
											if (mProfessionalLnrLayoutl.getVisibility() == View.VISIBLE)
											{
												if (Utility.isFieldEmpty(mTxtVwHeight.getText().toString()))
												{
													Utility.showMsgDialog(this, getResources().getString(R.string.alt_heigth));
												}
												else
												{
													if (Utility.isFieldEmpty(mTxtVwWidth.getText().toString()))
													{
														Utility.showMsgDialog(this, getResources().getString(R.string.alt_wigth));
													}
													else
													{
														if (Utility.isFieldEmpty(mTxtVwlength.getText().toString()))
														{
															Utility.showMsgDialog(this, getResources().getString(R.string.alt_lenght));
														}
														else
															return true;
													}
												}

											}
											else
											{
												if (Utility.isFieldEmpty(mTruckAddressEditText.getText().toString()))
												{
													Utility.showMsgDialog(this, getResources().getString(R.string.alt_company));

												}
												else
													return true;
											}

										}

									}
								}
								else
								{
									Utility.showMsgDialog(this, getResources().getString(R.string.alt_pwdmatch));
								}
							}
						}
					}
					else
					{
						Utility.showMsgDialog(this, getResources().getString(R.string.alt_invalidemail));
					}
				}
			}
			return false;
		}

	}

	private void getImageFromGalleryCamera()
	{
		AlertDialog.Builder adb = new AlertDialog.Builder(this);

		adb.setTitle("Choose Photo From");

		// adb.setIcon(android.R.drawable.ic_dialog_alert);

		adb.setPositiveButton(getResources().getString(R.string.lbl_gallery), new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{

				imageCaptureRefrence.selectFromGalley(SignUpActivityForNewUser.this);
			}
		});

		adb.setNegativeButton(getResources().getString(R.string.lbl_camera), new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				imageCaptureRefrence.captureImage(SignUpActivityForNewUser.this);

			}
		});
		adb.show();

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
					mImagePath = onPhotoTaken(data, SignUpActivityForNewUser.this);

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
						mSavePath = Utility.getCacheFilePath(SignUpActivityForNewUser.this);
						Log.e("path is ", mSavePath);

						mProfilePicImageView.setImageBitmap(getCroppedBitmap(BitmapFactory.decodeFile(mImagePath), Integer.parseInt(exif.getAttribute(ExifInterface.TAG_ORIENTATION))));
						String orientationInString = String.valueOf(Integer.parseInt(exif.getAttribute(ExifInterface.TAG_ORIENTATION)));
						Utility.setSharedPrefAddressStringData(SignUpActivityForNewUser.this, "orientation", orientationInString);

					}
					break;

				case Constant.REQUEST_CODE_IMAGE_GALLERY:
					mImagePath = onPhotoTakenFromgallery(data, SignUpActivityForNewUser.this);
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
						mSavePath = Utility.getCacheFilePath(SignUpActivityForNewUser.this);
						Log.e("path is gallery ", mSavePath);

						mProfilePicImageView.setImageBitmap(getCroppedBitmap(BitmapFactory.decodeFile(mImagePath), Integer.parseInt(exif.getAttribute(ExifInterface.TAG_ORIENTATION))));
					}
					break;
				default:
					break;
			}

		}

	}

	public Bitmap getCroppedBitmap(Bitmap bitmapOrg, int exifOrientation)
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

	private void getSignUp()
	{

		if (mProfessionalLnrLayoutl.getVisibility() == View.VISIBLE)
		{

			mHeigth = (mTxtVwHeight.getText().toString().length() > 0) ? mTxtVwHeight.getText().toString() : "";
			mLength = (mTxtVwlength.getText().toString().length() > 0) ? mTxtVwlength.getText().toString() : "";
			mWidth = (mTxtVwWidth.getText().toString().length() > 0) ? mTxtVwWidth.getText().toString() : "";
		}
		else
		{
			mHeigth = "";
			mLength = "";
			mWidth = "";
		}

		mCustomProgressDialog = new CustomProgressDialog(SignUpActivityForNewUser.this, "");
		mCustomProgressDialog.setCancelable(false);
		new Thread(new Runnable()
		{

			String response = "";

			@Override
			public void run()
			{

				/*
				 * { "address": "sample string 9" "deviceToken":
				 * "sample string 3" }
				 */

				JSONObject signUpJsonObject = new JSONObject();
				try
				{
					if (checkEditpfle)
					{
						signUpJsonObject.put("Userid", Utility.getSharedPrefString(SignUpActivityForNewUser.this, "userId"));
					}

					if (Utility.getSharedPrefStringData(SignUpActivityForNewUser.this, "driverrtype").equalsIgnoreCase("1"))
					{

						signUpJsonObject.put("TruckHeight", Utility.getSharedPrefStringData(SignUpActivityForNewUser.this, "height"));
						signUpJsonObject.put("TruckWidth", Utility.getSharedPrefStringData(SignUpActivityForNewUser.this, "width"));
						signUpJsonObject.put("TruckLength", Utility.getSharedPrefStringData(SignUpActivityForNewUser.this, "length"));
					}
					else
					{
						signUpJsonObject.put("TruckHeight", mHeigth);
						signUpJsonObject.put("TruckWidth", mWidth);
						signUpJsonObject.put("TruckLength", mLength);
					}

					mDriverType = (mProfesionalCheckBox.isChecked()) ? "1" : "2";
					signUpJsonObject.put("Name", mNameEditText.getText().toString().trim());
					signUpJsonObject.put("Email", mEmailEditText.getText().toString().trim());
					signUpJsonObject.put("DateOfBirth", mBirthDayTextView.getText().toString().trim());
					signUpJsonObject.put("Password", mPasswordEditText.getText().toString().trim());
					signUpJsonObject.put("DeviceToken", SplashActivity.regId);
					signUpJsonObject.put("CompanyID", mCompanyId);
					signUpJsonObject.put("Address", mTruckAddressEditText.getText().toString().trim());
					signUpJsonObject.put("DriverType", mDriverType);
					signUpJsonObject.put("Image", "");

				}
				catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.e("params for first", signUpJsonObject.toString());
				String url = APIUtils.BASE_URL + APIUtils.SIGNUP;
				response = Utility.sendJsonWithFile(APIUtils.BASE_URL + APIUtils.SIGNUP, mImagePath, signUpJsonObject.toString(), APIUtils.SIGNUP);
				Log.e("response", response.toString());
				mSignUpHandler.post(new Runnable()
				{

					private String msg;

					@Override
					public void run()
					{
						mCustomProgressDialog.dismissDialog();

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
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							try
							{
								JSONObject responseJsonObject = new JSONObject(response);
								if (responseJsonObject.optBoolean("Success"))
								{
									JSONObject jObject = responseJsonObject.getJSONObject("Result");

									if (!checkEditpfle)
									{
										Utility.setSharedPrefStringData(SignUpActivityForNewUser.this, "username", mEmailEditText.getText().toString().trim());
										Utility.setSharedPrefStringData(SignUpActivityForNewUser.this, "password", mPasswordEditText.getText().toString().trim());
										Utility.setSharedPrefStringData(SignUpActivityForNewUser.this, "Image", jObject.getString("ImageUrl"));
										Utility.setSharedPrefStringData(SignUpActivityForNewUser.this, "name", mNameEditText.getText().toString().trim());
										Utility.setSharedPrefStringData(SignUpActivityForNewUser.this, "birthday", mBirthDayTextView.getText().toString().trim());
										Utility.setSharedPrefStringData(SignUpActivityForNewUser.this, "company", mTruckCompanyNameTextView.getText().toString().trim());
										Utility.setSharedPrefStringData(SignUpActivityForNewUser.this, "address", mTruckAddressEditText.getText().toString().trim());
										Utility.setSharedPrefStringData(SignUpActivityForNewUser.this, "usertype", "1");
										Utility.setSharedPrefStringData(SignUpActivityForNewUser.this, "compantId", jObject.optString("CompanyID"));
									}
									else
									{
										Utility.setSharedPrefStringData(SignUpActivityForNewUser.this, "compantId", jObject.optString("CompanyID"));
										Utility.setSharedPrefStringData(SignUpActivityForNewUser.this, "company", mTruckCompanyNameTextView.getText().toString().trim());
										Utility.setSharedPrefStringData(SignUpActivityForNewUser.this, "Image", jObject.getString("ImageUrl"));
									}

									Utility.setSharedPrefStringData(SignUpActivityForNewUser.this, "driverrtype", mDriverType);

									Utility.setSharedPrefStringData(SignUpActivityForNewUser.this, "userId", jObject.optString("Userid"));

									if (mDriverType.equalsIgnoreCase("1"))
									{
										Utility.setSharedPrefStringData(SignUpActivityForNewUser.this, "height", jObject.optString("TruckHeight"));
										Utility.setSharedPrefStringData(SignUpActivityForNewUser.this, "width", jObject.optString("TruckWidth"));
										Utility.setSharedPrefStringData(SignUpActivityForNewUser.this, "length", jObject.optString("TruckLength"));
									}

									Utility.setSharedPrefStringData(SignUpActivityForNewUser.this, "IsLogin", "true");

									if (checkEditpfle)
									{
										msg = responseJsonObject.optString("Message");
										Utility.showToastMessage(SignUpActivityForNewUser.this, msg);
									}
									else
									{
										Intent homeIntent = new Intent(SignUpActivityForNewUser.this, HomeActivity.class);
										homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
										startActivity(homeIntent);
									}

									SignUpActivityForNewUser.this.finish();
								}
								else
								{
									msg = responseJsonObject.optString("Message");
									Utility.showMsgDialog(SignUpActivityForNewUser.this, msg);
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
