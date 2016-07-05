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

import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpSession;

import jp.go.nict.mcml.exception.MCMLException;
import jp.go.nict.mcml.servlet.control.ControlServerProperties;
import jp.go.nict.mcml.servlet.control.ControlServerProperties.AdoptationMode;
import jp.go.nict.mcml.servlet.control.dispatcher.connector.Connector;
import jp.go.nict.mcml.servlet.control.dispatcher.connector.Tools;
import jp.go.nict.mcml.servlet.control.dispatcher.container.DestinationContainer;
import jp.go.nict.mcml.servlet.control.dispatcher.container.DispatchContainer;
import jp.go.nict.mcml.servlet.control.dispatcher.container.ResponseContainer;
import jp.go.nict.mcml.servlet.control.dispatcher.database.ServerDatabase;
import jp.go.nict.mcml.servlet.control.dispatcher.database.ServerRecord;
import jp.go.nict.mcml.xml.XMLTypeTools;

import org.apache.log4j.Logger;

import com.MCML.MCMLDoc;

/**
 * RequestDispatcher class.
 * 
 * @version 4.0
 * @since 20120921
 */
public class RequestDispatcher {
    private static final Logger LOG = Logger.getLogger(RequestDispatcher.class
            .getName());

    // ------------------------------------------
    // public member functions
    // ------------------------------------------
    /**
     * generateDispatchContainer
     * 
     * @param mcmlDoc
     * @return DispatchContainer
     * @throws Exception
     */
    public static DispatchContainer generateDispatchContainer(MCMLDoc mcmlDoc)
            throws Exception {
        ResponseContainerQueue responseContainerQueue = new ResponseContainerQueue();
        ArrayList<DestinationContainer> destinationContainers = generateDestinationContainers(mcmlDoc);
        DispatchContainer dispatchContainer = new DispatchContainer(
                destinationContainers, responseContainerQueue);

        // succeeded
        return dispatchContainer;
    }

    /**
     * dispatchRequest
     * 
     * @param dispatchContainer
     * @param inputMcmlDoc
     * @param inputBinaryList
     * @param session
     * @return ResponseContainer
     * @throws Exception
     */
    public static ResponseContainer dispatchRequest(
            DispatchContainer dispatchContainer, MCMLDoc inputMcmlDoc,
            ArrayList<byte[]> inputBinaryList, HttpSession session)
            throws Exception {
        // get response container queue
        ResponseContainerQueue responseContainerQueue = dispatchContainer
                .getResponseContainerQueue();

        // get server containers
        ArrayList<DestinationContainer> destinationContainers = dispatchContainer
                .getDestinationContainers();

        // dispatch request for all servers
        dispatchRequest(inputMcmlDoc, inputBinaryList, destinationContainers,
                responseContainerQueue, session);

        // adopt response for all servers
        boolean hasAsrSplit = hasAsrSplit(inputMcmlDoc, inputBinaryList);
        ResponseContainer responseContainer = adoptResponse(hasAsrSplit,
                destinationContainers, responseContainerQueue);

        MCMLDoc mcmlResponseDoc = null;
        try {
            mcmlResponseDoc = responseContainer.getMcmlDoc(); // <--- * HERE
        } catch (Exception e) {
            // disconnect all servers
            disconnectAllServers(destinationContainers);
            throw (e);
        }

        if (mcmlResponseDoc != null && mcmlResponseDoc.MCML.exists()
                && XMLTypeTools.hasResponse(mcmlResponseDoc)) {
            // disconnect all servers
            disconnectAllServers(destinationContainers);

        }

        // succeeded
        return responseContainer;
    }

    /**
     * disconnectAllServers
     * 
     * @param destinationContainers
     */
    public static void disconnectAllServers(
            ArrayList<DestinationContainer> destinationContainers) {
        for (DestinationContainer destinationContainer : destinationContainers) {
            ArrayList<Connector> connectors = destinationContainer
                    .getConnectors();
            for (Connector connector : connectors) {
                connector.closeConnection();
            }
        }

        // succeeded
        return;
    }

    // ------------------------------------------
    // private member functions
    // ------------------------------------------

