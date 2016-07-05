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
 * @file ComData.h
 * @brief Communication data class declaration
 */

#ifndef __COMDATA_H__
#define __COMDATA_H__

#include <vector>
#include <string>

namespace mcml {
    // Class
	class CComData
	{
		// Member value
    private:
    std::string m_XML;
    std::vector <std::string> m_BinaryList;
        
		// Member method
    public:
        /**
         * @brief Constructor
         */
		CComData();
        /**
         * @brief Destructor
         */
		~CComData();
        /**
         * @brief Gets XML data
         * @param xmlData XML data
         */
		void getXML(std::string& xmlData);
        /**
         * @brief Gets binary data
         * @param binaryData  binary data
         */
		void getBinary(std::string& binaryData);
        /**
         * @brief Gets binary data stream
         * @param binaryDataList binary data stream
         */
		void getBinaryList(std::vector<std::string>& binaryDataList);
        /**
         * @brief Sets XML data
         * @param xmlData XML data
         */
		void setXML(const std::string& xmlData);
        /**
         * @brief Sets binary data
         * @param binaryData  binary data
         */
		void setBinary(const std::string& binaryData);
        /**
         * @brief Sets binary data stream
         * @param binaryDataList  binary data stream
         */
		void setBinaryList(const std::vector <std::string>& binaryDataList);
	};
} // namespace mcml

#endif // __COMDATA_H__
