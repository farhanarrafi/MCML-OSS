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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;

import jp.go.nict.mcml.serverap.common.AudioConverter;
import jp.go.nict.mcml.serverap.common.MCMLData;
import jp.go.nict.mcml.serverap.common.ServerApCorpusLogger;
import jp.go.nict.mcml.serverap.common.ServerApLogger;
import jp.go.nict.mcml.serverap.common.SpeexAudioConverter;
import jp.go.nict.mcml.servlet.MCMLException;
import jp.go.nict.mcml.servlet.MCMLStatics;
import jp.go.nict.mcml.servlet.XMLTypeTools;
import jp.go.nict.mcml.xml.types.AudioType;
import jp.go.nict.mcml.xml.types.ChunkType;
import jp.go.nict.mcml.xml.types.DataType;
import jp.go.nict.mcml.xml.types.GlobalPositionType;
import jp.go.nict.mcml.xml.types.InputUserProfileType;
import jp.go.nict.mcml.xml.types.LanguageType;
import jp.go.nict.mcml.xml.types.MCMLType;
import jp.go.nict.mcml.xml.types.ResponseType;
import jp.go.nict.mcml.xml.types.SentenceSequenceType;
import jp.go.nict.mcml.xml.types.SentenceType;
import jp.go.nict.mcml.xml.types.SurfaceType;
import jp.go.nict.mcml.xml.types.SurfaceType2;

/**
 * TTSCorpusLogger class.
 *
 *
 */
public class TTSCorpusLogger extends ServerApCorpusLogger {
    // ------------------------------------------
    // private member constant
    // ------------------------------------------
    private static final String TTS_COMPLETE_TIME = "TTSCompleteTime";
    private static final String SAMPLING_FREQUENCY = "SamplingFrequency";
    private static final String OUTPUT_AUDIO_FORMAT = "OutputAudioFormat";
    private static final String OUTPUT_UTTERANCE_LENGTH = "OutputUtteranceLength";
    private static final String VOICE_FONT_ID = "VoiceFontID";
    private static final String F0 = "F0";
    private static final String RATE = "Rate";
    private static final String VOICEID_INPUT = "INPUT";
    private static final String COMMA_MARK = ",";

    // ------------------------------------------
    // public member functions
    // ------------------------------------------
    /**
     * Constructor
     *
     * @param isUtteranceInfoOn
     * @param basedirectryName
     * @param language
     * @param prefix
     * @param removedSymbols
     */
    public TTSCorpusLogger(boolean isUtteranceInfoOn, String basedirectryName,
            String language, String prefix, String[] removedSymbols) {
        super(isUtteranceInfoOn, basedirectryName, language, prefix,
                removedSymbols);
    }

    /**
     * set information from InputMCML
     */
    @Override
    public void setInputMCMLInfo(MCMLType inputMCML, int corpusLogInfoID) {
        try {
            if (m_IsUtteranceInfoOn) {
                // get User's Ingfomation.
                String userURI = "";
                String userID = "";
                String clientIP = "";

                // get URI
                userURI = XMLTypeTools.getURI(inputMCML);

                // get UserID
                userID = XMLTypeTools.getUserID(inputMCML);
                if (!userURI.isEmpty()) {
                    // get ClientIP
                    clientIP = XMLTypeTools.getIPAddressFromUserURI(userURI);
                }

                // get processOrder
                String processOrder = XMLTypeTools.getProcessOrder(inputMCML);

                GlobalPositionType globalPositionType = XMLTypeTools
                        .getGlobalPosition(inputMCML);

                String longitude = "";
                String latitude = "";
                if (globalPositionType != null) {
                    longitude = XMLTypeTools.getLongitude(globalPositionType);
                    latitude = XMLTypeTools.getLatitude(globalPositionType);
                }

                // get TargetLanguage

                String language = XMLTypeTools
                        .getTargetOutputLanguageType(inputMCML);

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

                DataType inputData = XMLTypeTools
                        .getDataTypeFromMCML(inputMCML);

                // get sentence
                String inputSentence = createSentence(inputData);

                // get Chunk
                String inputChunk = createChunk(inputData);

                // get Rate
                TTSProperties prop = TTSProperties.getInstance();
                float rate = prop.getRateValue();

                // set information from inputMCML to CorpusLogInfo.
                setInputMCMLInfoSub(corpusLogInfoID, userURI, userID, clientIP,
                        voiceID, processOrder, language, longitude, latitude,
                        gender, age, nativeLanguage, firstForeignLanguage,
                        secondForeignLanguage, inputSentence, inputChunk, rate);
            }

            // remove UserInfomation String(ClientIP and Recieved Time on
            // Servlet).
            removeUserInfoStr(inputMCML);
        } catch (Exception e) {
            ServerApLogger.getInstance().writeError(e.getMessage());
        }
    }

