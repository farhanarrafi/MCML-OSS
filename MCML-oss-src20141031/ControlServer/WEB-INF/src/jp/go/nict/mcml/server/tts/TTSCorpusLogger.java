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

import jp.go.nict.mcml.exception.MCMLException;
import jp.go.nict.mcml.serverap.common.AudioConverter;
import jp.go.nict.mcml.serverap.common.MCMLData;
import jp.go.nict.mcml.serverap.common.ServerApCorpusLogger;
import jp.go.nict.mcml.xml.MCMLStatics;
import jp.go.nict.mcml.xml.XMLTypeTools;

import org.apache.log4j.Logger;

import com.MCML.AudioType;
import com.MCML.ChunkType;
import com.MCML.DataType;
import com.MCML.InputUserProfileType;
import com.MCML.MCMLDoc;
import com.MCML.ResponseType;
import com.MCML.SentenceSequenceType;
import com.MCML.SentenceType;
import com.MCML.SurfaceType;
import com.MCML.SurfaceType2;

/**
 * TTSCorpusLogger class (for TTS server).
 * 
 * @version 4.0
 * @serial 20120921
 * 
 */
public class TTSCorpusLogger extends ServerApCorpusLogger {
    private static final Logger LOG = Logger.getLogger(TTSCorpusLogger.class
            .getName());
    // ------------------------------------------
    // private member constant
    // ------------------------------------------
    private static final String TTS_COMPLETE_TIME = "TTSCompleteTime";
    private static final String SAMPLING_FREQUENCY = "SamplingFrequency";
    private static final String OUTPUT_AUDIO_FORMAT = "OutputAudioFormat";
    private static final String OUTPUT_UTTERANCE_LENGTH = "OutputUtteranceLength";
    private static final String VOICE_FONT_ID = "VoiceFontID";
    private static final String F0 = "F0";
    private static final String VOICEID_INPUT = "INPUT";
    private static final String COMMA_MARK = ",";

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
    public TTSCorpusLogger(boolean utteranceInfoOnOff,
            String baseDirectoryName, String language, String prefix,
            String[] removedSymbols, boolean urlInfoOnOff) {
        super(utteranceInfoOnOff, baseDirectoryName, language, prefix,
                removedSymbols, urlInfoOnOff);
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

                // get TargetLanguage
                String language = XMLTypeTools.getLanguageTypeID(inputMCMLDoc);

                // get voiceID,age,gender
                String voiceID = "";
                String age = "";
                String gender = "";
                InputUserProfileType inputUserProfile = XMLTypeTools
                        .getInputUserProfileType(inputMCMLDoc);
                if (inputUserProfile != null) {
                    if (inputUserProfile.ID.exists()) {
                        voiceID = inputUserProfile.ID.getValue();
                    }

                    if (inputUserProfile.Age.exists()) {
                        age = Integer.toString(inputUserProfile.Age.getValue());
                    }

                    if (inputUserProfile.Gender.exists()) {
                        gender = inputUserProfile.Gender.getValue();
                    }

                }

                DataType inputData = XMLTypeTools
                        .getDataTypeFromMCML(inputMCMLDoc);

                // get sentence
                String inputSentence = createSentence(inputData);

                // get chunk
                String inputChunk = createChunk(inputData);

                // set information from inputMCML to CorpusLogInfo.
                setInputMCMLInfoSub(corpusLogInfoID, userURI, userID, clientIP,
                        voiceID, processOrder, language, gender, age,
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
                    // get TTSCorpusLogInfo instance.
                    TTSCorpusLogInfo corpusLogInfo = (TTSCorpusLogInfo) (m_CorpusLogInfoMap
                            .get((Integer.valueOf(corpusLogInfoID))));

                    try {
                        DataType outputData = XMLTypeTools
                                .getDataTypeFromMCML(outputMCMLDoc);
                        corpusLogInfo.setDataType(outputData);
                        if (outputData != null) {
                            int samplingFrequency = 0;
                            String outputAudioFormat = "";
                            boolean isBigEndian = true;
                            String voiceId = "";
                            ResponseType responseType = outputMCMLDoc.MCML
                                    .first().Server.first().Response.first();
                            AudioType audioType = XMLTypeTools
                                    .getAudioType(responseType);
                            if (audioType != null) {
                                if (audioType.Signal.exists()) {
                                    if (audioType.Signal.first().AudioFormat
                                            .exists()) {
                                        outputAudioFormat = audioType.Signal
                                                .first().AudioFormat.getValue();
                                    }
                                    if (audioType.Signal.first().Endian
                                            .exists()) {
                                        String endian = audioType.Signal
                                                .first().Endian.getValue();
                                        if (endian
                                                .equalsIgnoreCase(MCMLStatics.ENDIAN_BIG)) {
                                            isBigEndian = true;
                                        } else if (endian
                                                .equalsIgnoreCase(MCMLStatics.ENDIAN_LITTLE)) {
                                            isBigEndian = false;
                                        }
                                    }
                                    if (audioType.Signal.first().SamplingRate
                                            .exists()) {
                                        samplingFrequency = audioType.Signal
                                                .first().SamplingRate
                                                .getValue();
                                    }
                                }
                                if (audioType.ModelType.exists()
                                        && audioType.ModelType.first().Personality
                                                .exists()) {
                                    if (audioType.ModelType.first().Personality
                                            .first().ID.exists()) {
                                        voiceId = audioType.ModelType.first().Personality
                                                .first().ID.getValue();
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

    /**
     * {@inheritDoc}
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

    /** write information in the Corpuslogfile */
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
                int processOrder = 0;
                String language = "";
                String inputSentence = "";
                String inputChunk = "";
                String state = "";
                String gender = "";
                String age = "";
                int samplingFrequency = 0;
                String outputAudioFormat = "";
                long outputUtteranceLength = 0;
                long processTime = 0;
                double rtf = 0;
                String voiceFontID = "";
                float f0 = Float.NaN;
                ArrayList<String> destinationURLList = null;
                String destinationURL = "";

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
                        gender = corpusLogInfo.getGender();
                        age = corpusLogInfo.getAge();
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
                        destinationURLList = corpusLogInfo.getDestinationURL();
                        destinationURL = createDestinationURL(destinationURLList);
                    } else {
                        // no output data.
                        LOG.warn("CorpusLog Info is not exists.");
                        return;
                    }
                }

                // create OutputDirectory(by not exist OutputDirectory).
                boolean isCreate = createDirectory(m_BasedirectoryName,
                        language, destinationURLList, false);

                if ((!m_CorpusLogStreamMap.containsKey(m_LanguagePrefix))
                        || isCreate) {
                    // create Corpus Log file.
                    createFile();

                    // write first line.
                    writeItemName();
                }

                // create CorpusLogInfoString.
                outputString = createCorpusLogInfoString(ssCompleteTime,
                        userURI, userID, clientIP, voiceID, processOrder,
                        language, inputSentence, inputChunk, state, gender,
                        age, samplingFrequency, outputAudioFormat,
                        outputUtteranceLength, processTime, rtf, voiceFontID,
                        f0, destinationURL);

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
        return;
    }

    /** set first frame arrived time. */
    @Override
    public void setFirstFrameArrivedTime(int corpusLogInfoID,
            long firstFrameArrivedTime) {
        // no process
    }

    /** set last frame arrived time. */
    @Override
    public void setLastFrameArrivedTime(int corpusLogInfoID,
            long lastFrameArrivedTime) {
        // no process
    }

    /** set "Output" wave data */
    @Override
    public void setWaveData(ArrayList<byte[]> waveData, int corpusLogInfoID) {
        setOutputWaveData(waveData, corpusLogInfoID);
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
        // set Output Wave Data
        setOutputWaveData(outputMCMLData.getBinaryDataList(), corpusLogInfoID);
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
                    // get TTSCorpusLogInfo instance.
                    TTSCorpusLogInfo corpusLogInfo = (TTSCorpusLogInfo) (m_CorpusLogInfoMap
                            .get((Integer.valueOf(corpusLogInfoID))));

                    // set SR CompleteTime
                    corpusLogInfo.setDestinationURL(urlList);
                }
            }
        }
    }

    // ------------------------------------------
    // protected member function
    // ------------------------------------------
    // write item name in the corpus log file
    @Override
    protected void writeItemName() throws IOException, FileNotFoundException {
        String itemNameString = TTS_COMPLETE_TIME + ":" + USER_URI + ":"
                + USER_ID + ":" + CLIENT_IP + ":" + VOICE_ID + ":"
                + PROCESS_ORDER + ":" + LANGUAGE + ":" + INPUT_SENTENCE + ":"
                + INPUT_CHUNK + ":" + STATE + ":" + GENDER + ":" + AGE + ":"
                + SAMPLING_FREQUENCY + ":" + OUTPUT_AUDIO_FORMAT + ":"
                + OUTPUT_UTTERANCE_LENGTH + ":" + PROCESS_TIME + ":" + RTF
                + ":" + VOICE_FONT_ID + ":" + F0;

        // add corpus log information only socketservlet
        if (m_IsURLInfoOn) {
            itemNameString += ":" + DESTINATION_URL;
        }

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
            SentenceSequenceType sentenceSequenceType = dataType.Text.first().SentenceSequence
                    .first();
            int sCount = sentenceSequenceType.Sentence.count();

            for (int i = 0; i < sCount; i++) {
                // get sentence value.
                SentenceType s = sentenceSequenceType.Sentence.at(i);
                if (s.Surface.exists()) {
                    // get Data/Text/SentenceSequence/Sentence/Surface
                    SurfaceType2 surfaceType = s.Surface.first();
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

    private void setInputMCMLInfoSub(int corpusLogInfoID, String userURI,
            String userID, String clientIP, String voiceID, int processOrder,
            String language, String gender, String age, String inputSentence,
            String inputChunk) {
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
            corpusLogInfo.setGender(gender);
            corpusLogInfo.setAge(age);
            corpusLogInfo.setInputSentence(inputSentence);
            corpusLogInfo.setInputChunk(inputChunk);
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
            int samplingFrequency = corpusLogInfo.getSamplingFrequency();

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
            LOG.warn(e.getMessage(), e);
        }
        return;
    }

    // calculate UtteranceLength from binarydata
    private long calculateUtteranceLength(ByteBuffer outputData,
            String audioformat, boolean isBigEndian, int samplingFrequency)
            throws MCMLException {
        long utteranceLength = 0;

        // in case data format is DSR
        if (audioformat.equalsIgnoreCase(MCMLStatics.AUDIO_DSR)) {
            LOG.error("irregular audioformat.");
        } else {
            ByteBuffer convertedData = null;
            // in case data format is ADPCM , convert to PCM
            if (audioformat.equalsIgnoreCase(MCMLStatics.AUDIO_ADPCM)) {
                // convert Wave Data(ADPCM=>PCM).
                convertedData = convertToPCM(outputData, false, isBigEndian);
            } else {
                // PCM Big Endian=> PCM LittleEndian or not convert.
                convertedData = convertToPCM(outputData, true, isBigEndian);
            }

            int dataSize = convertedData.array().length;
            long samplingFrequencyValue = 0;
            if (samplingFrequency == MCMLStatics.SAMPLING_FREQUENCY_16K) {

                samplingFrequencyValue = SAMPLINGFREQUENCY_16K;
            } else if (samplingFrequency == MCMLStatics.SAMPLING_FREQUENCY_8K) {

                samplingFrequencyValue = SAMPLINGFREQUENCY_8K;
            } else {
                LOG.warn("irregular samplingfrequency.");
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
            LOG.error("calculate ProcessTime failed.");
        }
        return processtime;
    }

    // calculate RTF
    private double calculateRTF(long processTime, long utteranceLength) {
        double rtf = (double) processTime / utteranceLength;
        if (rtf <= 0.0) {
            rtf = 0;
            LOG.error("calculate RTF failed.");
        }
        return rtf;
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

    private String createCorpusLogInfoString(Date ssCompleteTime,
            String userURI, String userID, String clientIP, String voiceID,
            int processOrder, String language, String inputSentence,
            String inputChunk, String state, String gender, String age,
            int samplingFrequency, String outputAudioFormat,
            long outputUtteranceLength, long processTime, double rtf,
            String voiceFontID, float f0, String destinationURL) {
        String outputString = "";

        // create output string.
        outputString = OUTPUT_DATE_FORMAT.format(ssCompleteTime) + ":";
        outputString += userURI + ":";
        outputString += userID + ":";
        outputString += clientIP + ":";
        outputString += voiceID + ":";
        outputString += Integer.toString(processOrder) + ":";
        outputString += language + ":";
        outputString += inputSentence + ":";
        outputString += inputChunk + ":";
        outputString += state + ":";
        outputString += gender + ":";
        outputString += age + ":";
        outputString += Integer.toString(samplingFrequency) + ":";
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

        // add corpus log information only socketservlet
        if (m_IsURLInfoOn) {
            outputString += ":";
            outputString += destinationURL;
        }
        return outputString;
    }
}
