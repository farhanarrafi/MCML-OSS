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

package jp.go.nict.mcml.servlet.control.dispatcher;

//import jp.go.nict.mcml.xml.MCMLException;
import java.util.ArrayList;

import javax.servlet.http.HttpSession;

import jp.go.nict.mcml.com.client.ResponseData;
import jp.go.nict.mcml.exception.MCMLException;
import jp.go.nict.mcml.server.log.LogServerConnecter;
import jp.go.nict.mcml.servlet.control.ControlServerProperties;
import jp.go.nict.mcml.servlet.control.dispatcher.connector.Connector;
import jp.go.nict.mcml.servlet.control.dispatcher.container.DestinationContainer;
import jp.go.nict.mcml.servlet.control.dispatcher.container.ResponseContainer;
import jp.go.nict.mcml.xml.XMLTypeCopyTools;
import jp.go.nict.mcml.xml.XMLTypeTools;

import org.apache.log4j.Logger;

import com.MCML.MCMLDoc;
import com.MCML.ResponseType;
import com.MCML.ServerType;
import com.MCML.UserType;

/**
 * ThreadPerRequest class.
 * 
 * @version 4.0
 * @since 20120921
 */
public class ThreadPerRequest extends Thread {
    private static final Logger LOG = Logger.getLogger(ThreadPerRequest.class
            .getName());
    // ------------------------------------------
    // private member variables
    // ------------------------------------------
    private MCMLDoc mcmlDoc;
    private ArrayList<byte[]> binaryList;
    private DestinationContainer destinationContainer;
    private ResponseContainerQueue responseContainerQueue;
    private HttpSession session;
    private int threadCount;

    // ------------------------------------------
    // public member functions
    // ------------------------------------------
    public ThreadPerRequest(String xmlData, ArrayList<byte[]> binaryList,
            DestinationContainer destinationContainer,
            ResponseContainerQueue responseContainerQueue, int threadCount,
            HttpSession session) throws Exception {

        this.mcmlDoc = null;
        if (xmlData != null) {
            this.mcmlDoc = MCMLDoc.loadFromString(xmlData);
        }
        this.binaryList = binaryList;
        this.destinationContainer = destinationContainer;
        this.responseContainerQueue = responseContainerQueue;
        this.session = session;
        this.threadCount = threadCount;
    }

    /**
     * run
     */
    @Override
    public void run() {
        MCMLDoc responseMCMLDoc = null;
        ArrayList<byte[]> responseBinaryList = null;

        session.setAttribute("ThreadCount", threadCount);

        try {
            responseMCMLDoc = MCMLDoc.createDocument();
            ArrayList<Connector> connectors = destinationContainer
                    .getConnectors();

            ResponseData responseData = (!hasPivotRequest()) ? connectors
                    .get(0).request(mcmlDoc, binaryList, session) // <---
                                                                  // ConnectorByClientComCtrl.request
                    : processForPivotRequest(connectors);

            responseMCMLDoc = MCMLDoc.loadFromString(responseData.getXML());

            if (!hasPivotRequest()) {
                String company1 = destinationContainer.getCompany1();

                setCompanyUriFromType(responseMCMLDoc, company1);
            }

            String logServerUrl = ControlServerProperties.getInstance()
                    .getLogServerURL();

            String xml = responseMCMLDoc.saveToString(true);
            LogServerConnecter logServerConnecter = new LogServerConnecter(
                    logServerUrl, xml, session, null);
            logServerConnecter.start();

            responseBinaryList = responseData.getBinaryList();
        } catch (MCMLException exception) {
            LOG.error(exception.getMessage(), exception);
            mcmlExceptionToMcmlType(exception.getErrorCode(),
                    exception.getExplanation(), responseMCMLDoc);
            responseBinaryList = null;
        } catch (Exception exception) {
            LOG.error(exception.getMessage(), exception);
            exception.printStackTrace();

            exceptionToMcmlType(exception, responseMCMLDoc); // <--- Actually
                                                             // thrown as an
                                                             // MCMLException
            responseBinaryList = null;
        }

        // generate response container
        int id = destinationContainer.getId();
        float priorityCoefficient = destinationContainer
                .getPriorityCoefficient();
        ResponseContainer responseContainer = new ResponseContainer(id,
                priorityCoefficient, responseMCMLDoc, responseBinaryList);

        // enqueue response container
        responseContainerQueue.enqueue(responseContainer);

        // succeeded
        return;
    }

