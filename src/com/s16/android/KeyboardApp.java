package com.s16.android;

import java.lang.reflect.Field;

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
	
	public static boolean isDebug() {
		boolean result = false;
		Class<?> cls = null;
		try {
			cls = Class.forName("com.s16.inputmethod.skeyboard.BuildConfig");
        } catch (ClassNotFoundException e) {
        }
		if (cls != null) {
			Field field = null;
			try {
				field = cls.getField("DEBUG");
	        } catch (SecurityException e) {
	        } catch (NoSuchFieldException e) {
	        }
			if (field != null) {
				try {
					result = (boolean)field.getBoolean(null);
				} catch (IllegalAccessException e) {
				} catch (IllegalArgumentException e) {
				}
			}
		}
		
		return result;
	}
}
