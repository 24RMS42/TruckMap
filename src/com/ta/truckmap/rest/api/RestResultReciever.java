package com.ta.truckmap.rest.api;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

public class RestResultReciever extends ResultReceiver
{
	public Reciever mReciever;

	public RestResultReciever(Handler handler)
	{
		super(handler);

	}

	public void setReciever(Reciever mReciever)
	{
		this.mReciever = mReciever;
	}

	@Override
	protected void onReceiveResult(int resultCode, Bundle resultData)
	{

		super.onReceiveResult(resultCode, resultData);
		if (mReciever != null)
		{
			Log.e("code", resultCode + " " + resultData);
			mReciever.onRecieveResult(resultCode, resultData);
		}
	}

	@Override
	public void send(int resultCode, Bundle resultData)
	{

		super.send(resultCode, resultData);
	}

}
