package com.s16.android;

import com.android.inputmethod.skeyboard.R;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;

public class InputTestActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_input_test);
		
		final Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/zawgyi.ttf");
		final EditText editTextNormal = (EditText)findViewById(R.id.editTextNormal);
		editTextNormal.setTypeface(typeface);
		
		final EditText editTextPersonName = (EditText)findViewById(R.id.editTextPersonName);
		editTextPersonName.setTypeface(typeface);
		
		final EditText editTextFilter = (EditText)findViewById(R.id.editTextFilter);
		editTextFilter.setTypeface(typeface);
		
		final EditText editTextEmail = (EditText)findViewById(R.id.editTextEmail);
		editTextEmail.setTypeface(typeface);
		
		final EditText editTextUri = (EditText)findViewById(R.id.editTextUri);
		editTextUri.setTypeface(typeface);
		
		final EditText editTextWebEditText = (EditText)findViewById(R.id.editTextWebEditText);
		editTextWebEditText.setTypeface(typeface);
		
		final EditText editTextMessage = (EditText)findViewById(R.id.editTextMessage);
		editTextMessage.setTypeface(typeface);
		
		final EditText editTextPhone = (EditText)findViewById(R.id.editTextPhone);
		editTextPhone.setTypeface(typeface);
		
		final EditText editTextNumber = (EditText)findViewById(R.id.editTextNumber);
		editTextNumber.setTypeface(typeface);
		
		final EditText editTextDateTime = (EditText)findViewById(R.id.editTextDateTime);
		editTextDateTime.setTypeface(typeface);
		
		final EditText editTextMultiLine = (EditText)findViewById(R.id.editTextMultiLine);
		editTextMultiLine.setTypeface(typeface);
		
		final ImageButton imageTitle = (ImageButton)findViewById(R.id.imageButtonTitleBack);
		imageTitle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	InputTestActivity.this.finish();
            	//this.dispatchKeyEvent(new Keyevent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
            }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.input_test, menu);
		return true;
	}

}
