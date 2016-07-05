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

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.Date;

import jp.go.nict.mcml.serverap.common.AudioConverter;
import jp.go.nict.mcml.serverap.common.CacheManager;
import jp.go.nict.mcml.serverap.common.EngineCtrl;
import jp.go.nict.mcml.serverap.common.MCMLData;
import jp.go.nict.mcml.serverap.common.SpeexAudioConverter;
import jp.go.nict.mcml.servlet.MCMLException;
import jp.go.nict.mcml.servlet.MCMLStatics;
import jp.go.nict.mcml.servlet.XMLTypeTools;
import jp.go.nict.mcml.xml.types.AttachedBinaryType;
import jp.go.nict.mcml.xml.types.AudioType;
import jp.go.nict.mcml.xml.types.DataType;
import jp.go.nict.mcml.xml.types.InputUserProfileType;
import jp.go.nict.mcml.xml.types.MCMLType;
import jp.go.nict.mcml.xml.types.ModelTypeType;
import jp.go.nict.mcml.xml.types.OutputType;
import jp.go.nict.mcml.xml.types.PersonalityType;
import jp.go.nict.mcml.xml.types.RequestType;
import jp.go.nict.mcml.xml.types.ResponseType;
import jp.go.nict.mcml.xml.types.SentenceSequenceType;
import jp.go.nict.mcml.xml.types.SentenceType;
import jp.go.nict.mcml.xml.types.ServerType;
import jp.go.nict.mcml.xml.types.SignalType;
import jp.go.nict.mcml.xml.types.SurfaceType2;
import jp.go.nict.mcml.xml.types.TargetOutputType;
import jp.go.nict.mcml.xml.types.TextType;
import jp.go.nict.ssml.xml.XMLProcessor;
import jp.go.nict.ssml.xml.types.speakType;
import jp.go.nict.ssml.xml.types.voice;

/**
 * TTSEngineCtrl class.
 *
 *
 */
public class TTSEngineCtrl extends EngineCtrl {
    // ------------------------------------------
    // private member constant
    // ------------------------------------------
    private static final String ENGINE_CLOSED_ERRORCODE = "E-20030110";

    private static final String SYNCHRONIZER = "ForSynchronize";

    // ------------------------------------------
    // private member variables(instance field)
    // ------------------------------------------
    private TTSComCtrl m_ComCtrl;
    private int m_EngineNo;
    private int m_BytesPerFrame;
    private SsIOFilter m_SsIOFilter;
    private CacheManager m_CacheManager;
    private short m_ConsecutiveNo;
    private VoiceFontManager m_VoiceFontManager;

    // ------------------------------------------
    // public member functions
    // ------------------------------------------
    /**
     * Constructor
     *
     * @param cacheManager
     */
    public TTSEngineCtrl(CacheManager cacheManager) {
        // set ServerType
        super(MCMLException.TTS);

        // create Comunication control class for TTS.
        m_ComCtrl = new TTSComCtrl();
        m_EngineNo = 0;
        m_BytesPerFrame = 0;
        m_SsIOFilter = null;
        m_CacheManager = cacheManager;
        m_ConsecutiveNo = 0;
    }

    /**
     * Initialization
     *
     * @param engineNo
     * @return boolean
     * @throws IllegalStateException
     * @throws IOException
     * @throws InterruptedException
     * @throws Exception
     */
    public boolean initialize(int engineNo) throws IllegalStateException,
            IOException, InterruptedException, Exception {
        m_EngineNo = engineNo;

        // get TTS's Parameter Manager class
        TTSProperties prop = TTSProperties.getInstance();
        TTSParam param = prop.getTTSParam(m_EngineNo);

        // EngineNumber isn't overrange.
        if (param == null) {
            throw new IllegalStateException("Engine Number is out of Bounds");
        }
        m_BytesPerFrame = param.getBytesPerFrame();

        m_VoiceFontManager = new VoiceFontManager();
        m_VoiceFontManager.readVoiceFontTableFile(prop.getVoiceFontFileName());

        // TTS Port connecting start.
        String ipAddress = param.getEngineHost();
        int port = param.getEnginePort();
        String clientHost = param.getEngineClientHost();
        int clientPort = param.getEngineClientPort();
        int retryTimes = prop.getConnectRetryTimes();
        int retryInterval = prop.getConnectRetryInterval();

        // create I/O filter
        m_SsIOFilter = new SsIOFilter(prop.getTextFilterFile());

        return m_ComCtrl.connect(ipAddress, port, clientHost, clientPort,
                retryTimes, retryInterval, m_EngineNo);
    }

