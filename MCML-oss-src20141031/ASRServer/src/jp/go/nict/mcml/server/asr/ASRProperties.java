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

import java.util.ArrayList;

import jp.go.nict.mcml.serverap.common.ServerApProperties;
import jp.go.nict.mcml.servlet.MCMLStatics;

/**
 * ASRProperties property class.
 *
 *
 */
class ASRProperties extends ServerApProperties {
    // ------------------------------------------
    // private member constant
    // ------------------------------------------
    private static final String KEY_ENGINE_ASR = KEY_ENGINE + "asr";
    private static final String KEY_HOST = "host";
    private static final String KEY_SPEECHINPUTPORT = "speechinputport";
    private static final String KEY_NBESTRECEIVEPORT = "nbestreceiveport";
    private static final String KEY_CLIENTHOST = "clienthost";
    private static final String KEY_CLIENTSPEECHINPUTPORT = "clientspeechinputport";
    private static final String KEY_CLIENTNBESTRECEIVEPORT = "clientnbestreceiveport";
    private static final String KEY_RPCCOMMANDSENDER = "rpccommandsender";
    private static final String KEY_RPCNUMBER = "rpcnumber";
    private static final String KEY_FRAMESYNCSZE = "framesyncsize";
    private static final String KEY_COMMANDTABLEFILENAME = "commandtablefilename";
    private static final String KEY_ACOUSTICMODEL = "acousticmodel";
    private static final String KEY_LANGUAGEMODEL = "languagemodel";
    private static final String KEY_VOICEFONTSELECTOR = "voicefontselector";
    private static final String KEY_TIMEOUT = "timeout";
    private static final String KEY_PORT = "port";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_NBEST = "nbest";
    private static final String KEY_PARTOFSPEECH = "partofspeech";
    private static final String KEY_RECOGNITIONRESULT = "recognitionresult.";
    private static final String KEY_MODELFILENAME = "modelfilename";
    private static final String KEY_UTF8 = "utf8";
    private static final String KEY_EUC = "euc";
    private static final String KEY_INPUTSPEECHDATA = "inputspeechdata";
    private static final String KEY_GWPP = "gwpp";
    private static final String KEY_SRTIMEOUT = "srtimeout";
    private static final String KEY_TIME = "time";
    private static final String KEY_RATIO = "ratio";
    private static final String KEY_ABORT = KEY_ENGINE + "abort";
    private static final String KEY_ABORTCOMMANDLIST = "commandlist";
    private static final String KEY_RECEIVETIMEOUT = "receivetimeout";
    private static final String KEY_MODELRECEIVEPORT = "modelreceiveport";
    private static final String KEY_MODELOUTPUTPATH = "modeloutputpath";
    private static final String KEY_RPCRECEIVEMODULE = "rpcreceivemodule";
    private static final String KEY_ENGINE_NULLRESPONSE_SLEEPMSEC = KEY_ENGINE
            + "nullresponse.sleepmsec";
    private static final String KEY_ENGINE_NULLRESPONSE_COUNTER = KEY_ENGINE
            + "nullresponse.counter";
    private static final String KEY_SEND_ONE_WAV = "sendonewav";
    private static final String KEY_PARSETYPE = "parsetype";
    private static final String KEY_RESULTCONVERTER = "resultconverter";

    // ------------------------------------------
    // protected member variable(class field)
    // ------------------------------------------
    protected static final ASRProperties M_INSTANCE = new ASRProperties();

