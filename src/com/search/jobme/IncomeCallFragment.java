package com.search.jobme;

import com.search.jobme.until.RingtonePlayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

@SuppressLint("NewApi")
public class IncomeCallFragment extends Fragment implements OnClickListener {
	
	ChatActivity m_Parent;
	ImageView btnReject, btnAccept;
	
	private Vibrator vibrator;
    private RingtonePlayer ringtonePlayer;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		
		m_Parent = (ChatActivity) getActivity();
		
		View v = inflater.inflate(
	    		  R.layout.incoming_call_view, container, false);
		
		btnReject = (ImageView) v.findViewById(R.id.btnReject);
		btnReject.setOnClickListener(this);
		
		btnAccept = (ImageView) v.findViewById(R.id.btnAccept);
		btnAccept.setOnClickListener(this);
		
		ringtonePlayer = new RingtonePlayer(m_Parent);
		
		return v;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
			case R.id.btnReject:
				reject();
				break;
			case R.id.btnAccept:
				accept();
				break;
		}
	}
	
	public void startCallNotification() {

        ringtonePlayer.play(false);

        vibrator = (Vibrator) m_Parent.getSystemService(m_Parent.VIBRATOR_SERVICE);

        long[] vibrationCycle = {0, 1000, 1000};
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(vibrationCycle, 1);
        }

    }

    private void stopCallNotification() {
        if (ringtonePlayer != null) {
            ringtonePlayer.stop();
        }

        if (vibrator != null) {
            vibrator.cancel();
        }
    }
	
	public void onStop() {
        stopCallNotification();
        super.onDestroy();
    }
	
	private void accept() {
        btnAccept.setClickable(false);
        stopCallNotification();

        m_Parent.addConversationFragmentReceiveCall();
    }

    private void reject() {
        btnReject.setClickable(false);

        stopCallNotification();

        m_Parent.rejectCurrentSession();
        m_Parent.removeIncomeCallFragment();
        m_Parent.addChatFragment();
    }
}
