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

package jp.go.nict.mcml.server.log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;

import jp.go.nict.mcml.com.client.ClientComCtrl;
import jp.go.nict.mcml.com.client.ResponseData;

import org.apache.log4j.Logger;

/**
 * LogServerConnecter class.
 * 
 * @version 4.0
 * @since 20121019
 */
public class LogServerConnecter extends Thread {
    private static final Logger LOG = Logger.getLogger(LogServerConnecter.class
            .getName());
    // ------------------------------------------
    // private member variables
    // ------------------------------------------
    private String logServerUrl;
    private String xmlData;

    private ClientComCtrl clientComCtrl;
    private HttpSession session;
    private ArrayList<byte[]> binaryList;

    // ------------------------------------------
    // public member functions
    // ------------------------------------------
    /**
     * Constructor
     * 
     * @param logServerUrl
     * @param xmlData
     * @param session
     * @param binaryDataList
     */
    public LogServerConnecter(String logServerUrl, String xmlData,
            HttpSession session, ArrayList<byte[]> binaryDataList) {

        this.logServerUrl = logServerUrl;
        this.xmlData = xmlData;
        this.session = session;
        this.binaryList = binaryDataList;

        // create ClientComCtrl for RoutingServer
        clientComCtrl = new ClientComCtrl();

    }

    /**
     * run
     */
    @Override
    public void run() {

        ResponseData responseData = null;
        try {
            responseData = clientComCtrl.requestLogServer(logServerUrl,
                    xmlData, session, binaryList);
        } catch (MalformedURLException e) {
            LOG.error(e.getMessage(), e);
        } catch (IOException e) {
            // When overloaded, this occurs with a message like:
            // 'Server returned HTTP response code: 503 for URL:
            // http://127.0.0.1/LogServer/LogServer'.
            // It recovers automatically when Tomcat sessions expire to a
            // certain amount.
            LOG.error(e.getMessage(), e);
        } catch (MessagingException e) {
            LOG.error(e.getMessage(), e);
        }

        LOG.debug("Send XML to LogServer" + responseData.getXML());

    }
}
