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

import java.util.HashMap;

/**
 * MTLanguageInfo class.
 *
 */
public class MTLanguageInfo {

    // ------------------------------------------
    // private member variable
    // ------------------------------------------
    private String m_Delimiter;
    private String m_StringCode;
    private String m_TextFilterFileName;
    private HashMap<String, String> m_DirectionCommandMap;

    // ------------------------------------------
    // Constructor
    // ------------------------------------------
    /**
     * Default constructor
     */
    public MTLanguageInfo() {

        m_Delimiter = "";
        m_StringCode = "";
        m_TextFilterFileName = "";
        m_DirectionCommandMap = null;
    }

    // ------------------------------------------
    // public member function
    // ------------------------------------------
    /**
     * Sets DirectionCommandMap.
     *
     * @param directionCommandMap
     */
    public void setDirectionCommandMap(
            HashMap<String, String> directionCommandMap) {

        this.m_DirectionCommandMap = directionCommandMap;

        return;
    }

    /**
     * Gets DirectionCommand.
     *
     * @param targetLanguage
     * @return DirectionCommand
     */
    public String getDirectionCommand(String targetLanguage) {

        String directionCommand = "";

        directionCommand = m_DirectionCommandMap.get(targetLanguage);

        return directionCommand;
    }

    /**
     * Sets Delimiter.
     *
     * @param delimiter
     */
    public void setDelimiter(String delimiter) {
        this.m_Delimiter = delimiter;
    }

    /**
     * Sets Stringcode.
     *
     * @param stringcode
     */
    public void setStringcode(String stringcode) {
        this.m_StringCode = stringcode;
    }

    /**
     * Sets Textfilterfile.
     *
     * @param textfilterfile
     */
    public void setTextfilterfile(String textfilterfile) {
        this.m_TextFilterFileName = textfilterfile;
    }

    /**
     * Gets Delimiter.
     *
     * @return Delimiter
     */
    public String getDelimiter() {
        return m_Delimiter;
    }

    /**
     * Gets Stringcode.
     *
     * @return Stringcode
     */
    public String getStringcode() {
        return m_StringCode;
    }

    /**
     * GetsTextfilterfile.
     *
     * @return Textfilterfile
     */
    public String getTextfilterfile() {
        return m_TextFilterFileName;
    }
}
