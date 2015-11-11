package com.s16.preference;

import com.s16.inputmethod.skeyboard.R;
import com.s16.picker.colorpicker.ColorPickerDialog;
import com.s16.picker.colorpicker.ColorPickerSwatch;

import android.preference.Preference;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ColorPickerPreference extends Preference {

	protected static final String TAG = ColorPickerPreference.class.getSimpleName();
	
	private int[] mColorChoices = {};
	private int mDefaultValue = 0;
    private int mValue = 0;
    private int mItemLayoutId = R.layout.calendar_grid_item_color;
    private int mNumColumns = 5;
    private CharSequence mNegativeButtonText;
    private CharSequence mPositiveButtonText;
    private DialogInterface.OnClickListener mPositiveButtonListener;
    private DialogInterface.OnClickListener mNegativeButtonListener;
    private int mMode = 1;
    
    private View mPreviewView;
    private boolean mIsTablet = false;
	
	public ColorPickerPreference(Context context) {
		super(context);
		initAttrs(context, null, 0);
	}
	
	public ColorPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs, 0);
    }

    public ColorPickerPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttrs(context, attrs, defStyle);
    }
	
    private void initAttrs(Context context, AttributeSet attrs, int defStyle) {
    	TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs, R.styleable.ColorPickerPreference, defStyle, defStyle);

    	boolean showDefault = false;
    	boolean showCancel = false;
        try {
            mItemLayoutId = a.getResourceId(R.styleable.ColorPickerPreference_cpp_itemLayout, mItemLayoutId);
            mNumColumns = a.getInteger(R.styleable.ColorPickerPreference_cpp_numColumns, mNumColumns);
            mMode = a.getInteger(R.styleable.ColorPickerPreference_cpp_mode, 1);
            showDefault = a.getBoolean(R.styleable.ColorPickerPreference_cpp_showDefault, false);
            showCancel = a.getBoolean(R.styleable.ColorPickerPreference_cpp_showCancel, false);
            int choicesResId = a.getResourceId(R.styleable.ColorPickerPreference_cpp_choices,
                    R.array.default_color_choice_values);
            if (choicesResId > 0) {
                String[] choices = a.getResources().getStringArray(choicesResId);
                mColorChoices = new int[choices.length];
                for (int i = 0; i < choices.length; i++) {
                    mColorChoices[i] = Color.parseColor(choices[i]);
                }
            }

        } finally {
            a.recycle();
        }
        a = context.obtainStyledAttributes(attrs, new int[] { android.R.attr.positiveButtonText, android.R.attr.negativeButtonText, android.R.attr.defaultValue });
        mPositiveButtonText = a.getString(0);
        mNegativeButtonText = a.getString(1);
        mDefaultValue = a.getInt(2, 0);
        a.recycle();
        
        if (showDefault) {
        	if (TextUtils.isEmpty(mPositiveButtonText)) {
        		mPositiveButtonText = context.getText(R.string.cpp_set_default);
        	}
        	mPositiveButtonListener = new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					setValue(mDefaultValue);
					dialog.dismiss();
				}
			};
        }
        
        if (showCancel) {
        	if (TextUtils.isEmpty(mNegativeButtonText)) {
        		mNegativeButtonText = context.getText(android.R.string.cancel);
        	}
        	mNegativeButtonListener = new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			};
        }
        
        setWidgetLayoutResource(mItemLayoutId);
        
        mIsTablet = (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
    
    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        mPreviewView = view.findViewById(R.id.calendar_color_view);
        setColorViewValue(mPreviewView, mValue);
    }

    public void setValue(int value) {
        if (callChangeListener(value)) {
            mValue = value;
            persistInt(value);
            notifyChanged();
        }
    }

    @Override
    protected void onClick() {
        super.onClick();

        final ColorPickerDialog colorPicker = (ColorPickerDialog) ColorPickerDialog.newInstance(R.string.color_picker_default_title,
				mColorChoices, getValue(), mNumColumns, mIsTablet ? ColorPickerDialog.SIZE_LARGE : ColorPickerDialog.SIZE_SMALL);
        
        FragmentActivity activity = (FragmentActivity) getContext();
        activity.getSupportFragmentManager().beginTransaction()
                .add(colorPicker, getFragmentTag())
                .commit();
        
        if (mMode == 2) {
        	colorPicker.showProgressBarView();
        } else {
        	colorPicker.showProgressBarView();
        }
        
        colorPicker.setPositiveButton(mPositiveButtonText, mPositiveButtonListener);
        colorPicker.setNegativeButton(mNegativeButtonText, mNegativeButtonListener);
        colorPicker.setOnColorSelectedListener(listener);
    }
    
    /**
     * Implement listener to get selected color value
     */
    ColorPickerSwatch.OnColorSelectedListener listener = new ColorPickerSwatch.OnColorSelectedListener() {
		
		@Override
		public void onColorSelected(int color) {
			setValue(color);
		}
	};
    
    @Override
    protected void onAttachedToActivity() {
        super.onAttachedToActivity();

        FragmentActivity activity = (FragmentActivity) getContext();
        ColorPickerDialog colorcalendar = (ColorPickerDialog) activity
                .getSupportFragmentManager().findFragmentByTag(getFragmentTag());
        if (colorcalendar != null) {
            // re-bind listener to fragment
            colorcalendar.setOnColorSelectedListener(listener);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 0);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
    	setValue(restoreValue ? getPersistedInt(0) : (Integer) defaultValue);
    }

    public String getFragmentTag() {
        return "color_" + getKey();
    }

    public int getValue() {
        return mValue;
    }
    
    private static void setColorViewValue(View view, int color) {
        if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;
            Resources res = imageView.getContext().getResources();

            Drawable currentDrawable = imageView.getDrawable();
            GradientDrawable colorChoiceDrawable;
            if (currentDrawable != null && currentDrawable instanceof GradientDrawable) {
                // Reuse drawable
                colorChoiceDrawable = (GradientDrawable) currentDrawable;
            } else {
                colorChoiceDrawable = new GradientDrawable();
                colorChoiceDrawable.setShape(GradientDrawable.OVAL);
            }

            // Set stroke to dark version of color
            int darkenedColor = Color.rgb(
                    Color.red(color) * 192 / 256,
                    Color.green(color) * 192 / 256,
                    Color.blue(color) * 192 / 256);

            colorChoiceDrawable.setColor(color);
            colorChoiceDrawable.setStroke((int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 1, res.getDisplayMetrics()), darkenedColor);
            imageView.setImageDrawable(colorChoiceDrawable);

        } else if (view instanceof TextView) {
            ((TextView) view).setTextColor(color);
        }
    }
}
