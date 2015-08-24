/*
 * Copyright (C) 2011 Darren Salt
 *
 * Licensed under the Apache License, Version 2.0 (the "Licence"); you may
 * not use this file except in compliance with the Licence. You may obtain
 * a copy of the Licence at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * Licence for the specific language governing permissions and limitations
 * under the Licence.
 */

package com.s16.inputmethod.skeyboard;

import java.util.HashMap;
import java.util.Map;

public class DeadAccentSequence {
    @SuppressWarnings("unused")
    private static final String TAG = DeadAccentSequence.class.getSimpleName();
    
    //public final static char DEAD_KEY_PLACEHOLDER = 0x25cc; // dotted small circle
    public final static char DEAD_KEY_PLACEHOLDER = 0x2013;
    public final static char DEAD_KEY_PLACEHOLDER_X2 = 0x2015; 
    public final static String DEAD_KEY_PLACEHOLDER_STRING = Character.toString(DEAD_KEY_PLACEHOLDER);
    
    // 0x2013  0x2015
    
    protected static final Map<String, String> mMap = new HashMap<String, String>();

    public DeadAccentSequence() {
    }
    
    protected static String get(String key) {
        if (key == null || key.length() == 0) {
            return null;
        }
        return mMap.get(key);
    }
    
    protected static void put(String key, String value) {
    	mMap.put(key, value);
    }
    
    private static void putAccent(String nonSpacing, String spacing, String ascii) {
        if (ascii == null) ascii = spacing;
        put("" + nonSpacing + " ", ascii);
        put(nonSpacing + nonSpacing, spacing);
        put(DEAD_KEY_PLACEHOLDER + nonSpacing, spacing);
    }
    
    private static void putMM(String nonSpacing, String spacing) {
        put(nonSpacing, spacing);
        put(DEAD_KEY_PLACEHOLDER + nonSpacing, spacing);
    }
    
    public static boolean isDeadAccent(CharSequence value) {
    	if(value.length() == 2) {
    		return value.charAt(0) == DEAD_KEY_PLACEHOLDER
    				|| value.charAt(0) == DEAD_KEY_PLACEHOLDER_X2
    				|| value.charAt(1) == DEAD_KEY_PLACEHOLDER
    				|| value.charAt(1) == DEAD_KEY_PLACEHOLDER_X2;
    	}
    	String spacing = get("" + DEAD_KEY_PLACEHOLDER + value);
    	return (spacing != null);
    }
    
    public static String getSpacing(char nonSpacing) {
        String spacing = get("" + DEAD_KEY_PLACEHOLDER + nonSpacing);
        if (spacing == null) return "" + nonSpacing;
        return spacing;
    }
    
