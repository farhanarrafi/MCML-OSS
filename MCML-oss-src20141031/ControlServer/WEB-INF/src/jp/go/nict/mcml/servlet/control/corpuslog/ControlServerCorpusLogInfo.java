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

package jp.go.nict.mcml.servlet.control.corpuslog;

/**
 * ControlServerCorpusLogInfo class.
 * 
 */
public class ControlServerCorpusLogInfo {
    // ------------------------------------------
    // private member variables
    // ------------------------------------------
    private long m_FirstFrameArrivedTime;
    private long m_LastFrameArrivedTime;
    private String m_ClientIP;

    // ------------------------------------------
    // public member functions
    // ------------------------------------------
    ControlServerCorpusLogInfo() {
        m_FirstFrameArrivedTime = 0;
        m_LastFrameArrivedTime = 0;
        m_ClientIP = "";
    }

    /**
     * Setter
     * 
     * @param firstFrameArrivedTime
     */
    public void setFirstFrameArrivedTime(long firstFrameArrivedTime) {
        m_FirstFrameArrivedTime = firstFrameArrivedTime;
    }

    /**
     * Sets ClientIP.
     * 
     * @param clientIP
     */
    public void setClientIP(String clientIP) {
        m_ClientIP = clientIP;
    }

    /**
     * Sets lastFrameArrivedTime.
     * 
     * @param lastFrameArrivedTime
     */
    public void setLastFrameArrivedTime(long lastFrameArrivedTime) {
        m_LastFrameArrivedTime = lastFrameArrivedTime;
    }

    /**
     * Getter
     * 
     * @return FirstFrameArrivedTime
     */
    public long getFirstFrameArrivedTime() {
        return m_FirstFrameArrivedTime;
    }

    /**
     * Gets ClientIP.
     * 
     * @return ClientIP
     */
    public String getClientIP() {
        return m_ClientIP;
    }

    /**
     * Gets LastFrameArrivedTime.
     * 
     * @return LastFrameArrivedTime
     */
    public long getLastFrameArrivedTime() {
        return m_LastFrameArrivedTime;
    }
}
