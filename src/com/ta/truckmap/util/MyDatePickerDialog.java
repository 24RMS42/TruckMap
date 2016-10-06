package com.ta.truckmap.util;

import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

public class MyDatePickerDialog extends DatePickerDialog
{

	private Date maxDate;
	private Date minDate;
	private boolean isAllowedBefore;

	public MyDatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth, boolean isAllowedBefore)
	{
		super(context, callBack, year, monthOfYear, dayOfMonth);
		this.isAllowedBefore = isAllowedBefore;
		init(year, monthOfYear, dayOfMonth);
	}

	private void init(int year, int monthOfYear, int dayOfMonth)
	{
		Calendar cal = Calendar.getInstance();

		cal.set(year, monthOfYear, dayOfMonth);
		minDate = cal.getTime();

		cal.set(year, Calendar.JANUARY, 1);
		maxDate = cal.getTime();

		cal.set(year, monthOfYear, dayOfMonth);
	}

	public void onDateChanged(final DatePicker view, int year, int month, int day)
	{
		Calendar cal1 = Calendar.getInstance();
		Date currentDate = cal1.getTime();

		Calendar resetCal = Calendar.getInstance();
		resetCal.set(year, month, day);
		//	    final Calendar resetCal = cal; 

		if (!isAllowedBefore && resetCal.before(cal1))
		{
			view.updateDate(cal1.get(Calendar.YEAR), cal1.get(Calendar.MONTH), cal1.get(Calendar.DAY_OF_MONTH));
		}
		//	    else if(resetCal.before(currentDate)){
		//	        cal.setTime(resetCal.getTime());
		//	        view.updateDate(resetCal.get(Calendar.YEAR), resetCal.get(Calendar.MONTH), resetCal.get(Calendar.DAY_OF_MONTH));
		//	    } 
		//	    else
		//	    {
		//	    	cal.setTime(resetCal.getTime());
		//	        view.updateDate(resetCal.get(Calendar.YEAR), resetCal.get(Calendar.MONTH), resetCal.get(Calendar.DAY_OF_MONTH));
		//	    }
	}

	public void setMaxDate(Date date)
	{
		this.maxDate = date;
	}

	public void setMinDate(Date date)
	{
		this.minDate = date;
	}

}
