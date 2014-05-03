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


package com.android.inputmethod.skeyboard;


import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

public class KeyboardThemes {

	private static Typeface mTypeface;
	
    //private final int NUMBER_HINT_COUNT = 10;
    
	// This should be aligned with Keyboard.keyIcon enum.
	public static final int ICON_UNDEFINED = 0;
	public static final int ICON_MIC_KEY = 1;
	public static final int ICON_SETTINGS_KEY = 2;
	public static final int ICON_LANGUAGE_KEY = 3;
	public static final int ICON_SHIFT_KEY = 4;
	public static final int ICON_SHIFTLOCKED_KEY = 5;
	public static final int ICON_SPACE_KEY = 6;
	public static final int ICON_TAB_KEY = 7;
	public static final int ICON_RETURN_KEY = 8;
	public static final int ICON_SEARCH_KEY = 9;
	public static final int ICON_DELETE_KEY = 10;
	public static final int ICON_DONE_KEY = 11;
	public static final int ICON_HINT_POPUP = 12;
	private static final int ICON_LAST = ICON_HINT_POPUP; 
	
	private static int[] mIconValues;
	static {
		mIconValues = new int[ICON_LAST + 1];
		mIconValues[ICON_UNDEFINED] = ICON_LAST;
		mIconValues[ICON_MIC_KEY] = 0xF002;
		mIconValues[ICON_SETTINGS_KEY] = 0xE013;
		mIconValues[ICON_LANGUAGE_KEY] = 0xE073;
		mIconValues[ICON_SHIFT_KEY] = 0xF006;
		mIconValues[ICON_SHIFTLOCKED_KEY] = 0xF007;
		mIconValues[ICON_SPACE_KEY] = 0xF004;
		mIconValues[ICON_TAB_KEY] = 0xF009;
		mIconValues[ICON_RETURN_KEY] = 0xF008;
		mIconValues[ICON_SEARCH_KEY] = 0xE012;
		mIconValues[ICON_DELETE_KEY] = 0xE041;
		mIconValues[ICON_DONE_KEY] = 0xF005;
		mIconValues[ICON_HINT_POPUP] = 0x2026;
	}
	
	public static final int THEME_BASIC = 0;
	public static final int THEME_STONE = 1;
	public static final int THEME_WHITE = 2;
	public static final int THEME_IPHONE = 3;
	public static final int THEME_GINGERBREAD = 4;
	public static final int THEME_HOLO = 5;
	public static final int THEME_GALAXY = 6;
	public static final int THEME_PINK = 7;
	
	private final Theme mTheme;
	
	public static Typeface getTypeFace(Context context) {
		if(mTypeface == null) {
			mTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/zawgyi.ttf");
		}
		
		return mTypeface;
	}
	
	public static void getRgbColor(int color, float[] argb) {
		argb[0] = (color >> 24) & 0xFF;
		argb[1] = (color >> 16) & 0xFF;
		argb[2] = (color >> 8) & 0xFF;
		argb[3] = (color >> 0) & 0xFF;
	}
	
	public static int getDarkerColor(int color) {
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		hsv[2] *= 0.8f; // value component
		return Color.HSVToColor(hsv);
	}

	public static int toGrayscale(int color) {
		int red = Color.red(color);
		int green = Color.green(color);
		int blue = Color.blue(color);
		int gray = (int) (red * 0.3 + green * 0.59 + blue * 0.11);
		return Color.rgb(gray, gray, gray);
	}
	
	public static String getIconicLabel(int iconId) {
        if (iconId <= ICON_UNDEFINED || iconId > ICON_LAST)
            return null;
		
		return String.valueOf((char)mIconValues[iconId]);
	}
	
	public KeyboardThemes(int themeId) {
		mTheme = getTheme(themeId);
	}
	
