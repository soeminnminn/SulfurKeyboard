package com.s16.android;

import com.android.inputmethod.skeyboard.R;

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
		
		final Button btnShowSettings = (Button)findViewById(R.id.btnShowSettings);
		btnShowSettings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(new Intent(android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS), 0);
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
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem paramMenuItem) {
	    switch (paramMenuItem.getItemId()) {
	      case 0:
	    	  startActivityForResult(new Intent(android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS), 0);
	        break;
	      default:
	        break;
	    }
	    return true;
	  }

}
