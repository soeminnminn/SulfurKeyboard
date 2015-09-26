package com.s16.android;

import com.s16.app.ClipboardHelper;
import com.s16.drawing.HomeUpDrawable;
import com.s16.drawing.IconFontDrawable;
import com.s16.inputmethod.skeyboard.MMFontConvert;
import com.s16.inputmethod.skeyboard.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class ConverterActivity extends Activity {

	private final TextWatcher mTextUnicodeTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                int count) {
        	if (mEditTextConverterZawgyi != null) {
        		mEditTextConverterZawgyi.removeTextChangedListener(mTextZawgyiTextWatcher);
        		if (!TextUtils.isEmpty(s)) {
        			mEditTextConverterZawgyi.setText(MMFontConvert.uniToZawgyi(s));
        		} else {
        			mEditTextConverterZawgyi.setText("");
    			}
        		mEditTextConverterZawgyi.addTextChangedListener(mTextZawgyiTextWatcher);
        	}
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
        
    };
    
    private final TextWatcher mTextZawgyiTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                int count) {
        	if (mEditTextConverterUnicode != null){
        		mEditTextConverterUnicode.removeTextChangedListener(mTextUnicodeTextWatcher);
        		if (!TextUtils.isEmpty(s)) {
        			mEditTextConverterUnicode.setText(MMFontConvert.zawgyiToUni(s));
        		} else {
        			mEditTextConverterUnicode.setText("");
        		}
        		mEditTextConverterUnicode.addTextChangedListener(mTextUnicodeTextWatcher);
        	}
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
        
    };
    
    private View.OnLongClickListener mActionItemLongClick = new View.OnLongClickListener() {
		
		@Override
		public boolean onLongClick(View v) {
			if (!v.isEnabled()) return false;
			final CharSequence description = v.getContentDescription();
			if (TextUtils.isEmpty(description)) return false;
			
			final int[] screenPos = new int[2];
	        final Rect displayFrame = new Rect();
	        v.getLocationOnScreen(screenPos);
	        v.getWindowVisibleDisplayFrame(displayFrame);

			final Context context = getContext();
	        final int width = v.getWidth();
	        final int height = v.getHeight();
	        final int midy = screenPos[1] + height / 2;
	        final int screenWidth = context.getResources().getDisplayMetrics().widthPixels;

	        Toast cheatSheet = Toast.makeText(context, description, Toast.LENGTH_SHORT);
	        if (midy < displayFrame.height()) {
	            // Show along the top; follow action buttons
	            cheatSheet.setGravity(Gravity.TOP | Gravity.RIGHT, screenWidth - screenPos[0] - width / 2, height);
	        } else {
	            // Show along the bottom center
	            cheatSheet.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, height);
	        }
	        cheatSheet.show();
			return true;
		}
	};
    
    private EditText mEditTextConverterUnicode;
    private EditText mEditTextConverterZawgyi;
    
    protected Context getContext() {
    	return this;
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_converter);
		
		final Typeface typefaceZawgyi = Typeface.createFromAsset(getAssets(), "fonts/zawgyi.ttf");
		final Typeface typefaceNotoSans = Typeface.createFromAsset(getAssets(), "fonts/notosans.ttf");
		
		mEditTextConverterUnicode = (EditText)findViewById(R.id.editTextConverterUnicode);
		mEditTextConverterUnicode.setTypeface(typefaceNotoSans);
		mEditTextConverterUnicode.addTextChangedListener(mTextUnicodeTextWatcher);
		
		mEditTextConverterZawgyi = (EditText)findViewById(R.id.editTextConverterZawgyi);
		mEditTextConverterZawgyi.setTypeface(typefaceZawgyi);
		mEditTextConverterZawgyi.addTextChangedListener(mTextZawgyiTextWatcher);
		
		final ImageButton imageTitle = (ImageButton)findViewById(R.id.imageButtonTitleBack);
		imageTitle.setImageDrawable(new HomeUpDrawable(getContext(), HomeUpDrawable.MODE_HOME_UP));
		imageTitle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	ConverterActivity.this.finish();
            }
        });
		
		int iconColor = getResources().getColor(R.color.dim_foreground_holo_dark);
		//int intrinsicSize = IconFontDrawable.getDefaultIntrinsicSize(getContext());
		final ImageButton buttonCopy = (ImageButton)findViewById(R.id.imageButtonTitleCopy);
		buttonCopy.setImageDrawable(new IconFontDrawable(typefaceZawgyi, '\uE042', iconColor, 48));
		buttonCopy.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				performCopy();
			}
		});
		buttonCopy.setLongClickable(true);
		buttonCopy.setOnLongClickListener(mActionItemLongClick);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}
	
	protected void performCopy() {
		final CharSequence uniText = mEditTextConverterUnicode.getText();
		final CharSequence zawGyiText = mEditTextConverterZawgyi.getText();
		if (TextUtils.isEmpty(uniText) || TextUtils.isEmpty(zawGyiText)) return;
		
		String[] chooseItemArray = getResources().getStringArray(R.array.choose_action_copy);
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
		dialogBuilder.setSingleChoiceItems(chooseItemArray, 0, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				CharSequence text = null;
				if (which == 1) {
					text = uniText;
					
				} else if (which == 2) {
					text = zawGyiText;
					
				} else {
					StringBuilder textBuilder = new StringBuilder();
					textBuilder.append("[Zawgyi]\n");
					textBuilder.append(zawGyiText);
					textBuilder.append("\n\n");
					
					textBuilder.append("[Unicode]\n");
					textBuilder.append(uniText);
					textBuilder.append("\n");
					
					text = textBuilder.toString();
				}
				
				ClipboardHelper.copyToClipboard(getContext(), "copied from converter", text);
				dialog.dismiss();
				Toast.makeText(getContext(), R.string.message_copy_success, Toast.LENGTH_LONG).show();
			}
		});
		dialogBuilder.show();
	}
}
