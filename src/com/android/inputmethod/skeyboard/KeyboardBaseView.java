/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.inputmethod.skeyboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.inputmethod.skeyboard.Keyboard;
import com.android.inputmethod.skeyboard.Keyboard.Key;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.WeakHashMap;

import com.android.inputmethod.skeyboard.R;

/**
 * A view that renders a virtual {@link LatinKeyboard}. It handles rendering of keys and
 * detecting key presses and touch movements.
 *
 * TODO: References to LatinKeyboard in this class should be replaced with ones to its base class.
 *
 * @attr ref R.styleable#KeyboardBaseView_keyBackground
 * @attr ref R.styleable#KeyboardBaseView_keyPreviewLayout
 * @attr ref R.styleable#KeyboardBaseView_keyPreviewOffset
 * @attr ref R.styleable#KeyboardBaseView_labelTextSize
 * @attr ref R.styleable#KeyboardBaseView_keyTextSize
 * @attr ref R.styleable#KeyboardBaseView_keyTextColor
 * @attr ref R.styleable#KeyboardBaseView_verticalCorrection
 * @attr ref R.styleable#KeyboardBaseView_popupLayout
 */
public class KeyboardBaseView extends View implements PointerTracker.UIProxy {
    private static final String TAG = "KeyboardBaseView";
    private static final boolean DEBUG = false;

    public static final int NOT_A_TOUCH_COORDINATE = -1;
    private static final float UNDEFINED_RATIO = -1.0f;

    public interface OnKeyboardActionListener {

        /**
         * Called when the user presses a key. This is sent before the
         * {@link #onKey} is called. For keys that repeat, this is only
         * called once.
         *
         * @param primaryCode
         *            the unicode of the key being pressed. If the touch is
         *            not on a valid key, the value will be zero.
         */
        void onPress(int primaryCode);

        /**
         * Called when the user releases a key. This is sent after the
         * {@link #onKey} is called. For keys that repeat, this is only
         * called once.
         *
         * @param primaryCode
         *            the code of the key that was released
         */
        void onRelease(int primaryCode);

        /**
         * Send a key press to the listener.
         *
         * @param primaryCode
         *            this is the key that was pressed
         * @param keyCodes
         *            the codes for all the possible alternative keys with
         *            the primary code being the first. If the primary key
         *            code is a single character such as an alphabet or
         *            number or symbol, the alternatives will include other
         *            characters that may be on the same key or adjacent
         *            keys. These codes are useful to correct for
         *            accidental presses of a key adjacent to the intended
         *            key.
         * @param x
         *            x-coordinate pixel of touched event. If onKey is not called by onTouchEvent,
         *            the value should be NOT_A_TOUCH_COORDINATE.
         * @param y
         *            y-coordinate pixel of touched event. If onKey is not called by onTouchEvent,
         *            the value should be NOT_A_TOUCH_COORDINATE.
         */
        void onKey(int primaryCode, int[] keyCodes, int x, int y);

        /**
         * Sends a sequence of characters to the listener.
         *
         * @param text
         *            the sequence of characters to be displayed.
         */
        void onText(CharSequence text);

        /**
         * Called when user released a finger outside any key.
         */
        void onCancel();

        /**
         * Called when the user quickly moves the finger from right to
         * left.
         */
        void swipeLeft();

        /**
         * Called when the user quickly moves the finger from left to
         * right.
         */
        void swipeRight();

        /**
         * Called when the user quickly moves the finger from up to down.
         */
        void swipeDown();

        /**
         * Called when the user quickly moves the finger from down to up.
         */
        void swipeUp();
    }

    // Timing constants
    private final int mKeyRepeatInterval;

    // HORIZONTAL ELLIPSIS "...", character for popup hint.
    //private static final String POPUP_HINT_CHAR = "\u2026";
    
    // Miscellaneous constants
    public static final int NOT_A_KEY = -1;
    private static final int[] LONG_PRESSABLE_STATE_SET = { android.R.attr.state_long_pressable };
    private static final int NUMBER_HINT_VERTICAL_ADJUSTMENT_PIXEL = -1;

    // XML attribute
    protected float mKeyLabelTextRatio;
    protected int mKeyLabelTextSize;
    protected float mKeyTextRatio;
    protected int mKeyTextSize;
    protected int mKeyTextColor;
    protected int mKeyModifierColor;
    protected float mKeyHintTextRatio;
    protected int mKeyHintTextSize;
    protected int mKeyHintColor;
    protected float mTrailTextRatio;
    protected int mTrailTextSize;
    protected int mTrailTextColor;
    protected int mLanguagebarTextColor;
    protected int mLanguagebarShadowColor;
    protected int mShadowColor;
    protected float mShadowRadius;
    protected int mKeyTextStyle = 0;
    protected Drawable mKeyBackground;
    protected float mBackgroundDimAmount;
    protected float mKeyHysteresisDistance;
    protected float mVerticalCorrection;
    protected int mPreviewOffset;
    protected int mPreviewHeight;
    protected Drawable mPreviewBackground;
    protected Drawable mPreviewSlideBackground;
    protected int mPopupLayout;
    
    protected int mNumHintTopPadding;
    protected int mNumHintRightPadding;
    protected int mHintPopupRightPadding;
    protected int mHintPopupBottomPadding;

    // Main keyboard
    protected Keyboard mKeyboard;
    protected Key[] mKeys;
    // TODO this attribute should be gotten from Keyboard.
    protected int mKeyboardVerticalGap;
    private float mKeyTextSizeScale = 1.0f;
    // SMM {
    private int mKeyHorizontalMargin;
    private int mKeyVerticalMargin;
    private boolean mAutoHideMiniKeyboard;
    // } SMM

    // Key preview popup
    private TextView mPreviewText;
    private PopupWindow mPreviewPopup;
    private int mPreviewTextSizeLarge;
    private int[] mOffsetInWindow;
    private int mOldPreviewKeyIndex = NOT_A_KEY;
    private boolean mShowPreview = true;
    private boolean mShowTouchPoints = true;
    private int mPopupPreviewOffsetX;
    private int mPopupPreviewOffsetY;
    private int mWindowY;
    private int mPopupPreviewDisplayedY;
    private final int mDelayBeforePreview;
    private final int mDelayBeforeSpacePreview; // SMM
    private final int mDelayAfterPreview;

    // Popup mini keyboard
    private PopupWindow mMiniKeyboardPopup;
    private KeyboardBaseView mMiniKeyboard;
    private View mMiniKeyboardParent;
    private final WeakHashMap<Key, View> mMiniKeyboardCache = new WeakHashMap<Key, View>();
    private int mMiniKeyboardOriginX;
    private int mMiniKeyboardOriginY;
    private long mMiniKeyboardPopupTime;
    private int[] mWindowOffset;
    private final float mMiniKeyboardSlideAllowance;
    private int mMiniKeyboardTrackerId;

    /** Listener for {@link OnKeyboardActionListener}. */
    private OnKeyboardActionListener mKeyboardActionListener;

    private final ArrayList<PointerTracker> mPointerTrackers = new ArrayList<PointerTracker>();
    private boolean mIgnoreMove = false; // SMM

    // TODO: Let the PointerTracker class manage this pointer queue
    private final PointerQueue mPointerQueue = new PointerQueue();

    private final boolean mHasDistinctMultitouch;
    private int mOldPointerCount = 1;

    protected KeyDetector mKeyDetector = new ProximityKeyDetector();

    // Swipe gesture detector
    private final GestureDetector mGestureDetector;
    private final SwipeTracker mSwipeTracker = new SwipeTracker();
    private final int mSwipeThreshold;
    private final boolean mDisambiguateSwipe;

    // Drawing
    /** Whether the keyboard bitmap needs to be redrawn before it's blitted. **/
    private boolean mDrawPending;
    /** The dirty region in the keyboard bitmap */
    private final Rect mDirtyRect = new Rect();
    /** The keyboard bitmap for faster updates */
    private Bitmap mBuffer;
    /** Notes if the keyboard just changed, so that we could possibly reallocate the mBuffer. */
    private boolean mKeyboardChanged;
    private Key mInvalidatedKey;
    /** The canvas for the above mutable keyboard bitmap */
    private Canvas mCanvas;
    private final Paint mPaint;
    private final Rect mPadding;
    private final Rect mClipRegion = new Rect(0, 0, 0, 0);
    // This map caches key label text height in pixel as value and key label text size as map key.
    @SuppressLint("UseSparseArrays")
	private final HashMap<Integer, Float> mTextHeightCache = new HashMap<Integer, Float>();
    // Distance from horizontal center of the key, proportional to key label text height.
    private final float KEY_LABEL_VERTICAL_ADJUSTMENT_FACTOR = 0.55f;
    private final String KEY_LABEL_HEIGHT_REFERENCE_CHAR = "H";

    private final UIHandler mHandler = new UIHandler();

    class UIHandler extends Handler {
        private static final int MSG_POPUP_PREVIEW = 1;
        private static final int MSG_DISMISS_PREVIEW = 2;
        private static final int MSG_REPEAT_KEY = 3;
        private static final int MSG_LONGPRESS_KEY = 4;

