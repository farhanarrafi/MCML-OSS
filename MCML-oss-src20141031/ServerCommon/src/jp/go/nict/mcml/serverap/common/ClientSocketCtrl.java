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
import java.util.ArrayList;
import java.util.Date;

import jp.go.nict.mcml.servlet.MCMLException;
import jp.go.nict.mcml.servlet.MCMLStatics;
import jp.go.nict.mcml.servlet.XMLTypeTools;
import jp.go.nict.mcml.xml.XMLProcessor;
import jp.go.nict.mcml.xml.types.ErrorType;
import jp.go.nict.mcml.xml.types.MCMLType;
import jp.go.nict.mcml.xml.types.ResponseType;
import jp.go.nict.mcml.xml.types.ServerType;

/**
 * ClientSocketCtrl class.
 * 
 */
public class ClientSocketCtrl extends ServerApThread {
    // ------------------------------------------
    // private member variable(class field)
    // ------------------------------------------
    private static final XMLProcessor M_XML_PROCESSOR = new XMLProcessor();
    private static int mCounter = 0;

    // ------------------------------------------
    // private member variable(instance field)
    // ------------------------------------------
    private Socket m_ClientSocket;
    private DataInputStream m_InputStream;
    private DataOutputStream m_OutputStream;
    private ServerApCorpusLogger m_CorpusLogger;
    private int m_ClientSocketCtrlID;
    private boolean m_SupportDivision;
    private ReceiveTimer m_ReceiveTimer;

    private long m_RequestLimitThreshold;
    private boolean m_IsEnableRequestTimeout;

    // ------------------------------------------
    // public member function
    // ------------------------------------------
    /**
     * Constructor
     * 
     * @param socket
     * @param corpusLogger
     * @param clientSocketCtrlID
     * @param supportDivision
     * @param receiveTimeoutTime
     * @param requestLimitThreshold
     * @param isEnableRequestTimeout
     */
    public ClientSocketCtrl(Socket socket, ServerApCorpusLogger corpusLogger,
            int clientSocketCtrlID, boolean supportDivision,
            long receiveTimeoutTime, long requestLimitThreshold,
            boolean isEnableRequestTimeout) {
        super("ClientSocketCtrl" + mCounter + "-"
                + socket.getInetAddress().toString() + "-" + socket.getPort());
        m_ClientSocket = socket;
        m_CorpusLogger = corpusLogger;
        m_ClientSocketCtrlID = clientSocketCtrlID;
        m_InputStream = null;
        m_OutputStream = null;
        mCounter++;
        m_SupportDivision = supportDivision;
        if (0 < receiveTimeoutTime) {
            m_ReceiveTimer = new ReceiveTimer(this, receiveTimeoutTime);
        } else {
            m_ReceiveTimer = null;
        }
        m_RequestLimitThreshold = requestLimitThreshold;
        m_IsEnableRequestTimeout = isEnableRequestTimeout;
    }

    /**
     * hasConnection
     * 
     * @return {@code true} when m_ClientSocket is other than {@code null}, otherwise {@code false}
     */
    public synchronized boolean hasConnection() {
        return (m_ClientSocket != null);
    }

    /**
     * processTimeout
     * 
     * @param mcmlData
     */
    public void processTimeout(MCMLData mcmlData) {
        // stop Receive Timer
        m_ReceiveTimer.stop();

        // set last data.
        mcmlData.putBinaryQueue(null);
        mcmlData.setIsErrorOccured(true);
        try {
            disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            ServerApLogger.getInstance().writeError("Receive Timeout.");
            ServerApLogger.getInstance().writeException(e);
        }
    }

