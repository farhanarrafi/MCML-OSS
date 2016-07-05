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

import java.io.FileInputStream;
import java.util.Properties;

import jp.go.nict.mcml.servlet.MCMLStatics;

/**
 * ServerAp property file abstract class.
 * 
 */
public abstract class ServerApProperties {
    // ------------------------------------------
    // protected member constants
    // ------------------------------------------
    protected static final String KEY_ENGINE = "engine.";
    protected static final String KEY_SERVLET = "servlet.";
    protected static final String KEY_AUDIO = KEY_ENGINE + "audio.";
    protected static final String KEY_SERVLET_ACCEPTPORT = KEY_SERVLET
            + "acceptport";
    protected static final String KEY_ENGINE_MAXNUMBER = KEY_ENGINE
            + "maxnumber";
    protected static final String KEY_ENGINE_LANGUAGE1 = KEY_ENGINE
            + "language1";
    protected static final String KEY_ENGINE_LANGUAGE1_DELIMITER = KEY_ENGINE_LANGUAGE1
            + ".delimiter";
    protected static final String KEY_ENGINE_LANGUAGE1_STRINGCODE = KEY_ENGINE_LANGUAGE1
            + ".stringcode";
    protected static final String KEY_ENGINE_REQUEST_LIMIT_THRESHOLD = KEY_ENGINE
            + "request.limit.threshold";
    protected static final String KEY_ENGINE_REQUEST_TIMEOUT = KEY_ENGINE
            + "request.timeout";
    protected static final String KEY_ENGINE_REQUEST_TIMEOUT_INTERVAL = KEY_ENGINE_REQUEST_TIMEOUT
            + ".interval";
    protected static final String KEY_ENGINE_CONNECT = KEY_ENGINE + "connect.";
    protected static final String KEY_ENGINE_CONNECT_RETRYTIMES = KEY_ENGINE_CONNECT
            + "retrytimes";
    protected static final String KEY_ENGINE_CONNECT_REYRYINTERVAL = KEY_ENGINE_CONNECT
            + "retryinterval";
    protected static final String KEY_LOG = "log.";
    protected static final String KEY_LOG_BASEFILENAME = KEY_LOG
            + "basefilename";
    protected static final String KEY_LOG_LEVEL = KEY_LOG + "level";
    protected static final String KEY_ENGINE_TEXTFILTERFILE = KEY_ENGINE
            + "textfilterfile";
    protected static final String KEY_ENGINE_CORPUSLOG = KEY_ENGINE
            + "corpuslog.";
    protected static final String KEY_ENGINE_CORPUSLOG_BASEDIRECTRYNAME = KEY_ENGINE_CORPUSLOG
            + "basedirectryname";
    protected static final String KEY_ENGINE_CORPUSLOG_FILEPREFIX = KEY_ENGINE_CORPUSLOG
            + "fileprefix";
    protected static final String KEY_ENGINE_CORPUSLOG_UTTERANCEINFO = KEY_ENGINE_CORPUSLOG
            + "utteranceinfo";
    protected static final String KEY_ENGINE_CORPUSLOG_REMOVEDSYMBOL = KEY_ENGINE_CORPUSLOG
            + "removedsymbol";

    protected static final String KEY_ENGINE_AUDIO_ENDIAN = KEY_AUDIO
            + "endian";

    protected static final String KEY_ON = "on";
    protected static final String KEY_OFF = "off";
    protected static final int PORT_NUMBER_MAX = 65535;

    // ------------------------------------------
    // protected member variables
    // ------------------------------------------
    protected Properties m_Properties;
    protected int m_EngineMaxNumber;

