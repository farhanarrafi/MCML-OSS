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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import jp.go.nict.mcml.com.dao.TrProjectDao;
import jp.go.nict.mcml.com.entity.TrProjectEntity;
import jp.go.nict.mcml.engine.socket.SocketAndStream;
import jp.go.nict.mcml.exception.MCMLException;
import jp.go.nict.mcml.servlet.control.ControlServerProperties;
import jp.go.nict.mcml.servlet.control.dispatcher.database.ServerDatabase;
import jp.go.nict.mcml.servlet.control.dispatcher.database.ServerRecord;
import jp.go.nict.mcml.xml.MCMLStatics;
import jp.go.nict.mcml.xml.XMLTypeCopyTools;
import jp.go.nict.mcml.xml.XMLTypeTools;

import org.apache.log4j.Logger;

import com.MCML.MCMLDoc;
import com.MCML.TextType;
import com.MCML.UserProfileType;

/**
 * DialogController class.
 * 
 * @version 4.0
 * @since 20120921
 */
public class DialogController implements HttpSessionListener {
    private static final Logger LOG = Logger.getLogger(DialogController.class
            .getName());

    private static final String DEBUG_MARK = "/debug";

    // ------------------------------------------
    // public member function
    // ------------------------------------------
    /**
     * sessionCreated
     */
    @Override
    public void sessionCreated(HttpSessionEvent event) {
    }

    /**
     * sessionDestroyed
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        // get session attributes
        HttpSession session = event.getSession();

        if (session != null) {
            DialogSessionAttribute dialogSessionAttribute = (DialogSessionAttribute) session
                    .getAttribute(DialogSessionAttribute.SESSION_ATTRIBUTE);
            if (dialogSessionAttribute != null) {
                DialogWorker dialogWorker = dialogSessionAttribute
                        .getDialogWorker();
                if (dialogWorker != null) {
                    try {
                        dialogWorker.terminate();
                    } catch (Exception exception) {
                        LOG.error(exception.getMessage(), exception);
                        // no process
                    }
                }
            }
        }
    }

    /**
     * MCML connection processing
     * 
     * @param httpRequest
     * @param inputMCMLDoc
     * @param outputMCMLDoc
     * @throws Exception
     */
    public static void doDialogConnect(HttpServletRequest httpRequest,
            MCMLDoc inputMCMLDoc, MCMLDoc outputMCMLDoc) throws Exception {

        String value = (String) httpRequest.getRequestURI();
        value = value.toLowerCase();
        value.contains(DEBUG_MARK);
        // get Transmitter/URI
        String transmitterUri = XMLTypeTools.getURI(inputMCMLDoc);
        // get Transmitter/UserProfile ID
        UserProfileType userProfileType = XMLTypeTools
                .getUserProfileType(inputMCMLDoc);
        String transmitterId = XMLTypeTools.getID(userProfileType);
        if (transmitterId.isEmpty()) {
            throw new MCMLException(MCMLException.FATAL, MCMLException.COMMON,
                    MCMLException.XML_FORMAT_ERROR_ID);
        }

        String sourceLanguage = XMLTypeTools.getInputLanguageID(inputMCMLDoc);
        String targetLanguage = XMLTypeTools.getLanguageTypeID(inputMCMLDoc);

        // Gets server list.
        ArrayList<ServerRecord> serverRecords = ServerDatabase.getInstance()
                .getDestinationFromServerList("DM", sourceLanguage,
                        targetLanguage);

        TextType textType = XMLTypeTools.getTextType(inputMCMLDoc);
        String input_domain = textType.ModelType.first().Domain.first()
                .getValue();

        LOG.debug("DialogController doDialogConnect() input_domain："
                + textType.ModelType.first().Domain.first().getValue());

        // Get DM IP address
        String ipAddress = ControlServerProperties.getInstance().getDmIp();

        // Get a port number
        int port = getPort(input_domain);
        if (port == -1) {
            return;
        }

        LOG.debug("DialogController doDialogConnect() ipAddress：" + ipAddress
                + "  port：" + port);
        // generate SocketAndStream
        SocketAndStream socketAndStream = new SocketAndStream(ipAddress, port);

        // get session
        HttpSession session = httpRequest.getSession();
        String sessionId = session.getId();
        if (sessionId == null) {
            throw new MCMLException(MCMLException.ERROR, MCMLException.DM,
                    MCMLException.HTTP_SESSION_ID_ERROR);
        }

        // generate and start worker thread
        DialogWorker dialogWorker = new DialogWorker(socketAndStream,
                Integer.toString(port), transmitterUri, transmitterId, session);

        session = httpRequest.getSession(false);
        if (session != null) {
            // save session attribute
            DialogSessionAttribute dialogSessionAttribute = new DialogSessionAttribute(
                    socketAndStream, dialogWorker);
            session.setAttribute(DialogSessionAttribute.SESSION_ATTRIBUTE,
                    dialogSessionAttribute);

            LOG.debug("DialogController doDialogConnect() "
                    + "put session DialogSessionAttribute");
        }

        // Dialogue connection request transmission
        doDialog(httpRequest, inputMCMLDoc, outputMCMLDoc);
        return;
    }

