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

import jp.go.nict.mcml.serverap.common.AudioConverter;
import jp.go.nict.mcml.serverap.common.EngineInfo;
import jp.go.nict.mcml.serverap.common.MCMLData;
import jp.go.nict.mcml.serverap.common.ServerApCorpusLogger;
import jp.go.nict.mcml.serverap.common.ServerApLogger;
import jp.go.nict.mcml.serverap.common.SpeexAudioConverter;
import jp.go.nict.mcml.serverap.common.WaveLogWriter;
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
import jp.go.nict.mcml.xml.types.SentenceSequenceType;
import jp.go.nict.mcml.xml.types.SentenceType;
import jp.go.nict.mcml.xml.types.SurfaceType;
import jp.go.nict.mcml.xml.types.SurfaceType2;

/**
 * ASRCorpusLogger class.
 *
 */
public class ASRCorpusLogger extends ServerApCorpusLogger {

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

    // ------------------------------------------
    // private member valuables
    // ------------------------------------------
    private boolean m_IsEucOutputOn;
    private boolean m_IsUtf8OutputOn;
    private boolean m_IsInputSpeechDataOutputOn;
    private boolean m_IsModelNameOutputOn;

    // ------------------------------------------
    // public member functions
    // ------------------------------------------
    /**
     * constructor
     *
     * @param isUtteranceInfoOutputOn
     * @param basedirectryName
     * @param language
     * @param prefix
     * @param isEucOutputOn
     * @param isUtf8OutputOn
     * @param isInputSpeechDataOutputOn
     * @param isModelNameOutputOn
     * @param removedSymbols
     */
    public ASRCorpusLogger(boolean isUtteranceInfoOutputOn,
            String basedirectryName, String language, String prefix,
            boolean isEucOutputOn, boolean isUtf8OutputOn,
            boolean isInputSpeechDataOutputOn, boolean isModelNameOutputOn,
            String[] removedSymbols) {
        super(isUtteranceInfoOutputOn, basedirectryName, language, prefix,
                removedSymbols);

        // set Output Flag.
        m_IsEucOutputOn = isEucOutputOn;
        m_IsUtf8OutputOn = isUtf8OutputOn;
        m_IsInputSpeechDataOutputOn = isInputSpeechDataOutputOn;
        m_IsModelNameOutputOn = isModelNameOutputOn;
    }

