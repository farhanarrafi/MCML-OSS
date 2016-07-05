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

package jp.go.nict.mcml.engine.socket;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import jp.go.nict.mcml.engine.EngineCtrl;
import jp.go.nict.mcml.exception.MCMLException;
import jp.go.nict.mcml.serverap.logserver.LogServerConnecter;
import jp.go.nict.mcml.servlet.control.ControlServerProperties;
import jp.go.nict.mcml.servlet.control.corpuslog.ControlServerCorpusLogger;
import jp.go.nict.mcml.servlet.control.dispatcher.RequestDispatcher;
import jp.go.nict.mcml.servlet.control.dispatcher.container.DestinationContainer;
import jp.go.nict.mcml.servlet.control.dispatcher.container.DispatchContainer;
import jp.go.nict.mcml.servlet.control.dispatcher.container.ResponseContainer;
import jp.go.nict.mcml.servlet.control.dispatcher.database.ServerDatabase;
import jp.go.nict.mcml.xml.MCMLStatics;
import jp.go.nict.mcml.xml.XMLTypeCopyTools;
import jp.go.nict.mcml.xml.XMLTypeTools;

import org.apache.log4j.Logger;

import com.MCML.DataType;
import com.MCML.ErrorType;
import com.MCML.InputUserProfileType;
import com.MCML.MCMLDoc;
import com.MCML.MCMLType;
import com.MCML.RequestType;
import com.MCML.ResponseType;
import com.MCML.SentenceSequenceType;
import com.MCML.SentenceType;
import com.MCML.ServerType;
import com.MCML.TargetOutputType;
import com.MCML.UserType;
import com.altova.xml.XmlException;

/**
 * SocketEngineCtrl class.
 * 
 * @version 4.0
 * @since 20120921
 */
public class SocketEngineCtrl extends EngineCtrl implements HttpSessionListener {
    private static final Logger LOG = Logger.getLogger(SocketEngineCtrl.class
            .getName());
    // ------------------------------------------
    // private member variable
    // ------------------------------------------
    private static final String ONE_SEND_LANG = "fr";
    private final String schemaLocation = "MCML4ITU_Sep6-12.xsd";
    private final String optionKeyBinary = "Binary";

    // ------------------------------------------
    // public member function
    // ------------------------------------------
    /**
     * initialize
     */
    public void initialize() {
        ControlServerProperties prop = ControlServerProperties.getInstance();

        // read list file
        if (!ServerDatabase.getInstance().initialize(prop.getListFileName())) {
            LOG.fatal("Failed to initialize ServerDatabase.");
        }
    }

