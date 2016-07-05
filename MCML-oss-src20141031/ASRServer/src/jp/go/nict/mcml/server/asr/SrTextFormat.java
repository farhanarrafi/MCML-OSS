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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

/**
 * SrTextFormat class.
 *
 */
public class SrTextFormat {
    private static final String SECTION_SETTING = "[setting]";
    private static final String SECTION_REPLACE_WORD_1 = "[replace_word-1]";
    private static final String SECTION_REPLACE_WORD_2 = "[replace_word-2]";
    private static final String SECTION_QUESTION_WORD_1 = "[question_word-1]";
    private static final String SECTION_QUESTION_WORD_2 = "[question_word-2]";
    private static final String SECTION_QUESTION_WORD_3 = "[question_word-3]";
    private static final String SECTION_DELETE_WORDID_1 = "[delete_wordid-1]";
    private static final String KEY_SETTING_CAPITALIZE = "CAPITALIZE=";
    private static final String KEY_SETTING_PERIOD = "PERIOD=";
    private static final String KEY_SETTING_QUESTION_MARK = "QUESTION_MARK=";
    private static final String KEY_SETTING_WORD_CONNECTOR = "WORD_CONNECTOR=";
    private static final String VALUE_ON = "ON";
    private static final String VALUE_OFF = "OFF";
    private static final String DELIMITER_PARTS = "\t";
    private static final String DELIMITER_WORD = "/";

    // splite delimiter because of composite word
    private static final String DELEMITER_UNDERBAR = "_";

    private String m_filetFile; // filter file name with path
    private boolean m_capitalize; // capitalize flag
    private String m_period; // normal end mark
    private String m_question_mark; // question end mark

    LinkedHashMap<String, String> m_replace_word_1; // replace word-1
    LinkedHashMap<String, String> m_replace_word_2; // replace word-2
    LinkedHashSet<String> m_question_word_1; // question word-1
    LinkedHashSet<String> m_question_word_2; // question word-2
    LinkedHashSet<String> m_question_word_3; // question word-3
    LinkedHashMap<String, String> m_delete_wordid_1; // delete wordid-1 (delete
                                                     // interjection)

    /**
     * Constructor
     *
     * @param filePath
     */
    public SrTextFormat(String filePath) {
        // initialize
        m_filetFile = "";
        m_capitalize = false;
        m_period = "";
        m_question_mark = "";
        m_replace_word_1 = new LinkedHashMap<String, String>();
        m_replace_word_2 = new LinkedHashMap<String, String>();
        m_question_word_1 = new LinkedHashSet<String>();
        m_question_word_2 = new LinkedHashSet<String>();
        m_question_word_3 = new LinkedHashSet<String>();
        m_delete_wordid_1 = new LinkedHashMap<String, String>();

        if (filePath.length() <= 0) {
            // not need format
            return;
        }

        m_filetFile = filePath;
        // read filter file
        if (readFilterFile() < 0) {
            return;
        }
    }

    /**
     * formatSentence
     *
     * @param wordList
     * @param sDelimiter
     * @return sentence
     */
    public String formatSentence(ArrayList<String> wordList, String sDelimiter) {
        String sentence = "";
        if (wordList.size() <= 0) {
            return sentence;
        }

        if (m_filetFile.length() <= 0) { // not need format
            sentence = "";
            for (int i = 0; i < wordList.size(); i++) {
                sentence += wordList.get(i);
                if (i < wordList.size() - 1) {
                    sentence += sDelimiter;
                }
            }
            return sentence;
        }

        // get end mark
        String end_mark = m_period;

        // is question sentence?
        if (isQuestionSentence(wordList)) {
            end_mark = m_question_mark;
        }

        // replace word 2
        replaceWord2(wordList);

        // create sentence without end mark
        sentence = "";
        for (int i = 0; i < wordList.size(); i++) {
            sentence += wordList.get(i);
            if (i < wordList.size() - 1) {
                sentence += sDelimiter;
            }
        }

        // replace word 1
        sentence = replaceWord1(sentence);

        // capitalize
        sentence = capitalizeSentence(sentence);

        // add end mark
        sentence = addEndMark(sentence, end_mark);

        // delete under-bar
        sentence = sentence.replace(DELEMITER_UNDERBAR, " ");

        return sentence;
    }