    // ------------------------------------------
    // private member variable(instance field)
    // ------------------------------------------
    private ArrayList<ASRParam> m_ASRParamList;
    private String m_RPCCommandSender;
    private boolean m_IsSendOneWav;
    private boolean m_IsModelNameOutputOn;
    private boolean m_IsUtf8OutputOn;
    private boolean m_IsEucOutputOn;
    private boolean m_IsInputSpeechdataOutputOn;
    private boolean m_WordTagPartOfSpeech = false;
    private long m_TimeOutTime;
    private long m_TimeOutRatio;
    private boolean m_IsAbortOn;
    private String m_AbortCommandListFileName;
    private String m_ModelOutputPath;
    private String m_RPCReceiveModule;
    private int m_ModelReceivePort;
    private long m_ReceiveTimeout;
    private String m_NbestParseType;
    private String m_ResultConverter;
    private String m_VoiceFontSelectorHost;
    private int m_VoiceFontSelectorPort;
    private long m_VoiceFontSelectorTimeout;
    private String m_VoiceFontSelectorLanguage;
    private int m_NullResponseSleepMSec = 10;
    private int m_NullResponseeCounter = 10;

    // ------------------------------------------
    // public member function
    // ------------------------------------------
    public static ASRProperties getInstance() {
        return M_INSTANCE;
    }

