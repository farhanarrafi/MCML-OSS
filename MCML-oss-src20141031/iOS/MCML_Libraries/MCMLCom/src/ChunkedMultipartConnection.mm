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
 * @file ChunkedMultipartConnection.mm
 * @brief ChunkedMultipartConnection.mm
 */

#import <sys/socket.h>
#import <unistd.h>
#import <CFNetwork/CFNetwork.h>
#include <vector>

#import "Code.h"
#import "ChunkedMultipartConnection.h"

#define kDefaultTimeoutSeconds (30.0)
#define kStreamBufferSize (1024)

// Thread stopper.
static NSString *const exitKey = @"ThreadShouldExitNow";

//=======================================================================================
// Utility class for NSData.

@interface CDataUtils : NSObject
+ (NSInteger)indexOf:(NSData *)binaryTarget withData:(NSData *)binaryData;
+ (NSString *)stringWithData:(NSData *)data;
@end

@implementation CDataUtils
+ (NSInteger)indexOf:(NSData *)binaryTarget withData:(NSData *)binaryData {
	const char *data = (const char *)[binaryData bytes];
	int dataLength = [binaryData length];
    
	const char *target = (const char *)[binaryTarget bytes];
	size_t targetLength = [binaryTarget length];
	int i = 0, j = 0;
	for (i = 0; i < dataLength; ) {
		if (data[i] == target[j]) {
			j++;
			i++;
			if (j == targetLength) {
				break;
			}
		}
		else {
			i = i - j + 1;
			j = 0;
		}
	}
	if (i == dataLength && j != targetLength) {
		return -1;
	}
	return i - targetLength;
}

+ (NSString *)stringWithData:(NSData *)data {
	return [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];     //#MCML
}

@end

//-------------------------------------------------------------------------------------
// Utitlites from Apple's SimpleURLConnections Sample.

static void CFStreamCreateBoundPairCompat(
                                          CFAllocatorRef    alloc,
                                          CFReadStreamRef * readStreamPtr,
                                          CFWriteStreamRef *writeStreamPtr,
                                          CFIndex           transferBufferSize
                                          ) {
	// This is a drop-in replacement for CFStreamCreateBoundPair that is necessary because that
	// code is broken on iOS versions prior to iOS 5.0 <rdar://problem/7027394> <rdar://problem/7027406>.
	// This emulates a bound pair by creating a pair of UNIX domain sockets and wrapper each end in a
	// CFSocketStream.  This won't give great performance, but it doesn't crash!
	NSLog(@"METHOD: %s", __func__);
    
#pragma unused(transferBufferSize)
	int err;
	Boolean success;
	CFReadStreamRef readStream;
	CFWriteStreamRef writeStream;
	int fds[2];
    
	assert(readStreamPtr != NULL);
	assert(writeStreamPtr != NULL);
    
	readStream = NULL;
	writeStream = NULL;
    
	// Create the UNIX domain socket pair.
    
	err = socketpair(AF_UNIX, SOCK_STREAM, 0, fds);
	if (err == 0) {
		CFStreamCreatePairWithSocket(alloc, fds[0], &readStream,  NULL);
		CFStreamCreatePairWithSocket(alloc, fds[1], NULL, &writeStream);
        
		// If we failed to create one of the streams, ignore them both.
        
		if ((readStream == NULL) || (writeStream == NULL)) {
			if (readStream != NULL) {
				CFRelease(readStream);
				readStream = NULL;
			}
			if (writeStream != NULL) {
				CFRelease(writeStream);
				writeStream = NULL;
			}
		}
		assert((readStream == NULL) == (writeStream == NULL));
        
		// Make sure that the sockets get closed (by us in the case of an error,
		// or by the stream if we managed to create them successfull).
        
		if (readStream == NULL) {
			err = close(fds[0]);
			assert(err == 0);
			err = close(fds[1]);
			assert(err == 0);
		}
		else {
			success = CFReadStreamSetProperty(readStream, kCFStreamPropertyShouldCloseNativeSocket, kCFBooleanTrue);
			assert(success);
			success = CFWriteStreamSetProperty(writeStream, kCFStreamPropertyShouldCloseNativeSocket, kCFBooleanTrue);
			assert(success);
		}
	}
    
	*readStreamPtr = readStream;
	*writeStreamPtr = writeStream;
}

