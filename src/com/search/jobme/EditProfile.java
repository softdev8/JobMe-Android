package com.search.jobme;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.tangke.slidemenu.SlideMenu;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.search.jobme.CandidateActivity.SearchAdapter;
import com.search.jobme.ImageLoader.ImageLoader;
import com.search.jobme.model.Education;
import com.search.jobme.model.Experience;
import com.search.jobme.model.SkillModel;
import com.search.jobme.model.UserInfo;
import com.search.jobme.until.APIManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class EditProfile extends Fragment implements OnClickListener {
	
	private static final int				EXPERIENCE 	= 1;
	private static final int				EDUCATION 	= 2;
	private static final int				FUNCTION 	= 3;
	private static final int				INDUSTRY 	= 4;
	private static final int 				PICK_FROM_CAMERA = 5;
	private static final int 				CROP_FROM_CAMERA = 6;
	private static final int 				PICK_FROM_FILE = 7;
	private static final int 				LOCATION = 8;
	private static final int 				SKILL = 9;
	
	private SlideMenu mSlideMenu;
	
	ImageView btnMenu, btnSave;
	
	MainActivity mParent;
	CircularImageView user_picture;
	EditText txt_first_name, txt_last_name;
	TextView txt_add_photo;
	EditText txt_headline, txt_experience_years;
	
	LinearLayout experience_sub, experience_layout, education_sub, education_layout, skill_layout;
	RelativeLayout location_layout, function_layout;
	
	TextView txt_function, txt_location, txt_skill;
	
	public static ArrayList<Experience> experience_data = new ArrayList<Experience>();
	public static ArrayList<Education> education_data = new ArrayList<Education>();
	
	int job_function = 0;
	String location;
	String lat="", lot="";
	String str_skill = "";
	String edit_skill;
	String gskill="";
	
	private AlertDialog dialog;
	boolean m_bCamera = false;
	String gallery_url = "";
	public Uri mImageCaptureUri;
	
	HttpEntity resEntity;
	
	private String[] arr_job_functions;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		
		mParent = (MainActivity) this.getActivity();
		
		View v = inflater.inflate(
	    		  R.layout.edit_profile, container, false);
		
		captureImageInitialization();
		mSlideMenu = mParent.getSlideMenu();
		
		arr_job_functions = getResources().getStringArray(R.array.main_cat);
		
		btnMenu = (ImageView) v.findViewById(R.id.btnMenu);
		btnMenu.setOnClickListener(this);
		
		btnSave = (ImageView) v.findViewById(R.id.btnSave);
		btnSave.setOnClickListener(this);
		
		user_picture = (CircularImageView) v.findViewById(R.id.user_picture);
		user_picture.setOnClickListener(this);
		
		txt_add_photo = (TextView) v.findViewById(R.id.txt_add_photo);
		txt_add_photo.setOnClickListener(this);
		
		txt_first_name = (EditText) v.findViewById(R.id.txt_first_name);
		txt_first_name.setBackgroundResource(android.R.color.transparent);
		txt_last_name = (EditText) v.findViewById(R.id.txt_last_name);
		txt_last_name.setBackgroundResource(android.R.color.transparent);
		
		txt_headline = (EditText) v.findViewById(R.id.txt_headline);
		txt_headline.setBackgroundResource(android.R.color.transparent);
		
		txt_experience_years = (EditText) v.findViewById(R.id.txt_years);
		txt_experience_years.setBackgroundResource(android.R.color.transparent);
		
		experience_layout = (LinearLayout) v.findViewById(R.id.experience_layout);
		experience_layout.setOnClickListener(this);
		experience_sub = (LinearLayout) v.findViewById(R.id.experience_sub);
		
		education_layout = (LinearLayout) v.findViewById(R.id.education_layout);
		education_layout.setOnClickListener(this);
		education_sub = (LinearLayout) v.findViewById(R.id.education_sub);
		
		txt_location = (TextView) v.findViewById(R.id.txt_location);
		txt_function = (TextView) v.findViewById(R.id.txt_function);
		
		skill_layout = (LinearLayout) v.findViewById(R.id.skill_layout);
		skill_layout.setOnClickListener(this);
		txt_skill = (TextView) v.findViewById(R.id.txt_skill);
		
		location_layout = (RelativeLayout) v.findViewById(R.id.location_layout);
		location_layout.setOnClickListener(this);
		
		function_layout = (RelativeLayout) v.findViewById(R.id.job_function_layout);
		function_layout.setOnClickListener(this);
		
		new GetProfileTask().execute();
		
		return v;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		
		if(resultCode == Activity.RESULT_OK) {
			switch(requestCode) {
				case EXPERIENCE:
					if(!data.getBooleanExtra("update", false)) {		//new add
						Experience item = new Experience();
						item.setExp_id("0");
						item.setHeadline(data.getStringExtra("headline"));
						item.setCompany(data.getStringExtra("company_name"));
						item.setStart_date(data.getStringExtra("start_date"));
						item.setEnd_date(data.getStringExtra("end_date"));
						experience_data.add(item);
						
						addExpereience(item, experience_data.size() - 1);
					} else {	//update
						experience_sub.removeAllViews();
						
						if(!data.getBooleanExtra("delete", false)) {
							int index = data.getIntExtra("exp_index", -1);
							Experience item = experience_data.get(index);
							item.setHeadline(data.getStringExtra("headline"));
							item.setCompany(data.getStringExtra("company_name"));
							item.setStart_date(data.getStringExtra("start_date"));
							item.setEnd_date(data.getStringExtra("end_date"));
						}
						
						for(int i = 0 ; i < experience_data.size() ; i++) {
			        		Experience _item = experience_data.get(i);
			        		addExpereience(_item, i);
			        	}
					}
					break;
				case EDUCATION:
					if(!data.getBooleanExtra("update", false)) {
						Education item1 = new Education();
						item1.setEdu_id("0");
						item1.setSchool(data.getStringExtra("school_name"));
						item1.setDegree(data.getStringExtra("degree"));
						item1.setField(data.getStringExtra("field_study"));
						item1.setStart_date(data.getStringExtra("start_date"));
						item1.setEnd_date(data.getStringExtra("end_date"));
						education_data.add(item1);
						
						addEducation(item1, education_data.size() - 1);
					} else {
						education_sub.removeAllViews();
						
						if(!data.getBooleanExtra("delete", false)) {
							int index = data.getIntExtra("edu_index", -1);
							Education item = education_data.get(index);
							item.setSchool(data.getStringExtra("school_name"));
							item.setDegree(data.getStringExtra("degree"));
							item.setField(data.getStringExtra("field_study"));
							item.setStart_date(data.getStringExtra("start_date"));
							item.setEnd_date(data.getStringExtra("end_date"));
						}
						
						for(int i = 0 ; i < education_data.size() ; i++) {
							Education _item = education_data.get(i);
			        		addEducation(_item, i);
			        	}
					}
					break;
				case FUNCTION:
					job_function = data.getIntExtra("select_index", -1);
					txt_function.setText(arr_job_functions[job_function]);
					break;
				case PICK_FROM_CAMERA:
	        		m_bCamera = true;
	        		
		        	File file = new File(Environment.getExternalStorageDirectory(), "/img_temp0.jpg");
					Bitmap myBitmap = decodeFile(file);
					Bitmap scaledBitmap = getLotated(myBitmap);
					user_picture.setImageBitmap(scaledBitmap);

		            break;
	        	case PICK_FROM_FILE:
	        		m_bCamera = false;
	        		
	        		Uri selectedImage = data.getData();
	        		
	                String[] filePathColumn = { MediaStore.Images.Media.DATA };
	 
	                // Get the cursor
	                Cursor cursor = mParent.getContentResolver().query(selectedImage,
	                        filePathColumn, null, null, null);
	                // Move to first row
	                cursor.moveToFirst();
	 
	                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	                gallery_url = cursor.getString(columnIndex);
	                cursor.close();
	                
	                // Set the Image in ImageView after decoding the String
	                user_picture.setImageBitmap(BitmapFactory
	                        .decodeFile(gallery_url));
	        		
	        		break;
	        	case LOCATION:
	        		txt_location.setText(data.getStringExtra("location"));
	        		lat = data.getStringExtra("lat");
	        		lot = data.getStringExtra("lot");
	        		break;
	        	case SKILL:
	        		ArrayList<String> skill = data.getStringArrayListExtra("skills");
	        		StringBuilder stringBuilder = new StringBuilder();
	        		for(int i = 0 ; i < skill.size() ; i++) {
						stringBuilder.append(skill.get(i));
						stringBuilder.append(", ");
					}
	        		String temp = stringBuilder.toString();
	        		gskill = temp.substring(0, temp.length() - 2);
	        		
	        		txt_skill.setText(gskill);
	        		break;
			}
		}
	}

	void addEducation(final Education item, final int index) {
		
		TextView line = new TextView(mParent);
		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 1);
		line.setBackgroundColor(Color.parseColor("#999999"));
		param.setMargins(20, 0, 0, 0);
		education_sub.addView(line, param);
		
		RelativeLayout sub_layout = new RelativeLayout(mParent);
    	RelativeLayout.LayoutParams sub_param = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, 120);
    	education_sub.addView(sub_layout, sub_param);
    	sub_layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mParent, EditEducation.class);
				intent.putExtra("update", true);
				intent.putExtra("edu_index", index);
				startActivityForResult(intent, EDUCATION);
			}
		});
    	
    	RelativeLayout.LayoutParams param1 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    	ImageView right_arrow = new ImageView(mParent);
    	right_arrow.setId(100);
    	right_arrow.setImageResource(R.drawable.right_arrow_1);
    	param1.addRule(RelativeLayout.CENTER_VERTICAL);
    	param1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
    	param1.setMargins(0, 0, 30, 0);
    	sub_layout.addView(right_arrow, param1);
    	
    	LinearLayout left_layout = new LinearLayout(mParent); 
    	left_layout.setOrientation(LinearLayout.VERTICAL);
    	left_layout.setGravity(Gravity.CENTER_VERTICAL);
    	
    	RelativeLayout.LayoutParams param3 = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, 120);
    	param3.addRule(RelativeLayout.CENTER_VERTICAL);
    	param3.addRule(RelativeLayout.LEFT_OF, right_arrow.getId());
    	param3.setMargins(30, 0, 0, 0);
    	sub_layout.addView(left_layout, param3);
    	
    	TextView text1 = new TextView(mParent);
    	text1.setText(item.getSchool());
    	text1.setTextSize(18);
    	text1.setTextColor(Color.parseColor("#555555"));
    	left_layout.addView(text1);
    	
    	TextView text2 = new TextView(mParent);
    	text2.setText(String.format("%s, %s - %s",  item.getDegree(), item.getStart_date(), item.getEnd_date()));
    	text2.setTextSize(14);
    	left_layout.addView(text2);
	}
	
	void addExpereience(final Experience item, final int index) {
		
		TextView line = new TextView(mParent);
		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 1);
		line.setBackgroundColor(Color.parseColor("#999999"));
		param.setMargins(20, 0, 0, 0);
		experience_sub.addView(line, param);
		
		RelativeLayout sub_layout = new RelativeLayout(mParent);
    	RelativeLayout.LayoutParams sub_param = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, 120);
    	experience_sub.addView(sub_layout, sub_param);
    	sub_layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mParent, EditExperience.class);
				intent.putExtra("update", true);
				intent.putExtra("exp_index", index);
				startActivityForResult(intent, EXPERIENCE);
			}
		});
    	
    	RelativeLayout.LayoutParams param1 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    	ImageView right_arrow = new ImageView(mParent);
    	right_arrow.setId(100);
    	right_arrow.setImageResource(R.drawable.right_arrow_1);
    	param1.addRule(RelativeLayout.CENTER_VERTICAL);
    	param1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
    	param1.setMargins(0, 0, 30, 0);
    	sub_layout.addView(right_arrow, param1);
    	
    	LinearLayout left_layout = new LinearLayout(mParent); 
    	left_layout.setOrientation(LinearLayout.VERTICAL);
    	left_layout.setGravity(Gravity.CENTER_VERTICAL);
    	
    	RelativeLayout.LayoutParams param3 = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, 120);
    	param3.addRule(RelativeLayout.CENTER_VERTICAL);
    	param3.addRule(RelativeLayout.LEFT_OF, right_arrow.getId());
    	param3.setMargins(30, 0, 0, 0);
    	sub_layout.addView(left_layout, param3);
    	
    	TextView text1 = new TextView(mParent);
    	text1.setText(item.getHeadline());
    	text1.setTextSize(18);
    	text1.setTextColor(Color.parseColor("#555555"));
    	left_layout.addView(text1);
    	
    	TextView text2 = new TextView(mParent);
    	text2.setText(String.format("%s, %s - %s",  item.getCompany(), item.getStart_date(), item.getEnd_date()));
    	text2.setTextSize(14);
    	left_layout.addView(text2);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch(v.getId()) {
			case R.id.btnMenu:
				mSlideMenu.open(false, true);
				mParent.getNotification();
				break;
			case R.id.btnSave:
				new UpdateProgressTask().execute();
				break;
			case R.id.user_picture:
				dialog.show();
				break;
			case R.id.txt_add_photo:
				dialog.show();
				break;
			case R.id.experience_layout:
				intent = new Intent(mParent, EditExperience.class);
				intent.putExtra("update", false);
				startActivityForResult(intent, EXPERIENCE);
				break;
			case R.id.education_layout:
				intent = new Intent(mParent, EditEducation.class);
				intent.putExtra("update", false);
				startActivityForResult(intent, EDUCATION);
				break;
			case R.id.skill_layout:
				intent = new Intent(mParent, EditSkill.class);
				intent.putExtra("skill", edit_skill);
				startActivityForResult(intent, SKILL);
				break;
			case R.id.location_layout:
				intent = new Intent(mParent, Location.class);
				startActivityForResult(intent, LOCATION);
				break;
			case R.id.job_function_layout:
				intent = new Intent(mParent, JobFunction.class);
				startActivityForResult(intent, FUNCTION);
				break;
		}
	}
	
	private void captureImageInitialization() {
        /**
         * a selector dialog to display two image source options, from camera
         * ‘Take from camera’ and from existing files ‘Select from gallery’
         */
        final String[] items = new String[] { "Take from camera",
                     "Select from gallery" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mParent,
                     android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(mParent);

        builder.setTitle("Select Image");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int item) { // pick from
                                                                                              // camera
             if (item == 0) {
            	 select_img(0);
             } else {
            	 select_img(1);
             }
           }
        });

        dialog = builder.create();
	}
	
	private void select_img(int index) {
		
        if (index == 0) {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			
			mImageCaptureUri = Uri.fromFile(new File(Environment
			              .getExternalStorageDirectory(), "img_temp0.jpg"));
			
			intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
			              mImageCaptureUri);
			
			try {
			       intent.putExtra("return-data", true);
			
		           startActivityForResult(intent, PICK_FROM_CAMERA);
		    } catch (ActivityNotFoundException e) {
		           e.printStackTrace();
		    }
		 } else {
		    // pick from file
			
			Intent intent = new Intent();
			
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			
			startActivityForResult(Intent.createChooser(intent,
		                  "Complete action using"), PICK_FROM_FILE);
		 }
    }
	 
	private Bitmap decodeFile(File f) {
	    try {
	        // Decode image size
	        BitmapFactory.Options o = new BitmapFactory.Options();
	        o.inJustDecodeBounds = true;
	        BitmapFactory.decodeStream(new FileInputStream(f), null, o);

	        // The new size we want to scale to
	        final int REQUIRED_SIZE=70;

	        // Find the correct scale value. It should be the power of 2.
	        int scale = 1;
	        while(o.outWidth / scale / 2 >= REQUIRED_SIZE && 
	              o.outHeight / scale / 2 >= REQUIRED_SIZE) {
	            scale *= 2;
	        }

	        // Decode with inSampleSize
	        BitmapFactory.Options o2 = new BitmapFactory.Options();
	        o2.inSampleSize = scale;
	        return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
	    } catch (FileNotFoundException e) {
	    	
	    }
	    
	    return null;
	}
	
	private Bitmap getLotated(Bitmap bitmap) {
		
		Bitmap scaledBMP = null;
		
		// Getting width & height of the given image.
		if (bitmap != null) {
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();
			// Setting post rotate to 90
			Matrix mtx = new Matrix();
			mtx.postRotate(90);
			// Rotating Bitmap
			Bitmap rotatedBMP = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
  
			int width = rotatedBMP.getWidth();
			int height = rotatedBMP.getHeight();
			int maxSize = 300;
			float bitmapRatio = (float)width / (float) height;
			if (bitmapRatio > 0) {
				width = maxSize;
				height = (int) (width / bitmapRatio);
			} else {
				height = maxSize;
				width = (int) (height * bitmapRatio);
			}
  
			scaledBMP = Bitmap.createScaledBitmap(rotatedBMP, width, height, false);
		}
		
		return scaledBMP;
	}
	
	private class UpdateProgressTask extends AsyncTask<String, Void, Void> {
        private ProgressDialog progressDialog;
        boolean bStatus = false;
 
        protected void onPreExecute() {
        	progressDialog = ProgressDialog.show(mParent, "", "Submitting...", true);
        }
 
        @Override
        protected void onPostExecute(Void result) {
        	if(bStatus) {
        		Toast.makeText(mParent, "Update Successful!",
			              Toast.LENGTH_SHORT).show();
        	}
			progressDialog.dismiss();
        }
 
        protected Void doInBackground(final String... args) {
        	
        	String url = Constants.SERVER_URL + "account/update_profile";
    		String img_url;
    		
    		File attach_file;
    		try 
    		{
    			HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(url);
                
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mParent);
                String access_token = prefs.getString("Access-Token", null);
                String device_id = prefs.getString("Device-Id", null);
                
                post.addHeader("Access-Token", access_token);
                post.addHeader("Device-Id", device_id);
                	
                MultipartEntity reqEntity = new MultipartEntity();
                
                if(m_bCamera) {
	                attach_file = new File(Environment.getExternalStorageDirectory() + "/img_temp0.jpg");
	           	 	if (attach_file.exists()) {
	           	 		img_url = Environment.getExternalStorageDirectory() + "/img_temp0.jpg";
	    	            File file1 = new File(img_url);
	    	            FileBody bin1 = new FileBody(file1);
	    	            reqEntity.addPart("avatar", bin1);
	                }
                } else {
                	attach_file = new File(gallery_url);
	           	 	if (attach_file.exists()) {
	    	            File file1 = new File(gallery_url);
	    	            FileBody bin1 = new FileBody(file1);
	    	            reqEntity.addPart("avatar", bin1);
	                }
                }
           	 	
           	 	reqEntity.addPart("first_name", new StringBody(txt_first_name.getText().toString()));
           	 	reqEntity.addPart("last_name", new StringBody(txt_last_name.getText().toString()));
                reqEntity.addPart("email", new StringBody(prefs.getString("email", null)));
                reqEntity.addPart("headline", new StringBody(txt_headline.getText().toString()));
                reqEntity.addPart("location", new StringBody(txt_location.getText().toString()));
                reqEntity.addPart("latitude", new StringBody(lat));
                reqEntity.addPart("longitude", new StringBody(lot));
                reqEntity.addPart("job_function", new StringBody(String.valueOf(job_function)));
                reqEntity.addPart("skill_ids", new StringBody(gskill));
                reqEntity.addPart("experience_year", new StringBody(txt_experience_years.getText().toString()));
                reqEntity.addPart("salary_min", new StringBody(""));
                reqEntity.addPart("salary_max", new StringBody(""));
                
                if(experience_data.size() > 0) {
	                StringBuffer experience_str = new StringBuffer();
	                for(int i = 0 ; i < experience_data.size() ; i++) {
	                	Experience item = experience_data.get(i);
	                	experience_str.append(String.format("{\"exp_id\":\"%s\",\"headline\":\"%s\",\"company\":\"%s\",\"start_date\":\"%s\",\"end_date\":\"%s\"},", item.getExp_id(), item.getHeadline(), item.getCompany(), item.getStart_date(), item.getEnd_date()));
	        		}
	        		String result = experience_str.substring(0, experience_str.length() - 1);
	        		
	        		StringBuffer experience_arr_str = new StringBuffer();
	        		experience_arr_str.append("[");
	        		experience_arr_str.append(result);
	        		experience_arr_str.append("]");
	        		
	                reqEntity.addPart("experience", new StringBody(experience_arr_str.toString()));
                } else {
                	reqEntity.addPart("experience", new StringBody(""));
                }
                
                if(education_data.size() > 0) {
	                StringBuffer education_str = new StringBuffer();
	                for(int i = 0 ; i < education_data.size() ; i++) {
	                	Education item = education_data.get(i);
	                	education_str.append(String.format("{\"edu_id\":\"%s\",\"school_name\":\"%s\",\"degree\":\"%s\",\"field_study\":\"%s\",\"start_date\":\"%s\",\"end_date\":\"%s\"},", item.getEdu_id(), item.getSchool(), item.getDegree(), item.getField(), item.getStart_date(), item.getEnd_date()));
	        		}
	        		String result1 = education_str.substring(0, education_str.length() - 1);
	        		
	        		StringBuffer education_arr_str = new StringBuffer();
	        		education_arr_str.append("[");
	        		education_arr_str.append(result1);
	        		education_arr_str.append("]");
	                reqEntity.addPart("education", new StringBody(education_arr_str.toString()));
                } else {
                	reqEntity.addPart("education", new StringBody(""));
                }
                                
                post.setEntity(reqEntity);
                HttpResponse response = client.execute(post);
                resEntity = response.getEntity();
                final String response_str = EntityUtils.toString(resEntity);
                if (resEntity != null) {
                	try {
                 	   JSONObject jobject = new JSONObject( response_str );
                 	   
                 	   String status = jobject.getString("success");
                 	   if(status.equals("1")) {
                     	   bStatus = true;
                          
                     	   JSONObject obj = jobject.getJSONObject("info");
                     	  
                     	   SharedPreferences.Editor e = prefs.edit();
                     	   e.putString("avatar", obj.getString("avatar"));
	       	               e.putString("job_function", obj.getString("main_cat_id"));
	    	               e.putString("location", obj.getString("location"));
	    	               
	    	               new LoadSkillTask().execute();
                 	   } 
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
                if (attach_file.exists()) attach_file.delete();
				
    		} catch (Exception ex){
                Log.e("Debug", "error: " + ex.getMessage(), ex);
    		}
            
            return null;
        }
    }
	
	class GetProfileTask extends AsyncTask<String, Integer, ArrayList<UserInfo>> {
        private ProgressDialog progressDialog;
        
        String avatar, first_name, last_name, headline, experience_year="", location="";
        
        protected void onPreExecute() {
        	progressDialog = ProgressDialog.show(mParent, "", "Loading...", true);
        }
        
        @Override
        protected void onPostExecute(ArrayList<UserInfo> result) {
    		
        	String avatar_url = String.format("%s%s", Constants.PHOTO_UPLOAD, avatar);
        	UrlImageViewHelper.setUrlDrawable(user_picture, avatar_url);
        	
			txt_first_name.setText(first_name);
			txt_last_name.setText(last_name);
			txt_headline.setText(headline);
			experience_year = (experience_year == "") ? "0" : experience_year;
			txt_experience_years.setText(String.format("%s years", experience_year));
			
			if(job_function == 0) {
				txt_function.setText(arr_job_functions[job_function]);
			} else {
				txt_function.setText(arr_job_functions[job_function]);
			}
			
			if(!location.equals("") && (location != null)) {
				txt_location.setText(location);
			}
			
        	for(int i = 0 ; i < experience_data.size() ; i++) {
        		Experience item = experience_data.get(i);
        		addExpereience(item, i);
        	}
        	
        	for(int i = 0 ; i < education_data.size() ; i++) {
        		Education item = education_data.get(i);
        		addEducation(item, i);
        	}
        	
        	String skills = "";
        	edit_skill = "";
    		String str_skills = str_skill;
    		if(!str_skills.contains(",")) {
    			if(!str_skills.equals("")) {
    				for(int i = 0 ; i < MainActivity.skill_data.size() ; i++) {
    					SkillModel item = MainActivity.skill_data.get(i);
    					if(str_skills.equals(item.getId())) {
    						skills = item.getTitle();
    						edit_skill = item.getTitle();
    					}
    				}					
    			} 
    		} else {
    			String[] arr = str_skills.split(",");
    			StringBuilder stringBuilder = new StringBuilder();
    			StringBuilder stringBuilder1 = new StringBuilder();
    			for(int i = 0 ; i < arr.length ; i++) {
    				for(int j = 0 ; j < MainActivity.skill_data.size() ; j++) {
    					SkillModel item = MainActivity.skill_data.get(j);
    					if(arr[i].equals(item.getId())) {
    						stringBuilder.append(item.getTitle());
    						stringBuilder.append(" • ");
    						
    						stringBuilder1.append(item.getTitle());
    						stringBuilder1.append(", ");
    						break;
    					}
    				}
    			}
    			
    			String temp = stringBuilder.toString();
    			skills = temp.substring(0, temp.length() - 3);
    			
    			String temp1 = stringBuilder1.toString();
    			edit_skill = temp1.substring(0, temp1.length() - 2);
    		}
        	
    		gskill = edit_skill;
    		txt_skill.setText(skills);
    		
            progressDialog.dismiss();
        }
 
        @Override
        protected ArrayList<UserInfo> doInBackground(String... param) {
        	
        	experience_data.clear();
        	education_data.clear();
        	
        	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mParent);
        	
        	List<NameValuePair> params = new ArrayList<NameValuePair>();
	        params.add(new BasicNameValuePair("sel_id", prefs.getString("uid", "")));
	        
        	JSONObject result = null;
       		result =  APIManager.getInstance().callPost(mParent, "get_profile", params, true);
        	
        	try {
				
        		if(result.getString("success").equals("1")) {
					JSONObject c = result.getJSONObject("user_info");
					
					avatar = c.getString("avatar");
					first_name = c.getString("first_name");
					last_name = c.getString("last_name");
					headline = c.getString("headline");
					experience_year = c.getString("experience_year");
					job_function = c.getInt("main_cat_id");
					location = c.getString("location");
					lat = c.getString("lat");
					lot = c.getString("lot");
					
					JSONArray arr_experience = c.getJSONArray("experience");
					for(int i = 0 ; i < arr_experience.length() ; i++) {
						JSONObject b = arr_experience.getJSONObject(i);
						
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
					
					JSONArray arr_education = c.getJSONArray("education");
					for(int i = 0 ; i < arr_education.length() ; i++) {
						JSONObject b = arr_education.getJSONObject(i);
						
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
					
					str_skill = c.getString("skill_ids");
				}
        	} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

            return null;
        }
    }
	
	class LoadSkillTask extends AsyncTask<String, Integer, ArrayList<SkillModel>> {
        private ProgressDialog progressDialog;
        
        protected void onPreExecute() {
        	
        }
        
        @Override
        protected void onPostExecute(ArrayList<SkillModel> result) {
            
        }
 
        @Override
        protected ArrayList<SkillModel> doInBackground(String... param) {
        	
        	MainActivity.skill_data.clear();
        	
        	JSONObject result = null;
       		result =  APIManager.getInstance().callPost(mParent, "getSkill", null, true);
        	
        	try {
        		
        		if(result.getString("success").equals("1")) {
	        		JSONArray jarray = result.getJSONArray("skill");
					
					for (int i = 0; i < jarray.length(); i++) {
						JSONObject c = jarray.getJSONObject(i);
						
						SkillModel info = new SkillModel();
						
						info.setId(c.getString("id"));
						info.setTitle(c.getString("title"));
						
						MainActivity.skill_data.add(info);
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
	
}
