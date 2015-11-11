/*
 * Copyright (C) 2008 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.s16.inputmethod.skeyboard;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
//import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceGroup;
import android.support.v4.app.PreferenceFragment;
import android.text.AutoText;
import android.widget.Toast;

import com.s16.android.KeyboardApp;

public class IMESettings extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

	public static final String APPLICATRION_ICON_KEY = "application_icon";
    private static final String QUICK_FIXES_KEY = "quick_fixes";
    private static final String PREDICTION_SETTINGS_KEY = "prediction_settings";
    /* package */ static final String PREF_SETTINGS_KEY = "settings_key";
    /* package */ static final String PREF_LANGUAGE_KEY = "language_key";
    /* package */ static final String PREF_AUTO_HIDE_MINIKEYBOARD = "auto_hide_minikeyboard";
    /* package */ static final String PREF_EXTENDED_ROW = "extended_row";
    /* package */ static final String PREF_KEYBOARD_BACKGROUND_COLOR = "keyboard_background_color";
    private static final String KEYBOARD_LAYOUT_SETTINGS_KEY = "keyboard_layout";
    private static final String TEXT_SIZE_SETTINGS_KEY = "key_text_size";
    //private static final String USED_UNICODE_SETTINGS_KEY = "used_unicode";
    
    //private static final String CATEGORY_PREDICTION_KEY = "prediction_category";

    protected static final String TAG = "IMESettings";

    private CheckBoxPreference mQuickFixes;
    private ListPreference mLanguageKeyPreference;
    private ListPreference mKeyboardLayoutPreference;
    private ListPreference mKeyboardTextSizePreference;
    private ListPreference mKeyboardExtendedRowPreference;

	@Override
	public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.prefs);
        
        mQuickFixes = (CheckBoxPreference) findPreference(QUICK_FIXES_KEY);
        //mSettingsKeyPreference = (ListPreference) findPreference(PREF_SETTINGS_KEY);
        mLanguageKeyPreference = (ListPreference) findPreference(PREF_LANGUAGE_KEY);
        mKeyboardLayoutPreference = (ListPreference) findPreference(KEYBOARD_LAYOUT_SETTINGS_KEY);
        mKeyboardTextSizePreference = (ListPreference) findPreference(TEXT_SIZE_SETTINGS_KEY);
        mKeyboardExtendedRowPreference = (ListPreference) findPreference(PREF_EXTENDED_ROW);

        //final PreferenceGroup predictionCategory = (PreferenceGroup) findPreference(CATEGORY_PREDICTION_KEY);
        //predictionCategory.removePreference(findPreference(USED_UNICODE_SETTINGS_KEY));
        
        SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
        prefs.registerOnSharedPreferenceChangeListener(this);

		//CheckBoxPreference jbFix = (CheckBoxPreference)findPreference("used_jbfix");
		//jbFix.setEnabled(Build.VERSION.SDK_INT > 15);
    }

    @Override
    public void onStart() {
        super.onStart();
        IMESettings.AlertForInstallLocation(getActivity(), null);
    }
    
	@Override
	public void onResume() {
        super.onResume();
        int autoTextSize = AutoText.getSize(getListView());
        if (autoTextSize < 1) {
            ((PreferenceGroup) findPreference(PREDICTION_SETTINGS_KEY)).removePreference(mQuickFixes);
        }
        //updateSettingsKeySummary();
        updateLanguageKeySummary();
        updateKeyboardLayoutSummary();
        updateTextSizeSummary();
        updateShowExtendedRowSummary();
    }

	@Override
	public void onDestroy() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        (new BackupManager(getActivity())).dataChanged();
        
        if (key.equals(APPLICATRION_ICON_KEY)) {
        	boolean defaultAppIcon = getResources().getBoolean(R.bool.default_application_icon);
        	if (prefs.getBoolean(APPLICATRION_ICON_KEY, defaultAppIcon)) {
        		KeyboardApp.ShowApplicationIcon(getActivity().getApplicationContext());
        	} else {
        		KeyboardApp.HideApplicationIcon(getActivity().getApplicationContext());
        	}
        }
        
        updateLanguageKeySummary();
        updateKeyboardLayoutSummary();
        updateTextSizeSummary();
        updateShowExtendedRowSummary();
    }
    
    private void updateLanguageKeySummary() {
        mLanguageKeyPreference.setSummary(
                getResources().getStringArray(R.array.language_key_modes)
                [mLanguageKeyPreference.findIndexOfValue(mLanguageKeyPreference.getValue())]);
    }
    
    private void updateKeyboardLayoutSummary() {
    	mKeyboardLayoutPreference.setSummary(
                getResources().getStringArray(R.array.keyboard_layout_modes)
                [mKeyboardLayoutPreference.findIndexOfValue(mKeyboardLayoutPreference.getValue())]);
    }
    
    private void updateTextSizeSummary() {
    	mKeyboardTextSizePreference.setSummary(
	            getResources().getStringArray(R.array.keyboard_text_size_modes)
	            [mKeyboardTextSizePreference.findIndexOfValue(mKeyboardTextSizePreference.getValue())]);
	}
    
    private void updateShowExtendedRowSummary() {
    	mKeyboardExtendedRowPreference.setSummary(
	            getResources().getStringArray(R.array.enabled_extened_row_visibilities)
	            [mKeyboardExtendedRowPreference.findIndexOfValue(mKeyboardExtendedRowPreference.getValue())]);
	}

	// SMM {
	/* package */ static boolean AlertForInstallLocation(Context context, DialogInterface.OnDismissListener dismissListener) {
		String pkgPath = context.getPackageCodePath();
		if(pkgPath == null || pkgPath == "") {
			try {
				pkgPath = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0).sourceDir;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		if(pkgPath != null && !pkgPath.startsWith("/data/app/") && !pkgPath.startsWith("/system/app/")) {
			
			if(context instanceof Activity) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle(context.getText(R.string.alert_installocation_title));
				builder.setMessage(context.getText(R.string.alert_installocation_message));
				builder.setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				AlertDialog dialog = builder.create();
				if(dismissListener != null) dialog.setOnDismissListener(dismissListener);
				dialog.show();
				return false;
			} else {
				Toast.makeText(context, R.string.alert_installocation_message, Toast.LENGTH_LONG).show();
				return false;
			}
		}
		return true;
	}
	// } SMM
}
