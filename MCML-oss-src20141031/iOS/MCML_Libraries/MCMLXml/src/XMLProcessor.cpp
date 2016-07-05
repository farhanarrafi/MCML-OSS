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
 * @file XMLProcessor.cpp
 * @brief XMLProcessor.cpp
 * XML data processing class implementation
 */
#include "XMLProcessor.h"

#include <string>
#include <stdio.h>

namespace mcml
{
    /**
     * @brief Constructor
     */
    CXMLProcessor::CXMLProcessor(void) {
    }
    /**
     * @brief Destructor
     */
    CXMLProcessor::~CXMLProcessor(void) {
    }
    
    /**
     * @brief Generation
     * @param McmlType CMCMMLType object
     * @return XML data character string
     */
    tstring CXMLProcessor::Generate(CMCMLType& McmlType) {
        LOCKING_THIS_SCOPE(m_Lock);
        
        tstring retVal;
        CMCMLDoc doc;
        
        retVal = doc.Generate(McmlType);
        
        //printf(&retVal[0]);
        
        return retVal;
    }
    
    /**
     * @brief  Analysis
     * @param XmlData XML data character string
     * @return CMCMMLType object
     */
    CMCMLType CXMLProcessor::Parse(const tstring& XmlData) {
        LOCKING_THIS_SCOPE(m_Lock);
        
        // generate document from text
        CMCMLDoc doc;
        CMCMLType retVal = CMCMLType(doc.Parse(XmlData));
        
        // return node information
        return retVal;
    }
} // namespace mcml
