package com.android.inputmethod.skeyboard;

import android.content.Context;
import android.content.res.Resources;
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

public class KeyboardTheme {
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
	public static final int ICON_EMOJI = 12;
	public static final int ICON_HINT_POPUP = 13;
	private static final int ICON_LAST = ICON_HINT_POPUP; 
	
	private static int[] mIconValues;
	static {
		mIconValues = new int[ICON_LAST + 1];
		mIconValues[ICON_UNDEFINED] = ICON_LAST;
		mIconValues[ICON_MIC_KEY] = 0xF022;
		mIconValues[ICON_SETTINGS_KEY] = 0xE013;
		mIconValues[ICON_LANGUAGE_KEY] = 0xE073;
		mIconValues[ICON_SHIFT_KEY] = 0xF026;
		mIconValues[ICON_SHIFTLOCKED_KEY] = 0xF027;
		mIconValues[ICON_SPACE_KEY] = 0xF024;
		mIconValues[ICON_TAB_KEY] = 0xF029;
		mIconValues[ICON_RETURN_KEY] = 0xF028;
		mIconValues[ICON_SEARCH_KEY] = 0xE012;
		mIconValues[ICON_DELETE_KEY] = 0xE041;
		mIconValues[ICON_DONE_KEY] = 0xE018;
		mIconValues[ICON_EMOJI] = 0xE017;
		mIconValues[ICON_HINT_POPUP] = 0x2026;
	}
	
	public static final int THEME_BASIC = 0;
	public static final int THEME_STONE = 1;
	public static final int THEME_WHITE = 2;
	public static final int THEME_IPHONE = 3;
	public static final int THEME_GINGERBREAD = 4;
	public static final int THEME_HOLO = 5;
	public static final int THEME_KITKLP = 6;
	public static final int THEME_GALAXY = 7;
	public static final int THEME_PINK = 8;
	private static int[] mThemesResIdArray = new int[] {
		R.style.KeyboardBaseView_basic,
		R.style.KeyboardBaseView_stone,
		R.style.KeyboardBaseView_white, 
		R.style.KeyboardBaseView_iphone, 
		R.style.KeyboardBaseView_gingerbread, 
		R.style.KeyboardBaseView_holo,
		R.style.KeyboardBaseView_kitkat,
		R.style.KeyboardBaseView_galaxy, 
		R.style.KeyboardBaseView_pink
	};
	
	public static class ThemedIconsParams {
		public Drawable keyHintPopup;
		public Drawable spaceKeyIcon;
		public Drawable spaceKeyIconModifier;
		public Drawable spaceAutoCompletionIndicator;
	}
	
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
	
	public static int getThemeResId(int themeId) {
		return mThemesResIdArray[themeId];
	}
	
	public static BitmapDrawable createSpaceKeyIconDrawable(Context context, final int color) {
		final Bitmap bitmapSpace = createSpaceKeyIcon(context, color);
		if(bitmapSpace != null) {
			BitmapDrawable spaceKeyIconModifier = new BitmapDrawable(context.getResources(), bitmapSpace);
			spaceKeyIconModifier.setBounds(0, 0, bitmapSpace.getWidth(), bitmapSpace.getHeight());
			return spaceKeyIconModifier;
		}
		return null;
	}
	
	public static Bitmap createSpaceKeyIcon(Context context, final int color) {
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
}
