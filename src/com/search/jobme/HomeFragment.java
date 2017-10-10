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
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.users.model.QBUser;
import com.search.jobme.ImageLoader.ImageLoader;
import com.search.jobme.model.Education;
import com.search.jobme.model.Experience;
import com.search.jobme.model.JobInfo;
import com.search.jobme.model.SkillModel;
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
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint("NewApi")
public class HomeFragment extends Fragment implements OnClickListener {
	
	private static final String TAG = "HomeFragment";
	
	private static final int				FUNCTION 	= 1;
	private static final int				LOCATION 	= 2;
	
	private ArrayList<String> al;
	private ArrayAdapter<String> arrayAdapter;
	
    public static ArrayList<JobInfo> job_data = new ArrayList<JobInfo>();
    public static ArrayList<UserInfo> candidate_data = new ArrayList<UserInfo>();
    ArrayList<Experience> experience_data;
    ArrayList<Education> education_data;
    
    ListView match_listView;
	public static ArrayList<UserInfo> match_data = new ArrayList<UserInfo>();
	MatchAdapter macth_adapter;
    
    JobAdapter job_adapter;
    CandidateAdapter candidate_adapter;
    
    private int i;

    SwipeFlingAdapterView flingContainer;
    
    MainActivity mParent;
    
    ImageView btnDisLike, btnDetails, btnLike;
    private SlideMenu mSlideMenu;
    
    ImageView btnMenu, btnMatch;
    private String[] arr_job_function;
    private String[] arr_industry;
    
    TextView txt_home_title, txt_location, job_function, location, txt_job_count, txtfunction, txtlocation;
    LinearLayout job, search_result, sub_layout, welcome_layout;
    RelativeLayout function_layout, location_layout;
    boolean bUpArrow = true;
    
    int job_index = -1;
    int job_count = 0, candidate_count = 0;
    
    String lat, lot;
    boolean mbJobFunction = false;
    boolean mbLocation = false;
    
    TextView txt_new_badge;
    int notification_badge = 0;
    
    boolean mGNotification = false;
    SharedPreferences prefs;
    String is_company;
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		
		mParent = (MainActivity) this.getActivity();
		
		View v = inflater.inflate(
	    		  R.layout.home, container, false);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(mParent);
		is_company = prefs.getString("is_employer", "0");
		
		mSlideMenu = mParent.getSlideMenu();
		
		arr_job_function = getResources().getStringArray(R.array.main_cat);
		arr_industry = getResources().getStringArray(R.array.sub_cat);
		
		txt_home_title = (TextView) v.findViewById(R.id.txt_home_title);
		txt_home_title.setOnClickListener(this);
		
		txt_location = (TextView) v.findViewById(R.id.txt_location);
		txt_location.setOnClickListener(this);
		
		job_function = (TextView) v.findViewById(R.id.job_function);
		job_function.setOnClickListener(this);
		location = (TextView) v.findViewById(R.id.location);
		location.setOnClickListener(this);
		txt_job_count = (TextView) v.findViewById(R.id.txt_job_count);
		
		job = (LinearLayout) v.findViewById(R.id.job);
		search_result = (LinearLayout) v.findViewById(R.id.search_result);
		
		sub_layout = (LinearLayout) v.findViewById(R.id.sub_layout);
		sub_layout.setVisibility(View.GONE);
		txtfunction = (TextView) v.findViewById(R.id.txtfunction);
		txtlocation = (TextView) v.findViewById(R.id.txtlocation);
		
		function_layout = (RelativeLayout) v.findViewById(R.id.function_layout);
		function_layout.setOnClickListener(this);

		location_layout = (RelativeLayout) v.findViewById(R.id.location_layout);
		location_layout.setOnClickListener(this);
		
		welcome_layout = (LinearLayout) v.findViewById(R.id.welcome_layout);
		
		btnMenu = (ImageView) v.findViewById(R.id.btnMenu);
		btnMenu.setOnClickListener(this);
		
		btnMatch = (ImageView) v.findViewById(R.id.btnMatch);
		btnMatch.setOnClickListener(this);
		
		txt_new_badge = (TextView) v.findViewById(R.id.txt_new_badge);
		
		flingContainer = (SwipeFlingAdapterView) v.findViewById(R.id.frame);
		
		btnDisLike = (ImageView) v.findViewById(R.id.btnDisLike);
		btnDisLike.setOnClickListener(this);
		
