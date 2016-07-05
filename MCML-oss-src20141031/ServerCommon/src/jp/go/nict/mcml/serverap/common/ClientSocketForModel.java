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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import jp.go.nict.mcml.servlet.MCMLException;

/**
 * ClientSocketForModel class.
 * 
 */
public class ClientSocketForModel extends ServerApThread {
    // ------------------------------------------
    // private member variable(class field)
    // ------------------------------------------
    private static int mCounter = 0;

    // ------------------------------------------
    // private member variable(instance field)
    // ------------------------------------------
    private Socket m_ClientSocket;
    private DataInputStream m_InputStream;
    private DataOutputStream m_OutputStream;
    private DataQueue<FrameData<byte[]>> m_FrameDataQueue;
    private String m_Host;
    private int m_Port;
    private int m_RetryTimes;
    private int m_RetryInterval;
    private ConnectState m_ConnectState;

    // ------------------------------------------
    // public member functions
    // ------------------------------------------
    /**
     * Constructor
     * 
     * @param socket
     * @param frameDataQueue
     */
    public ClientSocketForModel(Socket socket,
            DataQueue<FrameData<byte[]>> frameDataQueue) {
        super("ClientSocketForModel" + mCounter + "-"
                + socket.getInetAddress().toString() + "-" + socket.getPort());
        m_ClientSocket = socket;
        m_InputStream = null;
        m_OutputStream = null;
        m_FrameDataQueue = frameDataQueue;
        m_Host = socket.getInetAddress().toString();
        m_Port = socket.getPort();
        m_RetryTimes = -1;
        m_RetryInterval = -1;
        m_ConnectState = ConnectState.CONNECTED;

        if (mCounter < Integer.MAX_VALUE) {
            mCounter++;
        } else {
            mCounter = 0;
        }
    }

    /**
     * Default constructor
     */
    public ClientSocketForModel() {
        super("ClientSocketForModel" + mCounter);
        m_ClientSocket = null;
        m_InputStream = null;
        m_OutputStream = null;
        m_FrameDataQueue = null;
        m_Host = "";
        m_Port = -1;
        m_RetryTimes = -1;
        m_RetryInterval = -1;
        m_ConnectState = ConnectState.FAILED;

        if (mCounter < Integer.MAX_VALUE) {
            mCounter++;
        } else {
            mCounter = 0;
        }
    }

    /**
     * hasConnection
     * 
     * @return boolean
     */
    public synchronized boolean hasConnection() {
        return (m_ClientSocket != null);
    }

    /**
     * startConnecting
     * 
     * @param host
     * @param port
     * @param connectRetryTimes
     * @param connectRetryInterval
     * @param portName
     */
    public void startConnecting(String host, int port, int connectRetryTimes,
            int connectRetryInterval, String portName) {
        m_Host = host;
        m_Port = port;
        m_RetryTimes = connectRetryTimes;
        m_RetryInterval = connectRetryInterval;
        start();
        m_ConnectState = ConnectState.CONNECTING;
    }

    /**
     * Gets ConnectState.
     * 
     * @return ConnectState
     */
    public synchronized ConnectState getConnectState() {
        // check connected
        return m_ConnectState;
    }

    /**
     * connect
     * 
     * @param host
     * @param port
     * @param connectRetryTimes
     * @param connectRetryInterval
     * @return boolean
     * @throws IOException
     * @throws InterruptedException
     */
    public boolean connect(String host, int port, int connectRetryTimes,
            int connectRetryInterval) throws IOException, InterruptedException {
        boolean retVal = false;

        writeLog("connect started");

        // Connect And Retry
        for (int i = 0; i < connectRetryTimes; i++) {
            try {
                m_ClientSocket = new Socket(host, port);

                if (m_ClientSocket != null) {
                    // succeeded connecting.
                    retVal = true;
                    String connectMessage = "Connect to " + host + " success.";
                    System.out.println(connectMessage);
                    break;
                }

                // connecting failed.
            } catch (UnknownHostException e) {
                ServerApLogger.getInstance().writeTrace(e.getMessage());
            } catch (IOException e) {
                ServerApLogger.getInstance().writeTrace(e.getMessage());
            }
            String retryMessage = "Connect to " + host + " retry:"
                    + String.valueOf(i);
            ServerApLogger.getInstance().writeTrace(retryMessage);
            System.out.println(retryMessage);

            sleep(connectRetryInterval);
        }

        // check connected the Socket.
        if (m_ClientSocket != null) {
            // stream open.
            m_InputStream = new DataInputStream(m_ClientSocket.getInputStream());
            m_OutputStream = new DataOutputStream(
                    m_ClientSocket.getOutputStream());
        }

        if (retVal) {
            m_ConnectState = ConnectState.CONNECTED;
            writeLog("connect completed");
        } else {
            m_ConnectState = ConnectState.FAILED;
            writeLog("connect failed");
        }

        return retVal;
    }

