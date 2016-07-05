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

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.go.nict.mcml.serverap.common.NBestStreamReader;

/**
 * ASRNBestStreamReader stream read class.
 *
 */
public class ASRNBestStreamReader extends NBestStreamReader {
    // ------------------------------------------
    // private member constants(class field)
    // ------------------------------------------
    private static final String LANGUAGE_MODEL_KEY = "lmname=";
    private static final String ACOUSTIC_MODEL_KEY = "amname=";

    // The pattern of "amname=( * ))".
    private static final Pattern ACOUSTIC_MODEL_PATTERN = Pattern
            .compile(ACOUSTIC_MODEL_KEY + "(\\S*)");

    // ------------------------------------------
    // private member variable(instance field)
    // ------------------------------------------
    private String m_AcousticModelName;

    // ------------------------------------------
    // public member functions
    // ------------------------------------------
    /**
     * Constructor
     *
     * @param stream
     */
    public ASRNBestStreamReader(InputStream stream) {
        super(stream);
        m_AcousticModelName = null;
    }

    /**
     * Gets EngineInfoFromNBest.
     */
    @Override
    public void getEngineInfoFromNBest(String nbest) {
        if (nbest.isEmpty()) {
            // empty line.
            return;
        }

        if (getLanguageModelName(nbest)) {
            // NBest is Language Model Name.
            return;
        }
        getAcousticModelName(nbest);

        // normal end.
        return;
    }

    /**
     * Gets AcousticModelName.
     *
     * @return AcousticModelName
     */
    public String getAcousticModelName() {
        return m_AcousticModelName;
    }

    // ------------------------------------------
    // private member functions
    // ------------------------------------------
    private boolean getLanguageModelName(String nbest) {
        // check "lmname=" is the head.
        int lmNameIndex = nbest.indexOf(LANGUAGE_MODEL_KEY);
        if (lmNameIndex == -1) {
            // no "lmname="
            return false;
        }
        return true;
    }

    private boolean getAcousticModelName(String nbest) {
        // check "amname=" is the head.
        int amNameIndex = nbest.indexOf(ACOUSTIC_MODEL_KEY);
        if (amNameIndex == -1) {
            // no "amname="
            return false;
        }

        // get Acoustic Model Name.
        Matcher acousticModelMatcher = ACOUSTIC_MODEL_PATTERN.matcher(nbest);
        if (acousticModelMatcher.find()) {

            m_AcousticModelName = nbest.substring(ACOUSTIC_MODEL_KEY.length());
        }

        return true;
    }
}
