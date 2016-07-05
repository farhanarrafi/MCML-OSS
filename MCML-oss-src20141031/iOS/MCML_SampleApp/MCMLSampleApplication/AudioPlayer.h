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
 * @file AudioPlayer.h
 * @brief Audio play class declaration
 */

#import <Foundation/Foundation.h>
#import <AudioToolbox/AudioToolbox.h>

@interface AudioPlayer : NSObject {

	AudioQueueRef mAudioQueue;      //!< @brief Audio queue
	BOOL mDonePlaying;              //!< @brief  Play state
	NSData *mSoundData;             //!< @brief Synthesized sound
	NSUInteger mPosition;           //!< @brief  Play position
}

@property BOOL mDonePlaying;        //!< @brief  Play state

/**
 * @brief  Play start method
 * @param soundData Played music data
 * @return True when successful. false when failed
 */
- (bool)start:(NSData *)soundData;

/**
 * @brief  Play stop method
 * @param shouldStopImmediate 
 */
- (void)stop:(BOOL)shouldStopImmediate;

/**
 * @brief  Playable state getter method
 * @return True when play can be continued, false when play cannot be continued.
 */
- (BOOL)hasPlayableSounds;

/**
 * @brief  Next buffer setting method
 * @param outBuffer Audio queue buffer
 */
- (void)setNextBuffer:(AudioQueueBufferRef)outBuffer;

@end
