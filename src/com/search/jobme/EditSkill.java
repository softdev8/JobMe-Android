package com.search.jobme;

import java.util.ArrayList;
import java.util.Calendar;

import com.search.jobme.model.SkillModel;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.OnEditorActionListener;

public class EditSkill extends Activity implements OnClickListener {
	
	AutoCompleteTextView txt_skill;
	ImageView btnClose, btnSave;
	LinearLayout skill_parent;
	TextView txt_width;
	LinearLayout temp0;
	int parent_width;
	int gWidth;
	String[] arr_total_skill;
	String str_skill = "";
	ArrayList<String> selected_skill = new ArrayList<String>();
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.skill);
		
		Intent intent = getIntent();
		str_skill = intent.getStringExtra("skill");
		
		if(!str_skill.contains(",")) {
			if(!str_skill.equals("")) {
				selected_skill.add(str_skill);					
			} 
		} else {
			String[] arr = str_skill.split(",");
			StringBuilder stringBuilder = new StringBuilder();
			for(int i = 0 ; i < arr.length ; i++) {
				selected_skill.add(arr[i]);
			}
		}
		
		arr_total_skill = new String[MainActivity.skill_data.size()];
		for(int i = 0 ; i < MainActivity.skill_data.size(); i++) {
			SkillModel item = MainActivity.skill_data.get(i);
			arr_total_skill[i] = item.getTitle();
		}
		
		btnClose = (ImageView) findViewById(R.id.btnClose);
		btnClose.setOnClickListener(this);

		btnSave = (ImageView) findViewById(R.id.btnSave);
		btnSave.setOnClickListener(this);
		
		txt_skill = (AutoCompleteTextView) findViewById(R.id.txt_skill);
		txt_skill.setBackgroundResource(android.R.color.transparent);
		txt_skill.setOnItemClickListener(new OnItemClickListener() {

	        @Override
	        public void onItemClick(AdapterView<?> parent, View arg1, int pos,
	                long id) {
	        	
	        	String selection = (String) parent.getItemAtPosition(pos);
	        	
	        	addSkill(selection);
	        }
	    });
		
		txt_skill.setOnEditorActionListener(new OnEditorActionListener() {

            public boolean onEditorAction(TextView v, int actionId,
                    KeyEvent event) {
                if (event != null&& (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in = (InputMethodManager) getSystemService(EditSkill.INPUT_METHOD_SERVICE);
                    addSkill(v.getText().toString());
                 
                    return true;
                }
                return false;
            }
        });
		
		ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, arr_total_skill);
	      
		txt_skill.setAdapter(adapter);
		txt_skill.setThreshold(1);
		
		txt_width = (TextView) findViewById(R.id.txt_width);
		
		skill_parent = (LinearLayout) findViewById(R.id.skill_parent);
		skill_parent.post(new Runnable() 
	    {
	        @Override
	        public void run()
	        {
	        	parent_width = skill_parent.getWidth();
	        	gWidth = parent_width;
	        	
	        	addSubLayout();
	        }
	    });
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.btnClose:
				finish();
				break;
			case R.id.btnSave:
				Intent intent = new Intent();
				intent.putStringArrayListExtra("skills", selected_skill);
				setResult(RESULT_OK, intent);
				finish();
				break;
		}
	}
	
	private void addSkill(String new_skill) {
		int count = 0;
		
		if(new_skill.equals("")) return;
		
    	for(int i = 0 ; i < selected_skill.size(); i++) {
    		if(selected_skill.get(i).toString().equals(new_skill)) {
    			count++;
    		}
    	}
    	
    	if(count == 0) {
    		selected_skill.add(new_skill);
    		
    		skill_parent.removeAllViews();
    		
    		parent_width = gWidth; 
    		addSubLayout();
    	}
    	
    	txt_skill.setText("");
    	
	}
	
	private void addSubLayout() {
		LinearLayout temp0 = new LinearLayout(EditSkill.this);
	    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.MATCH_PARENT);
	    temp0.setOrientation(LinearLayout.HORIZONTAL);
	    param.setMargins(0, 10, 0, 0);
	    
	    skill_parent.addView(temp0, param);
	    
    	addLayout(temp0);
	}
	
	private void addLayout(LinearLayout temp0) {
	    
	    for(int i = 0 ; i < selected_skill.size(); i++) {
	    	Rect bounds = new Rect();
    		Paint textPaint = txt_width.getPaint();
    		String text = selected_skill.get(i);
    		textPaint.getTextBounds(text,0,text.length(),bounds);
    		int skill_text_width = bounds.width() + 110;
    		
    		if(parent_width - skill_text_width > 0) {
    			addSkill(temp0, selected_skill.get(i), i);
    		} else {
    			temp0 = new LinearLayout(EditSkill.this);
    			LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
    					LayoutParams.FILL_PARENT, LayoutParams.MATCH_PARENT);
    		    temp0.setOrientation(LinearLayout.HORIZONTAL);
    		    param.setMargins(0, 10, 0, 0);
    		    skill_parent.addView(temp0, param);
    		    
    		    addSkill(temp0, selected_skill.get(i), i);
    		    parent_width = gWidth;
    		    parent_width -= skill_text_width;
    		    continue;
    		}
    		
    		parent_width -= skill_text_width;
	    }
	}
	
	private void addSkill(LinearLayout parent, final String str_skill, int i) {
		LinearLayout temp = new LinearLayout(EditSkill.this);
	    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, 70);
	    temp.setOrientation(LinearLayout.HORIZONTAL);
	    temp.setGravity(Gravity.CENTER_VERTICAL);
	    temp.setBackgroundResource(R.drawable.edittext_border);
	    param.setMargins(10, 0, 0, 0);
	    parent.addView(temp, param);
	     	            
	    TextView text1 = new TextView(EditSkill.this);
	    LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		text1.setText(str_skill);
		text1.setTextColor(Color.parseColor("#555555"));
		param1.setMargins(20, 0, 0, 0);
		temp.addView(text1, param1);
			
		ImageView icon = new ImageView(EditSkill.this);
		LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(40, 40);
		icon.setImageResource(R.drawable.close);
		param2.setMargins(20, 0, 20, 0);
		temp.addView(icon, param2);
		icon.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selected_skill.remove(str_skill);
				skill_parent.removeAllViews();
        		
        		parent_width = gWidth; 
        		addSubLayout();
			}
		});
			
	}
}
