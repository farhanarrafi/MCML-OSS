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

package jp.go.nict.mcml.server.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import jp.go.nict.mcml.engine.socket.Pair;
import jp.go.nict.mcml.servlet.control.dispatcher.connector.Tools;
import jp.go.nict.mcml.xml.MCMLStatics;

import org.apache.log4j.Logger;

import com.MCML.MCMLDoc;

/**
 * ServerApCorpusLogger class.
 * 
 * @version 4.0
 * @since 20120921
 */
public abstract class ServerApCorpusLogger {
    private static final Logger LOG = Logger
            .getLogger(ServerApCorpusLogger.class.getName());
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
    protected static final String DESTINATION_URL = "DestinationURL";
    protected static final String LANGUAGE_PREFIX_ALL = "ALL";
    protected static final String CONTROL_SERVER = "ControlServer";
    protected static final String STRING_URL_HTTP = "http://";
    protected static final String STRING_URL_HTTPS = "https://";

    // ------------------------------------------
    // protected member variable
    // ------------------------------------------
    protected boolean m_IsUtteranceInfoOn;
    protected String m_BasedirectoryName;
    protected String m_CorpusLogOutputDirectoryPath;
    protected String m_PropertiesLanguage;
    protected String m_Prefix;
    protected HashMap<Integer, CorpusLogInfo> m_CorpusLogInfoMap;
    protected String m_Date;
    protected String[] m_RemovedSymbols;
    protected boolean m_IsURLInfoOn;
    protected String m_LanguagePrefix;
    protected HashMap<String, FileOutputStream> m_CorpusLogStreamMap;

