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
 * @file MimeData.cpp
 * @brief MIME data class implementation
 */

#include "MimeData.h"

namespace mcml
{
    // define string
    const std::string c_BOUNDARY_BAR            = "--";
    const std::string c_BOUNDARY_KEY            = "boundary=";
    const std::string c_BOUNDARY_STR            = "Boundary";
    const std::string c_HEADER_CONTENT_TYPE     = "Content-Type:";
    const std::string c_HEADER_CONTENT_ENCODING = "Content-Transfer-Encoding:";
    const std::string c_CONTENT_TYPE_MIXED      = "multipart/mixed";
    const std::string c_CONTENT_TYPE_TEXT       = "text/xml;charset=utf-8"; //TODO: space.
    const std::string c_CONTENT_TYPE_BINARY     = "application/octet-stream";
    const std::string c_ENCODING_7BIT           = "7bit";
    const std::string c_ENCODING_BINARY         = "binary";
    const std::string c_LINE_FEED               = "\r\n";
    const std::string c_LINE_FEEDS              = "\r\n\r\n";
    const std::string c_HEADER_SUBJECT          = "Subject: ";
    // chunked. add
    const std::string c_ENCODING_8BIT                       = "8bit";
    const std::string c_HEADER_CONTENT_ENCRYPTION_TYPE
    = "X-Content-Encryption-Type:";
    const std::string c_HEADER_CONTENT_LENGTH   = "Content-Length:";
    const std::string c_ENCRYPTION_ENCTYPE02    = "enctype02";
    // chunked. end
    
    // define char
    const char c_CHAR_SPACE                     = ' ';
    const char c_CHAR_DB_QUOTE                  = '"';
    
    /**
     * @brief Constructor
     */
    CMimeData::CMimeData() {
        m_PostData = "";
        m_MimeHeader = c_HEADER_CONTENT_TYPE + " " + c_CONTENT_TYPE_MIXED + "; " + c_BOUNDARY_KEY + "\"" + c_BOUNDARY_STR + "\"" + c_LINE_FEEDS;
        m_MimeFooter = c_BOUNDARY_BAR + c_BOUNDARY_STR + c_BOUNDARY_BAR + c_LINE_FEED;
        m_EmptyData = "";
        m_XMLList.clear();
        m_BinaryList.clear();
        m_Subject = "";
    }
    
    /**
     * @brief Destructor
     */
    CMimeData::~CMimeData() {
    }
    
    /**
     * @brief MIME header addition
     */
    void CMimeData::addMimeHeader() {
        // set data
        m_PostData.append(m_MimeHeader);
        // chunked. add
        const std::string c_ENCODING_8BIT                       = "8bit";
        const std::string c_HEADER_CONTENT_ENCRYPTION_TYPE
	    = "X-Content-Encryption-Type:";
        const std::string c_HEADER_CONTENT_LENGTH   = "Content-Length:";
        const std::string c_ENCRYPTION_ENCTYPE02    = "enctype02";
        // chunked. end
    }
    
    /**
     * @brief MIME body addition
     * @param xmlData XML data
     */
    void CMimeData::addMimeBodyPart(const std::string& xmlData) {
        // set data
        m_PostData.append(c_BOUNDARY_BAR + c_BOUNDARY_STR + c_LINE_FEED);
        m_PostData.append(c_HEADER_CONTENT_TYPE + " " + c_CONTENT_TYPE_TEXT + c_LINE_FEED);
        m_PostData.append(c_HEADER_CONTENT_ENCODING + " " + c_ENCODING_7BIT + c_LINE_FEEDS);
        m_PostData.append(xmlData);
        // modified for iPhone start
        // - null line is needless
        // m_PostData.append(c_LINE_FEEDS);
        m_PostData.append(c_LINE_FEED);
        // modified for iPhone end
    }
    
    /**
     * @brief MIME body addition
     * @param binaryData  Binary data
     */
    void CMimeData::addMimeBody(const std::string& binaryData) {
        // set data
        m_PostData.append(c_BOUNDARY_BAR + c_BOUNDARY_STR + c_LINE_FEED);
        m_PostData.append(c_HEADER_CONTENT_TYPE + " " + c_CONTENT_TYPE_BINARY + c_LINE_FEED);
        m_PostData.append(c_HEADER_CONTENT_ENCODING + " " + c_ENCODING_BINARY + c_LINE_FEEDS);
        m_PostData.append(binaryData);
        // modified for iPhone start
        // - null line is needless
        // m_PostData.append(c_LINE_FEEDS);
        m_PostData.append(c_LINE_FEED);
        // modified for iPhone end
    }
    
    /**
     * @brief MIME Footer addition
     */
    void CMimeData::addMimeFooter() {
        // set data
        m_PostData.append(m_MimeFooter);
    }
    
    /**
     * @brief MIME message acquisition
     * @return MIME message
     */
    const std::string& CMimeData::getMimeMessage() {
        return m_PostData;
    }
    
    /**
     * @brief  Gets text data
     * @param no
     * @return Returns null data under no, otherwiseXML data
     */
    const std::string& CMimeData::getTextData(int no) {
        if ((int)m_XMLList.size() <= no) {
            return m_EmptyData;
        }
        return m_XMLList.at(no);
    }
    
    /**
     * @brief Gets binary data
     * @param no
     * @return Returns null data under no, otherwise binary data
     */
    const std::string& CMimeData::getBinaryData(int no) {
        if ((int)m_BinaryList.size() <= no) {
            return m_EmptyData;
        }
        return m_BinaryList.at(no);
    }
    
    /**
     * @brief Gets binary data stream
     * @return Returns binary data stream
     */
    const std::vector <std::string>& CMimeData::getBinaryList() {
        return m_BinaryList;
    }
    
