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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import jp.go.nict.mcml.serverap.common.EngineCtrl;
import jp.go.nict.mcml.serverap.common.MCMLData;
import jp.go.nict.mcml.serverap.common.NBestStreamReader;
import jp.go.nict.mcml.servlet.MCMLException;
import jp.go.nict.mcml.servlet.MCMLStatics;
import jp.go.nict.mcml.servlet.XMLTypeTools;
import jp.go.nict.mcml.xml.types.ChunkType;
import jp.go.nict.mcml.xml.types.DataType;
import jp.go.nict.mcml.xml.types.LanguageType;
import jp.go.nict.mcml.xml.types.MCMLType;
import jp.go.nict.mcml.xml.types.ModelTypeType;
import jp.go.nict.mcml.xml.types.OutputType;
import jp.go.nict.mcml.xml.types.RequestType;
import jp.go.nict.mcml.xml.types.ResponseType;
import jp.go.nict.mcml.xml.types.SentenceSequenceType;
import jp.go.nict.mcml.xml.types.SentenceType;
import jp.go.nict.mcml.xml.types.ServerType;
import jp.go.nict.mcml.xml.types.SurfaceType2;
import jp.go.nict.mcml.xml.types.TargetOutputType;
import jp.go.nict.mcml.xml.types.TextType;

/**
 * MTEngineCtrl class.
 */
public class MTEngineCtrl extends EngineCtrl {
    // ------------------------------------------
    // private member constant
    // ------------------------------------------
    private static final String DIRECTION_COMMAND = "CON::DIRECTION=";
    private static final String SRC_TEXT_COMMAND = "CON::SRC_TEXT=";
    private static final String SRC_MOR_LIST_COMMAND = "CON::SRC_MOR_LIST=";
    private static final String MT_RESULT_START = "CON::TIME=";
    private static final String MT_RESULT_END = "CON::END";
    private static final String DELIMITER_WORD_SPLITTER = "/";
    private static final String DELIMITER_MOR_SPLITTER = "|";

    private static final String ENGINE_CLOSED_ERRORCODE = "E-20020110";

    // ------------------------------------------
    // private member variable(instance field)
    // ------------------------------------------
    private Process m_MTstartup;
    private Process m_MTcontroller;
    private NBestStreamReader m_NBestReader;
    private BufferedOutputStream m_TranslaterInput;
    private MTLogger m_StarupLogger;
    private ControllerLogger m_ControllerLogger;

    private String m_TranslateDirection;
    private String m_EngineName;
    HashMap<String, MtIOFilter> m_MtIOFilter;
    private boolean m_InputWordID;
    private boolean m_localInputWordID;
    private MTLanguageInfoManager m_LangInfoManager;
    private String m_SourceLanguage;
    private String m_TargetLanguage;
    private MTLanguageInfo m_SourceLanguageInfo;
    private MTLanguageInfo m_TargetLanguageInfo;

    // ------------------------------------------
    // public member function
    // ------------------------------------------
    /**
     * Default constructor
     */
    public MTEngineCtrl() {
        super(MCMLException.MT);
        m_MTstartup = null;
        m_MTcontroller = null;
        m_NBestReader = null;
        m_TranslaterInput = null;
        m_StarupLogger = null;
        m_ControllerLogger = null;
        m_TranslateDirection = "";
        m_EngineName = "";
        m_MtIOFilter = null;
        m_InputWordID = false;
        m_localInputWordID = m_InputWordID;
        m_LangInfoManager = null;
        m_SourceLanguage = "";
        m_TargetLanguage = "";
        m_SourceLanguageInfo = null;
        m_TargetLanguageInfo = null;
    }

