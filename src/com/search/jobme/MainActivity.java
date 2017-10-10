package com.search.jobme;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.nostra13.socialsharing.common.AuthListener;
import com.nostra13.socialsharing.facebook.FacebookFacade;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.users.model.QBUser;
import com.search.jobme.ShareDialog.OnMyDialogResult;
import com.search.jobme.ImageLoader.ImageLoader;
import com.search.jobme.model.Education;
import com.search.jobme.model.Experience;
import com.search.jobme.model.NotificationModel;
import com.search.jobme.model.SkillModel;
import com.search.jobme.model.UserInfo;
import com.search.jobme.until.APIManager;
import com.search.jobme.until.FacebookEventObserver;

import me.tangke.slidemenu.SlideMenu;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class MainActivity extends BaseSlideMenuActivity implements OnClickListener {
	
	private static final String TAG = "MainActivity";
	
	private SlideMenu mSlideMenu;
	
	CircularImageView user_picture;
	TextView txt_user_name;
	TextView txt_edit_profile;
	
	LinearLayout candidate_layout, manage_layout, notification_layout, match_layout, setting_layout, contact_layout, invite_layout;
	
	ImageView img_candidate, img_manage, img_match, img_setting, img_contact, img_invite;
	TextView txt_candidate, txt_notification_count;
	
	ListView match_listView;
	public ArrayList<UserInfo> match_data = new ArrayList<UserInfo>();
	public static ArrayList<SkillModel> skill_data = new ArrayList<SkillModel>();
	public static ArrayList<NotificationModel> notification_data = new ArrayList<NotificationModel>();
	
	public static boolean mbJobFunction;
	public static boolean mbLocation;
	public static int job_function_index;
	public static String address;
	public static String lat;
	public static String lot;
	
	SearchAdapter adapter;
	
	Context context;
	
	int notification;
	Intent intent;
	
	SharedPreferences prefs;
	private String[] arr_job_function;
	
	public static QBChatService chatService;
	
    ArrayList<Experience> experience_data;
    ArrayList<Education> education_data;
    
    int notification_count = 0;

    private FacebookFacade facebook;
	private FacebookEventObserver facebookEventObserver;
	private Map<String, String> actionsMap;
	
	@SuppressLint("NewApi")
	@Override
	public void onContentChanged() {
		super.onContentChanged();
		setSlideRole(R.layout.activity_main);
		setSlideRole(R.layout.left_menu);
		setSlideRole(R.layout.match_list);
		mSlideMenu = getSlideMenu();
		
		context = this;
		arr_job_function = getResources().getStringArray(R.array.main_cat);
		
		QBSettings.getInstance().fastConfigInit(Constants.APP_ID, Constants.AUTH_KEY, Constants.AUTH_SECRET);
		
        QBChatService.setDebugEnabled(true);
        if (!QBChatService.isInitialized()) {
            QBChatService.init(this);
            chatService = QBChatService.getInstance();
        }
        createAppSession();
        
        facebook = new FacebookFacade(this, "1633315070245562");
		facebookEventObserver = FacebookEventObserver.newInstance();
        
        new LoadSkillTask().execute();
        getNotification();
        
		Intent intent = getIntent();
		notification = intent.getIntExtra("notification", 0);

		mSlideMenu.setSlideMode(SlideMenu.MODE_SLIDE_WINDOW);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		user_picture = (CircularImageView) findViewById(R.id.user_picture);
		String avatar_url = String.format("%s%s", Constants.PHOTO_UPLOAD, prefs.getString("avatar", ""));
		UrlImageViewHelper.setUrlDrawable(user_picture, avatar_url);
		
		txt_user_name = (TextView) findViewById(R.id.txt_user_name);
		txt_user_name.setText(String.format("%s %s", prefs.getString("first_name", ""), prefs.getString("last_name", "")));
		
		txt_edit_profile = (TextView) findViewById(R.id.txt_edit_profile);
		txt_edit_profile.setOnClickListener(this);
		
		String is_employer = prefs.getString("is_employer", "0");
		
		candidate_layout = (LinearLayout) findViewById(R.id.candidate_layout);
		candidate_layout.setOnClickListener(this);
		
		manage_layout = (LinearLayout) findViewById(R.id.manage_layout);
		manage_layout.setOnClickListener(this);
		
		notification_layout = (LinearLayout) findViewById(R.id.notification_layout);
		notification_layout.setOnClickListener(this);

		match_layout = (LinearLayout) findViewById(R.id.match_layout);
		match_layout.setOnClickListener(this);
		
		setting_layout = (LinearLayout) findViewById(R.id.setting_layout);
		setting_layout.setOnClickListener(this);
		
		contact_layout = (LinearLayout) findViewById(R.id.contact_layout);
		contact_layout.setOnClickListener(this);
		
		invite_layout = (LinearLayout) findViewById(R.id.invite_layout);
		invite_layout.setOnClickListener(this);
		
		img_candidate = (ImageView) findViewById(R.id.img_candidate);
		img_manage = (ImageView) findViewById(R.id.img_manage);
		img_match = (ImageView) findViewById(R.id.img_match);
		img_setting = (ImageView) findViewById(R.id.img_setting);
		img_contact = (ImageView) findViewById(R.id.img_contact);
		img_invite = (ImageView) findViewById(R.id.img_invite);
		
		txt_candidate = (TextView) findViewById(R.id.txt_candidate);
		txt_notification_count = (TextView) findViewById(R.id.txt_notification_count);
		
		if(is_employer.equals("0")) {
			txt_candidate.setText("Jobs");
			notification_layout.setVisibility(View.VISIBLE);
			manage_layout.setVisibility(View.GONE);
		} else {
			txt_candidate.setText("Candidates");
			manage_layout.setVisibility(View.VISIBLE);
			notification_layout.setVisibility(View.GONE);
		}
		
		//Match Right Menu
		match_listView = (ListView) findViewById(R.id.match_listView);
		adapter = new SearchAdapter(match_data, this);
		match_listView.setAdapter(adapter);
		match_listView.setDividerHeight(0);
		
		match_listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
				UserInfo info = match_data.get(position);
				
				String qlogin = prefs.getString("email", "");
				String qpassword = prefs.getString("password", "");
				
				createSession(qlogin, qpassword, info, position);
			}
		});		
		
		FragmentManager fragmentManager = getFragmentManager();

		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.replace(R.id.fragment_layout, new HomeFragment());
		fragmentTransaction.commit();
		
		if(notification == 1) {
			setButtonImage(3);
		}
		
		IntentFilter match= new IntentFilter(GCMNotificationIntentService.MATCH);
		LocalBroadcastManager.getInstance(this).registerReceiver(onMatchNotice, match);
	}	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.txt_edit_profile:
				setButtonImage(0);
				break;
			case R.id.candidate_layout:
				setButtonImage(1);
				break;
			case R.id.manage_layout:
				setButtonImage(2);
				break;
			case R.id.notification_layout:
				setButtonImage(3);
				break;
			case R.id.match_layout:
				setButtonImage(4);
				break;
			case R.id.setting_layout:
				setButtonImage(5);
				break;
			case R.id.contact_layout:
				setButtonImage(6);
				break;
			case R.id.invite_layout:
				setButtonImage(7);
				break;
		}
	}
	
	private void createAppSession() {
        QBAuth.createSession(new QBEntityCallback<QBSession>() {

            @Override
            public void onSuccess() {
            	Log.d("", "");
            }

            @Override
            public void onError(List<String> list) {
            	Log.d("", "");
            }

			@Override
			public void onSuccess(QBSession arg0, Bundle arg1) {
				// TODO Auto-generated method stub
				Log.d("", "");
			}
        });
    }

	@SuppressLint("NewApi")
	private void setButtonImage(int index) {
		
		Fragment fr = null;
		
		if(index < 4) {
			if(index == 0) {
				fr = new EditProfile();				
			} else if(index == 1) {
				fr = new HomeFragment();	
			} else if(index == 2) {				
				fr = new ManageFragment();
			} else if(index == 3) {
				fr = new NotificationFragment();
			}
			
			mSlideMenu.close(true);

			FragmentManager fragmentManager = getFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.replace(R.id.fragment_layout, fr);
			fragmentTransaction.commit();
		} else if(index == 4) {
			
			mSlideMenu.close(true);
			mSlideMenu.open(true, true);
			
			new LoadMatchTask().execute();
			
		} else if(index == 5) {
			startActivity(new Intent(this, SettingActivity.class));
		} else if(index == 6) {
			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			String[] recipients = new String[]{"info@jobme.co.nz", "",};

			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, recipients);
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Android Device");
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, Constants.share_string);
			emailIntent.setType("text/plain");

			startActivity(Intent.createChooser(emailIntent, "Send mail..."));
		} else if(index == 7) {
			ShareDialog statusDialog = new ShareDialog(this);
			
	        Window window = statusDialog.getWindow();
	        window.setGravity(Gravity.BOTTOM);
	        statusDialog.show();
	        
	        statusDialog.setDialogResult(new OnMyDialogResult() {

				@Override
				public void finish(String result) {
					// TODO Auto-generated method stub
					if(result.equals("facebook")) {
						if (facebook.isAuthorized()) {
							publishMessage();
						} else {
							// Start authentication dialog and publish message after successful authentication
							facebook.authorize(new AuthListener() {
								@Override
								public void onAuthSucceed() {
									publishMessage();
								}

								@Override
								public void onAuthFail(String error) { // Do noting
								}
							});
						}
					}
				}
				
			});
		} 
	}
	
	class LoadMatchTask extends AsyncTask<String, Integer, ArrayList<UserInfo>> {
        private ProgressDialog progressDialog;
        
        protected void onPreExecute() {
        	progressDialog = ProgressDialog.show(context, "", "Loading...", true);
        }
        
        @Override
        protected void onPostExecute(ArrayList<UserInfo> result) {
        	adapter = new SearchAdapter(match_data, context);
        	match_listView.setAdapter(adapter);
    		
            progressDialog.dismiss();
        }
 
        @Override
        protected ArrayList<UserInfo> doInBackground(String... param) {
        	
        	match_data.clear();
        	
        	JSONObject result = null;
       		result =  APIManager.getInstance().callPost(context, "client/get_matches", null, true);
        	
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

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
	}
	
	private BroadcastReceiver onMatchNotice= new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
        	
        	Intent _intent = new Intent(context, MatchActivity.class);
        	_intent.putExtra("message", intent.getStringExtra("message"));
        	_intent.putExtra("sender_id", intent.getStringExtra("sender_id"));
        	_intent.putExtra("sender_photo", intent.getStringExtra("sender_photo"));
        	_intent.putExtra("sender_name", intent.getStringExtra("sender_name"));
        	_intent.putExtra("company_name", intent.getStringExtra("company_name"));
        	_intent.putExtra("headline", intent.getStringExtra("headline"));
        	_intent.putExtra("job_main_cat_id", intent.getStringExtra("job_main_cat_id"));
        	_intent.putExtra("last_company", intent.getStringExtra("last_company"));
        	
        	startActivity(_intent);        	
        }
    };
    
    private void createSession(final String login, final String password, final UserInfo userinfo, final int position) {

        final QBUser user = new QBUser(login, password);
        QBAuth.createSession(login, password, new QBEntityCallbackImpl<QBSession>() {
            @Override
            public void onSuccess(QBSession session, Bundle bundle) {
                Log.d(TAG, "onSuccess create session with params");
                user.setId(session.getUserId());

                if (chatService.isLoggedIn()){
                    startCallActivity(userinfo, position);
                } else {
                    chatService.login(user, new QBEntityCallbackImpl<QBUser>() {

                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "onSuccess login to chat");

                            startCallActivity(userinfo, position);
                        }

                        @Override
                        public void onError(List errors) {

                        	Toast.makeText(MainActivity.this, "Error when login", Toast.LENGTH_SHORT).show();
                            for (Object error : errors) {
                                Log.d(TAG, error.toString());
                            }
                        }
                    });
                }

            }

            @Override
            public void onError(List<String> errors) {

                Toast.makeText(MainActivity.this, "Error when login, check test users login and password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startCallActivity(UserInfo info, int index) {
		
		Intent intent = new Intent(MainActivity.this, ChatActivity.class);
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
		startActivity(intent);

    }
    
    class LoadSkillTask extends AsyncTask<String, Integer, ArrayList<SkillModel>> {
        private ProgressDialog progressDialog;
        
        protected void onPreExecute() {
        	progressDialog = ProgressDialog.show(context, "", "Loading...", true);
        }
        
        @Override
        protected void onPostExecute(ArrayList<SkillModel> result) {
            progressDialog.dismiss();
        }
 
        @Override
        protected ArrayList<SkillModel> doInBackground(String... param) {
        	
        	skill_data.clear();
        	
        	JSONObject result = null;
       		result =  APIManager.getInstance().callPost(context, "getSkill", null, true);
        	
        	try {
        		
        		if(result.getString("success").equals("1")) {
	        		JSONArray jarray = result.getJSONArray("skill");
					
					for (int i = 0; i < jarray.length(); i++) {
						JSONObject c = jarray.getJSONObject(i);
						
						SkillModel info = new SkillModel();
						
						info.setId(c.getString("id"));
						info.setTitle(c.getString("title"));
						
						skill_data.add(info);
					}
        		}
        	} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

            return null;
        }
    }
    
    public void getNotification() {
    	new LoadNotificationTask().execute();
    }
    
    class LoadNotificationTask extends AsyncTask<String, Integer, ArrayList<SkillModel>> {
        private ProgressDialog progressDialog;
        
        protected void onPreExecute() {
        	progressDialog = ProgressDialog.show(context, "", "Loading...", true);
        }
        
        @Override
        protected void onPostExecute(ArrayList<SkillModel> result) {
            progressDialog.dismiss();
            
            if(notification_count > 0) {
            	txt_notification_count.setVisibility(View.VISIBLE);
            	txt_notification_count.setText(String.valueOf(notification_count));
            } else {
            	txt_notification_count.setVisibility(View.GONE);
            }
        }
 
        @Override
        protected ArrayList<SkillModel> doInBackground(String... param) {
        	
        	notification_data.clear();
        	
        	JSONObject result = null;
       		result =  APIManager.getInstance().callPost(context, "freelancer/get_invite", null, true);
        	
        	try {
        		
        		if(result.getString("success").equals("1")) {
	        		JSONArray jarray = result.getJSONArray("invited");
					
	        		notification_count = jarray.length();
					for (int i = 0; i < jarray.length(); i++) {
						JSONObject c = jarray.getJSONObject(i);
						
						NotificationModel model = new NotificationModel();
						
						model.setEmployer_id(c.getString("client_id"));
						model.setNoti_id(c.getString("noti_id"));
						model.setCreated(c.getString("created"));
						
						JSONObject u = c.getJSONObject("user_info");
						
						UserInfo user_info = new UserInfo();
						user_info.setUser_id(u.getString("id"));
		    			user_info.setFirst_name(u.getString("first_name"));
		    			user_info.setLast_name(u.getString("last_name"));
		    			user_info.setEmail(u.getString("email"));
		    			user_info.setCompany(u.getString("company"));
		    			user_info.setCompany_avatar(u.getString("companyAvatar"));
		    			user_info.setCompany_description(u.getString("companyDescr"));
		    			user_info.setAvatar(u.getString("avatar"));
		    			user_info.setHeadline(u.getString("headline"));
		    			user_info.setLocation(u.getString("location"));
		    			user_info.setLatitude(u.getString("lat"));
		    			user_info.setLongitude(u.getString("lot"));
		    			user_info.setMain_cat_id(u.getString("main_cat_id"));
		    			user_info.setSub_cat_id(u.getString("sub_cat_id"));
		    			user_info.setExperience_year(u.getString("experience_year"));
						
		    			experience_data = new ArrayList<Experience>();
						JSONArray arr_experience = u.getJSONArray("experience");
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
						JSONArray arr_education = u.getJSONArray("education");
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
						
						user_info.setSkill_ids(u.getString("skill_ids"));
						user_info.setOnline(u.getString("online"));
						user_info.setStatus(u.getString("status"));
						user_info.setSocial_id(u.getString("social_id"));
						user_info.setCreated(u.getString("created"));
						user_info.setSalary_min(u.getString("salary_min"));
						user_info.setSalary_max(u.getString("salary_max"));
						
						if(u.has("matched")) {
							user_info.setMatched(u.getString("matched"));
						} else {
							user_info.setMatched("");
						}
						
						if(u.has("message_count")) {
							user_info.setMessage_count(u.getString("message_count"));
						} else {
							user_info.setMessage_count("");
						}
						model.setUserInfo(user_info);
						notification_data.add(model);
					}
        		}
        	} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

            return null;
        }
    }
    
    private void publishMessage() {
    	actionsMap = new HashMap<String, String>();
		actionsMap.put(Constants.FACEBOOK_SHARE_ACTION_NAME, Constants.FACEBOOK_SHARE_ACTION_LINK);
		
		facebook.publishMessage(Constants.FACEBOOK_SHARE_MESSAGE, Constants.FACEBOOK_SHARE_LINK, Constants.FACEBOOK_SHARE_LINK_NAME, Constants.FACEBOOK_SHARE_LINK_DESCRIPTION, Constants.FACEBOOK_SHARE_PICTURE, actionsMap);
	}
    
    @Override
	public void onStart() {
		super.onStart();
		facebookEventObserver.registerListeners(this);
	}

	@Override
	public void onStop() {
		facebookEventObserver.unregisterListeners();
		super.onStop();
	}
}
