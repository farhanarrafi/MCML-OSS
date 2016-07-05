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
 *                                変数定義
 * Date: 2006/9/30
 */


#ifndef ATRNS_DEFINE_HH
#define ATRNS_DEFINE_HH

#include "SignalCommon.h"


// 音響分析 (FB)

#define ATRNS_SMPFREQ    (16000.0)  //       サンプリング周波数
#define ATRNS_HIGHPASS    (8000.0)  //           ハイパス周波数
#define ATRNS_LOWPASS        (0.0)  //           ローパス周波数
#define ATRNS_FFTLEN         (512)  //               FFT 分析幅
#define ATRNS_WINDOW         (320)  //                 分析窓幅
#define ATRNS_SHIFT          (160)  //         フレームシフト幅
#define ATRNS_PREEMP        (0.98)  //     プリエンファシス係数

// 雑音抑圧 (MMSE)

#define ATRNS_VECLEN          (24)  //         フィルタバンク数
#define ATRNS_MIXNUM         (512)  //        音声 GMM の混合数
// #define ATRNS_NBEST           (10)  //          NBest 分布数
#define ATRNS_CODESIZE        (16)  //   分布コードブックサイズ

#define DEFAULT_NBEST         (10)  //             NBest 分布数
#define ATRNS_NBEST (gNsVadParam.NBest)	//         NBest 分布数

// 雑音推定 (PF)

#define DEFAULT_INITNUM         (10)  // 初期雑音推定用サンプル数
#define DEFAULT_INTERVAL        (16)  //     雑音重畳音声推定間隔
#define DEFAULT_SMPINT          (20) //         サンプリング間隔
#define DEFAULT_SMPNUM           (5)  //           パーティクル数
#define DEFAULT_DRVVAR      (0.0005) //             雑音分布変動
#define DEFAULT_SYSNUM           (4)  //               システム数
#define DEFAULT_MAXFGT         (0.2)  //             最大忘却係数
#define DEFAULT_MAXSCALE       (2.0)  //     最大スケーリング係数
#define DEFAULT_MAXAVELEN       (20)  //               最大平均長
#define DEFAULT_SENSFACT       (0.1)  //                 敏感係数

#define ATRNS_INITNUM   (gNsVadParam.NsInitNum)	  // 初期雑音推定用サンプル数
#define ATRNS_INTERVAL	(gNsVadParam.NsInterval)  //     雑音重畳音声推定間隔
#define ATRNS_SMPINT	(gNsVadParam.SamplingInterval)  //   サンプリング間隔
#define ATRNS_SMPNUM	(gNsVadParam.ParticleNum)  //          パーティクル数
#define ATRNS_SYSNUM	(gNsVadParam.SystemNum)    //              システム数
#define ATRNS_MAXAVELEN	(gNsVadParam.MaxAverageLength)  //         最大平均長

#define ATRNS_BUFSIZE	(gNsVadParam.BufferSize)		// 出力バッファサイズ
#define DEFAULT_BUFSIZE	 		(5000) //                  出力バッファサイズ

#ifndef __DSR_FLOAT_DEBUG__
// 運用版
#define ATRNS_DRVVAR	(gNsVadParam.DRVVAR)	// 雑音分布変動
#define ATRNS_MAXFGT	(gNsVadParam.MaxForget)	// 最大忘却係数
#define ATRNS_MAXSCALE	(gNsVadParam.MaxScale)	// 最大スケーリング係数
#define ATRNS_SENSFACT	(gNsVadParam.SensFact)	// 敏感係数
#else
// floatの丸め誤差を吸収したデバッグ版
#define ATRNS_DRVVAR	DEFAULT_DRVVAR		// 雑音分布変動
#define ATRNS_MAXFGT	DEFAULT_MAXFGT		// 最大忘却係数
#define ATRNS_MAXSCALE	DEFAULT_MAXSCALE	// 最大スケーリング係数
#define ATRNS_SENSFACT	DEFAULT_SENSFACT	// 敏感係数
#endif

// 発話区間検出 (VAD)

#define DEFAULT_ALPHA          (2.0)  //   雑音パワーに対する倍率
#define DEFAULT_TRIGGER          (3)  //           音声区間判定長
#define DEFAULT_STARTING         (7)  //   開始繰り上げフレーム数
#define DEFAULT_ENDING          (35)  //       終了遅延フレーム数

#define ATRNS_ALPHA	(gNsVadParam.Alpha)  //   雑音パワーに対する倍率
#define ATRNS_TRIGGER	(gNsVadParam.Trigger)  //           音声区間判定長
#define ATRNS_STARTING	(gNsVadParam.Starting)  //   開始繰り上げフレーム数
#define ATRNS_ENDING	(gNsVadParam.Ending)  //       終了遅延フレーム数


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
