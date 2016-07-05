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
import jp.go.nict.mcml.serverap.common.ServerApLogger;
import jp.go.nict.mcml.servlet.MCMLStatics;
import jp.go.nict.mcml.servlet.XMLTypeTools;
import jp.go.nict.mcml.xml.types.ChunkType;
import jp.go.nict.mcml.xml.types.DataType;
import jp.go.nict.mcml.xml.types.GlobalPositionType;
import jp.go.nict.mcml.xml.types.InputUserProfileType;
import jp.go.nict.mcml.xml.types.LanguageType;
import jp.go.nict.mcml.xml.types.MCMLType;
import jp.go.nict.mcml.xml.types.SentenceSequenceType;
import jp.go.nict.mcml.xml.types.SentenceType;
import jp.go.nict.mcml.xml.types.SurfaceType;
import jp.go.nict.mcml.xml.types.SurfaceType2;
import jp.go.nict.mcml.xml.types.TextType;

/**
 * MTCorpusLogger class.
 *
 */
public class MTCorpusLogger extends ServerApCorpusLogger {

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
     * @param basedirectryName
     * @param language
     * @param prefix
     * @param removedSymbols
     */
    public MTCorpusLogger(boolean utteranceInfoOnOff, String basedirectryName,
            String language, String prefix, String[] removedSymbols) {
        super(utteranceInfoOnOff, basedirectryName, language, prefix,
                removedSymbols);

    }

    /** write item name in the corpus log file */
    @Override
    protected void writeItemName() throws IOException, FileNotFoundException {
        String itemNameString = MT_COMPLETE_TIME + ":" + USER_URI + ":"
                + USER_ID + ":" + CLIENT_IP + ":" + LONGITUDE + ":" + LATITUDE
                + ":" + VOICE_ID + ":" + GENDER + ":" + AGE + ":"
                + NATIVE_LANGUAGE + ":" + FIRST_FOREIGN_LANGUAGE + ":"
                + SECOND_FOREIGN_LANGUAGE + ":" + PROCESS_ORDER + ":"
                + LOCATION + ":" + DOMAIN + ":" + DIRECTION + ":"
                + INPUT_SENTENCE + ":" + INPUT_CHUNK + ":" + STATE + ":"
                + SENTENCE + ":" + CHUNK + ":" + PROCESS_TIME;

        // write First Line String.
        write(itemNameString);
        changeParagraph();

        return;
    }

