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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import jp.go.nict.mcml.serverap.common.MCMLData;
import jp.go.nict.mcml.serverap.common.ServerApCorpusLogger;
import jp.go.nict.mcml.xml.MCMLStatics;
import jp.go.nict.mcml.xml.XMLTypeTools;

import org.apache.log4j.Logger;

import com.MCML.ChunkType;
import com.MCML.DataType;
import com.MCML.MCMLDoc;
import com.MCML.SentenceSequenceType;
import com.MCML.SentenceType;
import com.MCML.SurfaceType;
import com.MCML.SurfaceType2;
import com.MCML.TextType;

/**
 * MTCorpusLogger class (for MT server).
 * 
 * @version 4.0
 * @since 20120921
 */
public class MTCorpusLogger extends ServerApCorpusLogger {
    private static final Logger LOG = Logger.getLogger(MTCorpusLogger.class
            .getName());

    // ------------------------------------------
    // private member constant
    // ------------------------------------------
    private static final String MT_COMPLETE_TIME = "MTCompleteTime";
    private static final String DIRECTION = "Direction";

    // ------------------------------------------
    // public member functions
    // ------------------------------------------
    /**
     * Constructor
     * 
     * @param utteranceInfoOnOff
     * @param baseDirectoryName
     * @param language
     * @param prefix
     * @param removedSymbols
     * @param urlInfoOnOff
     */
    public MTCorpusLogger(boolean utteranceInfoOnOff, String baseDirectoryName,
            String language, String prefix, String[] removedSymbols,
            boolean urlInfoOnOff) {
        super(utteranceInfoOnOff, baseDirectoryName, language, prefix,
                removedSymbols, urlInfoOnOff);

    }

    // write item name in the corpus log file
    @Override
    protected void writeItemName() throws IOException, FileNotFoundException {
        String itemNameString = MT_COMPLETE_TIME + ":" + USER_URI + ":"
                + USER_ID + ":" + CLIENT_IP + ":" + PROCESS_ORDER + ":"
                + LOCATION + ":" + DOMAIN + ":" + DIRECTION + ":"
                + INPUT_SENTENCE + ":" + INPUT_CHUNK + ":" + STATE + ":"
                + SENTENCE + ":" + CHUNK + ":" + PROCESS_TIME;

        // add corpus log information only socketservlet
        if (m_IsURLInfoOn) {
            itemNameString += ":" + DESTINATION_URL;
        }

        // write First Line String.
        write(itemNameString);
        changeParagraph();
    }

