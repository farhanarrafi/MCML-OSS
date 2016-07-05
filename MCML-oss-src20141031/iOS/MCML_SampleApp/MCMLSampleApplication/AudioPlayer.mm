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
 * @file AudioPlayer.mm
 * @brief Audio play class implementation
 */

#import "AudioPlayer.h"
#import "Define.h"

@implementation AudioPlayer

@synthesize mDonePlaying;        //!< @brief Play state

/**
 * @brief Static callback function
 * @param outUserData
 * @param outAQ
 * @param outBuffer
 */
static void callbackFunction(void *outUserData, AudioQueueRef outAQ, AudioQueueBufferRef outBuffer) {
	// Gets AudioPlayer object
	AudioPlayer *player = (AudioPlayer *)outUserData;
    
	// Determines if playable
	if ([player hasPlayableSounds]) {
		// Next buffer setting
		[player setNextBuffer:outBuffer];
		// Adds buffer to audio queue
		AudioQueueEnqueueBuffer(outAQ, outBuffer, 0, NULL);
	}
	else {
		// Determine if currently playing
		if (!player.mDonePlaying) {
			//  Play stop
			[player stop:NO];
			player.mDonePlaying = YES;
		}
	}
    
	// Normal end
	return;
}

/**
 * @brief  Play start method
 * @param soundData  Played music data
 * @return True when successful. false when failed
 */
- (bool)start:(NSData *)soundData {
	[mSoundData release];
	mSoundData = [soundData retain];
	mPosition = 0;
	mDonePlaying = NO;
    
	// Audio format setting
	AudioStreamBasicDescription description;
	description.mSampleRate = AUDIO_SAMPLING_FREQUENCY;
	description.mFormatID = kAudioFormatLinearPCM;
    
	if ([MCML_PLAYBACK_ENDIAN isEqualToString:@"Big"])
		description.mFormatFlags = kAudioFormatFlagIsSignedInteger | kAudioFormatFlagIsPacked | kAudioFormatFlagIsBigEndian;
	else
		description.mFormatFlags = kAudioFormatFlagIsSignedInteger | kAudioFormatFlagIsPacked;
    
	description.mBytesPerPacket = 2;
	description.mBytesPerFrame = 2;
	description.mFramesPerPacket = 1;
	description.mChannelsPerFrame = 1;
	description.mBitsPerChannel = 16;
	description.mReserved = 0;
    
	// Audio queue creation
	AudioQueueNewOutput(&description, callbackFunction, self, NULL, NULL, 0, &mAudioQueue);
    
	// Adds buffer to audio queue
	AudioQueueBufferRef buffers[3];
	for (int bufferIndex = 0; bufferIndex < 3; bufferIndex++) {
		AudioQueueAllocateBuffer(mAudioQueue, AUDIO_PLAYBACK_UNIT_BYTES, &buffers[bufferIndex]);
		callbackFunction(self, mAudioQueue, buffers[bufferIndex]);
	}
    
	// Play start
	if (AudioQueueStart(mAudioQueue, NULL)) {
		// Failed
		return false;
	}
    
	// Normal end
	return true;
}

/**
 * @brief  Play stop method
 * @param shouldStopImmediate
 */
- (void)stop:(BOOL)shouldStopImmediate {
	//  Play stop
	AudioQueueStop(mAudioQueue, shouldStopImmediate);
	// Normal end
	return;
}

/**
 * @brief Playable state getter method
 * @return True when play can be continued. False when play cannot be continued.
 */
- (BOOL)hasPlayableSounds {
	// Evaluates if in the middle of playing synthesized sounds.
	if (mPosition < [mSoundData length]) {
		return TRUE;    // Play can be continued
	}
	else {
		// Play has reached the termination of the synthesized sounds or currently not playing
		return FALSE;   // Play cannot be continued
	}
}

/**
 * @brief Next buffer setter method
 * @param outBuffer Audio  queue buffer
 */
- (void)setNextBuffer:(AudioQueueBufferRef)outBuffer {
	// Calculates size of synthesized sounds copied to buffer
	NSUInteger soundLength = [mSoundData length];
	NSRange range;
	range.location = mPosition;
	range.length = (mPosition + AUDIO_PLAYBACK_UNIT_BYTES <= soundLength) ? AUDIO_PLAYBACK_UNIT_BYTES : soundLength - mPosition;
    
	// Copies next synthesized sound to buffer
	[mSoundData getBytes:outBuffer->mAudioData range:range];
    
	// Updates play position
	mPosition += range.length;
    
	// Sets size of synthesized sound to be played
	outBuffer->mAudioDataByteSize = range.length;
    
	// Normal end
	return;
}

/**
 * @brief  Instance release method
 */
- (void)dealloc {
	// Destruction of audio queue
	AudioQueueDispose(mAudioQueue, YES);
	[super dealloc];
    
	// Normal end
	return;
}

@end
