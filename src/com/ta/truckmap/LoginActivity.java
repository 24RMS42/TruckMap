package com.ta.truckmap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ta.truckmap.rest.api.APIUtils;
import com.ta.truckmap.util.CustomProgressDialog;
import com.ta.truckmap.util.Utility;
import com.truckmap.parsers.LoginParser;
import com.truckmap.parsers.Parser;

public class LoginActivity extends Activity implements View.OnClickListener
{

	Button mButtonLogin, mButtonSignUpAsNewUser, mForgetButton, mSignUpAsACompany;
	EditText mlogin_email, mlogin_password;
	TextView mforgetPasswordText;
	EditText mforgetEditText;
	Dialog mDialogShow;
	ImageView closeForget;
	TextView forgetPassword_tvline1, forgetPassword_tvline2, forgetPassword_tvline3;
	protected Parser<?> parser;
	String email, pass;
	Context mContext;
	protected static final int CLOSE_ACTIVITY_ASYNC = 0;
	protected static final int SHOW_DAILOG_LOGIN = 1;
	Handler mHandler;
	Handler mLoginHandler = new Handler();
	Handler mForgotHandler = new Handler();
	CustomProgressDialog mCustomProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome_ui);
		mContext = this;
		SplashActivity.regId = Utility.getSharedPrefStringData(LoginActivity.this, "regId");
		initiViewReference();
		setFontFamily();

	}

	private void setFontFamily()
	{
		Utility.textViewFontRobotoLight(mlogin_email, getAssets());
		Utility.textViewFontRobotoLight(mlogin_password, getAssets());
		Utility.textViewFontRobotoLight(mforgetPasswordText, getAssets());
		Utility.textViewFontRobotoLight(mButtonLogin, getAssets());
		Utility.textViewFontRobotoLight(mButtonSignUpAsNewUser, getAssets());
		Utility.textViewFontRobotoLight(mSignUpAsACompany, getAssets());
	}

	private void initiViewReference()
	{
		mButtonLogin = (Button) findViewById(R.id.btn_Login);
		mButtonSignUpAsNewUser = (Button) findViewById(R.id.btn_SignUp_as_new_user);

		mlogin_email = (EditText) findViewById(R.id.ed_email);
		mlogin_password = (EditText) findViewById(R.id.ed_password);
		mSignUpAsACompany = (Button) findViewById(R.id.btn_SignUp_as_company);
		mforgetPasswordText = (TextView) findViewById(R.id.tv_forgetPassword);

		mButtonLogin.setOnClickListener(this);
		mButtonSignUpAsNewUser.setOnClickListener(this);
		mforgetPasswordText.setOnClickListener(this);
		mSignUpAsACompany.setOnClickListener(this);
		mButtonSignUpAsNewUser.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub

		if (v == mSignUpAsACompany)
		{
			startActivity(new Intent(LoginActivity.this, SignUpActivityForCompany.class));

		}
		else
			if (v == mButtonSignUpAsNewUser)
			{
				startActivity(new Intent(LoginActivity.this, SignUpActivityForNewUser.class));
			}

			else
				if (v == mforgetPasswordText)
				{

					// Forget password Screen

					/**
					 * @author nipun
					 * 
					 */
					mDialogShow = new Dialog(LoginActivity.this);

					mDialogShow.requestWindowFeature(Window.FEATURE_NO_TITLE);
					mDialogShow.setContentView(R.layout.forgetpassword_ui);
					mDialogShow.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
					mDialogShow.setCancelable(false);
					mforgetEditText = (EditText) mDialogShow.findViewById(R.id.forgetPassword_ed_EmailId);
					mForgetButton = (Button) mDialogShow.findViewById(R.id.forgetPassword_ui_btnSend);
					forgetPassword_tvline1 = (TextView) mDialogShow.findViewById(R.id.tvLine1);
					forgetPassword_tvline2 = (TextView) mDialogShow.findViewById(R.id.tvLine2);
					forgetPassword_tvline3 = (TextView) mDialogShow.findViewById(R.id.tvLine3);
					Utility.textViewFontRobotoLight(mforgetPasswordText, getAssets());
					Utility.textViewFontRobotoLight(mForgetButton, getAssets());
					Utility.textViewFontRobotoLight(forgetPassword_tvline1, getAssets());
					Utility.textViewFontRobotoLight(forgetPassword_tvline2, getAssets());
					Utility.textViewFontRobotoLight(forgetPassword_tvline3, getAssets());
					Utility.textViewFontRobotoLight(mforgetEditText, getAssets());

					mDialogShow.show();

					// Click on Forget Password Button

					mForgetButton.setOnClickListener(new OnClickListener()
					{

						@Override
						public void onClick(View v)
						{
							if (Utility.isFieldEmpty(mforgetEditText.getText().toString().trim()))
							{
								Utility.showMsgDialog(LoginActivity.this, "Please enter your email id");
							}
							else
							{

								if (Utility.isEmailValid(mforgetEditText.getText().toString().trim()))
								{
									getPassword();
									mDialogShow.dismiss();

								}

								else
								{
									Utility.showMsgDialog(LoginActivity.this, "Invalid Format");
								}
							}

						}
					});

					// Close Forgot Password Screen

					closeForget = (ImageView) mDialogShow.findViewById(R.id.forgot_password_relative_ImageViewCancel);

					closeForget.setOnClickListener(new View.OnClickListener()
					{

						@Override
						public void onClick(View v)
						{

							mDialogShow.dismiss();
						}
					});
				}

				else
					if (v == mButtonLogin)
					{
						email = mlogin_email.getText().toString();
						pass = mlogin_password.getText().toString();

						if (email.equals(""))
						{
							Utility.showMsgDialog(LoginActivity.this, "Please enter email id.");
						}
						else
							if (!email.matches("^([a-zA-Z0-9_.-])+@([a-zA-Z0-9_.-])+\\.([a-zA-Z])+([a-zA-Z])+"))
							{
								Utility.showMsgDialog(LoginActivity.this, "Please enter valid email id.");
							}

							else
								if (pass.equals(""))
								{
									Utility.showMsgDialog(LoginActivity.this, "Please enter password.");
								}

								// else
								// if (pass.length() < 6)
								// {
								// Utility.showMsgDialog(LoginActivity.this,
								// "Please enter password of length more than 5 characters.");
								// }
								else
								{
									getLogin();
								}
					}
	}

	private void getLogin()
	{
		mCustomProgressDialog = new CustomProgressDialog(LoginActivity.this, "Please wait");
		mCustomProgressDialog.setCancelable(false);
		new Thread(new Runnable()
		{

			String response = "";

			@Override
			public void run()
			{

				JSONObject loginJsonObject = new JSONObject();
				try
				{

					loginJsonObject.put("Email", mlogin_email.getText().toString().trim());
					loginJsonObject.put("Password", mlogin_password.getText().toString().trim());
					loginJsonObject.put("DeviceToken", SplashActivity.regId);

				}
				catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.e("params for first", loginJsonObject.toString());
				String url = APIUtils.BASE_URL + APIUtils.LOGIN;
				response = Utility.POST(url, loginJsonObject.toString(), APIUtils.LOGIN);
				Log.e("response", response.toString());
				mLoginHandler.post(new Runnable()
				{

					private String msg;

					@Override
					public void run()
					{
						mCustomProgressDialog.dismissDialog();

						if (response != null && response.length() > 0)
						{
							Log.e("msg", "is:" + response);

							parser = new LoginParser();
							try
							{
								parser.parse(new JSONObject(response));
							}
							catch (JSONException e1)
							{
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}

							try
							{
								JSONObject responseJsonObject = new JSONObject(response);
								if (responseJsonObject.optBoolean("Success"))
								{
									msg = responseJsonObject.optString("Message");
									JSONObject result = responseJsonObject.getJSONObject("Result");

									if (result.optString("UserType").equalsIgnoreCase("1"))
									{
										Utility.setResponseDataInshrdPref(mContext, responseJsonObject);
									}
									else
									{
										Utility.setSharedPrefStringData(LoginActivity.this, "userId", result.optString("Userid"));
										Utility.setSharedPrefStringData(LoginActivity.this, "usertype", result.getString("UserType"));
									}
									Utility.setSharedPrefStringData(LoginActivity.this, "day", "1");
									Utility.setSharedPrefStringData(LoginActivity.this, "username", mlogin_email.getText().toString().trim());
									Utility.setSharedPrefStringData(LoginActivity.this, "password", mlogin_password.getText().toString().trim());
									Utility.setSharedPrefStringData(LoginActivity.this, "IsLogin", "true");

									Intent startProfileActivity = new Intent(LoginActivity.this, HomeActivity.class);
									startActivity(startProfileActivity);
									LoginActivity.this.finish();

								}
								else
								{
									msg = responseJsonObject.optString("Message");
									Utility.showMsgDialog(LoginActivity.this, msg);
								}

							}
							catch (JSONException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}

					}
				});

			}
		}).start();

	}

	private void getPassword()
	{
		mCustomProgressDialog = new CustomProgressDialog(LoginActivity.this, "");
		mCustomProgressDialog.setCancelable(false);
		new Thread(new Runnable()
		{

			String response = "";

			@Override
			public void run()
			{

				JSONObject forgetJsonObject = new JSONObject();
				try
				{

					forgetJsonObject.put("Email", mforgetEditText.getText().toString().trim());
					forgetJsonObject.put("DeviceToken", "AxygtehsksT");

				}
				catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.e("params for first", forgetJsonObject.toString());
				String url = APIUtils.BASE_URL + APIUtils.FORGETPASSWORD;
				response = Utility.POST(url, forgetJsonObject.toString(), APIUtils.FORGETPASSWORD);
				Log.e("response", response.toString());
				mForgotHandler.post(new Runnable()
				{

					private String msg;

					@Override
					public void run()
					{
						mCustomProgressDialog.dismissDialog();

						if (response != null && response.length() > 0)
						{
							Log.e("msg", "is:" + response);
							try
							{
								JSONObject responseJsonObject = new JSONObject(response);
								if (responseJsonObject.optBoolean("Success"))
								{
									msg = responseJsonObject.optString("Message");
									// via vikash
									Utility.setResponseDataInshrdPref(mContext, responseJsonObject);

									Utility.showMsgDialog(LoginActivity.this, msg);
									mDialogShow.dismiss();
								}
								else
								{
									msg = responseJsonObject.optString("Message");
									Utility.showMsgDialog(LoginActivity.this, msg);
								}

							}
							catch (JSONException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}

					}
				});

			}
		}).start();

	}
}