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
 * @file SRController.mm
 * @brief Speech recognition control class implementation
 */

#import "SRController.h"
#import "Define.h"
#import "XMLProcessor.h"
#import "MCMLSampleApplicationAppDelegate.h"
#import <AudioToolbox/AudioToolbox.h>

@interface SRController ()

@property (nonatomic, retain) NSMutableData *recordedData;      //!< @brief  Recorded data

@end

#define CHUNKED_CONNECTION 1
#define SAVE_AUDIO_IN_DOCUMENTS 1

using namespace mcml;

@implementation SRController

@synthesize mAccessURL;         //!< @brief Communication URL
@synthesize recordedData;       //!< @brief  Recorded data

/**
 * @brief Initialization method
 * @param delegate Delegate object
 * @param requestQueue Request queue
 * @param comCtrl Communication controller
 * @param aURL Communication URL
 * @return self
 */
- (id)initWithParameters:(id <SRControllerDelegate> )delegate
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
		// Initilize CSignalAdpcm
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
 * @brief  Thread  start method
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
    
	// Initializes serial number for asynchronous divided transmission (Set 1 as origin)
	mRequestNumber = 1;
    
    
	// Loop until thread stop flag is set
	while (!mStopFlag) {
		// Gets request from queue
		NSData *audioData = (NSData *)[mRequestQueue dequeue];
		if (audioData == nil) {
			// No request
			[NSThread sleepForTimeInterval:0.01];
			continue;
		}
        
		// Determines whether termination data
		if (audioData.length != 0) {
			// Determines whether divided transmission head data
			if (mRequestNumber == 1) {
				// Sends XML data
				if (![self sendXMLData]) {
					// Executes delegate method and notifies speech recognition results (error)
					[self notifyResult:nil];
					// No processing carried out thereafter
					continue;
				}
			}
            
			// Wait until next data is accumulated in queue
			NSData *nextData = nil;
			while (nextData == nil) {
				nextData = (NSData *)[mRequestQueue peek];
				[NSThread sleepForTimeInterval:0.01];
			}
            
			// Determines whether next data is termination data
			bool isLastData = (nextData.length == 0);
            
			// Sends binary data (divided voice)
			if (![self sendBinaryData:audioData isLastData:isLastData]) {
				// Executes delegate method and notifies speech recognition results (error)
				[self notifyResult:nil];
			}
		}
		else {
			// Determines whether head data has been sent
			if (mRequestNumber > 1) {
				// Sends termination data
				NSString *resultString = [self sendTerminator];
				// Executes delegate method and notifies speech recognition results
				[self notifyResult:resultString];
			}
			else {
				// Executes delegate method and notifies speech recognition results (error)
				[self notifyResult:nil];
			}
		}
	}
    
	[pool release];
    
	// Normal end
	return;
}

/**
 * @brief XML data transmission method
 * @return True when successful. false when failed
 */
- (bool)sendXMLData {
	recordedData = [NSMutableData data];
    
	if (CHUNKED_CONNECTION) {
		mComCtrl->setTransferEncodingChunked(true);
	}
	else {
		mComCtrl->setTransferEncodingChunked(false);
	}
    
	// XML data generation and log output
	std::string xmlData = [self generateXMLData];
	NSString *logString = [NSString stringWithUTF8String:xmlData.c_str()];
	NSLog(@"SR_IN: %@", logString);
    
	// XML data transmission and response reception
	mcml::CResponseData responseData;
    
	//Assigns transmission numbers
	std::string url = [mAccessURL UTF8String];
	mRequestNumber++;
    
	int result = mComCtrl->request(url, xmlData, responseData);
	if (result != 0) {
		// Failed
		NSLog(@"ERROR: request(): %d", result);
		return false;
	}
    
	// Gets XML data from response and outputs logs
	responseData.getXML(xmlData);
	logString = [NSString stringWithUTF8String:xmlData.c_str()];
	NSLog(@"SR_OUT: %@", logString);
    
	// Response XML data processing
	if ([self processResponseData:xmlData] == nil) {
		return false;
	}
    
	// Normal end
	return true;
}

