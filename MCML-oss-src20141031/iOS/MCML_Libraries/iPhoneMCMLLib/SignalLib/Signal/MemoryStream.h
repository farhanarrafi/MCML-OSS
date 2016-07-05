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
 *  FileI/O ��Ʊ���ε�ǽ���󶡤������������
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

///	��������롣
EXTERN_C void MemoryStream_Init();

///	���ꤵ�줿̾������ĥ��ȥ꡼��򳫤���
EXTERN_C MemoryStream MemoryStream_Open(const char* name, const char* option);

EXTERN_C MemoryStream MemoryStream_Open_BySize(const char* name, const char* option,unsigned int Size);

///	���ȥ꡼�फ���ɤ߹��ࡣ
EXTERN_C size_t MemoryStream_Read(void* ptr, size_t size, size_t nmemb, MemoryStream stream);

///	���ȥ꡼�फ���ɤ߹���ʽ��աˡ�
EXTERN_C int MemoryStream_Scanf(MemoryStream stream, const char *format, char* value);

///	���ȥ꡼��ؽ񤭹��ࡣ
EXTERN_C size_t MemoryStream_Write(const void* ptr, size_t size, size_t nmemb, MemoryStream stream);

///	���ȥ꡼��ؽ񤭹���ʽ��աˡ�
EXTERN_C int MemoryStream_Printf(MemoryStream stream, const char* format, ...);

///	���ȥ꡼����ɤ߽Ф����֤�ʤ��
EXTERN_C int MemoryStream_ForwardReadPos(MemoryStream stream, unsigned int pos) ;

///	���ȥ꡼��ν񤭹��߰��֤��᤹
EXTERN_C int MemoryStream_BackwardWritePos(MemoryStream stream, unsigned int pos) ;

/// ���ȥ꡼����˽񤭹��ߺѤߤΥǡ����Ŀ����֤�
EXTERN_C unsigned int MemoryStream_GetDataNum(MemoryStream stream) ;

/// ���ȥ꡼����ǡ����Υ��ꥢ
EXTERN_C void MemoryStream_Clear(MemoryStream stream) ;

/// �ʲ������ߡ��ؿ�

///	���ȥ꡼��ΰ��֤��ѹ����롣
EXTERN_C int MemoryStream_Seek(MemoryStream stream, long offset, int whence);

///	���ȥ꡼��ΰ��֤��֤���
EXTERN_C long MemoryStream_Tell(MemoryStream stream);

///	���ȥ꡼��ΰ��֤���Ƭ���ѹ����롣
EXTERN_C void MemoryStream_Rewind(MemoryStream stream);

///	���ȥ꡼�ब��λ���֤ˤ��뤫�ɤ������֤���
EXTERN_C int MemoryStream_Eof(MemoryStream stream);

///	���ȥ꡼�ब�۾���֤ˤ��뤫�ɤ������֤���
EXTERN_C int MemoryStream_Error(MemoryStream stream);

///	���ȥ꡼������Ƥ���Ū�˽��Ϥ��롣
EXTERN_C int MemoryStream_Flush(MemoryStream stream);

//////////////////////////////////////////////////////////////////////

#endif	//	__MEMORYSTREAM_H__
