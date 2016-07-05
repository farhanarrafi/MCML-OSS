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

package jp.go.nict.mcml.servlet.control.corpuslog;

import java.util.ArrayList;
import java.util.HashMap;

import jp.go.nict.mcml.server.asr.ASRCorpusLogger;
import jp.go.nict.mcml.server.mt.MTCorpusLogger;
import jp.go.nict.mcml.server.tts.TTSCorpusLogger;
import jp.go.nict.mcml.serverap.common.ServerApCorpusLogger;
import jp.go.nict.mcml.servlet.control.ControlServerProperties;
import jp.go.nict.mcml.xml.XMLTypeTools;

import org.apache.log4j.Logger;

import com.MCML.MCMLDoc;

/**
 * ControlServerCorpusLogger class.
 * 
 * @version 4.0
 * @since 20120921
 */
public class ControlServerCorpusLogger {
    private static final Logger LOG = Logger
            .getLogger(ControlServerCorpusLogger.class.getName());
    // ------------------------------------------
    // private member variables(instance field)
    // ------------------------------------------
    private static final ControlServerCorpusLogger M_INSTANCE = new ControlServerCorpusLogger();
    private HashMap<String, ControlServerCorpusLogInfo> m_LogInfoMap;
    private HashMap<Integer, ServerApCorpusLogger> m_CorpusLoggerMap;
    private ASRCorpusLogger m_ASRCorpusLogger;
    private MTCorpusLogger m_MTCorpusLogger;
    private TTSCorpusLogger m_TTSCorpusLogger;
    private int m_CorpusLogInfoID;
    private static final Object M_CORPUS_LOGGER_MAP_LOCK = new Object();

    // ------------------------------------------
    // public member functions
    // ------------------------------------------
    /** Gets instance. */
    public static ControlServerCorpusLogger getInstance() {
        return M_INSTANCE;
    }

    /**
     * Performs initialization.
     * 
     * @param prop
     */
    public void initialize(ControlServerProperties prop) {
        m_LogInfoMap = new HashMap<String, ControlServerCorpusLogInfo>();
        m_CorpusLoggerMap = new HashMap<Integer, ServerApCorpusLogger>();
        m_CorpusLogInfoID = 0;

        // create engine server corpus logger
        m_ASRCorpusLogger = new ASRCorpusLogger(
                prop.getCorpusLogASRUtteranceInfo(),
                prop.getCorpusLogASRBaseDirectoryName(),
                null, // Language
                prop.getCorpusLogASRFilePrefix(),
                prop.isCorpusLogASREucOutputOn(),
                prop.isCorpusLogASRUtf8OutputOn(),
                prop.isCorpusLogASRInputSpeechdataOutputOn(),
                true, // ModelNameOutput(always true)
                prop.getCorpusLogASRRemovedSymbols(),
                prop.getCorpusLogASRURLInfo());

        m_MTCorpusLogger = new MTCorpusLogger(
                prop.getCorpusLogMTUtteranceInfo(),
                prop.getCorpusLogMTBaseDirectoryName(),
                null, // Language
                prop.getCorpusLogMTFilePrefix(),
                prop.getCorpusLogMTRemovedSymbols(),
                prop.getCorpusLogMTURLInfo());

        m_TTSCorpusLogger = new TTSCorpusLogger(
                prop.getCorpusLogTTSUtteranceInfo(),
                prop.getCorpusLogTTSBaseDirectoryName(),
                null, // Language
                prop.getCorpusLogTTSFilePrefix(),
                prop.getCorpusLogTTSRemovedSymbols(),
                prop.getCorpusLogTTSURLInfo());
    }

    /**
     * set arrived time for binary
     * 
     * @param sessionId
     */
    public void setArrivedTimeForBinary(String sessionId) {
        // get Arrived Time.
        long arrivedTime = System.currentTimeMillis();

        synchronized (m_LogInfoMap) {
            // is CorpusLogInfo?
            ControlServerCorpusLogInfo corpusLogInfo = null;
            if (m_LogInfoMap.containsKey(sessionId)) {
                corpusLogInfo = m_LogInfoMap.get(sessionId);
            } else {
                // not exist CorpusLogInfo instance(= Lump Data).
                // create CorpusLogInfo instance.
                corpusLogInfo = new ControlServerCorpusLogInfo();

                // set CorpusLogInfo Map.
                m_LogInfoMap.put(sessionId, corpusLogInfo);
            }

            if (corpusLogInfo.getFirstFrameArrivedTime() == 0) {
                // set First SpeechDataFrame received Time(= Multi Data)
                corpusLogInfo.setFirstFrameArrivedTime(arrivedTime);
            }
            // set Last SpeechDataFrame received Time.
            corpusLogInfo.setLastFrameArrivedTime(arrivedTime);
        }
    }

