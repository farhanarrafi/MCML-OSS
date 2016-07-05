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
 * ReceiveTimerTask class.
 * 
 */
public class ReceiveTimerTask extends ServerApTimerTask {
    // ------------------------------------------
    // private member valuables(instance field)
    // ------------------------------------------
    private ClientSocketCtrl m_ClientSocketCtrl;
    private MCMLData m_MCMLData;

    /**
     * Constructor
     * 
     * @param socketCtrl
     */
    public ReceiveTimerTask(ClientSocketCtrl socketCtrl) {
        m_ClientSocketCtrl = socketCtrl;
        m_MCMLData = null;
    }

    /**
     * MCMLData.
     * 
     * @param data
     */
    public void setMCMLData(MCMLData data) {
        m_MCMLData = data;
    }

    @Override
    protected void processTimeout() {
        // Timeout occurred to CallBack EngineCtrl.
        m_ClientSocketCtrl.processTimeout(m_MCMLData);
        m_MCMLData = null;
    }

}
