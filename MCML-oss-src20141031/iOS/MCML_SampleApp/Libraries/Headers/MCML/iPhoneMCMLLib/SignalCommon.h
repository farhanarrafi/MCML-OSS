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
// Update    : 13 May, 2009 (Ver.0.0)
// Change    : STMD2-->iPhoneSTML 
//-------------------------------------------------------------------
//-------------------------------------------------------------------
// Update    : 9 March, 2007 (Ver.0.3)
// Function  : Definition common to signal processing library 
//-------------------------------------------------------------------

#ifndef _SIGNALCOMMON_H
#define _SIGNALCOMMON_H


// Definition of data structure


namespace iPhoneSignal
{

	// Sampling frequency
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

	// Sampling bitrate
	enum SAMPLING_BIT_RATE {
		RATE_8BIT	= 8,
		RATE_16BIT	= 16,
		RATE_24BIT	= 24
	} ;


	// Speech format
	enum SPEECH_FORMAT {
		LINEAR_PCM,		// Linear PCM
		ADPCM_2BIT,		// ADPCM 2bit
		ADPCM_3BIT,		// ADPCM 3bit
		ADPCM_4BIT,		// ADPCM 4bit
		FEATURE,		// Feature
		DSR_212,		// DSR compression (VQ compression)
		MYU_LAW,
		UNKNOWN_FORMAT	       // unknown
	} ;

	// Bite endian
	enum BYTE_ENDIAN {
		BYTE_ENDIAN_LITTLE,	// Little endian
		BYTE_ENDIAN_BIG		// Big endian
	} ;

	// Error information
	enum ERR_CODE{
		NO_ERROR_CODE,			// No error
		ERR_ADPCM_BITRATE,		// ADPCM bitrate not set within 2 to 4
		ERR_ADPCM_ALIGN,		// ADPCM alignment incorrect
		ERR_DATA_FORMAT,		// Audio format incorrect
		ERR_SAMPLING_FREQUENCY,	// Sampling frequency incorrect
		ERR_SAMPLING_BITRATE,	// Sampling bitrate incorrect
		ERR_CHANNELS,			// Channel incorrect
		ERR_DATA_BYTES,			// Databyte incorrect
		ERR_NULL_POINTER,		// NULL pointer specified
		ERR_FUNCTION,			// Failed executing internal function
		ERR_INITIALIZE,			// Failed initialization
		ERR_CONFIG_PARAMETER,	        // Parameter in configuration file wrong
		ERR_MEMORY,		        // Insufficient memory
		ERR_DATA_NONE,			// No recorded data
		ERR_PROCEDURE			// Order of executing function incorrect
	} ;

	// Type of noise suppression (VAD is necessarily required for noise suppression)
	enum NOISE_SUPPRESS_TYPE {
		NS_TYPE_UN_SUPPRESS,	// Not do noise suppression and VAD
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