    /**
     * Initialization
     *
     * @param engineNo
     * @throws IOException
     */
    public void initialize(int engineNo) throws IOException {
        // get MT's Parameter Manager class
        MTProperties properties = MTProperties.getInstance();
        MTParam param = properties.getMTParam(engineNo);

        // get LanguageIngoManager
        m_LangInfoManager = properties.getLanguageInfoManager();

        // MT startup start.
        m_MTstartup = Runtime.getRuntime().exec(param.getStartUpCommand());

        // MT controller start.
        m_MTcontroller = Runtime.getRuntime()
                .exec(param.getControllerCommand());

        String languages = "";

        for (int i = 0; i < m_LangInfoManager.getLanguageList().size(); i++) {

            languages += m_LangInfoManager.getLanguageList().get(i);
        }

        // create EngineName.
        m_EngineName = "mt_" + languages + String.valueOf(engineNo + 1);
        // set Logger of MT(startup) Standard Output and Standard Error.
        m_StarupLogger = new MTLogger(m_MTstartup.getInputStream(),
                m_MTstartup.getErrorStream(), "US-ASCII", m_EngineName);

        // Logger Start
        m_StarupLogger.start();

        // get Standard Input by controller.
        m_NBestReader = new NBestStreamReader(m_MTcontroller.getInputStream());

        // get Standard Output by controller.
        m_TranslaterInput = new BufferedOutputStream(
                m_MTcontroller.getOutputStream());

        m_ControllerLogger = new ControllerLogger(
                m_MTcontroller.getErrorStream(), "US-ASCII", m_EngineName);
        m_ControllerLogger.start();

        // create I/O filter
        m_MtIOFilter = new HashMap<String, MtIOFilter>();

        for (int i = 0; i < m_LangInfoManager.getLanguageList().size(); i++) {

            String languageName = "";
            String textfilterfileName = "";

            languageName = m_LangInfoManager.getLanguageList().get(i);

            if (languageName != null && !languageName.isEmpty()) {

                textfilterfileName = m_LangInfoManager.getLanguageInfo(
                        languageName).getTextfilterfile();

                m_MtIOFilter.put(languageName, new MtIOFilter(
                        textfilterfileName));
            }
        }

        /* 2009/08/20 start */
        m_InputWordID = properties.isInputWordID();
        /* 2009/08/20 end */
    }

    // ------------------------------------------
    // protected member function(implementation)
    // ------------------------------------------
    // MTRequest process
    @Override
    protected Date processRequest(MCMLData data) throws Exception {
        try {

            // get inputMCML
            MCMLType inputMCML = data.getMCMLType();

            // check service and check language and get Request
            RequestType request = getRequest(inputMCML);

            // get Translate Language
            TargetOutputType targetOutputType = request.getTargetOutput();

            String srcLanguage = getSrcLanguage(inputMCML);
            String targetLanguage = getTargetLanguage(targetOutputType);

            // get LanguageInformation of srcLanguage and trgLanguage
            getLanguageInformation(srcLanguage, targetLanguage);

            // set Translate Direction.
            setTranslateDirection(m_TargetLanguage);

            // get SourceString for Translate.
            TextType inputTextType = XMLTypeTools.getTextType(request);
            if (inputTextType == null) {
                // MCML is wrong.
                writeLog("MCML/Server/Request/Input/Data/Text is null.");
                throw new MCMLException(
                        "MCML/Server/Request/Input/Data/Text is null.",
                        MCMLException.ERROR, m_ServerType,
                        MCMLException.ABNORMAL_XML_DATA_FORMAT);
            }
            String sentence = getSentence(inputTextType);

            // filter sentence

            Iterator<String> it = m_MtIOFilter.keySet().iterator();
            while (it.hasNext()) {
                String key = (it.next()).toString();
                if (srcLanguage.equalsIgnoreCase(key)) {
                    sentence = m_MtIOFilter.get(key).inputFilter(sentence,
                            m_localInputWordID)
                            + "\n";
                    break;
                }
            }

            writeLog(m_EngineName + "input sentence. :" + sentence);

            String sourceString;
            if (!m_localInputWordID) {
                sourceString = SRC_TEXT_COMMAND + sentence;
            } else {
                sourceString = SRC_MOR_LIST_COMMAND + sentence;
            }

            // input SourceString for Translate.
            String delimiter = writeTranslateString(m_SourceLanguage,
                    sourceString);

            // read Translate result.
            String result = readTranslateResult();

            Date responceReceiveTime = new Date();

            writeLog(m_EngineName + "read sentence. :" + result);

            // parse NBestResult.
            MTNBestProcessor nbestProc = new MTNBestProcessor();
            delimiter = m_TargetLanguageInfo.getDelimiter();
            ArrayList<SentenceSequenceType> outputSentenceSequenceTypes = nbestProc
                    .parseNBestResult(result, delimiter);

            // get ModelType
            ModelTypeType inputModelType = inputTextType.hasModelType() ? inputTextType
                    .getModelType() : null;
            if (inputModelType == null) {
                throw new MCMLException(
                        "no ModelType(MCML/Server/Request/Input/Data/Text/ModelType).",
                        MCMLException.ERROR, m_ServerType,
                        MCMLException.ABNORMAL_XML_DATA_FORMAT);
            }

            // create outputMCML
            MCMLType outputMCML = createOutputMCMLType(inputMCML, request,
                    inputModelType, targetOutputType, targetLanguage,
                    outputSentenceSequenceTypes);

            // clear SpeechData.
            data.clearBinaryData();

            // set Result MCML(XML)
            data.setMCMLType(outputMCML);

            Date responceSendTime = new Date();
            writeLog("[ProcessTime]ReceiveResponse->SendResponse: "
                    + (responceSendTime.getTime() - responceReceiveTime
                            .getTime()) + "msec ");
        } catch (MCMLException exp) {
            // ErrorMessage set.
            MCMLType errMCML = doError(exp);
            data.setMCMLType(errMCML);
        }
        return null;
    }