	/*@SuppressWarnings("unused")
	private static Bitmap createNumHintIcon(Context context, int iconId, final Theme theme, int width, int height) {
		if(iconId < 0 || iconId > 9) return null;
		
		final Resources res = context.getResources();
		final int topPadding = res.getDimensionPixelSize(R.dimen.num_popup_icon_top_padding);
		final int rightPadding = res.getDimensionPixelSize(R.dimen.num_popup_icon_right_padding);
		
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setTextAlign(Align.CENTER);
		paint.setAlpha(255);
		paint.setTextSize(theme.getKeyHintTextSize());
		paint.setTypeface(getTypeFace(context));
		
		paint.setShadowLayer(theme.getShadowRadius(), 0, 0, theme.getShadowColor());
		
		final Bitmap buffer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(buffer);
        canvas.drawColor(res.getColor(R.color.transparent), PorterDuff.Mode.CLEAR);
        
        final String text = String.valueOf((char)('0' + iconId));
        Rect textBounds = new Rect();
        paint.getTextBounds(text, 0, 1, textBounds);
        
        paint.setColor(theme.getKeyHintColor());
        
        final float x = width - rightPadding - (textBounds.width() / 2);
        final float y = topPadding + textBounds.height();
        
        canvas.drawText(text, x, y, paint);
        
        paint.setShadowLayer(0, 0, 0, 0);
        
		return buffer;
	}
	
	@SuppressWarnings("unused")
	private static Bitmap createHintPopupIcon(Context context, final Theme theme, int width, int height) {
		final Resources res = context.getResources();
		final int bottomPadding = res.getDimensionPixelSize(R.dimen.hint_popup_icon_bottom_padding);
		final int rightPadding = res.getDimensionPixelSize(R.dimen.hint_popup_icon_right_padding);
		
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setTextAlign(Align.RIGHT);
		paint.setAlpha(255);
		paint.setTextSize(theme.getKeyHintTextSize());
		paint.setTypeface(getTypeFace(context));
		
		final Bitmap buffer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(buffer);
        canvas.drawColor(res.getColor(R.color.transparent), PorterDuff.Mode.CLEAR);
        
        final String text = String.valueOf((char)mIconValues[ICON_HINT_POPUP]);
        Rect textBounds = new Rect();
        paint.getTextBounds(text, 0, 1, textBounds);
        paint.setColor(theme.getLanguagebarTextColor());
        
        final float x = (width - rightPadding) + (textBounds.width() / 3);
        final float y = (height - bottomPadding) + (textBounds.height() / 2);
        
        canvas.drawText(text, x, y, paint);
        
		return buffer;
	}*/
	
	private static Bitmap createSpaceKeyIcon(Context context, final int color) {
		final Resources res = context.getResources();
		final int width = res.getDimensionPixelSize(R.dimen.key_icon_width);
		final int height = res.getDimensionPixelSize(R.dimen.key_icon_height);
		final int textSize = res.getDimensionPixelSize(R.dimen.key_icon_text_size);
		
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setTextAlign(Align.CENTER);
		paint.setAlpha(255);
		paint.setTextSize(textSize);
		paint.setTypeface(getTypeFace(context));
		
		final Bitmap buffer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(buffer);
        canvas.drawColor(res.getColor(R.color.transparent), PorterDuff.Mode.CLEAR);
        
        final String text = String.valueOf((char)mIconValues[ICON_SPACE_KEY]);
        Rect textBounds = new Rect();
        paint.getTextBounds(text, 0, 1, textBounds);
        paint.setColor(color);
        
        final float x = width * 0.5f;
        final float y = height - textBounds.height();
        
        canvas.drawText(text, x, y, paint);
        
		return buffer;
	}
	
	private Theme getTheme(int themeId) {
		switch(themeId) {
		default:
		case THEME_BASIC:
			return new Theme(R.style.KeyboardBaseView_basic);
		case THEME_STONE:
			return new Theme(R.style.KeyboardBaseView_stone);
		case THEME_WHITE:
			return new Theme(R.style.KeyboardBaseView_white);
		case THEME_IPHONE:
			return new Theme(R.style.KeyboardBaseView_iphone);
		case THEME_GINGERBREAD:
			return new Theme(R.style.KeyboardBaseView_gingerbread);
		case THEME_HOLO:
			return new Theme(R.style.KeyboardBaseView_holo);
		case THEME_GALAXY:
			return new Theme(R.style.KeyboardBaseView_galaxy);
		case THEME_PINK:
			return new Theme(R.style.KeyboardBaseView_pink);
		}
	}
	
	public Theme loadTheme(Context context, AttributeSet attrs, int defStyle) {
		if(mTheme == null || attrs == null || context == null)
			return null;
		
		mTheme.load(context, attrs, defStyle);
		return mTheme;
	}
	
	public Theme loadTheme(Context context) {
		if(mTheme == null || context == null)
			return mTheme;
		
		mTheme.load(context);
		return mTheme;
	}
	
	public Theme getTheme() {
		return mTheme;
	}
	
	public class Theme {
		
		private final int mResId;
		
