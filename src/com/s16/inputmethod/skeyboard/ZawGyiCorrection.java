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

import java.util.ArrayList;

public class ZawGyiCorrection {
	private static final int NULL_CHAR = 0x00;
	private static final int JBFIX_CHAR = 0x200C; // 0x200C || 0xFEFF;
	
	private static final class CharIndex {
		public static final int ThaWaiHtoe = 0;
		public static final int RaRitt = 1;
		public static final int Consonant = 2;
		public static final int DoubleConsonant = 3;
		public static final int LowerConsonant = 4;
		public static final int LoneGyiTin = 5; 
		public static final int HaHtoe = 6; 
		public static final int WaSwal = 7; 
		public static final int YaPint = 8;
		public static final int TeeTeeTin = 9;
		public static final int ChangNyin = 10; 
		public static final int NoutPyit = 11; 
		public static final int YeeKhya = 12;
		public static final int AThart = 13;
		public static final int OutNyint = 14;
		public static final int WittSaPout = 15;
		public static final int Other = 16;
	}
	
	private ZawGyiCorrection() {
	}
	
	private static int[] toNormalChar(int code) {
		if(!isMyChar(code)) {
			return  new int[] {code};
		}
		
		switch(code) {
			case 0x107F:
			case 0x1081:
			case 0x1083:
			case 0x1080:
			case 0x1082:
			case 0x1084:
			case 0x107E:
				return new int[] {0x103B};
			case 0x108B:
				return new int[] {0x1064, 0x102D};
			case 0x108E:
				return new int[] {0x102D, 0x1036};
			case 0x108C:
				return new int[] {0x1004, 0x1039, 0x102E};
			case 0x108A:
				return new int[] {0x103C, 0x103D};
			case 0x1087:
				return new int[] {0x103D};
			case 0x107D:
				return new int[] {0x103A};
			case 0x1033:
				return new int[] {0x102F};
			case 0x1088:
				return new int[] {0x103D, 0x102F};
			case 0x1034:
				return new int[] {0x1030};
			case 0x1089:
				return new int[] {0x103D, 0x1030};
			case 0x1094:
			case 0x1095:
				return new int[] {0x1037};
			case 0x1064:
				return new int[] {0x1004, 0x1039};
			case 0x108D:
				return new int[] {0x1004, 0x1039, 0x1036};
			case 0x102B:
				return new int[] {0x102C};
			case 0x105A:
				return new int[] {0x102C, 0x1039};
			case 0x106A:
				return new int[] {0x1025};
			case 0x1026:
				return new int[] {0x1025, 0x102E};
			case 0x1029:
				return new int[] {0x107E, 0x101E};
			case 0x102A:
				return new int[] {0x1031, 0x107E, 0x101E, 0x102C, 0x1039};
			case 0x104E:
				return new int[] {0x1044, 0x1004, 0x1039, 0x1038};
			case 0x1008:
				return new int[] {0x1005, 0x103A};
			case 0x1009:
			case 0x106B:
				return new int[] {0x100A};
			case 0x108F:
				return new int[] {0x1014};
			case 0x1090:
				return new int[] {0x101B};
			case 0x106E:
				return new int[] {0x100D, 0x100D};
			case 0x106F:
				return new int[] {0x100E, 0x100D};
			case 0x1091:
				return new int[] {0x100F, 0x100D};
			case 0x1092:
				return new int[] {0x100B, 0x106D};
			case 0x1097:
				return new int[] {0x100B, 0x106C};
			case 0x1066:
				return new int[] {0x1067};
			case 0x1071:
				return new int[] {0x1072};
			case 0x1073:
				return new int[] {0x1074};
			case 0x107B:
				return new int[] {0x1093};
			default:
				return  new int[] {code};
		}
	}
	