    // ------------------------------------------
    // private member variables
    // ------------------------------------------
    protected int m_ServletPort;
    protected String m_LogBaseFileName;
    protected int m_LogLevel;
    protected String m_Language1;
    protected String m_Language1Delimiter;
    protected String m_Language1StringCode;
    protected long m_RequestLimitThreshold;
    protected long m_RequestTimeout;
    protected long m_RequestTimeoutInterval;
    protected boolean m_IsEnableRequestTimeout;
    protected int m_ConnectRetryTimes;
    protected int m_ConnectRetryInterval;
    protected String m_TextFilterFile;
    protected String m_CorpusLogBaseDirectryName;
    protected String m_CorpusLogFilePrefix;
    protected boolean m_CorpusLogUtteranceInfo;
    protected String[] m_CorpusLogRemovedSymbols;
    protected String m_EngineBigEndian;

    // ------------------ ------------------------
    // public member functions
    // -------------------------------------------
    /**
     * Default constructor
     */
    public ServerApProperties() {
        m_Properties = new Properties();
        m_EngineMaxNumber = 0;
        m_ServletPort = 0;
        m_LogBaseFileName = "";
        m_LogLevel = 0;
        m_Language1 = "";
        m_Language1Delimiter = "";
        m_Language1StringCode = "";
        m_RequestLimitThreshold = -1;
        m_RequestTimeout = -1;
        m_RequestTimeoutInterval = -1;
        m_IsEnableRequestTimeout = false;
        m_ConnectRetryTimes = 0;
        m_ConnectRetryInterval = 0;
        m_TextFilterFile = "";
        m_CorpusLogBaseDirectryName = "";
        m_CorpusLogFilePrefix = "";
        m_CorpusLogUtteranceInfo = false;
        m_CorpusLogRemovedSymbols = null;
    }

