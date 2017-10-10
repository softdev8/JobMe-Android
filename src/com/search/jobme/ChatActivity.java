package com.search.jobme;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.webrtc.VideoCapturerAndroid;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.quickblox.chat.*;
import com.quickblox.chat.listeners.QBVideoChatSignalingManagerListener;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCConfig;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;
import com.quickblox.videochat.webrtc.QBSignalingSpec.QBSignalCMD;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientSessionCallbacks;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientVideoTracksCallbacks;
import com.quickblox.videochat.webrtc.callbacks.QBRTCSessionConnectionCallbacks;
import com.quickblox.videochat.webrtc.callbacks.QBRTCSignalingCallback;
import com.quickblox.videochat.webrtc.exception.QBRTCException;
import com.quickblox.videochat.webrtc.exception.QBRTCSignalException;
import com.search.jobme.ImageLoader.ImageLoader;
import com.search.jobme.model.HistroyChatModel;
import com.search.jobme.model.UserInfo;
import com.search.jobme.until.APIManager;
import com.search.jobme.until.FragmentExecuotr;
import com.search.jobme.until.RingtonePlayer;
import com.search.jobme.until.Utility;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class ChatActivity extends Activity implements QBRTCClientSessionCallbacks, QBRTCSessionConnectionCallbacks, QBRTCSignalingCallback {
	
	private static final String TAG = ChatActivity.class.getSimpleName();
	
	public static final String CHAT_FRAGMENT = "chat_fragment";		//OPPONENTS_CALL_FRAGMENT
    public static final String INCOME_CALL_FRAGMENT = "income_call_fragment";
    public static final String CONVERSATION_CALL_FRAGMENT = "conversation_call_fragment";
    public static final String CALLER_NAME = "caller_name";
    public static final String SESSION_ID = "sessionID";
    public static final String START_CONVERSATION_REASON = "start_conversation_reason";
	
    private QBRTCSession currentSession;
    public  List<QBUser> opponentsList = new ArrayList<QBUser>();
    private Runnable showIncomingCallWindowTask;
    private Handler showIncomingCallWindowTaskHandler;
    private BroadcastReceiver wifiStateReceiver;
    private boolean closeByWifiStateAllow = true;
    private String hangUpReason;
    
    private boolean isInCommingCall;
    private boolean isInFront;
    private QBRTCClient rtcClient;
    private QBRTCSessionUserCallback sessionUserCallback;
    private boolean wifiEnabled = true;
    private SharedPreferences sharedPref;
    private RingtonePlayer ringtonePlayer;
    
//    private Chronometer timerABWithTimer;
    private boolean isStarted = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

		Intent intent = getIntent();
		String receiver_name = intent.getStringExtra("receiver_name");
		String qlogin = intent.getStringExtra("qlogin");
		String videochat_id = intent.getStringExtra("videochat_id");
		
        QBUser newUser = new QBUser(qlogin, "");
        newUser.setId(Integer.valueOf(videochat_id).intValue());
        newUser.setFullName(receiver_name);
        opponentsList.add(newUser);
        
		if(savedInstanceState == null) {
			addChatFragment();
		}
		
		if(rtcClient == null) {
			initQBRTCClient();
		}
        initWiFiManagerListener();
        ringtonePlayer = new RingtonePlayer(this, R.raw.beep);
	}	
	
	private void initQBRTCClient() {
        rtcClient = QBRTCClient.getInstance(this);
        // Add signalling manager
        QBChatService.getInstance().getVideoChatWebRTCSignalingManager().addSignalingManagerListener(new QBVideoChatSignalingManagerListener() {
            @Override
            public void signalingCreated(QBSignaling qbSignaling, boolean createdLocally) {
                if (!createdLocally) {
                    rtcClient.addSignaling((QBWebRTCSignaling) qbSignaling);
                }
            }
        });

        rtcClient.setCameraErrorHendler(new VideoCapturerAndroid.CameraErrorHandler() {
            @Override
            public void onCameraError(final String s) {
                ChatActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ChatActivity.this, s, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });


        // Configure
        //
        QBRTCConfig.setMaxOpponentsCount(6);
        QBRTCConfig.setDisconnectTime(30);
        QBRTCConfig.setAnswerTimeInterval(30l);
        QBRTCConfig.setDebugEnabled(true);


        // Add activity as callback to RTCClient
        rtcClient.addSessionCallbacksListener(this);
        // Start mange QBRTCSessions according to VideoCall parser's callbacks
        rtcClient.prepareToProcessCalls();
    }

    private void initWiFiManagerListener() {
        wifiStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                processCurrentWifiState(context);
            }
        };
    }
    
    private void processCurrentWifiState(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(WIFI_SERVICE);
        if (wifiEnabled != wifi.isWifiEnabled()) {
            wifiEnabled = wifi.isWifiEnabled();
        }
    }
    
    private void disableConversationFragmentButtons() {
        ConversationFragment fragment = (ConversationFragment) getFragmentManager().findFragmentByTag(CONVERSATION_CALL_FRAGMENT);
        if (fragment != null) {
//            fragment.actionButtonsEnabled(false);
        }
    }
    
    public void addConversationFragmentStartCall(List<QBUser> opponents,
            QBRTCTypes.QBConferenceType qbConferenceType,
            Map<String, String> userInfo) {
		QBRTCSession newSessionWithOpponents = rtcClient.createNewSessionWithOpponents(getOpponentsIds(opponents), qbConferenceType);
		
		initCurrentSession(newSessionWithOpponents);
		ConversationFragment fragment = ConversationFragment.newInstance(opponents, opponents.get(0).getFullName(), qbConferenceType, userInfo, StartConversetionReason.OUTCOME_CALL_MADE, getCurrentSession().getSessionID());
		FragmentExecuotr.addFragment(getFragmentManager(), R.id.fragment_container, fragment, CONVERSATION_CALL_FRAGMENT);
		ringtonePlayer.play(true);
	}
    
    public ArrayList<Integer> getOpponentsIds(List<QBUser> opponents) {
        ArrayList<Integer> ids = new ArrayList<Integer>();
        for (QBUser user : opponents) {
            ids.add(user.getId());
        }
        return ids;
    }
	    
    public void addConversationFragmentReceiveCall() {

        QBRTCSession session = getCurrentSession();

        if (getCurrentSession() != null) {
            Integer myId = QBChatService.getInstance().getUser().getId();
            ArrayList<Integer> opponentsWithoutMe = new ArrayList<Integer>(session.getOpponents());
            opponentsWithoutMe.remove(new Integer(myId));
            opponentsWithoutMe.add(session.getCallerID());

            ArrayList<QBUser> opponents = getUsersByIDs(opponentsWithoutMe.toArray(new Integer[opponentsWithoutMe.size()]));
            ConversationFragment fragment = ConversationFragment.newInstance(opponents,
                    "",
                    session.getConferenceType(), session.getUserInfo(),
                    StartConversetionReason.INCOME_CALL_FOR_ACCEPTION, getCurrentSession().getSessionID());
            // Start conversation fragment
            FragmentExecuotr.addFragment(getFragmentManager(), R.id.fragment_container, fragment, CONVERSATION_CALL_FRAGMENT);
        }
    }
    
    public ArrayList<QBUser> getUsersByIDs(Integer... ids) {
        ArrayList<QBUser> result = new ArrayList<QBUser>();
        for (Integer userId : ids) {
            for (QBUser user : opponentsList) {
                if (userId.equals(user.getId())){
                    result.add(user);
                }
            }
        }
        return result;
    }
    
    public void addVideoTrackCallbacksListener(QBRTCClientVideoTracksCallbacks videoTracksCallbacks) {
        if (currentSession != null){
            currentSession.addVideoTrackCallbacksListener(videoTracksCallbacks);
        }
    }

    public void addTCClientConnectionCallback(QBRTCSessionConnectionCallbacks clientConnectionCallbacks) {
        if (currentSession != null) {
            currentSession.addSessionCallbacksListener(clientConnectionCallbacks);
        }
    }

    public void removeRTCClientConnectionCallback(QBRTCSessionConnectionCallbacks clientConnectionCallbacks) {
        if (currentSession != null) {
            currentSession.removeSessionnCallbacksListener(clientConnectionCallbacks);
        }
    }

    public void addRTCSessionUserCallback(QBRTCSessionUserCallback sessionUserCallback) {
        this.sessionUserCallback = sessionUserCallback;
    }

    public void removeRTCSessionUserCallback(QBRTCSessionUserCallback sessionUserCallback) {
        this.sessionUserCallback = null;
    }
    
    private void initIncommingCallTask() {
        showIncomingCallWindowTaskHandler = new Handler(Looper.myLooper());
        showIncomingCallWindowTask = new Runnable() {
            @Override
            public void run() {
                IncomeCallFragment incomeCallFragment = (IncomeCallFragment) getFragmentManager().findFragmentByTag(INCOME_CALL_FRAGMENT);
                if (incomeCallFragment == null) {
                    ConversationFragment conversationFragment = (ConversationFragment) getFragmentManager().findFragmentByTag(CONVERSATION_CALL_FRAGMENT);
                    if (conversationFragment != null) {
                        disableConversationFragmentButtons();
                        ringtonePlayer.stop();
                        hangUpCurrentSession();
                    }
                } else {
                    rejectCurrentSession();
                }
                Toast.makeText(ChatActivity.this, "Call was stopped by timer", Toast.LENGTH_LONG).show();
            }
        };
    }
    
    public void rejectCurrentSession() {
        if (getCurrentSession() != null) {
            getCurrentSession().rejectCall(new HashMap<String, String>());
        }
    }

    public void hangUpCurrentSession() {
        ringtonePlayer.stop();
        if (getCurrentSession() != null) {
            getCurrentSession().hangUp(new HashMap<String, String>());
        }
    }
    
    private Fragment getCurrentFragment(){
        return getFragmentManager().findFragmentById(R.id.fragment_container);
    }
	
	@SuppressLint("NewApi")
	public void addChatFragment() {
        FragmentExecuotr.addFragment(getFragmentManager(), R.id.fragment_container,  new ChatFragment(), CHAT_FRAGMENT);
    }
	
	public void removeIncomeCallFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(INCOME_CALL_FRAGMENT);

        if (fragment != null) {
            FragmentExecuotr.removeFragment(fragmentManager, fragment);
        }
    }
	
	@SuppressLint("NewApi")
	private void addIncomeCallFragment(QBRTCSession session) {

        if (session != null && isInFront) {
            Fragment fragment = new IncomeCallFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("sessionDescription", session.getSessionDescription());
            bundle.putIntegerArrayList("opponents", new ArrayList<Integer>(session.getOpponents()));
            fragment.setArguments(bundle);
            FragmentExecuotr.addFragment(getFragmentManager(), R.id.fragment_container, fragment, INCOME_CALL_FRAGMENT);
        } else {
            Log.d(TAG, "SKIP addIncomeCallFragment method");
        }
    }
	
	public QBRTCSession getCurrentSession() {
        return currentSession;
    }

    private void forbidenCloseByWifiState() {
        closeByWifiStateAllow = false;
    }


    public void initCurrentSession(QBRTCSession sesion) {
        this.currentSession = sesion;
        this.currentSession.addSessionCallbacksListener(ChatActivity.this);
        this.currentSession.addSignalingCallback(ChatActivity.this);
    }

    public void releaseCurrentSession() {
        this.currentSession.removeSessionnCallbacksListener(ChatActivity.this);
        this.currentSession.removeSignalingCallback(ChatActivity.this);
        this.currentSession = null;
    }
    
    private void startIncomeCallTimer(long time) {
        showIncomingCallWindowTaskHandler.postAtTime(showIncomingCallWindowTask, SystemClock.uptimeMillis() + time);
    }

    private void stopIncomeCallTimer() {
        Log.d(TAG, "stopIncomeCallTimer");
        showIncomingCallWindowTaskHandler.removeCallbacks(showIncomingCallWindowTask);
    }
    
    //QBRTC Callback
	@Override
	public void onErrorSendingPacket(QBSignalCMD arg0, Integer arg1,
			QBRTCSignalException arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSuccessSendingPacket(QBSignalCMD arg0, Integer arg1) {
		// TODO Auto-generated method stub
		
	}
	
	public static enum StartConversetionReason {
        INCOME_CALL_FOR_ACCEPTION,
        OUTCOME_CALL_MADE;
    }

	@Override
	public void onConnectedToUser(QBRTCSession arg0, Integer arg1) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isInCommingCall) {
//                    stopIncomeCallTimer();
                }

