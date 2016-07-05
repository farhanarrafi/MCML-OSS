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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;

import jp.go.nict.mcml.exception.MCMLException;
import jp.go.nict.mcml.serverap.common.AudioConverter;
import jp.go.nict.mcml.serverap.common.EngineInfo;
import jp.go.nict.mcml.serverap.common.MCMLData;
import jp.go.nict.mcml.serverap.common.ServerApCorpusLogger;
import jp.go.nict.mcml.serverap.common.WaveLogWriter;
import jp.go.nict.mcml.xml.MCMLStatics;
import jp.go.nict.mcml.xml.XMLTypeTools;

import org.apache.log4j.Logger;

import com.MCML.AudioType;
import com.MCML.ChunkType;
import com.MCML.DataType;
import com.MCML.InputUserProfileType;
import com.MCML.MCMLDoc;
import com.MCML.SentenceSequenceType;
import com.MCML.SentenceType;
import com.MCML.SurfaceType;
import com.MCML.SurfaceType2;

/**
 * ASRCorpusLogger class. (For ASR server)
 * 
 * @version 4.0
 * @since 20120921
 */
public class ASRCorpusLogger extends ServerApCorpusLogger {
    private static final Logger LOG = Logger.getLogger(ASRCorpusLogger.class
            .getName());

    // ------------------------------------------
    // private member constants(class field)
    // ------------------------------------------
    private static final String ASR_COMPLETE_TIME = "ASRCompleteTime";
    private static final String NATIVE = "Native";
    private static final String INPUT_AUDIO_FORMAT = "InputAudioFormat";
    private static final String UTTERANCE_LENGTH = "UtteranceLength";
    private static final String NETWORK_DELAY = "NetworkDelay";
    private static final String FIRST_FRAME_ARRIVED_TIME = "FirstFrameArrivedTime";
    private static final String LAST_FRAME_ARRIVED_TIME = "LastFrameArrivedTime";
    private static final String SPEECH_DATA_FILE_NAME = "SpeechDataFileName";
    private static final String ACOUSTIC_MODEL_NAME = "AcousticModelName";
    private static final String NICTLATTICE_MODEL = "NICTlatticeModel";
    private static final String NICTRESCORE_MODEL = "NICTrescoreModel";
    private static final String RECOGNITION_RESULT_FILE_NAME_EUC = "RecoginitionResultFileNameEUC";
    private static final String RECOGNITION_RESULT_FILE_NAME_UTF8 = "RecoginitionResultFileNameUTF8";

    // ------------------------------------------
    // private member valuables
    // ------------------------------------------
    private boolean m_IsEucOutputOn;
    private boolean m_IsUtf8OutputOn;
    private boolean m_IsInputSpeechDataOutputOn;
    private boolean m_IsModelNameOutputOn;

    // -----------------------------------------
    // public member functions
    // ------------------------------------------
    /**
     * constructor
     * 
     * @param isUtteranceInfoOutputOn
     * @param basedirectoryName
     * @param language
     * @param prefix
     * @param isEucOutputOn
     * @param isUtf8OutputOn
     * @param isInputSpeechDataOutputOn
     * @param isModelNameOutputOn
     * @param removedSymbols
     * @param isURLInfoOutputOn
     */
    public ASRCorpusLogger(boolean isUtteranceInfoOutputOn,
            String basedirectoryName, String language, String prefix,
            boolean isEucOutputOn, boolean isUtf8OutputOn,
            boolean isInputSpeechDataOutputOn, boolean isModelNameOutputOn,
            String[] removedSymbols, boolean isURLInfoOutputOn) {
        super(isUtteranceInfoOutputOn, basedirectoryName, language, prefix,
                removedSymbols, isURLInfoOutputOn);

        // set Output Flag.
        m_IsEucOutputOn = isEucOutputOn;
        m_IsUtf8OutputOn = isUtf8OutputOn;
        m_IsInputSpeechDataOutputOn = isInputSpeechDataOutputOn;
        m_IsModelNameOutputOn = isModelNameOutputOn;
    }

    /** set first frame arrived time. */
    @Override
    public void setFirstFrameArrivedTime(int corpusLogInfoID,
            long firstFrameArrivedTime) {
        if (m_IsUtteranceInfoOn) {
            synchronized (m_CorpusLogInfoMap) {
                // get ASRCorpusLogInfo instance.
                ASRCorpusLogInfo corpusLogInfo = null;
                if (m_CorpusLogInfoMap.containsKey(Integer
                        .valueOf(corpusLogInfoID))) {
                    corpusLogInfo = (ASRCorpusLogInfo) (m_CorpusLogInfoMap
                            .get((Integer.valueOf(corpusLogInfoID))));
                } else {
                    // create CorpusLogInfo
                    corpusLogInfo = new ASRCorpusLogInfo();

                    // set LogInfoMap;
                    m_CorpusLogInfoMap.put(Integer.valueOf(corpusLogInfoID),
                            corpusLogInfo);
                }

                // set First Frame ArrivedTime.
                corpusLogInfo.setFirstFrameArrivedTime(firstFrameArrivedTime);
            }
        }
    }

