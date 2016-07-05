// Copyright 2013, NICT
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are
// met:
//
//     * Redistributions of source code must retain the above copyright
// notice, this list of conditions and the following disclaimer.
//     * Redistributions in binary form must reproduce the above
// copyright notice, this list of conditions and the following disclaimer
// in the documentation and/or other materials provided with the
// distribution.
//     * Neither the name of NICT nor the names of its
// contributors may be used to endorse or promote products derived from
// this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
/**
 *\file SignalFeVqHTK.h
 */
//-------------------------------------------------------------------
// Update    : 13 May, 2009 (Ver.0.0)
// Change    : STMD2-->iPhoneSTML 
//-------------------------------------------------------------------
//-------------------------------------------------------------------
// Update    : 7 Feburary, 2007 (Ver.0.1)
// Function  : Class definition for feature extraction and VQ suppression
//-------------------------------------------------------------------

#pragma once
#ifndef _32AA7B7B_F3FF_454f_A1DC_88266610AEDA_SIGNALFEVQHTK
#define _32AA7B7B_F3FF_454f_A1DC_88266610AEDA_SIGNALFEVQHTK

#include <string>
#include <vector>
#include "ScopedMem.h"
#include "Endian.h"
#include "SignalCommon.h"
#include "SignalConfig.h"
#include "SignalFeVq.h"
#include "ExtAdvFrontEnd_ATR.h"
#include "ExtCoder_VAD_ATR.h"
#include "MemoryStream.h"
#include "NsVadDef.h"


class CSignalFeVqHTK : public CSignalFeVq
{


	// ******** Member function ********
public:
	CSignalFeVqHTK(bool bBigEndian, bool bdoVQ);
	virtual ~CSignalFeVqHTK(void);

	bool	DoDSR212fromNsVad(NSVADRESULT& pData, bool isLastData);
	bool	GetResultMFCC(MFCCRESULT& pMFCCData);

	bool DSR_Init();
	bool DSR_Do(DSR_VEC& destMFCC, const DSR_Data* src,int Vad = 1);
	bool DSR_Flush(DSR_VEC& destMFCC);

private:

};
#endif // _32AA7B7B_F3FF_454f_A1DC_88266610AEDA_SIGNALFEVQ