        private boolean mInKeyRepeat;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_POPUP_PREVIEW:
                    showKey(msg.arg1, (PointerTracker)msg.obj);
                    break;
                case MSG_DISMISS_PREVIEW:
                    mPreviewPopup.dismiss();
                    break;
                case MSG_REPEAT_KEY: {
                    final PointerTracker tracker = (PointerTracker)msg.obj;
                    tracker.repeatKey(msg.arg1);
                    startKeyRepeatTimer(mKeyRepeatInterval, msg.arg1, tracker);
                    break;
                }
                case MSG_LONGPRESS_KEY: {
                    final PointerTracker tracker = (PointerTracker)msg.obj;
                    openPopupIfRequired(msg.arg1, tracker);
                    break;
                }
            }
        }

        public void popupPreview(long delay, int keyIndex, PointerTracker tracker) {
            removeMessages(MSG_POPUP_PREVIEW);
            if (mPreviewPopup.isShowing() && mPreviewText.getVisibility() == VISIBLE) {
                // Show right away, if it's already visible and finger is moving around
                showKey(keyIndex, tracker);
            } else {
                sendMessageDelayed(obtainMessage(MSG_POPUP_PREVIEW, keyIndex, 0, tracker),delay);
            }
        }

        public void cancelPopupPreview() {
            removeMessages(MSG_POPUP_PREVIEW);
        }

        public void dismissPreview(long delay) {
            if (mPreviewPopup.isShowing()) {
                sendMessageDelayed(obtainMessage(MSG_DISMISS_PREVIEW), delay);
            }
        }

        public void cancelDismissPreview() {
            removeMessages(MSG_DISMISS_PREVIEW);
        }

        public void startKeyRepeatTimer(long delay, int keyIndex, PointerTracker tracker) {
            mInKeyRepeat = true;
            sendMessageDelayed(obtainMessage(MSG_REPEAT_KEY, keyIndex, 0, tracker), delay);
        }

        public void cancelKeyRepeatTimer() {
            mInKeyRepeat = false;
            removeMessages(MSG_REPEAT_KEY);
        }

        public boolean isInKeyRepeat() {
            return mInKeyRepeat;
        }

        public void startLongPressTimer(long delay, int keyIndex, PointerTracker tracker) {
            removeMessages(MSG_LONGPRESS_KEY);
            sendMessageDelayed(obtainMessage(MSG_LONGPRESS_KEY, keyIndex, 0, tracker), delay);
        }

        public void cancelLongPressTimer() {
            removeMessages(MSG_LONGPRESS_KEY);
        }

        public void cancelKeyTimers() {
            cancelKeyRepeatTimer();
            cancelLongPressTimer();
        }

        public void cancelAllMessages() {
            cancelKeyTimers();
            cancelPopupPreview();
            cancelDismissPreview();
        }
    };

    static class PointerQueue {
        private LinkedList<PointerTracker> mQueue = new LinkedList<PointerTracker>();

        public void add(PointerTracker tracker) {
            mQueue.add(tracker);
        }

        public int lastIndexOf(PointerTracker tracker) {
            LinkedList<PointerTracker> queue = mQueue;
            for (int index = queue.size() - 1; index >= 0; index--) {
                PointerTracker t = queue.get(index);
                if (t == tracker)
                    return index;
            }
            return -1;
        }

        public void releaseAllPointersOlderThan(PointerTracker tracker, long eventTime) {
            LinkedList<PointerTracker> queue = mQueue;
            int oldestPos = 0;
            for (PointerTracker t = queue.get(oldestPos); t != tracker; t = queue.get(oldestPos)) {
                if (t.isModifier()) {
                    oldestPos++;
                } else {
                    t.onUpEvent(t.getLastX(), t.getLastY(), eventTime);
                    t.setAlreadyProcessed();
                    queue.remove(oldestPos);
                }
            }
        }

        public void releaseAllPointersExcept(PointerTracker tracker, long eventTime) {
            for (PointerTracker t : mQueue) {
                if (t == tracker)
                    continue;
                t.onUpEvent(t.getLastX(), t.getLastY(), eventTime);
                t.setAlreadyProcessed();
            }
            mQueue.clear();
            if (tracker != null)
                mQueue.add(tracker);
        }

        public void remove(PointerTracker tracker) {
            mQueue.remove(tracker);
        }
    }

    public KeyboardBaseView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.keyboardViewStyle);
    }

    @SuppressWarnings("deprecation")
	public KeyboardBaseView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        final Resources res = getResources();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.KeyboardBaseView, defStyle, R.style.KeyboardBaseView);
        LayoutInflater inflate = LayoutInflater.from(context);
        
        int previewLayout = 0;
        //int keyHintPopup = 0;
        
        mNumHintTopPadding = res.getDimensionPixelSize(R.dimen.num_popup_icon_top_padding);
        mNumHintRightPadding = res.getDimensionPixelSize(R.dimen.num_popup_icon_right_padding);
        mHintPopupRightPadding = res.getDimensionPixelSize(R.dimen.hint_popup_icon_right_padding);
        mHintPopupBottomPadding = res.getDimensionPixelSize(R.dimen.hint_popup_icon_bottom_padding);
        
        final int defKeyTextColor = res.getColor(R.color.key_text_color_dark);
        final int defKeyModifierColor = res.getColor(R.color.key_modifier_color_dark);
        final int defKeyHintColor = res.getColor(R.color.key_hint_color_dark);
        final int defTrailTextColor = res.getColor(R.color.key_trail_color_dark);
        final int defLanguagebarTextColor = res.getColor(R.color.languagebar_text_dark);
        final int defLanguagebarShadowColor = getResources().getColor(R.color.languagebar_shadow_dark);
        
        final int defLabelTextSize = res.getDimensionPixelSize(R.dimen.key_label_text_size);
        final int defKeyTextSize = res.getDimensionPixelSize(R.dimen.key_text_size);
        final int defKeyHintTextSize = res.getDimensionPixelSize(R.dimen.key_hint_text_size);
        final int defTrailTextSize = res.getDimensionPixelSize(R.dimen.key_trail_text_size);
        
        mKeyTextColor = defKeyTextColor;
        mKeyModifierColor = defKeyModifierColor;
        mKeyHintColor = defKeyHintColor;
        mTrailTextColor = defTrailTextColor;

		mKeyBackground = a.getDrawable(R.styleable.KeyboardBaseView_keyBackground);
		mKeyHysteresisDistance = a.getDimensionPixelOffset(R.styleable.KeyboardBaseView_keyHysteresisDistance, 0);
		mVerticalCorrection = a.getDimensionPixelOffset(R.styleable.KeyboardBaseView_verticalCorrection, 0);
		previewLayout = a.getResourceId(R.styleable.KeyboardBaseView_keyPreviewLayout, 0);
		mPreviewOffset = a.getDimensionPixelOffset(R.styleable.KeyboardBaseView_keyPreviewOffset, 0);
		mPreviewHeight = a.getDimensionPixelSize(R.styleable.KeyboardBaseView_keyPreviewHeight, 80);
		mPreviewBackground = a.getDrawable(R.styleable.KeyboardBaseView_keyPreviewBackground);
		mPreviewSlideBackground = a.getDrawable(R.styleable.KeyboardBaseView_keyPreviewSlideBackground);
		if (a.hasValue(R.styleable.KeyboardBaseView_keyLabelTextSize)) {
			mKeyLabelTextRatio = UNDEFINED_RATIO;
			mKeyLabelTextSize = a.getDimensionPixelSize(R.styleable.KeyboardBaseView_keyLabelTextSize, defLabelTextSize);
		} else {
			mKeyLabelTextRatio = getRatio(a, R.styleable.KeyboardBaseView_keyLabelTextRatio);
		}
		if (a.hasValue(R.styleable.KeyboardBaseView_keyTextSize)) {
			mKeyTextRatio = UNDEFINED_RATIO;
			mKeyTextSize = a.getDimensionPixelSize(R.styleable.KeyboardBaseView_keyTextSize, defKeyTextSize);	
		} else {
			mKeyTextRatio = getRatio(a, R.styleable.KeyboardBaseView_keyTextRatio);
		}
		if (a.hasValue(R.styleable.KeyboardBaseView_keyHintTextSize)) {
			mKeyHintTextRatio = UNDEFINED_RATIO;
			mKeyHintTextSize = a.getDimensionPixelSize(R.styleable.KeyboardBaseView_keyHintTextSize, defKeyHintTextSize);	
		} else {
			mKeyHintTextRatio = getRatio(a, R.styleable.KeyboardBaseView_keyHintTextRatio);
		}
		if (a.hasValue(R.styleable.KeyboardBaseView_keyTrailTextSize)) {
			mTrailTextRatio = UNDEFINED_RATIO;
			mTrailTextSize = a.getDimensionPixelSize(R.styleable.KeyboardBaseView_keyTrailTextSize, defTrailTextSize);	
		} else {
			mTrailTextRatio = getRatio(a, R.styleable.KeyboardBaseView_keyTrailTextRatio);
		}
		mKeyTextColor = a.getColor(R.styleable.KeyboardBaseView_keyTextColor, defKeyTextColor);
		mKeyModifierColor = a.getColor(R.styleable.KeyboardBaseView_keyModifierColor, defKeyModifierColor);
		mKeyHintColor = a.getColor(R.styleable.KeyboardBaseView_keyHintColor, defKeyHintColor);
		mTrailTextColor = a.getColor(R.styleable.KeyboardBaseView_keyTrailColor, defTrailTextColor);
		mLanguagebarTextColor = a.getColor(R.styleable.KeyboardBaseView_languagebarTextColor, defLanguagebarTextColor);
		mLanguagebarShadowColor = a.getColor(R.styleable.KeyboardBaseView_languagebarShadowColor, defLanguagebarShadowColor);
		mPopupLayout = a.getResourceId(R.styleable.KeyboardBaseView_popupLayout, 0);
		mShadowColor = a.getColor(R.styleable.KeyboardBaseView_shadowColor, 0);
		mShadowRadius = a.getFloat(R.styleable.KeyboardBaseView_shadowRadius, 0f);
		// TODO: Use Theme (android.R.styleable.Theme_backgroundDimAmount)
		mBackgroundDimAmount = a.getFloat(R.styleable.KeyboardBaseView_backgroundDimAmount, 0.5f);
		mKeyTextStyle = a.getInt(R.styleable.KeyboardBaseView_keyTextStyle, 0);
		// keyHintPopup = a.getResourceId(R.styleable.KeyboardBaseView_keyHintPopup, R.drawable.hint_popup);


        if(mPreviewBackground != null) {
        	mPreviewBackground.setBounds(0, 0, mPreviewBackground.getIntrinsicWidth(), mPreviewBackground.getIntrinsicHeight());
        }
        
        if(mPreviewSlideBackground != null) {
        	mPreviewSlideBackground.setBounds(0, 0, mPreviewSlideBackground.getIntrinsicWidth(), mPreviewSlideBackground.getIntrinsicHeight());
        }

        mPreviewPopup = new PopupWindow(context);
        if (previewLayout != 0) {
            mPreviewText = (TextView) inflate.inflate(previewLayout, null);
            mPreviewText.setBackgroundDrawable(mPreviewBackground);
            mPreviewTextSizeLarge = (int) res.getDimension(R.dimen.key_preview_text_size_large);
            mPreviewPopup.setContentView(mPreviewText);
            mPreviewPopup.setBackgroundDrawable(null);
        } else {
            mShowPreview = false;
        }
        mPreviewPopup.setTouchable(false);
        mPreviewPopup.setAnimationStyle(R.style.KeyPreviewAnimation);
        
        mPadding = new Rect(0, 0, 0, 0);
        mKeyBackground.getPadding(mPadding);

        mDelayBeforePreview = res.getInteger(R.integer.config_delay_before_preview);
        mDelayBeforeSpacePreview = res.getInteger(R.integer.config_delay_before_space_preview); // SMM
        mDelayAfterPreview = res.getInteger(R.integer.config_delay_after_preview);

        mMiniKeyboardParent = this;
        mMiniKeyboardPopup = new PopupWindow(context);
        mMiniKeyboardPopup.setBackgroundDrawable(null);
        mMiniKeyboardPopup.setAnimationStyle(R.style.MiniKeyboardAnimation);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextAlign(Align.CENTER);
        mPaint.setAlpha(255);

        mSwipeThreshold = (int) (500 * res.getDisplayMetrics().density);
        // TODO: Refer frameworks/base/core/res/res/values/config.xml
        mDisambiguateSwipe = res.getBoolean(R.bool.config_swipeDisambiguation);
        mMiniKeyboardSlideAllowance = res.getDimension(R.dimen.mini_keyboard_slide_allowance);

        GestureDetector.SimpleOnGestureListener listener =
                new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent me1, MotionEvent me2, float velocityX,
                    float velocityY) {
                final float absX = Math.abs(velocityX);
                final float absY = Math.abs(velocityY);
                float deltaX = me2.getX() - me1.getX();
                float deltaY = me2.getY() - me1.getY();
                // Calculate swipe distance threshold based on screen width & height, taking the smaller distance.
                int travelX = getWidth() / 3; // Half the keyboard width
                int travelY = getHeight() / 3; // Half the keyboard height
                int travelMin = Math.min(travelX, travelY);
                mSwipeTracker.computeCurrentVelocity(1000);
                final float endingVelocityX = mSwipeTracker.getXVelocity();
                final float endingVelocityY = mSwipeTracker.getYVelocity();
                if (velocityX > mSwipeThreshold && absY < absX && deltaX > travelMin) {
                    if (mDisambiguateSwipe && endingVelocityX >= velocityX / 4) {
                    	swipeRight();
                    	return true;
                    }
                } else if (velocityX < -mSwipeThreshold && absY < absX && deltaX < -travelMin) {
                    if (mDisambiguateSwipe && endingVelocityX <= velocityX / 4) {
                    	swipeLeft();
                    	return true;
                    }
                } else if (velocityY < -mSwipeThreshold && absX < absY && deltaY < -travelMin) {
                    if (mDisambiguateSwipe && endingVelocityY <= velocityY / 4) {
                    	swipeUp();
                    	return true;
                    }
                } else if (velocityY > mSwipeThreshold && absX < absY / 2 && deltaY > travelMin) {
                    if (mDisambiguateSwipe && endingVelocityY >= velocityY / 4) {
                    	swipeDown();
                    	return true;
                    }
                }
                return false;
            }
        };

        final boolean ignoreMultitouch = true;
        mGestureDetector = new GestureDetector(getContext(), listener, null, ignoreMultitouch);
        mGestureDetector.setIsLongpressEnabled(false);

        mHasDistinctMultitouch = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH_DISTINCT);
        mKeyRepeatInterval = res.getInteger(R.integer.config_key_repeat_interval);
    }
    
    public void setOnKeyboardActionListener(OnKeyboardActionListener listener) {
        mKeyboardActionListener = listener;
        for (PointerTracker tracker : mPointerTrackers) {
            tracker.setOnKeyboardActionListener(listener);
        }
    }

    /**
     * Returns the {@link OnKeyboardActionListener} object.
     * @return the listener attached to this keyboard
     */
    protected OnKeyboardActionListener getOnKeyboardActionListener() {
        return mKeyboardActionListener;
    }
    
    // SMM {
    protected static float getRatio(TypedArray a, int index) {
        return a.getFraction(index, 1000, 1000, 1) / 1000.0f;
    }

    @SuppressWarnings("deprecation")
	public void setStyle(Context context, int styleResId) {
    	final Resources res = context.getResources();
    	
    	if (styleResId == 0) {
    		styleResId = R.style.KeyboardBaseView;
    	}
    	final TypedArray a = context.obtainStyledAttributes(styleResId, R.styleable.KeyboardBaseView);
    	
    	final int defNumHintTopPadding = res.getDimensionPixelSize(R.dimen.num_popup_icon_top_padding);
		final int defNumHintRightPadding = res.getDimensionPixelSize(R.dimen.num_popup_icon_right_padding);
		final int defHintPopupBottomPadding = res.getDimensionPixelSize(R.dimen.hint_popup_icon_bottom_padding);
		final int defHintPopupRightPadding = res.getDimensionPixelSize(R.dimen.hint_popup_icon_right_padding);
		
		final int defKeyTextColor = res.getColor(R.color.key_text_color_dark);
        final int defKeyModifierColor = res.getColor(R.color.key_modifier_color_dark);
        final int defKeyHintColor = res.getColor(R.color.key_hint_color_dark);
        final int defTrailTextColor = res.getColor(R.color.key_trail_color_dark);
        final int defLanguagebarTextColor = res.getColor(R.color.languagebar_text_dark);
        final int defLanguagebarShadowColor = res.getColor(R.color.languagebar_shadow_dark);
        
        final int defLabelTextSize = res.getDimensionPixelSize(R.dimen.key_label_text_size);
        final int defKeyTextSize = res.getDimensionPixelSize(R.dimen.key_text_size);
        final int defKeyHintTextSize = res.getDimensionPixelSize(R.dimen.key_hint_text_size);
        final int defTrailTextSize = res.getDimensionPixelSize(R.dimen.key_trail_text_size);
        
        Drawable background = null;
        int paddingBottom = 0;
        int keyPreviewLayout = 0;
        
    	background = a.getDrawable(R.styleable.KeyboardBaseView_keyboardBackground);
    	mKeyBackground = a.getDrawable(R.styleable.KeyboardBaseView_keyBackground);
    	paddingBottom = a.getDimensionPixelOffset(R.styleable.KeyboardBaseView_keyboardPaddingBottom, 0);
    	mKeyHorizontalMargin = a.getDimensionPixelOffset(R.styleable.KeyboardBaseView_keyHorizontalMargin, 0);
    	mKeyVerticalMargin = a.getDimensionPixelOffset(R.styleable.KeyboardBaseView_keyVerticalMargin, 0);
    	mKeyTextStyle = a.getInt(R.styleable.KeyboardBaseView_keyTextStyle, 0);
    	mKeyHysteresisDistance = a.getDimensionPixelOffset(R.styleable.KeyboardBaseView_keyHysteresisDistance, 0);
    	mVerticalCorrection = a.getDimensionPixelOffset(R.styleable.KeyboardBaseView_verticalCorrection, 0);
    	keyPreviewLayout = a.getResourceId(R.styleable.KeyboardBaseView_keyPreviewLayout, 0);
    	mPreviewOffset = a.getDimensionPixelOffset(R.styleable.KeyboardBaseView_keyPreviewOffset, 0);
    	mPreviewHeight = a.getDimensionPixelSize(R.styleable.KeyboardBaseView_keyPreviewHeight, 80);
    	mPreviewBackground = a.getDrawable(R.styleable.KeyboardBaseView_keyPreviewBackground);
    	mPreviewSlideBackground = a.getDrawable(R.styleable.KeyboardBaseView_keyPreviewSlideBackground);
    	if (a.hasValue(R.styleable.KeyboardBaseView_keyLabelTextSize)) {
			mKeyLabelTextRatio = UNDEFINED_RATIO;
			mKeyLabelTextSize = a.getDimensionPixelSize(R.styleable.KeyboardBaseView_keyLabelTextSize, defLabelTextSize);
		} else {
			mKeyLabelTextRatio = getRatio(a, R.styleable.KeyboardBaseView_keyLabelTextRatio);
		}
		if (a.hasValue(R.styleable.KeyboardBaseView_keyTextSize)) {
			mKeyTextRatio = UNDEFINED_RATIO;
			mKeyTextSize = a.getDimensionPixelSize(R.styleable.KeyboardBaseView_keyTextSize, defKeyTextSize);	
		} else {
			mKeyTextRatio = getRatio(a, R.styleable.KeyboardBaseView_keyTextRatio);
		}
		if (a.hasValue(R.styleable.KeyboardBaseView_keyHintTextSize)) {
			mKeyHintTextRatio = UNDEFINED_RATIO;
			mKeyHintTextSize = a.getDimensionPixelSize(R.styleable.KeyboardBaseView_keyHintTextSize, defKeyHintTextSize);	
		} else {
			mKeyHintTextRatio = getRatio(a, R.styleable.KeyboardBaseView_keyHintTextRatio);
		}
		if (a.hasValue(R.styleable.KeyboardBaseView_keyTrailTextSize)) {
			mTrailTextRatio = UNDEFINED_RATIO;
			mTrailTextSize = a.getDimensionPixelSize(R.styleable.KeyboardBaseView_keyTrailTextSize, defTrailTextSize);	
		} else {
			mTrailTextRatio = getRatio(a, R.styleable.KeyboardBaseView_keyTrailTextRatio);
		}
    	mKeyTextColor = a.getColor(R.styleable.KeyboardBaseView_keyTextColor, defKeyTextColor);
    	mKeyModifierColor = a.getColor(R.styleable.KeyboardBaseView_keyModifierColor, defKeyModifierColor);
    	mKeyHintColor = a.getColor(R.styleable.KeyboardBaseView_keyHintColor, defKeyHintColor);
    	mTrailTextColor = a.getColor(R.styleable.KeyboardBaseView_keyTrailColor, defTrailTextColor);
    	mLanguagebarTextColor = a.getColor(R.styleable.KeyboardBaseView_languagebarTextColor, defLanguagebarTextColor);
    	mLanguagebarShadowColor = a.getColor(R.styleable.KeyboardBaseView_languagebarShadowColor, defLanguagebarShadowColor);
    	mPopupLayout = a.getResourceId(R.styleable.KeyboardBaseView_popupLayout, 0);
    	mShadowColor = a.getColor(R.styleable.KeyboardBaseView_shadowColor, 0);
    	mShadowRadius = a.getFloat(R.styleable.KeyboardBaseView_shadowRadius, 0f);
    	mBackgroundDimAmount = a.getFloat(R.styleable.KeyboardBaseView_backgroundDimAmount, 0.5f);
            	
        // Hint Padding
    	mNumHintTopPadding = a.getDimensionPixelSize(R.styleable.KeyboardBaseView_numHintTopPadding, defNumHintTopPadding);
    	mNumHintRightPadding = a.getDimensionPixelSize(R.styleable.KeyboardBaseView_numHintRightPadding, defNumHintRightPadding);
    	mHintPopupBottomPadding = a.getDimensionPixelSize(R.styleable.KeyboardBaseView_hintPopupBottomPadding, defHintPopupBottomPadding);
    	mHintPopupRightPadding = a.getDimensionPixelSize(R.styleable.KeyboardBaseView_hintPopupRightPadding, defHintPopupRightPadding);
    	
    	a.recycle();
    	
    	if(mKeyBackground != null) {
    		mKeyBackground.setBounds(0, 0, mKeyBackground.getIntrinsicWidth(), mKeyBackground.getIntrinsicHeight());
         }
    	
    	 if(mPreviewBackground != null) {
         	mPreviewBackground.setBounds(0, 0, mPreviewBackground.getIntrinsicWidth(), mPreviewBackground.getIntrinsicHeight());
         }
         
         if(mPreviewSlideBackground != null) {
         	mPreviewSlideBackground.setBounds(0, 0, mPreviewSlideBackground.getIntrinsicWidth(), mPreviewSlideBackground.getIntrinsicHeight());
         }
         
        if (background != null) {
        	setBackgroundDrawable(background);
        }
     	setPadding(0, 0, 0, paddingBottom);
    	
    	mPreviewPopup = new PopupWindow(context);
        if (keyPreviewLayout != 0) {
            mPreviewText = (TextView)LayoutInflater.from(context).inflate(keyPreviewLayout, null);
            mPreviewText.setBackgroundDrawable(mPreviewBackground);
            mPreviewTextSizeLarge = (int) res.getDimension(R.dimen.key_preview_text_size_large);
            mPreviewPopup.setContentView(mPreviewText);
            mPreviewPopup.setBackgroundDrawable(null);
        } else {
            mShowPreview = false;
        }
        mPreviewPopup.setTouchable(false);
        mPreviewPopup.setAnimationStyle(R.style.KeyPreviewAnimation);
        
        mMiniKeyboardPopup = new PopupWindow(context);
        mMiniKeyboardPopup.setBackgroundDrawable(null);
        mMiniKeyboardPopup.setAnimationStyle(R.style.MiniKeyboardAnimation);
        
        if(mKeyBackground != null) {
        	mKeyBackground.getPadding(mPadding);
        }
        
        if (mKeyboard != null) {
        	updateKeyHeight(mKeyboard.getKeyHeight());
        }
        
        invalidateAllKeys();
    }
    
    protected Typeface getKeyTextTypeface(CharSequence label) {
    	return getKeyTextTypeface(label, false);
    }
    
    protected Typeface getKeyTextTypeface(CharSequence label, boolean isIconic) {
    	if(isIconic) {
    		return KeyboardTheme.getZawgyiTypeFace(getContext());
    	}
    	
    	if(label == null) return Typeface.DEFAULT;
    	if(ZawGyiCorrection.isMyChar(label)) {
    		return KeyboardTheme.getZawgyiTypeFace(getContext());
    	}
    	switch(mKeyTextStyle) {
	    	case 0:
	    		return Typeface.DEFAULT;
	    	case 1:
	    		return Typeface.DEFAULT_BOLD;
	    	default:
	    		return Typeface.defaultFromStyle(mKeyTextStyle);
    	}
    }
    // } SMM
    
    /**
     * Attaches a keyboard to this view. The keyboard can be switched at any time and the
     * view will re-layout itself to accommodate the keyboard.
     * @see Keyboard
     * @see #getKeyboard()
     * @param keyboard the keyboard to display in this view
     */
    public void setKeyboard(Keyboard keyboard) {
        if (mKeyboard != null) {
            dismissKeyPreview();
        }
        if(DEBUG) {
        	Log.i(TAG, "setKeyboard");
        }
        // Remove any pending messages, except dismissing preview
        mHandler.cancelKeyTimers();
        mHandler.cancelPopupPreview();
        mKeyboard = keyboard;
        IMELogger.onSetKeyboard(keyboard);
        mKeys = mKeyDetector.setKeyboard(keyboard, -getPaddingLeft(),
                -getPaddingTop() + mVerticalCorrection);
        mKeyboardVerticalGap = (int)getResources().getDimension(R.dimen.key_bottom_gap);
        for (PointerTracker tracker : mPointerTrackers) {
            tracker.setKeyboard(mKeys, mKeyHysteresisDistance);
        }
        // SMM {
        if (keyboard != null) {
        	updateKeyHeight(keyboard.getKeyHeight());
        }
        // } SMM
        requestLayout();
        // Hint to reallocate the buffer if the size changed
        mKeyboardChanged = true;
        invalidateAllKeys();
        computeProximityThreshold(keyboard);
        mMiniKeyboardCache.clear();
        mIgnoreMove = true;
    }

    /**
     * Returns the current keyboard being displayed by this view.
     * @return the currently attached keyboard
     * @see #setKeyboard(Keyboard)
     */
    public Keyboard getKeyboard() {
        return mKeyboard;
    }

    /**
     * Return whether the device has distinct multi-touch panel.
     * @return true if the device has distinct multi-touch panel.
     */
    public boolean hasDistinctMultitouch() {
        return mHasDistinctMultitouch;
    }

    /**
     * Sets the state of the shift key of the keyboard, if any.
     * @param shifted whether or not to enable the state of the shift key
     * @return true if the shift key state changed, false if there was no change
     */
    public boolean setShifted(boolean shifted) {
        if (mKeyboard != null) {
            if (mKeyboard.setShifted(shifted)) {
                // The whole keyboard probably needs to be redrawn
                invalidateAllKeys();
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the state of the shift key of the keyboard, if any.
     * @return true if the shift is in a pressed state, false otherwise. If there is
     * no shift key on the keyboard or there is no keyboard attached, it returns false.
     */
    public boolean isShifted() {
        if (mKeyboard != null) {
            return mKeyboard.isShifted();
        }
        return false;
    }

    /**
     * Enables or disables the key feedback popup. This is a popup that shows a magnified
     * version of the depressed key. By default the preview is enabled.
     * @param previewEnabled whether or not to enable the key feedback popup
     * @see #isPreviewEnabled()
     */
    public void setPreviewEnabled(boolean previewEnabled) {
        mShowPreview = previewEnabled;
    }

    /**
     * Returns the enabled state of the key feedback popup.
     * @return whether or not the key feedback popup is enabled
     * @see #setPreviewEnabled(boolean)
     */
    public boolean isPreviewEnabled() {
        return mShowPreview;
    }

    // SMM {
    public int getLanguagebarTextColor() {
    	return mLanguagebarTextColor;
    }
    
    public int getLanguagebarShadowColor() {
    	return mLanguagebarShadowColor;
    }
    
    public void setAutoHideMiniKeyboard(boolean value) {
    	if (mAutoHideMiniKeyboard != value) {
    		mAutoHideMiniKeyboard = value;
    		dismissPopupKeyboard();
    	}
    }
    
    public void setVerticalCorrection(float value) {
    	mVerticalCorrection = value;
    }
    
    public void setTextSizeScale(final float textSizeScale) {
    	mKeyTextSizeScale = textSizeScale;
    }
    
    public void updateKeyHeight(int keyHeight) {
    	if (mKeyLabelTextRatio >= 0.0f) {
    		mKeyLabelTextSize = (int)(keyHeight * mKeyLabelTextRatio);
    	}
    	if (mKeyTextRatio >= 0.0f) {
    		mKeyTextSize = (int)(keyHeight * mKeyTextRatio);
    	}
    	if (mKeyHintTextRatio >= 0.0f) {
    		mKeyHintTextSize = (int)(keyHeight * mKeyHintTextRatio);
    	}
    	if (mTrailTextRatio >= 0.0f) {
    		mTrailTextSize = (int)(keyHeight * mTrailTextRatio);
    	}
    }
    // } SMM 
    
    public void setPopupParent(View v) {
        mMiniKeyboardParent = v;
    }

    public void setPopupOffset(int x, int y) {
        mPopupPreviewOffsetX = x;
        mPopupPreviewOffsetY = y;
        mPreviewPopup.dismiss();
    }

    /**
     * When enabled, calls to {@link OnKeyboardActionListener#onKey} will include key
     * codes for adjacent keys.  When disabled, only the primary key code will be
     * reported.
     * @param enabled whether or not the proximity correction is enabled
     */
    public void setProximityCorrectionEnabled(boolean enabled) {
        mKeyDetector.setProximityCorrectionEnabled(enabled);
    }

    /**
     * Returns true if proximity correction is enabled.
     */
    public boolean isProximityCorrectionEnabled() {
        return mKeyDetector.isProximityCorrectionEnabled();
    }

    protected boolean mayBeAdjustCase() {
    	if(mKeyboard == null) return false;
    	return mKeyboard.isShifted();
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Round up a little
        if (mKeyboard == null) {
            setMeasuredDimension(getPaddingLeft() + getPaddingRight(), getPaddingTop() + getPaddingBottom());
        } else {
            int width = mKeyboard.getMinWidth() + getPaddingLeft() + getPaddingRight();
            if (MeasureSpec.getSize(widthMeasureSpec) < width + 10) {
                width = MeasureSpec.getSize(widthMeasureSpec);
            }
            setMeasuredDimension(width, mKeyboard.getHeight() + getPaddingTop() + getPaddingBottom());
        }
    }

    /**
     * Compute the average distance between adjacent keys (horizontally and vertically)
     * and square it to get the proximity threshold. We use a square here and in computing
     * the touch distance from a key's center to avoid taking a square root.
     * @param keyboard
     */
    private void computeProximityThreshold(Keyboard keyboard) {
        if (keyboard == null) return;
        final Key[] keys = mKeys;
        if (keys == null) return;
        int length = keys.length;
        int dimensionSum = 0;
        for (int i = 0; i < length; i++) {
            Key key = keys[i];
            dimensionSum += Math.min(key.width, key.height + mKeyboardVerticalGap) + key.gap;
        }
        if (dimensionSum < 0 || length == 0) return;
        mKeyDetector.setProximityThreshold((int) (dimensionSum * 1.4f / length));
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // SMM {
        if (mKeyboard != null) {
        	mKeyboard.setKeyboardWidth(w);
        }
        // } SMM
        // Release the buffer, if any and it will be reallocated on the next draw
        mBuffer = null;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDrawPending || mBuffer == null || mKeyboardChanged) {
            onBufferDraw();
        }
        canvas.drawBitmap(mBuffer, 0, 0, null);
    }

    private void onBufferDraw() {
        if (mBuffer == null || mKeyboardChanged) {
            if (mBuffer == null || mKeyboardChanged &&
                    (mBuffer.getWidth() != getWidth() || mBuffer.getHeight() != getHeight())) {
                // Make sure our bitmap is at least 1x1
                final int width = Math.max(1, getWidth());
                final int height = Math.max(1, getHeight());
                mBuffer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                mCanvas = new Canvas(mBuffer);
            }
            invalidateAllKeys();
            mKeyboardChanged = false;
        }
        final Canvas canvas = mCanvas;
        canvas.clipRect(mDirtyRect, Op.REPLACE);
        
        if (mKeyboard == null) return;

        final Paint paint = mPaint;
        final Drawable keyBackground = mKeyBackground;
        final Rect clipRegion = mClipRegion;
        final Rect padding = mPadding;
        final int kbdPaddingLeft = getPaddingLeft();
        final int kbdPaddingTop = getPaddingTop();
        final Key[] keys = mKeys;
        final Key invalidKey = mInvalidatedKey;
        final int keyMarginX = mKeyHorizontalMargin;
        final int keyMarginY = mKeyVerticalMargin;

        // paint.setColor(mKeyTextColor); // SMM
        boolean drawSingleKey = false;
        if (invalidKey != null && canvas.getClipBounds(clipRegion)) {
            // TODO we should use Rect.inset and Rect.contains here.
            // Is clipRegion completely contained within the invalidated key?
            if (invalidKey.x + kbdPaddingLeft - 1 <= clipRegion.left &&
                    invalidKey.y + kbdPaddingTop - 1 <= clipRegion.top &&
                    invalidKey.x + invalidKey.width + kbdPaddingLeft + 1 >= clipRegion.right &&
                    invalidKey.y + invalidKey.height + kbdPaddingTop + 1 >= clipRegion.bottom) {
                drawSingleKey = true;
            }
        }
        canvas.drawColor(0x00000000, PorterDuff.Mode.CLEAR);
        final int keyCount = keys.length;
        for (int i = 0; i < keyCount; i++) {
            final Key key = keys[i];
            if (drawSingleKey && invalidKey != key) {
                continue;
            }
            
            // Draw key background
            final int bgX = key.x + kbdPaddingLeft;
            final int bgY = key.y + kbdPaddingTop;
            int[] drawableState = key.getCurrentDrawableState();
            keyBackground.setState(drawableState);
            
            final Rect bounds = keyBackground.getBounds();
            if (key.width != bounds.right || key.height != bounds.bottom) {
                //keyBackground.setBounds(0, 0, key.width, key.height);
            	keyBackground.setBounds(keyMarginX, keyMarginX, key.width - keyMarginX, key.height - keyMarginY); // SMM
            }
            canvas.translate(bgX, bgY);
            keyBackground.draw(canvas);

            // Switch the character to uppercase if shift is pressed
            //String label = key.label == null? null : adjustCase(key.label).toString();
            final boolean adjustCase = mayBeAdjustCase(); 
            String label = key.getLabel(adjustCase) == null ? null : key.getLabel(adjustCase).toString(); // SMM

            boolean shouldDrawIcon = true;
            if (label != null) {
                // For characters, use large font. For labels like "Done", use small font.
                final int labelSize;
                
                final int textWeight = mKeyboard.getKeyTextWeight();
                if(key.iconKey) {
                	//final int minSize = Math.min(key.height, key.width);
                	//final int iconicTextSize = (int)getContext().getResources().getFraction(R.fraction.key_iconic_text_size, minSize, minSize);
                	final int iconicTextSize = (int)(Math.min(key.height, key.width) * key.iconSizeAdjust);
                	labelSize = Math.max(iconicTextSize, mKeyTextSize);
                } else if (label.length() > 1 && key.codes.length < 2 && !key.fullTextSize && !DeadAccentSequence.isDeadAccent(label)) {
                    labelSize = (int)(mKeyLabelTextSize * mKeyTextSizeScale);
                } else {
	                if(textWeight < 2) {
	                	labelSize = mKeyTextSize - (mKeyTextSize / 4); 
	                } else if (textWeight > 2) {
	                	labelSize = mKeyTextSize + (mKeyTextSize / 4);
	                } else {
	                	labelSize = (int)(mKeyTextSize * mKeyTextSizeScale);
	                }
                }
                
                final int centerX = (key.width + padding.left - padding.right) / 2;
                final int centerY = (key.height + padding.top - padding.bottom) / 2;
                
                // Draw trail label
                String trail = key.trailLabel == null ? null : key.trailLabel.toString();
                if(trail != null) {
                	final int trailTextSize = mTrailTextSize;
                	paint.setColor(mTrailTextColor);
                	paint.setTextSize(trailTextSize);
                	paint.setTypeface(Typeface.DEFAULT);
                	paint.setTextAlign(Align.LEFT);
                	paint.setShadowLayer(mShadowRadius, 0, 0, mShadowColor);
                	
                	final float trailLabelHeight = getLabelHeight(paint, trailTextSize);
                	final int trailX = padding.left + key.width / 3;
                    final float trailBaseline = centerY + trailLabelHeight * KEY_LABEL_VERTICAL_ADJUSTMENT_FACTOR;
                    canvas.drawText(trail, trailX, trailBaseline, paint);
                    paint.setShadowLayer(0, 0, 0, 0);
                }
                
                paint.setTypeface(getKeyTextTypeface(label, key.iconic)); // SMM
                paint.setTextSize(labelSize);
                
                // SMM {
                paint.setColor(mKeyTextColor);
                if(key.modifier) {
                	paint.setColor(mKeyModifierColor);
                }
                paint.setTextAlign(Align.CENTER);
                // }
                
                int labelX = centerX;
                if(trail != null) {
                	labelX = padding.left + key.width / 6;
                }
                
                final String fixLabel = ZawGyiCorrection.ZawGyiDrawFix(label).toString(); 
                final float labelHeight = getLabelHeight(paint, labelSize);
                if(key.iconKey) {
                    final float baseline = centerY + labelHeight * 0.42f;
                    paint.setShadowLayer(0, 0, 0, 0);
                	canvas.drawText(fixLabel, labelX, baseline, paint);
                } else {
                	final float baseline = centerY + labelHeight * KEY_LABEL_VERTICAL_ADJUSTMENT_FACTOR;
                    // Draw a drop shadow for the text
                	paint.setShadowLayer(mShadowRadius, 0, 0, mShadowColor);
                	canvas.drawText(fixLabel, labelX, baseline, paint);
                    paint.setShadowLayer(0, 0, 0, 0);
                }
                
                // Draw hint label (if present) behind the main key
                String hint = key.hintLabel == null ? null : key.hintLabel.toString();
                if (hint != null && !key.isShifted()) {
                	final int hintTextSize = mKeyHintTextSize;
                	paint.setColor(mKeyHintColor);
                	paint.setTextSize(hintTextSize);
                	paint.setTypeface(Typeface.DEFAULT_BOLD);
                	paint.setTextAlign(Align.CENTER);

                    final float x = key.width - mNumHintRightPadding;
                    final float hintBaseline = mNumHintTopPadding;
                    canvas.drawText(hint, x, hintBaseline, paint);
                    paint.setShadowLayer(0, 0, 0, 0);
                }

                // Usually don't draw icon if label is not null, but we draw icon for the number
                // hint and popup hint.
                shouldDrawIcon = shouldDrawLabelAndIcon(key) || SoftKeyboard.hasPopupHint(key);
            }
            
            // Draw popup hint "..." at the bottom right corner of the key.
            if (key.icon == null && SoftKeyboard.hasPopupHint(key)) {
            	final int hintTextSize = mKeyHintTextSize;
            	paint.setColor(mLanguagebarTextColor);
            	paint.setTextSize(hintTextSize);
            	paint.setTypeface(Typeface.DEFAULT);
            	paint.setTextAlign(Align.CENTER);

                final float x = key.width - mHintPopupRightPadding;
                final float hintBaseline = key.height - mHintPopupBottomPadding;
                
                canvas.drawText(KeyboardTheme.getIconicLabel(KeyboardTheme.ICON_HINT_POPUP), x, hintBaseline, paint);
                paint.setShadowLayer(0, 0, 0, 0);
            }
            
            // Draw key icon.
            if (key.icon != null && shouldDrawIcon) {
                // Special handing for the upper-right number hint icons
                final int drawableWidth;
                final int drawableHeight;
                final int drawableX;
                final int drawableY;
                if (shouldDrawIconFully(key)) {
                    drawableWidth = key.width;
                    drawableHeight = key.height;
                    drawableX = 0;
                    drawableY = NUMBER_HINT_VERTICAL_ADJUSTMENT_PIXEL;
                } else {
                    drawableWidth = key.icon.getIntrinsicWidth();
                    drawableHeight = key.icon.getIntrinsicHeight();
                    drawableX = (key.width + padding.left - padding.right - drawableWidth) / 2;
                    drawableY = (key.height + padding.top - padding.bottom - drawableHeight) / 2;
                }
                canvas.translate(drawableX, drawableY);
                key.icon.setBounds(0, 0, drawableWidth, drawableHeight);
                key.icon.draw(canvas);
                canvas.translate(-drawableX, -drawableY);
            }
            canvas.translate(-bgX, -bgY);
        }
        mInvalidatedKey = null;
        // Overlay a dark rectangle to dim the keyboard
        if (mMiniKeyboard != null) {
            paint.setColor((int) (mBackgroundDimAmount * 0xFF) << 24);
            canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
        }

        if (DEBUG) {
            if (mShowTouchPoints) {
                for (PointerTracker tracker : mPointerTrackers) {
                    int startX = tracker.getStartX();
                    int startY = tracker.getStartY();
                    int lastX = tracker.getLastX();
                    int lastY = tracker.getLastY();
                    paint.setAlpha(128);
                    paint.setColor(0xFFFF0000);
                    canvas.drawCircle(startX, startY, 3, paint);
                    canvas.drawLine(startX, startY, lastX, lastY, paint);
                    paint.setColor(0xFF0000FF);
                    canvas.drawCircle(lastX, lastY, 3, paint);
                    paint.setColor(0xFF00FF00);
                    canvas.drawCircle((startX + lastX) / 2, (startY + lastY) / 2, 2, paint);
                }
            }
        }

        mDrawPending = false;
        mDirtyRect.setEmpty();
    }
    
    private float getLabelHeight(Paint paint, int labelSize) {
    	final Float cachedValue = mTextHeightCache.get(labelSize);
        if (cachedValue != null)
        	return cachedValue;
        
        Rect textBounds = new Rect();
        paint.getTextBounds(KEY_LABEL_HEIGHT_REFERENCE_CHAR, 0, 1, textBounds);
        final float labelHeight = textBounds.height();
        mTextHeightCache.put(labelSize, labelHeight);
        return labelHeight;
    }

    // TODO: clean up this method.
    private void dismissKeyPreview() {
        for (PointerTracker tracker : mPointerTrackers)
            tracker.updateKey(NOT_A_KEY);
        showPreview(NOT_A_KEY, null);
    }

    public void showPreview(int keyIndex, PointerTracker tracker) {
        int oldKeyIndex = mOldPreviewKeyIndex;
        mOldPreviewKeyIndex = keyIndex;
        final boolean isLanguageSwitchEnabled = (mKeyboard instanceof SoftKeyboard)
                && ((SoftKeyboard)mKeyboard).isLanguageSwitchEnabled();
        // We should re-draw popup preview when 1) we need to hide the preview, 2) we will show
        // the space key preview and 3) pointer moves off the space key to other letter key, we
        // should hide the preview of the previous key.
        final boolean hidePreviewOrShowSpaceKeyPreview = (tracker == null)
                || tracker.isSpaceKey(keyIndex) || tracker.isSpaceKey(oldKeyIndex);
        // If key changed and preview is on or the key is space (language switch is enabled)
        if (oldKeyIndex != keyIndex
                && (mShowPreview
                        || (hidePreviewOrShowSpaceKeyPreview && isLanguageSwitchEnabled))) {
            if (keyIndex == NOT_A_KEY) {
                mHandler.cancelPopupPreview();
                mHandler.dismissPreview(mDelayAfterPreview);
            } else if (tracker != null) {
            	int delay = mShowPreview ? mDelayBeforePreview : mDelayBeforeSpacePreview; // SMM
                mHandler.popupPreview(delay, keyIndex, tracker); // SMM
                // mHandler.popupPreview(mDelayBeforePreview, keyIndex, tracker);
            }
        }
    }

    @SuppressWarnings("deprecation")
	protected void showKey(final int keyIndex, PointerTracker tracker) {
        Key key = tracker.getKey(keyIndex);
        if (key == null)
            return;
        
        // Should not draw hint icon in key preview
        if ((key.icon != null || key.iconPreview != null) && !shouldDrawLabelAndIcon(key)) {
            mPreviewText.setCompoundDrawables(null, null, null,
                    key.iconPreview != null ? key.iconPreview : key.icon);
            mPreviewText.setText(null);
        } else if (key.label != null && key.codes != null) { // SMM
            mPreviewText.setCompoundDrawables(null, null, null, null);
            
            final boolean adjustCase = mayBeAdjustCase(); 
            final CharSequence previewText = tracker.getPreviewText(key, adjustCase);
            mPreviewText.setText(ZawGyiCorrection.ZawGyiDrawFix(previewText));
            if (key.label.length() > 1 && key.codes.length < 2) {
                mPreviewText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mKeyTextSize);
                //mPreviewText.setTypeface(Typeface.DEFAULT_BOLD); // SMM
            } else {
                mPreviewText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mPreviewTextSizeLarge);
                //mPreviewText.setTypeface(mKeyTextStyle); // SMM
            }
            mPreviewText.setTypeface(getKeyTextTypeface(key.label)); // SMM
        }
        mPreviewText.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        int popupWidth = Math.max(mPreviewText.getMeasuredWidth(), key.width
                + mPreviewText.getPaddingLeft() + mPreviewText.getPaddingRight());
        final int popupHeight = mPreviewHeight;
        LayoutParams lp = mPreviewText.getLayoutParams();
        if (lp != null) {
            lp.width = popupWidth;
            lp.height = popupHeight;
        }

        int popupPreviewX = key.x - (popupWidth - key.width) / 2;
        int popupPreviewY = key.y - popupHeight + mPreviewOffset;

        mHandler.cancelDismissPreview();
        if (mOffsetInWindow == null) {
            mOffsetInWindow = new int[2];
            getLocationInWindow(mOffsetInWindow);
            mOffsetInWindow[0] += mPopupPreviewOffsetX; // Offset may be zero
            mOffsetInWindow[1] += mPopupPreviewOffsetY; // Offset may be zero
            int[] windowLocation = new int[2];
            getLocationOnScreen(windowLocation);
            mWindowY = windowLocation[1];
        }
        popupPreviewX += mOffsetInWindow[0];
        popupPreviewY += mOffsetInWindow[1];

        // If the popup cannot be shown above the key, put it on the side
        if (popupPreviewY + mWindowY < 0) {
            // If the key you're pressing is on the left side of the keyboard, show the popup on
            // the right, offset by enough to see at least one key to the left/right.
            if (key.x + key.width <= getWidth() / 2) {
                popupPreviewX += (int) (key.width * 2.5);
            } else {
                popupPreviewX -= (int) (key.width * 2.5);
            }
            popupPreviewY += popupHeight;
        }

        // Set the preview background state
        // SMM {
        if(key.codes[0] == KeyCodes.KEYCODE_SPACE) {
        	mPreviewText.setBackgroundDrawable(mPreviewSlideBackground);
        } else {
        	mPreviewText.setBackgroundDrawable(mPreviewBackground);
        }
        // } SMM
        mPreviewText.getBackground().setState(key.popupResId != 0 ? LONG_PRESSABLE_STATE_SET : EMPTY_STATE_SET);
        
        // Record popup preview position to display mini-keyboard later at the same positon
        mPopupPreviewDisplayedY = popupPreviewY;
        mPreviewText.setVisibility(VISIBLE);
        
        if (mPreviewPopup.isShowing()) {
            mPreviewPopup.update(popupPreviewX, popupPreviewY, popupWidth, popupHeight);
        } else {
            mPreviewPopup.setWidth(popupWidth);
            mPreviewPopup.setHeight(popupHeight);
            mPreviewPopup.showAtLocation(mMiniKeyboardParent, Gravity.NO_GRAVITY, popupPreviewX, popupPreviewY);
        }
    }

    /**
     * Requests a redraw of the entire keyboard. Calling {@link #invalidate} is not sufficient
     * because the keyboard renders the keys to an off-screen buffer and an invalidate() only
     * draws the cached buffer.
     * @see #invalidateKey(Key)
     */
    public void invalidateAllKeys() {
        mDirtyRect.union(0, 0, getWidth(), getHeight());
        mDrawPending = true;
        invalidate();
    }

    /**
     * Invalidates a key so that it will be redrawn on the next repaint. Use this method if only
     * one key is changing it's content. Any changes that affect the position or size of the key
     * may not be honored.
     * @param key key in the attached {@link Keyboard}.
     * @see #invalidateAllKeys
     */
    public void invalidateKey(Key key) {
        if (key == null)
            return;
        mInvalidatedKey = key;
        // TODO we should clean up this and record key's region to use in onBufferDraw.
        mDirtyRect.union(key.x + getPaddingLeft(), key.y + getPaddingTop(),
                key.x + key.width + getPaddingLeft(), key.y + key.height + getPaddingTop());
        onBufferDraw();
        invalidate(key.x + getPaddingLeft(), key.y + getPaddingTop(),
                key.x + key.width + getPaddingLeft(), key.y + key.height + getPaddingTop());
    }
    
    private boolean openPopupIfRequired(int keyIndex, PointerTracker tracker) {
        // Check if we have a popup layout specified first.
        if (mPopupLayout == 0) {
            return false;
        }

        Key popupKey = tracker.getKey(keyIndex);
        if (popupKey == null)
            return false;
        boolean result = onLongPress(popupKey);
        if (result) {
            dismissKeyPreview();
            mMiniKeyboardTrackerId = tracker.mPointerId;
            // Mark this tracker "already processed" and remove it from the pointer queue
            tracker.setAlreadyProcessed();
            mPointerQueue.remove(tracker);
        }
        return result;
    }

    private View inflateMiniKeyboardContainer(Key popupKey) {
        int popupKeyboardId = popupKey.popupResId;
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        View container = inflater.inflate(mPopupLayout, null);
        if (container == null)
            throw new NullPointerException();

        KeyboardBaseView miniKeyboard =
                (KeyboardBaseView)container.findViewById(R.id.KeyboardBaseView);
        miniKeyboard.setOnKeyboardActionListener(new OnKeyboardActionListener() {
            public void onKey(int primaryCode, int[] keyCodes, int x, int y) {
                mKeyboardActionListener.onKey(primaryCode, keyCodes, x, y);
                dismissPopupKeyboard();
            }

            public void onText(CharSequence text) {
                mKeyboardActionListener.onText(text);
                dismissPopupKeyboard();
            }

            public void onCancel() {
                dismissPopupKeyboard();
            }

            public void swipeLeft() {
            }
            public void swipeRight() {
            }
            public void swipeUp() {
            }
            public void swipeDown() {
            }
            public void onPress(int primaryCode) {
                mKeyboardActionListener.onPress(primaryCode);
            }
            public void onRelease(int primaryCode) {
                mKeyboardActionListener.onRelease(primaryCode);
            }
        });
        // Override default ProximityKeyDetector.
        miniKeyboard.mKeyDetector = new MiniKeyboardKeyDetector(mMiniKeyboardSlideAllowance);

        Keyboard keyboard;
        if (popupKey.popupCharacters != null) {
            keyboard = new Keyboard(getContext(), popupKeyboardId, popupKey.popupCharacters,
                    -1, getPaddingLeft() + getPaddingRight());
        } else {
            keyboard = new Keyboard(getContext(), popupKeyboardId);
        }
        // SMM {
        if (!mAutoHideMiniKeyboard) {
        	miniKeyboard.setVerticalCorrection(0);
        }
        // } SMM
        miniKeyboard.setKeyboard(keyboard);
        miniKeyboard.setPopupParent(this);

        container.measure(MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.AT_MOST));

        return container;
    }

    private static boolean isOneRowKeys(List<Key> keys) {
        if (keys.size() == 0) return false;
        final int edgeFlags = keys.get(0).edgeFlags;
        // HACK: The first key of mini keyboard which was inflated from xml and has multiple rows,
        // does not have both top and bottom edge flags on at the same time.  On the other hand,
        // the first key of mini keyboard that was created with popupCharacters must have both top
        // and bottom edge flags on.
        // When you want to use one row mini-keyboard from xml file, make sure that the row has
        // both top and bottom edge flags set.
        return (edgeFlags & Keyboard.EDGE_TOP) != 0 && (edgeFlags & Keyboard.EDGE_BOTTOM) != 0;
    }

    /**
     * Called when a key is long pressed. By default this will open any popup keyboard associated
     * with this key through the attributes popupLayout and popupCharacters.
     * @param popupKey the key that was long pressed
     * @return true if the long press is handled, false otherwise. Subclasses should call the
     * method on the base class if the subclass doesn't wish to handle the call.
     */
    protected boolean onLongPress(Key popupKey) {
        // TODO if popupKey.popupCharacters has only one letter, send it as key without opening
        // mini keyboard.
        if (popupKey.popupResId == 0)
            return false;

        View container = mMiniKeyboardCache.get(popupKey);
        if (container == null) {
            container = inflateMiniKeyboardContainer(popupKey);
            mMiniKeyboardCache.put(popupKey, container);
        }
        mMiniKeyboard = (KeyboardBaseView)container.findViewById(R.id.KeyboardBaseView);
        if (mWindowOffset == null) {
            mWindowOffset = new int[2];
            getLocationInWindow(mWindowOffset);
        }

        // Get width of a key in the mini popup keyboard = "miniKeyWidth".
        // On the other hand, "popupKey.width" is width of the pressed key on the main keyboard.
        // We adjust the position of mini popup keyboard with the edge key in it:
        //  a) When we have the leftmost key in popup keyboard directly above the pressed key
        //     Right edges of both keys should be aligned for consistent default selection
        //  b) When we have the rightmost key in popup keyboard directly above the pressed key
        //     Left edges of both keys should be aligned for consistent default selection
        final List<Key> miniKeys = mMiniKeyboard.getKeyboard().getKeys();
        final int miniKeyWidth = miniKeys.size() > 0 ? miniKeys.get(0).width : 0;

        // HACK: Have the leftmost number in the popup characters right above the key
        boolean isNumberAtLeftmost = hasMultiplePopupChars(popupKey) && isNumberAtLeftmostPopupChar(popupKey);
        int popupX = popupKey.x + mWindowOffset[0];
        popupX += getPaddingLeft();
        if (isNumberAtLeftmost) {
            popupX += popupKey.width - miniKeyWidth;  // adjustment for a) described above
            popupX -= container.getPaddingLeft();
        } else {
            popupX += miniKeyWidth;  // adjustment for b) described above
            popupX -= container.getMeasuredWidth();
            popupX += container.getPaddingRight();
        }
        int popupY = popupKey.y + mWindowOffset[1];
        popupY += getPaddingTop();
        popupY -= container.getMeasuredHeight();
        popupY += container.getPaddingBottom();
        
        final int x = popupX;
        //final int y = mShowPreview && isOneRowKeys(miniKeys) ? mPopupPreviewDisplayedY : popupY;
        final int y = mShowPreview && isOneRowKeys(miniKeys) ? Math.max(mPopupPreviewDisplayedY, popupY) : popupY; // SMM

        int adjustedX = x;
        if (x < 0) {
            adjustedX = 0;
        } else if (x > (getMeasuredWidth() - container.getMeasuredWidth())) {
            adjustedX = getMeasuredWidth() - container.getMeasuredWidth();
        }
        mMiniKeyboardOriginX = adjustedX + container.getPaddingLeft() - mWindowOffset[0];
        mMiniKeyboardOriginY = y + container.getPaddingTop() - mWindowOffset[1];
        mMiniKeyboard.setPopupOffset(adjustedX, y);
        mMiniKeyboard.setShifted(isShifted());
        // Mini keyboard needs no pop-up key preview displayed.
        mMiniKeyboard.setPreviewEnabled(false);
        mMiniKeyboardPopup.setContentView(container);
        mMiniKeyboardPopup.setWidth(container.getMeasuredWidth());
        mMiniKeyboardPopup.setHeight(container.getMeasuredHeight());
        mMiniKeyboardPopup.showAtLocation(this, Gravity.NO_GRAVITY, x, y);

        // Inject down event on the key to mini keyboard.
        long eventTime = SystemClock.uptimeMillis();
        mMiniKeyboardPopupTime = eventTime;
        
        if (mAutoHideMiniKeyboard) { // SMM
	        MotionEvent downEvent = generateMiniKeyboardMotionEvent(MotionEvent.ACTION_DOWN, popupKey.x
	                + popupKey.width / 2, popupKey.y + popupKey.height / 2, eventTime);
	        mMiniKeyboard.onTouchEvent(downEvent);
	        downEvent.recycle();
        }
        invalidateAllKeys();
        return true;
    }

    private static boolean hasMultiplePopupChars(Key key) {
        if (key.popupCharacters != null && key.popupCharacters.length() > 1) {
            return true;
        }
        return false;
    }

    private boolean shouldDrawIconFully(Key key) {
        return isNumberAtEdgeOfPopupChars(key) || isF1Key(key)
                || SoftKeyboard.hasKeyModifierPopup(key);
    }

    private boolean shouldDrawLabelAndIcon(Key key) {
        return (isNumberAtEdgeOfPopupChars(key) || isNonMicF1Key(key)
                || SoftKeyboard.hasKeyModifierPopup(key)) && (!key.iconKey);
    }

    private boolean isF1Key(Key key) {
        return (mKeyboard instanceof SoftKeyboard) && ((SoftKeyboard)mKeyboard).isF1Key(key);
    }

    private boolean isNonMicF1Key(Key key) {
        return isF1Key(key) && key.label != null;
    }
    
    /* package */ static boolean isNumberAtEdgeOfPopupChars(Key key) {
        return isNumberAtLeftmostPopupChar(key) || isNumberAtRightmostPopupChar(key);
    }

    /* package */ static boolean isNumberAtLeftmostPopupChar(Key key) {
        if (key.popupCharacters != null && key.popupCharacters.length() > 0
                && isAsciiDigit(key.popupCharacters.charAt(0))) {
            return true;
        }
        return false;
    }

    /* package */ static boolean isNumberAtRightmostPopupChar(Key key) {
        if (key.popupCharacters != null && key.popupCharacters.length() > 0
                && isAsciiDigit(key.popupCharacters.charAt(key.popupCharacters.length() - 1))) {
            return true;
        }
        return false;
    }

    /* package */ static boolean isAsciiDigit(char c) {
        return (c < 0x80) && Character.isDigit(c);
    }

    private MotionEvent generateMiniKeyboardMotionEvent(int action, int x, int y, long eventTime) {
        return MotionEvent.obtain(mMiniKeyboardPopupTime, eventTime, action,
                    x - mMiniKeyboardOriginX, y - mMiniKeyboardOriginY, 0);
    }

    private PointerTracker getPointerTracker(final int id) {
        final ArrayList<PointerTracker> pointers = mPointerTrackers;
        final Key[] keys = mKeys;
        final OnKeyboardActionListener listener = mKeyboardActionListener;

        // Create pointer trackers until we can get 'id+1'-th tracker, if needed.
        for (int i = pointers.size(); i <= id; i++) {
            final PointerTracker tracker =
                new PointerTracker(i, mHandler, mKeyDetector, this, getResources());
            if (keys != null)
                tracker.setKeyboard(keys, mKeyHysteresisDistance);
            if (listener != null)
                tracker.setOnKeyboardActionListener(listener);
            pointers.add(tracker);
        }

        return pointers.get(id);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent me) {
        final int pointerCount = me.getPointerCount();
        final int action = me.getActionMasked();
        final int oldPointerCount = mOldPointerCount;
        mOldPointerCount = pointerCount;

        // TODO: cleanup this code into a multi-touch to single-touch event converter class?
        // If the device does not have distinct multi-touch support panel, ignore all multi-touch
        // events except a transition from/to single-touch.
        if (!mHasDistinctMultitouch && pointerCount > 1 && oldPointerCount > 1) {
            return true;
        }

        // Track the last few movements to look for spurious swipes.
        mSwipeTracker.addMovement(me);

        // We must disable gesture detector while mini-keyboard is on the screen.
        if (mMiniKeyboard == null && mGestureDetector.onTouchEvent(me)) {
            dismissKeyPreview();
            mHandler.cancelKeyTimers();
            return true;
        }

        final long eventTime = me.getEventTime();
        final int index = me.getActionIndex();
        final int id = me.getPointerId(index);
        final int x = (int)me.getX(index);
        final int y = (int)me.getY(index);

        // Needs to be called after the gesture detector gets a turn, as it may have
        // displayed the mini keyboard
        if (mMiniKeyboard != null) {
        	// SMM {
        	if (!mAutoHideMiniKeyboard) {
        		if (action == MotionEvent.ACTION_DOWN) {
        			dismissPopupKeyboard();
        		}
        		return true;
        	} // } SMM
            final int miniKeyboardPointerIndex = me.findPointerIndex(mMiniKeyboardTrackerId);
            if (miniKeyboardPointerIndex >= 0 && miniKeyboardPointerIndex < pointerCount) {
                final int miniKeyboardX = (int)me.getX(miniKeyboardPointerIndex);
                final int miniKeyboardY = (int)me.getY(miniKeyboardPointerIndex);
                MotionEvent translated = generateMiniKeyboardMotionEvent(action,
                        miniKeyboardX, miniKeyboardY, eventTime);
                mMiniKeyboard.onTouchEvent(translated);
                translated.recycle();
            }
            return true;
        }

        if (mHandler.isInKeyRepeat()) {
            // It will keep being in the key repeating mode while the key is being pressed.
            if (action == MotionEvent.ACTION_MOVE) {
                return true;
            }
            final PointerTracker tracker = getPointerTracker(id);
            // Key repeating timer will be canceled if 2 or more keys are in action, and current
            // event (UP or DOWN) is non-modifier key.
            if (pointerCount > 1 && !tracker.isModifier()) {
                mHandler.cancelKeyRepeatTimer();
            }
            // Up event will pass through.
        }

        // TODO: cleanup this code into a multi-touch to single-touch event converter class?
        // Translate mutli-touch event to single-touch events on the device that has no distinct
        // multi-touch panel.
        if (!mHasDistinctMultitouch) {
            // Use only main (id=0) pointer tracker.
            PointerTracker tracker = getPointerTracker(0);
            //int oldPointerCount = mOldPointerCount; // SMM
            if (pointerCount == 1 && oldPointerCount == 2) {
                // Multi-touch to single touch transition.
                // Send a down event for the latest pointer.
                tracker.onDownEvent(x, y, eventTime);
            } else if (pointerCount == 2 && oldPointerCount == 1) {
                // Single-touch to multi-touch transition.
                // Send an up event for the last pointer.
                tracker.onUpEvent(tracker.getLastX(), tracker.getLastY(), eventTime);
            } else if (pointerCount == 1 && oldPointerCount == 1) {
                tracker.onTouchEvent(action, x, y, eventTime);
            } else {
                Log.w(TAG, "Unknown touch panel behavior: pointer count is " + pointerCount
                        + " (old " + oldPointerCount + ")");
            }
            //mOldPointerCount = pointerCount;// SMM  
            return true;
        }

        if (action == MotionEvent.ACTION_MOVE) {
        	if (!mIgnoreMove) {
	            for (int i = 0; i < pointerCount; i++) {
	                PointerTracker tracker = getPointerTracker(me.getPointerId(i));
	                tracker.onMoveEvent((int)me.getX(i), (int)me.getY(i), eventTime);
	            }
        	}
        } else {
            PointerTracker tracker = getPointerTracker(id);
            switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
            	mIgnoreMove = false;
                onDownEvent(tracker, x, y, eventTime);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            	mIgnoreMove = false;
                onUpEvent(tracker, x, y, eventTime);
                break;
            case MotionEvent.ACTION_CANCEL:
                onCancelEvent(tracker, x, y, eventTime);
                break;
            }
        }

        return true;
    }

    private void onDownEvent(PointerTracker tracker, int x, int y, long eventTime) {
        if (tracker.isOnModifierKey(x, y)) {
            // Before processing a down event of modifier key, all pointers already being tracked
            // should be released.
            mPointerQueue.releaseAllPointersExcept(null, eventTime);
        }
        tracker.onDownEvent(x, y, eventTime);
        mPointerQueue.add(tracker);
    }

    private void onUpEvent(PointerTracker tracker, int x, int y, long eventTime) {
        if (tracker.isModifier()) {
            // Before processing an up event of modifier key, all pointers already being tracked
            // should be released.
            mPointerQueue.releaseAllPointersExcept(tracker, eventTime);
        } else {
            int index = mPointerQueue.lastIndexOf(tracker);
            if (index >= 0) {
                mPointerQueue.releaseAllPointersOlderThan(tracker, eventTime);
            } else {
                Log.w(TAG, "onUpEvent: corresponding down event not found for pointer "
                        + tracker.mPointerId);
            }
        }
        tracker.onUpEvent(x, y, eventTime);
        mPointerQueue.remove(tracker);
    }

    private void onCancelEvent(PointerTracker tracker, int x, int y, long eventTime) {
        tracker.onCancelEvent(x, y, eventTime);
        mPointerQueue.remove(tracker);
    }

    protected void swipeRight() {
        mKeyboardActionListener.swipeRight();
    }

    protected void swipeLeft() {
    	mKeyboardActionListener.swipeLeft();
    }

    protected void swipeUp() {
    	mKeyboardActionListener.swipeUp();
    }

    protected void swipeDown() {
    	mKeyboardActionListener.swipeDown();
    }

    public void closing() {
        mPreviewPopup.dismiss();
        mHandler.cancelAllMessages();

        dismissPopupKeyboard();
        mBuffer = null;
        mCanvas = null;
        mMiniKeyboardCache.clear();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        closing();
    }

    private void dismissPopupKeyboard() {
        if (mMiniKeyboardPopup.isShowing()) {
            mMiniKeyboardPopup.dismiss();
            mMiniKeyboard = null;
            mMiniKeyboardOriginX = 0;
            mMiniKeyboardOriginY = 0;
            invalidateAllKeys();
        }
    }

    public boolean handleBack() {
        if (mMiniKeyboardPopup.isShowing()) {
            dismissPopupKeyboard();
            return true;
        }
        return false;
    }
}
