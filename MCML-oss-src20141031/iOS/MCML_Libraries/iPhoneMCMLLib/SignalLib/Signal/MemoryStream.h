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
 *  \file
 *  FileI/O と同等の機能を提供するメモリ入出力
 */

#ifndef __MEMORYSTREAM_H__
#define __MEMORYSTREAM_H__

//////////////////////////////////////////////////////////////////////

// #include <basetyps.h>
#ifdef __cplusplus
    #define EXTERN_C    extern "C"
#else
	#ifndef	EXTERN_C
		#define EXTERN_C    extern
	#endif
#endif


//////////////////////////////////////////////////////////////////////
typedef void* MemoryStream;

//////////////////////////////////////////////////

///	初期化する。
EXTERN_C void MemoryStream_Init();

///	指定された名前を持つストリームを開く。
EXTERN_C MemoryStream MemoryStream_Open(const char* name, const char* option);

EXTERN_C MemoryStream MemoryStream_Open_BySize(const char* name, const char* option,unsigned int Size);

///	ストリームから読み込む。
EXTERN_C size_t MemoryStream_Read(void* ptr, size_t size, size_t nmemb, MemoryStream stream);

///	ストリームから読み込む（書式付）。
EXTERN_C int MemoryStream_Scanf(MemoryStream stream, const char *format, char* value);

///	ストリームへ書き込む。
EXTERN_C size_t MemoryStream_Write(const void* ptr, size_t size, size_t nmemb, MemoryStream stream);

///	ストリームへ書き込む（書式付）。
EXTERN_C int MemoryStream_Printf(MemoryStream stream, const char* format, ...);

///	ストリームの読み出し位置を進める
EXTERN_C int MemoryStream_ForwardReadPos(MemoryStream stream, unsigned int pos) ;

///	ストリームの書き込み位置を戻す
EXTERN_C int MemoryStream_BackwardWritePos(MemoryStream stream, unsigned int pos) ;

/// ストリーム内に書き込み済みのデータ個数を返す
EXTERN_C unsigned int MemoryStream_GetDataNum(MemoryStream stream) ;

/// ストリーム内データのクリア
EXTERN_C void MemoryStream_Clear(MemoryStream stream) ;

/// 以下、ダミー関数

///	ストリームの位置を変更する。
EXTERN_C int MemoryStream_Seek(MemoryStream stream, long offset, int whence);

///	ストリームの位置を返す。
EXTERN_C long MemoryStream_Tell(MemoryStream stream);

///	ストリームの位置を先頭に変更する。
EXTERN_C void MemoryStream_Rewind(MemoryStream stream);

///	ストリームが終了位置にあるかどうかを返す。
EXTERN_C int MemoryStream_Eof(MemoryStream stream);

///	ストリームが異常状態にあるかどうかを返す。
EXTERN_C int MemoryStream_Error(MemoryStream stream);

///	ストリームの内容を強制的に出力する。
EXTERN_C int MemoryStream_Flush(MemoryStream stream);

//////////////////////////////////////////////////////////////////////

#endif	//	__MEMORYSTREAM_H__