//-----------------------------------------------------------------------------------
// CChunkedMultipartConnection implementation.

// Private category.
@interface CChunkedMultipartConnectionImpl : NSObject <NSURLConnectionDataDelegate, NSStreamDelegate>
{
	BOOL _first;
}

// Private properties.
@property (nonatomic, strong) NSString *multipartBoundary;
@property (nonatomic, assign) id delegate;
@property (nonatomic, assign) BOOL needEncryption;
@property (nonatomic, assign) BOOL sendDelayed;
@property (nonatomic, assign) NSTimeInterval timeoutSeconds;
@property (nonatomic, strong) NSURLConnection *connection;
@property (nonatomic, strong) NSOutputStream *producerStream;
@property (nonatomic, strong) NSInputStream *consumerStream;
@property (nonatomic, strong) NSString *cookie;
@property (nonatomic, strong) NSMutableArray *chunkStack;
@property (nonatomic, assign) NSInteger statusCode;

// Private methods.
- (void)prepareRequestStreams;
- (void)stopSendWithStatus:(NSString *)statusString;
+ (void)createBoundInputStream:(NSInputStream **)inputStreamPtr
                  outputStream:(NSOutputStream **)outputStreamPtr
                    bufferSize:(NSUInteger)bufferSize;

// Public methods.
- (id)initWithDelegate:(id <NSURLConnectionDataDelegate> )delegate
        needEncryption:(BOOL)encryption;
- (void)openConnectionWithURL:(NSString *)urlString;
- (void)sendString:(NSString *)string;
- (void)sendData:(NSData *)data;
- (void)sendTail;
- (void)cancelConnection;
- (void)closeConnection;
- (NSString *)getRecvCookie;
- (void)setSendCookie:(NSString *)cookie;
- (NSInteger)getStatusCode;
- (void)setTimeout:(NSUInteger)timeoutMilliSeconds;
- (BOOL)isOpen;

@end

@implementation CChunkedMultipartConnectionImpl

//-----------------------------------------------------------------------------------
// Properties.

@synthesize multipartBoundary = _multipartBoundary;
@synthesize delegate = _delegate;
@synthesize needEncryption = _needEncryption;
@synthesize sendDelayed = _sendDelayed;
@synthesize timeoutSeconds = _timeoutSeconds;
@synthesize connection = _connection;
@synthesize producerStream = _producerStream;
@synthesize consumerStream = _consumerStream;
@synthesize cookie = _cookie;
@synthesize chunkStack = _chunkStack;
@synthesize statusCode = _statusCode;

//-----------------------------------------------------------------------------------
// Public Methods.

- (id)initWithDelegate:(id <NSURLConnectionDataDelegate> )delegate needEncryption:(BOOL)encryption {
	NSLog(@"METHOD: %s", __func__);
    
	if (self = [super init]) {
		self.delegate = delegate;
		self.needEncryption = encryption;
        
		self.timeoutSeconds = kDefaultTimeoutSeconds;
		self.sendDelayed = NO;
		self.chunkStack = [NSMutableArray array];
        
		_first = YES;
	}
	return self;
}

- (void)dealloc {
	NSLog(@"METHOD: %s", __func__);
    
	//#MCML Start
	/*
     [_multipartBoundary release];
     [_connection release];
     [_producerStream release];
     [_consumerStream release];
     [_cookie release];
     [_chunkStack release];
     
     [super dealloc];
	 */
	//#MCML End
}

- (void)openConnectionWithURL:(NSString *)urlString {
	NSLog(@"METHOD: %s", __func__);
    
	[self stopSendWithStatus:nil];
	[self.chunkStack removeAllObjects];
	[self prepareRequestStreams];
    
	NSURL *url = [NSURL URLWithString:urlString];
	NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:url];
	NSAssert(request != nil, @"Failed to create request.");
    
	[request setHTTPMethod:@"POST"];
	[request setHTTPBodyStream:self.consumerStream];
    
	self.multipartBoundary = @"9FDC54D2C4F243A1A5B9";
	NSString *contentType = [NSString stringWithFormat:@"multipart/mixed; boundary=\"%@\"", self.multipartBoundary];
    
	[request setTimeoutInterval:_timeoutSeconds];
	[request setValue:contentType forHTTPHeaderField:@"Content-Type"];
	[request setValue:@"chunked" forHTTPHeaderField:@"Transfer-Encoding"];
	[request setValue:@"chunked" forHTTPHeaderField:@"X-Transfer-Encoding"];
    
	self.sendDelayed = NO;
    
	@synchronized(self)
	{
		self.connection = nil;
		_connection = [[NSURLConnection alloc] initWithRequest:request delegate:self startImmediately:YES];
	}
	NSAssert(self.connection != nil, @"Failed to create connection.");
    
	NSLog(@"Did start send.");
}

