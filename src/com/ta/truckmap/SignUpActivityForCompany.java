package com.ta.truckmap;

import java.io.IOException;
import java.util.Calendar;

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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ta.truckmap.rest.api.APIUtils;
import com.ta.truckmap.util.Constant;
import com.ta.truckmap.util.CustomProgressDialog;
import com.ta.truckmap.util.ImageCaputureUtility;
import com.ta.truckmap.util.Utility;
import com.truckmap.parsers.LoginParser;
import com.truckmap.parsers.Parser;

public class SignUpActivityForCompany extends Activity implements OnClickListener
{
	private EditText mNameEditText, mEmailEditText, mPasswordEditText, mConfirmPasswordEditText, mMobileEditText, mTruckAddressEditText, mTruckCompanyNameEditText;
	private ImageView mBackImageView, mProfilePicImageView, mUploadImageView;
	public TextView mHeadingTextView;
	private Button mSignUpBtn;
	Handler mSignUpHandler = new Handler();
	private CustomProgressDialog mCustomProgressDialog;
	private ImageCaputureUtility imageCaptureRefrence = new ImageCaputureUtility();
	String mImagePath = null, mSavePath;
	boolean isCamera = true;
	protected Parser<?> parser;
	ExifInterface exif;
	private Calendar cal, now;
	private int year;
	private int month;
	private int day;
	boolean fired;
	private String mCompanyId = "";
	Context mContext;
	private DatePicker dpResult;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup_ui_for_company);
		mContext = this;
		initiViewReference();
		setFontFamily();

	}

	private void setFontFamily()
	{
		Utility.textViewFontRobotoLight(mNameEditText, getAssets());
		Utility.textViewFontRobotoLight(mEmailEditText, getAssets());
		Utility.textViewFontRobotoLight(mPasswordEditText, getAssets());
		Utility.textViewFontRobotoLight(mConfirmPasswordEditText, getAssets());
		Utility.textViewFontRobotoLight(mSignUpBtn, getAssets());
		Utility.textViewFontRobotoLight(mTruckCompanyNameEditText, getAssets());
		Utility.textViewFontRobotoLight(mTruckAddressEditText, getAssets());
		Utility.textViewFontRobotoLight(mMobileEditText, getAssets());
		Utility.textViewFontRobotoLight(mHeadingTextView, getAssets());

	}

	private void initiViewReference()
	{
		mHeadingTextView = (TextView) findViewById(R.id.heading_textview);
		mNameEditText = (EditText) findViewById(R.id.ed_name_SignupScreen);
		mEmailEditText = (EditText) findViewById(R.id.ed_email_SignupScreen);
		mPasswordEditText = (EditText) findViewById(R.id.ed_password_SignupScreen);
		mConfirmPasswordEditText = (EditText) findViewById(R.id.ed_confirmPassword_SignupScreen);
		mTruckCompanyNameEditText = (EditText) findViewById(R.id.company_name_edit_text);
		mTruckAddressEditText = (EditText) findViewById(R.id.address_edittext);
		mMobileEditText = (EditText) findViewById(R.id.mobile_edittext);
		mBackImageView = (ImageView) findViewById(R.id.signup_ui_backBtn);
		mProfilePicImageView = (ImageView) findViewById(R.id.signup_ui_profileImageView);
		mUploadImageView = (ImageView) findViewById(R.id.signup_ui_uploadImageView);
		mSignUpBtn = (Button) findViewById(R.id.btn_Signup_SignUpScreen);

		mNameEditText.setFilters(new InputFilter[] { filter });

		mSignUpBtn.setOnClickListener(this);
		mUploadImageView.setOnClickListener(this);
		mBackImageView.setOnClickListener(this);

		final Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);

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

	/*
	 * private void showDropDownDriverTypeList() { if (drivertype != null &&
	 * drivertype.length > 0) {
	 * 
	 * ArrayAdapter<String> adapter = new ArrayAdapter<String>(
	 * SignUpActivity.this, android.R.layout.select_dialog_item, drivertype);
	 * AlertDialog.Builder builder = new AlertDialog.Builder(
	 * SignUpActivity.this);
	 * 
	 * builder.setTitle("Select Driver Type"); builder.setAdapter(adapter, new
	 * DialogInterface.OnClickListener() {
	 * 
	 * public void onClick(DialogInterface dialog, int which) {
	 * 
	 * mDriverTypeTextView.setText(drivertype[which]);
	 * 
	 * } }); final AlertDialog dialog = builder.create();
	 * 
	 * dialog.show(); } else { Toast.makeText(SignUpActivity.this,
	 * "No Type available", Toast.LENGTH_SHORT).show(); }
	 * 
	 * }
	 */

	@Override
	public void onClick(View v)
	{
		if (v == mSignUpBtn)
		{
			if (getValidation())
				getSignUp();

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
		/*
		 * else if (v == mDobTextView) { showDatePicker(); }
		 */

	}

	@SuppressWarnings("deprecation")
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
			if (selectedYear > now.get(Calendar.YEAR) || selectedMonth > now.get(Calendar.MONTH) && selectedYear == now.get(Calendar.YEAR) || selectedMonth > now.get(Calendar.MONTH)
					&& year == now.get(Calendar.YEAR) && selectedMonth == now.get(Calendar.MONTH))
			{

				if (fired == true)
				{
					return;
				}
				else
				{
					fired = true;
					Utility.showMsgDialog(SignUpActivityForCompany.this, "Invalid Date Of Birth.");

				}

			}
			/*
			 * else { fired = true; mDobTextView.setText((selectedMonth + 1) +
			 * " / " + selectedDay + " / " + selectedYear); }
			 */

		}

	};

	private boolean getValidation()
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
								if (Utility.isFieldEmpty(mMobileEditText.getText().toString()))
								{
									Utility.showMsgDialog(this, getResources().getString(R.string.alt_type));

								}
								else
									if (mMobileEditText.getText().toString().length() > 10)
									{
										Utility.showMsgDialog(this, "Mobile Number not more than 10 digit!");
									}
									else
									{
										if (Utility.isFieldEmpty(mTruckCompanyNameEditText.getText().toString()))
										{
											Utility.showMsgDialog(this, getResources().getString(R.string.alt_company_name));
										}

										else
										{
											if (Utility.isFieldEmpty(mTruckAddressEditText.getText().toString()))
											{
												Utility.showMsgDialog(this, getResources().getString(R.string.alt_company));

											}
											else
											{
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
					Utility.showMsgDialog(SignUpActivityForCompany.this, getResources().getString(R.string.alt_invalidemail));
				}
			}
		}
		return false;
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

				imageCaptureRefrence.selectFromGalley(SignUpActivityForCompany.this);
			}
		});

		adb.setNegativeButton(getResources().getString(R.string.lbl_camera), new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				imageCaptureRefrence.captureImage(SignUpActivityForCompany.this);

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
					mImagePath = onPhotoTaken(data, SignUpActivityForCompany.this);

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
						mSavePath = Utility.getCacheFilePath(SignUpActivityForCompany.this);
						Log.e("path is ", mSavePath);

						mProfilePicImageView.setImageBitmap(getCroppedBitmap(BitmapFactory.decodeFile(mImagePath), Integer.parseInt(exif.getAttribute(ExifInterface.TAG_ORIENTATION))));

					}
					break;

				case Constant.REQUEST_CODE_IMAGE_GALLERY:
					mImagePath = onPhotoTakenFromgallery(data, SignUpActivityForCompany.this);
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
						mSavePath = Utility.getCacheFilePath(SignUpActivityForCompany.this);
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
		mCustomProgressDialog = new CustomProgressDialog(SignUpActivityForCompany.this, "");
		mCustomProgressDialog.setCancelable(false);
		new Thread(new Runnable()
		{

			String response = "";

			@Override
			public void run()
			{

				/*
				public string PhoneNo { get; set; }
				public string Address { get; set; }
				*/

				JSONObject signUpJsonObject = new JSONObject();
				try
				{
					signUpJsonObject.put("Name", mNameEditText.getText().toString().trim());
					signUpJsonObject.put("Email", mEmailEditText.getText().toString().trim());
					// signUpJsonObject.put("DateOfBirth",
					// mDobTextView.getText()
					// .toString().trim());
					signUpJsonObject.put("CompanyID", mCompanyId);
					signUpJsonObject.put("Password", mPasswordEditText.getText().toString().trim());
					signUpJsonObject.put("DeviceToken", SplashActivity.regId);
					signUpJsonObject.put("CompanyName", mTruckCompanyNameEditText.getText().toString().trim());
					signUpJsonObject.put("Address", mTruckAddressEditText.getText().toString().trim());
					signUpJsonObject.put("PhoneNo", mMobileEditText.getText().toString().trim());
					signUpJsonObject.put("Image", "");

				}
				catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.e("params for first", signUpJsonObject.toString());//http://10.11.5.147/api/WebService/CreateCompany

				String url = APIUtils.BASE_URL + "CreateCompany"/*"http://10.11.5.147/api/WebService/CreateCompany" APIUtils.BASE_URL + APIUtils.SIGNUP*/;
				response = Utility.sendJsonWithFile(url, mImagePath, signUpJsonObject.toString(), "CreateCompany");
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
									JSONObject resultJsonObject = responseJsonObject.getJSONObject("Result");
									Utility.setSharedPrefStringData(SignUpActivityForCompany.this, "day", "1");
									Utility.setSharedPrefStringData(SignUpActivityForCompany.this, "username", mEmailEditText.getText().toString().trim());
									Utility.setSharedPrefStringData(SignUpActivityForCompany.this, "password", mPasswordEditText.getText().toString().trim());
									Utility.setSharedPrefStringData(SignUpActivityForCompany.this, "userId", resultJsonObject.optString("Userid"));
									Utility.setSharedPrefStringData(SignUpActivityForCompany.this, "IsLogin", "true");
									Utility.setSharedPrefStringData(SignUpActivityForCompany.this, "usertype", "2");
									Intent homeIntent = new Intent(SignUpActivityForCompany.this, HomeActivity.class);
									homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									startActivity(homeIntent);
									SignUpActivityForCompany.this.finish();
								}
								else
								{
									msg = responseJsonObject.optString("Message");
									Utility.showMsgDialog(SignUpActivityForCompany.this, msg);
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
