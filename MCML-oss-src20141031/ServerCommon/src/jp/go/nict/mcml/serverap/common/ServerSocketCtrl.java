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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * ServerSocketCtrl class.
 * 
 */
public class ServerSocketCtrl extends ServerApThread {
    // ------------------------------------------
    // private member variable
    // ------------------------------------------
    private ServerSocket m_ServerSocket;
    private ArrayList<ClientSocketCtrl> m_ClientSocketCtrlList;
    private ServerApCorpusLogger m_CorpusLogger;
    private int m_RequestID;
    private boolean m_SupportDivision;
    private long m_ReceiveTimeoutTime;
    private long m_RequestLimitThreshold;
    private boolean m_IsEnableRequestTimeout;

    /**
     * Constructor
     * 
     * @param corpusLogger
     * @param requestLimitThreshold
     * @param isEnablerequestTimeout
     */
    // ------------------------------------------
    // public member function
    // ------------------------------------------
    public ServerSocketCtrl(ServerApCorpusLogger corpusLogger,
            long requestLimitThreshold, boolean isEnablerequestTimeout) {
        super("ServerSocketCtrl");
        m_CorpusLogger = corpusLogger;
        m_RequestID = 0;
        m_SupportDivision = false;
        m_ReceiveTimeoutTime = -1;
        m_RequestLimitThreshold = requestLimitThreshold;
        m_IsEnableRequestTimeout = isEnablerequestTimeout;
    }

    /**
     * Constructor
     * 
     * @param corpusLogger
     * @param supportDivision
     * @param receiveTimeoutTime
     */
    public ServerSocketCtrl(ServerApCorpusLogger corpusLogger,
            boolean supportDivision, long receiveTimeoutTime) {
        super("ServerSocketCtrl");
        m_CorpusLogger = corpusLogger;
        m_RequestID = 0;
        m_SupportDivision = supportDivision;
        m_ReceiveTimeoutTime = receiveTimeoutTime;
        m_RequestLimitThreshold = -1;
        m_IsEnableRequestTimeout = false;
    }

    /**
     * startAcceptation
     * 
     * @param port
     * @throws IOException
     */
    public void startAcceptation(int port) throws IOException {
        if (m_ServerSocket != null) {
            return;
        }

        // create server socket
        m_ServerSocket = new ServerSocket(port);
        m_ServerSocket.setReuseAddress(true);

        // create client socket controller list
        m_ClientSocketCtrlList = new ArrayList<ClientSocketCtrl>();

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
            while (m_ServerSocket != null) {
                removeClientSocketCtrl();
                Socket clientSocket = m_ServerSocket.accept();
                ClientSocketCtrl clientCtrl = new ClientSocketCtrl(
                        clientSocket, m_CorpusLogger, m_RequestID,
                        m_SupportDivision, m_ReceiveTimeoutTime,
                        m_RequestLimitThreshold, m_IsEnableRequestTimeout);
                m_ClientSocketCtrlList.add(clientCtrl);
                clientCtrl.start();

                // increment RequestID for next request
                if (m_RequestID == Integer.MAX_VALUE) {
                    m_RequestID = 0;
                } else {
                    m_RequestID++;
                }
            }
        } catch (SocketException exp) {
            // no process
        } finally {
            terminateClientSocketCtrl();
        }

        // normal end
        return;
    }

    @Override
    protected void processTermination() throws IOException {
        // close socket
        if (m_ServerSocket != null) {
            m_ServerSocket.close();
            m_ServerSocket = null;
        }

        // normal end
        return;
    }

    // ------------------------------------------
    // private member function
    // ------------------------------------------
    private void removeClientSocketCtrl() {
        Iterator<ClientSocketCtrl> it = m_ClientSocketCtrlList.iterator();

        while (it.hasNext()) {
            ClientSocketCtrl clientCtrl = it.next();
            if (!clientCtrl.hasConnection()) {
                it.remove();
            }
        }

        // normal end
        return;
    }

    private void terminateClientSocketCtrl() throws Exception {
        Iterator<ClientSocketCtrl> it = m_ClientSocketCtrlList.iterator();

        while (it.hasNext()) {
            ClientSocketCtrl clientCtrl = it.next();
            clientCtrl.terminate();
        }

        // normal end
        return;
    }
}
