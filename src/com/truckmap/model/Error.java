package com.truckmap.model;

import android.os.Parcel;

public class Error implements IModel {
	private String error = "Some internal error occured!";

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	@Override
	public int describeContents()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		// TODO Auto-generated method stub
		
	}

}
