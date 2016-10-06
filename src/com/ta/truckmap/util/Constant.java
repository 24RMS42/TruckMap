package com.ta.truckmap.util;

import java.util.ArrayList;
import java.util.List;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.ta.truckmap.bean.EventBean;
import com.truckmap.model.FinishedTripDataBean;

public class Constant
{

	public static final String CUISINE_REGION = "region";

	//	public static UserDataBean mUserDataBean;
	//	public static com.pickmykid.model.Error mError;
	public static final String TAG = "In-App";
	// shared Preference declaration
	public static Uri URI_IMAGE_CAPTURED = null;
	public static Uri URI_VIDEO_CAPTURED = null;
	/*Strings for taking images from camera/gallery*/
	public static final String IS_PROFESSIONAL_DRIVER = "is_professional_driver";
	public static final int REQUEST_CODE_IMAGE_CAPTURED = 1;
	public static final int REQUEST_CODE_IMAGE_GALLERY = 0;
	public static final int REQUEST_CODE_VIDEO_CAPTURED = 3;
	public static final int REQUEST_CODE_VIDEO_GALLERY = 4;
	public static final String SHARED_PREF_NAME = "App_shared_pref";
	public static String AD_UNIT_ID = "ca-app-pub-8429393674950200/7506664371";
	// track context
	//mRoutListLatLngsCurrentLine

	public static FinishedTripDataBean mFinishedTripDataBean;
	public static FinishedTripDataBean mCurrentTripDataBean;
	public static FinishedTripDataBean mDrawSharedLineDataBean;
	public static FinishedTripDataBean mRefreshTripDataBean;

	public static ArrayList<LatLng> RoutListLatLngsCurrentLine;

	public static final int CONNECTION_TIME_OUT = 20000;
	public static List<EventBean> EVENTLIST = new ArrayList<EventBean>();

	public static String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqF60w9J5ssh45cs2TKN2zMuD7/yMKkjZCS9MPGcrLJEZnlv8b2oKxQg5GLGoAxu1Dk1IW0ed1ruFsyXmXdYKnahPqAvNVyFxUh2cPl/c/BiH0/0Fnnm+WpgLiYqhNlkiP4kVu8ZnvKHfTlSEHPLmQfFYQgYEbnPUtkWwThx+AjaVNsazzZd+wM+WLMsv0zWDv+Ul8FzYul87rwfXKE2h4kdMnhcMx7bK8IM3sKR0e6qvaKR+m6EKIlmh7vjPrxLOks+KkeetLA39BwaAlKyvCu6/Jf8lVxjDiCurxlrmICMiPY98xAsLHO9ZAzqr7fB9gFuTQySiUKTsQb229nbT9wIDAQAB";
}
