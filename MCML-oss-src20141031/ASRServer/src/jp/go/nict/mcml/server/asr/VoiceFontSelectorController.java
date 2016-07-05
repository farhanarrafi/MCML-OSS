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

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

import jp.go.nict.mcml.serverap.common.ConnectState;
import jp.go.nict.mcml.serverap.common.ServerApLogger;
import jp.go.nict.mcml.serverap.common.ServerApThread;

/**
 * VoiceFontSelectorControllerクラスです。
 *
 */
public class VoiceFontSelectorController extends ServerApThread {
    // ------------------------------------------
    // private member variable(instance field)
    // ------------------------------------------
    private Socket m_ClientSocket;
    private DataInputStream m_InputStream;
    private DataOutputStream m_OutputStream;

    private String m_IPAddress; // Connect Target IP Address
    private int m_Port; // Connect Port
    private int m_RetryTimes; // Connect Retry Times
    private int m_RetryInterval; // Connect Retry Interval
    private DataBuffer m_DataBuffer;
    private long m_TimeoutMilliSec;

    private String m_VoiceFontName;

    private boolean m_IsEnd;
    private Object m_EndFlagLock;
    private ConnectState m_ConnectState;
    private Object m_ConnectStateLock;
    private boolean m_DoSelectOn;

    /**
     * コンストラクタ
     *
     * @param name
     * @param timeoutMilliSec
     */
    public VoiceFontSelectorController(String name, long timeoutMilliSec) {
        super(name);
        m_ClientSocket = null;
        m_InputStream = null;
        m_OutputStream = null;
        m_IPAddress = "";
        m_Port = -1;
        m_RetryTimes = -1;
        m_ConnectState = ConnectState.FAILED;
        m_DataBuffer = new DataBuffer();
        m_VoiceFontName = "";
        m_IsEnd = false;
        m_EndFlagLock = new Object();
        m_ConnectStateLock = new Object();
        m_TimeoutMilliSec = timeoutMilliSec;
        m_DoSelectOn = false;
    }

    /**
     * connect start
     *
     * @param ipAddress
     * @param port
     * @param connectRetryTimes
     * @param connectRetryInterval
     */
    public void startConnecting(String ipAddress, int port,
            int connectRetryTimes, int connectRetryInterval) {
        if ((ipAddress == null || ipAddress.isEmpty()) || (port < 0)) {
            writeLog("IP Address or Port is wrong(not use VoiceFontSelector)");
            return;
        }
        setEnd(false);
        m_IPAddress = ipAddress;
        m_Port = port;
        m_RetryTimes = connectRetryTimes;
        m_RetryInterval = connectRetryInterval;
        start();
        m_ConnectState = ConnectState.CONNECTING;
    }

    /**
     * disconnect Socket and Stream
     *
     * @throws IOException
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
     * is connected check
     *
     * @return ConnectState
     */
    public ConnectState getConnectState() {
        synchronized (m_ConnectStateLock) {
            // check connected
            return m_ConnectState;
        }
    }

    /**
     * SourceEndianを設定します。
     *
     * @param isBigEndian
     */
    public void setSourceEndian(boolean isBigEndian) {
        if (isBigEndian) {
            m_DataBuffer
                    .setOrder(ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN);
        } else {
            m_DataBuffer.setOrder(ByteOrder.LITTLE_ENDIAN,
                    ByteOrder.LITTLE_ENDIAN);
        }
    }

    /**
     * IsVoiceFontSelectOnを設定します。
     *
     * @param doSelectOn
     */
    public void setIsVoiceFontSelectOn(boolean doSelectOn) {
        m_DoSelectOn = doSelectOn;
    }

    /**
     * select VoiceFont
     *
     * @param rawData
     */
    public void selectVoiceFont(byte[] rawData) {
        if (getConnectState() != ConnectState.CONNECTED) {
            return;
        }

        if (!m_DoSelectOn) {
            return;
        }

        try {
            if (rawData != null) {
                // buffering data
                m_DataBuffer.setData(rawData);
                return;
            }

            // clear result(before time's result)
            m_VoiceFontName = "";

            // get Data
            byte[] data = m_DataBuffer.getData();
            m_DataBuffer.clear();

            // send data
            // convert endian
            int size = Integer.reverseBytes(data.length);

            // send data size
            m_OutputStream.writeInt(size);

            // send data
            m_OutputStream.write(data);

            // send last frame
            m_OutputStream.writeInt(0);
            m_OutputStream.flush();
        } catch (IOException e) {
            ServerApLogger.getInstance().writeException(e);
            ServerApLogger.getInstance().writeError(
                    super.getName() + ":selectVoiceFont() failed");
        }

        // normal end
        return;
    }