		btnDetails = (ImageView) v.findViewById(R.id.btnDetails);
		btnDetails.setOnClickListener(this);
		
		btnLike = (ImageView) v.findViewById(R.id.btnLike);
		btnLike.setOnClickListener(this);
		
		int job_index = prefs.getInt("job_function_index", 0);
		if (job_index != 0) {
			welcome_layout.setVisibility(View.GONE);
			job.setVisibility(View.GONE);
			search_result.setVisibility(View.VISIBLE);
			
			String str_job_function = arr_job_function[prefs.getInt("job_function_index", 0)];
			job_function.setText(str_job_function);
			txtfunction.setText(str_job_function);
	    }
	    
	    if (!prefs.getString("address", "").equals("")) {
	        welcome_layout.setVisibility(View.GONE);
	        job.setVisibility(View.GONE);
			search_result.setVisibility(View.VISIBLE);
			
			location.setText(prefs.getString("address", ""));
			txtlocation.setText(prefs.getString("address", ""));
	    }
		
		match_listView = (ListView) mParent.findViewById(R.id.match_listView);
		macth_adapter = new MatchAdapter(match_data, mParent);
		match_listView.setAdapter(macth_adapter);
		match_listView.setDividerHeight(0);
		
		match_listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				mSlideMenu.close(true);
				
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
		
		if(job_index != 0 || !prefs.getString("address", "").equals("")) {
			new LoadProductTask().execute();
		}
		
		candidate_data.clear();
		job_data.clear();

