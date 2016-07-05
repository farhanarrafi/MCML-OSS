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
 * @file ChunkedMultipartConnection.h
 * @brief ChunkedMultipartConnection
 */

#import <Foundation/Foundation.h>

//#define kDefaultTimeoutSeconds (10.0)
#define kStreamBufferSize (1024)

// Connection class using TransferEncoding: chunked in HTTP protocol.
// This class was created by #666.
@interface CChunkedMultipartConnection : NSObject

/**
 * @brief Initialization method
 * @param delegate Delegate object
 * @param encryption Encryption flag
 * @return self
 */
- (id)initWithDelegate:(id <NSURLConnectionDataDelegate> )delegate needEncryption:(BOOL)encryption;

/**
 * @brief URL connection method
 * @param urlString  Target URL
 */
- (void)openConnectionWithURL:(NSString *)urlString;

/**
 * @brief  Character string transmission
 * @param string  Target character string
 */
- (void)sendString:(NSString *)string;

/**
 * @brief Data transmission
 * @param data  Target data
 */
- (void)sendData:(NSData *)data;

/**
 * @brief Termination data transmission
 */
- (void)sendTail;

/**
 * @brief Connection cancel
 */
- (void)cancelConnection;

/**
 * @brief Connection close
 */
- (void)closeConnection;

/**
 * @brief Gets Cookie
 * @return cookie
 */
- (NSString *)getRecvCookie;

/**
 * @brief Sets Cookie
 * @param cookie
 */
- (void)setSendCookie:(NSString *)cookie;

/**
 * @brief Gets status code
 * @return Status code
 */
- (NSInteger)getStatusCode;

/**
 * @brief Sets timeout value
 * @param timeoutMilliSeconds Request timeout value (milliseconds)
 */
- (void)setTimeout:(NSUInteger)timeoutMilliSeconds;

/**
 * @brief isOpen
 * @return True if self.connection != nil
 */
- (BOOL)isOpen;

/**
 * @brief setExitNow
 */
- (void)setExitNow;

@end
