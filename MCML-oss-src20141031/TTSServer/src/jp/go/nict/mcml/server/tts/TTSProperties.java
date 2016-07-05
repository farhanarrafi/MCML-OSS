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

import java.util.ArrayList;

import jp.go.nict.mcml.serverap.common.ServerApProperties;
import jp.go.nict.mcml.servlet.MCMLStatics;

/**
 * TTSProperties class.
 *
 *
 */
public class TTSProperties extends ServerApProperties {
    // ------------------------------------------
    // private member constants
    // ------------------------------------------
    private static final String KEY_ENGINE_TTS = KEY_ENGINE + "tts";
    private static final String KEY_HOST = "host";
    private static final String KEY_PORT = "port";
    private static final String KEY_CLIENTHOST = "clienthost";
    private static final String KEY_CLIENTPORT = "clientport";
    private static final String KEY_BYTES_PER_FRAME = "bytesperframe";
    private static final int QUARTER_INT = 500000000;
    private static final String KEY_CACHE = KEY_ENGINE + "cache";
    private static final String KEY_CACHE_BASEDIRECTRYNAME = "basedirectryname";
    private static final String KEY_CACHEDATALISTFILENAME = "cachedatalistfilename";
    private static final String KEY_MAXCACHECOUNT = "maxcachecount";
    private static final String KEY_DELETECOUNT = "deletecount";
    private static final int LIMIT_MAXCACHECOUNT = 99999;
    private static final String KEY_RATECHANGE = KEY_ENGINE + "changerate";
    private static final String KEY_ENGINE_LANGUAGE1_COUNTRYCODE = KEY_ENGINE_LANGUAGE1
            + ".countrycode";
    private static final String KEY_ENGINE_VOICEFONTTABLEFILENAME = KEY_ENGINE
            + "voicefonttablefilename";
    private static final String KEY_RATEVALUE = KEY_ENGINE + "rate";
    private static final String KEY_ENGINE_AUDIO = KEY_ENGINE + "audio.";
    private static final String KEY_ENGINE_AUDIO_FORMAT = KEY_ENGINE_AUDIO
            + "format";
    private static final String KEY_ENGINE_AUDIO_ENDIAN = KEY_ENGINE_AUDIO
            + "endian";
    private static final String KEY_ENGINE_AUDIO_FREQUENCY = KEY_ENGINE_AUDIO
            + "frequency";
    private static final String KEY_ENGINE_AUDIO_VALUETYPE = KEY_ENGINE_AUDIO
            + "valuetype";
    private static final String KEY_ENGINE_AUDIO_BITRATE = KEY_ENGINE_AUDIO
            + "bitrate";
    private static final String KEY_ENGINE_AUDIO_CHANNELQTY = KEY_ENGINE_AUDIO
            + "channelqty";

    // ------------------------------------------
    // protected member variables(class field)
    // ------------------------------------------
    protected static final TTSProperties M_INSTANCE = new TTSProperties();

    // ------------------------------------------
    // private member variables(instance field)
    // ------------------------------------------
    private ArrayList<TTSParam> m_TTSParamList;
    private boolean m_IsCacheOn;
    private String m_CacheBaseDirectryName;
    private String m_CacheDataListFileName;
    private int m_MaxCacheCount;
    private int m_DeleteCount;
    private boolean m_IsRateChange;
    private String m_Language1CountryCode;
    private String m_VoiceFontFileName;
    private float m_RateValue;
    private String m_AudioFormat;
    private String m_AudioEndian;
    private String m_AudioFrequency;
    private String m_AudioValueType;
    private int m_AudioBitRate;
    private int m_AudioChannelQty;

    /**
     * Gets instance.
     *
     * @return Instance
     */
    // ------------------------------------------
    // public member functions
    // ------------------------------------------
    public static TTSProperties getInstance() {
        return M_INSTANCE;
    }

