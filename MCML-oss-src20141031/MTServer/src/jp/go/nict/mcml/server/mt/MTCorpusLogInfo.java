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

package jp.go.nict.mcml.server.mt;

import jp.go.nict.mcml.serverap.common.CorpusLogInfo;

/**
 * MTCorpusLogInfo class.
 * 
 */
public class MTCorpusLogInfo extends CorpusLogInfo {

    // ------------------------------------------
    // private member variable
    // ------------------------------------------
    private long m_MTCompleteTime;
    private String m_Direction;

    // ------------------------------------------
    // constructer
    // ------------------------------------------
    MTCorpusLogInfo() {

        super();

        m_MTCompleteTime = 0;
        m_Direction = "";
    }

    // ------------------------------------------
    // setter and getter
    // ------------------------------------------
    /**
     * Gets MTCompleteTime
     * 
     * @return m_MTCompleteTime
     */
    public long getMTCompleteTime() {
        return m_MTCompleteTime;
    }

    /**
     * Sets MTCompleteTime.
     * 
     * @param completeTime
     */
    public void setMTCompleteTime(long completeTime) {
        m_MTCompleteTime = completeTime;
    }

    /**
     * Gets Direction.
     * 
     * @return m_Direction
     */
    public String getDirection() {
        return m_Direction;
    }

    /**
     * Sets Direction.
     * 
     * @param direction
     */
    public void setDirection(String direction) {
        m_Direction = direction;
    }

}
