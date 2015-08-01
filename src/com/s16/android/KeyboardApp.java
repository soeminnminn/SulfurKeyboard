package com.s16.android;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;

public class KeyboardApp extends Application {

	@Override
	public void onCreate() {
	}
	
	public static void ShowApplicationIcon(Context applicationContext) {
		PackageManager p = applicationContext.getPackageManager();
		ComponentName componentName = new ComponentName(applicationContext, com.s16.android.MainActivity.class);
		p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
	}
	
	public static void HideApplicationIcon(Context applicationContext) {
		PackageManager p = applicationContext.getPackageManager();
		ComponentName componentName = new ComponentName(applicationContext, com.s16.android.MainActivity.class);
		p.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
	}
}
