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
 * @file ComCtrl.mm
 * @brief  Client communication control class implementation
 */
#import <Foundation/Foundation.h>
#include "ComCtrl.h"
#include "MimeData.h"

@interface MyDelegate : NSObject {
	NSMutableData *m_ReceivedData;
	Boolean m_Completed;
	Boolean m_TimeoutError;
	Boolean m_OtherError;
	Boolean m_IsUsingSeifSignedCertification;
	int m_HTTPStatusCode;
	NSString *m_HTTPCookie;
}

@property (retain) NSMutableData *m_ReceivedData;
@property (readwrite) Boolean m_Completed;
@property (readwrite) Boolean m_TimeoutError;
@property (readwrite) Boolean m_OtherError;
@property (readwrite) Boolean m_IsUsingSeifSignedCertification;
@property (readwrite) int m_HTTPStatusCode;
@property (retain) NSString *m_HTTPCookie;

- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response;
- (void)connectionDidFinishLoading:(NSURLConnection *)connection;
- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data;
- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error;
// for https
- (BOOL)connection:(NSURLConnection *)connection canAuthenticateAgainstProtectionSpace:(NSURLProtectionSpace *)protectionSpace;
- (void)connection:(NSURLConnection *)connection didReceiveAuthenticationChallenge:(NSURLAuthenticationChallenge *)challenge;
@end

@implementation MyDelegate
@synthesize m_ReceivedData;
@synthesize m_Completed;
@synthesize m_TimeoutError;
@synthesize m_OtherError;
@synthesize m_IsUsingSeifSignedCertification;
@synthesize m_HTTPStatusCode;
@synthesize m_HTTPCookie;

- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response {
	m_HTTPStatusCode = [(NSHTTPURLResponse *)response statusCode];
	NSDictionary *headers = [(NSHTTPURLResponse *)response allHeaderFields];
	// get cookie
	if (headers && [headers count] > 0) {
		self.m_HTTPCookie = [headers objectForKey:@"Set-Cookie"];
	}
	m_ReceivedData = [[NSMutableData alloc]init];
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data {
	[m_ReceivedData appendData:data];
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection {
	m_Completed = TRUE;
}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error {
	m_OtherError = FALSE;
	m_Completed = TRUE;
}

// for https
- (BOOL)connection:(NSURLConnection *)connection canAuthenticateAgainstProtectionSpace:(NSURLProtectionSpace *)protectionSpace {
	return [protectionSpace.authenticationMethod isEqualToString:NSURLAuthenticationMethodServerTrust];
}

- (void)connection:(NSURLConnection *)connection didReceiveAuthenticationChallenge:(NSURLAuthenticationChallenge *)challenge {
	[challenge.sender useCredential:[NSURLCredential credentialForTrust:challenge.protectionSpace.serverTrust]
	     forAuthenticationChallenge:challenge];
	[challenge.sender continueWithoutCredentialForAuthenticationChallenge:challenge];
}

- (void)dealloc {
	//[m_ReceivedData release];
	//[m_HTTPCookie release];
	//[super dealloc];
}

@end

namespace mcml
{
#define TIMEOUT_MONITORING_PERIOD_FLOAT 0.1 // 100msec(Floating)
#define TIMEOUT_MONITORING_PERIOD_FIX   100 // 100msec(Fixed)
    
    /**
     * @brief Constructor
     */
    CComCtrl::CComCtrl() {
        m_SendCookie = "";
        m_RecvCookie = "";
        m_TimeoutCounterThreshold = UINT_MAX;
        m_IsUsingSeifSignedCertification = FALSE;
    }
    
    /**
     * @brief Destructor
     */
    CComCtrl::~CComCtrl() {
    }
    
    // sendrecv
    int CComCtrl::sendrecv(const std::string& url, const std::string& xmlData, const std::vector <std::string>& binaryDataList, CComData& comData) {
        //NSLog(@"sendrecv start");
        //-----------------------------
        // Make Body
        //-----------------------------
        CMimeData mimeData;
        // set header
        mimeData.addMimeHeader();
        // set string data part
        // modified for iPhone start
        // - xmldata is always necessary
        // if (!xmlData.empty()) {
        mimeData.addMimeBodyPart(xmlData);
        // }
        // modified for iPhone end
        // set binary data part
        for (int i = 0; i < (int)binaryDataList.size(); i++) {
            mimeData.addMimeBody(binaryDataList.at(i));
        }
        // set footer
        mimeData.addMimeFooter();
        
        //-------------------------------------
        // Make Data & Preparation Send/Receive
        //-------------------------------------
        // create post request
        std::string urlWork = url;
        std::string::size_type pos = urlWork.length() - 1;
        if (urlWork.at(pos) == '/') {
            urlWork.erase(pos);
        }
        NSURL *postUrl = [NSURL URLWithString:[NSString stringWithUTF8String:urlWork.c_str()]];
        NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:postUrl];
        [request setCachePolicy:NSURLRequestUseProtocolCachePolicy];
        [request setHTTPMethod:@"POST"];
        [request setHTTPShouldHandleCookies:NO];    // disable NSHTTPCookieStorage
        //NSLog(@"setHTTPShouldHandleCookies:NO");
        
        // set cookie
        if (!m_SendCookie.empty()) {
            //NSLog(@"set cookie=%s",m_SendCookie.c_str());
            [request setValue:[NSString stringWithUTF8String:m_SendCookie.c_str()] forHTTPHeaderField:@"Cookie"];
        }
        // set body
        [request setHTTPBody:[NSData dataWithBytes:mimeData.getMimeMessage().c_str() length:mimeData.getMimeMessage().size()]];
        
        //-----------------------------
        // Send and Receive
        //-----------------------------
        // connect
        MyDelegate *mydel = [[MyDelegate alloc] init];
        mydel.m_Completed = FALSE;
        mydel.m_TimeoutError = FALSE;
        mydel.m_OtherError = FALSE;
        mydel.m_IsUsingSeifSignedCertification = m_IsUsingSeifSignedCertification;
        
        NSURLConnection *connection = [[NSURLConnection alloc] initWithRequest:request delegate:mydel];
        if (!connection) {
            //[mydel release] ;
            return -1;
        }
        
        m_currentTimeoutCounter = 0;
        
        // create thread
        @autoreleasepool {
            while (!mydel.m_Completed) {
                [[NSRunLoop currentRunLoop] runUntilDate:[NSDate dateWithTimeIntervalSinceNow:TIMEOUT_MONITORING_PERIOD_FLOAT]];
                if (m_currentTimeoutCounter >= m_TimeoutCounterThreshold) {
                    NSLog(@"A TIMEOUT HAS BEEN FORCED/REACHED!");
                    //Make sure no more delegate callbacks are invoked by this connection
                    [connection cancel];
                    
                    if (mydel.m_Completed == FALSE) {
                        mydel.m_Completed = TRUE;
                        mydel.m_TimeoutError = TRUE;
                    }
                }
                else {
                    m_currentTimeoutCounter++;
                }
            }
            
            // ocurred time out error
            if (mydel.m_TimeoutError) {
                return -101;
            }
            
            // communicaiton errot
            if (mydel.m_OtherError) {
                return -102;
            }
            
            // HTTP response
            if (mydel.m_HTTPStatusCode != 200) {
                return -103;
            }
            
            //-----------------------------
            // Parse
            //-----------------------------
            // get Cookie
            if (mydel.m_HTTPCookie != nil) {
                m_RecvCookie = [mydel.m_HTTPCookie UTF8String];
            }
            if ([mydel.m_ReceivedData length] == 0) {
                return -1;
            }
            
            // parse data
            std::string recvData;
            recvData.assign((char *)[mydel.m_ReceivedData bytes], [mydel.m_ReceivedData length]);
            mimeData.parseData(recvData);
            
            //-----------------------------
            // Set Result
            //-----------------------------
            if (mimeData.getTextData(0).empty()) {
                return -1;
            }
            comData.setXML(mimeData.getTextData(0));
            comData.setBinaryList(mimeData.getBinaryList());
        }
        //NSLog(@"sendrecv end");
        return 0;
    }
    
    std::string& CComCtrl::getRecvCookie() {
        return m_RecvCookie;
    }
    
    void CComCtrl::setSendCookie(const std::string& cookie) {
        m_SendCookie = cookie;
    }
    
    /**
     * @brief Communication timeout value setting
     * @param timeoutMilliSeconds Request timeout value (milliseconds) [in]
     */
    void CComCtrl::setTimeout(const unsigned long& timeoutMilliSeconds) {
        //NSLog(@"setTimeout");
        if (timeoutMilliSeconds > 0) {
            m_TimeoutCounterThreshold = timeoutMilliSeconds / TIMEOUT_MONITORING_PERIOD_FIX;
            if (m_TimeoutCounterThreshold == 0) {
                m_TimeoutCounterThreshold = 1;
            }
        }
        else {
            // near infinitie
            m_TimeoutCounterThreshold = UINT_MAX;
        }
    }
    
    /**
     * @brief Self-signed certification usage setting
     */
    void CComCtrl::setUsingSelfSignedCertification(void) {
        //NSLog(@"setUsingSelfSignedCertification");
        m_IsUsingSeifSignedCertification = TRUE;
    }
    
    void CComCtrl::forceTimout() {
        //NSLog(@"CComCtrl::forceTimeout()");
        m_currentTimeoutCounter = m_TimeoutCounterThreshold;
    }
    
} // namespace mcml
