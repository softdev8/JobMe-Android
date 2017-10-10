package com.search.jobme.until;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.text.ParseException;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utility {
	
	Context context;
	ArrayList<Bitmap> ImageList;
	
	public Utility(Context context) {
		this.context = context;

	}

	public boolean isConnectingToInternet() {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}

		}
		return false;
	}

	public void Message(String msg) {
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.setTitle("JobMe");
		alertDialog.setMessage(msg);
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		alertDialog.show();

	}

	public void MessageWithFinish(final Activity activity, String msg) {
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.setTitle("JobMe");
		alertDialog.setMessage(msg);
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				activity.finish();
			}
		});
		alertDialog.show();

	}
	
	
}
