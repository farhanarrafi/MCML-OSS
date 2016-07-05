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
 *\file SignalNoiseSuppress.h
 */
//-------------------------------------------------------------------
// Update    : March 30, 2007 (Ver.0.2)
// Function  : Class definition for noise supression and VAD control
//-------------------------------------------------------------------

#ifndef _32AA7B7B_F3FF_454f_A1DC_88266610AEDA_SIGNALNOISESUPPRESS
#define _32AA7B7B_F3FF_454f_A1DC_88266610AEDA_SIGNALNOISESUPPRESS

#include <string>
#include <vector>
#include "SignalConfigNoiseSuppress.h"
#include "atrns.hh"
#include "NsVadDef.h"


/*!
	@brief	Noise supression and VAD control class
	@date	2007/03/30: created
*/
class CSignalNoiseSuppress
{
private:
	// ******** Member constant ********
	iPhoneSignal::ERR_CODE	m_ErrorCode	;
	bool					m_bBigEndian ;

	// ******** Member variable ********
protected:
	ATRNS::FBANK*			p_FBank;
	ATRNS::WIENER*			p_Wiener;
	ATRNS::PFILTER*			p_PFilter;
	ATRNS::VAD*				p_VAD ;
	float					m_InitMean[ATRNS_VECLEN];
	float					m_InitVar[ATRNS_VECLEN];
	float*					p_FBSaveBuf ;
	short int*				p_WaveSaveBuf ;
	unsigned int			m_FrameNum ;

	CSignalConfigNoiseSuppress m_Param;
	// Option for noiseSuppress and Vad
	NS_VAD_PARAM			gNsVadParam ;

	// Input data buffer
	NSVADLINEAR_BUFFER		m_LinearDataBuffer ;
	// Output data buffer
	NSVADRESULT_VECTOR		m_ResultVector;
	


	// ******** Member function ********
public:
	CSignalNoiseSuppress(bool bBigEndian) ;
	virtual ~CSignalNoiseSuppress(void) ;
	bool initialize(std::string& configfile);
	bool doNoiseSuppress(std::string& Input, bool Flush) ;
	int  getResultNoiseSuppress(std::vector<NSVADRESULT >& Output) ;

	bool GetVad() { return m_Param.GetVad(); } 
	iPhoneSignal::NOISE_SUPPRESS_TYPE GetNsType() { return m_Param.GetNsType(); } ;
   
protected:
	void EstimateInitialNoise(const float *pFilterBank, float* pFBSaveBuf, short int* pWaveSaveBuf) ;
	void PFilter(const float *pFilterBank, bool Flush) ;
	void GetBackPFilter(bool Flush) ;
	void MMSE(bool Flush) ;
	void GetBackMMSE(bool Flush) ;
	void NsVad(const float *pFilterBank, const short int *, bool Flush) ;

	bool GetErrorParameter(std::string& SectionName,std::string& KeyName);
	iPhoneSignal::ERR_CODE	GetErrorCode();

};

#endif // _32AA7B7B_F3FF_454f_A1DC_88266610AEDA_SIGNALNOISESUPPRESS
