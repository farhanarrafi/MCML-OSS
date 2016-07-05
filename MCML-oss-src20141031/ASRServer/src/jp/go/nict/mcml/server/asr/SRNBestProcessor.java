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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.go.nict.mcml.servlet.MCMLException;
import jp.go.nict.mcml.servlet.MCMLStatics;
import jp.go.nict.mcml.xml.types.ChunkType;
import jp.go.nict.mcml.xml.types.POSType;
import jp.go.nict.mcml.xml.types.SentenceSequenceType;
import jp.go.nict.mcml.xml.types.SentenceType;
import jp.go.nict.mcml.xml.types.SurfaceType;
import jp.go.nict.mcml.xml.types.SurfaceType2;

/**
 * SRNBestProcessor class.
 *
 */
public class SRNBestProcessor {
    // ------------------------------------------
    // private member constant
    // ------------------------------------------
    private static final String DELIMITER_UTTERANCE = "\n" + "UTTERANCE=";
    private static final String DELIMITER_NBEST = "\n" + "NBEST=";
    private static final String DELIMITER_ORDER = "\n" + "ORDER=";
    private static final String DELIMITER_ORDER_ELEM = " ";
    private static final String SR_WORDS = "WORDS=";
    private static final String SR_WORDIDS = "wordids=";
    private static final String SR_UTT_START = "UTT-START";
    private static final String SR_UTT_END = "UTT-END";
    /* Add 2010/10/14 tmuraka for S-00812 start */
    private static final String SR_UTT_START_SYMPLE = "|S|";
    private static final String SR_UTT_END_SYMPLE = "|E|";
    private static final String SR_NBEST_PARSETYPE_KO2010 = "ko_2010";
    private static final String WORD_PREFIX_S = "S_";
    private static final String WORD_PREFIX_L = "L_";
    private static final String WORD_PREFIX_C = "C_";
    private static final String WORD_PREFIX_R = "R_";
    /* Add 2010/10/14 tmuraka for S-00812 end */
    private static final String SR_SENT_START_END = "SENT-START-END";
    private static final String DELIMITER_WORD_SPLITER = "/";
    private static final String DELIMITER_WORD_SPLITER2 = "+";
    private static final String DELIMITER_WORD_SPLITER3 = "&";
    // private static final String DELIMITER_WORD_SPLITER2 = "|";

    private static final String SR_UTIME = "utime=";
    private static final String SR_CONFIDENCE = "confidence=";
    private static final String SR_CONFIDENCES = "confidences=";
    private static final String SR_GWPP = "GWPP=";
    private static final String SR_GUPP = "GUPP=";
    private static final String SR_PERIOD_JC = "。";
    private static final String SR_PERIOD_E = ".";

    private static final String XML_LANGUAGE_JA = "ja";
    private static final String XML_LANGUAGE_ZH = "zh";

    private static final String SR_VARS = "vars=";
    private static final String SR_TIMES = "times=";

    // The pattern of "utime=([^ ]*)".
    private static final Pattern UTIME_PATTERN = Pattern.compile("\\s"
            + SR_UTIME + "(\\S*)\\s");

    // The pattern of "confidence=([^ ]*) ".
    private static final Pattern CONFIDENCE_PATTERN = Pattern.compile("\\s"
            + SR_CONFIDENCE + "(\\S*)\\s");

    // The pattern of "confidences=([^ ]*) ".
    private static final Pattern CONFIDENCES_PATTERN = Pattern.compile("\\s"
            + SR_CONFIDENCES + "(\\S*)");

    // The pattern of "GWPP=([^ ]*) ".
    private static final Pattern GWPP_PATTERN = Pattern.compile("\\s" + SR_GWPP
            + "(\\S*)");

    // The pattern of "GUPP=([^ ]*) ".
    private static final Pattern GUPP_PATTERN = Pattern.compile("\\s" + SR_GUPP
            + "(\\S*)\\s");

    /**
     * private member variable
     */
    private String m_Period;
    private String m_Delimiter;
    private boolean m_IsGwppOn;
    private SrTextFormat m_SrTextFormat;
    private ConvertNumKo m_ConvertNumKo;

