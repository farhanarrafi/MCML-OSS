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

//-------------------------------------------------------------------
//Ver.3.0
//2011/12/06
//-------------------------------------------------------------------

package jp.go.nict.mcml.serverap.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.PatternSyntaxException;

import jp.go.nict.mcml.servlet.MCMLStatics;
import jp.go.nict.mcml.xml.types.MCMLType;
import jp.go.nict.mcml.xml.types.UserType;

/**
 * ServerApCorpusLogger abstract class.
 * 
 */
public abstract class ServerApCorpusLogger {
    // ------------------------------------------
    // protected member constant
    // ------------------------------------------
    protected static final int SAMPLINGFREQUENCY_16K = 16000;
    protected static final int SAMPLINGFREQUENCY_8K = 8000;
    protected static final int DEFAULT_SAMPLING_FREQUENCY = 16000;
    protected static final short DEFAULT_SAMPLING_BIT = 16;
    protected static final short DEFAULT_CHANNEL_NUM = 1;
    protected static final int DSR_SPEECH_LENGTH_PER_ONE_FRAME = 240;
    protected static final int DSR_PACKET_LENGTH = 168;
    protected static final String USER_URI = "UserURI";
    protected static final String USER_ID = "UserID";
    protected static final String CLIENT_IP = "ClientIP";
    protected static final String VOICE_ID = "VoiceID";
    protected static final String PROCESS_ORDER = "ProcessOrder";
    protected static final String LOCATION = "Location";
    protected static final String DOMAIN = "Domain";
    protected static final String LANGUAGE = "Language";
    protected static final String INPUT_SENTENCE = "InputSentence";
    protected static final String INPUT_CHUNK = "InputChunk";
    protected static final String SENTENCE = "Sentence";
    protected static final String CHUNK = "Chunk";
    protected static final String STATE = "State";
    protected static final String GENDER = "Gender";
    protected static final String AGE = "Age";
    protected static final String PROCESS_TIME = "ProcessTime";
    protected static final String RTF = "RTF";
    protected static final String FILE_NAME_UTTERINFO = "UttrInfo";
    protected static final String TIME_TO_RECIEVE = "TimetoRecieve";
    protected static final SimpleDateFormat OUTPUT_DATE_FORMAT = new SimpleDateFormat(
            "yyyyMMdd HHmmss SSS");

    protected static final String LONGITUDE = "Longitude";
    protected static final String LATITUDE = "Latitude";
    protected static final String NATIVE_LANGUAGE = "NativeLanguage";
    protected static final String FIRST_FOREIGN_LANGUAGE = "FirstForeignLanguage";
    protected static final String SECOND_FOREIGN_LANGUAGE = "SecondForeignLanguage";

    // ------------------------------------------
    // protected member variable
    // ------------------------------------------
    protected boolean m_IsUtteranceInfoOn;
    protected FileOutputStream m_CorpusLogStream;
    protected String m_BasedirectryName;
    protected String m_CorpusLogOutputDirectoryPath;
    protected String m_PropertiesLanguage;
    protected String m_Prefix;
    protected HashMap<Integer, CorpusLogInfo> m_CorpusLogInfoMap;
    protected String m_Date;
    protected String[] m_RemovedSymbols;

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
    public ServerApCorpusLogger(boolean isUtteranceInfoOn,
            String basedirectryName, String language, String prefix,
            String[] removedSymbols) {
        m_IsUtteranceInfoOn = isUtteranceInfoOn;
        m_BasedirectryName = basedirectryName;
        m_PropertiesLanguage = language;
        m_Prefix = prefix;
        m_Date = "";
        m_RemovedSymbols = removedSymbols;

        // create CorpusLogInfoMap.
        m_CorpusLogInfoMap = new HashMap<Integer, CorpusLogInfo>();
    }

    /**
     * Writes CorpusLogInfo.
     * 
     * @param corpusLogInfoID
     */
    public abstract void writeCorpusLogInfo(int corpusLogInfoID);

    /**
     * Sets InputMCMLInfo.
     * 
     * @param inputMCML
     * @param corpusLogInfoID
     */
    public abstract void setInputMCMLInfo(MCMLType inputMCML,
            int corpusLogInfoID);

    /**
     * Sets OutputMCMLInfo.
     * 
     * @param outputMCMLData
     * @param corpusLogInfoID
     */
    public abstract void setOutputMCMLInfo(MCMLData outputMCMLData,
            int corpusLogInfoID);

    /**
     * Sets CompleteTime.
     * 
     * @param corpusLogInfoID
     */
    public abstract void setCompleteTime(int corpusLogInfoID);

    /**
     * Sets FirstFrameArrivedTime.
     * 
     * @param corpusLogInfoID
     */
    public abstract void setFirstFrameArrivedTime(int corpusLogInfoID);

    /**
     * Sets LastFrameArrivedTime.
     * 
     * @param corpusLogInfoID
     */
    public abstract void setLastFrameArrivedTime(int corpusLogInfoID);

    /**
     * Sets Wave data.
     * 
     * @param waveData
     * @param corpusLogInfoID
     */
    public void setWaveData(ArrayList<byte[]> waveData, int corpusLogInfoID) {
    }

    /**
     * Sets engine information.
     * 
     * @param engineInfo
     * @param corpusLogInfoID
     */
    public void setEngineInfo(EngineInfo engineInfo, int corpusLogInfoID) {
    }

