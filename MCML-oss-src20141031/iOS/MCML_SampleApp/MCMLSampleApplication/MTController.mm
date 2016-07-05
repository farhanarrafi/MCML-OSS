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
 * @file MTController.mm
 * @brief Translation control class implementation
 */

#import "MTController.h"
#import "Define.h"
#import "XMLProcessor.h"
#import "MCMLSampleApplicationAppDelegate.h"

@implementation MTController

@synthesize mAccessURL;             //!< @brief Communication URL

/**
 * @brief Initialization method
 * @param delegate Delegate object
 * @param requestQueue Request queue
 * @param comCtrl Communication controller
 * @param aURL Communication URL
 * @return self
 */
- (id)initWithParameters:(id <MTControllerDelegate> )delegate
                 request:(NSMutableArray *)requestQueue
           clientComCtrl:(mcml::CClientComCtrl *)comCtrl
                     url:(NSString *)aURL {
	self = [super init];
	if (self != nil) {
		// Stores delegate object
		mDelegate = delegate;
		// Stores request queue
		mRequestQueue = requestQueue;
		// Clears thread stop flag
		mStopFlag = false;
		// Stores communication controller
		mComCtrl = comCtrl;
		// Communication URL
		mAccessURL = [aURL retain];
	}
    
	// Normal end
	return self;
}

/**
 * @brief Parameter setter method
 * @param myLanguage Speaker's language type
 * @param otherLanguage Listener's language type
 * @param utteranceId Speech ID
 */
- (void)setParameters:(std::string)myLanguage
        otherLanguage:(std::string)otherLanguage
          utteranceId:(int)utteranceId {
	// Stores speaker's language type
	mMyLanguage = myLanguage;
	// Stores listener's language type
	mOtherLanguage = otherLanguage;
	// Stores speech ID
	mUtteranceId = utteranceId;
    
	// Normal end
	return;
}

/**
 * @brief  Thread start method
 */
- (void)runThread {
	//  Starts thread and executes main loop
	[NSThread detachNewThreadSelector:@selector(threadLoop) toTarget:self withObject:self];
    
	// Normal end
	return;
}

/**
 * @brief  Thread stop method
 */
- (void)stopThread {
	mStopFlag = true;
}

/**
 * @brief  Thread main loop
 */
- (void)threadLoop {
	NSAutoreleasePool *pool = [[NSAutoreleasePool alloc] init];
    
	// Loop until thread stop flag is set
	while (!mStopFlag) {
		// Gets request from queue
		NSString *inputString = (NSString *)[mRequestQueue dequeue];
		if (inputString == nil) {
			//No request
			[NSThread sleepForTimeInterval:0.01];
			continue;
		}
        
		// Executes translation processing
		NSString *resultString = [self doMachineTranslation:inputString];
        
		// Determines if method is set to delegate object
		if ([mDelegate respondsToSelector:@selector(onOutputMTResult:)]) {
			// Executes delegate method and notifies translation results
			[(NSObject *)mDelegate performSelectorOnMainThread : @selector(onOutputMTResult:) withObject : resultString waitUntilDone : YES];
		}
        
		// Determines if translation results are null
		if (resultString != nil && resultString.length != 0) {
			// Executes reverse translation processing
			resultString = [self doReverseTranslation:resultString];
            
			// Determines if method is set to delegate object
			if ([mDelegate respondsToSelector:@selector(onOutputRTResult:)]) {
				// Executes delegate method and notifies reverse translation results
				[(NSObject *)mDelegate performSelectorOnMainThread : @selector(onOutputRTResult:) withObject : resultString waitUntilDone : YES];
			}
		}
	}
    
	[pool release];
    
	// Normal end
	return;
}

/**
 * @brief Translation processing execution method
 * @param inputString  Input character string
 * @return Translation results
 */