    /**
     * public member function
     *
     * @param language
     * @param delimiter
     * @param isGwppOn
     * @param textFormat
     * @param convertNumKo
     */
    public SRNBestProcessor(String language, String delimiter,
            boolean isGwppOn, SrTextFormat textFormat, ConvertNumKo convertNumKo) {
        if (language.equalsIgnoreCase(XML_LANGUAGE_JA)) {
            m_Period = SR_PERIOD_JC;
        } else if (language.equalsIgnoreCase(XML_LANGUAGE_ZH)) {
            m_Period = SR_PERIOD_JC;
        } else {
            m_Period = SR_PERIOD_E;
        }
        m_Delimiter = delimiter;
        m_SrTextFormat = textFormat;
        m_IsGwppOn = isGwppOn;
        m_ConvertNumKo = convertNumKo;
    }

    /**
     * Analyzes nbest results, and Gets SentenceSequenceType list.
     *
     * @param nbestResult
     *            nbest results
     * @return SentenceSequenceType list
     * @throws MCMLException
     */
    public ArrayList<SentenceSequenceType> parseNBestResult(String nbestResult)
            throws MCMLException {
        ArrayList<SentenceSequenceType> sentenceSequenceList = null;

        if (nbestResult != null) {
            sentenceSequenceList = new ArrayList<SentenceSequenceType>();

            // splitting result for each UTTERANCE.
            String[] utterances = nbestResult.split(DELIMITER_UTTERANCE);
            Iterator<String> uttranceIte = Arrays.asList(utterances).iterator();
            uttranceIte.next(); // Abandon first scrap

            // check UTTERANCE number.
            if (utterances.length < 2) {
                // no UTTERANCE.
                throw new MCMLException("no UTTERANCE.", MCMLException.ERROR,
                        MCMLException.ASR, MCMLException.OTHER_ERROR);
            }

            // roop of child node settings.(for utterance roop)
            while (uttranceIte.hasNext()) {
                String utterance = (String) uttranceIte.next();
                // parse Utterance.
                // get utime
                double utime = Double.NaN;
                Matcher utimeMatcher = UTIME_PATTERN.matcher(utterance);
                if (utimeMatcher.find()) {
                    utime = Double.parseDouble(utimeMatcher.group(1));
                }

                // splitting utterance for each NBEST.
                String[] nbests = utterance.split(DELIMITER_NBEST);
                Iterator<String> nbestIte = Arrays.asList(nbests).iterator();
                nbestIte.next(); // Abandon first scrap

                // check NBEST number.
                if (nbests.length < 2) {
                    // no NBEST.
                    throw new MCMLException("no NBEST.", MCMLException.ERROR,
                            MCMLException.ASR, MCMLException.OTHER_ERROR);
                }

                // roop of child node settings.(for NBest roop)
                while (nbestIte.hasNext()) {
                    String nbest = (String) nbestIte.next();

                    // splitting NBest for each ORDER.
                    String[] orders = nbest.split(DELIMITER_ORDER);
                    Iterator<String> orderIte = Arrays.asList(orders)
                            .iterator();
                    orderIte.next(); // Abandon first scrap

                    // check ORDER number.
                    if (orders.length < 2) {
                        // no ORDER.
                        throw new MCMLException("no ORDER.",
                                MCMLException.ERROR, MCMLException.ASR,
                                MCMLException.OTHER_ERROR);
                    }

                    // roop of child node settings.(for ORDER roop)
                    int sentenceSequenceCount = 0;
                    while (orderIte.hasNext()) {
                        sentenceSequenceCount++;
                        sentenceSequenceList.add(parseOrder(
                                (String) orderIte.next(),
                                sentenceSequenceCount, utime));
                    }
                }
            }
        } else {
            // Received Data is empty.
            throw new MCMLException("no Result.", MCMLException.ERROR,
                    MCMLException.ASR, MCMLException.OTHER_ERROR);
        }

        return sentenceSequenceList;
    }

