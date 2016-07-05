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

/**
 * RequestTimeoutWatcher thread class.
 * 
 */
public class RequestTimeoutWatcher extends ServerApThread {
    // ------------------------------------------
    // private member variable(instance field)
    // ------------------------------------------
    private long m_RequestTimeoutValue;
    private long m_RequestTimeoutInterval;

    // ------------------------------------------
    // public member function
    // ------------------------------------------
    /**
     * Constructor
     * 
     * @param requestTimeoutValue
     * @param requestTimeoutInterval
     */
    public RequestTimeoutWatcher(long requestTimeoutValue,
            long requestTimeoutInterval) {
        super("RequestTimeoutWatcher");
        m_RequestTimeoutValue = requestTimeoutValue;
        m_RequestTimeoutInterval = requestTimeoutInterval;
    }

    /**
     * startWatcher
     */
    public void startWatcher() {
        // start thread
        this.start();

        // normal end
        return;
    }

    // ------------------------------------------
    // protected member function
    // ------------------------------------------
    @Override
    protected void processMain() throws Exception {
        try {
            while (true) {
                boolean endfg = false;
                long currentTime = System.currentTimeMillis();
                while (!endfg) {
                    if (MCMLRequestQueue.getInstance().size() == 0) {
                        endfg = true;
                    } else {
                        MCMLData data = MCMLRequestQueue.getInstance()
                                .peekQueue();
                        // check request timeout
                        if (currentTime - data.getReceiveTime() >= m_RequestTimeoutValue) {
                            data = MCMLRequestQueue.getInstance().takeData(); // Dequeue
                            data.setIsRequestTimeoutOccurred(true); // set
                                                                    // request
                                                                    // timeout
                            data.doNotification();
                        } else {
                            endfg = true;
                            ServerApLogger.getInstance().writeDebug(
                                    "RequestTimeoutWatcher: still ok! elapsed time:"
                                            + (currentTime - data
                                                    .getReceiveTime()));
                        }
                    }
                }
                // wait for next time
                Thread.sleep(m_RequestTimeoutInterval);
            }
        } catch (Exception exp) {
            // pending
        }
        return;
    }

    @Override
    protected void processTermination() throws Exception {

        interrupt();

        join();

        return;
    }

}
