package com.search.jobme;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.search.jobme.model.Education;
import com.search.jobme.model.Experience;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class EditEducation extends Activity implements OnClickListener {
	
	EditText txt_school_name, txt_field, txt_degree;
	TextView txt_start_date, txt_end_date, txt_select_date, txt_select_date1, line, txt_delete;
	ImageView btnClose, btnSave, img_status;
	RelativeLayout layout;
	
	boolean m_bOn = true;
	
	private Calendar calendar;
	private int year, month, day;
	
	boolean iEndDate = false;
	int gEMonth, gEYear, gSMonth, gSYear;
	
	private AlertDialog dialog;
	private String[] arr_degree;
	int degreeIndex = -1;
	
	boolean bUpdate = false;
	int edu_index;
	Education item;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.education);
		
		Intent intent = getIntent();
		bUpdate = intent.getBooleanExtra("update", false);
		edu_index = intent.getIntExtra("edu_index", -1);
		
		calendar = Calendar.getInstance();
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);

		txt_school_name = (EditText) findViewById(R.id.txt_school_name);
		txt_school_name.setBackgroundResource(android.R.color.transparent);
		
		txt_field = (EditText) findViewById(R.id.txt_field);
		txt_field.setBackgroundResource(android.R.color.transparent);
		
		txt_start_date = (TextView) findViewById(R.id.txt_start_date);
		txt_end_date = (TextView) findViewById(R.id.txt_end_date);
		txt_select_date = (TextView) findViewById(R.id.txt_select_date);
		txt_select_date.setOnClickListener(this);
		txt_select_date1 = (TextView) findViewById(R.id.txt_select_date1);
		txt_select_date1.setOnClickListener(this);
		
		txt_delete = (TextView) findViewById(R.id.txt_delete);
		txt_delete.setOnClickListener(this);
		
		txt_degree = (EditText) findViewById(R.id.txt_degree);
		txt_degree.setBackgroundResource(android.R.color.transparent);
		
		btnClose = (ImageView) findViewById(R.id.btnClose);
		btnClose.setOnClickListener(this);
		
		btnSave = (ImageView) findViewById(R.id.btnSave);
		btnSave.setOnClickListener(this);
		
		img_status = (ImageView) findViewById(R.id.img_status);
		img_status.setOnClickListener(this);
		
		img_status.setImageResource(R.drawable.on);	
		
		layout = (RelativeLayout) findViewById(R.id.layout);
		layout.setVisibility(View.GONE);
		line = (TextView) findViewById(R.id.line);
		line.setVisibility(View.GONE);
		
		if(bUpdate) {
			item = EditProfile.education_data.get(edu_index);
			
			txt_school_name.setText(item.getSchool());
			txt_field.setText(item.getField());
			txt_degree.setText(item.getDegree());
			
			String[] _date = getDate(item.getStart_date());
			String start_date = String.format("%s %s", getMonthShortName(Integer.valueOf(_date[0]).intValue() - 1), _date[1]);
			txt_start_date.setText(start_date);
			
			gSMonth = Integer.valueOf(_date[0]).intValue() - 1;
			gSYear = Integer.valueOf(_date[1]).intValue();
			
			if(item.getEnd_date().equals("Present")) {
				m_bOn = false;
				txt_end_date.setText("Present");
			} else {
				m_bOn = true;
				String[] _date1 = getDate(item.getEnd_date());
				String end_date = String.format("%s %s", getMonthShortName(Integer.valueOf(_date1[0]).intValue() - 1), _date1[1]);
				txt_end_date.setText(end_date);
				
				gEMonth = Integer.valueOf(_date1[0]).intValue() - 1;
				gEYear = Integer.valueOf(_date1[1]).intValue();
			}
			setStatus();			
		}
	}
	
	private void setStatus() {
		if (m_bOn) {
			m_bOn = false;
			img_status.setImageResource(R.drawable.off);
			layout.setVisibility(View.VISIBLE);
			line.setVisibility(View.VISIBLE);
		} else {
			m_bOn = true;
			img_status.setImageResource(R.drawable.on);
			layout.setVisibility(View.GONE);
			line.setVisibility(View.GONE);
		}
	}
	
	private String[] getDate(String str_date) {
		
		String[] arr_date = str_date.split("/");
		
		return arr_date;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
			case R.id.img_status:
				setStatus();
				break;
			case R.id.btnClose:
				finish();
				break;
			case R.id.btnSave:
				if(txt_school_name.length() == 0) {
					Toast.makeText(this, "Please enter school name",
				              Toast.LENGTH_SHORT).show();
				} else if(txt_degree.length() == 0) {
					Toast.makeText(this, "Please enter degree",
				              Toast.LENGTH_SHORT).show();
				} else if(txt_field.length() == 0) {
					Toast.makeText(this, "Please enter field of study",
				              Toast.LENGTH_SHORT).show();
				} else {
					Intent intent = new Intent();
					intent.putExtra("delete", false);
					intent.putExtra("update", bUpdate);
					intent.putExtra("edu_index", edu_index);
					intent.putExtra("school_name", txt_school_name.getText().toString());
					intent.putExtra("degree", txt_degree.getText().toString());
					intent.putExtra("field_study", txt_field.getText().toString());
					
					intent.putExtra("start_date", String.format("%d/%d", gSMonth + 1, gSYear));
					if(m_bOn) {
						intent.putExtra("end_date", "Present");
					} else {
						intent.putExtra("end_date", String.format("%d/%d", gEMonth + 1, gEYear));
					}
					
					setResult(RESULT_OK, intent);
					finish();
				}
				break;
			case R.id.txt_select_date:
				if(bUpdate) {
					String[] _date = getDate(item.getStart_date());
					month = Integer.valueOf(_date[0]).intValue() - 1;
					year = Integer.valueOf(_date[1]).intValue();
 				}
				createDialogWithoutDateField().show();
				break;
			case R.id.txt_select_date1:
				if(bUpdate) {
					if(!item.getEnd_date().equals("Present")) {
						String[] _date = getDate(item.getEnd_date());
						month = Integer.valueOf(_date[0]).intValue() - 1;
						year = Integer.valueOf(_date[1]).intValue();
					} else {
						year = calendar.get(Calendar.YEAR);
						month = calendar.get(Calendar.MONTH);
					}
				}
				iEndDate = true;
				createDialogWithoutDateField().show();
				break;
			case R.id.txt_delete:
				EditProfile.education_data.remove(edu_index);
				
				Intent intent = new Intent();
				intent.putExtra("delete", true);
				intent.putExtra("update", true);
				setResult(RESULT_OK, intent);
				finish();
				break;
		}
	}
	
	private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker arg0, int year, int month, int day) {
			// TODO Auto-generated method stub
			showDate(year, month, day);
		}
	};

	@SuppressLint("NewApi")
	private void showDate(int year, int month, int day) {
		String strMonth = getMonthShortName(month);
		

		if(!iEndDate) {
			gSMonth = month;
			gSYear = year;
			
			txt_start_date.setText(new StringBuilder().append(strMonth).append(" ")
				.append(year));
		} else {

			gEMonth = month;
			gEYear = year;
			
			txt_end_date.setText(new StringBuilder().append(strMonth).append(" ")
					.append(year));
		}
	}

	private DatePickerDialog createDialogWithoutDateField() {
		DatePickerDialog dpd = new DatePickerDialog(this, myDateListener, year,
				month, day);
		try {
			java.lang.reflect.Field[] datePickerDialogFields = dpd.getClass()
					.getDeclaredFields();
			for (java.lang.reflect.Field datePickerDialogField : datePickerDialogFields) {
				if (datePickerDialogField.getName().equals("mDatePicker")) {
					datePickerDialogField.setAccessible(true);
					DatePicker datePicker = (DatePicker) datePickerDialogField
							.get(dpd);
					java.lang.reflect.Field[] datePickerFields = datePickerDialogField
							.getType().getDeclaredFields();
					for (java.lang.reflect.Field datePickerField : datePickerFields) {
						if ("mDaySpinner".equals(datePickerField.getName())) {
							datePickerField.setAccessible(true);
							Object dayPicker = datePickerField.get(datePicker);
							((View) dayPicker).setVisibility(View.GONE);
						}
					}
				}
			}
		} catch (Exception ex) {
		}
		return dpd;
	}

	public String getMonthShortName(int monthNumber) {
		String monthName = "";

		if (monthNumber >= 0 && monthNumber < 12)
			try {
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.MONTH, monthNumber);

				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM");
				simpleDateFormat.setCalendar(calendar);
				monthName = simpleDateFormat.format(calendar.getTime());
			} catch (Exception e) {
				if (e != null)
					e.printStackTrace();
			}
		
		return monthName;
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
	}
}
