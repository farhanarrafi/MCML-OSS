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
 * @file SSController.mm
 * @brief Voice synthesis control class implementation
 */

#import "SSController.h"
#import "Define.h"
#import "XMLProcessor.h"
#import "MCMLSampleApplicationAppDelegate.h"

@implementation SSController

@synthesize mAccessURL;                     //!< @brief Communication URL

/**
 * @brief Initialization method
 * @param delegate Delegate object
 * @param requestQueue Request queue
 * @param comCtrl Communication controller
 * @param aURL Communication URL
 * @return self
 */
- (id)initWithParameters:(id <SSControllerDelegate> )delegate
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
		// Initialize CSignalAdpcm
		mSignalAdpcm = new CSignalAdpcm(false);
		mSignalAdpcm->InitializeEncode();
		// Communication URL
		mAccessURL = [aURL retain];
	}
    
	// Normal end
	return self;
}

/**
 * @brief Parameter setter method
 * @param language Speaker's language type
 * @param utteranceId Speech ID
 */
- (void)setParameters:(std::string)language
          utteranceId:(int)utteranceId {
	// Stores language type
	mLanguage = language;
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
	// Sets thread stop flag
	mStopFlag = true;
    
	// Normal end
	return;
}

/**
 * @brief  Thread main loop
 */
- (void)threadLoop {
	NSAutoreleasePool *pool = [[NSAutoreleasePool alloc] init];
    
	// Loop until thread stop  flag is set
	while (!mStopFlag) {
		// Gets request from queue
		NSString *inputString = (NSString *)[mRequestQueue dequeue];
		if (inputString == nil) {
			// No request
			[NSThread sleepForTimeInterval:0.01];
			continue;
		}
        
		// Executes voice synthesis processing
		NSData *audioData = [self doSppehSynthesis:inputString];
        
		// Determines whether method is set to delegate object
		if ([mDelegate respondsToSelector:@selector(onOutputSSResult:)]) {
			// Executes delegate method and notifies voice synthesis results (synthesized sounds)
			[(NSObject *)mDelegate performSelectorOnMainThread : @selector(onOutputSSResult:) withObject : audioData waitUntilDone : YES];
		}
	}
    
	[pool release];
    
	// Normal end
	return;
}

/**
 * @brief Voice synthesis execution method
 * @param inputString  Input character string
 * @return Voice synthesis results
 */
- (NSData *)doSppehSynthesis:(NSString *)inputString {
	mComCtrl->setTransferEncodingChunked(false);
    
	// XML data generation and log output
	std::string xmlData = [self generateXMLData:inputString];
	NSString *logString = [NSString stringWithUTF8String:xmlData.c_str()];
	NSLog(@"SS_IN: %@", logString);
    
	// XML data transmission and response reception
	mcml::CResponseData responseData;
	std::string url = [self.mAccessURL UTF8String];
    
	int result = mComCtrl->request(url, xmlData, responseData);
	if (result != 0) {
		// Failed
		NSLog(@"ERROR: requestBinary(): %d", result);
		return nil;
	}
    
	// Outputs logs of XML data acquisition from response
	responseData.getXML(xmlData);
	logString = [NSString stringWithUTF8String:xmlData.c_str()];
	NSLog(@"SS_OUT: %@", logString);
    
	NSData *audioData = nil;
    
	// Response XML data processing
	if ([self processResponseData:xmlData]) {
		// Gets binary data (linear data synthesized sound) from response
		std::string adpcmData;
		responseData.getBinary(adpcmData);
		if (!adpcmData.empty()) {
			// Convert ADPCM to PCM
			std::string binaryData;
			if (!mSignalAdpcm->Decode(adpcmData, binaryData, true)) {
				// 失敗
				NSLog(@"ERROR: Decode()");
				return nil;
			}
            
			// Convert PCM to NSData
			if ([MCML_PLAYBACK_AUDIO isEqualToString:@"ADPCM"])
				audioData = [NSData dataWithBytes:binaryData.data() length:binaryData.size()];
			else
				audioData = [NSData dataWithBytes:adpcmData.data() length:adpcmData.size()];
		}
	}
    
	// Normal end
	return audioData;
}

/**
 * @brief XML data generation method
 * @param inputString  Input character string
 * @return XML data
 */
