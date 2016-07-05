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

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import jp.go.nict.mcml.serverap.common.ConnectState;
import jp.go.nict.mcml.serverap.common.ServerApLogger;
import jp.go.nict.mcml.serverap.common.ServerApThread;
import jp.go.nict.mcml.servlet.MCMLException;

/**
 * TTSClientSocketForEngine class.
 *
 *
 */
public class TTSClientSocketForEngine extends ServerApThread {

    // ------------------------------------------
    // private member variable(instance field)
    // ------------------------------------------
    private Socket m_ClientSocket;
    private DataInputStream m_InputStream;
    private DataOutputStream m_OutputStream;

    private String m_IPAddress; // Connect Target IP Address
    private int m_Port; // Connect Port
    private String m_ClientHost; // Connect Client Host
    private int m_ClientPort; // Connect Client Port
    private int m_RetryTimes; // Connect Retry Times
    private int m_RetryInterval; // Connect Retry Interval
    private String m_PortName; // This Socket to use and User(engine)Name.
                               // "xxxengne.xxxport"
    private int m_ServerType = 0;
    private ConnectState m_ConnectState;

    // ------------------------------------------
    // public member function
    // ------------------------------------------
    /**
     * Constructor
     *
     * @param serverType
     *           Server type.
     */
    public TTSClientSocketForEngine(int serverType) {
        super("ClientSocketForEngine");
        m_ClientSocket = null;
        m_InputStream = null;
        m_OutputStream = null;
        m_IPAddress = "";
        m_Port = -1;
        m_ClientHost = "";
        m_ClientPort = -1;
        m_RetryTimes = -1;
        m_PortName = "";
        m_ServerType = serverType;
        m_ConnectState = ConnectState.FAILED;
    }

    /**
     * Constructor
     *
     * @param serverType
     *           Server type.
     * @param socket
     *            Socket
     * @throws IOException
     *             If failed in input stream acquisition, and in output stream acquisition
     */
    public TTSClientSocketForEngine(int serverType, Socket socket)
            throws IOException {
        super("ClientSocketForEngine");
        m_ClientSocket = socket;
        m_InputStream = new DataInputStream(m_ClientSocket.getInputStream());
        m_OutputStream = new DataOutputStream(m_ClientSocket.getOutputStream());
        m_ServerType = serverType;
        m_ConnectState = ConnectState.CONNECTED;
    }

    /**
     * Connect Start
     *
     * @param ipAddress
     *            IP address
     * @param port
     *            Port number
     * @param clientHost
     * @param clientPort
     * @param connectRetryTimes
     *            Connection retry time
     * @param connectRetryInterval
     *             Connection retry interval
     * @param portName
     *            Port name
     */
    public void startConnecting(String ipAddress, int port, String clientHost,
            int clientPort, int connectRetryTimes, int connectRetryInterval,
            String portName) {
        m_IPAddress = ipAddress;
        m_Port = port;
        m_ClientHost = clientHost;
        m_ClientPort = clientPort;
        m_RetryTimes = connectRetryTimes;
        m_RetryInterval = connectRetryInterval;
        start();
        m_ConnectState = ConnectState.CONNECTING;
        m_PortName = portName;
    }

    /**
     * Connected Check
     *
     * @return  Connection state
     */
    public synchronized ConnectState getConnectState() {
        // check connected
        return m_ConnectState;
    }

    /**
     * send Data(for ASR server)
     *
     * @param data
     *            Byte data
     * @throws IOException
     *             When output stream write failed.
     */
    public void sendData(byte[] data) throws IOException {
        m_OutputStream.write(data);
    }

