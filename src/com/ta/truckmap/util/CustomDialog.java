package com.ta.truckmap.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.ta.truckmap.R;

/**
 * @author 
 * 
 * 
 *         Alert dialog setArguments bundle title, message,label ok, label
 *         cancel dialog type,interface object, layout id if any else -1.
 * 
 *         For Ok only use onActionOk callback. For OkCancel use onActionOk and
 *         onActioncalcel callback.
 * 
 *         Intermediate progress dialog
 * 
 *         setArguments bundle message,interface object, layout id if any else
 *         -1
 */

public class CustomDialog
{
	private static CustomDialog customDialog;
	private IActionOKCancel callback;
	private DIALOG_TYPE type;
	private String title, message;
	private String ok, cancel, neutral;
	private int viewID, requestCode = -1;
	private Context context;
	private boolean cancelable = true;

	public enum DIALOG_TYPE
	{
		OK_ONLY, OK_CANCEL, INTERMDIATE_PROGRESS_WITH_MSG, INTERMDIATE_PROGRESS_WO_MSG, OK_ONLY_BACK, THREE_BUTTON
	};

	/**
	 * 
	 * Alert dialog setArguments bundle title, message,label ok, label cancel
	 * dialog type,interface object[class must implement IactionOkCancel],
	 * layout id if any else -1.
	 * 
	 * For Ok only use onActionOk callback. For OkCancel use onActionOk and
	 * onActioncalcel callback.
	 * 
	 * Intermediate progress dialog setArguments bundle message,interface
	 * object, layout id if any else -1
	 */

	public static CustomDialog getInstance(Context con, IActionOKCancel callback, String message, String title, DIALOG_TYPE type, int layoutId, String labelOK, String labelCancel, int requestCode)
	{
		customDialog = new CustomDialog();
		customDialog.context = con;
		customDialog.callback = callback;
		customDialog.type = type;
		customDialog.title = title == null ? "Mo-Bia App" : title;
		customDialog.cancel = labelCancel;
		customDialog.ok = labelOK;
		customDialog.message = message == null ? "Mo-Bia App" : message;
		customDialog.requestCode = requestCode;
		return customDialog;

	}

	public static CustomDialog getInstance(Context con, IActionOKCancel callback, String message, DIALOG_TYPE type, int requestCode)
	{
		customDialog = new CustomDialog();
		customDialog.context = con;
		customDialog.callback = callback;
		customDialog.type = type;
		customDialog.title = "Message";
		customDialog.cancel = "Cancel";
		customDialog.ok = "OK";
		customDialog.message = message == null ? "Mo-Bia App" : message;
		customDialog.requestCode = requestCode;
		return customDialog;

	}

	public static CustomDialog getInstance(Context con, IActionOKCancel callback, String message, String title, DIALOG_TYPE type, int requestCode)
	{
		customDialog = new CustomDialog();
		customDialog.context = con;
		customDialog.callback = callback;
		customDialog.type = type;
		customDialog.title = title;
		customDialog.cancel = "Cancel";
		customDialog.ok = "OK";
		customDialog.message = message == null ? "Mo-Bia App" : message;
		customDialog.requestCode = requestCode;
		return customDialog;

	}

	public void show()
	{
		ShowDialog(type);
	}

	//	public void setStyle()
	//	{
	//		setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppTheme);
	//	}

	public void setCancellable(boolean isCancel)
	{
		customDialog.cancelable = isCancel;
	}

	public void ShowDialog(DIALOG_TYPE type)
	{
		switch (type)
		{
			case OK_ONLY:
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle(title);
				builder.setIcon(R.drawable.appicon);
				builder.setMessage(message);

				builder.setPositiveButton(ok, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						dialog.dismiss();
						if (callback != null)
							callback.onActionOk(requestCode);

					}
				}).create().show();

				// Create the AlertDialog object and return it
			}
				break;
			case OK_ONLY_BACK:
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle(title);
				builder.setIcon(R.drawable.appicon);
				builder.setMessage(message);

				builder.setPositiveButton(ok, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						dialog.dismiss();

					}
				}).create().show();

				// Create the AlertDialog object and return it
			}
				break;
			case OK_CANCEL:
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle(title);
				builder.setIcon(R.drawable.appicon);
				builder.setMessage(message);

				builder.setPositiveButton(ok, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						dialog.dismiss();
						if (callback != null)
							callback.onActionOk(requestCode);

					}
				});

				builder.setNegativeButton(cancel, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						dialog.dismiss();
						if (callback != null)
							callback.onActionCancel(requestCode);

					}
				}).create().show();
				// Create the AlertDialog object and return it
			}
				break;
			case THREE_BUTTON:

			{
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle(title);
				builder.setIcon(R.drawable.appicon);
				builder.setMessage(message);

				builder.setPositiveButton(ok, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						dialog.dismiss();
						if (callback != null)
							callback.onActionOk(requestCode);

					}
				});

				builder.setNegativeButton(cancel, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						dialog.dismiss();
						if (callback != null)
							callback.onActionCancel(requestCode);

					}
				});

				builder.setNeutralButton(neutral, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						dialog.dismiss();
						if (callback != null)
							callback.onActionNeutral(requestCode);

					}
				}).create().show();

				// Create the AlertDialog object and return it
			}
				break;
			default:
				break;
		}
	}

}
