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
 * @file MCMLSampleApplicationViewController.mm
 * @brief View controller class implementation
 */

#import "MCMLSampleApplicationViewController.h"
#import "Define.h"

@implementation MCMLSampleApplicationViewController

@synthesize mAccessURL;        //!< @brief Communication URL

/**
 * @brief  Instance release method
 */
- (void)dealloc {
	// Speech recognition/translation/voice synthesis thread stopping
	[mSRController stopThread];
	[mMTController stopThread];
	[mSSController stopThread];
    
	// Audio type release
	[mRecorder release];
	[mPlayer release];
    
	// Speech recognition/translation/synthesis controller release
	[mSRController release];
	[mMTController release];
	[mSSController release];
    
	// Speech recognition/translation/synthesis request queue release
	[mSRRequestQueue release];
	[mMTRequestQueue release];
	[mSSRequestQueue release];
    
	// Communication controller release
	delete mComCtrl;
    
	// Communication URL release
	[mAccessURL release];
    
	[super dealloc];
    
	// Normal end
	return;
}

/**
 * @brief Method called when memory is insufficient
 */
- (void)didReceiveMemoryWarning {
	// Releases the view if it doesn't have a superview.
	[super didReceiveMemoryWarning];
    
	// Release any cached data, images, etc that aren't in use.
}

#pragma mark - View lifecycle

/**
 * @brief  Method called only at initial loading
 */
- (void)viewDidLoad {
	[super viewDidLoad];
    
	// Audio type initialization
	[self initializeAudio];
    
	// Communication controller generation
	[self createComCtrl];
    
	// Speech ID initialization(Set as 1 origin)
	mUtteranceId = 1;
    
	// Speech recognition request queue generation
	mSRRequestQueue = [[NSMutableArray alloc] init];
	// Speech recognition controller generation
	mSRController = [[SRController alloc] initWithParameters:self request:mSRRequestQueue clientComCtrl:mComCtrl url:self.mAccessURL];
	// Speech recognition thread start
	[mSRController runThread];
    
	// Translation request queue generation
	mMTRequestQueue = [[NSMutableArray alloc] init];
	// Translation controller generation
	mMTController = [[MTController alloc] initWithParameters:self request:mMTRequestQueue clientComCtrl:mComCtrl url:self.mAccessURL];
	// Translation thread start
	[mMTController runThread];
    
	// Voice synthesis request queue generation
	mSSRequestQueue = [[NSMutableArray alloc] init];
	// Voice synthesis control generation
	mSSController = [[SSController alloc] initWithParameters:self request:mSSRequestQueue clientComCtrl:mComCtrl url:self.mAccessURL];
	// Voice synthesis thread start
	[mSSController runThread];
    
	// Normal end
	return;
}

/**
 * @brief Method for checking information at start
 * @param showUpdateMessage Message update flag
 */
- (void)serverOperationsAndShowUpdateMessage:(BOOL)showUpdateMessage {
	//Assign the server
	self.mAccessURL = MCML_SERVER;
    
	//Establish that a server connection is possible ( that the device can communicate ) with a simple "GET" request
	NSURL *url = [NSURL URLWithString:mAccessURL];
	NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:url];
	[request setTimeoutInterval:APPLOADING_TIMEOUT];
	request.HTTPMethod = @"GET";
	[request setValue:@"text/html; charset=UTF-8" forHTTPHeaderField:@"Content-Type"];
    
	//send the request and receive the response
	[NSURLConnection sendAsynchronousRequest:request queue:[NSOperationQueue mainQueue] completionHandler: ^(NSURLResponse *response, NSData *data, NSError *error) {
	    //NSLog(@"Status code for log Attempt: %d",[(NSHTTPURLResponse *)response statusCode]);
        
	    //if the response is not valid, show the alertview
	    if ([(NSHTTPURLResponse *)response statusCode] != 200) {
	        UIAlertView *noButtonAlert = [[UIAlertView alloc] initWithTitle:@"NO SERVER CONNECTION" message:@"A server connection could not be established. Please confirm your network connection and try again." delegate:nil cancelButtonTitle:nil otherButtonTitles:nil];
            
	        [noButtonAlert show];
	        [noButtonAlert release];
		}
	}];
    
	NSLog(@"mAccessURL=%@", self.mAccessURL);
    
	//Assign the url to each controller
	if (mSRController) mSRController.mAccessURL = self.mAccessURL;
	if (mMTController) mMTController.mAccessURL = self.mAccessURL;
	if (mSSController) mSSController.mAccessURL = self.mAccessURL;
}

/**
 * @brief Method called when memory is insufficient
 */
- (void)viewDidUnload {
	[super viewDidUnload];
	// Release any retained subviews of the main view.
	// e.g. self.myOutlet = nil;
}

/**
 * @brief Japanese Push to talk Button press action method
 */
