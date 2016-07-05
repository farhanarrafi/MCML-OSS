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

package jp.go.nict.mcml.engine.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import jp.go.nict.mcml.com.client.ClientComCtrl;

/**
 * SocketAndStream class.
 * 
 */
public class SocketAndStream {
    // ------------------------------------------
    // private member variable
    // ------------------------------------------
    private Socket m_Socket;
    private DataOutputStream m_DataOutputStream;
    private DataInputStream m_DataInputStream;
    private ClientComCtrl m_ClientComCtrl;
    private String m_URL;

    // ------------------------------------------
    // public member function
    // ------------------------------------------
    /**
     * Constructor
     * 
     * @param host
     * @param port
     * @throws IOException
     */
    public SocketAndStream(String host, int port) throws IOException {
        m_Socket = new Socket(host, port);
        m_DataOutputStream = new DataOutputStream(m_Socket.getOutputStream());
        m_DataInputStream = new DataInputStream(m_Socket.getInputStream());
        m_ClientComCtrl = null;
    }

    /**
     * Constructor
     * 
     * @param clientComCtrl
     * @param url
     */
    public SocketAndStream(ClientComCtrl clientComCtrl, String url) {
        m_Socket = null;
        m_DataOutputStream = null;
        m_DataInputStream = null;
        m_ClientComCtrl = clientComCtrl;
        m_URL = url;
    }

    /**
     * Gets DataOutputStream.
     * 
     * @return m_DataOutputStream
     */
    public DataOutputStream getDataOutputStream() {
        return m_DataOutputStream;
    }

    /**
     * Gets DataInputStream.
     * 
     * @return m_DataInputStream
     */
    public DataInputStream getDataInputStream() {
        return m_DataInputStream;
    }

    /**
     * Gets ClientComCtrl.
     * 
     * @return m_ClientComCtrl
     */
    public ClientComCtrl getClientComCtrl() {
        return m_ClientComCtrl;
    }

    /**
     * Gets URL.
     * 
     * @return m_URL
     */
    public String getURL() {
        return m_URL;
    }

    /**
     * Sets URL.
     * 
     * @param url
     */
    public void setURL(String url) {
        m_URL = url;
    }

    /**
     * hasClientComCtrl
     * 
     * @return boolean
     */
    public boolean hasClientComCtrl() {
        if (m_ClientComCtrl == null) {
            return false;
        }
        return true;
    }

    /**
     * close
     * 
     * @throws IOException
     */
    public void close() throws IOException {
        if (m_DataOutputStream != null) {
            m_DataOutputStream.close();
        }

        if (m_DataInputStream != null) {
            m_DataInputStream.close();
        }

        if (m_Socket != null) {
            m_Socket.close();
        }

        if (m_ClientComCtrl != null) {
            m_ClientComCtrl = null;
        }

        return;
    }
}
