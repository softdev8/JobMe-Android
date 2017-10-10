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
import com.search.jobme.ImageLoader.ImageLoader;
import com.search.jobme.model.Education;
import com.search.jobme.model.Experience;
import com.search.jobme.model.Manage;
import com.search.jobme.model.UserInfo;
import com.search.jobme.until.APIManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class CandidateActivity extends Activity implements OnClickListener {
	
	ImageView btnBack;
	
	ListView candidate_listView;
	
	public static ArrayList<UserInfo> candidate_data = new ArrayList<UserInfo>();
	
	SearchAdapter adapter;
	
	Context context;
	String job_id = "";
	
	ArrayList<Experience> experience_data = new ArrayList<Experience>();
	ArrayList<Education> education_data = new ArrayList<Education>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.candidate);
	
		context = this;
		
		Intent intent = getIntent();
		job_id = intent.getStringExtra("job_id");
		
		btnBack = (ImageView) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(this);
		
		candidate_listView = (ListView) findViewById(R.id.candidate_listView);
		adapter = new SearchAdapter(candidate_data, this);
		candidate_listView.setAdapter(adapter);
		candidate_listView.setDividerHeight(0);
		
		new LoadProductTask().execute();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
			case R.id.btnBack:
				finish();
				break;
		}
	}
	
	class LoadProductTask extends AsyncTask<String, Integer, ArrayList<UserInfo>> {
        private ProgressDialog progressDialog;
        
        protected void onPreExecute() {
        	progressDialog = ProgressDialog.show(context, "", "Loading...", true);
        }
        
        @Override
        protected void onPostExecute(ArrayList<UserInfo> result) {
        	adapter = new SearchAdapter(candidate_data, context);
        	candidate_listView.setAdapter(adapter);
    		
            progressDialog.dismiss();
        }
 
        @Override
        protected ArrayList<UserInfo> doInBackground(String... param) {
        	
        	candidate_data.clear();
        	
        	List<NameValuePair> params = new ArrayList<NameValuePair>();
	        params.add(new BasicNameValuePair("post_id", job_id));
	        
        	JSONObject result = null;
       		result =  APIManager.getInstance().callPost(context, "client/get_candidate", params, true);
        	
        	try {
				
        		if(result.getString("success").equals("1")) {
	        		JSONArray jarray = result.getJSONArray("candidates");
					
					for (int i = 0; i < jarray.length(); i++) {
						JSONObject c = jarray.getJSONObject(i);
						
						UserInfo info = new UserInfo();
						
						info.setUser_id(c.getString("id"));
						info.setFirst_name(c.getString("first_name"));
						info.setLast_name(c.getString("last_name"));
						info.setEmail(c.getString("email"));
						info.setCompany(c.getString("company"));
						info.setAvatar(c.getString("avatar"));
						info.setHeadline(c.getString("headline"));
						info.setLocation(c.getString("location"));
						info.setLatitude(c.getString("lat"));
						info.setLongitude(c.getString("lot"));
						info.setMain_cat_id(c.getString("main_cat_id"));
						info.setSub_cat_id(c.getString("sub_cat_id"));
						info.setExperience_year(c.getString("experience_year"));
						
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
						
						info.setExperience(experience_data);
						
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
						
						info.setEducation(education_data);
						
						info.setSkill_ids(c.getString("skill_ids"));
						info.setOnline(c.getString("online"));
						info.setStatus(c.getString("status"));
						info.setSocial_id(c.getString("social_id"));
						info.setCreated(c.getString("created"));
						info.setSalary_min(c.getString("salary_min"));
						info.setSalary_max(c.getString("salary_max"));
						
						if(c.has("matched")) {
							info.setMatched(c.getString("matched"));
						} else {
							info.setMatched("");
						}
						
						if(c.has("message_count")) {
							info.setMessage_count(c.getString("message_count"));
						} else {
							info.setMessage_count("");
						}
						
						candidate_data.add(info);
					}
        		}
        	} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

            return null;
        }
    }
	
	class SearchAdapter extends ArrayAdapter<UserInfo> {
		
		private ArrayList<UserInfo> itemList;
		private Context context;
		
		
		public SearchAdapter(ArrayList<UserInfo> itemList, Context ctx) {
			super(ctx, android.R.layout.simple_list_item_1, itemList);
			this.itemList = itemList;
			this.context = ctx;		
			
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			
			View v = convertView;
			if (v == null) {
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				
				v = inflater.inflate(R.layout.candidate_list_item, null);
			}
			
			final UserInfo item = candidate_data.get(position);
			
			LinearLayout user_layout = (LinearLayout) v.findViewById(R.id.user_layout);
			user_layout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(context, CandidateProfileActivity.class);
					intent.putExtra("index", position);
					startActivity(intent);
				}
				
			});
			
			final CircularImageView user_picture = (CircularImageView) v.findViewById(R.id.user_picture);
			final String avatar_url = String.format("%s%s", Constants.PHOTO_UPLOAD, item.getAvatar());
			if(item.getMatched().equals("1")) {
				UrlImageViewHelper.setUrlDrawable(user_picture, avatar_url);
			} 
			
			TextView username = (TextView) v.findViewById(R.id.txt_username);
			username.setText(String.format("%s %s", item.getFirst_name(), item.getLast_name()));
			
			TextView txt_headline = (TextView) v.findViewById(R.id.txt_headline);
			txt_headline.setText(item.getHeadline());
			
			TextView txt_location = (TextView) v.findViewById(R.id.txt_location);
			txt_location.setText(item.getLocation());
			
			TextView txt_infor = (TextView) v.findViewById(R.id.txt_infor);
			txt_infor.setText(String.format("%s years", item.getExperience_year()));
			
			final ImageView btnLike = (ImageView) v.findViewById(R.id.btnLike);
			final ImageView btnDislike = (ImageView) v.findViewById(R.id.btnDislike);
			
			final TextView txt_status = (TextView) v.findViewById(R.id.txt_status);
			
			if(item.getMatched().equals("1")) {
				txt_status.setText("Matched");
				txt_status.setTextColor(Color.parseColor("#41c289"));
				btnLike.setEnabled(false);
				btnDislike.setEnabled(false);
			} else if(item.getStatus().equals("100")) {
				txt_status.setText("DisLiked");
				txt_status.setTextColor(Color.parseColor("#aaaaaa"));
				btnLike.setEnabled(false);
				btnDislike.setEnabled(false);
			} else {
				txt_status.setText("");
				btnLike.setEnabled(true);
				btnDislike.setEnabled(true);
			}
			
			btnLike.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					item.setStatus("1");
					UrlImageViewHelper.setUrlDrawable(user_picture, avatar_url);
					
					List<NameValuePair> params = new ArrayList<NameValuePair>();
			        params.add(new BasicNameValuePair("post_id", job_id));
			        params.add(new BasicNameValuePair("freelancer_id", item.getUser_id()));
			        
		        	JSONObject result = null;
		       		APIManager.getInstance().callPost(context, "client/like", params, true);
		       		
		       		
		       		txt_status.setText("Matched");
		       		txt_status.setTextColor(Color.parseColor("#41c289"));
					btnLike.setEnabled(false);
					btnDislike.setEnabled(false);
				}
			});
			
			btnDislike.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					item.setStatus("100");
					
					List<NameValuePair> params = new ArrayList<NameValuePair>();
			        params.add(new BasicNameValuePair("post_id", job_id));
			        params.add(new BasicNameValuePair("freelancer_id", item.getUser_id()));
			        
		        	JSONObject result = null;
		       		APIManager.getInstance().callPost(context, "client/dislike", params, true);
		       		
		       		txt_status.setText("DisLiked");
		       		txt_status.setTextColor(Color.parseColor("#aaaaaa"));
					btnLike.setEnabled(false);
					btnDislike.setEnabled(false);
				}
			});
			return v;
		}
	}
	
	private String getDate(String str) {
		long timestemp = Long.valueOf(str) * 1000;
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestemp);
		return String.format("%s/%s", cal.get(Calendar.MONTH), cal.get(Calendar.YEAR));
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
	}
}