    /**
     * Dialogue processing
     * 
     * @param httpRequest
     * @param inputMCMLDoc
     * @param outputMCMLDoc
     * @throws Exception
     */
    public static void doDialog(HttpServletRequest httpRequest,
            MCMLDoc inputMCMLDoc, MCMLDoc outputMCMLDoc) throws Exception {
        // get SessionAttribute
        HttpSession session = httpRequest.getSession(false);
        if (session == null) {
            throw new MCMLException(MCMLException.ERROR, MCMLException.DM,
                    MCMLException.HTTP_SESSION_ERROR);
        }

        DialogSessionAttribute dialogSessionAttribute = (DialogSessionAttribute) session
                .getAttribute(DialogSessionAttribute.SESSION_ATTRIBUTE);

        if (dialogSessionAttribute == null) {
            throw new MCMLException(MCMLException.ERROR, MCMLException.DM,
                    MCMLException.HTTP_ATTRIBUTE_ERROR);
        }
        if (dialogSessionAttribute.getSocketAndStream() == null) {
            throw new MCMLException(MCMLException.ERROR, MCMLException.DM,
                    MCMLException.HTTP_SOCKET_ERROR);
        }

        String mcmlXML = XMLTypeTools.generate(inputMCMLDoc);

        LOG.debug("DialogController doDialog() mcmlXML:" + mcmlXML);

        byte[] message = mcmlXML.getBytes(MCMLStatics.CHARSET_NAME);

        DataOutputStream outputStream = dialogSessionAttribute
                .getSocketAndStream().getDataOutputStream();

        sendFrameData(outputStream, message);
        sendFrameData(outputStream, null);

        outputStream.flush();

        // Gets response data.
        getResponseData(dialogSessionAttribute.getSocketAndStream(),
                outputMCMLDoc);

        return;
    }

    /**
     * doDialogDisconnect
     * 
     * @param httpRequest
     * @param inputMCMLDoc
     * @param outputMCMLDoc
     * @throws Exception
     */
    public static void doDialogDisconnect(HttpServletRequest httpRequest,
            MCMLDoc inputMCMLDoc, MCMLDoc outputMCMLDoc) throws Exception {
        // get SessionAttribute
        HttpSession session = httpRequest.getSession(false);
        if (session == null) {
            throw new MCMLException(MCMLException.ERROR, MCMLException.DM,
                    MCMLException.HTTP_SESSION_ERROR);
        }

        DialogSessionAttribute dialogSessionAttribute = (DialogSessionAttribute) session
                .getAttribute(DialogSessionAttribute.SESSION_ATTRIBUTE);
        if (dialogSessionAttribute == null
                || dialogSessionAttribute.getDialogWorker() == null) {
            throw new MCMLException(MCMLException.ERROR, MCMLException.DM,
                    MCMLException.HTTP_ATTRIBUTE_ERROR);
        }

        // stop worker thread
        DialogWorker dialogWorker = dialogSessionAttribute.getDialogWorker();
        dialogWorker.terminate();

        LOG.debug("DialogWorker was successfully terminated.");

        // output empty data
        XMLTypeTools.generateMCMLType(outputMCMLDoc);
        // succeeded
        return;
    }

