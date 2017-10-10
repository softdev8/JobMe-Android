package com.search.jobme;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.search.jobme.UpdateEmail.LoadUpdateEmailTask;
import com.search.jobme.until.APIManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UpdatePassword extends Activity implements OnClickListener {

	ImageView btnBack;
	EditText txt_password;
	TextView txt_save, label;

	Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_password_change);
		
		context = this;
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		btnBack = (ImageView) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(this);
		
		txt_password = (EditText) findViewById(R.id.txt_password);
		txt_password.setBackgroundResource(android.R.color.transparent);
		
		txt_save = (TextView) findViewById(R.id.txt_save);
		txt_save.setOnClickListener(this);
		
		String email = prefs.getString("email", null);
		label = (TextView) findViewById(R.id.label);
		label.setText(String.format("current email : %s", email));
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.btnBack:
				finish();
				break;
			case R.id.txt_save:
				if(txt_password.length() == 0) {
					Toast.makeText(this, "Please enter password",
				              Toast.LENGTH_SHORT).show();
					txt_password.requestFocus();
				} else {
					new LoadUpdatePasswordTask().execute();
				}
				break;
		}
	}
	
	class LoadUpdatePasswordTask extends AsyncTask<String, Integer, String> {
        private ProgressDialog progressDialog;
        String value = "";
        
        protected void onPreExecute() {
        	progressDialog = ProgressDialog.show(context, "", "Update...", true);
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
	        params.add(new BasicNameValuePair("new_password", txt_password.getText().toString()));
	        
        	JSONObject result = null;
       		result =  APIManager.getInstance().callPost(context, "account/update_password", params, true);
        	
        	try {
				
        		if(result.getString("success").equals("1")) {
        			value = "Update Successful";
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