    // ------------------------------------------
    // protected member function
    // ------------------------------------------
    @Override
    protected void processMain() throws Exception {
        try {
            Date receiveRequest = new Date();
            // writeLog("[ProcessTime]Receive request");

            // for debug
            writeLog("make stream");
            // for debug

            m_InputStream = new DataInputStream(m_ClientSocket.getInputStream());
            m_OutputStream = new DataOutputStream(
                    m_ClientSocket.getOutputStream());

            // for debug
            writeLog("make stream success");
            // for debug

            // receive request data
            MCMLData mcmlData = new MCMLData(m_SupportDivision);
            receive(mcmlData);

            // for debug
            writeLog("receive mcmldata success");
            // for debug

            Date sendRequest = new Date();
            // writeLog("[ProcessTime]send request");
            writeLog("[ProcessTime]ReceiveRequest->SsendRequest: "
                    + (sendRequest.getTime() - receiveRequest.getTime())
                    + "msec ");
            // is error occurred?(on During division processing)
            boolean result = false;
            if (mcmlData.isErrorOccured()) {
                ServerApLogger.getInstance().writeWarning("Error occured.");
            } else {
                writeLog("wait for processing");

                // wait notification
                result = mcmlData.waitNotification();

                writeLog("process completed");
            }

            // Request time out occurred?
            if (m_IsEnableRequestTimeout
                    && mcmlData.getIsRequestTimeoutOccurred()) {
                ServerApLogger.getInstance().writeWarning(
                        "Engine server request time out");
                throw new MCMLException("Engine server request time out",
                        MCMLException.SYSTEM, MCMLException.MT,
                        MCMLException.ENGINE_SERVER_REQUEST_TIME_OUT);
            }

            if (result || mcmlData.isErrorOccured()) {
                // set OutputMCML.
                m_CorpusLogger
                        .setOutputMCMLInfo(mcmlData, m_ClientSocketCtrlID);

                // set Engine Information.
                m_CorpusLogger.setEngineInfo(mcmlData.getEngineInfo(),
                        m_ClientSocketCtrlID);

                // send response data
                send(mcmlData);

                // wait disconnection
                waitDisconnecion();
            } else { // terminated
                     // no process
            }
        } catch (MCMLException exp) {
            onError(exp);
        } finally {
            // write CorpusLog.
            m_CorpusLogger.writeCorpusLogInfo(m_ClientSocketCtrlID);

            disconnect();

            // Receive Timer shutdown
            if (m_ReceiveTimer != null) {
                m_ReceiveTimer.stop();
                m_ReceiveTimer.shutdown();
            }
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
    private void receive(MCMLData mcmlData) throws IOException, MCMLException {
        // read XML part.
        byte[] dataBody = readFrameData();
        if (dataBody == null) {
            throw new MCMLException("wrong number of frame data",
                    MCMLException.ERROR, MCMLException.COMMON,
                    MCMLException.ABNORMAL_DATA_FORMAT);
        }
        // ------------------------------------------------------------
        // parse XML part from Frame Data.
        // ------------------------------------------------------------
        mcmlData.setMCMLType(parseMCMLType(dataBody));

        boolean isProcessingSpeechData = false;
        try {
            // create BinaryDataList.
            ArrayList<byte[]> binaryDataList = new ArrayList<byte[]>();

            for (int i = 0;; i++) {
                // ReceiveTimer start.
                if (m_ReceiveTimer != null) {
                    m_ReceiveTimer.start(mcmlData);
                }

                // read binaryData.
                byte[] binData = readFrameData();

                // ReceiveTimer stop.
                if (m_ReceiveTimer != null) {
                    m_ReceiveTimer.stop();
                }

                if (i == 0) {
                    // set First Frame ArrivedTime.
                    m_CorpusLogger
                            .setFirstFrameArrivedTime(m_ClientSocketCtrlID);

                    if (m_SupportDivision) {
                        // put Request Data to queue
                        MCMLRequestQueue.getInstance().putData(mcmlData);
                        isProcessingSpeechData = true;
                    }
                }

                if (binData == null) {
                    // set Last Frame ArrivedTime.
                    m_CorpusLogger
                            .setLastFrameArrivedTime(m_ClientSocketCtrlID);

                    // set Input BinaryData.
                    m_CorpusLogger.setWaveData(binaryDataList,
                            m_ClientSocketCtrlID);

                    if (m_SupportDivision) {
                        mcmlData.putBinaryQueue(binData);
                        isProcessingSpeechData = false;
                    } else {
                        // enable check request limit
                        if (m_RequestLimitThreshold > 0) {
                            // over request limit occurred?
                            if (MCMLRequestQueue.getInstance().size() >= m_RequestLimitThreshold) {
                                ServerApLogger.getInstance().writeWarning(
                                        "Engine server busy");
                                throw new MCMLException("Engine server busy",
                                        MCMLException.SYSTEM, MCMLException.MT,
                                        MCMLException.ENGINE_SERVER_BUSY);
                            }
                        }
                        // enable check request timeout
                        if (m_IsEnableRequestTimeout) {
                            // set receive time
                            mcmlData.setReceiveTime();
                        }
                        // add request data to queue
                        MCMLRequestQueue.getInstance().putData(mcmlData);
                    }
                    break;
                }

                if (m_SupportDivision) {
                    mcmlData.putBinaryQueue(binData);
                    writeLog("putBinaryQueue");
                    // is error occurred?
                    if (mcmlData.isErrorOccured()) {
                        isProcessingSpeechData = false;
                        break;
                    }
                } else {
                    mcmlData.addBinaryData(binData);
                }
                binaryDataList.add(binData);
            }
        } finally {
            // add terminate data to queue
            if (isProcessingSpeechData) {
                mcmlData.putBinaryQueue((byte[]) (null));
            }
        }
    }

    private byte[] readFrameData() throws IOException, MCMLException {
        byte[] dataBody = null;
        // receive data size
        int dataBytes = m_InputStream.readInt();
        if (dataBytes < 0) {
            throw new MCMLException("wrong data size", MCMLException.ERROR,
                    MCMLException.COMMON, MCMLException.ABNORMAL_DATA_FORMAT);
        }

        if (dataBytes == 0) {
            // no data body (for last frame)
            return dataBody;
        }

        // receive data body
        dataBody = new byte[dataBytes];
        for (int i = 0; dataBytes > 0;) {
            int readBytes = m_InputStream.read(dataBody, i, dataBytes);
            if (readBytes < 0) {
                throw new MCMLException("DataInputStream.read() was failed",
                        MCMLException.SYSTEM, MCMLException.COMMON,
                        MCMLException.ENGINE_DOWN);
            }
            dataBytes -= readBytes;
            i += readBytes;
        }

        return dataBody;
    }

    private MCMLType parseMCMLType(byte[] dataBody) throws IOException {
        MCMLType mcmlType = null;
        synchronized (M_XML_PROCESSOR) {
            // parse input XML data
            String xmlData = new String(dataBody, MCMLStatics.CHARSET_NAME);
            mcmlType = M_XML_PROCESSOR.parse(xmlData);
            // set InputMCML informations.
            m_CorpusLogger.setInputMCMLInfo(mcmlType, m_ClientSocketCtrlID);
        }
        return mcmlType;
    }

    private void send(MCMLData mcmlData) throws IOException {
        ArrayList<byte[]> frameData = new ArrayList<byte[]>();

        // --------------------------------------------------------------------------------
        // set XML part to frame data
        // --------------------------------------------------------------------------------
        String xmlData = null;
        synchronized (M_XML_PROCESSOR) {
            // generate input XML data
            xmlData = M_XML_PROCESSOR.generate(mcmlData.getMCMLType());
        }
        frameData.add(xmlData.getBytes(MCMLStatics.CHARSET_NAME));

        // --------------------------------------------------------------------------------
        // set binary part to frame data
        // --------------------------------------------------------------------------------
        for (int i = 0; i < mcmlData.getBinaryDataList().size(); i++) {
            frameData.add(mcmlData.getBinaryDataList().get(i));
        }

        // --------------------------------------------------------------------------------
        // send frame data
        // --------------------------------------------------------------------------------
        for (int i = 0; i < frameData.size(); i++) {
            byte[] dataBody = frameData.get(i);

            // send data size
            m_OutputStream.writeInt(dataBody.length);

            // send data body
            m_OutputStream.write(dataBody);
        }

        // send data size 0 (for last frame)
        m_OutputStream.writeInt(0);

        // set ProcessComplete Time.
        m_CorpusLogger.setCompleteTime(m_ClientSocketCtrlID);

        // normal end
        return;
    }

    private void waitDisconnecion() throws IOException {
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

    private void onError(MCMLException exception) throws Exception {
        ServerApLogger.getInstance().writeException(exception);

        // --------------------------------------------------------------------------------
        // MCML
        // --------------------------------------------------------------------------------
        MCMLType mcml = new MCMLType();
        mcml.addVersion("1.0");

        // --------------------------------------------------------------------------------
        // MCML/Error
        // --------------------------------------------------------------------------------
        ErrorType error = XMLTypeTools.generateErrorType(
                exception.getErrorCode(), exception.getExplanation(),
                exception.getService());

        ResponseType response = new ResponseType();
        response.addError(error);
        response.addService(exception.getService());
        response.addProcessOrder("0");

        ServerType server = new ServerType();
        server.addResponse(response);

        mcml.addVersion(MCMLStatics.VERSION);
        mcml.addServer(server);

        // --------------------------------------------------------------------------------
        // send frame data
        // --------------------------------------------------------------------------------
        MCMLData mcmlData = new MCMLData();
        mcmlData.setMCMLType(mcml);
        send(mcmlData);

        // --------------------------------------------------------------------------------
        // wait disconnection
        // --------------------------------------------------------------------------------
        waitDisconnecion();

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