    @Override
    public void readProperties(String fileName) throws Exception {
        super.readProperties(fileName);

        // check read parameter.
        checkParameter();

        String temp = readRPCCommandSender(KEY_ENGINE);
        if (temp == null) {
            // no send RPC Command.
            System.out.println("Invalid Parameter: " + KEY_ENGINE
                    + KEY_RPCCOMMANDSENDER);
        }
        m_RPCCommandSender = temp;

        System.out.println(KEY_ENGINE + KEY_SEND_ONE_WAV);
        m_IsSendOneWav = readFileOutputOnOff(KEY_ENGINE + KEY_SEND_ONE_WAV);

        m_RPCReceiveModule = m_Properties.getProperty(KEY_ENGINE_ASR + "."
                + KEY_SEND_ONE_WAV);
        if (m_RPCReceiveModule == null || m_RPCReceiveModule.isEmpty()) {
            System.out.println("Invalid Parameter: " + KEY_ENGINE_ASR + "."
                    + KEY_SEND_ONE_WAV);
            m_RPCReceiveModule = "";
        }

        // read abort function on/off switch.
        readAbortOnOff();

        // read abort command list file name.
        temp = readAbortCommandListFileName(KEY_ABORT + ".");
        if (temp == null) {
            // no send RPC Command.
            System.out.println("Invalid Parameter: " + KEY_ABORT + "."
                    + KEY_ABORTCOMMANDLIST);
        }
        m_AbortCommandListFileName = temp;

        m_WordTagPartOfSpeech = readPartOfSpeech(KEY_ENGINE + KEY_NBEST + "."
                + KEY_PARTOFSPEECH);

        temp = readNbestParseType(KEY_ENGINE + KEY_NBEST + "." + KEY_PARSETYPE);
        if (temp == null) {
            m_NbestParseType = "";
        } else {
            m_NbestParseType = temp;
        }

        temp = readResultConverter(KEY_ENGINE + KEY_NBEST + "."
                + KEY_RESULTCONVERTER);
        if (temp == null) {
            m_ResultConverter = "";
        } else {
            m_ResultConverter = temp;
        }

        // Acoustic and Language Model File Names out put on/off switch.
        m_IsModelNameOutputOn = readFileOutputOnOff(KEY_ENGINE_CORPUSLOG_UTTERANCEINFO
                + "." + KEY_MODELFILENAME);

        // utf8 result text file on/off switch
        m_IsUtf8OutputOn = readFileOutputOnOff(KEY_ENGINE_CORPUSLOG
                + KEY_RECOGNITIONRESULT + KEY_UTF8);

        // euc result text file on/off switch
        m_IsEucOutputOn = readFileOutputOnOff(KEY_ENGINE_CORPUSLOG
                + KEY_RECOGNITIONRESULT + KEY_EUC);

        // input speech data on/off file switch
        m_IsInputSpeechdataOutputOn = readFileOutputOnOff(KEY_ENGINE_CORPUSLOG
                + KEY_INPUTSPEECHDATA);

        // read SR Timeout parameter.
        readSRTimeoutParameter();

        readReceiveTimeout();

        // read ModelManager's parameter
        readModelManagerParameter();

        // read AcousticModel Change Command Table FileName.
        String amFileName = readAMCommandTableFileName(KEY_ENGINE_ASR);
        if (amFileName == null) {
            // read Next Engine Parameters.
            System.out.println("Invalid Parameter: " + KEY_ENGINE_ASR + "."
                    + KEY_COMMANDTABLEFILENAME + "." + KEY_ACOUSTICMODEL);
        }

        // read LanguageModel Chande Command Table FileName.
        String lmFileName = readLMCommandTableFileName(KEY_ENGINE_ASR);
        if (lmFileName == null) {
            // read Next Engine Parameters.
            System.out.println("Invalid Parameter: " + KEY_ENGINE_ASR + "."
                    + KEY_COMMANDTABLEFILENAME + "." + KEY_LANGUAGEMODEL);
        }

        readNullResponseParameter();

        // read VoiceFontSelector's parameter
        readVoiceFontSelectorsParameter(KEY_ENGINE_ASR);

        // Roop by Max Engine number
        for (int i = 1; i <= m_EngineMaxNumber; i++) {
            // Create engine.asrX section name.
            String paramSection = KEY_ENGINE_ASR + String.valueOf(i);

            // Parameter Manager Class Create
            ASRParam params = new ASRParam();

            // Engine IPAddress
            temp = readHost(paramSection);
            if (temp == null) {
                // read Next Engine Parameters.
                System.out.println("Invalid Parameter: " + paramSection + "."
                        + KEY_HOST);
                continue;
            }
            params.setEngineHost(temp);

            // NBestReceive Port from Engine
            int nbestReceivePort = readNbestReceivePort(paramSection);
            if (nbestReceivePort < 0 || PORT_NUMBER_MAX < nbestReceivePort) {
                // read Next Engine Parameters.
                System.out.println("Invalid Parameter: " + paramSection + "."
                        + KEY_NBESTRECEIVEPORT);
                continue;
            }
            params.setNBestReceivePort(nbestReceivePort);

            // SpeechInputPort for Engine
            int speechInputPort = readSpeechInputPort(paramSection);
            if (speechInputPort < 0 || PORT_NUMBER_MAX < speechInputPort) {
                // read Next Engine Parameters.
                System.out.println("Invalid Parameter: " + paramSection + "."
                        + KEY_SPEECHINPUTPORT);
                continue;
            }
            params.setSpeechInputPort(speechInputPort);

            // Client Engine IPAddress
            temp = readClientHost(paramSection);
            if (temp == null) {
                temp = "";
            }
            params.setClientEngineHost(temp);

            // Client NBestReceive Port from Engine
            int clientNbestReceivePort = readClientNbestReceivePort(paramSection);
            if (PORT_NUMBER_MAX < clientNbestReceivePort) {
                // read Next Engine Parameters.
                System.out.println("Invalid Parameter: " + paramSection + "."
                        + KEY_CLIENTNBESTRECEIVEPORT);
                continue;
            }
            params.setClientNBestReceivePort(clientNbestReceivePort);

            // Client SpeechInputPort for Engine
            int clientSpeechInputPort = readClientSpeechInputPort(paramSection);
            if (PORT_NUMBER_MAX < clientSpeechInputPort) {
                // read Next Engine Parameters.
                System.out.println("Invalid Parameter: " + paramSection + "."
                        + KEY_CLIENTSPEECHINPUTPORT);
                continue;
            }
            params.setClientSpeechInputPort(clientSpeechInputPort);

            // RPC Command No(port) for Engine
            int rpcNumber = readRPCNumber(paramSection);
            if (rpcNumber < 0 || PORT_NUMBER_MAX < rpcNumber) {
                // read Next Engine Parameters.
                System.out.println("Invalid Parameter: " + paramSection + "."
                        + KEY_RPCNUMBER);
                continue;
            }
            params.setRpcNumber(rpcNumber);

            // set AcousticModel Change Command Table FileName.
            params.setAMCommandTableFileName(amFileName);

            // set LanguageModel Chande Command Table FileName.
            params.setLMCommandTableFileName(lmFileName);

            // FrameSync Data Size for Engine
            int frameSyncSize = readFrameSyncSize(paramSection);
            if (frameSyncSize < 0) {
                // read Next Engine Parameters.
                System.out.println("Invalid Parameter: " + paramSection + "."
                        + KEY_FRAMESYNCSZE);
                continue;
            }
            params.setFrameSyncSize(frameSyncSize);

            // GWPP On/Off.
            params.setIsGwppUsed(readOnOff(paramSection + "." + KEY_GWPP));

            // Set ASR Prameter Manager List.
            m_ASRParamList.add(params);
        }
        // normal end
        return;
    }

