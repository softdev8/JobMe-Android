package com.search.jobme;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMNotificationIntentService extends IntentService {

	public static final String MESSAGE_RECEIVED = "GotMessage";
	public static final String PUSH_RECEIVED = "GotPush";
	public static final String MATCH = "GotMatch";
	
	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;

	public GCMNotificationIntentService() {
		super("GcmIntentService");
	}

	public static final String TAG = "GCMNotificationIntentService";

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		String messageType = gcm.getMessageType(intent);
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				sendNotification("Send error: " + extras.toString(), "", "", "", "");
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				sendNotification("Deleted messages on server: "
						+ extras.toString(), "", "", "", "");
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {

				if(extras.get(Constants.TYPE).equals("chat")) {
					if(!prefs.getString("CURRENT_ACTIVE","").equals(extras.getString("sender_id"))) {
						sendNotification("", extras.get("alert").toString(), extras.get("sender_photo").toString(), "", extras.get("sender_id").toString());
					} else {
						Intent messageIntent = new Intent(MESSAGE_RECEIVED);
						messageIntent.putExtra("message", extras.get("message").toString());
						messageIntent.putExtra("sender_id", extras.get("sender_id").toString());
						messageIntent.putExtra("chat_id", extras.get("chat_id").toString());
						messageIntent.putExtra("sender_photo", extras.get("sender_photo").toString());
						LocalBroadcastManager.getInstance(GCMNotificationIntentService.this).sendBroadcast(messageIntent);
					}
				} else if(extras.get(Constants.TYPE).equals("match")) {
					
					boolean appActived = false;
					ActivityManager activityManager = (ActivityManager) this.getSystemService( ACTIVITY_SERVICE );
			        List<RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
			        for(int i = 0; i < procInfos.size(); i++)
			        {
			            if(procInfos.get(i).processName.equals("com.search.jobme")) 
			            {
			            	if(procInfos.get(i).importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
			            		appActived = true;
			            	}
			            }
			        }
			        
			        if(appActived) {
						Intent pushIntent = new Intent(MATCH);
						pushIntent.putExtra("message", extras.get("message").toString());
						pushIntent.putExtra("sender_id", extras.get("sender_id").toString());
						pushIntent.putExtra("sender_photo", extras.get("sender_photo").toString());
						pushIntent.putExtra("sender_name", extras.get("sender_name").toString());
						pushIntent.putExtra("company_name", extras.get("company_name").toString());
						pushIntent.putExtra("headline", extras.get("headline").toString());
						pushIntent.putExtra("job_main_cat_id", extras.get("job_main_cat_id").toString());
						pushIntent.putExtra("last_company", extras.get("last_company").toString());
						
						LocalBroadcastManager.getInstance(GCMNotificationIntentService.this).sendBroadcast(pushIntent);
			        } else {
			        	sendNotification(extras.get(Constants.TYPE).toString(), extras.get(Constants.EXTRA_MESSAGE).toString(), extras.get(Constants.SENDER_PHOTO).toString(), "", extras.get("sender_id").toString());
			        }
				} else {
					sendNotification(extras.get(Constants.TYPE).toString(), extras.get(Constants.EXTRA_MESSAGE).toString(), extras.get(Constants.SENDER_PHOTO).toString(), "", extras.get("sender_id").toString());
				}
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void sendNotification(String type, String message, String photo, String username, String sender_id) {
		
		if(type.equals("chat")) {
			
			NotificationCompat.Builder notification;
		    NotificationManager manager;
		    
			Bundle args = new Bundle();
			args.putString("receiver_id", sender_id);
			args.putString("receiver_user_avatar", photo);
			args.putString("receiver_name", username);
	        args.putString("message", message);
	        
	        Intent chat = new Intent(this, ChatActivity.class);
	        chat.putExtra("INFO", args);
	        notification = new NotificationCompat.Builder(this);
	        notification.setContentTitle(username);
	        notification.setContentText(message);
	        notification.setTicker("JobMe");
	        notification.setSmallIcon(R.drawable.ic_launcher);

	        PendingIntent contentIntent = PendingIntent.getActivity(this, 1000,
	                chat, PendingIntent.FLAG_CANCEL_CURRENT);
	        notification.setContentIntent(contentIntent);
	        notification.setAutoCancel(true);
	        manager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	        manager.notify(0, notification.build());
	        
	        Intent pushIntent = new Intent(PUSH_RECEIVED);
			LocalBroadcastManager.getInstance(GCMNotificationIntentService.this).sendBroadcast(pushIntent);
		} else {
			int icon = R.drawable.ic_launcher;
	        long when = System.currentTimeMillis();
	        NotificationManager notificationManager = (NotificationManager)
	                this.getSystemService(Context.NOTIFICATION_SERVICE);
	        Notification notification = new Notification(icon, message, when);
	        
	        Intent notificationIntent = new Intent(this, MainActivity.class);
	        notificationIntent.putExtra("notification", 1);
	        notificationIntent.putExtra("type", type);
	        
	        // set intent so it does not start a new activity
	        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
	                Intent.FLAG_ACTIVITY_SINGLE_TOP);
	        PendingIntent intent =
	                PendingIntent.getActivity(this, 0, notificationIntent, 0);
	        notification.setLatestEventInfo(this, "JobMe", message, intent);
	        notification.flags |= Notification.FLAG_AUTO_CANCEL;
	        
	        // Play default notification sound
	        notification.defaults |= Notification.DEFAULT_SOUND;
	        notification.number |= 5;
	        
	        // Vibrate if vibrate is enabled
	        notification.defaults |= Notification.DEFAULT_VIBRATE;
	        notificationManager.notify(0, notification);    
		}
	}
}