    /**
     * receive Data(for SS Server)
     *
     * @param dataBytes
     *            Byte data
     * @return Byte arrangement data
     * @throws IOException
     *              When input stream read failed, output stream write failed
     * @throws MCMLException
     *              When parameter byte data is smaller than 0, input stream read data is smaller than 0
     *              When output stream byte array is under 0
     */
    public byte[] receiveData(int dataBytes) throws IOException, MCMLException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writeLog("receiveData: " + this.m_ConnectState);
        // ----------------------------------------------------------
        // receive frame data
        // ----------------------------------------------------------
        while (true) {

            if (dataBytes < 0) {
                throw new MCMLException("wrong data size", MCMLException.ERROR,
                        m_ServerType, MCMLException.ABNORMAL_DATA_FORMAT);
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
                    writeLog("receiveData readBytes < 0: "
                            + this.m_ConnectState);
                    throw new MCMLException(
                            "DataInputStream.read() was failed",
                            MCMLException.SYSTEM, m_ServerType,
                            MCMLException.ENGINE_CLOSED);
                }
                dataBytes -= readBytes;
                i += readBytes;
            }

            // add data
            baos.write(dataBody);
        }

        byte[] retVal = baos.toByteArray();
        baos.close();

        if (retVal.length <= 0) {
            throw new MCMLException("wrong number of frame data",
                    MCMLException.ERROR, m_ServerType,
                    MCMLException.OTHER_ERROR);
        }
        return retVal;
    }

    /**
     * disconnect Socket and Stream
     *
     * @throws IOException
     *              When output stream close failed, input stream close failed, client socket close failed
     */
    public void disconnect() throws IOException {
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

    /**
     * get InputStream (for Character Code convert)
     *
     * @return  Input stream
     */
    public InputStream getInputStream() {
        return m_InputStream;

    }

    /**
     * get OutputStream (for Character Code convert)
     *
     * @return Output stream
     */
    public OutputStream getOutputStream() {
        return m_OutputStream;
    }

    /**
     * Evaluates connection.
     *
     * @param ipAddress
     *              IP address
     * @param port
     *              Port number
     * @param clientHost
     * @param clientPort
     * @param connectRetryTimes
     *              Connection retry time
     * @param connectRetryInterval
     *              Connection retry interval
     * @param portName
     *              Port name
     * @return {@code false}  connection connected to {@code true} failed.
     * @throws IOException
     *             When client socketinput stream acquisition failed, and client socket output stream acquisition failed
     * @throws InterruptedException
     *             When sleep failed
     */
    public boolean connect(String ipAddress, int port, String clientHost,
            int clientPort, int connectRetryTimes, int connectRetryInterval,
            String portName) throws IOException, InterruptedException {
        boolean retVal = false;

        writeLog("connect started");

        // Connect And Retry
        for (int i = 0; i < connectRetryTimes; i++) {
            try {
                if (clientHost != null && !clientHost.isEmpty()
                        && clientPort > 0) {
                    m_ClientSocket = new Socket(ipAddress, port,
                            InetAddress.getByName(clientHost), clientPort);
                } else {
                    m_ClientSocket = new Socket(ipAddress, port);
                }

                if (m_ClientSocket != null) {
                    // succeeded connecting.
                    retVal = true;
                    String connectMessage = "Connect to " + portName
                            + " success.";
                    System.out.println(connectMessage);
                    break;
                }

                // connecting failed.
            } catch (UnknownHostException e) {
                ServerApLogger.getInstance().writeTrace(e.getMessage());
            } catch (IOException e) {
                ServerApLogger.getInstance().writeTrace(e.getMessage());
            }
            String retryMessage = "Connect to " + portName + " retry:"
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

    // ------------------------------------------
    // protected member function
    // ------------------------------------------
    /**
     * Connect Process
     */
    @Override
    protected void processMain() throws Exception {
        writeLog("Connect and retry started");

        connect(m_IPAddress, m_Port, m_ClientHost, m_ClientPort, m_RetryTimes,
                m_RetryInterval, m_PortName);

        writeLog("Connect and retry end");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void processTermination() throws Exception {
        // interrupt for waiting on MCMLRequestQueue
        interrupt();

        // wait for thread termination
        join();

        // disconnect Stream
        disconnect();

        // normal end
        return;
    }

}
