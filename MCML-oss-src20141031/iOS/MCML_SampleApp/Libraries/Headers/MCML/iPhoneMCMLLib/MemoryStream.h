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
 *  Input/output memory stream which is providing function equivalent to FileI/O
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

///	Initialize
EXTERN_C void MemoryStream_Init();

///	Open stream of specified name
EXTERN_C MemoryStream MemoryStream_Open(const char* name, const char* option);

EXTERN_C MemoryStream MemoryStream_Open_BySize(const char* name, const char* option,unsigned int Size);

///	Read from stream
EXTERN_C size_t MemoryStream_Read(void* ptr, size_t size, size_t nmemb, MemoryStream stream);

///	Read from stream with format
EXTERN_C int MemoryStream_Scanf(MemoryStream stream, const char *format, char* value);

///	Write to stream
EXTERN_C size_t MemoryStream_Write(const void* ptr, size_t size, size_t nmemb, MemoryStream stream);

///	Write to stream with format
EXTERN_C int MemoryStream_Printf(MemoryStream stream, const char* format, ...);

///	Move reading position of stream forward
EXTERN_C int MemoryStream_ForwardReadPos(MemoryStream stream, unsigned int pos) ;

///	Move writing position of stream backward
EXTERN_C int MemoryStream_BackwardWritePos(MemoryStream stream, unsigned int pos) ;

///     Rerurn number of data written in stream
EXTERN_C unsigned int MemoryStream_GetDataNum(MemoryStream stream) ;

///     Clear data in stream
EXTERN_C void MemoryStream_Clear(MemoryStream stream) ;

/// Dummy function

///	Change stream position
EXTERN_C int MemoryStream_Seek(MemoryStream stream, long offset, int whence);

///	Return stream position
EXTERN_C long MemoryStream_Tell(MemoryStream stream);

///	Rewind stream position
EXTERN_C void MemoryStream_Rewind(MemoryStream stream);

///	Return whether stream postion is at end
EXTERN_C int MemoryStream_Eof(MemoryStream stream);

///	Return whether stream is in error
EXTERN_C int MemoryStream_Error(MemoryStream stream);

///	Force to output data in stream
EXTERN_C int MemoryStream_Flush(MemoryStream stream);

//////////////////////////////////////////////////////////////////////

#endif	//	__MEMORYSTREAM_H__