    // ------------------------------------------
    // protected member functions
    // ------------------------------------------
    // create directory and set the path in return value
    // path example:(baseDirectoryName)/(date)/(language)
    protected boolean createDirectory(String baseDirectoryName, String language) {
        // get date used for directry's name
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String dateString = dateFormat.format(date);

        if (m_Date.equalsIgnoreCase(dateString)) {
            // already created Date directory.
            return false;
        } else {
            // save Date string.
            m_Date = dateString;
        }

        File dateDirectory = null;
        if (!baseDirectoryName.isEmpty()) {
            File baseDir = new File(baseDirectoryName);

            // if there is no base directory ,create it
            if (!baseDir.exists()) {
                baseDir.mkdir();
            }
            dateDirectory = new File(baseDirectoryName, dateString);
        } else {
            // if basedirecty's name is not specifed,make date directory on
            // current directory
            dateDirectory = new File(dateString);
        }

        // is Date Directory exist?
        if (!dateDirectory.exists()) {
            // not exist => create date directory.
            dateDirectory.mkdirs();
        }

        File outPutDirectory = new File(dateDirectory, language);
        // is output Directory exist?
        if (!outPutDirectory.exists()) {
            // not exist => create output directory
            outPutDirectory.mkdirs();
        }

        m_CorpusLogOutputDirectoryPath = outPutDirectory.getPath();

        return true;
    }

    // create corpus log file
    protected void createFile() throws FileNotFoundException, IOException {
        // get Date for File Name.
        Date date = new Date();

        // create UtteranceInfo FileName.
        SimpleDateFormat dataFormat = new SimpleDateFormat(
                "yyyyMMdd_HHmmss_SSS");
        String corpusLogFileName = m_Prefix + "_" + m_PropertiesLanguage + "_"
                + dataFormat.format(date) + "_" + FILE_NAME_UTTERINFO + ".txt";

        // create UtteranceInfo File.
        File corpusLogFile = new File(m_CorpusLogOutputDirectoryPath,
                corpusLogFileName);

        if (m_CorpusLogStream != null) {
            // close CorpusLog File.
            m_CorpusLogStream.close();
        }

        // UtteranceInfo File open.
        m_CorpusLogStream = new FileOutputStream(corpusLogFile);
        m_CorpusLogStream.flush();
    }

    // common function of writing some string in corpus Log file
    protected synchronized void write(String str) throws FileNotFoundException,
            IOException {
        synchronized (m_CorpusLogStream) {
            String line = "";
            line = removeSymbolsFromString(str);
            m_CorpusLogStream.write(line.getBytes(MCMLStatics.CHARSET_UTF_8));
        }
    }

    // write "\n" in corpus Log file to change Paragraph
    protected void changeParagraph() throws FileNotFoundException, IOException {
        synchronized (m_CorpusLogStream) {
            String line = "";
            line = "\n";
            m_CorpusLogStream.write(line.getBytes(MCMLStatics.CHARSET_UTF_8));
        }
    }

    // pick Out Ip adress from MCML/User
    protected String pickOutClientIp(String srcString) {
        String clientIP = "";

        try {
            String[] subString = srcString.split("/", 2);
            if (subString.length != 2) {
                return "";
            }
            String[] subString2 = subString[0].split(":", 2);
            if (subString2.length != 2) {
                return "";
            }
            if (subString2[0].equalsIgnoreCase(CLIENT_IP)) {
                clientIP = subString2[1];
            }
        } catch (PatternSyntaxException e) {
            ServerApLogger.getInstance().writeError(e.getMessage());
        }

        return clientIP;
    }

    // pick Out Received Time on Servlet.
    protected String pickOutTimeToRecieve(String srcString) {
        String timeToRecieve = "";

        try {
            String[] subString = srcString.split("/", 2);
            if (subString.length != 2) {
                return "";
            }
            String[] subString2 = subString[1].split(":", 2);
            if (subString2.length != 2) {
                return "";
            }
            if (subString2[0].equalsIgnoreCase(TIME_TO_RECIEVE)) {
                timeToRecieve = subString2[1];
            }
        } catch (PatternSyntaxException e) {
            ServerApLogger.getInstance().writeError(e.getMessage());
        }

        return timeToRecieve;
    }

    // merge String for sentence or Chunk.
    protected String mergeString(String srcString, String dstString) {
        String addString = "";

        // contain ";"?
        if (srcString.indexOf(";") != -1) {
            addString = srcString.replace(";", "");
        } else {
            addString = srcString;
        }

        // append String.
        if (dstString.isEmpty()) {
            dstString = addString;
        } else {
            dstString += ";" + addString;
        }

        return dstString;
    }

    protected void removeUserInfoStr(MCMLType inputMCML) throws Exception {
        // remove UserInfomation String(ClientIP and Recieved Time on Servlet).
        if (inputMCML.hasUser()) {
            UserType userType = new UserType();
            if (inputMCML.getUser().hasTransmitter()) {
                userType.addTransmitter(inputMCML.getUser().getTransmitter());
            }
            for (int i = 0; i < inputMCML.getUser().getReceiverCount(); i++) {
                userType.addReceiver(inputMCML.getUser().getReceiverAt(i));
            }
            inputMCML.replaceUserAt(userType, 0);
        }
    }

    protected String removeSymbolsFromString(String srcStr) {
        if (m_RemovedSymbols == null || m_RemovedSymbols.length == 0) {
            // not filtering
            return srcStr;
        }

        // do filtering
        for (int i = 0; i < m_RemovedSymbols.length; i++) {
            srcStr = srcStr.replace(m_RemovedSymbols[i], "");
        }
        return srcStr;
    }

    protected abstract void writeItemName() throws IOException,
            FileNotFoundException;
}