    // get Engine(ASR) Parameter Manager class
    public ASRParam getASRParam(int engineNo) {
        return m_ASRParamList.get(engineNo);
    }

    @Override
    public int getEngineNumber() {
        return m_ASRParamList.size();
    }

    public String getRPCCommendSender() {
        return m_RPCCommandSender;
    }

    public boolean getWordTagPartOfSpeech() {
        return m_WordTagPartOfSpeech;
    }

    public boolean isUtf8OutputOn() {
        return m_IsUtf8OutputOn;
    }

    public boolean isEucOutputOn() {
        return m_IsEucOutputOn;
    }

    public boolean isInputSpeechdataOutputOn() {
        return m_IsInputSpeechdataOutputOn;
    }

    public boolean isSendOneWav() {
        return m_IsSendOneWav;
    }

    public boolean isModelNameOutputOn() {
        return m_IsModelNameOutputOn;
    }

    public long getTimeOutRatio() {
        return m_TimeOutRatio;
    }

    public long getTimeOutTime() {
        return m_TimeOutTime;
    }

    public long getReceiveTimeout() {
        return m_ReceiveTimeout;
    }

    public boolean isAbortOn() {
        return m_IsAbortOn;
    }

    public String getAbortCommandListFileName() {
        return m_AbortCommandListFileName;
    }

    public int getModelReceivePort() {
        return m_ModelReceivePort;
    }

    public String getModelOutputPath() {
        return m_ModelOutputPath;
    }

    public String getRPCReceiveModule() {
        return m_RPCReceiveModule;
    }

    public int getNullResponseSleepMSec() {
        return m_NullResponseSleepMSec;
    }

    public int getNullResponseCounter() {
        return m_NullResponseeCounter;
    }

    public String getNbestParseType() {
        return m_NbestParseType;
    }

    public String getResultConverter() {
        return m_ResultConverter;
    }

    public String getVoiceFontSelectorHost() {
        return m_VoiceFontSelectorHost;
    }

    public int getVoiceFontSelectorPort() {
        return m_VoiceFontSelectorPort;
    }

    public long getVoiceFontSelectorTimeout() {
        return m_VoiceFontSelectorTimeout;
    }

    public String getVoiceFontSelectorLanguage() {
        return m_VoiceFontSelectorLanguage;
    }

    // ------------------------------------------
    // private member function
    // ------------------------------------------
    // Constructor
    private ASRProperties() {
        super();
        m_ASRParamList = new ArrayList<ASRParam>();
    }

    private void checkParameter() throws Exception {
        // check ConnectRetryTimes to Engine.
        if (getConnectRetryTimes() < 0) {
            System.out.println("Invalid Parameter: "
                    + KEY_ENGINE_CONNECT_RETRYTIMES);
            throw new Exception("Invalid Parameter: "
                    + KEY_ENGINE_CONNECT_RETRYTIMES);
        }

        // check ConnectRetryInterval to Engine.
        if (getConnectRetryInterval() < 0) {
            System.out.println("Invalid Parameter: "
                    + KEY_ENGINE_CONNECT_REYRYINTERVAL);
            throw new Exception("Invalid Parameter: "
                    + KEY_ENGINE_CONNECT_REYRYINTERVAL);
        }
    }