- (BOOL)isOpen {
	return self.connection != nil;
}

#define ADD_STRING(B, S) [B appendData:[S dataUsingEncoding:NSUTF8StringEncoding]];

- (void)sendString:(NSString *)string {
	NSLog(@"METHOD: %s", __func__);
    
	if (string == nil) {
		string = [NSString string];
	}
    
	NSData *xmlData = [string dataUsingEncoding:NSUTF8StringEncoding];
    
	if (self.needEncryption) {
		// Encryption.
		std::vector <unsigned char> temp([xmlData length]);
		[xmlData getBytes:&temp[0] length:[xmlData length]];
		Code code;
		code.encode(&temp[0], temp.size());
		xmlData = [NSData dataWithBytes:&temp[0] length:temp.size()];
	}
    
	NSMutableData *buffer = [NSMutableData dataWithLength:0];
	NSMutableData *body = [NSMutableData dataWithLength:0];
    
	if (_first) {
		ADD_STRING(buffer, ([NSString stringWithFormat:@"--%@\r\n", self.multipartBoundary]));
		_first = NO;
	}
    
	if (self.needEncryption) {
		ADD_STRING(buffer, @"Content-Type: application/octet-stream\r\n");
		ADD_STRING(buffer, @"Content-Transfer-Encoding: binary\r\n");
		ADD_STRING(buffer, @"X-Content-Encryption-Type: enctype02\r\n");
	}
	else {
		ADD_STRING(buffer, @"Content-Type: text/xml;charset=utf-8\r\n");
		ADD_STRING(buffer, @"Content-Transfer-Encoding: 8bit\r\n");
	}
    
	ADD_STRING(buffer, ([NSString stringWithFormat:@"Content-Length: %d\r\n", [xmlData length]]));
    
	if ([xmlData length] > 0) {
		ADD_STRING(buffer, @"\r\n");
	}
	[buffer appendData:xmlData];
    
	ADD_STRING(buffer, ([NSString stringWithFormat:@"\r\n--%@\r\n", self.multipartBoundary]));
	[body appendData:buffer];
    
	[self.chunkStack addObject:body];
    
	NSLog(@"SENT:\n%@", [CDataUtils stringWithData:body]);
    
	if (self.sendDelayed) {
		// Send right now.
		[self stream:self.producerStream handleEvent:NSStreamEventHasSpaceAvailable];
	}
}

- (void)sendData:(NSData *)data {
	NSLog(@"METHOD: %s", __func__);
    
	if (self.needEncryption) {
		// Encryption.
		std::vector <unsigned char> temp([data length]);
		[data getBytes:&temp[0] length:[data length]];
		Code code;
		code.encode(&temp[0], temp.size());
		data = [NSData dataWithBytes:&temp[0] length:temp.size()];
	}
    
	NSMutableData *buffer = [NSMutableData dataWithLength:0];
	NSMutableData *body = [NSMutableData dataWithLength:0];
    
	if (_first) {
		ADD_STRING(buffer, ([NSString stringWithFormat:@"--%@\r\n", self.multipartBoundary]));
		_first = NO;
	}
    
	ADD_STRING(buffer, @"Content-Type: application/octet-stream\r\n");
	ADD_STRING(buffer, @"Content-Transfer-Encoding: binary\r\n");
	if (self.needEncryption) {
		ADD_STRING(buffer, @"X-Content-Encryption-Type: enctype02\r\n");
	}
	else {
		//Nothing to do.
	}
    
	ADD_STRING(buffer, ([NSString stringWithFormat:@"Content-Length: %d\r\n", [data length]]));
	if ([data length] > 0) {
		ADD_STRING(buffer, @"\r\n");
	}
	[buffer appendData:data];
    
	ADD_STRING(buffer, ([NSString stringWithFormat:@"\r\n--%@\r\n", self.multipartBoundary]));
	[body appendData:buffer];
    
	[self.chunkStack addObject:body];
    
	NSLog(@"SENT:\n%@", [CDataUtils stringWithData:body]);
    
	if (self.sendDelayed) {
		// Send right now.
		[self stream:self.producerStream handleEvent:NSStreamEventHasSpaceAvailable];
	}
}