    /** Reads property. */
    @Override
    public void readProperties(String fileName) throws Exception {
        super.readProperties(fileName);

        // check ConnectRetryTimes and ConnectRetryInterval.
        checkParameter();

        // read and check Cache Parameters
        readCacheParameters();

        // read rate change Parameters
        readRateParameters();

        // read audio Parameters
        readAudioParameters();

        // Language1 CountryCode for Engine
        m_Language1CountryCode = m_Properties
                .getProperty(KEY_ENGINE_LANGUAGE1_COUNTRYCODE);
        if (m_Language1CountryCode == null || m_Language1CountryCode.isEmpty()) {
            System.out.println("Invalid Parameter: "
                    + KEY_ENGINE_LANGUAGE1_COUNTRYCODE);
            throw new Exception("Invalid Parameter: "
                    + KEY_ENGINE_LANGUAGE1_COUNTRYCODE);
        }

        // VoiceFont FileName for Engine
        m_VoiceFontFileName = m_Properties
                .getProperty(KEY_ENGINE_VOICEFONTTABLEFILENAME);
        if (m_VoiceFontFileName == null || m_VoiceFontFileName.isEmpty()) {
            m_VoiceFontFileName = "";

        }

        // loop by Engine number
        for (int i = 1; i <= m_EngineMaxNumber; i++) {
            // Parameter Manager Class Create
            TTSParam params = new TTSParam();

            String paramKey = KEY_ENGINE_TTS + String.valueOf(i) + ".";

            // Engine Host
            String temp = m_Properties.getProperty(paramKey + KEY_HOST);
            if (temp == null || temp.isEmpty()) {
                // read Next Engine Parameters.
                System.out.println("Invalid Parameter: " + paramKey + "."
                        + KEY_HOST);
                continue;
            }
            params.setEngineHost(temp);

            // Port connecting Engine
            temp = m_Properties.getProperty(paramKey + KEY_PORT);
            if (temp == null || temp.isEmpty()) {
                // read Next Engine Parameters.
                System.out.println("Invalid Parameter: " + paramKey + "."
                        + KEY_PORT);
                continue;
            }
            int portNo = Integer.parseInt(temp);
            if (portNo < 0 || PORT_NUMBER_MAX < portNo) {
                // read Next Engine Parameters.
                System.out.println("Invalid Parameter: " + paramKey + "."
                        + KEY_PORT);
                continue;
            }
            params.setEnginePort(portNo);

            // Engine Client Host
            temp = m_Properties.getProperty(paramKey + KEY_CLIENTHOST);
            if (temp == null || temp.isEmpty()) {
            } else {
                params.setEngineClientHost(temp);
            }

            // Client Port connecting Engine
            temp = m_Properties.getProperty(paramKey + KEY_CLIENTPORT);
            if (temp == null || temp.isEmpty()) {
            } else {
                portNo = Integer.parseInt(temp);
            }
            if (PORT_NUMBER_MAX < portNo) {
                // read Next Engine Parameters.
                System.out.println("Invalid Parameter: " + paramKey + "."
                        + KEY_CLIENTPORT);
                continue;
            }
            params.setEngineClientPort(portNo);

            // Bytes per frame
            temp = m_Properties.getProperty(paramKey + KEY_BYTES_PER_FRAME);
            if (temp == null || temp.isEmpty()) {
                // read Next Engine Parameters.
                System.out.println("Invalid Parameter: " + paramKey + "."
                        + KEY_BYTES_PER_FRAME);
                continue;
            }
            int bytesPerFrame = Integer.parseInt(temp);
            if (bytesPerFrame < 0 || QUARTER_INT < bytesPerFrame) {
                // read Next Engine Parameters.
                System.out.println("Invalid Parameter: " + paramKey + "."
                        + KEY_BYTES_PER_FRAME);
                continue;
            }
            params.setBytesPerFrame(bytesPerFrame);

            // add TTS's Parameter.
            m_TTSParamList.add(params);
        }

        // normal end
        return;
    }

    /**
     * get Engine(TTS) Parameter Manager class
     *
     * @param engineNumber
     * @return TTSParam
     */
    public TTSParam getTTSParam(int engineNumber) {
        return m_TTSParamList.get(engineNumber);
    }

    /** get valid engine number */
    @Override
    public int getEngineNumber() {
        return m_TTSParamList.size();
    }

    /**
     * Gets CacheBaseDirectryName.
     *
     * @return CacheBaseDirectryName
     */
    public String getCacheBaseDirectryName() {
        return m_CacheBaseDirectryName;
    }

    /**
     * Gets getCacheDataListFileName.
     *
     * @return getCacheDataListFileName
     */
    public String getCacheDataListFileName() {
        return m_CacheDataListFileName;
    }

