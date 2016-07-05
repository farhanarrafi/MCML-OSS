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
 * @file ClientComCtrl.mm
 * @brief  Client communication control class implementation
 */

#include "ClientComCtrl.h"
#include "MimeData.h"

#define kDefaultTimeoutSeconds (30.0)

//Added for #666 Unit Test.

#ifdef UNIT_TEST
#define UT666(m, args ...) NSLog(@"[UT666] " m, ## args)
#else
#define UT666(m, args ...)
#endif

// chunked start.
// Original encryption flag.
#define ENCRYPTION_ON NO
// chunked end.

namespace mcml
{
    /**
     * @brief Constructor
     */
    CClientComCtrl::CClientComCtrl() {
        // chunked start.
        cmConnection = nil;
        cmDelegate = nil;
        timeoutMilliSeconds = kDefaultTimeoutSeconds * 1000;
        chunked = false;
        started = false;
        // chunked end.
    }
    
    /**
     * @brief Destructor
     */
    CClientComCtrl::~CClientComCtrl() {
        // chunked start.
#ifdef UNIT_TEST
        UT666(@"cmConnection and cmDelegate released.");
#endif
        // chunked end.
    }
    
    // chunked start.
    void CClientComCtrl::setTransferEncodingChunked(bool chunked) {
        this->chunked = chunked;
        
        if (chunked) {
            UT666(@"Flag is chunked");
            if (cmDelegate == nil) {
                UT666(@"cmDelegate is nil.");
                cmDelegate = [[DefaultDelegate alloc] init];
                UT666(@" -> initialized.");
            }
            if (cmConnection == nil) {
                UT666(@"cmConnection is nil.");
                cmConnection = [[CChunkedMultipartConnection alloc]
                                initWithDelegate:(id <NSURLConnectionDataDelegate> ) cmDelegate
                                needEncryption:ENCRYPTION_ON];
                UT666(@" -> intialized.");
            }
            else {
                UT666(@"cmConnection exists.");
                [cmConnection closeConnection];
                cmDelegate.mOtherError = NO;
                cmDelegate.mCompleted = NO;
                cmDelegate.mTimeoutError = NO;
                cmDelegate.mHTTPCookie = nil;
                cmDelegate.mHTTPStatusCode = 0;
                cmDelegate.myCredential = nil;
                [cmDelegate.myData setLength:0];
                UT666(@" -> cmConnection was closed. And cmDelegate reset.");
            }
        }
        else {
            UT666(@"Flag is not chunked.");
            if (cmConnection != nil) {
                UT666(@"cmConnection exists.");
                [cmConnection closeConnection];
                cmDelegate.mOtherError = NO;
                cmDelegate.mCompleted = NO;
                cmDelegate.mTimeoutError = NO;
                cmDelegate.mHTTPCookie = nil;
                cmDelegate.mHTTPStatusCode = 0;
                cmDelegate.myCredential = nil;
                [cmDelegate.myData setLength:0];
                UT666(@" -> cmConnection was closed. And cmDelegate reset.");
            }
        }
    }
    
    bool CClientComCtrl::isTransferEncodingChunked() {
        return this->chunked;
    }
    
    // chunked end.
    
    /**
     * @brief  Request processing (Only XML data specified, no binary data)
     * @param url web Server side URL [in]
     * @param xmlData XML data (text) [in]
     * @param resData web Details of response from server side [out]
     * @return Returns 0 when communication is successful, and -1 when failed
     */
    int CClientComCtrl::request(const std::string& url, const std::string& xmlData, CResponseData& resData) {
        // chunked start.
        if (this->chunked) {
            if (![cmConnection isOpen]) {
                if (!started) {
                    started = true;
                    [cmConnection setTimeout:timeoutMilliSeconds];
                    [cmConnection openConnectionWithURL:[NSString stringWithUTF8String:url.c_str()]];
                }
                else {
                    if (cmDelegate.mCompleted) {
                        processResponse(resData);
                        return 0;
                    }
                    else {
                        //return -1;
                    }
                }
            }
            
            [cmConnection sendString:[NSString stringWithUTF8String:xmlData.c_str()]];
            if (cmDelegate.mOtherError) {
                return -1;
            }
            return 0;
        }
        // chunked start.
        
        std::vector <std::string> binaryDataList;
        
        // call request
        return request(url, xmlData, binaryDataList, resData);
    }
    