    /**
     * Reads property.
     * 
     * @param fileName
     * @throws Exception
     */
    public void readProperties(String fileName) throws Exception {
        String temp = "";

        m_Properties.load(new FileInputStream(fileName));

        // for Network
        temp = m_Properties.getProperty(KEY_SERVLET_ACCEPTPORT);
        if (temp == null || temp.isEmpty()) {
            System.out.println("Invalid Parameter: " + KEY_SERVLET_ACCEPTPORT);
            throw new Exception("Invalid Parameter: " + KEY_SERVLET_ACCEPTPORT);
        }

        try {
            m_ServletPort = Integer.valueOf(temp);
            if (m_ServletPort < 0 || PORT_NUMBER_MAX < m_ServletPort) {
                System.out.println("Invalid Parameter: "
                        + KEY_SERVLET_ACCEPTPORT);
                throw new Exception("Invalid Parameter: "
                        + KEY_SERVLET_ACCEPTPORT);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid Parameter: " + KEY_SERVLET_ACCEPTPORT
                    + " is abnormal format");
            throw new Exception("Invalid Parameter: " + KEY_SERVLET_ACCEPTPORT
                    + " is abnormal format");
        }

        // for Engine
        temp = m_Properties.getProperty(KEY_ENGINE_MAXNUMBER);

        if (temp == null || temp.isEmpty()) {
            System.out.println("Invalid Parameter: " + KEY_ENGINE_MAXNUMBER);
            throw new Exception("Invalid Parameter: " + KEY_ENGINE_MAXNUMBER);
        }

        try {
            m_EngineMaxNumber = Integer.parseInt(temp);

            if (m_EngineMaxNumber < 1) {
                System.out.println("Invalid Parameter: " + KEY_ENGINE_MAXNUMBER
                        + " must be more than 1");
                throw new Exception("Invalid Parameter: "
                        + KEY_ENGINE_MAXNUMBER + " must be more than 1");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid Parameter: " + KEY_ENGINE_MAXNUMBER
                    + " is abnormal format");
            throw new Exception("Invalid Parameter: " + KEY_ENGINE_MAXNUMBER
                    + " is abnormal format");
        }

        m_EngineBigEndian = m_Properties.getProperty(KEY_ENGINE_AUDIO_ENDIAN,
                MCMLStatics.ENDIAN_BIG);

        try {
            m_EngineMaxNumber = Integer.parseInt(temp);

            if (m_EngineMaxNumber < 1) {
                System.out.println("Invalid Parameter: " + KEY_ENGINE_MAXNUMBER
                        + " must be more than 1");
                throw new Exception("Invalid Parameter: "
                        + KEY_ENGINE_MAXNUMBER + " must be more than 1");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid Parameter: " + KEY_ENGINE_MAXNUMBER
                    + " is abnormal format");
            throw new Exception("Invalid Parameter: " + KEY_ENGINE_MAXNUMBER
                    + " is abnormal format");
        }

        // Language1 for Engine
        m_Language1 = m_Properties.getProperty(KEY_ENGINE_LANGUAGE1);
        if (m_Language1 == null || m_Language1.isEmpty()) {
            System.out.println("Invalid Parameter: " + KEY_ENGINE_LANGUAGE1);
            throw new Exception("Invalid Parameter: " + KEY_ENGINE_LANGUAGE1);
        }

        // Language1 Delimiter for Engine
        m_Language1Delimiter = m_Properties
                .getProperty(KEY_ENGINE_LANGUAGE1_DELIMITER);
        if (m_Language1Delimiter == null) {
            System.out.println("Invalid Parameter: "
                    + KEY_ENGINE_LANGUAGE1_DELIMITER);
            throw new Exception("Invalid Parameter: "
                    + KEY_ENGINE_LANGUAGE1_DELIMITER);
        }

        // Language1 StringCode for Engine
        m_Language1StringCode = m_Properties
                .getProperty(KEY_ENGINE_LANGUAGE1_STRINGCODE);
        if (m_Language1StringCode == null || m_Language1StringCode.isEmpty()) {
            System.out.println("Invalid Parameter: "
                    + KEY_ENGINE_LANGUAGE1_STRINGCODE);
            throw new Exception("Invalid Parameter: "
                    + KEY_ENGINE_LANGUAGE1_STRINGCODE);
        }

        // request limit for engine
        try {
            temp = m_Properties.getProperty(KEY_ENGINE_REQUEST_LIMIT_THRESHOLD,
                    "-1");
            if (temp == null || temp.isEmpty()) {
                System.out.println("Invalid Parameter: "
                        + KEY_ENGINE_REQUEST_LIMIT_THRESHOLD);
                throw new Exception("Invalid Parameter: "
                        + KEY_ENGINE_REQUEST_LIMIT_THRESHOLD);
            }
            m_RequestLimitThreshold = Long.parseLong(temp);
        } catch (NumberFormatException nfe) {
            m_RequestLimitThreshold = -1;
        }

        // request time out for engine
        try {
            temp = m_Properties.getProperty(KEY_ENGINE_REQUEST_TIMEOUT, "-1");
            if (temp == null || temp.isEmpty()) {
                System.out.println("Invalid Parameter: "
                        + KEY_ENGINE_REQUEST_TIMEOUT);
                throw new Exception("Invalid Parameter: "
                        + KEY_ENGINE_REQUEST_TIMEOUT);
            }
            m_RequestTimeout = Long.parseLong(temp);
        } catch (NumberFormatException nfe) {
            m_RequestTimeout = -1;
        }

        try {
            // request time out interval for engine
            temp = m_Properties.getProperty(
                    KEY_ENGINE_REQUEST_TIMEOUT_INTERVAL, "-1");
            if (temp == null || temp.isEmpty()) {
                System.out.println("Invalid Parameter: "
                        + KEY_ENGINE_REQUEST_TIMEOUT_INTERVAL);
                throw new Exception("Invalid Parameter: "
                        + KEY_ENGINE_REQUEST_TIMEOUT_INTERVAL);
            }
            m_RequestTimeoutInterval = Long.parseLong(temp);
        } catch (NumberFormatException nfe) {
            m_RequestTimeoutInterval = -1;
        }

        // check enable time out request
        if ((m_RequestTimeout > 0) && (m_RequestTimeoutInterval > 0)) {
            // enable request time out
            m_IsEnableRequestTimeout = true;
            // time out interval larger than time out
            if (m_RequestTimeout < m_RequestTimeoutInterval) {
                m_RequestTimeoutInterval = m_RequestTimeout;
            }
        } else {
            // disable request time out
            m_IsEnableRequestTimeout = false;
            m_RequestTimeout = -1;
            m_RequestTimeoutInterval = -1;
        }

        // ConnectRetryTimes to Engine
        temp = m_Properties.getProperty(KEY_ENGINE_CONNECT_RETRYTIMES, "-1");
        m_ConnectRetryTimes = Integer.parseInt(temp);

        // ConnectRetryInterval to Engine
        temp = m_Properties.getProperty(KEY_ENGINE_CONNECT_REYRYINTERVAL, "-1");
        m_ConnectRetryInterval = Integer.parseInt(temp);

        // Text filter file for Engine
        m_TextFilterFile = m_Properties.getProperty(KEY_ENGINE_TEXTFILTERFILE);
        if (m_TextFilterFile == null || m_TextFilterFile.isEmpty()) {
            m_TextFilterFile = "";
        }

        // for Logging
        m_LogBaseFileName = m_Properties.getProperty(KEY_LOG_BASEFILENAME, "");
        temp = m_Properties.getProperty(KEY_LOG_LEVEL);
        if (temp == null || temp.isEmpty()) {
            System.out.println("Invalid Parameter: " + KEY_LOG_LEVEL);
            throw new Exception("Invalid Parameter: " + KEY_LOG_LEVEL);
        } else {
            m_LogLevel = Integer.valueOf(temp);
            if (m_LogLevel < 0 || 4 < m_LogLevel) {
                System.out.println("Invalid Parameter: " + KEY_LOG_LEVEL);
                throw new Exception("Invalid Parameter: " + KEY_LOG_LEVEL);
            }
        }

        // for Corpus Log.
        // Base directory name for corpus log file
        m_CorpusLogBaseDirectryName = m_Properties
                .getProperty(KEY_ENGINE_CORPUSLOG_BASEDIRECTRYNAME);
        if (m_CorpusLogBaseDirectryName == null
                || m_CorpusLogBaseDirectryName.isEmpty()) {
            m_CorpusLogBaseDirectryName = "";
        }

        // File prefix for corpus Log file
        m_CorpusLogFilePrefix = m_Properties
                .getProperty(KEY_ENGINE_CORPUSLOG_FILEPREFIX);
        if (m_CorpusLogFilePrefix == null || m_CorpusLogFilePrefix.isEmpty()) {

            m_CorpusLogFilePrefix = "";
        }

        // Utteranceinfo file output switch
        m_CorpusLogUtteranceInfo = readFileOutputOnOff(KEY_ENGINE_CORPUSLOG_UTTERANCEINFO);

        // Corpus Log Filtering words
        temp = m_Properties.getProperty(KEY_ENGINE_CORPUSLOG_REMOVEDSYMBOL);
        if (temp != null && !temp.isEmpty()) {
            m_CorpusLogRemovedSymbols = temp.split(",");
        }

        // normal end
        return;
    }

