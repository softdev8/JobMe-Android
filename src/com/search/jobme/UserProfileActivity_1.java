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

public class UserProfileActivity_1 extends Activity {

	LinearLayout parent;
	Context context;

	ImageView btnClose;
	CircularImageView user_picture;
	TextView txt_username, txt_headline, txt_location, txt_experience_year,
			txt_salary;

	ArrayList<Experience> experience_data = new ArrayList<Experience>();
	ArrayList<Education> education_data = new ArrayList<Education>();

	String sel_id;
	
	private String[] arr_skills;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.job_details);

		Intent intent = getIntent();
		sel_id = intent.getStringExtra("sel_id");

		context = this;
		
//		arr_skills = getResources().getStringArray(R.array.skills);

		parent = (LinearLayout) findViewById(R.id.parent);
		btnClose = (ImageView) findViewById(R.id.btnClose);
		btnClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
			
		});
		
		user_picture = (CircularImageView) findViewById(R.id.user_picture);
		txt_username = (TextView) findViewById(R.id.txt_username);
		txt_headline = (TextView) findViewById(R.id.txt_headline);
		txt_location = (TextView) findViewById(R.id.txt_location);
		txt_experience_year = (TextView) findViewById(R.id.txt_experience_year);
		txt_salary = (TextView) findViewById(R.id.txt_salary);

		new UserProfileTask().execute();

	}

	class UserProfileTask extends
			AsyncTask<String, Integer, ArrayList<UserInfo>> {
		private ProgressDialog progressDialog;

		String avatar, first_name, last_name, headline, experience_year,
				location, str_skills;

		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(UserProfileActivity_1.this, "",
					"Loading...", true);
		}

		@Override
        protected void onPostExecute(ArrayList<UserInfo> result) {
    		
        	String avatar_url = String.format("%s%s", Constants.PHOTO_UPLOAD, avatar);
        	UrlImageViewHelper.setUrlDrawable(user_picture, avatar_url);
        	
        	txt_username.setText(String.format("%s %s", first_name, last_name));
			txt_headline.setText(headline);
			txt_experience_year.setText(String.format("%s years", experience_year));
			txt_location.setText(location);
			
			if(experience_data.size() > 0) {
				initExpereince(experience_data);
			}
			
			if(education_data.size() > 0) {
				initEducation(education_data);
			}
			
			String skills = "";
			if(!str_skills.contains(",")) {
				if(!str_skills.equals("")) {
					skills = arr_skills[Integer.valueOf(str_skills).intValue() - 1];
				} else {
					skills = str_skills;
				}
			} else {
				String[] arr = str_skills.split(",");
				StringBuilder stringBuilder = new StringBuilder();
				for(int i = 0 ; i < arr.length ; i++) {
					stringBuilder.append(arr_skills[Integer.valueOf(arr[i]).intValue() - 1]);
					stringBuilder.append(", ");
				}
				
				String temp = stringBuilder.toString();
				
				skills = temp.substring(0, temp.length() - 2);
			}
			
			if(!skills.equals("")) {
				initSkill(skills);
			}
    		
            progressDialog.dismiss();
        }

		@Override
		protected ArrayList<UserInfo> doInBackground(String... param) {

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("sel_id", sel_id));

			JSONObject result = null;
			result = APIManager.getInstance().callPost(context, "get_profile",
					params, true);

			try {

				if (result.getString("success").equals("1")) {
					JSONObject c = result.getJSONObject("user_info");

					avatar = c.getString("avatar");
					first_name = c.getString("first_name");
					last_name = c.getString("last_name");
					headline = c.getString("headline");
					experience_year = c.getString("experience_year");
					location = c.getString("location");

					JSONArray arr_experience = c.getJSONArray("experience");
					for (int i = 0; i < arr_experience.length(); i++) {
						JSONObject b = arr_experience.getJSONObject(i);

						Experience item = new Experience();
						item.setExp_id(b.getString("id"));
						item.setHeadline(b.getString("job_title"));
						item.setCompany(b.getString("company"));
						item.setStart_date(getDate(b.getString("start_date")));
						if (b.getString("end_date").equals("Present")) {
							item.setEnd_date(b.getString("end_date"));
						} else {
							item.setEnd_date(getDate(b.getString("end_date")));
						}
						experience_data.add(item);
					}

					JSONArray arr_education = c.getJSONArray("education");
					for (int i = 0; i < arr_education.length(); i++) {
						JSONObject b = arr_education.getJSONObject(i);

						Education item = new Education();
						item.setEdu_id(b.getString("id"));
						item.setSchool(b.getString("school_name"));
						item.setDegree(b.getString("degree"));
						item.setField(b.getString("field_study"));
						item.setStart_date(getDate(b.getString("start_date")));
						if (b.getString("end_date").equals("Present")) {
							item.setEnd_date(b.getString("end_date"));
						} else {
							item.setEnd_date(getDate(b.getString("end_date")));
						}
						education_data.add(item);
					}
					str_skills = c.getString("skill_ids");
				}
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			return null;
		}
	}

	void initExpereince(ArrayList<Experience> item) {
		LinearLayout experience_layout = new LinearLayout(this);
		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		param.setMargins(0, 20, 0, 0);
		parent.addView(experience_layout, param);

		ImageView icon = new ImageView(this);
		param = new LinearLayout.LayoutParams(120, LayoutParams.WRAP_CONTENT);
		icon.setImageResource(R.drawable.experience);
		param.setMargins(0, 9, 0, 0);
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
			if (i > 0)
				param.setMargins(0, 10, 0, 0);
			sub.addView(sub1, param);

			TextView text1 = new TextView(this);
			text1.setText(model.getHeadline());
			text1.setTextSize(22);
			text1.setTextColor(Color.parseColor("#555555"));
			sub1.addView(text1);

			TextView text2 = new TextView(this);
			text2.setText(model.getCompany());
			text2.setTextSize(18);
			text2.setTextColor(Color.parseColor("#8C8C8C"));
			param.setMargins(0, 3, 0, 0);
			sub1.addView(text2, param);

			TextView text3 = new TextView(this);
			text3.setText(String.format("%s ~ %s", model.getStart_date(),
					model.getEnd_date()));
			text3.setTextSize(14);
			sub1.addView(text3);
		}
	}

	void initEducation(ArrayList<Education> item) {
		LinearLayout education_layout = new LinearLayout(this);
		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		param.setMargins(0, 20, 0, 0);
		parent.addView(education_layout, param);

		ImageView icon = new ImageView(this);

		param = new LinearLayout.LayoutParams(120, LayoutParams.WRAP_CONTENT);
		icon.setImageResource(R.drawable.education);
		param.setMargins(0, 9, 0, 0);
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
			if (i > 0)
				param.setMargins(0, 10, 0, 0);
			sub.addView(sub1, param);

			TextView text1 = new TextView(this);
			text1.setText(model.getSchool());
			text1.setTextSize(22);
			text1.setTextColor(Color.parseColor("#555555"));
			sub1.addView(text1);

			TextView text2 = new TextView(this);
			text2.setText(model.getDegree());
			text2.setTextSize(18);
			text2.setTextColor(Color.parseColor("#8C8C8C"));
			param.setMargins(0, 3, 0, 0);
			sub1.addView(text2, param);

			TextView text3 = new TextView(this);
			text3.setText(String.format("%s ~ %s", model.getStart_date(),
					model.getEnd_date()));
			text3.setTextSize(14);
			sub1.addView(text3);
		}
	}

	void initSkill(String skill) {
		LinearLayout education_layout = new LinearLayout(this);
		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		param.setMargins(0, 20, 0, 0);
		parent.addView(education_layout, param);

		ImageView icon = new ImageView(this);
		param = new LinearLayout.LayoutParams(120, LayoutParams.WRAP_CONTENT);
		icon.setImageResource(R.drawable.skills);
		param.setMargins(0, 9, 0, 0);
		education_layout.addView(icon, param);

		TextView text1 = new TextView(this);
		text1.setText(skill);
		text1.setTextSize(22);
		text1.setTextColor(Color.parseColor("#555555"));
		education_layout.addView(text1);
	}

	private String getDate(String str) {
		long timestemp = Long.valueOf(str) * 1000;
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestemp);
		return String.format("%s/%s", cal.get(Calendar.MONTH),
				cal.get(Calendar.YEAR));
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
	}
}
