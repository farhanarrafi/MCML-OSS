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

import java.io.IOException;

import jp.go.nict.mcml.serverap.common.ClientSocketForEngine;
import jp.go.nict.mcml.serverap.common.ConnectState;
import jp.go.nict.mcml.serverap.common.ServerApLogger;
import jp.go.nict.mcml.servlet.MCMLException;

/**
 * ASRComCtrl class.
 * 
 */
public class ASRComCtrl {
    // ------------------------------------------
    // private member constant
    // ------------------------------------------
    private static final String SR_NBEST_START = "comment=START-OF-FILE";
    private static final String SR_NBEST_END = "comment=END-OF-FILE";

    // ------------------------------------------
    // private member variable(instance field)
    // ------------------------------------------
    private ClientSocketForEngine m_SpeechInputSocket;
    private ClientSocketForEngine m_NBestReceiveSocket;
    private ASRNBestStreamReader m_NBestReader;
    private String m_EngineAddress;
    private int m_ConnectRetryTimes;
    private int m_ConnectRetryInterval;
    private int m_SpeechInputPort;
    private int m_NBestReceivePort;
    private String m_ClientHost;
    private int m_ClientSpeechInputPort;
    private int m_ClientNBestReceivePort;

    // ------------------------------------------
    // public member function
    // ------------------------------------------
    /**
     * Constructor
     * 
     * @param ipAddress
     * @param connectRetryTimes
     * @param connectRetryInterval
     * @param speechInputPort
     * @param nbestReceivePort
     * @param clientHost
     * @param clientSpeechInputPort
     * @param clientNbestReceivePort
     */
    public ASRComCtrl(String ipAddress, int connectRetryTimes,
            int connectRetryInterval, int speechInputPort,
            int nbestReceivePort, String clientHost, int clientSpeechInputPort,
            int clientNbestReceivePort) {
        m_NBestReceiveSocket = null;
        m_SpeechInputSocket = null;
        m_NBestReader = null;
        m_EngineAddress = ipAddress;
        m_ConnectRetryTimes = connectRetryTimes;
        m_ConnectRetryInterval = connectRetryInterval;
        m_SpeechInputPort = speechInputPort;
        m_NBestReceivePort = nbestReceivePort;
        m_ClientHost = clientHost;
        m_ClientSpeechInputPort = clientSpeechInputPort;
        m_ClientNBestReceivePort = clientNbestReceivePort;
    }

    /**
     * Check Connected
     * 
     * @return ConnectState
     */
    public ConnectState getConnectState() {
        // Socket created or not
        if (m_NBestReceiveSocket == null || m_SpeechInputSocket == null) {
            return ConnectState.FAILED;
        }

        // Socket connected or not
        ConnectState resultReceiveSocketState = m_NBestReceiveSocket
                .getConnectState();
        ConnectState speechInputSocketState = m_SpeechInputSocket
                .getConnectState();

        if (resultReceiveSocketState == ConnectState.FAILED
                || speechInputSocketState == ConnectState.FAILED) {
            return ConnectState.FAILED;
        }

        // Connect State is CONNECTING or CONNECTED.
        if (resultReceiveSocketState == ConnectState.CONNECTING
                || speechInputSocketState == ConnectState.CONNECTING) {
            return ConnectState.CONNECTING;
        }

        // Connect State is CONNECTED.
        return ConnectState.CONNECTED;
    }

    /**
     * connect
     * 
     * @param engineName
     * @throws IOException
     * @throws IllegalStateException
     */
    public void connect(String engineName) throws IOException,
            IllegalStateException {
        writeLog("ASRComCtrl::connect() start");

        // check SpeechDataInputSocket already opened.
        if (m_SpeechInputSocket != null || m_NBestReceiveSocket != null) {
            throw new IllegalStateException(
                    "SpeechDataInputSocket already opened.");
        }

        // create EngineName.
        engineName += ".";

        // connect start(SpeechInputPort)
        m_SpeechInputSocket = new ClientSocketForEngine(MCMLException.ASR);
        m_SpeechInputSocket.startConnecting(m_EngineAddress, m_SpeechInputPort,
                m_ClientHost, m_ClientSpeechInputPort, m_ConnectRetryTimes,
                m_ConnectRetryInterval, engineName + "speechinputport");

        // connect start(NBestReceivePort)
        m_NBestReceiveSocket = new ClientSocketForEngine(MCMLException.ASR);
        m_NBestReceiveSocket.startConnecting(m_EngineAddress,
                m_NBestReceivePort, m_ClientHost, m_ClientNBestReceivePort,
                m_ConnectRetryTimes, m_ConnectRetryInterval, engineName
                        + "nbestreceiveport");

        writeLog("ASRComCtrl::connect() end");
    }

    /**
     * disconnect
     * 
     * @throws IOException
     */
    public void disconnect() throws IOException {
        writeLog("ASRComCtrl::disconnect() start");
        // Socket close.
        if (m_NBestReceiveSocket != null) {
            m_NBestReceiveSocket.disconnect();
        }
        if (m_SpeechInputSocket != null) {
            m_SpeechInputSocket.disconnect();
        }
        writeLog("ASRComCtrl::disconnect() end");
    }

    /**
     * sendData
     * 
     * @param sendData
     * @throws MCMLException
     */
    public void sendData(byte[] sendData) throws MCMLException {
        try {
            m_SpeechInputSocket.sendData(sendData);
        } catch (IOException e) {
            writeLog("Data send failed.");
            throw new MCMLException("Data send failed.", MCMLException.ERROR,
                    MCMLException.ASR, MCMLException.ENGINE_CLOSED);
        }
    }

    /**
     * receiveData
     * 
     * @return m_NBestReader.read()
     * @throws MCMLException
     */
    public String receiveData() throws MCMLException {
        try {
            return m_NBestReader.read();
        } catch (IOException e) {
            System.out.println("Data receive failed.");
            throw new MCMLException("Data receive failed.",
                    MCMLException.ERROR, MCMLException.ASR,
                    MCMLException.ENGINE_CLOSED);
        } catch (InterruptedException e) {
            System.out.println("Data receive failed.");
            throw new MCMLException("Data receive failed.",
                    MCMLException.ERROR, MCMLException.ASR,
                    MCMLException.ENGINE_CLOSED);
        }
    }

    /**
     * Initialized NBestStreamReader
     * 
     * @param charset
     * @param engineName
     * @throws IOException
     * @throws InterruptedException
     */
    public void initNBestStreamReader(String charset, String engineName)
            throws IOException, InterruptedException {
        m_NBestReader = new ASRNBestStreamReader(
                m_NBestReceiveSocket.getInputStream());
        m_NBestReader.init(SR_NBEST_START, SR_NBEST_END, charset, engineName,
                ASRProperties.getInstance().getNullResponseSleepMSec(),
                ASRProperties.getInstance().getNullResponseCounter());
    }

    /**
     * Gets AcousticModelName.
     * 
     * @return AcousticModelName
     */
    public String getAcousticModelName() {
        return m_NBestReader.getAcousticModelName();
    }

    // ------------------------------------------
    // private member function
    // ------------------------------------------
    private void writeLog(String message) {
        ServerApLogger.getInstance().writeDebug(message);
    }
}
