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

package jp.go.nict.mcml.servlet.control;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * ControlServer property class.
 * 
 */
public class ControlServerProperties {
    private static final Logger LOG = Logger
            .getLogger(ControlServerProperties.class.getName());
    // ------------------------------------------
    // private member constants
    // ------------------------------------------
    private static final String KEY_SERVER = "server.";
    private static final String KEY_SERVER_CORPUSLOG = KEY_SERVER
            + "corpuslog.";
    private static final String KEY_SERVER_CORPUSLOG_ASR = KEY_SERVER_CORPUSLOG
            + "asr.";
    private static final String KEY_SERVER_CORPUSLOG_MT = KEY_SERVER_CORPUSLOG
            + "mt.";
    private static final String KEY_SERVER_CORPUSLOG_TTS = KEY_SERVER_CORPUSLOG
            + "tts.";
    private static final String KEY_RECOGNITIONRESULT = KEY_SERVER_CORPUSLOG_ASR
            + "recognitionresult.";
    private static final String KEY_INPUTSPEECHDATA = KEY_SERVER_CORPUSLOG_ASR
            + "inputspeechdata";
    private static final String KEY_BASEDIRECTORYNAME = "basedirectoryname";
    private static final String KEY_FILEPREFIX = "fileprefix";
    private static final String KEY_UTTERANCEINFO = "utteranceinfo";
    private static final String KEY_REMOVEDSYMBOL = "removedsymbol";
    private static final String KEY_URLINFO = "urlinfo";
    private static final String KEY_UTF8 = "utf8";
    private static final String KEY_EUC = "euc";
    private static final String VALUE_ON = "on";
    private static final String VALUE_OFF = "off";

    private static final String KEY_SERVER_LOGSERVER = KEY_SERVER
            + "logserver.";

    private static final String KEY_SERVER_DM = KEY_SERVER + "dm.";
    private static final String KEY_DM_IP = KEY_SERVER_DM + "ip";

    // for MCML Server
    private static final String KEY_PARTYREGISTRATIONSERVER = "partyregistrationserver.";
    private static final String KEY_ROUTINGSERVER = "routingserver.";
    private static final String SUBKEY_URL = "url";
    private static final String SUBKEY_USERID = "userid";
    private static final String SUBKEY_IMAGEFILENAME = "imagefilename";

    private static final String KEY_LISTFILENAME = "listfilename";
    private static final String KEY_TIMEOUT = "timeout.";
    private static final String KEY_MILLISECONDS = "milliseconds";
    private static final String KEY_RESULTADOPTIONMODE = "resultadoptionmode";
    private static final String KEY_LIMIT = "limit.";
    private static final String KEY_SWITCH = "switch";
    private static final String KEY_NUMBER = "number";

    /** DB information keys */
    private static final String KEY_DB = "db.";
    private static final String KEY_DB_URL = KEY_DB + "url";
    private static final String KEY_DB_DRIVER = KEY_DB + "driver";
    private static final String KEY_DB_USER = KEY_DB + "user";
    private static final String KEY_DB_PASSWORD = KEY_DB + "password";

    private static final ControlServerProperties M_INSTANCE = new ControlServerProperties();

    // ------------------------------------------
    // public member constants
    // ------------------------------------------
    /**
     * AdoptationMode type enumeration class.
     * 
     */
    public enum AdoptationMode {
        /** LIST_TOP */
        LIST_TOP,
        /** FASTEST */
        FASTEST,
        /** LIST_ORDER */
        LIST_ORDER,
        /** SCORE_ORDER */
        SCORE_ORDER
    }

    // ------------------------------------------
    // private member variables
    // ------------------------------------------
    private Properties m_Prop;

    private String m_CorpusLogASRBaseDirectoryName;
    private String m_CorpusLogASRFilePrefix;
    private boolean m_CorpusLogASRUtteranceInfo;
    private String[] m_CorpusLogASRRemovedSymbols;
    private boolean m_CorpusLogASRIsUtf8OutputOn;
    private boolean m_CorpusLogASRIsEucOutputOn;
    private boolean m_CorpusLogASRIsInputSpeechdataOutputOn;
    private boolean m_CorpusLogASRURLInfo;