    /**
     * Gets DeleteCount.
     *
     * @return DeleteCount
     */
    public int getDeleteCount() {
        return m_DeleteCount;
    }

    /**
     * Determines whether CacheOn or not.
     *
     * @return m_IsCacheOn
     */
    public boolean isCacheOn() {
        return m_IsCacheOn;
    }

    /**
     * Determines whether RateChange or not.
     *
     * @return m_IsRateChange
     */
    public boolean isRateChange() {
        return m_IsRateChange;
    }

    /**
     * Gets MaxCacheCount.
     *
     * @return MaxCacheCount
     */
    public int getMaxCacheCount() {
        return m_MaxCacheCount;
    }

    /**
     * Gets Language1CountryCode.
     *
     * @return Language1CountryCode
     */
    public String getLanguage1CountryCode() {
        return m_Language1CountryCode;
    }

    /**
     * Gets VoiceFontFileName.
     *
     * @return VoiceFontFileName
     */
    public String getVoiceFontFileName() {
        return m_VoiceFontFileName;
    }

    /**
     * Gets RateValue.
     *
     * @return RateValue
     */
    public float getRateValue() {
        return m_RateValue;
    }

    /**
     * Gets AudioFormat.
     *
     * @return AudioFormat
     */
    public String getAudioFormat() {
        return m_AudioFormat;
    }

    /**
     * Gets AudioEndian.
     *
     * @return AudioEndian
     */
    public String getAudioEndian() {
        return m_AudioEndian;
    }

    /**
     * Gets AudioFrequency.
     *
     * @return AudioFrequency
     */
    public String getAudioFrequency() {
        return m_AudioFrequency;
    }

    /**
     * Gets AudioValueType.
     *
     * @return AudioValueType
     */
    public String getAudioValueType() {
        return m_AudioValueType;
    }

    /**
     * Gets AudioBitRate.
     *
     * @return AudioBitRate
     */
    public int getAudioBitRate() {
        return m_AudioBitRate;
    }

    /**
     * Gets AudioChannelQty.
     *
     * @return AudioChannelQty
     */
    public int getAudioChannelQty() {
        return m_AudioChannelQty;
    }

    // ------------------------------------------
    // private member functions
    // ------------------------------------------
    private TTSProperties() {
        m_TTSParamList = new ArrayList<TTSParam>();
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

        return;
    }

    private void readRateParameters() {
        // read RateChange On/Off.
        m_IsRateChange = readOnOff(KEY_RATECHANGE);

        // read RateValue
        m_RateValue = getRate();
    }

    private void readCacheParameters() {
        // read cache On/Off.
        m_IsCacheOn = readOnOff(KEY_CACHE);
        if (!m_IsCacheOn) {
            // Cache Off.
            clearCacheParameter();
            return;
        }

        // read Cache BaseDirectryName.
        String temp = m_Properties.getProperty(KEY_CACHE + "."
                + KEY_CACHE_BASEDIRECTRYNAME);
        if (temp == null) {
            temp = "";
        }
        m_CacheBaseDirectryName = temp;

        // read CacheDataListFileName.
        temp = m_Properties.getProperty(KEY_CACHE + "."
                + KEY_CACHEDATALISTFILENAME);
        if (!checkCacheParameter(temp, KEY_CACHE + "."
                + KEY_CACHEDATALISTFILENAME)) {
            return;
        }
        m_CacheDataListFileName = temp;

        // read MaxCacheCount.
        temp = m_Properties.getProperty(KEY_CACHE + "." + KEY_MAXCACHECOUNT);
        if (!checkCacheParameter(temp, KEY_CACHE + "." + KEY_MAXCACHECOUNT)) {
            return;
        }
        m_MaxCacheCount = Integer.parseInt(temp);
        if (m_MaxCacheCount <= 0 || LIMIT_MAXCACHECOUNT < m_MaxCacheCount) {
            System.out.println("Invalid Parameter: " + KEY_CACHE + "."
                    + KEY_MAXCACHECOUNT);
            System.out.println(KEY_MAXCACHECOUNT + "is out of range.");
            clearCacheParameter();
            return;
        }

        // read DeleteCount.
        temp = m_Properties.getProperty(KEY_CACHE + "." + KEY_DELETECOUNT);
        if (!checkCacheParameter(temp, KEY_CACHE + "." + KEY_DELETECOUNT)) {
            return;
        }
        m_DeleteCount = Integer.parseInt(temp);
        if (m_DeleteCount <= 0 || LIMIT_MAXCACHECOUNT < m_DeleteCount) {
            System.out.println("Invalid Parameter: " + KEY_CACHE + "."
                    + KEY_DELETECOUNT);
            System.out.println(KEY_DELETECOUNT + "is out of range.");
            clearCacheParameter();
            return;
        }
        // normal end
        return;
    }