    /**
     * set arrived time for request
     * 
     * @param sessionId
     */
    public void setArrivedTimeForRequest(String sessionId) {
        // get Arrived Time.
        long arrivedTime = System.currentTimeMillis();

        synchronized (m_LogInfoMap) {
            // is CorpusLogInfo?
            if (m_LogInfoMap.containsKey(sessionId)) {
                ControlServerCorpusLogInfo corpusLogInfo = m_LogInfoMap
                        .get(sessionId);

                // set Last DataFrame received Time.
                corpusLogInfo.setLastFrameArrivedTime(arrivedTime);
            }
        }
    }

    /** set information from inputMCML */
    public void setInputMCMLInfo(MCMLDoc inputMCMLDoc, int corpusLogInfoID) {
        ServerApCorpusLogger corpusLogger = getCorpusLogger(corpusLogInfoID);
        corpusLogger.setInputMCMLInfo(inputMCMLDoc, corpusLogInfoID);
    }

    /**
     * Gets WaveData.
     * 
     * @param corpusLogInfoID
     */
    public ArrayList<byte[]> getWaveData(int corpusLogInfoID) {
        ASRCorpusLogger corpusLogger = (ASRCorpusLogger) (getCorpusLogger(corpusLogInfoID));
        return corpusLogger.getWaveData(corpusLogInfoID);
    }

    /**
     * set wave data
     * 
     * @param waveData
     * @param corpusLogInfoID
     */
    public void setWaveData(ArrayList<byte[]> waveData, int corpusLogInfoID) {
        ServerApCorpusLogger corpusLogger = getCorpusLogger(corpusLogInfoID);
        corpusLogger.setWaveData(waveData, corpusLogInfoID);
    }

    /**
     * set information from outputMCML
     * 
     * @param urlList
     * @param outputMCMLDoc
     * @param corpusLogInfoID
     */
    public void setOutputMCMLInfo(ArrayList<String> urlList,
            MCMLDoc outputMCMLDoc, int corpusLogInfoID) {
        ServerApCorpusLogger corpusLogger = getCorpusLogger(corpusLogInfoID);
        corpusLogger.setOutputMCMLInfo(outputMCMLDoc, corpusLogInfoID);
        corpusLogger.setDestinationURL(urlList, corpusLogInfoID);
        corpusLogger.writeCorpusLogInfo(corpusLogInfoID);
        removeCorpusLogger(corpusLogInfoID);
    }

    /**
     * set process complete time
     * 
     * @param corpusLogInfoID
     */
    public void setCompleteTime(int corpusLogInfoID) {
        ServerApCorpusLogger corpusLogger = getCorpusLogger(corpusLogInfoID);
        corpusLogger.setCompleteTime(corpusLogInfoID);
    }

    /**
     * set first frame arrived time
     * 
     * @param corpusLogInfoID
     * @param fristFrameArrivedTime
     */
    public void setFirstFrameArrivedTime(int corpusLogInfoID,
            long fristFrameArrivedTime) {
        ServerApCorpusLogger corpusLogger = getCorpusLogger(corpusLogInfoID);
        corpusLogger.setFirstFrameArrivedTime(corpusLogInfoID,
                fristFrameArrivedTime);
    }

    /**
     * set last frame arrived time
     * 
     * @param corpusLogInfoID
     */
    public void setLastFrameArrivedTime(int corpusLogInfoID) {
        ServerApCorpusLogger corpusLogger = getCorpusLogger(corpusLogInfoID);
        corpusLogger.setLastFrameArrivedTime(corpusLogInfoID);
    }

    /**
     * set last frame arrived time
     * 
     * @param corpusLogInfoID
     * @param fristFrameArrivedTime
     */
    public void setLastFrameArrivedTime(int corpusLogInfoID,
            long fristFrameArrivedTime) {
        ServerApCorpusLogger corpusLogger = getCorpusLogger(corpusLogInfoID);
        corpusLogger.setLastFrameArrivedTime(corpusLogInfoID,
                fristFrameArrivedTime);
    }

    /**
     * set Client IP Address
     * 
     * @param sessionId
     * @param clientIP
     */
    public void setClientIP(String sessionId, String clientIP) {
        synchronized (m_LogInfoMap) {
            // is CorpusLogInfo?
            if (m_LogInfoMap.containsKey(sessionId)) {
                // set Client IP Address
                m_LogInfoMap.get(sessionId).setClientIP(clientIP);
            }
        }
    }

    /**
     * get Client IP Address
     * 
     * @param sessionId
     * @return Client IP Address
     */
    public String getClientIP(String sessionId) {
        String clientIP = "";

        synchronized (m_LogInfoMap) {
            // is CorpusLogInfo?
            if (m_LogInfoMap.containsKey(sessionId)) {
                // get Client IP Address.
                clientIP = (m_LogInfoMap.get(sessionId).getClientIP());
            }
        }

        return clientIP;
    }