    private String m_CorpusLogMTBaseDirectoryName;
    private String m_CorpusLogMTFilePrefix;
    private boolean m_CorpusLogMTUtteranceInfo;
    private String[] m_CorpusLogMTRemovedSymbols;
    private boolean m_CorpusLogMTURLInfo;

    private String m_CorpusLogTTSBaseDirectoryName;
    private String m_CorpusLogTTSFilePrefix;
    private boolean m_CorpusLogTTSUtteranceInfo;
    private String[] m_CorpusLogTTSRemovedSymbols;
    private boolean m_CorpusLogTTSURLInfo;

    private boolean m_ClientIp;
    private boolean m_LogDb;

    private Date m_EffectiveDate;
    private String m_Version;
    private String m_MessageType;
    private String m_MessageJaBody;
    private String m_MessageEnBody;
    private String m_MessageJaTitle;
    private String m_MessageEnTitle;
    private long m_AccessKeyUpdateMinute;
    private boolean m_AccessKeyControl;
    private boolean m_SecureComCtrl;

    /** debug flag */
    // hmatsumo

    // for MCML Server
    private String m_PartyRegistrationServerURL;
    private String m_PartyRegistrationServerUserID;
    private String m_PartyRegistrationServerImageFileName;
    private String m_RoutingServerURL;

    private String m_LogServerURL;

    /** DM information */
    private String dmIp = null;

    private String listFileName;
    private int timeoutMilliSeconds;
    private AdoptationMode resultAdoptionMode;
    private boolean timeoutLimitSwitch;
    private int timeoutLimitNumber;

    // private boolean errorLogTraceSwitch;

    /** DB informations */
    private String dbUrl = null;
    private String dbUser = null;
    private String dbPassword = null;
    private String dbDriver = null;


    // ------------------------------------------
    // public member functions
    // ------------------------------------------
    /**
     * Gets instance.
     * 
     * @return Instance
     */
    public static ControlServerProperties getInstance() {
        return M_INSTANCE;
    }

    /**
     * Gets CorpusLogASRBaseDirectoryName.
     * 
     * @return m_CorpusLogASRBaseDirectoryName
     */
    public String getCorpusLogASRBaseDirectoryName() {
        return m_CorpusLogASRBaseDirectoryName;
    }

    /**
     * Gets CorpusLogASRFilePrefix.
     * 
     * @return m_CorpusLogASRFilePrefix
     */
    public String getCorpusLogASRFilePrefix() {
        return m_CorpusLogASRFilePrefix;
    }

    /**
     * Gets CorpusLogASRUtteranceInfo.
     * 
     * @return CorpusLogASRUtteranceInfo
     */
    public boolean getCorpusLogASRUtteranceInfo() {
        return m_CorpusLogASRUtteranceInfo;
    }

    /**
     * Gets CorpusLogASRRemovedSymbols.
     * 
     * @return CorpusLogASRRemovedSymbols
     */
    public String[] getCorpusLogASRRemovedSymbols() {
        return m_CorpusLogASRRemovedSymbols;
    }

    /**
     * Determines if CorpusLogASRUtf8OutputOn is true or not.
     * 
     * @return m_CorpusLogASRIsUtf8OutputOn
     */
    public boolean isCorpusLogASRUtf8OutputOn() {
        return m_CorpusLogASRIsUtf8OutputOn;
    }

    /**
     * Determines if CorpusLogASREucOutputOn is true or not.
     * 
     * @return m_CorpusLogASRIsEucOutputOn
     */
    public boolean isCorpusLogASREucOutputOn() {
        return m_CorpusLogASRIsEucOutputOn;
    }

