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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.go.nict.mcml.serverap.common.AudioConverter;
import jp.go.nict.mcml.serverap.common.ConnectState;
import jp.go.nict.mcml.serverap.common.EngineCtrl;
import jp.go.nict.mcml.serverap.common.MCMLData;
import jp.go.nict.mcml.serverap.common.ServerApLogger;
import jp.go.nict.mcml.serverap.common.SpeexAudioConverter;
import jp.go.nict.mcml.servlet.MCMLException;
import jp.go.nict.mcml.servlet.MCMLStatics;
import jp.go.nict.mcml.servlet.XMLTypeTools;
import jp.go.nict.mcml.xml.types.AudioType;
import jp.go.nict.mcml.xml.types.DataType;
import jp.go.nict.mcml.xml.types.ErrorType;
import jp.go.nict.mcml.xml.types.InputUserProfileType;
import jp.go.nict.mcml.xml.types.LanguageType;
import jp.go.nict.mcml.xml.types.MCMLType;
import jp.go.nict.mcml.xml.types.ModelTypeType;
import jp.go.nict.mcml.xml.types.OutputType;
import jp.go.nict.mcml.xml.types.PersonalityType;
import jp.go.nict.mcml.xml.types.RequestType;
import jp.go.nict.mcml.xml.types.ResponseType;
import jp.go.nict.mcml.xml.types.SentenceSequenceType;
import jp.go.nict.mcml.xml.types.ServerType;
import jp.go.nict.mcml.xml.types.TargetOutputType;
import jp.go.nict.mcml.xml.types.TextType;

/**
 * ASREngineCtrl class.
 *
 *
 */
class ASREngineCtrl extends EngineCtrl {
    // ------------------------------------------
    // private member constant
    // ------------------------------------------
    private static final String ENGINE_CLOSED_ERRORCODE = "E-20010110";
    private static final Pattern F0_PATTERN = Pattern.compile("\\[.*\\]");

    // ------------------------------------------
    // private member variable(instance field)
    // ------------------------------------------
    private ASRComCtrl m_ASRComCtrl;
    private int m_FrameSyncBytes;
    private ModelChanger m_ModelChanger;
    private ASRParam m_ASRParam;
    private String m_EngineName;
    private SrTextFormat m_SrTextFormat;
    private SRTimer m_SRTimer;
    private VoiceFontSelectorController m_VoiceFontSelector;
    private boolean m_IsSendOneWav;
    private ByteArrayOutputStream m_SpeechData;
    private ConvertNumKo m_ConvertNumKo;

    // ------------------------------------------
    // public member function
    // ------------------------------------------
    public ASREngineCtrl() {
        // set ServerType
        super(MCMLException.ASR);

        // create Comunication control class for ASR.
        m_ASRComCtrl = null;
        m_FrameSyncBytes = 0;
        m_ModelChanger = null;
        m_ASRParam = null;
        m_EngineName = "";
        m_SrTextFormat = null;
        m_SRTimer = null;
        m_VoiceFontSelector = null;
        m_SpeechData = null;
        m_ConvertNumKo = null;
    }