    // ------------------------------------------
    // protected member function
    // ------------------------------------------
    /** SSRequest process */
    @Override
    protected Date processRequest(MCMLData data) throws Exception {
        try {
            Date responceReceiveTime = null;

            // print MCML received state.
            int dispEngineNo = 1;
            dispEngineNo += m_EngineNo;
            TTSProperties prop = TTSProperties.getInstance();
            String lang = prop.getLanguage1();
            String charset = prop.getLanguage1StringCode();
            String country = prop.getLanguage1CountryCode();
            System.out.println("TTS_" + lang + dispEngineNo
                    + " : Recieved MCML data");
            writeLog("TTS_" + lang + dispEngineNo + " : Recieved MCML data");

            // get inputMCML
            MCMLType inputMCML = data.getMCMLType();

            // check service and check language and get Request
            RequestType request = getRequest(inputMCML);

            InputUserProfileType inputUserProfile = null;
            if (request.hasInputUserProfile()) {
                inputUserProfile = request.getInputUserProfile();
            }

            TextType inputTextType = XMLTypeTools.getTextType(request);
            if (inputTextType == null) {
                // MCML is wrong.
                writeLog("MCML/Server/Request/Input/Data/Text is null.");
                throw new MCMLException(
                        "MCML/Server/Request/Input/Data/Text is null.",
                        MCMLException.ERROR, m_ServerType,
                        MCMLException.ABNORMAL_XML_DATA_FORMAT);
            }

            // get voiceId
            String voiceFontName = "";
            String f0_mean = Float.toString(SSMLStatics.F0_MEAN);

            if (m_VoiceFontManager != null) {
                synchronized (m_VoiceFontManager) {

                    VoiceFontManager.VoiceFont voiceFont = getVoiceID(
                            inputMCML, inputUserProfile);
                    if (voiceFont != null) {
                        voiceFontName = voiceFont.getVoiceFontName();
                        f0_mean = voiceFont.getF0Mean();
                    }
                }
            }

            String gender = "";
            String age = "";
            String ssmlGender = "";
            if (inputUserProfile != null) {
                // check and get gender
                gender = getGender(inputUserProfile);
                ssmlGender = getSSMLGender(gender);
                age = getAge(inputUserProfile);
            }

            // get sentence and adjust its delimiter
            String ssml = "";
            String sentence = "";
            SentenceSequenceType inputSentenceSequenceType = inputTextType
                    .hasSentenceSequence() ? inputTextType
                    .getSentenceSequence() : null;
            if (inputSentenceSequenceType == null) {
                // MCML is wrong.
                writeLog("MCML/Server/Request/Input/Data/Text/SentenceSequence is null.");
                throw new MCMLException(
                        "MCML/Server/Request/Input/Data/Text/SentenceSequence is null.",
                        MCMLException.ERROR, m_ServerType,
                        MCMLException.ABNORMAL_XML_DATA_FORMAT);
            }
            sentence = getSentence(inputSentenceSequenceType);

            // set locale
            String locale = lang + "-" + country;

            // create SSML
            ssml = createSSML(lang, locale, voiceFontName, f0_mean, ssmlGender,
                    sentence, charset);

            System.out.println("TTS_" + lang + dispEngineNo
                    + " : Input ssml = \"" + ssml + "\"");
            writeLog("TTS_" + lang + dispEngineNo + " : Input ssml = \"" + ssml
                    + "\"");

            float rateVal = SSMLStatics.SPEAK_RATE;
            if (prop.isRateChange()) {
                // get Rate's value.
                rateVal = getRate(prop.getRateValue());
            }

            // get CacheData.
            String cacheKey = sentence + "_" + voiceFontName + "_" + f0_mean
                    + "_" + Float.toString(rateVal);
            byte[] cacheData = null;
            if (m_CacheManager != null) {
                cacheData = m_CacheManager.getCacheData(cacheKey);
                System.out.println("TTS_" + lang + dispEngineNo
                        + " : cacheKey = \"" + cacheKey + "\"");
                writeLog("TTS_" + lang + dispEngineNo + " : cacheKey = \""
                        + cacheKey + "\"");
            }

            // set result Data.
            ByteArrayInputStream bais = null;

            if (cacheData == null) {
                // set send frame
                TTSFrameData ttsData = new TTSFrameData();
                System.out.println("TTS_" + lang + dispEngineNo
                        + " : setSendFrame() ConsecutiveNo="
                        + getConsecutiveNo() + ", rate=" + rateVal
                        + ", locale=" + locale + ", voiceFontName="
                        + voiceFontName + ", charset=" + charset);
                writeLog("TTS_" + lang + dispEngineNo
                        + " : setSendFrame() ConsecutiveNo="
                        + getConsecutiveNo() + ", rate=" + rateVal
                        + ", locale=" + locale + ", voiceFontName="
                        + voiceFontName + ", charset=" + charset);
                byte[] sendFramePacket = ttsData.setSendFrame(
                        TTSFrameData.TTS_COMMAND_CONVERT_REQUEST,
                        getConsecutiveNo(), (byte) 0x00, (byte) 0x00,
                        new Date(), false, 0, rateVal, locale, voiceFontName,
                        ssml, charset);

                // send frame packet to Engine
                m_ComCtrl.sendData(sendFramePacket);

                // receive binary data from TTS
                writeLog("TTS_" + lang + dispEngineNo
                        + " : start to recieve binary data");
                byte[] recieveData = m_ComCtrl.recieveData();

                bais = new ByteArrayInputStream(recieveData);

                responceReceiveTime = new Date();

                System.out.println("TTS_" + lang + dispEngineNo
                        + " : Recieved BinaryData from TTS");
                writeLog("TTS_" + lang + dispEngineNo
                        + " : Recieved BinaryData from TTS");

                // create CacheFile.
                if (m_CacheManager != null) {
                    m_CacheManager.setCacheData(cacheKey, recieveData);
                }

                incrementConsecutiveNo();
            } else {
                System.out.println("TTS_" + lang + dispEngineNo
                        + " : Used CacheData");
                writeLog("TTS_" + lang + dispEngineNo + " : Used CacheData");
                bais = new ByteArrayInputStream(cacheData);
            }

            // clear SpeechData.
            data.clearBinaryData();

            // get and check AudioType
            String audio = prop.getAudioFormat();
            AudioConverter.AudioType audioType = getAudioType(audio);

            boolean isBigEndian = false;
            String endian = MCMLStatics.ENDIAN_LITTLE;
            if (audioType.equals(AudioConverter.AudioType.Raw)) {
                // get and check Endian.
                endian = prop.getAudioEndian();
                isBigEndian = getEndian(endian);
            }

            // create AudioConverter
            AudioConverter audioConv = createAudioConverter(audioType,
                    isBigEndian);

            // calculate bytes per frame
            int bytesPerFrame = m_BytesPerFrame;
            if (audioType == AudioConverter.AudioType.ADPCM) {
                bytesPerFrame *= 4;
            }

            while (bais.available() > 0) {
                // get frame length
                int readBytes = (bytesPerFrame <= bais.available()) ? bytesPerFrame
                        : bais.available();
                if (audioType == AudioConverter.AudioType.Speex) {
                    readBytes = bais.available();
                }
                byte[] frameData = new byte[readBytes];

                // read frame data from recievedSSData
                bais.read(frameData);

                // set frame data to MCMLData
                if (audioType == AudioConverter.AudioType.Speex) {
                    byte[] encodeData = ((SpeexAudioConverter) audioConv)
                            .pre_convert(frameData);
                    data.addBinaryData(encodeData);
                    System.out.println("raw=" + frameData.length + " speex="
                            + encodeData.length);

                    BufferedOutputStream raw = new BufferedOutputStream(
                            new FileOutputStream("output.raw"));
                    raw.write(frameData);

                    BufferedOutputStream spx = new BufferedOutputStream(
                            new FileOutputStream("output.spx"));
                    spx.write(encodeData);
                    spx.flush();

                } else if (audioConv != null) {
                    byte[] encodeData = audioConv.convert(frameData);
                    data.addBinaryData(encodeData);
                } else {
                    data.addBinaryData(frameData);
                }
            }
            if (audioType == AudioConverter.AudioType.Speex) {
                ((SpeexAudioConverter) audioConv).destroy();
            }

            // get and check SamplingFrequency
            String samplingFrequency = prop.getAudioFrequency();

            // create outputMCML
            MCMLType outputMCML = createOutputMCMLType(inputMCML, request,
                    inputTextType, voiceFontName, f0_mean, gender, age, audio,
                    endian, samplingFrequency, lang);

            data.setMCMLType(outputMCML);

            Date responceSendTime = new Date();
            if (responceReceiveTime != null) {
                writeLog("[ProcessTime]ReceiveResponse->SendResponse: "
                        + (responceSendTime.getTime() - responceReceiveTime
                                .getTime()) + "msec ");
            }

        } catch (MCMLException exp) {
            // ErrorMessage set.
            MCMLType errMCML = doError(exp);
            data.setMCMLType(errMCML);
        }
        // normal end
        return null;
    }