    /**
     * @brief  Request processing (XML data+ binary data specified)
     * @param url web Server side URL [in]
     * @param xmlData XML data (text) [in]
     * @param binaryData  binary data [in]
     * @param resData web Details of response from server side [out]
     * @return Returns 0 when communication is successful, and -1 when failed
     */
    int CClientComCtrl::request(const std::string& url, const std::string& xmlData, const std::string& binaryData, CResponseData& resData) {
        // chunked start.
        if (this->chunked) {
            if (![cmConnection isOpen]) {
                if (!started) {
                    started = true;
                    [cmConnection setTimeout:timeoutMilliSeconds];
                    [cmConnection openConnectionWithURL:[NSString stringWithUTF8String:url.c_str()]];
                }
                else {
                    if (cmDelegate.mCompleted) {
                        processResponse(resData);
                        return 0;
                    }
                }
            }
            [cmConnection sendString:[NSString stringWithUTF8String:xmlData.c_str()]];
            [cmConnection sendData:[NSData dataWithBytes:binaryData.data() length:binaryData.size()]];
            if (cmDelegate.mOtherError) {
                return -1;
            }
            return 0;
        }
        // chunked end.
        
        // set binaryDataList
        std::vector <std::string> binaryDataList;
        if (!binaryData.empty()) {
            binaryDataList.push_back(binaryData);
        }
        
        // call request
        return request(url, xmlData, binaryDataList, resData);
    }
    
    /**
     * @brief  Request processing (XML data+ binary data stream specified)
     * @param url web Server side URL [in]
     * @param xmlData XML data (text) [in]
     * @param binaryDataList  binary data stream [in]
     * @param resData web Details of response from server side [out]
     * @return Returns 0 when communication is successful, and -1 when failed
     */
    int CClientComCtrl::request(const std::string& url, const std::string& xmlData, const std::vector <std::string>& binaryDataList, CResponseData& resData) {
        // chunked start.
        if (this->chunked) {
            if (![cmConnection isOpen]) {
                if (!started) {
                    started = true;
                    [cmConnection setTimeout:timeoutMilliSeconds];
                    [cmConnection openConnectionWithURL:[NSString stringWithUTF8String:url.c_str()]];
                }
                else {
                    if (cmDelegate.mCompleted) {
                        processResponse(resData);
                        return 0;
                    }
                }
            }
            [cmConnection sendString:[NSString stringWithUTF8String:xmlData.c_str()]];
            for (int i = 0; i < binaryDataList.size(); i++) {
                [cmConnection sendData:[NSData dataWithBytes:binaryDataList[i].data() length:binaryDataList[i].size()]];
            }
            if (cmDelegate.mOtherError) {
                return -1;
            }
            return 0;
        }
        // chunked end.
        
        
        // set cookie
        CComCtrl::setSendCookie(CComCtrl::getRecvCookie());
        
        // send and recv
        return CComCtrl::sendrecv(url, xmlData, binaryDataList, resData);
    }
    
    // chunked start.
    int CClientComCtrl::processResponse(CResponseData& resData) {
        long counter = this->timeoutMilliSeconds / 100;
        while (counter-- > 0) {
            BOOL completed = NO;
            if (cmDelegate.mOtherError) {
                NSLog(@"CM Connection has error.");
                completed = YES;
            }
            else if (cmDelegate.mCompleted) {
                NSLog(@"CM Connection completed");
                completed = YES;
            }
            
            if (completed) {
                int statusCode = [cmConnection getStatusCode];
                UT666(@"Cookie: %s", this->getCurrentCookie().c_str());
                NSLog(@"CM Status Code = %d", statusCode);
                
                if (statusCode == 200) {
                    if ([cmDelegate.myData length] > 0) {
                        std::string recvData;
                        CMimeData mimeData;
                        
                        //TEST: start.
                        //NSString *path = [[NSHomeDirectory() stringByAppendingPathComponent:@"tmp"]
                        //                  stringByAppendingPathComponent:@"rec.dat"];
                        //[cmDelegate.myData writeToFile:path atomically:YES];
                        //TEST: end.
                        
                        recvData.assign((char *)[cmDelegate.myData mutableBytes], [cmDelegate.myData length]);
                        mimeData.parseData(recvData);
                        resData.setXML(mimeData.getTextData(0));
                        resData.setBinaryList(mimeData.getBinaryList());
                        NSLog(@"ResponseData was set.");

                        if (mimeData.getTextData(0).empty()) {
                            return -1;
                        }
                        else {
                            return 0;
                        }
                    }
                    else {
                        return -1;
                    }
                }
                else {
                    return -103;
                }
            }
            [NSThread sleepForTimeInterval:0.1];
        }
        return -101;
    }
    
    //  chunked end.
    
