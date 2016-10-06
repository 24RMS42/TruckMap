package com.truckmap.parsers;

import java.util.ArrayList;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.truckmap.model.FinishedTripDataBean;
import com.truckmap.model.IModel;

public class FinishTripParser implements Parser<IModel>
{

	@Override
	public IModel parse(JSONObject jsonObject) throws JSONException
	{

		boolean result = jsonObject.optBoolean("Success");
		Log.e("result", "" + result);
		if (result)

		{
			JSONArray responseArray = jsonObject.getJSONArray("Result");

			///*Constant.mFinishedTripDataBean = (FinishedTripDataBean)*/ getFinishedList(responseArray);

			return getFinishedList(responseArray);

		}
		else
			return null;
	}

	@Override
	public Collection<IModel> parse(JSONArray array) throws JSONException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IModel parse(String resp) throws JSONException
	{

		JSONObject jsonObject = new JSONObject(resp);

		boolean result = jsonObject.optBoolean("Success");
		Log.e("result", "" + result);
		if (result)

		{
			JSONArray responseArray = jsonObject.getJSONArray("Result");

			//Constant.mFinishedTripDataBean = (FinishedTripDataBean) getFinishedList(responseArray);

			return getFinishedList(responseArray);

		}
		else
			return null;
	}

	public static final IModel getFinishedList(JSONArray responseArray) throws JSONException
	{
		IModel tripModel = null;

		ArrayList<FinishedTripDataBean> tripList = new ArrayList<FinishedTripDataBean>();

		for (int i = 0; i < responseArray.length(); i++)
		{
			JSONObject obj = responseArray.getJSONObject(i);

			tripModel = parseTripWithDrawLineArrayObj(obj, tripList);
		}

		return tripModel;
	}

	private static IModel parseTripWithDrawLineArrayObj(JSONObject obj, ArrayList<FinishedTripDataBean> tripList) throws JSONException
	{

		FinishedTripDataBean finishedTripDataBean = new FinishedTripDataBean();
		JSONArray JsonArrayDrawline, JsonArrayShareScreen;

		if (obj != null)
		{

			finishedTripDataBean.setTripId(obj.optString("TripId"));
			finishedTripDataBean.setName(obj.optString("Name"));
			finishedTripDataBean.setStartLongitude(obj.optString("StartLongitude"));

			finishedTripDataBean.setStartLatitude(obj.optString("StartLatitude"));
			finishedTripDataBean.setEndLongitude(obj.optString("EndLongitude"));
			finishedTripDataBean.setEndLatitude(obj.optString("EndLatitude"));

			finishedTripDataBean.setStartPointName(obj.optString("StartPointName"));
			finishedTripDataBean.setEndPointName(obj.optString("EndPointName"));

			finishedTripDataBean.setmSndPtLat(obj.optString("EndFirstLatitude"));
			finishedTripDataBean.setmSndPtLog(obj.optString("EndFirstLongitude"));
			finishedTripDataBean.setmSndPtName(obj.optString("EndFirstPointName"));

			finishedTripDataBean.setmRoutPointLatLongArryString(obj.optString("routePointsInformation"));
			finishedTripDataBean.setmExtraInformation(obj.optString("extraInformation"));
			finishedTripDataBean.setmCotactDetail(obj.optString("cntdetails"));

			finishedTripDataBean.setmTruckHeight(obj.optString("Height"));
			finishedTripDataBean.setmTruckWidth(obj.optString("Width"));
			finishedTripDataBean.setmTruckLength(obj.optString("Length"));
			finishedTripDataBean.setmIsrouteOk(obj.optString("isRootOk"));

			finishedTripDataBean.setmComment(obj.optString("Comment"));

			JsonArrayDrawline = obj.optJSONArray("DrawLines");
			JsonArrayShareScreen = obj.optJSONArray("ShareScreens");

			finishedTripDataBean.setDrawLineArray(getDrawLineArrayList(JsonArrayDrawline));

			finishedTripDataBean.setShareScreenArray(getShareScreenArrayList(JsonArrayShareScreen));

			tripList.add(finishedTripDataBean);

			finishedTripDataBean.setTripArray(tripList);

		}
		return finishedTripDataBean;
	}

