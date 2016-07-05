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
 * @file XMLProcessor.h
 * @brief XMLProcessor.h
 * XML data processing class declaration
 */
#pragma once

#include <iostream>
#include <sstream>
#include <string>
#include "Lock.h"
#include "SchemaTypes.h"
#include "Doc.h"
#include "Node.h"
#include "XmlException.h"
#include "mcml.h"

class CMCMLType;
class CMCMLDoc;

namespace mcml {
	class CXMLProcessor
	{
    protected:
		CLock m_Lock;
        
    public:
        /**
         * @brief Constructor
         */
		CXMLProcessor(void);
        /**
         * @brief Destructor
         */
		virtual ~CXMLProcessor(void);
        /**
         * @brief Generation
         * @param McmlType CMCMMLType object
         * @return XML data character string
         */
		tstring Generate(CMCMLType& McmlType);
        /**
         * @brief  Analysis
         * @param XmlData XML data character string
         * @return CMCMMLType object
         */
		CMCMLType Parse(const tstring& XmlData);
	};
} // namespace mcml
