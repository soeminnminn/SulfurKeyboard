/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#ifndef LATINIME_ZAWGYI_CORRECTION_H
#define LATINIME_ZAWGYI_CORRECTION_H

namespace skeyboard {

#define NULL_CHAR 					0x00

#define CHARINDEX_ThaWaiHtoe 		0
#define CHARINDEX_RaRitt 			1
#define CHARINDEX_Consonant			2
#define CHARINDEX_DoubleConsonant		3
#define CHARINDEX_LowerConsonant		4
#define CHARINDEX_LoneGyiTin			5 
#define CHARINDEX_HaHtoe			6
#define CHARINDEX_WaSwal			7
#define CHARINDEX_YaPint				8
#define CHARINDEX_TeeTeeTin			9
#define CHARINDEX_ChangNyin			10
#define CHARINDEX_NoutPyit			11
#define CHARINDEX_YeeKhya			12
#define CHARINDEX_AThart			13
#define CHARINDEX_OutNyint			14
#define CHARINDEX_WittSaPout			15
#define CHARINDEX_Other 			16
#define WORD_LENGTH				17

class ZawgyiCorrection {
public:
    ZawgyiCorrection();
    ~ZawgyiCorrection();

    bool isMyChar(unsigned short code);
    bool isAlphabet(unsigned short code);
    int getWords(int *inCodes, int inCodesSize, unsigned short *outCodes, int outCodesSize);

private:
	bool isLetter(unsigned short code);
	int getCharIndex(int code);
	void addToList(int code);
	void getWordResult();
	void setValue(int charIndex, int code);
	bool hasConsonant();
	void reset();
	void fix();

	void fixThaWaiHtoe();
	void fixRaYitt();
	void fixConsonant();
	void fixLowerConsonant();
	void fixLoneGyiTin();
	void fixHaHtoe();
	void fixWaSwal();
	void fixYaPint();
	void fixChangNyin();
	void fixTeeTeeTin();
	void fixNoutPyit();
	void fixYeeKhya();
	void fixAThart();
	void fixOutNyint();
	void fixWittSaPout();
	void fixMixChar();

private:
	int mWord[WORD_LENGTH];
	unsigned short *mOutCodes;
	int mOutCodesSize;
};

// ----------------------------------------------------------------------------

}; // namespace skeyboard

#endif // LATINIME_ZAWGYI_CORRECTION_H
