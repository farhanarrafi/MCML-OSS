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
// 更新日時  ：2009年05月13日(Ver.0.0)
// 更新内容  ：STMD2-->iPhoneSTML 
//-------------------------------------------------------------------
//-------------------------------------------------------------------
// 更新日時  ：2007年03月09日(Ver.0.3)
// 機能      ：信号処理ライブラリ共通の定義
//-------------------------------------------------------------------

#ifndef _SIGNALCOMMON_H
#define _SIGNALCOMMON_H


// データ構造体の定義


namespace iPhoneSignal
{

	// サンプリング周波数
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

	// サンプリングビットレート
	enum SAMPLING_BIT_RATE {
		RATE_8BIT	= 8,
		RATE_16BIT	= 16,
		RATE_24BIT	= 24
	} ;


	// 発話フォーマット
	enum SPEECH_FORMAT {
		LINEAR_PCM,		// リニアPCM
		ADPCM_2BIT,		// ADPCM 2bit
		ADPCM_3BIT,		// ADPCM 3bit
		ADPCM_4BIT,		// ADPCM 4bit
		FEATURE,		// 特徴量
		DSR_212,		// DSR圧縮データ(VQ圧縮データ)
		MYU_LAW,
		UNKNOWN_FORMAT	// 不明
	} ;

	// バイトエンディアン
	enum BYTE_ENDIAN {
		BYTE_ENDIAN_LITTLE,	// リトルエンディアン
		BYTE_ENDIAN_BIG		// ビッグエンディアン
	} ;

	// エラー情報
	enum ERR_CODE{
		NO_ERROR_CODE,			// 正常
		ERR_ADPCM_BITRATE,		// ADPCMビットレートが2〜4以外
		ERR_ADPCM_ALIGN,		// ADPCMデータの処理単位が不正
		ERR_DATA_FORMAT,		// 音声フォーマットが不正
		ERR_SAMPLING_FREQUENCY,	// サンプリング周波数が不正
		ERR_SAMPLING_BITRATE,	// サンプリングビットレートが不正
		ERR_CHANNELS,			// チャネルが不正
		ERR_DATA_BYTES,			// データバイト数が不正
		ERR_NULL_POINTER,		// NULLポインタが指定された
		ERR_FUNCTION,			// 内部関数の実行に失敗
		ERR_INITIALIZE,			// 初期化失敗
		ERR_CONFIG_PARAMETER,	        // 設定ファイルのパラメータが異常
		ERR_MEMORY,		        // メモリ不足
		ERR_DATA_NONE,			// 録音済みデータがない
		ERR_PROCEDURE			// 関数実行順番異常
	} ;

	// ノイズ削除のタイプ（ノイズ削除を行う場合は必ず発話検出も行う
	enum NOISE_SUPPRESS_TYPE {
		NS_TYPE_UN_SUPPRESS,	// ノイズ削除・発話検出　しない
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