    /**
     * deleteWordID
     *
     * @param wordList
     * @param wordIdList
     * @return int
     */
    public int deleteWordID(ArrayList<WordValue> wordList,
            ArrayList<WordValue> wordIdList) {
        for (int i = 0; i < wordIdList.size(); i++) {
            if (m_filetFile.length() <= 0) { // not delete
                return 0;
            }
            if (m_delete_wordid_1.size() <= 0) { // not delete
                return 0;
            }

            if (!m_delete_wordid_1.containsKey(wordIdList.get(i).getWord()
                    .replaceAll(" ", ""))) {
                continue;
            }

            wordIdList.remove(i);
            wordList.remove(i);
            i--;
        }
        return 0;
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
                } else if (line.indexOf(SECTION_REPLACE_WORD_1) == 0) {
                    iMode = 2;
                    continue;
                } else if (line.indexOf(SECTION_REPLACE_WORD_2) == 0) {
                    iMode = 3;
                    continue;
                } else if (line.indexOf(SECTION_QUESTION_WORD_1) == 0) {
                    iMode = 4;
                    continue;
                } else if (line.indexOf(SECTION_QUESTION_WORD_2) == 0) {
                    iMode = 5;
                    continue;
                } else if (line.indexOf(SECTION_QUESTION_WORD_3) == 0) {
                    iMode = 6;
                    continue;
                } else if (line.indexOf(SECTION_DELETE_WORDID_1) == 0) {
                    iMode = 7;
                    continue;
                }

                if (iMode <= 0) {
                    continue;
                }