- (IBAction)touchDownButtonJapanese {
	// Disables English Push to talk Button
	[mButtonEnglish setEnabled:NO];
    
	// Sets speaker's language to Japanese, listener's language to English, and starts recording
	[self startRecording:MCML_LANGUAGE_JAPANESE otherLanguage:MCML_LANGUAGE_ENGLISH myTextView:mTextViewJapanese otherTextView:mTextViewEnglish];
    
	// Timer start
	if (AUDIO_RECORDING_TIME_LIMIT != 0)
		[self performSelector:@selector(touchUpButtonJapanese) withObject:nil afterDelay:AUDIO_RECORDING_TIME_LIMIT];
    
    
	// Normal end
	return;
}

/**
 * @brief Japanese Push to talk Button release action method
 */
- (IBAction)touchUpButtonJapanese {
	// No action if nothing recorded
	if ([mButtonEnglish isEnabled])
		return;
    
	// Recording stop
	[self stopRecording:false];
    
	// Enables English Push to talk Button
	[mButtonEnglish setEnabled:YES];
    
	// Timer cancel
	[NSObject cancelPreviousPerformRequestsWithTarget:self];
    
	// Normal end
	return;
}

/**
 * @brief English Push to talk Button press action method
 */
- (IBAction)touchDownButtonEnglish {
	// Disables Japanese Push to talk Button
	[mButtonJapanese setEnabled:NO];
    
	// Sets speaker's language to English, listener's language to Japanese, and starts recording
	[self startRecording:MCML_LANGUAGE_ENGLISH otherLanguage:MCML_LANGUAGE_JAPANESE myTextView:mTextViewEnglish otherTextView:mTextViewJapanese];
    
	// Timer end timer start
	if (AUDIO_RECORDING_TIME_LIMIT != 0)
		[self performSelector:@selector(touchUpButtonEnglish) withObject:nil afterDelay:AUDIO_RECORDING_TIME_LIMIT];
    
	// Normal end
	return;
}

/**
 * @brief English Push to talk Button release action method
 */
- (IBAction)touchUpButtonEnglish {
	//No action if nothing recorded
	if ([mButtonJapanese isEnabled])
		return;
    
	// Recording stop
	[self stopRecording:false];
    
	// Enable Japanese Push to talk.
	[mButtonJapanese setEnabled:YES];
    
	// Timer cancel
	[NSObject cancelPreviousPerformRequestsWithTarget:self];
    
	// Normal end
	return;
}

/**
 * @brief Audio initialization method
 */
- (void)initializeAudio {
	// AudioSession initialization, enable
	AudioSessionInitialize(NULL, NULL, NULL, NULL);
	AudioSessionSetActive(YES);
    
	// Sets property for carrying out recording and playing simultaneously
	UInt32 sessionCategory = kAudioSessionCategory_PlayAndRecord;
	AudioSessionSetProperty(kAudioSessionProperty_AudioCategory,
	                        sizeof(sessionCategory), &sessionCategory);
    
    
	// Synthesized sound playing part generation
	mPlayer = [[AudioPlayer alloc] init];
    
	// Normal end
	return;
}

/**
 * @brief Communication controller generation method
 */
- (void)createComCtrl {
	// Information check at start
	[self serverOperationsAndShowUpdateMessage:YES];
    
	// Communication controller generation
	mComCtrl = new mcml::CClientComCtrl();
	// Timeout time setting
	mComCtrl->setTimeout(COMMUNICATION_TIMEOUT);
	// Normal end
	return;
}

/**
 * @brief Recording start  method
 * @param myLanguage Speaker's language type
 * @param otherLanguage Listener's language type
 * @param myTextView Speaker's text view
 * @param otherTextView listener text view
 */
- (void)startRecording:(std::string)myLanguage
         otherLanguage:(std::string)otherLanguage
            myTextView:(UITextView *)myTextView
         otherTextView:(UITextView *)otherTextView {
	// Speaker's language type
	mMyLanguage = myLanguage;
	// Listener's language type
	mOtherLanguage = otherLanguage;
	// Speaker's results display area
	mMyTextView = myTextView;
	// Listener's results display area
	mOtherTextView = otherTextView;
    
	// Displays message currently being recorded in speaker's results display area
	mMyTextView.text = MESSAGE_RECORDING;
	// Changes background color of speaker's results display area
	mMyTextView.backgroundColor = COLOR_RECORDING;
	// Clears listener's results display area
	mOtherTextView.text = @"";
    
	// Sets language type and speech ID in speech recognition/translation/voice synthesis controller
	[mSRController setParameters:mMyLanguage utteranceId:mUtteranceId];
	[mMTController setParameters:mMyLanguage otherLanguage:mOtherLanguage utteranceId:mUtteranceId];
	[mSSController setParameters:mOtherLanguage utteranceId:mUtteranceId];
    
	// Updates speech ID for next execution
	mUtteranceId++;
    
	// Generates speech recording controller (Refer to request queue for speech recognition)
	mRecorder = [[AudioRecorder alloc]initWithParameters:mSRRequestQueue];
    
    
	// Sets property for carrying out recording and play simultaneously
	UInt32 sessionCategory = kAudioSessionCategory_PlayAndRecord;
	AudioSessionSetProperty(kAudioSessionProperty_AudioCategory,
	                        sizeof(sessionCategory), &sessionCategory);
    
	// Recording start
	[mRecorder start];
    
	// Normal end
	return;
}

