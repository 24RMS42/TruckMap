package com.ta.truckmap.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CustomImageView extends ImageView
{

	public CustomImageView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public CustomImageView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public CustomImageView(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		try
		{
			float width = MeasureSpec.getSize(widthMeasureSpec);
			float w = getDrawable().getIntrinsicWidth();
			float h = getDrawable().getIntrinsicHeight();

			//float heightf = width * (getDrawable().getIntrinsicWidth() / getDrawable().getIntrinsicHeight());
			float heightf = width / (w / h);
			int height = (int) heightf;
			setMeasuredDimension((int) width, height);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}