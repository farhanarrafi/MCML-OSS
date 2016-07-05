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

package jp.go.nict.mcml.servlet.control.dispatcher.connector;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpSession;

import jp.go.nict.mcml.com.client.ResponseData;
import jp.go.nict.mcml.engine.socket.SocketAndStream;
import jp.go.nict.mcml.exception.MCMLException;
import jp.go.nict.mcml.xml.MCMLStatics;
import jp.go.nict.mcml.xml.XMLTypeTools;

import org.apache.log4j.Logger;

import com.MCML.MCMLDoc;

/**
 * ConnectorBySocketAndStream class.
 * 
 */
public class ConnectorBySocketAndStream extends Connector {
    private static final Logger LOG = Logger
            .getLogger(ConnectorBySocketAndStream.class.getName());
    // ------------------------------------------
    // private member variable
    // ------------------------------------------
    private SocketAndStream socketAndStream; // socket and stream
    private String host; // request server host name
    private int port; // request server port number

    // ------------------------------------------
    // public member function
    // ------------------------------------------
    /**
     * constructor
     * 
     * @param listId
     * @param host
     * @param port
     * @param coefficientASRTimeout
     */
    public ConnectorBySocketAndStream(int listId, String host, int port,
            float coefficientASRTimeout) {
        super(listId, coefficientASRTimeout);
        this.socketAndStream = null;
        this.host = host;
        this.port = port;
    }

    /** send request to server and receive response from server */
    @Override
    public ResponseData request(MCMLDoc mcmlDoc,
            ArrayList<byte[]> binariesList, HttpSession session)
            throws MCMLException, Exception {
        String xmlData = null;
        try {
            if (mcmlDoc != null) {
                xmlData = mcmlDoc.saveToString(true);
            }
            if (socketAndStream == null) {
                socketAndStream = new SocketAndStream(host, port);
            }
        } catch (IOException e) {
            LOG.error(e.getMessage());
            throw new MCMLException(MCMLException.ERROR, MCMLException.COMMON,
                    MCMLException.XML_CREATION_ERROR);
        }
        // timer start
        ConnectorTimer connectorTimer = new ConnectorTimer(this);
        isTimeoutTimerTask = false;
        connectorTimer.start(super.getTimeoutMilliSeconds(xmlData, mcmlDoc,
                binariesList));

        boolean isCloseSocketAndStream = true;
        ResponseData responseData = null;
        try {
            byte[] dataBody = null;
            // Request is empty
            if ((xmlData == null || xmlData.isEmpty())
                    && (binariesList == null || binariesList.isEmpty())) {
                // send terminate
                responseData = sendTerminate(session);
            } else {
                // Request has XML Data
                if (xmlData != null && !xmlData.isEmpty()) {
                    String service = XMLTypeTools.getService(mcmlDoc);
                    // send XML Part
                    dataBody = xmlData.getBytes(MCMLStatics.CHARSET_NAME);
                    sendFrameData(socketAndStream.getDataOutputStream(),
                            dataBody);
                    if (service.equals(MCMLStatics.SERVICE_MT)
                            || service.equals(MCMLStatics.SERVICE_TTS)) {
                        // send terminate
                        responseData = sendTerminate(session);
                    } else {
                        if (binariesList == null || binariesList.isEmpty()) {
                            isCloseSocketAndStream = false; // keep connection
                        }
                    }
                }

                LOG.info("[" + session.getId() + "]\n URL: " + host + ":"
                        + port + "\n Send: " + xmlData);

                // Request has BinaryData
                if (binariesList != null && !binariesList.isEmpty()) {
                    // send binary part
                    for (int i = 0; i < binariesList.size(); i++) {
                        dataBody = binariesList.get(i);
                        sendFrameData(socketAndStream.getDataOutputStream(),
                                dataBody);
                    }
                    if (xmlData != null && !xmlData.isEmpty()) {
                        // send terminate
                        responseData = sendTerminate(session);
                    } else {
                        isCloseSocketAndStream = false; // keep connection
                    }
                }
            }
            if (responseData == null) {
                responseData = new ResponseData();
                MCMLDoc responseMCMLDoc = MCMLDoc.createDocument();
                responseMCMLDoc.MCML.append();
                String responseXML = responseMCMLDoc.saveToString(true);
                responseData.setXML(responseXML);
            }
            // reset timeout counter
            super.resetTimeoutCounter();
        } catch (Exception e) {
            // timer task time out
            if (isTimeoutTimerTask) {
                throw new MCMLException(MCMLException.ERROR,
                        MCMLException.COMMON,
                        MCMLException.PIVOT_REQUEST_TIME_OUT);

                // other error
            } else {
                throw new MCMLException(MCMLException.ERROR,
                        MCMLException.COMMON, MCMLException.PIVOT_REQUEST_ERROR);
            }
        } finally {
            // stop timer
            connectorTimer.stop();

            // close connection
            if (isCloseSocketAndStream) {
                closeConnection();
            }
        }

        return responseData;
    }