- (NSString *)doMachineTranslation:(NSString *)inputString {
	NSLog(@"Setting the chunked part!");
	mComCtrl->setTransferEncodingChunked(false);
    
	// XML data generation and log output
	std::string xmlData = [self generateXMLData:mMyLanguage targetLanguage:mOtherLanguage inputString:inputString requestPrefix:nil];
	NSString *logString = [NSString stringWithUTF8String:xmlData.c_str()];
	NSLog(@"MT_IN: %@", logString);
    
	// XML data transmission and response reception
	mcml::CResponseData responseData;
    
	std::string url = [self.mAccessURL UTF8String];
    
	int result = mComCtrl->request(url, xmlData, responseData);
	if (result != 0) {
		// Failed
		NSLog(@"ERROR: requestBinary(): %d", result);
		return nil;
	}
    
	// Gets XML data from response and outputs logs
	responseData.getXML(xmlData);
	logString = [NSString stringWithUTF8String:xmlData.c_str()];
	NSLog(@"MT_OUT: %@", logString);
    
	// Response XML data processing
	NSString *resultString = [self processResponseData:xmlData];
    
	// Normal end
	return resultString;
}

/**
 * @brief Reverse translation processing execution method
 * @param inputString  Input character string
 * @return Reverse translation results
 */
- (NSString *)doReverseTranslation:(NSString *)inputString {
	NSLog(@"Setting the chunked part!");
	mComCtrl->setTransferEncodingChunked(false);
    
	// XML data generation and log output
	std::string xmlData = [self generateXMLData:mOtherLanguage targetLanguage:mMyLanguage inputString:inputString requestPrefix:nil];
	NSString *logString = [NSString stringWithUTF8String:xmlData.c_str()];
	NSLog(@"Reverse MT_IN: %@", logString);
    
	// XML data transmission and response reception
	mcml::CResponseData responseData;
    
    
	std::string url = [self.mAccessURL UTF8String];
    
	int result = mComCtrl->request(url, xmlData, responseData);
	if (result != 0) {
		// Failed
		NSLog(@"ERROR: requestBinary(): %d", result);
		return nil;
	}
    
	// Gets XML data from response and outputs logs
	responseData.getXML(xmlData);
	logString = [NSString stringWithUTF8String:xmlData.c_str()];
	NSLog(@"Reverse MT_OUT: %@", logString);
    
	// Response XML data processing
	NSString *resultString = [self processResponseData:xmlData];
    
	// Normal end
	return resultString;
}

/**
 * @brief XML data generation method
 * @param sourceLanguage Input language type
 * @param targetLanguage Output language type
 * @param inputString  Input character string
 * @param requestPrefix Request prefix
 * @return XML data
 */
