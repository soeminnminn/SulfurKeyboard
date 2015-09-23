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
// SMM {

package com.s16.inputmethod.skeyboard;


public class MyanmarIMEUtils extends MMFontConvert {
	
	public static boolean isMyChar(int code) {
		return (code >= 0x1000 && code <= 0x109F) || (code >= 0xAA60 && code <= 0xAA7B);
	}
	
	public static boolean isMyChar(CharSequence label) {
		if(label == null) return false;
		boolean isMyChar = false;
    	for(int i = 0; i < label.length(); i++) {
    		if(isMyChar(label.charAt(i))) {
    			isMyChar = true;
    			break;
    		}
    	}
		return isMyChar;
	}
	
	public static boolean isZawgyiAlphabet(int code) {
		return ZawGyiCorrection.isAlphabet(code);
	}
	
	public static CharSequence getZawgyiFixed(CharSequence text) {
		if (text != null) {
			return ZawGyiCorrection.getWord(text);
		}
		return text;
	}
	
	public static CharSequence getJellyBeanFix(CharSequence text) {
		if (text != null) {
			return ZawGyiCorrection.getJellyBeanFix(text);
		}
		return text;
	}
}
//} SMM