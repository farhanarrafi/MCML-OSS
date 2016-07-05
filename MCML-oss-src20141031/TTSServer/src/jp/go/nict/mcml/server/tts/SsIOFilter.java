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

//package main;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

/**
 * SsIOFilter class.
 *
 *
 */
public class SsIOFilter {
    private static final String SECTION_SETTING = "[setting]";
    private static final String SECTION_DELETE_WORD = "[delete_word]";
    private static final String SECTION_REPLACE_WORD = "[replace_word]";
    private static final String KEY_SETTING_CHAR_TYPE = "CHAR_TYPE=";
    private static final String VALUE_CHAR_TYPE_ASCII = "ASCII";
    private static final String VALUE_CHAR_TYPE_MULTI = "MULTI";
    private static final String DELIMITER_PARTS = "\t";

    private String m_filetFile; // filter file name with path
    private String m_char_type; // normal end mark

    LinkedHashSet<String> m_delete_word; // question word
    LinkedHashMap<String, String> m_replace_word; // replace word

    /**
     * Constructor
     *
     * @param filePath
     */
    public SsIOFilter(String filePath) {
        // initialize
        m_filetFile = "";
        m_char_type = "";
        m_delete_word = new LinkedHashSet<String>();
        m_replace_word = new LinkedHashMap<String, String>();

        if (filePath.length() <= 0) {
            // not need filter
            return;
        }

        m_filetFile = filePath;
        // read filter file
        if (readFilterFile() < 0) {
            return;
        }
    }

    /**
     * Gets new sentence.
     *
     * @param sentence
     * @return New sentence
     */
    public String inputFilter(String sentence) {
        String new_sentence = sentence;
        if (new_sentence.length() <= 0) {
            new_sentence = "";
        }

        if (m_filetFile.length() <= 0) { // not need filter
            return new_sentence;
        }

        // replace
        Iterator<String> it_rep = m_replace_word.keySet().iterator();
        while (it_rep.hasNext()) {
            String key = (it_rep.next()).toString();
            new_sentence = new_sentence.replace(key, m_replace_word.get(key));
        }

        // delete
        Iterator<String> it_del = m_delete_word.iterator();
        while (it_del.hasNext()) {
            String key = (it_del.next()).toString();
            new_sentence = new_sentence.replace(key, "");
        }

        // char filter
        new_sentence = filterCharactor(new_sentence);

        // delete under-bar
        new_sentence = new_sentence.replace("_", " ");

        if (new_sentence.length() <= 0 || isErrorSentence(new_sentence)) {
            new_sentence = "";
        }

        return new_sentence;
    }

    private boolean isErrorSentence(String sentence) {
        try {
            char[] chars = sentence.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                String sTmp = "";
                sTmp += chars[i];
                if ((sTmp.getBytes("UTF-8")).length <= 1) {
                    if (chars[i] >= 0x30 && chars[i] <= 0x39
                            || chars[i] >= 0x41 && chars[i] <= 0x5A
                            || chars[i] >= 0x61 && chars[i] <= 0x7A) { // num or
                                                                       // alpha
                        return false;
                    }
                } else {
                    return false;
                }
            }
        } catch (IOException e) {
        }
        return true;
    }

    private String filterCharactor(String sentence) {
        String new_sentence = "";
        try {
            if (m_char_type.compareToIgnoreCase(VALUE_CHAR_TYPE_ASCII) == 0) { // ASCII
                                                                               // char
                                                                               // only
                char[] chars = sentence.toCharArray();
                for (int i = 0; i < chars.length; i++) {
                    String sTmp = "";
                    sTmp += chars[i];
                    if ((sTmp.getBytes("UTF-8")).length <= 1) {
                        if (chars[i] >= 0x20 && chars[i] <= 0x7e) { // space to
                                                                    // ~
                            new_sentence += chars[i];
                        }
                    }
                }
            } else if (m_char_type.compareToIgnoreCase(VALUE_CHAR_TYPE_MULTI) == 0) { // multi
                                                                                      // char
                                                                                      // only
                char[] chars = sentence.toCharArray();
                for (int i = 0; i < chars.length; i++) {
                    String sTmp = "";
                    sTmp += chars[i];
                    if ((sTmp.getBytes("UTF-8")).length >= 2) {
                        new_sentence += chars[i];
                    }
                }
            } else { // not need filter
                new_sentence = sentence;
            }
        } catch (IOException e) {
        }

        return new_sentence;
    }

    private int readFilterFile() {
        if (m_filetFile.length() <= 0) {
            return 0;
        }
        String sInf = "Loading " + m_filetFile + " ...";
        System.err.println(sInf);
        try {
            FileInputStream is = new FileInputStream(m_filetFile);
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            int iMode = -1;
            while ((line = br.readLine()) != null) {
                if (line.length() <= 0 || line.charAt(0) == '#') {
                    continue;
                }

                if (line.indexOf(SECTION_SETTING) == 0) {
                    iMode = 1;
                    continue;
                } else if (line.indexOf(SECTION_DELETE_WORD) == 0) {
                    iMode = 2;
                    continue;
                } else if (line.indexOf(SECTION_REPLACE_WORD) == 0) {
                    iMode = 3;
                    continue;
                }

                if (iMode <= 0) {
                    continue;
                }

                if (iMode == 1) { // setting
                    if (line.indexOf(KEY_SETTING_CHAR_TYPE) == 0) {
                        String value = line.substring(KEY_SETTING_CHAR_TYPE
                                .length());
                        if (value == null || value.isEmpty()) {
                            m_char_type = "";
                        } else if (value
                                .compareToIgnoreCase(VALUE_CHAR_TYPE_ASCII) == 0) {
                            m_char_type = value;
                        } else if (value
                                .compareToIgnoreCase(VALUE_CHAR_TYPE_MULTI) == 0) {
                            m_char_type = value;
                        } else {
                            String sError = "[ERROR] bad option value. ("
                                    + KEY_SETTING_CHAR_TYPE + ")";
                            System.err.println(sError);
                        }
                    } else {
                        String sError = "[ERROR] bad option value. (" + line
                                + ")";
                        System.err.println(sError);
                    }
                } else if (iMode == 2) { // delete word
                    m_delete_word.add(line);
                } else if (iMode == 3) { // replace word
                    String[] parts = line.split(DELIMITER_PARTS, -1);
                    if (parts.length != 2) {
                        String sError = "[ERROR] bad option value. (" + line
                                + ")";
                        System.err.println(sError);
                    }
                    m_replace_word.put(parts[0], parts[1]);
                }
            }
            br.close();
            isr.close();
            is.close();
        } catch (IOException e) {
            String sError = "[ERROR] can’t open text filter file. ("
                    + m_filetFile + ")";
            System.err.println(sError);
        }
        sInf = "done.";
        System.err.println(sInf);

        return 0;
    }

}