	private static int getCharIndex(int code) {
		int result = CharIndex.Other;
		switch(code)
		{
            // Consonant
            case 0x1001:
            case 0x1002:
            case 0x1004:
            case 0x1005:
            case 0x1007:
            case 0x1008:
            case 0x100B:
            case 0x100C:
            case 0x100D:
            case 0x100E:
            case 0x1012:
            case 0x1013:
            case 0x1014:
            case 0x1015:
            case 0x1016:
            case 0x1017:
            case 0x1019:
            case 0x101B:
            case 0x101D:
            case 0x1020:
            // SymbolConsonant
            case 0x1025:
            case 0x1026:
            case 0x1027:
            case 0x104C:
            case 0x104D:
            case 0x104F:
            case 0x106A:
            case 0x106E:
            case 0x106F:
            case 0x108F:
            case 0x1090:
            case 0x1092:
            case 0x1097:
            // Digit
            case 0x1040:
            case 0x1041:
            case 0x1042:
            case 0x1043:
            case 0x1044:
            case 0x1045:
            case 0x1046:
            case 0x1047:
            case 0x1048:
            case 0x1049:
            // Sign
            case 0x104A:
            case 0x104B:
                result = CharIndex.Consonant;
                break;

            // DoubleConsonant
            case 0x1000:
            case 0x1003:
            case 0x1006:
            case 0x100A:
            case 0x100F:
            case 0x1010:
            case 0x1011:
            case 0x1018:
            case 0x101A:
            case 0x101C:
            case 0x101E:
            case 0x101F:
            case 0x1021:
            // SymbolConsonant
            case 0x1009:
            case 0x1023:
            case 0x1024:
            case 0x1029:
            case 0x102A:
            case 0x103F:
            case 0x104E:
            case 0x106B:
            case 0x1086:
            case 0x1091:
                result = CharIndex.DoubleConsonant;
                break;

            // LowerConsonant
            case 0x1060:
            case 0x1061:
            case 0x1062:
            case 0x1063:
            case 0x1065:
            case 0x1066:
            case 0x1067:
            case 0x1068:
            case 0x1069:
            case 0x106C:
            case 0x106D:
            case 0x1070:
            case 0x1071:
            case 0x1072:
            case 0x1073:
            case 0x1074:
            case 0x1075:
            case 0x1076:
            case 0x1077:
            case 0x1078:
            case 0x1079:
            case 0x107A:
            case 0x107B:
            case 0x107C:
            case 0x1085:
            case 0x1093:
            case 0x1096:
                result = CharIndex.LowerConsonant;
                break;

            case 0x1031:
                result = CharIndex.ThaWaiHtoe;
                break;

			case 0x103B:
			case 0x107E:
			case 0x107F:
			case 0x1080:
			case 0x1081:
			case 0x1082:
			case 0x1083:
			case 0x1084:
                result = CharIndex.RaRitt;
                break;
			
			case 0x102D:
			case 0x102E:
			case 0x108B:
			case 0x108C:
			case 0x108E:
                result = CharIndex.LoneGyiTin;
                break;

			case 0x103C:
			case 0x108A:
                result = CharIndex.WaSwal;
                break;

			case 0x103D:
			case 0x1087:
                result = CharIndex.HaHtoe;
                break;

			case 0x103A:
			case 0x107D:
                result = CharIndex.YaPint;
                break;

			case 0x102F:
			case 0x1030:
			case 0x1033:
			case 0x1034:
			case 0x1088:
			case 0x1089:
                result = CharIndex.ChangNyin;
                break;

			case 0x1036:
                result = CharIndex.TeeTeeTin;
                break;

			case 0x1037:
			case 0x1094:
			case 0x1095:
                result = CharIndex.OutNyint;
                break;

			case 0x1039:
			case 0x1064:
			case 0x108D:
                result = CharIndex.AThart;
                break;

			case 0x1032:
                result = CharIndex.NoutPyit;
                break;

			case 0x102B:
			case 0x102C:
			case 0x105A:
                result = CharIndex.YeeKhya;
                break;

			case 0x1038:
                result = CharIndex.WittSaPout;
                break;
			
			default:
                result = CharIndex.Other;
                break;
		}
		
		return result;
	}
	
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
	
	public static boolean isAlphabet(int code) {
		int charIndex = getCharIndex(code);
		switch(charIndex) {
			case CharIndex.ThaWaiHtoe:
			case CharIndex.RaRitt:
			case CharIndex.Consonant:
			case CharIndex.DoubleConsonant:
				return true;
				
			case CharIndex.Other:
			default:
				return Character.isLetter(code);
		}
	}
	
	public static boolean isCharEqual(int code1, int code2) {
		if(isMyChar(code1) && isMyChar(code2)) {
			int[] codes1 = toNormalChar(code1);
			int[] codes2 = toNormalChar(code2);
			if(codes1.length == codes2.length) {
				for(int i = 0; i < codes1.length; i++) {
					if(codes1[i] != codes2[i]) {
						return false;
					}
				}
				return true;
			}
			return false;
		}
		return code1 == code2;
	}
	
    private static void appendResult(Word word, ArrayList<Integer> resultList) {
    	int[] result = word.getResult();
    	for (int i : result) {
    		resultList.add(i);
    	}
    }
    
    public static CharSequence getWord(CharSequence value) {
    	if(value == null) return value;
    	int[] iArray = new int[value.length()];
    	
    	for(int i = 0; i < value.length(); i++) {
    		iArray[i] = (int)value.charAt(i);
    	}
    	
    	char[] chArray = getWord(iArray);
    	if(chArray != null) {
			return String.valueOf(chArray);	
    	}
    	
    	return value;
    }
    
    public static char[] getWord(int[] value) {
    	if((value == null) || (value.length < 2)) return null;
    	
    	ArrayList<Integer> resultList = new ArrayList<Integer>();
    	ZawGyiCorrection zawGyi = new ZawGyiCorrection();
    	Word word = zawGyi.new Word();
		
		for(int i = 0; i < value.length; i++)
		{
            int code = value[i];

            //System.Diagnostics.Debug.Write(string.Format("0x{0:X} ", code));
            //System.Diagnostics.Debug.WriteLine(code.ToString() + " =   " + ((char)code).ToString());

			int charIndex = getCharIndex(code);
            boolean firstChar = ((charIndex == CharIndex.ThaWaiHtoe) 
                || (charIndex == CharIndex.RaRitt) 
                || (charIndex == CharIndex.Consonant) 
                || (charIndex == CharIndex.DoubleConsonant)
                || (charIndex == CharIndex.Other));

            if (word.hasConsonant() && firstChar)
            {
                word.fix();
                appendResult(word, resultList);
                word.reset();
                //System.Diagnostics.Debug.WriteLine("--------------------------------------");
            }

            if (charIndex == CharIndex.Other)
            	resultList.add(code);
            else
            	word.setValue((int)charIndex, code);
		}

        word.fix();
        appendResult(word, resultList);
        word.reset();
        
        char[] arrChar = new char[resultList.size()];
		int count = 0;
		for(Integer ch : resultList) {
			arrChar[count++] = (char)ch.intValue();
		}
		
		return arrChar;
    }
	