    /**
     * processRequest
     */
    @Override
    public boolean processRequest(HttpServletRequest httpRequest,
            HttpServletResponse httpResponse, MCMLDoc inputMCMLDoc,
            ArrayList<byte[]> inputBinaryList, MCMLDoc outputMCMLDoc,
            ArrayList<byte[]> outputBinaryList) throws Exception {
        boolean retVal = true;

        try {
            // copy and check MCMLType
            if (inputMCMLDoc != null && inputMCMLDoc.MCML.exists()) {
                copyMCMLType(inputMCMLDoc, outputMCMLDoc);
            }

            // process request
            processRequestSub(httpRequest, inputMCMLDoc, inputBinaryList,
                    outputMCMLDoc, outputBinaryList);
        } catch (MCMLException exp) {
            doError(outputMCMLDoc, exp);
            retVal = false;
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(e.getMessage(), e);
            throw e;
        }

        return retVal;
    }

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
            SessionAttribute sessionAttribute = (SessionAttribute) session
                    .getAttribute(SessionAttribute.SESSION_ATTRIBUTE);
            if (sessionAttribute != null) {
                // get saved socket list
                ArrayList<DispatchContainer> dispatchContainers = sessionAttribute
                        .getDispatchContainers();

                // disconnect all servers
                for (DispatchContainer dispatchInfo : dispatchContainers) {
                    ArrayList<DestinationContainer> destinationContainers = dispatchInfo
                            .getDestinationContainers();
                    RequestDispatcher
                            .disconnectAllServers(destinationContainers);
                }
            }
        }

        // succeeded
        return;
    }

    // ------------------------------------------
    // private member function
    // ------------------------------------------
    private void processRequestSub(HttpServletRequest httpReq,
            MCMLDoc inputMCMLDoc, ArrayList<byte[]> inputBinaryList,
            MCMLDoc outputMCMLDoc, ArrayList<byte[]> outputBinaryList)
            throws Exception {
        try {
            if (inputMCMLDoc != null && inputMCMLDoc.MCML.exists()) {
                if (XMLTypeTools.hasRequest(inputMCMLDoc)) {
                    String service = XMLTypeTools.getService(inputMCMLDoc);

                    if (XMLTypeTools.serviceIsASR(service)
                            || XMLTypeTools.serviceIsMT(service)
                            || XMLTypeTools.serviceIsTTS(service)) {
                        // --------------------------------------------------------------------------------
                        // use SR Server
                        // --------------------------------------------------------------------------------
                        if (doSpeechRecognition(httpReq, inputMCMLDoc,
                                inputBinaryList, outputMCMLDoc)) {
                            // speech recognition completed

                            // --------------------------------------------------------------------------------
                            // use MT,SS Server
                            // --------------------------------------------------------------------------------
                            doMachineTranslation(httpReq, inputMCMLDoc,
                                    outputMCMLDoc);
                            doSpeechSynthesize(httpReq, inputMCMLDoc,
                                    outputMCMLDoc, outputBinaryList);
                        }
                    } else {
                        throw new MCMLException(MCMLException.FATAL,
                                MCMLException.COMMON,
                                MCMLException.XML_FORMAT_ERROR_SERVICE);
                    }
                } else {
                    throw new MCMLException(MCMLException.FATAL,
                            MCMLException.COMMON,
                            MCMLException.XML_FORMAT_ERROR_MCML);
                }

            } else { // speech data for SR
                     // --------------------------------------------------------------------------------
                     // use SR Server
                     // --------------------------------------------------------------------------------
                if (doSpeechRecognition(httpReq, inputMCMLDoc, inputBinaryList,
                        outputMCMLDoc)) {
                    // speech recognition completed

                    // --------------------------------------------------------------------------------
                    // get saved inputMCML
                    // --------------------------------------------------------------------------------
                    SessionAttribute sessionAttribute = getSessionAttribute(httpReq);
                    inputMCMLDoc = sessionAttribute.getMcmlDoc();

                    // --------------------------------------------------------------------------------
                    // use MT,SS Server
                    // --------------------------------------------------------------------------------
                    doMachineTranslation(httpReq, inputMCMLDoc, outputMCMLDoc);
                    doSpeechSynthesize(httpReq, inputMCMLDoc, outputMCMLDoc,
                            outputBinaryList);
                }
            }
        } catch (UnknownHostException exp) {
            LOG.error(exp.getMessage(), exp);
            throw new MCMLException(MCMLException.ERROR, MCMLException.COMMON,
                    MCMLException.UNKNOWN_HOST_ERROR);
        } catch (ConnectException exp) {
            LOG.error(exp.getMessage(), exp);
            throw new MCMLException(MCMLException.ERROR, MCMLException.COMMON,
                    MCMLException.CONNECT_ERROR);
        } catch (SocketException exp) {
            LOG.error(exp.getMessage(), exp);
            throw new MCMLException(MCMLException.ERROR, MCMLException.COMMON,
                    MCMLException.SOCKET_ERROR);
        } catch (EOFException exp) {
            LOG.error(exp.getMessage(), exp);
            throw new MCMLException(MCMLException.ERROR, MCMLException.COMMON,
                    MCMLException.EOF_ERROR);
        } catch (XmlException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new MCMLException(MCMLException.ERROR, MCMLException.COMMON,
                    MCMLException.XML_PARSE_ERROR);
        }
        // normal end
        return;
    }

    private void doError(MCMLDoc outputMCMLDoc, MCMLException isExp)
            throws Exception {
        LOG.error(isExp.getMessage(), isExp);

        MCMLType outputMCML = outputMCMLDoc.MCML.first();
        ServerType serverType = outputMCML.Server.append();
        ResponseType responseType = serverType.Response.append();
        responseType.Service.setValue(isExp.getService());
        responseType.ProcessOrder.setValue(0);
        ErrorType errorType = responseType.Error.append();
        XMLTypeTools.generateErrorType(isExp.getErrorCode(),
                isExp.getExplanation(), isExp.getService(), errorType);

        // normal end
        return;
    }

    private boolean doSpeechRecognition(HttpServletRequest httpReq,
            MCMLDoc inputMCMLDoc, ArrayList<byte[]> inputBinaryList,
            MCMLDoc outputMCMLDoc) throws Exception {
        if (inputMCMLDoc != null
                && inputMCMLDoc.MCML.exists()
                && (!XMLTypeTools.hasRequest(inputMCMLDoc) || !XMLTypeTools
                        .serviceIsASR(XMLTypeTools.getService(inputMCMLDoc)))) {
            // no process
            return true;
        }

        boolean completed = (inputMCMLDoc != null && inputMCMLDoc.MCML.exists()) ? doSpeechRecognition1stContact(
                httpReq, inputMCMLDoc, inputBinaryList, outputMCMLDoc)
                : doSpeechRecognition2ndContact(httpReq, inputBinaryList,
                        outputMCMLDoc);

        return completed;
    }

    private boolean doSpeechRecognition1stContact(HttpServletRequest httpReq,
            MCMLDoc inputMCMLDoc, ArrayList<byte[]> inputBinaryList,
            MCMLDoc outputMCMLDoc) throws Exception {

        String sessionId = httpReq.getSession().getId();
        try {
            ArrayList<DispatchContainer> dispatchContainers = new ArrayList<DispatchContainer>();
            ArrayList<Integer> corpusLogInfoIDList = new ArrayList<Integer>();
            ControlServerCorpusLogger corpusLogger = ControlServerCorpusLogger
                    .getInstance();
            boolean hasBinaryData = (inputBinaryList != null && inputBinaryList
                    .size() > 0);

            // get session
            HttpSession session = httpReq.getSession(false);
            if (session == null) {
                throw new MCMLException(MCMLException.ERROR, MCMLException.ASR,
                        MCMLException.HTTP_SESSION_ERROR);
            }

            // create session attribute
            SessionAttribute sessionAttribute = new SessionAttribute();
            sessionAttribute.setMcmlDoc(inputMCMLDoc);

            if (!inputMCMLDoc.MCML.first().Server.exists()) {
                throw new MCMLException(MCMLException.FATAL, MCMLException.ASR,
                        MCMLException.XML_FORMAT_ERROR_SERVER);
            }
            for (int i = 0; i < inputMCMLDoc.MCML.first().Server.first().Request
                    .count(); i++) {
                // get Request
                RequestType request = inputMCMLDoc.MCML.first().Server.first().Request
                        .at(i);
                if (!XMLTypeTools.serviceIsASR(request.Service.getValue())) {
                    continue;
                }

                String sourceLanguage = XMLTypeTools.getDataID(request);
                if (sourceLanguage.isEmpty()) {
                    if (!request.InputUserProfile.exists()) {
                        throw new MCMLException(MCMLException.FATAL,
                                MCMLException.ASR,
                                MCMLException.XML_FORMAT_ERROR_INPUT_USER_PROF);
                    }
                    InputUserProfileType inputUserProfileType = request.InputUserProfile
                            .first(); // InputUserProfile is required
                    sourceLanguage = XMLTypeTools
                            .getInputLanguageID(inputUserProfileType);
                }

                if (sourceLanguage.isEmpty()) {
                    throw new MCMLException(MCMLException.FATAL,
                            MCMLException.ASR,
                            MCMLException.XML_FORMAT_ERROR_SRC_LANGUAGE);
                }

                // generate XML String to send
                MCMLDoc mcmlRequestDoc = MCMLDoc.createDocument();
                copyMCMLType(inputMCMLDoc, mcmlRequestDoc);

                // create RequestType
                XMLTypeCopyTools.copyRequestType(
                        mcmlRequestDoc.MCML.first().Server.append().Request
                                .append(), request);

                // create corpus information
                int corpusLogInfoID = corpusLogger
                        .createCorpusLogInformation(mcmlRequestDoc);
                // set first frame arrived time
                corpusLogger.setFirstFrameArrivedTime(corpusLogInfoID,
                        corpusLogger.getFirstFrameArrivedTime(sessionId));

                // generate dispatch information
                DispatchContainer dispatchContainer = RequestDispatcher
                        .generateDispatchContainer(mcmlRequestDoc);

                // set InputMCML information
                corpusLogger.setInputMCMLInfo(mcmlRequestDoc, corpusLogInfoID);

                // save corpus log information id
                corpusLogInfoIDList.add(corpusLogInfoID);
                sessionAttribute.setCorpusLogInfoIDList(corpusLogInfoIDList);

                // save dispatch containers
                dispatchContainers.add(dispatchContainer);
                sessionAttribute.setDispatchContainers(dispatchContainers);

                // update session attribute
                session.setAttribute(SessionAttribute.SESSION_ATTRIBUTE,
                        sessionAttribute);

                ResponseContainer responseInfo;
                if (hasBinaryData) {

                    String logServerUrl = ControlServerProperties.getInstance()
                            .getLogServerURL();

                    // Assigns response
                    String xmlData = mcmlRequestDoc.saveToString(true);
                    LogServerConnecter logServerConnecter = new LogServerConnecter(
                            logServerUrl, xmlData, session, inputBinaryList);
                    logServerConnecter.start();

                    responseInfo = RequestDispatcher.dispatchRequest(
                            dispatchContainer, mcmlRequestDoc, inputBinaryList,
                            session);

                    MCMLDoc mcmlDocResponse = responseInfo.getMcmlDoc(); // Response MCML

                    // append to OutputMCML
                    appendRecvMCML(mcmlDocResponse, outputMCMLDoc); // Necessary?

                    // set complete time
                    corpusLogger.setCompleteTime(corpusLogInfoID);

                    // set last frame arrived time
                    corpusLogger.setLastFrameArrivedTime(corpusLogInfoID,
                            corpusLogger.getLastFrameArrivedTime(sessionId));

                    // set Wave Data and set OutputMCML information and write
                    // file
                    corpusLogger.setWaveData(inputBinaryList, corpusLogInfoID);

                    // get destinations list
                    ArrayList<String> destList = ServerDatabase.getInstance()
                            .getDestinationsByID(responseInfo.getId());

                    corpusLogger.setOutputMCMLInfo(destList, mcmlDocResponse,
                            corpusLogInfoID);

                    if (sessionAttribute != null) {
                        sessionAttribute.setEnd();
                        session.setAttribute(
                                SessionAttribute.SESSION_ATTRIBUTE,
                                sessionAttribute);
                    }
                } else {
                    if (!inputMCMLDoc.MCML.first().Server.first().Request
                            .first().Input.first().AttachedBinary.first().DataID
                            .getValue().equals(ONE_SEND_LANG)) {
                        responseInfo = RequestDispatcher.dispatchRequest(
                                dispatchContainer, mcmlRequestDoc,
                                inputBinaryList, session);
                    }

                    // Version copy
                    String version = inputMCMLDoc.MCML.first().Version
                            .getValue();
                    if (!version.isEmpty()) {
                        inputMCMLDoc.MCML.first().Version.setValue(version);
                    }
                }
            }

            if (hasSpeechRecognitionResult(inputMCMLDoc)) {
                // completed
                return true;
            }
        } catch (UnknownHostException exp) {
            LOG.error(exp.getMessage(), exp);
            throw new MCMLException(MCMLException.ERROR, MCMLException.ASR,
                    MCMLException.UNKNOWN_HOST_ERROR);
        } catch (ConnectException exp) {
            LOG.error(exp.getMessage(), exp);
            throw new MCMLException(MCMLException.ERROR, MCMLException.ASR,
                    MCMLException.CONNECT_ERROR);
        } catch (SocketException exp) {
            LOG.error(exp.getMessage(), exp);
            throw new MCMLException(MCMLException.ERROR, MCMLException.ASR,
                    MCMLException.SOCKET_ERROR);
        } catch (EOFException exp) {
            LOG.error(exp.getMessage(), exp);
            throw new MCMLException(MCMLException.ERROR, MCMLException.ASR,
                    MCMLException.EOF_ERROR);
        } catch (XmlException exp) {
            LOG.error(exp.getMessage(), exp);
            throw new MCMLException(MCMLException.ERROR, MCMLException.ASR,
                    MCMLException.XML_PARSE_ERROR);
        } catch (IOException exp) {
            LOG.error(exp.getMessage(), exp);

            throw new MCMLException(MCMLException.ERROR, MCMLException.ASR,
                    MCMLException.ENGINE_COMMUNICATION_ERROR);
        } catch (MCMLException exp) {
            LOG.error(exp.getMessage(), exp);
            throw exp;
        }
        // continued
        return false;
    }

    // Causes NullPointerException when ControlServer2 is down.
    private boolean doSpeechRecognition2ndContact(HttpServletRequest httpReq,
            ArrayList<byte[]> inputBinaryList, MCMLDoc outputMCMLDoc)
            throws Exception {

        String sessionId = httpReq.getSession().getId();
        int corpusLogInfoID;
        ControlServerCorpusLogger corpusLogger = ControlServerCorpusLogger
                .getInstance();

        try {
            boolean hasBinaryData = (inputBinaryList != null && inputBinaryList
                    .size() > 0);

            // get session attributes
            SessionAttribute sessionAttribute = getSessionAttribute(httpReq);

            // get session
            HttpSession session = httpReq.getSession(false);

            // get saved dispatch informations
            ArrayList<DispatchContainer> dispatchContainers = sessionAttribute
                    .getDispatchContainers();

            // get corpus log information ID list
            ArrayList<Integer> corpusLogInfoIDList = sessionAttribute
                    .getCorpusLogInfoIDList();

            // get saved inputMCML
            MCMLDoc inputMCMLDoc = sessionAttribute.getMcmlDoc();

            for (int i = 0; i < dispatchContainers.size(); i++) {
                DispatchContainer dispatchContainer = dispatchContainers.get(i);
                corpusLogInfoID = corpusLogInfoIDList.get(i);

                // for French data
                ResponseContainer responseInfo;
                if (inputMCMLDoc.MCML.first().Server.first().Request.first().Input
                        .first().AttachedBinary.first().DataID.getValue()
                        .equals(ONE_SEND_LANG)) {
                    if (!hasBinaryData) {
                        ArrayList<byte[]> input = corpusLogger
                                .getWaveData(corpusLogInfoID);
                        ByteArrayOutputStream speechData = new ByteArrayOutputStream();
                        for (int j = 0; j < input.size(); j++) {
                            speechData.write(input.get(j));
                        }
                        ArrayList<byte[]> output = new ArrayList<byte[]>();
                        output.add(speechData.toByteArray());

                        // Sends to LogServer.
                        String logServerUrl = ControlServerProperties
                                .getInstance().getLogServerURL();

                        // Assigns response.
                        String xmlData = inputMCMLDoc.saveToString(true);
                        LogServerConnecter logServerConnecter = new LogServerConnecter(
                                logServerUrl, xmlData, session, output);
                        logServerConnecter.start();

                        responseInfo = RequestDispatcher.dispatchRequest(
                                dispatchContainer, inputMCMLDoc, output,
                                session);

                    } else {
                        responseInfo = null;
                    }
                } else {
                    if (!hasBinaryData) {

                        ArrayList<byte[]> input = corpusLogger
                                .getWaveData(corpusLogInfoID);
                        ByteArrayOutputStream speechData = new ByteArrayOutputStream();
                        for (int j = 0; j < input.size(); j++) {
                            speechData.write(input.get(j));
                        }
                        ArrayList<byte[]> output = new ArrayList<byte[]>();
                        output.add(speechData.toByteArray());

                        // Sends to LogServer.
                        String logServerUrl = ControlServerProperties
                                .getInstance().getLogServerURL();

                        // Assigns response.
                        String xmlData = inputMCMLDoc.saveToString(true);
                        LogServerConnecter logServerConnecter = new LogServerConnecter(
                                logServerUrl, xmlData, session, output);
                        logServerConnecter.start();
                    }

                    responseInfo = RequestDispatcher.dispatchRequest(
                            dispatchContainer, null, inputBinaryList, session);

                }

                if (hasBinaryData) {
                    // Version copy
                    String version = inputMCMLDoc.MCML.first().Version
                            .getValue();
                    if (!version.isEmpty()) {
                        if (!outputMCMLDoc.MCML.exists()) {
                            outputMCMLDoc.MCML.append();
                        }
                        outputMCMLDoc.MCML.first().Version.setValue(version);
                    }
                    // set Wave Data
                    corpusLogger.setWaveData(inputBinaryList, corpusLogInfoID);
                } else {
                    // rewrite outputMCML
                    copyMCMLType(inputMCMLDoc, outputMCMLDoc);

                    // set complete time
                    corpusLogger.setCompleteTime(corpusLogInfoID);

                    MCMLDoc mcmlDocResponse = responseInfo.getMcmlDoc(); // Response MCML

                    // append to OutputMCML
                    appendRecvMCML(mcmlDocResponse, outputMCMLDoc);

                    // set last frame arrived time
                    corpusLogger.setLastFrameArrivedTime(corpusLogInfoID,
                            corpusLogger.getLastFrameArrivedTime(sessionId));

                    // get destinations list
                    ArrayList<String> destList = ServerDatabase.getInstance()
                            .getDestinationsByID(responseInfo.getId());

                    // set OutputMCML information and write file
                    corpusLogger.setOutputMCMLInfo(destList, mcmlDocResponse,
                            corpusLogInfoID);

                    if (sessionAttribute != null) {
                        sessionAttribute.setEnd();
                        session.setAttribute(
                                SessionAttribute.SESSION_ATTRIBUTE,
                                sessionAttribute);
                    }
                }
            }

            // update dispatch containers
            sessionAttribute.setDispatchContainers(dispatchContainers);

            // update session attribute
            session.setAttribute(SessionAttribute.SESSION_ATTRIBUTE,
                    sessionAttribute);
        } catch (UnknownHostException exp) {
            LOG.error(exp.getMessage(), exp);
            throw new MCMLException(MCMLException.ERROR, MCMLException.ASR,
                    MCMLException.UNKNOWN_HOST_ERROR);
        } catch (ConnectException exp) {
            LOG.error(exp.getMessage(), exp);
            throw new MCMLException(MCMLException.ERROR, MCMLException.ASR,
                    MCMLException.CONNECT_ERROR);
        } catch (SocketException exp) {
            LOG.error(exp.getMessage(), exp);
            throw new MCMLException(MCMLException.ERROR, MCMLException.ASR,
                    MCMLException.SOCKET_ERROR);
        } catch (EOFException exp) {
            LOG.error(exp.getMessage(), exp);
            throw new MCMLException(MCMLException.ERROR, MCMLException.ASR,
                    MCMLException.EOF_ERROR);
        } catch (XmlException exp) {
            LOG.error(exp.getMessage(), exp);
            throw new MCMLException(MCMLException.ERROR, MCMLException.ASR,
                    MCMLException.XML_PARSE_ERROR);
        } catch (IOException exp) {
            LOG.error(exp.getMessage(), exp);
            throw new MCMLException(MCMLException.ERROR, MCMLException.ASR,
                    MCMLException.ENGINE_COMMUNICATION_ERROR);
        } catch (MCMLException exp) {
            LOG.error(exp.getMessage(), exp);
            throw exp;
        }

        return hasSpeechRecognitionResult(outputMCMLDoc);
    }

    private void doMachineTranslation(HttpServletRequest httpReq,
            MCMLDoc inputMCMLDoc, MCMLDoc outputMCMLDoc) throws Exception {
        ControlServerCorpusLogger corpusLogger = ControlServerCorpusLogger
                .getInstance();

        String xmlData = inputMCMLDoc.saveToString(true); // for debug
        LOG.debug(xmlData);

        try {
            ArrayList<DispatchContainer> dispatchContainers = new ArrayList<DispatchContainer>();
            ArrayList<Integer> corpusLogInfoIDList = new ArrayList<Integer>();

            // get session
            HttpSession session = httpReq.getSession(false);
            if (session == null) {
                throw new MCMLException(MCMLException.ERROR, MCMLException.MT,
                        MCMLException.HTTP_SESSION_ERROR);
            }

            // create session attribute
            SessionAttribute sessionAttribute = new SessionAttribute();

            if (!inputMCMLDoc.MCML.first().Server.exists()) {
                throw new MCMLException(MCMLException.FATAL, MCMLException.MT,
                        MCMLException.XML_FORMAT_ERROR_SERVER);
            }
            for (int i = 0; i < inputMCMLDoc.MCML.first().Server.first().Request
                    .count(); i++) {
                // get Request
                RequestType request = inputMCMLDoc.MCML.first().Server.first().Request
                        .at(i);
                if (!XMLTypeTools.serviceIsMT(request.Service.getValue())) {
                    continue;
                }

                String sourceLanguage;
                DataType dataType = XMLTypeTools
                        .getDataTypeFromRequest(request);
                if (dataType != null) {
                    sourceLanguage = XMLTypeTools.getModelLanguage(dataType);
                } else {
                    if (!request.InputUserProfile.exists()) {
                        throw new MCMLException(MCMLException.FATAL,
                                MCMLException.MT,
                                MCMLException.XML_FORMAT_ERROR_INPUT_USER_PROF);
                    }
                    InputUserProfileType inputUserProfileType = request.InputUserProfile
                            .first(); // InputUserProfile is required
                    sourceLanguage = XMLTypeTools
                            .getInputLanguageID(inputUserProfileType);
                    if (sourceLanguage.isEmpty()) {
                        throw new MCMLException(MCMLException.FATAL,
                                MCMLException.MT,
                                MCMLException.XML_FORMAT_ERROR_SRC_LANGUAGE);
                    }
                }
                // get target language
                if (!request.TargetOutput.exists()) {
                    throw new MCMLException(MCMLException.FATAL,
                            MCMLException.MT,
                            MCMLException.XML_FORMAT_ERROR_TARGET_OUTPUT);
                }
                TargetOutputType targetOutputType = request.TargetOutput
                        .first(); // TargetOutput is required
                String targetLanguage = XMLTypeTools
                        .getTargetOutputLanguageID(targetOutputType);
                if (targetLanguage.isEmpty()) {
                    throw new MCMLException(MCMLException.FATAL,
                            MCMLException.MT,
                            MCMLException.XML_FORMAT_ERROR_DST_LANGUAGE);
                }

                // modify requestType
                modifySendRequestType(request, outputMCMLDoc, sourceLanguage);

                // generate XML String to send
                MCMLDoc mcmlRequestDoc = MCMLDoc.createDocument();
                copyMCMLType(inputMCMLDoc, mcmlRequestDoc);

                XMLTypeCopyTools.copyRequestType(
                        mcmlRequestDoc.MCML.first().Server.append().Request
                                .append(), request);

                // get corpus information id
                int corpusLogInfoID = corpusLogger
                        .createCorpusLogInformation(mcmlRequestDoc);
                // set last frame arrived time
                corpusLogger.setLastFrameArrivedTime(corpusLogInfoID);

                // generate dispatch information
                DispatchContainer dispatchContainer = RequestDispatcher
                        .generateDispatchContainer(mcmlRequestDoc);

                String logServerUrl = ControlServerProperties.getInstance()
                        .getLogServerURL();

                LogServerConnecter logServerConnecter = new LogServerConnecter(
                        logServerUrl, xmlData, session, null);
                logServerConnecter.start();

                // process request
                ResponseContainer responseInfo = RequestDispatcher
                        .dispatchRequest(dispatchContainer, mcmlRequestDoc,
                                null, session);

                // set inputMCML information
                corpusLogger.setInputMCMLInfo(mcmlRequestDoc, corpusLogInfoID);

                // save corpus log information id
                corpusLogInfoIDList.add(corpusLogInfoID);
                sessionAttribute.setCorpusLogInfoIDList(corpusLogInfoIDList);

                // save dispatch containers
                dispatchContainers.add(dispatchContainer);
                sessionAttribute.setDispatchContainers(dispatchContainers);

                // update session attribute
                session.setAttribute(SessionAttribute.SESSION_ATTRIBUTE,
                        sessionAttribute);

                // set complete time
                corpusLogger.setCompleteTime(corpusLogInfoID);

                MCMLDoc mcmlDoc = responseInfo.getMcmlDoc();

                // append to OutputMCML
                appendRecvMCML(mcmlDoc, outputMCMLDoc);

                // get destinations list
                ArrayList<String> destList = ServerDatabase.getInstance()
                        .getDestinationsByID(responseInfo.getId());

                // set OutputMCML information
                corpusLogger.setOutputMCMLInfo(destList, mcmlDoc,
                        corpusLogInfoID);

            }
            if (sessionAttribute != null) {
                sessionAttribute.setEnd();
                session.setAttribute(SessionAttribute.SESSION_ATTRIBUTE,
                        sessionAttribute);
            }
        } catch (UnknownHostException exp) {
            LOG.error(exp.getMessage(), exp);
            throw new MCMLException(MCMLException.ERROR, MCMLException.MT,
                    MCMLException.UNKNOWN_HOST_ERROR);
        } catch (ConnectException exp) {
            LOG.error(exp.getMessage(), exp);
            throw new MCMLException(MCMLException.ERROR, MCMLException.MT,
                    MCMLException.CONNECT_ERROR);
        } catch (SocketException exp) {
            LOG.error(exp.getMessage(), exp);
            throw new MCMLException(MCMLException.ERROR, MCMLException.MT,
                    MCMLException.SOCKET_ERROR);
        } catch (EOFException exp) {
            LOG.error(exp.getMessage(), exp);
            throw new MCMLException(MCMLException.ERROR, MCMLException.MT,
                    MCMLException.EOF_ERROR);
        } catch (XmlException exp) {
            LOG.error(exp.getMessage(), exp);
            throw new MCMLException(MCMLException.ERROR, MCMLException.MT,
                    MCMLException.XML_PARSE_ERROR);
        } catch (IOException exp) {
            LOG.error(exp.getMessage(), exp);

            throw new MCMLException(MCMLException.ERROR, MCMLException.MT,
                    MCMLException.ENGINE_COMMUNICATION_ERROR);
        } catch (MCMLException exp) {
            LOG.error(exp.getMessage(), exp);
            throw exp;
        }

        // normal end
        return;
    }

    private void doSpeechSynthesize(HttpServletRequest httpReq,
            MCMLDoc inputMCMLDoc, MCMLDoc outputMCMLDoc,
            ArrayList<byte[]> outputBinaryList) throws Exception {
        ControlServerCorpusLogger corpusLogger = ControlServerCorpusLogger
                .getInstance();
        try {
            ArrayList<DispatchContainer> dispatchContainers = new ArrayList<DispatchContainer>();
            ArrayList<Integer> corpusLogInfoIDList = new ArrayList<Integer>();

            // get session
            HttpSession session = httpReq.getSession(false);
            if (session == null) {
                throw new MCMLException(MCMLException.ERROR, MCMLException.TTS,
                        MCMLException.HTTP_SESSION_ERROR);
            }

            // create session attribute
            SessionAttribute sessionAttribute = new SessionAttribute();

            if (!inputMCMLDoc.MCML.first().Server.exists()) {
                throw new MCMLException(MCMLException.FATAL, MCMLException.TTS,
                        MCMLException.XML_FORMAT_ERROR_SERVER);
            }
            for (int i = 0; i < inputMCMLDoc.MCML.first().Server.first().Request
                    .count(); i++) {
                // get Request
                RequestType request = inputMCMLDoc.MCML.first().Server.first().Request
                        .at(i);
                if (!XMLTypeTools.serviceIsTTS(request.Service.getValue())) {
                    continue;
                }

                // get target language
                if (!request.TargetOutput.exists()) {
                    throw new MCMLException(MCMLException.FATAL,
                            MCMLException.TTS,
                            MCMLException.XML_FORMAT_ERROR_TARGET_OUTPUT);
                }
                TargetOutputType targetOutputType = request.TargetOutput
                        .first(); // TargetOutput is required
                String targetLanguage = XMLTypeTools
                        .getTargetOutputLanguageID(targetOutputType);
                if (targetLanguage.isEmpty()) {
                    throw new MCMLException(MCMLException.FATAL,
                            MCMLException.TTS,
                            MCMLException.XML_FORMAT_ERROR_DST_LANGUAGE);
                }

                // modify requestType
                modifySendRequestType(request, outputMCMLDoc, targetLanguage);

                String keyData = null;
                String valueData = null;
                try {
                    if (XMLTypeTools.getOptionType(inputMCMLDoc) != null) {
                        for (int j = 0; j < XMLTypeTools
                                .getOptionCont(inputMCMLDoc); j++) {
                            keyData = XMLTypeTools
                                    .getTargetOutputType(inputMCMLDoc).Option
                                    .at(j).Key.getValue();
                            valueData = XMLTypeTools
                                    .getTargetOutputType(inputMCMLDoc).Option
                                    .at(j).Value2.getValue();

                            if (keyData.equals(this.optionKeyBinary)) {
                                XMLTypeTools.getTargetOutputType(inputMCMLDoc).Option
                                        .removeAt(j);
                                session.setAttribute(this.optionKeyBinary,
                                        valueData);
                            }
                        }
                    }
                } catch (Exception e) {
                    // nothing to do
                }
                // generate XML String to send
                MCMLDoc mcmlRequestDoc = MCMLDoc.createDocument();

                // process is duplicate
                copyMCMLType(inputMCMLDoc, mcmlRequestDoc);

                // create ServerType
                XMLTypeCopyTools.copyRequestType(
                        mcmlRequestDoc.MCML.first().Server.append().Request
                                .append(), request);

                // get corpus information id & select corpus logger & frame send
                // time
                int corpusLogInfoID = corpusLogger
                        .createCorpusLogInformation(mcmlRequestDoc);
                // set last frame arrived time
                corpusLogger.setLastFrameArrivedTime(corpusLogInfoID);

                // generate dispatch information
                DispatchContainer dispatchContainer = RequestDispatcher
                        .generateDispatchContainer(mcmlRequestDoc);

                String logServerUrl = ControlServerProperties.getInstance()
                        .getLogServerURL();
                // Assigns response.
                String xmlData = mcmlRequestDoc.saveToString(true);
                LogServerConnecter logServerConnecter = new LogServerConnecter(
                        logServerUrl, xmlData, session, null);
                logServerConnecter.start();

                // process request
                ResponseContainer responseInfo = RequestDispatcher
                        .dispatchRequest(dispatchContainer, mcmlRequestDoc,
                                null, session);

                // set inputMCML information
                corpusLogger.setInputMCMLInfo(mcmlRequestDoc, corpusLogInfoID);

                // save corpus log information id
                corpusLogInfoIDList.add(corpusLogInfoID);
                sessionAttribute.setCorpusLogInfoIDList(corpusLogInfoIDList);

                // save dispatch containers
                dispatchContainers.add(dispatchContainer);
                sessionAttribute.setDispatchContainers(dispatchContainers);

                // update session attribute
                session.setAttribute(SessionAttribute.SESSION_ATTRIBUTE,
                        sessionAttribute);

                // set complete time
                corpusLogger.setCompleteTime(corpusLogInfoID);

                MCMLDoc mcmlDoc = responseInfo.getMcmlDoc();

                // append to OutputMCML
                appendRecvMCML(mcmlDoc, outputMCMLDoc);

                // set output BinaryData
                if (responseInfo.getBinaryList() != null) {
                    outputBinaryList.addAll(responseInfo.getBinaryList());
                }

                // set "Output" wave data
                corpusLogger.setWaveData(outputBinaryList, corpusLogInfoID);

                // get destinations list
                ArrayList<String> destList = ServerDatabase.getInstance()
                        .getDestinationsByID(responseInfo.getId());

                // set OutputMCML information
                corpusLogger.setOutputMCMLInfo(destList, mcmlDoc,
                        corpusLogInfoID);
            }

            if (sessionAttribute != null) {
                sessionAttribute.setEnd();
                session.setAttribute(SessionAttribute.SESSION_ATTRIBUTE,
                        sessionAttribute);
            }
        } catch (UnknownHostException exp) {
            LOG.error(exp.getMessage(), exp);
            throw new MCMLException(MCMLException.ERROR, MCMLException.TTS,
                    MCMLException.UNKNOWN_HOST_ERROR);
        } catch (ConnectException exp) {
            LOG.error(exp.getMessage(), exp);
            throw new MCMLException(MCMLException.ERROR, MCMLException.TTS,
                    MCMLException.CONNECT_ERROR);
        } catch (SocketException exp) {
            LOG.error(exp.getMessage(), exp);
            throw new MCMLException(MCMLException.ERROR, MCMLException.TTS,
                    MCMLException.SOCKET_ERROR);
        } catch (EOFException exp) {
            LOG.error(exp.getMessage(), exp);
            throw new MCMLException(MCMLException.ERROR, MCMLException.TTS,
                    MCMLException.EOF_ERROR);
        } catch (XmlException exp) {
            LOG.error(exp.getMessage(), exp);
            throw new MCMLException(MCMLException.ERROR, MCMLException.TTS,
                    MCMLException.XML_PARSE_ERROR);
        } catch (IOException exp) {
            LOG.error(exp.getMessage(), exp);
            throw new MCMLException(MCMLException.ERROR, MCMLException.TTS,
                    MCMLException.ENGINE_COMMUNICATION_ERROR);
        } catch (MCMLException exp) {
            LOG.error(exp.getMessage(), exp);
            throw exp;
        }

        // normal end
        return;
    }

    private SessionAttribute getSessionAttribute(HttpServletRequest httpReq)
            throws Exception {
        // get session attributes
        HttpSession session = httpReq.getSession(false);
        if (session == null) {
            throw new MCMLException(MCMLException.ERROR, MCMLException.COMMON,
                    MCMLException.HTTP_SESSION_ERROR);
        }

        SessionAttribute sessionAttribute = (SessionAttribute) session
                .getAttribute(SessionAttribute.SESSION_ATTRIBUTE);
        if (sessionAttribute == null) {
            LOG.error("No object was bound with '"
                    + SessionAttribute.SESSION_ATTRIBUTE + "'");
            throw new MCMLException(MCMLException.ERROR, MCMLException.COMMON,
                    MCMLException.HTTP_ATTRIBUTE_ERROR);
        }

        return sessionAttribute;
    }

    /**
     * To copy the element under "USER" including "USER".<br>
     * originalDoc --> copyDoc.
     * 
     * @param originalDoc
     * @param copyDoc
     * @throws Exception
     */
    private void copyMCMLType(MCMLDoc originalDoc, MCMLDoc copyDoc)
            throws Exception {

        if (copyDoc.MCML.exists()) {
            copyDoc.MCML.remove();
        }
        copyDoc.MCML.append();

        String version = null;

        // Version copy
        if (originalDoc.MCML.exists()
                && originalDoc.MCML.first().Version.exists()) {
            version = originalDoc.MCML.first().Version.getValue();
            copyDoc.MCML.first().Version.setValue(version);
        }

        copyDoc.setSchemaLocation(this.schemaLocation);

        // User copy
        UserType user = null;
        if (originalDoc.MCML.exists() && originalDoc.MCML.first().User.exists()) {
            user = originalDoc.MCML.first().User.first();
            XMLTypeCopyTools.copyUserType(copyDoc.MCML.first().User.append(),
                    originalDoc.MCML.first().User.first());

        }
        // check required parameter
        if (version.isEmpty()) {
            throw new MCMLException(MCMLException.FATAL, MCMLException.COMMON,
                    MCMLException.XML_FORMAT_ERROR_VERSION);
        }
        String userID = XMLTypeTools.getUserID(originalDoc);
        if (userID.isEmpty()) {
            throw new MCMLException(MCMLException.FATAL, MCMLException.COMMON,
                    MCMLException.XML_FORMAT_ERROR_ID);
        }

        // check User/Receiver has same ID and URI as User/Transmitter
        String userURI = XMLTypeTools.getURI(originalDoc);
        boolean isMatch = false;
        for (int i = 0; i < user.Receiver.count(); i++) {
            if (XMLTypeTools.getUserID(user.Receiver.at(i)).equals(userID)
                    && XMLTypeTools.getURI(user.Receiver.at(i)).equals(userURI)) {
                isMatch = true;
                break;
            }
        }
        if (!isMatch) {
            throw new MCMLException(MCMLException.ERROR, MCMLException.COMMON,
                    MCMLException.XML_COMBINATION_ERROR);
        }

        XMLTypeCopyTools.copyHistoryTypeList(copyDoc.MCML.first(),
                originalDoc.MCML.first());

        // normal end
        return;
    }

    /**
     * Sentence Check
     * 
     * @param dataType
     * @return {@code true} if sentence corresponding to parameter data type exists, otherwise {@code false}
     * @throws Exception
     */
    private boolean hasSentence(DataType dataType) throws Exception {
        boolean result = false;
        if (dataType != null) {
            SentenceSequenceType sentenceSequenceType = XMLTypeTools
                    .getSentenceSequenceTypeFromDataType(dataType);
            if (sentenceSequenceType != null) {
                for (int k = 0; k < sentenceSequenceType.Sentence.count(); k++) {
                    SentenceType sentenceType = sentenceSequenceType.Sentence
                            .at(k);
                    if (sentenceType != null) {
                        result = true;
                        break;
                    }
                }
            }
        }
        return result;
    }

    private DataType getLaterOutputData(MCMLDoc mcmlDoc, String language)
            throws Exception {
        // get later DataType from MTResponse
        DataType dataType = getLaterOutputDataFromResponse(mcmlDoc, language,
                MCMLStatics.SERVICE_MT);
        if (dataType == null) {
            // get later DataType from ASRResponse
            dataType = getLaterOutputDataFromResponse(mcmlDoc, language,
                    MCMLStatics.SERVICE_ASR);
        }

        // normal end
        return dataType;
    }

    private DataType getLaterOutputDataFromResponse(MCMLDoc mcmlDoc,
            String language, String responseService) throws Exception {
        DataType resultData = null;

        if (!XMLTypeTools.hasResponse(mcmlDoc)) {
            return null;
        }
        if (!responseService.equalsIgnoreCase(MCMLStatics.SERVICE_ASR)
                && !responseService.equalsIgnoreCase(MCMLStatics.SERVICE_MT)) {
            return null;
        }
        for (int i = mcmlDoc.MCML.first().Server.first().Response.count() - 1; i >= 0; i--) {
            ResponseType responseType = mcmlDoc.MCML.first().Server.first().Response
                    .at(i);
            if (responseType == null || !responseType.Service.exists()) {
                continue;
            }
            String service = responseType.Service.getValue();
            if (!((responseService.equalsIgnoreCase(MCMLStatics.SERVICE_ASR) && XMLTypeTools
                    .serviceIsASR(service)) || (responseService
                    .equalsIgnoreCase(MCMLStatics.SERVICE_MT) && XMLTypeTools
                    .serviceIsMT(service)))) {
                continue;
            }
            DataType dataType = XMLTypeTools
                    .getDataTypeFromResponse(responseType);
            String responcelanguange = XMLTypeTools.getModelLanguage(dataType);
            if (responcelanguange.equalsIgnoreCase(language)) {
                resultData = dataType;
                break;
            }
        }
        // normal end
        return resultData;
    }

    private void modifySendRequestType(RequestType request,
            MCMLDoc outputMCMLDoc, String language) throws Exception {
        DataType requestDataType = XMLTypeTools.getDataTypeFromRequest(request);
        if (!hasSentence(requestDataType)) {
            // getLaterOutputData
            DataType laterResponseDataType = getLaterOutputData(outputMCMLDoc,
                    language);
            if (!hasSentence(laterResponseDataType)) {
                int proc = MCMLException.COMMON;
                if (XMLTypeTools.serviceIsMT(request.Service.getValue())) {
                    proc = MCMLException.MT;
                } else if (XMLTypeTools
                        .serviceIsTTS(request.Service.getValue())) {
                    proc = MCMLException.TTS;
                }
                throw new MCMLException(MCMLException.ERROR, proc,
                        MCMLException.XML_FORMAT_ERROR_SENTENCE_SEQ);
            }
            // check Personality ID
            String laterResponsePersonalityID = XMLTypeTools
                    .getPersonalityID(laterResponseDataType);
            // set InputUserProfile
            if (!laterResponsePersonalityID.isEmpty()) {
                if (!request.InputUserProfile.exists()) {
                    request.InputUserProfile.append();
                }
                request.InputUserProfile.first().ID
                        .setValue(laterResponsePersonalityID);
            }
        }
        return;
    }

    private boolean hasSpeechRecognitionResult(MCMLDoc inputMCMLDoc) {
        try {
            if (XMLTypeTools.hasResponse(inputMCMLDoc)
                    && XMLTypeTools.serviceIsASR(XMLTypeTools
                            .getService(inputMCMLDoc))) {
                return true;
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return false;
    }

    private void appendRecvMCML(MCMLDoc mcmlDoc, MCMLDoc outputMCMLDoc)
            throws Exception {
        try {
            if (mcmlDoc == null) {
                return;
            }
            // copy all elements
            if (XMLTypeTools.hasResponse(mcmlDoc)) {
                if (outputMCMLDoc.MCML.exists()
                        && outputMCMLDoc.MCML.first().Server.exists()) {
                    XMLTypeCopyTools.copyResponseTypeList(
                            outputMCMLDoc.MCML.first().Server.first(),
                            mcmlDoc.MCML.first().Server.first());
                } else {
                    XMLTypeCopyTools.copyResponseTypeList(
                            outputMCMLDoc.MCML.first().Server.append(),
                            mcmlDoc.MCML.first().Server.first());
                }
                if (outputMCMLDoc.MCML.exists()
                        && mcmlDoc.MCML.first().History.exists()) {
                    XMLTypeCopyTools
                            .copyResponseType(
                                    outputMCMLDoc.MCML.first().History.append().Response
                                            .append(),
                                    mcmlDoc.MCML.first().History.first().Response
                                            .first());
                }
            }
        } catch (Exception e) {
            throw e;
        }
        // normal end
        return;
    }

}
