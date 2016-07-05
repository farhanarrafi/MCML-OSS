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

package jp.go.nict.common.util.android;

/**
 * Gets terminal information
 * 
 * @version 2.11 2011/09/15
 */
public class Information {
    /** Default connector */
    public static final String DEFAULT_CONNECTOR = "-";
    /** Default deletion code */
    public static final String[] DEFAULT_DELETECODE = { "/", "\\", "?", "*",
            ":", "|", "\"", "<", ">", ".", "_", " ", DEFAULT_CONNECTOR };

    /**
     * Gets model name. Gets android.os.Build.MODEL and deletes character specified by sDeleteCode[ ].
     * Gives sConnector to head. Also enables use of multibyte characters.
     * 
     * @param sConnector
     *            String  connection character
     * @param sDeleteCode
     *            Character for deleting String[ ]
     * @return String model name
     */
    public static String getModelName(String sConnector, String[] sDeleteCode) {
        String sModelName = android.os.Build.MODEL;
        String sDeletePattern = "";

        for (int i = 0; i < sDeleteCode.length; i++) {
            sDeletePattern = "\\" + sDeleteCode[i];
            sModelName = sModelName.replaceAll(sDeletePattern, "");
        }

        return sConnector + sModelName;
    }

    /**
     * Gets model name. Gets android.os.Build.MODEL, and deletes characters specified by DEFAULT_DELETECODE.
     * Assigns sConnector to head. Also enables use of multibyte characters.
     * 
     * @param sConnector
     *            String connection character
     * @return String model name
     */
    public static String getModelName(String sConnector) {
        return getModelName(sConnector, DEFAULT_DELETECODE);
    }

    /**
     * Gets model name. Gets android.os.Build.MODEL, and deletes characters specified by DEFAULT_DELETECODE.
     * Gives DEFAULT_CONNECTOR to head. Also enables use of multibyte characters.
     * 
     * @return String model name
     */
    public static String getModelName() {
        return getModelName(DEFAULT_CONNECTOR);
    }
}