	private static ArrayList<FinishedTripDataBean> getDrawLineArrayList(JSONArray jsonArray) throws JSONException
	{

		ArrayList<FinishedTripDataBean> drawLineArrayList = new ArrayList<FinishedTripDataBean>();
		for (int i = 0; i < jsonArray.length(); i++)
		{
			FinishedTripDataBean finishedTripDataBean = new FinishedTripDataBean();
			JSONObject jsonObj = jsonArray.getJSONObject(i);

			finishedTripDataBean.setmDrawLineStartLongitude(jsonObj.optString("StartLongitude"));
			finishedTripDataBean.setmDrawLineStartLatitude(jsonObj.optString("StartLatitude"));
			finishedTripDataBean.setmDrawLineEndLongitude(jsonObj.optString("EndLongitude"));
			finishedTripDataBean.setmDrawEndLatitude(jsonObj.optString("EndLatitude"));
			finishedTripDataBean.setmDrawComment(jsonObj.optString("Comment"));
			finishedTripDataBean.setmDrawColor(jsonObj.optString("Color"));
			/* "StartPointName": "sample string 11",
			"EndPointName": "sample string 12"*/

			finishedTripDataBean.setmDrawLineStartName(jsonObj.optString("StartPointName"));
			finishedTripDataBean.setmDrawLineEndName(jsonObj.optString("EndPointName"));
			finishedTripDataBean.setmCategory(jsonObj.optString("CategoryName"));

			drawLineArrayList.add(finishedTripDataBean);
		}

		return drawLineArrayList;
	}

	private static ArrayList<FinishedTripDataBean> getShareScreenArrayList(JSONArray jsonArray) throws JSONException
	{

		/*"ShareScreens":[

		{
		"Id":"10210",
		"Title":"sdertert",
		"Comment":"retertert",
		"CurrentLongitude":"2.857000000000000e+001",
		"CurrentLatitude":"7.731999833333330e+001",
		"TripId":"10405",
		"ImageUrl":"http://staging10.techaheadcorp.com/Truckmap/Images/6456415db7894ad09455fbbabe883caa.jpg",
		"LocationName":"A-53, Tulsi Marg, Block A, Sector 17, Noida, Uttar Pradesh 201301, India",
		"VideoUrl":"",
		"AudioUrl":"",
		"Userid":"1062"
		}

		]*/

		ArrayList<FinishedTripDataBean> drawLineArrayList = new ArrayList<FinishedTripDataBean>();
		for (int i = 0; i < jsonArray.length(); i++)
		{
			FinishedTripDataBean finishedTripDataBean = new FinishedTripDataBean();
			JSONObject jsonObj = jsonArray.getJSONObject(i);

			finishedTripDataBean.setShareScreenId(jsonObj.optString("Id"));
			finishedTripDataBean.setShareScreenTitle(jsonObj.optString("Title"));
			finishedTripDataBean.setShareScreenComment(jsonObj.optString("Comment"));
			finishedTripDataBean.setShareScreenCurrentLatitude(jsonObj.optString("CurrentLatitude"));
			finishedTripDataBean.setShareScreenCurrentLongitude(jsonObj.optString("CurrentLongitude"));
			finishedTripDataBean.setShareScreenImageUrl(jsonObj.optString("ImageUrl"));

			finishedTripDataBean.setShareScreenLocationName(jsonObj.optString("LocationName"));
			finishedTripDataBean.setShareScreenVideoUrl(jsonObj.optString("VideoUrl"));
			finishedTripDataBean.setShareScreenAudioUrl(jsonObj.optString("AudioUrl"));

			drawLineArrayList.add(finishedTripDataBean);
		}

		return drawLineArrayList;
	}

}
