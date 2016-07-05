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
 * @file DefaultDelegate.mm
 * @brief Default delegate class implementation
 */

#import "DefaultDelegate.h"


@implementation DefaultDelegate

@synthesize myData;
@synthesize mHTTPCookie;
@synthesize mCompleted;
@synthesize myCredential;
@synthesize mOtherError;
@synthesize mTimeoutError;
@synthesize mHTTPStatusCode;

/**
 * @brief Initialization
 */
- (DefaultDelegate *)init {
	if (self = [super init]) {
		self.myData = [NSMutableData data];
		self.mCompleted = FALSE;
	}
    
	return self;
}

/**
 * @brief Object release.
 */
- (void)dealloc {
	//#MCML Start
	/*
     [myData release];
     [mHTTPCookie release];
     [myCredential release];
     [super dealloc];
	 */
	//#MCML End
}

/**
 * @brief Sent when the connection has received sufficient data to construct the URL response for its request.
 * @param connection
 *          The connection sending the message.
 * @param response
 *          The URL response for the connection's request. This object is immutable and will not be modified
 *          by the URL loading system once it is presented to the delegate.
 */
- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response {
	self.mHTTPStatusCode = [(NSHTTPURLResponse *)response statusCode];
	NSDictionary *headers = [(NSHTTPURLResponse *)response allHeaderFields];
    
	// get cookie
	if (headers && [headers count] > 0) {
		if ([headers objectForKey:@"Set-Cookie"] != nil) {
			//NSLog(@">%@<",[headers objectForKey:@"Set-Cookie"]);
			self.mHTTPCookie = [headers objectForKey:@"Set-Cookie"];
			//NSLog(@"mHTTPCookie is set:%@",mHTTPCookie);
		}
		else {
			self.mHTTPCookie = nil; //Modification for #434 Tabor
		}
	}
	self.myData = [NSMutableData data];
	//NSLog(@"<RESET THE DATA VARIABLE>...%@",[[NSThread currentThread] name]);
}

/**
 * @brief Sent as a connection loads data incrementally.
 * @param connection
 *          The connection sending the message.
 * @param data
 *          The newly available data. The delegate should concatenate the contents of each data object delivered
 *          to build up the complete data for a URL load.
 */
- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data {
	NSLog(@"Received Data of Size %d...%@", [data length], [[NSThread currentThread] name]);
    
    
	[self.myData appendData:data];
}

/**
 * @brief Sent when a connection has finished loading successfully.
 * @param connection
 *          The connection sending the message.
 */
- (void)connectionDidFinishLoading:(NSURLConnection *)connection {
	//NSLog(@"CONNECTION FINISHED");
	self.mCompleted = TRUE;
	//[connection release];// Removed
}

/**
 * @brief Sent when a connection fails to load its request successfully.
 * @param connection
 *          The connection sending the message.
 * @param error
 *          An error object containing details of why the connection failed to load the request successfully.
 */
- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error {
	//NSLog(@"CONNECTION FAILED");
	self.mOtherError = FALSE;
	self.mCompleted = TRUE;
}

/**
 * @brief Sent to determine whether the delegate is able to respond to a protection space’s form of authentication.
 * @param connection
 *          The connection sending the message.
 * @param protectionSpace
 *          The protection space that generates an authentication challenge.
 * @return YES if the delegate was able to respond to the protection space’s form of authentication, otherwise NO.
 */
- (BOOL)connection:(NSURLConnection *)connection canAuthenticateAgainstProtectionSpace:(NSURLProtectionSpace *)protectionSpace {
	return [protectionSpace.authenticationMethod isEqualToString:NSURLAuthenticationMethodServerTrust];
}

/**
 * @brief Sent when a connection must authenticate a challenge in order to download its request.
 * @param connection
 *          The connection sending the message.
 * @param challenge
 *          The challenge that connection must authenticate in order to download its request.
 */
- (void)connection:(NSURLConnection *)connection didReceiveAuthenticationChallenge:(NSURLAuthenticationChallenge *)challenge {
	//NSLog(@"RECEIVED AUTHENTICATION CHALLENGE");
    
	if (self.myCredential != nil) {
		//NSLog(@"Existing Credentials sent");
		[challenge.sender useCredential:self.myCredential forAuthenticationChallenge:challenge];
	}
	else {
		//NSLog(@"No credentials exist, so none are sent. Continuing");
		[challenge.sender continueWithoutCredentialForAuthenticationChallenge:challenge];
	}

}

@end