- (void)sendTail {
	NSLog(@"METHOD: %s", __func__);
    
	NSMutableData *buffer = [NSMutableData dataWithLength:0];
    
	if (_first) {
		ADD_STRING(buffer, ([NSString stringWithFormat:@"--%@\r\n", self.multipartBoundary]));
		_first = NO;
	}
	ADD_STRING(buffer, @"Content-Type: application/octet-stream\r\n");
	ADD_STRING(buffer, @"Content-Transfer-Encoding: binary\r\n");
	if (self.needEncryption) {
		ADD_STRING(buffer, @"X-Content-Encryption-Type: enctype02\r\n");
	}
	else {
		//Nothing to do.
	}
	ADD_STRING(buffer, @"Content-Length: 0\r\n");
	ADD_STRING(buffer, ([NSString stringWithFormat:@"\r\n--%@--\r\n", self.multipartBoundary]));
	ADD_STRING(buffer, @"\r\n");
    
	[self.chunkStack addObject:buffer];
    
	NSLog(@"SENT:\n%@", [CDataUtils stringWithData:buffer]);
    
	[self.chunkStack addObject:[NSData data]];
    
	if (self.sendDelayed) {
		// Send right now.
		[self stream:self.producerStream handleEvent:NSStreamEventHasSpaceAvailable];
	}
}

- (void)cancelConnection {
	NSLog(@"METHOD: %s", __func__);
    
	[self stopSendWithStatus:@"canceled"];
}

- (void)closeConnection {
	NSLog(@"METHOD: %s", __func__);
    
	[self stopSendWithStatus:nil];
}

// For conventional.

- (NSString *)getRecvCookie {
	NSLog(@"METHOD: %s", __func__);
    
	return _cookie;
}

- (void)setSendCookie:(NSString *)cookie {
	NSLog(@"METHOD: %s", __func__);
    
	self.cookie = cookie;
}

- (NSInteger)getStatusCode {
	NSLog(@"METHOD: %s", __func__);
    
	return _statusCode;
}

- (void)setTimeout:(NSUInteger)timeoutMilliSeconds {
	NSLog(@"METHOD: %s", __func__);
    
	_timeoutSeconds = timeoutMilliSeconds / 1000.0;
}

//-----------------------------------------------------------------------------------
// Private Methods.

