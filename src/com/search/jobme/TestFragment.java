package com.search.jobme;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.LinearLayout.LayoutParams;

public final class TestFragment extends Fragment {

    int[] resource_id = { R.drawable.slide_1, R.drawable.slide_2, R.drawable.slide_3, R.drawable.slide_4 };
    
    public static TestFragment newInstance(int position) {
        TestFragment fragment = new TestFragment();

        fragment.page_index = position;

        return fragment;
    }

    private int page_index;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
    	View view = inflater.inflate(R.layout.main_pagerview, null);
		
		ImageView menu_icon = (ImageView) view.findViewById(R.id.menu_icon);
		menu_icon.setBackgroundResource(resource_id[page_index]);
		
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