    /**
     * @param order
     * @param cnt
     * @param utime
     * @return SentenceSequenceType
     * @throws MCMLException
     */
    private SentenceSequenceType parseOrder(String order, int cnt, double utime)
            throws MCMLException {
        SentenceSequenceType sentenceSequenceType = null;
        try {
            sentenceSequenceType = new SentenceSequenceType();

            // splitting Order for OrderNum and others.
            String[] elements = order.split(DELIMITER_ORDER_ELEM, 2);

            sentenceSequenceType.addOrder(String.valueOf(cnt));

            // check order number.
            String orderNoStr = elements[0];
            Integer.parseInt(orderNoStr);

            // set Order number to MCML(NBest).
            sentenceSequenceType.addN_bestRank(orderNoStr);

            // **** parse others start ****//
            String others = elements[1];

            // get confidence.
            double confidence = getConfidence(others);
            if (!Double.isNaN(confidence)) {

                sentenceSequenceType.addScore(String.valueOf(confidence));
            }

            // WORDS parsing
            /* String.split() can not be use on the parsing, */
            /* Because WORDS String includes ' '. */
            int wordsHead = others.indexOf(SR_WORDS);
            if (wordsHead == -1) {
                throw new MCMLException("NBest Data Format Error(no WORDS).",
                        MCMLException.ERROR, MCMLException.ASR,
                        MCMLException.OTHER_ERROR);
            }
            // get after "WORDS=".
            String afterWORDS = others.substring(wordsHead + SR_WORDS.length());

            // get before "wordids=".
            int wordIdsHead = afterWORDS.indexOf(SR_WORDIDS);
            if (wordIdsHead == -1) {
                throw new MCMLException(
                        "NBest Data Format Error(no wordids=).",
                        MCMLException.ERROR, MCMLException.ASR,
                        MCMLException.OTHER_ERROR);
            }
            String words = afterWORDS.substring(0, wordIdsHead);

            // get "wordids".
            int varsHead = afterWORDS.indexOf(SR_VARS);
            if (varsHead == -1) {
                varsHead = afterWORDS.indexOf(SR_TIMES);
                if (varsHead == -1) {
                    throw new MCMLException(
                            "NBest Data Format Error(no vars=).",
                            MCMLException.ERROR, MCMLException.ASR,
                            MCMLException.OTHER_ERROR);
                }
            }
            String wordIds = afterWORDS.substring(
                    wordIdsHead + SR_WORDIDS.length(), varsHead);

            // get "confidences=([^ ]*) "
            String[] confidenceValues = getConfidences(others);

            // get words.WORDS=(*)
            ArrayList<WordValue> wordList = splitWords(words, confidenceValues);

            // get words.WORDIDS=(*)
            ArrayList<WordValue> wordIdList = splitWords(wordIds,
                    confidenceValues);

            // create sList.
            ArrayList<SentenceType> sList = createSentenceTypes(wordList,
                    wordIdList, confidence, utime);

            // add Sentence.
            for (int i = 0; i < sList.size(); i++) {
                sentenceSequenceType.addSentence(sList.get(i));
            }
        } catch (Exception e) {
            throw new MCMLException(e.getMessage(), MCMLException.ERROR,
                    MCMLException.ASR, MCMLException.OTHER_ERROR);
        }
        return sentenceSequenceType;
    }

    private double getConfidence(String srcString) {
        double confidence = Double.NaN;
        Matcher confidenceMatcher = null;

        // is GWPP used?
        if (!m_IsGwppOn) {
            // get "confidence="
            confidenceMatcher = CONFIDENCE_PATTERN.matcher(srcString);
        } else {
            // get "GUPP="
            confidenceMatcher = GUPP_PATTERN.matcher(srcString);
        }

        if (confidenceMatcher.find()) {
            // Check confidence.
            confidence = Double.parseDouble(confidenceMatcher.group(1));
        }

        return confidence;
    }

    private String[] getConfidences(String srcString) {
        String[] confidenceValues = null;
        Matcher confidencesMatcher = null;

        // is GWPP used?
        if (!m_IsGwppOn) {
            // get "confidences="
            confidencesMatcher = CONFIDENCES_PATTERN.matcher(srcString);
        } else {
            // get "GWPP="
            confidencesMatcher = GWPP_PATTERN.matcher(srcString);
        }

        if (confidencesMatcher.find()) {
            String confidences = confidencesMatcher.group(1);
            confidenceValues = confidences.split(DELIMITER_WORD_SPLITER);
        }

        return confidenceValues;
    }