                if (iMode == 1) { // setting
                    if (line.indexOf(KEY_SETTING_CAPITALIZE) == 0) {
                        String value = line.substring(KEY_SETTING_CAPITALIZE
                                .length());
                        if (value.compareToIgnoreCase(VALUE_ON) == 0) {
                            m_capitalize = true;
                        } else if (value.compareToIgnoreCase(VALUE_OFF) == 0) {
                            m_capitalize = false;
                        } else {
                            String sError = "[ERROR] bad option value. ("
                                    + KEY_SETTING_CAPITALIZE + ")";
                            System.err.println(sError);
                        }
                    } else if (line.indexOf(KEY_SETTING_PERIOD) == 0) {
                        if (line.length() > KEY_SETTING_PERIOD.length()) {
                            m_period = line.substring(KEY_SETTING_PERIOD
                                    .length());
                        } else {
                            m_period = "";
                        }
                    } else if (line.indexOf(KEY_SETTING_QUESTION_MARK) == 0) {
                        if (line.length() > KEY_SETTING_QUESTION_MARK.length()) {
                            m_question_mark = line
                                    .substring(KEY_SETTING_QUESTION_MARK
                                            .length());
                        } else {
                            m_question_mark = "";
                        }
                    } else {
                        String sError = "[ERROR] bad option value. (" + line
                                + ")";
                        System.err.println(sError);
                    }
                } else if (iMode == 2) { // replace word-1
                    String[] parts = line.split(DELIMITER_PARTS, -1);
                    if (parts.length != 2) {
                        String sError = "[ERROR] bad option value. (" + line
                                + ")";
                        System.err.println(sError);
                    }
                    m_replace_word_1.put(parts[0], parts[1]);
                } else if (iMode == 3) { // replace word-2
                    String[] parts = line.split(DELIMITER_PARTS, -1);
                    if (parts.length != 2) {
                        String sError = "[ERROR] bad option value. (" + line
                                + ")";
                        System.err.println(sError);
                    }
                    m_replace_word_2.put(parts[0], parts[1]);
                } else if (iMode == 4) { // question word-1
                    m_question_word_1.add(line);
                } else if (iMode == 5) { // question word-2
                    m_question_word_2.add(line);
                } else if (iMode == 6) { // question word-3
                    m_question_word_3.add(line);
                } else if (iMode == 7) { // delete wordid-1
                    String[] parts = line.split(DELIMITER_PARTS, -1);
                    if (parts.length != 2) {
                        String sError = "[ERROR] bad option value. (" + line
                                + ")";
                        System.err.println(sError);
                    }
                    m_delete_wordid_1.put(parts[0], parts[1]);
                }
            }
            br.close();
            isr.close();
            is.close();
        } catch (IOException e) {
            String sError = "[ERROR] can't open text filter file. ("
                    + m_filetFile + ")";
            System.err.println(sError);
        }
        sInf = "done.";
        System.err.println(sInf);

        return 0;
    }

    private String addEndMark(String sentenceOrg, String endMark) {
        String sentence = sentenceOrg;
        if (!m_period.isEmpty() && sentence.length() >= m_period.length()) {
            int pos;
            while ((pos = sentence.lastIndexOf(m_period)) == sentence.length()
                    - m_period.length()) {
                sentence = sentence.substring(0, pos);
            }
        }

        if (!m_question_mark.isEmpty()
                && sentence.length() >= m_question_mark.length()) {
            int pos;
            while ((pos = sentence.lastIndexOf(m_question_mark)) == sentence
                    .length() - m_question_mark.length()) {
                sentence = sentence.substring(0, pos);
            }
        }

        sentence += endMark;
        return sentence;
    }

    private String capitalizeSentence(String sentenceOrg) {
        String sentence = sentenceOrg;
        if (!m_capitalize || sentence.length() <= 0) {
            return sentence;
        }

        String top = sentence.substring(0, 1);
        sentence = top.toUpperCase() + sentence.substring(1);
        return sentence;
    }

    private String replaceWord1(String sentenceOrg) {
        String sentence = sentenceOrg;
        if (m_replace_word_1.size() <= 0) {
            return sentence;
        }

        Iterator<String> it = m_replace_word_1.keySet().iterator();
        while (it.hasNext()) {
            String key = (it.next()).toString();
            int pos = 0;
            int spos = 0;
            String new_sentence = "";
            while ((pos = sentence.indexOf(key, spos)) >= 0) {
                new_sentence += sentence.substring(spos, pos);
                if (isAloneWord(sentence, key, pos)) {
                    new_sentence += m_replace_word_1.get(key);
                } else {
                    new_sentence += key;
                }
                spos = pos + key.length();
            }
            if (spos < sentence.length()) {
                new_sentence += sentence.substring(spos);
            }

            sentence = new_sentence;
        }
        return sentence;
    }

    private boolean isAloneWord(String sentence, String key, int pos) {
        int before_flag = 0;
        int after_flag = 0;
        if (pos <= 0) {
            before_flag = 1;
        } else {
            char c = sentence.charAt(pos - 1);
            // is ascii (num or alpha)
            if (c >= 0x30 && c <= 0x39 || c >= 0x41 && c <= 0x5a || c >= 0x61
                    && c <= 0x7a) {
            } else {
                before_flag = 1;
            }
        }

        if (pos + key.length() >= sentence.length()) {
            after_flag = 1;
        } else {
            char c = sentence.charAt(pos + key.length());
            // is ascii (num or alpha)
            if (c >= 0x30 && c <= 0x39 || c >= 0x41 && c <= 0x5a || c >= 0x61
                    && c <= 0x7a) {
            } else {
                after_flag = 1;
            }
        }

        if (before_flag != 0 && after_flag != 0) {
            return true;
        }
        return false;
    }

    private void replaceWord2(ArrayList<String> wordList) {
        if (m_replace_word_2.size() <= 0) {
            return;
        }

        for (int i = 0; i < wordList.size(); i++) {
            if (!m_replace_word_2.containsKey(wordList.get(i))) {
                continue;
            }
            String value = (m_replace_word_2.get(wordList.get(i))).toString();
            wordList.set(i, value);
        }

    }

    private boolean isQuestionSentence(ArrayList<String> wordList) {
        // question_word-3
        if (isExistQuestionWordAtAnyPlace(wordList)) {
            return true;
        }

        // question_word-1
        if (isExistQuestionWordAtTopOfSentence(wordList)) {
            return true;
        }

        // question_word-2
        if (isExistQuestionWordAtEndOfSentence(wordList)) {
            return true;
        }

        return false;
    }

    private boolean isExistQuestionWordAtAnyPlace(ArrayList<String> wordList) {
        if (m_question_word_3.size() <= 0) {
            return false;
        }

        for (int i = 0; i < wordList.size(); i++) {
            Iterator<String> it = m_question_word_3.iterator();
            while (it.hasNext()) {
                String qword = it.next().toString();
                String[] q_words = qword.split(DELIMITER_WORD, -1);
                if (q_words.length > wordList.size() - i) {
                    continue;
                }
                int not_match_flag = 0;
                for (int j = 0; j < q_words.length; j++) {
                    if (q_words[j].compareTo(wordList.get(i + j)) != 0) {
                        not_match_flag = 1;
                        break;
                    }
                }
                if (not_match_flag == 0) { // question sentence
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isExistQuestionWordAtTopOfSentence(
            ArrayList<String> wordList) {
        if (m_question_word_1.size() <= 0) {
            return false;
        }

        Iterator<String> it = m_question_word_1.iterator();
        while (it.hasNext()) {
            String qword = it.next().toString();
            String[] q_words = qword.split(DELIMITER_WORD, -1);
            if (q_words.length > wordList.size()) {
                continue;
            }
            int not_match_flag = 0;
            for (int j = 0; j < q_words.length; j++) {
                if (q_words[j].compareTo(wordList.get(j)) != 0) {
                    not_match_flag = 1;
                    break;
                }
            }
            if (not_match_flag == 0) { // question sentence
                return true;
            }
        }
        return false;
    }

    private boolean isExistQuestionWordAtEndOfSentence(
            ArrayList<String> wordList) {
        if (m_question_word_2.size() <= 0) {
            return false;
        }

        Iterator<String> it = m_question_word_2.iterator();
        while (it.hasNext()) {
            String qword = it.next().toString();
            String[] q_words = qword.split(DELIMITER_WORD, -1);
            if (q_words.length > wordList.size()) {
                continue;
            }
            int not_match_flag = 0;
            int i = wordList.size() - 1;
            for (int j = q_words.length - 1; j >= 0; j--) {
                if (q_words[j].compareTo(wordList.get(i)) != 0) {
                    not_match_flag = 1;
                    break;
                }
                i--;
            }
            if (not_match_flag == 0) { // question sentence
                return true;
            }
        }
        return false;
    }
}