    // ------------------------------------------
    // private member functions
    // ------------------------------------------

    private boolean hasPivotRequest() throws Exception {
        if (mcmlDoc == null) {
            return false;
        }
        String service = XMLTypeTools.getService(mcmlDoc);
        if (!XMLTypeTools.serviceIsMT(service)) {
            return false;
        }
        if (destinationContainer.getLanguages().size() < 3) {
            return false;
        }
        if (destinationContainer.getConnectors().size() < 2) {
            return false;
        }
        // succeeded
        return true;
    }

    /**
     * 
     * If MCML doesn't have URI, add URI in formType of MCML
     * 
     * @param returnMcmlDoc
     * @param company
     * @throws Exception
     */
    private void setCompanyUriFromType(MCMLDoc returnMcmlDoc, String company)
            throws Exception {

        if (returnMcmlDoc.MCML.first().Server.exists()) {
            if (company != null && !company.equals("")) {
                // get Response from MCML
                ServerType serverTypeOrg = returnMcmlDoc.MCML.first().Server
                        .first();
                ResponseType responseTypeOrg = serverTypeOrg.Response.first();

                if (responseTypeOrg.Routing.exists()) {
                    responseTypeOrg.Routing.remove();
                }

                // add company1 in the from
                returnMcmlDoc.MCML.first().Server.first().Response.first().Routing
                        .append().From.append().URI.setValue(company);
            }
        }
        return;
    }

    /**
     * 
     * make MCML for pipvot
     * 
     * @param connectors
     * @return ResponseData
     * @throws Exception
     */
    private ResponseData processForPivotRequest(ArrayList<Connector> connectors)
            throws Exception {
        ResponseData responseData = null;

        // process 1st pivot request
        MCMLDoc mcmlRequest1stDoc = MCMLDoc.createDocument();
        generate1stPivotRequest(mcmlDoc, mcmlRequest1stDoc);

        ResponseData responseData1st = connectors.get(0).request(
                mcmlRequest1stDoc, null, session);
        String xmlData = responseData1st.getXML();

        MCMLDoc tempMcmlDoc = MCMLDoc.loadFromString(xmlData);

        String company1 = destinationContainer.getCompany1();
        setCompanyUriFromType(tempMcmlDoc, company1);

        if (XMLTypeTools.hasError(tempMcmlDoc)) {
            responseData = responseData1st;
        } else {
            // create XML data
            String stResponseData1st = XMLTypeTools.generate(tempMcmlDoc);

            responseData1st.setXML(stResponseData1st);

            // process 2nd pivot request
            MCMLDoc mcmlRequest2ndDoc = MCMLDoc.createDocument();
            generate2ndPivotRequest(tempMcmlDoc, mcmlRequest2ndDoc);
            ResponseData responseData2nd = connectors.get(1).request(
                    mcmlRequest2ndDoc, null, session);
            xmlData = responseData2nd.getXML();
            tempMcmlDoc = MCMLDoc.loadFromString(xmlData);

            String company2 = destinationContainer.getCompany2();
            setCompanyUriFromType(tempMcmlDoc, company2);

            if (XMLTypeTools.hasError(tempMcmlDoc)) {
                String response_string = XMLTypeTools.generate(tempMcmlDoc);

                responseData2nd.setXML(response_string);
                responseData = responseData2nd;
            } else {
                MCMLDoc tempMcmlType1stDoc = MCMLDoc
                        .loadFromString(responseData1st.getXML());

                // add history in the 2nd response
                XMLTypeCopyTools.copyResponseType(
                        tempMcmlDoc.MCML.first().History.append().Response
                                .append(),
                        tempMcmlType1stDoc.MCML.first().Server.first().Response
                                .first());
                String responseData12 = XMLTypeTools.generate(tempMcmlDoc);
                responseData2nd.setXML(responseData12);
                responseData = responseData2nd;
            }
        }
        // succeeded
        return responseData;
    }

