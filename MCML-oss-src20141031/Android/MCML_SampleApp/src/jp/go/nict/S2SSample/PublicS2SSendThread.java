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

package jp.go.nict.S2SSample;

import java.util.Enumeration;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;

import jp.go.nict.S2SSample.Event.CompleteEvent;
import jp.go.nict.S2SSample.Event.CompleteEventListener;
import jp.go.nict.mcml.com.client.ClientComCtrl;
import jp.go.nict.mcml.com.client.ResponseData;
import android.content.Context;
import android.util.Log;

/**
 * Class for sending voice translation request to MCML server.
 *
 */
public class PublicS2SSendThread extends Thread {

    /** Loop waiting time  (msec) */
    protected static final int THREAD_ROOP_WAIT = 100;

    /** MCML data send/receive controller */
    protected ClientComCtrl mClientComCtrl = null;

    protected int adpcmDivideDecodeSize = 4000;

    /** Queue of requests sent to MCML server */
    private LinkedBlockingQueue<SpeechTranslationData> mRequestQueue = new LinkedBlockingQueue<SpeechTranslationData>();
    /** Event listener notifying completion of MCML request processing */
    protected Vector<CompleteEventListener> mvArrivalEventListeners = new Vector<CompleteEventListener>();

    /** Server URL */
    private final String serverUrl = "http://MyServer/ControlServer/ControlServer";

    /**
     * Flag controlling execution and stopping of process. Executes process when true.  {@link #suspendExecute} ,
     * Controls flag with {@link #resumeExecute}.
     */
    boolean mFlagExecute = true;

    /** When stopping thread run(), sets this flag to false using {@link #destroyExecute}.  */
    boolean mFlagLiving = true;

    /**
     * Creates constructor MCML data send/receive controller without proxy.
     */
    public PublicS2SSendThread(Context context) {
        super();
        mClientComCtrl = new ClientComCtrl();
    }

    /**
     * Creates constructor MCML data send/receive controller without proxy.
     *
     * @param sProxyHost
     *            Proxy host nameorIP address
     * @param iProxyPort
     *            Proxy port number
     */
    public PublicS2SSendThread(String sProxyHost, int iProxyPort,
            Context context) {
        super();
        mClientComCtrl = new ClientComCtrl(sProxyHost, iProxyPort);
    }

    /**
     * Receives constructor MCML data send/receive controller as argument.
     *
     * @param clientComCtrl
     *            MCML data send/receive controller
     */
    public PublicS2SSendThread(ClientComCtrl clientComCtrl) {
        mClientComCtrl = clientComCtrl;
    }

    /**
     * Terminates processing. Terminates processing being executed in loop.
     */
    public void suspendExecute() {
        mFlagExecute = false;
    }

    /**
     * Resumes processing. Resumes processing being  executed in loop.
     */
    public void resumeExecute() {
        mFlagExecute = true;
    }

    /**
     * Gets MCML data send/receive controller.
     *
     * @return MCML data send/receive controller.
     */
    public ClientComCtrl getClientComCtrl() {
        return mClientComCtrl;
    }

    /**
     * Adds MCML request to queue. Added requests are processed in order.
     *
     * @param request
     *            MCML request
     */
    public void addRequest(SpeechTranslationData request) {
        mRequestQueue.add(request);
    }

    /**
     *  Empty request queue.
     */
    public void clearRequest() {
        mRequestQueue.clear();
    }

    /**
     * destroyExecute
     */
    public void destroyExecute() {
        mFlagLiving = false;
    }

