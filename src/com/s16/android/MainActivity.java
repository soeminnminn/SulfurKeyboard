package com.s16.android;

import com.s16.inputmethod.skeyboard.IMESettings;
import com.s16.inputmethod.skeyboard.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		String html = getString(R.string.main_body);
		try {
			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			html += "<p><i>" + getText(R.string.version_name) + ":" + pInfo.versionName + "</i></p>";
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
        
        Spanned content = Html.fromHtml(html);
        TextView description = (TextView) findViewById(R.id.txtHelp);
        description.setMovementMethod(LinkMovementMethod.getInstance());
        description.setText(content, BufferType.SPANNABLE);
        
        final Button btnEnableIme = (Button)findViewById(R.id.btnEnableKeyboard);
        btnEnableIme.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(new Intent(android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS), 0);
            }
        });
        
        final Button btnSetIme = (Button)findViewById(R.id.btnSetInputMethod);
        btnSetIme.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).showInputMethodPicker();
            }
        });
		
		final Button btnShowSettings = (Button)findViewById(R.id.btnShowSettings);
		btnShowSettings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	startActivity(new Intent(MainActivity.this.getBaseContext(), IMESettings.class));
            }
        });
		
		final Button btnShowInputTest = (Button)findViewById(R.id.btnShowInputTest);
		btnShowInputTest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this.getBaseContext(), InputTestActivity.class));
            }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	      case 0:
	    	  startActivityForResult(new Intent(android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS), 0);
	        break;
	      default:
	        break;
	    }
	    return super.onOptionsItemSelected(item);
	}

}
