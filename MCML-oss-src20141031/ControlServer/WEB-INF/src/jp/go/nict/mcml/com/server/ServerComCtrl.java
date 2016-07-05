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

package jp.go.nict.mcml.com.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;

import jp.go.nict.mcml.com.ComCtrl;

import org.apache.log4j.Logger;

/**
 * ServerComCtrl class.
 * 
 * @version 4.0
 * @since 20120921
 */
public class ServerComCtrl extends ComCtrl {
    private static final Logger LOG = Logger.getLogger(ServerComCtrl.class
            .getName());

    // ------------------------------------------
    // public member function
    // ------------------------------------------

    /**
     * constructor
     */
    public ServerComCtrl() {
    }

    // Constructor with encryption control.
    public ServerComCtrl(boolean encryption) {
        super(encryption);
    }

    /**
     * receive request MIME data
     * 
     * @param stream
     * @param url
     * @param session
     * @return RequestData
     * @throws MessagingException
     * @throws IOException
     */
    public RequestData receiveRequesData(InputStream stream, String url,
            HttpSession session) throws MessagingException, IOException {
        // data receive
        RequestData retVal = new RequestData();
        super.receive(stream, retVal, url, session);

        // success
        return retVal;
    }

    /**
     * send response MIME data (multi data version)
     * 
     * @param stream
     * @param xmlData
     * @param binaryDataList
     * @param url
     * @param session
     * @throws MessagingException
     */
    public void sendResponseData(OutputStream stream, String xmlData,
            ArrayList<byte[]> binaryDataList, String url, HttpSession session)
            throws MessagingException {
        // data send
        try {
            super.send(stream, xmlData, binaryDataList, url, session);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }

        if (session.getAttribute("ThreadCount") != null) {
            int threadCount = (Integer) session.getAttribute("ThreadCount");
            String[] arrayDataEnd = (String[]) session.getAttribute("DataEnd");
            session.setAttribute("ResponseSendTime", new Date());

            if (arrayDataEnd.length > threadCount
                    && arrayDataEnd[threadCount] != null
                    && arrayDataEnd[threadCount].equals("yes")) {
                SimpleDateFormat sdf = new SimpleDateFormat(
                        "yyyy/MM/dd HH:mm:ss.SSS");
                Date requestProcessStartTime = (Date) session
                        .getAttribute("ProcessingTime");

                String[] arrayDestination = (String[]) session
                        .getAttribute("Destination");
                Date[] arrayResponseReceiveTime = (Date[]) session
                        .getAttribute("ResponseReceiveTime");
                Date responseSendTime = (Date) session
                        .getAttribute("ResponseSendTime");

                if (arrayResponseReceiveTime[threadCount] != null) {
                    LOG.debug("ResponseReceiveTime["
                            + arrayDestination[threadCount] + "]: "
                            + sdf.format(arrayResponseReceiveTime[threadCount]));
                }
                LOG.debug("ResponseSendTime:" + sdf.format(responseSendTime));

                String printText = "";
                String service = "";
                if (session.getAttribute("Service") != null) {
                    service = (String) session.getAttribute("Service");

                }

                printText = printText
                        + "[ProcessTime]["
                        + service
                        + "]ClientRequest->ResponseResult:"
                        + (responseSendTime.getTime() - requestProcessStartTime
                                .getTime()) + "msec ";

                if (arrayResponseReceiveTime != null
                        && arrayResponseReceiveTime[0] != null) {
                    printText = printText + "EngineResponse->SendClientSide["
                            + arrayDestination[threadCount] + "]: ";
                    printText = printText
                            + ": "
                            + (responseSendTime.getTime() - arrayResponseReceiveTime[threadCount]
                                    .getTime()) + "msec ";
                }
                if (!printText.equals("")) {
                    LOG.info(printText);
                }
            }
        } else {

        }

        // success
        return;
    }

    /**
     * send response No MIME data
     * 
     * @param stream
     * @param xmlData
     * @throws MessagingException
     * @throws IOException
     */
    public void sendResponseDataNoMime(OutputStream stream, String xmlData)
            throws MessagingException, IOException {
        // data send
        super.sendNoMime(stream, xmlData);
    }

}