    /** set first frame arrived time. */
    @Override
    public void setFirstFrameArrivedTime(int corpusLogInfoID) {
        // get First Frame ArrivedTime.
        long firstFrameArrivedTime = System.currentTimeMillis();
        setFirstFrameArrivedTime(corpusLogInfoID, firstFrameArrivedTime);
    }

    /** set Last Frame ArrivedTime. */
    @Override
    public void setLastFrameArrivedTime(int corpusLogInfoID,
            long lastFrameArrivedTime) {
        if (m_IsUtteranceInfoOn) {
            synchronized (m_CorpusLogInfoMap) {
                ASRCorpusLogInfo corpusLogInfo = null;
                // is CorpusLogInfo?.
                if (m_CorpusLogInfoMap.containsKey(Integer
                        .valueOf(corpusLogInfoID))) {
                    // get ASRCorpusLogInfo instance.
                    corpusLogInfo = (ASRCorpusLogInfo) (m_CorpusLogInfoMap
                            .get((Integer.valueOf(corpusLogInfoID))));
                } else {
                    // create CorpusLogInfo
                    corpusLogInfo = new ASRCorpusLogInfo();

                    // set LogInfoMap;
                    m_CorpusLogInfoMap.put(Integer.valueOf(corpusLogInfoID),
                            corpusLogInfo);
                }
                // set Last Frame ArrivedTime.
                corpusLogInfo.setLastFrameArrivedTime(lastFrameArrivedTime);
            }
        }
    }

    /** set Last Frame ArrivedTime. */
    @Override
    public void setLastFrameArrivedTime(int corpusLogInfoID) {
        // get Last Frame ArrivedTime.
        long lastFrameArrivedTime = System.currentTimeMillis();
        setLastFrameArrivedTime(corpusLogInfoID, lastFrameArrivedTime);
    }

