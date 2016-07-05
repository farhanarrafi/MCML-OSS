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
 *\file: define.hh
 *                                Definition of variable
 * Date: 2006/9/30
 */


#ifndef ATRNS_DEFINE_HH
#define ATRNS_DEFINE_HH

#include "SignalCommon.h"


// Acoustic analysis (FB)

#define ATRNS_SMPFREQ    (16000.0)  //       Sampling frequency
#define ATRNS_HIGHPASS    (8000.0)  //       High-pass frequency 
#define ATRNS_LOWPASS        (0.0)  //       Low-pass frequency
#define ATRNS_FFTLEN         (512)  //       Band width of FFT
#define ATRNS_WINDOW         (320)  //       Width of analysis window
#define ATRNS_SHIFT          (160)  //       Frame shift
#define ATRNS_PREEMP        (0.98)  //       Pre-emphasis factor

// Noise suppression (MMSE)

#define ATRNS_VECLEN          (24)  //        Number of filter bank
#define ATRNS_MIXNUM         (512)  //        Number of speech GMM mixture
// #define ATRNS_NBEST           (10)  //     Number of NBest distribution
#define ATRNS_CODESIZE        (16)  //        Size of distribution codebook

#define DEFAULT_NBEST         (10)  //             Number of NBest distribution
#define ATRNS_NBEST (gNsVadParam.NBest)	//         Number of NBest distribution

// 雑音推定 (PF)

#define DEFAULT_INITNUM         (10)  // Number of sample for initial noise estimation
#define DEFAULT_INTERVAL        (16)  // Estimation interval of speech added with noise
#define DEFAULT_SMPINT          (20)  // Sampling interval
#define DEFAULT_SMPNUM           (5)  // Number of particle
#define DEFAULT_DRVVAR      (0.0005)  // Variance of nosie distribution
#define DEFAULT_SYSNUM           (4)  // Number of system
#define DEFAULT_MAXFGT         (0.2)  // Maximum forgetting factor
#define DEFAULT_MAXSCALE       (2.0)  // Maximum scaling factor
#define DEFAULT_MAXAVELEN       (20)  // Maximum mean length
#define DEFAULT_SENSFACT       (0.1)  // Sensitivity factor

#define ATRNS_INITNUM   (gNsVadParam.NsInitNum)	        // Number of sample for initial noise estimation
#define ATRNS_INTERVAL	(gNsVadParam.NsInterval)        // Estimation interval of speech added with noise    
#define ATRNS_SMPINT	(gNsVadParam.SamplingInterval)  // Sampling interval 
#define ATRNS_SMPNUM	(gNsVadParam.ParticleNum)       // Number of particle     
#define ATRNS_SYSNUM	(gNsVadParam.SystemNum)         // Number of system
#define ATRNS_MAXAVELEN	(gNsVadParam.MaxAverageLength)  // Maximum mean length

#define ATRNS_BUFSIZE	(gNsVadParam.BufferSize)		// Output buffer size
#define DEFAULT_BUFSIZE	 		(5000)                  // Output buffer size

#ifndef __DSR_FLOAT_DEBUG__
// For release
#define ATRNS_DRVVAR	(gNsVadParam.DRVVAR)	// Variance of nosie distribution
#define ATRNS_MAXFGT	(gNsVadParam.MaxForget)	// Maximum forgetting factor
#define ATRNS_MAXSCALE	(gNsVadParam.MaxScale)	// Maximum scaling factor
#define ATRNS_SENSFACT	(gNsVadParam.SensFact)	// Sensitivity factor
#else
// For debug (no rounding error of float)
#define ATRNS_DRVVAR	DEFAULT_DRVVAR		// Variance of nosie distribution
#define ATRNS_MAXFGT	DEFAULT_MAXFGT		// Maximum forgetting factor
#define ATRNS_MAXSCALE	DEFAULT_MAXSCALE	// Maximum scaling factor
#define ATRNS_SENSFACT	DEFAULT_SENSFACT	// Sensitivity factor
#endif

// Voice activity detection (VAD)

#define DEFAULT_ALPHA          (2.0)  // Amplification of noise power
#define DEFAULT_TRIGGER          (3)  // Length of detecting speech period
#define DEFAULT_STARTING         (7)  // Number of frames to advance start
#define DEFAULT_ENDING          (35)  // Number of frames to delay end

#define ATRNS_ALPHA	(gNsVadParam.Alpha)    // Amplification of noise power
#define ATRNS_TRIGGER	(gNsVadParam.Trigger)  // 
#define ATRNS_STARTING	(gNsVadParam.Starting) // Number of frames to advance start
#define ATRNS_ENDING	(gNsVadParam.Ending)   // Number of frames to delay end


struct NS_VAD_PARAM {
	iPhoneSignal::NOISE_SUPPRESS_TYPE	NoiseSuppressType ;
	bool				VAD ;
	bool				ParticleFilter ;
	unsigned short		NBest ;
	unsigned short		NsInitNum ;
	unsigned short		NsInterval ;
	unsigned short		SamplingInterval ;
	unsigned short		ParticleNum ;
	float				DRVVAR ;
	unsigned short		SystemNum ;
	float				MaxForget ;
	float				MaxScale ;
	unsigned short		MaxAverageLength ;
	float				SensFact ;
	//float				WaveThreshold ;
	//float				SpecThreshold ;
	//float				MelThreshold ;
	unsigned short		BufferSize ;
	float				Alpha ;
	unsigned short		Trigger ;
	unsigned short		Starting;
	unsigned short		Ending ;
	//unsigned short		HangOver ;
	//unsigned short		SangOver ;
	//unsigned short		VangOver ;
	//unsigned short		HTrigger ;
	//unsigned short		VTrigger ;
	//iPhoneSignal::LANGUAGE_TYPE	LanguageType ;
} ;

#endif