    /**
     * Gets ServletPort.
     * 
     * @return ServletPort
     */
    public int getServletPort() {
        return m_ServletPort;
    }

    /**
     * Gets EngineMaxNumber.
     * 
     * @return EngineMaxNumber
     */
    public int getEngineMaxNumber() {
        return m_EngineMaxNumber;
    }

    /**
     * Gets Language1.
     * 
     * @return Language1
     */
    public String getLanguage1() {
        return m_Language1;
    }

    /**
     * Gets Language1Delimiter.
     * 
     * @return Language1Delimiter
     */
    public String getLanguage1Delimiter() {
        return m_Language1Delimiter;
    }

    /**
     * Gets Language1StringCode.
     * 
     * @return Language1StringCode
     */
    public String getLanguage1StringCode() {
        return m_Language1StringCode;
    }

    /**
     * Gets ConnectRetryInterval.
     * 
     * @return ConnectRetryInterval
     */
    public int getConnectRetryInterval() {
        return m_ConnectRetryInterval;
    }

    /**
     * Gets ConnectRetryTimes.
     * 
     * @return ConnectRetryTimes
     */
    public int getConnectRetryTimes() {
        return m_ConnectRetryTimes;
    }

    /**
     * Gets TextFilterFile.
     * 
     * @return TextFilterFile
     */
    public String getTextFilterFile() {
        return m_TextFilterFile;
    }