/**
 * @brief Recording stop method
 * @param errorFlag  Error flag
 */
- (void)stopRecording:(bool)errorFlag;
{
	if (!errorFlag && mRecorder != nil) {
		// Displays message currently being voice-recognized in speaker's results display area
		mMyTextView.text = MESSAGE_RECOGNIZING;
		// Changes background color of speaker's results display area
		mMyTextView.backgroundColor = COLOR_RECOGNIZING;
		// Returns background color of listener's results display area to original color
		mOtherTextView.backgroundColor = COLOR_IDLING;
	}
	else {
		// Displays speech recognition error message in speaker's results display area
		mMyTextView.text = MESSAGE_ERROR_RECIGNITION;
		// Returns background color of speaker's results display area to original color
		mMyTextView.backgroundColor = COLOR_IDLING;
		// Returns background color of listener's results display area to original color
		mOtherTextView.backgroundColor = COLOR_IDLING;
	}
    
	if (mRecorder != nil) {
		// Recording stop
        
		//[mRecorder stop];
		[mRecorder performStop];
	}
    
    
	/*
     // Sets property for playing
     UInt32 sessionCategory = kAudioSessionCategory_MediaPlayback;
     AudioSessionSetProperty(kAudioSessionProperty_AudioCategory,
     sizeof (sessionCategory), &sessionCategory);
	 */
    
	// Normal end
	return;
}

/**
 * @brief Delegate method executed during output of speech recognition results
 * @param resultString Speech recognition results
 */
- (void)onOutputSRResult:(NSString *)resultString {
	// Returns background color of speaker's results display area to original color
	mMyTextView.backgroundColor = COLOR_IDLING;
    
	// Determines if speech recognition results are null or not
	if (resultString != nil && resultString.length != 0) {
		// Displays speech recognition results in speaker's results display area
		mMyTextView.text = resultString;
		// Sets speech recognition results in translation request queue
		[mMTRequestQueue enqueue:resultString];
		// Displays message being translated in listener's results display area
		mOtherTextView.text = MESSAGE_TRANSLATING;
		// Changes background color of listener's results display area
		mOtherTextView.backgroundColor = COLOR_TRANSLATING;
	}
	else {
		// Recording stop
		[self stopRecording:true];
	}
    
	// Releases speech recording controller
	[mRecorder release];
	mRecorder = nil;
    
	// Sets property for playing
	UInt32 sessionCategory = kAudioSessionCategory_MediaPlayback;
	AudioSessionSetProperty(kAudioSessionProperty_AudioCategory,
	                        sizeof(sessionCategory), &sessionCategory);
    
	// Normal end
	return;
}

/**
 * @brief Delegate method executed during output of translation results
 * @param resultString Translation results
 */
- (void)onOutputMTResult:(NSString *)resultString {
	// Determines whether translation results are null or not
	if (resultString != nil && resultString.length != 0) {
		// Displays speech recognition results in listener's results display area
		mOtherTextView.text = resultString;
		// Sets translation results in request queue for voice synthesis
		[mSSRequestQueue enqueue:resultString];
		// Changes background color of speaker's results display area
		mMyTextView.backgroundColor = COLOR_TRANSLATING;
		// Changes background color of listener's results display area
		mOtherTextView.backgroundColor = COLOR_PLAYING;
	}
	else {
		// Displays translation error message in listener's results display area
		mOtherTextView.text = MESSAGE_ERROR_TRANSLATION;
		// Returns background color of speaker's results display area to original color
		mMyTextView.backgroundColor = COLOR_IDLING;
		// Returns background color of listener's results display area to original color
		mOtherTextView.backgroundColor = COLOR_IDLING;
	}
    
	// Normal end
	return;
}

/**
 * @brief Delegate method executed during output of reverse translation results
 * @param resultString Reverse translation results
 */
- (void)onOutputRTResult:(NSString *)resultString {
	// Returns background color of speaker's results display area to original color
	mMyTextView.backgroundColor = COLOR_IDLING;
    
	// Determines whether reverse translation results are null or not
	if (resultString != nil && resultString.length != 0) {
		// Adds reverse translation errors to speaker's results display area
		mMyTextView.text = [mMyTextView.text stringByAppendingFormat:@"\n<<%@>>", resultString];
	}
	else {
		// Adds reverse translation error message to speaker's results display area
		mMyTextView.text = [mMyTextView.text stringByAppendingString:@"\n" MESSAGE_ERROR_BACK_TRANSLATION];
	}
    
	// Normal end
	return;
}

/**
 * @brief Delegate method executed during output of voice synthesis results
 * @param audioData Synthesized sound
 */
- (void)onOutputSSResult:(NSData *)audioData {
	// Returns background color of speaker's results display area to original color
	mMyTextView.backgroundColor = COLOR_IDLING;
	// Returns background color of listener's results display area to original color
	mOtherTextView.backgroundColor = COLOR_IDLING;
    
	// Determines whether synthesized results are null or not
	if (audioData != nil && audioData.length != 0) {
		// Starts play of synthesized sounds.
		[mPlayer start:audioData];
	}
    
	// Normal end
	return;
}

@end