    /**
     * @brief  Request processing (No XML data, no binary data )
     * @param url web Server side URL [in]
     * @param resData web Details of response from server side [out]
     * @return Returns 0 when communication is successful, and -1 when failed
     */
    int CClientComCtrl::request(const std::string& url, CResponseData& resData) {
        // chunked start.
        if (this->chunked) {
            @try {
                if (![cmConnection isOpen]) {
                    if (!started) {
                        [cmConnection setTimeout:timeoutMilliSeconds];
                        [cmConnection openConnectionWithURL:[NSString stringWithUTF8String:url.c_str()]];
                    }
                    else {
                        if (cmDelegate.mCompleted) {
                            return processResponse(resData);
                        }
                    }
                }
                started = false;
                [cmConnection sendTail];
                return processResponse(resData);
            }
            @finally
            {
                started = false;
                [cmConnection closeConnection];
                cmDelegate.mOtherError = NO;
                cmDelegate.mCompleted = NO;
                cmDelegate.mTimeoutError = NO;
                cmDelegate.mHTTPCookie = nil;
                cmDelegate.mHTTPStatusCode = 0;
                cmDelegate.myCredential = nil;
                [cmDelegate.myData setLength:0];
            }
        }
        // chunked end.
        
        std::string xmlData;
        std::vector <std::string> binaryDataList;
        
        // call request
        return request(url, xmlData, binaryDataList, resData);
    }
    
    /**
     * @brief  Request processing (No XML data, binary data specified)
     * @param url web Server side URL [in]
     * @param binaryData  Binary data [in]
     * @param resData web Details of response from server side [out]
     * @return Returns 0 when communication is successful, and -1 when failed
     */
    int CClientComCtrl::requestBinary(const std::string& url, const std::string& binaryData, CResponseData& resData) {
        // chunked start.
        if (this->chunked) {
            if (![cmConnection isOpen]) {
                if (!started) {
                    started = true;
                    [cmConnection setTimeout:timeoutMilliSeconds];
                    [cmConnection openConnectionWithURL:[NSString stringWithUTF8String:url.c_str()]];
                }
                else {
                    if (cmDelegate.mCompleted) {
                        processResponse(resData);
                        return 0;
                    }
                }
            }
            [cmConnection sendData:[NSData dataWithBytes:binaryData.data() length:binaryData.size()]];
            if (cmDelegate.mOtherError) {
                return -1;
            }
            return 0;
        }
        // chunked end.
        
        std::string xmlData;
        std::vector <std::string> binaryDataList;
        if (!binaryData.empty()) {
            binaryDataList.push_back(binaryData);
        }
        
        // call request
        return request(url, xmlData, binaryDataList, resData);
    }
    
    /**
     * @brief  Request processing (No XML data,  binary data stream specified)
     * @param url web Server side URL [in]
     * @param binaryDataList  Binary data stream [in]
     * @param resData web Details of response from server side [out]
     * @return Returns 0 when communication is successful, and -1 when failed
     */
    int CClientComCtrl::requestBinary(const std::string& url, const std::vector <std::string>& binaryDataList, CResponseData& resData) {
        // chunked start.
        if (this->chunked) {
            if (![cmConnection isOpen]) {
                if (!started) {
                    started = true;
                    [cmConnection setTimeout:timeoutMilliSeconds];
                    [cmConnection openConnectionWithURL:[NSString stringWithUTF8String:url.c_str()]];
                }
                else {
                    if (cmDelegate.mCompleted) {
                        processResponse(resData);
                        return 0;
                    }
                }
            }
            for (int i = 0; i < binaryDataList.size(); i++) {
                [cmConnection sendData:[NSData dataWithBytes:binaryDataList[i].data() length:binaryDataList[i].size()]];
            }
            if (cmDelegate.mOtherError) {
                return -1;
            }
            return 0;
        }
        // chunked end.
        
        
        std::string xmlData;
        
        // call request
        return request(url, xmlData, binaryDataList, resData);
    }
    
    /**
     * @brief Communication timeout value setting
     * @param timeoutMilliSeconds Request timeout value (milliseconds) [in]
     */
    void CClientComCtrl::setTimeout(const unsigned long& timeoutMilliSeconds) {
        this->timeoutMilliSeconds = timeoutMilliSeconds;
        if (this->chunked) {
            if (cmConnection != nil) {
                [cmConnection setTimeout:timeoutMilliSeconds];
            }
            // Dont return;
        }
        CComCtrl::setTimeout(timeoutMilliSeconds);
    }
    
} // namespace mcml
