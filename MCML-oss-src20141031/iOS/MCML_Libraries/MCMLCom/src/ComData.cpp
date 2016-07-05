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
 * @file ComData.cpp
 * @brief Communication data class implementation
 */

#include "ComData.h"

namespace mcml
{
    /**
     * @brief Constructor
     */
    CComData::CComData() {
        m_XML = "";
        m_BinaryList.clear();
    }
    
    /**
     * @brief Destructor
     */
    CComData::~CComData() {
    }
    
    /**
     * @brief Gets XML data
     * @param xmlData XML data
     */
    void CComData::getXML(std::string& xmlData) {
        xmlData = m_XML;
    }
    
    /**
     * @brief  Gets binary data
     * @param binaryData  Binary data
     */
    void CComData::getBinary(std::string& binaryData) {
        if (m_BinaryList.size() == 0) {
            binaryData = "";
        }
        else {
            binaryData = m_BinaryList.at(0);
        }
    }
    
    /**
     * @brief Gets binary data stream
     * @param binaryDataList  Binary data stream
     */
    void CComData::getBinaryList(std::vector <std::string>& binaryDataList) {
        binaryDataList.clear();
        for (int i = 0; i < (int)m_BinaryList.size(); i++) {
            binaryDataList.push_back(m_BinaryList.at(i));
        }
    }
    
    /**
     * @brief Sets XML data
     * @param xmlData XML data
     */
    void CComData::setXML(const std::string& xmlData) {
        m_XML = xmlData;
    }
    
    /**
     * @brief Sets binary data
     * @param binaryData  Binary data
     */
    void CComData::setBinary(const std::string& binaryData) {
        m_BinaryList.clear();
        m_BinaryList.push_back(binaryData);
    }
    
    /**
     * @brief Sets binary data stream
     * @param binaryDataList  Binary data stream
     */
    void CComData::setBinaryList(const std::vector <std::string>& binaryDataList) {
        m_BinaryList.clear();
        for (int i = 0; i < (int)binaryDataList.size(); i++) {
            m_BinaryList.push_back(binaryDataList.at(i));
        }
    }
} // namespace mcml
