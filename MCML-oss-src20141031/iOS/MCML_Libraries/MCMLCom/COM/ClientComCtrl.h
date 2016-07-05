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
 * @file ClientComCtrl.h
 * @brief Client communication control class declaration
 */

#ifndef __CLIENTCOMCTRL_H__
#define __CLIENTCOMCTRL_H__

#include <vector>
#include <string>
#include "ComCtrl.h"
#include "ResponseData.h"
#import "ChunkedMultipartConnection.h"
#import "DefaultDelegate.h"



namespace mcml {
	// Class
	class CClientComCtrl : public CComCtrl
	{
    private:
		// chunked start.
		CChunkedMultipartConnection *cmConnection;
		DefaultDelegate *cmDelegate;
		long timeoutMilliSeconds;
		bool chunked;
		int processResponse(CResponseData& resData);
        
		bool started;
		// chunked end.
        
    public:
        /**
         * @brief Constructor
         */
		CClientComCtrl();
        /**
         * @brief Destructor
         */
		~CClientComCtrl();

        /**
         * @brief  Request processing (Only XML data specified, no binary data)
         * @param url web Server side URL [in]
         * @param xmlData XML data (text) [in]
         * @param resData web Details of response from server side [out]
         * @return Returns 0 when communication is successful, and -1 when failed
         */
		int request(const std::string& url, const std::string& xmlData, CResponseData& resData);

        /**
         * @brief  Request processing (XML data+ binary data stream specified)
         * @param url web Server side URL [in]
         * @param xmlData XML data (text) [in]
         * @param binaryDataList  Binary data stream [in]
         * @param resData web Details of response from server side [out]
         * @return Returns 0 when communication is successful, and -1 when failed
         */
		int request(const std::string& url, const std::string& xmlData, const std::vector<std::string>& binaryDataList, CResponseData& resData);

        /**
         * @brief  Request processing (XML data+ binary data specified)
         * @param url web Server side URL [in]
         * @param xmlData XML data (text) [in]
         * @param binaryData  Binary data [in]
         * @param resData web Details of response from server side [out]
         * @return Returns 0 when communication is successful, and -1 when failed
         */
		int request(const std::string& url, const std::string& xmlData, const std::string& binaryData, CResponseData& resData);
        
        /**
         * @brief  Request processing (No XML data, no binary data )
         * @param url web Server side URL [in]
         * @param resData web Details of response from server side [out]
         * @return Returns 0 when communication is successful, and -1 when failed
         */
		int request(const std::string& url, CResponseData& resData);
        
        /**
         * @brief  Request processing (No XML data, binary data specified)
         * @param url web Server side URL [in]
         * @param binaryData  Binary data [in]
         * @param resData web Details of response from server side [out]
         * @return Returns 0 when communication is successful, and -1 when failed
         */
		int requestBinary(const std::string& url, const std::string& binaryData, CResponseData& resData);
        
        /**
         * @brief  Request processing (No XML data, binary data stream specified)
         * @param url web Server side URL [in]
         * @param binaryDataList  Binary data stream [in]
         * @param resData web Details of response from server side [out]
         * @return Returns 0 when communication is successful, and -1 when failed
         */
		int requestBinary(const std::string& url, const std::vector<std::string>& binaryDataList, CResponseData& resData);
        
		// chunked start.
		// Call this every time for sending voice data.
		void setTransferEncodingChunked(bool chunked);
		bool isTransferEncodingChunked();
        
		// chunked end.
        /**
         * @brief Communication timeout value setting
         * @param timeoutMilliSeconds Request timeout value (milliseconds) [in]
         */
		void setTimeout(const unsigned long& timeoutMilliSeconds);
		void forceTimeout() {
			CComCtrl::forceTimout();
		}
	};
} // namespace mcml

#endif // __CLIENTCOMCTRL_H__
