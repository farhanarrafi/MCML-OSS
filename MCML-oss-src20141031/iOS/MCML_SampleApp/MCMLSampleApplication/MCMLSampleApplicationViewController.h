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
 * @file MCMLSampleApplicationViewController.h
 * @brief  View controller class declaration
 */

#import <UIKit/UIKit.h>
#import "AudioRecorder.h"
#import "AudioPlayer.h"
#import "NSMutableArray+Queue.h"
#import "SRController.h"
#import "MTController.h"
#import "SSController.h"
#import "ClientComCtrl.h"

@interface MCMLSampleApplicationViewController : UIViewController
<SRControllerDelegate, MTControllerDelegate, SSControllerDelegate>
{
	IBOutlet UIButton *mButtonJapanese;         //!< @brief Japanese Push to talk Button
	IBOutlet UITextView *mTextViewJapanese;     //!< @brief Japanese results display area
	IBOutlet UIButton *mButtonEnglish;          //!< @brief English Push to talk Button
	IBOutlet UITextView *mTextViewEnglish;      //!< @brief English results display area
    
    std::string mMyLanguage;                    //!< @brief Speaker's language type (i.e. Language type for speech recognition)
    std::string mOtherLanguage;                 //!< @brief Listener's language type (i.e. Language type for translation voice synthesis)
	UITextView *mMyTextView;                    //!< @brief Speaker's results display area
	UITextView *mOtherTextView;                 //!< @brief Listener's results display area
    
	AudioRecorder *mRecorder;                   //!< @brief Speech recording controller
	AudioPlayer *mPlayer;                       //!< @brief Synthesized sound play controller
    
	NSMutableArray *mSRRequestQueue;            //!< @brief Speech recognition request queue
	NSMutableArray *mMTRequestQueue;            //!< @brief Translation request queue
	NSMutableArray *mSSRequestQueue;            //!< @brief Voice synthesis request queue
    
	SRController *mSRController;                //!< @brief Speech recognition controller
	MTController *mMTController;                //!< @brief Translation  controller
	SSController *mSSController;                //!< @brief Voices synthesis  controller
    
    mcml::CClientComCtrl * mComCtrl;            //!< @brief Communication controller
    
	int mUtteranceId;                           //!< @brief Speech ID
    
	NSString *mAccessURL;                       //!< @brief Communication URL
}

@property (strong) NSString *mAccessURL;        //!< @brief Communication URL

/**
 * @brief Japanese Push to talk Button press action method
 */
- (IBAction)touchDownButtonJapanese;

/**
 * @brief Japanese Push to talk Button release action method
 */
- (IBAction)touchUpButtonJapanese;

/**
 * @brief English Push to talk Button press action method
 */
- (IBAction)touchDownButtonEnglish;

/**
 * @brief English Push to talk Button release action method
 */
- (IBAction)touchUpButtonEnglish;

/**
 * @brief Audio initialization method
 */
- (void)initializeAudio;

/**
 * @brief Communication controller generation method
 */
- (void)createComCtrl;

/**
 * @brief Recording start  method
 * @param myLanguage Speaker's language type
 * @param otherLanguage Listener's language type
 * @param myTextView Speaker's text view
 * @param otherTextView Listener's text view
 */
- (void)startRecording:(std ::string)myLanguage
         otherLanguage:(std ::string)otherLanguage
            myTextView:(UITextView *)myTextView
         otherTextView:(UITextView *)otherTextView;

/**
 * @brief  recording stop method
 * @param errorFlag  Error flag
 */
- (void)stopRecording:(bool)errorFlag;


/**
 * @brief Method for checking information at start
 * @param showUpdateMessage Message update flag
 */
- (void)serverOperationsAndShowUpdateMessage:(BOOL)showUpdateMessage;

@end