	public static CharSequence getJellyBeanFix(CharSequence input) {
		ArrayList<Integer> resultList = new ArrayList<Integer>();
		
		for(int i = 0; i < input.length(); i++) {
    		int iValue = (int)input.charAt(i);
    		if(iValue == 0x1031) {
    			resultList.add(JBFIX_CHAR);
    			resultList.add(iValue);
    		} else if(iValue == 0x1039) {
    			resultList.add(iValue);
    			resultList.add(JBFIX_CHAR);
    		} else {
    			resultList.add(iValue);
    		}
    	}
		
		char[] chArray = new char[resultList.size()];
		int count = 0;
		for(Integer ch : resultList) {
			chArray[count++] = (char)ch.intValue();
		}
		
		return String.valueOf(chArray);
		//String output = input.toString();
		//output = output.replaceAll("\u1031", String.valueOf((char)JBFIX_CHAR) + "\u1031");
		//output = output.replaceAll("\u1039", "\u1039" + String.valueOf((char)JBFIX_CHAR));
		//return output;
	}
		
	public static CharSequence ZawGyiDrawFix(CharSequence input) {
		String output = input.toString();
		int index = 0;
		char[] chArray = new char[output.length()];
		for(int i = 0; i < output.length(); i++) {
			int ch = (int)output.charAt(i);
			if((ch != NULL_CHAR) && (isMyChar(ch))) {
				chArray[index++] = (char)(ch + 0xEA00);
			}
			else {
				chArray[index++] = (char)ch;
			}
    	}
		return String.valueOf(chArray);
	}
	
	class Word {
		private static final int WORD_LENGTH = 17;
        private int[] mWord = null;
        
        public Word() {
            this.mWord = new int[WORD_LENGTH];
        }
        
        private void fixThaWaiHtoe() {
            if (this.mWord[(int)CharIndex.ThaWaiHtoe] == NULL_CHAR) return;
            // No Change
        }

		private void fixRaYitt() {
            if (this.mWord[(int)CharIndex.RaRitt] == NULL_CHAR) return;

			boolean upper = ((this.mWord[(int)CharIndex.LoneGyiTin] != NULL_CHAR) 
                                || (this.mWord[(int)CharIndex.NoutPyit] != NULL_CHAR)
                                || (this.mWord[(int)CharIndex.TeeTeeTin] != NULL_CHAR)
                                || (this.mWord[(int)CharIndex.AThart] == 0x1064)
                                || (this.mWord[(int)CharIndex.AThart] == 0x108D));

			boolean lower = ((this.mWord[(int)CharIndex.WaSwal] != NULL_CHAR) 
							|| (this.mWord[(int)CharIndex.LowerConsonant] != NULL_CHAR));

			if (this.mWord[(int)CharIndex.Consonant] != NULL_CHAR)
			{
				if (upper && lower)
				{
					this.mWord[(int)CharIndex.RaRitt] = 0x1083;
				}
				else if (upper)
				{
					this.mWord[(int)CharIndex.RaRitt] = 0x107F;
				}
				else if (lower)
				{
					this.mWord[(int)CharIndex.RaRitt] = 0x1081;
				}
				else
				{
					this.mWord[(int)CharIndex.RaRitt] = 0x103B;
				}
			}
			else
			{
				if (upper && lower)
				{
					this.mWord[(int)CharIndex.RaRitt] = 0x1084;
				}
				else if (upper)
				{
					this.mWord[(int)CharIndex.RaRitt] = 0x1080;
				}
				else if (lower)
				{
					this.mWord[(int)CharIndex.RaRitt] = 0x1082;
				}
				else
				{
					this.mWord[(int)CharIndex.RaRitt] = 0x107E;
				}
			}
		}
		
		private void fixConsonant() {
            if ((this.mWord[(int)CharIndex.Consonant] == NULL_CHAR) && (this.mWord[(int)CharIndex.DoubleConsonant] == NULL_CHAR)) return;

			if ((this.mWord[(int)CharIndex.DoubleConsonant] == 0x100A) 
			    || (this.mWord[(int)CharIndex.DoubleConsonant] == 0x106B)) // ည
			{
				if((this.mWord[(int)CharIndex.WaSwal] != NULL_CHAR)
				  || (this.mWord[(int)CharIndex.LowerConsonant] != NULL_CHAR))
				{
					this.mWord[(int)CharIndex.DoubleConsonant] = 0x106B;
				}
				else
				{
					this.mWord[(int)CharIndex.DoubleConsonant] = 0x100A;
				}
			}
			
			if ((this.mWord[(int)CharIndex.Consonant] == 0x1014) 
			    || (this.mWord[(int)CharIndex.Consonant] == 0x108F)) // န
			{
				if((this.mWord[(int)CharIndex.RaRitt] != NULL_CHAR)
				   || (this.mWord[(int)CharIndex.WaSwal] != NULL_CHAR)
				   || (this.mWord[(int)CharIndex.HaHtoe] != NULL_CHAR)
				   || (this.mWord[(int)CharIndex.YaPint] != NULL_CHAR)
				   || (this.mWord[(int)CharIndex.ChangNyin] != NULL_CHAR)
				   || (this.mWord[(int)CharIndex.LowerConsonant] != NULL_CHAR)
				   )
				{
					this.mWord[(int)CharIndex.Consonant] = 0x108F;
				}
				else
				{
					this.mWord[(int)CharIndex.Consonant] = 0x1014;
				}
			}
			if ((this.mWord[(int)CharIndex.Consonant] == 0x101B) 
			    || (this.mWord[(int)CharIndex.Consonant] == 0x1090)) // ရ
			{
				if(this.mWord[(int)CharIndex.ChangNyin] != NULL_CHAR)
				{
					this.mWord[(int)CharIndex.Consonant] = 0x1090;
				}
				else
				{
					this.mWord[(int)CharIndex.Consonant] = 0x101B;
				}
			}
			if ((this.mWord[(int)CharIndex.Consonant] == 0x1025) 
			    || (this.mWord[(int)CharIndex.Consonant] == 0x106A)) // ဥ
			{
				if((this.mWord[(int)CharIndex.WaSwal] != NULL_CHAR)
				  || (this.mWord[(int)CharIndex.LowerConsonant] != NULL_CHAR))
				{
					this.mWord[(int)CharIndex.Consonant] = 0x106A;
				}
				else
				{
					this.mWord[(int)CharIndex.Consonant] = 0x1025;
				}
			}
		}

