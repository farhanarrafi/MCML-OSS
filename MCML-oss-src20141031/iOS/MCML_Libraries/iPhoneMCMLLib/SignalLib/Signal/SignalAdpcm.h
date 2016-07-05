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
 *\file SignalAdpcm.h
 */
//-------------------------------------------------------------------
// 更新日時  ：2007年03月30日(Ver.0.5)
// 機能      ：ADPCM符号化クラスの定義
//-------------------------------------------------------------------

#ifndef _SIGNALADPCM_H
#define _SIGNALADPCM_H

#include <string>

#include "ScopedMem.h"
#include "SignalCommon.h"

extern "C"
{
#include "g726.h"
}

class CSignalAdpcm
{
    // ******** メンバ定数 ********
public:

    // ******** メンバ変数 ********
protected:
    iPhoneSignal::ERR_CODE    m_ErrorCode    ;

    iPhoneSignal::SPEECH_FORMAT        m_SpeechData_Format ;            // 音声フォーマット
    iPhoneSignal::SAMPLING_BIT_RATE    m_SpeechData_SamplingBitRate ;
    iPhoneSignal::BYTE_ENDIAN          m_SpeechData_ByteEndian ;        // バイトエンディアン

    G726_state        m_EncodeState ;
    short             m_EncodeReset ;
    unsigned short    m_EncodeBitRate ;
    unsigned int      m_EncodeSampleBytes ;
    std::string       m_EncodeInputBuf ;
    std::string       m_EncodeOutputBuf ;

    G726_state        m_DecodeState ;
    short             m_DecodeReset ;
    unsigned short    m_DecodeBitRate ;
    unsigned int      m_DecodeSampleNum ;
    std::string       m_DecodeInputBuf ;
    std::string       m_DecodeOutputBuf ;

    // ******** メンバ関数 ********
public:
    CSignalAdpcm(void) ;
    CSignalAdpcm(bool bBigEndian) ;
    ~CSignalAdpcm(void) ;
    void InitializeEncode() ;
    void InitializeDecode() ;
    bool Encode(std::string& pSpeechData, std::string& pOutData) ;
    bool Encode(std::string& pSpeechData, std::string& pOutData, bool isLastData) ;
    bool Decode(std::string& pSpeechData, std::string& pOutData) ;
    bool Decode(std::string& pSpeechData, std::string& pOutData, bool isLastData) ;
    iPhoneSignal::ERR_CODE GetLastError(void) ;

protected:
    bool DoEncode(bool IsEOF) ;
    bool DoDecode(bool IsEOF) ;
    bool DoDecodeSub(unsigned int DecodeSampleNum) ;
};

#endif // _SIGNALADPCM_H
