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

/**
 * ASRParam class.
 * 
 */
public class ASRParam {
    // ------------------------------------------
    // private member variable(instance field)
    // ------------------------------------------
    private String m_EngineHost;
    private int m_NBestReceivePort;
    private int m_SpeechInputPort;
    private String m_ClientEngineHost;
    private int m_ClientNBestReceivePort;
    private int m_ClientSpeechInputPort;
    private int m_RpcNumber;
    private int m_FrameSyncSize;
    private String m_AMCommandTableFileName;
    private String m_LMCommandTableFileName;
    private boolean m_WordTagPartOfSpeech;
    private boolean m_IsGwppUsed;

    // ------------------------------------------
    // public member function
    // ------------------------------------------
    /**
     * Default constructor
     */
    public ASRParam() {
        m_EngineHost = "";
        m_NBestReceivePort = -1;
        m_SpeechInputPort = -1;
        m_ClientEngineHost = "";
        m_ClientNBestReceivePort = -1;
        m_ClientSpeechInputPort = -1;
        m_RpcNumber = -1;
        m_FrameSyncSize = -1;
        m_AMCommandTableFileName = "";
        m_LMCommandTableFileName = "";
        m_WordTagPartOfSpeech = false;
        m_IsGwppUsed = false;
    }

    /**
     * Gets NBestReceivePort.
     * 
     * @return NBestReceivePort
     */
    public int getNBestReceivePort() {
        return m_NBestReceivePort;
    }

    /**
     * Sets NBestReceivePort.
     * 
     * @param nbestReceivePort
     */
    public void setNBestReceivePort(int nbestReceivePort) {
        m_NBestReceivePort = nbestReceivePort;
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
     * Gets SpeechInputPort.
     * 
     * @return SpeechInputPort
     */
    public int getSpeechInputPort() {
        return m_SpeechInputPort;
    }

    /**
     * Sets SpeechInputPort.
     * 
     * @param speechInputPort
     */
    public void setSpeechInputPort(int speechInputPort) {
        m_SpeechInputPort = speechInputPort;
    }

    /**
     * Gets ClientNBestReceivePort.
     * 
     * @return ClientNBestReceivePort
     */
    public int getClientNBestReceivePort() {
        return m_ClientNBestReceivePort;
    }

    /**
     * Sets ClientNBestReceivePort.
     * 
     * @param clientNbestReceivePort
     */
    public void setClientNBestReceivePort(int clientNbestReceivePort) {
        m_ClientNBestReceivePort = clientNbestReceivePort;
    }

    /**
     * Gets ClientEngineHost.
     * 
     * @return ClientEngineHost
     */
    public String getClientEngineHost() {
        return m_ClientEngineHost;
    }

    /**
     * Sets ClientEngineHost.
     * 
     * @param clientEngineHost
     */
    public void setClientEngineHost(String clientEngineHost) {
        m_ClientEngineHost = clientEngineHost;
    }

    /**
     * Gets ClientSpeechInputPort.
     * 
     * @return ClientSpeechInputPort
     */
    public int getClientSpeechInputPort() {
        return m_ClientSpeechInputPort;
    }

    /**
     * Sets ClientSpeechInputPort.
     * 
     * @param clientSpeechInputPort
     */
    public void setClientSpeechInputPort(int clientSpeechInputPort) {
        m_ClientSpeechInputPort = clientSpeechInputPort;
    }

    /**
     * Gets FrameSyncSize.
     * 
     * @return RpcNumber
     */
    public int getRpcNumber() {
        return m_RpcNumber;
    }

    /**
     * Sets RPCNumber.
     * 
     * @param rpcNumber
     */
    public void setRpcNumber(int rpcNumber) {
        m_RpcNumber = rpcNumber;
    }

    /**
     * Gets FrameSyncSize.
     * 
     * @return FrameSyncSize
     */
    public int getFrameSyncSize() {
        return m_FrameSyncSize;
    }

    /**
     * Sets FrameSyncSize.
     * 
     * @param frameSyncSize
     */
    public void setFrameSyncSize(int frameSyncSize) {
        m_FrameSyncSize = frameSyncSize;
    }

    /**
     * Gets AMCommandTableFileName.
     * 
     * @return AMCommandTableFileName
     */
    public String getAMCommandTableFileName() {
        return m_AMCommandTableFileName;
    }

    /**
     * Sets AMCommandTableFileName.
     * 
     * @param commandTableFileName
     */
    public void setAMCommandTableFileName(String commandTableFileName) {
        m_AMCommandTableFileName = commandTableFileName;
    }

    /**
     * Gets WordTagPartOfSpeech.
     * 
     * @return WordTagPartOfSpeech
     */
    public boolean getWordTagPartOfSpeech() {
        return m_WordTagPartOfSpeech;
    }

    /**
     * Sets WordTagPartOfSpeech.
     * 
     * @param partOfSpeech
     */
    public void setWordTagPartOfSpeech(boolean partOfSpeech) {
        m_WordTagPartOfSpeech = partOfSpeech;
    }

    /**
     * Gets LMCommandTableFileName.
     * 
     * @return LMCommandTableFileName
     */
    public String getLMCommandTableFileName() {
        return m_LMCommandTableFileName;
    }

    /**
     * Sets LMCommandTableFileName.
     * 
     * @param commandTableFileName
     */
    public void setLMCommandTableFileName(String commandTableFileName) {
        m_LMCommandTableFileName = commandTableFileName;
    }

    /**
     * Is GwppUsed?
     * 
     * @return m_IsGwppUsed
     */
    public boolean isGwppUsed() {
        return m_IsGwppUsed;
    }

    /**
     * Sets IsGwppUsed.
     * 
     * @param isGwppUsed
     */
    public void setIsGwppUsed(boolean isGwppUsed) {
        m_IsGwppUsed = isGwppUsed;
    }

}