    /**
     * Initialization
     *
     * @param engineNo
     * @param abortCtrl
     * @param modelManager
     * @throws Exception
     */
    public void initialize(int engineNo,
            ModelManager modelManager) throws Exception {
        // get ASR's Parameter Manager class
        ASRProperties properties = ASRProperties.getInstance();
        m_ASRParam = properties.getASRParam(engineNo);

        // set FrameSyncBytes.
        m_FrameSyncBytes = m_ASRParam.getFrameSyncSize();

        // read Acoustic Model Change Command Table.
        // read Model Change Command Table.
        m_ModelChanger = new ModelChanger();
        m_ModelChanger.initialize(m_ServerType,
                properties.getRPCCommendSender(), m_ASRParam.getEngineHost(),
                m_ASRParam.getRpcNumber(),
                m_ASRParam.getLMCommandTableFileName(),
                m_ASRParam.getAMCommandTableFileName());

        // regist ModelChanger to ModelManager.
        if (modelManager != null) {
            modelManager.addModelChanger(m_ModelChanger);
        }
        // get ASR's parameter for connecting.
        String ipAddress = m_ASRParam.getEngineHost();
        int retryTimes = properties.getConnectRetryTimes();
        int retryInterval = properties.getConnectRetryInterval();
        int speechInputPort = m_ASRParam.getSpeechInputPort();
        int nbestReceivePort = m_ASRParam.getNBestReceivePort();
        String clientHost = m_ASRParam.getClientEngineHost();
        int clientSpeechInputPort = m_ASRParam.getClientSpeechInputPort();
        int clientNbestReceivePort = m_ASRParam.getClientNBestReceivePort();
        m_ASRComCtrl = new ASRComCtrl(ipAddress, retryTimes, retryInterval,
                speechInputPort, nbestReceivePort, clientHost,
                clientSpeechInputPort, clientNbestReceivePort);

        // create ASRName(asrX is 1 origin.).
        m_EngineName = "asr_" + properties.getLanguage1()
                + String.valueOf(engineNo + 1);
        m_IsSendOneWav = properties.isSendOneWav();

        System.out.println("send one wave=" + m_IsSendOneWav);

        // ASR Speech Input Port and NBest Receive Port connecting start.
        m_ASRComCtrl.connect(m_EngineName);

        // initialize VoiceFontSelector
        m_VoiceFontSelector = new VoiceFontSelectorController(m_EngineName,
                properties.getVoiceFontSelectorTimeout());
        m_VoiceFontSelector.startConnecting(
                properties.getVoiceFontSelectorHost(),
                properties.getVoiceFontSelectorPort(),
                properties.getConnectRetryTimes(),
                properties.getConnectRetryInterval());
        // set Source Endian to VoiceFontSelector(only BigEndian)
        m_VoiceFontSelector.setSourceEndian(true);

        // check connected.
        while (true) {
            ConnectState state = m_ASRComCtrl.getConnectState();
            if (state == ConnectState.FAILED) {
                throw new IOException("ASR connect failed.");
            }
            if (state == ConnectState.CONNECTED) {
                break;
            }
        }

        // set Charset Code to NBestreader.
        m_ASRComCtrl.initNBestStreamReader(properties.getLanguage1StringCode(),
                m_EngineName);

        // create Sr text formater
        m_SrTextFormat = new SrTextFormat(properties.getTextFilterFile());

        String resultConverter = properties.getResultConverter();
        if (resultConverter != null && !resultConverter.isEmpty()) {
            int pos = resultConverter.indexOf(":");
            if (pos >= 1) {
                String lang = resultConverter.substring(0, pos);
                if (lang.equalsIgnoreCase("ko")) { // korean
                    m_ConvertNumKo = new ConvertNumKo();
                    m_ConvertNumKo.initialize(resultConverter
                            .substring(pos + 1));
                }
            }
        }

        // create SRTimer.
        long time = properties.getTimeOutTime();
        long ratio = properties.getTimeOutRatio();
        if (0 < time && 0 < ratio) {
            writeLog("SRTimer On");
            m_SRTimer = new SRTimer(this, time, ratio);
        }
    }

    @Override
    public void processTimeout(MCMLData data) {
        synchronized (data) {
            if (!m_SRTimer.isStoped()) {
                // write TraceLog.
                ServerApLogger.getInstance().writeWarning("Time out occured.");

                // set TimeoutFlag.
                m_IsTimeout = true;

                // --------------------------------------------------------------------------------
                // create MCML/Error
                // --------------------------------------------------------------------------------
                MCMLException exp = new MCMLException("SR Time out occured.",
                        MCMLException.ERROR, m_ServerType,
                        MCMLException.TIME_OUT);

                // SYSTEM_TIME_OUT
                ErrorType error = new ErrorType();
                MCMLType outputMCML = new MCMLType();
                try {
                    error = XMLTypeTools.generateErrorType(exp.getErrorCode(),
                            exp.getExplanation(), exp.getService());

                    ResponseType responseType = new ResponseType();
                    responseType.addService(MCMLStatics.SERVICE_ASR);
                    responseType.addProcessOrder("0");
                    responseType.addError(error);
                    ServerType serverType = new ServerType();
                    serverType.addResponse(responseType);
                    outputMCML.addServer(serverType);
                } catch (Exception e) {
                    ServerApLogger.getInstance().writeError(
                            "create Timeout responce failed.");
                    ServerApLogger.getInstance().writeException(e);
                }
                data.setMCMLType(outputMCML);

                // notification for receiver thread
                data.doNotification();

                // SRTimer stop.
                m_SRTimer.stop();
            }
        }
    }