    /**
     * Determines if CorpusLogASRInputSpeechdataOutputOn is true or not.
     * 
     * @return m_CorpusLogASRIsInputSpeechdataOutputOn
     */
    public boolean isCorpusLogASRInputSpeechdataOutputOn() {
        return m_CorpusLogASRIsInputSpeechdataOutputOn;
    }

    /**
     * Gets CorpusLogASRURLInfo.
     * 
     * @return CorpusLogASRURLInfo
     */
    public boolean getCorpusLogASRURLInfo() {
        return m_CorpusLogASRURLInfo;
    }

    /**
     * Gets CorpusLogMTBaseDirectoryName.
     * 
     * @return CorpusLogMTBaseDirectoryName
     */
    public String getCorpusLogMTBaseDirectoryName() {
        return m_CorpusLogMTBaseDirectoryName;
    }

    /**
     * Gets CorpusLogMTFilePrefix.
     * 
     * @return CorpusLogMTFilePrefix
     */
    public String getCorpusLogMTFilePrefix() {
        return m_CorpusLogMTFilePrefix;
    }

    /**
     * Gets CorpusLogMTUtteranceInfo.
     * 
     * @return CorpusLogMTUtteranceInfo
     */
    public boolean getCorpusLogMTUtteranceInfo() {
        return m_CorpusLogMTUtteranceInfo;
    }

    /**
     * Gets CorpusLogMTRemovedSymbols.
     * 
     * @return m_CorpusLogMTRemovedSymbols
     */
    public String[] getCorpusLogMTRemovedSymbols() {
        return m_CorpusLogMTRemovedSymbols;
    }

    /**
     * Gets CorpusLogMTURLInfo evaluation.
     * 
     * @return CorpusLogMTURLInfo
     */
    public boolean getCorpusLogMTURLInfo() {
        return m_CorpusLogMTURLInfo;
    }

    /**
     * Gets CorpusLogTTSBaseDirectoryName.
     * 
     * @return m_CorpusLogTTSBaseDirectoryName
     */
    public String getCorpusLogTTSBaseDirectoryName() {
        return m_CorpusLogTTSBaseDirectoryName;
    }

    /**
     * Gets CorpusLogTTSFilePrefix.
     * 
     * @return CorpusLogTTSFilePrefix
     */
    public String getCorpusLogTTSFilePrefix() {
        return m_CorpusLogTTSFilePrefix;
    }

    /**
     * Gets CorpusLogTTSUtteranceInfo.
     * 
     * @return CorpusLogTTSUtteranceInfo
     */
    public boolean getCorpusLogTTSUtteranceInfo() {
        return m_CorpusLogTTSUtteranceInfo;
    }

    /**
     * Gets CorpusLogTTSRemovedSymbols.
     * 
     * @return CorpusLogTTSRemovedSymbols
     */
    public String[] getCorpusLogTTSRemovedSymbols() {
        return m_CorpusLogTTSRemovedSymbols;
    }

    /**
     * Gets CorpusLogTTSURLInfo.
     * 
     * @return CorpusLogTTSURLInfo
     */
    public boolean getCorpusLogTTSURLInfo() {
        return m_CorpusLogTTSURLInfo;
    }

    /**
     * Gets ClientIp results.
     * 
     * @return ClientIp
     */
    public boolean getClientIp() {
        return m_ClientIp;
    }

    /**
     * Gets LogDb.
     * 
     * @return m_LogDb
     */
    public boolean getLogDb() {
        return m_LogDb;
    }

    // hmatsumo
    /**
     * Gets EffectiveDate.
     * 
     * @return EffectiveDate
     */
    public Date getEffectiveDate() {
        return m_EffectiveDate;
    }

    /**
     * Gets MessageType.
     * 
     * @return MessageType
     */
    public String getMessageType() {
        return m_MessageType;
    }

    /**
     * Gets Version.
     * 
     * @return Version
     */
    public String getVersion() {
        return m_Version;
    }

    /**
     * Gets MessageJaBody.
     * 
     * @return MessageJaBody
     */
    public String getMessageJaBody() {
        return m_MessageJaBody;
    }