		public Drawable background;
		public Drawable keyBackground;
		public int paddingBottom;
		public int keyTextStyle;
		public int labelTextSize;
		public int keyTextSize;
		public int keyTextColor;
		public int keyModifierColor;
		public int keyHintTextSize;
		public int keyHintColor;
		public int keyTrailTextSize;
		public int keyTrailColor;
		public int languagebarTextColor;
		public int languagebarShadowColor;
		public int keyPreviewLayout;
		public int keyPreviewOffset;
		public int keyPreviewHeight;
		public Drawable keyPreviewBackground;
		public Drawable keyPreviewSlideBackground;
		public int popupLayout;
		public int keyHysteresisDistance;
		public int verticalCorrection;
		public int shadowColor;
		public float shadowRadius;
		public float backgroundDimAmount;
		public Drawable keyHintPopup;
		
		public Drawable spaceKeyIcon;
		public Drawable spaceKeyIconModifier;
		public Drawable spaceAutoCompletionIndicator;
		
		public Drawable candidateBackground;
		public Drawable candidateSelection;
		public Drawable candidateDivider;
		public int candidateTextColorNormal;
		public int candidateTextColorRecommended;
		public int candidateTextColorOther;
		
		public int numHintTopPadding;
		public int numHintRightPadding;
		public int hintPopupBottomPadding;
		public int hintPopupRightPadding;
		
		public Theme(int resId) {
			mResId = resId;
		}
		
		public void load(Context context) {
			load(context, mResId);
		}
		
		public void load(Context context, int resId) {
			load(context, context.obtainStyledAttributes(resId, R.styleable.KeyboardBaseView));
		}
		
		public void load(Context context, AttributeSet attrs, int defStyle) {
			load(context, context.obtainStyledAttributes(attrs, R.styleable.KeyboardBaseView, defStyle, R.style.KeyboardBaseView));
		}
		
		public void create(Context context, int backgroundColor, int keyBackgroundColor, int keyTextColor) {
		}
		