    /**
     * Gets LogBaseFileName.
     * 
     * @return LogBaseFileName
     */
    public String getLogBaseFileName() {
        return m_LogBaseFileName;
    }

    /**
     * Gets LogLevel.
     * 
     * @return LogLevel
     */
    public int getLogLevel() {
        return m_LogLevel;
    }

    /**
     * Gets RequestLimitThreshold.
     * 
     * @return RequestLimitThreshold
     */
    public long getRequestLimitThreshold() {
        return m_RequestLimitThreshold;
    }

    /**
     * Gets RequestTimeout.
     * 
     * @return RequestTimeout
     */
    public long getRequestTimeout() {
        return m_RequestTimeout;
    }

    /**
     * Gets RequestTimeoutInterval.
     * 
     * @return RequestTimeoutInterval
     */
    public long getRequestTimeoutInterval() {
        return m_RequestTimeoutInterval;
    }

    /**
     * Gets IsEnableRequestTimeout.
     * 
     * @return IsEnableRequestTimeout
     */
    public boolean getIsEnableRequestTimeout() {
        return m_IsEnableRequestTimeout;
    }

    /**
     * Gets EngineNumber.
     * 
     * @return EngineNumber
     */
    public abstract int getEngineNumber();

    /**
     * Gets CorpusLogBaseDirectryName.
     * 
     * @return CorpusLogBaseDirectryName
     */
    public String getCorpusLogBaseDirectryName() {
        return m_CorpusLogBaseDirectryName;
    }

    /**
     * Gets CorpusLogFilePrefix.
     * 
     * @return CorpusLogFilePrefix
     */
    public String getCorpusLogFilePrefix() {
        return m_CorpusLogFilePrefix;
    }

    /**
     * Gets CorpusLogUtteranceInfo.
     * 
     * @return CorpusLogUtteranceInfo
     */
    public boolean getCorpusLogUtteranceInfo() {
        return m_CorpusLogUtteranceInfo;
    }

    /**
     * Gets CorpusLogRemovedSymbols.
     * 
     * @return CorpusLogRemovedSymbols
     */
    public String[] getCorpusLogRemovedSymbols() {
        return m_CorpusLogRemovedSymbols;
    }

    /**
     * Determines whether BigEndian or not.
     * 
     * @return boolean
     */
    public boolean isBigEndian() {
        return m_EngineBigEndian.equalsIgnoreCase(MCMLStatics.ENDIAN_BIG);
    }

    // ------------------ ------------------------
    // protected member functions
    // -------------------------------------------
    protected boolean readFileOutputOnOff(String key) {
        String temp = m_Properties.getProperty(key);

        // value is omitted
        if (temp == null) {
            return true;

            // value is on or empty
        } else if (temp.equalsIgnoreCase(KEY_ON) || temp.isEmpty()) {
            return true;

            // value is off
        } else if (temp.equalsIgnoreCase(KEY_OFF)) {
            return false;
        } else {
            // value is other invalid string
            System.out.println("Invalid Parameter:" + key);
            return false;
        }
    }

    protected boolean readOnOff(String key) {
        String temp = m_Properties.getProperty(key);
        // value is omitted
        if (temp == null) {
            System.out.println("Invalid Parameter:" + key);
            return false;

            // value is on
        } else if (temp.equalsIgnoreCase(KEY_ON)) {
            return true;

            // value is off
        } else if (temp.equalsIgnoreCase(KEY_OFF)) {
            return false;

            // value is empty
        } else if (temp.isEmpty()) {
            System.out.println("Invalid Parameter:" + key);
            return false;
        } else {
            // value is other invalid string
            System.out.println("Invalid Parameter:" + key);
            return false;
        }
    }
}
