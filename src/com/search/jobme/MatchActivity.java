package com.search.jobme;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.users.model.QBUser;
import com.search.jobme.model.Education;
import com.search.jobme.model.Experience;
import com.search.jobme.model.UserInfo;
import com.search.jobme.until.APIManager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class MatchActivity extends Activity implements OnClickListener {
	
	private static final String TAG = "MatchActivity";
	
	CircularImageView receiver_picture, sender_picture;
	TextView txt_message, txt_company_info;
	TextView txt_sender_username, txt_headline, txt_last_company;
	TextView txt_receiver_username, txt_receiver_headline, txt_company_name;
	TextView txt_chat, txt_keep;
	
	String message, receiver_id, receiver_photo, receiver_name, company_name, headline, job_main_cat_id, last_company;
	
	public static UserInfo user_info;
	ArrayList<Experience> experience_data = new ArrayList<Experience>();
	ArrayList<Education> education_data = new ArrayList<Education>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.match_activity);
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		Intent intent = getIntent();
		message = intent.getStringExtra("message");
		receiver_id = intent.getStringExtra("sender_id");
		receiver_photo = intent.getStringExtra("sender_photo");
		receiver_name = intent.getStringExtra("sender_name");
		company_name = intent.getStringExtra("company_name");
		headline = intent.getStringExtra("headline");
		job_main_cat_id = intent.getStringExtra("job_main_cat_id");
		last_company = intent.getStringExtra("last_company");
		
    	String str_message = "Does this sound like you?";
    	if(prefs.getString("is_employer", "0").equals("1")) {
    		str_message = "We have found a match for your listing.";
    	}
		txt_message = (TextView) findViewById(R.id.txt_message);
		txt_message.setText(str_message);
		
		String str_company = (prefs.getString("is_employer", "0").equals("1")) ? prefs.getString("company_name", "") :  company_name;
		txt_company_info = (TextView) findViewById(R.id.txt_company_info);
		txt_company_info.setText(String.format("%s at %s", job_main_cat_id, str_company));

		sender_picture = (CircularImageView) findViewById(R.id.sender_picture);
		txt_sender_username = (TextView) findViewById(R.id.txt_sender_username);
		txt_headline = (TextView) findViewById(R.id.txt_headline);
		txt_last_company = (TextView) findViewById(R.id.txt_last_company);
		
		receiver_picture = (CircularImageView) findViewById(R.id.receiver_picture);
		txt_receiver_username = (TextView) findViewById(R.id.txt_receiver_username);
		txt_receiver_headline = (TextView) findViewById(R.id.txt_receiver_headline);
		txt_company_name = (TextView) findViewById(R.id.txt_company_name);
		
		if(prefs.getString("is_employer", "0").equals("1")) {	//employer login
			String avatar_url = String.format("%s%s", Constants.PHOTO_UPLOAD, receiver_photo);
	    	UrlImageViewHelper.setUrlDrawable(sender_picture, avatar_url);
	    	txt_sender_username.setText(receiver_name);
	    	txt_headline.setText(headline);
	    	txt_last_company.setText(last_company);
			
			avatar_url = String.format("%s%s", Constants.PHOTO_UPLOAD, prefs.getString("avatar", ""));
	    	UrlImageViewHelper.setUrlDrawable(receiver_picture, avatar_url);
			txt_receiver_username.setText(String.format("%s %s", prefs.getString("first_name", ""),  prefs.getString("last_name", "")));
			txt_receiver_headline.setText(prefs.getString("headline", ""));
			txt_company_name.setText(prefs.getString("company_name", ""));
			
			getMatchUser(receiver_id);
			
		} else {
			String avatar_url = String.format("%s%s", Constants.PHOTO_UPLOAD, prefs.getString("avatar", ""));
	    	UrlImageViewHelper.setUrlDrawable(sender_picture, avatar_url);
	    	txt_sender_username.setText(String.format("%s %s", prefs.getString("first_name", ""),  prefs.getString("last_name", "")));
	    	txt_headline.setText(prefs.getString("headline", ""));
	    	txt_last_company.setText(prefs.getString("last_company", ""));
			
			avatar_url = String.format("%s%s", Constants.PHOTO_UPLOAD, receiver_photo);
	    	UrlImageViewHelper.setUrlDrawable(receiver_picture, avatar_url);
			txt_receiver_username.setText(receiver_name);
			txt_receiver_headline.setText(headline);
			txt_company_name.setText(company_name);
			
			getMatchUser(receiver_id);
		}
		
		txt_chat = (TextView) findViewById(R.id.txt_chat);
		txt_chat.setText(String.format("Chat with %s", receiver_name));
		txt_chat.setOnClickListener(this);
		
		txt_keep = (TextView) findViewById(R.id.txt_keep);
		txt_keep.setOnClickListener(this);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		switch(v.getId()) {
			case R.id.txt_chat:
				
				String qlogin = prefs.getString("email", "");
				String qpassword = prefs.getString("password", "");
				
				createSession(qlogin, qpassword, user_info, 0);				
				break;
			case R.id.txt_keep:
				finish();
				break;
		}
	}
	
	private void startCallActivity(UserInfo info, int index) {
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		Intent intent = new Intent(this, ChatActivity.class);
		intent.putExtra("receiver_id", receiver_id);
		intent.putExtra("receiver_user_avatar", receiver_photo);
		intent.putExtra("receiver_name", receiver_name);
		
		if(prefs.getString("is_employer", "0").equals("1")) {
			intent.putExtra("headline", info.getHeadline());
		} else {
			intent.putExtra("headline", info.getCompany());
		}
		intent.putExtra("online", info.getOnline());
		intent.putExtra("qlogin", info.getEmail());
		intent.putExtra("videochat_id", info.getVideochat_id());
		intent.putExtra("match", 1);
		
		startActivity(intent);
	}
	
	private void getMatchUser(String matched_user_id) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
    	params.add(new BasicNameValuePair("matched_user_id", matched_user_id));
    	
    	String api_url = "get_match_user";
    	
    	JSONObject result = null;
   		result = APIManager.getInstance().callPost(this, api_url, params, true);
   		
   		try {
    		
    		if(result.getString("success").equals("1")) {
    			JSONArray arr = result.getJSONArray("match_user");
    			
    			user_info =  new UserInfo();
    			
    			JSONObject c = arr.getJSONObject(0);
				
    			user_info.setUser_id(c.getString("id"));
    			user_info.setFirst_name(c.getString("first_name"));
    			user_info.setLast_name(c.getString("last_name"));
    			user_info.setEmail(c.getString("email"));
    			user_info.setCompany(c.getString("company"));
    			user_info.setCompany_avatar(c.getString("companyAvatar"));
    			user_info.setCompany_description(c.getString("companyDescr"));
    			user_info.setAvatar(c.getString("avatar"));
    			user_info.setHeadline(c.getString("headline"));
    			user_info.setLocation(c.getString("location"));
    			user_info.setLatitude(c.getString("lat"));
    			user_info.setLongitude(c.getString("lot"));
    			user_info.setMain_cat_id(c.getString("main_cat_id"));
    			user_info.setSub_cat_id(c.getString("sub_cat_id"));
    			user_info.setExperience_year(c.getString("experience_year"));
    			user_info.setVideochat_id(c.getString("videochat_id"));
				
				JSONArray arr_experience = c.getJSONArray("experience");
				for(int j = 0 ; j < arr_experience.length() ; j++) {
					JSONObject b = arr_experience.getJSONObject(j);
					
					Experience item = new Experience();
					item.setExp_id(b.getString("id"));
					item.setHeadline(b.getString("job_title"));
					item.setCompany(b.getString("company"));
					item.setStart_date(getDate(b.getString("start_date")));
					if(b.getString("end_date").equals("Present")) {
						item.setEnd_date(b.getString("end_date"));
					} else {
						item.setEnd_date(getDate(b.getString("end_date")));
					}
					experience_data.add(item);
				}
				
				user_info.setExperience(experience_data);
				
				JSONArray arr_education = c.getJSONArray("education");
				for(int j = 0 ; j < arr_education.length() ; j++) {
					JSONObject b = arr_education.getJSONObject(j);
					
					Education item = new Education();
					item.setEdu_id(b.getString("id"));
					item.setSchool(b.getString("school_name"));
					item.setDegree(b.getString("degree"));
					item.setField(b.getString("field_study"));
					item.setStart_date(getDate(b.getString("start_date")));
					if(b.getString("end_date").equals("Present")) {
						item.setEnd_date(b.getString("end_date"));
					} else {
						item.setEnd_date(getDate(b.getString("end_date")));
					}
					education_data.add(item);
				}
				
				user_info.setEducation(education_data);
				
				user_info.setSkill_ids(c.getString("skill_ids"));
				user_info.setOnline(c.getString("online"));
				user_info.setStatus(c.getString("status"));
				user_info.setSocial_id(c.getString("social_id"));
				user_info.setCreated(c.getString("created"));
				user_info.setSalary_min(c.getString("salary_min"));
				user_info.setSalary_max(c.getString("salary_max"));
				
				if(c.has("matched")) {
					user_info.setMatched(c.getString("matched"));
				} else {
					user_info.setMatched("");
				}
				
				if(c.has("message_count")) {
					user_info.setMessage_count(c.getString("message_count"));
				} else {
					user_info.setMessage_count("");
				}
    		} 
    	} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private String getDate(String str) {
		long timestemp = Long.valueOf(str) * 1000;
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestemp);
		return String.format("%s/%s", cal.get(Calendar.MONTH), cal.get(Calendar.YEAR));
	}
	
	private void createSession(final String login, final String password, final UserInfo userinfo, final int index) {

        final QBUser user = new QBUser(login, password);
        QBAuth.createSession(login, password, new QBEntityCallbackImpl<QBSession>() {
            @Override
            public void onSuccess(QBSession session, Bundle bundle) {
                user.setId(session.getUserId());

                if (MainActivity.chatService.isLoggedIn()){
                    startCallActivity(userinfo, index);
                } else
                if (!MainActivity.chatService.isLoggedIn()){
                	MainActivity.chatService.login(user, new QBEntityCallbackImpl<QBUser>() {

                        @Override
                        public void onSuccess() {
                        	 Log.d(TAG, "successfully");
                        	 
                        	 startCallActivity(userinfo, index);
                        }

                        @Override
                        public void onError(List errors) {

                        	Toast.makeText(MatchActivity.this, "Error when login", Toast.LENGTH_SHORT).show();
                            for (Object error : errors) {
                                Log.d(TAG, error.toString());
                            }
                        }
                    });
                }

            }

            @Override
            public void onError(List<String> errors) {

                Toast.makeText(MatchActivity.this, "Error when login, check test users login and password", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