    /**
     * set information from outputMCML
     *
     * @param outputMCML
     * @param corpusLogInfoID
     */
    public void setOutputMCMLInfo(MCMLType outputMCML, int corpusLogInfoID) {
        if (m_IsUtteranceInfoOn) {
            synchronized (m_CorpusLogInfoMap) {
                // is CorpusLogInfo?.
                if (m_CorpusLogInfoMap.containsKey(Integer
                        .valueOf(corpusLogInfoID))) {
                    // get TTSCorpusLogInfo instance.
                    TTSCorpusLogInfo corpusLogInfo = (TTSCorpusLogInfo) (m_CorpusLogInfoMap
                            .get((Integer.valueOf(corpusLogInfoID))));

                    try {
                        DataType outputData = XMLTypeTools
                                .getDataTypeFromMCML(outputMCML);
                        corpusLogInfo.setDataType(outputData);
                        if (outputData != null) {
                            String samplingFrequency = "";
                            String outputAudioFormat = "";
                            boolean isBigEndian = true;
                            String voiceId = "";
                            ResponseType responseType = outputMCML.getServer()
                                    .getResponse();
                            AudioType audioType = XMLTypeTools
                                    .getAudioType(responseType);
                            if (audioType != null) {
                                if (audioType.hasSignal()) {
                                    if (audioType.getSignal().hasAudioFormat()) {
                                        outputAudioFormat = audioType
                                                .getSignal().getAudioFormat()
                                                .getValue();
                                    }
                                    if (audioType.getSignal().hasEndian()) {
                                        String endian = audioType.getSignal()
                                                .getEndian().getValue();
                                        if (endian
                                                .equalsIgnoreCase(MCMLStatics.ENDIAN_BIG)) {
                                            isBigEndian = true;
                                        } else if (endian
                                                .equalsIgnoreCase(MCMLStatics.ENDIAN_LITTLE)) {
                                            isBigEndian = false;
                                        }
                                    }
                                    if (audioType.getSignal().hasSamplingRate()) {
                                        samplingFrequency = audioType
                                                .getSignal().getSamplingRate()
                                                .toString();
                                    }
                                }
                                if (audioType.hasModelType()
                                        && audioType.getModelType()
                                                .hasPersonality()) {
                                    if (audioType.getModelType()
                                            .getPersonality().hasID()) {
                                        voiceId = audioType.getModelType()
                                                .getPersonality().getID()
                                                .getValue();
                                    }
                                }
                            }
                            if (!voiceId.isEmpty()
                                    && !voiceId.equalsIgnoreCase(VOICEID_INPUT)) {
                                String[] temp = voiceId.split(COMMA_MARK, 2);
                                corpusLogInfo.setVoiceFontID(temp[0]);
                                if (temp.length >= 2) {
                                    float f0 = new Float(temp[1]);
                                    corpusLogInfo.setF0(f0);
                                }
                            }

                            // set Sampling Frequency.
                            corpusLogInfo
                                    .setSamplingFrequency(samplingFrequency);

                            // set Output Audio Format.
                            corpusLogInfo
                                    .setOutputAudioFormat(outputAudioFormat);

                            // set Is Big Endian
                            corpusLogInfo.setIsBigEndian(isBigEndian);

                            // set Result State.
                            corpusLogInfo
                                    .setState(MCMLStatics.PROCESS_STATE_SUCCESS);

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
    }

    /**
     * setFirstFrameArrivedTime
     */
    @Override
    public void setFirstFrameArrivedTime(int corpusLogInfoID) {
    }

    /**
     * Sets LastFrameArrivedTime.
     */
    @Override
    public void setLastFrameArrivedTime(int corpusLogInfoID) {
        // get Last Frame ArrivedTime.
        long lastFrameArrivedTime = System.currentTimeMillis();

        if (m_IsUtteranceInfoOn) {
            synchronized (m_CorpusLogInfoMap) {
                TTSCorpusLogInfo corpusLogInfo = null;
                // is CorpusLogInfo?.
                if (m_CorpusLogInfoMap.containsKey(Integer
                        .valueOf(corpusLogInfoID))) {
                    // get TTSCorpusLogInfo instance.
                    corpusLogInfo = (TTSCorpusLogInfo) (m_CorpusLogInfoMap
                            .get((Integer.valueOf(corpusLogInfoID))));
                } else {
                    // create CorpusLogInfo
                    corpusLogInfo = new TTSCorpusLogInfo();

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
                    // get TTSCorpusLogInfo instance.
                    TTSCorpusLogInfo corpusLogInfo = (TTSCorpusLogInfo) (m_CorpusLogInfoMap
                            .get((Integer.valueOf(corpusLogInfoID))));

                    // set SR CompleteTime
                    corpusLogInfo.setSSCompleteTime(completeTime);
                }
            }
        }
    }

    /**
     * write information in the Corpuslogfile
     */
    @Override
    public void writeCorpusLogInfo(int corpusLogInfoID) {
        try {
            if (m_IsUtteranceInfoOn) {
                String outputString = "";
                Date ssCompleteTime = null;
                String userURI = "";
                String userID = "";
                String clientIP = "";
                String voiceID = "";
                String processOrder = "";
                String language = "";
                String inputSentence = "";
                String inputChunk = "";
                String state = "";

                String longitude = "";
                String latitude = "";

                String gender = "";
                String age = "";

                String nativeLanguage = "";
                String firstForeignLanguage = "";
                String secondForeignLanguage = "";

                String samplingFrequency = "";
                String outputAudioFormat = "";
                long outputUtteranceLength = 0;
                long processTime = 0;
                double rtf = 0;
                String voiceFontID = "";
                float f0 = Float.NaN;
                float rate = Float.NaN;

                synchronized (m_CorpusLogInfoMap) {
                    // is CorpusLogInfo?.
                    if (m_CorpusLogInfoMap.containsKey(Integer
                            .valueOf(corpusLogInfoID))) {
                        // get TTSCorpusLogInfo instance.
                        TTSCorpusLogInfo corpusLogInfo = (TTSCorpusLogInfo) (m_CorpusLogInfoMap
                                .get((Integer.valueOf(corpusLogInfoID))));

                        // Start Calculation
                        calculation(corpusLogInfo);

                        // get Corpus Log Infomation for Utterance Infomation
                        // Log.
                        ssCompleteTime = new Date(
                                corpusLogInfo.getSSCompleteTime());
                        userURI = corpusLogInfo.getUserURI();
                        userID = corpusLogInfo.getUserID();
                        clientIP = corpusLogInfo.getClientIP();
                        voiceID = corpusLogInfo.getVoiceID();
                        processOrder = corpusLogInfo.getProcessOrder();
                        language = corpusLogInfo.getLanguage();
                        inputSentence = corpusLogInfo.getInputSentence();
                        inputChunk = corpusLogInfo.getInputChunk();
                        state = corpusLogInfo.getState();

                        longitude = corpusLogInfo.getLongitude();
                        latitude = corpusLogInfo.getLatitude();

                        gender = corpusLogInfo.getGender();
                        age = corpusLogInfo.getAge();

                        nativeLanguage = corpusLogInfo.getNativeLanguage();
                        firstForeignLanguage = corpusLogInfo
                                .getFirstForeignLanguage();
                        secondForeignLanguage = corpusLogInfo
                                .getSecondForeignLanguage();

                        samplingFrequency = corpusLogInfo
                                .getSamplingFrequency();
                        outputAudioFormat = corpusLogInfo
                                .getOutputAudioFormat();
                        outputUtteranceLength = corpusLogInfo
                                .getOutputUtteranceLength();
                        processTime = corpusLogInfo.getProcessTime();
                        rtf = corpusLogInfo.getRTF();
                        voiceFontID = corpusLogInfo.getVoiceFontID();
                        f0 = corpusLogInfo.getF0();
                        rate = corpusLogInfo.getRate();
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
                outputString = createCorpusLogInfoString(ssCompleteTime,
                        userURI, userID, clientIP, voiceID, processOrder,
                        language, inputSentence, inputChunk, state,

                        longitude, latitude,

                        gender, age, nativeLanguage, firstForeignLanguage,
                        secondForeignLanguage, samplingFrequency,
                        outputAudioFormat, outputUtteranceLength, processTime,
                        rtf, voiceFontID, f0, rate);

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
     * Sets OutputMCMLInfo.
     */
    @Override
    public void setOutputMCMLInfo(MCMLData outputMCMLData, int corpusLogInfoID) {
        MCMLType outputMCML = outputMCMLData.getMCMLType();
        setOutputMCMLInfo(outputMCML, corpusLogInfoID);
        // set Output Wave Data
        setOutputWaveData(outputMCMLData.getBinaryDataList(), corpusLogInfoID);
    }

    // ------------------------------------------
    // protected member function
    // ------------------------------------------
    /** write item name in the corpus log file */
    @Override
    protected void writeItemName() throws IOException, FileNotFoundException {
        // Output order is set here
        String itemNameString = TTS_COMPLETE_TIME + ":" + USER_URI + ":"
                + USER_ID + ":" + CLIENT_IP + ":" + PROCESS_ORDER + ":"
                + LANGUAGE + ":" + INPUT_SENTENCE + ":" + INPUT_CHUNK + ":"
                + STATE + ":"

                + LONGITUDE + ":" + LATITUDE + ":"

                + VOICE_ID + ":" + GENDER + ":" + AGE + ":"

                + NATIVE_LANGUAGE + ":" + FIRST_FOREIGN_LANGUAGE + ":"
                + SECOND_FOREIGN_LANGUAGE + ":"

                + ":" + SAMPLING_FREQUENCY + ":" + OUTPUT_AUDIO_FORMAT + ":"
                + OUTPUT_UTTERANCE_LENGTH + ":" + PROCESS_TIME + ":" + RTF
                + ":" + VOICE_FONT_ID + ":" + F0 + ":" + RATE;

        // write First Line String.
        write(itemNameString);
        changeParagraph();
    }

    // ------------------------------------------
    // private member function
    // ------------------------------------------
    // pick out sentence from dataType
    private String createSentence(DataType dataType) {
        String sentence = "";

        try {
            SentenceSequenceType sentenceSequenceType = dataType.getText()
                    .getSentenceSequence();
            int sCount = sentenceSequenceType.getSentenceCount();

            for (int i = 0; i < sCount; i++) {
                // get sentence value.
                SentenceType s = sentenceSequenceType.getSentenceAt(i);
                if (s.hasSurface()) {
                    // get Data/Text/SentenceSequence/Sentence/Surface
                    SurfaceType2 surfaceType = s.getSurface();
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

    /**
     * private member functions
     *
     * @param corpusLogInfoID
     * @param userURI
     * @param userID
     * @param clientIP
     * @param voiceID
     * @param processOrder
     * @param language
     * @param longitude
     * @param latitude
     * @param gender
     * @param age
     * @param nativeLanguage
     * @param firstForeignLanguage
     * @param secondForeignLanguage
     * @param inputSentence
     * @param inputChunk
     * @param rate
     */
    private void setInputMCMLInfoSub(int corpusLogInfoID, String userURI,
            String userID, String clientIP, String voiceID,
            String processOrder, String language, String longitude,
            String latitude,

            String gender, String age, String nativeLanguage,
            String firstForeignLanguage, String secondForeignLanguage,
            String inputSentence, String inputChunk, float rate) {
        synchronized (m_CorpusLogInfoMap) {
            TTSCorpusLogInfo corpusLogInfo = null;
            // is CorpusLogInfo?.
            if (m_CorpusLogInfoMap
                    .containsKey(Integer.valueOf(corpusLogInfoID))) {
                // get TTSCorpusLogInfo instance.
                corpusLogInfo = (TTSCorpusLogInfo) (m_CorpusLogInfoMap
                        .get((Integer.valueOf(corpusLogInfoID))));
            } else {
                // create Instance.
                corpusLogInfo = new TTSCorpusLogInfo();
                m_CorpusLogInfoMap.put(Integer.valueOf(corpusLogInfoID),
                        corpusLogInfo);
            }

            // set information from InputMCML
            corpusLogInfo.setUserURI(userURI);
            corpusLogInfo.setUserID(userID);
            corpusLogInfo.setClientIP(clientIP);
            corpusLogInfo.setVoiceID(voiceID);
            corpusLogInfo.setProcessOrder(processOrder);
            corpusLogInfo.setLanguage(language);

            corpusLogInfo.setLongitude(longitude);
            corpusLogInfo.setLatitude(latitude);

            corpusLogInfo.setGender(gender);
            corpusLogInfo.setAge(age);

            corpusLogInfo.setNativeLanguage(nativeLanguage);
            corpusLogInfo.setFirstForeignLanguage(firstForeignLanguage);
            corpusLogInfo.setSecondForeignLanguage(secondForeignLanguage);

            corpusLogInfo.setInputSentence(inputSentence);
            corpusLogInfo.setInputChunk(inputChunk);
            corpusLogInfo.setRate(rate);
        }
        return;
    }

    private void setOutputWaveData(ArrayList<byte[]> waveData,
            int corpusLogInfoID) {
        if (m_IsUtteranceInfoOn) {
            synchronized (m_CorpusLogInfoMap) {
                // is corpusLogInfo?
                if (m_CorpusLogInfoMap.containsKey(Integer
                        .valueOf(corpusLogInfoID))) {
                    // get TTSCorpusLogInfo instance.
                    TTSCorpusLogInfo corpusLogInfo = (TTSCorpusLogInfo) (m_CorpusLogInfoMap
                            .get((Integer.valueOf(corpusLogInfoID))));
                    corpusLogInfo.setOutputData(waveData);
                }
            }
        }
        return;
    }

    // do calculation
    private void calculation(TTSCorpusLogInfo corpusLogInfo) {
        try {
            // get values neccessary for calculation
            Date lastFrameArrivedTime = new Date(
                    corpusLogInfo.getLastFrameArrivedTime());
            Date srCompleteTime = new Date(corpusLogInfo.getSSCompleteTime());
            ArrayList<byte[]> outputData = corpusLogInfo.getOutputData();
            String outputAudioFormat = corpusLogInfo.getOutputAudioFormat();
            boolean isBigEndian = corpusLogInfo.getIsBigEndian();
            String samplingFrequency = corpusLogInfo.getSamplingFrequency();

            // calculate outputDataBytes
            int outputDataBytes = 0;
            for (int i = 0; i < outputData.size(); i++) {
                outputDataBytes += outputData.get(i).length;
            }

            // ByteBuffer memory allocate
            ByteBuffer outputDataBuff = ByteBuffer.allocate(outputDataBytes);

            // set data to outputDataBuff
            for (int i = 0; i < outputData.size(); i++) {
                outputDataBuff.put(outputData.get(i));
            }

            long processTime = calculateProcessTime(lastFrameArrivedTime,
                    srCompleteTime);
            corpusLogInfo.setProcessTime(processTime);

            long utteranceLength = calculateUtteranceLength(outputDataBuff,
                    outputAudioFormat, isBigEndian, samplingFrequency);
            corpusLogInfo.setOutputUtteranceLength(utteranceLength);

            double rtf = calculateRTF(processTime, utteranceLength);
            corpusLogInfo.setRTF(rtf);
        } catch (MCMLException e) {
            ServerApLogger.getInstance().writeWarning(
                    "writeWaveLogFile() failed.");
            ServerApLogger.getInstance().writeWarning(e.getMessage());
            ServerApLogger.getInstance().writeWarning(e.getExplanation());
            ServerApLogger.getInstance().writeException(e);
        }
        return;
    }

    // calculate UtteranceLength from binarydata
    private long calculateUtteranceLength(ByteBuffer outputData,
            String audioformat, boolean isBigEndian, String samplingFrequency)
            throws MCMLException {
        long utteranceLength = 0;

        // in case data format is DSR
        if (audioformat.equalsIgnoreCase(MCMLStatics.AUDIO_DSR)) {
            ServerApLogger.getInstance().writeError(
                    "calculateUtteranceLength() irregular audioformat.");
        } else {
            ByteBuffer convertedData = null;
            // in case data format is ADPCM , convert to PCM
            if (audioformat.equalsIgnoreCase(MCMLStatics.AUDIO_ADPCM)) {
                // convert Wave Data(ADPCM=>PCM).
                convertedData = convertToPCM(outputData, false, isBigEndian);
            } else if (audioformat.equalsIgnoreCase(MCMLStatics.AUDIO_SPEEX)) {
                convertedData = convertSpeexToPCM(outputData, isBigEndian);
            } else {
                // PCM Big Endian=> PCM LittleEndian or not convert.
                convertedData = convertToPCM(outputData, true, isBigEndian);
            }

            int dataSize = convertedData.array().length;
            long samplingFrequencyValue = 0;
            if (samplingFrequency
                    .equalsIgnoreCase(MCMLStatics.SAMPLING_FREQUENCY_16K)) {
                samplingFrequencyValue = SAMPLINGFREQUENCY_16K;
            } else if (samplingFrequency
                    .equalsIgnoreCase(MCMLStatics.SAMPLING_FREQUENCY_8K)) {
                samplingFrequencyValue = SAMPLINGFREQUENCY_8K;
            } else {
                ServerApLogger
                        .getInstance()
                        .writeError(
                                "calculateUtteranceLength() irregular samplingfrequency.");
            }

            if (samplingFrequencyValue > 0) {
                utteranceLength = (dataSize * 1000)
                        / (samplingFrequencyValue * (DEFAULT_SAMPLING_BIT / 8) * DEFAULT_CHANNEL_NUM);
            }
        }
        return utteranceLength;
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

    // calculate RTF
    private double calculateRTF(long processTime, long utteranceLength) {
        double rtf = (double) processTime / utteranceLength;
        if (rtf <= 0.0) {
            rtf = 0;
            ServerApLogger.getInstance().writeError("calculateRTF() failed.");
        }
        return rtf;
    }

    // convert speex to pcm
    private ByteBuffer convertSpeexToPCM(ByteBuffer inputData,
            boolean isBigEndian) throws MCMLException {
        SpeexAudioConverter speexConv = new SpeexAudioConverter(false,
                isBigEndian, true, false, MCMLException.TTS);
        byte[] bytedata = inputData.array();
        try {
            bytedata = speexConv.pre_convert(bytedata);
        } finally {
            speexConv.destroy();
        }
        ByteBuffer pcmData = ByteBuffer.allocate(bytedata.length);
        pcmData.put(bytedata);
        return pcmData;
    }

    // convert adpcm data to pcm
    private ByteBuffer convertToPCM(ByteBuffer outputData, boolean isPCM,
            boolean isBigEndian) throws MCMLException {
        ByteBuffer pcmData = null;
        byte[] bytedata = outputData.array();

        // create AudioConverter.
        AudioConverter audioConv = null;
        if (isPCM && isBigEndian) {
            // AudioConverter(boolean srcIsRawData, boolean srcIsBigEndian,
            // boolean trgIsRawData, boolean trgIsBigEndian)
            // RAW and BigEndian => LittleEndian Converter
            audioConv = new AudioConverter(isPCM, isBigEndian, isPCM, false,
                    MCMLException.TTS);
        } else if (!isPCM) {
            // AudioConverter(boolean srcIsRawData, boolean srcIsBigEndian,
            // boolean trgIsRawData, boolean trgIsBigEndian)
            // ADPCM => RAW LittleEndian Converter
            audioConv = new AudioConverter(isPCM, isBigEndian, true, false,
                    MCMLException.TTS);
        }

        if (audioConv != null) {
            byte[] pcmbyteData = audioConv.convert(bytedata);
            pcmData = ByteBuffer.allocate(pcmbyteData.length);
            pcmData.put(pcmbyteData);
        } else {
            pcmData = ByteBuffer.allocate(bytedata.length);
            pcmData.put(bytedata);
        }
        return pcmData;
    }

    /**
     * create corpus log information
     *
     * @param ssCompleteTime
     * @param userURI
     * @param userID
     * @param clientIP
     * @param voiceID
     * @param processOrder
     * @param language
     * @param inputSentence
     * @param inputChunk
     * @param state
     * @param longitude
     * @param latitude
     * @param gender
     * @param age
     * @param nativeLanguage
     * @param firstForeignLanguage
     * @param secondForeignLanguage
     * @param samplingFrequency
     * @param outputAudioFormat
     * @param outputUtteranceLength
     * @param processTime
     * @param rtf
     * @param voiceFontID
     * @param f0
     * @param rate
     * @return Output character string
     */
    private String createCorpusLogInfoString(Date ssCompleteTime,
            String userURI, String userID, String clientIP, String voiceID,
            String processOrder, String language, String inputSentence,
            String inputChunk, String state,

            String longitude, String latitude,

            String gender, String age, String nativeLanguage,
            String firstForeignLanguage, String secondForeignLanguage,
            String samplingFrequency, String outputAudioFormat,
            long outputUtteranceLength, long processTime, double rtf,
            String voiceFontID, float f0, float rate) {
        String outputString = "";

        // Output order is set here
        // create output string.
        outputString = OUTPUT_DATE_FORMAT.format(ssCompleteTime) + ":";
        outputString += userURI + ":";
        outputString += userID + ":";
        outputString += clientIP + ":";

        outputString += processOrder + ":";
        outputString += language + ":";
        outputString += inputSentence + ":";
        outputString += inputChunk + ":";
        outputString += state + ":";

        outputString += longitude + ":";
        outputString += latitude + ":";

        outputString += voiceID + ":";

        outputString += gender + ":";
        outputString += age + ":";

        outputString += nativeLanguage + ":";
        outputString += firstForeignLanguage + ":";
        outputString += secondForeignLanguage + ":";

        outputString += samplingFrequency + ":";
        outputString += outputAudioFormat + ":";

        if (outputUtteranceLength > 0) {
            outputString += Long.toString(outputUtteranceLength);
        }
        outputString += ":";

        if (processTime > 0) {
            outputString += Long.toString(processTime);
        }
        outputString += ":";

        if (rtf > 0.0) {
            outputString += Double.toString(rtf);
        }
        outputString += ":";

        outputString += voiceFontID + ":";
        if (!Float.isNaN(f0)) {
            outputString += Float.toString(f0);
        }
        outputString += ":";

        if (!Float.isNaN(rate)) {
            outputString += Float.toString(rate);
        }

        return outputString;
    }
}