    private String readRPCCommandSender(String paramSection) {
        String temp = m_Properties.getProperty(paramSection
                + KEY_RPCCOMMANDSENDER);

        if (temp == null || temp.isEmpty()) {
            // engine.rpccommandsender is invalid.
            temp = null;
        }

        return temp;
    }

    private void readAbortOnOff() {
        // read cache On/Off.
        m_IsAbortOn = readOnOff(KEY_ABORT);
    }

    private String readAbortCommandListFileName(String paramSection) {
        String temp = m_Properties.getProperty(paramSection
                + KEY_ABORTCOMMANDLIST);

        if (temp == null || temp.isEmpty()) {
            // engine.rpccommandsender is invalid.
            temp = null;
        }

        return temp;
    }

    private String readHost(String paramSection) {
        String temp = m_Properties.getProperty(paramSection + "." + KEY_HOST);

        if (temp == null || temp.isEmpty()) {
            // asrX.host is invalid.
            temp = null;
        }

        return temp;
    }

    private int readNbestReceivePort(String paramSection) {
        String temp = m_Properties.getProperty(paramSection + "."
                + KEY_NBESTRECEIVEPORT);

        if (temp == null || temp.isEmpty()) {
            // asrX.nbestreceiveport is invalid.
            return -1;
        }

        return Integer.valueOf(temp);
    }

    private int readSpeechInputPort(String paramSection) {
        String temp = m_Properties.getProperty(paramSection + "."
                + KEY_SPEECHINPUTPORT);

        if (temp == null || temp.isEmpty()) {
            // asrX.speechinputport is invalid.
            return -1;
        }

        return Integer.valueOf(temp);
    }

    private String readClientHost(String paramSection) {
        String temp = m_Properties.getProperty(paramSection + "."
                + KEY_CLIENTHOST);

        if (temp == null || temp.isEmpty()) {
            // asrX.host is invalid.
            temp = null;
        }

        return temp;
    }

    private int readClientNbestReceivePort(String paramSection) {
        String temp = m_Properties.getProperty(paramSection + "."
                + KEY_CLIENTNBESTRECEIVEPORT);

        if (temp == null || temp.isEmpty()) {
            // asrX.nbestreceiveport is invalid.
            return -1;
        }

        return Integer.valueOf(temp);
    }

    private int readClientSpeechInputPort(String paramSection) {
        String temp = m_Properties.getProperty(paramSection + "."
                + KEY_CLIENTSPEECHINPUTPORT);

        if (temp == null || temp.isEmpty()) {
            // asrX.speechinputport is invalid.
            return -1;
        }

        return Integer.valueOf(temp);
    }

    private int readRPCNumber(String paramSection) {
        String temp = m_Properties.getProperty(paramSection + "."
                + KEY_RPCNUMBER);

        if (temp == null || temp.isEmpty()) {
            // asrX.rpcnumber is invalid.
            return -1;
        }

        return Integer.valueOf(temp);
    }

    private String readAMCommandTableFileName(String paramSection) {
        String temp = m_Properties.getProperty(paramSection + "."
                + KEY_COMMANDTABLEFILENAME + "." + KEY_ACOUSTICMODEL);

        if (temp == null || temp.isEmpty()) {
            // asr.commandtablefilename.acousticmodel is invalid.
            temp = null;
        }

        return temp;
    }

    private String readLMCommandTableFileName(String paramSection) {
        String temp = m_Properties.getProperty(paramSection + "."
                + KEY_COMMANDTABLEFILENAME + "." + KEY_LANGUAGEMODEL);

        if (temp == null || temp.isEmpty()) {
            // asr.commandtablefilename.languagemodel is invalid.
            temp = null;
        }

        return temp;
    }

