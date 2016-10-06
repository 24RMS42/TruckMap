package com.ta.truckmap.adapter;

import com.ta.truckmap.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class FinishTripImportentInfoListAdapter extends BaseAdapter{
	private Context mContext;
	private LayoutInflater mInflater;
	private String []mItemList;
	private boolean []checkedPosition;

	public FinishTripImportentInfoListAdapter(Context context,String []item) {
		mContext=context;
		mItemList=item;
		checkedPosition=new boolean[item.length];
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		for(int i=0;i<checkedPosition.length; i++){
			checkedPosition[i]=false;
		}
		
	}
	
	@Override
	public int getCount() {
		if(mItemList!=null)
			return mItemList.length;
		return 0;
	}

	@Override
	public String getItem(int position) {
		if(mItemList!=null && mItemList.length> 0 && position >=0)
			return mItemList[position];
		return "";
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		final ViewHolder holder;

		if (convertView == null)
		{
			holder = new ViewHolder();
			v = mInflater.inflate(R.layout.checkbox_row_layout, null);
			holder.checkBox = (CheckBox) v.findViewById(R.id.checkBox1);
			v.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.checkBox.setChecked(checkedPosition[position]);
		holder.checkBox.setText(mItemList[position]);
		
		
		final int pos=position;
		holder.checkBox.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				CheckBox ch= ((CheckBox)v);
					checkedPosition[pos]=ch.isChecked();
			}
		});
		
		return v;
	}
	
	private class ViewHolder
	{
		CheckBox checkBox;
	}
	
	public String getSelectedTexts(){
		String selectedText="";
		for(int i=0;i<checkedPosition.length;i++){
			if(checkedPosition[i]){
				
				selectedText=selectedText+mItemList[i]+", ";
			}
		}
		
		return selectedText;
		
	}

}
