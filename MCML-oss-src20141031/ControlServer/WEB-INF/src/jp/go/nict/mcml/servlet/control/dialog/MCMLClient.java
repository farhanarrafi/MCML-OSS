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

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import javax.servlet.http.HttpSession;

import jp.go.nict.mcml.com.client.ClientComCtrl;
import jp.go.nict.mcml.com.client.ResponseData;
import jp.go.nict.mcml.exception.MCMLException;
import jp.go.nict.mcml.servlet.control.ControlServerProperties;
import jp.go.nict.mcml.xml.MCMLStatics;
import jp.go.nict.mcml.xml.XMLTypeTools;

import org.apache.log4j.Logger;

import com.MCML.AttachedBinaryType;
import com.MCML.InputType;
import com.MCML.MCMLDoc;
import com.MCML.MCMLType;
import com.MCML.RequestType;

/**
 * MCMLClient class.
 * 
 * @version 4.0
 * @since 20120921
 */
public class MCMLClient {
    private static final Logger LOG = Logger.getLogger(MCMLClient.class
            .getName());

    // ------------------------------------------
    // public member function
    // ------------------------------------------
    /**
     * partyRegistration
     * 
     * @param transmitterUri
     * @param transmitterId
     * @param language
     * @param registSwitch
     * @param clientComCtrl
     * @param session
     * @throws Exception
     */
    public static void partyRegistration(String transmitterUri,
            String transmitterId, String language, boolean registSwitch,
            ClientComCtrl clientComCtrl, HttpSession session) throws Exception {
        MCMLDoc mcmlDoc = MCMLDoc.createDocument();

        MCMLType mcmlType = mcmlDoc.MCML.append();
        mcmlType.User.append().Transmitter.append().Device.append().Location
                .append().URI.append().setValue(transmitterUri);

        RequestType requestType = mcmlType.Server.append().Request.append();
        requestType.Service.setValue(MCMLStatics.SERVICE_PARTY_REGISTRATION);
        requestType.ProcessOrder.setValue(1);

        if (registSwitch) {
            InputType inputType = requestType.Input.append();
            inputType.Data.append().Image.append().ChannelID.setValue(0);
            AttachedBinaryType attachedBinaryType = inputType.AttachedBinary
                    .append();

            attachedBinaryType.ChannelID.setValue(1);
            attachedBinaryType.DataID.setValue(language);
            attachedBinaryType.DataType2.setValue("image");
        }

        // create XML data
        String xmlData = XMLTypeTools.generate(mcmlDoc);

        // read binary data
        FileChannel fileChannel = new FileInputStream(ControlServerProperties
                .getInstance().getPartyRegistrationServerImageFileName())
                .getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate((int) fileChannel.size());
        fileChannel.read(byteBuffer);

        // send request
        ResponseData responseData = null;

        try {
            responseData = clientComCtrl.request(ControlServerProperties
                    .getInstance().getPartyRegistrationServerURL(), xmlData,
                    byteBuffer.array(), session);
        } catch (Exception exception) {
            LOG.error(exception.getMessage(), exception);
            throw new MCMLException(MCMLException.ERROR, MCMLException.DM,
                    MCMLException.PARTY_REG_SERVER_DOWN);
        }

        // check response
        if (responseData == null || !responseData.hasXML()) {
            throw new MCMLException(MCMLException.ERROR, MCMLException.DM,
                    MCMLException.PARTY_REG_SERVER_RESPONSE_ERROR);
        }
        String responseXml = responseData.getXML();
        mcmlDoc = MCMLDoc.loadFromString(responseXml);
        if (XMLTypeTools.hasError(mcmlDoc)) {
            throw new MCMLException(MCMLException.ERROR, MCMLException.DM,
                    MCMLException.PARTY_REG_SERVER_REQUEST_ERROR);
        }
        // succeeded
        return;
    }

    /**
     * dialog
     * 
     * @param transmitterUri
     * @param transmitterId
     * @param receiverUri
     * @param receiverId
     * @param language
     * @param message
     * @param processOrder
     * @param clientComCtrl
     * @param session
     * @throws Exception
     */
    public static void dialog(String transmitterUri, String transmitterId,
            String receiverUri, String receiverId, String language,
            String message, int processOrder, ClientComCtrl clientComCtrl,
            HttpSession session) throws Exception {
        // send request
        ResponseData responseData = null;

        try {
            responseData = clientComCtrl.request(ControlServerProperties
                    .getInstance().getRoutingServerURL(), message, session);
        } catch (Exception exception) {
            LOG.error(exception.getMessage(), exception);
            throw new MCMLException(MCMLException.ERROR, MCMLException.DM,
                    MCMLException.ROUTING_SERVER_DOWN);
        }

        // check response
        if (responseData == null || !responseData.hasXML()) {
            throw new MCMLException(MCMLException.ERROR, MCMLException.DM,
                    MCMLException.ROUTING_SERVER_RESPONSE_ERROR);
        }

        String responseXml = responseData.getXML();

        MCMLDoc mcmlDoc = MCMLDoc.loadFromString(responseXml);
        if (XMLTypeTools.hasError(mcmlDoc)) {
            throw new MCMLException(MCMLException.ERROR, MCMLException.DM,
                    MCMLException.ROUTING_SERVER_REQUEST_ERROR);
        }
        // succeeded
        return;
    }

}
