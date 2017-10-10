package com.search.jobme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jivesoftware.smack.SmackException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.quickblox.chat.QBChatService;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCTypes;
import com.search.jobme.ManageFragment.LoadProductTask;
import com.search.jobme.ManageFragment.MatchAdapter;
import com.search.jobme.ManageFragment.SearchAdapter;
import com.search.jobme.model.HistroyChatModel;
import com.search.jobme.model.UserInfo;
import com.search.jobme.until.APIManager;
import com.search.jobme.until.Utility;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint("NewApi")
public class ChatFragment extends Fragment {
	
	ChatAdapter chatadapter;
	ListView listmessage;
	
	int SendCheck = 0;
	
	Boolean isInternetPresent = false;
	Utility utility;
	
	public static ArrayList<HistroyChatModel> HistoryChatList = new ArrayList<HistroyChatModel>();
	
	Context context;
	
	String receiver_id = "";
	String receiver_user_avatar = "";
	String receiver_name = "";
	String headline = "";
	String online_status;
	int index;
	int match;
	
	String current_user, current_user_avatar;
	
	ImageView btnBack, btnVideo;
	TextView txt_username, txt_company, txt_message, txt_send;
	
	ChatActivity m_Parent;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		
		m_Parent = (ChatActivity) getActivity();
		
		View v = inflater.inflate(
	    		  R.layout.fragment_chat, container, false);
		
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(m_Parent);
		SharedPreferences.Editor edit = prefs.edit();
		
		current_user = prefs.getString("uid", null);
		current_user_avatar = prefs.getString("avatar", null);
		
		Intent intent = m_Parent.getIntent();
		receiver_id = intent.getStringExtra("receiver_id");
		receiver_user_avatar = intent.getStringExtra("receiver_user_avatar");
		receiver_name = intent.getStringExtra("receiver_name");
		online_status = intent.getStringExtra("online");
		headline = intent.getStringExtra("headline");
		match = intent.getIntExtra("match", 0);
		index = intent.getIntExtra("index", -1);
		
		Bundle bundle1 = m_Parent.getIntent().getBundleExtra("INFO");
		if(bundle1 != null) {
			
			receiver_id 	= bundle1.getString("receiver_id");
			receiver_user_avatar 	= bundle1.getString("receiver_user_avatar");
			receiver_name 	= bundle1.getString("receiver_name");
		}
		
		edit.putString("CURRENT_ACTIVE", receiver_id);
		edit.commit();
		
		utility = new Utility(m_Parent);
		listmessage = (ListView) v.findViewById(R.id.listmessage);

		btnBack = (ImageView) v.findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(m_Parent);
				SharedPreferences.Editor edit = prefs.edit();
				edit.putString("CURRENT_ACTIVE", "");
				edit.commit();
				