- (std::string)generateXMLData:(std::string)sourceLanguage
                targetLanguage:(std::string)targetLanguage
                   inputString:(NSString *)inputString
                 requestPrefix:(NSString *)requestPrefix {
	MCMLSampleApplicationAppDelegate *appDelegate = (MCMLSampleApplicationAppDelegate *)[[UIApplication sharedApplication] delegate];
	@synchronized(appDelegate.mcmlLock)
	{
		mcml::CUserType user;
        
		/****SETUP THE TRANSMITTER****/
        
		{
			mcml::CTransmitterType transmitter;
			mcml::CDeviceType device;
			mcml::CLocationType location;
			//add information to the location
			location.AddURI([MCML_DEVICE UTF8String]);
            
			//add information to the userProfile
			mcml::CUserProfileType userProfile;
			userProfile.AddID([MCML_SEND_ID UTF8String]);
			userProfile.AddGender([MCML_GENDER UTF8String]);
			userProfile.AddAge([MCML_AGE UTF8String]);
            
			//add the location to the device
			device.AddLocation(location);
            
			//add the userProfile to the transmitter
			transmitter.AddDevice(device);
			transmitter.AddUserProfile(userProfile);
            
			//add the transmitter to the userType
			user.AddTransmitter(transmitter);
		}
        
		/****SETUP THE RECEIVER****/
        
		{
			mcml::CReceiverType receiver;
			mcml::CDeviceType receiverDevice;
			mcml::CLocationType receiverLocation;
            
			//add information to the userProfile
			mcml::CUserProfileType receiverUserProfile;
			receiverUserProfile.AddID([MCML_RECEIVE_ID UTF8String]);
			receiverUserProfile.AddGender([MCML_GENDER UTF8String]);
			receiverUserProfile.AddAge([MCML_AGE UTF8String]);
            
			//add information to the location
			receiverLocation.AddURI([MCML_DEVICE UTF8String]);
            
			//add the location to the device
			receiverDevice.AddLocation(receiverLocation);
            
			receiver.AddDevice(receiverDevice);
			receiver.AddUserProfile(receiverUserProfile);
			user.AddReceiver(receiver);
		}
        
		/****SETUP THE SERVER AND REQUEST****/
        
		mcml::CServerType server;
		mcml::CRequestType request;
        
		request.AddService([MCML_TRANSLATION_SERVICE UTF8String]);
		request.AddProcessOrder(1);
        
		//Request Input
		{
			mcml::CInputUserProfileType inputUserProfile;
			inputUserProfile.AddID([MCML_SEND_ID UTF8String]);
			inputUserProfile.AddGender([MCML_GENDER UTF8String]);
			inputUserProfile.AddAge([MCML_AGE UTF8String]);
            
			//The modality
			mcml::CInputModalityType inputModality;
			mcml::CSpeakingType speakingType;
			mcml::CLanguageType language;
            
			language.AddID(sourceLanguage);
			language.AddFluency(5);
			speakingType.AddLanguage(language);
			inputModality.AddSpeaking(speakingType);
			inputUserProfile.AddInputModality(inputModality);
            
			request.AddInputUserProfile(inputUserProfile);
		}
        
		//Request Output
		{
			mcml::CTargetOutputType targetOutput;
			mcml::CHypothesisFormatType hypothesisFormat;
			mcml::CLanguageTypeType targetOutputLanguage;
            
			hypothesisFormat.AddNofN_best("1");
			targetOutputLanguage.AddID(targetLanguage);
			targetOutput.AddHypothesisFormat(hypothesisFormat);
			targetOutput.AddLanguageType(targetOutputLanguage);
            
			request.AddTargetOutput(targetOutput);
		}
        
		/****SETUP THE DATA ****/
        
		mcml::CInputType input;
		mcml::CDataType data;
		mcml::CTextType text;
        
		{
			mcml::CModelTypeType modelType;
			mcml::CLanguageType language;
			mcml::CPersonalityType personality;
			mcml::CSentenceSequenceType sentenceSequence;
            
			NSMutableArray *inputTextArray = [NSMutableArray arrayWithObject:inputString];
			NSMutableArray *inputDelimiterArray = [NSMutableArray arrayWithObject:@""];
			int sentences = [inputTextArray count];
			int delimiters = [inputDelimiterArray count];
            
            
			text.AddChannelID("1");
			language.AddID(sourceLanguage);
			modelType.AddLanguage(language);
			modelType.AddDomain("Travel");
			modelType.AddTask("Dictation");
			personality.AddID([MCML_SEND_ID UTF8String]);
			modelType.AddPersonality(personality);
			text.AddModelType(modelType);
            
			//  SentenceSequence
			sentenceSequence.AddN_bestRank("5");
            
			for (int i = 0; i < sentences; ++i) {
				mcml::CSentenceType sentence;
				mcml::CSurfaceType2 surface;
				char buf[32];
				std::string inputTextStr;
				std::string inputDelimStr;
                
				snprintf(buf, sizeof(buf), "%d", i + 1);    //  1 origin
				inputTextStr = [[inputTextArray objectAtIndex:i] cStringUsingEncoding:NSUTF8StringEncoding];
				if (i >= delimiters) {
					inputDelimStr = [[inputDelimiterArray lastObject] cStringUsingEncoding:NSUTF8StringEncoding];
				}
				else {
					inputDelimStr = [[inputDelimiterArray objectAtIndex:i] cStringUsingEncoding:NSUTF8StringEncoding];
				}
                
				sentence.AddOrder(buf);
				sentence.AddFunction("text");
                
				surface.AddDelimiter(inputDelimStr);
				surface.SetValue(inputTextStr);
				sentence.AddSurface(surface);
                
				int chunkOrder = 1;
				std::string::size_type first = 0;
				std::string::size_type second = 0;
				std::string chunkText;
				do {
					mcml::CChunkType chunk;
					mcml::CSurfaceType chunkSurface;
					char buf[32];
                    
					second = inputTextStr.find(inputDelimStr, first);
					if (second == std::string::npos) {
						chunkText = inputTextStr.substr(first);
						first = second;
					}
					else {
						chunkText = inputTextStr.substr(first, (second - first));
						first = second + inputDelimStr.length();
					}
                    
					sprintf(buf, "%d", chunkOrder);
					chunk.AddOrder(buf);
					chunkSurface.SetValue(chunkText);
					chunk.AddSurface(chunkSurface);
                    
					sentence.AddChunk(chunk);
                    
					chunkOrder++;
				}
				while (first != second);
                
				sentenceSequence.AddSentence(sentence);
			}
            
			text.AddSentenceSequence(sentenceSequence);
			data.AddText(text);
			input.AddData(data);
            
			request.AddInput(input);
		}
        
		//Add the request to the server
		server.AddRequest(request);
        
		//Put everything together
		mcml::CMCMLType mcml;
		mcml.AddVersion([MCML_VERSION UTF8String]);
		mcml.AddUser(user);
		mcml.AddServer(server);
        
		//Generate the XML
		mcml::CXMLProcessor xmlProcessor;
		std::string xmlString = &(xmlProcessor.Generate(mcml))[0];
        
		return xmlString;
	}
}