    /**
     * Gets response from MCML
     * 
     * @param socketAndStream
     * @throws IOException
     * @throws MCMLException
     */
    private static void getResponseData(SocketAndStream socketAndStream,
            MCMLDoc outputMCMLDoc) throws Exception, MCMLException {

        DataInputStream inputStream = socketAndStream.getDataInputStream();

        int dataBytes = inputStream.readInt();
        if (dataBytes < 0) {

            throw new MCMLException(MCMLException.ERROR, MCMLException.DM,
                    MCMLException.MCML_RESPONSE_DATA_EMPTY);
        }

        // receive data body
        byte[] dataBody = new byte[dataBytes];
        for (int i = 0; dataBytes > 0;) {
            int readBytes = inputStream.read(dataBody, i, dataBytes);
            if (readBytes < 0) {
                throw new MCMLException(MCMLException.ERROR, MCMLException.DM,
                        MCMLException.MCML_RESPONSE_DATA_BODY_EMPTY);
            }
            dataBytes -= readBytes;
            i += readBytes;
        }

        String responseData = new String(dataBody, MCMLStatics.CHARSET_NAME);

        LOG.debug("DialogController getResponseData() response data:"
                + responseData);

        MCMLDoc tempMCMLDoc = MCMLDoc.loadFromString(responseData);

        if (tempMCMLDoc.MCML.exists()) {
            outputMCMLDoc.MCML.append();
            if (tempMCMLDoc.MCML.first().User.exists()) {
                XMLTypeCopyTools.copyUserType(
                        outputMCMLDoc.MCML.first().User.append(),
                        tempMCMLDoc.MCML.first().User.first());
            }
            if (tempMCMLDoc.MCML.first().Server.exists()) {
                XMLTypeCopyTools.copyServerType(
                        outputMCMLDoc.MCML.first().Server.append(),
                        tempMCMLDoc.MCML.first().Server.first());
            }
        }
    }

    // ------------------------------------------
    // private member function
    // ------------------------------------------
    private static void sendFrameData(DataOutputStream outputStream,
            byte[] dataBody) throws IOException {
        if (dataBody == null || dataBody.length == 0) {
            // send data size (for last frame)
            outputStream.writeInt(0);
        } else {
            // create FrameData
            ByteBuffer buffer = ByteBuffer.allocate(dataBody.length
                    + (Integer.SIZE / Byte.SIZE));
            buffer.putInt(dataBody.length);
            buffer.put(dataBody);
            // send data body
            outputStream.write(buffer.array());
        }
        // normal end
        return;
    }

    /**
     * Get a port number
     * 
     * @param domain
     * @return Port number. If the parameter length is less than or equal to 1 when split, returns {@literal -1}.
     */
    private static int getPort(String domain) {

        String[] splitList = domain.split("_", 2);
        if (splitList.length < 2) {
            return -1;
        }

        TrProjectEntity trProjectEntity = new TrProjectEntity();
        TrProjectDao trProjectDao = new TrProjectDao();
        try {
            trProjectDao.select(splitList[1], splitList[0], trProjectEntity);
        } catch (SQLException e) {
            e.printStackTrace();
            LOG.error("Select tr_project table");
        }
        int port = Integer.parseInt(trProjectEntity.getPort());
        return port;
    }
}
