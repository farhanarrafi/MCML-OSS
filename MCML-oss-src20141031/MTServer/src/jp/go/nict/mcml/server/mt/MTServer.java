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

package jp.go.nict.mcml.server.mt;

import jp.go.nict.mcml.serverap.common.EngineCtrl;
import jp.go.nict.mcml.serverap.common.ServerAp;
import jp.go.nict.mcml.serverap.common.ServerApCorpusLogger;
import jp.go.nict.mcml.serverap.common.ServerApLogger;
import jp.go.nict.mcml.serverap.common.ServerApProperties;

/**
 * MTServer class.
 *
 */
public class MTServer extends ServerAp {
    // ------------------------------------------
    // private member constant
    // ------------------------------------------
    private static final String TITLE = "MT Application Program [Ver.3.0.0]";
    private static final String DEFAULT_PROPERTY_FILE_NAME = "MTServer.properties";
    private static final String PROPERTY_FILE_FLAG = "-propertyfilename";

    /**
     * Main method
     *
     * @param args
     */
    public static void main(String[] args) {
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
            MTProperties propaties = MTProperties.getInstance();
            propaties.readProperties(propertyFileName);

            // start main routine
            MTServer ap = new MTServer();
            ap.processMain(propaties);
        } catch (Exception exp) {
            ServerApLogger.getInstance().writeException(exp);
        }

        return;
    }

    // create MT Controller
    @Override
    protected EngineCtrl createEngineCtrl(int engineNo) {
        MTEngineCtrl eng = new MTEngineCtrl();

        try {
            eng.initialize(engineNo);
        } catch (Exception e) {
            // Socket error
            ServerApLogger.getInstance().writeException(e);
            eng = null;
        }

        return eng;
    }

    @Override
    protected ServerApCorpusLogger createCorpusLogger(
            ServerApProperties properties) {

        // cast to MTProperties
        MTProperties prop = (MTProperties) properties;

        // get language string
        String languages = "";

        for (int i = 0; i < prop.getLanguageInfoManager().getLanguageList()
                .size(); i++) {

            languages += prop.getLanguageInfoManager().getLanguageList().get(i);
        }

        // create MTCorpusLogger instance.
        MTCorpusLogger corpusLogger = new MTCorpusLogger(
                prop.getCorpusLogUtteranceInfo(),
                prop.getCorpusLogBaseDirectryName(), languages,
                prop.getCorpusLogFilePrefix(),
                prop.getCorpusLogRemovedSymbols());

        return corpusLogger;
    }
}
