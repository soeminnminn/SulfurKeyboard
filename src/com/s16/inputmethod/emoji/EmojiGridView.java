package com.s16.inputmethod.emoji;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class EmojiGridView extends GridView {
	
	public EmojiGridView(Context context) {
        super(context);
        init(context);
    }

    public EmojiGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EmojiGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }
    
    private void init(Context context) {
    	
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
