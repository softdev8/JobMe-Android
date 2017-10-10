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
import com.search.jobme.HomeFragment.MatchAdapter;
import com.search.jobme.ImageLoader.ImageLoader;
import com.search.jobme.model.Education;
import com.search.jobme.model.Experience;
import com.search.jobme.model.JobInfo;
import com.search.jobme.model.SkillModel;
import com.search.jobme.model.UserInfo;
import com.search.jobme.until.APIManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class JobDetailsActivity extends Activity {

	ImageView company_picture, btnClose;
	CircularImageView user_picture;
	TextView  txt_user_name,txt_job_title, txt_headline, txt_location, txt_experience_year, txt_salary;
	TextView txt_skill, txt_description;

	private String[] arr_job_function;
	
	JobInfo job_info;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.job_details);

		job_info = HomeFragment.job_data.get(0);
		UserInfo user_info = job_info.getUserInfo();
		
		arr_job_function = getResources().getStringArray(R.array.main_cat);

		btnClose = (ImageView) findViewById(R.id.btnClose);
		btnClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
			
		});		
		
		company_picture = (ImageView) findViewById(R.id.company_picture);
		LinearLayout no_company_layout = (LinearLayout) findViewById(R.id.no_company_layout);
		
		if(user_info.getCompany_avatar().equals("")) {
			no_company_layout.setVisibility(View.VISIBLE);
			company_picture.setVisibility(View.GONE);
			 
			TextView no_company = (TextView) findViewById(R.id.no_company);
			no_company.setText(user_info.getCompany().substring(0, 1));
			
			TextView company_name = (TextView) findViewById(R.id.company_name);
			company_name.setText(user_info.getCompany());
			
		} else {
			String company_avatar_url = String.format("%s%s", Constants.COMPANY_UPLOAD, user_info.getCompany_avatar());
			UrlImageViewHelper.setUrlDrawable(company_picture, company_avatar_url);
			no_company_layout.setVisibility(View.GONE);
		}
		
		txt_job_title = (TextView) findViewById(R.id.txt_job_title);
		txt_job_title.setText(job_info.getJob_title());
		
		txt_location = (TextView) findViewById(R.id.txt_location);
		txt_location.setText(job_info.getLocation());
		
		int year_count = Integer.valueOf(job_info.getExperience_year()).intValue();
		String str = (year_count > 1) ? "years" : "year";
		txt_experience_year = (TextView) findViewById(R.id.txt_experience_year);
		txt_experience_year.setText(String.format("%s %s", job_info.getExperience_year(), str));
		
		txt_salary = (TextView) findViewById(R.id.txt_salary);
		txt_salary.setText(String.format("%s ~ %sK", job_info.getSalary_min(), job_info.getSalary_max()));
		
		user_picture = (CircularImageView) findViewById(R.id.user_picture);
		String avatar_url = String.format("%s%s", Constants.PHOTO_UPLOAD, user_info.getAvatar());
		UrlImageViewHelper.setUrlDrawable(user_picture, avatar_url);
		user_picture.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				goEmployerPage();
			}
		});
		
		txt_headline = (TextView) findViewById(R.id.txt_headline);
		txt_headline.setText(String.format("HIRING FROM %s", user_info.getCompany()));

		txt_user_name = (TextView) findViewById(R.id.txt_user_name);
		txt_user_name.setText(String.format("%s %s", user_info.getFirst_name(), user_info.getLast_name()));
		txt_user_name.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				goEmployerPage();
			}
		});
		
		txt_skill = (TextView) findViewById(R.id.txt_skill);
		
		String str_skills = job_info.getSkill_ids();
		String skills = "";
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
		
		txt_skill.setText(skills);
		
		txt_description = (TextView) findViewById(R.id.txt_description);
		txt_description.setText(job_info.getDescription());
		
	}
	
	private void goEmployerPage() {
		Intent intent = new Intent(JobDetailsActivity.this, EmployerProfileActivity.class);
		intent.putExtra("employer", 0);
		startActivity(intent);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
	}
}
