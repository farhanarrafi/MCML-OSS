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

package jp.go.nict.mcml.server.common;

import java.util.LinkedList;
import java.util.Queue;

/**
 * MCMLDataQueue class.
 * 
 */
public class MCMLDataQueue {
    // ------------------------------------------
    // private member variable
    // ------------------------------------------
    private Queue<MCMLData> m_Queue;

    // ------------------------------------------
    // public member function
    // ------------------------------------------
    /**
     * Default constructor
     */
    public MCMLDataQueue() {
        m_Queue = new LinkedList<MCMLData>();
    }

    /**
     * Adds data.
     * 
     * @param data
     */
    public synchronized void putData(MCMLData data) {
        m_Queue.add(data);
    }

    /**
* Deletes data.
     * 
     * @return Queue head
     */
    public synchronized MCMLData takeData() {
        return m_Queue.remove();
    }

    /**
     *Gets Queue element.
     * 
     * @return Queue element
     */
    public synchronized MCMLData peekData() {
        return m_Queue.element();
    }

    /**
     * Gets Queue size.
     * 
     * @return Queue size
     */
    public synchronized int size() {
        return m_Queue.size();
    }
}
