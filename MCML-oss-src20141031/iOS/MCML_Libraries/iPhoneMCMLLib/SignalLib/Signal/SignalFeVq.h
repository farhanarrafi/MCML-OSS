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
 *\file SignalFeVq.h
 */
//-------------------------------------------------------------------
// 更新日時  ：2009年05月13日(Ver.0.0)
// 更新内容  ：STMD2-->iPhoneSTML 
//-------------------------------------------------------------------
//-------------------------------------------------------------------
// 更新日時  ：2007年02月07日(Ver.0.1)
// 機能      ：特徴量抽出、VQ圧縮クラスの定義
//-------------------------------------------------------------------

#pragma once
#ifndef _32AA7B7B_F3FF_454f_A1DC_88266610AEDA_SIGNALFEVQ
#define _32AA7B7B_F3FF_454f_A1DC_88266610AEDA_SIGNALFEVQ

#include <string>
#include <vector>
#include "ScopedMem.h"
#include "SignalCommon.h"
#include "SignalFeVq.h"
#include "SignalConfigFeVq.h"
#include "ExtAdvFrontEnd_ATR.h"
#include "ExtCoder_VAD_ATR.h"
#include "MemoryStream.h"
#include "NsVadDef.h"

//////////////////////////////////////////////////////////////////////
///	一塊のデータ
typedef struct {

	///	データを指すポインタ
	char*	data;

	///	データのサイズ
	int		size;

} DSR_Data;

//////////////////////////////////////////////////

class CSignalFeVq
{
public:
	typedef std::vector<std::string>	DSR_VEC;
	// base::CLock				m_Lock ;
	iPhoneSignal::ERR_CODE	m_ErrorCode	;
	unsigned short			m_FeKind;			// 実行種別管理フラグ
	bool					m_IsInitialezed;	// 初期化有無判定パラメータ

protected:
	bool					m_bBigEndian ;
	bool					m_bdoVQ ;
	CSignalConfigFeVq		m_Param ;			// パラメータ管理クラス
	void*					p_ExtAdvFrontEnd ;	// 特徴抽出クラス
	void*					p_ExtCoderVAD ;		// VQ圧縮クラス

	std::vector<std::string >	m_VQ212ResultQue ;
	///	入力に使用するストリーム
	MemoryStream	in_;

	///	結果の取得に使用するストリーム
	MemoryStream	out_mfcc;


	// ******** メンバ関数 ********
public:
	CSignalFeVq(void);
	virtual ~CSignalFeVq(void);
	bool	initialize(const std::string ConfigFileName);
	bool	DoDSR212(std::string& pSpeechData, bool isLastData);
	bool	GetResultVQ212(std::string& pVQData);
	virtual bool	GetResultMFCC(MFCCRESULT& pMFCCData) = 0;


	bool	GetErrorParam(std::string& SectionName,std::string& KeyName);
	iPhoneSignal::ERR_CODE	GetLastError();

protected:
	virtual bool DSR_Init() = 0;
	virtual bool DSR_Do(DSR_VEC& destMFCC, const DSR_Data* src,int Vad = 1) = 0;
	virtual bool DSR_Flush(DSR_VEC& destMFCC) = 0;

	bool SetVQ212ResultQue(std::string& Result);
	bool GetResultfromMemory(DSR_VEC& Dest, unsigned int Size);

	/// パラメータ参照用
	iPhoneSignal::SAMPLING_FREQUENCY GetSamplingFrequency(void) ;
	unsigned int		GetFrameSize(void);
	unsigned int		GetFrameShiftSize(void);
	bool				GetErrorParameter(std::string& SectionName,std::string& KeyName);
	iPhoneSignal::ERR_CODE	GetErrorCode();

};
#endif // _32AA7B7B_F3FF_454f_A1DC_88266610AEDA_SIGNALFEVQ