    private void readVoiceFontSelectorsParameter(String paramSection) {
        // read VoiceFontSelector's host name
        String temp = m_Properties.getProperty(paramSection + "."
                + KEY_VOICEFONTSELECTOR + "." + KEY_HOST);

        if (temp == null || temp.isEmpty()) {
            // asr.voicefontselector.host is invalid.
            System.out.println("asr.voicefontselector.host is empty");
            temp = "";
        }
        m_VoiceFontSelectorHost = temp;

        // read VoiceFontSelector accept port number
        try {
            temp = m_Properties.getProperty(paramSection + "."
                    + KEY_VOICEFONTSELECTOR + "." + KEY_PORT);
            m_VoiceFontSelectorPort = Integer.parseInt(temp);
            if (m_VoiceFontSelectorPort < 0
                    || PORT_NUMBER_MAX < m_VoiceFontSelectorPort) {
                System.out
                        .println("Invalid Parameter: asr.voicefontselector.port");
                m_VoiceFontSelectorPort = -1;
            }
        } catch (Exception e) {
            System.out.println("Invalid Parameter: asr.voicefontselector.port");
            m_VoiceFontSelectorPort = -1;
        }

        // read VoiceFont select processing timeout time(millisecond)
        try {
            temp = m_Properties.getProperty(paramSection + "."
                    + KEY_VOICEFONTSELECTOR + "." + KEY_TIMEOUT);
            m_VoiceFontSelectorTimeout = Long.parseLong(temp);
            if (m_VoiceFontSelectorTimeout < 0) {
                System.out
                        .println("Invalid Parameter: asr.voicefontselector.timeout");
                m_VoiceFontSelectorTimeout = 0;
            }
        } catch (Exception e) {
            System.out
                    .println("Invalid Parameter: asr.voicefontselector.timeout");
            m_VoiceFontSelectorTimeout = 0;
        }

        // read VoiceFont's Language
        temp = m_Properties.getProperty(paramSection + "."
                + KEY_VOICEFONTSELECTOR + "." + KEY_LANGUAGE);
        if (temp == null || temp.isEmpty()) {
            m_VoiceFontSelectorLanguage = "";
        } else {
            m_VoiceFontSelectorLanguage = temp;
        }

        return;
    }

    private int readFrameSyncSize(String paramSection) {
        String temp = m_Properties.getProperty(paramSection + "."
                + KEY_FRAMESYNCSZE);

        if (temp == null || temp.isEmpty()) {
            // asrX.framesyncsize is invalid.
            return -1;
        }

        return Integer.valueOf(temp);
    }

    private boolean readPartOfSpeech(String paramSection) {
        String temp = m_Properties.getProperty(paramSection);

        if (temp == null || temp.isEmpty()) {
            // asrX.framesyncsize is invalid.
            return false;
        }

        if (temp.toLowerCase().indexOf(MCMLStatics.STRING_YES) >= 0) {
            return true;
        }

        return false;
    }

    private String readNbestParseType(String paramSection) {
        String temp = m_Properties.getProperty(paramSection);

        if (temp == null || temp.isEmpty()) {
            // asrX.framesyncsize is invalid.
            return null;
        }

        if (temp.toLowerCase().indexOf(MCMLStatics.STRING_YES) >= 0) {
            temp = null;
        }

        return temp;
    }

    private String readResultConverter(String paramSection) {
        String temp = m_Properties.getProperty(paramSection);

        if (temp == null || temp.isEmpty()) {
            return null;
        }

        return temp;
    }

    private void readSRTimeoutParameter() {
        // create SRTimeout section name.
        String sectionName = KEY_ENGINE + KEY_SRTIMEOUT;
        String temp = null;
        try {
            // get SRTimeout time.
            temp = m_Properties.getProperty(sectionName + "." + KEY_TIME);
            m_TimeOutTime = Long.parseLong(temp);
        } catch (NumberFormatException e) {
            System.out.println("Invalid Parameter: " + sectionName + "."
                    + KEY_TIME);
            m_TimeOutTime = -1;
        }

        try {
            // get SRTimeout ratio.
            temp = m_Properties.getProperty(sectionName + "." + KEY_RATIO);
            m_TimeOutRatio = Long.parseLong(temp);
        } catch (NumberFormatException e) {
            System.out.println("Invalid Parameter: " + sectionName + "."
                    + KEY_RATIO);
            m_TimeOutRatio = -1;
        }
    }