    /**
     * Processes MCML request. For processing  results, events are provided as CompleteEvents.
     */
    @Override
    public void run() {
        /** MCML request */
        SpeechTranslationData request = null;
        /** MCMLprocessing results */
        SpeechTranslationData result = null;

        /** Results obtained from MCML server */
        ResponseData response = null;
        /** Binary data obtained from MCML server */
        byte[] responseBinary = null;
        /** XML character string obtained from MCML server */
        String sResponseString = null;

        /** <XX_IN> XML sent to MCML server */
        String sMCML_IN = null;
        /** Binary data sent to MCML server */
        byte[] binary = null;

        while (mFlagLiving) {
            if (!mFlagExecute) {
                //  Processing is not executed during SUSPEND
                try {
                    Thread.sleep(THREAD_ROOP_WAIT);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
                continue;
            }

            request = mRequestQueue.poll(); // Gets requests from request queue

            try {
                if (request != null) {
                    result = new SpeechTranslationData();

                    if (request.isSR_IN()) { // When request is speech recognition
                        // Gets XML and binary from request.
                        sMCML_IN = request.generate();
                        binary = request.getBinaryData();

                        if ((sMCML_IN != null) && (binary != null)) { // Collective transmission
                            Log.i("S2SSample", "Collective transmission " + serverUrl + " XML "
                                    + sMCML_IN);
                            // Sends XML voice binary to MCML server.
                            response = mClientComCtrl.request(serverUrl,
                                    sMCML_IN, binary);
                            // Gets results XML.
                            sResponseString = response.getXML();
                            // Adds results.
                            result.parse(sResponseString);
                            // Sends completion event.
                            requestTaskCompleteEvent(new CompleteEvent(this,
                                    result));
                        } else if ((sMCML_IN != null) && (binary == null)) { // Divided transmission
                                                                             // Head XML
                            Log.i("S2SSample", "Divided transmission head " + serverUrl + " XML "
                                    + sMCML_IN);
                            // Sets marking of divided transmission head to results.
                            result.setSRDivFirstResponse();

                            // Sends XML to MCML server.
                            response = mClientComCtrl.request(serverUrl,
                                    sMCML_IN);

                            // Gets results XML.
                            if (response != null) {
                                sResponseString = response.getXML();
                                // Sets marking of divided transmission head to results.
                                result.parse(sResponseString);

                                // Sends completion event. Receiver is determined as divided transmission head by isSRDivFirstResponse().
                                requestTaskCompleteEvent(new CompleteEvent(
                                        this, result));
                            } else {
                                // When remaining number of times is 0 or when communication failed.
                                result.setError();
                                result.setSR_IN();
                                requestTaskCompleteEvent(new CompleteEvent(
                                        this, result));
                            }

                        } else if ((sMCML_IN == null) && (binary != null)) { // Divided transmission
                                                                             // Data
                            Log.i("S2SSample", "Divided transmission data " + serverUrl);
                            // Sends voice binary to MCML server.
                            response = mClientComCtrl
                                    .request(serverUrl, binary);
                            // Gets XML of results.
                            if (response != null) {
                                sResponseString = response.getXML();
                            } else {
                                // If access key changes or server dies during divided transmission
                            }
                        } else if ((sMCML_IN == null) && (binary == null)) { // Divided transmission
                                                                             // Termination
                            Log.i("S2SSample", "Divided transmission end " + serverUrl);
                            // Sends XML, no binary data, which are markings of divided transmission terminal.
                            response = mClientComCtrl.request(serverUrl);
                            // Gets the results XML.
                            if (response != null) {
                                sResponseString = response.getXML();
                                // Adds results.
                                result.parse(sResponseString);
                                // Sets flag of input source if error.
                                if (result.isError()) {
                                    result.setSR_IN();
                                }
                            } else {
                                // As communication results are error, sets error flag. Comes here when access key changes halfway through.
                                result.setError();
                                result.setSR_IN();
                            }

                            // Sends completion event.
                            requestTaskCompleteEvent(new CompleteEvent(this,
                                    result));
                        }
                    } else { // When request is translation and voice synthesis
                             // Removes XML from request.
                        sMCML_IN = request.generate();
                        Log.i("S2SSample", "Request " + sMCML_IN);

                        // Sends XML to MCML server.
                        response = mClientComCtrl.request(serverUrl, sMCML_IN);
                        // Gets XML of results.
                        sResponseString = response.getXML();
                        // Adds to results.
                        result.parse(sResponseString);

                        // Only occurs during synthesis
                        // Gets binary and adds to results.
                        responseBinary = response.getBinary();
                        if (responseBinary != null) {
                            result.setBinaryData(responseBinary);
                        }

                        // Only for translation
                        // If there are markings of reverse translation in request, also marks reverse translation in results.
                        if (request.isBackTranslation()) {
                            result.setBackTranslation();
                        }

                        // Sets input source flag to clarify which input produced the error.
                        if (result.isError()) {
                            if (request.isMT_IN()) {
                                result.setMT_IN();
                            } else if (request.isSS_IN()) {
                                result.setSS_IN();
                            }
                        }

                        // Sends completion event.
                        requestTaskCompleteEvent(new CompleteEvent(this, result));
                    }
                } else { // When there is no request.
                    // Processing is not executed.
                    try {
                        Thread.sleep(THREAD_ROOP_WAIT);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }
            } catch (java.net.SocketTimeoutException ste) { // on timeout
                sendErrorEvent(request, result);
            } catch (java.net.UnknownHostException uhe) { // when there is no host
                sendErrorEvent(request, result);
            } catch (java.io.FileNotFoundException ffe) { // When URL is incorrect.
                sendErrorEvent(request, result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sends error events
     *
     * @param request
     *            MCML request
     * @param result
     *            MCML results
     */
    private void sendErrorEvent(SpeechTranslationData request,
            SpeechTranslationData result) {
        // Sends completion event with results as error.
        result.setError();

        // Sets input source flag to clarify which input produced the error.
        if (result.isError()) {
            if (request.isMT_IN()) {
                result.setMT_IN();
            } else if (request.isSS_IN()) {
                result.setSS_IN();
            } else if (request.isSR_IN()) {
                result.setSR_IN();
            }
        }
        requestTaskCompleteEvent(new CompleteEvent(this, result));

    }

    /**
     * When MCML request completes, registers events executed.
     *
     * @param completeEventListener
     */
    public void addActionListener(CompleteEventListener completeEventListener) {
        mvArrivalEventListeners.addElement(completeEventListener);
    }

    /**
     * When MCML request completes, deletes events executed.
     *
     * @param completeEventListener
     */
    public void removeActionListener(CompleteEventListener completeEventListener) {
        mvArrivalEventListeners.removeElement(completeEventListener);
    }

    /**
     * When MCML request completes, notifies events.
     *
     * @param completeEvent
     *            CompleteEvent completion event
     */
    protected void requestTaskCompleteEvent(CompleteEvent completeEvent) {
        Vector cloneArrivalEventListeners = (Vector) mvArrivalEventListeners
                .clone();
        Enumeration arrivalEventListenersElements = cloneArrivalEventListeners
                .elements();
        while (arrivalEventListenersElements.hasMoreElements()) {
            CompleteEventListener listener = (CompleteEventListener) arrivalEventListenersElements
                    .nextElement();
            listener.requestTaskComplete(completeEvent);
        }
    }

}
