package com.molice.oneingdufs.activities;

import com.molice.oneingdufs.R;
import com.molice.oneingdufs.layouts.ActionBarController;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;

public class CommonCalendarActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_calendar);
		
		// …Ë÷√±ÍÃ‚
		ActionBarController.setTitle(this, R.string.common_calendar);
		
	}
	@Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
    }
}