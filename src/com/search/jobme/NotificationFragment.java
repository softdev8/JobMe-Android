package com.search.jobme;

import java.util.ArrayList;
import java.util.List;

import me.tangke.slidemenu.SlideMenu;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.search.jobme.ManageFragment.LoadProductTask;
import com.search.jobme.ManageFragment.MatchAdapter;
import com.search.jobme.ManageFragment.SearchAdapter;
import com.search.jobme.model.NotificationModel;
import com.search.jobme.model.UserInfo;
import com.search.jobme.until.APIManager;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint("NewApi")
public class NotificationFragment extends Fragment implements OnClickListener {
	
	MainActivity mParent;
	ImageView btnMenu;
	
	ListView notification_listView;
	
	SearchAdapter adapter;
	private SlideMenu mSlideMenu;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		
		mParent = (MainActivity) this.getActivity();		
		View v = inflater.inflate(
	    		  R.layout.fragment_notification, container, false);
		
		mSlideMenu = mParent.getSlideMenu();
		
		btnMenu = (ImageView) v.findViewById(R.id.btnMenu);
		btnMenu.setOnClickListener(this);
		
		notification_listView = (ListView) v.findViewById(R.id.notification_listView);
		adapter = new SearchAdapter(MainActivity.notification_data, mParent);
		notification_listView.setAdapter(adapter);
		notification_listView.setDividerHeight(0);
		
		return v;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
			case R.id.btnMenu:
				mSlideMenu.open(false, true);
				mParent.getNotification();
				break;
		}
	}
	
	class SearchAdapter extends ArrayAdapter<NotificationModel> {
		
		private ArrayList<NotificationModel> itemList;
		private Context context;
		
		
		public SearchAdapter(ArrayList<NotificationModel> itemList, Context ctx) {
			super(ctx, android.R.layout.simple_list_item_1, itemList);
			this.itemList = itemList;
			this.context = ctx;		
			
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			
			View v = convertView;
			if (v == null) {
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				
				v = inflater.inflate(R.layout.notification_list_item, null);
			}
			
			final NotificationModel model = MainActivity.notification_data.get(position);
			final UserInfo item = model.getUserInfo();
			
			CircularImageView user_picture = (CircularImageView) v.findViewById(R.id.user_picture);
			String avatar_url = String.format("%s%s", Constants.PHOTO_UPLOAD, item.getAvatar());
			UrlImageViewHelper.setUrlDrawable(user_picture, avatar_url);
			
			LinearLayout user_layout = (LinearLayout) v.findViewById(R.id.user_layout);
			user_layout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(mParent, EmployerProfileActivity.class);
					intent.putExtra("employer", 1);
					intent.putExtra("postition", position);
					startActivity(intent);
				}
			});
			
			TextView username = (TextView) v.findViewById(R.id.txt_username);
			username.setText(String.format("%s %s", item.getFirst_name(), item.getLast_name()));
			
			TextView txt_job_function = (TextView) v.findViewById(R.id.txt_job_function);
			txt_job_function.setText(item.getHeadline());
			
			TextView txt_company_name = (TextView) v.findViewById(R.id.txt_company_name);
			txt_company_name.setText(item.getLocation());
			
			final ImageView btnLike = (ImageView) v.findViewById(R.id.btnLike);
			final ImageView btnDislike = (ImageView) v.findViewById(R.id.btnDislike);
			
			final TextView txt_status = (TextView) v.findViewById(R.id.txt_status);
			
			if(item.getMatched().equals("1")) {
				txt_status.setText("Matched");
				btnLike.setEnabled(false);
				btnDislike.setEnabled(false);
			} else if(item.getStatus().equals("100")) {
				txt_status.setText("DisLiked");
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
					
					List<NameValuePair> params = new ArrayList<NameValuePair>();
			        params.add(new BasicNameValuePair("employer_id", item.getUser_id()));
			        
		        	JSONObject result = null;
		       		APIManager.getInstance().callPost(context, "freelancer/invite_like", params, true);
		       		
		       		txt_status.setText("Matched");
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
			        params.add(new BasicNameValuePair("employer_id", item.getUser_id()));
			        
		        	JSONObject result = null;
		       		APIManager.getInstance().callPost(context, "freelancer/invite_dislike", params, true);
		       		
		       		txt_status.setText("DisLiked");
					btnLike.setEnabled(false);
					btnDislike.setEnabled(false);
				}
			});
			return v;
		}
	}
}