        private void fixLowerConsonant() {
            if (this.mWord[(int)CharIndex.LowerConsonant] == NULL_CHAR) return;

            if ((this.mWord[(int)CharIndex.LowerConsonant] == 0x1066)
                || (this.mWord[(int)CharIndex.LowerConsonant] == 0x1067))
            {
                if (this.mWord[(int)CharIndex.DoubleConsonant] != NULL_CHAR)
				{
                    this.mWord[(int)CharIndex.LowerConsonant] = 0x1066;
				}
                else
				{
                    this.mWord[(int)CharIndex.LowerConsonant] = 0x1067;
				}
            }

            if ((this.mWord[(int)CharIndex.LowerConsonant] == 0x1071)
                || (this.mWord[(int)CharIndex.LowerConsonant] == 0x1072))
            {
                if (this.mWord[(int)CharIndex.DoubleConsonant] != NULL_CHAR)
				{
                    this.mWord[(int)CharIndex.LowerConsonant] = 0x1071;
				}
                else
				{
                    this.mWord[(int)CharIndex.LowerConsonant] = 0x1072;
				}
            }

            if ((this.mWord[(int)CharIndex.LowerConsonant] == 0x1073)
                || (this.mWord[(int)CharIndex.LowerConsonant] == 0x1074))
            {
                if (this.mWord[(int)CharIndex.DoubleConsonant] != NULL_CHAR)
				{
                    this.mWord[(int)CharIndex.LowerConsonant] = 0x1073;
				}
                else
				{
                    this.mWord[(int)CharIndex.LowerConsonant] = 0x1074;
				}
            }

            if ((this.mWord[(int)CharIndex.LowerConsonant] == 0x107B)
                || (this.mWord[(int)CharIndex.LowerConsonant] == 0x1093))
            {
                if (this.mWord[(int)CharIndex.DoubleConsonant] != NULL_CHAR)
				{
                    this.mWord[(int)CharIndex.LowerConsonant] = 0x107B;
				}
                else
				{
                    this.mWord[(int)CharIndex.LowerConsonant] = 0x1093;
				}
            }
        }
		
		private void fixLoneGyiTin() {
			if (this.mWord[(int)CharIndex.LoneGyiTin] == NULL_CHAR) return;
			
			if (this.mWord[(int)CharIndex.AThart] == 0x1064)
			{
				if (this.mWord[(int)CharIndex.LoneGyiTin] == 0x102D)
				{
					this.mWord[(int)CharIndex.LoneGyiTin] = 0x108B;
				}
				else
				{
					this.mWord[(int)CharIndex.LoneGyiTin] = 0x108C;
				}
				this.mWord[(int)CharIndex.AThart] = NULL_CHAR;
			}
			else if (this.mWord[(int)CharIndex.TeeTeeTin] != NULL_CHAR)
			{
				this.mWord[(int)CharIndex.LoneGyiTin] = 0x108E;
				this.mWord[(int)CharIndex.AThart] = NULL_CHAR;
			}
			else if ((this.mWord[(int)CharIndex.LoneGyiTin] == 0x102D) 
			         || (this.mWord[(int)CharIndex.LoneGyiTin] == 0x108B)
			         || (this.mWord[(int)CharIndex.LoneGyiTin] == 0x108E))
			{
				this.mWord[(int)CharIndex.LoneGyiTin] = 0x102D;
			}
			else if ((this.mWord[(int)CharIndex.LoneGyiTin] == 0x102E)
			         || (this.mWord[(int)CharIndex.LoneGyiTin] == 0x108C))
			{
				this.mWord[(int)CharIndex.LoneGyiTin] = 0x102E;
			}
		}
		
		private void fixHaHtoe() {
			if (this.mWord[(int)CharIndex.HaHtoe] == NULL_CHAR) return;
			
			if ((this.mWord[(int)CharIndex.RaRitt] != NULL_CHAR)
					|| (this.mWord[(int)CharIndex.DoubleConsonant] == 0x100A)
                    || (this.mWord[(int)CharIndex.DoubleConsonant] == 0x1009)
                    || (this.mWord[(int)CharIndex.DoubleConsonant] == 0x106B)
                    || (this.mWord[(int)CharIndex.Consonant] == 0x100C)
                    || (this.mWord[(int)CharIndex.DoubleConsonant] == 0x1029)
                    || (this.mWord[(int)CharIndex.DoubleConsonant] == 0x102A)
                    )
			{
				this.mWord[(int)CharIndex.HaHtoe] = 0x1087;
			}
			else
			{
				this.mWord[(int)CharIndex.HaHtoe] = 0x103D;
			}
		}
		
		private void fixWaSwal() {
			if (this.mWord[(int)CharIndex.WaSwal] == NULL_CHAR) return;
			
			if (this.mWord[(int)CharIndex.HaHtoe] != NULL_CHAR)
			{
				this.mWord[(int)CharIndex.WaSwal] = 0x108A;
				this.mWord[(int)CharIndex.HaHtoe] = NULL_CHAR;
			}
			else
			{
				this.mWord[(int)CharIndex.WaSwal] = 0x103C;
			}
		}
		
		private void fixYaPint() {
			if (this.mWord[(int)CharIndex.YaPint] == NULL_CHAR) return;

            if (this.mWord[(int)CharIndex.WaSwal] != NULL_CHAR)
			{
				this.mWord[(int)CharIndex.YaPint] = 0x107D;
			}
			else
			{
				this.mWord[(int)CharIndex.YaPint] = 0x103A;
			}
		}
		