		if(is_company.equals("1")) {
			candidate_adapter = new CandidateAdapter(candidate_data, this.getActivity());
			flingContainer.setAdapter(candidate_adapter);
		} else {
			job_adapter = new JobAdapter(job_data, this.getActivity());
			flingContainer.setAdapter(job_adapter);
		}	
        
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
            	set_like_dislike(false);
            }

            @Override
            public void onRightCardExit(Object dataObject) {
            	set_like_dislike(true);
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here

            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                View view = flingContainer.getSelectedView();
                RelativeLayout sub = (RelativeLayout) view.findViewById(R.id.sub);
                sub.findViewById(R.id.img_no_interest).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                sub.findViewById(R.id.img_interest).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {

            }
        });
        
        IntentFilter iff= new IntentFilter(GCMNotificationIntentService.PUSH_RECEIVED);
		LocalBroadcastManager.getInstance(mParent).registerReceiver(onPushNotice, iff);
		
		updateBadge(0);
		
		return v;
	}
	
	private void createAppSession() {
        QBAuth.createSession(new QBEntityCallback<QBSession>() {

            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(List<String> list) {

            }

			@Override
			public void onSuccess(QBSession arg0, Bundle arg1) {
				// TODO Auto-generated method stub
				
			}
        });
    }
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = null;
		switch(v.getId()) {
			case R.id.btnMenu:
				mSlideMenu.open(false, true);
				mParent.getNotification();
				break;
			case R.id.btnMatch:
				mSlideMenu.open(true, true);
				new LoadMatchTask().execute();
				break;
			case R.id.btnDisLike:
				if(is_company.equals("0")) {
					if(job_data.size() > 0) {
						flingContainer.getTopCardListener().selectLeft();
					}
				} else {
					if(candidate_data.size() > 0) {
						flingContainer.getTopCardListener().selectLeft();
					}
				}
				break;
			case R.id.btnDetails:
				if(is_company.equals("0")) {
					if(job_data.size() > 0) {
						intent = new Intent(mParent, JobDetailsActivity.class);
						startActivity(intent);
					}
				} else {
					if(candidate_data.size() > 0) {
						intent = new Intent(mParent, CandidateProfileActivity.class);
						intent.putExtra("index", -1);
						startActivity(intent);
					}
				}
				break;
			case R.id.btnLike:
				if(is_company.equals("0")) {
					if(job_data.size() > 0) {
						flingContainer.getTopCardListener().selectRight();
					}
				} else {
					if(candidate_data.size() > 0) {
						flingContainer.getTopCardListener().selectRight();
					}
				}
				break;
				
			case R.id.txt_home_title:
				showSubLayout();
				break;
			case R.id.txt_location:
				showSubLayout();
				break;
			case R.id.job_function:
				showSubLayout();
				break;
			case R.id.location:
				showSubLayout();
				break;
			case R.id.function_layout:
				intent = new Intent(mParent, JobFunction.class);
				startActivityForResult(intent, FUNCTION);
				break;
			case R.id.location_layout:
				intent = new Intent(mParent, Location.class);
				startActivityForResult(intent, LOCATION);
				break;
		}
	}
	
	private void showSubLayout() {
		if(bUpArrow) {
			bUpArrow = false;
			sub_layout.setVisibility(View.VISIBLE);
		} else {
			bUpArrow = true;
			sub_layout.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mParent);
        SharedPreferences.Editor e = prefs.edit();
        
		if(resultCode == Activity.RESULT_OK) {
			switch(requestCode) {
				case FUNCTION:
					job_function.setText(data.getStringExtra("selected_title"));
					txtfunction.setText(data.getStringExtra("selected_title"));
					
					job.setVisibility(View.GONE);
					search_result.setVisibility(View.VISIBLE);
					sub_layout.setVisibility(View.GONE);
					
					welcome_layout.setVisibility(View.GONE);
				    
	                e.putInt("job_function_index", data.getIntExtra("select_index", -1));
	                e.putBoolean("mbJobFunction", true);
	                
	                if(data.getIntExtra("select_index", -1) == 0) {
	                	e.putBoolean("mbJobFunction", false);
	                }
	                
	                e.commit();
				    
					new LoadProductTask().execute();
					
					break;
				case LOCATION:
					
					if(data.getStringExtra("any").equals("0")) {
						
						e.putString("address", data.getStringExtra("location"));
						e.putString("lat", data.getStringExtra("lat"));
						e.putString("lot", data.getStringExtra("lot"));
						
						location.setText(data.getStringExtra("location"));
						txtlocation.setText(data.getStringExtra("location"));
						
						e.putBoolean("mbLocation", true);
					} else {
						location.setText("Any Location");
						txtlocation.setText("Any Location");
						
						e.putString("address", "Any Location");
						
						int job_index = prefs.getInt("job_function_index", 0);
						
						if (job_index != 0) {
							job_function.setText(arr_job_function[job_index]);
							txtfunction.setText(arr_job_function[job_index]);
						}
						
						e.putBoolean("mbLocation", false);
					}
					
					job.setVisibility(View.GONE);
					search_result.setVisibility(View.VISIBLE);
					sub_layout.setVisibility(View.GONE);
					
					welcome_layout.setVisibility(View.GONE);
				    
				    e.commit();
				    
					new LoadProductTask().execute();
					
					break;
			}
		}
	}

	private void set_like_dislike(boolean bLike) {
		
		JobInfo info = null;
		UserInfo userinfo = null;
		
    	if(is_company.equals("0")) {
			info = job_data.get(0);
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
	    	params.add(new BasicNameValuePair("post_id", info.getJob_id()));
	    	
	    	String api_url = "";
	    	if(bLike) {
	    		api_url = "freelancer/like";
	    	} else {
	    		api_url = "freelancer/dislike";
	    	}
	    	
	    	JSONObject result = null;
	   		result = APIManager.getInstance().callPost(mParent, api_url, params, true);
	   		
	   		try {
	    		
	    		if(!result.getString("success").equals("1")) {
	    			Toast.makeText(mParent, "Internet Connection Error",
				              Toast.LENGTH_SHORT).show();
	    		} else {
    				job_data.remove(0);
	                job_adapter.notifyDataSetChanged();
	    		}
	    	} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	   		
	   		job_count--;
	   		if(job_count > 99) {
	   			txt_job_count.setText("99++");
	   		} else {
	   			txt_job_count.setText(String.valueOf(job_count));
	   		}
		} else {
			userinfo = candidate_data.get(0);
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
	    	params.add(new BasicNameValuePair("freelancer_id", userinfo.getUser_id()));
	    	
	    	String api_url = "";
	    	if(bLike) {
	    		api_url = "client/invite_like";
	    	} else {
	    		api_url = "client/invite_dislike";
	    	}
	    	
	    	JSONObject result = null;
	   		result = APIManager.getInstance().callPost(mParent, api_url, params, true);
	   		
	   		try {
	    		
	    		if(!result.getString("success").equals("1")) {
	    			Toast.makeText(mParent, "Internet Connection Error",
				              Toast.LENGTH_SHORT).show();
	    		} else {
	    			candidate_data.remove(0);
	                candidate_adapter.notifyDataSetChanged();
	    		}
	    	} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            
            candidate_count--;
            if(candidate_count > 99) {
            	txt_job_count.setText("99+");
            } else {
            	txt_job_count.setText(String.valueOf(candidate_count));
            }
		}
	}
	
	class LoadProductTask extends AsyncTask<String, Integer, ArrayList<UserInfo>> {
        private ProgressDialog progressDialog;
        
        protected void onPreExecute() {
        	progressDialog = ProgressDialog.show(mParent, "", "Loading...", true);
        }
        
        @Override
        protected void onPostExecute(ArrayList<UserInfo> result) {
        	
        	if(is_company.equals("0")) {
        		job_adapter.notifyDataSetChanged();
        	} else {
        		candidate_adapter.notifyDataSetChanged();
        	}
        	
        	int count = (is_company.equals("0")) ? job_count : candidate_count;
        	if(count > 0) {
        		txt_job_count.setVisibility(View.VISIBLE);
        		if(count > 99) {
        			txt_job_count.setText("99+");
        		} else {
        			txt_job_count.setText(String.valueOf(count));
        		}
        	} 
        	
            progressDialog.dismiss();
        }
 
        @Override
        protected ArrayList<UserInfo> doInBackground(String... param) {
        	
        	job_data.clear();
        	candidate_data.clear();
        	
        	JSONObject result = null;
        	
        	List<NameValuePair> params = new ArrayList<NameValuePair>();
        	
        	int job_index = prefs.getInt("job_function_index", 0);
        	
//        	if (!prefs.getString("address", "").equals("Any Location")) {
	        	if(job_index != 0 && prefs.getBoolean("mbLocation", false)) {
	    	        params.add(new BasicNameValuePair("job_function", String.valueOf(prefs.getInt("job_function_index", 0))));
	    	        params.add(new BasicNameValuePair("latitude", prefs.getString("lat", "")));
	    	        params.add(new BasicNameValuePair("longitude", prefs.getString("lot", "")));
	        	} else if(job_index != 0) {
	    	        params.add(new BasicNameValuePair("job_function", String.valueOf(prefs.getInt("job_function_index", 0))));
	        	} else if(prefs.getBoolean("mbLocation", false)) {
	    	        params.add(new BasicNameValuePair("latitude", prefs.getString("lat", "")));
	    	        params.add(new BasicNameValuePair("longitude", prefs.getString("lot", "")));
	        	}
//        	}
        	
        	if(is_company.equals("0")) {
        		result =  APIManager.getInstance().callPost(mParent, "freelancer/get_post", params, true);
        	} else {
        		result =  APIManager.getInstance().callPost(mParent, "client/get_freelancer", params, true);
        	}
       		        	
        	try {
        		
        		if(result.getString("success").equals("1")) {
        			
        			if(is_company.equals("0")) {
		        		JSONArray jarray = result.getJSONArray("posts");
						
		        		job_count = jarray.length();
						for (int i = 0; i < jarray.length(); i++) {
							JSONObject c = jarray.getJSONObject(i);
							
							JobInfo job = new JobInfo();
							job.setJob_id(c.getString("id"));
							job.setUser_id(c.getString("user_id"));
							job.setJob_title(c.getString("post_title"));
							job.setJob_function(c.getString("main_cat_id"));
							job.setLocation(c.getString("location"));
							job.setLatitude(c.getString("lat"));
							job.setRadius(c.getString("radius"));
							job.setRelocate(c.getString("relocate"));
							job.setRemote(c.getString("remote"));
							job.setExperience_year(c.getString("experience_year"));
							job.setSalary_min(c.getString("salary_min"));
							job.setSalary_max(c.getString("salary_max"));
							job.setSkill_ids(c.getString("skill_ids"));
							job.setPerks(c.getString("perks"));
							job.setDescription(c.getString("description"));
							job.setCreated(c.getString("created"));
							job.setQualification(c.getString("qualification"));
							
							JSONObject user = c.getJSONObject("client");
							
							UserInfo info = new UserInfo();
							
							info.setUser_id(user.getString("id"));
							info.setFirst_name(user.getString("first_name"));
							info.setLast_name(user.getString("last_name"));
							info.setEmail(user.getString("email"));
							info.setCompany(user.getString("company"));
							info.setCompany_avatar(user.getString("companyAvatar"));
							info.setCompany_description(user.getString("companyDescr"));
							info.setAvatar(user.getString("avatar"));
							info.setHeadline(user.getString("headline"));
							info.setLocation(user.getString("location"));
							info.setLatitude(user.getString("lat"));
							info.setLongitude(user.getString("lot"));
							info.setMain_cat_id(user.getString("main_cat_id"));
							info.setSub_cat_id(user.getString("sub_cat_id"));
							info.setExperience_year(user.getString("experience_year"));
							
							experience_data = new ArrayList<Experience>();
							JSONArray arr_experience = user.getJSONArray("experience");
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
							
							education_data = new ArrayList<Education>();
							JSONArray arr_education = user.getJSONArray("education");
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
							info.setExperience(experience_data);
							info.setEducation(education_data);
							
							info.setSkill_ids(user.getString("skill_ids"));
							info.setOnline(user.getString("online"));
							info.setStatus(user.getString("status"));
							info.setSocial_id(user.getString("social_id"));
							info.setCreated(user.getString("created"));
							info.setSalary_min(user.getString("salary_min"));
							info.setSalary_max(user.getString("salary_max"));
							
							if(user.has("matched")) {
								info.setMatched(user.getString("matched"));
							} else {
								info.setMatched("");
							}
							
							if(user.has("message_count")) {
								info.setMessage_count(user.getString("message_count"));
							} else {
								info.setMessage_count("");
							}
							
							job.setUserInfo(info);
							
							job_data.add(job);
						}
        			} else {
        				JSONArray jarray = result.getJSONArray("candidates");
						
        				candidate_count = jarray.length();
						for (int i = 0; i < jarray.length(); i++) {
							JSONObject user = jarray.getJSONObject(i);
							
							UserInfo info = new UserInfo();
							
							info.setUser_id(user.getString("id"));
							info.setFirst_name(user.getString("first_name"));
							info.setLast_name(user.getString("last_name"));
							info.setEmail(user.getString("email"));
							info.setCompany(user.getString("company"));
							info.setAvatar(user.getString("avatar"));
							info.setHeadline(user.getString("headline"));
							info.setLocation(user.getString("location"));
							info.setLatitude(user.getString("lat"));
							info.setLongitude(user.getString("lot"));
							info.setMain_cat_id(user.getString("main_cat_id"));
							info.setSub_cat_id(user.getString("sub_cat_id"));
							info.setExperience_year(user.getString("experience_year"));
							
							experience_data = new ArrayList<Experience>();
							JSONArray arr_experience = user.getJSONArray("experience");
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
							
							education_data = new ArrayList<Education>();
							JSONArray arr_education = user.getJSONArray("education");
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
							info.setExperience(experience_data);
							info.setEducation(education_data);
							
							info.setSkill_ids(user.getString("skill_ids"));
							info.setOnline(user.getString("online"));
							info.setStatus(user.getString("status"));
							info.setSocial_id(user.getString("social_id"));
							info.setCreated(user.getString("created"));
							info.setSalary_min(user.getString("salary_min"));
							info.setSalary_max(user.getString("salary_max"));
							
							if(user.has("matched")) {
								info.setMatched(user.getString("matched"));
							} else {
								info.setMatched("");
							}
							
							if(user.has("message_count")) {
								info.setMessage_count(user.getString("message_count"));
							} else {
								info.setMessage_count("");
							}
							
							candidate_data.add(info);
						}
        			}
        		}
        	} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

            return null;
        }
    }
	
	class JobAdapter extends ArrayAdapter<JobInfo> {
		
		Context context;
		public JobAdapter(ArrayList<JobInfo> itemList, Context ctx) {
			super(ctx, 0, itemList);
			context = ctx;
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			
			View v = convertView;
			if (v == null) {
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				
				v = inflater.inflate(R.layout.home_job_item, null);
			}
			
			RelativeLayout sub = (RelativeLayout) v.findViewById(R.id.sub);
			
			JobInfo info = job_data.get(position);
			UserInfo user_info = info.getUserInfo();
			
			String avatar_url = "";
			LinearLayout no_company = (LinearLayout) v.findViewById(R.id.no_company);
			ImageView company_picture = (ImageView) v.findViewById(R.id.company_picture);
			
			if(user_info.getCompany_avatar().equals("")) {
				no_company.setVisibility(View.VISIBLE);
				company_picture.setVisibility(View.GONE);
				
				String first_text = user_info.getCompany().substring(0, 1);
				
				TextView company_first = (TextView) v.findViewById(R.id.company_first);
				company_first.setText(first_text);
				
				TextView txt_company = (TextView) v.findViewById(R.id.txt_company);
				txt_company.setText(user_info.getCompany());
				
			} else {
				no_company.setVisibility(View.GONE);
				company_picture.setVisibility(View.VISIBLE);
				
				avatar_url = String.format("%s%s", Constants.COMPANY_UPLOAD, user_info.getCompany_avatar());
				UrlImageViewHelper.setUrlDrawable(company_picture, avatar_url);
			}
			
			TextView txt_job_title = (TextView) v.findViewById(R.id.txt_job_title);
			txt_job_title.setText(info.getJob_title());
			
			TextView txt_location = (TextView) v.findViewById(R.id.txt_location);
			String str_location = (info.getLocation().equals("")) ? "-" : info.getLocation();
			txt_location.setText(str_location);
			
			TextView txt_experience_year = (TextView) v.findViewById(R.id.txt_experience_year);
			String str_experience = (Integer.valueOf(info.getExperience_year()).intValue() > 1) ? "years" : "year";
			txt_experience_year.setText(String.format("%s %s", info.getExperience_year(), str_experience));
			
			TextView txt_salary = (TextView) v.findViewById(R.id.txt_salary);
			txt_salary.setText(String.format("%s ~ %sK", info.getSalary_min(), info.getSalary_max()));
			
			TextView txt_headline = (TextView) v.findViewById(R.id.txt_headline);
			txt_headline.setText(String.format("HIRING FROM %s", user_info.getCompany()));
			
			TextView txt_user_name = (TextView) v.findViewById(R.id.txt_user_name);
			txt_user_name.setText(String.format("%s %s", user_info.getFirst_name(), user_info.getLast_name()));
			
			CircularImageView user_picture = (CircularImageView) v.findViewById(R.id.user_picture);
			avatar_url = String.format("%s%s", Constants.PHOTO_UPLOAD, user_info.getAvatar());
			UrlImageViewHelper.setUrlDrawable(user_picture, avatar_url);

			final TextView txt_description = (TextView) v.findViewById(R.id.txt_description);
			txt_description.setText(info.getDescription());
			ViewTreeObserver vto = txt_description.getViewTreeObserver();
		    vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
	
		        @Override
		        public void onGlobalLayout() {
		            ViewTreeObserver obs = txt_description.getViewTreeObserver();
		            obs.removeGlobalOnLayoutListener(this);
	                int lineEndIndex = txt_description.getLayout().getLineEnd(1);
	                if(lineEndIndex > 2) {
		                String text = txt_description.getText().subSequence(0, lineEndIndex - 2)+"...";
		                
		                txt_description.setText(Html.fromHtml(text).toString());
	                }
		        }
		    });

			String skills = "";
			String str_skills = info.getSkill_ids();
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
			
			TextView txt_skills = (TextView) v.findViewById(R.id.txt_skills);
			txt_skills.setText(skills);
				
			return sub;
		}
	}
	
	class CandidateAdapter extends ArrayAdapter<UserInfo> {
		
		Context context;
		public CandidateAdapter(ArrayList<UserInfo> itemList, Context ctx) {
			super(ctx, 0, itemList);
			context = ctx;
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			
			View v = convertView;
			if (v == null) {
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				
				v = inflater.inflate(R.layout.home_candidate_item, null);
			}
			
			RelativeLayout sub = (RelativeLayout) v.findViewById(R.id.sub);
			
			UserInfo info = candidate_data.get(position);
			
			TextView txt_job_function = (TextView) sub.findViewById(R.id.txt_job_function);
			int job_index = Integer.valueOf(info.getMain_cat_id()).intValue();
			txt_job_function.setText(arr_job_function[job_index]);
			
			TextView txt_headline = (TextView) sub.findViewById(R.id.txt_headline);
			txt_headline.setText(info.getHeadline());

			TextView txt_location = (TextView) sub.findViewById(R.id.txt_location);
			txt_location.setText(info.getLocation());
			
			int year_count = Integer.valueOf(info.getExperience_year()).intValue();
			String str = (year_count > 1) ? "years" : "year";
			
			TextView txt_experience_year = (TextView) sub.findViewById(R.id.txt_experience_year);
			txt_experience_year.setText(String.format("%s %s", info.getExperience_year(), str));
			
			long login_timestamp = Long.valueOf(info.getCreated()).longValue();
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
		    
			TextView txt_last_activity = (TextView) sub.findViewById(R.id.txt_last_activity);
			txt_last_activity.setText(str);
			
			RelativeLayout experience_layout = (RelativeLayout) sub.findViewById(R.id.experience_layout);
			RelativeLayout education_layout = (RelativeLayout) sub.findViewById(R.id.education_layout);
			LinearLayout skill_layout = (LinearLayout) sub.findViewById(R.id.skill_layout);
			
			LinearLayout experience_1 = (LinearLayout) sub.findViewById(R.id.experience_1);
			TextView txt_headline1 = (TextView) sub.findViewById(R.id.txt_headline1);
			TextView txt_company_name1 = (TextView) sub.findViewById(R.id.txt_company_name1);
			TextView txt_date1 = (TextView) sub.findViewById(R.id.txt_date1);

			int experience_count = info.getExperience().size();
			if(experience_count > 0) {
				ArrayList<Experience> item = info.getExperience();
				Experience model = item.get(experience_count - 1);
				
				txt_headline1.setText(model.getHeadline());
				txt_company_name1.setText(model.getCompany());
				txt_date1.setText(String.format("%s ~ %s", model.getStart_date(), model.getEnd_date()));
			} else {
				experience_layout.setVisibility(View.GONE);
			}
			
			TextView txt_school = (TextView) sub.findViewById(R.id.txt_school);
			TextView txt_degree = (TextView) sub.findViewById(R.id.txt_degree);
			TextView txt_date2 = (TextView) sub.findViewById(R.id.txt_date2);
			
			int education_count = info.getEducation().size(); 
			if(education_count > 0) {
				ArrayList<Education> item = info.getEducation();
				Education model = item.get(education_count - 1);
				
				txt_school.setText(model.getSchool());
				txt_degree.setText(model.getDegree());
				txt_date2.setText(String.format("%s ~ %s", model.getStart_date(), model.getEnd_date()));
			} else {
				education_layout.setVisibility(View.GONE);
			}
			
			String skills = "";
			String str_skills = info.getSkill_ids();
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
			
			TextView txt_skill = (TextView) sub.findViewById(R.id.txt_skill);
			txt_skill.setText(skills);
			
			if(skills.equals("")) {
				skill_layout.setVisibility(View.GONE);
			}
			
			return sub;
		}
	}

	private String getDate(String str) {
		long timestemp = Long.valueOf(str) * 1000;
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestemp);
		return String.format("%s/%s", cal.get(Calendar.MONTH), cal.get(Calendar.YEAR));
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
				username.setTextColor(Color.parseColor("#41c289"));
				txt_headline.setTextColor(Color.parseColor("#41c289"));
				company_name.setTextColor(Color.parseColor("#41c289"));
			} else {
				username.setTextColor(Color.parseColor("#8a8a8a"));
				txt_headline.setTextColor(Color.parseColor("#8a8a8a"));
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

	public void updateBadge(int status) {
    	
		if(status == 0) {
			new LoadBadgeTask().execute();
		} else if(status == 1) {
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
        	
        	if(notification_badge == 0){
        		txt_new_badge.setVisibility(View.GONE);
        	}else if(notification_badge > 0){
        		txt_new_badge.setVisibility(View.VISIBLE);
        		txt_new_badge.setText(String.valueOf(notification_badge));
        	}
        	
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
    
    private void createSession(final String login, final String password, final UserInfo userinfo, final int index) {

        final QBUser user = new QBUser(login, password);
        QBAuth.createSession(login, password, new QBEntityCallbackImpl<QBSession>() {
            @Override
            public void onSuccess(QBSession session, Bundle bundle) {
                Log.d(TAG, "onSuccess create session with params");
                user.setId(session.getUserId());

                if (mParent.chatService.isLoggedIn()){
                    startCallActivity(userinfo, index);
                } else {
                	mParent.chatService.login(user, new QBEntityCallbackImpl<QBUser>() {

                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "onSuccess login to chat");

                            startCallActivity(userinfo, index);
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
		intent.putExtra("home", true);
		startActivity(intent);

    }
    
}
