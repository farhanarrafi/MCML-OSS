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
 *\file SignalCommon.h
 */
//-------------------------------------------------------------------
// ��������  ��2009ǯ05��13��(Ver.0.0)
// ��������  ��STMD2-->iPhoneSTML 
//-------------------------------------------------------------------
//-------------------------------------------------------------------
// ��������  ��2007ǯ03��09��(Ver.0.3)
// ��ǽ      ����������饤�֥�궦�̤����
//-------------------------------------------------------------------

#ifndef _SIGNALCOMMON_H
#define _SIGNALCOMMON_H


// �ǡ�����¤�Τ����


namespace iPhoneSignal
{

	// ����ץ�󥰼��ȿ�
	enum SAMPLING_FREQUENCY {
		FREQ_8000HZ		= 8000,
		FREQ_11025HZ	= 11025,
		FREQ_16000HZ	= 16000,
		FREQ_22050HZ	= 22050,
		FREQ_32000HZ	= 32000,
		FREQ_32075HZ	= 32075,
		FREQ_44100HZ	= 44100,
		FREQ_48000HZ	= 48000
	} ;

	// ����ץ�󥰥ӥåȥ졼��
	enum SAMPLING_BIT_RATE {
		RATE_8BIT	= 8,
		RATE_16BIT	= 16,
		RATE_24BIT	= 24
	} ;


	// ȯ�åե����ޥå�
	enum SPEECH_FORMAT {
		LINEAR_PCM,		// ��˥�PCM
		ADPCM_2BIT,		// ADPCM 2bit
		ADPCM_3BIT,		// ADPCM 3bit
		ADPCM_4BIT,		// ADPCM 4bit
		FEATURE,		// ��ħ��
		DSR_212,		// DSR���̥ǡ���(VQ���̥ǡ���)
		MYU_LAW,
		UNKNOWN_FORMAT	// ����
	} ;

	// �Х��ȥ���ǥ�����
	enum BYTE_ENDIAN {
		BYTE_ENDIAN_LITTLE,	// ��ȥ륨��ǥ�����
		BYTE_ENDIAN_BIG		// �ӥå�����ǥ�����
	} ;

	// ���顼����
	enum ERR_CODE{
		NO_ERROR_CODE,			// ����
		ERR_ADPCM_BITRATE,		// ADPCM�ӥåȥ졼�Ȥ�2��4�ʳ�
		ERR_ADPCM_ALIGN,		// ADPCM�ǡ����ν���ñ�̤�����
		ERR_DATA_FORMAT,		// �����ե����ޥåȤ�����
		ERR_SAMPLING_FREQUENCY,	// ����ץ�󥰼��ȿ�������
		ERR_SAMPLING_BITRATE,	// ����ץ�󥰥ӥåȥ졼�Ȥ�����
		ERR_CHANNELS,			// ����ͥ뤬����
		ERR_DATA_BYTES,			// �ǡ����Х��ȿ�������
		ERR_NULL_POINTER,		// NULL�ݥ��󥿤����ꤵ�줿
		ERR_FUNCTION,			// �����ؿ��μ¹Ԥ˼���
		ERR_INITIALIZE,			// ���������
		ERR_CONFIG_PARAMETER,	        // ����ե�����Υѥ�᡼�����۾�
		ERR_MEMORY,		        // ������­
		ERR_DATA_NONE,			// Ͽ���Ѥߥǡ������ʤ�
		ERR_PROCEDURE			// �ؿ��¹Խ��ְ۾�
	} ;

	// �Υ�������Υ����סʥΥ��������Ԥ�����ɬ��ȯ�ø��Ф�Ԥ�
	enum NOISE_SUPPRESS_TYPE {
		NS_TYPE_UN_SUPPRESS,	// �Υ��������ȯ�ø��С����ʤ�
		NS_TYPE_MMSE,			// MMSE
		NS_TYPE_PF				// ParticleFilter
	} ;

	enum LANGUAGE_TYPE{
		NSVAD_JA,
		NSVAD_EN,
		NSVAD_ZH
	};

} ; // namespace

#endif // _SIGNALCOMMON_H
