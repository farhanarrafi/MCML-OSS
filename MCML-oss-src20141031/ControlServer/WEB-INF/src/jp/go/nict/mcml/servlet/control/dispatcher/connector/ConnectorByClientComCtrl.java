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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;

import jp.go.nict.mcml.com.client.ClientComCtrl;
import jp.go.nict.mcml.com.client.ResponseData;
import jp.go.nict.mcml.exception.MCMLException;

import org.apache.log4j.Logger;

import com.MCML.MCMLDoc;

/**
 * ConnectorByClientComCtrl class.
 * 
 * @version 4.0
 * @since 20120925
 */
public class ConnectorByClientComCtrl extends Connector {
    private static final Logger LOG = Logger
            .getLogger(ConnectorByClientComCtrl.class.getName());

    // ------------------------------------------
    // private member variables
    // ------------------------------------------
    private String url; // request URL
    private ClientComCtrl clientComCtrl; // client communication controller

    // ------------------------------------------
    // public member function
    // ------------------------------------------
    /**
     * constructor
     * 
     * @param listId
     * @param url
     * @param coefficientASRTimeout
     */
    public ConnectorByClientComCtrl(int listId, String url,
            float coefficientASRTimeout) {
        super(listId, coefficientASRTimeout);
        this.url = url;
        this.clientComCtrl = new ClientComCtrl();
    }

    /**
     * send request to server and receive response from server
     * 
     * @param mcmlDoc
     * @param binariesList
     * @param session
     * @return ResponseData
     */
    @Override
    public ResponseData request(MCMLDoc mcmlDoc,
            ArrayList<byte[]> binariesList, HttpSession session)
            throws UnsupportedEncodingException, IOException, Exception,
            MCMLException {
        // String message;
        ResponseData responseData = null;

        // generate XML Data
        String xmlData = null;
        if (mcmlDoc != null) {
            xmlData = mcmlDoc.saveToString(true);
        }

        // set timeout
        clientComCtrl.setTimeout(super.getTimeoutMilliSeconds(xmlData, mcmlDoc,
                binariesList));

        try {
            // send request and receive response
            responseData = clientComCtrl.request(url, xmlData, binariesList,
                    session);
        } catch (MalformedURLException e) {
            LOG.error("The URL for ControlServer2 is invalid.");
            throw new MCMLException(MCMLException.ERROR, MCMLException.COMMON,
                    MCMLException.CS2_MALFORMED_URL);
        } catch (SocketTimeoutException e) {
            LOG.error(e.getMessage());
            throw new MCMLException(MCMLException.ERROR, MCMLException.COMMON,
                    MCMLException.REQUEST_SOCKET_TIME_OUT);
        } catch (IOException e) {
            LOG.error("Could not attach to " + e.getMessage()
                    + " (IOException)");
            throw new MCMLException(MCMLException.ERROR, MCMLException.COMMON,
                    MCMLException.CS2_DOWN_IO);
        } catch (MessagingException e) {
            LOG.error(e.getMessage());
            throw new MCMLException(MCMLException.ERROR, MCMLException.COMMON,
                    MCMLException.REQUEST_MESSAGING_ERROR);
        }
        // reset timeout counter
        super.resetTimeoutCounter();
        // succeeded
        return responseData;
    }

    // close connection to server
    @Override
    public void closeConnection() {
        clientComCtrl.closeURLConnection();
    }
}
