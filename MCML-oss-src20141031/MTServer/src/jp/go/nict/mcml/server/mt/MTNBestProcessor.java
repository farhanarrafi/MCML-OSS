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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.go.nict.mcml.servlet.MCMLException;
import jp.go.nict.mcml.xml.types.ChunkType;
import jp.go.nict.mcml.xml.types.SentenceSequenceType;
import jp.go.nict.mcml.xml.types.SentenceType;
import jp.go.nict.mcml.xml.types.SurfaceType;
import jp.go.nict.mcml.xml.types.SurfaceType2;

/**
 * MTNBestProcessor class.
 *
 */
public class MTNBestProcessor {
    // ------------------------------------------
    // private member constant
    // ------------------------------------------
    private static final String DELIMITER_NBEST = "\n";
    private static final String MT_RESPONSE_NBEST = "CON::N_BEST=";
    private static final String MT_RESPONSE_ORDER = "CON::ORDER=";
    private static final String MT_RESPONSE_TRG_TEXT = "CON::TRG_TEXT=";
    private static final String MT_RESPONSE_SCORE = "CON::SCORE=";
    private static final String MT_RESPONSE_TRG_MOR_LIST = "CON::TRG_MOR_LIST=";
    private static final String MT_UTT_START = "UTT-START";
    private static final String MT_UTT_END = "UTT-END";
    private static final String MT_SENT_START_END = "SENT-START-END";
    private static final String DELIMITER_MOR_SPLITTER = "/";
    private static final String DELIMITER_MOR_SPLITTER2 = "|";

    // The pattern of "CON::TRG_TEXT=([^ ]*) ".
    private static final Pattern TRG_TEXT_PATTERN = Pattern
            .compile(MT_RESPONSE_TRG_TEXT + "(.*)");

    // The pattern of "CON::SCORE=([^ ]*) ".
    private static final Pattern SCORE_PATTERN = Pattern
            .compile(MT_RESPONSE_SCORE + "(.*)");

    // The pattern of "CON::TRG_MOR_LIST=([^ ]*) ".
    private static final Pattern MOR_LIST_PATTERN = Pattern
            .compile(MT_RESPONSE_TRG_MOR_LIST + "(.*)");

    /**
     * Default constructor
     */
    public MTNBestProcessor() {
    }

    // ------------------------------------------
    // public member function
    // ------------------------------------------
    /**
     * parseNBestResult
     *
     * @param nbestResult
     * @param delimiter
     * @return nbestList
     * @throws MCMLException
     */
    public ArrayList<SentenceSequenceType> parseNBestResult(String nbestResult,
            String delimiter) throws MCMLException {
        ArrayList<SentenceSequenceType> nbestList = null;

        if (nbestResult != null) {
            nbestList = new ArrayList<SentenceSequenceType>();

            // splitting result for each N_BEST.
            String[] nbests = (DELIMITER_NBEST + nbestResult)
                    .split(MT_RESPONSE_NBEST);
            Iterator<String> nbestIte = Arrays.asList(nbests).iterator();
            nbestIte.next(); // Abandon first scrap

            // check N_BEST number.
            if (nbests.length < 2) {
                // no N_BEST
                throw new MCMLException("no N_BEST.", MCMLException.ERROR,
                        MCMLException.MT, MCMLException.OTHER_ERROR);
            }

            // roop of child node settings.(for N_BEST roop)
            while (nbestIte.hasNext()) {
                String nbest = (String) nbestIte.next();

                // parse N_BEST.
                // splitting utterance for each ORDER.
                String[] orders = nbest.split("\n" + MT_RESPONSE_ORDER);
                Iterator<String> orderIte = Arrays.asList(orders).iterator();
                orderIte.next(); // Abandon first scrap

                // check ORDER number.
                if (orders.length < 2) {
                    // no ORDER
                    throw new MCMLException("no ORDER.", MCMLException.ERROR,
                            MCMLException.MT, MCMLException.OTHER_ERROR);
                }

                // roop of child node settings.(for NBest roop)
                int orderCnt = 0;
                while (orderIte.hasNext()) {
                    // roop of child node settings.(for ORDER roop)
                    orderCnt++;
                    nbestList.add(parseOrder((String) orderIte.next(),
                            delimiter, orderCnt));
                }
            }
        } else {
            // Receive data is empty.
            throw new MCMLException("no Result.", MCMLException.ERROR,
                    MCMLException.MT, MCMLException.OTHER_ERROR);
        }

        return nbestList;
    }