				try {
                    QBRTCClient.getInstance(m_Parent).destroy();
                    QBChatService.getInstance().logout();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
				
				m_Parent.finish();
			}
		});
		
		btnVideo = (ImageView) v.findViewById(R.id.btnVideo);
		if(online_status.equals("1") && prefs.getString("is_employer", "0").equals("1")) {
			btnVideo.setVisibility(View.VISIBLE);
		} 
		btnVideo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				QBRTCTypes.QBConferenceType qbConferenceType = QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO;
				
				Map<String, String> userInfo = new HashMap<String, String>();
		        userInfo.put("any_custom_data", "some data");
		        userInfo.put("my_avatar_url", "avatar_reference");
		        
				m_Parent.addConversationFragmentStartCall(m_Parent.opponentsList,
                        qbConferenceType, userInfo);
			}
		});

		
		txt_username = (TextView) v.findViewById(R.id.txt_username);
		txt_username.setText(receiver_name);
		txt_username.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent;
				if(prefs.getString("is_employer", "0").equals("1")) {	//Employer login
					intent = new Intent(m_Parent, CandidateProfileActivity.class);
				} else {	//Candidate login
					intent = new Intent(m_Parent, EmployerProfileActivity.class);
					intent.putExtra("employer", 2);
				}
				
				intent.putExtra("match", match);
				intent.putExtra("index", index);
				startActivity(intent);
			}
		});
		
		txt_company = (TextView) v.findViewById(R.id.txt_company);
		txt_company.setText(headline);

		txt_message = (TextView) v.findViewById(R.id.txt_message);
		txt_message.setBackgroundResource(android.R.color.transparent);
		
		txt_send = (TextView) v.findViewById(R.id.txt_send);
		txt_send.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isInternetPresent = utility.isConnectingToInternet();
				if(txt_message.length() > 0) {
					if (isInternetPresent) {
						String strMessage = txt_message.getText().toString();
						
						HistroyChatModel histroyChatModel = new HistroyChatModel(
								current_user, strMessage, "");
	
						HistoryChatList.add(histroyChatModel);
	
						chatadapter.notifyDataSetChanged();
						listmessage.setSelection(listmessage.getAdapter()
								.getCount() - 1);
						
						List<NameValuePair> params = new ArrayList<NameValuePair>();
				        params.add(new BasicNameValuePair("receiver_id", receiver_id));
				        params.add(new BasicNameValuePair("message", txt_message.getText().toString()));
				        
			        	JSONObject result = null;
			       		result =  APIManager.getInstance().callPost(m_Parent, "chat/sendMessage", params, true);
			       		
			       		txt_message.setText("");
	
					} else {
						utility.Message("Please check internet connection.");
					}
				}
			}
		});
		
		ChatHistory();
		
		return v;
	}

	public void ChatHistory() {

		isInternetPresent = utility.isConnectingToInternet();
		if (isInternetPresent) {
			new LoadChatTask().execute();
		} else {
			utility.Message("Please check internet connection.");
		}
	}

	class LoadChatTask extends AsyncTask<String, Integer, ArrayList<UserInfo>> {
        private ProgressDialog progressDialog;
        
        protected void onPreExecute() {
        	progressDialog = ProgressDialog.show(m_Parent, "", "Loading...", true);
        }
        
        @Override
        protected void onPostExecute(ArrayList<UserInfo> result) {
        	chatadapter = new ChatAdapter(m_Parent, HistoryChatList);
			listmessage.setAdapter(chatadapter);

			chatadapter.notifyDataSetChanged();
			listmessage.setSelection(listmessage.getAdapter()
					.getCount() - 1);
    		
            progressDialog.dismiss();
        }
 
        @Override
        protected ArrayList<UserInfo> doInBackground(String... param) {
        	
        	List<NameValuePair> params = new ArrayList<NameValuePair>();
	        params.add(new BasicNameValuePair("receiver_id", receiver_id));
	        
        	JSONObject result = null;
       		result =  APIManager.getInstance().callPost(m_Parent, "chat/getChatHistory", params, true);
        	
        	try {
        		
        		if(result.getString("success").equals("1")) {
	        		JSONArray jarray = result.getJSONArray("history");
					
	        		HistoryChatList = new ArrayList<HistroyChatModel>();
					for (int i = 0; i < jarray.length(); i++) {
						JSONObject c = jarray.getJSONObject(i);
						
						String sender = c.getString("sender");
						String message = c.getString("message");
						String created = c.getString("created");

						HistroyChatModel histroyChatModel = new HistroyChatModel(
								sender, message, created);
						HistoryChatList.add(histroyChatModel);
					}
					
        		}
        	} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

            return null;
        }
    }
	
	
	class ChatAdapter extends ArrayAdapter<HistroyChatModel> {
		
		private ArrayList<HistroyChatModel> itemList;
		private Context context;
		
		
		public ChatAdapter(Activity chatActivity,
				ArrayList<HistroyChatModel> historyChatList_tmp) {
			// TODO Auto-generated constructor stub
			super(chatActivity, android.R.layout.simple_list_item_1, historyChatList_tmp);
			this.itemList = itemList;
			this.context = chatActivity;	
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				
				convertView = inflater.inflate(R.layout.chat_item_model, null);
			}
			
			final HistroyChatModel data = HistoryChatList.get(position);
			
			CircularImageView iv_receivemessage = (CircularImageView) convertView
					.findViewById(R.id.iv_receivemessage);
			CircularImageView iv_sendmessage = (CircularImageView) convertView
					.findViewById(R.id.iv_sendmessage);
			TextView txt_receivemessage = (TextView) convertView
					.findViewById(R.id.txt_receivemessage);
			TextView txt_sendmessage = (TextView) convertView
					.findViewById(R.id.txt_sendmessage);

			LinearLayout ly_send_message = (LinearLayout) convertView
					.findViewById(R.id.ly_send_message);
			RelativeLayout ly_receive_message = (RelativeLayout) convertView
					.findViewById(R.id.ly_receivemessage);
			
			ly_receive_message.setVisibility(View.GONE);
			ly_send_message.setVisibility(View.GONE);
			
			// Sender User
			if (data.getSender().equals(current_user)) {	//Current login user

				txt_receivemessage.setText(data.getMessage());

				ly_receive_message.setVisibility(View.VISIBLE);

				String receiver_avatar_path = String.format("%s%s", Constants.PHOTO_UPLOAD, current_user_avatar);
				UrlImageViewHelper.setUrlDrawable(iv_receivemessage, receiver_avatar_path);

			} else {

				txt_sendmessage.setText(data.getMessage());

				ly_send_message.setVisibility(View.VISIBLE);

				String sender_avatar_path = String.format("%s%s", Constants.PHOTO_UPLOAD, receiver_user_avatar);
				UrlImageViewHelper.setUrlDrawable(iv_sendmessage, sender_avatar_path);
			}
			
			return convertView;
		}
	}
	
	private BroadcastReceiver onNotice= new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            String receiver_id = intent.getStringExtra("sender_id");
            String chat_id = intent.getStringExtra("chat_id");
            
            HistroyChatModel histroyChatModel = new HistroyChatModel(
					receiver_id, message, "");

			HistoryChatList.add(histroyChatModel);

			chatadapter.notifyDataSetChanged();
			listmessage.setSelection(listmessage.getAdapter()
					.getCount() - 1);
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
	        params.add(new BasicNameValuePair("chat_id", chat_id));
	        
       		APIManager.getInstance().callPost(context, "chat/checkMessage", params, true);
        }
    };

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		IntentFilter iff= new IntentFilter(GCMNotificationIntentService.MESSAGE_RECEIVED);
		LocalBroadcastManager.getInstance(m_Parent).registerReceiver(onNotice, iff);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		 LocalBroadcastManager.getInstance(m_Parent).unregisterReceiver(onNotice);
	}
    
    
}
