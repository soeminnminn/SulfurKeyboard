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

package com.s16.inputmethod.skeyboard;

public final class KeyCodes {
	
	static final int KEYCODE_NULL = 0;
	static final int KEYCODE_OPTIONS = -176; // KEYCODE_SETTINGS = 176;
    static final int KEYCODE_OPTIONS_LONGPRESS = -175; // KEYCODE_CAPTIONS = 175;
    static final int KEYCODE_VOICE = -94; // KEYCODE_PICTSYMBOLS = 94;
    static final int KEYCODE_CHARSET = -95; // KEYCODE_SWITCH_CHARSET = 95;
    static final int KEYCODE_EMOJI = -96;
    static final int KEYCODE_DPAD = -97;
    // KEYCODE_BUTTON_1 = 188; to KEYCODE_BUTTON_16 = 203;
    static final int KEYCODE_F1 = -188;
    static final int KEYCODE_F2 = -189;
    static final int KEYCODE_F3 = -190;
    static final int KEYCODE_F4 = -191;
    static final int KEYCODE_F5 = -192;
    static final int KEYCODE_F6 = -193;
    static final int KEYCODE_F7 = -194;
    static final int KEYCODE_F8 = -195;
    static final int KEYCODE_F9 = -196;
    static final int KEYCODE_F10 = -197;
    static final int KEYCODE_F11 = -198;
    static final int KEYCODE_F12 = -199;
    static final int KEYCODE_F13 = -200;
    static final int KEYCODE_F14 = -201;
    static final int KEYCODE_F15 = -202;
    static final int KEYCODE_F16 = -203;
    
    static final int KEYCODE_NEXT_LANGUAGE = -117; // KEYCODE_META_LEFT = 117;
    static final int KEYCODE_PREV_LANGUAGE = -118; // KEYCODE_META_RIGHT = 118;
    static final int KEYCODE_LANGUAGE = -177;
    static final int KEYCODE_INPUT_METHOD = -178;
    
    // The following keycodes match (negative) KeyEvent keycodes.
    // Would be better to use the real KeyEvent values, but many
    // don't exist prior to the Honeycomb API (level 11).
    static final int KEYCODE_DPAD_UP = -19;
    static final int KEYCODE_DPAD_DOWN = -20;
    static final int KEYCODE_DPAD_LEFT = -21;
    static final int KEYCODE_DPAD_RIGHT = -22;
    static final int KEYCODE_DPAD_CENTER = -23;
    static final int KEYCODE_ALT_LEFT = -57;
    static final int KEYCODE_PAGE_UP = -92;
    static final int KEYCODE_PAGE_DOWN = -93;
    static final int KEYCODE_ESCAPE = -111;
    static final int KEYCODE_FORWARD_DEL = -112;
    static final int KEYCODE_CTRL_LEFT = -113;
    static final int KEYCODE_CAPS_LOCK = -115;
    static final int KEYCODE_SCROLL_LOCK = -116;
    static final int KEYCODE_FN = -119;
    static final int KEYCODE_SYSRQ = -120;
    static final int KEYCODE_BREAK = -121;
    static final int KEYCODE_HOME = -122;
    static final int KEYCODE_END = -123;
    static final int KEYCODE_INSERT = -124;
    static final int KEYCODE_FKEY_F1 = -131;
    static final int KEYCODE_FKEY_F2 = -132;
    static final int KEYCODE_FKEY_F3 = -133;
    static final int KEYCODE_FKEY_F4 = -134;
    static final int KEYCODE_FKEY_F5 = -135;
    static final int KEYCODE_FKEY_F6 = -136;
    static final int KEYCODE_FKEY_F7 = -137;
    static final int KEYCODE_FKEY_F8 = -138;
    static final int KEYCODE_FKEY_F9 = -139;
    static final int KEYCODE_FKEY_F10 = -140;
    static final int KEYCODE_FKEY_F11 = -141;
    static final int KEYCODE_FKEY_F12 = -142;
    static final int KEYCODE_NUM_LOCK = -143;
    
    // From Keyboard.java
    static final int KEYCODE_SHIFT = -1;
    static final int KEYCODE_MODE_CHANGE = -2;
    static final int KEYCODE_CANCEL = -3;
    static final int KEYCODE_DONE = -4;
    static final int KEYCODE_DELETE = -5;
    static final int KEYCODE_ALT = -6;
    static final int KEYCODE_TAB = 9;
    static final int KEYCODE_RETURN = 10;
    
    // From LatinIME.java
    static final int KEYCODE_ENTER = '\n';
    static final int KEYCODE_SPACE = ' ';
    static final int KEYCODE_PERIOD = '.';
    
}