    // ------------------------------------------
    // private member function
    // ------------------------------------------
    private SentenceSequenceType parseOrder(String order, String delimiter,
            int orderCnt) throws MCMLException {
        SentenceSequenceType sentenceSequenceType = null;

        try {
            sentenceSequenceType = new SentenceSequenceType();

            // check Order number.
            String[] elements = order.split(DELIMITER_NBEST, 2);
            String orderNoStr = elements[0];
            Integer.parseInt(orderNoStr);

            // set Order number to MCML(NBest).
            sentenceSequenceType.addN_bestRank(orderNoStr);
            sentenceSequenceType.addOrder(String.valueOf(orderCnt));

            // get after "CON::SCORE=".
            Matcher scoreMatcher = SCORE_PATTERN.matcher(order);
            if (scoreMatcher.find()) {
                // check Score.
                double score = Double.parseDouble(scoreMatcher.group(1));
                if (!Double.isNaN(score)) {
                    // set Score(=CON::SCORE) to MCML(NBest).
                    sentenceSequenceType.addScore(String.valueOf(score));
                }
            }

            // get after "CON::TRG_TEXT=".
            SentenceType sentenceType = new SentenceType();
            sentenceType.addOrder(String.valueOf(orderCnt));
            SurfaceType2 surfaceType = new SurfaceType2();
            Matcher trgTextMatcher = TRG_TEXT_PATTERN.matcher(order);
            if (trgTextMatcher.find()) {
                surfaceType.setValue(trgTextMatcher.group(1));
                // set Delimiter to MCML.
                if (!delimiter.isEmpty() || delimiter != null) {
                    surfaceType.addDelimiter(delimiter);
                }
                sentenceType.addSurface(surfaceType);
            }

            // get after "CON::TRG_MOR_LIST=".
            Matcher morListMatcher = MOR_LIST_PATTERN.matcher(order);
            ArrayList<String> morList = null;
            if (morListMatcher.find()) {
                if (MTProperties.getInstance().getWordTagPartOfSpeech()) {
                    morList = parseMorListWordID(morListMatcher.group(1));
                } else {
                    morList = parseMorList(morListMatcher.group(1));
                }
            }

            // set to CON::TRG_MOR_LIST to MCML(word)
            if (morList != null) {
                for (int i = 0; i < morList.size(); i++) {
                    SurfaceType surface = new SurfaceType();
                    surface.setValue(morList.get(i));
                    ChunkType chunk = new ChunkType();
                    chunk.addOrder(String.valueOf(i + 1));
                    chunk.addSurface(surface);
                    // add chunk to MCML(s).
                    sentenceType.addChunk(chunk);
                }
            }
            sentenceSequenceType.addSentence(sentenceType);
        } catch (Exception e) {
            throw new MCMLException(e.getMessage(), MCMLException.ERROR,
                    MCMLException.MT, MCMLException.OTHER_ERROR);
        }

        return sentenceSequenceType;
    }

    private ArrayList<String> parseMorList(String morListSrc) {
        ArrayList<String> outMorList = null;

        // delete "UTT-START" and "UTT-END".
        String temp = morListSrc.replace(MT_UTT_START, "");
        String temp2 = temp.replace(MT_SENT_START_END, "");
        String parseSrc = temp2.replace(MT_UTT_END, "");

        // parse MOR_LIST.
        int startIndex = 0;
        outMorList = new ArrayList<String>();
        while (true) {
            int splitterIndex = parseSrc.indexOf(DELIMITER_MOR_SPLITTER,
                    startIndex);
            int splitter2Index = parseSrc.indexOf(DELIMITER_MOR_SPLITTER2,
                    startIndex);

            // MOR_LIST hasn't SPLITTER("/","|").
            if (splitterIndex == -1 && splitter2Index == -1) {
                break;
            }

            // MOR_LIST has both Type SPLITTER("/","|").
            int endpoint = -1;
            int nextpoint = 0;
            if (0 <= splitterIndex && 0 <= splitter2Index) {
                // Get End Point of Word.
                if (splitterIndex < splitter2Index) {
                    endpoint = splitterIndex;
                } else {
                    endpoint = splitter2Index;
                }
                nextpoint = splitterIndex;

                // MOR_LIST has SPLITTER("/").
            } else if (0 <= splitterIndex && splitter2Index < 0) {
                endpoint = splitterIndex;
                nextpoint = splitterIndex;
            } else { // if(SPLITTERIndex < 0 && 0 <= SPLITTER2Index){
                endpoint = splitter2Index;
                nextpoint = parseSrc.length();
            }

            // more than one character.
            if (startIndex < endpoint) {
                // get before SPLITTER("/"or"|") from MOR_LIST.
                outMorList.add(parseSrc.substring(startIndex, endpoint));
            }

            // move Starting point of serch.

            startIndex = nextpoint + 1;
        }

        return outMorList;
    }

    private ArrayList<String> parseMorListWordID(String morListSrc) {
        ArrayList<String> outMorList = null;

        // parse MOR_LIST.
        outMorList = new ArrayList<String>();

        String[] sSplit = morListSrc.split(DELIMITER_MOR_SPLITTER);

        for (int i = 0; i < sSplit.length; i++) {
            outMorList.add(sSplit[i]);
        }

        return outMorList;
    }

}