- (void)stopSendWithStatus:(NSString *)statusString {
	NSLog(@"METHOD: %s", __func__);
    
	if (self.connection != nil) {
		[self.connection cancel];
		self.connection = nil;
	}
    
	if (self.producerStream != nil) {
		self.producerStream.delegate = nil;
		[self.producerStream removeFromRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
		[self.producerStream close];
		self.producerStream = nil;
	}
	self.consumerStream = nil;
    
	NSLog(@"Did stop send: %@", statusString != nil ? statusString : @"Succeeded");
}

- (void)prepareRequestStreams {
	NSLog(@"METHOD: %s", __func__);
    
	NSAssert(self.consumerStream == nil, @"consumer must be nil.");
	NSAssert(self.producerStream == nil, @"producer must be nil.");
    
	NSInputStream *consStream;
	NSOutputStream *prodStream;
    
	// Open producer/consumer streams.  We open the producerStream straight
	// away.  We leave the consumerStream alone; NSURLConnection will deal
	// with it.
    
	[CChunkedMultipartConnectionImpl createBoundInputStream:&consStream
	                                           outputStream:&prodStream
	                                             bufferSize:kStreamBufferSize];
	NSAssert(consStream != nil, @"Failed to create consumer stream.");
	NSAssert(prodStream != nil, @"Failed to create producer stream.");
    
	self.consumerStream = consStream;
	self.producerStream = prodStream;
    
	self.producerStream.delegate = self;
	[self.producerStream scheduleInRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
	[self.producerStream open];
}

+ (void)createBoundInputStream:(NSInputStream **)inputStreamPtr outputStream:(NSOutputStream **)outputStreamPtr bufferSize:(NSUInteger)bufferSize {
	NSLog(@"METHOD: %s", __func__);
    
	CFReadStreamRef readStream;
	CFWriteStreamRef writeStream;
    
	assert((inputStreamPtr != NULL) || (outputStreamPtr != NULL));
    
	readStream = NULL;
	writeStream = NULL;
    
#if defined(__MAC_OS_X_VERSION_MIN_REQUIRED) && (__MAC_OS_X_VERSION_MIN_REQUIRED < 1070)
#error If you support Mac OS X prior to 10.7, you must re-enable CFStreamCreateBoundPairCompat.
#endif
#if defined(__IPHONE_OS_VERSION_MIN_REQUIRED) && (__IPHONE_OS_VERSION_MIN_REQUIRED < 50000)
#error If you support iOS prior to 5.0, you must re-enable CFStreamCreateBoundPairCompat.
#endif
    
	if (NO) {
		CFStreamCreateBoundPairCompat(
                                      NULL,
                                      ((inputStreamPtr  != nil) ? &readStream : NULL),
                                      ((outputStreamPtr != nil) ? &writeStream : NULL),
                                      (CFIndex)bufferSize
                                      );
	}
	else {
		CFStreamCreateBoundPair(
                                NULL,
                                ((inputStreamPtr  != nil) ? &readStream : NULL),
                                ((outputStreamPtr != nil) ? &writeStream : NULL),
                                (CFIndex)bufferSize
                                );
	}
    
	if (inputStreamPtr != NULL) {
		*inputStreamPtr  = CFBridgingRelease(readStream);
	}
	if (outputStreamPtr != NULL) {
		*outputStreamPtr = CFBridgingRelease(writeStream);
	}
}

//----------------------------------------------------------------------------------------------------
// NSStreamDelegate implementation.

- (void)stream:(NSStream *)aStream handleEvent:(NSStreamEvent)eventCode
// An NSStream delegate callback that's called when events happen on our
// network stream.
{
	NSLog(@"METHOD: %s", __func__);
    
#pragma unused(aStream)
	NSAssert(aStream == self.producerStream, @"Must be producder.");
    
	switch (eventCode) {
		case NSStreamEventOpenCompleted:
		{
			NSLog(@"eventCode: NSStreamEventOpenCompleted");
			NSLog(@"producer stream opened");
		}
            break;
            
		case NSStreamEventHasBytesAvailable:
		{
			NSLog(@"eventCode: NSStreamEventHasBytesAvailable");
			assert(NO);
			// should never happen for the output stream
		}
            break;
            
		case NSStreamEventHasSpaceAvailable:
		{
			// Check to see if we've run off the end of our buffer.  If we have,
			// work out the next buffer of data to send.
            
			// If we've failed to produce any more data, we close the stream
			// to indicate to NSURLConnection that we're all done.  We only do
			// this if producerStream is still valid to avoid running it in the
			// file read error case.
			NSLog(@"eventCode: NSStreamEventHasSpaceAvailable");
            
			if (self.producerStream != nil) {
				// WRITE!!
				if ([self.chunkStack count] > 0) {
					NSData *data = [self.chunkStack objectAtIndex:0];
					NSUInteger len = [data length];
                    
					NSLog(@"DATA LENGTH: %d", len);
                    
					if (len > 0) {
						const uint8_t *buffer = (const uint8_t *)[data bytes];
						NSInteger bytesWritten = [self.producerStream write:&buffer[0] maxLength:len];
                        
						NSLog(@"SEND: %d", bytesWritten);
                        
						if (bytesWritten <= 0) {
							[self stopSendWithStatus:@"Network write error"];
						}
                        
						if (((NSInteger)len - (NSInteger)bytesWritten) > 0) {
							data = [data subdataWithRange:NSMakeRange(bytesWritten, len - bytesWritten)];
							[self.chunkStack replaceObjectAtIndex:0 withObject:data];
                            
							NSLog(@"DATA REST");
						}
						else {
							[self.chunkStack removeObjectAtIndex:0];
                            
							NSLog(@"DATA REMOVED");
						}
					}
					else {
						// Empty data is the last data mark.
						// Cleans up at the end of stream data.
						[self.producerStream close];
						self.producerStream = nil;
                        
						NSLog(@"Remaining data count: %d", [self.chunkStack count]);
					}
					self.sendDelayed = NO;
				}
				else {
					NSLog(@"DATA DELAYED");
                    
					self.sendDelayed = YES;
				}
			}
		}
            break;
            
		case NSStreamEventErrorOccurred:
		{
			NSLog(@"eventCode: NSStreamEventErrorOccurred");
			NSLog(@"producer stream error %@", [aStream streamError]);
			[self stopSendWithStatus:@"Stream open error"];
		}
            break;
            
		case NSStreamEventEndEncountered:
		{
			NSLog(@"eventCode: NSStreamEventEndEncountered");
			//assert(NO);
			// should never happen for the output stream
		}
            break;
            
		default:
		{
			NSLog(@"eventCode: default case.");
			//assert(NO);
		}
            break;
	}
}

//----------------------------------------------------------------------------------------------------
// NSURLConnection delegate implementation.

- (void)connection:(NSURLConnection *)theConnection didReceiveResponse:(NSURLResponse *)response
// A delegate method called by the NSURLConnection when the request/response
// exchange is complete.  We look at the response to check that the HTTP
// status code is 2xx.  If it isn't, we fail right now.
{
	NSLog(@"METHOD: %s", __func__);
    
#pragma unused(theConnection)
	NSAssert(theConnection == self.connection, @"connection is only one.");
    
	NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *)response;
	NSAssert([httpResponse isKindOfClass:[NSHTTPURLResponse class]], @"response must be kind of NSHTTPURLResponse.");
    
	self.statusCode = httpResponse.statusCode;
    
	NSLog(@"STATUS CODE: %d", self.statusCode);
    
	//if ((httpResponse.statusCode / 100) != 2) {
	//    [self stopSendWithStatus:[NSString stringWithFormat:@"HTTP error %zd", (ssize_t) httpResponse.statusCode]];
	//}
    
	if ([self.delegate respondsToSelector:@selector(connection:didReceiveResponse:)]) {
		[self.delegate connection:theConnection didReceiveResponse:response];
	}
}

- (void)connection:(NSURLConnection *)theConnection didReceiveData:(NSData *)data
// A delegate method called by the NSURLConnection as data arrives.  The
// response data for a POST is only for useful for debugging purposes,
// so we just drop it on the floor.
{
	NSLog(@"METHOD: %s", __func__);
    
#pragma unused(theConnection)
#pragma unused(data)
    
	NSAssert(theConnection == self.connection, @"connection is only one.");
    
	// do nothing
    
	if ([self.delegate respondsToSelector:@selector(connection:didReceiveData:)]) {
		[self.delegate connection:theConnection didReceiveData:data];
	}
}

- (void)connection:(NSURLConnection *)theConnection didFailWithError:(NSError *)error
// A delegate method called by the NSURLConnection if the connection fails.
// We shut down the connection and display the failure.  Production quality code
// would either display or log the actual error.
{
	NSLog(@"METHOD: %s", __func__);
    
#pragma unused(theConnection)
#pragma unused(error)
    
	NSAssert(theConnection == self.connection, @"connection is only one.");
    
	//[self stopSendWithStatus:@"Connection failed"];
	if ((self.statusCode / 100) != 2) {
		[self stopSendWithStatus:[NSString stringWithFormat:@"HTTP error %zd", (ssize_t)self.statusCode]];
	}
	else {
		[self stopSendWithStatus:nil];
	}
	NSLog(@"error: %@", error);
    
	if ([self.delegate respondsToSelector:@selector(connection:didFailWithError:)]) {
		[self.delegate connection:theConnection didFailWithError:error];
	}
}

- (void)connectionDidFinishLoading:(NSURLConnection *)theConnection
// A delegate method called by the NSURLConnection when the connection has been
// done successfully.  We shut down the connection with a nil status, which
// causes the image to be displayed.
{
	NSLog(@"METHOD: %s", __func__);
    
#pragma unused(theConnection)
    
	NSAssert(theConnection == self.connection, @"connection is only one.");
    
	if ((self.statusCode / 100) != 2) {
		[self stopSendWithStatus:[NSString stringWithFormat:@"HTTP error %zd", (ssize_t)self.statusCode]];
	}
	else {
		[self stopSendWithStatus:nil];
	}
    
	if ([self.delegate respondsToSelector:@selector(connectionDidFinishLoading:)]) {
		[self.delegate connectionDidFinishLoading:theConnection];
	}
}

- (void)           connection:(NSURLConnection *)theConnection
              didSendBodyData:(NSInteger)bytesWritten
            totalBytesWritten:(NSInteger)totalBytesWritten
    totalBytesExpectedToWirte:(NSInteger)totalBytesExpectedToWrite {
	NSLog(@"METHOD: %s", __func__);
    
	if ([self.delegate respondsToSelector:@selector(connection:didSendBodyData:totalBytesWritten:totalBytesExpectedToWirte:)]) {
		[self.delegate connectionDidFinishLoading:theConnection];
	}
}

- (void)forwardInvocation:(NSInvocation *)anInvocation {
	NSLog(@"METHOD: %s", __func__);
    
	if ([self.delegate respondsToSelector:[anInvocation selector]]) {
		[anInvocation invokeWithTarget:self.delegate];
	}
	else {
		[super forwardInvocation:anInvocation];
	}
}

- (NSMethodSignature *)methodSignatureForSelector:(SEL)aSelector {
	NSLog(@"METHOD: %s", __func__);
    
	NSMethodSignature *signature = [super methodSignatureForSelector:aSelector];
    
	if (signature == nil) {
		signature = [self.delegate methodSignatureForSelector:aSelector];
	}
    
	return signature;
}

@end

//=======================================================================================
// API class implementation.

// Private category.
@interface CChunkedMultipartConnection ()

@property (nonatomic, strong) NSThread *executionThread;
@property (nonatomic, strong) CChunkedMultipartConnectionImpl *connection;

@end

@implementation CChunkedMultipartConnection

@synthesize executionThread = _executionThread;
@synthesize connection = _connection;

/**
 * @brief Initialization method
 * @param delegate Delegate object
 * @param encryption Encryption flag
 * @return self
 */
- (id)initWithDelegate:(id <NSURLConnectionDataDelegate> )delegate needEncryption:(BOOL)encryption {
	NSLog(@"METHOD: %s", __func__);
    
	if (self = [super init]) {
		_connection = [[CChunkedMultipartConnectionImpl alloc] initWithDelegate:delegate needEncryption:encryption];
        
		_executionThread = [[NSThread alloc] initWithTarget:self selector:@selector(executionRoutine) object:nil];
		[_executionThread start];
        
		if (_connection == nil || _executionThread == nil) {
			//[self release]; //#MCML
			[self setExitNow];     //#MCML
			return nil;
		}
	}
	return self;
}

- (void)executionRoutine {
	NSLog(@"METHOD: %s", __func__);
    
	@autoreleasepool {
		BOOL exitNow = NO;
        
		NSRunLoop *runLoop = [NSRunLoop currentRunLoop];
		[runLoop addPort:[NSMachPort port] forMode:NSDefaultRunLoopMode];
        
		NSMutableDictionary *threadDict = [[NSThread currentThread] threadDictionary];
		[threadDict setValue:[NSNumber numberWithBool:exitNow] forKey:exitKey];
        
		while (!exitNow) {
			@autoreleasepool {
				@try {
					if (![runLoop runMode:NSDefaultRunLoopMode beforeDate:[NSDate dateWithTimeIntervalSinceNow:0.5]]) {
						break;
					}
				}
				@catch (NSException *exception)
				{
					NSLog(@"%@", [exception description]);
				}
				//NSLog(@"THREAD LOOP: %@", [NSThread currentThread]);
				exitNow = [[threadDict objectForKey:exitKey] boolValue];
			}
		}
		NSLog(@"My thread ended.");
	}
}

//#MCML Start
/*
 - (void)release //#MCML
 {
 NSLog(@"METHOD: %s", __func__);
 
 [self setExitNow];
 //[super release]; //#MCML
 }
 */
//#MCML End

/**
 * @brief  Instance release method
 */
- (void)dealloc {
	NSLog(@"METHOD: %s", __func__);
    
	//[self setExitNow];
	//[_executionThread release]; //#MCML
	//[_connection release]; //#MCML
    
	//[super dealloc]; //#MCML
	NSLog(@"dealloc finished.");
}

/**
 * @brief URL connection method
 * @param urlString  Target URL
 */
- (void)openConnectionWithURL:(NSString *)urlString {
	NSLog(@"METHOD: %s", __func__);
    
	[self.connection performSelector:@selector(openConnectionWithURL:) onThread:(self.executionThread) withObject:urlString waitUntilDone:NO];
}

/**
 * @brief isOpen
 * @return True if self.connection != nil
 */
- (BOOL)isOpen {
	NSLog(@"METHOD: %s", __func__);
    
	return [self.connection isOpen];
}

/**
 * @brief  Character string transmission
 * @param string  Target character string
 */
- (void)sendString:(NSString *)string;
{
	NSLog(@"METHOD: %s", __func__);
    
	[self.connection performSelector:@selector(sendString:) onThread:(self.executionThread) withObject:string waitUntilDone:NO];
}

/**
 * @brief Data transmission
 * @param data  Target data
 */
- (void)sendData:(NSData *)data {
	NSLog(@"METHOD: %s", __func__);
    
	[self.connection performSelector:@selector(sendData:) onThread:(self.executionThread) withObject:data waitUntilDone:NO];
}

/**
 * @brief Termination data transmission
 */
- (void)sendTail {
	NSLog(@"METHOD: %s", __func__);
    
	[self.connection performSelector:@selector(sendTail) onThread:(self.executionThread) withObject:nil waitUntilDone:NO];
}

/**
 * @brief  Connection cancel
 */
- (void)cancelConnection {
	NSLog(@"METHOD: %s", __func__);
    
	[self.connection performSelector:@selector(cancelConnection) onThread:(self.executionThread) withObject:nil waitUntilDone:NO];
}

/**
 * @brief  Connection close
 */
- (void)closeConnection {
	NSLog(@"METHOD: %s", __func__);
    
	[self.connection performSelector:@selector(closeConnection) onThread:(self.executionThread) withObject:nil waitUntilDone:NO];
}

/**
 * @brief Gets Cookie
 * @return cookie
 */
- (NSString *)getRecvCookie {
	NSLog(@"METHOD: %s", __func__);
	return [self.connection getRecvCookie];
}

/**
 * @brief Sets Cookie
 * @param cookie
 */
- (void)setSendCookie:(NSString *)cookie;
{
	NSLog(@"METHOD: %s", __func__);
	[self.connection setSendCookie:cookie];
}

/**
 * @brief  Gets status code
 * @return Status code
 */
- (NSInteger)getStatusCode;
{
	NSLog(@"METHOD: %s", __func__);
	return [self.connection getStatusCode];
}

/**
 * @brief  Sets timeout value
 * @param timeoutMilliSeconds Request timeout value (millisecond)
 */
- (void)setTimeout:(NSUInteger)timeoutMilliSeconds;
{
	NSLog(@"METHOD: %s", __func__);
	NSLog(@"CChunkedMultipartConnection::setTimeout: %u", timeoutMilliSeconds);
    
	[self.connection setTimeout:timeoutMilliSeconds];
}

/**
 * @brief setExitNow
 */
- (void)setExitNow {
	NSLog(@"METHOD: %s", __func__);
	if (self.executionThread != nil) {
		BOOL exitNow = YES;
		NSMutableDictionary *threadDict = [self.executionThread threadDictionary];
		NSNumber *object = [threadDict objectForKey:exitKey];
        
		if (object) {
			[threadDict setObject:[NSNumber numberWithBool:exitNow] forKey:exitKey];
		}
		while (self.executionThread.isExecuting) {
			[NSThread sleepForTimeInterval:0.1];
		}
		self.executionThread = nil;
		NSLog(@"Connection thread finished.");
	}
	else {
		NSLog(@"No connection thread.");
	}
}

@end
