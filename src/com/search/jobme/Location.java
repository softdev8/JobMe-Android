package com.search.jobme;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.search.jobme.model.LocationModel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Location extends Activity implements
		OnItemClickListener, OnClickListener {

	private ImageView btnClose, btnSave;
	TextView txt_current_location;
	ArrayList<LocationModel> data = new ArrayList<LocationModel>();
	
	String lat, lot;
	AutoCompleteTextView autoCompView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location);
		
		autoCompView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
		autoCompView.setBackgroundResource(android.R.color.transparent);
		
		btnClose = (ImageView) findViewById(R.id.btnClose);
		btnClose.setOnClickListener(this);
		
		btnSave = (ImageView) findViewById(R.id.btnSave);
		btnSave.setOnClickListener(this);
		
		txt_current_location = (TextView) findViewById(R.id.txt_current_location);
		txt_current_location.setOnClickListener(this);
		
		autoCompView.setAdapter(new GooglePlacesAutocompleteAdapter(this,
				R.layout.list_item));
		autoCompView.setOnItemClickListener(this);
	}

	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		
		LocationModel item = data.get(position);
		
		lat =  item.getLatitude();
		lot = item.getLongitude();
	}

	public ArrayList<String> autocomplete(String input) {
		ArrayList<String> resultList = new ArrayList<String>();
		
		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		try {

			URL url = new URL(
					"https://maps.googleapis.com/maps/api/geocode/json?sensor=true&address="
							+ input);

			System.out.println("URL: " + url);
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());

			// Load the results into a StringBuilder
			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1) {
				jsonResults.append(buff, 0, read);
			}
		} catch (MalformedURLException e) {
			return resultList;
		} catch (IOException e) {
			return resultList;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		data.clear();
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = new JSONObject(jsonResults.toString());

			JSONArray arr = jsonObject.getJSONArray("results");

			for (int i = 0; i < arr.length(); i++) {
				JSONObject obj = arr.getJSONObject(i);

				LocationModel item = new LocationModel();
	        	
				resultList.add(obj.getString("formatted_address"));
	        	item.setLocation_name(obj.getString("formatted_address"));
	        	
//	        	JSONArray arr_address = obj.getJSONArray("address_components");
//	        	JSONObject obj1 = arr_address.getJSONObject(arr_address.length() - 1);
//	        	resultList.add(String.format("%s, %s", obj1.getString("short_name"), obj1.getString("long_name")));
//	        	item.setLocation_name(String.format("%s, %s", obj1.getString("short_name"), obj1.getString("long_name")));
	        	
	        	JSONObject location_obj = obj.getJSONObject("geometry");
	        	JSONObject sub_obj = location_obj.getJSONObject("location");
	        	
	        	item.setLongitude(sub_obj.getString("lng"));
	        	item.setLatitude(sub_obj.getString("lat"));
	        	
	        	data.add(item);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return resultList;
	}

	class GooglePlacesAutocompleteAdapter extends ArrayAdapter<String>
			implements Filterable {
		private ArrayList<String> resultList;

		public GooglePlacesAutocompleteAdapter(Context context,
				int textViewResourceId) {
			super(context, textViewResourceId);
		}

		@Override
		public int getCount() {
			return resultList.size();
		}

		@Override
		public String getItem(int index) {
			return resultList.get(index);
		}

		@Override
		public Filter getFilter() {
			Filter filter = new Filter() {
				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults filterResults = new FilterResults();
					if (constraint != null) {
						// Retrieve the autocomplete results.
						resultList = autocomplete(constraint.toString());

						// Assign the data to the FilterResults
						filterResults.values = resultList;
						filterResults.count = resultList.size();
					}
					return filterResults;
				}

				@Override
				protected void publishResults(CharSequence constraint,
						FilterResults results) {
					if (results != null && results.count > 0) {
						notifyDataSetChanged();
					} else {
						notifyDataSetInvalidated();
					}
				}
			};
			return filter;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch(v.getId()) {
			case R.id.btnClose:
				finish();
				break;
			case R.id.btnSave:
				intent = new Intent();
				intent.putExtra("any", "0");
				intent.putExtra("location", autoCompView.getText().toString());
				intent.putExtra("lat", lat);
				intent.putExtra("lot", lot);
				setResult(RESULT_OK, intent);
				finish();
				break;
			case R.id.txt_current_location:
				intent = new Intent();
				intent.putExtra("any", "1");
				setResult(RESULT_OK, intent);
				finish();
				break;
		}
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
	}

}
