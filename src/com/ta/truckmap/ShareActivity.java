package com.ta.truckmap;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ta.truckmap.R.id;
import com.ta.truckmap.gpstracking.GPSTracker;
import com.ta.truckmap.rest.api.APIUtils;
import com.ta.truckmap.util.Constant;
import com.ta.truckmap.util.ImageCaputureUtility;
import com.ta.truckmap.util.LoadAdViewInEveryActivity;
import com.ta.truckmap.util.Utility;

/**
 * @author nipun
 * 
 * 
 */
public class ShareActivity extends BaseActivity implements OnClickListener
{
	ImageView mShareBackImgView, share_ui_uploadImageView, share_ui_shareImageView;
	//CustomImageView share_ui_shareImageView;
	EditText share_ui_ed_comment;
	TextView share_ui_tv_comment, share_ui_tv_logo;
	Button share_ui_submitBtn;

	private final int REQ_CAMERA_GALLERY = 0, REQ_CANCEL = 1008;
	private ImageCaputureUtility imageCaptureRefrence = new ImageCaputureUtility();
	public static String location_string;
	String mImagePath = null, mSavePath;

	Handler mShareHandler = new Handler();
	ProgressDialog shareProgressDialog;
	LinearLayout mAddMobsLinearLayout;
	GPSTracker gpsTracker;
	Location getCurrentLocation;
	ExifInterface exif;
	Handler mImageSetUpHandler = new Handler();
	Options option = new Options();

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_ui);
		initiViewReference();
		setFontFamily();
		gpsTracker = new GPSTracker(ShareActivity.this);

	}

	private void setFontFamily()
	{
		Utility.textViewFontRobotoLight(share_ui_submitBtn, getAssets());
		Utility.textViewFontRobotoLight(share_ui_ed_comment, getAssets());
		Utility.textViewFontRobotoLight(share_ui_tv_comment, getAssets());
		Utility.textViewFontRobotoLight(share_ui_tv_logo, getAssets());
	}

	private void initiViewReference()
	{
		mShareBackImgView = (ImageView) findViewById(R.id.share_ui_backImgview);
		share_ui_ed_comment = (EditText) findViewById(R.id.share_ui_ed_comment);
		share_ui_shareImageView = (ImageView) findViewById(R.id.share_ui_shareImageView);
		share_ui_uploadImageView = (ImageView) findViewById(R.id.share_ui_uploadImageView);
		share_ui_submitBtn = (Button) findViewById(R.id.share_ui_submitBtn);
		share_ui_tv_comment = (TextView) findViewById(id.share_ui_tv_comment);
		share_ui_tv_logo = (TextView) findViewById(id.share_ui_tv_logo);
		mAddMobsLinearLayout = (LinearLayout) findViewById(R.id.add_mobs_lnrlyt);
		LoadAdViewInEveryActivity.loadAdds(this, mAddMobsLinearLayout);
		share_ui_submitBtn.setOnClickListener(this);
		share_ui_uploadImageView.setOnClickListener(this);
		mShareBackImgView.setOnClickListener(this);

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

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub

		if (v == mShareBackImgView)
		{
			finish();

		}

		else
			if (v == share_ui_uploadImageView)
			{
				getImageFromGalleryCamera();

			}
			else
				if (v == share_ui_submitBtn)
				{
					if (getValidation())
					{
						getShare();

					}
				}
	}

	private boolean getValidation()
	{
		if (Utility.isFieldEmpty(share_ui_ed_comment.getText().toString()))
		{
			Utility.showMsgDialog(this, getResources().getString(R.string.alt_comment));
			return false;
		}
		return true;

	}

	private boolean checkgps()
	{
		gpsTracker = new GPSTracker(ShareActivity.this);

		if (gpsTracker.canGetLocation())
		{
			return true;
		}
		else
		{
			gpsTracker.showSettingsAlert();
			return false;
		}
	}

	private void getShare()
	{

		shareProgressDialog = new ProgressDialog(ShareActivity.this);
		shareProgressDialog.setTitle("MO-BIA");
		shareProgressDialog.setCancelable(false);
		shareProgressDialog.show();
		new Thread(new Runnable()
		{

			String response = "";

			@Override
			public void run()
			{

				JSONObject shareJsonObject = new JSONObject();

				JSONObject getLocationName = Utility.getLocationInfo(gpsTracker.getLocation().getLatitude(), gpsTracker.getLocation().getLongitude());
				JSONObject Currentlocation;
				{

					try
					{
						Currentlocation = getLocationName.getJSONArray("results").getJSONObject(0);
						Log.e("RESULT STRING ", Currentlocation.toString());
						location_string = Currentlocation.getString("formatted_address");

						shareJsonObject.put("UserId", Utility.getSharedPrefString(ShareActivity.this, "userId"));

						shareJsonObject.put("Comment", share_ui_ed_comment.getText().toString().trim());

						//"Image": "sample string 5"
						shareJsonObject.put("CurrentLongitude", String.valueOf(gpsTracker.getLocation().getLongitude()));
						shareJsonObject.put("CurrentLatitude", String.valueOf(gpsTracker.getLocation().getLatitude()));
						shareJsonObject.put("TripId", Utility.getSharedPrefStringData(ShareActivity.this, "TripId"));
						shareJsonObject.put("CategoryId", 121);
						shareJsonObject.put("Image", "");

						shareJsonObject.put("LocationName", location_string);

						shareJsonObject.put("DeviceToken", "AxygtehsksT");
					}
					catch (JSONException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Log.e("params for first", shareJsonObject.toString());
					String url = APIUtils.BASE_URL + APIUtils.SHARESCREEN;

					response = Utility.sendJsonWithFile(url, mImagePath, shareJsonObject.toString(), APIUtils.SHARESCREEN);
					Log.e("response", response.toString());
					mShareHandler.post(new Runnable()
					{

						private String msg;

						@Override
						public void run()
						{
							shareProgressDialog.dismiss();

							if (response != null && response.length() > 0)
							{
								Log.e("msg", "is:" + response);

								try
								{
									JSONObject responseJsonObject = new JSONObject(response);
									if (responseJsonObject.optBoolean("Success"))
									{
										msg = responseJsonObject.optString("Message");
										Utility.showMsgDialogForFinish(ShareActivity.this, getResources().getString(R.string.drawlinesuccess));
									}
									else
									{
										msg = responseJsonObject.optString("Message");
										Utility.showMsgDialog(ShareActivity.this, msg);
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

	private void getImageFromGalleryCamera()
	{
		AlertDialog.Builder adb = new AlertDialog.Builder(this);

		adb.setTitle("Choose Photo From");

		//adb.setIcon(android.R.drawable.ic_dialog_alert);

		adb.setPositiveButton(getResources().getString(R.string.lbl_gallery), new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{

				imageCaptureRefrence.selectFromGalley_Share(ShareActivity.this);
			}
		});

		adb.setNegativeButton(getResources().getString(R.string.lbl_camera), new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				imageCaptureRefrence.captureImage(ShareActivity.this);

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

			option.inPreferredConfig = Bitmap.Config.RGB_565;
			option.inSampleSize = 4;

			switch (req)
			{
				case Constant.REQUEST_CODE_IMAGE_CAPTURED:
					mImagePath = onPhotoTaken(data, ShareActivity.this);

					if (mImagePath != null)
					{
						try
						{
							exif = new ExifInterface(mImagePath);
						}
						catch (IOException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						new Thread(new Runnable()
						{
							@Override
							public void run()
							{
								mSavePath = Utility.getCacheFilePath(ShareActivity.this);
								Log.e("path is ", mSavePath);
								mImageSetUpHandler.post(new Runnable()
								{
									@Override
									public void run()
									{

										share_ui_shareImageView.setImageBitmap(getCroppedBitmap(BitmapFactory.decodeFile(mImagePath, option),
												Integer.parseInt(exif.getAttribute(ExifInterface.TAG_ORIENTATION))));
										share_ui_shareImageView.setScaleType(ScaleType.FIT_XY);
									}
								});

							}
						}).start();

					}
					break;

				case Constant.REQUEST_CODE_IMAGE_GALLERY:
					mImagePath = onPhotoTakenFromgallery(data, ShareActivity.this);
					if (mImagePath != null)
					{
						try
						{
							exif = new ExifInterface(mImagePath);
						}
						catch (IOException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						mSavePath = Utility.getCacheFilePath(ShareActivity.this);
						Log.e("path is gallery ", mSavePath);

						share_ui_shareImageView.setImageBitmap(getCroppedBitmap(BitmapFactory.decodeFile(mImagePath, option), Integer.parseInt(exif.getAttribute(ExifInterface.TAG_ORIENTATION))));
						share_ui_shareImageView.setScaleType(ScaleType.FIT_XY);
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
					//				if (width > height) 
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
				resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, width, (height), matrix, true);
			}
			else
				resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, width, (height), matrix, true);

			//		mImageView.setImageBitmap(resizedBitmap);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
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

}
