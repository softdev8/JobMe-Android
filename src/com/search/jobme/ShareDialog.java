package com.search.jobme;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ShareDialog extends Dialog implements 
		android.view.View.OnClickListener {
	
	protected static final String TAG = ShareDialog.class.getName();
	
	Activity activity;
	Button btnCancel, btnMessage, btnFacebook;
	
	private ProgressDialog pDialog;
	
	static OnMyDialogResult mDialogResult;
	
	public ShareDialog(Activity activity) {
		super(activity);
		this.activity = activity;
	}
	
	public void setDialogResult(OnMyDialogResult dialogResult){
        mDialogResult = dialogResult;
    }

    public interface OnMyDialogResult{
       void finish(String result);
    }
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		setContentView(R.layout.share_dialog);

		setCanceledOnTouchOutside(false);
		
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(this);
		
		btnMessage = (Button) findViewById(R.id.btnMessage);
		btnMessage.setOnClickListener(this);
		
		btnFacebook = (Button) findViewById(R.id.btnFacebook);
		btnFacebook.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btnCancel:
				break;
			case R.id.btnMessage:
				Intent smsIntent = new Intent(Intent.ACTION_VIEW);
				smsIntent.setType("vnd.android-dir/mms-sms");
				smsIntent.putExtra("address", "");
				smsIntent.putExtra("sms_body","Body of Message");
				activity.startActivity(smsIntent);
				break;
			case R.id.btnFacebook:
				if(mDialogResult != null) {
					mDialogResult.finish("facebook");
				}
				break;
		}
		
		dismiss();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}  
	
}
