package com.s16.inputmethod.emoji;

import com.s16.inputmethod.skeyboard.R;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EmojiIconsTabBar extends LinearLayout {

	private ViewPager mPager;
	private ViewPager.OnPageChangeListener mDelegatePageListener;
	private int mSelectedIndex;
	private int mTextColor;
	private int mSelectedTextColor;
	private Typeface mTabTypeface = Typeface.DEFAULT;
	private Typeface mTabSelectedTypeface = Typeface.DEFAULT_BOLD;
	
	public interface IconTabProvider {
		public int getPageIcon(int position);
	}
	
	private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int position) {
			mSelectedIndex = position;
			onSelectedIndexChanged();
			if (mDelegatePageListener != null) {
				mDelegatePageListener.onPageSelected(position);
			}
		}
		
		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			if (mDelegatePageListener != null) {
				mDelegatePageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
			}
		}
		
		@Override
		public void onPageScrollStateChanged(int state) {
			if (mDelegatePageListener != null) {
				mDelegatePageListener.onPageScrollStateChanged(state);
			}
		}
	};
	
	private View.OnClickListener mItemClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			setSelectedItem(v);
		}
	};
	
	public EmojiIconsTabBar(Context context) {
        super(context);
        init(context);
    }

    public EmojiIconsTabBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EmojiIconsTabBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }
    
    private void init(Context context) {
    	if (isInEditMode()) {
    		return;
    	}
    	mTextColor = context.getResources().getColor(R.color.dim_foreground_holo_dark);
    	mSelectedTextColor = context.getResources().getColor(R.color.bright_foreground_holo_dark);
    }
    
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    	super.onLayout(changed, l, t, r, b);
    	updateIcons();
    }
    
    protected void updateIcons() {
    	if (mPager != null && mPager.getAdapter() != null && 
    			mPager.getAdapter() instanceof IconTabProvider) {
    		
    		IconTabProvider iconTabProvider = (IconTabProvider)mPager.getAdapter();
	    	for(int i=0; i<getChildCount(); i++) {
	    		if (getChildAt(i) instanceof TextView) {
	    			TextView child = (TextView)getChildAt(i);
	    			int codePoint = iconTabProvider.getPageIcon(i);
	    			String text = child.getText() == null ? "" : child.getText().toString();
	    			if (Character.charCount(codePoint) == 1) {
	    				text = String.valueOf((char)codePoint);
	    	        } else {
	    	        	text = new String(Character.toChars(codePoint));
	    	        }
	    			child.setText(text);
	    		}
	    	}
    	}
    }
    
    protected void onSelectedIndexChanged() {
    	
    	for(int i=0; i<getChildCount(); i++) {
    		getChildAt(i).setSelected((i == mSelectedIndex));
    		
    		if (getChildAt(i) instanceof TextView) {
    			TextView child = (TextView)getChildAt(i);
    			if (i != mSelectedIndex) {
    				child.setTextColor(mTextColor);
    				child.setTypeface(mTabTypeface);
	    		} else {
	    			child.setTextColor(mSelectedTextColor);
    				child.setTypeface(mTabSelectedTypeface);
				}
    			child.invalidate();
			}
		}
    	
    	if (mPager != null && mPager.getCurrentItem() != mSelectedIndex) {
    		mPager.setCurrentItem(mSelectedIndex);
    	}
    }
    
    protected View getSelectChild() {
    	int childCount = getChildCount();
    	if (childCount > 0 && mSelectedIndex > -1 && mSelectedIndex < childCount) {
    		View child = getChildAt(mSelectedIndex);
    		return child;
    	}
    	return null;
    }
    
    private void setSelectedItem(View v) {
    	if (v != null) {
    		for(int i=0; i<getChildCount(); i++) {
    			if (getChildAt(i).equals(v)) {
    				setSelectedIndex(i);
    				break;
    			}
    		}
    	}
    }
    
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
		mDelegatePageListener = listener;
	}
    
    public void setSelectedIndex(int index) {
    	mSelectedIndex = index;
    	onSelectedIndexChanged();
    }
    
    public int getSelectedIndex() {
    	return mSelectedIndex;
    }
    
    public void setTabTypeface(Typeface typeface) {
    	mTabTypeface = typeface;
    }
    
    public void setTabSelectedTypeface(Typeface typeface) {
    	mTabSelectedTypeface = typeface;
    }
    
    public void setViewPager(ViewPager pager) {
    	mPager = pager;
    	if (pager.getAdapter() == null) {
			throw new IllegalStateException("ViewPager does not have adapter instance.");
		}
    	mPager.setOnPageChangeListener(mPageChangeListener);
    	notifyDataSetChanged();
    }
    
    public void notifyDataSetChanged() {
    	int childCount = getChildCount();
    	if (childCount > 0) {
    		for(int i=0; i<childCount; i++) {
    			getChildAt(i).setOnClickListener(mItemClickListener);
    		}
    	}
    }
}
 