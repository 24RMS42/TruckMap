package com.ta.truckmap.asyntask;

import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.ta.truckmap.AddNewEventActivity;
import com.ta.truckmap.CallBack;
import com.ta.truckmap.R;
import com.ta.truckmap.gpstracking.GPSTracker;
import com.ta.truckmap.rest.api.APIUtils;
import com.ta.truckmap.util.CustomProgressDialog;
import com.ta.truckmap.util.Utility;

public class CreateEventAsynTask extends AsyncTask<Void, Void, String> implements CallBack
{
	private CustomProgressDialog mCustomProgressDialog;
	Context context;
	String mMessageString = "failed, try again.";
	String success = "";
	private String mEventName, mDetails, mWhere, mWhen, mStartTime, mEndTime, mImagePath, mVideoPath, mDocPath, mEndDate = "", mType = "";
	private String mAmount;
	private GPSTracker gpsTracker;
	private LatLng currentLatLong;

	public CreateEventAsynTask(Context context, String type, String eventname, String details, String where, String when, String enddate, String start, String end, String amount, String imagepath,
			String videopath, String docpath)
	{
		this.context = context;
		mEventName = eventname;
		mDetails = details;
		mWhere = where;
		mWhen = when;
		mStartTime = start;
		mEndTime = end;
		mAmount = amount;
		mImagePath = imagepath;
		mVideoPath = videopath;
		mDocPath = docpath;
		mEndDate = enddate;
		mType = type;

	}

	@Override
	protected void onPreExecute()
	{
		mCustomProgressDialog = new CustomProgressDialog(context, "Please wait");
		checkgps();
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
		if (mCustomProgressDialog != null && mCustomProgressDialog.isVisible())
		{
			try
			{
				mCustomProgressDialog.dismissDialog();
				if (result.equalsIgnoreCase("true"))
				{
					//Toast.makeText(context, mMessageString, Toast.LENGTH_SHORT).show();
					if (context instanceof AddNewEventActivity)
					{
						((AddNewEventActivity) context).onEventCreated(mMessageString);
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
			paramJson.put("eventname", mEventName);
			paramJson.put("details", mDetails);
			paramJson.put("where", mWhere);
			paramJson.put("when", mWhen);
			paramJson.put("enddate", mEndDate);
			paramJson.put("starttime", mStartTime);
			paramJson.put("endtime", mEndTime);
			paramJson.put("amount", mAmount);

			paramJson.put("latitude", currentLatLong.latitude);
			paramJson.put("longtitude", currentLatLong.longitude);
			paramJson.put("type", mType);

			String url = APIUtils.BASE_URL + APIUtils.CREATE_EVENT;
			//jsonResponseString = Utility.POSTWithoutKey(url, paramJson, Constants.BOOK_NEW_COURIER_URL);
			if (Utility.isNetworkAvailable(context))
				jsonResponseString = Utility.sendJsonWithFile(url, mImagePath, mVideoPath, mDocPath, paramJson.toString(), APIUtils.CREATE_EVENT);
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

	private void checkgps()
	{
		gpsTracker = new GPSTracker(context, this);

		if (gpsTracker.canGetLocation())
		{

			currentLatLong = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
		}
		else
			gpsTracker.showSettingsAlert();
	}

	@Override
	public void onChngedLoc(Location object)
	{
		currentLatLong = new LatLng(object.getLatitude(), object.getLongitude());

	}

}
