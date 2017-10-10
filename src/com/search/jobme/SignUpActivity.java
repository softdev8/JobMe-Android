package com.search.jobme;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.search.jobme.until.APIManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SignUpActivity extends Activity implements OnClickListener {
	
	ImageView btnBack;
	EditText txt_email, txt_password, txt_first_name, txt_last_name;
	TextView label, txt_create_account, txt_employer_signup;
	
	Context context;
	String videochat_id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		
		context = this;
		
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
		
		txt_first_name = (EditText) findViewById(R.id.txt_first_name);
		txt_first_name.setBackgroundResource(android.R.color.transparent);
		txt_first_name.setTypeface(font);
		
		txt_last_name = (EditText) findViewById(R.id.txt_last_name);
		txt_last_name.setBackgroundResource(android.R.color.transparent);
		txt_last_name.setTypeface(font);
		
		txt_create_account = (TextView) findViewById(R.id.txt_create_account);
		txt_create_account.setOnClickListener(this);
		txt_create_account.setTypeface(font);
		
		txt_employer_signup = (TextView) findViewById(R.id.txt_employer_signup);
		txt_employer_signup.setOnClickListener(this);
		txt_employer_signup.setTypeface(font);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
			case R.id.btnBack:
				finish();
				break;
			case R.id.txt_create_account:
				if(isConfirm()) {
					
					final QBUser user = new QBUser(txt_email.getText().toString(), txt_password.getText().toString());
		        	user.setEmail(txt_email.getText().toString());
		        	user.setFullName(String.format("%s %s", txt_first_name.getText().toString(), txt_last_name.getText().toString()));
		        	
	                QBUsers.signUp(user, new QBEntityCallback<QBUser>() {
		        	    @Override
		        	    public void onSuccess(QBUser user, Bundle args) {
		        	    	videochat_id = user.getId().toString();
							new LoadSignupTask().execute();
		        	    }
		        	 
		        	    public void onError(QBResponseException errors) {
		        	    	Log.d("error", "123");
		        	    }
	
						@Override
						public void onError(List<String> arg0) {
							// TODO Auto-generated method stub
							Log.d("error", "456");
						}
	
						@Override
						public void onSuccess() {
							// TODO Auto-generated method stub
							Log.d("error", "qweqw");
						}
		        	});

				}
				break;
			case R.id.txt_employer_signup:
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://jobme.co.nz/main/signup"));
				startActivity(browserIntent);
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
		
		if(txt_password.length() < 8) {
			Toast.makeText(context, "Please enter over 8 characters",
		              Toast.LENGTH_SHORT).show();
			txt_password.requestFocus();
			return false;
		}
		if(txt_first_name.length() == 0) {
			Toast.makeText(context, "Please enter first name",
		              Toast.LENGTH_SHORT).show();
			txt_first_name.requestFocus();
			return false;
		}
		
		if(txt_last_name.length() == 0) {
			Toast.makeText(context, "Please enter last name",
		              Toast.LENGTH_SHORT).show();
			txt_last_name.requestFocus();
			return false;
		}
		
		return true;
	}
	
	@SuppressLint("NewApi")
	class LoadSignupTask extends AsyncTask<String, Integer, String> {
        private ProgressDialog progressDialog;
        String value = "";
        
        protected void onPreExecute() {
        	progressDialog = ProgressDialog.show(context, "", "Register...", true);
        }
        
        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            
            if(!result.equals("")) {
				Toast.makeText(context, result,
			              Toast.LENGTH_SHORT).show();
				finish();
				
				Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
				startActivity(intent);
            }
        }
 
        @Override
        protected String doInBackground(String... param) {
        	
	    	List<NameValuePair> params = new ArrayList<NameValuePair>();
	        params.add(new BasicNameValuePair("email", txt_email.getText().toString()));
	        params.add(new BasicNameValuePair("password", txt_password.getText().toString()));
	        params.add(new BasicNameValuePair("first_name", txt_first_name.getText().toString()));
	        params.add(new BasicNameValuePair("last_name", txt_last_name.getText().toString()));
	        params.add(new BasicNameValuePair("videochat_id", videochat_id));
	        
        	JSONObject result = null;
       		result =  APIManager.getInstance().callPost(context, "account/signup", params, false);
        	
        	try {
				
        		if(result.getString("success").equals("1")) {
        			value = "Successful";
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
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
	}
	
}