    // ------------------------------------------
    // protected member functions(implementation)
    // ------------------------------------------
    @Override
    protected Date processRequest(MCMLData data) throws Exception {
        Date responceStartTime = null;
        try {
            // get inputMCML
            MCMLType inputMCML = data.getMCMLType();

            // check service and check language and get Request
            RequestType request = getRequest(inputMCML);

            // get Audio
            AudioType mcmlAudioType = XMLTypeTools.getAudioType(request);
            String audio = getAudio(mcmlAudioType);

            // get AudioConverter.AudioType
            AudioConverter.AudioType audioConverterAudioType = AudioConverter.AudioType.Unexpected;
            audioConverterAudioType = getAudioType(audio);

            boolean isBigEndian = true;
            if (audioConverterAudioType == AudioConverter.AudioType.Raw
                    || audioConverterAudioType == AudioConverter.AudioType.Speex) {
                // get Endian
                String endian = getEndian(mcmlAudioType);
                isBigEndian = getIsBigEndian(endian);
            }

            // create AudioConverter
            AudioConverter audioConv = createAudioConverter(
                    audioConverterAudioType, isBigEndian);

            // get InputUserProfileType
            InputUserProfileType inputUserProfile = request
                    .hasInputUserProfile() ? request.getInputUserProfile()
                    : null;

            // get is VoiceFont select on
            m_VoiceFontSelector
                    .setIsVoiceFontSelectOn(isVoiceFontSelectOn(inputUserProfile));

            // change LanguageModel
            String task = getTask(mcmlAudioType);
            m_ModelChanger.changeLanguageModel(task);

            // change Acoustic Model.
            m_ModelChanger.changeAcousticModel(inputMCML, inputUserProfile);

            // get Binary Data from Client and send Binary Data to Engine.
            processSpeechData(data, audioConverterAudioType, audioConv);

            // start SRTimer
            if (m_SRTimer != null) {
                m_SRTimer.start(audioConverterAudioType, data);
            }

            // get and parse NBest.
            String domain = getDomain(mcmlAudioType);
            responceStartTime = processNBest(inputMCML, request, data,
                    inputUserProfile, domain, task);
        } catch (MCMLException exp) {
            // ErrorMessage set.
            MCMLType errMCML = doError(exp);
            data.setMCMLType(errMCML);
            data.setIsErrorOccured(true);

            // VoiceFontSelector reconnect start
            reconnectVoiceFontSelector();
        } catch (Exception e) {
            writeLog("error in processSpeechData");
            writeLog(e.getMessage());
            MCMLException exp = new MCMLException(e.getMessage(),
                    MCMLException.SYSTEM, m_ServerType,
                    MCMLException.INTERNAL_ABNORMALITY);
            // ErrorMessage set.
            MCMLType errMCML = doError(exp);
            data.setMCMLType(errMCML);
            data.setIsErrorOccured(true);
        }
        return responceStartTime;
    }

    /** terminate */
    @Override
    protected void processTermination() throws Exception {
        writeLog("ASREngineCtrl::processTermination() start.");
        // Socket close for VoiceFontSelector
        m_VoiceFontSelector.terminate();

        // Socket close for ASR.
        m_ASRComCtrl.disconnect();

        // SRTimer shutdown.
        if (m_SRTimer != null) {
            m_SRTimer.stop();
            m_SRTimer.shutdown();
        }

        super.processTermination();

        // normal end
        writeLog("ASREngineCtrl::processTermination() end.");
        return;
    }

    @Override
    protected void postProcessRequest(String errorCode) throws Exception {
        if (errorCode != null) {
            if (errorCode.equalsIgnoreCase(ENGINE_CLOSED_ERRORCODE)) {
                throw new Exception("Engine closed.");
            }
        }
    }

