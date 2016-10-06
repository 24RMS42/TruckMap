package com.truckmap.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author ravindmaurya
 * 
 */
public class UserDataBean implements IModel, Parcelable
{

	private String Name = "", Email = "", ImageUrl = "", Userid = "", truckHeight="", truckWidth="", truckLenght="";

	public String getTruckHeight()
	{
		return truckHeight;
	}

	public void setTruckHeight(String truckHeight)
	{
		this.truckHeight = truckHeight;
	}

	public String getTruckWidth()
	{
		return truckWidth;
	}

	public void setTruckWidth(String truckWidth)
	{
		this.truckWidth = truckWidth;
	}

	public String getTruckLenght()
	{
		return truckLenght;
	}

	public void setTruckLenght(String truckLenght)
	{
		this.truckLenght = truckLenght;
	}

	public String getName()
	{
		return Name;
	}

	public void setName(String name)
	{
		Name = name;
	}

	public String getEmail()
	{
		return Email;
	}

	public void setEmail(String email)
	{
		Email = email;
	}

	public String getImageUrl()
	{
		return ImageUrl;
	}

	public void setImageUrl(String imageUrl)
	{
		ImageUrl = imageUrl;
	}

	public String getUserid()
	{
		return Userid;
	}

	public void setUserid(String userid)
	{
		Userid = userid;
	}

	public UserDataBean()
	{

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

		dest.writeString(Name);
		dest.writeString(Email);
		dest.writeString(ImageUrl);
		dest.writeString(truckHeight);
		dest.writeString(truckWidth);
		dest.writeString(truckLenght);

	}

	private UserDataBean(Parcel source)
	{
		Name = source.readString();
		Email = source.readString();
		ImageUrl = source.readString();
		truckHeight = source.readString();
		truckWidth = source.readString();
		truckLenght = source.readString();

	}

	public static final Parcelable.Creator<UserDataBean> CREATOR = new Parcelable.Creator<UserDataBean>()
	{

		@Override
		public UserDataBean createFromParcel(Parcel source)
		{

			return new UserDataBean(source);
		}

		@Override
		public UserDataBean[] newArray(int size)
		{

			return new UserDataBean[size];
		}
	};
}