    /** set information from inputMCML */
    @Override
    public void setInputMCMLInfo(MCMLDoc inputMCMLDoc, int corpusLogInfoID) {
        try {
            // UtteranceInfo on or ResultLog on or WaveLog on.
            if (m_IsUtteranceInfoOn || m_IsUtf8OutputOn || m_IsEucOutputOn
                    || m_IsInputSpeechDataOutputOn) {
                // get User's Ingfomation.
                String userURI = "";
                String userID = "";
                String clientIP = "";
                String recievedTimeOnServlet = "";

                // get URI
                userURI = XMLTypeTools.getURI(inputMCMLDoc);

                // get Client IP address
                clientIP = XMLTypeTools.getIPAddressFromUserURI(userURI);

                // // get UserID
                userID = XMLTypeTools.getUserID(inputMCMLDoc);

                // get processOrder
                int processOrder = XMLTypeTools.getProcessOrder(inputMCMLDoc);

                // get Task
                String location = "";

                // get Domain
                String domain = "";

                // get InputAudioFormat
                String inputAudioFormat = "";

                // get isBigEndian
                boolean isBigEndian = true;
                AudioType audioType = XMLTypeTools.getAudioType(inputMCMLDoc);
                if (audioType != null) {
                    if (audioType.ModelType.exists()) {
                        if (audioType.ModelType.first().Task.exists()) {
                            location = audioType.ModelType.first().Task.first()
                                    .getValue();
                        }
                        if (audioType.ModelType.first().Domain.exists()) {
                            domain = audioType.ModelType.first().Domain.first()
                                    .getValue();
                        }
                    }
                    if (audioType.Signal.exists()) {
                        if (audioType.Signal.first().AudioFormat.exists()) {
                            inputAudioFormat = audioType.Signal.first().AudioFormat
                                    .getValue();
                        }
                        if (audioType.Signal.first().Endian.exists()) {
                            String endian = audioType.Signal.first().Endian
                                    .getValue();
                            if (endian.equalsIgnoreCase(MCMLStatics.ENDIAN_BIG)) {
                                isBigEndian = true;
                            } else if (endian
                                    .equalsIgnoreCase(MCMLStatics.ENDIAN_LITTLE)) {
                                isBigEndian = false;
                            }
                        }
                    }
                }

                // get voiceID,age,gender
                String voiceID = "";
                String age = "";
                String gender = "";
                String fluency = "";
                InputUserProfileType inputUserProfile = XMLTypeTools
                        .getInputUserProfileType(inputMCMLDoc);
                if (inputUserProfile != null) {
                    if (inputUserProfile.ID.exists()) {
                        voiceID = inputUserProfile.ID.getValue();
                    }

                    if (inputUserProfile.Age.exists()) {
                        age = String.valueOf(inputUserProfile.Age.getValue());
                    }

                    if (inputUserProfile.Gender.exists()) {
                        gender = inputUserProfile.Gender.getValue();
                    }

                }
                // get SourceLanguage
                String language = XMLTypeTools.getInputLanguageID(inputMCMLDoc);

                // get nativeVal
                String nativeVal = "";
                fluency = XMLTypeTools.getInputLanguageFluency(inputMCMLDoc);
                if (!fluency.isEmpty()) {
                    if (Integer.valueOf(fluency).intValue() >= 5) {
                        nativeVal = MCMLStatics.STRING_YES;
                    } else {
                        nativeVal = MCMLStatics.STRING_NO;
                    }
                }

                // set information from inputMCML to CorpusLogInfo.
                setInputMCMLInfoSub(corpusLogInfoID, userURI, userID, clientIP,
                        voiceID, processOrder, location, domain, language,
                        gender, age, nativeVal, inputAudioFormat, isBigEndian,
                        recievedTimeOnServlet);
            }

            // remove UserInfomation String(ClientIP and Recieved Time on
            // Servlet).
            XMLTypeTools.removeUserInfoStr(inputMCMLDoc);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    /** set Input Speech Data. */
    @Override
    public void setWaveData(ArrayList<byte[]> waveData, int corpusLogInfoID) {
        if (m_IsInputSpeechDataOutputOn || m_IsUtteranceInfoOn) {
            synchronized (m_CorpusLogInfoMap) {
                // is corpusLogInfo?
                if (m_CorpusLogInfoMap.containsKey(Integer
                        .valueOf(corpusLogInfoID))) {
                    // get ASRCorpusLogInfo instance.
                    ASRCorpusLogInfo corpusLogInfo = (ASRCorpusLogInfo) (m_CorpusLogInfoMap
                            .get((Integer.valueOf(corpusLogInfoID))));
                    corpusLogInfo.setInputData(waveData);
                }
            }
        }
    }

    /**
     * Gets WaveData.
     * 
     * @param corpusLogInfoID
     * @return WaveData
     */
    public ArrayList<byte[]> getWaveData(int corpusLogInfoID) {
        if (m_IsInputSpeechDataOutputOn || m_IsUtteranceInfoOn) {
            synchronized (m_CorpusLogInfoMap) {
                if (m_CorpusLogInfoMap.containsKey(Integer
                        .valueOf(corpusLogInfoID))) {
                    // get ASRCorpusLogInfo instance.
                    ASRCorpusLogInfo corpusLogInfo = (ASRCorpusLogInfo) (m_CorpusLogInfoMap
                            .get((Integer.valueOf(corpusLogInfoID))));

                    ArrayList<byte[]> ret = new ArrayList<byte[]>();
                    ret = corpusLogInfo.getInputData();
                    return ret;
                }
            }
        }
        return null;
    }

    /** set information from outputMCML */
    @Override
    public void setOutputMCMLInfo(MCMLDoc outputMCMLDoc, int corpusLogInfoID) {
        // UtteranceInfo on or ResultLog on.
        if (m_IsUtteranceInfoOn || m_IsUtf8OutputOn || m_IsEucOutputOn) {
            synchronized (m_CorpusLogInfoMap) {
                // is CorpusLogInfo?.
                if (m_CorpusLogInfoMap.containsKey(Integer
                        .valueOf(corpusLogInfoID))) {
                    // get ASRCorpusLogInfo instance.
                    ASRCorpusLogInfo corpusLogInfo = (ASRCorpusLogInfo) (m_CorpusLogInfoMap
                            .get((Integer.valueOf(corpusLogInfoID))));

                    try {
                        DataType outputData = XMLTypeTools
                                .getDataTypeFromMCML(outputMCMLDoc);
                        corpusLogInfo.setDataType(outputData);
                        if (outputData != null) {
                            // get sentence
                            String sentence = createSentence(outputData);

                            // get chunk
                            String chunk = createChunk(outputData);

                            // set sentence.
                            corpusLogInfo.setSentence(sentence);

                            // set chunk.
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

    /**
     * {@inheritDoc}
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
                    // get ASRCorpusLogInfo instance.
                    ASRCorpusLogInfo corpusLogInfo = (ASRCorpusLogInfo) (m_CorpusLogInfoMap
                            .get((Integer.valueOf(corpusLogInfoID))));

                    // set SR CompleteTime
                    corpusLogInfo.setSRCompleteTime(completeTime);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEngineInfo(EngineInfo engineInfo, int corpusLogInfoID) {
        if (m_IsUtteranceInfoOn && engineInfo != null) {
            synchronized (m_CorpusLogInfoMap) {
                // is CorpusLogInfo?.
                if (m_CorpusLogInfoMap.containsKey(Integer
                        .valueOf(corpusLogInfoID))) {
                    // get ASRCorpusLogInfo instance.
                    ASRCorpusLogInfo corpusLogInfo = (ASRCorpusLogInfo) (m_CorpusLogInfoMap
                            .get((Integer.valueOf(corpusLogInfoID))));

                    // set NICTlattice Model Name.
                    corpusLogInfo
                            .setNICTlatticeModel(((ASREngineInfo) engineInfo)
                                    .getNICTlatticeModelName());

                    // set NICTrescore Model Name.
                    corpusLogInfo
                            .setNICTrescoreModel(((ASREngineInfo) engineInfo)
                                    .getNICTrescoreModelName());

                    // set Acoustic Model Name.
                    corpusLogInfo
                            .setAcousticModelName(((ASREngineInfo) engineInfo)
                                    .getAcousticModelName());
                }
            }
        }
    }

    //
    /**
     * write information in the Corpus log file
     * 
     * @param corpusLogInfoID
     */
    @Override
    public void writeCorpusLogInfo(int corpusLogInfoID) {
        try {
            if (m_IsUtteranceInfoOn || m_IsEucOutputOn || m_IsUtf8OutputOn
                    || m_IsInputSpeechDataOutputOn) {
                String outputString = "";
                Date srCompleteTime = null;
                String clientIP = "";
                String userURI = "";
                String userID = "";
                String voiceID = "";
                int processOrder = 0;
                String locaton = "";
                String domain = "";
                String language = "";
                String sentence = "";
                String chunk = "";
                String state = "";
                String gender = "";
                String age = "";
                String nativeVal = "";
                String inputAudioFormat = "";
                String acousticModelName = "";
                String nictlatticeModelName = "";
                String nictrescoreModelName = "";
                long utteranceLength = 0;
                long processTime = 0;
                long netWorkdelay = 0;
                double rtf = 0.0;
                Date firstFrameArrivedTime = null;
                Date lastFrameArrivedTime = null;
                DataType dataType = null;
                ArrayList<byte[]> inputData = null;
                boolean isBigEndian = false;
                ArrayList<String> destinationURLList = null;
                String destinationURL = "";

                synchronized (m_CorpusLogInfoMap) {
                    // is CorpusLogInfo?.
                    if (m_CorpusLogInfoMap.containsKey(Integer
                            .valueOf(corpusLogInfoID))) {
                        // get ASRCorpusLogInfo instance.
                        ASRCorpusLogInfo corpusLogInfo = (ASRCorpusLogInfo) (m_CorpusLogInfoMap
                                .get((Integer.valueOf(corpusLogInfoID))));

                        if (m_IsUtteranceInfoOn) {
                            // Start Calculation
                            calculation(corpusLogInfo);

                            // get Corpus Log Information for Utterance
                            // Information Log.
                            srCompleteTime = new Date(
                                    corpusLogInfo.getSRCompleteTime());
                            voiceID = corpusLogInfo.getVoiceID();
                            locaton = corpusLogInfo.getLocation();
                            domain = corpusLogInfo.getDomain();

                            language = corpusLogInfo.getLanguage();
                            sentence = corpusLogInfo.getSentence();
                            chunk = corpusLogInfo.getChunk();
                            state = corpusLogInfo.getState();
                            gender = corpusLogInfo.getGender();
                            age = corpusLogInfo.getAge();
                            nativeVal = corpusLogInfo.getNative();
                            utteranceLength = corpusLogInfo
                                    .getUtteranceLength();
                            processTime = corpusLogInfo.getProcessTime();
                            netWorkdelay = corpusLogInfo.getNetworkDelay();
                            rtf = corpusLogInfo.getRTF();
                            firstFrameArrivedTime = new Date(
                                    corpusLogInfo.getFirstFrameArrivedTime());
                            lastFrameArrivedTime = new Date(
                                    corpusLogInfo.getLastFrameArrivedTime());
                            acousticModelName = corpusLogInfo
                                    .getAcousticModelName();
                            nictlatticeModelName = corpusLogInfo
                                    .getNICTlatticeModel();
                            nictrescoreModelName = corpusLogInfo
                                    .getNICTrescoreModel();
                            destinationURLList = corpusLogInfo
                                    .getDestinationURL();
                            destinationURL = createDestinationURL(destinationURLList);
                        }

                        // get Corpus Log Information.
                        userURI = corpusLogInfo.getUserURI();
                        userID = corpusLogInfo.getUserID();
                        clientIP = corpusLogInfo.getClientIP();
                        processOrder = corpusLogInfo.getProcessOrder();
                        inputAudioFormat = corpusLogInfo.getInputAudioFormat();

                        // get Corpus Log Information for Result Log.
                        if (m_IsEucOutputOn || m_IsUtf8OutputOn) {
                            dataType = corpusLogInfo.getDataType();
                        }

                        // get Corpus Log Information for Wave Log.
                        if (m_IsInputSpeechDataOutputOn) {
                            inputData = corpusLogInfo.getInputData();
                            isBigEndian = corpusLogInfo.getIsBigEndian();
                        }
                    } else {
                        // no output data.
                        LOG.warn("CorpusLog Info is not exists.");
                        return;
                    }
                }

                // create OutputDirectory(by not exist OutputDirectory).
                boolean isCreate = createDirectory(m_BasedirectoryName,
                        language, destinationURLList, false);

                String speechDataFileName = "";
                String recognitionResultFileNameEUC = "";
                String recognitionResultFileNameUTF8 = "";

                if (m_IsInputSpeechDataOutputOn || m_IsEucOutputOn
                        || m_IsUtf8OutputOn) {
                    // get date for file name
                    Date fileNameDate = new Date();

                    if (m_IsInputSpeechDataOutputOn) {
                        // write Wave Log File and get SpeechDataFileName.
                        speechDataFileName = writeWaveLogFile(clientIP,
                                userURI, userID, processOrder, fileNameDate,
                                inputAudioFormat, inputData,
                                DEFAULT_CHANNEL_NUM,
                                DEFAULT_SAMPLING_FREQUENCY,
                                DEFAULT_SAMPLING_BIT, isBigEndian);
                    }

                    if (state.equalsIgnoreCase("Success")) {
                        if (m_IsEucOutputOn) {
                            recognitionResultFileNameEUC = writeResultLogFile(
                                    clientIP, userURI, userID, processOrder,
                                    fileNameDate, dataType, true);
                        }
                        if (m_IsUtf8OutputOn) {
                            recognitionResultFileNameUTF8 = writeResultLogFile(
                                    clientIP, userURI, userID, processOrder,
                                    fileNameDate, dataType, false);
                        }
                    }
                }

                if (m_IsUtteranceInfoOn) {
                    if ((!m_CorpusLogStreamMap.containsKey(m_LanguagePrefix))
                            || isCreate) {
                        // create Corpus Log file.
                        createFile();

                        // write first line.
                        writeItemName();
                    }

                    // create CorpusLogInfoString.
                    outputString = createCorpusLogInfoString(srCompleteTime,
                            userURI, userID, clientIP, voiceID, processOrder,
                            locaton, domain, language, sentence, chunk, state,
                            gender, age, nativeVal, inputAudioFormat,
                            utteranceLength, processTime, netWorkdelay, rtf,
                            firstFrameArrivedTime, lastFrameArrivedTime,
                            speechDataFileName, acousticModelName,
                            nictlatticeModelName, nictrescoreModelName,
                            recognitionResultFileNameEUC,
                            recognitionResultFileNameUTF8, destinationURL);

                    // write output string.
                    write(outputString);

                    // writing information of this request is complete and
                    // change paragraph
                    changeParagraph();
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        } finally {
            if (m_IsUtteranceInfoOn || m_IsEucOutputOn || m_IsUtf8OutputOn
                    || m_IsInputSpeechDataOutputOn) {
                synchronized (m_CorpusLogInfoMap) {
                    // remove CorpusLogInfo.
                    m_CorpusLogInfoMap.remove(corpusLogInfoID);
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
     * 
     * @param urlList
     * @param corpusLogInfoID
     */
    public void setDestinationURL(ArrayList<String> urlList, int corpusLogInfoID) {
        if (m_IsUtteranceInfoOn) {
            synchronized (m_CorpusLogInfoMap) {
                // is CorpusLogInfo?.
                if (m_CorpusLogInfoMap.containsKey(Integer
                        .valueOf(corpusLogInfoID))) {
                    // get ASRCorpusLogInfo instance.
                    ASRCorpusLogInfo corpusLogInfo = (ASRCorpusLogInfo) (m_CorpusLogInfoMap
                            .get((Integer.valueOf(corpusLogInfoID))));

                    // set SR CompleteTime
                    corpusLogInfo.setDestinationURL(urlList);
                }
            }
        }
    }

    // ------------------------------------------
    // protected member functions
    // ------------------------------------------
    // write item name in the corpus log file
    protected void writeItemName() throws IOException, FileNotFoundException {
        String itemNameString = "";
        if (m_IsModelNameOutputOn) {
            // create First Line String(for Model Name output On).
            itemNameString = ASR_COMPLETE_TIME + ":" + USER_URI + ":" + USER_ID
                    + ":" + CLIENT_IP + ":" + VOICE_ID + ":" + PROCESS_ORDER
                    + ":" + LOCATION + ":" + DOMAIN + ":" + LANGUAGE + ":"
                    + SENTENCE + ":" + CHUNK + ":" + STATE + ":" + GENDER + ":"
                    + AGE + ":" + NATIVE + ":" + INPUT_AUDIO_FORMAT + ":"
                    + UTTERANCE_LENGTH + ":" + PROCESS_TIME + ":"
                    + NETWORK_DELAY + ":" + RTF + ":"
                    + FIRST_FRAME_ARRIVED_TIME + ":" + LAST_FRAME_ARRIVED_TIME
                    + ":" + SPEECH_DATA_FILE_NAME + ":" + ACOUSTIC_MODEL_NAME
                    + ":" + NICTLATTICE_MODEL + ":" + NICTRESCORE_MODEL;
        } else {
            // create First Line String(for Model Name output Off).
            itemNameString = ASR_COMPLETE_TIME + ":" + USER_URI + ":" + USER_ID
                    + ":" + CLIENT_IP + ":" + VOICE_ID + ":" + PROCESS_ORDER
                    + ":" + LOCATION + ":" + DOMAIN + ":" + LANGUAGE + ":"
                    + SENTENCE + ":" + CHUNK + ":" + STATE + ":" + GENDER + ":"
                    + AGE + ":" + NATIVE + ":" + INPUT_AUDIO_FORMAT + ":"
                    + UTTERANCE_LENGTH + ":" + PROCESS_TIME + ":"
                    + NETWORK_DELAY + ":" + RTF + ":"
                    + FIRST_FRAME_ARRIVED_TIME + ":" + LAST_FRAME_ARRIVED_TIME
                    + ":" + SPEECH_DATA_FILE_NAME;
        }
        // add corpus log information only socketservlet
        if (m_IsURLInfoOn) {
            itemNameString += ":" + RECOGNITION_RESULT_FILE_NAME_EUC + ":"
                    + RECOGNITION_RESULT_FILE_NAME_UTF8 + ":" + DESTINATION_URL;
        }

        // write First Line String.
        write(itemNameString);
        changeParagraph();
    }

    // ------------------------------------------
    // private member functions
    // ------------------------------------------
    /**
     * set the corpus Log Info
     * 
     * @param corpusLogInfoID
     * @param userURI
     * @param userID
     * @param clientIP
     * @param voiceID
     * @param processOrder
     * @param location
     * @param domain
     * @param language
     * @param gender
     * @param age
     * @param nativeVal
     * @param inputAudioFormat
     * @param isBigEndian
     * @param recievedTimeOnServlet
     */
    private void setInputMCMLInfoSub(int corpusLogInfoID, String userURI,
            String userID, String clientIP, String voiceID, int processOrder,
            String location, String domain, String language, String gender,
            String age, String nativeVal, String inputAudioFormat,
            boolean isBigEndian, String recievedTimeOnServlet) {
        synchronized (m_CorpusLogInfoMap) {
            // is CorpusLogInfo?.
            ASRCorpusLogInfo corpusLogInfo = null;
            if (m_CorpusLogInfoMap
                    .containsKey(Integer.valueOf(corpusLogInfoID))) {
                // get ASRCorpusLogInfo instance.
                corpusLogInfo = (ASRCorpusLogInfo) (m_CorpusLogInfoMap
                        .get((Integer.valueOf(corpusLogInfoID))));
            } else {
                // create Instance.
                corpusLogInfo = new ASRCorpusLogInfo();
                m_CorpusLogInfoMap.put(Integer.valueOf(corpusLogInfoID),
                        corpusLogInfo);
            }

            // set information from InputMCML
            corpusLogInfo.setUserURI(userURI);
            corpusLogInfo.setUserID(userID);
            corpusLogInfo.setClientIP(clientIP);
            corpusLogInfo.setVoiceID(voiceID);
            corpusLogInfo.setProcessOrder(processOrder);
            corpusLogInfo.setLocation(location);
            corpusLogInfo.setDomain(domain);
            corpusLogInfo.setLanguage(language);
            corpusLogInfo.setGender(gender);
            corpusLogInfo.setAge(age);
            corpusLogInfo.setNative(nativeVal);
            corpusLogInfo.setInputAudioFormat(inputAudioFormat);
            corpusLogInfo.setIsBigEndian(isBigEndian);

            // Warnning ASRServer only!
            // set recievedTimeOnServlet.

            if (recievedTimeOnServlet == null
                    || recievedTimeOnServlet.equals("")) {
                recievedTimeOnServlet = "0";
            }
            corpusLogInfo.setRecievedTimeOnServlet(Long
                    .parseLong(recievedTimeOnServlet));
        }
    }

    // pick out sentence from dataType
    private String createSentence(DataType dataType) {
        String sentence = "";

        try {

            if (!dataType.Text.exists()) {
                dataType.Text.append();
            }
            if (!dataType.Text.first().SentenceSequence.exists()) {
                dataType.Text.first().SentenceSequence.append();
            }
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

    // do calculation
    private void calculation(ASRCorpusLogInfo corpusLogInfo) {
        try {
            // get values neccessary for calculation.
            Date lastFrameArrivedTime = new Date(
                    corpusLogInfo.getLastFrameArrivedTime());
            Date srCompleteTime = new Date(corpusLogInfo.getSRCompleteTime());
            ArrayList<byte[]> inputData = corpusLogInfo.getInputData();
            String inputAudioFormat = corpusLogInfo.getInputAudioFormat();
            boolean isBigEndian = corpusLogInfo.getIsBigEndian();
            long recievedTimeOnServlet = corpusLogInfo
                    .getRecievedTimeOnServlet();

            // calculate inputDataBytes
            int inputDataBytes = 0;
            for (int i = 0; i < inputData.size(); i++) {
                inputDataBytes += inputData.get(i).length;
            }

            // ByteBuffer memory allocate
            ByteBuffer inputDataBuff = ByteBuffer.allocate(inputDataBytes);

            // set data to inputDataBuff
            for (int i = 0; i < inputData.size(); i++) {
                inputDataBuff.put(inputData.get(i));
            }

            // calculate and set Process Time.
            long processTime = calculateProcessTime(lastFrameArrivedTime,
                    srCompleteTime);
            corpusLogInfo.setProcessTime(processTime);

            // calculate and set Utterance Length.
            long utteranceLength;
            utteranceLength = calculateUtteranceLength(inputDataBuff,
                    inputAudioFormat, isBigEndian);
            corpusLogInfo.setUtteranceLength(utteranceLength);

            // calculate and set RTF.
            double rtf = calculateRTF(processTime, utteranceLength);
            corpusLogInfo.setRTF(rtf);

            // in case parting data send
            if (recievedTimeOnServlet > 0 && !inputData.isEmpty()) {
                // caluculate and set Network Delay.
                Date firstFrameArrivedTime = new Date(recievedTimeOnServlet);
                long networkDelay = calculateNetworkDelay(
                        firstFrameArrivedTime, srCompleteTime, utteranceLength,
                        inputData.get(0), inputAudioFormat, processTime,
                        isBigEndian);
                corpusLogInfo.setNetworkDelay(networkDelay);
            }
        } catch (MCMLException e) {
            LOG.warn("calculation() failed.", e);
        }
    }

    // calculate UtteranceLength from binarydata
    private long calculateUtteranceLength(ByteBuffer inputData,
            String audioformat, boolean isBigEndian) throws MCMLException {
        long utteranceLength = 0;

        // in case data format is DSR
        if (audioformat.equalsIgnoreCase(MCMLStatics.AUDIO_DSR)) {
            utteranceLength = calculateDSRUtteranceLength(inputData);
        } else {
            ByteBuffer convertedData = null;
            // in case data format is ADPCM , convert to PCM
            if (audioformat.equalsIgnoreCase(MCMLStatics.AUDIO_ADPCM)) {
                // convert Wave Data(ADPCM=>PCM).
                convertedData = convertToPCM(inputData, false, isBigEndian);
            } else {
                // PCM Big Endian=> PCM LittleEndian or not convert.
                convertedData = convertToPCM(inputData, true, isBigEndian);
            }
            int dataSize = convertedData.array().length;
            utteranceLength = (dataSize * 1000)
                    / (DEFAULT_SAMPLING_FREQUENCY * (DEFAULT_SAMPLING_BIT / 8) * DEFAULT_CHANNEL_NUM);
        }
        return utteranceLength;
    }

    // calculate DSR data UtteranceLength
    private long calculateDSRUtteranceLength(ByteBuffer dsrData) {
        long utterancelength = 0;

        int dataSize = dsrData.array().length;
        if (dataSize > 0) {
            utterancelength = dataSize * DSR_SPEECH_LENGTH_PER_ONE_FRAME
                    / DSR_PACKET_LENGTH;
        } else {
            LOG.error("calculateDSRUtteranceLength() failed.");
        }

        return utterancelength;
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

    // calculate RTF
    private double calculateRTF(long processTime, long utteranceLength) {
        double rtf = (double) processTime / utteranceLength;

        if (rtf <= 0.0) {
            rtf = 0;
            LOG.error("calculateRTF() failed.");
        }

        return rtf;
    }

    // calculate NetWorkDelay
    private long calculateNetworkDelay(Date firstFrameArrivedTime,
            Date completeTime, long utteranceLength, byte[] firstFrameData,
            String audioFormat, long processTime, boolean isBigEndian)
            throws MCMLException {
        // calculate firstFrameDataBytes
        int firstFrameDataBytes = firstFrameData.length;

        // ByteBuffer memory allocate
        ByteBuffer firstFrameDataBuff = ByteBuffer
                .allocate(firstFrameDataBytes);

        // set data to firstFrameDataBuff
        firstFrameDataBuff.put(firstFrameData);

        long wholeProcessTime = completeTime.getTime()
                - firstFrameArrivedTime.getTime();

        long firstFrameUtteranceLength = calculateUtteranceLength(
                firstFrameDataBuff, audioFormat, isBigEndian);

        long networkDelay = wholeProcessTime
                - (utteranceLength - firstFrameUtteranceLength) - processTime;
        if (networkDelay <= 0) {
            networkDelay = 0;
            LOG.error("calculateNetworkDelay() failed.");
        }

        return networkDelay;
    }

    // convert adpcm data to pcm
    private ByteBuffer convertToPCM(ByteBuffer inputData, boolean isPCM,
            boolean isBigEndian) throws MCMLException {
        ByteBuffer pcmData = null;

        byte[] bytedata = inputData.array();

        // create AudioConverter.
        AudioConverter audioConv = null;
        if (isPCM && isBigEndian) {
            // AudioConverter(boolean srcIsRawData, boolean srcIsBigEndian,
            // boolean trgIsRawData, boolean trgIsBigEndian)
            // RAW and BigEndian => LittleEndian Converter
            audioConv = new AudioConverter(isPCM, isBigEndian, isPCM, false,
                    MCMLException.ASR);
        } else if (!isPCM) {
            // AudioConverter(boolean srcIsRawData, boolean srcIsBigEndian,
            // boolean trgIsRawData, boolean trgIsBigEndian)
            // ADPCM => RAW LittleEndian Converter
            audioConv = new AudioConverter(isPCM, isBigEndian, true, false,
                    MCMLException.ASR);
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

    // write result log file
    private String writeResultLogFile(String clientIP, String userURI,
            String userID, int utteranceID, Date date, DataType dataType,
            boolean isEUC) { // isEUC = ture(EUC)/false(UTF8)

        String resultLogFileName = "";
        try {
            // create instance to ResultWriter class.(EUC or UTF-8)
            ASRResultWriter resultWriter = new ASRResultWriter(isEUC);

            // create Result String from Output.
            String result;
            result = resultWriter.pickOutResult(dataType);

            // create Output Directory.
            if (resultWriter.createDirectory(m_CorpusLogOutputDirectoryPath)) {
                // write Result Log.
                resultWriter.writeFile(m_Prefix, m_LanguagePrefix, clientIP,
                        userURI, userID, utteranceID, date, result);
                resultLogFileName = resultWriter.getResultLogFileName();
            }
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
        }
        return resultLogFileName;
    }

    // create wave log file
    private String writeWaveLogFile(String clientIP, String userURI,
            String userID, int processOrder, Date date,
            String inputAudioFormat, ArrayList<byte[]> inputData,
            short channels, int samplingFrequency, short samplingBitRate,
            boolean isBigEndian) {
        String waveLogFileName = "";
        try {
            // convert ADPCM to PCM or PCM BigEndian to PCM LittleEndian(DSR is
            // only merge).
            byte[] writeData;
            writeData = convertToWriteData(inputData, inputAudioFormat,
                    isBigEndian);

            if (m_IsInputSpeechDataOutputOn) {
                WaveLogWriter wavelog = null;
                // create instance Wave Log writer.
                if (inputAudioFormat.equalsIgnoreCase(MCMLStatics.AUDIO_DSR)) {
                    wavelog = new WaveLogWriter(false);
                } else {
                    wavelog = new WaveLogWriter(true);
                }

                // create Output Directory.
                if (wavelog.createDirectory(m_CorpusLogOutputDirectoryPath)) {
                    // wtrite Wave Log.
                    wavelog.writeFile(m_Prefix, m_LanguagePrefix, clientIP,
                            userURI, userID, processOrder, date,
                            inputAudioFormat, writeData, channels,
                            samplingFrequency, samplingBitRate);
                }

                // get Wave Log file name.
                waveLogFileName = wavelog.getWaveLogFileName();
            }
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
        }

        return waveLogFileName;
    }

    private byte[] convertToWriteData(ArrayList<byte[]> inputData,
            String audioformat, boolean isBigEndian) throws MCMLException {
        int dataSize = 0;
        for (int i = 0; i < inputData.size(); i++) {
            dataSize += inputData.get(i).length;
        }

        ByteBuffer writeData = null;
        // in case data format is DSR
        if (audioformat.equalsIgnoreCase(MCMLStatics.AUDIO_DSR)) {
            // merge DSR Data.
            writeData = ByteBuffer.allocate(dataSize);
            for (int j = 0; j < inputData.size(); j++) {
                writeData.put(inputData.get(j));
            }
        } else {
            // merge ADPCM or Raw(PCM) Data.
            ByteBuffer convertData = null;
            convertData = ByteBuffer.allocate(dataSize);
            for (int k = 0; k < inputData.size(); k++) {
                convertData.put(inputData.get(k));
            }

            // in case data format is ADPCM , convert to PCM
            if (audioformat.equalsIgnoreCase(MCMLStatics.AUDIO_ADPCM)) {
                // convert Wave Data(ADPCM=>PCM).
                writeData = convertToPCM(convertData, false, isBigEndian);
            } else {
                // PCM Big Endian=> PCM LittleEndian or not convert.
                writeData = convertToPCM(convertData, true, isBigEndian);
            }
        }

        return writeData.array();
    }

    private String createCorpusLogInfoString(Date srCompleteTime,
            String userURI, String userID, String clientIP, String voiceID,
            int processOrder, String locaton, String domain, String language,
            String sentence, String chunk, String state, String gender,
            String age, String nativeVal, String inputAudioFormat,
            long utteranceLength, long processTime, long netWorkdelay,
            double rtf, Date firstFrameArrivedTime, Date lastFrameArrivedTime,
            String speechDataFileName, String acousticModelName,
            String nictlatticeModelName, String nictrescoreModelName,
            String recogintionResultFileNameEUC,
            String recogintionResultFileNameUTF8, String destinationURL) {
        String outputString = "";

        // create output string.
        outputString = OUTPUT_DATE_FORMAT.format(srCompleteTime) + ":";
        outputString += userURI + ":";
        outputString += userID + ":";
        outputString += clientIP + ":";
        outputString += voiceID + ":";
        outputString += Integer.toString(processOrder) + ":";
        outputString += locaton + ":";
        outputString += domain + ":";
        outputString += language + ":";
        outputString += sentence + ":";
        outputString += chunk + ":";
        outputString += state + ":";
        outputString += gender + ":";
        outputString += age + ":";
        outputString += nativeVal + ":";
        outputString += inputAudioFormat + ":";

        if (utteranceLength > 0) {
            outputString += Long.toString(utteranceLength);
        }
        outputString += ":";

        if (processTime > 0) {
            outputString += Long.toString(processTime);
        }
        outputString += ":";

        if (netWorkdelay > 0) {
            outputString += Long.toString(netWorkdelay);
        }
        outputString += ":";

        if (rtf > 0.0) {
            outputString += Double.toString(rtf);
        }
        outputString += ":";

        outputString += OUTPUT_DATE_FORMAT.format(firstFrameArrivedTime) + ":";
        outputString += OUTPUT_DATE_FORMAT.format(lastFrameArrivedTime) + ":";
        outputString += speechDataFileName;

        if (m_IsModelNameOutputOn) {
            outputString += ":";
            outputString += acousticModelName;
            outputString += ":";
            outputString += nictlatticeModelName;
            outputString += ":";
            outputString += nictrescoreModelName;
        }

        // add corpus log information only socketservlet
        if (m_IsURLInfoOn) {
            outputString += ":";
            outputString += recogintionResultFileNameEUC;
            outputString += ":";
            outputString += recogintionResultFileNameUTF8;
            outputString += ":";
            outputString += destinationURL;
        }
        return outputString;
    }

}
