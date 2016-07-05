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

import java.io.File;

import jp.go.nict.mcml.serverap.common.ResultWriter;
import jp.go.nict.mcml.xml.MCMLStatics;

import org.apache.log4j.Logger;

import com.MCML.DataType;
import com.MCML.SentenceSequenceType;
import com.MCML.SentenceType;

/**
 * ASRResultWriter class (for ASR server).
 * 
 * @version 4.0
 * @since 20120921
 */
public class ASRResultWriter extends ResultWriter {
    private static final Logger LOG = Logger.getLogger(ASRResultWriter.class
            .getName());
    // ------------------------------------------
    // private member constans(class field)
    // ------------------------------------------
    private static final String DIRECTORY_EUC = "EUC";
    private static final String DIRECTORY_UTF_8 = "UTF-8";
    private static final String EXTENTION_EUC = ".txt";
    private static final String EXTENTION_UTF_8 = ".utf8";

    // ------------------------------------------
    // private member valiables(instance field)
    // ------------------------------------------
    private boolean m_IsEUC;

    // ------------------------------------------
    // public member constans(class field)
    // ------------------------------------------
    /** constructer */
    public ASRResultWriter(boolean isEUC) {
        super();
        m_IsEUC = isEUC;
    }

    /**
     * Creates directory.
     * 
     * @param parentdirectoryPath
     * @return {@code true} if output directory exists, otherwise {@code false}
     */
    @Override
    public boolean createDirectory(String parentdirectoryPath) {
        // create OutputDirectory Name.
        if (m_IsEUC) {
            m_OutputDirectory = new File(parentdirectoryPath, DIRECTORY_EUC);
        } else {
            m_OutputDirectory = new File(parentdirectoryPath, DIRECTORY_UTF_8);
        }

        // is OutputDirectory?
        if (!m_OutputDirectory.exists()) {
            // not exist OutputDirectory.
            // create OutputDirectory.
            return m_OutputDirectory.mkdirs();
        }
        return true;
    }

    /**
     * pick out result string from dataType
     * 
     * @param dataType
     * @return String
     */
    public String pickOutResult(DataType dataType) {
        String result = "";

        try {
            // get NBest Count.
            int nbestCount = dataType.Text.first().SentenceSequence.count();
            for (int i = 0; i < nbestCount; i++) {
                SentenceSequenceType sentenceSequenceType = dataType.Text
                        .first().SentenceSequence.at(i);
                int sCount = sentenceSequenceType.Sentence.count();

                for (int j = 0; j < sCount; j++) {
                    // get sentence value.
                    SentenceType sentenceType = sentenceSequenceType.Sentence
                            .at(j);
                    String tempString = "";
                    if (sentenceType.Surface.exists()) {
                        tempString = sentenceType.Surface.first().getValue();
                    }

                    result += tempString;
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return result;
    }

    // ------------------------------------------
    // protected member function
    // ------------------------------------------
    @Override
    protected String selectExtension() {
        if (m_IsEUC) {
            return EXTENTION_EUC;
        }
        return EXTENTION_UTF_8;
    }

    /** get text charset */
    @Override
    protected String selectCharset(String languageinfo) {
        String charset = "";

        // charset is utf8
        if (!m_IsEUC) {
            charset = MCMLStatics.CHARSET_UTF_8;

            // select charset according to language
        } else {
            if (languageinfo.equalsIgnoreCase(MCMLStatics.LANGUAGE_JAPANESE)
                    || languageinfo
                            .equalsIgnoreCase(MCMLStatics.LANGUAGE_ENGLISH)) { // EUC_JP(Japanease
                                                                               // and
                                                                               // English).
                charset = MCMLStatics.CHARSET_EUC_JP;
            } else if (languageinfo
                    .equalsIgnoreCase(MCMLStatics.LANGUAGE_CHINESE)) { // EUC_CN(Chinease).
                charset = MCMLStatics.CHARSET_EUC_CN;

            } else if (languageinfo
                    .equalsIgnoreCase(MCMLStatics.LANGUAGE_KOREAN)) { // EUC_KR(Korean).
                charset = MCMLStatics.CHARSET_EUC_KR;

            } else {
                // invalid CharSet(Unexpected Error).
                charset = MCMLStatics.CHARSET_UTF_8;
            }
        }
        return charset;
    }
}
