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

//import java.io.IOException;
import java.util.ArrayList;

import jp.go.nict.mcml.servlet.control.ControlServerProperties;
import jp.go.nict.mcml.servlet.control.ControlServerProperties.AdoptationMode;
import jp.go.nict.mcml.servlet.control.dispatcher.container.DestinationContainer;
import jp.go.nict.mcml.servlet.control.dispatcher.container.ResponseContainer;
//import jp.go.nict.mcml.servlet.MCMLLogger;
import jp.go.nict.mcml.xml.XMLTypeTools;

import com.MCML.MCMLDoc;

/**
 * ResponseAdopter class.
 * 
 * @version 4.0
 * @since 20120921
 */
public class ResponseAdopter {
    // ------------------------------------------
    // private member variables
    // ------------------------------------------
    private ArrayList<DestinationContainer> destinationContainers;
    private ResponseContainerQueue responseContainerQueue;

    // ------------------------------------------
    // public member functions
    // ------------------------------------------
    /**
     * Constructor
     * 
     * @param destinationContainers
     * @param responseContainerQueue
     */
    public ResponseAdopter(
            ArrayList<DestinationContainer> destinationContainers,
            ResponseContainerQueue responseContainerQueue) {
        this.destinationContainers = destinationContainers;
        this.responseContainerQueue = responseContainerQueue;
    }

    /**
     * process
     * 
     * @return ResponseContainer
     * @throws Exception
     */
    public ResponseContainer process() throws Exception {
        ResponseContainer responseContainer = null;
        ResponseContainer errorContainer = null;

        AdoptationMode adoptationMode = ControlServerProperties.getInstance()
                .getResultAdoptionMode();
        int listTopId = destinationContainers.get(0).getId();
        int minId = Integer.MAX_VALUE;
        int minErrorId = Integer.MAX_VALUE;

        for (int responseNumber = 0; responseNumber < destinationContainers
                .size(); responseNumber++) {
            ResponseContainer tempContainer = responseContainerQueue.dequeue();
            int id = tempContainer.getId();

            if (adoptationMode == AdoptationMode.LIST_TOP) {
                if (id == listTopId) {
                    responseContainer = tempContainer;
                    // no more process
                    break;
                }
            } else if (adoptationMode == AdoptationMode.FASTEST) {
                if (!XMLTypeTools.hasError(tempContainer.getMcmlDoc())) { // case
                                                                          // succeeded
                    responseContainer = tempContainer;
                    // no more process
                    break;
                } else { // case error
                    if (errorContainer == null) {
                        errorContainer = tempContainer;
                    }
                }
            } else if (adoptationMode == AdoptationMode.LIST_ORDER) {
                if (!XMLTypeTools.hasError(tempContainer.getMcmlDoc())) { // case
                                                                          // succeeded
                    if (id < minId) {
                        responseContainer = tempContainer;
                        minId = id;
                    }
                } else { // case error
                    if (id < minErrorId) {
                        errorContainer = tempContainer;
                        minErrorId = id;
                    }
                }
            }
        }

        if (responseContainer == null) {
            // all server were responding error
            responseContainer = errorContainer;
        }

        // succeeded
        return responseContainer;
    }

    /**
     * processForAsrSplit
     * 
     * @return ResponseContainer
     * @throws Exception
     */
    public ResponseContainer processForAsrSplit() throws Exception {
        waitForAllResponse();

        ResponseContainer responseContainer = null;
        ResponseContainer errorContainer = null;

        AdoptationMode adoptationMode = ControlServerProperties.getInstance()
                .getResultAdoptionMode();
        if (destinationContainers.size() > 0) {

            int listTopId = destinationContainers.get(0).getId();

            while (responseContainerQueue.size() > 0) {
                ResponseContainer tempContainer = responseContainerQueue
                        .dequeue();
                int id = tempContainer.getId();

                if (adoptationMode == AdoptationMode.LIST_TOP) {
                    if (id == listTopId) {
                        responseContainer = tempContainer;
                    } else if (XMLTypeTools
                            .hasError(tempContainer.getMcmlDoc())) { // case
                                                                     // error
                        removeDestinationContainer(tempContainer.getId());
                    }
                } else { // AdoptationMode.FASTEST
                    if (!XMLTypeTools.hasError(tempContainer.getMcmlDoc())) { // case
                                                                              // succeeded
                        if (responseContainer == null) {
                            responseContainer = tempContainer;
                        }
                    } else { // case error
                        removeDestinationContainer(tempContainer.getId());
                        if (errorContainer == null) {
                            errorContainer = tempContainer;
                        }
                    }
                }
            }

        }

        if (responseContainer == null) {
            // all server were responding error
            responseContainer = errorContainer;
        }

        // succeeded
        return responseContainer;
    }

    /**
     * processForScoreOrder
     * 
     * @return ResponseContainer
     * @throws Exception
     */
    public ResponseContainer processForScoreOrder() throws Exception {
        waitForAllResponse();

        ResponseContainer responseContainer = null;
        ResponseContainer listOrderContainer = null;
        ResponseContainer errorContainer = null;

        int minId = Integer.MAX_VALUE;
        int minErrorId = Integer.MAX_VALUE;
        float maxPriority = Float.MIN_VALUE;

        while (responseContainerQueue.size() > 0) {
            ResponseContainer tempContainer = responseContainerQueue.dequeue();
            int id = tempContainer.getId();

            if (!XMLTypeTools.hasError(tempContainer.getMcmlDoc())) { // case
                                                                      // succeeded
                if (id < minId) {
                    // keep list order container
                    listOrderContainer = tempContainer;
                    minId = id;
                }

                // calculate priority
                float coefficient = tempContainer.getPriorityCoefficient();
                float priority = calculatePriority(tempContainer.getMcmlDoc(),
                        coefficient);

                // compare priority
                if (maxPriority < priority) {
                    responseContainer = tempContainer;
                    maxPriority = priority;
                } else if (maxPriority == priority) {
                    responseContainer = null;
                }
            } else { // case error
                if (id < minErrorId) {
                    errorContainer = tempContainer;
                    minErrorId = id;
                }
            }
        }

        if (responseContainer == null) {
            if (listOrderContainer != null) {
                // has same priority
                responseContainer = listOrderContainer;
            } else {
                // all server were responding error
                responseContainer = errorContainer;
            }
        }

        // succeeded
        return responseContainer;
    }

    // ------------------------------------------
    // private member functions
    // ------------------------------------------

    private void waitForAllResponse() throws InterruptedException {
        while (responseContainerQueue.size() < destinationContainers.size()) {
            Thread.sleep(0);
        }
        // succeeded
        return;
    }

    private float calculatePriority(MCMLDoc mcmlDoc, float coefficient) {
        // PENDING
        return coefficient;
    }

    private void removeDestinationContainer(int id) throws Exception {
        for (int i = 0; i < destinationContainers.size(); i++) {
            DestinationContainer destinationContainer = destinationContainers
                    .get(i);
            if (destinationContainer.getId() == id) {
                destinationContainers.remove(i);
                break;
            }
        }
        // succeeded
        return;
    }

}
