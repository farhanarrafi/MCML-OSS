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

//-------------------------------------------------------------------
//Ver.3.0
//2011/12/06
//-------------------------------------------------------------------

package jp.go.nict.mcml.serverap.common;

import java.util.ArrayList;
import java.util.Iterator;

import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * ServerAp abstract class.
 * 
 */
public abstract class ServerAp implements SignalHandler {
    // ------------------------------------------
    // private member variable
    // ------------------------------------------
    private ArrayList<EngineCtrl> m_EngineCtrlList;
    private boolean m_CaughtSignal;
    private long m_RequestLimitThreshold;
    private boolean m_IsEnableRequestTimeout;
    protected ServerApCorpusLogger m_CorpusLogger;

    // ------------------------------------------
    // public member function
    // ------------------------------------------
    /**
     * handle
     */
    @Override
    public synchronized void handle(Signal signal) {
        ServerApLogger.getInstance().writeDebug("caught signal: " + signal);
        m_CaughtSignal = true;
        notifyAll();
    }

    /**
     * processMain
     * 
     * @param properties
     * @throws Exception
     */
    public synchronized void processMain(ServerApProperties properties)
            throws Exception {
        // create Corpus Logger.
        ServerApCorpusLogger corpusLogger = createCorpusLogger(properties);

        // get request limit and request time out parameter
        m_RequestLimitThreshold = properties.getRequestLimitThreshold();
        m_IsEnableRequestTimeout = properties.getIsEnableRequestTimeout();

        m_CaughtSignal = false;
        // create ServerSocket.
        ServerSocketCtrl severSocketCtrl = createServerSocketCtrl(corpusLogger);
        RequestTimeoutWatcher requestTimeoutWatcher = null;

        try {
            // initialize logger
            ServerApLogger.getInstance().initialize(
                    properties.getLogBaseFileName(), properties.getLogLevel());

            // set signal handler
            setSignalHandler("INT"); // for Ctrl+C
            setSignalHandler("TERM"); // for kill

            // create RequestTimeoutWatcher thread
            if (m_IsEnableRequestTimeout) {
                requestTimeoutWatcher = new RequestTimeoutWatcher(
                        properties.getRequestTimeout(),
                        properties.getRequestTimeoutInterval());
                requestTimeoutWatcher.startWatcher();
                ServerApLogger.getInstance().writeDebug(
                        "started timeout watcher timeout:"
                                + properties.getRequestTimeout() + " interval="
                                + properties.getRequestTimeoutInterval());
            }

            // cleate engine controller list
            m_EngineCtrlList = new ArrayList<EngineCtrl>();

            // start engine controller (worker thread)
            for (int i = 0; i < properties.getEngineNumber(); i++) {
                EngineCtrl eng = createEngineCtrl(i);

                if (eng != null) {
                    // create EngineCtrl Success.
                    m_EngineCtrlList.add(eng);
                    eng.start();
                }
            }

            // no EngineController
            if (0 < m_EngineCtrlList.size()) {
                // start acceptation thread
                severSocketCtrl.startAcceptation(properties.getServletPort());

                // wait for terminate
                while (true) {
                    wait(1000);

                    if (!m_CaughtSignal) {
                        // EngineCtrl thread processing check.
                        checkProcessingEngine();
                        if (m_EngineCtrlList.size() == 0) {
                            ServerApLogger.getInstance().writeDebug(
                                    "All engines closed. terminate");
                            // All EngineCtrl End.
                            break;
                        }
                    } else {
                        // caught Signal.
                        break;
                    }
                }
            }
        } finally {
            // stop acceptation thread
            severSocketCtrl.terminate();
            // stop engine controller
            terminateEnginrCtrl();
            // stop request time out watcher thread
            if (requestTimeoutWatcher != null) {
                requestTimeoutWatcher.terminate();
            }
        }

        return;
    }

    // ------------------------------------------
    // private member function
    // ------------------------------------------
    private void setSignalHandler(String signalName) {
        Signal signalInt = new Signal(signalName);
        Signal.handle(signalInt, this);
    }

    private void terminateEnginrCtrl() throws Exception {
        Iterator<EngineCtrl> it = m_EngineCtrlList.iterator();
        while (it.hasNext()) {
            EngineCtrl eng = it.next();
            eng.terminate();
        }
    }

    private void checkProcessingEngine() throws Exception {
        // check End to Begin.
        for (int i = m_EngineCtrlList.size() - 1; i >= 0; i--) {
            EngineCtrl engCtrl = m_EngineCtrlList.get(i);
            if (!engCtrl.isProcessing()) {
                engCtrl.terminate();
                m_EngineCtrlList.remove(i);
            }
        }
    }

    // ------------------------------------------
    // protected member functions
    // ------------------------------------------
    protected abstract EngineCtrl createEngineCtrl(int engineNo);

    protected abstract ServerApCorpusLogger createCorpusLogger(
            ServerApProperties properties);

    protected ServerSocketCtrl createServerSocketCtrl(
            ServerApCorpusLogger corpusLogger) {
        return new ServerSocketCtrl(corpusLogger, m_RequestLimitThreshold,
                m_IsEnableRequestTimeout);
    }
}
