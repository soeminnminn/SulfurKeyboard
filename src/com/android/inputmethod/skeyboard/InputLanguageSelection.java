/*
 * Copyright (C) 2008-2009 Google Inc.
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

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import com.android.inputmethod.skeyboard.R;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class InputLanguageSelection extends PreferenceActivity {

    private String mSelectedLanguages;
    private ArrayList<Loc> mAvailableLanguages = new ArrayList<Loc>();
    
    public static final String DEFAULT_SELECTED_LANGUAGES = "en_US,my_MM,";
    
    private static final String[] BLACKLIST_LANGUAGES = {
        "ko", "ja", "zh", "el"
    };
	
	private static final String[] MUST_BE_ADD_LANGUAGES = {
        "en_US", "my_MM"
    };
    
    // Languages for which auto-caps should be disabled
    public static final Set<String> NOCAPS_LANGUAGES = new HashSet<String>();
    static {
        NOCAPS_LANGUAGES.add("ar");
        NOCAPS_LANGUAGES.add("iw");
        NOCAPS_LANGUAGES.add("th");
		NOCAPS_LANGUAGES.add("my");
    }

    // Languages which should not use dead key logic. The modifier is entered after the base character.
    public static final Set<String> NODEADKEY_LANGUAGES = new HashSet<String>();
    static {
        NODEADKEY_LANGUAGES.add("ar");
        NODEADKEY_LANGUAGES.add("iw"); // TODO: currently no niqqud in the keymap?
        NODEADKEY_LANGUAGES.add("th");
    }

    // Languages which should not auto-add space after completions
    public static final Set<String> NOAUTOSPACE_LANGUAGES = new HashSet<String>();
    static {
        NOAUTOSPACE_LANGUAGES.add("th");
		NOAUTOSPACE_LANGUAGES.add("my");
    }

    private static class Loc implements Comparable<Object> {
        static Collator sCollator = Collator.getInstance();

        String label;
        Locale locale;

        public Loc(String label, Locale locale) {
            this.label = label;
            this.locale = locale;
        }

        @Override
        public String toString() {
            return this.label;
        }

        public int compareTo(Object o) {
            return sCollator.compare(this.label, ((Loc) o).label);
        }
    }

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.language_prefs);
        // Get the settings preferences
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        mSelectedLanguages = sp.getString(LatinIME.PREF_SELECTED_LANGUAGES, DEFAULT_SELECTED_LANGUAGES);
        String[] languageList = mSelectedLanguages.split(",");
        mAvailableLanguages = getUniqueLocales();
        PreferenceGroup parent = getPreferenceScreen();
		// SMM {
		ArrayList<String> mustBeAddLocale = new ArrayList<String>();
		for (int i = 0; i < MUST_BE_ADD_LANGUAGES.length; i++) {
			mustBeAddLocale.add(MUST_BE_ADD_LANGUAGES[i]);
		}
		// } SMM
        for (int i = 0; i < mAvailableLanguages.size(); i++) {
            final CheckBoxPreference pref = new CheckBoxPreference(this);
            final Locale locale = mAvailableLanguages.get(i).locale;
			// SMM { 
			final String localeCode = get5Code(locale);
			if (arrayContains(MUST_BE_ADD_LANGUAGES, localeCode)) {
				mustBeAddLocale.remove(localeCode);
			}
			pref.setTitle(getLocaleName(locale)); 
			// } SMM
            //pref.setTitle(LanguageSwitcher.toTitleCase(locale.getDisplayName(locale)));
            final boolean checked = isLocaleIn(locale, languageList);
            pref.setChecked(checked);
            if (hasDictionary(locale)) {
                pref.setSummary(R.string.has_dictionary);
            }
            parent.addPreference(pref);
        }
		
		// SMM {
		if (mustBeAddLocale.size() > 0) {
			for (int i = 0; i < mustBeAddLocale.size(); i++) {
				final String localeCode = mustBeAddLocale.get(i);
				String[] partList = localeCode.split("_");
				Locale locale = null;
				if (partList.length == 2) {
					locale = new Locale(partList[0], partList[1]);
				} else if (partList.length == 3) {
					locale = new Locale(partList[0], partList[1], partList[3]);
				} else {
					locale = new Locale(partList[0]);
				}
				
				if (locale != null) {
					final CheckBoxPreference pref = new CheckBoxPreference(this);
					pref.setTitle(getLocaleName(locale));
					final boolean checked = isLocaleIn(locale, languageList);
					pref.setChecked(checked);
					if (hasDictionary(locale)) {
						pref.setSummary(R.string.has_dictionary);
					}
					parent.addPreference(pref);
				}
			}
		}
		// } SMM
    }

    private boolean isLocaleIn(Locale locale, String[] list) {
        String lang = get5Code(locale);
        for (int i = 0; i < list.length; i++) {
            if (lang.equalsIgnoreCase(list[i])) return true;
        }
        return false;
    }

    private boolean hasDictionary(Locale locale) {
        Resources res = getResources();
        Configuration conf = res.getConfiguration();
        Locale saveLocale = conf.locale;
        boolean haveDictionary = false;
        conf.locale = locale;
        res.updateConfiguration(conf, res.getDisplayMetrics());

        int[] dictionaries = LatinIME.getDictionary(res);
        BinaryDictionary bd = new BinaryDictionary(this, dictionaries, Suggest.DIC_MAIN);

        // Is the dictionary larger than a placeholder? Arbitrarily chose a lower limit of
        // 4000-5000 words, whereas the LARGE_DICTIONARY is about 20000+ words.
        if (bd.getSize() > Suggest.LARGE_DICTIONARY_THRESHOLD / 4) {
            haveDictionary = true;
        }
        bd.close();
        conf.locale = saveLocale;
        res.updateConfiguration(conf, res.getDisplayMetrics());
        return haveDictionary;
    }

    private String get5Code(Locale locale) {
        String country = locale.getCountry();
        return locale.getLanguage()
                + (TextUtils.isEmpty(country) ? "" : "_" + country);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save the selected languages
        String checkedLanguages = "";
        PreferenceGroup parent = getPreferenceScreen();
        int count = parent.getPreferenceCount();
        for (int i = 0; i < count; i++) {
            CheckBoxPreference pref = (CheckBoxPreference) parent.getPreference(i);
            if (pref.isChecked()) {
                Locale locale = mAvailableLanguages.get(i).locale;
                checkedLanguages += get5Code(locale) + ",";
            }
        }
        if (checkedLanguages.length() < 1) checkedLanguages = null; // Save null
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        Editor editor = sp.edit();
        editor.putString(LatinIME.PREF_SELECTED_LANGUAGES, checkedLanguages);
        SharedPreferencesCompat.apply(editor);
    }
    
    private static String getLocaleName(Locale l) {
        String lang = l.getLanguage();
        String country = l.getCountry();
        if (lang.equals("en") && country.equals("DV")) {
            return "English (Dvorak)";
        } else if (lang.equals("en") && country.equals("EX")) {
                return "English (4x11)";
        } else if (lang.equals("es") && country.equals("LA")) {
            return "Español (Latinoamérica)";
        } else if (lang.equals("cs") && country.equals("QY")) {
            return "Čeština (QWERTY)";
        } else if (lang.equals("sk") && country.equals("QY")) {
            return "Slovenčina (QWERTY)";
        } else if (lang.equals("ru") && country.equals("PH")) {
            return "Русский (Phonetic)";
        } else if (lang.equals("my") && country.equals("MM")) {
            return "Myanmar (Zawgyi-one)";
        } else {
            return LanguageSwitcher.toTitleCase(l.getDisplayName(l));
        }
    }

    ArrayList<Loc> getUniqueLocales() {
        String[] locales = getAssets().getLocales();
        Arrays.sort(locales);
        ArrayList<Loc> uniqueLocales = new ArrayList<Loc>();

        final int origSize = locales.length;
        Loc[] preprocess = new Loc[origSize];
        int finalSize = 0;
        for (int i = 0 ; i < origSize; i++ ) {
            String s = locales[i];
            int len = s.length();
            if (len == 5) {
                String language = s.substring(0, 2);
                String country = s.substring(3, 5);
                Locale l = new Locale(language, country);

                // Exclude languages that are not relevant to LatinIME
                if (arrayContains(BLACKLIST_LANGUAGES, language)) continue;

                if (finalSize == 0) {
                    preprocess[finalSize++] =
                            new Loc(LanguageSwitcher.toTitleCase(l.getDisplayName(l)), l);
                } else {
                    // check previous entry:
                    //  same lang and a country -> upgrade to full name and
                    //    insert ours with full name
                    //  diff lang -> insert ours with lang-only name
                    if (preprocess[finalSize-1].locale.getLanguage().equals(language)) {
                    	preprocess[finalSize-1].label = getLocaleName(preprocess[finalSize-1].locale);
                        preprocess[finalSize++] = new Loc(getLocaleName(l), l);
                    } else {
                        String displayName;
                        if (s.equals("zz_ZZ")) {
                        } else {
                        	displayName = getLocaleName(l);
                            preprocess[finalSize++] = new Loc(displayName, l);
                        }
                    }
                }
            }
        }
        for (int i = 0; i < finalSize ; i++) {
            uniqueLocales.add(preprocess[i]);
        }
        return uniqueLocales;
    }

    private boolean arrayContains(String[] array, String value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equalsIgnoreCase(value)) return true;
        }
        return false;
    }
}