    /** set information from InputMCML */
    @Override
    public void setInputMCMLInfo(MCMLType inputMCML, int corpusLogInfoID) {
        try {
            if (m_IsUtteranceInfoOn) {
                // get User's Ingfomation.
                // get URI
                String userURI = XMLTypeTools.getURI(inputMCML);

                // get UserID
                String userID = XMLTypeTools.getUserID(inputMCML);

                // get Client IP
                String clientIP = "";
                if (!userURI.isEmpty()) {
                    // get ClientIP
                    clientIP = XMLTypeTools.getIPAddressFromUserURI(userURI);
                }

                // get processOrder
                String processOrder = XMLTypeTools.getProcessOrder(inputMCML);

                // get Task and Domain
                String location = "";
                String domain = "";
                TextType textType = XMLTypeTools.getTextType(inputMCML);
                if (textType != null && textType.hasModelType()) {
                    if (textType.getModelType().hasTask()) {
                        location = textType.getModelType().getTask().getValue();
                    }
                    if (textType.getModelType().hasDomain()) {
                        domain = textType.getModelType().getDomain().getValue();
                    }
                }

                GlobalPositionType globalPositionType = XMLTypeTools
                        .getGlobalPosition(inputMCML);

                String longitude = "";
                String latitude = "";
                if (globalPositionType != null) {
                    longitude = XMLTypeTools.getLongitude(globalPositionType);
                    latitude = XMLTypeTools.getLatitude(globalPositionType);
                }

                // get SourceLanguage and TargetLanguage

                String sourceLanguage = XMLTypeTools
                        .getInputDataTextModelTypeLanguageType(inputMCML);

                // get voiceID,age,gender
                String voiceID = "";
                String age = "";
                String gender = "";
                InputUserProfileType inputUserProfile = XMLTypeTools
                        .getInputUserProfileType(inputMCML);
                if (inputUserProfile != null) {
                    if (inputUserProfile.hasID()) {
                        voiceID = inputUserProfile.getID().getValue();
                    }

                    if (inputUserProfile.hasAge()) {
                        age = inputUserProfile.getAge().toString();
                    }

                    if (inputUserProfile.hasGender()) {
                        gender = inputUserProfile.getGender().getValue();
                    }
                }

                String nativeLanguage = "";
                String firstForeignLanguage = "";
                String secondForeignLanguage = "";

                ArrayList<LanguageType> languageTypeList = XMLTypeTools
                        .getLanguageList(inputMCML);
                int languageTypeListSize = languageTypeList.size();

                for (int i = 0; i < languageTypeListSize; i++) {
                    languageTypeList.get(i).getFluency();
                    if (languageTypeList.get(i).getFluency().getValue() == MCMLStatics.NATIVE_LANGUAGE) {
                        nativeLanguage = languageTypeList.get(i).getID()
                                .getValue();
                    } else if (languageTypeList.get(i).getFluency().getValue() == MCMLStatics.FOREIGN_LANGUAGE_1ST) {
                        firstForeignLanguage = languageTypeList.get(i).getID()
                                .getValue();
                    } else if (languageTypeList.get(i).getFluency().getValue() == MCMLStatics.FOREIGN_LANGUAGE_2ND) {
                        secondForeignLanguage = languageTypeList.get(i).getID()
                                .getValue();
                    }
                }

                String targetLanguage = XMLTypeTools
                        .getLanguageTypeID(inputMCML);

                String direction = sourceLanguage + "-" + targetLanguage;

                DataType inputData = XMLTypeTools
                        .getDataTypeFromMCML(inputMCML);

                // get sentence
                String inputSentence = createSentence(inputData);

                // get Chunk
                String inputChunk = createChunk(inputData);

                // set information from inputMCML to CorpusLogInfo.
                setInputMCMLInfoSub(corpusLogInfoID, userURI, userID, clientIP,

                longitude, latitude,

                voiceID, gender, age, nativeLanguage, firstForeignLanguage,
                        secondForeignLanguage,

                        processOrder, location, domain, direction,
                        inputSentence, inputChunk);
            }

            // remove UserInfomation String(ClientIP and Recieved Time on
            // Servlet).
            removeUserInfoStr(inputMCML);
        } catch (Exception e) {
            ServerApLogger.getInstance().writeError(e.getMessage());
        }
        return;
    }