    /**
     * Gets MessageEnBody.
     * 
     * @return MessageEnBody
     */
    public String getMessageEnBody() {
        return m_MessageEnBody;
    }

    /**
     * Gets MessageJaTitle.
     * 
     * @return MessageJaTitle
     */
    public String getMessageJaTitle() {
        return m_MessageJaTitle;
    }

    /**
     * Gets MessageEnTitle.
     * 
     * @return MessageEnTitle
     */
    public String getMessageEnTitle() {
        return m_MessageEnTitle;
    }

    /**
     * Gets AccessKeyUpdateMinute.
     * 
     * @return AccessKeyUpdateMinute
     */
    public long getAccessKeyUpdateMinute() {
        return m_AccessKeyUpdateMinute;
    }

    /**
     * Gets AccessKeyControl.
     * 
     * @return AccessKeyControl
     */
    public boolean getAccessKeyControl() {
        return m_AccessKeyControl;
    }

    /**
     * Gets SecureComCtrl.
     * 
     * @return SecureComCtrl
     */
    public boolean getSecureComCtrl() {
        return m_SecureComCtrl;
    }

    // hmatsumo

    // for MCML Server
    /**
     * Gets PartyRegistrationServerURL.
     * 
     * @return PartyRegistrationServerURL
     */
    public String getPartyRegistrationServerURL() {
        return m_PartyRegistrationServerURL;
    }

    /**
     * Gets PartyRegistrationServerUserID.
     * 
     * @return PartyRegistrationServerUserID
     */
    public String getPartyRegistrationServerUserID() {
        return m_PartyRegistrationServerUserID;
    }

    /**
     * Gets PartyRegistrationServerImageFileName.
     * 
     * @return PartyRegistrationServerImageFileName
     */
    public String getPartyRegistrationServerImageFileName() {
        return m_PartyRegistrationServerImageFileName;
    }

    /**
     * Gets RoutingServerURL.
     * 
     * @return RoutingServerURL
     */
    public String getRoutingServerURL() {
        return m_RoutingServerURL;
    }

    /**
     * Gets LogServerURL.
     * 
     * @return LogServerURL
     */
    public String getLogServerURL() {
        return m_LogServerURL;
    }

    /**
     * Get DM IP address
     * 
     * @return dmIp
     */
    public String getDmIp() {
        return dmIp;
    }

    /**
     * Gets list filename.
     * 
     * @return List filename.
     */
    public String getListFileName() {
        return listFileName;
    }

    /**
     * Gets timeout in milliseconds.
     * 
     * @return Timeout in milliseconds.
     */
    public int getTimeoutMilliSeconds() {
        return timeoutMilliSeconds;
    }

    /**
     * Gets ResultAdoptionMode.
     * 
     * @return ResultAdoptionMode
     */
    public AdoptationMode getResultAdoptionMode() {
        return resultAdoptionMode;
    }

    /**
     * Gets TimeoutLimitSwitch.
     * 
     * @return TimeoutLimitSwitch
     */
    public boolean getTimeoutLimitSwitch() {
        return timeoutLimitSwitch;
    }

    /**
     * Gets TimeoutLimitNumber.
     * 
     * @return TimeoutLimitNumber
     */
    public int getTimeoutLimitNumber() {
        return timeoutLimitNumber;
    }

    /**
     * Get a connecting DB URL
     * 
     * @return dbURL
     */
    public String getDbUrl() {
        return dbUrl;
    }

    /**
     * User name(DB account)
     * 
     * @return db user
     */
    public String getDbUser() {
        return dbUser;
    }

    /**
     * Password(DB account)
     * 
     * @return db password
     */
    public String getDbPassword() {
        return dbPassword;
    }

    /**
     * DB driver name
     * 
     * @return db driver
     */
    public String getDbDriver() {
        return dbDriver;
    }

