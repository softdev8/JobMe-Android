package com.search.jobme;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

public class SplashActivity extends Activity {
	
	Context context;
	protected boolean _active = true;
	protected int _splashTime = 3000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		final SplashActivity sPlashScreen = this;
		
//		PackageInfo info;
//		try {
//		    info = getPackageManager().getPackageInfo("com.search.jobme", PackageManager.GET_SIGNATURES);
//		    for (Signature signature : info.signatures) {
//		        MessageDigest md;
//		        md = MessageDigest.getInstance("SHA");
//		        md.update(signature.toByteArray());
//		        String something = new String(Base64.encode(md.digest(), 0));
//		        //String something = new String(Base64.encodeBytes(md.digest()));
//		        Log.e("hash key", something);
////		        GQpT25naa78I8UyhgLiBmeh5Dr8=
//		    }
//		} catch (NameNotFoundException e1) {
//		    Log.e("name not found", e1.toString());
//		} catch (NoSuchAlgorithmException e) {
//		    Log.e("no such an algorithm", e.toString());
//		} catch (Exception e) {
//		    Log.e("exception", e.toString());
//		}

		context = this;

		final Thread splashTread = new Thread() {
			@Override
			public void run() {
				try {
					int waited = 0;
					while (_active && waited < _splashTime) {
						sleep(100);
						if (_active) {
							waited += 100;
						}
					}
				} catch (final InterruptedException e) {
					// do nothing
				} finally {
	
					runOnUiThread(new Runnable() {
	
						@Override
						public void run() {
							// TODO Auto-generated method stub
							
							SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
					        
							String access_token = prefs.getString("Access-Token", null);
							
							Intent intent = null;
							if (access_token != null) {
								
								SharedPreferences.Editor edit = prefs.edit();
								edit.putString("CURRENT_ACTIVE", "");
								edit.commit();
								
								intent = new Intent(sPlashScreen, MainActivity.class);
							} else {
								intent = new Intent(sPlashScreen, SignActivity.class);
							}
							startActivity(intent);
							finish();
						}
					});
				}
			}
		};
	
		splashTread.start();
	}
}
