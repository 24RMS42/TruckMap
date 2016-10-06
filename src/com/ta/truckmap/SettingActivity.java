package com.ta.truckmap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ta.truckmap.rest.api.APIUtils;
import com.ta.truckmap.util.Constant;
import com.ta.truckmap.util.Utility;

public class SettingActivity extends BaseActivity implements OnClickListener {

	private TextView mTxtVwChngRds, mTxtVwLogout, mTxtVwEdtPrfle;
	private ImageView mImgVwBack;
	private Dialog mDialogShow;
	private EditText enterMilesEditText;
	private TextView mSettingTextView;
	private LinearLayout mEditPrflLinearLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		initiView();

	}

	private void initiView() {

		mTxtVwChngRds = (TextView) findViewById(R.id.setting_chng_rds_txtvw);
		mTxtVwLogout = (TextView) findViewById(R.id.setting_logout_txtvw);
		mImgVwBack = (ImageView) findViewById(R.id.finishTrip_ui_backImgView);
		mSettingTextView = (TextView) findViewById(R.id.finishtrip_ui_tv_notif);
		mTxtVwEdtPrfle = (TextView) findViewById(R.id.edit_profile_txtvw);
		mEditPrflLinearLayout = (LinearLayout) findViewById(R.id.setting_edit_prf_lnrlyt);

		if (Utility.getSharedPrefStringData(SettingActivity.this, "usertype")
				.equalsIgnoreCase("1"))

			mEditPrflLinearLayout.setVisibility(View.VISIBLE);

		else
			mEditPrflLinearLayout.setVisibility(View.GONE);

		mSettingTextView.setText("Settings");
		Utility.textViewFontRobotoLight(mSettingTextView, getAssets());
		mTxtVwChngRds.setOnClickListener(this);
		mTxtVwEdtPrfle.setOnClickListener(this);
		mTxtVwLogout.setOnClickListener(this);
		mImgVwBack.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		if (v == mTxtVwChngRds) {

			mDialogShow = new Dialog(SettingActivity.this);
			mDialogShow.requestWindowFeature(Window.FEATURE_NO_TITLE);
			mDialogShow.setContentView(R.layout.route_dialog_layout);
			mDialogShow.getWindow().setBackgroundDrawable(
					new ColorDrawable(android.graphics.Color.TRANSPARENT));
			mDialogShow.setCancelable(false);
			mDialogShow.show();
			Button sendBtn = (Button) mDialogShow
					.findViewById(R.id.route_btnSend);
			ImageView backImageView = (ImageView) mDialogShow
					.findViewById(R.id.route_cancel_image_view);
			enterMilesEditText = (EditText) mDialogShow
					.findViewById(R.id.input_radius_edit_text);
			backImageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					mDialogShow.dismiss();
				}
			});
			sendBtn.setOnClickListener(new OnClickListener() {
				String enterMilesUnit = "";

				@Override
				public void onClick(View v) {
					enterMilesUnit = enterMilesEditText.getText().toString()
							.trim();

					if (enterMilesUnit.isEmpty()) {
						Utility.showToastMessage(SettingActivity.this,
								"Please enter radius in miles!");

					}else{
						if (Utility.isNetworkAvailable(SettingActivity.this))
						{
							if(10000>=(Integer.parseInt(enterMilesEditText.getText().toString().trim()))  && 30<=(Integer.parseInt(enterMilesEditText.getText().toString().trim())))
							{
								new RadiusInMilesAsynTask(SettingActivity.this).execute(enterMilesUnit);
								
							}else
								Utility.showToastMessage(SettingActivity.this, "The Radius must be between 30 to 10000 miles");
								
							// call web service
							

						}
						else
						{
							Utility.showMsgDialog(SettingActivity.this, "Internet not Available");
						}
					}
				}

			});

		} else if (v.equals(mTxtVwEdtPrfle)) {
			Intent intent = new Intent(SettingActivity.this,
					SignUpActivityForNewUser.class);
			intent.putExtra("from_setting", true);
			startActivity(intent);
			finish();
		} else if (v == mTxtVwLogout) {

			Utility.setSharedPrefStringData(SettingActivity.this, "isPurchase",
					"false");
			Utility.setSharedPrefStringData(SettingActivity.this, "day", "2");
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					SettingActivity.this);

			alertDialogBuilder.setTitle(" "
					+ this.getResources().getString(R.string.app_name));
			alertDialogBuilder.setCancelable(false);
			alertDialogBuilder.setMessage("Are you sure you want to log out?");

			// set positive button: Yes message
			alertDialogBuilder.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// go to a new activity of the app

							Utility.setSharedPrefStringData(
									SettingActivity.this, "username", "");
							Utility.setSharedPrefStringData(
									SettingActivity.this, "password", "");
							Utility.setSharedPrefStringData(
									SettingActivity.this, "IsLogin", "");
							Intent positveActivity = new Intent(
									SettingActivity.this, LoginActivity.class);

							startActivity(positveActivity);
							SettingActivity.this.finish();
						}
					});

			// set negative button: No message
			alertDialogBuilder.setNegativeButton("No",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// cancel the alert box and put a Toast to the user
							dialog.cancel();

						}
					});

			AlertDialog alertDialog = alertDialogBuilder.create();
			// show alert
			alertDialog.show();

		} else if (v == mImgVwBack) {
			finish();
		}

	}

	public class RadiusInMilesAsynTask extends AsyncTask<String, Void, String> {
		private ProgressDialog mProgressDialogCurrentTrip;
		private Context mContext;

		public RadiusInMilesAsynTask(SettingActivity settingActivity) {
			mContext = settingActivity;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialogCurrentTrip = new ProgressDialog(mContext);
			mProgressDialogCurrentTrip.setTitle("MO-BIA");
			mProgressDialogCurrentTrip.setCancelable(false);
			mProgressDialogCurrentTrip.show();
		}

		// {"Success":true,"Message":"Record updated successfully.","Result":null}
		@Override
		protected String doInBackground(String... params) {
			String message = "";
			String response = getJsonResponse(params[0]);
			try {
				JSONObject jsonObjectResponse = new JSONObject(response);
				boolean success = jsonObjectResponse.getBoolean("Success");
				message = jsonObjectResponse.getString("Message");
				if (success) {
					return message;
				} else {
					return message;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return message;
		}

		private String getJsonResponse(String radius) {
			String response = "";
			JSONObject mJsonObject = new JSONObject();
			try {
				mJsonObject.put("UserId", Utility.getSharedPrefString(SettingActivity.this, "userId"));

				mJsonObject.put("radius", radius);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Log.e("params for first", mJsonObject.toString());
			String url = APIUtils.BASE_URL + APIUtils.UPDATERADIUS;
			response = Utility.POST(url, mJsonObject.toString(),
					APIUtils.UPDATERADIUS);
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (mProgressDialogCurrentTrip.isShowing()) {
				mProgressDialogCurrentTrip.dismiss();
			}
			if (result != null && !result.isEmpty()) {
				mDialogShow.dismiss();
				Utility.showMsgDialog(mContext, result);

			}

		}
	}

}
