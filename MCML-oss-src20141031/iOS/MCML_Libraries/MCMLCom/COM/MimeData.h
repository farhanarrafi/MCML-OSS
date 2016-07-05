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
 * @file MimeData.h
 * @brief MimeData.h
 * MIME data class declaration
 */

#ifndef __MIMEDATA_H__
#define __MIMEDATA_H__

#include <vector>
#include <string>
#include "Code.h"

namespace mcml {
    // Class
	class CMimeData
	{
		// Member value
    private:
    std::string m_PostData;
    std::string m_MimeHeader;
    std::string m_MimeFooter;
    std::string m_EmptyData;
    std::vector <std::string> m_XMLList;
    std::vector <std::string> m_BinaryList;
    std::string m_Subject;
		Code *code;
		Code decrypter;
		// Member method
    public:
        /**
         * @brief Constructor
         */
		CMimeData();
        /**
         * @brief Destructor
         */
		~CMimeData();
        /**
         * @brief MIME header addition
         */
		void addMimeHeader();
        /**
         * @brief MIME Body addition
         * @param xmlData XML data
         */
		void addMimeBodyPart(const std::string& xmlData);
        /**
         * @brief MIME Body addition
         * @param binaryData  Binary data
         */
		void addMimeBody(const std::string& binaryData);
        /**
         * @brief MIME Footer addition
         */
		void addMimeFooter();
        
        /**
         * @brief Gets MIME message
         * @return MIME message
         */
		const std::string& getMimeMessage();

        /**
         * @brief Gets text data
         * @param no
         * @return Returns null data under no, otherwise XML data
         */
		const std::string& getTextData(int no);
        
        /**
         * @brief Gets binary data
         * @param no
         * @return Returns null data under no, otherwise binary data
         */
		const std::string& getBinaryData(int no);
        
        /**
         * @brief Gets binary data stream
         * @return Returns binary data stream
         */
		const std::vector <std::string>& getBinaryList();
        
        /**
         * @brief Data analysis
         * @param recvData Data
         */
		void parseData(const std::string& recvData);
        
        /**
         * @brief Sets Subject
         * @param subject subject
         */
		void setSubject(const std::string& subject);
        
        /**
         * @brief Gets Subject
         * @return Subject value
         */
        std::string getSubject(void);
        
    private:
		void parseKeyValue(const std::string& strData, const std::string::size_type pos_index, int pos_len, const std::string strKey, std::string& strValue);
		void chompString(std::string& strData);
        
    std::string decodeString(const std::string& data);
        
    std::string encodeString(const std::string& data);
        
    public:
		void setCode(Code *code) {
			this->code = code;
		}
	};
} // namespace mcml

#endif // __MIMEDATA_H__
