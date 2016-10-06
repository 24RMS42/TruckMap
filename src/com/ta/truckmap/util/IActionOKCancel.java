package com.ta.truckmap.util;

import java.io.Serializable;

public interface IActionOKCancel extends Serializable {
	public void onActionOk(int requestCode);

	public void onActionCancel(int requestCode);
	public void onActionNeutral(int requestCode);
}
