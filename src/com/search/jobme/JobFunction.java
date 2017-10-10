package com.search.jobme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class JobFunction extends Activity implements OnClickListener {

	TextView lbl_title;
	ImageView btnClose;
	
	private String[] arr_string;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.function_industry);
		
		arr_string = getResources().getStringArray(R.array.main_cat);
		
		btnClose = (ImageView) findViewById(R.id.btnClose);
		btnClose.setOnClickListener(this);
		
		ListView listView = (ListView) findViewById(R.id.listView);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
	              R.layout.job_function_item, R.id.lbl_function, arr_string);
		listView.setAdapter(adapter);
		listView.setDividerHeight(0);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("select_index", position);
				intent.putExtra("selected_title", arr_string[position]);
				setResult(RESULT_OK, intent);
				finish();
			}
			
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
			case R.id.btnClose:
				finish();
				break;
		}
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
	}
}
