package com.android.inputmethod.skeyboard;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;

public class KeyboardIconsSet {

	private static final String TAG = KeyboardIconsSet.class.getSimpleName();
	public static final int ICON_UNDEFINED = 0;

    // This should be aligned with Keyboard.keyIcon enum.
	public static final int ICON_123MIC_KEY = 1;
	public static final int ICON_MIC_KEY = 2;
	public static final int ICON_SETTINGS_KEY = 3;
	public static final int ICON_SHIFT_KEY = 4;
	public static final int ICON_SHIFTLOCKED_KEY = 5;
	public static final int ICON_SPACE_KEY = 6;
	public static final int ICON_TAB_KEY = 7;
	public static final int ICON_RETURN_KEY = 8;
	public static final int ICON_SEARCH_KEY = 9;
	public static final int ICON_DELETE_KEY = 10;
	public static final int ICON_DONE_KEY = 11;
    
	public static final int ICON_NUM0_KEY = 12;
	public static final int ICON_NUM1_KEY = 13;
	public static final int ICON_NUM2_KEY = 14;
	public static final int ICON_NUM3_KEY = 15;
	public static final int ICON_NUM4_KEY = 16;
	public static final int ICON_NUM5_KEY = 17;
	public static final int ICON_NUM6_KEY = 18;
	public static final int ICON_NUM7_KEY = 19;
	public static final int ICON_NUM8_KEY = 20;
	public static final int ICON_NUM9_KEY = 21;
	public static final int ICON_NUMALT_KEY = 22;
	public static final int ICON_NUMPOUND_KEY = 23;
	public static final int ICON_NUMSTAR_KEY = 24;
	
	public static final int ICON_HINT_POPUP = 25;

    private static final int ICON_LAST = 25;
    
    private static final int[] THEMES = new int [] {
    	R.style.KeyboardIcons, R.style.KeyboardIcons_black, R.style.KeyboardIcons_white, 
        R.style.KeyboardIcons_iPhone};

    private final Drawable mIcons[] = new Drawable[ICON_LAST + 1];
    
    public KeyboardIconsSet(Context context, int keyIconsSet) {
    	int resId = THEMES[keyIconsSet];
    	final TypedArray typedArray = context.obtainStyledAttributes(resId, R.styleable.KeyboardIcons);
    	loadIcons(typedArray);
    	typedArray.recycle();
    }

    public KeyboardIconsSet(Context context, AttributeSet attrs, int defStyle) {
    	final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.KeyboardIcons, defStyle, R.style.KeyboardIcons);
    	loadIcons(typedArray);
    	typedArray.recycle();
    }
    
    private final int getIconId(final int attrIndex) {
        switch (attrIndex) {
        case R.styleable.KeyboardIcons_icon123micKey:
            return ICON_123MIC_KEY;
        case R.styleable.KeyboardIcons_iconMicKey:
            return ICON_MIC_KEY;
        case R.styleable.KeyboardIcons_iconSettingsKey:
            return ICON_SETTINGS_KEY;
        case R.styleable.KeyboardIcons_iconShiftKey:
            return ICON_SHIFT_KEY;
        case R.styleable.KeyboardIcons_iconShiftLockedKey:
            return ICON_SHIFTLOCKED_KEY;
        case R.styleable.KeyboardIcons_iconSpaceKey:
            return ICON_SPACE_KEY;
        case R.styleable.KeyboardIcons_iconTabKey:
            return ICON_TAB_KEY;
        case R.styleable.KeyboardIcons_iconReturnKey:
            return ICON_RETURN_KEY;
        case R.styleable.KeyboardIcons_iconSearchKey:
            return ICON_SEARCH_KEY;
        case R.styleable.KeyboardIcons_iconDeleteKey:
            return ICON_DELETE_KEY;
        case R.styleable.KeyboardIcons_iconDoneKey:
            return ICON_DONE_KEY;
        case R.styleable.KeyboardIcons_iconNum0Key:
            return ICON_NUM0_KEY;
        case R.styleable.KeyboardIcons_iconNum1Key:
            return ICON_NUM1_KEY;
        case R.styleable.KeyboardIcons_iconNum2Key:
            return ICON_NUM2_KEY;
        case R.styleable.KeyboardIcons_iconNum3Key:
            return ICON_NUM3_KEY;
        case R.styleable.KeyboardIcons_iconNum4Key:
            return ICON_NUM4_KEY;
        case R.styleable.KeyboardIcons_iconNum5Key:
            return ICON_NUM5_KEY;
        case R.styleable.KeyboardIcons_iconNum6Key:
            return ICON_NUM6_KEY;
        case R.styleable.KeyboardIcons_iconNum7Key:
            return ICON_NUM7_KEY;
        case R.styleable.KeyboardIcons_iconNum8Key:
            return ICON_NUM8_KEY;
        case R.styleable.KeyboardIcons_iconNum9Key:
            return ICON_NUM9_KEY;
        case R.styleable.KeyboardIcons_iconNumAltKey:
            return ICON_NUMALT_KEY;
        case R.styleable.KeyboardIcons_iconNumPoundKey:
            return ICON_NUMPOUND_KEY;
        case R.styleable.KeyboardIcons_iconNumStarKey:
            return ICON_NUMSTAR_KEY;
        default:
            return ICON_UNDEFINED;
        }
    }

    private void loadIcons(final TypedArray keyboardAttrs) {
        final int count = keyboardAttrs.getIndexCount();
        //Log.i(TAG, "IndexCount = " + count);
        for (int i = 0; i < count; i++) {
            final int attrIndex = keyboardAttrs.getIndex(i);
            final int iconId = getIconId(attrIndex);
            if (iconId != ICON_UNDEFINED) {
                try {
                    mIcons[iconId] = setDefaultBounds(keyboardAttrs.getDrawable(attrIndex));
                } catch (Resources.NotFoundException e) {
                    Log.w(TAG, "Drawable resource for icon #" + iconId + " not found");
                }
            }
        }
    }

    public Drawable getIcon(final int iconId) {
        if (iconId == ICON_UNDEFINED)
            return null;
        if (iconId < 0 || iconId >= mIcons.length)
            throw new IllegalArgumentException("icon id is out of range: " + iconId);
        return mIcons[iconId];
    }
    
    public void setIcon(int iconId, final Drawable value) {
    	if (iconId < 0 || iconId >= mIcons.length)
            throw new IllegalArgumentException("icon id is out of range: " + iconId);
    	if(value == null)
    		throw new NullPointerException("icon is null");
    	
    	mIcons[iconId] = value;
    }

    private Drawable setDefaultBounds(final Drawable icon)  {
        if (icon != null) {
            icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
        }
        return icon;
    }
}
