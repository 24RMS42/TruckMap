package com.ta.truckmap.rest.api;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.os.ResultReceiver;

public class RestService extends IntentService
{
	ResultReceiver receiver;
	// private User user = new User();
	String response;
	private String feedID;

	public enum REQUEST_CODE
	{
		MULTIPART_POST_DATA
	};

	public enum RESULT_CODE
	{
		REQUEST_STARTED, SUCESS, ERROR_COMPLETE
	};

	public RestService()

	{
		super("restapi");

	}

	public static final String URL_KEY = "url", DATA_MAP_KEY = "data", FILE_MAP_KEY = "file", RESULT_RECIEVER = "receiver";
	private HashMap<String, Object> keyValue;
	private String url;

	public RestService(String name)
	{
		super("restapi");

	}

	@Override
	protected void onHandleIntent(Intent intent)
	{/*

		if (intent != null) {
		Bundle bundle = intent.getExtras();
		keyValue = bundle.containsKey(DATA_MAP_KEY) ? (HashMap<String, Object>) bundle
		.get(DATA_MAP_KEY) : null;
		url = bundle.containsKey(URL_KEY) ? (String) bundle.get(URL_KEY)
		: null;

		if (Utility.isNetworkAvailable(this) && url != null
		&& keyValue != null) {

		response = Utility.httpPostRequestToServer(
		APIUtils.getBaseURL(url), getJSONParams(keyValue));
		if (response != null)
		Log.e("response= ", response);
		} else {
		Utility.showNetworkNotAvailToast(this);
		}

		}
		*/
	}

	public String getJSONParams(HashMap<String, Object> paramMap)
	{
		if (paramMap == null)
		{
			return null;
		}
		JSONObject obj = new JSONObject();
		for (Map.Entry<String, Object> entry : paramMap.entrySet())
		{
			try
			{
				if (entry.getKey().equalsIgnoreCase("Emails"))
				{
					String[] emailArray = (String[]) entry.getValue();
					JSONArray emailJsonArray = new JSONArray();
					for (int i = 0; i < emailArray.length; i++)
					{
						emailJsonArray.put(emailArray[i]);
					}
					obj.put(entry.getKey(), emailJsonArray);
				}
				else
				{
					obj.put(entry.getKey(), entry.getValue());
				}

			}
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return obj.toString();
	}

}