    /**
     * send
     * 
     * @param frameData
     * @throws IOException
     */
    public void send(FrameData<byte[]> frameData) throws IOException {
        // --------------------------------------------------------------------------------
        // send frame data
        // --------------------------------------------------------------------------------
        for (int i = 0; i < frameData.size(); i++) {
            byte[] dataBody = frameData.getFrameDataAt(i);

            // send data size
            m_OutputStream.writeInt(dataBody.length);

            // send data body
            m_OutputStream.write(dataBody);
        }

        // send data size 0 (for last frame)
        m_OutputStream.writeInt(0);

        // normal end
        return;
    }

    /**
     * waitDisconnecion
     * 
     * @throws IOException
     */
    public void waitDisconnecion() throws IOException {
        writeLog("wait for disconnection");

        try {
            while (true) {
                m_InputStream.readInt();
            }
        } catch (EOFException exp) {
            writeLog("disconnected");
        } catch (SocketException exp) {
            writeLog("closed socket");
        }

        // normal end
        return;
    }

    // ------------------------------------------
    // protected member functions
    // ------------------------------------------
    @Override
    protected void processMain() throws Exception {
        try {
            if (m_ClientSocket != null) {
                // *** receive process. ***//
                m_InputStream = new DataInputStream(
                        m_ClientSocket.getInputStream());
                m_OutputStream = new DataOutputStream(
                        m_ClientSocket.getOutputStream());

                // receive request data
                FrameData<byte[]> frameData = new FrameData<byte[]>();
                receive(frameData);

                // add request data to queue
                m_FrameDataQueue.putData(frameData);

                writeLog("wait for processing");

                // wait notification
                frameData.waitNotification();

                writeLog("process completed");
            } else {
                // *** connect process. ***//
                writeLog("Connect and retry started");
                connect(m_Host, m_Port, m_RetryTimes, m_RetryInterval);
                writeLog("Connect and retry end");
            }
        } catch (MCMLException exp) {
            writeLog("ClientSocketForModel::processMain() error occured.");
            ServerApLogger.getInstance().writeException(exp);
        } finally {
            disconnect();
        }

        // normal end
        return;
    }

    @Override
    protected void processTermination() throws Exception {
        // disconnect to MCML server
        disconnect();

        // interrupt for waiting on MCMLData
        interrupt();

        // wait for thread termination
        join();

        // normal end
        return;
    }

    // ------------------------------------------
    // private member function
    // ------------------------------------------
    private void receive(FrameData<byte[]> frameData) throws IOException,
            MCMLException {
        // --------------------------------------------------------------------------------
        // receive frame data
        // --------------------------------------------------------------------------------
        while (true) {
            // receive data size
            int dataBytes = m_InputStream.readInt();
            if (dataBytes < 0) {
                throw new MCMLException("wrong data size", MCMLException.ERROR,
                        MCMLException.COMMON,
                        MCMLException.ABNORMAL_DATA_FORMAT);
            }

            if (dataBytes == 0) {
                // no data body (for last frame)
                break;
            }

            // receive data body
            byte[] dataBody = new byte[dataBytes];
            for (int i = 0; dataBytes > 0;) {
                int readBytes = m_InputStream.read(dataBody, i, dataBytes);
                if (readBytes < 0) {
                    throw new MCMLException(
                            "DataInputStream.read() was failed",
                            MCMLException.SYSTEM, MCMLException.COMMON,
                            MCMLException.ENGINE_DOWN);
                }
                dataBytes -= readBytes;
                i += readBytes;
            }

            // add data
            frameData.addFrameData(dataBody);
        }

        if (frameData.size() <= 0) {
            throw new MCMLException("wrong number of frame data",
                    MCMLException.ERROR, MCMLException.COMMON,
                    MCMLException.ABNORMAL_DATA_FORMAT);
        }

        // normal end
        return;
    }

    private synchronized void disconnect() throws IOException {
        // close stream
        if (m_OutputStream != null) {
            m_OutputStream.close();
            m_OutputStream = null;
        }
        if (m_InputStream != null) {
            m_InputStream.close();
            m_InputStream = null;
        }

        // close socket
        if (m_ClientSocket != null) {
            m_ClientSocket.close();
            m_ClientSocket = null;
        }

        // normal end
        return;
    }
}