    /** set First Frame ArrivedTime. */
    @Override
    public void setFirstFrameArrivedTime(int corpusLogInfoID) {
        // get First Frame ArrivedTime.
        long firstFrameArrivedTime = System.currentTimeMillis();

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

    /** set Last Frame ArrivedTime. */
    @Override
    public void setLastFrameArrivedTime(int corpusLogInfoID) {
        // get Last Frame ArrivedTime.
        long lastFrameArrivedTime = System.currentTimeMillis();

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

    /** set information from inputMCML */
    @Override
    public void setInputMCMLInfo(MCMLType inputMCML, int corpusLogInfoID) {
        try {
            // UtteranceInfo on or ResultLog on or WaveLog on.
            if (m_IsUtteranceInfoOn || m_IsUtf8OutputOn || m_IsEucOutputOn
                    || m_IsInputSpeechDataOutputOn) {
                // get User's Information.
                // get URI
                String userURI = XMLTypeTools.getURI(inputMCML);

                // get UserID
                String userID = XMLTypeTools.getUserID(inputMCML);

                String clientIP = "";
                String recievedTimeOnServlet = "";
                if (!userURI.isEmpty()) {
                    // get ClientIP
                    clientIP = XMLTypeTools.getIPAddressFromUserURI(userURI);
                }

                String inputAudioFormat = "";
                String location = "";
                String domain = "";
                boolean isBigEndian = true;
                AudioType audioType = XMLTypeTools.getAudioType(inputMCML);
                if (audioType != null) {
                    // get Task and Domain
                    if (audioType.hasModelType()) {
                        if (audioType.getModelType().hasTask()) {
                            location = audioType.getModelType().getTask()
                                    .getValue();
                        }
                        if (audioType.getModelType().hasDomain()) {
                            domain = audioType.getModelType().getDomain()
                                    .getValue();
                        }
                    }

                    if (audioType.hasSignal()) {
                        // get InputAudioFormat
                        if (audioType.getSignal().hasAudioFormat()) {
                            inputAudioFormat = audioType.getSignal()
                                    .getAudioFormat().getValue();
                        }

                        // get isBigEndian
                        if (audioType.getSignal().hasEndian()) {
                            String endian = audioType.getSignal().getEndian()
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

                // Add the contents of the log
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

                GlobalPositionType globalPositionType = XMLTypeTools
                        .getGlobalPosition(inputMCML);

                String longitude = "";
                String latitude = "";
                if (globalPositionType != null) {
                    longitude = XMLTypeTools.getLongitude(globalPositionType);
                    latitude = XMLTypeTools.getLatitude(globalPositionType);
                }

                String language = XMLTypeTools
                        .getInputAttachedBinaryID(inputMCML);

                String nativeVal;
                // get nativeVal
                if (XMLTypeTools.hasNative(inputMCML, language)) {
                    nativeVal = MCMLStatics.STRING_YES;
                } else {
                    nativeVal = MCMLStatics.STRING_NO;
                }

                // get processOrder
                String processOrder = XMLTypeTools.getProcessOrder(inputMCML);

                // set information from inputMCML to CorpusLogInfo.
                setInputMCMLInfoSub(corpusLogInfoID, userURI, userID, clientIP,
                        voiceID, processOrder, location, domain, language,
                        longitude, latitude, gender, age, nativeLanguage,
                        firstForeignLanguage, secondForeignLanguage, nativeVal,
                        inputAudioFormat, isBigEndian, recievedTimeOnServlet);
            }

            // remove UserInfomation String(ClientIP and Recieved Time on
            // Servlet).
            removeUserInfoStr(inputMCML);
        } catch (Exception e) {
            ServerApLogger.getInstance().writeError(e.getMessage());
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
     * set information from outputMCML
     *
     * @param outputMCML
     * @param corpusLogInfoID
     */
    public void setOutputMCMLInfo(MCMLType outputMCML, int corpusLogInfoID) {
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
                                .getDataTypeFromMCML(outputMCML);
                        corpusLogInfo.setDataType(outputData);

                        if (XMLTypeTools.hasResponse(outputMCML)
                                && outputData != null) {
                            // get sentence and set sentence
                            String sentence = createSentence(outputData);
                            corpusLogInfo.setSentence(sentence);

                            // get chunk and set chunk.
                            String chunk = createChunk(outputData);
                            corpusLogInfo.setChunk(chunk);

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
                        ServerApLogger.getInstance().writeError(
                                "setOutputMCMLInfo" + e.getMessage());

                    }
                }
            }
        }
    }

    /**
     * Sets completion time.
     *
     *
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

    /** Sets EngineInfo. */
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

                    // set Acoustic Model Name.
                    corpusLogInfo
                            .setAcousticModelName(((ASREngineInfo) engineInfo)
                                    .getAcousticModelName());
                }
            }
        }
    }