		private void fixChangNyin() {
			if (this.mWord[(int)CharIndex.ChangNyin] == NULL_CHAR) return;
			
			boolean isLong = ((this.mWord[(int)CharIndex.RaRitt] != NULL_CHAR)
                    || (this.mWord[(int)CharIndex.YaPint] != NULL_CHAR)
                    || (this.mWord[(int)CharIndex.WaSwal] != NULL_CHAR)
                    || (this.mWord[(int)CharIndex.LowerConsonant] != NULL_CHAR)
                    
                    || (this.mWord[(int)CharIndex.Consonant] == 0x1008) // ဈ
                    || (this.mWord[(int)CharIndex.Consonant] == 0x100B) // ဋ
                    || (this.mWord[(int)CharIndex.Consonant] == 0x100C) // ဌ
                    || (this.mWord[(int)CharIndex.Consonant] == 0x100D) // ဍ
                    || (this.mWord[(int)CharIndex.Consonant] == 0x1020) // ဠ
                    || (this.mWord[(int)CharIndex.Consonant] == 0x1025) // ဥ
                    || (this.mWord[(int)CharIndex.Consonant] == 0x1026) // ဦ
                    || (this.mWord[(int)CharIndex.Consonant] == 0x106A) // ၪ
                    || (this.mWord[(int)CharIndex.Consonant] == 0x104C) // ၌
                    || (this.mWord[(int)CharIndex.Consonant] == 0x104D) // ၍
                    || (this.mWord[(int)CharIndex.Consonant] == 0x1092) // ႒
                    || (this.mWord[(int)CharIndex.Consonant] == 0x106E) // ၮ
                    || (this.mWord[(int)CharIndex.Consonant] == 0x106F) // ၯ
                    || (this.mWord[(int)CharIndex.Consonant] == 0x1097) // ႗

                    || (this.mWord[(int)CharIndex.Consonant] == 0x1042) // ၂
                    || (this.mWord[(int)CharIndex.Consonant] == 0x1043) // ၃
                    || (this.mWord[(int)CharIndex.Consonant] == 0x1044) // ၄
                    || (this.mWord[(int)CharIndex.Consonant] == 0x1045) // ၅
                    || (this.mWord[(int)CharIndex.Consonant] == 0x1046) // ၆
                    || (this.mWord[(int)CharIndex.Consonant] == 0x1047) // ၇
                    || (this.mWord[(int)CharIndex.Consonant] == 0x1049) // ၉

                    || (this.mWord[(int)CharIndex.DoubleConsonant] == 0x100A) // ည
                    || (this.mWord[(int)CharIndex.DoubleConsonant] == 0x1009) // ဉ
                    || (this.mWord[(int)CharIndex.DoubleConsonant] == 0x106B) // ၫ
                    || (this.mWord[(int)CharIndex.DoubleConsonant] == 0x1023) // ဣ
                    || (this.mWord[(int)CharIndex.DoubleConsonant] == 0x1024) // ဤ
                    || (this.mWord[(int)CharIndex.DoubleConsonant] == 0x1029) // ဩ
                    || (this.mWord[(int)CharIndex.DoubleConsonant] == 0x102A) // ဪ
                    || (this.mWord[(int)CharIndex.DoubleConsonant] == 0x104E) // ၎
                    || (this.mWord[(int)CharIndex.DoubleConsonant] == 0x1091) // ႑
                    );
			
			if ((this.mWord[(int)CharIndex.ChangNyin] == 0x102F) 
				|| (this.mWord[(int)CharIndex.ChangNyin] == 0x1033))
			{
				if (isLong)
				{
                    this.mWord[(int)CharIndex.ChangNyin] = 0x1033;
				}
				else if(this.mWord[(int)CharIndex.HaHtoe] != NULL_CHAR) 
				{
                    this.mWord[(int)CharIndex.ChangNyin] = 0x1088;
					this.mWord[(int)CharIndex.HaHtoe] = NULL_CHAR;
				}
				else
				{
                    this.mWord[(int)CharIndex.ChangNyin] = 0x102F;
				}
			}
			else // 0x1030 | 0x1034
			{
				if (isLong)
				{
                    this.mWord[(int)CharIndex.ChangNyin] = 0x1034;
				}
				else if(this.mWord[(int)CharIndex.HaHtoe] != NULL_CHAR) 
				{
                    this.mWord[(int)CharIndex.ChangNyin] = 0x1089;
					this.mWord[(int)CharIndex.HaHtoe] = NULL_CHAR;
				}
				else
				{
                    this.mWord[(int)CharIndex.ChangNyin] = 0x1030;
				}
			}
		}
		
		private void fixTeeTeeTin() {
			if (this.mWord[(int)CharIndex.TeeTeeTin] == NULL_CHAR) return;
			// No Change 
		}
		
		private void fixNoutPyit() {
			if (this.mWord[(int)CharIndex.NoutPyit] == NULL_CHAR) return;
			// No Change
		}
		