    /**
     * @param words
     * @param confidenceValues
     * @return WordValue list
     */
    private ArrayList<WordValue> splitWords(String words,
            String[] confidenceValues) {
        ArrayList<WordValue> wordList = null;

        // Replace "+" or "&" to "/"
        words = words.replace(DELIMITER_WORD_SPLITER2, DELIMITER_WORD_SPLITER);
        words = words.replace(DELIMITER_WORD_SPLITER3, DELIMITER_WORD_SPLITER);

        // Delete "UTT-START" and "UTT-END".
        String[] sSplit = words.split("/");

        wordList = new ArrayList<WordValue>();

        // int count = 0;
        for (int i = 0; i < sSplit.length; i++) {
            // Words hasn't Spliter("/").
            if (sSplit.length < 1) {
                break;
            }

            WordValue wordval = new WordValue();
            wordval.setWord(sSplit[i]);
            if (confidenceValues != null) {
                if (sSplit.length == confidenceValues.length) {
                    wordval.setConfidenceFactor(confidenceValues[i]);
                } else if (1 < sSplit.length && confidenceValues.length == 1) {
                    wordval.setConfidenceFactor(confidenceValues[0]);
                } else {

                }
            }

            wordList.add(wordval);

        }

        return wordList;
    }

    private void convertFormatFromKo2010(ArrayList<WordValue> wordList) {
        String connector = "_";
        String delimiter = " ";
        int connectFlg = 0;
        String sOldSuffix = "";
        for (int i = 0; i < wordList.size(); i++) {
            String sOld = wordList.get(i).getWord();
            String sNew = sOld;
            int iPos = 0;
            String sSuffix = "";
            String sPreffix = "";
            if (sNew.indexOf("|") >= 0) { // propn or |S| or |E|
                sSuffix = "";
                sPreffix = "";
            } else {
                // get Hyouki
                String[] wordArray = sOld.split("_");
                if (wordArray.length >= 3) {
                    sNew = wordArray[2];
                }

                if (sOld.indexOf(WORD_PREFIX_S) == 0) {
                    sSuffix = delimiter;
                    if (connectFlg != 0 && sOldSuffix.isEmpty()) {
                        sPreffix = delimiter;
                    }
                    connectFlg = 0;
                } else if (sOld.indexOf(WORD_PREFIX_L) == 0) {
                    sSuffix = "";
                    if (connectFlg != 0 && sOldSuffix.isEmpty()) {
                        sPreffix = delimiter;
                    }
                    connectFlg = 1;
                } else if (sOld.indexOf(WORD_PREFIX_C) == 0) {
                    sSuffix = "";
                    if (connectFlg == 0 && sOldSuffix.isEmpty()) {
                        sPreffix = delimiter;
                    }
                    connectFlg = 1;
                } else if (sOld.indexOf(WORD_PREFIX_R) == 0) {
                    sSuffix = delimiter;
                    if (connectFlg == 0 && sOldSuffix.isEmpty()) {
                        sPreffix = delimiter;
                    }
                    connectFlg = 0;
                } else {
                    sSuffix = delimiter;
                    if (connectFlg != 0 && sOldSuffix.isEmpty()) {
                        sPreffix = delimiter;
                    }
                    connectFlg = 0;

                    // propn
                    sNew = sOld;
                }
            }

            // last word's suffix is ""
            if (i >= wordList.size() - 2) {
                sSuffix = "";
            }

            sNew = sPreffix + sNew;
            sNew += sSuffix;
            wordList.get(i).setWord(sNew);
            sOldSuffix = sSuffix;
        }
        return;
    }