    private static ArrayList<DestinationContainer> generateDestinationContainers(
            MCMLDoc mcmlDoc) throws Exception {
        ArrayList<DestinationContainer> destinationContainers = null;

        String xmlData = mcmlDoc.saveToString(true);
        LOG.debug(xmlData);

        // get service,sourceLanguage,targetLanguage
        String service = XMLTypeTools.getService(mcmlDoc);
        String sourceLanguage = XMLTypeTools.getInputLanguageID(mcmlDoc);
        String targetLanguage = XMLTypeTools.getLanguageTypeID(mcmlDoc);

        ArrayList<ServerRecord> serverRecords;
        if (!service.equals("MT")) {
            serverRecords = ServerDatabase.getInstance()
                    .getDestinationFromServerList(service, sourceLanguage,
                            targetLanguage);
        } else {
            serverRecords = ServerDatabase.getInstance().getMTServerList(
                    sourceLanguage, targetLanguage);
        }
        // found request destination
        if (serverRecords != null && !serverRecords.isEmpty()) {
            destinationContainers = new ArrayList<DestinationContainer>();
            for (int i = 0; i < serverRecords.size(); i++) {
                // get server list data
                ServerRecord serverRecord = serverRecords.get(i);

                // generate server connection
                ArrayList<Connector> connectors = generateConnectors(
                        serverRecord.getListID(),
                        serverRecord.getDestinations(),
                        serverRecord.getCoefficientASRTimeout());

                // generate destination container
                DestinationContainer destinationContainer = new DestinationContainer(
                        serverRecord.getListID(),
                        serverRecord.getCoefficientPriority(),
                        serverRecord.getLanguages(), connectors,
                        serverRecord.getCompany1(), serverRecord.getCompany2());

                // add destination container array list
                destinationContainers.add(destinationContainer);
            }
        } else {
            if (ServerDatabase.getInstance().isEmptyServerRecords()) {
                // server records is empty (serverRecords.size() == 0)
                // -----------------------------------------------------------
                // This is thrown when:
                // 1. ServerListData.properties is missing
                // 2. An invalid parameter was detected in the .properties file
                // -----------------------------------------------------------
                throw new MCMLException(MCMLException.FATAL,
                        MCMLException.COMMON,
                        MCMLException.SERVER_INITIALIZE_ERROR);
            } else {
                // server records is not empty and not found destination

                // ------------------------------
                // serverRecords was not empty, but failed to generate server
                // connection / destination container.
                throw new MCMLException(MCMLException.ERROR,
                        MCMLException.COMMON,
                        MCMLException.SERVER_DESTINATION_ERROR);
            }
        }

        // normal end
        return destinationContainers;
    }

    private static void dispatchRequest(MCMLDoc mcmlDoc,
            ArrayList<byte[]> binaryList,
            ArrayList<DestinationContainer> destinationContainers,
            ResponseContainerQueue responseContainerQueue, HttpSession session)
            throws Exception {

        String xmlData = null;
        if (mcmlDoc != null) {
            xmlData = mcmlDoc.saveToString(true);

        }

        int threadCount = 0;
        int destSize = destinationContainers.size();

        if (destSize < 1) {

            LOG.error("'destinationContainers' is empty (destSize is zero).");
        }
        String[] arrayDestination = new String[destSize];
        session.setAttribute("Destination", arrayDestination);
        String[] arrayDataEnd = new String[destSize];
        session.setAttribute("DataEnd", arrayDataEnd);
        Date[] arrayRequestSendTime = new Date[destSize];
        session.setAttribute("RequestSendTime", arrayRequestSendTime);
        Date[] arrayResponseReceiveTime = new Date[destSize];
        session.setAttribute("ResponseReceiveTime", arrayResponseReceiveTime);

        for (DestinationContainer destinationContainer : destinationContainers) {
            ThreadPerRequest threadPerRequest = new ThreadPerRequest(xmlData,
                    binaryList, destinationContainer, responseContainerQueue,
                    threadCount, session);
            threadPerRequest.start();

            threadCount = threadCount + 1;
        }
        // succeeded
        return;
    }

    private static boolean hasAsrSplit(MCMLDoc mcmlDoc,
            ArrayList<byte[]> binaryList) throws Exception {
        boolean hasAsrSplit = false;

        if (mcmlDoc != null && mcmlDoc.MCML.exists()) {
            String service = XMLTypeTools.getService(mcmlDoc);
            if (XMLTypeTools.serviceIsASR(service) && binaryList.isEmpty()) {
                hasAsrSplit = true;
            }
        } else if (!binaryList.isEmpty()) {
            hasAsrSplit = true;
        }
        // succeeded
        return hasAsrSplit;
    }

    private static ResponseContainer adoptResponse(boolean hasAsrSplit,
            ArrayList<DestinationContainer> destinationContainers,
            ResponseContainerQueue responseContainerQueue) throws Exception {
        ResponseAdopter responseAdopter = new ResponseAdopter(
                destinationContainers, responseContainerQueue);
        ResponseContainer responseContainer = null;

        if (hasAsrSplit) {
            responseContainer = responseAdopter.processForAsrSplit();
        } else {
            AdoptationMode adoptationMode = ControlServerProperties
                    .getInstance().getResultAdoptionMode();
            if (adoptationMode == AdoptationMode.SCORE_ORDER) {
                responseContainer = responseAdopter.processForScoreOrder();
            } else {
                responseContainer = responseAdopter.process();
            }
        }
        // succeeded
        return responseContainer;
    }

    // generate server connection array list
    private static ArrayList<Connector> generateConnectors(int listId,
            ArrayList<String> destinations, float coefficientASRTimeout) {
        ArrayList<Connector> serverConnections = new ArrayList<Connector>();
        // loop number of request destination
        for (int i = 0; i < destinations.size(); i++) {
            Connector serverConnection = null;
            if (destinations.get(i) != null
                    && !destinations.get(i).toString().isEmpty()) {
                // generate server connection
                serverConnection = Tools.generate(listId, destinations.get(i),
                        coefficientASRTimeout);
                // add server connection array list
                serverConnections.add(serverConnection);
            }
        }
        return serverConnections;
    }
}