    /** set information from InputMCML */
    @Override
    public void setInputMCMLInfo(MCMLDoc inputMCMLDoc, int corpusLogInfoID) {
        try {
            if (m_IsUtteranceInfoOn) {
                // get User's Ingfomation.
                String userURI = "";
                String userID = "";
                String clientIP = "";

                // get URI
                userURI = XMLTypeTools.getURI(inputMCMLDoc);

                // get Client IP address
                clientIP = XMLTypeTools.getIPAddressFromUserURI(userURI);

                // get UserID
                userID = XMLTypeTools.getUserID(inputMCMLDoc);

                // get processOrder
                int processOrder = XMLTypeTools.getProcessOrder(inputMCMLDoc);

                // get Task and Domain
                String location = "";
                String domain = "";
                TextType textType = XMLTypeTools.getTextType(inputMCMLDoc);
                if (textType != null && textType.ModelType.exists()) {
                    if (textType.ModelType.first().Task.exists()) {
                        location = textType.ModelType.first().Task.first()
                                .getValue();
                    }
                    if (textType.ModelType.first().Domain.exists()) {
                        domain = textType.ModelType.first().Domain.first()
                                .getValue();
                    }
                }

                // get SourceLanguage and TargetLanguage
                String sourceLanguage = XMLTypeTools
                        .getInputLanguageID(inputMCMLDoc);
                String targetLanguage = XMLTypeTools
                        .getLanguageTypeID(inputMCMLDoc);
                String direction = sourceLanguage + "-" + targetLanguage;

                DataType inputData = XMLTypeTools
                        .getDataTypeFromMCML(inputMCMLDoc);

                // get sentence
                String inputSentence = createSentence(inputData);

                // get Chunk
                String inputChunk = createChunk(inputData);

                // set information from inputMCML to CorpusLogInfo.
                setInputMCMLInfoSub(corpusLogInfoID, userURI, userID, clientIP,
                        processOrder, location, domain, direction,
                        inputSentence, inputChunk);
            }

            // remove UserInfomation String(ClientIP and Recieved Time on
            // Servlet).
            XMLTypeTools.removeUserInfoStr(inputMCMLDoc);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

    }

    /** set information from outputMCML */
    @Override
    public void setOutputMCMLInfo(MCMLDoc outputMCMLDoc, int corpusLogInfoID) {
        if (m_IsUtteranceInfoOn) {
            synchronized (m_CorpusLogInfoMap) {
                // is CorpusLogInfo?.
                if (m_CorpusLogInfoMap.containsKey(Integer
                        .valueOf(corpusLogInfoID))) {
                    // get MTCorpusLogInfo instance.
                    MTCorpusLogInfo corpusLogInfo = (MTCorpusLogInfo) (m_CorpusLogInfoMap
                            .get((Integer.valueOf(corpusLogInfoID))));

                    try {
                        DataType outputData = XMLTypeTools
                                .getDataTypeFromMCML(outputMCMLDoc);
                        if (outputData != null) {
                            // get sentence
                            String sentence = createSentence(outputData);

                            // get Chunk
                            String chunk = createChunk(outputData);

                            // set sentence.
                            corpusLogInfo.setSentence(sentence);

                            // set Chunk.
                            corpusLogInfo.setChunk(chunk);

                            // set Result State.
                            corpusLogInfo
                                    .setState(MCMLStatics.PROCESS_STATE_SUCCESS);
                        } else {
                            // set Result State(Error).
                            String errorCode = XMLTypeTools
                                    .getErrorCode(outputMCMLDoc);
                            String explanation = XMLTypeTools
                                    .getErrorMessage(outputMCMLDoc);
                            String outputString = MCMLStatics.PROCESS_STATE_FAIL
                                    + errorCode + " " + explanation;
                            corpusLogInfo.setState(outputString);
                        }
                    } catch (Exception e) {
                        LOG.error(e.getMessage(), e);
                    }
                }
            }
        }

    }

    /** write information in the Corpuslogfile */
    @Override
    public void writeCorpusLogInfo(int corpusLogInfoID) {
        try {
            if (m_IsUtteranceInfoOn) {
                String outputString = "";
                Date mtCompleteTime = null;
                String userURI = "";
                String userID = "";
                String clientIP = "";
                int processOrder = 0;
                String locaton = "";
                String domain = "";
                String direction = "";
                String inputSentence = "";
                String inputChunk = "";
                String state = "";
                String sentence = "";
                String chunk = "";
                long processTime = 0;
                ArrayList<String> destinationURLList = null;
                String destinationURL = "";

                String directionForCreateDir = "";

                synchronized (m_CorpusLogInfoMap) {
                    // is CorpusLogInfo?.
                    if (m_CorpusLogInfoMap.containsKey(Integer
                            .valueOf(corpusLogInfoID))) {
                        // get MTCorpusLogInfo instance.
                        MTCorpusLogInfo corpusLogInfo = (MTCorpusLogInfo) (m_CorpusLogInfoMap
                                .get((Integer.valueOf(corpusLogInfoID))));

                        // Start Calculation
                        calculation(corpusLogInfo);

                        // get Corpus Log Infomation for Utterance Infomation
                        // Log.
                        mtCompleteTime = new Date(
                                corpusLogInfo.getMTCompleteTime());
                        userURI = corpusLogInfo.getUserURI();
                        userID = corpusLogInfo.getUserID();
                        clientIP = corpusLogInfo.getClientIP();
                        processOrder = corpusLogInfo.getProcessOrder();
                        locaton = corpusLogInfo.getLocation();
                        domain = corpusLogInfo.getDomain();
                        direction = corpusLogInfo.getDirection();
                        inputSentence = corpusLogInfo.getInputSentence();
                        inputChunk = corpusLogInfo.getInputChunk();
                        state = corpusLogInfo.getState();
                        sentence = corpusLogInfo.getSentence();
                        chunk = corpusLogInfo.getChunk();
                        processTime = corpusLogInfo.getProcessTime();
                        destinationURLList = corpusLogInfo.getDestinationURL();
                        destinationURL = createDestinationURL(destinationURLList);

                        directionForCreateDir = corpusLogInfo.getDirection();
                    } else {
                        // no output data.
                        LOG.error("CorpusLog Info is not exists.");
                        return;
                    }
                }
                // create OutputDirectory(by not exist OutputDirectory).
                boolean isCreate = createDirectory(m_BasedirectoryName,
                        directionForCreateDir, destinationURLList, false);

                if ((!m_CorpusLogStreamMap.containsKey(m_LanguagePrefix))
                        || isCreate) {
                    // create Corpus Log file.
                    createFile();

                    // write first line.
                    writeItemName();
                }

                // create CorpusLogInfoString.
                outputString = createCorpusLogInfoString(mtCompleteTime,
                        userURI, userID, clientIP, processOrder, locaton,
                        domain, direction, inputSentence, inputChunk, state,
                        sentence, chunk, processTime, destinationURL);

                // write output string.
                write(outputString);

                // writing information of this request is complete and change
                // paragraph
                changeParagraph();

            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        } finally {
            if (m_IsUtteranceInfoOn) {
                synchronized (m_CorpusLogInfoMap) {
                    // remove CorpusLogInfo.
                    m_CorpusLogInfoMap.remove(corpusLogInfoID);
                }
            }
        }

    }

    /** set first frame arrived time. */
    @Override
    public void setFirstFrameArrivedTime(int corpusLogInfoID,
            long firstFrameArrivedTime) {
        // no process
    }

    /** set first frame arrived time. */
    @Override
    public void setFirstFrameArrivedTime(int corpusLogInfoID) {
        // no process
    }

    /** set last frame arrived time. */
    @Override
    public void setLastFrameArrivedTime(int corpusLogInfoID,
            long lastFrameArrivedTime) {
        // no process
    }

    /** set Last Frame ArrivedTime. */
    @Override
    public void setLastFrameArrivedTime(int corpusLogInfoID) {
        // get Last Frame ArrivedTime.
        long lastFrameArrivedTime = System.currentTimeMillis();

        if (m_IsUtteranceInfoOn) {
            synchronized (m_CorpusLogInfoMap) {
                MTCorpusLogInfo corpusLogInfo = null;
                // is CorpusLogInfo?.
                if (m_CorpusLogInfoMap.containsKey(Integer
                        .valueOf(corpusLogInfoID))) {
                    // get MTCorpusLogInfo instance.
                    corpusLogInfo = (MTCorpusLogInfo) (m_CorpusLogInfoMap
                            .get((Integer.valueOf(corpusLogInfoID))));
                } else {
                    // create CorpusLogInfo
                    corpusLogInfo = new MTCorpusLogInfo();

                    // set LogInfoMap;
                    m_CorpusLogInfoMap.put(Integer.valueOf(corpusLogInfoID),
                            corpusLogInfo);
                }
                // set Last Frame ArrivedTime.
                corpusLogInfo.setLastFrameArrivedTime(lastFrameArrivedTime);
            }
        }

    }

    /** Sets CompleteTime. */
    @Override
    public void setCompleteTime(int corpusLogInfoID) {
        // get process Complete Time.
        long completeTime = System.currentTimeMillis();

        if (m_IsUtteranceInfoOn) {
            synchronized (m_CorpusLogInfoMap) {
                // is CorpusLogInfo?.
                if (m_CorpusLogInfoMap.containsKey(Integer
                        .valueOf(corpusLogInfoID))) {
                    // get MTCorpusLogInfo instance.
                    MTCorpusLogInfo corpusLogInfo = (MTCorpusLogInfo) (m_CorpusLogInfoMap
                            .get((Integer.valueOf(corpusLogInfoID))));

                    // set MT CompleteTime
                    corpusLogInfo.setMTCompleteTime(completeTime);
                }
            }
        }

    }

    /**
     * Sets OutputMCMLInfo.
     * 
     * @param outputMCMLData
     * @param corpusLogInfoID
     */
    public void setOutputMCMLInfo(MCMLData outputMCMLData, int corpusLogInfoID) {
        MCMLDoc outputMCMLDoc = outputMCMLData.getMcmlDoc();
        setOutputMCMLInfo(outputMCMLDoc, corpusLogInfoID);
    }

    /**
     * Sets DestinationURL.
     */
    @Override
    public void setDestinationURL(ArrayList<String> urlList, int corpusLogInfoID) {
        if (m_IsUtteranceInfoOn) {
            synchronized (m_CorpusLogInfoMap) {
                // is CorpusLogInfo?.
                if (m_CorpusLogInfoMap.containsKey(Integer
                        .valueOf(corpusLogInfoID))) {
                    // get MTCorpusLogInfo instance.
                    MTCorpusLogInfo corpusLogInfo = (MTCorpusLogInfo) (m_CorpusLogInfoMap
                            .get((Integer.valueOf(corpusLogInfoID))));

                    // set SR CompleteTime
                    corpusLogInfo.setDestinationURL(urlList);
                }
            }
        }
    }

    // ------------------------------------------
    // private member functions
    // ------------------------------------------
    private void setInputMCMLInfoSub(int corpusLogInfoID, String userURI,
            String userID, String clientIP, int processOrder, String location,
            String domain, String direction, String inputSentence,
            String inputChunk) {
        synchronized (m_CorpusLogInfoMap) {
            MTCorpusLogInfo corpusLogInfo = null;
            // is CorpusLogInfo?.
            if (m_CorpusLogInfoMap
                    .containsKey(Integer.valueOf(corpusLogInfoID))) {
                // get MTCorpusLogInfo instance.
                corpusLogInfo = (MTCorpusLogInfo) (m_CorpusLogInfoMap
                        .get((Integer.valueOf(corpusLogInfoID))));
            } else {
                // create Instance.
                corpusLogInfo = new MTCorpusLogInfo();
                m_CorpusLogInfoMap.put(Integer.valueOf(corpusLogInfoID),
                        corpusLogInfo);
            }

            // set information from InputMCML
            corpusLogInfo.setUserURI(userURI);
            corpusLogInfo.setUserID(userID);
            corpusLogInfo.setClientIP(clientIP);
            corpusLogInfo.setProcessOrder(processOrder);
            corpusLogInfo.setLocation(location);
            corpusLogInfo.setDomain(domain);
            corpusLogInfo.setDirection(direction);
            corpusLogInfo.setInputSentence(inputSentence);
            corpusLogInfo.setInputChunk(inputChunk);
        }
    }

    private String createCorpusLogInfoString(Date mtCompleteTime,
            String userURI, String userID, String clientIP, int processOrder,
            String locaton, String domain, String direction,
            String inputSentence, String inputChunk, String state,
            String sentence, String chunk, long processTime,
            String destinationURL) {
        String outputString = "";

        // create output string.
        outputString = OUTPUT_DATE_FORMAT.format(mtCompleteTime) + ":";
        outputString += userURI + ":";
        outputString += userID + ":";
        outputString += clientIP + ":";
        outputString += Integer.toString(processOrder) + ":";
        outputString += locaton + ":";
        outputString += domain + ":";
        outputString += direction + ":";
        outputString += inputSentence + ":";
        outputString += inputChunk + ":";
        outputString += state + ":";
        outputString += sentence + ":";
        outputString += chunk + ":";

        if (processTime > 0) {
            outputString += Long.toString(processTime);
        }

        // add corpus log information only socketservlet
        if (m_IsURLInfoOn) {
            outputString += ":";
            outputString += destinationURL;
        }

        return outputString;
    }

    // pick out sentence from dataType
    private String createSentence(DataType dataType) {
        String sentence = "";

        try {
            SentenceSequenceType sentenceSequenceType = dataType.Text.first().SentenceSequence
                    .first();
            int sCount = sentenceSequenceType.Sentence.count();

            for (int i = 0; i < sCount; i++) {
                // get sentence value.
                SentenceType sentenceType = sentenceSequenceType.Sentence.at(i);
                if (sentenceType.Surface.exists()) {
                    // get Data/Text/SentenceSequence/Sentence/Surface
                    SurfaceType2 surfaceType = sentenceType.Surface.first();
                    String tempString = surfaceType.getValue().toString();
                    sentence = mergeString(tempString, sentence);
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            sentence = "";
        }

        return sentence;
    }

    // pick out Chunk from dataType
    private String createChunk(DataType dataType) {
        String chunk = "";

        try {
            SentenceSequenceType sentenceSequenceType = dataType.Text.first().SentenceSequence
                    .first();
            int sCount = sentenceSequenceType.Sentence.count();

            for (int i = 0; i < sCount; i++) {
                SentenceType sentenceType = sentenceSequenceType.Sentence.at(i);
                int wCount = sentenceType.Chunk.count();

                for (int j = 0; j < wCount; j++) {
                    // get sentence value.
                    ChunkType chunkType = sentenceType.Chunk.at(j);
                    if (chunkType.Surface.exists()) {
                        // get
                        // Data/Text/SentenceSequence/Sentence/chunkType/Surface
                        SurfaceType surfaceType = chunkType.Surface.first();
                        String tempString = surfaceType.getValue().toString();
                        chunk = mergeString(tempString, chunk);
                    }
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            chunk = "";
        }

        return chunk;
    }

    // do calculation
    private void calculation(MTCorpusLogInfo corpusLogInfo) {
        // get values neccessary for calculation
        Date lastFrameArrivedTime = new Date(
                corpusLogInfo.getLastFrameArrivedTime());
        Date mtCompleteTime = new Date(corpusLogInfo.getMTCompleteTime());

        long processTime = calculateProcessTime(lastFrameArrivedTime,
                mtCompleteTime);
        corpusLogInfo.setProcessTime(processTime);

    }

    // calcuate processtime
    private long calculateProcessTime(Date lastFrameArrivedTime,
            Date completeTime) {

        long processtime = completeTime.getTime()
                - lastFrameArrivedTime.getTime();

        if (processtime <= 0) {
            processtime = 0;
            LOG.error("calculateProcessTime() failed.");
        }

        return processtime;
    }

}