    /** write information in the Corpuslogfile */
    @Override
    public void writeCorpusLogInfo(int corpusLogInfoID) {
        try {
            if (m_IsUtteranceInfoOn || m_IsEucOutputOn || m_IsUtf8OutputOn
                    || m_IsInputSpeechDataOutputOn) {
                Date srCompleteTime = null;
                String clientIP = "";
                String userURI = "";
                String userID = "";
                String voiceID = "";
                String processOrder = "";
                String locaton = "";
                String domain = "";
                String language = "";
                String sentence = "";
                String chunk = "";
                String state = "";
                String longitude = "";
                String latitude = "";
                String gender = "";
                String age = "";
                String nativeLanguage = "";
                String firstForeignLanguage = "";
                String secondForeignLanguage = "";
                String nativeVal = "";
                String inputAudioFormat = "";
                String acousticModelName = "";
                long utteranceLength = 0;
                long processTime = 0;
                long netWorkdelay = 0;
                double rtf = 0.0;
                Date firstFrameArrivedTime = null;
                Date lastFrameArrivedTime = null;
                DataType dataType = null;
                ArrayList<byte[]> inputData = null;
                boolean isBigEndian = false;

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

                            // get Corpus Log Infomation for Utterance
                            // Infomation Log.
                            srCompleteTime = new Date(
                                    corpusLogInfo.getSRCompleteTime());
                            voiceID = corpusLogInfo.getVoiceID();
                            locaton = corpusLogInfo.getLocation();
                            domain = corpusLogInfo.getDomain();
                            language = corpusLogInfo.getLanguage();
                            sentence = corpusLogInfo.getSentence();
                            chunk = corpusLogInfo.getChunk();
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
                        }

                        // get Corpus Log Infomation.
                        userURI = corpusLogInfo.getUserURI();
                        userID = corpusLogInfo.getUserID();
                        clientIP = corpusLogInfo.getClientIP();
                        processOrder = corpusLogInfo.getProcessOrder();
                        inputAudioFormat = corpusLogInfo.getInputAudioFormat();

                        // get Corpus Log Infomation for Result Log.
                        if (m_IsEucOutputOn || m_IsUtf8OutputOn) {
                            dataType = corpusLogInfo.getDataType();
                        }

                        // get Corpus Log Infomation for Wave Log.
                        if (m_IsInputSpeechDataOutputOn) {
                            inputData = corpusLogInfo.getInputData();
                            isBigEndian = corpusLogInfo.getIsBigEndian();
                        }
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

                String speechDataFileName = "";
                if (m_IsInputSpeechDataOutputOn || m_IsEucOutputOn
                        || m_IsUtf8OutputOn) {
                    // get date for file name
                    Date fileNameDate = new Date();

                    if (m_IsInputSpeechDataOutputOn) {
                        // write Wave Log File and get SpeechDataFileName.
                        speechDataFileName = writeWaveLogFile(clientIP, userID,
                                processOrder, fileNameDate, inputAudioFormat,
                                inputData, DEFAULT_CHANNEL_NUM,
                                DEFAULT_SAMPLING_FREQUENCY,
                                DEFAULT_SAMPLING_BIT, isBigEndian);
                    }

                    if (state.equalsIgnoreCase("Success")) {
                        if (m_IsEucOutputOn || m_IsUtf8OutputOn) {
                            // write ResultLog File.
                            writeResultLogFile(clientIP, userID, processOrder,
                                    fileNameDate, dataType);
                        }
                    }
                }

                if (m_IsUtteranceInfoOn) {
                    if (m_CorpusLogStream == null || isCreate) {
                        // create Corpus Log file.
                        createFile();

                        // write first line.
                        writeItemName();
                    }

                    // create CorpusLogInfoString.
                    String outputString = createCorpusLogInfoString(
                            srCompleteTime, userURI, userID, clientIP, voiceID,
                            processOrder, locaton, domain, language, sentence,
                            chunk, state, longitude, latitude, gender, age,
                            nativeLanguage, firstForeignLanguage,
                            secondForeignLanguage, nativeVal, inputAudioFormat,
                            utteranceLength, processTime, netWorkdelay, rtf,
                            firstFrameArrivedTime, lastFrameArrivedTime,
                            speechDataFileName, acousticModelName);

                    // write output string.
                    write(outputString);

                    // writing information of this request is complete and
                    // change paragraph
                    changeParagraph();
                }
            }
        } catch (Exception e) {
            ServerApLogger.getInstance().writeError(e.getMessage());
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
     */
    @Override
    public void setOutputMCMLInfo(MCMLData outputMCMLData, int corpusLogInfoID) {
        MCMLType outputMCML = outputMCMLData.getMCMLType();
        setOutputMCMLInfo(outputMCML, corpusLogInfoID);
    }

    // ------------------------------------------
    // protected member functions
    // ------------------------------------------
    // write item name in the corpus log file
    @Override
    protected void writeItemName() throws IOException, FileNotFoundException {
        // Output order is set here
        String itemNameString = "";
        if (m_IsModelNameOutputOn) {
            // create First Line String(for Model Name output On).
            itemNameString = ASR_COMPLETE_TIME + ":" + USER_URI + ":" + USER_ID
                    + ":" + CLIENT_IP + ":" + PROCESS_ORDER + ":" + LOCATION
                    + ":" + DOMAIN + ":" + LANGUAGE + ":" + SENTENCE + ":"
                    + CHUNK + ":" + STATE + ":" + LONGITUDE + ":" + LATITUDE
                    + ":" + VOICE_ID + ":" + GENDER + ":" + AGE + ":"
                    + NATIVE_LANGUAGE + ":" + FIRST_FOREIGN_LANGUAGE + ":"
                    + SECOND_FOREIGN_LANGUAGE + ":" + NATIVE + ":"
                    + INPUT_AUDIO_FORMAT + ":" + UTTERANCE_LENGTH + ":"
                    + PROCESS_TIME + ":" + NETWORK_DELAY + ":" + RTF + ":"
                    + FIRST_FRAME_ARRIVED_TIME + ":" + LAST_FRAME_ARRIVED_TIME
                    + ":" + SPEECH_DATA_FILE_NAME + ":" + ACOUSTIC_MODEL_NAME;
        } else {
            // create First Line String(for Model Name output Off).
            itemNameString = ASR_COMPLETE_TIME + ":" + USER_URI + ":" + USER_ID
                    + ":" + CLIENT_IP + ":" + PROCESS_ORDER + ":" + LOCATION
                    + ":" + DOMAIN + ":" + LANGUAGE + ":" + SENTENCE + ":"
                    + CHUNK + ":" + STATE + ":" + LONGITUDE + ":" + LATITUDE
                    + ":" + VOICE_ID + ":" + GENDER + ":" + AGE + ":"
                    + NATIVE_LANGUAGE + ":" + FIRST_FOREIGN_LANGUAGE + ":"
                    + SECOND_FOREIGN_LANGUAGE + ":" + NATIVE + ":"
                    + INPUT_AUDIO_FORMAT + ":" + UTTERANCE_LENGTH + ":"
                    + PROCESS_TIME + ":" + NETWORK_DELAY + ":" + RTF + ":"
                    + FIRST_FRAME_ARRIVED_TIME + ":" + LAST_FRAME_ARRIVED_TIME
                    + ":" + SPEECH_DATA_FILE_NAME;
        }

        // write First Line String.
        write(itemNameString);
        changeParagraph();
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
     * @param location
     * @param domain
     * @param language
     * @param longitude
     * @param latitude
     * @param gender
     * @param age
     * @param nativeLanguage
     * @param firstForeignLanguage
     * @param secondForeignLanguage
     * @param nativeVal
     * @param inputAudioFormat
     * @param isBigEndian
     * @param recievedTimeOnServlet
     */
    private void setInputMCMLInfoSub(int corpusLogInfoID, String userURI,
            String userID, String clientIP, String voiceID,
            String processOrder, String location, String domain,
            String language, String longitude, String latitude, String gender,
            String age, String nativeLanguage, String firstForeignLanguage,
            String secondForeignLanguage, String nativeVal,
            String inputAudioFormat, boolean isBigEndian,
            String recievedTimeOnServlet) {
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
            corpusLogInfo.setLongitude(longitude);
            corpusLogInfo.setLatitude(latitude);
            corpusLogInfo.setGender(gender);
            corpusLogInfo.setAge(age);
            corpusLogInfo.setNativeLanguage(nativeLanguage);
            corpusLogInfo.setFirstForeignLanguage(firstForeignLanguage);
            corpusLogInfo.setSecondForeignLanguage(secondForeignLanguage);
            corpusLogInfo.setNative(nativeVal);
            corpusLogInfo.setInputAudioFormat(inputAudioFormat);
            corpusLogInfo.setIsBigEndian(isBigEndian);

            // Warnning ASRServer only!
            // set recievedTimeOnServlet.
            corpusLogInfo.setRecievedTimeOnServlet(Long
                    .parseLong(recievedTimeOnServlet));
        }
    }

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
            ServerApLogger.getInstance().writeError(
                    "createSentence: " + e.getMessage());
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
            ServerApLogger.getInstance().writeWarning("calculation() failed.");
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
            } else if (audioformat.equalsIgnoreCase(MCMLStatics.AUDIO_SPEEX)) {
                convertedData = convertSpeexToPCM(inputData, isBigEndian);
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
            ServerApLogger.getInstance().writeError(
                    "calculateDSRUtteranceLength() failed.");
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
            ServerApLogger.getInstance().writeError(
                    "calculateNetworkDelay() failed.");
        }

