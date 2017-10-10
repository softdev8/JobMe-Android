package com.search.jobme;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.tangke.slidemenu.SlideMenu;

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
import com.search.jobme.HomeFragment.LoadMatchTask;
import com.search.jobme.HomeFragment.MatchAdapter;
import com.search.jobme.ImageLoader.ImageLoader;
import com.search.jobme.model.Education;
import com.search.jobme.model.Experience;
import com.search.jobme.model.JobInfo;
import com.search.jobme.model.Manage;
import com.search.jobme.model.UserInfo;
import com.search.jobme.until.APIManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint("NewApi")
public class ManageFragment extends Fragment implements OnClickListener {
	
	private static final String TAG = "ManageFragment";
	
	ListView manage_listView;
	
	ArrayList<Manage> project_data = new ArrayList<Manage>();
	
	SearchAdapter adapter;
	Context context;
	
	private String[] arr_job_function;
	
	MainActivity mParent;
	private SlideMenu mSlideMenu;
    
    ImageView btnMenu, btnMatch;
    
    ListView match_listView;
	public static ArrayList<UserInfo> match_data = new ArrayList<UserInfo>();
	MatchAdapter macth_adapter;
	
	TextView txt_new_badge;
    int notification_badge = 0;
    
    boolean mGNotification = false;
    SharedPreferences prefs;
    
    ArrayList<Experience> experience_data;
    ArrayList<Education> education_data;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		
		mParent = (MainActivity) this.getActivity();
		
		View v = inflater.inflate(
	    		  R.layout.manage_posting, container, false);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(mParent);
		
		mSlideMenu = mParent.getSlideMenu();
		
		match_listView = (ListView) mParent.findViewById(R.id.match_listView);
		macth_adapter = new MatchAdapter(match_data, mParent);
		match_listView.setAdapter(macth_adapter);
		match_listView.setDividerHeight(0);
		
		btnMenu = (ImageView) v.findViewById(R.id.btnMenu);
		btnMenu.setOnClickListener(this);
		
		btnMatch = (ImageView) v.findViewById(R.id.btnMatch);
		btnMatch.setOnClickListener(this);
		
		txt_new_badge = (TextView) v.findViewById(R.id.txt_new_badge);
		
		arr_job_function = getResources().getStringArray(R.array.main_cat);

		manage_listView = (ListView) v.findViewById(R.id.manage_listView);
		adapter = new SearchAdapter(project_data, mParent);
		manage_listView.setAdapter(adapter);
		manage_listView.setDividerHeight(0);
		
		match_listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				UserInfo info = match_data.get(position);
				
				int missed_message_count = Integer.valueOf(info.getMessage_count()).intValue();
				if(missed_message_count != 0) {
					notification_badge = notification_badge - missed_message_count;
					updateBadge(2);
				}
				
				String qlogin = prefs.getString("email", "");
				String qpassword = prefs.getString("password", "");
				
