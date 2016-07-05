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

import jp.go.nict.mcml.serverap.common.CorpusLogInfo;

import com.MCML.DataType;

/**
 * TTS Corpus Log Info
 * 
 * @version 4.0
 * @since 20120921
 */
public class TTSCorpusLogInfo extends CorpusLogInfo {
    // ------------------------------------------
    // private member variables
    // ------------------------------------------
    private ArrayList<byte[]> m_OutputData;
    private long m_SSCompleteTime;
    private int m_SamplingFrequency;
    private String m_OutputAudioFormat;
    private long m_OutputUtteranceLength;
    private DataType m_DataType;
    private String m_VoiceFontID;
    private float m_F0;
    private float m_Rate;

    // ------------------------------------------
    // public member functions
    // ------------------------------------------
    /**
     * Constructor
     */
    public TTSCorpusLogInfo() {
        super();
        m_OutputData = new ArrayList<byte[]>();
        m_SSCompleteTime = 0;
        m_SamplingFrequency = 0;
        m_OutputAudioFormat = "";
        m_OutputUtteranceLength = 0;
        m_DataType = null;
        m_VoiceFontID = "";
        m_F0 = Float.NaN;
        m_Rate = Float.NaN;
    }

    /**
     * Gets OutputData.
     * 
     * @return m_OutputData
     */
    // ------------------------------------------
    // setter and getter
    // ------------------------------------------
    public ArrayList<byte[]> getOutputData() {
        return m_OutputData;
    }

    /**
     * Sets getSSCompleteTime.
     * 
     * @param outputData
     */
    public void setOutputData(ArrayList<byte[]> outputData) {
        m_OutputData.addAll(outputData);
    }

    /**
     * Gets SSCompleteTime.
     * 
     * @return m_SSCompleteTime
     */
    public long getSSCompleteTime() {
        return m_SSCompleteTime;
    }

    /**
     * Sets SSCompleteTime.
     * 
     * @param completeTime
     */
    public void setSSCompleteTime(long completeTime) {
        m_SSCompleteTime = completeTime;
    }

    /**
     * Gets SamplingFrequency.
     * 
     * @return m_SamplingFrequency
     */
    public int getSamplingFrequency() {
        return m_SamplingFrequency;
    }

    /**
     * Sets SamplingFrequency.
     * 
     * @param samplingFrequency
     */
    public void setSamplingFrequency(int samplingFrequency) {
        m_SamplingFrequency = samplingFrequency;
    }

    /**
     * Gets OutputAudioFormat.
     * 
     * @return m_OutputAudioFormat
     */
    public String getOutputAudioFormat() {
        return m_OutputAudioFormat;
    }

    /**
     * Sets OutputAudioFormat.
     * 
     * @param outputAudioFormat
     */
    public void setOutputAudioFormat(String outputAudioFormat) {
        m_OutputAudioFormat = outputAudioFormat;
    }

    /**
     * Gets OutputUtteranceLength.
     * 
     * @return m_OutputUtteranceLength
     */
    public long getOutputUtteranceLength() {
        return m_OutputUtteranceLength;
    }

    /**
     * Sets OutputUtteranceLength.
     * 
     * @param outputUtteranceLength
     */
    public void setOutputUtteranceLength(long outputUtteranceLength) {
        m_OutputUtteranceLength = outputUtteranceLength;
    }

    /**
     * Gets DataType.
     * 
     * @return m_DataType
     */
    public DataType getDataType() {
        return m_DataType;
    }

    /**
     * Sets DataType.
     * 
     * @param dataType
     */
    public void setDataType(DataType dataType) {
        m_DataType = dataType;
    }

    /**
     * Gets VoiceFont ID.
     * 
     * @return m_VoiceFontID
     */
    public String getVoiceFontID() {
        return m_VoiceFontID;
    }

    /**
     * Sets VoiceFont ID
     * 
     * @param voiceFontId
     */
    public void setVoiceFontID(String voiceFontId) {
        this.m_VoiceFontID = voiceFontId;
    }

    /**
     * Gets F0.
     * 
     * @return m_F0
     */
    public float getF0() {
        return m_F0;
    }

    /**
     * Sets F0.
     * 
     * @param f0
     */
    public void setF0(float f0) {
        this.m_F0 = f0;
    }

    /**
     * Gets Rate.
     * 
     * @return m_Rate
     */
    public float getRate() {
        return m_Rate;
    }

    /**
     * Sets Rate.
     * 
     * @param rate
     */
    public void setRate(float rate) {
        this.m_Rate = rate;
    }

}