/**
 * @brief  Binary data transmission method
 * @param audioData Recording data
 * @param isLastData Data termination flag
 * @return True when successful. false when failed
 */
- (bool)sendBinaryData:(NSData *)audioData
            isLastData:(bool)isLastData {
	int remainder = [audioData length] % 4;
	NSRange range = NSMakeRange(0, [audioData length] - remainder);
    
	char speechData[[audioData length] - remainder];
	[audioData getBytes:speechData range:range];
    
	[self.recordedData appendBytes:speechData length:range.length];
    
	std::string binaryData = "";
	binaryData.append(speechData, range.length);
    
    
    
	// Convert binary datas to ADPCM.
	if ([MCML_RECORD_AUDIO isEqualToString:@"ADPCM"]) {
		std::string adpcmData;
		if (!mSignalAdpcm->Encode(binaryData, adpcmData, isLastData)) {
			// Failed
			NSLog(@"ERROR: Encode()");
			return false;
		}
        
		// Logging
		NSLog(@"SR IN: %lu bytes", adpcmData.size());
		binaryData = adpcmData;
	}
	// binary data transmission and response reception
	mcml::CResponseData responseData;
    
    
	// Assigns transmission numbers
	std::string url = [mAccessURL UTF8String];
	mRequestNumber++;
    
    
	int result = mComCtrl->requestBinary(url, binaryData, responseData);
	if (result != 0) {
		// Failed
		NSLog(@"ERROR: requestBinary(): %d", result);
		return false;
	}
    
	// Gets XML data from response and outputs logs
	std::string xmlData;
	responseData.getXML(xmlData);
	NSString *nsXmlData = [NSString stringWithUTF8String:xmlData.c_str()];
	NSLog(@"SR OUT: %@", nsXmlData);
    
	// Response XML data processing
	if ([self processResponseData:xmlData] == nil) {
		// Failed
		return false;
	}
    
	// Normal end
	return true;
}

/**
 * @brief Termination data transmission method
 * @return Response XML data processing results
 */
- (NSString *)sendTerminator {
	if (SAVE_AUDIO_IN_DOCUMENTS) {
		NSOperationQueue *backQueue = [[[NSOperationQueue alloc] init] autorelease];
        
		[backQueue addOperationWithBlock: ^{
		    NSLog(@"The total size of RECORDED data: %d", [self.recordedData length]);
            
            
		    //NSFileManager *fileManager = [NSFileManager defaultManager];
            
		    NSString *date = [NSString stringWithFormat:@"/Documents/%@.wav", [[NSDate date] description]];
		    NSString *filePath = [NSHomeDirectory() stringByAppendingPathComponent:date];
		    NSURL *fileURL = [NSURL fileURLWithPath:filePath];
            
            
		    //create an ASBD for the file
		    AudioStreamBasicDescription audioFormatDescription;
		    memset(&audioFormatDescription, 0, sizeof(audioFormatDescription));
		    //set the defaults for the instances ASBD
		    audioFormatDescription.mFormatID            =   kAudioFormatLinearPCM;
		    audioFormatDescription.mSampleRate          =   MCML_SAMPLING_FREQUENCY;
		    audioFormatDescription.mChannelsPerFrame    =   1;
		    audioFormatDescription.mBitsPerChannel      =   MCML_BITRATE;
		    audioFormatDescription.mBytesPerFrame       =   2;
		    audioFormatDescription.mBytesPerPacket      =   2;
		    audioFormatDescription.mFramesPerPacket     =   1;
		    audioFormatDescription.mFormatFlags         =   kLinearPCMFormatFlagIsSignedInteger | kLinearPCMFormatFlagIsPacked;
            
		    //create an audioFile handler
		    AudioFileID audioFileID = nil;
            
		    //Create the audio file *only when a WAV file is specified.
		    AudioFileCreateWithURL((CFURLRef)fileURL, kAudioFileWAVEType, &audioFormatDescription, kAudioFileFlags_EraseFile, &audioFileID);
            
		    char *dataToRecord;
		    UInt32 len = [recordedData length];
		    dataToRecord = (char *)malloc(len);
		    [recordedData getBytes:dataToRecord];
            
		    AudioFileOpenURL((CFURLRef)fileURL, kAudioFileWritePermission, kAudioFileWAVEType, &audioFileID);
            
		    AudioFileWriteBytes(audioFileID, FALSE, 0, &len, dataToRecord);
            
		    AudioFileClose(audioFileID);
            
		    // Memory leak support
		    if (dataToRecord != nil)
				free(dataToRecord);
		}];
	}
    
	// Termination data transmission and response reception
	mcml::CResponseData responseData;
    
	// Assigns transmission numbers
	std::string url = [mAccessURL UTF8String];
	mRequestNumber++;
    
	int result = mComCtrl->request(url, responseData);
	if (result != 0) {
		// Failed
		NSLog(@"ERROR: request(): %d", result);
		return nil;
	}
    
	// Gets XML data from response and outputs logs
	std::string xmlData;
	responseData.getXML(xmlData);
	NSString *nsXmlData = [NSString stringWithUTF8String:xmlData.c_str()];
	NSLog(@"SR OUT: %@", nsXmlData);
    
	// Response XML data processing
	return [self processResponseData:xmlData];
}

