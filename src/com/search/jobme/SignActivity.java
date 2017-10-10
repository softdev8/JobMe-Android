package com.search.jobme;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBSettings;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.search.jobme.SignUpActivity.LoadSignupTask;
import com.search.jobme.until.APIManager;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;






import android.support.v4.app.FragmentActivity;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;
import android.preference.PreferenceManager;

public class SignActivity extends FragmentActivity implements OnClickListener {

	private static final String TAG = "SignActivity";
	
	private CallbackManager callbackManager;
    private AccessToken accessToken;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    
	TextView txt_post_job, txt_login;
	
	private PagerAdapter mAdapter;
	private ViewPager mPager;
	private PageIndicator mIndicator;
	
	Context context;
	GoogleCloudMessaging gcm;
	String regID;
	String videochat_id;
	
	public QBChatService chatService;
	String facebookId, firstName, lastName, email, avatar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign);

		context = this;
        registerInBackground();
		
		QBSettings.getInstance().fastConfigInit(Constants.APP_ID, Constants.AUTH_KEY, Constants.AUTH_SECRET);
		
        QBChatService.setDebugEnabled(true);
        if (!QBChatService.isInitialized()) {
            QBChatService.init(this);
            chatService = QBChatService.getInstance();
        }
        createAppSession();
		initializeFaceBook();
		
		txt_post_job = (TextView) findViewById(R.id.txt_post_job);
		txt_post_job.setOnClickListener(this);
		
		txt_login = (TextView) findViewById(R.id.txt_login);
		txt_login.setOnClickListener(this);
		
		mAdapter = new TestFragmentAdapter(getSupportFragmentManager());
		
		mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        
        mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
        
        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int state) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageSelected(int position) {
				mPager.setCurrentItem(position);
			}
        });
        
	}
	
	private void initializeFaceBook() {
        FacebookSdk.sdkInitialize(context);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS);
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logOut();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        accessToken = loginResult.getAccessToken();
                        GetUserProfile();
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException exception) {
                    	Toast.makeText(context, exception.getMessage(),
		  			              Toast.LENGTH_SHORT).show();
                    }
                });

        setAccessTokenTracker();
        setProfileTracker();
    }
	 
	public void onFacebookClick(View view) {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
    }
	
	private void setAccessTokenTracker(){
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.
                accessToken = currentAccessToken;
            }
        };
    }

    private void setProfileTracker(){
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                 LoginManager.getInstance().logOut();
            }
        };
    }
	    
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	private void GetUserProfile() {
        
		final ProgressDialog pd = new ProgressDialog(SignActivity.this);
		pd.setCancelable(false);
		pd.setCanceledOnTouchOutside(false);
		pd.setMessage("Loading...");
		pd.show();
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    
					@Override
					public void onCompleted(JSONObject object,
							GraphResponse response) {
						// TODO Auto-generated method stub
						JSONObject obj = response.getJSONObject();
						try {
							
							facebookId = obj.getString("id");
							avatar = "https://graph.facebook.com/" + facebookId + "/picture?type=large";
							firstName = obj.getString("first_name");
							lastName = obj.getString("last_name");
							email = obj.getString("email");
							
							final QBUser user = new QBUser(email, "12345678");
        		        	user.setEmail(email);
        		        	user.setFullName(String.format("%s %s", firstName, lastName));
        		        	
        		        	QBUsers.signUp(user, new QBEntityCallback<QBUser>() {
        		        	    @Override
        		        	    public void onSuccess(QBUser user, Bundle args) {
        		        	    	
        		        	    	pd.dismiss();
        		        	    	
        		        	    	videochat_id = user.getId().toString();
        		        	    	getFBDetails();
        		        	    }
        		        	 
        		        	    public void onError(QBResponseException errors) {
        		        	    	Log.d("error", "123");
        		        	    }

        						@Override
        						public void onError(List<String> arg0) {
        							// TODO Auto-generated method stub
        							Log.d("error", "456");
        						}

        						@Override
        						public void onSuccess() {
        							// TODO Auto-generated method stub
        							Log.d("error", "qweqw");
        						}
        		        	});
							
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name,email");
        request.setParameters(parameters);
        request.executeAsync();
    }
	
	private void getFBDetails() {
							
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("social_id", facebookId));
	    params.add(new BasicNameValuePair("first_name", firstName));
	    params.add(new BasicNameValuePair("last_name", lastName));
	    params.add(new BasicNameValuePair("email", email));
	    params.add(new BasicNameValuePair("device_id", regID));
	    params.add(new BasicNameValuePair("device_type", "android"));
	    params.add(new BasicNameValuePair("avatar", avatar));
	    params.add(new BasicNameValuePair("videochat_id", videochat_id));
		
		JSONObject result =  APIManager.getInstance().callPost(getBaseContext(), "account/social_signup", params, false);
		
		try {
			if(result.getString("success").equals("1")) {
				
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
	            SharedPreferences.Editor e = prefs.edit();
	            
	            e.putString("Access-Token", result.getString("Access-Token"));
	            e.putString("Device-Id", result.getString("Device-Id"));
	            e.putString("avatar", result.getString("avatar"));
//	            e.putString("facebook", facebookId);
	            
	            JSONObject userinfo = result.getJSONObject("userinfo");
	            
	            e.putString("uid", userinfo.getString("id"));
	            e.putString("email", userinfo.getString("email"));
	            e.putString("first_name", userinfo.getString("first_name"));
	            e.putString("last_name", userinfo.getString("last_name"));
	            e.putString("company", userinfo.getString("company"));
	            e.putString("videochat_id", userinfo.getString("videochat_id"));
                e.putString("password", "12345678");
                e.putString("is_employer", userinfo.getString("is_company"));
	            
	            if(userinfo.getString("education_ids").equals("")) {
	            	e.putString("education_ids", "");
	            } else {
	            	e.putString("education_ids", userinfo.getString("education_ids"));
	            }
	            if(userinfo.getString("experience_ids").equals("")) {
	            	e.putString("experience_ids", "");
	            } else {
	            	e.putString("experience_ids", userinfo.getString("experience_ids"));
	            }
	            
	            e.putString("last_company", "");
                
	            e.putString("experience_year", userinfo.getString("experience_year"));
	            e.putString("headline", userinfo.getString("headline"));
	            e.putString("skill_ids", userinfo.getString("skill_ids"));
	            
	            JSONObject settings = result.getJSONObject("setting");
                e.putString("like_push", settings.getString("like_push"));
                e.putString("like_email", settings.getString("like_email"));
                e.putString("talent_push", settings.getString("talent_push"));
                e.putString("talent_email", settings.getString("talent_email"));
                e.putString("match_push", settings.getString("match_push"));
                e.putString("match_email", settings.getString("match_email"));
	            
	            e.commit();
	            
	            startActivity(new Intent(SignActivity.this, MainActivity.class));
			} else {
				Toast.makeText(context, result.getString("message"),
		              Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressLint("NewApi")
	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regID = gcm.register(Constants.GOOGLE_SENDER_ID);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					Log.d("RegisterActivity", "Error: " + msg);
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				
			}
		}.execute(null, null, null);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		Intent intent = null;
		
		switch(v.getId()) {
			case R.id.txt_post_job:
				intent = new Intent(SignActivity.this, SignUpActivity.class);
				startActivity(intent);
				break;
			case R.id.txt_login:
				intent = new Intent(SignActivity.this, SignInActivity.class);
				startActivity(intent);
				break;
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		callbackManager.onActivityResult(requestCode, resultCode, data);
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
}
