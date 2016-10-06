package com.truckmap.model;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class FinishedTripDataBean implements IModel, Parcelable
{
	public String getShareScreenLocationName()
	{
		return shareScreenLocationName;
	}

	public String getmIsrouteOk()
	{
		return mIsrouteOk;
	}

	public void setmIsrouteOk(String mIsrouteOk)
	{
		this.mIsrouteOk = mIsrouteOk;
	}

	public void setShareScreenLocationName(String shareScreenLocationName)
	{
		this.shareScreenLocationName = shareScreenLocationName;
	}

	private String TripId = "", Name, StartLongitude, StartLatitude, EndLongitude, EndLatitude, StartPointName, EndPointName, mDrawLineStartLongitude, mDrawLineStartLatitude, mDrawLineEndLongitude,
			mDrawEndLatitude, mDrawComment, mDrawColor, mDrawLineStartName, mDrawLineEndName, mCategory, mSndPtLat = "", mSndPtLog = "", mSndPtName = "", shareScreenId = "", shareScreenTitle = "",
			shareScreenComment = "", shareScreenCurrentLongitude = "", shareScreenCurrentLatitude = "", shareScreenImageUrl = "", shareScreenVideoUrl = "", shareScreenAudioUrl = "",
			shareScreenLocationName = "", mComment = "", mCotactDetail = "", mRoutPointLatLongArryString = "", mExtraInformation = "", mTruckHeight = "", mTruckWidth = "", mTruckLength = "",
			mIsrouteOk = "";

	/*	"Id":"10210",
		"Title":"sdertert",
		"Comment":"retertert",
		"CurrentLongitude":"2.857000000000000e+001",
		"CurrentLatitude":"7.731999833333330e+001",
		"TripId":"10405",
		"ImageUrl":"http://staging10.techaheadcorp.com/Truckmap/Images/6456415db7894ad09455fbbabe883caa.jpg",
		"LocationName":"A-53, Tulsi Marg, Block A, Sector 17, Noida, Uttar Pradesh 201301, India",
		"VideoUrl":"",
		"AudioUrl":"",
		"Userid":"1062"*/

	public String getmCotactDetail()
	{
		return mCotactDetail;
	}

	public void setmCotactDetail(String mCotactDetail)
	{
		this.mCotactDetail = mCotactDetail;
	}

	public String getmTruckHeight()
	{
		return mTruckHeight;
	}

	public void setmTruckHeight(String mTruckHeight)
	{
		this.mTruckHeight = mTruckHeight;
	}

	public String getmTruckWidth()
	{
		return mTruckWidth;
	}

	public void setmTruckWidth(String mTruckWidth)
	{
		this.mTruckWidth = mTruckWidth;
	}

	public String getmTruckLength()
	{
		return mTruckLength;
	}

	public void setmTruckLength(String mTruckLength)
	{
		this.mTruckLength = mTruckLength;
	}

	public String getmExtraInformation()
	{
		return mExtraInformation;
	}

	public void setmExtraInformation(String mExtraInformation)
	{
		this.mExtraInformation = mExtraInformation;
	}

	public String getmRoutPointLatLongArryString()
	{
		return mRoutPointLatLongArryString;
	}

	public void setmRoutPointLatLongArryString(String mRoutPointLatLongArryString)
	{
		this.mRoutPointLatLongArryString = mRoutPointLatLongArryString;
	}

	public String getmComment()
	{
		return mComment;
	}

	public void setmComment(String mComment)
	{
		this.mComment = mComment;
	}

	public String getShareScreenId()
	{
		return shareScreenId;
	}

	public void setShareScreenId(String shareScreenId)
	{
		this.shareScreenId = shareScreenId;
	}

	public String getShareScreenTitle()
	{
		return shareScreenTitle;
	}

	public void setShareScreenTitle(String shareScreenTitle)
	{
		this.shareScreenTitle = shareScreenTitle;
	}

	public String getShareScreenComment()
	{
		return shareScreenComment;
	}

	public void setShareScreenComment(String shareScreenComment)
	{
		this.shareScreenComment = shareScreenComment;
	}

	public String getShareScreenCurrentLongitude()
	{
		return shareScreenCurrentLongitude;
	}

	public void setShareScreenCurrentLongitude(String shareScreenCurrentLongitude)
	{
		this.shareScreenCurrentLongitude = shareScreenCurrentLongitude;
	}

	public String getShareScreenCurrentLatitude()
	{
		return shareScreenCurrentLatitude;
	}

	public void setShareScreenCurrentLatitude(String shareScreenCurrentLatitude)
	{
		this.shareScreenCurrentLatitude = shareScreenCurrentLatitude;
	}

	public String getShareScreenImageUrl()
	{
		return shareScreenImageUrl;
	}

	public void setShareScreenImageUrl(String shareScreenImageUrl)
	{
		this.shareScreenImageUrl = shareScreenImageUrl;
	}

	public String getShareScreenVideoUrl()
	{
		return shareScreenVideoUrl;
	}

	public void setShareScreenVideoUrl(String shareScreenVideoUrl)
	{
		this.shareScreenVideoUrl = shareScreenVideoUrl;
	}

	public String getShareScreenAudioUrl()
	{
		return shareScreenAudioUrl;
	}

	public void setShareScreenAudioUrl(String shareScreenAudioUrl)
	{
		this.shareScreenAudioUrl = shareScreenAudioUrl;
	}

	public String getmSndPtLat()
	{
		return mSndPtLat;
	}

	public void setmSndPtLat(String mSndPtLat)
	{
		this.mSndPtLat = mSndPtLat;
	}

	public String getmSndPtLog()
	{
		return mSndPtLog;
	}

	public void setmSndPtLog(String mSndPtLog)
	{
		this.mSndPtLog = mSndPtLog;
	}

	public String getmSndPtName()
	{
		return mSndPtName;
	}

	public void setmSndPtName(String mSndPtName)
	{
		this.mSndPtName = mSndPtName;
	}

	public FinishedTripDataBean(Parcel source)
	{
		TripId = source.readString();
		Name = source.readString();
		StartLongitude = source.readString();
		StartLatitude = source.readString();
		EndLongitude = source.readString();
		EndLatitude = source.readString();
		StartPointName = source.readString();
		EndPointName = source.readString();
		mDrawLineStartLongitude = source.readString();
		mDrawLineStartLatitude = source.readString();
		mDrawLineEndLongitude = source.readString();

		mDrawEndLatitude = source.readString();
		mDrawComment = source.readString();
		mDrawColor = source.readString();
		mDrawLineStartName = source.readString();
		mDrawLineEndName = source.readString();
		mCategory = source.readString();
		mSndPtLat = source.readString();
		mSndPtLog = source.readString();
		mSndPtName = source.readString();
		mTruckHeight = source.readString();
		mTruckWidth = source.readString();
		mTruckLength = source.readString();
		mIsrouteOk = source.readString();
		mCotactDetail = source.readString();

	}

	public String getmCategory()
	{
		if (mCategory == null)
		{
			mCategory = "";
		}
		return mCategory;
	}

	public void setmCategory(String mCategory)
	{
		this.mCategory = mCategory;
	}

	public String getmDrawLineStartName()
	{
		return mDrawLineStartName;
	}

	public void setmDrawLineStartName(String mDrawLineStartName)
	{
		this.mDrawLineStartName = mDrawLineStartName;
	}

	public String getmDrawLineEndName()
	{
		return mDrawLineEndName;
	}

	public void setmDrawLineEndName(String mDrawLineEndName)
	{
		this.mDrawLineEndName = mDrawLineEndName;
	}

	public FinishedTripDataBean()
	{

	}

	public String getEndLatitude()
	{
		return EndLatitude;
	}

	public void setEndLatitude(String endLatitude)
	{
		EndLatitude = endLatitude;
	}

	private ArrayList<FinishedTripDataBean> tripArray;
	private ArrayList<FinishedTripDataBean> drawLineArray;

	public ArrayList<FinishedTripDataBean> getShareScreenArray()
	{
		return shareScreenArray;
	}

	public void setShareScreenArray(ArrayList<FinishedTripDataBean> shareScreenArray)
	{
		this.shareScreenArray = shareScreenArray;
	}

	private ArrayList<FinishedTripDataBean> shareScreenArray;

	public String getTripId()
	{
		return TripId;
	}

	public void setTripId(String tripId)
	{
		TripId = tripId;
	}

	public String getName()
	{
		return Name;
	}

	public void setName(String name)
	{
		Name = name;
	}

	public String getStartLongitude()
	{
		return StartLongitude;
	}

	public void setStartLongitude(String startLongitude)
	{
		StartLongitude = startLongitude;
	}

	public String getStartLatitude()
	{
		return StartLatitude;
	}

	public void setStartLatitude(String startLatitude)
	{
		StartLatitude = startLatitude;
	}

	public String getEndLongitude()
	{
		return EndLongitude;
	}

	public void setEndLongitude(String endLongitude)
	{
		EndLongitude = endLongitude;
	}

	public String getStartPointName()
	{
		return StartPointName;
	}

	public void setStartPointName(String startPointName)
	{
		StartPointName = startPointName;
	}

	public String getEndPointName()
	{
		return EndPointName;
	}

	public void setEndPointName(String endPointName)
	{
		EndPointName = endPointName;
	}

	public String getmDrawLineStartLongitude()
	{
		return mDrawLineStartLongitude;
	}

	public void setmDrawLineStartLongitude(String mDrawLineStartLongitude)
	{
		this.mDrawLineStartLongitude = mDrawLineStartLongitude;
	}

	public String getmDrawLineStartLatitude()
	{
		return mDrawLineStartLatitude;
	}

	public void setmDrawLineStartLatitude(String mDrawLineStartLatitude)
	{
		this.mDrawLineStartLatitude = mDrawLineStartLatitude;
	}

	public String getmDrawLineEndLongitude()
	{
		return mDrawLineEndLongitude;
	}

	public void setmDrawLineEndLongitude(String mDrawLineEndLongitude)
	{
		this.mDrawLineEndLongitude = mDrawLineEndLongitude;
	}

	public String getmDrawEndLatitude()
	{
		return mDrawEndLatitude;
	}

	public void setmDrawEndLatitude(String mDrawEndLatitude)
	{
		this.mDrawEndLatitude = mDrawEndLatitude;
	}

	public String getmDrawComment()
	{
		return mDrawComment;
	}

	public void setmDrawComment(String mDrawComment)
	{
		this.mDrawComment = mDrawComment;
	}

	public String getmDrawColor()
	{
		return mDrawColor;
	}

	public void setmDrawColor(String mDrawColor)
	{
		this.mDrawColor = mDrawColor;
	}

	public ArrayList<FinishedTripDataBean> getTripArray()
	{
		return tripArray;
	}

	public void setTripArray(ArrayList<FinishedTripDataBean> tripArray)
	{
		this.tripArray = tripArray;
	}

	public ArrayList<FinishedTripDataBean> getDrawLineArray()
	{
		return drawLineArray;
	}

	public void setDrawLineArray(ArrayList<FinishedTripDataBean> drawLineArray)
	{
		this.drawLineArray = drawLineArray;
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
		dest.writeString(TripId);
		dest.writeString(Name);
		dest.writeString(StartLongitude);
		dest.writeString(StartLatitude);
		dest.writeString(EndLongitude);
		dest.writeString(EndLatitude);
		dest.writeString(StartPointName);
		dest.writeString(EndPointName);
		dest.writeString(mDrawLineStartLongitude);
		dest.writeString(mDrawLineStartLatitude);
		dest.writeString(mDrawLineEndLongitude);
		dest.writeString(mDrawEndLatitude);
		dest.writeString(mDrawComment);
		dest.writeString(mDrawColor);
		dest.writeString(mDrawLineStartName);
		dest.writeString(mDrawLineEndName);

		dest.writeString(mSndPtLat);
		dest.writeString(mSndPtLog);
		dest.writeString(mSndPtName);

		dest.writeString(mTruckHeight);
		dest.writeString(mTruckLength);
		dest.writeString(mTruckWidth);
		dest.writeString(mIsrouteOk);
		dest.writeString(mCotactDetail);

	}

	public static final Parcelable.Creator<FinishedTripDataBean> CREATOR = new Parcelable.Creator<FinishedTripDataBean>()
	{

		@Override
		public FinishedTripDataBean createFromParcel(Parcel source)
		{

			return new FinishedTripDataBean(source);
		}

		@Override
		public FinishedTripDataBean[] newArray(int size)
		{

			return new FinishedTripDataBean[size];
		}
	};

}