    static {
        // space + combining diacritical
        // cf. http://unicode.org/charts/PDF/U0300.pdf
        putAccent("\u0300", "\u02cb", "`");  // grave
        putAccent("\u0301", "\u02ca", "Â´");  // acute
        putAccent("\u0302", "\u02c6", "^");  // circumflex
        putAccent("\u0303", "\u02dc", "~");  // small tilde
        putAccent("\u0304", "\u02c9", "Â¯");  // macron
        putAccent("\u0305", "\u00af", "Â¯");  // overline
        putAccent("\u0306", "\u02d8", null);  // breve
        putAccent("\u0307", "\u02d9", null);  // dot above
        putAccent("\u0308", "\u00a8", "Â¨");  // diaeresis
        putAccent("\u0309", "\u02c0", null);  // hook above
        putAccent("\u030a", "\u02da", "Â°");  // ring above
        putAccent("\u030b", "\u02dd", "\"");  // double acute 
        putAccent("\u030c", "\u02c7", null);  // caron
        putAccent("\u030d", "\u02c8", null);  // vertical line above
        putAccent("\u030e", "\"", "\"");  // double vertical line above
        putAccent("\u0313", "\u02bc", null);  // comma above
        putAccent("\u0314", "\u02bd", null);  // reversed comma above

        put("\u0308\u0301\u03b9", "\u0390");  // Greek Dialytika+Tonos, iota
        put("\u0301\u0308\u03b9", "\u0390");  // Greek Dialytika+Tonos, iota
        put("\u0301\u03ca", "\u0390");        // Greek Dialytika+Tonos, iota
        put("\u0308\u0301\u03c5", "\u03b0");  // Greek Dialytika+Tonos, upsilon
        put("\u0301\u0308\u03c5", "\u03b0");  // Greek Dialytika+Tonos, upsilon
        put("\u0301\u03cb", "\u03b0");        // Greek Dialytika+Tonos, upsilon
        
        // For My_MM
        putMM("\u1060", DEAD_KEY_PLACEHOLDER + "\u1060");
        putMM("\u1061", DEAD_KEY_PLACEHOLDER + "\u1061");
        putMM("\u1062", DEAD_KEY_PLACEHOLDER + "\u1062");
        putMM("\u1063", DEAD_KEY_PLACEHOLDER + "\u1063");
        putMM("\u1065", DEAD_KEY_PLACEHOLDER + "\u1065");
        putMM("\u1066", DEAD_KEY_PLACEHOLDER + "\u1066");
        putMM("\u1067", DEAD_KEY_PLACEHOLDER + "\u1067");
        putMM("\u1068", DEAD_KEY_PLACEHOLDER + "\u1068");
        putMM("\u1069", DEAD_KEY_PLACEHOLDER + "\u1069");
        putMM("\u106C", DEAD_KEY_PLACEHOLDER + "\u106C");
        putMM("\u106D", DEAD_KEY_PLACEHOLDER + "\u106D");
        putMM("\u1070", DEAD_KEY_PLACEHOLDER + "\u1070");
        putMM("\u1071", DEAD_KEY_PLACEHOLDER + "\u1071");
        putMM("\u1072", DEAD_KEY_PLACEHOLDER + "\u1072");
        putMM("\u1073", DEAD_KEY_PLACEHOLDER + "\u1073");
        putMM("\u1074", DEAD_KEY_PLACEHOLDER + "\u1074");
        putMM("\u1075", DEAD_KEY_PLACEHOLDER + "\u1075");
        putMM("\u1076", DEAD_KEY_PLACEHOLDER + "\u1076");
        putMM("\u1077", DEAD_KEY_PLACEHOLDER + "\u1077");
        putMM("\u1078", DEAD_KEY_PLACEHOLDER + "\u1078");
        putMM("\u1079", DEAD_KEY_PLACEHOLDER + "\u1079");
        putMM("\u107A", DEAD_KEY_PLACEHOLDER + "\u107A");
        putMM("\u107B", DEAD_KEY_PLACEHOLDER + "\u107B");
        putMM("\u107C", DEAD_KEY_PLACEHOLDER + "\u107C");
        putMM("\u1085", DEAD_KEY_PLACEHOLDER + "\u1085");
        putMM("\u1093", DEAD_KEY_PLACEHOLDER + "\u1093");
        putMM("\u1096", DEAD_KEY_PLACEHOLDER + "\u1096");

        putMM("\u102B", DEAD_KEY_PLACEHOLDER + "\u102B");
        putMM("\u102C", DEAD_KEY_PLACEHOLDER + "\u102C");
        putMM("\u102D", DEAD_KEY_PLACEHOLDER + "\u102D");
        putMM("\u102E", DEAD_KEY_PLACEHOLDER + "\u102E");
        putMM("\u102F", DEAD_KEY_PLACEHOLDER + "\u102F");
        putMM("\u1030", DEAD_KEY_PLACEHOLDER + "\u1030");
        putMM("\u1032", DEAD_KEY_PLACEHOLDER + "\u1032");
        putMM("\u1033", DEAD_KEY_PLACEHOLDER + "\u1033");
        putMM("\u1034", DEAD_KEY_PLACEHOLDER + "\u1034");
        putMM("\u1036", DEAD_KEY_PLACEHOLDER + "\u1036");
        putMM("\u1037", DEAD_KEY_PLACEHOLDER + "\u1037");
        putMM("\u1038", DEAD_KEY_PLACEHOLDER + "\u1038");
        putMM("\u1039", DEAD_KEY_PLACEHOLDER + "\u1039");
        putMM("\u103A", DEAD_KEY_PLACEHOLDER + "\u103A");
        putMM("\u103B", "\u103B" + DEAD_KEY_PLACEHOLDER);
        putMM("\u103C", DEAD_KEY_PLACEHOLDER + "\u103C");
        putMM("\u103D", DEAD_KEY_PLACEHOLDER + "\u103D");
        putMM("\u105A", DEAD_KEY_PLACEHOLDER + "\u105A");
        putMM("\u1064", DEAD_KEY_PLACEHOLDER + "\u1064");
        putMM("\u107D", DEAD_KEY_PLACEHOLDER + "\u107D");
        putMM("\u107E", "\u107E" + DEAD_KEY_PLACEHOLDER_X2);
        putMM("\u107F", "\u107F" + DEAD_KEY_PLACEHOLDER);
        putMM("\u1080", "\u1080" + DEAD_KEY_PLACEHOLDER_X2);
        putMM("\u1081", "\u1081" + DEAD_KEY_PLACEHOLDER);
        putMM("\u1082", "\u1082" + DEAD_KEY_PLACEHOLDER_X2);
        putMM("\u1083", "\u1083" + DEAD_KEY_PLACEHOLDER);
        putMM("\u1084", "\u1084" + DEAD_KEY_PLACEHOLDER_X2);
        putMM("\u1087", DEAD_KEY_PLACEHOLDER + "\u1087");
        putMM("\u1088", DEAD_KEY_PLACEHOLDER + "\u1088");
        putMM("\u1089", DEAD_KEY_PLACEHOLDER + "\u1089");
        putMM("\u108A", DEAD_KEY_PLACEHOLDER + "\u108A");
        putMM("\u108B", DEAD_KEY_PLACEHOLDER + "\u108B");
        putMM("\u108C", DEAD_KEY_PLACEHOLDER + "\u108C");
        putMM("\u108D", DEAD_KEY_PLACEHOLDER + "\u108D");
        putMM("\u108E", DEAD_KEY_PLACEHOLDER + "\u108E");
        putMM("\u1094", DEAD_KEY_PLACEHOLDER + "\u1094");
        putMM("\u1095", DEAD_KEY_PLACEHOLDER + "\u1095");
   }
}