    private void generate1stPivotRequest(MCMLDoc requestMcmlDoc,
            MCMLDoc newMCMLDoc) throws Exception {
        ArrayList<String> languages = destinationContainer.getLanguages();

        XMLTypeCopyTools.copyServerType(
                newMCMLDoc.MCML.append().Server.append(),
                requestMcmlDoc.MCML.first().Server.first());

        // replace targetLanguage(Language is required)
        newMCMLDoc.MCML.first().Server.first().Request.first().TargetOutput
                .first().LanguageType2.first().ID.setValue(languages.get(1));

        // Version copy
        String version = requestMcmlDoc.MCML.first().Version.getValue();
        if (!version.isEmpty()) {
            newMCMLDoc.MCML.first().Version.setValue(version);
        }

        // User copy
        UserType user = null;
        if (requestMcmlDoc.MCML.exists()
                && requestMcmlDoc.MCML.first().User.exists()) {
            user = requestMcmlDoc.MCML.first().User.first();
            if (user != null) {
                XMLTypeCopyTools.copyUserType(
                        newMCMLDoc.MCML.first().User.append(), user);
            }
        }
        return;
    }

    private void generate2ndPivotRequest(MCMLDoc responseMcmlDoc,
            MCMLDoc newMCMLDoc) throws Exception {
        ArrayList<String> languages = destinationContainer.getLanguages();

        if (this.mcmlDoc.MCML.first().Server.exists()) {
            this.mcmlDoc.MCML.first().Server.append();
        }

        XMLTypeCopyTools.copyServerType(
                newMCMLDoc.MCML.append().Server.append(),
                this.mcmlDoc.MCML.first().Server.first());

        ServerType newServerType = newMCMLDoc.MCML.first().Server.first();

        // replace sourceLanguage and targetLanguage(Language is required)
        if (newServerType.Request.first().Input.first().Data.first().Text
                .first().ModelType.exists()) {
            newServerType.Request.first().Input.first().Data.first().Text
                    .first().ModelType.first().Language.first().ID
                    .setValue(languages.get(1));
        } else {
            newServerType.Request.first().InputUserProfile.first().InputModality
                    .first().Speaking.first().Language.first().ID
                    .setValue(languages.get(1));
        }
        newServerType.Request.first().TargetOutput.first().LanguageType2
                .first().ID.setValue(languages.get(2));
        newServerType.Request.first().Input.first().Data.remove();
        XMLTypeCopyTools
                .copyDataType(newServerType.Request.first().Input.first().Data
                        .append(),
                        responseMcmlDoc.MCML.first().Server.first().Response
                                .first().Output.first().Data.first());

        // Version copy
        String version = this.mcmlDoc.MCML.first().Version.getValue();
        if (!version.isEmpty()) {
            newMCMLDoc.MCML.first().Version.setValue(version);
        }

        // User copy
        UserType user = null;
        if (this.mcmlDoc.MCML.first().User.exists()) {
            user = this.mcmlDoc.MCML.first().User.first();
            if (user != null) {
                XMLTypeCopyTools.copyUserType(
                        newMCMLDoc.MCML.first().User.append(), user);
            }
        }
        return;
    }

    private void mcmlExceptionToMcmlType(String errorCode, String explanation,
            MCMLDoc returnValue) {
        try {
            XMLTypeTools.generateErrorResponse(errorCode, explanation, mcmlDoc,
                    returnValue);

            String logServerUrl = ControlServerProperties.getInstance()
                    .getLogServerURL();
            String xmlData = returnValue.saveToString(true);
            LogServerConnecter logServerConnecter = new LogServerConnecter(
                    logServerUrl, xmlData, session, null);
            logServerConnecter.start();

        } catch (Exception exception) {
            LOG.error(exception.getMessage(), exception);
            returnValue = null; // Necessary?
        }
        return;
    }

    // Catch Exception at run(), throw it as an MCMLException here, and convert
    // it to MCMLType.
    private void exceptionToMcmlType(Exception exception,
            MCMLDoc responseMCMLDoc) {
        // generate error response

        MCMLException mcmlException = new MCMLException(MCMLException.ERROR,
                MCMLException.COMMON, MCMLException.MCMLDOC_ERROR);
        // convert to MCMLType
        mcmlExceptionToMcmlType(mcmlException.getErrorCode(),
                mcmlException.getExplanation(), responseMCMLDoc);
        return;
    }
}
