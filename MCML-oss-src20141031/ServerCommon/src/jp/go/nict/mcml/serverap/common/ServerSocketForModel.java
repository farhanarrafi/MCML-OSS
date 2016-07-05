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
 * ServerSocketForModel class.
 * 
 */
public class ServerSocketForModel extends ServerApThread {
    // ------------------------------------------
    // private member variable
    // ------------------------------------------
    private ServerSocket m_ServerSocket;
    private ArrayList<ClientSocketForModel> m_ClientSocketList;
    private DataQueue<FrameData<byte[]>> m_FrameDataQueue;

    // ------------------------------------------
    // public member functions
    // ------------------------------------------
    /**
     * Constructor
     * 
     * @param frameDataQueue
     */
    public ServerSocketForModel(DataQueue<FrameData<byte[]>> frameDataQueue) {
        super("ServerSocketForModel");
        m_FrameDataQueue = frameDataQueue;
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
        m_ClientSocketList = new ArrayList<ClientSocketForModel>();

        // start thread
        this.start();

        // normal end
        return;
    }

    // ------------------------------------------
    // protected member functions
    // ------------------------------------------
    @Override
    protected void processMain() throws Exception {
        try {
            while (m_ServerSocket != null) {
                removeClientSocketCtrl();
                Socket clientSocket = m_ServerSocket.accept();
                ClientSocketForModel framSocket = new ClientSocketForModel(
                        clientSocket, m_FrameDataQueue);
                m_ClientSocketList.add(framSocket);
                framSocket.start();
            }
        } catch (SocketException exp) {
            // no processs
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
    // private member functions
    // ------------------------------------------
    private void removeClientSocketCtrl() {
        Iterator<ClientSocketForModel> it = m_ClientSocketList.iterator();

        while (it.hasNext()) {
            ClientSocketForModel frameSocket = it.next();
            if (!frameSocket.hasConnection()) {
                it.remove();
            }
        }

        // normal end
        return;
    }

    private void terminateClientSocketCtrl() throws Exception {
        Iterator<ClientSocketForModel> it = m_ClientSocketList.iterator();

        while (it.hasNext()) {
            ClientSocketForModel frameSocket = it.next();
            frameSocket.terminate();
        }

        // normal end
        return;
    }
}