    /**
     * get first frame arrived time
     * 
     * @param sessionId
     * @return first frame arrived time
     */
    public long getFirstFrameArrivedTime(String sessionId) {
        long arrivedTime = 0;

        synchronized (m_LogInfoMap) {
            // is CorpusLogInfo?
            if (m_LogInfoMap.containsKey(sessionId)) {
                // get First Frame Arrived Time.
                arrivedTime = (m_LogInfoMap.get(sessionId)
                        .getFirstFrameArrivedTime());
            }
        }
        return arrivedTime;
    }

    /**
     * get last frame arrived time
     * 
     * @param sessionId
     * @return last frame arrived time
     */
    public long getLastFrameArrivedTime(String sessionId) {
        long arrivedTime = 0;

        synchronized (m_LogInfoMap) {
            // is CorpusLogInfo?
            if (m_LogInfoMap.containsKey(sessionId)) {
                // get Last Frame Arrived Time.
                arrivedTime = (m_LogInfoMap.get(sessionId)
                        .getLastFrameArrivedTime());
            }
        }
        return arrivedTime;
    }

    /**
     * create corpus log information
     * 
     * @param inputMCMLDoc
     * @return currentCorpusLogInfoID
     */
    public int createCorpusLogInformation(MCMLDoc inputMCMLDoc) {
        // Note:In this function "m_CorpusLoggerMapLock" does the exclusive
        // access control of "m_CorpusLogInfoID" in addition to
        // "m_CorpusLoggerMap",too.
        // Be careful because it does't work normally when removing
        // "m_CorpusLogInfoID" from the exclusive access control.
        synchronized (M_CORPUS_LOGGER_MAP_LOCK) {
            // get current corpus log information id
            int currentCorpusLogInfoID = m_CorpusLogInfoID;

            // update corpus log information id
            if (m_CorpusLogInfoID == Integer.MAX_VALUE) {
                m_CorpusLogInfoID = 0;
            } else {
                m_CorpusLogInfoID++;
            }

            // select corpus logger object
            ServerApCorpusLogger corpusLogger = selectCorpusLogger(inputMCMLDoc);

            // set corpus logger to corpus logger map
            if (!m_CorpusLoggerMap.containsKey(currentCorpusLogInfoID)) {
                m_CorpusLoggerMap.put(currentCorpusLogInfoID, corpusLogger);
            }
            return currentCorpusLogInfoID;
        }
    }

    /**
     * remove log information map on session ID
     * 
     * @param sessionId
     */
    public void removeLogInfoMap(String sessionId) {
        synchronized (m_LogInfoMap) {
            if (m_LogInfoMap.containsKey(sessionId)) {
                m_LogInfoMap.remove(sessionId);
            }
        }
    }

    // ------------------------------------------
    // private member functions
    // ------------------------------------------
    // constructor
    private ControlServerCorpusLogger() {
    }

    // select corpus logger
    private ServerApCorpusLogger selectCorpusLogger(MCMLDoc inputMCMLDoc) {
        try {
            if (inputMCMLDoc == null
                    || (!XMLTypeTools.hasRequest(inputMCMLDoc))) {
                return null; // no process
            }
            if (XMLTypeTools
                    .serviceIsASR(XMLTypeTools.getService(inputMCMLDoc))) {
                return (m_ASRCorpusLogger);
            } else if (XMLTypeTools.serviceIsMT(XMLTypeTools
                    .getService(inputMCMLDoc))) {
                return (m_MTCorpusLogger);
            } else if (XMLTypeTools.serviceIsTTS(XMLTypeTools
                    .getService(inputMCMLDoc))) {
                return (m_TTSCorpusLogger);
            } else {
                return null; // no process
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return null; // no process
        }
    }

    // get corpus logger
    private ServerApCorpusLogger getCorpusLogger(int corpusLogInfoID) {
        ServerApCorpusLogger corpusLogger = null;
        synchronized (M_CORPUS_LOGGER_MAP_LOCK) {
            if (m_CorpusLoggerMap.containsKey(corpusLogInfoID)) {
                corpusLogger = m_CorpusLoggerMap.get(corpusLogInfoID);
            }
            return corpusLogger;
        }
    }

    // remove corpus log information id to corpus logger map
    private void removeCorpusLogger(int corpusLogInfoID) {
        synchronized (M_CORPUS_LOGGER_MAP_LOCK) {
            if (m_CorpusLoggerMap.containsKey(corpusLogInfoID)) {
                m_CorpusLoggerMap.remove(corpusLogInfoID);
            }
        }
    }
}
