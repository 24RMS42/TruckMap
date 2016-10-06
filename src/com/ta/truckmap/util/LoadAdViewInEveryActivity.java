package com.ta.truckmap.util;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class LoadAdViewInEveryActivity
{
	private static AdView adView = null;

	public static void loadAdds(Activity act, LinearLayout adLayout)
	{
		adLayout.setVisibility(View.VISIBLE);
		adView = new AdView(act);
		adView.setAdSize(AdSize.SMART_BANNER);
		adView.setAdUnitId(Constant.AD_UNIT_ID);

		AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
		adRequestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
		adLayout.addView(adView);
		adView.loadAd(adRequestBuilder.build());
	}

	public static void destroyView()
	{
		if (adView != null)
		{
			adView.destroy();
			adView = null;
		}
	}
}
