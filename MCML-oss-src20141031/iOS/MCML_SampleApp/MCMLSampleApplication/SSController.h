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
 * @file SSController.h
 * @brief Voice synthesis control class declaration
 */

#import <Foundation/Foundation.h>
#import "ClientComCtrl.h"
#import "SignalAdpcm.h"
#import "NSMutableArray+Queue.h"

// Delegate for voice synthesis results output notification
@protocol SSControllerDelegate <NSObject>
/**
 * @brief Voice synthesis results output notification
 * @param audioData Synthesized sound
 */
- (void)onOutputSSResult:(NSData *)audioData;
@end

@interface SSController : NSObject
{

	id <SSControllerDelegate> mDelegate;            //!< @brief Above delegate object
	NSMutableArray *mRequestQueue;                  //!< @brief Request queue
    std::string mLanguage;                          //!< @brief Language type
	int mUtteranceId;                               //!< @brief Speech ID
    mcml::CClientComCtrl * mComCtrl;                //!< @brief Communication controller
	bool mStopFlag;                                 //!< @brief Thread stop flag
	CSignalAdpcm *mSignalAdpcm;                     //!< @brief ADPCM処理部
	NSString *mAccessURL;                           //!< @brief Communication URL
}

@property (strong) NSString *mAccessURL;            //!< @brief Communication URL

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
           clientComCtrl:(mcml ::CClientComCtrl *)comCtrl
                     url:(NSString *)aURL;

/**
 * @brief Parameter setting method
 * @param language Speaker's language type
 * @param utteranceId Speech ID
 */
- (void)setParameters:(std ::string)language
          utteranceId:(int)utteranceId;

/**
 * @brief Thread start method
 */
- (void)runThread;

/**
 * @brief Thread stop method
 */
- (void)stopThread;

/**
 * @brief Thread main loop
 */
- (void)threadLoop;

/**
 * @brief Voice synthesis execution method
 * @param inputString  Input character string
 * @return Voice synthesis results
 */
- (NSData *)doSppehSynthesis:(NSString *)inputString;

/**
 * @brief XML data generation method
 * @param inputString  Input character string
 * @return XML data
 */
- (std ::string)generateXMLData:(NSString *)inputString;

/**
 * @brief Response processing method
 * @param xmlData XML data
 * @return Normal end true
 */
- (bool)processResponseData:(std ::string)xmlData;

@end