    private void readReceiveTimeout() {
        String temp = null;
        try {
            // get SRTimeout time.
            temp = m_Properties.getProperty(KEY_ENGINE + KEY_RECEIVETIMEOUT);
            m_ReceiveTimeout = Long.parseLong(temp);
        } catch (NumberFormatException e) {
            System.out.println("Invalid Parameter: " + KEY_ENGINE
                    + KEY_RECEIVETIMEOUT);
            m_ReceiveTimeout = -1;
        }
    }

    private void readModelManagerParameter() {
        // get ModelOutputPath.
        String temp = m_Properties.getProperty(KEY_ENGINE_ASR + "."
                + KEY_MODELRECEIVEPORT);
        try {
            m_ModelReceivePort = Integer.parseInt(temp);
        } catch (NumberFormatException e) {
            System.out.println("Invalid Parameter: " + KEY_ENGINE_ASR + "."
                    + KEY_MODELRECEIVEPORT);
            m_ModelReceivePort = -1;
        }
        if (m_ModelReceivePort < 0 || PORT_NUMBER_MAX < m_ModelReceivePort) {
            System.out.println("Invalid Parameter: " + KEY_ENGINE_ASR + "."
                    + KEY_MODELRECEIVEPORT);
            m_ModelReceivePort = -1;
        }

        // get ModelOutputPath.
        m_ModelOutputPath = m_Properties.getProperty(KEY_ENGINE_ASR + "."
                + KEY_MODELOUTPUTPATH);
        if (m_ModelOutputPath == null || m_ModelOutputPath.isEmpty()) {
            System.out.println("Invalid Parameter: " + KEY_ENGINE_ASR + "."
                    + KEY_MODELOUTPUTPATH);
            m_ModelOutputPath = "";
        }

        // get RPCReceiveModule.
        m_RPCReceiveModule = m_Properties.getProperty(KEY_ENGINE_ASR + "."
                + KEY_RPCRECEIVEMODULE);
        if (m_RPCReceiveModule == null || m_RPCReceiveModule.isEmpty()) {
            System.out.println("Invalid Parameter: " + KEY_ENGINE_ASR + "."
                    + KEY_RPCRECEIVEMODULE);
            m_RPCReceiveModule = "";
        }

    }

    private void readNullResponseParameter() {
        String temp = null;
        // NullResponseSleepMSec
        try {
            temp = m_Properties.getProperty(KEY_ENGINE_NULLRESPONSE_SLEEPMSEC,
                    "10");
            if (temp == null || temp.isEmpty()) {
                System.out.println("Invalid Parameter: "
                        + KEY_ENGINE_NULLRESPONSE_SLEEPMSEC);
            } else {
                int tempVal = Integer.parseInt(temp);
                if (tempVal > 0) {
                    m_NullResponseSleepMSec = tempVal;
                } else {
                    System.out.println("Invalid Parameter: "
                            + KEY_ENGINE_NULLRESPONSE_SLEEPMSEC + " <= 0");
                }
            }
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid Parameter: "
                    + KEY_ENGINE_NULLRESPONSE_SLEEPMSEC);
        }

        // NullResponseCounter
        try {
            temp = m_Properties.getProperty(KEY_ENGINE_NULLRESPONSE_COUNTER,
                    "10");
            if (temp == null || temp.isEmpty()) {
                System.out.println("Invalid Parameter: "
                        + KEY_ENGINE_NULLRESPONSE_COUNTER);
            } else {
                int tempVal = Integer.parseInt(temp);
                if (tempVal > 0) {
                    m_NullResponseeCounter = tempVal;
                } else {
                    System.out.println("Invalid Parameter: "
                            + KEY_ENGINE_NULLRESPONSE_COUNTER + " <= 0");
                }
            }
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid Parameter: "
                    + KEY_ENGINE_NULLRESPONSE_COUNTER);
        }
    }
}
