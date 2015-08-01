package com.android.inputmethod.emoji;

import com.android.inputmethod.emoji.EmojiCategory;
import com.android.inputmethod.skeyboard.KeyboardBaseView;
import com.android.inputmethod.skeyboard.KeyboardSwitcher;
import com.android.inputmethod.skeyboard.KeyboardTheme;
import com.android.inputmethod.skeyboard.R;
import com.android.inputmethod.skeyboard.SoftKeyboard;
import com.android.inputmethod.skeyboard.SoftKeyboardView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

public class EmojiInput {

	private KeyboardBaseView.OnKeyboardActionListener mKeyboardActionListener = 
		new KeyboardBaseView.OnKeyboardActionListener() {
			
			@Override
			public void swipeUp() { }
			
			@Override
			public void swipeRight() { }
			
			@Override
			public void swipeLeft() { }
			
			@Override
			public void swipeDown() { }
			
			@Override
			public void onText(CharSequence text) { }
			
			@Override
			public void onRelease(int primaryCode) { }
			
			@Override
			public void onPress(int primaryCode) { }
			
			@Override
			public void onKey(int primaryCode, int[] keyCodes, int x, int y) { }
			
			@Override
			public void onCancel() { }
		};
		
	private View.OnClickListener mEmojiClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Object tag = v.getTag();
			if (tag != null && mKeyboardActionListener != null) {
				EmojiIconKey key = (EmojiIconKey)tag;
				onKey(key);
			}
		}
	};
	
	private class EmojisPagerAdapter extends PagerAdapter 
			implements EmojiIconsTabBar.IconTabProvider {

		private EmojiIconsAdapter[] mIconsAdapterList;
		
		public EmojisPagerAdapter() {
			mIconsAdapterList = new EmojiIconsAdapter[EmojiCategory.values().length];
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			EmojiCategory category = EmojiCategory.values()[position];
			
			LayoutInflater inflater = LayoutInflater.from(getContext());
			EmojiGridView gridView = (EmojiGridView)inflater.inflate(R.layout.emoji_icon_grid, container, false); 
			
			EmojiIconsAdapter iconAdapter = mIconsAdapterList[category.getIndex()]; 
			if (iconAdapter == null) {
				iconAdapter = new EmojiIconsAdapter(category);
				mIconsAdapterList[category.getIndex()] = iconAdapter;
			}
			gridView.setAdapter(iconAdapter);
			
			((ViewPager)container).addView(gridView, 0);
			return gridView;
		}
		
		@Override
		public int getCount() {
			return EmojiCategory.values().length;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			if (object instanceof View) {
				return (View)object == view;
			}
			return false;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object view) {
			((ViewPager)container).removeView((View)view);
		}
		
		public EmojiIconsAdapter getIconsAdapter(int position) {
			if (position > -1 && mIconsAdapterList.length > position) {
				return mIconsAdapterList[position];
			}
			return null;
		}

		@Override
		public int getPageIcon(int position) {
			return EmojiCategory.values()[position].getIconCode();
		}
	}
	
	private class EmojiIconsAdapter extends BaseAdapter {

		private final EmojiCategory mCategory;
		
		EmojiIconsAdapter(EmojiCategory category) {
			mCategory = category;
		}
		
		@Override
		public int getCount() {
			return mCategory.getCount(getContext());
		}

		@Override
		public Object getItem(int position) {
			return mCategory.getItem(getContext(), position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			EmojiIconKey key = (EmojiIconKey)getItem(position);
			EmojiIconTextView textView = (EmojiIconTextView)convertView;
			if (textView == null) {
				textView = new EmojiIconTextView(getContext());
				textView.setLayoutParams(new ViewGroup.LayoutParams(mIconSize, mIconSize));
				textView.setGravity(Gravity.CENTER);
				textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.emoji_icon_text_size));
				textView.setTextColor(getResources().getColor(R.color.bright_foreground_holo_dark));
				textView.setBackgroundResource(R.drawable.emoji_button);
				textView.setEmojiTypeface(KeyboardTheme.getEmojiTypeFace(getContext()));
				textView.setOnClickListener(mEmojiClick);
			}
			textView.setText(key.label);
			textView.setTag(key);
			
			return textView;
		}
		
	}
	
	private Context mContext;
	private KeyboardSwitcher mKeyboardSwitcher;
	private ViewGroup mEmojiInputView;
	private FrameLayout mEmojiKeyboardView;
	private SoftKeyboard mBottomKeyboard;
	private SoftKeyboardView mBottomKeyboardView;
	private EmojisPagerAdapter mPagerAdapter;
	
	private int mIconSize;
	
	public EmojiInput(Context context, KeyboardSwitcher keyboardSwitcher) {
		mContext = context;
		mKeyboardSwitcher = keyboardSwitcher;
		mIconSize = (int)context.getResources().getDimension(R.dimen.emoji_icon_size);
		
		newView();
	}
	
	protected Context getContext() {
		return mContext;
	}
	
	protected Resources getResources() {
		return mContext.getResources();
	}
	
	@SuppressLint("InflateParams")
	public void newView() {
		LayoutInflater inflater = LayoutInflater.from(getContext());
		mEmojiInputView = (ViewGroup)inflater.inflate(R.layout.emoji_icons_view, null, false);
		
		mEmojiKeyboardView = new FrameLayout(getContext()); 
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		mEmojiKeyboardView.addView(mEmojiInputView, params);
		
		mBottomKeyboardView = (SoftKeyboardView)mEmojiInputView.findViewById(R.id.emojiBottomKeyboard);
		mBottomKeyboard = new SoftKeyboard(getContext(), R.xml.kbd_switch_bottom, KeyboardSwitcher.KEYBOARDMODE_SYMBOLS);
		mBottomKeyboardView.setKeyboard(mBottomKeyboard);
		mBottomKeyboardView.setOnKeyboardActionListener(mKeyboardActionListener);
		
		EmojiIconsTabBar tabBar = (EmojiIconsTabBar)mEmojiInputView.findViewById(R.id.emojis_tab);
		tabBar.setTabTypeface(KeyboardTheme.getZawgyiTypeFace(getContext()));
		tabBar.setTabSelectedTypeface(KeyboardTheme.getZawgyiTypeFace(getContext()));
		
		ViewPager viewPager = (ViewPager)mEmojiInputView.findViewById(R.id.emojis_pager);
		mPagerAdapter = new EmojisPagerAdapter();
		viewPager.setAdapter(mPagerAdapter);
		tabBar.setViewPager(viewPager);
		tabBar.setSelectedIndex(0);
	}
	
	private void updateKeyboardView() {
		if (mKeyboardSwitcher != null) {
			final SoftKeyboardView keyboardView = mKeyboardSwitcher.getInputView();
			if (keyboardView != null) {
				int viewHeight = keyboardView.getMeasuredHeight();
				if (viewHeight > 0) {
					FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, viewHeight);
					mEmojiKeyboardView.removeAllViews();
					mEmojiKeyboardView.addView(mEmojiInputView, params);
				}
				if (mBottomKeyboard != null) {
					mBottomKeyboard.setLanguageSwitcher(mKeyboardSwitcher.getLanguageSwitcher(), 
							mKeyboardSwitcher.isAutoCompletionActive(), keyboardView.getLanguagebarTextColor(), 
							keyboardView.getLanguagebarShadowColor(), -1);
				}
			}
			
			if (mBottomKeyboardView != null) {
				mBottomKeyboardView.setStyle(mKeyboardSwitcher.getThemedContext(), mKeyboardSwitcher.getThemeResId());
			}
		}
	}
	
	private void onKey(EmojiIconKey key) {
		if (key == null) return;
		if (mKeyboardActionListener != null) {
			if (key.outputText != null) {
				mKeyboardActionListener.onText(key.outputText);
			} else {
				mKeyboardActionListener.onText(key.label);
			}
		}
		
		EmojiCategory.RECENTS.updateKey(getContext(), key);
		EmojiIconsAdapter recentAdapter = mPagerAdapter.getIconsAdapter(EmojiCategory.RECENTS.getIndex());
		if (recentAdapter != null) {
			recentAdapter.notifyDataSetChanged();
		}
	}
	
	public void setOnKeyboardActionListener(KeyboardBaseView.OnKeyboardActionListener actionListener) {
		mKeyboardActionListener = actionListener;
		if (mBottomKeyboardView != null) {
			mBottomKeyboardView.setOnKeyboardActionListener(mKeyboardActionListener);
		}
	}
	
	public View getView() {
		updateKeyboardView();
		return mEmojiKeyboardView;
	}
	
	public void setKeyboardSwitcher(KeyboardSwitcher keyboardSwitcher) {
		mKeyboardSwitcher = keyboardSwitcher;
		updateKeyboardView();
	}
	
	public void onConfigurationChanged() {
		
	}
}
