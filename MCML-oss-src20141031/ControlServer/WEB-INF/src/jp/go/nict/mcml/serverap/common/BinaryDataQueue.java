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

package jp.go.nict.mcml.serverap.common;

import java.util.LinkedList;
import java.util.Queue;

import org.apache.log4j.Logger;

/**
 * BinaryDataQueueクラスです。
 * 
 */
public class BinaryDataQueue {
    private static final Logger LOG = Logger.getLogger(BinaryDataQueue.class
            .getName());
    // ------------------------------------------
    // private member variable
    // ------------------------------------------
    private Queue<byte[]> m_Queue;

    // ------------------------------------------
    // public member function
    // ------------------------------------------
    /**
     * デフォルトコンストラクタ
     */
    public BinaryDataQueue() {
        m_Queue = new LinkedList<byte[]>();
    }

    /**
     * Queueを設定します。
     * 
     * @param data
     */
    public synchronized void putQueue(byte[] data) {
        m_Queue.add(data);

        // notification for worker thread
        notifyAll();

        // normal end
        return;
    }

    /**
     * Queueを削除します。
     * 
     * @return byte[]
     */
    public synchronized byte[] takeQueue() {
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
        return m_Queue.remove();
    }

    /**
     * Queue要素を取得します。
     * 
     * @return Queue要素
     */
    public synchronized byte[] peekQueue() {
        return m_Queue.element();
    }

    /**
     * Queueサイズを取得します。
     * 
     * @return Queueサイズ
     */
    public synchronized int size() {
        return m_Queue.size();
    }

}