        return networkDelay;
    }

    // convert speex data to pcm
    private ByteBuffer convertSpeexToPCM(ByteBuffer inputData,
            boolean isBigEndian) throws MCMLException {
        SpeexAudioConverter speexConv = new SpeexAudioConverter(false,
                isBigEndian, true, false, MCMLException.ASR);
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

    // create and write result logfile
    private void writeResultLogFile(String clientIP, String userID,
            String utteranceID, Date date, DataType dataType) {
        try {
            // write Result Log by EUC.
            if (m_IsEucOutputOn) {
                // create instance to ResultWriter class(for EUC).
                ASRResultWriter eucResult = new ASRResultWriter(true);

                // create Result String from Output.
                String result;
                result = eucResult.pickOutResult(dataType);

                // create Output Directry.
                if (eucResult.createDirectory(m_CorpusLogOutputDirectoryPath)) {
                    // wtrite Result Log.
                    eucResult.writeFile(m_Prefix, m_PropertiesLanguage,
                            clientIP, userID, utteranceID, date, result);
                }
            }

            // write Result Log by UTF-8.
            if (m_IsUtf8OutputOn) {
                // create instance to ResultWriter class(for UTF-8).
                ASRResultWriter utf8Result = new ASRResultWriter(false);

                // create Result String from Output.
                String result = utf8Result.pickOutResult(dataType);

                // create Output Directry.
                if (utf8Result.createDirectory(m_CorpusLogOutputDirectoryPath)) {
                    // wtrite Result Log.
                    utf8Result.writeFile(m_Prefix, m_PropertiesLanguage,
                            clientIP, userID, utteranceID, date, result);
                }
            }
        } catch (Exception e) {
            ServerApLogger.getInstance().writeWarning(
                    "writeWaveLogFile() failed.");
            ServerApLogger.getInstance().writeWarning(e.getMessage());
            ServerApLogger.getInstance().writeException(e);
        }

    }

    // create wave log file
    private String writeWaveLogFile(String clientIP, String userID,
            String utteranceID, Date date, String inputAudioFormat,
            ArrayList<byte[]> inputData, short channels, int samplingFrequency,
            short samplingBitRate, boolean isBigEndian) {
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

                // create Output Directry.
                if (wavelog.createDirectory(m_CorpusLogOutputDirectoryPath)) {
                    // wtrite Wave Log.
                    wavelog.writeFile(m_Prefix, m_PropertiesLanguage, clientIP,
                            userID, utteranceID, date, inputAudioFormat,
                            writeData, channels, samplingFrequency,
                            samplingBitRate);
                }

                // get Wave Log file name.
                waveLogFileName = wavelog.getWaveLogFileName();
            }
        } catch (Exception e) {
            ServerApLogger.getInstance().writeWarning(
                    "writeWaveLogFile() failed.");
            ServerApLogger.getInstance().writeWarning(e.getMessage());
            ServerApLogger.getInstance().writeException(e);
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
            } else if (audioformat.equalsIgnoreCase(MCMLStatics.AUDIO_SPEEX)) {
                writeData = convertSpeexToPCM(convertData, isBigEndian);
            } else {
                // PCM Big Endian=> PCM LittleEndian or not convert.
                writeData = convertToPCM(convertData, true, isBigEndian);
            }
        }

        return writeData.array();
    }

    /**
     * create corpus log information
     *
     * @param srCompleteTime
     * @param userURI
     * @param userID
     * @param clientIP
     * @param voiceID
     * @param processOrder
     * @param locaton
     * @param domain
     * @param language
     * @param sentence
     * @param chunk
     * @param state
     * @param longitude
     * @param latitude
     * @param gender
     * @param age
     * @param nativeLanguage
     * @param firstForeignLanguage
     * @param secondForeignLanguage
     * @param nativeVal
     * @param inputAudioFormat
     * @param utteranceLength
     * @param processTime
     * @param netWorkdelay
     * @param rtf
     * @param firstFrameArrivedTime
     * @param lastFrameArrivedTime
     * @param speechDataFileName
     * @param acousticModelName
     * @return Output character string
     */
    private String createCorpusLogInfoString(Date srCompleteTime,
            String userURI, String userID, String clientIP, String voiceID,
            String processOrder, String locaton, String domain,
            String language, String sentence, String chunk, String state,
            String longitude, String latitude, String gender, String age,
            String nativeLanguage, String firstForeignLanguage,
            String secondForeignLanguage, String nativeVal,
            String inputAudioFormat, long utteranceLength, long processTime,
            long netWorkdelay, double rtf, Date firstFrameArrivedTime,
            Date lastFrameArrivedTime, String speechDataFileName,
            String acousticModelName) {
        String outputString = "";

        // Output order is set here

        // create output string.
        outputString = OUTPUT_DATE_FORMAT.format(srCompleteTime) + ":";
        outputString += userURI + ":";
        outputString += userID + ":";
        outputString += clientIP + ":";
        outputString += processOrder + ":";
        outputString += locaton + ":";
        outputString += domain + ":";
        outputString += language + ":";
        outputString += sentence + ":";
        outputString += chunk + ":";
        outputString += state + ":";
        outputString += longitude + ":";
        outputString += latitude + ":";
        outputString += voiceID + ":";
        outputString += gender + ":";
        outputString += age + ":";
        outputString += nativeLanguage + ":";
        outputString += firstForeignLanguage + ":";
        outputString += secondForeignLanguage + ":";

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
        }

        return outputString;
    }

}