    /**
     * To create the Sentence tag.
     *
     * @param wordList
     * @param wordIdList
     * @param confidence
     * @param utime
     * @return SentenceType list
     * @throws Exception
     */
    private ArrayList<SentenceType> createSentenceTypes(
            ArrayList<WordValue> wordList, ArrayList<WordValue> wordIdList,
            double confidence, double utime) throws Exception {
        m_SrTextFormat.deleteWordID(wordList, wordIdList);

        String localUttStartID = SR_UTT_START;
        String localUttEndID = SR_UTT_END;

        String localUttStartIDSimple = SR_UTT_START_SYMPLE;
        String localUttEndIDSimple = SR_UTT_END_SYMPLE;

        // ko_2010 format
        if (ASRProperties.getInstance().getNbestParseType()
                .equalsIgnoreCase(SR_NBEST_PARSETYPE_KO2010)) {

            convertFormatFromKo2010(wordList);
            convertFormatFromKo2010(wordIdList);
        }

        ArrayList<SentenceType> sTypeList = new ArrayList<SentenceType>();
        ArrayList<ChunkType> chunkTypeList = new ArrayList<ChunkType>();
        String sentence = "";
        int sentenceCount = 0;

        ArrayList<String> textFormatList = new ArrayList<String>();
        String formated_sentence = "";
        for (int i = 0; i < wordList.size(); i++) {
            String appendword = "";
            String temp = wordList.get(i).getWord();
            String tempId = wordIdList.get(i).getWord();

            // Check sentence Endmark

            if (tempId.indexOf(localUttEndID) >= 0
                    || tempId.indexOf(localUttEndIDSimple) >= 0) {
                // delete SR_SENT_START_END.

                appendword = temp.replace(SR_UTT_END, m_Period);

                // Word append sentence.

                // create formated sentence
                if (!formated_sentence.isEmpty()) {
                    formated_sentence += m_Delimiter;
                }
                formated_sentence += m_SrTextFormat.formatSentence(
                        textFormatList, m_Delimiter);

                if (m_ConvertNumKo != null) {
                    formated_sentence = m_ConvertNumKo
                            .convert(formated_sentence);
                }

                if (ASRProperties.getInstance().getWordTagPartOfSpeech()) {
                    // add WordIdType.
                    chunkTypeList.add(createChunkType(wordIdList.get(i)
                            .getWord(), chunkTypeList.size() + 1, tempId));
                } else {
                    // add WordType.
                    chunkTypeList.add(createChunkType(appendword,
                            chunkTypeList.size() + 1, tempId));
                }

                // add SentenceType.
                sentenceCount++;
                sTypeList.add(createSentence(formated_sentence, sentenceCount,
                        chunkTypeList));

                // clear chunkTypeList.
                chunkTypeList.clear();

                // clear sentence.
                sentence = "";
                textFormatList.clear();

                continue;

            } else if (tempId.indexOf(localUttStartID) >= 0
                    || tempId.indexOf(localUttStartIDSimple) >= 0) {
            } else {
                if (tempId.indexOf(SR_SENT_START_END) >= 0) {
                    appendword = temp.replace(SR_SENT_START_END, m_Period);
                } else {
                    appendword = temp;
                }

                if (temp.length() > 0) {
                    textFormatList.add(appendword);
                }
            }

            if (sentence.isEmpty()) {
                sentence = appendword;
            } else {
                sentence += m_Delimiter + appendword;
            }

            if (ASRProperties.getInstance().getWordTagPartOfSpeech()) {

                chunkTypeList.add(createChunkType(wordIdList.get(i).getWord(),
                        chunkTypeList.size() + 1, wordIdList.get(i).getWord()));
            } else {

                chunkTypeList.add(createChunkType(appendword,
                        chunkTypeList.size() + 1, wordIdList.get(i).getWord()));
            }

            if (i + 1 == wordList.size()) {
                // add SentenceType.
                sentenceCount++;
                sTypeList.add(createSentence(sentence, sentenceCount,
                        chunkTypeList));

            }

        }
        return sTypeList;
    }

    /**
     * @param sentence
     * @param order
     * @param chunkTypeList
     * @return SentenceType
     * @throws Exception
     */
    private SentenceType createSentence(String sentence, int order,
            ArrayList<ChunkType> chunkTypeList) throws Exception {
        // Set Surface.
        SurfaceType2 surface = new SurfaceType2();

        // Set Delimiter.
        if (!m_Delimiter.isEmpty()) {
            surface.addDelimiter(m_Delimiter);
        }
        surface.setValue(sentence);

        // Set Order.
        SentenceType s = new SentenceType();
        s.addOrder(String.valueOf(order));

        // Set Function.
        s.addFunction(MCMLStatics.SENTENCE_FUNCTION);

        s.addSurface(surface);

        // add chunkType.
        for (int j = 0; j < chunkTypeList.size(); j++) {
            s.addChunk(chunkTypeList.get(j));
        }

        return s;
    }

    /**
     * To create the Chunk tag.
     *
     * @param word
     * @param cnt
     * @param morpheme
     * @return ChunkType
     * @throws Exception
     */
    private ChunkType createChunkType(String word, int cnt, String morpheme)
            throws Exception {
        // create ChunkType.
        SurfaceType surface = new SurfaceType();
        surface.setValue(word);
        ChunkType chunkType = new ChunkType();
        chunkType.addOrder(String.valueOf(cnt));
        chunkType.addSurface(surface);
        POSType pos = new POSType();
        pos.setValue(morpheme);
        chunkType.addPOS(pos);

        return chunkType;
    }
}