    // close connection to server
    @Override
    public void closeConnection() {
        if (socketAndStream != null) {
            try {
                socketAndStream.close();
                socketAndStream = null;
            } catch (IOException e) {
                LOG.debug(e.getMessage(), e);
                // when catch exception, nothing process
            }
        }
    }

    // ------------------------------------------
    // private member function
    // ------------------------------------------
    // send frame data
    private void sendFrameData(DataOutputStream outputStream, byte[] dataBody)
            throws IOException {
        if (dataBody == null || dataBody.length == 0) {
            // send data size (for last frame)
            outputStream.writeInt(0);
        } else {
            // send data size
            outputStream.writeInt(dataBody.length);

            // send data body
            outputStream.write(dataBody);
        }

        // normal end
        return;
    }

    // receive frame data
    private byte[] receiveFrameData(DataInputStream inputStream)
            throws IOException, MCMLException {
        // receive data size
        int dataBytes = inputStream.readInt();
        if (dataBytes < 0) {
            throw new MCMLException(MCMLException.ERROR, MCMLException.COMMON,
                    MCMLException.FRAME_DATA_EMPTY);
        }

        if (dataBytes == 0) {
            // no data body (for last frame)
            return null;
        }

        // receive data body
        byte[] dataBody = new byte[dataBytes];
        for (int i = 0; dataBytes > 0;) {
            int readBytes = inputStream.read(dataBody, i, dataBytes);
            if (readBytes < 0) {
                throw new MCMLException(MCMLException.ERROR,
                        MCMLException.COMMON,
                        MCMLException.FRAME_DATA_BODY_EMPTY);
            }
            dataBytes -= readBytes;
            i += readBytes;
        }

        // normal end
        return dataBody;
    }

    private ResponseData sendTerminate(HttpSession session) throws IOException,
            MCMLException {
        ResponseData responseData = null;
        byte[] dataBody = null;
        String xmlData;

        // send terminate
        sendFrameData(socketAndStream.getDataOutputStream(), null);

        int threadCount = (Integer) session.getAttribute("ThreadCount");
        String[] arrayDataEnd = (String[]) session.getAttribute("DataEnd");
        Date[] arrayRequestSendTime = (Date[]) session
                .getAttribute("RequestSendTime");

        arrayRequestSendTime[threadCount] = new Date();

        session.setAttribute("RequestSendTime", arrayRequestSendTime);

        // receive XML part
        dataBody = receiveFrameData(socketAndStream.getDataInputStream());

        Date[] arrayResponseReceiveTime = (Date[]) session
                .getAttribute("ResponseReceiveTime");

        arrayResponseReceiveTime[threadCount] = new Date();

        session.setAttribute("ResponseReceiveTime", arrayResponseReceiveTime);

        arrayDataEnd[threadCount] = "yes";
        session.setAttribute("DataEnd", arrayDataEnd);

        if (arrayDataEnd[threadCount] != null
                && arrayDataEnd[threadCount].equals("yes")) {
            SimpleDateFormat sdf = new SimpleDateFormat(
                    "yyyy/MM/dd HH:mm:ss.SSS");
            Date requestProcessStartTime = (Date) session
                    .getAttribute("ProcessingTime");
            LOG.debug("[" + session.getId() + "] RequestProcessStartTime:"
                    + sdf.format(requestProcessStartTime));
            LOG.debug("[" + session.getId() + "] RequestSendTime: "
                    + sdf.format(arrayRequestSendTime[threadCount]));

            String service = "";
            if (session.getAttribute("Service") != null) {
                service = (String) session.getAttribute("Service");

            }
            String printText = "";
            printText = printText + "[" + session.getId() + "] ";
            printText = printText + "[ProcessTime][" + service
                    + "]ClientRequest->SendEngineSide: ";
            printText = printText
                    + (arrayRequestSendTime[threadCount].getTime() - requestProcessStartTime
                            .getTime()) + "msec ";
            LOG.info(printText);
        }

        xmlData = new String(dataBody, MCMLStatics.CHARSET_NAME);

        LOG.info("[" + session.getId() + "]\n URL: " + host + ":" + port
                + "\n Receive: " + xmlData);

        // create output memory
        ByteArrayOutputStream outputBinary = new ByteArrayOutputStream();

        // receive binary part
        while ((dataBody = receiveFrameData(socketAndStream
                .getDataInputStream())) != null) {
            outputBinary.write(dataBody);
        }
        ArrayList<byte[]> outputBinaryList = null;
        if (outputBinary.size() > 0) {
            outputBinaryList = new ArrayList<byte[]>();
            outputBinaryList.add(outputBinary.toByteArray());
        }
        outputBinary.close();

        // create responceData;
        responseData = new ResponseData();
        responseData.setXML(xmlData);
        if (outputBinaryList != null) {
            responseData.setBinaryList(outputBinaryList);
        }
        return responseData;
    }
}
