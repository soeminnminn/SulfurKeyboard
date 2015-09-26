package com.s16.inputmethod.emoji;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import com.s16.inputmethod.skeyboard.KeyboardTheme;

public class EmojiIconTextView extends TextView {
	
	protected static class TypefaceSpan extends MetricAffectingSpan  {
	    private Typeface mTypeface;
	    
	    public TypefaceSpan(Context context, Typeface typeface) {
	    	mTypeface = typeface;
	    }

	    @Override
	    public void updateMeasureState(TextPaint p) {
	        p.setTypeface(mTypeface);
	        p.setFlags(p.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
	    }

	    @Override
	    public void updateDrawState(TextPaint tp) {
	        tp.setTypeface(mTypeface);
	        tp.setFlags(tp.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
	    }
	}
	
	private Typeface mEmojiTypeface = null;
	private EmojiIconKey mIconKey;
	
	public EmojiIconTextView(Context context) {
        super(context);
        init(context);
    }

    public EmojiIconTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EmojiIconTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }
    
    private void init(Context context) {
    	if (android.os.Build.VERSION.SDK_INT < 14) {
    		setTypeface(KeyboardTheme.getEmojiTypeFace(context));
    	}
    }
    
    public void setIconKey(EmojiIconKey key) {
    	mIconKey = key;
    	if (mIconKey != null && !mIconKey.isEmpty()) {
    		setText(mIconKey.label);
    	}
    }
    
    public EmojiIconKey getIconKey() {
    	return mIconKey;
    }
    
    @Override
    public void setText(CharSequence text, BufferType type) {
    	if (text != null && !text.equals("") && mEmojiTypeface != null && 
    			android.os.Build.VERSION.SDK_INT < 14) {
    		SpannableStringBuilder builder = new SpannableStringBuilder(text);
    		builder.setSpan(new TypefaceSpan(getContext(), mEmojiTypeface), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            super.setText(builder, type);
            
    	} else {
    		super.setText(text, type);
    	}
    }
    
    public void setEmojiTypeface(Typeface typeface) {
    	mEmojiTypeface = typeface;
    }
}
