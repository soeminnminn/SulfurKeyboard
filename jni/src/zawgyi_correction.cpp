/*
**
** Copyright 2009, The Android Open Source Project
**
** Licensed under the Apache License, Version 2.0 (the "License");
** you may not use this file except in compliance with the License.
** You may obtain a copy of the License at
**
**     http://www.apache.org/licenses/LICENSE-2.0
**
** Unless required by applicable law or agreed to in writing, software
** distributed under the License is distributed on an "AS IS" BASIS,
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
** See the License for the specific language governing permissions and
** limitations under the License.
*/
//#include "stdafx.h"

#include <assert.h>
#include <ctype.h>
#include <math.h>
#include <stdio.h>
//#include <sys/mman.h>
#include <string.h>
//#define LOG_TAG "dictionary.cpp"
//#include <cutils/log.h>
#define LOGI

#include "zawgyi_correction.h"
//#include "basechars.h"
//#include "char_utils.h"


#define DEBUG_DICT 0
#define DICTIONARY_VERSION_MIN 200
#define DICTIONARY_HEADER_SIZE 2
#define NOT_VALID_WORD -99

namespace skeyboard {
	
ZawgyiCorrection::ZawgyiCorrection()
	: mOutCodes(NULL)
{
};

ZawgyiCorrection::~ZawgyiCorrection()
{
};

bool ZawgyiCorrection::isMyChar(unsigned short code)
{
	return (code >= 0x1000 && code <= 0x109F) || (code >= 0xAA60 && code <= 0xAA7B);
};

bool ZawgyiCorrection::isAlphabet(unsigned short code)
{
	int charIndex = getCharIndex(code);
	switch(charIndex) {
		case CHARINDEX_ThaWaiHtoe:
		case CHARINDEX_RaRitt:
		case CHARINDEX_Consonant:
		case CHARINDEX_DoubleConsonant:
			return true;
			
		case CHARINDEX_Other:
		default:
			return isLetter(code);
	}
};

int ZawgyiCorrection::getWords(int *inCodes, int inCodesSize, unsigned short *outCodes, int outCodesSize)
{
	if(inCodesSize < 2) return 0;
	
	mOutCodes = new unsigned short[inCodesSize + WORD_LENGTH];
	mOutCodesSize = 0;
	reset();
	
	//int* codes = inCodes;
	for(int i = 0; i < inCodesSize; i++)
	{
		//int code = (*codes)++;
		int code = inCodes[i];
		int charIndex = getCharIndex(code);

		bool firstChar = ((charIndex == CHARINDEX_ThaWaiHtoe) 
                || (charIndex == CHARINDEX_RaRitt) 
                || (charIndex == CHARINDEX_Consonant) 
                || (charIndex == CHARINDEX_DoubleConsonant)
                || (charIndex == CHARINDEX_Other));

		if (hasConsonant() && firstChar) 
		{
			fix();
			getWordResult();
			reset();
		}

		if (charIndex == CHARINDEX_Other)
            addToList(code);
        else
        	setValue((int)charIndex, code);
	}

	fix();
	getWordResult();
	reset();

	// getResult
	outCodes = mOutCodes + 1;
	*outCodes = 0;
	outCodesSize = mOutCodesSize;

	return 1;
};

bool ZawgyiCorrection::isLetter(unsigned short code)
{
	return true;
};

void ZawgyiCorrection::addToList(int code)
{
	mOutCodes[mOutCodesSize++] = code;
};

void ZawgyiCorrection::getWordResult()
{
	bool fixAThart = ((mWord[(int)CHARINDEX_ThaWaiHtoe] == NULL_CHAR)
            && (mWord[(int)CHARINDEX_YaPint] != NULL_CHAR)
            && (mWord[(int)CHARINDEX_AThart] != NULL_CHAR));
	
	if ((mWord[(int)CHARINDEX_AThart] == 0x1064)
		|| (mWord[(int)CHARINDEX_AThart] == 0x108D))
	{
		fixAThart = true;
	}
	
	bool fixLoneGyiTin = (mWord[(int)CHARINDEX_YaPint] != NULL_CHAR);
	
	addToList(mWord[(int)CHARINDEX_ThaWaiHtoe]);
    addToList(mWord[(int)CHARINDEX_RaRitt]);
    addToList(mWord[(int)CHARINDEX_Consonant]);
    addToList(mWord[(int)CHARINDEX_DoubleConsonant]);
    addToList(mWord[(int)CHARINDEX_LowerConsonant]);
    if(!fixLoneGyiTin) addToList(mWord[(int)CHARINDEX_LoneGyiTin]);
    addToList(mWord[(int)CHARINDEX_HaHtoe]);
    addToList(mWord[(int)CHARINDEX_WaSwal]);
    addToList(mWord[(int)CHARINDEX_YaPint]);
    if (fixAThart) addToList(mWord[(int)CHARINDEX_AThart]);
    addToList(mWord[(int)CHARINDEX_TeeTeeTin]);
    addToList(mWord[(int)CHARINDEX_ChangNyin]);
    if(fixLoneGyiTin) addToList(mWord[(int)CHARINDEX_LoneGyiTin]);
    addToList(mWord[(int)CHARINDEX_NoutPyit]);
    addToList(mWord[(int)CHARINDEX_YeeKhya]);
    if (!fixAThart) addToList(mWord[(int)CHARINDEX_AThart]);
    addToList(mWord[(int)CHARINDEX_OutNyint]);
    addToList(mWord[(int)CHARINDEX_WittSaPout]);
    addToList(mWord[(int)CHARINDEX_Other]);
};

int ZawgyiCorrection::getCharIndex(int code) 
{
	if ((code >= 0x1000 && code <= 0x102A) 
		|| (code >= 0x103F && code <= 0x1057) 
		|| (code >= 0xAA60 && code <= 0xAA7B))
	{
		return CHARINDEX_Consonant;
	}
	switch(code)
	{
		case 0x1031:
			return CHARINDEX_ThaWaiHtoe;
			
		case 0x103C:
			return CHARINDEX_RaRitt;
			
		case 0x102D:
		case 0x102E:
			return CHARINDEX_LoneGyiTin;
			
		case 0x103E:
			return CHARINDEX_HaHtoe;
			
		case 0x103D:
			return CHARINDEX_WaSwal;
			
		case 0x103B:
			return CHARINDEX_YaPint;
			
		case 0x1036:
			return CHARINDEX_TeeTeeTin;
			
		case 0x102F:
		case 0x1030:
			return CHARINDEX_ChangNyin;
			
		case 0x1032:
			return CHARINDEX_NoutPyit;
			
		case 0x102B:
		case 0x102C:
			return CHARINDEX_YeeKhya;
			
		case 0x103A:
			return CHARINDEX_AThart;
			
		case 0x1037:
			return CHARINDEX_OutNyint;
			
		case 0x1038:
			return CHARINDEX_WittSaPout;
			
		default:
			break;
	}
	return CHARINDEX_Other;
};


bool ZawgyiCorrection::hasConsonant()
{
	return (mWord[(int)CHARINDEX_Consonant] != NULL_CHAR) || (mWord[(int)CHARINDEX_DoubleConsonant] != NULL_CHAR);
};

void ZawgyiCorrection::reset()
{
	for(int i = 0; i < WORD_LENGTH; i++)
	{
		mWord[i] = NULL_CHAR;
	}
};

void ZawgyiCorrection::setValue(int charIndex, int code)
{
	if(charIndex < 0) return;
        if(charIndex > CHARINDEX_Other) return;
        	
	switch (code)
	{
	case 0x106E: // ၮ
		mWord[(int)CHARINDEX_Consonant] = 0x100D;
		mWord[(int)CHARINDEX_LowerConsonant] = code;
		break;

	case 0x106F: // ၯ
		mWord[(int)CHARINDEX_Consonant] = 0x100E;
		mWord[(int)CHARINDEX_LowerConsonant] = code;
		break;

	case 0x1097: // ႗
		mWord[(int)CHARINDEX_Consonant] = 0x100B;
		mWord[(int)CHARINDEX_LowerConsonant] = code;
		break;

	case 0x102A: // ဪ
		mWord[(int)CHARINDEX_ThaWaiHtoe] = 0x1031;
		mWord[(int)CHARINDEX_RaRitt] = 0x107E;
		mWord[(int)CHARINDEX_DoubleConsonant] = 0x101E;
		mWord[(int)CHARINDEX_YeeKhya] = 0x102C;
		mWord[(int)CHARINDEX_AThart] = 0x1039;
		break;

	case 0x1029: // ဩ
		mWord[(int)CHARINDEX_RaRitt] = 0x107E;
		mWord[(int)CHARINDEX_DoubleConsonant] = 0x101E;
		break;

	case 0x1026: // ဦ
		mWord[(int)CHARINDEX_LoneGyiTin] = 0x102E;
		mWord[(int)CHARINDEX_Consonant] = 0x1025;
		break;

	// TODO Need to fix up
	case 0x104E: // ၎
		mWord[(int)CHARINDEX_Consonant] = 0x1044;
		mWord[(int)CHARINDEX_LowerConsonant] = 0x1004;
		mWord[(int)CHARINDEX_AThart] = 0x1039;
		mWord[(int)CHARINDEX_WittSaPout] = 0x1038;
		break;

	case 0x105A: // xၚ
		mWord[(int)CHARINDEX_YeeKhya] = 0x102C;
		mWord[(int)CHARINDEX_AThart] = 0x1039;
		break;

	case 0x1088: // ‍×ႈ
		mWord[(int)CHARINDEX_HaHtoe] = 0x103D;
		mWord[(int)CHARINDEX_ChangNyin] = 0x102F;
		break;

	case 0x1089: // ×ႉ
		mWord[(int)CHARINDEX_HaHtoe] = 0x103D;
		mWord[(int)CHARINDEX_ChangNyin] = 0x1030;
		break;

	case 0x108A: // ×‍ႊ
		mWord[(int)CHARINDEX_WaSwal] = 0x103C;
		mWord[(int)CHARINDEX_HaHtoe] = 0x103D;
		break;

	case 0x108B: // ‍×ႋ
		mWord[(int)CHARINDEX_AThart] = 0x1064;
		mWord[(int)CHARINDEX_LoneGyiTin] = 0x102D;
		break;

	case 0x108C: // ‍×ႌ
		mWord[(int)CHARINDEX_AThart] = 0x1064;
		mWord[(int)CHARINDEX_LoneGyiTin] = 0x102E;
		break;

	case 0x108D: // ×ႍ
		mWord[(int)CHARINDEX_AThart] = 0x1064;
		mWord[(int)CHARINDEX_TeeTeeTin] = 0x1036;
		break;

	case 0x108E: // ×ႎ
		mWord[(int)CHARINDEX_LoneGyiTin] = 0x102D;
		mWord[(int)CHARINDEX_TeeTeeTin] = 0x1036;
		break;

	default:
		mWord[(int)charIndex] = code;
		break;
	}
};

void ZawgyiCorrection::fix()
{
	if((mWord[(int)CHARINDEX_Consonant] == NULL_CHAR) && (mWord[(int)CHARINDEX_DoubleConsonant] == NULL_CHAR)) return;
	
	fixThaWaiHtoe();
	fixRaYitt();
	fixConsonant();
	fixLowerConsonant();
	fixLoneGyiTin();
	fixHaHtoe();
	fixWaSwal();
	fixYaPint();
	fixChangNyin();
	fixTeeTeeTin();
	fixNoutPyit();
	fixYeeKhya();
	fixAThart();
	fixOutNyint();
	fixWittSaPout();
	fixMixChar();
};

void ZawgyiCorrection::fixThaWaiHtoe()
{
	if (mWord[(int)CHARINDEX_ThaWaiHtoe] == NULL_CHAR) return;
	// No Change
};

void ZawgyiCorrection::fixRaYitt()
{
	if (mWord[(int)CHARINDEX_RaRitt] == NULL_CHAR) return;
	
	bool upper = ((mWord[(int)CHARINDEX_LoneGyiTin] != NULL_CHAR) 
                                || (mWord[(int)CHARINDEX_NoutPyit] != NULL_CHAR)
                                || (mWord[(int)CHARINDEX_TeeTeeTin] != NULL_CHAR)
                                || (mWord[(int)CHARINDEX_AThart] == 0x1064)
                                || (mWord[(int)CHARINDEX_AThart] == 0x108D));

	bool lower = ((mWord[(int)CHARINDEX_WaSwal] != NULL_CHAR) 
					|| (mWord[(int)CHARINDEX_LowerConsonant] != NULL_CHAR));

	if (mWord[(int)CHARINDEX_Consonant] != NULL_CHAR)
	{
		if (upper && lower)
		{
			mWord[(int)CHARINDEX_RaRitt] = 0x1083;
		}
		else if (upper)
		{
			mWord[(int)CHARINDEX_RaRitt] = 0x107F;
		}
		else if (lower)
		{
			mWord[(int)CHARINDEX_RaRitt] = 0x1081;
		}
		else
		{
			mWord[(int)CHARINDEX_RaRitt] = 0x103B;
		}
	}
	else
	{
		if (upper && lower)
		{
			mWord[(int)CHARINDEX_RaRitt] = 0x1084;
		}
		else if (upper)
		{
			mWord[(int)CHARINDEX_RaRitt] = 0x1080;
		}
		else if (lower)
		{
			mWord[(int)CHARINDEX_RaRitt] = 0x1082;
		}
		else
		{
			mWord[(int)CHARINDEX_RaRitt] = 0x107E;
		}
	}
};

void ZawgyiCorrection::fixConsonant()
{
	if ((mWord[(int)CHARINDEX_Consonant] == NULL_CHAR) && (mWord[(int)CHARINDEX_DoubleConsonant] == NULL_CHAR)) return;

	if ((mWord[(int)CHARINDEX_DoubleConsonant] == 0x100A) 
	    || (mWord[(int)CHARINDEX_DoubleConsonant] == 0x106B)) // ည
	{
		if((mWord[(int)CHARINDEX_WaSwal] != NULL_CHAR)
		  || (mWord[(int)CHARINDEX_LowerConsonant] != NULL_CHAR))
		{
			mWord[(int)CHARINDEX_DoubleConsonant] = 0x106B;
		}
		else
		{
			mWord[(int)CHARINDEX_DoubleConsonant] = 0x100A;
		}
	}
	
	if ((mWord[(int)CHARINDEX_Consonant] == 0x1014) 
	    || (mWord[(int)CHARINDEX_Consonant] == 0x108F)) // န
	{
		if((mWord[(int)CHARINDEX_RaRitt] != NULL_CHAR)
		   || (mWord[(int)CHARINDEX_WaSwal] != NULL_CHAR)
		   || (mWord[(int)CHARINDEX_HaHtoe] != NULL_CHAR)
		   || (mWord[(int)CHARINDEX_YaPint] != NULL_CHAR)
		   || (mWord[(int)CHARINDEX_ChangNyin] != NULL_CHAR)
		   || (mWord[(int)CHARINDEX_LowerConsonant] != NULL_CHAR)
		   )
		{
			mWord[(int)CHARINDEX_Consonant] = 0x108F;
		}
		else
		{
			mWord[(int)CHARINDEX_Consonant] = 0x1014;
		}
	}
	if ((mWord[(int)CHARINDEX_Consonant] == 0x101B) 
	    || (mWord[(int)CHARINDEX_Consonant] == 0x1090)) // ရ
	{
		if(mWord[(int)CHARINDEX_ChangNyin] != NULL_CHAR)
		{
			mWord[(int)CHARINDEX_Consonant] = 0x1090;
		}
		else
		{
			mWord[(int)CHARINDEX_Consonant] = 0x101B;
		}
	}
	if ((mWord[(int)CHARINDEX_Consonant] == 0x1025) 
	    || (mWord[(int)CHARINDEX_Consonant] == 0x106A)) // ဥ
	{
		if((mWord[(int)CHARINDEX_WaSwal] != NULL_CHAR)
		  || (mWord[(int)CHARINDEX_LowerConsonant] != NULL_CHAR))
		{
			mWord[(int)CHARINDEX_Consonant] = 0x106A;
		}
		else
		{
			mWord[(int)CHARINDEX_Consonant] = 0x1025;
		}
	}
};

void ZawgyiCorrection::fixLowerConsonant()
{
	if (mWord[(int)CHARINDEX_LowerConsonant] == NULL_CHAR) return;

            if ((mWord[(int)CHARINDEX_LowerConsonant] == 0x1066)
                || (mWord[(int)CHARINDEX_LowerConsonant] == 0x1067))
            {
                if (mWord[(int)CHARINDEX_DoubleConsonant] != NULL_CHAR)
		{
                    mWord[(int)CHARINDEX_LowerConsonant] = 0x1066;
		}
                else
		{
                    mWord[(int)CHARINDEX_LowerConsonant] = 0x1067;
		}
            }

            if ((mWord[(int)CHARINDEX_LowerConsonant] == 0x1071)
                || (mWord[(int)CHARINDEX_LowerConsonant] == 0x1072))
            {
                if (mWord[(int)CHARINDEX_DoubleConsonant] != NULL_CHAR)
		{
                    mWord[(int)CHARINDEX_LowerConsonant] = 0x1071;
		}
                else
		{
                    mWord[(int)CHARINDEX_LowerConsonant] = 0x1072;
		}
            }

            if ((mWord[(int)CHARINDEX_LowerConsonant] == 0x1073)
                || (mWord[(int)CHARINDEX_LowerConsonant] == 0x1074))
            {
                if (mWord[(int)CHARINDEX_DoubleConsonant] != NULL_CHAR)
		{
                    mWord[(int)CHARINDEX_LowerConsonant] = 0x1073;
		}
                else
		{
                    mWord[(int)CHARINDEX_LowerConsonant] = 0x1074;
		}
            }

            if ((mWord[(int)CHARINDEX_LowerConsonant] == 0x107B)
                || (mWord[(int)CHARINDEX_LowerConsonant] == 0x1093))
            {
                if (mWord[(int)CHARINDEX_DoubleConsonant] != NULL_CHAR)
		{
                    mWord[(int)CHARINDEX_LowerConsonant] = 0x107B;
		}
                else
		{
                    mWord[(int)CHARINDEX_LowerConsonant] = 0x1093;
		}
            }
};

void ZawgyiCorrection::fixLoneGyiTin()
{
	if (mWord[(int)CHARINDEX_LoneGyiTin] == NULL_CHAR) return;
			
	if (mWord[(int)CHARINDEX_AThart] == 0x1064)
	{
		if (mWord[(int)CHARINDEX_LoneGyiTin] == 0x102D)
		{
			mWord[(int)CHARINDEX_LoneGyiTin] = 0x108B;
		}
		else
		{
			mWord[(int)CHARINDEX_LoneGyiTin] = 0x108C;
		}
		mWord[(int)CHARINDEX_AThart] = NULL_CHAR;
	}
	else if (mWord[(int)CHARINDEX_TeeTeeTin] != NULL_CHAR)
	{
		mWord[(int)CHARINDEX_LoneGyiTin] = 0x108E;
		mWord[(int)CHARINDEX_TeeTeeTin] = NULL_CHAR;
	}
	else if ((mWord[(int)CHARINDEX_LoneGyiTin] == 0x102D) 
		 || (mWord[(int)CHARINDEX_LoneGyiTin] == 0x108B)
		 || (mWord[(int)CHARINDEX_LoneGyiTin] == 0x108E))
	{
		mWord[(int)CHARINDEX_LoneGyiTin] = 0x102D;
	}
	else if ((mWord[(int)CHARINDEX_LoneGyiTin] == 0x102E)
		 || (mWord[(int)CHARINDEX_LoneGyiTin] == 0x108C))
	{
		mWord[(int)CHARINDEX_LoneGyiTin] = 0x102E;
	}
};

void ZawgyiCorrection::fixHaHtoe()
{
	if (mWord[(int)CHARINDEX_HaHtoe] == NULL_CHAR) return;
			
	if ((mWord[(int)CHARINDEX_RaRitt] != NULL_CHAR)
		|| (mWord[(int)CHARINDEX_DoubleConsonant] == 0x100A)
		|| (mWord[(int)CHARINDEX_DoubleConsonant] == 0x1009)
		|| (mWord[(int)CHARINDEX_DoubleConsonant] == 0x106B)
		|| (mWord[(int)CHARINDEX_Consonant] == 0x100C)
		|| (mWord[(int)CHARINDEX_DoubleConsonant] == 0x1029)
		|| (mWord[(int)CHARINDEX_DoubleConsonant] == 0x102A)
		)
	{
		mWord[(int)CHARINDEX_HaHtoe] = 0x1087;
	}
	else
	{
		mWord[(int)CHARINDEX_HaHtoe] = 0x103D;
	}
};

void ZawgyiCorrection::fixWaSwal()
{
	if (mWord[(int)CHARINDEX_WaSwal] == NULL_CHAR) return;
			
	if (mWord[(int)CHARINDEX_HaHtoe] != NULL_CHAR)
	{
		mWord[(int)CHARINDEX_WaSwal] = 0x108A;
		mWord[(int)CHARINDEX_HaHtoe] = NULL_CHAR;
	}
	else
	{
		mWord[(int)CHARINDEX_WaSwal] = 0x103C;
	}
};

void ZawgyiCorrection::fixYaPint()
{
	if (mWord[(int)CHARINDEX_YaPint] == NULL_CHAR) return;

	if (mWord[(int)CHARINDEX_WaSwal] != NULL_CHAR)
	{
		mWord[(int)CHARINDEX_YaPint] = 0x107D;
	}
	else
	{
		mWord[(int)CHARINDEX_YaPint] = 0x103A;
	}
};

void ZawgyiCorrection::fixChangNyin()
{
	if (mWord[(int)CHARINDEX_ChangNyin] == NULL_CHAR) return;
			
	bool isLong = ((mWord[(int)CHARINDEX_RaRitt] != NULL_CHAR)
		|| (mWord[(int)CHARINDEX_YaPint] != NULL_CHAR)
		|| (mWord[(int)CHARINDEX_WaSwal] != NULL_CHAR)
		|| (mWord[(int)CHARINDEX_LowerConsonant] != NULL_CHAR)

		|| (mWord[(int)CHARINDEX_Consonant] == 0x1008) // ဈ
		|| (mWord[(int)CHARINDEX_Consonant] == 0x100B) // ဋ
		|| (mWord[(int)CHARINDEX_Consonant] == 0x100C) // ဌ
		|| (mWord[(int)CHARINDEX_Consonant] == 0x100D) // ဍ
		|| (mWord[(int)CHARINDEX_Consonant] == 0x1020) // ဠ
		|| (mWord[(int)CHARINDEX_Consonant] == 0x1025) // ရ
		|| (mWord[(int)CHARINDEX_Consonant] == 0x1026) // ႐
		|| (mWord[(int)CHARINDEX_Consonant] == 0x106A) // ၌
		|| (mWord[(int)CHARINDEX_Consonant] == 0x104C) // ၍
		|| (mWord[(int)CHARINDEX_Consonant] == 0x104D) // ႒
		|| (mWord[(int)CHARINDEX_Consonant] == 0x1092) // á‚’
		|| (mWord[(int)CHARINDEX_Consonant] == 0x106E) // ၮ
		|| (mWord[(int)CHARINDEX_Consonant] == 0x106F) // ၯ
		|| (mWord[(int)CHARINDEX_Consonant] == 0x1097) // ႗

		|| (mWord[(int)CHARINDEX_Consonant] == 0x1042) // ၂
		|| (mWord[(int)CHARINDEX_Consonant] == 0x1043) // ၃
		|| (mWord[(int)CHARINDEX_Consonant] == 0x1044) // ၄
		|| (mWord[(int)CHARINDEX_Consonant] == 0x1045) // ၅
		|| (mWord[(int)CHARINDEX_Consonant] == 0x1046) // ၆
		|| (mWord[(int)CHARINDEX_Consonant] == 0x1047) // ၇
		|| (mWord[(int)CHARINDEX_Consonant] == 0x1049) // ၉

		|| (mWord[(int)CHARINDEX_DoubleConsonant] == 0x100A) // ည
		|| (mWord[(int)CHARINDEX_DoubleConsonant] == 0x1009) // ဉ
		|| (mWord[(int)CHARINDEX_DoubleConsonant] == 0x106B) // ၫ
		|| (mWord[(int)CHARINDEX_DoubleConsonant] == 0x1023) // ဣ
		|| (mWord[(int)CHARINDEX_DoubleConsonant] == 0x1024) // ဤ
		|| (mWord[(int)CHARINDEX_DoubleConsonant] == 0x1029) // ဩ
		|| (mWord[(int)CHARINDEX_DoubleConsonant] == 0x102A) // ဪ
		|| (mWord[(int)CHARINDEX_DoubleConsonant] == 0x104E) // ၎
		|| (mWord[(int)CHARINDEX_DoubleConsonant] == 0x1091) // ႑
		);
			
	if ((mWord[(int)CHARINDEX_ChangNyin] == 0x102F) 
		|| (mWord[(int)CHARINDEX_ChangNyin] == 0x1033))
	{
		if (isLong)
		{
			mWord[(int)CHARINDEX_ChangNyin] = 0x1033;
		}
		else if(mWord[(int)CHARINDEX_HaHtoe] != NULL_CHAR) 
		{
			mWord[(int)CHARINDEX_ChangNyin] = 0x1088;
			mWord[(int)CHARINDEX_HaHtoe] = NULL_CHAR;
		}
		else
		{
			mWord[(int)CHARINDEX_ChangNyin] = 0x102F;
		}
	}
	else // 0x1030 | 0x1034
	{
		if (isLong)
		{
			mWord[(int)CHARINDEX_ChangNyin] = 0x1034;
		}
		else if(mWord[(int)CHARINDEX_HaHtoe] != NULL_CHAR) 
		{
			mWord[(int)CHARINDEX_ChangNyin] = 0x1089;
			mWord[(int)CHARINDEX_HaHtoe] = NULL_CHAR;
		}
		else
		{
			mWord[(int)CHARINDEX_ChangNyin] = 0x1030;
		}
	}
};

void ZawgyiCorrection::fixTeeTeeTin()
{
	if (mWord[(int)CHARINDEX_TeeTeeTin] == NULL_CHAR) return;
	// No Change 
};

void ZawgyiCorrection::fixNoutPyit()
{
	if (mWord[(int)CHARINDEX_NoutPyit] == NULL_CHAR) return;
	// No Change
};

void ZawgyiCorrection::fixYeeKhya()
{
	if (mWord[(int)CHARINDEX_YeeKhya] == NULL_CHAR) return;
			
	if((	(mWord[(int)CHARINDEX_Consonant] == 0x1001)  // ခ
		|| (mWord[(int)CHARINDEX_Consonant] == 0x1002)  // ဂ
		|| (mWord[(int)CHARINDEX_Consonant] == 0x1004)  // င
		|| (mWord[(int)CHARINDEX_Consonant] == 0x1012)  // ဒ
		|| (mWord[(int)CHARINDEX_Consonant] == 0x1015)  // ပ
		|| (mWord[(int)CHARINDEX_Consonant] == 0x101D)) // ဝ
		&& 
		(  (mWord[(int)CHARINDEX_RaRitt] == NULL_CHAR)
		&& (mWord[(int)CHARINDEX_LowerConsonant] == NULL_CHAR)
		&& (mWord[(int)CHARINDEX_LoneGyiTin] == NULL_CHAR)
		&& (mWord[(int)CHARINDEX_HaHtoe] == NULL_CHAR)
		&& (mWord[(int)CHARINDEX_WaSwal] == NULL_CHAR)
		&& (mWord[(int)CHARINDEX_YaPint] == NULL_CHAR)
		&& (mWord[(int)CHARINDEX_ChangNyin] == NULL_CHAR)
	))
	{
		if ((mWord[(int)CHARINDEX_YeeKhya] != NULL_CHAR)
			&& (mWord[(int)CHARINDEX_AThart] == 0x1039))
		{
			mWord[(int)CHARINDEX_YeeKhya] = 0x105A;
			mWord[(int)CHARINDEX_AThart] = NULL_CHAR;
		}
		else
		{
			mWord[(int)CHARINDEX_YeeKhya] = 0x102B;
		}
	}
	else
	{
		mWord[(int)CHARINDEX_YeeKhya] = 0x102C;
	}
};

void ZawgyiCorrection::fixAThart()
{
	if (mWord[(int)CHARINDEX_AThart] == NULL_CHAR) return;
	if (mWord[(int)CHARINDEX_AThart] == 0x1064) 
	{
		if (mWord[(int)CHARINDEX_TeeTeeTin] != NULL_CHAR) 
		{
			mWord[(int)CHARINDEX_AThart] = 0x108D;
			mWord[(int)CHARINDEX_TeeTeeTin] = NULL_CHAR;
		} 
		else 
		{
			mWord[(int)CHARINDEX_AThart] = 0x1064;
		}
	}
};

void ZawgyiCorrection::fixOutNyint()
{
	if (mWord[(int)CHARINDEX_OutNyint] == NULL_CHAR) return;
	if ((mWord[(int)CHARINDEX_YeeKhya] == NULL_CHAR) && ( 
		(mWord[(int)CHARINDEX_RaRitt] != NULL_CHAR)
		|| (mWord[(int)CHARINDEX_WaSwal] != NULL_CHAR)
		|| (mWord[(int)CHARINDEX_YaPint] != NULL_CHAR)
		|| ((mWord[(int)CHARINDEX_ChangNyin] != NULL_CHAR) && (mWord[(int)CHARINDEX_ChangNyin] != 0x102F))
		|| (mWord[(int)CHARINDEX_LowerConsonant] != NULL_CHAR)

		|| (mWord[(int)CHARINDEX_Consonant] == 0x1008) // ဈ
		|| (mWord[(int)CHARINDEX_Consonant] == 0x100B)  // ဋ
		|| (mWord[(int)CHARINDEX_Consonant] == 0x100C)  // ဌ
		|| (mWord[(int)CHARINDEX_Consonant] == 0x100D)  // ဍ
		|| (mWord[(int)CHARINDEX_Consonant] == 0x1020)  // ဠ
		|| (mWord[(int)CHARINDEX_Consonant] == 0x101B)  // ရ
		|| (mWord[(int)CHARINDEX_Consonant] == 0x1090) // ႐
		|| (mWord[(int)CHARINDEX_Consonant] == 0x104C) // ၌
		|| (mWord[(int)CHARINDEX_Consonant] == 0x104D) // ၍
		|| (mWord[(int)CHARINDEX_Consonant] == 0x1092) // ႒
		|| (mWord[(int)CHARINDEX_Consonant] == 0x106E) // ၮ
		|| (mWord[(int)CHARINDEX_Consonant] == 0x106F) // ၯ
		|| (mWord[(int)CHARINDEX_Consonant] == 0x1097) // ႗

		|| (mWord[(int)CHARINDEX_Consonant] == 0x1042) // ၂
		|| (mWord[(int)CHARINDEX_Consonant] == 0x1043) // ၃
		|| (mWord[(int)CHARINDEX_Consonant] == 0x1044) // ၄
		|| (mWord[(int)CHARINDEX_Consonant] == 0x1045) // ၅
		|| (mWord[(int)CHARINDEX_Consonant] == 0x1046) // ၆
		|| (mWord[(int)CHARINDEX_Consonant] == 0x1047) // ၇
		|| (mWord[(int)CHARINDEX_Consonant] == 0x1049) // ၉

		|| (mWord[(int)CHARINDEX_DoubleConsonant] == 0x1023) // ဣ
		|| (mWord[(int)CHARINDEX_DoubleConsonant] == 0x1024) // ဤ
		|| (mWord[(int)CHARINDEX_DoubleConsonant] == 0x1029) // ဩ
		|| (mWord[(int)CHARINDEX_DoubleConsonant] == 0x102A) // ဪ
		|| (mWord[(int)CHARINDEX_DoubleConsonant] == 0x104E) // ၎
		|| (mWord[(int)CHARINDEX_DoubleConsonant] == 0x1091) // ႑
	))
	{
        mWord[(int)CHARINDEX_OutNyint] = 0x1095;
	}
	else if ((mWord[(int)CHARINDEX_YeeKhya] == NULL_CHAR) && ( 
		(mWord[(int)CHARINDEX_HaHtoe] != NULL_CHAR)
		|| (mWord[(int)CHARINDEX_ChangNyin] == 0x102F)
		|| (mWord[(int)CHARINDEX_Consonant] == 0x1014)) // န
	)
	{
		mWord[(int)CHARINDEX_OutNyint] = 0x1094;
	}
	else
	{
		mWord[(int)CHARINDEX_OutNyint] = 0x1037;
    }
};

void ZawgyiCorrection::fixWittSaPout()
{
	if (mWord[(int)CHARINDEX_WittSaPout] == NULL_CHAR) return;
	// No Change
};

void ZawgyiCorrection::fixMixChar()
{
	if((mWord[(int)CHARINDEX_Consonant] == 0x100D)
		&& (mWord[(int)CHARINDEX_LowerConsonant] == 0x106E)) 
	{
		mWord[(int)CHARINDEX_Consonant] = 0x106E;
		mWord[(int)CHARINDEX_LowerConsonant] = NULL_CHAR;
	}
	
	if((mWord[(int)CHARINDEX_Consonant] == 0x100E) 
		&& (mWord[(int)CHARINDEX_LowerConsonant] == 0x106F)) 
	{
		mWord[(int)CHARINDEX_Consonant] = 0x106F;
		mWord[(int)CHARINDEX_LowerConsonant] = NULL_CHAR;
	}

	if((mWord[(int)CHARINDEX_Consonant] == 0x100B) 
		&& (mWord[(int)CHARINDEX_LowerConsonant] == 0x1097))
	{
		mWord[(int)CHARINDEX_Consonant] = 0x1097;
		mWord[(int)CHARINDEX_LowerConsonant] = NULL_CHAR;
	}

	if((mWord[(int)CHARINDEX_ThaWaiHtoe] == 0x1031)
		&& (mWord[(int)CHARINDEX_RaRitt] == 0x107E)
		&& (mWord[(int)CHARINDEX_DoubleConsonant] == 0x101E)
		&& (mWord[(int)CHARINDEX_YeeKhya] == 0x102C)
		&& (mWord[(int)CHARINDEX_AThart] == 0x1039)) 
	{
		mWord[(int)CHARINDEX_ThaWaiHtoe] = NULL_CHAR;
		mWord[(int)CHARINDEX_RaRitt] = NULL_CHAR;
		mWord[(int)CHARINDEX_DoubleConsonant] = 0x102A;
		mWord[(int)CHARINDEX_YeeKhya] = NULL_CHAR;
		mWord[(int)CHARINDEX_AThart] = NULL_CHAR;
	}

	if((mWord[(int)CHARINDEX_RaRitt] == 0x107E)
		&& (mWord[(int)CHARINDEX_DoubleConsonant] == 0x101E)) 
	{
		mWord[(int)CHARINDEX_RaRitt] = NULL_CHAR;
		mWord[(int)CHARINDEX_DoubleConsonant] = 0x1029;
	}

	if((mWord[(int)CHARINDEX_LoneGyiTin] == 0x102E)
		&& (mWord[(int)CHARINDEX_Consonant] == 0x1025)) 
	{
		mWord[(int)CHARINDEX_LoneGyiTin] = NULL_CHAR;
		mWord[(int)CHARINDEX_Consonant] = 0x1026;
	}

	if((mWord[(int)CHARINDEX_Consonant] == 0x1044)
		&& (mWord[(int)CHARINDEX_LowerConsonant] == 0x1004)
		&& (mWord[(int)CHARINDEX_AThart] == 0x1039)
		&& (mWord[(int)CHARINDEX_WittSaPout] == 0x1038))
	{
		mWord[(int)CHARINDEX_Consonant] = 0x104E;
		mWord[(int)CHARINDEX_LowerConsonant] = NULL_CHAR;
		mWord[(int)CHARINDEX_AThart] = NULL_CHAR;
		mWord[(int)CHARINDEX_WittSaPout] = NULL_CHAR;
	}
	
	if ((mWord[(int)CHARINDEX_Consonant] == 0x1005)
		&& (mWord[(int)CHARINDEX_YaPint] == 0x103A))
	{
		mWord[(int)CHARINDEX_Consonant] = 0x1008;
		mWord[(int)CHARINDEX_YaPint] = NULL_CHAR;
	}
};

} // namespace skeyboard