		private void load(Context context, final TypedArray a) {
			final Resources res = context.getResources();
			
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
	        
	        int n = a.getIndexCount();

	        for (int i = 0; i < n; i++) {
	            int attr = a.getIndex(i);

	            switch (attr) {
	            case R.styleable.KeyboardBaseView_keyboardBackground:
	            	background = a.getDrawable(attr);
	            	break;
	            	
	            case R.styleable.KeyboardBaseView_keyBackground:
	            	keyBackground = a.getDrawable(attr);
	            	break;
	            case R.styleable.KeyboardBaseView_keyboardPaddingBottom:
	            	paddingBottom = a.getDimensionPixelOffset(attr, 0);
	            	break;	
	            case R.styleable.KeyboardBaseView_keyTextStyle:
	            	keyTextStyle = a.getInt(attr, 0);
	            	break;
	            case R.styleable.KeyboardBaseView_keyHysteresisDistance:
	            	keyHysteresisDistance = a.getDimensionPixelOffset(attr, 0);
	            	break;
	            case R.styleable.KeyboardBaseView_verticalCorrection:
	            	verticalCorrection = a.getDimensionPixelOffset(attr, 0);
	            	break;
	            case R.styleable.KeyboardBaseView_keyPreviewLayout:
	            	keyPreviewLayout = a.getResourceId(attr, 0);
	            	break;
	            case R.styleable.KeyboardBaseView_keyPreviewOffset:
	            	keyPreviewOffset = a.getDimensionPixelOffset(attr, 0);
	            	break;
	            case R.styleable.KeyboardBaseView_keyPreviewHeight:
	            	keyPreviewHeight = a.getDimensionPixelSize(attr, 80);
	            	break;
	            case R.styleable.KeyboardBaseView_keyPreviewBackground:
	            	keyPreviewBackground = a.getDrawable(attr);
	            	break;
	            case R.styleable.KeyboardBaseView_keyPreviewSlideBackground:
	            	keyPreviewSlideBackground = a.getDrawable(attr);
	            	break;
	            case R.styleable.KeyboardBaseView_labelTextSize:
	            	labelTextSize = a.getDimensionPixelSize(attr, defLabelTextSize);
	            	break;
	            case R.styleable.KeyboardBaseView_keyTextSize:
	            	keyTextSize = a.getDimensionPixelSize(attr, defKeyTextSize);
	            	break;
	            case R.styleable.KeyboardBaseView_keyHintTextSize:
	            	keyHintTextSize = a.getDimensionPixelSize(attr, defKeyHintTextSize);
	            	break;
	            case R.styleable.KeyboardBaseView_keyTrailTextSize:
	            	keyTrailTextSize = a.getDimensionPixelSize(attr, defTrailTextSize);
	            	break;
	            case R.styleable.KeyboardBaseView_keyTextColor:
	            	keyTextColor = a.getColor(attr, defKeyTextColor);
	            	break;
	            case R.styleable.KeyboardBaseView_keyModifierColor:
	            	keyModifierColor = a.getColor(attr, defKeyModifierColor);
	            	break;
	            case R.styleable.KeyboardBaseView_keyHintColor:
	            	keyHintColor = a.getColor(attr, defKeyHintColor);
	            	break;
	            case R.styleable.KeyboardBaseView_keyTrailColor:
	            	keyTrailColor = a.getColor(attr, defTrailTextColor);
	            	break;
	            case R.styleable.KeyboardBaseView_languagebarTextColor:
	            	languagebarTextColor = a.getColor(attr, defLanguagebarTextColor);
	            	break;
	            case R.styleable.KeyboardBaseView_languagebarShadowColor:
	            	languagebarShadowColor = a.getColor(attr, defLanguagebarShadowColor);
	            	break;
	            case R.styleable.KeyboardBaseView_spaceAutoCompletionIndicator:
	            	spaceAutoCompletionIndicator = a.getDrawable(attr);
	            	break;
	            case R.styleable.KeyboardBaseView_popupLayout:
	            	popupLayout = a.getResourceId(attr, 0);
	            	break;
	            case R.styleable.KeyboardBaseView_shadowColor:
	            	shadowColor = a.getColor(attr, 0);
	            	break;
	            case R.styleable.KeyboardBaseView_shadowRadius:
	            	shadowRadius = a.getFloat(attr, 0f);
	            	break;
	            case R.styleable.KeyboardBaseView_backgroundDimAmount:
	            	backgroundDimAmount = a.getFloat(attr, 0.5f);
	            	break;
	            case R.styleable.KeyboardBaseView_keyHintPopup:
	            	keyHintPopup = a.getDrawable(attr);
	            	break;
	            	
	            // candidate styles
	            case R.styleable.KeyboardBaseView_candidateBackground:
	            	candidateBackground = a.getDrawable(attr);
	            	break;
	            case R.styleable.KeyboardBaseView_candidateSelection:
	            	candidateSelection = a.getDrawable(attr);
	            	break;
	            case R.styleable.KeyboardBaseView_candidateDivider:
	            	candidateDivider = a.getDrawable(attr);
	            	break;
	            case R.styleable.KeyboardBaseView_candidateTextColorNormal:
	            	candidateTextColorNormal = a.getColor(attr, 0);
	            	break;
	            case R.styleable.KeyboardBaseView_candidateTextColorRecommended:
	            	candidateTextColorRecommended = a.getColor(attr, 0);
	            	break;
	            case R.styleable.KeyboardBaseView_candidateTextColorOther:
	            	candidateTextColorOther = a.getColor(attr, 0);
	            	break;
	            	
	            // Hint Padding
	            case R.styleable.KeyboardBaseView_numHintTopPadding:
	            	numHintTopPadding = a.getDimensionPixelSize(attr, defNumHintTopPadding);
	            	break;
	            case R.styleable.KeyboardBaseView_numHintRightPadding:
	            	numHintRightPadding = a.getDimensionPixelSize(attr, defNumHintRightPadding);
	            	break;
	            case R.styleable.KeyboardBaseView_hintPopupBottomPadding:
	            	hintPopupBottomPadding = a.getDimensionPixelSize(attr, defHintPopupBottomPadding);
	            	break;
	            case R.styleable.KeyboardBaseView_hintPopupRightPadding:
	            	hintPopupRightPadding = a.getDimensionPixelSize(attr, defHintPopupRightPadding);
	            	break;
	            }
	        }
	        
	        createIcons(context);
		}
		
		private void createIcons(Context context) {
	        if(spaceKeyIcon == null) {
				final Bitmap bitmapSpace = createSpaceKeyIcon(context, this.keyTextColor);
				if(bitmapSpace != null) {
					spaceKeyIcon = new BitmapDrawable(bitmapSpace);
					spaceKeyIcon.setBounds(0, 0, bitmapSpace.getWidth(), bitmapSpace.getHeight());
				}
	        }
	        if(spaceKeyIconModifier == null) {
				final Bitmap bitmapSpace = createSpaceKeyIcon(context, this.keyModifierColor);
				if(bitmapSpace != null) {
					spaceKeyIconModifier = new BitmapDrawable(bitmapSpace);
					spaceKeyIconModifier.setBounds(0, 0, bitmapSpace.getWidth(), bitmapSpace.getHeight());
				}
	        }
		}
	}
}