    /**
     * get VoiceFont
     *
     * @return result
     * @throws InterruptedException
     */
    public String getVoiceFontName() throws InterruptedException {
        if (getConnectState() != ConnectState.CONNECTED) {
            return "";
        }

        if (!m_DoSelectOn) {
            return "";
        }

        long waitTime = m_TimeoutMilliSec;
        while (!isEnd()) {
            if (!m_VoiceFontName.isEmpty()) {
                break;
            }

            sleep(100);
            if (0 < m_TimeoutMilliSec) {
                waitTime -= 100;
                if (waitTime < 0) {
                    ServerApLogger.getInstance().writeWarning(
                            "timeout VoiceFontSelector");
                    break;
                }
            }
        }

        String result = "";
        String[] voicefont = m_VoiceFontName.split(":");
        if (voicefont.length == 0) {
            // result String is wrong
            ServerApLogger.getInstance().writeWarning(
                    "wrong VoiceFontSelector's result:" + m_VoiceFontName);
        } else {
            if (voicefont[0].equalsIgnoreCase("SUCCESS")) {
                result = new String(voicefont[1]);
            } else if (voicefont[0].equalsIgnoreCase("ERROR")) {
                ServerApLogger.getInstance().writeWarning(m_VoiceFontName);
                if (3 <= voicefont.length && !voicefont[2].isEmpty()) {
                    result = new String(voicefont[2]);
                }
            } else {
                // result String is wrong
                ServerApLogger.getInstance().writeWarning(
                        "wrong VoiceFontSelector's result:" + m_VoiceFontName);
            }
        }

        return result;
    }

    // ------------------------------------------
    // protected member functions
    // ------------------------------------------
    // Connect Process
    @Override
    protected void processMain() throws Exception {
        writeLog("VoiceFontSelector::processMain() start");
        while (!isEnd()) {
            try {
                writeLog("Connect and retry started:VoiceFontSelector");
                if (!connect(m_IPAddress, m_Port, m_RetryTimes,
                        m_RetryInterval, super.getName())) {
                    ServerApLogger.getInstance().writeError(
                            "faild in connect(VoiceFontSelector)");
                    break;
                }
                writeLog("Connect and retry end:VoiceFontSelector");

                // start receive
                while (true) {
                    m_VoiceFontName = receiveResult();
                    if (m_VoiceFontName.isEmpty()) {
                        writeLog("VoiceFontName is empty");
                        break;
                    }
                }
                setConnectState(ConnectState.CONNECTING);
                disconnect();
            } catch (Exception e) {
                ServerApLogger.getInstance().writeException(e);
                ServerApLogger.getInstance().writeError(
                        "Error VoiceFontSelector's I/O");
            }
        }
        writeLog("VoiceFontSelector::processMain() end");
    }

    @Override
    protected void processTermination() throws Exception {
        writeLog("VoiceFontSelector::processTermination() start");
        // set end flag
        setEnd(true);

        // disconnect Stream
        disconnect();

        // interrupt for waiting
        interrupt();

        // wait for thread termination
        join();

        m_VoiceFontName = "";

        // normal end
        writeLog("VoiceFontSelector::processTermination() end");
        return;
    }

    // ------------------------------------------
    // private member functions
    // ------------------------------------------
    private boolean connect(String ipAddress, int port, int connectRetryTimes,
            int connectRetryInterval, String portName) throws IOException,
            InterruptedException {
        boolean retVal = false;

        writeLog("connect started");

        // Connect And Retry
        for (int i = 0; i < connectRetryTimes; i++) {
            try {
                m_ClientSocket = new Socket(ipAddress, port);

                if (m_ClientSocket != null) {
                    // succeeded connecting.
                    retVal = true;
                    String connectMessage = "Connect to " + portName
                            + ".voicefontselector" + " success.";
                    System.out.println(connectMessage);
                    break;
                }

                // connecting failed.
            } catch (UnknownHostException e) {
                ServerApLogger.getInstance().writeTrace(e.getMessage());
            } catch (IOException e) {
                ServerApLogger.getInstance().writeTrace(e.getMessage());
            }
            String retryMessage = "Connect to " + portName
                    + ".voicefontselector" + " retry:" + String.valueOf(i);
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
            setConnectState(ConnectState.CONNECTED);
            writeLog("connect completed");
        } else {
            setConnectState(ConnectState.FAILED);
            writeLog("connect failed");
        }

        return retVal;
    }

    private String receiveResult() {
        byte[] retVal = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // ----------------------------------------------------------
            // receive frame data
            // ----------------------------------------------------------
            while (true) {
                // receive data size
                int dataBytes = m_InputStream.readInt();
                dataBytes = Integer.reverseBytes(dataBytes);
                if (dataBytes < 0) {
                    ServerApLogger.getInstance().writeError(
                            super.getName()
                                    + ":wrong data size(VoiceFontSelector)");
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
                        ServerApLogger
                                .getInstance()
                                .writeError(
                                        super.getName()
                                                + ":DataInputStream.read() was failed(VoiceFontSelector)");
                    }
                    dataBytes -= readBytes;
                    i += readBytes;
                }

                // add data
                baos.write(dataBody);
            }

            retVal = baos.toByteArray();
            baos.close();

            if (retVal.length <= 0) {
                ServerApLogger
                        .getInstance()
                        .writeError(
                                super.getName()
                                        + ":wrong number of frame data(VoiceFontSelector)");
            }
        } catch (IOException e) {
            ServerApLogger.getInstance().writeException(e);
            ServerApLogger.getInstance().writeError(
                    super.getName() + ":receiveResult() failed");
        }

        String result = "";
        if (retVal != null) {
            result = new String(retVal);
        }

        return result;
    }

    private void setConnectState(ConnectState state) {
        synchronized (m_ConnectStateLock) {
            this.m_ConnectState = state;
        }
    }

    private boolean isEnd() {
        synchronized (m_EndFlagLock) {
            return m_IsEnd;
        }
    }

    private void setEnd(boolean isEnd) {
        synchronized (m_EndFlagLock) {
            this.m_IsEnd = isEnd;
        }
    }
}
