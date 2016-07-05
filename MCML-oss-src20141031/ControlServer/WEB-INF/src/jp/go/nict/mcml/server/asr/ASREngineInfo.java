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

package jp.go.nict.mcml.server.asr;

import jp.go.nict.mcml.serverap.common.EngineInfo;

/**
 * ASREngineInfo class (for ASR server).
 * 
 */
public class ASREngineInfo extends EngineInfo {
    // ------------------------------------------
    // private member variable(instance field)
    // ------------------------------------------
    private String m_AcousticModelName;
    private String m_NICTlatticeModelName;
    private String m_NICTrescoreModelName;

    // ------------------------------------------
    // public member functions
    // ------------------------------------------
    /**
     * Default constructor
     */
    public ASREngineInfo() {
        super();
    }

    /**
     * Gets AcousticModelName.
     * 
     * @return m_AcousticModelName
     */
    public String getAcousticModelName() {
        return m_AcousticModelName;
    }

    /**
     * Sets AcousticModelName.
     * 
     * @param acousticModelName
     */
    public void setAcousticModelName(String acousticModelName) {
        m_AcousticModelName = acousticModelName;
    }

    /**
     * Gets NICTlatticeModelName.
     * 
     * @return m_NICTlatticeModelName
     */
    public String getNICTlatticeModelName() {
        return m_NICTlatticeModelName;
    }

    /**
     * Sets NICTlatticeModelName.
     * 
     * @param rlatticeModelName
     */
    public void setNICTlatticeModelName(String rlatticeModelName) {
        m_NICTlatticeModelName = rlatticeModelName;
    }

    /**
     * Gets NICTrescoreModelName.
     * 
     * @return m_NICTrescoreModelName
     */
    public String getNICTrescoreModelName() {
        return m_NICTrescoreModelName;
    }

    /**
     * Sets NICTrescoreModelName.
     * 
     * @param rrescoreModelName
     */
    public void setNICTrescoreModelName(String rrescoreModelName) {
        m_NICTrescoreModelName = rrescoreModelName;
    }

}
