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

package jp.go.nict.mcml.server.asr;

import jp.go.nict.mcml.serverap.common.EngineCtrl;
import jp.go.nict.mcml.serverap.common.ServerAp;
import jp.go.nict.mcml.serverap.common.ServerApCorpusLogger;
import jp.go.nict.mcml.serverap.common.ServerApLogger;
import jp.go.nict.mcml.serverap.common.ServerApProperties;
import jp.go.nict.mcml.serverap.common.ServerSocketCtrl;

/**
 * ASRServer class.
 *
 *
 */
class ASRServer extends ServerAp {
    // ------------------------------------------
    // private member constant
    // ------------------------------------------
    private static final String TITLE = "ASR Application Program [Ver.3.0.0]";
    private static final String DEFAULT_PROPERTY_FILE_NAME = "ASRServer.properties";
    private static final String PROPERTY_FILE_FLAG = "-propertyfilename";

    // ------------------------------------------
    // private member variables(instance field)
    // ------------------------------------------
    private ModelManager m_ModelManager;

    // ------------------------------------------
    // main routine
    // ------------------------------------------
    public static void main(String[] args) {
        ASRServer ap = null;
        try {
            System.out.printf(TITLE + "\n\n");

            // set input arg.
            String propertyFileName = DEFAULT_PROPERTY_FILE_NAME;
            if (args.length == 2) {
                if (args[0].equals(PROPERTY_FILE_FLAG)) {
                    propertyFileName = args[1];
                }
            }

            // read property file
            ASRProperties propaties = ASRProperties.getInstance();
            propaties.readProperties(propertyFileName);

            // start main routine
            ap = new ASRServer();
            ap.processMain(propaties);
        } catch (Exception exp) {
            ServerApLogger.getInstance().writeException(exp);
        } finally {
            // terminate
            if (ap != null) {
                ap.terminate();
            }
        }

        return;
    }

    // ------------------------------------------
    // public member function (implementation)
    // ------------------------------------------
    public void terminate() {
        try {
            m_ModelManager.terminate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ------------------------------------------
    // protected member function (implementation)
    // ------------------------------------------
    @Override
    protected EngineCtrl createEngineCtrl(int engineNo) {
        ASREngineCtrl eng = new ASREngineCtrl();
        ASRProperties prop = ASRProperties.getInstance();

        try {
            if (!prop.getRPCReceiveModule().isEmpty()
                    && prop.getModelReceivePort() != -1) {
                if (m_ModelManager == null) {
                    m_ModelManager = new ModelManager();
                    if (m_ModelManager
                            .initialize(prop.getModelReceivePort(), prop
                                    .getModelOutputPath(), prop
                                    .getRPCReceiveModule(), prop.getASRParam(0)
                                    .getLMCommandTableFileName(), prop
                                    .getASRParam(0).getAMCommandTableFileName())) {
                        m_ModelManager.start();
                    }
                }
            }

            // start connecting to engine.
            eng.initialize(engineNo, m_ModelManager);
        } catch (Exception e) {
            // Socket error
            ServerApLogger.getInstance().writeException(e);
            eng = null;
        }

        return eng;
    }

    // create and initialize Corpus Logger Class
    @Override
    protected ServerApCorpusLogger createCorpusLogger(
            ServerApProperties properties) {

        // cast to ASRProperties
        ASRProperties prop = (ASRProperties) properties;

        // create ASRCorpusLogger instance.
        ASRCorpusLogger corpusLogger = new ASRCorpusLogger(
                prop.getCorpusLogUtteranceInfo(),
                prop.getCorpusLogBaseDirectryName(), prop.getLanguage1(),
                prop.getCorpusLogFilePrefix(), prop.isEucOutputOn(),
                prop.isUtf8OutputOn(), prop.isInputSpeechdataOutputOn(),
                prop.isModelNameOutputOn(), prop.getCorpusLogRemovedSymbols());

        return corpusLogger;
    }

    @Override
    protected ServerSocketCtrl createServerSocketCtrl(
            ServerApCorpusLogger corpusLogger) {
        return new ServerSocketCtrl(corpusLogger, true, ASRProperties
                .getInstance().getReceiveTimeout());
    }
}