		private void fixYeeKhya() {
			if (this.mWord[(int)CharIndex.YeeKhya] == NULL_CHAR) return;
			
			if((   (this.mWord[(int)CharIndex.Consonant] == 0x1001)  // ခ
			    || (this.mWord[(int)CharIndex.Consonant] == 0x1002)  // ဂ
			    || (this.mWord[(int)CharIndex.Consonant] == 0x1004)  // င
			    || (this.mWord[(int)CharIndex.Consonant] == 0x1012)  // ဒ
			    || (this.mWord[(int)CharIndex.Consonant] == 0x1015)  // ပ
			    || (this.mWord[(int)CharIndex.Consonant] == 0x101D)) // ဝ
		        && 
		   		(  (this.mWord[(int)CharIndex.RaRitt] == NULL_CHAR)
		    	&& (this.mWord[(int)CharIndex.LowerConsonant] == NULL_CHAR)
                && (this.mWord[(int)CharIndex.LoneGyiTin] == NULL_CHAR)
                && (this.mWord[(int)CharIndex.HaHtoe] == NULL_CHAR)
                && (this.mWord[(int)CharIndex.WaSwal] == NULL_CHAR)
                && (this.mWord[(int)CharIndex.YaPint] == NULL_CHAR)
                && (this.mWord[(int)CharIndex.ChangNyin] == NULL_CHAR)
		    	))
			{
				if ((this.mWord[(int)CharIndex.YeeKhya] != NULL_CHAR)
                    && (this.mWord[(int)CharIndex.AThart] == 0x1039))
				{
					this.mWord[(int)CharIndex.YeeKhya] = 0x105A;
                    this.mWord[(int)CharIndex.AThart] = NULL_CHAR;
				}
				else
				{
					this.mWord[(int)CharIndex.YeeKhya] = 0x102B;
				}
			}
			else
			{
				this.mWord[(int)CharIndex.YeeKhya] = 0x102C;
			}
		}

        private void fixAThart() {
            if (this.mWord[(int)CharIndex.AThart] == NULL_CHAR) return;
            if (this.mWord[(int)CharIndex.AThart] == 0x1064) 
            {
                if (this.mWord[(int)CharIndex.TeeTeeTin] != NULL_CHAR) 
                {
                    this.mWord[(int)CharIndex.AThart] = 0x108D;
					this.mWord[(int)CharIndex.TeeTeeTin] = NULL_CHAR;
                } 
                else 
                {
                    this.mWord[(int)CharIndex.AThart] = 0x1064;
                }
            }
        }

        private void fixOutNyint() {
            if (this.mWord[(int)CharIndex.OutNyint] == NULL_CHAR) return;
            if ((this.mWord[(int)CharIndex.YeeKhya] == NULL_CHAR) && ( 
				(this.mWord[(int)CharIndex.RaRitt] != NULL_CHAR)
                || (this.mWord[(int)CharIndex.WaSwal] != NULL_CHAR)
                || (this.mWord[(int)CharIndex.YaPint] != NULL_CHAR)
                || ((this.mWord[(int)CharIndex.ChangNyin] != NULL_CHAR) && (this.mWord[(int)CharIndex.ChangNyin] != 0x102F))
                || (this.mWord[(int)CharIndex.LowerConsonant] != NULL_CHAR)
                
                || (this.mWord[(int)CharIndex.Consonant] == 0x1008)// ဈ
                || (this.mWord[(int)CharIndex.Consonant] == 0x100B)  // ဋ
                || (this.mWord[(int)CharIndex.Consonant] == 0x100C)  // ဌ
                || (this.mWord[(int)CharIndex.Consonant] == 0x100D)  // ဍ
                || (this.mWord[(int)CharIndex.Consonant] == 0x1020)  // ဠ
                || (this.mWord[(int)CharIndex.Consonant] == 0x101B)  // ရ
                || (this.mWord[(int)CharIndex.Consonant] == 0x1090) // ႐
                || (this.mWord[(int)CharIndex.Consonant] == 0x104C) // ၌
                || (this.mWord[(int)CharIndex.Consonant] == 0x104D) // ၍
                || (this.mWord[(int)CharIndex.Consonant] == 0x1092) // ႒
                || (this.mWord[(int)CharIndex.Consonant] == 0x106E) // ၮ
                || (this.mWord[(int)CharIndex.Consonant] == 0x106F) // ၯ
                || (this.mWord[(int)CharIndex.Consonant] == 0x1097) // ႗

                || (this.mWord[(int)CharIndex.Consonant] == 0x1042) // ၂
                || (this.mWord[(int)CharIndex.Consonant] == 0x1043) // ၃
                || (this.mWord[(int)CharIndex.Consonant] == 0x1044) // ၄
                || (this.mWord[(int)CharIndex.Consonant] == 0x1045) // ၅
                || (this.mWord[(int)CharIndex.Consonant] == 0x1046) // ၆
                || (this.mWord[(int)CharIndex.Consonant] == 0x1047) // ၇
                || (this.mWord[(int)CharIndex.Consonant] == 0x1049) // ၉
				
                || (this.mWord[(int)CharIndex.DoubleConsonant] == 0x1023) // ဣ
                || (this.mWord[(int)CharIndex.DoubleConsonant] == 0x1024) // ဤ
                || (this.mWord[(int)CharIndex.DoubleConsonant] == 0x1029) // ဩ
                || (this.mWord[(int)CharIndex.DoubleConsonant] == 0x102A) // ဪ
                || (this.mWord[(int)CharIndex.DoubleConsonant] == 0x104E) // ၎
                || (this.mWord[(int)CharIndex.DoubleConsonant] == 0x1091) // ႑
                ))
            {
                this.mWord[(int)CharIndex.OutNyint] = 0x1095;
            } 
            else if ((this.mWord[(int)CharIndex.YeeKhya] == NULL_CHAR) && ( 
				(this.mWord[(int)CharIndex.HaHtoe] != NULL_CHAR)
                || (this.mWord[(int)CharIndex.ChangNyin] == 0x102F)
                || (this.mWord[(int)CharIndex.Consonant] == 0x1014)) // န 
				)
            {
                this.mWord[(int)CharIndex.OutNyint] = 0x1094;
            } 
            else 
            {
                this.mWord[(int)CharIndex.OutNyint] = 0x1037;
            }
        }

        private void fixWittSaPout() {
			if (this.mWord[(int)CharIndex.WittSaPout] == NULL_CHAR) return;
			// No Change
		}
        
