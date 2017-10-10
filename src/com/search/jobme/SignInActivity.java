package com.search.jobme;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.search.jobme.ManageFragment.LoadProductTask;
import com.search.jobme.ManageFragment.SearchAdapter;
import com.search.jobme.model.Manage;
import com.search.jobme.until.APIManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SignInActivity extends Activity implements OnClickListener {
	
	ImageView btnBack;
	EditText txt_email, txt_password;
	TextView label, txt_login;
	
	Context context;
	
	GoogleCloudMessaging gcm;
	String regID = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		context = this;
		
		registerInBackground();
		
		Typeface font = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeue_Lt.ttf");
		
		btnBack = (ImageView) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(this);
		
		label = (TextView) findViewById(R.id.label);
		label.setTypeface(font);

		txt_email = (EditText) findViewById(R.id.txt_email);
		txt_email.setBackgroundResource(android.R.color.transparent);
		txt_email.setTypeface(font);
		
		txt_password = (EditText) findViewById(R.id.txt_password);
		txt_password.setBackgroundResource(android.R.color.transparent);
		txt_password.setTypeface(font);
		
		txt_login = (TextView) findViewById(R.id.txt_login);
		txt_login.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
			case R.id.btnBack:
				finish();
				break;
			case R.id.txt_login:
				if(isConfirm()) {
					new LoadProductTask().execute();
				}
				break;
		}
	}
	
	private boolean isConfirm() {

		if(txt_email.length() == 0) {
			Toast.makeText(context, "Please enter email",
		              Toast.LENGTH_SHORT).show();
			txt_email.requestFocus();
			return false;
		}
		if(txt_password.length() == 0) {
			Toast.makeText(context, "Please enter password",
		              Toast.LENGTH_SHORT).show();
			txt_password.requestFocus();
			return false;
		}

		return true;
	}
	
	class LoadProductTask extends AsyncTask<String, Integer, String> {
        private ProgressDialog progressDialog;
        String value = "";
        
        protected void onPreExecute() {
        	progressDialog = ProgressDialog.show(context, "", "Login...", true);
        }
        
        @Override
        protected void onPostExecute(String result) {
    		
            progressDialog.dismiss();
            
            if(!result.equals("")) {
				Toast.makeText(context, result,
			              Toast.LENGTH_SHORT).show();
            }
        }
 
        @Override
        protected String doInBackground(String... param) {
        	
        	List<NameValuePair> params = new ArrayList<NameValuePair>();
	        params.add(new BasicNameValuePair("email", txt_email.getText().toString()));
	        params.add(new BasicNameValuePair("password", txt_password.getText().toString()));
	        params.add(new BasicNameValuePair("device_id", regID));
	        params.add(new BasicNameValuePair("device_type", "android"));
	        
        	JSONObject result = null;
       		result =  APIManager.getInstance().callPost(context, "account/login", params, false);
        	
        	try {
				
        		if(result.getString("success").equals("1")) {
					
        			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
	                SharedPreferences.Editor e = prefs.edit();
	                
	                e.putString("Access-Token", result.getString("Access-Token"));
	                e.putString("Device-Id", result.getString("Device-Id"));
	                e.putString("avatar", result.getString("avatar"));
	                
	                JSONObject userinfo = result.getJSONObject("userinfo");
	                
	                e.putString("uid", userinfo.getString("id"));
	                e.putString("email", userinfo.getString("email"));
	                e.putString("first_name", userinfo.getString("first_name"));
	                e.putString("last_name", userinfo.getString("last_name"));
	                e.putString("company_name", userinfo.getString("company"));
	                e.putString("is_employer", userinfo.getString("is_company"));
	                e.putString("videochat_id", userinfo.getString("videochat_id"));
	                e.putString("password", txt_password.getText().toString());
	                
	                if(userinfo.getString("education_ids").equals("")) {
	                	e.putString("education_ids", "");
	                } else {
	                	e.putString("education_ids", userinfo.getString("education_ids"));
	                }
	                if(userinfo.getString("experience_ids").equals("")) {
	                	e.putString("experience_ids", "");
	                } else {
	                	e.putString("experience_ids", userinfo.getString("experience_ids"));
	                }
	                
                    JSONArray arr_experience = userinfo.getJSONArray("experience");
                    if(arr_experience.length() > 0) {
                    	JSONObject obj = arr_experience.getJSONObject(arr_experience.length() - 1);
                    	e.putString("last_company", obj.getString("company"));
                    }
                    
	                e.putString("experience_year", userinfo.getString("experience_year"));
	                e.putString("headline", userinfo.getString("headline"));
	                e.putString("skill_ids", userinfo.getString("skill_ids"));
	                e.putString("job_function", userinfo.getString("main_cat_id"));
	                e.putString("location", userinfo.getString("location"));
	                
	                JSONObject settings = result.getJSONObject("setting");
	                e.putString("like_push", settings.getString("like_push"));
	                e.putString("like_email", settings.getString("like_email"));
	                e.putString("talent_push", settings.getString("talent_push"));
	                e.putString("talent_email", settings.getString("talent_email"));
	                e.putString("match_push", settings.getString("match_push"));
	                e.putString("match_email", settings.getString("match_email"));
	                
	                e.commit();
	                
	                startActivity(new Intent(SignInActivity.this, MainActivity.class));
        		} else {
        			value = result.getString("message");
        		}
        	} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

            return value;
        }
    }
	
	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regID = gcm.register(Constants.GOOGLE_SENDER_ID);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					Log.d("RegisterActivity", "Error: " + msg);
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {

			}
		}.execute(null, null, null);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
	}
}