    // ------------------------------------------
    // public member functions
    // ------------------------------------------
    /**
     * Constructor
     * 
     * @param isUtteranceInfoOn
     * @param basedirectoryName
     * @param language
     * @param prefix
     * @param removedSymbols
     * @param isURLInfoOn
     */
    public ServerApCorpusLogger(boolean isUtteranceInfoOn,
            String basedirectoryName, String language, String prefix,
            String[] removedSymbols, boolean isURLInfoOn) {
        m_IsUtteranceInfoOn = isUtteranceInfoOn;
        m_BasedirectoryName = basedirectoryName;
        m_PropertiesLanguage = language;
        m_Prefix = prefix;
        m_Date = "";
        m_RemovedSymbols = removedSymbols;
        m_IsURLInfoOn = isURLInfoOn;
        m_LanguagePrefix = "";

        // create CorpusLogInfoMap.
        m_CorpusLogInfoMap = new HashMap<Integer, CorpusLogInfo>();
        m_CorpusLogStreamMap = new HashMap<String, FileOutputStream>(0);
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
     * @param inputMCMLDoc
     * @param corpusLogInfoID
     */
    public abstract void setInputMCMLInfo(MCMLDoc inputMCMLDoc,
            int corpusLogInfoID);

    /**
     * Sets OutputMCMLInfo.
     * 
     * @param outputMCMLDoc
     * @param corpusLogInfoID
     */
    public abstract void setOutputMCMLInfo(MCMLDoc outputMCMLDoc,
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
     * @param firstFrameArrivedTime
     */
    public abstract void setFirstFrameArrivedTime(int corpusLogInfoID,
            long firstFrameArrivedTime);

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
     * @param firstFrameArrivedTime
     */
    public abstract void setLastFrameArrivedTime(int corpusLogInfoID,
            long firstFrameArrivedTime);

    /**
     * Sets LastFrameArrivedTime.
     * 
     * @param corpusLogInfoID
     */
    public abstract void setLastFrameArrivedTime(int corpusLogInfoID);

    /**
     * Sets DestinationURL.
     * 
     * @param urlList
     * @param corpusLogInfoID
     */
    public abstract void setDestinationURL(ArrayList<String> urlList,
            int corpusLogInfoID);

    /**
     * Sets WaveData.
     * 
     * @param waveData
     * @param corpusLogInfoID
     */
    public void setWaveData(ArrayList<byte[]> waveData, int corpusLogInfoID) {
    }

    /**
     * Sets EngineInfo
     * 
     * @param engineInfo
     * @param corpusLogInfoID
     */
    public void setEngineInfo(EngineInfo engineInfo, int corpusLogInfoID) {
    }

    // ------------------------------------------
    // protected member functions
    // ------------------------------------------

    /**
     * create destination URL string(for corpus log)
     * 
     * @param urlList
     * @return URL character string
     */
    protected String createDestinationURL(ArrayList<String> urlList) {
        String url = "";
        if (urlList != null) {
            for (int i = 0; i < urlList.size(); i++) {
                String dest = urlList.get(i);
                Pair<String, Integer> target = new Pair<String, Integer>();
                if (Tools.parseURL(dest, target)) {
                    // host:port
                    url += target.getFirst() + "|" + target.getSecond();
                } else {
                    // URL
                    url += dest;
                }
                if (i < urlList.size() - 1) {
                    url += ";";
                }
            }
        }
        return url;

    }

    /**
     * create destination directory name string(for result log)
     * 
     * @param urlList
     * @return url
     */
    protected String createDestinationDirectoryName(ArrayList<String> urlList) {
        String url = "";
        if (urlList != null) {
            for (int i = 0; i < urlList.size(); i++) {
                String dest = urlList.get(i);
                Pair<String, Integer> target = new Pair<String, Integer>();
                if (Tools.parseURL(dest, target)) {
                    // host:port
                    url += target.getFirst() + "_" + target.getSecond();
                } else {
                    // URL
                    try {
                        // get substring
                        String temp = dest;
                        temp = temp.replaceFirst(STRING_URL_HTTP, "");
                        temp = temp.replaceFirst(STRING_URL_HTTPS, "");
                        temp = temp.split("/", 2)[0];
                        temp = temp.split(":", 2)[0];
                        url += CONTROL_SERVER + "_" + temp;
                    } catch (Exception e) {
                        LOG.error(e.getMessage(), e);
                        url = "";
                    }
                }
                if (i < urlList.size() - 1) {
                    url += "-";
                }
            }
        }
        return url;

    }

    /**
     * create directory and set the path in return value path
     * example:(baseDirectoryName)/(date)/(language)/(destinationName)
     * 
     * @param baseDirectoryName
     * @param language
     * @param destinationURLList
     * @param isMTRequest
     * @return {@code true}
     *         <ul>
     *         If output directory does not exist.
     *         </ul>
     *         {@code false}
     *         <ul>
     *         If output directory already exists.
     *         </ul>
     */
    protected boolean createDirectory(String baseDirectoryName,
            String language, ArrayList<String> destinationURLList,
            boolean isMTRequest) {
        // get date used for directory's name
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String dateString = dateFormat.format(date);

        if (m_Date.equalsIgnoreCase(dateString)) {
            // already created Date directory.
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
            // if basedirectory's name is not specified,make date directory on
            // current directory
            dateDirectory = new File(dateString);
        }

        // is Date Directory exist?
        if (!dateDirectory.exists()) {
            // not exist => create date directory.
            dateDirectory.mkdirs();
        }

        // is Language Directory exist?
        File outPutDirectory;

        // set language prefix
        if (isMTRequest) {
            //  if activate as socket servlet and receive MT request
            m_LanguagePrefix = LANGUAGE_PREFIX_ALL;
        } else {
            m_LanguagePrefix = language.toLowerCase();
        }
        // create language directory
        File languageDirectory = new File(dateDirectory, m_LanguagePrefix);
        // is language directory exist?
        if (!languageDirectory.exists()) {
            // not exist => create language directory.
            languageDirectory.mkdirs();
        }

        // create destination directory
        String destinationDirectoryName = createDestinationDirectoryName(destinationURLList);
        if (!destinationDirectoryName.isEmpty()) {
            outPutDirectory = new File(languageDirectory,
                    destinationDirectoryName);
        } else {
            outPutDirectory = languageDirectory;
        }

        // get output directory path
        m_CorpusLogOutputDirectoryPath = outPutDirectory.getPath();

        // is output Directory exist?
        if (!outPutDirectory.exists()) {
            // not exist => create output directory
            outPutDirectory.mkdirs();
            return true; // created output directory
        } else {
            return false; // already created output directory
        }
    }

    /**
     * create corpus log file
     * 
     * @throws FileNotFoundException
     * @throws IOException
     */
    protected void createFile() throws FileNotFoundException, IOException {
        // get Date for File Name.
        Date date = new Date();

        // create UtteranceInfo FileName.
        SimpleDateFormat dataFormat = new SimpleDateFormat(
                "yyyyMMdd_HHmmss_SSS");
        String corpusLogFileName = m_Prefix + "_" + m_LanguagePrefix + "_"
                + dataFormat.format(date) + "_" + FILE_NAME_UTTERINFO + ".txt";

        // create UtteranceInfo File.
        File corpusLogFile = new File(m_CorpusLogOutputDirectoryPath,
                corpusLogFileName);

        // UtteranceInfo File open.
        FileOutputStream corpusLogStream = new FileOutputStream(corpusLogFile);
        corpusLogStream.flush();
        m_CorpusLogStreamMap.put(m_LanguagePrefix, corpusLogStream);
    }

    /**
     * common function of writing some string in corpus Log file
     * 
     * @param str
     * @throws FileNotFoundException
     * @throws IOException
     */
    protected synchronized void write(String str) throws FileNotFoundException,
            IOException {
        synchronized (m_CorpusLogStreamMap.get(m_LanguagePrefix)) {
            String line = "";
            line = removeSymbolsFromString(str);
            m_CorpusLogStreamMap.get(m_LanguagePrefix).write(
                    line.getBytes(MCMLStatics.CHARSET_UTF_8));
        }
    }

    /**
     * write "\n" in corpus Log file to change Paragraph
     * 
     * @throws FileNotFoundException
     * @throws IOException
     */
    protected void changeParagraph() throws FileNotFoundException, IOException {
        synchronized (m_CorpusLogStreamMap.get(m_LanguagePrefix)) {
            String line = "";
            line = "\n";
            m_CorpusLogStreamMap.get(m_LanguagePrefix).write(
                    line.getBytes(MCMLStatics.CHARSET_UTF_8));
        }
    }

    /**
     * merge String for sentence or Chunk.
     * 
     * @param srcString
     * @param dstString
     * @return Character string merged to parameter dstString
     */
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

    /**
* Returns character string substituted with null characters if there is information on m_RemovedSymbols array in character string.
     * <ul>
     * <li> Returns parameter without changing if m_RemovedSymbols array is {@code null} or contains no information.
     * </ul>
     * 
     * @param srcStr
     * @return If there is information on m_RemovedSymbols array in parameter, character string substituted by null characters.
     */
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

    /**
     * @throws IOException
     * @throws FileNotFoundException
     */
    protected abstract void writeItemName() throws IOException,
            FileNotFoundException;
}