//                startTimer();
                Log.d(TAG, "onConnectedToUser() is started");

            }
        });
	}

	@Override
	public void onConnectionClosedForUser(QBRTCSession arg0, Integer arg1) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Close app after session close of network was disabled
                if (hangUpReason != null && hangUpReason.equals(Constants.WIFI_DISABLED)) {
                    Intent returnIntent = new Intent();
                    setResult(Constants.CALL_ACTIVITY_CLOSE_WIFI_DISABLED, returnIntent);
                    finish();
                }
            }
        });
	}

	@Override
	public void onConnectionFailedWithUser(QBRTCSession arg0, Integer arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnectedFromUser(QBRTCSession arg0, Integer arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnectedTimeoutFromUser(QBRTCSession arg0, Integer arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(QBRTCSession arg0, QBRTCException arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStartConnectToUser(QBRTCSession arg0, Integer arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCallAcceptByUser(QBRTCSession session, Integer userId,
			Map<String, String> userInfo) {
		// TODO Auto-generated method stub
		if (!session.equals(getCurrentSession())) {
            return;
        }
        if (sessionUserCallback != null) {
            sessionUserCallback.onCallAcceptByUser(session, userId, userInfo);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ringtonePlayer.stop();
            }
        });
	}

	@Override
	public void onCallRejectByUser(QBRTCSession session, Integer userId,
			Map<String, String> userInfo) {
		// TODO Auto-generated method stub
		if (!session.equals(getCurrentSession())) {
            return;
        }
        if (sessionUserCallback != null) {
            sessionUserCallback.onCallRejectByUser(session, userId, userInfo);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ringtonePlayer.stop();
            }
        });
	}

	@Override
	public void onReceiveHangUpFromUser(final QBRTCSession session, Integer userID) {
		// TODO Auto-generated method stub
		if (session.equals(getCurrentSession())) {

            if (sessionUserCallback != null) {
                sessionUserCallback.onReceiveHangUpFromUser(session, userID);
            }
        }
	}

	@Override
	public void onReceiveNewSession(final QBRTCSession session) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {
            @Override
            public void run() {

                String curSession = (getCurrentSession() == null) ? null : getCurrentSession().getSessionID();

                if (getCurrentSession() == null) {
                    initCurrentSession(session);
                    addIncomeCallFragment(session);

                    isInCommingCall = true;
                    initIncommingCallTask();
                } else {
                    Log.d(TAG, "Stop new session. Device now is busy");
                    session.rejectCall(null);
                }

            }
        });
	}

	@Override
	public void onSessionClosed(final QBRTCSession session) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Log.d(TAG, "Session " + session.getSessionID() + " start stop session");
                String curSession = (getCurrentSession() == null) ? null : getCurrentSession().getSessionID();

                if (session.equals(getCurrentSession())) {

                    Fragment currentFragment = getCurrentFragment();
                    if (isInCommingCall) {
                        stopIncomeCallTimer();
                        if (currentFragment instanceof IncomeCallFragment) {
                            removeIncomeCallFragment();
                        }
                    }

                    Log.d(TAG, "Stop session");
                    if (!(currentFragment instanceof ChatFragment)) {
                        addChatFragment();
                    }

                    releaseCurrentSession();

//                    stopTimer();
                    closeByWifiStateAllow = true;
                }
            }
        });
	}

	@Override
	public void onSessionStartClose(final QBRTCSession session) {
		// TODO Auto-generated method stub
		session.removeSessionnCallbacksListener(ChatActivity.this);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ConversationFragment fragment = (ConversationFragment) getFragmentManager().findFragmentByTag(CONVERSATION_CALL_FRAGMENT);
                if (fragment != null && session.equals(getCurrentSession())) {
//                    fragment.actionButtonsEnabled(false);
                }
            }
        });
	}

	@Override
	public void onUserNoActions(QBRTCSession arg0, Integer arg1) {
		// TODO Auto-generated method stub
//		startIncomeCallTimer(0);
	}

	@Override
	public void onUserNotAnswer(QBRTCSession session, Integer userID) {
		// TODO Auto-generated method stub
		if (!session.equals(getCurrentSession())) {
            return;
        }
        if (sessionUserCallback != null) {
            sessionUserCallback.onUserNotAnswer(session, userID);
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ringtonePlayer.stop();
            }
        });
	}
	
	
	public interface QBRTCSessionUserCallback {
        void onUserNotAnswer(QBRTCSession session, Integer userId);

        void onCallRejectByUser(QBRTCSession session, Integer userId, Map<String, String> userInfo);

        void onCallAcceptByUser(QBRTCSession session, Integer userId, Map<String, String> userInfo);

        void onReceiveHangUpFromUser(QBRTCSession session, Integer userId);
    }
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
	}
	
	@Override
    protected void onStart() {
        super.onStart();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
//        registerReceiver(wifiStateReceiver, intentFilter);
    }
    
    @Override
    protected void onResume() {
        isInFront = true;

        if (currentSession == null) {
            addChatFragment();
        }
        super.onResume();
    }
    
    @Override
    protected void onPause() {
        isInFront = false;
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        unregisterReceiver(wifiStateReceiver);
    }
    
//    public void startTimer() {
//        if (!isStarted) {
////            timerABWithTimer.setBase(SystemClock.elapsedRealtime());
////            timerABWithTimer.start();
//            isStarted = true;
//        }
//    }

//    public void stopTimer(){
//        if (timerABWithTimer != null){
//            timerABWithTimer.stop();
//            isStarted = false;
//        }
//    }

}