    private boolean checkCacheParameter(String checkValue, String keyName) {
        if (checkValue == null || checkValue.isEmpty()) {
            // Cache Off.
            System.out.println("Invalid Parameter: " + keyName);
            clearCacheParameter();
            return false;
        }
        return true;
    }

    private void clearCacheParameter() {
        m_IsCacheOn = false;
        m_CacheBaseDirectryName = null;
        m_CacheDataListFileName = null;
        m_MaxCacheCount = -1;
        m_DeleteCount = -1;
    }

    private void readAudioParameters() throws Exception {
        m_AudioFormat = m_Properties.getProperty(KEY_ENGINE_AUDIO_FORMAT,
                MCMLStatics.AUDIO_RAW);
        checkAudioType(m_AudioFormat);
        m_AudioEndian = m_Properties.getProperty(KEY_ENGINE_AUDIO_ENDIAN,
                MCMLStatics.ENDIAN_BIG);
        checkEndian(m_AudioEndian);
        m_AudioFrequency = m_Properties.getProperty(KEY_ENGINE_AUDIO_FREQUENCY,
                MCMLStatics.SAMPLING_FREQUENCY_16K);
        checkSamplingFrequency(m_AudioFrequency);
        m_AudioValueType = m_Properties.getProperty(KEY_ENGINE_AUDIO_VALUETYPE,
                MCMLStatics.SIGNAL_VALUE_TYPE_INTEGER);
        m_AudioBitRate = Integer.valueOf(m_Properties.getProperty(
                KEY_ENGINE_AUDIO_BITRATE, MCMLStatics.SIGNAL_BIT_RATE));
        m_AudioChannelQty = Integer.valueOf(m_Properties.getProperty(
                KEY_ENGINE_AUDIO_CHANNELQTY, MCMLStatics.SIGNAL_CHANNEL_QTY));

    }

    private float getRate() {
        String temp = m_Properties.getProperty(KEY_RATEVALUE,
                String.valueOf(SSMLStatics.SPEAK_RATE));
        float rate = Float.valueOf(temp);
        if ((rate != 0.0) && (rate < 0.5f || rate > 2.0f)) {
            System.out.println(KEY_RATEVALUE + " is out of range");
            rate = SSMLStatics.SPEAK_RATE;
        }

        // normal end
        return rate;
    }

    private void checkAudioType(String audio) throws Exception {
        if (!audio.equalsIgnoreCase(MCMLStatics.AUDIO_RAW)
                && !audio.equalsIgnoreCase(MCMLStatics.AUDIO_ADPCM)
                && !audio.equalsIgnoreCase(MCMLStatics.AUDIO_SPEEX)) {
            System.out.println("Invalid Parameter: " + KEY_ENGINE_AUDIO_FORMAT);
            throw new Exception("Invalid Parameter: " + KEY_ENGINE_AUDIO_FORMAT);
        }

        // normal end
        return;
    }

    private void checkEndian(String endian) throws Exception {

        if (!endian.equalsIgnoreCase(MCMLStatics.ENDIAN_BIG)
                && !endian.equalsIgnoreCase(MCMLStatics.ENDIAN_LITTLE)) {
            System.out.println("Invalid Parameter: " + KEY_ENGINE_AUDIO_ENDIAN);
            throw new Exception("Invalid Parameter: " + KEY_ENGINE_AUDIO_ENDIAN);
        }

        // normal end
        return;
    }

    private void checkSamplingFrequency(String samplingFrequency)
            throws Exception {
        if (!samplingFrequency
                .equalsIgnoreCase(MCMLStatics.SAMPLING_FREQUENCY_16K)) {
            System.out.println("Invalid Parameter: "
                    + KEY_ENGINE_AUDIO_FREQUENCY);
            throw new Exception("Invalid Parameter: "
                    + KEY_ENGINE_AUDIO_FREQUENCY);
        }

        // normal end
        return;
    }

}