    /** set information from outputMCML */
    public void setOutputMCMLInfo(MCMLType outputMCML, int corpusLogInfoID) {
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
                                .getDataTypeFromMCML(outputMCML);
                        if (outputData != null) {
                            // set Result State.
                            corpusLogInfo
                                    .setState(MCMLStatics.PROCESS_STATE_SUCCESS);

                            // get sentence from outputData to set sentence.
                            String sentence = createSentence(outputData);
                            corpusLogInfo.setSentence(sentence);

                            // get Chunk from outputData to set Chunk.
                            String chunk = createChunk(outputData);
                            corpusLogInfo.setChunk(chunk);
                        } else {
                            // set Result State(Error).
                            String errorCode = XMLTypeTools
                                    .getErrorCode(outputMCML);
                            String explanation = XMLTypeTools
                                    .getErrorMessage(outputMCML);
                            String outputString = MCMLStatics.PROCESS_STATE_FAIL
                                    + errorCode + " " + explanation;
                            corpusLogInfo.setState(outputString);
                        }
                    } catch (Exception e) {
                        ServerApLogger.getInstance().writeError(e.getMessage());
                    }
                }
            }
        }
        return;
    }

    /** write information in the Corpuslogfile */
    @Override
    public void writeCorpusLogInfo(int corpusLogInfoID) {
        try {
            if (m_IsUtteranceInfoOn) {
                Date mtCompleteTime = null;
                String userURI = "";
                String userID = "";
                String clientIP = "";

                String longitude = "";
                String latitude = "";

                String voiceID = "";
                String gender = "";
                String age = "";
                String nativeLanguage = "";
                String firstForeignLanguage = "";
                String secondForeignLanguage = "";

                String processOrder = "";
                String locaton = "";
                String domain = "";
                String direction = "";
                String inputSentence = "";
                String inputChunk = "";
                String state = "";
                String sentence = "";
                String chunk = "";
                long processTime = 0;

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

                        longitude = corpusLogInfo.getLongitude();
                        latitude = corpusLogInfo.getLatitude();

                        voiceID = corpusLogInfo.getVoiceID();
                        gender = corpusLogInfo.getGender();
                        age = corpusLogInfo.getAge();
                        nativeLanguage = corpusLogInfo.getNativeLanguage();
                        firstForeignLanguage = corpusLogInfo
                                .getFirstForeignLanguage();
                        secondForeignLanguage = corpusLogInfo
                                .getSecondForeignLanguage();

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
                    } else {
                        // no output data.
                        ServerApLogger.getInstance().writeWarning(
                                "CorpusLog Info is not exists.");
                        return;
                    }
                }
                // create OutputDirectry(by not exist OutputDirectry).
                boolean isCreate = createDirectory(m_BasedirectryName,
                        m_PropertiesLanguage);

                if (m_CorpusLogStream == null || isCreate) {
                    // create Corpus Log file.
                    createFile();

                    // write first line.
                    writeItemName();
                }

                // create CorpusLogInfoString.
                String outputString = createCorpusLogInfoString(mtCompleteTime,
                        userURI, userID, clientIP,

                        longitude, latitude,

                        voiceID, gender, age, nativeLanguage,
                        firstForeignLanguage, secondForeignLanguage,

                        processOrder, locaton, domain, direction,
                        inputSentence, inputChunk, state, sentence, chunk,
                        processTime);

                // write output string.
                write(outputString);

                // writing information of this request is complete and change
                // paragraph
                changeParagraph();
            }
        } catch (Exception e) {
            ServerApLogger.getInstance().writeError(e.getMessage());
        } finally {
            if (m_IsUtteranceInfoOn) {
                synchronized (m_CorpusLogInfoMap) {
                    // remove CorpusLogInfo.
                    m_CorpusLogInfoMap.remove(corpusLogInfoID);
                }
            }
        }
        return;
    }

    /**
     * setFirstFrameArrivedTime
     */
    @Override
    public void setFirstFrameArrivedTime(int corpusLogInfoID) {
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
        return;
    }

    /**
     * Sets CompleteTime.
     */
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
        return;
    }

    /**
     * Sets OutputMCMLInfo.
     */
    @Override
    public void setOutputMCMLInfo(MCMLData outputMCMLData, int corpusLogInfoID) {
        MCMLType outputMCML = outputMCMLData.getMCMLType();
        setOutputMCMLInfo(outputMCML, corpusLogInfoID);
        return;
    }

    // ------------------------------------------
    // private member functions
    // ------------------------------------------
    private void setInputMCMLInfoSub(int corpusLogInfoID, String userURI,
            String userID, String clientIP,

            String longitude, String latitude,

            String voiceID, String gender, String age, String nativeLanguage,
            String firstForeignLanguage, String secondForeignLanguage,

            String processOrder, String location, String domain,
            String direction, String inputSentence, String inputChunk) {
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

            corpusLogInfo.setLongitude(longitude);
            corpusLogInfo.setLatitude(latitude);

            corpusLogInfo.setVoiceID(voiceID);
            corpusLogInfo.setGender(gender);
            corpusLogInfo.setAge(age);
            corpusLogInfo.setNativeLanguage(nativeLanguage);
            corpusLogInfo.setFirstForeignLanguage(firstForeignLanguage);
            corpusLogInfo.setSecondForeignLanguage(secondForeignLanguage);

            corpusLogInfo.setProcessOrder(processOrder);
            corpusLogInfo.setLocation(location);
            corpusLogInfo.setDomain(domain);
            corpusLogInfo.setDirection(direction);
            corpusLogInfo.setInputSentence(inputSentence);
            corpusLogInfo.setInputChunk(inputChunk);
        }
        return;
    }

    private String createCorpusLogInfoString(Date mtCompleteTime,
            String userURI, String userID, String clientIP,

            String longitude, String latitude,

            String voiceID, String gender, String age, String nativeLanguage,
            String firstForeignLanguage, String secondForeignLanguage,

            String processOrder, String locaton, String domain,
            String direction, String inputSentence, String inputChunk,
            String state, String sentence, String chunk, long processTime) {
        String outputString = "";

        // create output string.
        outputString = OUTPUT_DATE_FORMAT.format(mtCompleteTime) + ":";
        outputString += userURI + ":";
        outputString += userID + ":";
        outputString += clientIP + ":";

        outputString += longitude + ":";
        outputString += latitude + ":";

        outputString += voiceID + ":";
        outputString += gender + ":";
        outputString += age + ":";
        outputString += nativeLanguage + ":";
        outputString += firstForeignLanguage + ":";
        outputString += secondForeignLanguage + ":";

        outputString += processOrder + ":";
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

        return outputString;
    }

    // pick out sentence from dataType
    private String createSentence(DataType dataType) {
        String sentence = "";

        try {
            SentenceSequenceType sentenceSequenceType = dataType.getText()
                    .getSentenceSequence();
            int sCount = sentenceSequenceType.getSentenceCount();

            for (int i = 0; i < sCount; i++) {
                // get sentence value.
                SentenceType sentenceType = sentenceSequenceType
                        .getSentenceAt(i);
                if (sentenceType.hasSurface()) {
                    // get Data/Text/SentenceSequence/Sentence/Surface
                    SurfaceType2 surfaceType = sentenceType.getSurface();
                    String tempString = surfaceType.getValue().toString();
                    sentence = mergeString(tempString, sentence);
                }
            }
        } catch (Exception e) {
            ServerApLogger.getInstance().writeError(e.getMessage());
            sentence = "";
        }

        return sentence;
    }

    // pick out Chunk from dataType
    private String createChunk(DataType dataType) {
        String chunk = "";

        try {
            SentenceSequenceType sentenceSequenceType = dataType.getText()
                    .getSentenceSequence();
            int sCount = sentenceSequenceType.getSentenceCount();

            for (int i = 0; i < sCount; i++) {
                SentenceType sentenceType = sentenceSequenceType
                        .getSentenceAt(i);
                int wCount = sentenceType.getChunkCount();

                for (int j = 0; j < wCount; j++) {
                    // get sentence value.
                    ChunkType chunkType = sentenceType.getChunkAt(j);
                    if (chunkType.hasSurface()) {
                        // get
                        // Data/Text/SentenceSequence/Sentence/chunkType/Surface
                        SurfaceType surfaceType = chunkType.getSurface();
                        String tempString = surfaceType.getValue().toString();
                        chunk = mergeString(tempString, chunk);
                    }
                }
            }
        } catch (Exception e) {
            ServerApLogger.getInstance().writeError(e.getMessage());
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
        return;
    }

    // calcuate processtime
    private long calculateProcessTime(Date lastFrameArrivedTime,
            Date completeTime) {

        long processtime = completeTime.getTime()
                - lastFrameArrivedTime.getTime();

        if (processtime <= 0) {
            processtime = 0;
            ServerApLogger.getInstance().writeError(
                    "calculateProcessTime() failed.");
        }

        return processtime;
    }
}