/**
 * @brief Response processing method
 * @param xmlData XML data
 * @return Response results
 */
- (NSString *)processResponseData:(std::string)xmlData {
	MCMLSampleApplicationAppDelegate *appDelegate = (MCMLSampleApplicationAppDelegate *)[[UIApplication sharedApplication] delegate];
	@synchronized(appDelegate.mcmlLock)
	{
		NSMutableArray *resultText = [NSMutableArray array];
        
		mcml::CXMLProcessor xmlProc;
		mcml::CMCMLType mcmlType = xmlProc.Parse(xmlData);
        
		if (!mcmlType.HasServer())
			return @"";
        
		mcml::CServerType server = mcmlType.GetServer();
		mcml::CResponseType response = server.GetResponse();
        
		if (!response.HasOutput())
			return @"";
        
		mcml::COutputType output = response.GetOutput();
		mcml::CDataType data = output.GetData();
		mcml::CTextType text = data.GetText();
		mcml::CSentenceSequenceType sentenceSequence = text.GetSentenceSequence();
		int sentences = sentenceSequence.GetSentenceCount();
        
		//  Removes multiple tests and treats as arrays
		for (int i = 0; i < sentences; ++i) {
			mcml::CSentenceType sentence = sentenceSequence.GetSentenceAt(i);
			mcml::CSurfaceType2 surface = sentence.GetSurface();
			std::string v = surface.GetValue();
			std::string d = "";
			if (surface.HasDelimiter()) {
				d = surface.GetDelimiter();
			}
			NSString *nsText = [NSString stringWithCString:v.c_str()
			                                      encoding:NSUTF8StringEncoding];
            
            
			[resultText addObject:nsText];
		}
        
		NSString *resultString = [resultText componentsJoinedByString:@""];
		return resultString;
	}
}

/**
 * @brief  Instance release method
 */
- (void)dealloc {
	[mAccessURL release];
	[super dealloc];
    
	// Normal end
	return;
}

@end