        private void fixMixChar() {
			if((this.mWord[(int)CharIndex.Consonant] == 0x100D)
				&& (this.mWord[(int)CharIndex.LowerConsonant] == 0x106E)) 
			{
				this.mWord[(int)CharIndex.Consonant] = 0x106E;
				this.mWord[(int)CharIndex.LowerConsonant] = NULL_CHAR;
			}
			
        	if((this.mWord[(int)CharIndex.Consonant] == 0x100E) 
        		&& (this.mWord[(int)CharIndex.LowerConsonant] == 0x106F)) 
    		{
        		this.mWord[(int)CharIndex.Consonant] = 0x106F;
        		this.mWord[(int)CharIndex.LowerConsonant] = NULL_CHAR;
    		}
        	
        	if((this.mWord[(int)CharIndex.Consonant] == 0x100B) 
    			&& (this.mWord[(int)CharIndex.LowerConsonant] == 0x1097))
        	{
        		this.mWord[(int)CharIndex.Consonant] = 0x1097;
        		this.mWord[(int)CharIndex.LowerConsonant] = NULL_CHAR;
        	}
        	
        	if((this.mWord[(int)CharIndex.ThaWaiHtoe] == 0x1031)
        		&& (this.mWord[(int)CharIndex.RaRitt] == 0x107E)
            	&& (this.mWord[(int)CharIndex.DoubleConsonant] == 0x101E)
            	&& (this.mWord[(int)CharIndex.YeeKhya] == 0x102C)
            	&& (this.mWord[(int)CharIndex.AThart] == 0x1039)) 
        	{
        		this.mWord[(int)CharIndex.ThaWaiHtoe] = NULL_CHAR;
                this.mWord[(int)CharIndex.RaRitt] = NULL_CHAR;
                this.mWord[(int)CharIndex.DoubleConsonant] = 0x102A;
                this.mWord[(int)CharIndex.YeeKhya] = NULL_CHAR;
                this.mWord[(int)CharIndex.AThart] = NULL_CHAR;
        	}
        	
        	if((this.mWord[(int)CharIndex.RaRitt] == 0x107E)
    			&& (this.mWord[(int)CharIndex.DoubleConsonant] == 0x101E)) 
        	{
        		this.mWord[(int)CharIndex.RaRitt] = NULL_CHAR;
        		this.mWord[(int)CharIndex.DoubleConsonant] = 0x1029;
        	}
        	
        	if((this.mWord[(int)CharIndex.LoneGyiTin] == 0x102E)
    			&& (this.mWord[(int)CharIndex.Consonant] == 0x1025)) 
        	{
        		this.mWord[(int)CharIndex.LoneGyiTin] = NULL_CHAR;
        		this.mWord[(int)CharIndex.Consonant] = 0x1026;
        	}
        	
        	if((this.mWord[(int)CharIndex.Consonant] == 0x1044)
        		&& (this.mWord[(int)CharIndex.LowerConsonant] == 0x1004)
        		&& (this.mWord[(int)CharIndex.AThart] == 0x1039)
        		&& (this.mWord[(int)CharIndex.WittSaPout] == 0x1038))
        	{
        		this.mWord[(int)CharIndex.Consonant] = 0x104E;
                this.mWord[(int)CharIndex.LowerConsonant] = NULL_CHAR;
                this.mWord[(int)CharIndex.AThart] = NULL_CHAR;
                this.mWord[(int)CharIndex.WittSaPout] = NULL_CHAR;
        	}
			
			if ((this.mWord[(int)CharIndex.Consonant] == 0x1005)
				&& (this.mWord[(int)CharIndex.YaPint] == 0x103A))
			{
				this.mWord[(int)CharIndex.Consonant] = 0x1008;
				this.mWord[(int)CharIndex.YaPint] = NULL_CHAR;
			}
		}
        
        private void addToList(int code, ArrayList<Integer> list) {
        	if(code != NULL_CHAR) {
        		list.add(code);
        	}
        }
        
        public void fix() {
            if (this.mWord == null) return;
			if((this.mWord[(int)CharIndex.Consonant] == NULL_CHAR) && (this.mWord[(int)CharIndex.DoubleConsonant] == NULL_CHAR)) return;

            this.fixThaWaiHtoe();
            this.fixRaYitt();
			this.fixConsonant();
            this.fixLowerConsonant();
			this.fixLoneGyiTin();
			this.fixHaHtoe();
			this.fixWaSwal();
			this.fixYaPint();
			this.fixChangNyin();
			this.fixTeeTeeTin();
			this.fixNoutPyit();
			this.fixYeeKhya();
            this.fixAThart();
            this.fixOutNyint();
            this.fixWittSaPout();
            this.fixMixChar();
        }

        public void reset() {
            this.mWord = new int[WORD_LENGTH];
        }

        public boolean hasConsonant() {
            return (this.mWord[(int)CharIndex.Consonant] != NULL_CHAR) || (this.mWord[(int)CharIndex.DoubleConsonant] != NULL_CHAR);
        }
        
