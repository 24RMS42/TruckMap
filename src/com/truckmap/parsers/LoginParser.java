package com.truckmap.parsers;

import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.ta.truckmap.util.Constant;
import com.truckmap.model.Error;
import com.truckmap.model.IModel;
import com.truckmap.model.UserDataBean;

/**
 * @author ravindmaurya
 *
 */
public class LoginParser implements Parser<IModel>
{

	@Override
	public IModel parse(JSONObject dataObj) throws JSONException
	{
		UserDataBean userDataBean = new UserDataBean();
		Log.e("json response", dataObj.toString());

		Log.e("json response", dataObj.toString());
		boolean result = dataObj.optBoolean("Success");

		if (result)
		{
			JSONObject json = dataObj.getJSONObject("Result");
			userDataBean.setName(json.optString("Name"));
			Log.e("firstName", json.optString("firstName"));
			userDataBean.setEmail(json.optString("Email"));
			userDataBean.setImageUrl(json.optString("ImageUrl"));
			userDataBean.setUserid(json.optString("Userid"));
			userDataBean.setTruckHeight(json.optString("TruckHeight"));
			userDataBean.setTruckLenght(json.optString("TruckLength"));
			userDataBean.setTruckWidth(json.optString("TruckWidth"));

			return userDataBean;

		}
		else
		{
			com.truckmap.model.Error error = new Error();
			error.setError(dataObj.optString("Message"));
			return error;

		}

	}

	@Override
	public Collection<IModel> parse(JSONArray array) throws JSONException
	{

		return null;
	}

	@Override
	public IModel parse(String resp) throws JSONException
	{
		// TODO Auto-generated method stub
		return null;
	}

}
