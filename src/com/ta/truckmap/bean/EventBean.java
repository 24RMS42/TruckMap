package com.ta.truckmap.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EventBean
{
	private String mEventId = "", mType = "", mEventname = "", mPlace = "", mDate = "", mStarttime = "", mEndtime = "", mDetails = "", mUserId = "", mEnddate = "";

	public String getmEnddate()
	{
		return mEnddate;
	}

	public String getmType()
	{
		return mType;
	}

	public void setmType(String mType)
	{
		this.mType = mType;
	}

	public void setmEnddate(String mEnddate)
	{
		this.mEnddate = mEnddate;
	}

	private List<String> mImagePathList = new ArrayList<String>(),/*mVideoPathList= new ArrayList<String>(),*/mDocPathList = new ArrayList<String>();

	private List<Map<String, String>> mVideoPathList = new ArrayList<Map<String, String>>();

	public String getUserId()
	{
		return mUserId;
	}

	public void setUserId(String userid)
	{
		mUserId = userid;
	}

	public List<String> getImagePathList()
	{
		return mImagePathList;
	}

	public void addImagePathInList(String imagePath)
	{
		mImagePathList.add(imagePath);
	}

	public List<Map<String, String>> getVideoPathList()
	{
		return mVideoPathList;
	}

	public void addVideoPathInList(Map<String, String> videoPath)
	{
		mVideoPathList.add(videoPath);
	}

	public List<String> getDocPathList()
	{
		return mDocPathList;
	}

	public void addDocPathInList(String docPath)
	{
		mDocPathList.add(docPath);
	}

	public String getEventId()
	{
		return mEventId;
	}

	public void setEventId(String eventId)
	{
		mEventId = eventId;
	}

	public String getEventname()
	{
		return mEventname;
	}

	public void setEventname(String eventname)
	{
		mEventname = eventname;
	}

	public String getPlace()
	{
		return mPlace;
	}

	public void setPlace(String place)
	{
		mPlace = place;
	}

	public String getDate()
	{
		return mDate;
	}

	public void setDate(String date)
	{
		mDate = date;
	}

	public String getStarttime()
	{
		return mStarttime;
	}

	public void setStarttime(String starttime)
	{
		mStarttime = starttime;
	}

	public String getEndtime()
	{
		return mEndtime;
	}

	public void setEndtime(String endtime)
	{
		mEndtime = endtime;
	}

	public String getDetails()
	{
		return mDetails;
	}

	public void setDetails(String details)
	{
		mDetails = details;
	}

}
