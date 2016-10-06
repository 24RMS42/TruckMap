package com.ta.truckmap.util;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.ta.truckmap.AddNewEventActivity;
import com.ta.truckmap.MapScreenActivity;
import com.ta.truckmap.R;
import com.ta.truckmap.util.CustomDialog.DIALOG_TYPE;

public class SetVideoFromCamera implements IActionOKCancel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Activity activity;
	private Fragment fragment;
	ImageCaputureUtility mImageCapRefrence;
	private Handler mHandler = new Handler();
	private final int REQ_VIDEO_GALLERY = 2894;
	private String mVideoPath;

	public SetVideoFromCamera(Activity fragActivity, Fragment postFragment)
	{
		activity = fragActivity;
		fragment = postFragment;
		mImageCapRefrence = ImageCaputureUtility.getInstance();
	}

	public SetVideoFromCamera(Activity fragActivity)
	{
		activity = fragActivity;
		mImageCapRefrence = ImageCaputureUtility.getInstance();
	}

	public void showVideoDialog()
	{

		CustomDialog dialog = CustomDialog.getInstance(activity, this, activity.getResources().getString(R.string.msg_select_video), null, DIALOG_TYPE.OK_CANCEL, -1, activity.getResources()
				.getString(R.string.lbl_camera), activity.getResources().getString(R.string.lbl_gallery), REQ_VIDEO_GALLERY);

		dialog.show();
	}

	private void setImageAndBorder(Bitmap image)
	{

		if (activity != null && activity instanceof AddNewEventActivity)
		{
			((AddNewEventActivity) activity).setVideoPreview(mVideoPath, image);
		}
		
		else if (activity != null && activity instanceof MapScreenActivity)
		{
			((MapScreenActivity) activity).setVideoPreview(mVideoPath, image);
		}

	}

	//code for getting the imagepath of video taken via Camera to imageView 
	public void onVideoRecord(Intent data)
	{
		try
		{
			String[] projection = { android.provider.MediaStore.Video.Media.DATA };
			@SuppressWarnings("deprecation")
			Cursor cursor = activity.managedQuery(data.getData(), projection, null, null, null);
			int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
			cursor.moveToFirst();
			mVideoPath = cursor.getString(column_index_data);
			Log.e("myImage path", mVideoPath);

			new Thread()
			{
				public void run()
				{
					mHandler.post(new Runnable()
					{
						@Override
						public void run()
						{
							try
							{
								Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(mVideoPath, MediaStore.Images.Thumbnails.MINI_KIND);

								setImageAndBorder(thumbnail);
							}
							catch (Exception e)
							{
								e.printStackTrace();
							}
						}
					});
				}
			}.start();
			Log.w("imagePath", mVideoPath);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Log.e("mVideoPath", "onactivity result");
		}
	}

	//code for getting the imagepath of video taken from Gallery to imageView 
	public void onVideoTakenFromgallery(Intent data)
	{
		try
		{
			Uri _uri = data.getData();
			if (_uri != null)
			{
				Cursor cursor = activity.getContentResolver().query(_uri, new String[] { android.provider.MediaStore.Video.Media.DATA, MediaStore.Video.Media.DURATION, MediaStore.Video.Media.SIZE },
						null, null, null);
				cursor.moveToFirst();

				Log.e("duration of file", "" + cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION)));
				Log.e("size of file", "" + cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE)));

				long sizeOfTitle = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
				long durationOfTitle = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
				if (sizeOfTitle > 10485760) // size of video should be greater than 10mb
					Utility.showToastMessage(activity, activity.getString(R.string.video_size_limit_exceed));
				else
					if ((durationOfTitle / 1000) > 15)
						Utility.showToastMessage(activity, activity.getString(R.string.video_duration_limit_exceed));
					else
					{
						mVideoPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
						Log.e("Image path", mVideoPath);

						new Thread()
						{
							public void run()
							{
								mHandler.post(new Runnable()
								{
									@Override
									public void run()
									{
										try
										{
											Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(mVideoPath, MediaStore.Images.Thumbnails.MINI_KIND);

											setImageAndBorder(thumbnail);
										}
										catch (Exception e)
										{
										}
									}
								});
							}
						}.start();
					}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Log.e("imagePath", "onactivity result");
		}
	}

	public void onVideoTakenResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode == Activity.RESULT_OK)
		{
			if (requestCode == Constant.REQUEST_CODE_VIDEO_CAPTURED)// capture video form camera
			{
				onVideoRecord(data);
			}
			else
				if (requestCode == Constant.REQUEST_CODE_VIDEO_GALLERY && data != null) // pick video from gallary
				{
					onVideoTakenFromgallery(data);
				}
		}
	}

	@Override
	public void onActionOk(int requestCode)
	{
		if (requestCode == REQ_VIDEO_GALLERY)
		{
			if (mImageCapRefrence != null)
				mImageCapRefrence.captureVideo(activity);
		}

	}

	@Override
	public void onActionCancel(int requestCode)
	{
		if (requestCode == REQ_VIDEO_GALLERY)
		{
			if (mImageCapRefrence != null)
				mImageCapRefrence.selectVideoFromGalley(activity);
		}
	}

	@Override
	public void onActionNeutral(int requestCode)
	{

	}

}
