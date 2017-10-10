package com.search.jobme;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.search.jobme.model.Education;
import com.search.jobme.model.Experience;
import com.search.jobme.until.APIManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends Activity implements OnClickListener {
	
	private static final int				FACEBOOK_SHARE 	= 1;
	
	ImageView btnClose;
	RelativeLayout email_layout, password_layout, logout_layout, delete_layout;
	TextView txt_like, txt_new, txt_match;
	ImageView btnLikeEmail, btnLikePhone, btnNewEmail, btnNewPhone, btnMatchEmail, btnMatchPhone;
	
	Context context;
	SharedPreferences prefs;
	SharedPreferences.Editor e;
	
	int bLikeEmail=1, bLikePhone=1, bNewEmail=1, bNewPhone=1, bMatchEmail=1, bMatchPhone=1;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		
		context = this;
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		e = prefs.edit();
		
		String is_employer = prefs.getString("is_employer", "0");
		
		btnClose = (ImageView) findViewById(R.id.btnClose);
		btnClose.setOnClickListener(this);
		
		email_layout = (RelativeLayout) findViewById(R.id.email_layout);
		email_layout.setOnClickListener(this);
		
		password_layout = (RelativeLayout) findViewById(R.id.password_layout);
		password_layout.setOnClickListener(this);
		
		logout_layout = (RelativeLayout) findViewById(R.id.logout_layout);
		logout_layout.setOnClickListener(this);
		
		delete_layout = (RelativeLayout) findViewById(R.id.delete_layout);
		delete_layout.setOnClickListener(this);
		
		txt_like = (TextView) findViewById(R.id.txt_like);
		txt_new = (TextView) findViewById(R.id.txt_new);
		txt_match = (TextView) findViewById(R.id.txt_match);
		
		btnLikeEmail = (ImageView) findViewById(R.id.btnLikeEmail);
		btnLikeEmail.setOnClickListener(this);
		
		btnLikePhone = (ImageView) findViewById(R.id.btnLikePhone);
		btnLikePhone.setOnClickListener(this);
		
		btnNewEmail = (ImageView) findViewById(R.id.btnNewEmail);
		btnNewEmail.setOnClickListener(this);
		
		btnNewPhone = (ImageView) findViewById(R.id.btnNewPhone);
		btnNewPhone.setOnClickListener(this);
		
		btnMatchEmail = (ImageView) findViewById(R.id.btnMatchEmail);
		btnMatchEmail.setOnClickListener(this);
		
		btnMatchPhone = (ImageView) findViewById(R.id.btnMatchPhone);
		btnMatchPhone.setOnClickListener(this);
		
		if (is_employer.equals("1")) {
	        txt_like.setText("Likes from Candidates");
	        txt_new.setText("New Candidates");
	    } else {
	    	txt_like.setText("Likes from Employers");
	        txt_new.setText("New Jobs");
	    }		
		
		if(prefs.getString("like_email", "0").equals("0")) {
			bLikeEmail = 0;
			btnLikeEmail.setImageResource(R.drawable.notification_email_1);
		} else {
			btnLikeEmail.setImageResource(R.drawable.notification_email);
		}
		
		if(prefs.getString("like_push", "0").equals("0")) {
			bLikePhone = 0;
			btnLikePhone.setImageResource(R.drawable.notification_mobile_1);
		} else {
			btnLikePhone.setImageResource(R.drawable.notification_mobile);
		}
		
		if(prefs.getString("talent_email", "0").equals("0")) {
			bNewEmail = 0;
			btnNewEmail.setImageResource(R.drawable.notification_email_1);
		} else {
			btnNewEmail.setImageResource(R.drawable.notification_email);
		}
		
		if(prefs.getString("talent_push", "0").equals("0")) {
			bNewPhone = 0;
			btnNewPhone.setImageResource(R.drawable.notification_mobile_1);
		} else {
			btnNewPhone.setImageResource(R.drawable.notification_mobile);
		}

		if(prefs.getString("match_email", "0").equals("0")) {
			bMatchEmail = 0;
			btnMatchEmail.setImageResource(R.drawable.notification_email_1);
		} else {
			btnMatchEmail.setImageResource(R.drawable.notification_email);
		}
		
		if(prefs.getString("match_push", "0").equals("0")) {
			bMatchPhone = 0;
			btnMatchPhone.setImageResource(R.drawable.notification_mobile_1);
		} else {
			btnMatchPhone.setImageResource(R.drawable.notification_mobile);
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch(v.getId()) {
			case R.id.btnClose:
				finish();
				break;
			case R.id.email_layout:
				startActivity(new Intent(this, UpdateEmail.class));
				break;
			case R.id.password_layout:
				startActivity(new Intent(this, UpdatePassword.class));
				break;
			case R.id.logout_layout:
				sendLogoutRequest();
				break;
			case R.id.delete_layout:
				
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
		        builder.setMessage("Are you sure you want to delete your account?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {

		               sendDeleteRequest();
                	   finish();
                   }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // User cancelled the dialog
                	   
                   }
                });
		        // Create the AlertDialog object and return it
		        AlertDialog alert = builder.create();
		        alert.show();
				
				break;
			case R.id.btnLikeEmail:
				if (bLikeEmail == 1) {
					bLikeEmail = 0;
			        btnLikeEmail.setImageResource(R.drawable.notification_email_1);
			    } else {
			    	bLikeEmail = 1;
			    	btnLikeEmail.setImageResource(R.drawable.notification_email);
			    }
			    
				e.putString("like_email", String.valueOf(bLikeEmail));
				e.commit();
				
				sendNotification("setting/like_email/", String.valueOf(bLikeEmail));
				break;
			case R.id.btnLikePhone:
				if (bLikePhone == 1) {
					bLikePhone = 0;
			        btnLikePhone.setImageResource(R.drawable.notification_mobile_1);
			    } else {
			    	bLikePhone = 1;
			    	btnLikePhone.setImageResource(R.drawable.notification_mobile);
			    }
			    
				e.putString("like_push", String.valueOf(bLikePhone));
				e.commit();
				
				sendNotification("setting/like_push/", String.valueOf(bLikePhone));
				break;
			case R.id.btnNewEmail:
				if (bNewEmail == 1) {
					bNewEmail = 0;
			        btnNewEmail.setImageResource(R.drawable.notification_email_1);
			    } else {
			    	bNewEmail = 1;
			    	btnNewEmail.setImageResource(R.drawable.notification_email);
			    }
			    
				e.putString("talent_email", String.valueOf(bNewEmail));
				e.commit();
				
				sendNotification("setting/talent_email/", String.valueOf(bNewEmail));
				break;
			case R.id.btnNewPhone:
				if (bNewPhone == 1) {
					bNewPhone = 0;
			        btnNewPhone.setImageResource(R.drawable.notification_mobile_1);
			    } else {
			    	bNewPhone = 1;
			    	btnNewPhone.setImageResource(R.drawable.notification_mobile);
			    }
			    
				e.putString("talent_push", String.valueOf(bNewPhone));
				e.commit();
				
				sendNotification("setting/talent_push/", String.valueOf(bNewPhone));
				break;
			case R.id.btnMatchEmail:
				if (bMatchEmail == 1) {
					bMatchEmail = 0;
			        btnMatchEmail.setImageResource(R.drawable.notification_email_1);
			    } else {
			    	bMatchEmail = 1;
			    	btnMatchEmail.setImageResource(R.drawable.notification_email);
			    }
			    
				e.putString("match_email", String.valueOf(bMatchEmail));
				e.commit();
				
				sendNotification("setting/match_email/", String.valueOf(bMatchEmail));
				break;
			case R.id.btnMatchPhone:
				if (bMatchPhone == 1) {
					bMatchPhone = 0;
			        btnMatchPhone.setImageResource(R.drawable.notification_mobile_1);
			    } else {
			    	bMatchPhone = 1;
			    	btnMatchPhone.setImageResource(R.drawable.notification_mobile);
			    }
			    
				e.putString("match_push", String.valueOf(bMatchPhone));
				e.commit();
				
				sendNotification("setting/match_push/", String.valueOf(bMatchPhone));
				break;
		}
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		
		if(resultCode == Activity.RESULT_OK) {
			if(requestCode == FACEBOOK_SHARE) {
				Log.d("", "");
			}
		}
	}
	
	void sendNotification(String api_url, String param) {
		String link = api_url + param;
		new LoadNotificationTask().execute(link);
	}
	
	void sendLogoutRequest() {
//		if(prefs.getString("facebook", "").equals("")) {
			new LoadLogoutTask().execute();
//		} else {
//			
//			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//            SharedPreferences.Editor e = prefs.edit();
//			
//			e.putString("Access-Token", null);
//			e.putString("Device-Id", null);
//			e.putString("address", "");
//			e.commit();
//			
//			startActivity(new Intent(SettingActivity.this, SignActivity.class));
//		}
	}
	
	void sendDeleteRequest() {
		new LoadDeleteTask().execute();
	}
	
	class LoadNotificationTask extends AsyncTask<String, Integer, String> {
        String value = "";
        
        protected void onPreExecute() {
        	
        }
        
        @Override
        protected void onPostExecute(String result) {
            
        }
 
        @Override
        protected String doInBackground(String... param) {
        	
       		APIManager.getInstance().callPost(context, param[0], null, true);
        	
            return value;
        }
    }
	
	class LoadLogoutTask extends AsyncTask<String, Integer, String> {
        private ProgressDialog progressDialog;
        String value = "";
        
        protected void onPreExecute() {
        	progressDialog = ProgressDialog.show(context, "", "Logout...", true);
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
        	
        	JSONObject result = null;
       		result =  APIManager.getInstance().callPost(context, "account/logout", null, true);
        	
        	try {
				
        		if(result.getString("success").equals("1")) {
        			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor e = prefs.edit();
    				
    				e.putString("Access-Token", null);
    				e.putString("Device-Id", null);
    				e.putString("address", "");
    				e.commit();
    				
    				startActivity(new Intent(SettingActivity.this, SignActivity.class));
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
	
	class LoadDeleteTask extends AsyncTask<String, Integer, String> {
        private ProgressDialog progressDialog;
        String value = "";
        
        protected void onPreExecute() {
        	progressDialog = ProgressDialog.show(context, "", "Deleting...", true);
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
        	
        	JSONObject result = null;
       		result =  APIManager.getInstance().callPost(context, "account/delete_account", null, true);
        	
        	try {
				
        		if(result.getString("success").equals("1")) {
        			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor e = prefs.edit();
    				
    				e.putString("Access-Token", null);
    				e.putString("Device-Id", null);
    				e.commit();
    				
    				startActivity(new Intent(SettingActivity.this, SignActivity.class));
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
