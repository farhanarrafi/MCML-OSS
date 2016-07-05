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

//-------------------------------------------------------------------
//Ver.3.0
//2011/12/06
//-------------------------------------------------------------------

package jp.go.nict.mcml.serverap.common;

import java.util.LinkedList;
import java.util.Queue;

/**
 * DataQueue class.
 * 
 * @param <T>
 */
public class DataQueue<T> {
    // ------------------------------------------
    // private member variable
    // ------------------------------------------
    private Queue<T> m_Queue;

    // ------------------------------------------
    // public member functions
    // ------------------------------------------
    /**
     * Default constructor
     */
    public DataQueue() {
        m_Queue = new LinkedList<T>();

    }

    /**
     * Adds to queue.
     * 
     * @param data
     */
    public synchronized void putData(T data) {
        m_Queue.add(data);

        // notification for worker thread
        notifyAll();

        // normal end
        return;
    }

    /**
     * Deletes Queue.
     * 
     * @return m_Queue.remove()
     */
    public synchronized T takeData() {
        // wait notification for queuing
        try {
            while (size() <= 0) {
                wait();
            }
        } catch (InterruptedException exp) {
            // terminated
            return null;
        }

        // normal end
        return m_Queue.remove();
    }

    /**
     *Gets Queue element.
     * 
     * @return Queue element
     */
    public synchronized T peekQueue() {
        return m_Queue.element();
    }

    /**
     * Gets size.
     * 
     * @return Queue size
     */
    public synchronized int size() {
        return m_Queue.size();
    }

}
