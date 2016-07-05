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

package jp.go.nict.mcml.servlet.control.dispatcher;

import java.util.LinkedList;
import java.util.Queue;

import jp.go.nict.mcml.servlet.control.dispatcher.container.ResponseContainer;

import org.apache.log4j.Logger;

/**
 * ResponseContainerQueue class.
 * 
 */
public class ResponseContainerQueue {
    private static final Logger LOG = Logger
            .getLogger(ResponseContainerQueue.class.getName());
    // ------------------------------------------
    // private member variables
    // ------------------------------------------

    /** ResponseContainer type queue */
    public Queue<ResponseContainer> queue;

    // ------------------------------------------
    // public member functions
    // ------------------------------------------
    /** Default constructor */
    public ResponseContainerQueue() {
        queue = new LinkedList<ResponseContainer>();

    }

    /**
     * enqueue
     * 
     * @param responseData
     */
    public synchronized void enqueue(ResponseContainer responseData) {
        queue.offer(responseData);

        // notification for worker thread
        notifyAll();

        // normal end
        return;
    }

    /**
     * dequeue
     * 
     * @return queue.poll()
     */
    public synchronized ResponseContainer dequeue() {
        // wait notification for queuing
        try {
            while (size() <= 0) {
                wait();
            }
        } catch (InterruptedException exp) {
            LOG.error(exp.getMessage(), exp);
            // terminated
            return null;
        }

        // normal end
        return queue.poll();
    }

    /**
     * Gets queue peek.
     * 
     * @return queue.peek()
     */
    public synchronized ResponseContainer peek() {
        return queue.peek();
    }

    /**
     * Gets queue size.
     * 
     * @return queue size
     */
    public synchronized int size() {
        return queue.size();
    }
}
