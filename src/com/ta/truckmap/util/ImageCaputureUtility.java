package com.ta.truckmap.util;

import java.io.Serializable;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.provider.MediaStore;
import android.provider.MediaStore.Video;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.ta.truckmap.R;
import com.ta.truckmap.ShareActivity;

public class ImageCaputureUtility implements Serializable
{
	ImageCaputureUtility imageCaptureRefrence;

	public static ImageCaputureUtility getInstance()
	{
		return new ImageCaputureUtility();
	}

	//code for camera button event
	public void captureImage(Activity fragmentActivity)
	{
		ContentValues values = new ContentValues();
		String fileName = System.currentTimeMillis() + ".png";
		values.put(MediaStore.Images.Media.TITLE, fileName);
		try
		{
			Constant.URI_IMAGE_CAPTURED = fragmentActivity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

			//intent.putExtra("crop", "true");
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Constant.URI_IMAGE_CAPTURED);
			Log.e("constant", Constant.URI_IMAGE_CAPTURED.getPath() + "\n" + Constant.URI_IMAGE_CAPTURED);
			fragmentActivity.startActivityForResult(intent, Constant.REQUEST_CODE_IMAGE_CAPTURED);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Log.w("in", "capture");
			Toast.makeText(fragmentActivity, fragmentActivity.getResources().getString(R.string.msg_sd_card_nt_avail), Toast.LENGTH_LONG).show();
		}
	}

	//code for camera button event
	public void captureImage(Fragment fragmentActivity)
	{
		ContentValues values = new ContentValues();
		String fileName = System.currentTimeMillis() + ".png";
		values.put(MediaStore.Images.Media.TITLE, fileName);
		try
		{
			Constant.URI_IMAGE_CAPTURED = fragmentActivity.getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

			//				intent.putExtra("crop", "true");
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Constant.URI_IMAGE_CAPTURED);
			Log.e("constant", Constant.URI_IMAGE_CAPTURED.getPath() + "\n" + Constant.URI_IMAGE_CAPTURED);
			fragmentActivity.startActivityForResult(intent, Constant.REQUEST_CODE_IMAGE_CAPTURED);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Log.w("in", "capture");
			Toast.makeText(fragmentActivity.getActivity(), fragmentActivity.getResources().getString(R.string.msg_sd_card_nt_avail), Toast.LENGTH_LONG).show();
		}
	}

	//code for gallery button event SignUp Screen
	public void selectFromGalley(Activity signUpActivity)
	{
		try
		{
			Intent picImage = new Intent();
			picImage.setAction(Intent.ACTION_PICK);
			picImage.setType("image/*");
			picImage.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			signUpActivity.startActivityForResult(picImage, Constant.REQUEST_CODE_IMAGE_GALLERY);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Toast.makeText(signUpActivity, signUpActivity.getResources().getString(R.string.msg_sd_card_nt_avail), Toast.LENGTH_LONG).show();
		}
		Log.w("in", "gallery");
	}

	//code for gallery button event ShareScreen 
	public void selectFromGalley_Share(ShareActivity shareActivity)
	{
		try
		{
			Intent picImage = new Intent();
			picImage.setAction(Intent.ACTION_PICK);
			picImage.setType("image/*");
			picImage.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			shareActivity.startActivityForResult(picImage, Constant.REQUEST_CODE_IMAGE_GALLERY);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Toast.makeText(shareActivity, shareActivity.getResources().getString(R.string.msg_sd_card_nt_avail), Toast.LENGTH_LONG).show();
		}
		Log.w("in", "gallery");
	}

	//code for gallery button evevnt
	public void selectFromGalley(Fragment fragmentActivity)
	{
		try
		{
			Intent picImage = new Intent();
			picImage.setAction(Intent.ACTION_PICK);
			picImage.setType("image/*");
			picImage.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			fragmentActivity.startActivityForResult(picImage, Constant.REQUEST_CODE_IMAGE_GALLERY);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Toast.makeText(fragmentActivity.getActivity(), fragmentActivity.getResources().getString(R.string.msg_sd_card_nt_avail), Toast.LENGTH_LONG).show();
		}
		Log.w("in", "gallery");
	}

	//code for camera button event for video
	public void captureVideo(Activity fragmentActivity)
	{
		ContentValues values = new ContentValues();
		String fileName = System.currentTimeMillis() + ".mp4";
		values.put(MediaStore.Video.Media.TITLE, fileName);
		values.put(Video.Media.MIME_TYPE, "video/mp4");
		try
		{
			Constant.URI_VIDEO_CAPTURED = fragmentActivity.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
			Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
			intent.putExtra("android.intent.extra.durationLimit", 15);
			intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 10485760L);//10 mb

			//			intent.putExtra(android.provider.MediaStore.EXTRA_VIDEO_QUALITY, 0);
			//			intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, value)
			intent.putExtra("android.intent.extra.videoQuality", 0);
			//			 intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT,4393216L);//4 mb
			//				intent.putExtra(MediaStore.EXTRA_OUTPUT, Constants.URI_VIDEO_CAPTURED);

			fragmentActivity.startActivityForResult(intent, Constant.REQUEST_CODE_VIDEO_CAPTURED);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Log.w("in", "capture");
			Toast.makeText(fragmentActivity, fragmentActivity.getResources().getString(R.string.sd_card_not_available), Toast.LENGTH_LONG).show();
		}
	}

	//code for gallery button evevnt for video
	public void selectVideoFromGalley(Activity fragmentActivity)
	{
		try
		{
			Intent picImage = new Intent();
			picImage.setAction(Intent.ACTION_PICK);
			picImage.setType("video/*");
			picImage.setData(MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
			fragmentActivity.startActivityForResult(picImage, Constant.REQUEST_CODE_VIDEO_GALLERY);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Toast.makeText(fragmentActivity, fragmentActivity.getResources().getString(R.string.msg_sd_card_nt_avail), Toast.LENGTH_LONG).show();
		}
		Log.w("in", "gallery");
	}

}
