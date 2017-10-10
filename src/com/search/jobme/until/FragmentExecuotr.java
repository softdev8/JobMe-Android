package com.search.jobme.until;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;

@SuppressLint("NewApi")
public class FragmentExecuotr {

	public static void addFragment(FragmentManager fragmentManager, int containerId, Fragment fragment, String tag) {
        fragmentManager.beginTransaction().replace(containerId, fragment, tag).commitAllowingStateLoss();
    }

	public static void removeFragment(FragmentManager fragmentManager, Fragment fragment) {
        fragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss();
    }
}
