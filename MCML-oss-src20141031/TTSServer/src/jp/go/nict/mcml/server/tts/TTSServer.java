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

package jp.go.nict.mcml.server.tts;

import jp.go.nict.mcml.serverap.common.CacheManager;
import jp.go.nict.mcml.serverap.common.EngineCtrl;
import jp.go.nict.mcml.serverap.common.ServerAp;
import jp.go.nict.mcml.serverap.common.ServerApCorpusLogger;
import jp.go.nict.mcml.serverap.common.ServerApLogger;
import jp.go.nict.mcml.serverap.common.ServerApProperties;

/**
 * TTSServer class.
 *
 *
 */
public class TTSServer extends ServerAp {
    // ------------------------------------------
    // private member constants
    // ------------------------------------------
    private static final String TITLE = "TTS Server Program [Ver.1.0.0]";
    private static final String DEFAULT_PROPERTY_FILE_NAME = "TTSServer.properties";
    private static final String PROPERTY_FILE_FLAG = "-propertyfilename";

    // ------------------------------------------
    // private member variables
    // ------------------------------------------
    private CacheManager m_CacheManager = null;
    private boolean m_IsInitialized;

    // ------------------------------------------
    // public member functions
    // ------------------------------------------
    /**
     * Main method.
     *
     * @param args
     */
    public static void main(String[] args) {
        CacheManager cacheManager = null;
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
            TTSProperties properties = TTSProperties.getInstance();
            properties.readProperties(propertyFileName);

            // create CacheManager.
            if (properties.isCacheOn()) {
                cacheManager = new CacheManager();
            }

            // start main routine
            TTSServer ap = new TTSServer(cacheManager);
            ap.processMain(properties);
        } catch (Exception exp) {
            ServerApLogger.getInstance().writeException(exp);
        } finally {
            // write ChacheDataListFile.
            if (cacheManager != null) {
                cacheManager.writeCacheDataListFile();
            }
        }

        return;
    }

    /**
     * Constructor
     *
     * @param cacheManager
     */
    public TTSServer(CacheManager cacheManager) {
        m_IsInitialized = false;
        m_CacheManager = cacheManager;
    }

    // ------------------------------------------
    // protected member functions
    // ------------------------------------------
    /** create TTS Controller */
    @Override
    protected EngineCtrl createEngineCtrl(int engineNo) {
        initializeCacheManager();
        TTSEngineCtrl eng = new TTSEngineCtrl(m_CacheManager);

        try {
            // start connecting to engine.
            if (!eng.initialize(engineNo)) {
                eng = null;
                ServerApLogger.getInstance().writeError(
                        "Failed to connect with the engine");
            }
        } catch (Exception e) {
            // connect failed.
            ServerApLogger.getInstance().writeException(e);
            eng = null;
        }

        // normal end
        return eng;
    }

    @Override
    protected ServerApCorpusLogger createCorpusLogger(
            ServerApProperties properties) {
        // cast to TTSProperties
        TTSProperties prop = (TTSProperties) properties;

        // create TTSCorpusLogger instance.
        TTSCorpusLogger corpusLogger = new TTSCorpusLogger(
                prop.getCorpusLogUtteranceInfo(),
                prop.getCorpusLogBaseDirectryName(), prop.getLanguage1(),
                prop.getCorpusLogFilePrefix(),
                prop.getCorpusLogRemovedSymbols());

        return corpusLogger;
    }

    // ------------------------------------------
    // private member functions
    // ------------------------------------------
    private void initializeCacheManager() {
        if (m_CacheManager != null && !m_IsInitialized) {
            TTSProperties properties = TTSProperties.getInstance();
            if (!m_CacheManager.initialize(
                    properties.getCacheBaseDirectryName(),
                    properties.getLanguage1(),
                    properties.getCacheDataListFileName(),
                    properties.getMaxCacheCount(), properties.getDeleteCount())) { // failed
                                                                                   // Initialize.
                ServerApLogger.getInstance().writeError(
                        "Failed to initializeCacheManager()");
                m_CacheManager = null;
            }
        }
    }
}