				createSession(qlogin, qpassword, info, position);
			}
		});
		
		new LoadProductTask().execute();
		
		IntentFilter iff= new IntentFilter(GCMNotificationIntentService.PUSH_RECEIVED);
		LocalBroadcastManager.getInstance(mParent).registerReceiver(onPushNotice, iff);
		
		updateBadge(0);
		
		return v;
	}
	
	public void updateBadge(int status) {
    	
		if(status == 0) {
			new LoadBadgeTask().execute();
		} else if(status == 1){
			notification_badge++;
		}

    	if(notification_badge == 0){
    		txt_new_badge.setVisibility(View.GONE);
    	}else if(notification_badge > 0){
    		txt_new_badge.setVisibility(View.VISIBLE);
    		txt_new_badge.setText(String.valueOf(notification_badge));
    	}
    }

	class LoadBadgeTask extends AsyncTask<String, Integer, String> {
        private ProgressDialog progressDialog;
        int count = 0;
        
        protected void onPreExecute() {
        	progressDialog = ProgressDialog.show(mParent, "", "Loading...", true);
        }
        
        @Override
        protected void onPostExecute(String result) {
        	notification_badge = count;
            progressDialog.dismiss();
        }
 
        @Override
        protected String doInBackground(String... param) {
        	
    		JSONObject result = APIManager.getInstance().getInstance().callPost(mParent, "notification/badge", null, true);

        	try {
    			count = Integer.valueOf(result.getString("badge"));
        		
        	} catch (JSONException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		}
    		
    	    return null;
        }
    }
	
	private BroadcastReceiver onPushNotice= new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            updateBadge(1);
        }
    };
	
	class LoadProductTask extends AsyncTask<String, Integer, ArrayList<Manage>> {
        private ProgressDialog progressDialog;
        
        protected void onPreExecute() {
        	progressDialog = ProgressDialog.show(mParent, "", "Loading...", true);
        }
        
        @Override
        protected void onPostExecute(ArrayList<Manage> result) {
        	adapter = new SearchAdapter(project_data, mParent);
        	manage_listView.setAdapter(adapter);
        	
            progressDialog.dismiss();
        }
 
        @Override
        protected ArrayList<Manage> doInBackground(String... param) {
        	
        	project_data.clear();
        	
        	JSONObject result = null;
       		result =  APIManager.getInstance().callPost(mParent, "client/manage", null, true);
        	
        	try {
				
        		if(result.getString("success").equals("1")) {
	        		JSONArray jarray = result.getJSONArray("result");
					
					for (int i = 0; i < jarray.length(); i++) {
						JSONObject c = jarray.getJSONObject(i);
						
						Manage info = new Manage();
						
						info.setJob_id(c.getString("id"));
						info.setJob_title(c.getString("post_title"));
						info.setJob_function(c.getString("category_title"));
						info.setLocation(c.getString("location"));
						info.setExperience_year(c.getString("experience_year"));
						info.setSalary_min(c.getString("salary_min"));
						info.setSalary_max(c.getString("salary_max"));
						info.setSkill_ids(c.getString("skill_ids"));
						info.setDescription(c.getString("description"));
						info.setLike_count(c.getString("like_count"));
						info.setCompany_avatar(c.getString("companyAvatar"));
						info.setUser_avatar(c.getString("avatar"));
						info.setHeadline(c.getString("headline"));
						info.setUsername(c.getString("first_name") + " " + c.getString("last_name"));
						info.setCompany_name(c.getString("company"));
						
						project_data.add(info);
					}
        		}
        	} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

            return null;
        }
    }
	
	class SearchAdapter extends ArrayAdapter<Manage> {
		
		private ArrayList<Manage> itemList;
		private Context context;
		
		
		public SearchAdapter(ArrayList<Manage> itemList, Context ctx) {
			super(ctx, android.R.layout.simple_list_item_1, itemList);
			this.itemList = itemList;
			this.context = ctx;		
			
		}
		
		@SuppressLint("NewApi")
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			
			View v = convertView;
			if (v == null) {
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				
				v = inflater.inflate(R.layout.manage_list_item, null);
			}
			
			final Manage item = project_data.get(position);
			
			ImageView company_picture = (ImageView) v.findViewById(R.id.company_picture);
			String avatar_url = String.format("%s%s", Constants.COMPANY_UPLOAD, item.getCompany_avatar());
			UrlImageViewHelper.setUrlDrawable(company_picture, avatar_url);
			
			TextView txt_job_title = (TextView) v.findViewById(R.id.txt_job_title);
			txt_job_title.setText(item.getJob_title());
			
			TextView txt_location = (TextView) v.findViewById(R.id.txt_location);
			txt_location.setText(item.getLocation());
			
			TextView txt_experience_year = (TextView) v.findViewById(R.id.txt_experience_year);
			txt_experience_year.setText(String.format("%s years", item.getExperience_year()));
			
			TextView txt_salary = (TextView) v.findViewById(R.id.txt_salary);
			txt_salary.setText(String.format("%s ~ %sK", item.getSalary_min(), item.getSalary_max()));
			
			TextView txt_headline = (TextView) v.findViewById(R.id.txt_headline);
			txt_headline.setText(String.format("HIRING FROM %s", item.getCompany_name()));
			
			TextView txt_user_name = (TextView) v.findViewById(R.id.txt_user_name);
			txt_user_name.setText(item.getUsername());
			
			CircularImageView user_picture = (CircularImageView) v.findViewById(R.id.user_picture);
			avatar_url = String.format("%s%s", Constants.PHOTO_UPLOAD, item.getUser_avatar());
			UrlImageViewHelper.setUrlDrawable(user_picture, avatar_url);

			TextView txt_description = (TextView) v.findViewById(R.id.txt_description);
			txt_description.setText(item.getDescription());
			
			TextView txt_like_count = (TextView) v.findViewById(R.id.txt_like_count);
			txt_like_count.setText(String.format("Likes (%s)", item.getLike_count()));
			
			int like_count = Integer.valueOf(item.getLike_count()).intValue();
			LinearLayout like_layout = (LinearLayout) v.findViewById(R.id.like_layout);
			if(like_count > 0) {
				like_layout.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(mParent, CandidateActivity.class);
						intent.putExtra("job_id", item.getJob_id());
						startActivity(intent);
					}
				});
			}
			
			return v;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
			case R.id.btnMenu:
				mSlideMenu.open(false, true);
				mParent.getNotification();
				break;
			case R.id.btnMatch:
				mSlideMenu.open(true, true);
				new LoadMatchTask().execute();
				break;
		}
	}
	
	class LoadMatchTask extends AsyncTask<String, Integer, ArrayList<UserInfo>> {
        private ProgressDialog progressDialog;
        
        protected void onPreExecute() {
        	progressDialog = ProgressDialog.show(mParent, "", "Loading...", true);
        }
        
        @Override
        protected void onPostExecute(ArrayList<UserInfo> result) {
        	macth_adapter = new MatchAdapter(match_data, mParent);
        	match_listView.setAdapter(macth_adapter);
    		
            progressDialog.dismiss();
        }
 
        @Override
        protected ArrayList<UserInfo> doInBackground(String... param) {
        	
        	match_data.clear();
        	
        	JSONObject result = null;
       		result =  APIManager.getInstance().callPost(mParent, "client/get_matches", null, true);
        	
        	try {
        		
        		if(result.getString("success").equals("1")) {
	        		JSONArray jarray = result.getJSONArray("matches");
					
					for (int i = 0; i < jarray.length(); i++) {
						JSONObject c = jarray.getJSONObject(i);
						
						UserInfo user_info = new UserInfo();
						
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
		    			user_info.setMain_cat_id(c.getString("main_cat_id"));
		    			user_info.setSub_cat_id(c.getString("sub_cat_id"));
		    			user_info.setExperience_year(c.getString("experience_year"));
		    			user_info.setVideochat_id(c.getString("videochat_id"));
						
		    			experience_data = new ArrayList<Experience>();
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
						
						education_data = new ArrayList<Education>();
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
						user_info.setCreated(c.getString("created"));
						
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
						
						match_data.add(user_info);
					}
        		}
        	} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

            return null;
        }
    }
	
	private String getDate(String str) {
		long timestemp = Long.valueOf(str) * 1000;
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestemp);
		return String.format("%s/%s", cal.get(Calendar.MONTH), cal.get(Calendar.YEAR));
	}
	
	class MatchAdapter extends ArrayAdapter<UserInfo> {
		
		private ArrayList<UserInfo> itemList;
		private Context context;
		
		
		public MatchAdapter(ArrayList<UserInfo> itemList, Context ctx) {
			super(ctx, android.R.layout.simple_list_item_1, itemList);
			this.itemList = itemList;
			this.context = ctx;		
			
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			
			View v = convertView;
			if (v == null) {
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				
				v = inflater.inflate(R.layout.match_list_item, null);
			}
			
			final UserInfo item = match_data.get(position);
			
			CircularImageView user_picture = (CircularImageView) v.findViewById(R.id.user_picture);
			String avatar_url = String.format("%s%s", Constants.PHOTO_UPLOAD, item.getAvatar());
			UrlImageViewHelper.setUrlDrawable(user_picture, avatar_url);
			
			TextView username = (TextView) v.findViewById(R.id.txt_username);
			username.setText(String.format("%s %s", item.getFirst_name(), item.getLast_name()));
			
			TextView txt_headline = (TextView) v.findViewById(R.id.txt_headline);
			TextView company_name = (TextView) v.findViewById(R.id.txt_company_name);
			
			String is_employer = prefs.getString("is_employer", "0");
			
			txt_headline.setText(item.getHeadline());
			if(is_employer.equals("1")) {
				company_name.setVisibility(View.GONE);
			} else {
				company_name.setText(item.getCompany());
			}
			
			if(item.getOnline().equals("1")) {
				username.setTextColor(Color.parseColor("#08a67f"));
				txt_headline.setTextColor(Color.parseColor("#08a67f"));
				company_name.setTextColor(Color.parseColor("#08a67f"));
			} else {
				username.setTextColor(Color.parseColor("#8a8a8a"));
				txt_headline.setTextColor(Color.parseColor("#8a8a8a"));
				company_name.setTextColor(Color.parseColor("#8a8a8a"));
			}

			RelativeLayout message_layout = (RelativeLayout) v.findViewById(R.id.message_layout);
			TextView txt_cnt = (TextView) v.findViewById(R.id.txt_cnt);
			
			if(item.getMessage_count().equals("0")) {
				message_layout.setVisibility(View.GONE);
			} else {
				message_layout.setVisibility(View.VISIBLE);
				txt_cnt.setText(item.getMessage_count());
			}
			
			
			return v;
		}
	}
	
	private void createSession(final String login, final String password, final UserInfo userinfo, final int position) {

        final QBUser user = new QBUser(login, password);
        QBAuth.createSession(login, password, new QBEntityCallbackImpl<QBSession>() {
            @Override
            public void onSuccess(QBSession session, Bundle bundle) {
                Log.d(TAG, "onSuccess create session with params");
                user.setId(session.getUserId());

                if (mParent.chatService.isLoggedIn()){
                    startCallActivity(userinfo, position);
                } else {
                	mParent.chatService.login(user, new QBEntityCallbackImpl<QBUser>() {

                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "onSuccess login to chat");

                            startCallActivity(userinfo, position);
                        }

                        @Override
                        public void onError(List errors) {

                        	Toast.makeText(mParent, "Error when login", Toast.LENGTH_SHORT).show();
                            for (Object error : errors) {
                                Log.d(TAG, error.toString());
                            }
                        }
                    });
                }

            }

            @Override
            public void onError(List<String> errors) {

                Toast.makeText(mParent, "Error when login, check test users login and password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startCallActivity(UserInfo info, int index) {
		
		Intent intent = new Intent(mParent, ChatActivity.class);
		intent.putExtra("receiver_id", info.getUser_id());
		intent.putExtra("receiver_user_avatar", info.getAvatar());
		intent.putExtra("receiver_name", info.getFirst_name() + " " + info.getLast_name());
		if(prefs.getString("is_employer", "0").equals("1")) {
			intent.putExtra("headline", info.getHeadline());
		} else {
			intent.putExtra("headline", info.getCompany());
		}
		intent.putExtra("online", info.getOnline());
		intent.putExtra("qlogin", info.getEmail());
		intent.putExtra("videochat_id", info.getVideochat_id());
		intent.putExtra("match", 0);
		intent.putExtra("index", index);
		intent.putExtra("home", false);
		startActivity(intent);		
    }
}
