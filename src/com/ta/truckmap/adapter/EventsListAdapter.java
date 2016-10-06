package com.ta.truckmap.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.ta.truckmap.CustomImageView1;
import com.ta.truckmap.R;
import com.ta.truckmap.bean.EventBean;
import com.ta.truckmap.util.Constant;
import com.ta.truckmap.util.Utility;

public class EventsListAdapter extends BaseAdapter
{
	private Context mContext;
	private LayoutInflater mInflater;
	private List<EventBean> mEventList;
	private ImageLoader imageLoader;
	private DisplayImageOptions mOptions;

	public EventsListAdapter(Context context)
	{
		mContext = context;
		this.imageLoader = Utility.getImageLoader(context);

		//imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		/*	this.mOptions = new DisplayImageOptions.Builder().showImageForEmptyUri(null).showImageOnFail(null).cacheInMemory(true).cacheOnDisc(true).considerExifParams(true)
					.displayer(new SimpleBitmapDisplayer()).imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();*/

		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mEventList = Constant.EVENTLIST;
		initializeImageLoader();
	}

	private void initializeImageLoader()
	{
		mOptions = new DisplayImageOptions.Builder().resetViewBeforeLoading()
		/* .showImageForEmptyUri(R.drawable.opacity) */.cacheInMemory().cacheOnDisc().build();
		imageLoader = ImageLoader.getInstance();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext).threadPoolSize(1).threadPriority(1).memoryCacheSize(1500000) // 1.5 Mb
				.discCacheSize(50000000) // 50 Mb
				.denyCacheImageMultipleSizesInMemory().build();
		imageLoader.init(config);
		imageLoader.clearMemoryCache();
		imageLoader.clearDiscCache();
	}

	@Override
	public int getCount()
	{
		if (mEventList != null)
			return mEventList.size();
		return 0;
	}

	@Override
	public EventBean getItem(int position)
	{
		if (mEventList != null)
		{

			return mEventList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position)
	{

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View v = convertView;
		final ViewHolder holder;

		if (convertView == null)
		{
			holder = new ViewHolder();
			v = mInflater.inflate(R.layout.event_row_layout, null);
			holder.imageView = (CustomImageView1) v.findViewById(R.id.imageView);
			holder.eventName = (TextView) v.findViewById(R.id.event_name);
			holder.cityName = (TextView) v.findViewById(R.id.city_text_view);
			holder.time = (TextView) v.findViewById(R.id.time_text_view);

			v.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}

		holder.eventName.setText(mEventList.get(position).getEventname());
		try
		{

			String starttime = Utility.getLocalTimeFromUTC(mEventList.get(position).getStarttime());/*= cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE)+" "+cal.get(Calendar.AM_PM);*/

			//holder.time.setText(mEventList.get(position).getDate()+", "+mEventList.get(position).getStarttime());
			holder.time.setText(mEventList.get(position).getDate() + ", " + starttime);
			Log.e("date", mEventList.get(position).getDate());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			holder.time.setText(mEventList.get(position).getDate() + ", " + mEventList.get(position).getStarttime());
		}
		setPlace(holder, mEventList.get(position));
		setImage(holder, mEventList.get(position));

		return v;
	}

	private void setImage(ViewHolder holder, EventBean eventBean)
	{
		String imageUrl = "";
		if (eventBean.getImagePathList().size() > 0)
		{
			imageUrl = eventBean.getImagePathList().get(0);

		}
		imageLoader.displayImage(imageUrl, holder.imageView, mOptions, null);

	}

	private void setPlace(ViewHolder holder, EventBean event)
	{

		String place = "";
		String[] arr = event.getPlace().split(",");
		if (arr.length > 2)
		{
			place = arr[arr.length - 2] + "," + arr[arr.length - 1];
		}
		else
		{
			place = event.getPlace();
		}

		holder.cityName.setText(place);

	}

	private class ViewHolder
	{
		private TextView eventName, cityName, time;
		private CustomImageView1 imageView;
	}

}
