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

package jp.go.nict.mcml.server.tts;

/**
 * TTSParam class.
 * 
 * 
 */
public class TTSParam {
    // ------------------------------------------
    // private member variables(instance field)
    // ------------------------------------------
    private String m_EngineHost;
    private int m_EnginePort;
    private int m_BytesPerFrame;
    private String m_EngineClientHost;
    private int m_EngineClientPort;

    // ------------------------------------------
    // public member functions
    // ------------------------------------------
    /**
     * Default constructor
     */
    public TTSParam() {
        m_EngineHost = "";
        m_EnginePort = -1;
        m_BytesPerFrame = -1;
        m_EngineClientHost = "";
        m_EngineClientPort = -1;
    }

    /**
     * Gets EngineHost.
     * 
     * @return EngineHost
     */
    public String getEngineHost() {
        return m_EngineHost;
    }

    /**
     * Sets EngineHost.
     * 
     * @param engineHost
     */
    public void setEngineHost(String engineHost) {
        m_EngineHost = engineHost;
    }

    /**
     * Gets EnginePort.
     * 
     * @return EnginePort
     */
    public int getEnginePort() {
        return m_EnginePort;
    }

    /**
     * Sets EnginePort.
     * 
     * @param port
     */
    public void setEnginePort(int port) {
        m_EnginePort = port;
    }

    /**
     * Gets EngineClientHost.
     * 
     * @return EngineClientHost
     */
    public String getEngineClientHost() {
        return m_EngineClientHost;
    }

    /**
     * Sets EngineClientHost.
     * 
     * @param engineClientHost
     */
    public void setEngineClientHost(String engineClientHost) {
        m_EngineClientHost = engineClientHost;
    }

    /**
     * Gets EngineClientPort.
     * 
     * @return EngineClientPort
     */
    public int getEngineClientPort() {
        return m_EngineClientPort;
    }

    /**
     * Sets EngineClientPort.
     * 
     * @param clientPort
     */
    public void setEngineClientPort(int clientPort) {
        m_EngineClientPort = clientPort;
    }

    /**
     * Gets BytesPerFrame.
     * 
     * @return BytesPerFrame
     */
    public int getBytesPerFrame() {
        return m_BytesPerFrame;
    }

    /**
     * Sets BytesPerFrame.
     * 
     * @param bytesPerFrame
     */
    public void setBytesPerFrame(int bytesPerFrame) {
        m_BytesPerFrame = bytesPerFrame;
    }
}