- (std::string)generateXMLData:(NSString *)inputString {
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
        
		/****SETUP THE SERVER AND REQUEST ****/
        
		mcml::CServerType server;
		mcml::CRequestType request;
        
		request.AddService([MCML_SYNTHESIS_SERVICE UTF8String]);
		request.AddProcessOrder(1);
        
		{
			//Request Input
			{
				mcml::CInputUserProfileType inputUserProfile;
				mcml::CInputModalityType inputModality;
				mcml::CSpeakingType speaking;
				mcml::CLanguageType language;
				std::string inName = [MCML_SEND_ID UTF8String];
				std::string inLang = mLanguage;
                
				inputUserProfile.AddID(inName);
				inputUserProfile.AddGender([MCML_GENDER UTF8String]);
				inputUserProfile.AddAge([MCML_AGE UTF8String]);
                
				language.AddID(inLang);
				language.AddFluency("0");
                
				speaking.AddLanguage(language);
				inputModality.AddSpeaking(speaking);
				inputUserProfile.AddInputModality(inputModality);
                
				request.AddInputUserProfile(inputUserProfile);
			}
            
			//Request Output
			{
				mcml::CTargetOutputType targetOutput;
				mcml::CHypothesisFormatType hypothesisFormat;
				mcml::CLanguageTypeType languageType;
				std::string outLang = mLanguage;
                
				languageType.AddID(outLang);
				targetOutput.AddLanguageType(languageType);
                
				request.AddTargetOutput(targetOutput);
			}
            
			/****SETUP THE DATA ****/
            
			mcml::CInputType input;
			mcml::CDataType data;
			mcml::CTextType text;
            
			text.AddChannelID("1");
			//  ModelType
			{
				mcml::CModelTypeType modelType;
				mcml::CLanguageType language;
				mcml::CPersonalityType personality;
                
				language.AddID(mLanguage);
				modelType.AddLanguage(language);
				modelType.AddDomain("Travel");
				modelType.AddTask("Dictation");
				personality.AddID([MCML_SEND_ID UTF8String]);
				modelType.AddPersonality(personality);
                
				text.AddModelType(modelType);
			}
            
            
			NSMutableArray *inputTextArray = [NSMutableArray arrayWithObject:inputString];
			NSMutableArray *inputDelimiterArray = [NSMutableArray arrayWithObject:@""];
            
            
			//  SentenceSequence
			{
				mcml::CSentenceSequenceType sentenceSequence;
				int sentences = [inputTextArray count];
                
				sentenceSequence.AddOrder("1");
				sentenceSequence.AddScore("0");
				sentenceSequence.AddN_bestRank("1");
                
				for (int i = 0; i < sentences; ++i) {
					mcml::CSentenceType sentence;
					mcml::CSurfaceType2 surface;
					char buf[32];
					std::string inputTextStr = [[inputTextArray objectAtIndex:i] cStringUsingEncoding:NSUTF8StringEncoding];
					std::string inputDelimStr = [[inputDelimiterArray objectAtIndex:i] cStringUsingEncoding:NSUTF8StringEncoding];
                    
					snprintf(buf, sizeof(buf), "%d", i + 1);    //  1 origin
					sentence.AddOrder(buf);
					sentence.AddFunction("text");
                    
					// Fixed value in S version
					surface.AddDelimiter("|");
                    
					surface.SetValue(inputTextStr);
					sentence.AddSurface(surface);
                    
					int chunkOrder = 1;
					std::string::size_type first = 0;
					std::string::size_type second = 0;
					std::string chunkText;
					if (inputDelimStr.empty()) {
						mcml::CChunkType chunk;
						mcml::CSurfaceType chunkSurface;
                        
						chunk.AddOrder("1");
						chunkSurface.SetValue(inputTextStr);
						chunk.AddSurface(chunkSurface);
						sentence.AddChunk(chunk);
					}
					else {
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
					}
                    
					sentenceSequence.AddSentence(sentence);
				}
                
				text.AddSentenceSequence(sentenceSequence);
			}
            
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
 * @return Normal end true
 */
- (bool)processResponseData:(std::string)xmlData {
	// essentially, the XML should be parsed to check for an error
	// with a method like ".HasError()" for example. But as long as we
	// get the audio we dont care about the XML...for now.
	return true;
}

/**
 * @brief  Instance release method
 */
- (void)dealloc {
	// CSignalAdpcm release
	delete mSignalAdpcm;
	[mAccessURL release];
	[super dealloc];
    
	// Normal end
	return;
}

@end