    /** reconnect */
    @Override
    protected void postProcessRequest(String errorCode) throws Exception {
        // check MCML has ErrorCode.
        if (errorCode != null) {
            if (errorCode.equals(ENGINE_CLOSED_ERRORCODE)) {
                // close TTS Socket.
                m_ComCtrl.closeEngineSocket();

                // try to reconnect.
                if (!initialize(m_EngineNo)) {
                    throw new SocketException("failed to reconnect");
                }
            }
        }
        return;
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

                // check service is TTS
                if (request.hasService()
                        && XMLTypeTools.serviceIsTTS(request.getService()
                                .getValue())) {

                    // get language.
                    String language = getTargetLanguage(request);

                    // check Language(check valid language).
                    if (language.equalsIgnoreCase(TTSProperties.getInstance()
                            .getLanguage1())) {
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
                    // }

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

    private AudioConverter.AudioType getAudioType(String audio)
            throws MCMLException {
        AudioConverter.AudioType audioFormatType = AudioConverter.AudioType.Unexpected;

        if (audio == null) {
            // audio is null
            writeLog("Audio is null.");
            throw new MCMLException("Audio is null.", MCMLException.ERROR,
                    m_ServerType, MCMLException.ABNORMAL_XML_DATA_FORMAT);
        }

        if (audio.equalsIgnoreCase(MCMLStatics.AUDIO_RAW)) {
            writeLog("Audio is Raw.");

            audioFormatType = AudioConverter.AudioType.Raw;
        } else if (audio.equalsIgnoreCase(MCMLStatics.AUDIO_ADPCM)) {
            writeLog("Audio is ADPCM.");

            audioFormatType = AudioConverter.AudioType.ADPCM;
        } else if (audio.equalsIgnoreCase(MCMLStatics.AUDIO_SPEEX)) {
            writeLog("Audio is Speex.");

            audioFormatType = AudioConverter.AudioType.Speex;
        } else {
            // UNEXPECTED String
            writeLog("Audio is Unexpected String.");
            throw new MCMLException("Audio is Unexpected String.",
                    MCMLException.ERROR, m_ServerType,
                    MCMLException.NON_CORESSPONDANCE_PARAM);
        }

        // normal end
        return audioFormatType;
    }

    private boolean getEndian(String endian) throws MCMLException {
        boolean isBigEndian = true;

        if (endian == null) {
            // endian is null
            writeLog("Endian is null.");
            throw new MCMLException("Endian is null.", MCMLException.ERROR,
                    m_ServerType, MCMLException.ABNORMAL_XML_DATA_FORMAT);
        }

        if (endian.equalsIgnoreCase(MCMLStatics.ENDIAN_BIG)) {
            isBigEndian = true;
        } else if (endian.equalsIgnoreCase(MCMLStatics.ENDIAN_LITTLE)) {
            isBigEndian = false;
        } else {
            // UNEXPECTED String
            writeLog("Endian is Unexpected String.");
            throw new MCMLException("Endian is Unexpected String.",
                    MCMLException.ERROR, m_ServerType,
                    MCMLException.NON_CORESSPONDANCE_PARAM);
        }

        // normal end
        return isBigEndian;
    }

    private AudioConverter createAudioConverter(
            AudioConverter.AudioType audioType, boolean isBigEndian) {
        AudioConverter audioConv;

        // AudioType = RAW and BigEndian
        if (audioType == AudioConverter.AudioType.Raw && isBigEndian) {
            audioConv = new AudioConverter(true, false, true, isBigEndian,
                    m_ServerType);

        } else if (audioType == AudioConverter.AudioType.ADPCM) {
            audioConv = new AudioConverter(true, false, false, false,
                    m_ServerType);
        } else if (audioType == AudioConverter.AudioType.Speex) {
            audioConv = new SpeexAudioConverter(true, false, false,
                    isBigEndian, m_ServerType);
        } else {
            // unnecessary Converter
            audioConv = null;
        }
        // normal end
        return audioConv;
    }

    private String getTargetLanguage(TargetOutputType targetOutputType)
            throws MCMLException {
        // get Language.
        String language = "";
        try {
            if (targetOutputType.hasLanguageType()
                    && targetOutputType.getLanguageType().hasID()) {
                language = targetOutputType.getLanguageType().getID()
                        .getValue();
            }
        } catch (Exception e) {
            // MCML is wrong.
            writeLog("MCML/Server/Request/TargetOutput/LanguageType@ID is invalid value.");
            throw new MCMLException(
                    "MCML/Server/Request/TargetOutput/LanguageType@ID is invalid value.",
                    MCMLException.ERROR, m_ServerType,
                    MCMLException.ABNORMAL_XML_DATA_FORMAT);
        }
        return language;
    }

    private String getTargetLanguage(RequestType request) throws MCMLException {
        // get Language.
        String language = "";
        try {
            if (request.hasInput() && request.getInput().hasData()
                    && request.getInput().getData().hasText()
                    && request.getInput().getData().getText().hasModelType()) {
                language = request.getInput().getData().getText()
                        .getModelType().getLanguage().getID().getValue();
            } else if (request.hasTargetOutput()) {
                language = getTargetLanguage(request.getTargetOutput());
            }
        } catch (Exception e) {
            // MCML is wrong.
            writeLog("MCML/Server/Request/Input/Data/ModelType/LanguageType@ID is invalid value.");
            throw new MCMLException(
                    "MCML/Server/Request/Input/Data/ModelType/LanguageType@ID is invalid value.",
                    MCMLException.ERROR, m_ServerType,
                    MCMLException.ABNORMAL_XML_DATA_FORMAT);
        }
        return language;
    }

    private float getRate(float rate) throws MCMLException {
        if (rate < 0.5f || rate > 2.0f) {
            rate = SSMLStatics.SPEAK_RATE;
        }

        // normal end
        return rate;
    }

    private VoiceFontManager.VoiceFont getVoiceID(MCMLType inputMCML,
            InputUserProfileType inputUserProfile) throws MCMLException {
        VoiceFontManager.VoiceFont voiceFont = null;

        try {

            voiceFont = m_VoiceFontManager.getVoiceFontID(inputMCML,
                    inputUserProfile);
        } catch (Exception e) {
            writeLog("failed to get VoiceID.");
        }
        // normal end
        return voiceFont;
    }

    private String getGender(InputUserProfileType inputUserProfile)
            throws MCMLException {
        String val = "";
        try {
            val = inputUserProfile.getGender().getValue();
        } catch (Exception e) {
            writeLog("failed to get MCML/Server/Request/InputUserProfile@Gender.");
            throw new MCMLException(
                    "failed to get MCML/Server/Request/InputUserProfile@Gender.",
                    MCMLException.ERROR, m_ServerType,
                    MCMLException.ABNORMAL_XML_DATA_FORMAT);
        }

        // normal end
        return val;
    }

    private String getAge(InputUserProfileType inputUserProfile)
            throws MCMLException {
        String val;
        try {
            val = inputUserProfile.getAge().toString();
        } catch (Exception e) {
            writeLog("failed to get MCML/Server/Request/InputUserProfile@Age.");
            throw new MCMLException(
                    "failed to get MCML/Server/Request/InputUserProfile@Age.",
                    MCMLException.ERROR, m_ServerType,
                    MCMLException.ABNORMAL_XML_DATA_FORMAT);
        }

        // normal end
        return val;
    }

    private String getSSMLGender(String gender) throws MCMLException {
        String val = "";
        if (gender.equalsIgnoreCase(MCMLStatics.GENDER_MALE)) {
            val = SSMLStatics.GENDER_MALE;
        } else if (gender.equalsIgnoreCase(MCMLStatics.GENDER_FEMALE)) {
            val = SSMLStatics.GENDER_FEMALE;
        } else {
            val = "";
        }

        // normal end
        return val;
    }

    private String getSentence(SentenceSequenceType sentenceSequenceType)
            throws MCMLException {
        String retVal = "";
        TTSProperties prop = TTSProperties.getInstance();

        try {
            int sCount = sentenceSequenceType.getSentenceCount();

            String newDelimiter = prop.getLanguage1Delimiter();

            for (int i = 0; i < sCount; i++) {
                SentenceType s = sentenceSequenceType.getSentenceAt(i);
                if (s.hasSurface()) {
                    // get Data/Text/SentenceSequence/Sentence/Surface
                    SurfaceType2 surfaceType = s.getSurface();
                    String sentenceSurface = surfaceType.getValue().toString();
                    if (!sentenceSurface.isEmpty()) {
                        // get and replace delimiter
                        String delimiter = "";
                        if (surfaceType.hasDelimiter()) {
                            delimiter = surfaceType.getDelimiter().getValue();
                        }
                        if (!delimiter.isEmpty()) {
                            retVal += sentenceSurface.replace(delimiter,
                                    newDelimiter);
                        } else {
                            retVal += sentenceSurface;
                        }
                    } else {
                        // get Data/Text/SentenceSequence/Sentence/Chunk/Surface
                        retVal += getChunk(s, newDelimiter);
                    }
                } else {
                    // get Data/Text/SentenceSequence/Sentence/Chunk/Surface
                    retVal += getChunk(s, newDelimiter);
                }
            }
            // filter sentence
            retVal = m_SsIOFilter.inputFilter(retVal);

            if (retVal.isEmpty()) {
                // no sentence(Data/Text/SentenceSequence/Sentence/Surface
                // and Data/Text/SentenceSequence/Sentence/Chunk/Surface is
                // null.)
                throw new Exception(
                        "no sentence(Data/Text/SentenceSequence/Sentence/Surface "
                                + "and Data/Text/SentenceSequence/Sentence/Chunk/Surface is null.)");
            }
        } catch (Exception e) {
            String message = e.getMessage();
            if (message == null || message.isEmpty()) {
                writeLog("get MCML/Server/Response/Output/Data/Text/SentenceSequence/Sentence/Surface failed.");
                message = "get MCML/Server/Response/Output/Data/Text/SentenceSequence/Sentence/Surface failed.";
            }
            // UNEXPECTED String
            throw new MCMLException(message, MCMLException.ERROR, m_ServerType,
                    MCMLException.ABNORMAL_XML_DATA_FORMAT);
        }

        // normal end
        return retVal;
    }

    private String getChunk(SentenceType s, String delimiter) throws Exception {
        String sentence = "";

        // get Word Count.
        int chunkCount = s.getChunkCount();

        // get Word String.
        for (int i = 0; i < chunkCount; i++) {
            if (s.getChunkAt(i).hasSurface()) {
                String surface = s.getChunkAt(i).getSurface().getValue()
                        .toString();

                // add Word String.
                if (sentence.isEmpty()) {
                    sentence = surface;
                } else {
                    sentence += delimiter + surface;
                }
            }
        }

        return sentence;
    }

    private String createSSML(String language, String locale,
            String voiceFontName, String f0Mean, String gender,
            String sentence, String encoding) throws Exception {
        speakType speakType = null;

        speakType = new speakType();

        speakType.addversion(SSMLStatics.VERSION);
        speakType.addlang(locale);

        if (voiceFontName != null && !voiceFontName.isEmpty()) {
            speakType.addvoice(setVoiceType(locale, voiceFontName, f0Mean,
                    gender, sentence));
        } else {
            speakType.setValue(sentence);
        }

        XMLProcessor xmlProcesser = new XMLProcessor();
        String ssml = xmlProcesser.generate(speakType);

        // normal end
        return ssml;
    }

    private voice setVoiceType(String locale, String voiceFontName,
            String f0Mean, String gender, String sentence) throws Exception {
        voice voiceType = new voice();
        voiceType.addlanguages(locale);
        voiceType.addname(voiceFontName);
        voiceType.addgender(gender);
        voiceType.addf0_mean(f0Mean);
        voiceType.setValue(sentence);

        // normal end
        return voiceType;
    }

    private MCMLType createOutputMCMLType(MCMLType inputMCML,
            RequestType request, TextType inputTextType, String voiceFontName,
            String f0Mean, String gender, String age, String audio,
            String endian, String samplingFrequency, String lang)
            throws Exception {
        // set voiceId to
        // MCML/Server/Response/Output/Data/Text/ModelType/Personality@ID
        String voiceId = "";
        if (!voiceFontName.isEmpty()) {
            voiceId = voiceFontName + "," + f0Mean;
        }

        AudioType outputAudioType = new AudioType();
        ModelTypeType inputModelType = inputTextType.getModelType();
        if (inputModelType != null) {
            outputAudioType.addModelType(inputModelType);
            if (outputAudioType.getModelType().hasPersonality()) {
                outputAudioType.getModelType().getPersonality().removeID();
                if (!voiceId.isEmpty()) {
                    outputAudioType.getModelType().getPersonality()
                            .addID(voiceId);
                }
            } else if (!voiceId.isEmpty() || !gender.isEmpty()
                    || !age.isEmpty()) {
                PersonalityType personalityType = new PersonalityType();
                if (!voiceId.isEmpty()) {
                    personalityType.addID(voiceId);
                }
                if (!gender.isEmpty()) {
                    personalityType.addGender(gender);
                }
                if (!age.isEmpty()) {
                    personalityType.addAge(age);
                }
                outputAudioType.getModelType().addPersonality(personalityType);
            }
        } else {
            if (!voiceId.isEmpty()) {
                ModelTypeType outputModelType = new ModelTypeType();
                PersonalityType personalityType = new PersonalityType();
                personalityType.addID(voiceId);
                outputModelType.addPersonality(personalityType);
                outputAudioType.addModelType(outputModelType);
            }
        }

        SignalType signalType = new SignalType();
        signalType.addAudioFormat(audio);
        signalType.addEndian(endian);
        signalType.addSamplingRate(samplingFrequency);

        TTSProperties prop = TTSProperties.getInstance();
        String bitRate = String.valueOf(prop.getAudioBitRate());
        if (bitRate != null && !bitRate.isEmpty()) {
            signalType.addBitRate(bitRate);
        }
        String channelQty = String.valueOf(prop.getAudioChannelQty());
        if (channelQty != null && !channelQty.isEmpty()) {
            signalType.addChannelQty(channelQty);
        }
        String valueType = String.valueOf(prop.getAudioValueType());
        if (valueType != null && !valueType.isEmpty()) {
            signalType.addValueType(valueType);
        }
        outputAudioType.addSignal(signalType);

        DataType dataType = new DataType();
        dataType.addAudio(outputAudioType);

        AttachedBinaryType attachedBinaryType = new AttachedBinaryType();
        attachedBinaryType.addChannelID(MCMLStatics.BINARY_CHANNEL_ID_AUDIO);
        attachedBinaryType.addDataID(lang);

        OutputType output = new OutputType();
        output.addAttachedBinary(attachedBinaryType);
        output.addData(dataType);

        // set Response
        ResponseType response = new ResponseType();
        response.addService(MCMLStatics.SERVICE_TTS);
        if (request.hasProcessOrder()) {
            response.addProcessOrder(request.getProcessOrder());
        }
        response.addOutput(output);

        // create ServerType
        ServerType server = new ServerType();
        server.addResponse(response);

        MCMLType outputMCML = new MCMLType();

        // check and copy required parameter
        copyMCMLType(inputMCML, outputMCML);
        outputMCML.addServer(server);

        // normal end
        return outputMCML;
    }

    private void incrementConsecutiveNo() {
        synchronized (SYNCHRONIZER) {
            if (m_ConsecutiveNo == Short.MAX_VALUE) {
                m_ConsecutiveNo = 0;
            } else {
                m_ConsecutiveNo++;
            }
        }
    }

    private short getConsecutiveNo() {
        synchronized (SYNCHRONIZER) {
            return m_ConsecutiveNo;
        }
    }

}