        public void setValue(int charIndex, int code) {
        	if(charIndex < 0) return;
        	if(charIndex > CharIndex.Other) return;
        	
        	switch (code)
            {
                case 0x106E: // �?�
                    this.mWord[(int)CharIndex.Consonant] = 0x100D;
                    this.mWord[(int)CharIndex.LowerConsonant] = code;
                    break;

                case 0x106F: // �?�
                    this.mWord[(int)CharIndex.Consonant] = 0x100E;
                    this.mWord[(int)CharIndex.LowerConsonant] = code;
                    break;

                case 0x1097: // ႗
                    this.mWord[(int)CharIndex.Consonant] = 0x100B;
                    this.mWord[(int)CharIndex.LowerConsonant] = code;
                    break;

                case 0x102A: // ဪ
                    this.mWord[(int)CharIndex.ThaWaiHtoe] = 0x1031;
                    this.mWord[(int)CharIndex.RaRitt] = 0x107E;
                    this.mWord[(int)CharIndex.DoubleConsonant] = 0x101E;
                    this.mWord[(int)CharIndex.YeeKhya] = 0x102C;
                    this.mWord[(int)CharIndex.AThart] = 0x1039;
                    break;
                    
                case 0x1029: // ဩ
                    this.mWord[(int)CharIndex.RaRitt] = 0x107E;
                    this.mWord[(int)CharIndex.DoubleConsonant] = 0x101E;
                    break;

                case 0x1026: // ဦ
                    this.mWord[(int)CharIndex.LoneGyiTin] = 0x102E;
                    this.mWord[(int)CharIndex.Consonant] = 0x1025;
                    break;

                // TODO Need to fix up
                case 0x104E: // �?�
                    this.mWord[(int)CharIndex.Consonant] = 0x1044;
                    this.mWord[(int)CharIndex.LowerConsonant] = 0x1004;
                    this.mWord[(int)CharIndex.AThart] = 0x1039;
                    this.mWord[(int)CharIndex.WittSaPout] = 0x1038;
                    break;

                case 0x105A: // x�?�
                    this.mWord[(int)CharIndex.YeeKhya] = 0x102C;
                    this.mWord[(int)CharIndex.AThart] = 0x1039;
                    break;

                case 0x1088: // �?×
                    this.mWord[(int)CharIndex.HaHtoe] = 0x103D;
                    this.mWord[(int)CharIndex.ChangNyin] = 0x102F;
                    break;

                case 0x1089: // ×ႉ
                    this.mWord[(int)CharIndex.HaHtoe] = 0x103D;
                    this.mWord[(int)CharIndex.ChangNyin] = 0x1030;
                    break;

                case 0x108A: // ×�?ႊ
                    this.mWord[(int)CharIndex.WaSwal] = 0x103C;
                    this.mWord[(int)CharIndex.HaHtoe] = 0x103D;
                    break;

                case 0x108B: // �?×ႋ
                    this.mWord[(int)CharIndex.AThart] = 0x1064;
                    this.mWord[(int)CharIndex.LoneGyiTin] = 0x102D;
                    break;

                case 0x108C: // �?×ႌ
                    this.mWord[(int)CharIndex.AThart] = 0x1064;
                    this.mWord[(int)CharIndex.LoneGyiTin] = 0x102E;
                    break;

                case 0x108D: // ×�?
                    this.mWord[(int)CharIndex.AThart] = 0x1064;
                    this.mWord[(int)CharIndex.TeeTeeTin] = 0x1036;
                    break;

                case 0x108E: // ×ႎ
                    this.mWord[(int)CharIndex.LoneGyiTin] = 0x102D;
                    this.mWord[(int)CharIndex.TeeTeeTin] = 0x1036;
                    break;

                default:
                    this.mWord[(int)charIndex] = code;
                    break;
            }
        }
	
        public int[] getResult() {
        	
        	boolean fixAThart = ((this.mWord[(int)CharIndex.ThaWaiHtoe] == NULL_CHAR)
                    && (this.mWord[(int)CharIndex.YaPint] != NULL_CHAR)
                    && (this.mWord[(int)CharIndex.AThart] != NULL_CHAR));
        	
			if ((this.mWord[(int)CharIndex.AThart] == 0x1064)
				|| (this.mWord[(int)CharIndex.AThart] == 0x108D))
			{
				fixAThart = true;
			}
			
        	final boolean fixLoneGyiTin = (this.mWord[(int)CharIndex.YaPint] != NULL_CHAR);
        	
        	ArrayList<Integer> list = new ArrayList<Integer>();
        	
        	this.addToList(this.mWord[(int)CharIndex.ThaWaiHtoe], list);
            this.addToList(this.mWord[(int)CharIndex.RaRitt], list);
            this.addToList(this.mWord[(int)CharIndex.Consonant], list);
            this.addToList(this.mWord[(int)CharIndex.DoubleConsonant], list);
            this.addToList(this.mWord[(int)CharIndex.LowerConsonant], list);
            if(!fixLoneGyiTin) this.addToList(this.mWord[(int)CharIndex.LoneGyiTin], list);
            this.addToList(this.mWord[(int)CharIndex.HaHtoe], list);
            this.addToList(this.mWord[(int)CharIndex.WaSwal], list);
            this.addToList(this.mWord[(int)CharIndex.YaPint], list);
            if (fixAThart) this.addToList(this.mWord[(int)CharIndex.AThart], list);
            this.addToList(this.mWord[(int)CharIndex.TeeTeeTin], list);
            this.addToList(this.mWord[(int)CharIndex.ChangNyin], list);
            if(fixLoneGyiTin) this.addToList(this.mWord[(int)CharIndex.LoneGyiTin], list);
            this.addToList(this.mWord[(int)CharIndex.NoutPyit], list);
            this.addToList(this.mWord[(int)CharIndex.YeeKhya], list);
            if (!fixAThart) this.addToList(this.mWord[(int)CharIndex.AThart], list);
            this.addToList(this.mWord[(int)CharIndex.OutNyint], list);
            this.addToList(this.mWord[(int)CharIndex.WittSaPout], list);
            this.addToList(this.mWord[(int)CharIndex.Other], list);
        	
            int[] result = new int[list.size()];
            int count = 0;
            for (Integer n : list) {
            	result[count++] = n.intValue();
            }
            
            return result;
        }
	}
}
// } SMM