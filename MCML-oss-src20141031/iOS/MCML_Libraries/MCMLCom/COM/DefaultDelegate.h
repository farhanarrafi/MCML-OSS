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
 * @file DefaultDelegate.h
 * @brief Default delegate class declaration
 */


#ifndef __DEFAULTDELEGATE_H__
#define __DEFAULTDELEGATE_H__

#include <vector>
#include <string>

//namespace mcml{
@interface DefaultDelegate : NSObject {
@private
	NSMutableData *myData;
	NSString *mHTTPCookie;
	NSURLCredential *myCredential;
	Boolean mCompleted;
	Boolean mOtherError;
	Boolean mTimeoutError;
	int mHTTPStatusCode;
}
/**
 * @brief Initialization
 */
- (id)init;
/**
 * @brief Sent when the connection has received sufficient data to construct the URL response for its request.
 * @param connection
 *          The connection sending the message.
 * @param response
 *          The URL response for the connection's request. This object is immutable and will not be modified
 *          by the URL loading system once it is presented to the delegate.
 */
- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response;
/**
 * @brief Sent when a connection has finished loading successfully.
 * @param connection
 *          The connection sending the message.
 */
- (void)connectionDidFinishLoading:(NSURLConnection *)connection;
/**
 * @brief Sent as a connection loads data incrementally.
 * @param connection
 *          The connection sending the message.
 * @param data
 *          The newly available data. The delegate should concatenate the contents of each data object delivered
 *          to build up the complete data for a URL load.
 */
- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data;
/**
 * @brief Sent when a connection fails to load its request successfully.
 * @param connection
 *          The connection sending the message.
 * @param error
 *          An error object containing details of why the connection failed to load the request successfully.
 */
- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error;

/**
 * @brief Sent to determine whether the delegate is able to respond to a protection space’s form of authentication.
 * @param connection
 *          The connection sending the message.
 * @param protectionSpace
 *          The protection space that generates an authentication challenge.
 * @return YES if the delegate if able to respond to a protection space’s form of authentication, otherwise NO.
 */
- (BOOL)connection:(NSURLConnection *)connection canAuthenticateAgainstProtectionSpace:(NSURLProtectionSpace *)protectionSpace;
/**
 * @brief Sent when a connection must authenticate a challenge in order to download its request.
 * @param connection
 *          The connection sending the message.
 * @param challenge
 *          The challenge that connection must authenticate in order to download its request.
 */
- (void)connection:(NSURLConnection *)connection didReceiveAuthenticationChallenge:(NSURLAuthenticationChallenge *)challenge;

// chunked start.
#ifdef UNIT_TEST
/**
 * @brief Sent as the body (message data) of a request is transmitted (such as in an http POST request).
 * @param connection
 *          The connection sending the message.
 * @param bytesWritten
 *          The number of bytes written in the latest write.
 * @param totalBytesWritten
 *          The total number of bytes written for this connection.
 * @param totalBytesExpectedToWrite
 *          The number of bytes the connection expects to write.
 */
- (void)   connection:(NSURLConnection *)connection didSendBodyData:(NSInteger)bytesWritten
    totalBytesWritten:(NSInteger)totalBytesWritten totalBytesExpectedToWrite:(NSInteger)totalBytesExpectedToWrite;
#endif
// chunked end.

@property (nonatomic, retain) NSMutableData *myData;
@property (nonatomic, retain) NSString *mHTTPCookie;
@property (nonatomic, retain) NSURLCredential *myCredential;
@property (nonatomic) Boolean mCompleted;
@property (nonatomic) Boolean mOtherError;
@property (nonatomic) Boolean mTimeoutError;
@property (nonatomic) int mHTTPStatusCode;


@end
//} // namespace mcml

#endif // __DEFAULTDELEGATE_H__
