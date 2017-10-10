package com.search.jobme;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.search.jobme.model.Education;
import com.search.jobme.model.Experience;
import com.search.jobme.model.JobInfo;
import com.search.jobme.model.NotificationModel;
import com.search.jobme.model.SkillModel;
import com.search.jobme.model.UserInfo;
import com.search.jobme.until.APIManager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EmployerProfileActivity extends Activity implements OnClickListener {
	
	ImageView btnBack, img_chat, company_picture;
	CircularImageView user_picture;
	TextView txt_user_name, txt_experience_year, txt_location, txt_latest_activity, txt_headline, txt_company_name, txt_description, txt_skills;
	LinearLayout experience_parent, education_parent;
	
	JobInfo job_info;
	UserInfo user_info;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.employer_profile);
		
		Intent intent = getIntent();
		int employer = intent.getIntExtra("employer", -1);
		
		if(employer == 0) {	//from JobDetailsPage
			job_info = HomeFragment.job_data.get(0);
			user_info = job_info.getUserInfo();
		} else if(employer == 1) {	//from Notification page 
			int position = intent.getIntExtra("position", 0);
			NotificationModel model = MainActivity.notification_data.get(position);
			user_info = model.getUserInfo();
			
			sendCheckInvite(model.getNoti_id());
		} else {	//from Chat page
			int match = intent.getIntExtra("match", -1);
			int index = intent.getIntExtra("index", -1);
			
			if(match == 0) {	//from Match List Menu
				if(intent.getBooleanExtra("home", true)) {
					user_info = HomeFragment.match_data.get(index);
				} else {
					user_info = ManageFragment.match_data.get(index);
				}
			} else if(match == 1) {	//from Match page
				user_info = MatchActivity.user_info;
			}
		}
		
		btnBack = (ImageView) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(this);
		
		img_chat = (ImageView) findViewById(R.id.img_chat);
		img_chat.setOnClickListener(this);
		
		if(user_info.getMatched().equals("1")) {
			img_chat.setVisibility(View.VISIBLE);
		}
		
		user_picture = (CircularImageView) findViewById(R.id.user_picture);
		String avatar_url = String.format("%s%s", Constants.PHOTO_UPLOAD, user_info.getAvatar());
		UrlImageViewHelper.setUrlDrawable(user_picture, avatar_url);
		
		txt_user_name = (TextView) findViewById(R.id.txt_user_name);
		txt_user_name.setText(String.format("%s %s", user_info.getFirst_name(), user_info.getLast_name()));
		
		int year_count = Integer.valueOf(user_info.getExperience_year()).intValue();
		String str = (year_count > 1) ? "years" : "year";
		txt_experience_year = (TextView) findViewById(R.id.txt_experience_year);
		if(year_count == 0) {
			txt_experience_year.setText("-");
		} else {
			txt_experience_year.setText(String.format("%s %s", user_info.getExperience_year(), str));
		}
		
		txt_location = (TextView) findViewById(R.id.txt_location);
		if(user_info.getLocation().equals("")) {
			txt_location.setText("-");
		} else {
			txt_location.setText(user_info.getLocation());
		}
		
	    long login_timestamp = Long.valueOf(user_info.getCreated()).longValue();
		long current_timestamp = System.currentTimeMillis() / 1000;
		long dif = current_timestamp - login_timestamp;
		
		str = "";
	    int day = 60 * 60 * 24;
	    if (dif < day) {
	        str = "Today";
	    } else if (dif < (day * 7)) {
	        str = "This week";
	    } else {
	        str = "Last week";
	    } 

	    txt_latest_activity = (TextView) findViewById(R.id.txt_latest_activity);
	    txt_latest_activity.setText(str);
		
		txt_company_name = (TextView) findViewById(R.id.txt_company_name);
		txt_company_name.setText(String.format("HIRING FROM %s", user_info.getCompany()));
		
		txt_headline = (TextView) findViewById(R.id.txt_headline);
		txt_headline.setText(user_info.getHeadline());

		company_picture = (ImageView) findViewById(R.id.company_picture);
		TextView no_company = (TextView) findViewById(R.id.no_company);
		
		if(user_info.getCompany_avatar().equals("")) {
			company_picture.setVisibility(View.GONE);
			no_company.setVisibility(View.VISIBLE);
			no_company.setText(user_info.getCompany().substring(0, 1));
		} else {
			String company_url = String.format("%s%s", Constants.COMPANY_UPLOAD, user_info.getCompany_avatar());
			UrlImageViewHelper.setUrlDrawable(company_picture, company_url);
			no_company.setVisibility(View.GONE);
		}
		
		txt_description = (TextView) findViewById(R.id.txt_description);
		txt_description.setText(user_info.getCompany_description());
		
		if(user_info.getCompany_description().equals("")) {
			txt_description.setVisibility(View.GONE);
			
			TextView lblDescription = (TextView) findViewById(R.id.lblDescription);
			lblDescription.setVisibility(View.GONE);
			
			TextView line1 = (TextView) findViewById(R.id.line1);
			line1.setVisibility(View.GONE);
		}
		
		String skills = "";
		String str_skills = user_info.getSkill_ids();
		if(!str_skills.contains(",")) {
			if(!str_skills.equals("")) {
				for(int i = 0 ; i < MainActivity.skill_data.size() ; i++) {
					SkillModel item = MainActivity.skill_data.get(i);
					if(str_skills.equals(item.getId())) {
						skills = item.getTitle();
					}
				}					
			} 
		} else {
			String[] arr = str_skills.split(",");
			StringBuilder stringBuilder = new StringBuilder();
			for(int i = 0 ; i < arr.length ; i++) {
				for(int j = 0 ; j < MainActivity.skill_data.size() ; j++) {
					SkillModel item = MainActivity.skill_data.get(j);
					if(arr[i].equals(item.getId())) {
						stringBuilder.append(item.getTitle());
						stringBuilder.append(" • ");
						break;
					}
				}
			}
			
			String temp = stringBuilder.toString();
			skills = temp.substring(0, temp.length() - 3);
		}
		
		txt_skills = (TextView) findViewById(R.id.txt_skills);
		txt_skills.setText(skills);
		
		
		experience_parent = (LinearLayout) findViewById(R.id.experience_layout);
		education_parent = (LinearLayout) findViewById(R.id.education_layout);
		
		if(user_info.getExperience().size() > 0) {
			initExpereince(user_info.getExperience());
		} else {
			TextView lblExperience = (TextView) findViewById(R.id.lblExperience);
			lblExperience.setVisibility(View.GONE);
			
			TextView line2 = (TextView) findViewById(R.id.line2);
			line2.setVisibility(View.GONE);
			
			experience_parent.setVisibility(View.GONE);
		}
		
		if(user_info.getEducation().size() > 0) {
			initEducation(user_info.getEducation());
		} else {
			TextView lblEducation = (TextView) findViewById(R.id.lblEducation);
			lblEducation.setVisibility(View.GONE);
			
			TextView line3 = (TextView) findViewById(R.id.line3);
			line3.setVisibility(View.GONE);
			
			education_parent.setVisibility(View.GONE);
		}
	}
	
	void initExpereince(ArrayList<Experience> item) {
		LinearLayout experience_layout = new LinearLayout(this);
		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		param.setMargins(0, 10, 0, 0);
		experience_parent.addView(experience_layout, param);

		LinearLayout sub = new LinearLayout(this);
		param = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		sub.setOrientation(LinearLayout.VERTICAL);
		experience_layout.addView(sub, param);

		for (int i = 0; i < item.size(); i++) {
			Experience model = item.get(i);

			LinearLayout sub1 = new LinearLayout(this);
			param = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT);
			sub1.setOrientation(LinearLayout.VERTICAL);
			if (i > 0)
				param.setMargins(0, 6, 0, 0);
			sub.addView(sub1, param);

			TextView text1 = new TextView(this);
			text1.setText(model.getHeadline());
			text1.setTextSize(15);
			text1.setTextColor(Color.parseColor("#555555"));
			sub1.addView(text1);

			TextView text2 = new TextView(this);
			text2.setText(model.getCompany());
			text2.setTextSize(13);
			text2.setTextColor(Color.parseColor("#8C8C8C"));
			param.setMargins(0, 3, 0, 0);
			sub1.addView(text2, param);

			TextView text3 = new TextView(this);
			text3.setText(String.format("%s ~ %s", model.getStart_date(),
					model.getEnd_date()));
			text3.setTextSize(12);
			sub1.addView(text3);
		}
	}

	void initEducation(ArrayList<Education> item) {
		LinearLayout education_layout = new LinearLayout(this);
		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		param.setMargins(0, 10, 0, 0);
		education_parent.addView(education_layout, param);

		LinearLayout sub = new LinearLayout(this);
		param = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		sub.setOrientation(LinearLayout.VERTICAL);
		education_layout.addView(sub, param);

		for (int i = 0; i < item.size(); i++) {
			Education model = item.get(i);

			LinearLayout sub1 = new LinearLayout(this);
			param = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT);
			sub1.setOrientation(LinearLayout.VERTICAL);
			if (i > 0)
				param.setMargins(0, 6, 0, 0);
			sub.addView(sub1, param);

			TextView text1 = new TextView(this);
			text1.setText(model.getSchool());
			text1.setTextSize(15);
			text1.setTextColor(Color.parseColor("#555555"));
			sub1.addView(text1);

			TextView text2 = new TextView(this);
			text2.setText(model.getDegree());
			text2.setTextSize(13);
			text2.setTextColor(Color.parseColor("#8C8C8C"));
			param.setMargins(0, 3, 0, 0);
			sub1.addView(text2, param);

			TextView text3 = new TextView(this);
			text3.setText(String.format("%s ~ %s", model.getStart_date(),
					model.getEnd_date()));
			text3.setTextSize(12);
			sub1.addView(text3);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
			case R.id.btnBack:
				finish();
				break;
			case R.id.img_chat:
				break;
		}
	}
	
	private void sendCheckInvite(String notification_id) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("noti_id", notification_id));
        
    	JSONObject result = null;
   		APIManager.getInstance().callPost(this, "freelancer/check_invite", params, true);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
	}
}