    // ------------------------------------------
    // private member function
    // ------------------------------------------
    private RequestType getRequest(MCMLType inputMCML) throws MCMLException {
        RequestType validRequest = null;

        try {
            // get Request count
            int requestCnt = inputMCML.getServer().getRequestCount();

            // check and get Request
            for (int i = 0; i < requestCnt; i++) {
                RequestType request = inputMCML.getServer().getRequestAt(i);

                // check service is ASR
                if (request.hasService()
                        && XMLTypeTools.serviceIsASR(request.getService()
                                .getValue())) {

                    // get Language
                    String language = getSrcLanguage(request);
                    if (!language.isEmpty()) {
                        // check Language(check valid language).

                        if (language.equalsIgnoreCase(ASRProperties
                                .getInstance().getLanguage1())) {
                            if (validRequest == null) {
                                // get Request
                                validRequest = request;
                            } else {
                                // Already got Request.
                                writeLog("Two Request have the same language.");
                                throw new Exception(
                                        "Two Request have the same language.");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // not supported Format
            String message = e.getMessage();
            if (message == null || message.isEmpty()) {
                message = "getRequest() failed.";
                writeLog("getRequest() failed.");
            }
            throw new MCMLException(message, MCMLException.ERROR, m_ServerType,
                    MCMLException.ABNORMAL_XML_DATA_FORMAT);
        }

        if (validRequest == null) {
            // Request hasn't support language.
            writeLog("Request hasn't support language.");
            throw new MCMLException("Request hasn't support language.",
                    MCMLException.ERROR, m_ServerType,
                    MCMLException.NOT_SUPPORT_LANGUAGE);
        }

        return validRequest;
    }

    private String getAudio(AudioType audioType) throws MCMLException {
        // audio is required
        String audio = "";
        try {
            audio = audioType.getSignal().getAudioFormat().getValue();
        } catch (Exception e) {
            // getAudio() failed.
            writeLog("MCML/Server/Request/Input/Data/Audio/Signal@AudioFormat is invalid value.");
            throw new MCMLException(
                    "MCML/Server/Request/Input/Data/Audio/Signal@AudioFormat is invalid value.",
                    MCMLException.ERROR, m_ServerType,
                    MCMLException.ABNORMAL_XML_DATA_FORMAT);
        }
        return audio;
    }

    private String getDomain(AudioType audioType) throws MCMLException {
        String domain = "";
        try {
            if (audioType.hasModelType()
                    && audioType.getModelType().hasDomain()) {
                // change Language Model.
                domain = audioType.getModelType().getDomain().getValue();
            }
        } catch (Exception e) {
            // MCML is wrong.
            writeLog("MCML/Server/Request/Input/Data/Audio/ModelType/Domain is invalid value.");
            throw new MCMLException(
                    "MCML/Server/Request/Input/Data/Audio/ModelType/Domain is invalid value.",
                    MCMLException.ERROR, m_ServerType,
                    MCMLException.ABNORMAL_XML_DATA_FORMAT);
        }

        return domain;
    }

    private String getTask(AudioType audioType) throws MCMLException {
        String task = "";
        try {
            if (audioType.hasModelType() && audioType.getModelType().hasTask()) {
                // change Language Model.
                task = audioType.getModelType().getTask().getValue();
            }
        } catch (Exception e) {
            // MCML is wrong.
            writeLog("MCML/Server/Request/Input/Data/Audio/ModelType/Task is invalid value.");
            throw new MCMLException(
                    "MCML/Server/Request/Input/Data/Audio/ModelType/Task is invalid value.",
                    MCMLException.ERROR, m_ServerType,
                    MCMLException.ABNORMAL_XML_DATA_FORMAT);
        }

        return task;
    }

    private AudioConverter.AudioType getAudioType(String audioType)
            throws MCMLException {
        AudioConverter.AudioType audioFormatType = AudioConverter.AudioType.Unexpected;
        if (audioType.equalsIgnoreCase(MCMLStatics.AUDIO_RAW)) {
            audioFormatType = AudioConverter.AudioType.Raw;
        } else if (audioType.equalsIgnoreCase(MCMLStatics.AUDIO_ADPCM)) {
            audioFormatType = AudioConverter.AudioType.ADPCM;
        } else if (audioType.equalsIgnoreCase(MCMLStatics.AUDIO_SPEEX)) {
            audioFormatType = AudioConverter.AudioType.Speex;
        } else if (audioType.equalsIgnoreCase(MCMLStatics.AUDIO_DSR)) {
            audioFormatType = AudioConverter.AudioType.DSR;
        } else {
            // UNEXPECTED String
            writeLog("MCML/Server/Request/Input/Data/Audio/Signal@AudioFormat is Unexpected String.");
            throw new MCMLException(
                    "MCML/Server/Request/Input/Data/Audio/Signal@AudioFormat is Unexpected String.",
                    MCMLException.ERROR, m_ServerType,
                    MCMLException.NON_CORESSPONDANCE_PARAM);
        }
        return audioFormatType;
    }

    private String getEndian(AudioType audioType) throws MCMLException {
        // endian is optional
        String endian = "";
        try {
            if (audioType.getSignal().hasEndian()) {
                endian = audioType.getSignal().getEndian().getValue();
            }
        } catch (Exception e) {
            // getAudio() failed.
            writeLog("MCML/Server/Request/Input/Data/Audio/Signal@Endian is invalid value.");
            throw new MCMLException(
                    "MCML/Server/Request/Input/Data/Audio/Signal@Endian is invalid value.",
                    MCMLException.ERROR, m_ServerType,
                    MCMLException.ABNORMAL_XML_DATA_FORMAT);
        }

        return endian;
    }

    private boolean getIsBigEndian(String endian) throws MCMLException {
        boolean isBigEndian = true;
        if (endian.equalsIgnoreCase(MCMLStatics.ENDIAN_BIG)) {
            isBigEndian = true;
        } else if (endian.equalsIgnoreCase(MCMLStatics.ENDIAN_LITTLE)) {
            isBigEndian = false;
        } else {
            // UNEXPECTED String
            writeLog("MCML/Server/Request/Input/Data/Audio/Signal@Endian is Unexpected String.");
            throw new MCMLException(
                    "MCML/Server/Request/Input/Data/Audio/Signal@Endian is Unexpected String.",
                    MCMLException.ERROR, m_ServerType,
                    MCMLException.NON_CORESSPONDANCE_PARAM);
        }

        return isBigEndian;
    }

    private AudioConverter createAudioConverter(
            AudioConverter.AudioType audioType, boolean isBigEndian) {
        AudioConverter audioConv;

        // AudioType = RAW and BigEndianz
        if (audioType == AudioConverter.AudioType.Raw && !isBigEndian) {
            // AudioConverter(boolean srcIsRawData, boolean srcIsBigEndian,
            // boolean trgIsRawData, boolean trgIsBigEndian)
            // LittleEndian => BigEndian Converter
            audioConv = new AudioConverter(true, isBigEndian, true, true,
                    m_ServerType);
        } else if (audioType == AudioConverter.AudioType.ADPCM) {
            // AudioConverter(boolean srcIsRawData, boolean srcIsBigEndian,
            // boolean trgIsRawData, boolean trgIsBigEndian)
            // ADPCM => RAW Converter
            audioConv = new AudioConverter(false, isBigEndian, true, true,
                    m_ServerType);
        } else if (audioType == AudioConverter.AudioType.Speex) {
            audioConv = new SpeexAudioConverter(false, isBigEndian, true, true,
                    m_ServerType);
        } else {
            // unnecessary Converter
            audioConv = null;
        }
        return audioConv;
    }

    private String getAge(InputUserProfileType inputUserProfileType)
            throws Exception {
        // get Age.
        String age = "";
        try {
            if (inputUserProfileType.hasAge()) {
                age = String.valueOf(inputUserProfileType.getAge().getValue());
            }
        } catch (Exception e) {
            // MCML is wrong.
            writeLog("MCML/Server/Request/InputUserProfile/@Age is invalid value.");
            throw new MCMLException(
                    "MCML/Server/Request/InputUserProfile/@Age is invalid value.",
                    MCMLException.ERROR, m_ServerType,
                    MCMLException.ABNORMAL_XML_DATA_FORMAT);
        }

        return age;
    }

    private String getGender(InputUserProfileType inputUserProfileType)
            throws Exception {
        // get Gender.
        String gender = "";
        try {
            if (inputUserProfileType.hasGender()) {
                gender = inputUserProfileType.getGender().getValue();
            }
        } catch (Exception e) {
            // MCML is wrong.
            writeLog("MCML/Server/Request/InputUserProfile/@Gender is invalid value.");
            throw new MCMLException(
                    "MCML/Server/Request/InputUserProfile/@Gender is invalid value.",
                    MCMLException.ERROR, m_ServerType,
                    MCMLException.ABNORMAL_XML_DATA_FORMAT);
        }

        return gender;
    }

    private String getSrcLanguage(RequestType request) throws Exception {
        // get Language.
        String language = "";
        try {

            if (request.hasInput() && request.getInput().hasAttachedBinary()
                    && request.getInput().getAttachedBinary().hasDataID()) {

                language = request.getInput().getAttachedBinary().getDataID()
                        .getValue();
            }

        } catch (Exception e) {
            // MCML is wrong.
            writeLog("MCML/Server/Request/Input/AttachedBinary@DataID is invalid value.");

            throw new MCMLException(
                    "MCML/Server/Request/Input/AttachedBinary@DataID is invalid value.",
                    MCMLException.ERROR, m_ServerType,
                    MCMLException.ABNORMAL_XML_DATA_FORMAT);
        }
        return language;
    }

    private boolean isVoiceFontSelectOn(InputUserProfileType inputUserProfile)
            throws MCMLException {
        if (inputUserProfile == null || !inputUserProfile.hasID()) {
            return false;
        }

        try {
            String id = inputUserProfile.getID().toString();
            if (id.equalsIgnoreCase("INPUT")) {
                return true;
            }
        } catch (Exception e) {
            // UNEXPECTED Error
            writeLog("MCML/Server/Request/InputUserProfile@ID is wrong.");
            throw new MCMLException(
                    "MCML/Server/Request/InputUserProfile@ID is wrong.",
                    MCMLException.ERROR, m_ServerType,
                    MCMLException.ABNORMAL_XML_DATA_FORMAT);
        }

        return false;
    }

    private int getMaxNBest(TargetOutputType targetOutput) throws Exception {
        // MaxNBest's Default Value is 1.
        int maxNBest = 1;
        try {
            if (targetOutput != null && targetOutput.hasHypothesisFormat()
                    && targetOutput.getHypothesisFormat().hasNofN_best()) {
                maxNBest = targetOutput.getHypothesisFormat().getNofN_best()
                        .intValue();
            } else {
                writeLog("MCML/Server/Request/TargetOutput/HypothesisFormat@NofN-best is null.");
                System.out
                        .println("MCML/Server/Request/TargetOutput/HypothesisFormat@NofN-best is null.");
            }
            if (maxNBest < 1) {
                writeLog("MCML/Server/Request/TargetOutput/HypothesisFormat@NofN-best is Negative Integer or 0.");
                System.out
                        .println("MCML/Server/Request/TargetOutput/HypothesisFormat@NofN-best is Negative Integer or 0.");
                maxNBest = 1;
            }
        } catch (Exception e) {
            writeLog("MCML/Server/Request/TargetOutput/HypothesisFormat@NofN_best is invalid value.");
            throw new MCMLException(
                    "MCML/Server/Request/TargetOutput/HypothesisFormat@NofN_best is invalid value.",
                    MCMLException.ERROR, m_ServerType,
                    MCMLException.ABNORMAL_XML_DATA_FORMAT);
        }
        return maxNBest;
    }

    private void processSpeechData(MCMLData data,
            AudioConverter.AudioType audioType, AudioConverter audioConv)
            throws MCMLException, Exception {
        try {
            // create Speech Data Buffer.
            ByteArrayOutputStream buffBAOS = new ByteArrayOutputStream();

            // check AudioType.
            int frameSize = getFrameSyncSize(audioType, audioConv);

            while (true) {
                // get Speech Data from Client.
                byte[] binData = data.takeBinaryQueue();

                // is Last Data?
                if (binData == null) {
                    if (!m_IsSending) {
                        writeLog("no Binary Data.");
                        throw new MCMLException("no Binary Data.",
                                MCMLException.SYSTEM, m_ServerType,
                                MCMLException.INTERNAL_ABNORMALITY);
                    }

                    // convert and send Speech Data.
                    if (audioType == AudioConverter.AudioType.DSR) {
                        // sending state end.
                        m_IsSending = false;
                        break;
                    } else if (audioType == AudioConverter.AudioType.ADPCM) {
                        convertAndSendSpeechData(buffBAOS.toByteArray(),
                                m_FrameSyncBytes / 4, audioConv, true);
                    } else {
                        convertAndSendSpeechData(buffBAOS.toByteArray(),
                                m_FrameSyncBytes, audioConv, true);
                    }

                    // send VoiceData(start to select VoiceFont)
                    m_VoiceFontSelector.selectVoiceFont(binData);

                    endToSendSpeechData();

                    if (audioType == AudioConverter.AudioType.Speex) {
                        ((SpeexAudioConverter) audioConv).destroy();
                    }

                    break;
                }

                if (audioType == AudioConverter.AudioType.DSR) {
                    // send SpeechData.
                    if (!m_IsSending) {
                        m_IsSending = true;
                    }

                    m_ASRComCtrl.sendData(binData);

                } else {

                    if (audioType == AudioConverter.AudioType.Speex) {
                        // decode Speex data

                        binData = ((SpeexAudioConverter) audioConv)
                                .pre_convert(binData);

                    } else {
                        // adpcm or raw data
                    }

                    // buffering Speech Data.
                    buffBAOS.write(binData);

                    // calc Utterance Length for SRTimer.
                    if (m_SRTimer != null) {
                        m_SRTimer.addSpeechDataSize(binData);
                    }

                    // check Speech Data Size(is convert OK?).
                    byte[] remainData = null;
                    if (buffBAOS.size() >= frameSize) {
                        // start to send Speech Data(at first time).
                        startToSendSpeechData();

                        // convert and send Speech Data.
                        remainData = convertAndSendSpeechData(
                                buffBAOS.toByteArray(), frameSize, audioConv,
                                false);
                    }
                    if (remainData != null) {
                        // reset Speech Data Buffer.
                        buffBAOS.reset();
                        buffBAOS.write(remainData);
                    }
                }
            }
            // close stream.
            buffBAOS.close();
        } catch (IllegalArgumentException e) {
            writeLog("SpeechDataSize is illegal.");
            throw new MCMLException("SpeechDataSize is illegal.",
                    MCMLException.SYSTEM, m_ServerType,
                    MCMLException.INTERNAL_ABNORMALITY);
        } catch (UnsupportedOperationException e) {
            writeLog("FrameSync Header unmatched.");
            throw new MCMLException("FrameSync Header unmatched.",
                    MCMLException.SYSTEM, m_ServerType,
                    MCMLException.INTERNAL_ABNORMALITY);
        } catch (IOException e) {
            writeLog(e.getMessage());
            throw new MCMLException(e.getMessage(), MCMLException.SYSTEM,
                    m_ServerType, MCMLException.INTERNAL_ABNORMALITY);
        } finally {
            if (m_IsSending) {
                // clear Engine process.
                endToSendSpeechData();
            }
        }
    }

    private int getFrameSyncSize(AudioConverter.AudioType audioType,
            AudioConverter audioConv) throws MCMLException {
        // get FrameSize
        int frameSize = 0;
        if (audioType == AudioConverter.AudioType.ADPCM) {
            if (audioConv == null) {
                writeLog("isn't AudioConverter(Unexpected Error).");
                throw new MCMLException(
                        "isn't AudioConverter(Unexpected Error).",
                        MCMLException.SYSTEM, m_ServerType,
                        MCMLException.INTERNAL_ABNORMALITY);
            }
            frameSize = m_FrameSyncBytes / 4;
        } else {
            frameSize = m_FrameSyncBytes;
        }
        return frameSize;
    }

    private void startToSendSpeechData() throws MCMLException {
        if (!m_IsSending) {
            // send TOF
            FrameSyncData tof = new FrameSyncData(m_FrameSyncBytes);
            tof.setHeader(FrameSyncData.TOF);
            m_ASRComCtrl.sendData(tof.toBinary());

            // send Start PU
            FrameSyncData startPU = new FrameSyncData(m_FrameSyncBytes);
            startPU.setHeader(FrameSyncData.STARTPU);
            m_ASRComCtrl.sendData(startPU.toBinary());

            // sending state start.
            m_IsSending = true;

            m_SpeechData = new ByteArrayOutputStream();

        }
    }

    private void endToSendSpeechData() throws MCMLException {
        // send End PU
        FrameSyncData endPU = new FrameSyncData(m_FrameSyncBytes);
        endPU.setHeader(FrameSyncData.ENDPU);
        m_ASRComCtrl.sendData(endPU.toBinary());

        // send EOF
        FrameSyncData eof = new FrameSyncData(m_FrameSyncBytes);
        eof.setHeader(FrameSyncData.EOF);
        m_ASRComCtrl.sendData(eof.toBinary());

        // sending state end.
        m_IsSending = false;

        m_SpeechData.reset();

    }

    private byte[] convertAndSendSpeechData(byte[] speechData, int readSize,
            AudioConverter audioConv, boolean isLastData) throws IOException,
            MCMLException {
        // create ReadBufferStream.
        ByteArrayInputStream buffBAIS = new ByteArrayInputStream(speechData);

        // convert and send Speech Data.
        while (buffBAIS.available() >= readSize) {
            byte[] sendData = convertAndSendSpeechDataSub(buffBAIS, readSize,
                    audioConv);

            if (m_IsSendOneWav) {
                // If you want to send bulk
                m_SpeechData.write(sendData);
            } else {
                // If you want to send split
                sendSpeechData(sendData);
            }
        }

        // flush Last Data.
        if (isLastData) {
            byte[] sendData = convertAndSendSpeechDataSub(buffBAIS, readSize,
                    audioConv);

            if (m_IsSendOneWav) {
                // If you want to send bulk
                m_SpeechData.write(sendData);
                ByteArrayInputStream buff = new ByteArrayInputStream(
                        m_SpeechData.toByteArray());
                byte[] readBuff = new byte[m_FrameSyncBytes];
                while (buff.available() > 0) {
                    // read Speech Data.
                    buff.read(readBuff);
                    sendSpeechData(readBuff);
                }
            } else {
                // If you want to send split
                sendSpeechData(sendData);
            }
        }

        // return remain Speech Data.
        byte[] remainData = new byte[buffBAIS.available()];
        buffBAIS.read(remainData);
        // close stream.
        buffBAIS.close();
        return remainData;
    }

    private byte[] convertAndSendSpeechDataSub(ByteArrayInputStream buffBAIS,
            int readSize, AudioConverter audioConv) throws IOException,
            MCMLException {
        // create ReadBuffer.
        byte[] readBuff = new byte[readSize];

        // read Speech Data.
        buffBAIS.read(readBuff);
        byte[] sendData = null;
        if (audioConv != null) {
            sendData = audioConv.convert(readBuff);

        } else {
            sendData = readBuff;
        }

        return sendData;

    }

    private void sendSpeechData(byte[] sendData) throws IOException,
            MCMLException {
        // buffering VoiceData
        m_VoiceFontSelector.selectVoiceFont(sendData);

        FrameSyncData frameSyncData = new FrameSyncData(m_FrameSyncBytes);
        frameSyncData.setHeader(FrameSyncData.DATA);
        frameSyncData.setSpeechData(sendData);

        // send SpeechData.
        m_ASRComCtrl.sendData(frameSyncData.toBinary());

    }

    private Date processNBest(MCMLType inputMCML, RequestType request,
            MCMLData data, InputUserProfileType inputUserProfile,
            String domain, String task) throws Exception {
        // Recognition Result Receive Start
        String receivedData = m_ASRComCtrl.receiveData();
        writeLog(m_EngineName + "NBest Data recived.");
        // writeLog("[ProcessTime]ReceiveResponce");
        Date responceStartTime = new Date();
        writeLog(receivedData);

        synchronized (data) {
            // stop SRTimer
            if (m_SRTimer != null) {
                m_SRTimer.stop();
            }

            if (!m_IsTimeout) {

                // convert NBestResult to MCML.
                ASRProperties properties = ASRProperties.getInstance();
                SRNBestProcessor nbestProc = new SRNBestProcessor(
                        properties.getLanguage1(),
                        properties.getLanguage1Delimiter(),
                        m_ASRParam.isGwppUsed(), m_SrTextFormat, m_ConvertNumKo);
                ArrayList<SentenceSequenceType> outputSentenceSequenceTypes = nbestProc
                        .parseNBestResult(receivedData);

                // create outputMCML
                MCMLType outputMCML = createOutputMCMLType(inputMCML, request,
                        inputUserProfile, domain, task,
                        outputSentenceSequenceTypes);

                // SpeechData clear.
                data.clearBinaryData();

                // set Result MCML(XML)
                data.setMCMLType(outputMCML);

                // set Acoustic Model Name.
                ASREngineInfo engineInfo = new ASREngineInfo();
                engineInfo.setAcousticModelName(m_ASRComCtrl
                        .getAcousticModelName());

                // set Engine Information.
                data.setEngineInfo(engineInfo);
            }
        }
        return responceStartTime;
    }

    private MCMLType createOutputMCMLType(MCMLType inputMCML,
            RequestType request, InputUserProfileType inputUserProfile,
            String domain, String task,
            ArrayList<SentenceSequenceType> outputSentenceSequenceTypes)
            throws Exception {
        // create TextType
        TextType textType = new TextType();
        textType.addChannelID(MCMLStatics.MODEL_CHANNEL_ID_TEXT);

        ModelTypeType modelType = createModelType(domain, task,
                inputUserProfile);
        textType.addModelType(modelType);

        // get TargetOutputType
        TargetOutputType targetOutputType = null;
        if (request.hasTargetOutput()) {
            targetOutputType = request.getTargetOutput();
        }
        int maxNBest = getMaxNBest(targetOutputType);
        // outputSentenceSequenceTypes null is already checked
        if (outputSentenceSequenceTypes.size() < maxNBest) {
            maxNBest = outputSentenceSequenceTypes.size();
        }
        for (int i = 0; i < maxNBest; i++) {
            textType.addSentenceSequence(outputSentenceSequenceTypes.get(i));
        }

        // set TextType to DataType
        DataType dataType = new DataType();
        dataType.addText(textType);

        // set DataType to OutputType
        OutputType output = new OutputType();
        output.addData(dataType);

        // set OutputType to ResponseType
        ResponseType response = new ResponseType();
        response.addService(MCMLStatics.SERVICE_ASR);
        if (request.hasProcessOrder()) {
            response.addProcessOrder(request.getProcessOrder());
        }
        response.addOutput(output);

        // set ResponseType to ServerType
        ServerType server = new ServerType();
        server.addResponse(response);

        // check and copy required parameter
        MCMLType outputMCML = new MCMLType();
        copyMCMLType(inputMCML, outputMCML);

        // set ServerType to Output MCML
        outputMCML.addServer(server);

        // normal end
        return outputMCML;
    }

    private ModelTypeType createModelType(String domain, String task,
            InputUserProfileType inputUserProfile) throws Exception {
        ModelTypeType modelTypeType = new ModelTypeType();

        // Language
        ASRProperties properties = ASRProperties.getInstance();
        LanguageType languageType = new LanguageType();
        languageType.addID(properties.getLanguage1());
        modelTypeType.addLanguage(languageType);

        // Domain
        if (!domain.isEmpty()) {
            modelTypeType.addDomain(domain);
        }

        // Task
        if (!task.isEmpty()) {
            modelTypeType.addTask(task);
        }

        // Personality
        String voiceFontName = m_VoiceFontSelector.getVoiceFontName();

        // replace string format VoiceFontID[f0] => VoiceFontID,f0
        Matcher match = F0_PATTERN.matcher(voiceFontName);
        String newVoiceFontName = "";
        if (match.find()) {
            newVoiceFontName = voiceFontName.substring(0, match.start());
            String temp = match.group();
            String f0 = temp.substring(1, temp.length() - 1);
            newVoiceFontName += "," + f0;
        } else {
            newVoiceFontName = voiceFontName;
        }

        PersonalityType personalityType = createPersonality(inputUserProfile,
                newVoiceFontName);
        if (personalityType != null) {
            modelTypeType.addPersonality(personalityType);
        }

        // succeeded
        return modelTypeType;
    }

    private PersonalityType createPersonality(
            InputUserProfileType inputUserProfile, String voiceFontName)
            throws Exception {
        if (inputUserProfile == null) {
            return null;
        }

        String age = getAge(inputUserProfile);
        String gender = getGender(inputUserProfile);

        if (age.isEmpty() && gender.isEmpty() && voiceFontName.isEmpty()) {
            return null;
        }

        PersonalityType personalityType = new PersonalityType();
        // Gender
        if (!gender.isEmpty()) {
            personalityType.addGender(gender);
        }

        // Age
        if (!age.isEmpty()) {
            personalityType.addAge(age);
        }

        // set VoiceFontName
        if (!voiceFontName.isEmpty()) {
            personalityType.addID(voiceFontName);
        }

        // succeeded
        return personalityType;
    }

    private void reconnectVoiceFontSelector() {
        try {
            // reconnect
            m_VoiceFontSelector.terminate();
            m_VoiceFontSelector = null;
            ASRProperties properties = ASRProperties.getInstance();
            m_VoiceFontSelector = new VoiceFontSelectorController(m_EngineName,
                    properties.getVoiceFontSelectorTimeout());
            m_VoiceFontSelector.startConnecting(
                    properties.getVoiceFontSelectorHost(),
                    properties.getVoiceFontSelectorPort(),
                    properties.getConnectRetryTimes(),
                    properties.getConnectRetryInterval());
        } catch (Exception e) {
            ServerApLogger.getInstance().writeError(
                    "m_VoiceFontSelector.terminate() failed");
        }
    }
}