    // ------------------ ------------------------
    // private member functions
    // -------------------------------------------
    // constructor
    private ControlServerProperties() {
        String filename = "ControlServer.properties";
        try {
            m_Prop = new Properties();
            m_Prop.load(this.getClass().getClassLoader()
                    .getResourceAsStream(filename));
            String temp = "";

            // for CorpusLog parameters.
            // for ASR
            m_CorpusLogASRBaseDirectoryName = m_Prop.getProperty(
                    KEY_SERVER_CORPUSLOG_ASR + KEY_BASEDIRECTORYNAME, "");
            m_CorpusLogASRFilePrefix = m_Prop.getProperty(
                    KEY_SERVER_CORPUSLOG_ASR + KEY_FILEPREFIX, "");
            m_CorpusLogASRUtteranceInfo = isValueOn(KEY_SERVER_CORPUSLOG_ASR
                    + KEY_UTTERANCEINFO, true);
            temp = m_Prop.getProperty(KEY_SERVER_CORPUSLOG_ASR
                    + KEY_REMOVEDSYMBOL, "");
            m_CorpusLogASRRemovedSymbols = (temp != null && !temp.isEmpty()) ? temp
                    .split(",") : new String[] { "" };
            m_CorpusLogASRIsUtf8OutputOn = isValueOn(KEY_RECOGNITIONRESULT
                    + KEY_UTF8, true);
            m_CorpusLogASRIsEucOutputOn = isValueOn(KEY_RECOGNITIONRESULT
                    + KEY_EUC, true);
            m_CorpusLogASRIsInputSpeechdataOutputOn = isValueOn(
                    KEY_INPUTSPEECHDATA, true);
            m_CorpusLogASRURLInfo = isValueOn(KEY_SERVER_CORPUSLOG_ASR
                    + KEY_URLINFO, false);

            // for MT
            m_CorpusLogMTBaseDirectoryName = m_Prop.getProperty(
                    KEY_SERVER_CORPUSLOG_MT + KEY_BASEDIRECTORYNAME, "");
            m_CorpusLogMTFilePrefix = m_Prop.getProperty(
                    KEY_SERVER_CORPUSLOG_MT + KEY_FILEPREFIX, "");
            m_CorpusLogMTUtteranceInfo = isValueOn(KEY_SERVER_CORPUSLOG_MT
                    + KEY_UTTERANCEINFO, true);
            temp = m_Prop.getProperty(KEY_SERVER_CORPUSLOG_MT
                    + KEY_REMOVEDSYMBOL, "");
            m_CorpusLogMTRemovedSymbols = (temp != null && !temp.isEmpty()) ? temp
                    .split(",") : new String[] { "" };
            m_CorpusLogMTURLInfo = isValueOn(KEY_SERVER_CORPUSLOG_MT
                    + KEY_URLINFO, false);

            // for TTS
            m_CorpusLogTTSBaseDirectoryName = m_Prop.getProperty(
                    KEY_SERVER_CORPUSLOG_TTS + KEY_BASEDIRECTORYNAME, "");
            m_CorpusLogTTSFilePrefix = m_Prop.getProperty(
                    KEY_SERVER_CORPUSLOG_TTS + KEY_FILEPREFIX, "");
            m_CorpusLogTTSUtteranceInfo = isValueOn(KEY_SERVER_CORPUSLOG_TTS
                    + KEY_UTTERANCEINFO, true);
            temp = m_Prop.getProperty(KEY_SERVER_CORPUSLOG_TTS
                    + KEY_REMOVEDSYMBOL, "");
            m_CorpusLogTTSRemovedSymbols = (temp != null && !temp.isEmpty()) ? temp
                    .split(",") : new String[] { "" };
            m_CorpusLogTTSURLInfo = isValueOn(KEY_SERVER_CORPUSLOG_TTS
                    + KEY_URLINFO, false);

            // for common
            temp = m_Prop.getProperty(KEY_SERVER_CORPUSLOG + "clientip",
                    VALUE_OFF);
            m_ClientIp = (temp.equalsIgnoreCase(VALUE_ON)) ? true : false;

            temp = m_Prop.getProperty(KEY_SERVER_LOGSERVER + "use", VALUE_OFF);
            m_LogDb = (temp.equalsIgnoreCase(VALUE_ON)) ? true : false;

            String d = m_Prop
                    .getProperty("informationservlet.message.effectiveDate");
            if (d != null) {
                m_EffectiveDate = new SimpleDateFormat("y/M/d").parse(d,
                        new ParsePosition(0));
            } else {
                m_EffectiveDate = null;
            }

            // for MCML Server
            m_PartyRegistrationServerURL = m_Prop.getProperty(
                    KEY_PARTYREGISTRATIONSERVER + SUBKEY_URL, "");
            m_PartyRegistrationServerUserID = m_Prop.getProperty(
                    KEY_PARTYREGISTRATIONSERVER + SUBKEY_USERID,
                    "ControlServer");
            m_PartyRegistrationServerImageFileName = m_Prop.getProperty(
                    KEY_PARTYREGISTRATIONSERVER + SUBKEY_IMAGEFILENAME, "");
            m_RoutingServerURL = m_Prop.getProperty(KEY_ROUTINGSERVER
                    + SUBKEY_URL, "");

            m_LogServerURL = m_Prop.getProperty(KEY_SERVER_LOGSERVER
                    + SUBKEY_URL, "");

            dmIp = m_Prop.getProperty(KEY_DM_IP, "");

            // list file name
            listFileName = m_Prop.getProperty(KEY_LISTFILENAME, "");

            // time out milliseconds(default is 0)
            int itemp = Integer.valueOf(m_Prop.getProperty(KEY_TIMEOUT
                    + KEY_MILLISECONDS, "0"));
            if (itemp < 0) {
                itemp = 0;
            }
            timeoutMilliSeconds = itemp;

            // result adoption mode
            itemp = Integer.valueOf(m_Prop.getProperty(KEY_RESULTADOPTIONMODE,
                    "0"));
            AdoptationMode adoptionMode;
            switch (itemp) {
            case 0:
                adoptionMode = AdoptationMode.LIST_TOP;
                break;
            case 1:
                adoptionMode = AdoptationMode.FASTEST;
                break;
            case 2:
                adoptionMode = AdoptationMode.LIST_ORDER;
                break;
            case 3:
                adoptionMode = AdoptationMode.SCORE_ORDER;
                break;
            default:
                adoptionMode = AdoptationMode.LIST_TOP; // default is LIST_TOP
            }
            resultAdoptionMode = adoptionMode;

            // timeout limit switch(default is off)
            timeoutLimitSwitch = false; // set default value
            itemp = Integer.valueOf(m_Prop.getProperty(KEY_TIMEOUT + KEY_LIMIT
                    + KEY_SWITCH, "0"));
            if (itemp == 1) {
                timeoutLimitSwitch = true;
            }

            // timeout limit number(default is 0)
            itemp = Integer.valueOf(m_Prop.getProperty(KEY_TIMEOUT + KEY_LIMIT
                    + KEY_NUMBER, "0"));
            if (itemp < 0) {
                itemp = 0;
            }
            timeoutLimitNumber = itemp;

            // DB informations
            dbUrl = m_Prop.getProperty(KEY_DB_URL, "");
            dbUser = m_Prop.getProperty(KEY_DB_USER, "");
            dbPassword = m_Prop.getProperty(KEY_DB_PASSWORD, "");
            dbDriver = m_Prop.getProperty(KEY_DB_DRIVER, "");

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    private boolean isValueOn(String key, boolean ommitedValue) {
        String temp = m_Prop.getProperty(key, "");

        // value is omitted
        if ((temp == null) || (temp.isEmpty())) {
            return ommitedValue;

            // value is on
        } else if (temp.equalsIgnoreCase(VALUE_ON)) {
            return true;
        }


        // If other than above, false.
        // value is off
        // invalid string
        return false;
    }
}
