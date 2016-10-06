package com.ta.truckmap.asyntask;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.ta.truckmap.AddNewEventActivity;
import com.ta.truckmap.MapScreenActivity;
import com.ta.truckmap.R;
import com.ta.truckmap.rest.api.APIUtils;
import com.ta.truckmap.util.Constant;
import com.ta.truckmap.util.Utility;

public class UpdateEventAsynTask extends AsyncTask<Void, Void, String>
{
	private ProgressDialog mDialog;
	Context context;
	String mMessageString = "failed, try again.";
	String success = "";
	private String mEventId, mEventName, mDetails, mWhere, mWhen, mStartTime, mEndTime, mAmount, mEnddate;
	private String mImagePath, mVideoPath, mDocPath;
	private int isImageDelete,isVideoDelete,isDocDelete;

	public UpdateEventAsynTask(Context context, String eventId, String eventname, String details, String where, String when, String enddate, String start, String end, String amount
			,String imagepath,String videopath, String docpath,int imageDel,int videoDel,int docDel)
	{
		this.context = context;
		mEventId = eventId;
		mEventName = eventname;
		mDetails = details;
		mWhere = where;
		mWhen = when;
		mStartTime = start;
		mEndTime = end;
		mAmount = amount;
		mEnddate = enddate;
		mImagePath = imagepath;
		mVideoPath = videopath;
		mDocPath = docpath;
		isImageDelete=imageDel;
		isVideoDelete=videoDel;
		isDocDelete=docDel;
	}

	@Override
	protected void onPreExecute()
	{
		mDialog = ProgressDialog.show(context, "", "Loading...");
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(Void... params)
	{
		String response = createEventAPIResponse();

		System.out.println(response);
		if (response != null && response.length() > 0)
		{
			try
			{
				JSONObject jsonObject = new JSONObject(response);
				success = jsonObject.getString("Success");
				mMessageString = jsonObject.getString("Message");
				//				JSONObject result=jsonObject.getJSONObject("Result");
				if (success.equalsIgnoreCase("true"))
				{

				}
				return success;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return null;
				//return context.getResources().getString(R.string.bad_response);
			}
		}
		else
		{
			//return context.getResources().getString(R.string.no_response);
			return null;
		}
	}

	@Override
	protected void onPostExecute(String result)
	{
		super.onPostExecute(result);
		if (mDialog != null && mDialog.isShowing())
		{
			try
			{
				mDialog.dismiss();
				if (result.equalsIgnoreCase("true"))
				{
					Toast.makeText(context, mMessageString, Toast.LENGTH_SHORT).show();
					int postion = -1;
					if (context instanceof Activity)
					{
						for (int i = 0; i < Constant.EVENTLIST.size(); i++)
						{
							if (mEventId.equalsIgnoreCase(Constant.EVENTLIST.get(i).getEventId()))
							{
								Constant.EVENTLIST.get(i).setEventname(mEventName);
								Constant.EVENTLIST.get(i).setDetails(mDetails);
								Constant.EVENTLIST.get(i).setPlace(mWhere);
								Constant.EVENTLIST.get(i).setDate(mWhen);
								Constant.EVENTLIST.get(i).setmEnddate(mEnddate);
								Constant.EVENTLIST.get(i).setStarttime(mStartTime);
								Constant.EVENTLIST.get(i).setEndtime(mEndTime);
								postion = i;
							}

						}

						if (context instanceof AddNewEventActivity)
							((AddNewEventActivity) context).onUpdateResult(postion);
					}
				}
				else
				{
					Utility.showMsgDialog(context, mMessageString);
				}

			}
			catch (Exception e)
			{
				// TODO: handle exception
			}

		}
	}

	private String createEventAPIResponse()
	{
		String jsonResponseString = "";
		try
		{
			JSONObject paramJson = new JSONObject();
			paramJson.put("UserID", Utility.getSharedPrefString(context, "userId"));
			paramJson.put("eventId", mEventId);
			paramJson.put("eventname", mEventName);
			paramJson.put("details", mDetails);
			paramJson.put("where", mWhere);
			paramJson.put("when", mWhen);
			paramJson.put("enddate", mEnddate);
			paramJson.put("starttime", mStartTime);
			paramJson.put("endtime", mEndTime);
			paramJson.put("amount", mAmount);
			
			paramJson.put("isimagedelete", isImageDelete);
			paramJson.put("isvideodelete", isVideoDelete);
			paramJson.put("isdocdelete", isDocDelete);
			
//			paramJson.put("isdocdelete", isImageDelete);
//			paramJson.put("", isVideoDelete);
//			paramJson.put("", isDocDelete);
			
			
			String url = APIUtils.BASE_URL + APIUtils.UPDATE_EVENT;
			//jsonResponseString = Utility.POSTWithoutKey(url, paramJson, Constants.BOOK_NEW_COURIER_URL);

			if (Utility.isNetworkAvailable(context))
				//jsonResponseString = Utility.sendJsonWithFile(url, "", paramJson.toString(), APIUtils.UPDATE_EVENT);
				jsonResponseString=Utility.sendJsonWithFile(url, mImagePath, mVideoPath, mDocPath,paramJson.toString(), APIUtils.UPDATE_EVENT);
				//jsonResponseString=Utility.sendJsonWithFile(url, mImagePath, mVideoPath, mDocPath,paramJson.toString(), APIUtils.UPDATE_EVENT);
			else
				Utility.showMsgDialog(context, context.getString(R.string.msg_netork_error));
			System.out.println(jsonResponseString);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return jsonResponseString;

	}
}