/**
 * @brief XML data generation method
 * @return XML data
 */
- (std::string)generateXMLData {
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
        
		request.AddService([MCML_RECOGNITION_SERVICE UTF8String]);
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
            
			language.AddID(mLanguage);
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
            
			hypothesisFormat.AddNofN_best(5);
			targetOutputLanguage.AddID(mLanguage);
			targetOutput.AddHypothesisFormat(hypothesisFormat);
			targetOutput.AddLanguageType(targetOutputLanguage);
            
			request.AddTargetOutput(targetOutput);
		}
        
        
		/****SETUP THE DATA****/
        
		{
			mcml::CInputType input;
			mcml::CDataType data;
			mcml::CAudioType audio;
			mcml::CAttachedBinaryType binaryType;
			audio.AddChannelID([@"1" UTF8String]);
            
			mcml::CModelTypeType modelType;
			mcml::CSignalType signal;
            
			modelType.AddDomain([@"Travel" UTF8String]);
			modelType.AddTask([@"Dictation" UTF8String]);
            
			signal.AddSamplingRate(MCML_SAMPLING_FREQUENCY);
			signal.AddValueType([@"integer" UTF8String]);
			signal.AddBitRate(MCML_BITRATE);
			signal.AddAudioFormat([MCML_RECORD_AUDIO UTF8String]);
			signal.AddEndian([MCML_RECORD_ENDIAN UTF8String]);
			signal.AddChannelQty([@"0" UTF8String]);
            
			audio.AddModelType(modelType);
			audio.AddSignal(signal);
			data.AddAudio(audio);
			binaryType.AddChannelID(1);
			binaryType.AddDataID(mLanguage);
            
			input.AddData(data);
            
			input.AddAttachedBinary(binaryType);
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
        
		//  Removes multiple texts and treats as arrays
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
 * @brief  Results notification method
 * @param resultString speech recognition results
 */
- (void)notifyResult:(NSString *)resultString {
	// Determines if method is set to delegate object
	if ([mDelegate respondsToSelector:@selector(onOutputSRResult:)]) {
		// Executes delegate method and notifies speech recognition results
		[(NSObject *)mDelegate performSelectorOnMainThread : @selector(onOutputSRResult:) withObject : resultString waitUntilDone : YES];
	}
    
	// Resets serial numbers for asynchronous divided transmission
	mRequestNumber = 1;
    
	// Clears remaining data in queue
	// (To prevent referencing of remaining data after error type is returned)
	[mRequestQueue removeAllObjects];
    
	// Normal end
	return;
}

/**
 * @brief  Instance release method
 */
- (void)dealloc {
	// SignalAdpcm release
	delete mSignalAdpcm;
    
	//AccessURL release
	[mAccessURL release];
    
	[super dealloc];
    
	// Normal end
	return;
}

@end
