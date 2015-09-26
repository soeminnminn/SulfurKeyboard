package com.s16.inputmethod.emoji;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.s16.inputmethod.skeyboard.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public enum EmojiCategory {
	
	RECENTS(0),
	FACES(1),
	OBJECTS(2),
	NATURE(3),
	PLACES(4),
	SYMBOLS(5),
	EMOTICONS(6);
	
	static final int[] sResourceArray = new int[] {
		0, R.array.emoji_faces, R.array.emoji_objects,
		R.array.emoji_nature, R.array.emoji_places, 
		R.array.emoji_symbols, R.array.emoji_emoticons
	};
	
	static final int[] sIconsArray = new int[] {
		0xe01e, 0xe017, 0xe01c, 0xe01b, 0xe01d, 0xe01f, 0xe01a
	};
	
	private static final int RECENTS_MAX_COUNT = 20;
	private static final String KEY_RECENTS = "emoji_recents";
	private static boolean S_RecentsUpdated = true;
	
	private final int mIndex;
	private String[] mCodesArray;
    
    EmojiCategory(int index) {
    	mIndex = index;
    }
    
    int getIndex() {
    	return mIndex;
    }
    
    public void init(Context context) {
    	if (mIndex > 0 && mCodesArray == null) {
    		int resId = sResourceArray[mIndex];
    		if (resId != 0) {
    			String[] resArray = context.getResources().getStringArray(resId);
    			List<String> list = new ArrayList<String>();
    			for(String codesArraySpec : resArray) {
    				if (codesArraySpec.matches("^[0-9a-fA-F]+\\|[0-9a-fA-F]+\\,[0-9a-fA-F]+\\|[0-9]+$") 
    						|| codesArraySpec.matches("^[0-9a-fA-F]+\\|\\|[0-9]+$")) {
    					int supportedMinSdkVersion = CodesArrayParser.getMinSupportSdkVersion(codesArraySpec);
    					if (Build.VERSION.SDK_INT >= supportedMinSdkVersion) {
    						list.add(codesArraySpec);
    					}
    					
    				} else {
    					list.add(codesArraySpec);
    				}
    			}
    			mCodesArray = new String[list.size()];
    			mCodesArray = list.toArray(mCodesArray);
    		}
    	}
    }
    
    private String[] getRecentsArray(Context context) {
    	if (S_RecentsUpdated) {
	    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
	    	String recentStr = preferences.getString(KEY_RECENTS, "");
	    	JSONArray jsonArray = null;
			if (!TextUtils.isEmpty(recentStr)) {
				try {
					jsonArray = new JSONArray(recentStr);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			if (jsonArray != null) {
				mCodesArray = new String[jsonArray.length()];
				for(int i=0; i<jsonArray.length(); i++) {
					String codesArraySpec = null;
					try {
						codesArraySpec = jsonArray.getString(i);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					mCodesArray[i] = codesArraySpec;
				}
			}
			
			S_RecentsUpdated = false;
    	}
		return mCodesArray;
    }
    
    public void updateKey(Context context, EmojiIconKey key) {
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String recentStr = preferences.getString(KEY_RECENTS, "");
		JSONArray jsonArray = null;
		if (!TextUtils.isEmpty(recentStr)) {
			try {
				jsonArray = new JSONArray(recentStr);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (jsonArray == null) {
			jsonArray = new JSONArray();
		}
		
		boolean found = false;
		int len = jsonArray.length();
		for(int i=0; i<len; i++) {
			String item = null;
			try {
				item = jsonArray.getString(i);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (item != null && item.equals(key.codesArraySpec)) {
				found = true;
				break;
			}
		}
		if (!found) {
			JSONArray result = new JSONArray();
			result.put(key.codesArraySpec == null ? null : key.codesArraySpec.toString());
			for(int i=0; i<len; i++) {
				if ((i + 1) < RECENTS_MAX_COUNT) {
					try {
						result.put(jsonArray.getString(i));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString(KEY_RECENTS, result.toString());
			editor.commit();
			S_RecentsUpdated = true;
		}
    }
    
    public int getIconCode() {
    	return sIconsArray[mIndex];
    }
    
    public int getCount(Context context) {
		if (mIndex == 0) {
			String[] recentsArray = getRecentsArray(context);
			if (recentsArray != null) {
				return recentsArray.length;
			}
		} else if (mCodesArray != null) {
			return mCodesArray.length;
		}
		return 0;
	}
    
    public String getItemText(Context context, int position) {
    	String result = "";
		if (mIndex == 0) {
			String[] recentsArray = getRecentsArray(context);
			if (recentsArray != null) {
				result = CodesArrayParser.parseOutputText(recentsArray[position]);
			}
		} else if (mCodesArray != null) {
			String codesArraySpec = mCodesArray[position];
			result = CodesArrayParser.parseOutputText(codesArraySpec);
		}
		return result;
    }
	
    public EmojiIconKey getItem(Context context, int position) {
		String codesArraySpec = null;
		if (mIndex == 0) {
			String[] recentsArray = getRecentsArray(context);
			if (recentsArray != null) {
				codesArraySpec = recentsArray[position];
			}
		} else if (mCodesArray != null) {
			codesArraySpec = mCodesArray[position];
		}
		
		return EmojiIconKey.from(context, codesArraySpec);
	}
}
