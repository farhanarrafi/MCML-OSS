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
 * @file AudioRecorder.mm
 * @brief Audio recording class implementation
 */

#import "AudioRecorder.h"
#import "Define.h"

@implementation AudioRecorder

@synthesize mRequestQueue;          //!< @brief Request queue for speech recognition
@synthesize mStopFlag;              //!< @brief Recording stop flag

/**
 * @brief  Status callback function
 * @param inUserData
 * @param inAQ
 * @param inBuffer
 * @param inStartTime
 * @param inNumberPacketDescriptions
 * @param inPacketDescs
 */
static void callbackFunction(void *inUserData, AudioQueueRef inAQ,
                             AudioQueueBufferRef inBuffer, const AudioTimeStamp *inStartTime,
                             UInt32 inNumberPacketDescriptions, const AudioStreamPacketDescription *inPacketDescs) {
	// Gets AudioRecorder object
	AudioRecorder *recorder = (AudioRecorder *)inUserData;
    
	// Gets recording data and byte count
	void *audioData = inBuffer->mAudioData;
	UInt32 audioDataBytes = inBuffer->mAudioDataByteSize;
    
	// Evaluates that the recording data is not null
	if (audioDataBytes != 0) {
		// Converts recording data to NSData
		NSData *data = [[NSData alloc] initWithBytes:audioData length:audioDataBytes];
        
		// Adds to request queue for speech recognition request queue
		[recorder.mRequestQueue enqueue:data];
		// Releases NSData
		[data release];
	}
    
	if (!recorder.mStopFlag) {
		// Adds buffer to Audio queue
		AudioQueueEnqueueBuffer(inAQ, inBuffer, 0, NULL);
	}
    
	// Normal end
	return;
}

/**
 * @brief Initialization method
 * @param requestQueue Request queue
 * @return self
 */
- (id)initWithParameters:(NSMutableArray *)requestQueue;
{
	self = [super init];
	if (self != nil) {
		// Stores request queue for speech recognition
		mRequestQueue = requestQueue;
		// Clears recording stop flag
		mStopFlag = false;
		// Internal processing initialization
		[self initializeAudio];
	}
    
	// Normal end
	return self;
}

/**
 * @brief Recording start method
 * @return True when successful. false when failed
 */
- (bool)start {
	// Recording start
	if (AudioQueueStart(mAudioQueue, NULL)) {
		// Failed
		return false;
	}
    
	// Normal end
	return true;
}

/**
 * @brief Voice recording stop method
 * @return True when successful. false when failed
 */
- (bool)performStop {
	//stop buffer enqueuing
	mStopFlag = true;
    
	//stop the audioQueue
	if (AudioQueueStop(mAudioQueue, false)) {
		return false;
	}
    
	return true;
}

/**
 * @brief Recording stop method
 * @return Normal end true
 */
- (bool)stop {
	//remove the property listener
	AudioQueueRemovePropertyListener(mAudioQueue, kAudioQueueProperty_IsRunning, recorderRunningPropListener, self);
    
	// Audio queue destruction
	AudioQueueDispose(mAudioQueue, false);
    
	// Adds termination data to request queue for speech recognition
	[mRequestQueue enqueue:[NSData data]];
    
	// Normal end
	return true;
}

/**
 * @brief Internal processing initialization method
 */
- (void)initializeAudio {
	// Audio format setting
	AudioStreamBasicDescription description;
	description.mSampleRate         = AUDIO_SAMPLING_FREQUENCY;
	description.mFormatID           = kAudioFormatLinearPCM;
	description.mFormatFlags        = kLinearPCMFormatFlagIsSignedInteger | kLinearPCMFormatFlagIsPacked;
	description.mFramesPerPacket    = 1;
	description.mChannelsPerFrame   = 1;
	description.mBitsPerChannel     = 16;
	description.mBytesPerPacket     = 2;
	description.mBytesPerFrame      = 2;
	description.mReserved           = 0;
    
	AudioQueueNewInput(&description, callbackFunction, self, CFRunLoopGetMain(), kCFRunLoopCommonModes, 0, &mAudioQueue);
    
	//Add a property listener
	AudioQueueAddPropertyListener(mAudioQueue, kAudioQueueProperty_IsRunning, recorderRunningPropListener, self);
    
	// Adds buffer to audio queue
	AudioQueueBufferRef buffers[3];
	for (int bufferIndex = 0; bufferIndex < 3; bufferIndex++) {
		AudioQueueAllocateBuffer(mAudioQueue, AUDIO_RECORDING_UNIT_BYTES, &buffers[bufferIndex]);
		AudioQueueEnqueueBuffer(mAudioQueue, buffers[bufferIndex], 0, NULL);
	}
    
	// Normal end
	return;
}

/**
 * @brief Recording property listener
 * @param inClientData  Client data
 * @param audioQueue Audio queue
 * @param propID  property ID
 */
void recorderRunningPropListener(void *inClientData, AudioQueueRef audioQueue, AudioQueuePropertyID propID) {
	//get our recorder instance
	AudioRecorder *currentRecorder = (AudioRecorder *)inClientData;
    
	//get the audioProperty
	UInt32 isCurrentlyRunning;
	UInt32 size = sizeof(isCurrentlyRunning);
	AudioQueueGetProperty(audioQueue, propID, &isCurrentlyRunning, &size);
    
	BOOL isRunning = (BOOL)isCurrentlyRunning;
    
	if (isRunning) {
		NSLog(@"The recorderQueue has started!");
	}
	else {
		NSLog(@"Ther recorderQueue has stopped!");
        
		//perform final procedures, such as dispose of the queue, etc.
		[currentRecorder stop];
	}
}

@end