    // terminate
    @Override
    protected void processTermination() throws Exception {
        // MT Logger terminate.
        m_StarupLogger.terminate();
        m_ControllerLogger.terminate();

        // MT Terminate
        m_MTcontroller.destroy();
        m_MTstartup.destroy();

        super.processTermination();

        // normal end
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

                // check service is MT
                if (request.hasService()
                        && XMLTypeTools.serviceIsMT(request.getService()
                                .getValue())) {

                    if (request.hasInputUserProfile()
                            && request.hasTargetOutput()) {

                        String srcLanguage = getSrcLanguage(inputMCML);

                        // get TargetLanguage.
                        String targetLanguage = getTargetLanguage(request
                                .getTargetOutput());

                        // check Language(check valid language).
                        if (m_LangInfoManager.isValidLanguage(srcLanguage)
                                && m_LangInfoManager
                                        .isValidLanguage(targetLanguage)) {

                            if (validRequest != null) {
                                // "Two request have the same language.".
                                writeLog("Two request have the same language.");
                                throw new Exception(
                                        "Two request have the same language.");
                            }

                            validRequest = request;
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

    private void getLanguageInformation(String srcLanguage, String trgLanguage)
            throws MCMLException {
        // if it is first request or Source language is change , get language
        // information
        if (!m_SourceLanguage.equalsIgnoreCase(srcLanguage)
                || m_SourceLanguageInfo == null) {

            m_SourceLanguage = srcLanguage;
            m_SourceLanguageInfo = m_LangInfoManager
                    .getLanguageInfo(m_SourceLanguage);

            if (m_SourceLanguageInfo == null) {
                // not supported language
                writeLog("MCML/Server/Request/InputUserProfile/InputModality/Speaking/Language@ID is not supported language.");
                throw new MCMLException(
                        "MCML/Server/Request/InputUserProfile/InputModality/Speaking/Language@ID is not supported language.",
                        MCMLException.ERROR, m_ServerType,
                        MCMLException.NOT_SUPPORT_LANGUAGE);
            }
        }

        // if it is first request or Target language is change , get language
        // information
        if (!m_TargetLanguage.equalsIgnoreCase(trgLanguage)
                || m_TargetLanguageInfo == null) {

            m_TargetLanguage = trgLanguage;
            m_TargetLanguageInfo = m_LangInfoManager
                    .getLanguageInfo(m_TargetLanguage);

            if (m_TargetLanguageInfo == null) {
                // not supported language
                writeLog("MCML/Server/Request/TargetOutput/LanguageType@ID is not supported language.");
                throw new MCMLException(
                        "MCML/Server/Request/TargetOutput/LanguageType@ID is not supported language.",
                        MCMLException.ERROR, m_ServerType,
                        MCMLException.NOT_SUPPORT_LANGUAGE);
            }
        }
    }

    private void sendTranslateDirection(String translateDirection)
            throws IOException {
        if (!m_TranslateDirection.equalsIgnoreCase(translateDirection)) {
            // set Translate Direction to MT.
            m_TranslateDirection = translateDirection;
            String sendCommand = DIRECTION_COMMAND + m_TranslateDirection
                    + MCMLStatics.RETURN_CODE;
            m_TranslaterInput.write(sendCommand.getBytes());
            m_TranslaterInput.flush();
        }
    }

    private void setTranslateDirection(String trgLanguage) throws MCMLException {

        String translateDirection = "";

        try {
            // get direction from language info
            translateDirection = m_SourceLanguageInfo
                    .getDirectionCommand(trgLanguage);

            // send TranslateDirection.
            if (translateDirection == null || translateDirection.isEmpty()) {
                // not supported language.
                writeLog("Not supported language. : " + trgLanguage);
                throw new MCMLException("Not supported language.",
                        MCMLException.ERROR, m_ServerType,
                        MCMLException.NOT_SUPPORT_LANGUAGE);
            }
            sendTranslateDirection(translateDirection);
        } catch (IOException e) {
            // Translate Direction set failed.
            writeLog("Translate Direction set failed.");
            throw new MCMLException("Translate Direction set failed.",
                    MCMLException.ERROR, m_ServerType,
                    MCMLException.OTHER_ERROR);
        }
    }

    /* 2009/08/20 start */
    private boolean checkWordIDs(SentenceSequenceType sentenceSequenceType,
            String newDelimiter) throws MCMLException {
        try {
            int checkedWordCount = 0;
            int sCount = sentenceSequenceType.getSentenceCount();
            for (int i = 0; i < sCount; i++) {
                SentenceType sentenceType = sentenceSequenceType
                        .getSentenceAt(i);
                int wordCnt = sentenceType.getChunkCount();

                for (int j = 0; j < wordCnt; j++) {
                    ChunkType chunkType = sentenceType.getChunkAt(j);
                    String wordID = chunkType.hasSurface() ? chunkType
                            .getSurface().getValue().toString() : "";
                    String sDelim = "\\" + DELIMITER_MOR_SPLITTER;
                    String[] parts = wordID.split(sDelim, -1);
                    if (parts.length != 8 && parts.length != 9) {
                        return false;
                    }
                    checkedWordCount++;
                }
            }

            if (checkedWordCount == 0) {
                return false;
            }
        } catch (Exception e) {
            String message = e.getMessage();
            if (message == null || message.isEmpty()) {
                writeLog("get Data/Text/SentenceSequence/Sentence/Chunk/Surface failed.");
                message = "Data/Text/SentenceSequence/Sentence/Chunk/Surface failed.";
            }
            // UNEXPECTED String
            throw new MCMLException(message, MCMLException.ERROR, m_ServerType,
                    MCMLException.ABNORMAL_XML_DATA_FORMAT);
        }

        return true;
    }

    private String convertNewWordID(String orgWordID) {
        String newWordIDs = "";
        String[] words = orgWordID.split(DELIMITER_WORD_SPLITTER, -1);
        for (int j = 0; j < words.length; j++) {
            if (j > 0) {
                newWordIDs += DELIMITER_WORD_SPLITTER;
            }
            String sDelim = "\\" + DELIMITER_MOR_SPLITTER;
            String[] parts = words[j].split(sDelim, -1);
            String newWords = "";
            if (parts.length == 8) {
                newWords = parts[0];
                for (int i = 0; i < parts.length; i++) {
                    newWords += DELIMITER_MOR_SPLITTER;
                    newWords += parts[i];
                }
            } else {
                newWords = words[j];
            }
            newWordIDs += newWords;
        }

        return newWordIDs;
    }

    private String getSentence(TextType inputTextType) throws MCMLException {
        String outSentence = "";

        try {
            SentenceSequenceType sentenceSequenceType = inputTextType
                    .hasSentenceSequence() ? inputTextType
                    .getSentenceSequence() : null;
            if (sentenceSequenceType == null) {
                throw new Exception(
                        "no sentence(MCML/Server/Request/Input/Data/Text/SentenceSequence is null.)");
            }

            // check wordid
            String newDelimiter = m_SourceLanguageInfo.getDelimiter();
            m_localInputWordID = m_InputWordID;
            if (m_localInputWordID) {
                m_localInputWordID = checkWordIDs(sentenceSequenceType,
                        newDelimiter);
            }

            int sCount = sentenceSequenceType.getSentenceCount();
            for (int i = 0; i < sCount; i++) {
                SentenceType sentenceType = sentenceSequenceType
                        .getSentenceAt(i);
                String appendSentence = "";

                if (!m_localInputWordID) {
                    if (sentenceType.hasSurface()) {
                        // get Data/Text/SentenceSequence/Sentence/Surface
                        SurfaceType2 surfaceType = sentenceType.getSurface();
                        String sentenceSurface = surfaceType.getValue()
                                .toString();
                        if (!sentenceSurface.isEmpty()) {
                            // get and replace delimiter
                            String delimiter = surfaceType.hasDelimiter() ? surfaceType
                                    .getDelimiter().getValue() : "";

                            if (!delimiter.isEmpty()) {
                                appendSentence = sentenceSurface.replace(
                                        delimiter, newDelimiter);
                            } else {
                                appendSentence = sentenceSurface;
                            }
                        }
                    } else {
                        // get Data/Text/SentenceSequence/Sentence/Chunk/Surface
                        appendSentence = getWord(sentenceType, newDelimiter);
                    }
                    // merge sentence
                    outSentence += appendSentence;
                } else {
                    if (i > 0) {
                        outSentence += "/";
                    }
                    outSentence += convertNewWordID(getWord(sentenceType,
                            DELIMITER_WORD_SPLITTER));
                }
            }
            if (outSentence.isEmpty()) {
                // no sentence(Data/Text/SentenceSequence/Sentence/Surface
                // and Data/Text/SentenceSequence/Sentence/Chunk/Surface is
                // null.)
                throw new Exception(
                        "no sentence(Data/Text/SentenceSequence/Sentence/Surface "
                                + "and Data/Text/SentenceSequence/Sentence/Chunk/Surface is null.)");
            }
            outSentence += "\n";
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
        return outSentence;
    }

    private String writeTranslateString(String srcLangage, String srcString)
            throws MCMLException {
        String stringcode = "";
        String delimiter = "";

        try {

            delimiter = m_SourceLanguageInfo.getDelimiter();
            stringcode = m_SourceLanguageInfo.getStringcode();

            // input SourceString
            m_TranslaterInput.write(srcString.getBytes(stringcode));

            m_TranslaterInput.flush();
        } catch (UnsupportedEncodingException e) {
            // input SourceString failed.
            writeLog("Unsupported StringCode.");
            throw new MCMLException("Unsupported StringCode.",
                    MCMLException.ERROR, m_ServerType,
                    MCMLException.OTHER_ERROR);
        } catch (IOException e) {
            // input SourceString failed.
            writeLog("input SourceString failed.");
            throw new MCMLException("input SourceString failed.",
                    MCMLException.ERROR, m_ServerType,
                    MCMLException.ENGINE_CLOSED);
        }
        return delimiter;
    }

    private String readTranslateResult() throws MCMLException {
        try {

            // initialize NBestReader.
            String stringcode = m_TargetLanguageInfo.getStringcode();
            m_NBestReader.init(MT_RESULT_START, MT_RESULT_END, stringcode,
                    m_EngineName, MTProperties.getInstance()
                            .getNullResponseSleepMSec(), MTProperties
                            .getInstance().getNullResponseCounter());

            // recieve Translate Result.
            return m_NBestReader.read();
        } catch (UnsupportedEncodingException e) {
            // input SourceString failed.
            writeLog("Unsupported StringCode.");
            throw new MCMLException("Unsupported StringCode.",
                    MCMLException.ERROR, m_ServerType,
                    MCMLException.OTHER_ERROR);
        } catch (IOException e) {
            // read TranslateResult failed.
            writeLog("read TranslateResult failed.");
            throw new MCMLException("read TranslateResult failed.",
                    MCMLException.ERROR, m_ServerType,
                    MCMLException.ENGINE_CLOSED);
        } catch (InterruptedException e) {
            // read TranslateResult failed.
            writeLog("read TranslateResult failed.");
            throw new MCMLException("read TranslateResult failed.",
                    MCMLException.ERROR, m_ServerType,
                    MCMLException.ENGINE_CLOSED);
        }
    }

    private String getWord(SentenceType sentenceType, String delimiter)
            throws Exception {
        String sentence = "";

        // get Word Count.
        int chunkCount = sentenceType.getChunkCount();

        // get Word String.
        for (int i = 0; i < chunkCount; i++) {
            if (sentenceType.getChunkAt(i).hasSurface()) {
                String surface = sentenceType.getChunkAt(i).getSurface()
                        .getValue().toString();

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

    private int getMaxNBest(TargetOutputType targetOutput) throws Exception {
        // MaxNBest's Default Value is 1.
        int maxNBest = 1;
        try {
            if (targetOutput != null && targetOutput.hasHypothesisFormat()
                    && targetOutput.getHypothesisFormat().hasNofN_best()) {
                maxNBest = targetOutput.getHypothesisFormat().getNofN_best()
                        .intValue();
            } else {
                writeLog("MCML/Server/Request/TargetOutput/HypothesisFormat@NofN_best is null.");
                System.out
                        .println("MCML/Server/Request/TargetOutput/HypothesisFormat@NofN_best is null.");
            }
            if (maxNBest < 1) {
                writeLog("MCML/Server/Request/TargetOutput/HypothesisFormat@NofN_best is Negative Integer or 0.");
                System.out
                        .println("MCML/Server/Request/TargetOutput/HypothesisFormat@NofN_best is Negative Integer or 0.");
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

    private String getSrcLanguage(MCMLType inputMCML) throws MCMLException {
        // get Language.
        String language = "";
        try {

            language = XMLTypeTools
                    .getInputDataTextModelTypeLanguageType(inputMCML);

        } catch (Exception e) {
            // MCML is wrong.
            writeLog("MCML/Server/Request/InputUserProfile/InputModality/Speaking/Language@ID is invalid value.");
            writeLog("MCML/Server/Request/InputUserProfile/InputModality/Speaking/Language@ID is invalid value.");
            throw new MCMLException(
                    "MCML/Server/Request/InputUserProfile/InputModality/Speaking/Language@ID is invalid value.",
                    MCMLException.ERROR, m_ServerType,
                    MCMLException.ABNORMAL_XML_DATA_FORMAT);
        }

        return language;
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

    private MCMLType createOutputMCMLType(MCMLType inputMCML,
            RequestType request, ModelTypeType inputModelType,
            TargetOutputType targetOutputType, String targetLanguage,
            ArrayList<SentenceSequenceType> outputSentenceSequenceTypes)
            throws Exception {
        // set output TextType
        TextType oytputTextType = new TextType();
        oytputTextType.addChannelID(MCMLStatics.MODEL_CHANNEL_ID_TEXT);
        ModelTypeType outputModelType = createModelType(inputModelType,
                targetLanguage);
        oytputTextType.addModelType(outputModelType);

        // set sentenceSequenceType to Response.
        int maxNBest = getMaxNBest(targetOutputType);
        // outputSentenceSequenceTypes null is already checked
        if (outputSentenceSequenceTypes.size() < maxNBest) {
            maxNBest = outputSentenceSequenceTypes.size();
        }
        for (int i = 0; i < maxNBest; i++) {
            oytputTextType.addSentenceSequence(outputSentenceSequenceTypes
                    .get(i));
        }

        DataType dataType = new DataType();
        dataType.addText(oytputTextType);
        OutputType output = new OutputType();
        output.addData(dataType);

        // create Response
        ResponseType response = new ResponseType();
        response.addService(MCMLStatics.SERVICE_MT);
        response.addOutput(output);
        if (request.hasProcessOrder()) {
            response.addProcessOrder(request.getProcessOrder());
        }

        // create ServerType
        ServerType server = new ServerType();
        server.addResponse(response);

        // check and copy required parameter
        MCMLType outputMCML = new MCMLType();
        copyMCMLType(inputMCML, outputMCML);
        outputMCML.addServer(server);

        // normal end
        return outputMCML;
    }

    private ModelTypeType createModelType(ModelTypeType inputModelType,
            String targetLanguage) throws Exception {
        ModelTypeType outputModelType = new ModelTypeType(inputModelType);

        // Language is required(Language@ID is required)
        outputModelType.removeLanguage();
        LanguageType languageType = new LanguageType();
        languageType.addID(targetLanguage);
        outputModelType.addLanguage(languageType);

        // succeeded
        return outputModelType;
    }
}
