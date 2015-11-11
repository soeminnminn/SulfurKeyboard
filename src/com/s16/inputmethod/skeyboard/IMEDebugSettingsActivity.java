package com.s16.inputmethod.skeyboard;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;

public class IMEDebugSettingsActivity extends FragmentActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		FragmentManager manager = getSupportFragmentManager();
		if (manager != null) {
			IMEDebugSettings fragment = new IMEDebugSettings();
			FragmentTransaction transaction = manager.beginTransaction();
			transaction.replace(R.id.settings_content, fragment);
			transaction.commit();
		}
	}
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}
}
