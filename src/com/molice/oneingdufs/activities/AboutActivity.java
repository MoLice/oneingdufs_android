package com.molice.oneingdufs.activities;

import com.molice.oneingdufs.R;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;

public class AboutActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
	}
	
	@Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
    }
}
