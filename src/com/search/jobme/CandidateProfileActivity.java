package com.search.jobme;

import java.util.ArrayList;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.search.jobme.model.Education;
import com.search.jobme.model.Experience;
import com.search.jobme.model.SkillModel;
import com.search.jobme.model.UserInfo;

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

public class CandidateProfileActivity extends Activity {
	
	ImageView btnClose;
	LinearLayout _experience, _education, user_layout;
	TextView txt_headline, txt_job_function, txt_location, txt_experience_year, txt_latest_activity, txt_skill;
	CircularImageView user_picture;
	TextView lblexperience, lblactivity;
	
	private String[] arr_job_function;
	private String[] arr_industry;
	
	UserInfo user_info;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.candidate_profile);
		
		Intent intent = getIntent();
		int index = intent.getIntExtra("index", -1);
		int match = intent.getIntExtra("match", -1);
		if(match == -1) {
			if(index == -1) {
				user_info = HomeFragment.candidate_data.get(0);
			} else {
				user_info = CandidateActivity.candidate_data.get(index);
			}	
		} else {
			if(match == 0) {
				if(intent.getBooleanExtra("home", true)) {
					user_info = HomeFragment.match_data.get(index);
				} else {
					user_info = ManageFragment.match_data.get(index);
				}
			} else {
				user_info = MatchActivity.user_info;
			}
		}
		
		arr_job_function = getResources().getStringArray(R.array.main_cat);
		arr_industry = getResources().getStringArray(R.array.sub_cat);
		
		btnClose = (ImageView) findViewById(R.id.btnClose);
		btnClose.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		user_picture = (CircularImageView) findViewById(R.id.user_picture);
		
		txt_job_function = (TextView) findViewById(R.id.txt_job_function);
		int job_index = Integer.valueOf(user_info.getMain_cat_id()).intValue();
		txt_job_function.setText(arr_job_function[job_index]);
				
		txt_headline = (TextView) findViewById(R.id.txt_headline);
		txt_headline.setText(user_info.getHeadline());

		txt_location = (TextView) findViewById(R.id.txt_location);
		txt_location.setText(user_info.getLocation());
		
		lblexperience = (TextView) findViewById(R.id.lblexperience);
		lblactivity = (TextView) findViewById(R.id.lblactivity);
		
		int year_count = Integer.valueOf(user_info.getExperience_year()).intValue();
		String str = (year_count > 1) ? "years" : "year";
		txt_experience_year = (TextView) findViewById(R.id.txt_experience_year);
		txt_experience_year.setText(String.format("%s %s", user_info.getExperience_year(), str));
		
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
		txt_skill = (TextView) findViewById(R.id.txt_skill);
		txt_skill.setText(skills);
		
		if(skills.equals("")) {
			txt_skill.setVisibility(View.GONE);
		}
		
		user_layout = (LinearLayout) findViewById(R.id.user_layout);
		if(user_info.getMatched().equals("1")) {
			user_layout.setBackgroundColor(Color.parseColor("#41c289"));
			
			String avatar_url = String.format("%s%s", Constants.PHOTO_UPLOAD, user_info.getAvatar());
			UrlImageViewHelper.setUrlDrawable(user_picture, avatar_url);
			
			txt_job_function.setText(String.format("%s %s", user_info.getFirst_name(), user_info.getLast_name()));
			txt_job_function.setTextColor(Color.parseColor("#ffffff"));

			txt_headline.setTextColor(Color.parseColor("#ffffff"));	
			
			txt_location.setTextColor(Color.parseColor("#ffffff"));
			lblexperience.setTextColor(Color.parseColor("#ffffff"));
			lblactivity.setTextColor(Color.parseColor("#ffffff"));
		}
		
		_experience = (LinearLayout) findViewById(R.id.experience_layout);
		_education = (LinearLayout) findViewById(R.id.education_layout);
		
		if(user_info.getExperience() != null) {
			if(user_info.getExperience().size() > 0) {
				initExpereince(user_info.getExperience());
			}
		}
		
		if(user_info.getEducation() != null) {
			if(user_info.getEducation().size() > 0) {
				initEducation(user_info.getEducation());
			}
		}
	}
	
	void initExpereince(ArrayList<Experience> item) {
		LinearLayout experience_layout = new LinearLayout(this);
		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		param.setMargins(0, 20, 0, 0);
		_experience.addView(experience_layout, param);

		ImageView icon = new ImageView(this);
		param = new LinearLayout.LayoutParams(60, LayoutParams.WRAP_CONTENT);
		icon.setImageResource(R.drawable.experience);
		param.setMargins(50, 0, 0, 0);
		experience_layout.addView(icon, param);

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
			sub1.setPadding(30, 0, 0, 0);
			if (i > 0)
				param.setMargins(0, 10, 0, 0);
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
		param.setMargins(0, 20, 0, 0);
		_education.addView(education_layout, param);

		ImageView icon = new ImageView(this);

		param = new LinearLayout.LayoutParams(60, LayoutParams.WRAP_CONTENT);
		icon.setImageResource(R.drawable.function);
		param.setMargins(50, 0, 0, 0);
		education_layout.addView(icon, param);

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
			sub1.setPadding(30, 0, 0, 0);
			if (i > 0)
				param.setMargins(0, 10, 0, 0);
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
	public void onBackPressed() {
		// TODO Auto-generated method stub
	}
	
}
