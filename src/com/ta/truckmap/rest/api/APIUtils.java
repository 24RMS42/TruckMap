package com.ta.truckmap.rest.api;

/**
 * @author adventss APIUtils provide baseurl method and declares all webservice
 *   
 *         name/url
 */
public class APIUtils
{

	public enum REQUEST_TYPE
	{
		GET, POST, MULTIPART_GET, MULTIPART_POST
	}

	//public static final String BASE_URL = "http://10.11.5.172/TruckMap/api/WebService/";
	//public static final String BASE_URL = "http://10.11.5.99/truckmap/api/WebService/";
	//http://www.mo-bia.com/api/WebService
	// http://10.11.5.147/api/WebService/SignUp
	//public static final String BASE_URL = "http://10.11.5.147/api/WebService/";

	public static final String BASE_URL = "http://www.mo-bia.com/api/WebService/";

	//public static final String BASE_URL = "http://staging10.techaheadcorp.com/Truckmap/api/WebService/";

	public static final String SIGNUP = "SignUp";
	public static final String LOGIN = "Login";
	public static final String NEWTRIP = "NewTrip";
	public static final String FINISHTRIP = "FinishTrip";
	public static final String EARLIESTTRIP = "EarliestTrip";
	public static final String FORGETPASSWORD = "ForgetPassword";
	public static final String DRAWLINE = "DrawLine";
	public static final String GETCATEGORY = "GetCategory";
	public static final String SHARESCREEN = "ShareScreen";
	public static final String CURRENTRIP = "CurrentTrip";
	public static final String UPDATERADIUS = "UpdateRadius";
	public static final String NOTIFICATIONS = "Notifications";
	public static final String SHARESCREENDETAIL = "ShareScreenDetail";
	public static final String CANCELTRIP = "CancelTrip";
	public static final String DRAWLINEDETAIL = "DrawLineDetail";
	public static final String UPDATELOCATION = "UpdateLocation";
	public static final String REFRESHMAP = "RefreshMap";
	public static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	public static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	public static final String OUT_JSON = "/json";
	public static final String CHECKPURCHASED = "checkPurchased";
	public static final String UPDATEPURCHASE = "UpdatePurchase";
	public static final String CREATE_EVENT = "CreateEvent";
	public static final String GET_EVENT_LIST = "GetEventList";

	public static final String GET_PLACE_INFO = "GetPlaceInfo";

	public static final String UPDATE_EVENT = "UpdateEvent";
	public static final String FINISH_TRIP_REQUEST = "FinishTripRequest";
	public static final String REUSE_TRIP = "ReUseTrip";

	public static final String GETEVENTDETAILSBYID = "GetEventDetailsById";

}
