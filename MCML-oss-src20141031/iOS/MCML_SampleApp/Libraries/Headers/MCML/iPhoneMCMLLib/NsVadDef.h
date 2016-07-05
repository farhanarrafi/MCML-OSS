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
 *\file NsVadDef.h
 */
//-------------------------------------------------------------------
// Update    : 7 February, 2007 (Ver.0.1)
// Function  : Macro definition for NsVad
//-------------------------------------------------------------------

#ifndef _5B7D87F5_2192_4b0b_85BE_3EB13070EB52_NSVADDEF
#define _5B7D87F5_2192_4b0b_85BE_3EB13070EB52_NSVADDEF

#include <vector>
#include <string>

// Macro definition for NsVad
typedef std::basic_string<short int> NSVADLINEAR_BUFFER ;

struct NSVADRESULT {
	NSVADLINEAR_BUFFER	LinearBuffer ;
	bool			VADFlag ;
} ;
typedef std::vector<NSVADRESULT> NSVADRESULT_VECTOR ;

#endif // _5B7D87F5_2192_4b0b_85BE_3EB13070EB52_NSVADDEF
