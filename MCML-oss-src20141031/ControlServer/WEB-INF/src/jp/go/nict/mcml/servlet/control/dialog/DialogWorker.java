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

package jp.go.nict.mcml.servlet.control.dialog;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import jp.go.nict.mcml.com.client.ClientComCtrl;
import jp.go.nict.mcml.engine.socket.SocketAndStream;
import jp.go.nict.mcml.exception.MCMLException;
import jp.go.nict.mcml.servlet.control.ControlServerProperties;
import jp.go.nict.mcml.xml.MCMLStatics;

import org.apache.log4j.Logger;

/**
 * DialogWorker class.
 * 
 * @version 4.0
 * @since 20120921
 */
public class DialogWorker extends Thread {
    private static final Logger LOG = Logger.getLogger(DialogWorker.class
            .getName());
    // ------------------------------------------
    // private member variables
    // ------------------------------------------
    private SocketAndStream socketAndStream;
    private String uri;
    private String receiverUri;
    private String receiverId;
    private ClientComCtrl clientComCtrl;
    String language;
    private HttpSession session;

    // ------------------------------------------
    // public member functions
    // ------------------------------------------
    /**
     * Constructor
     * 
     * @param socketAndStream
     * @param uri
     * @param receiverUri
     * @param receiverId
     * @param session
     */
    public DialogWorker(SocketAndStream socketAndStream, String uri,
            String receiverUri, String receiverId, HttpSession session) {
        this.socketAndStream = socketAndStream;
        this.uri = uri;
        this.receiverUri = receiverUri;
        this.receiverId = receiverId;
        this.session = session;

        // create ClientComCtrl for RoutingServer
        clientComCtrl = new ClientComCtrl();

        language = "ja";
    }

    /**
     * run
     */
    @Override
    public void run() {
        try {
            // party registration
            MCMLClient.partyRegistration(uri, ControlServerProperties
                    .getInstance().getPartyRegistrationServerUserID(),
                    language, true, clientComCtrl, session);

            for (int processOrder = 1;; processOrder++) {
                byte[] dataBody = receiveFrameData();
                byte[] terminator = receiveFrameData();
                if (dataBody == null || terminator != null) {
                    throw new MCMLException(MCMLException.ERROR,
                            MCMLException.DM,
                            MCMLException.FRAME_DATA_SEQUENCE_ERROR);
                }
                String message = new String(dataBody, MCMLStatics.CHARSET_NAME);

                MCMLClient.dialog(uri, ControlServerProperties.getInstance()
                        .getPartyRegistrationServerUserID(), receiverUri,
                        receiverId, language, message, processOrder,
                        clientComCtrl, session);
            }
        } catch (Exception exception) {
            LOG.error(exception.getMessage(), exception);
        }
    }

    /**
     * Performs close and executes join method.
     * 
     * @throws Exception
     */
    public void terminate() throws Exception {
        // stop thread
        socketAndStream.close();

        // wait stop
        join();

    }

    // ------------------------------------------
    // private member functions
    // ------------------------------------------
    private byte[] receiveFrameData() throws IOException, MCMLException {
        // receive data size
        int dataBytes = socketAndStream.getDataInputStream().readInt();
        if (dataBytes < 0) {
            throw new MCMLException(MCMLException.ERROR, MCMLException.DM,
                    MCMLException.FRAME_DATA_EMPTY);
        }

        if (dataBytes == 0) {
            // no data body (for last frame)
            return null;
        }

        // receive data body
        byte[] dataBody = new byte[dataBytes];
        for (int i = 0; dataBytes > 0;) {
            int readBytes = socketAndStream.getDataInputStream().read(dataBody,
                    i, dataBytes);
            if (readBytes < 0) {
                throw new MCMLException(MCMLException.ERROR, MCMLException.DM,
                        MCMLException.FRAME_DATA_BODY_EMPTY);
            }
            dataBytes -= readBytes;
            i += readBytes;
        }

        return dataBody;
    }
}
