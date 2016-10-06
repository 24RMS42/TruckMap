package com.truckmap.parsers;

import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.truckmap.model.IModel;

public interface Parser<T extends IModel>
{

	// ok, error are values recieved in webservice response "Error" key.
	public static final int OK = 0;
	public static final int ERROR = 1;

	public abstract T parse(JSONObject json) throws JSONException;

	public Collection<IModel> parse(JSONArray array) throws JSONException;

	public IModel parse(String resp) throws JSONException;

}
