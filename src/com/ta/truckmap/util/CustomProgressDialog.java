package com.ta.truckmap.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;

import com.ta.truckmap.R;

public class CustomProgressDialog
{

	private Context context;
	private Dialog dialog;
	private String message;
	private AsyncTask task;

	public CustomProgressDialog(Context context, String message, AsyncTask task)
	{
		this.context = context;
		this.message = message;
		this.task = task;
		showDialog();
	}

	public CustomProgressDialog(Context context, String message)
	{
		this.context = context;
		this.message = message;
		showDialog();
	}

	public void setCancelable(boolean flag)
	{
		if (dialog != null)
			dialog.setCancelable(flag);
	}

	public boolean isVisible()
	{
		if (dialog != null)
			return dialog.isShowing();
		else
		{
			return false;
		}
	}

	public void showDialog()
	{
		try
		{
			dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.setContentView(R.layout.progress_dialog_wt_message);
			//			(((TextView) ((Dialog) dialog).findViewById(R.id.dialog_message_usr))).setText(message);
			dialog.setCancelable(false);
			dialog.show();
		}
		catch (Exception e)
		{

		}
	}

	public void dismissDialog()
	{
		if (dialog != null && dialog.isShowing())
			dialog.dismiss();
	}

	public void setCancelListner()
	{
		if (dialog != null)
		{
			dialog.setOnCancelListener(new OnCancelListener()
			{

				@Override
				public void onCancel(DialogInterface dialog)
				{
					if (task != null)
						task.cancel(true);
				}
			});
		}
	}

}