    /**
     * @brief Data analysis
     * @param recvData Data
     */
    void CMimeData::parseData(const std::string& recvData) {
        // init
        m_XMLList.clear();
        m_BinaryList.clear();
        
        // get subject
        std::string strSubject;
        parseKeyValue(recvData, 0, recvData.size(), c_HEADER_SUBJECT, strSubject);
        m_Subject = strSubject;
        
        // get boundary
        std::string strBound;
        parseKeyValue(recvData, 0, recvData.size(), c_BOUNDARY_KEY, strBound);
        strBound = c_LINE_FEED + c_BOUNDARY_BAR + strBound;
        
        // loop
        std::string::size_type pos_part_st = 0;
        std::string::size_type pos_part_end = 0;
        while (pos_part_st != std::string::npos) {
            // search boundary
            pos_part_end = recvData.find(strBound, pos_part_st);
            if (pos_part_end == std::string::npos) {
                break;
            }
            // get header/body
            int part_head_len = 0;
            int part_body_len = 0;
            std::string::size_type pos_part_head = pos_part_st;
            std::string::size_type pos_part_body = recvData.find(c_LINE_FEEDS, pos_part_head);
            if ((pos_part_body != std::string::npos) && (pos_part_body < pos_part_end)) {
                part_head_len = pos_part_body - pos_part_head;
                pos_part_body += c_LINE_FEEDS.size();
                part_body_len = pos_part_end - pos_part_body;
            }
            // parse head
            std::string strContentType;
            parseKeyValue(recvData, pos_part_head, part_head_len, c_HEADER_CONTENT_TYPE, strContentType);
            
            // chunked. add
            std::string strContentEncryptionType;
            parseKeyValue(recvData, pos_part_head, part_head_len, c_HEADER_CONTENT_ENCRYPTION_TYPE, strContentEncryptionType);
            // chunked. end
            
            // parse body
            if (part_body_len > 0) {
                // chunked. add
                bool binaryContent = false;
                if (strContentEncryptionType == c_ENCRYPTION_ENCTYPE02) {
                    strContentType = c_CONTENT_TYPE_BINARY;
                    binaryContent = true;
                }
                // chunked. end
                
                if (strContentType == c_CONTENT_TYPE_TEXT) {
                    std::string xmlData = recvData.substr(pos_part_body, part_body_len);
                    // remove linefeed
                    chompString(xmlData);
                    // set
                    m_XMLList.push_back(xmlData);
                }
                else if (strContentType == c_CONTENT_TYPE_BINARY) {
                    std::string binaryData = recvData.substr(pos_part_body, part_body_len);
                    // remove linefeed
                    chompString(binaryData);
                    // chunked. mod
                    ////binaryData = decodeBase64String(binaryData);
                    if (binaryContent) { //if (code) {
                        // chunked. end
                        decrypter.decode((unsigned char *)binaryData.data(), binaryData.size());
                    }
                    // chunked. mod
                    // set
                    //m_BinaryList.push_back(binaryData);
                    if (m_XMLList.empty() && m_BinaryList.empty()) {
                        m_XMLList.push_back(binaryData);
                    }
                    else {
                        m_BinaryList.push_back(binaryData);
                    }
                    // chunked. end
                }
            }
            // update pos
            pos_part_end += strBound.size();
            if (recvData.find(c_LINE_FEED, pos_part_end) == 0) {
                pos_part_end += c_LINE_FEED.size();
            }
            pos_part_st = pos_part_end;
        }
    }
    
    void CMimeData::parseKeyValue(const std::string& strData, const std::string::size_type pos_index, int pos_len, const std::string strKey, std::string& strValue) {
        std::string::size_type pos_st = 0;
        std::string::size_type pos_end = 0;
        
        // search key
        pos_st = strData.find(strKey, pos_index);
        if (pos_st == std::string::npos) {
            return;
        }
        pos_st += strKey.size();
        if ((pos_len > 0) && ((int)(pos_st - pos_index) > pos_len)) {
            return;
        }
        
        // search linefeed
        pos_end = strData.find(c_LINE_FEED, pos_st);
        if (pos_end == std::string::npos) {
            return;
        }
        
        // remove space
        while (strData.at(pos_st) == c_CHAR_SPACE) {
            pos_st++;
        }
        
        // check double-quot
        if (strData.at(pos_st) == c_CHAR_DB_QUOTE) {
            // get next double-quot
            std::string::size_type pos_tmp = strData.find(c_CHAR_DB_QUOTE, pos_st + 1);
            if ((pos_st != std::string::npos) && (pos_tmp <= pos_end)) {
                pos_st++;
                pos_end = pos_tmp;
            }
        }
        else {
            // get next space
            // del don't use space as delimiter
            //		std::string::size_type pos_tmp = strData.find(c_CHAR_SPACE, pos_st);
            //		if ((pos_st != std::string::npos) && (pos_tmp <= pos_end)) {
            //			pos_end = pos_tmp;
            //		}
            // del end
        }
        
        // get value
        strValue = strData.substr(pos_st, pos_end - pos_st);
    }
    
    void CMimeData::chompString(std::string& strData) {
        // search linefeed
        std::string::size_type pos = strData.rfind(c_LINE_FEED);
        if (pos == std::string::npos) {
            return;
        }
        // remove linefeed
        if (pos == (strData.size() - c_LINE_FEED.size())) {
            strData.erase(pos);
        }
    }
    
    /**
     * @brief Sets Subject
     * @param subject subject
     */
    void CMimeData::setSubject(const std::string &subject) {
        m_Subject = subject;
    }
    
    /**
     * @brief Gets Subject
     * @return Subject value
     */
    std::string CMimeData::getSubject(void) {
        return(m_Subject);
    }
} // namespace mcml
