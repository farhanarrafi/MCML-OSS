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
 *\file SignalConfig.h
 */
//-------------------------------------------------------------------
// Update    : 13 May, 2009 (Ver.0.0)
// Change    : STMD2-->iPhoneSTML 
// iPhoneSignal/FeVq/DSR/src/ParameterManager.cpp --> iPhoneSignal/SignalConfig.cpp
// Function  : Class definition of CSignalConfig
//-------------------------------------------------------------------
//-------------------------------------------------------------------
// Update    : 30 March, 2007 (Ver.0.2)
// Function  : Class definition of CSignalConfig
//-------------------------------------------------------------------
#ifndef _32AA7B7B_F3FF_454f_A1DC_88266610AEDA_SIGNALCONFIG
#define _32AA7B7B_F3FF_454f_A1DC_88266610AEDA_SIGNALCONFIG
#pragma once

#include <string>
#include "SignalCommon.h"


/*!
	@brief	Management class for initialization parameter for 
                feature extraction and VQ suppression
	@date	2007/03/30: created
*/
class CSignalConfig
{
protected:
	iPhoneSignal::ERR_CODE	m_ErrorCode ;
	std::string				m_ErrorSectionName;
	std::string				m_ErrorKeyName;

public:
	CSignalConfig(void);
	virtual	~CSignalConfig(void);
	virtual bool	ReadInitFile(const std::string& FileName) = 0;	// Function of loading initialization file 

	iPhoneSignal::ERR_CODE				GetErrorCode();
	bool								GetErrorParameter(std::string& SectionName,std::string& KeyName);
};

#endif //_32AA7B7B_F3FF_454f_A1DC_88266610AEDA_PARAMETERMANAGER
