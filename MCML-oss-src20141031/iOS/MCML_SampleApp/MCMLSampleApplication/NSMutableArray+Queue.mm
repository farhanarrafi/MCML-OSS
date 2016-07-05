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
 * @file NSMutableArray+Queue.mm
 * @brief  Request queue class implementation
 */

#import "NSMutableArray+Queue.h"

@implementation NSMutableArray (Queue)

/**
 * @brief Gets object from queue
 * @return Normal end (Returns nil if no object in queue)
 */
- (id)dequeue {
	id object = nil;
    
	if ([self count] != 0) {
		object = [[[self objectAtIndex:0]retain]autorelease];
		[self removeObjectAtIndex:0];
	}
    
	// Normal end (Returns nil if no object in queue)
	return object;
}

/**
 * @brief Gets object from queue (Not deleted)
 * @return Normal end (Returns nil if no object in queue)
 */
- (id)peek {
	id object = nil;
    
	if ([self count] != 0) {
		object = [[[self objectAtIndex:0]retain]autorelease];
	}
    
	// Normal end(Returns nil if no object in queue)
	return object;
}

/**
 * @brief Adds object to queue
 * @param object  Object
 */
- (void)enqueue:(id)object {
	[self addObject:object];
    
	// Normal end
	return;
}

@end
