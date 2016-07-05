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

import jp.go.nict.mcml.serverap.common.CorpusLogInfo;

import com.MCML.DataType;

/**
 * ASRCorpusLogInfo class. (For ASR server)
 * 
 */
public class ASRCorpusLogInfo extends CorpusLogInfo {
    // ------------------------------------------
    // private member variable
    // ------------------------------------------
    private ArrayList<byte[]> m_InputData;
    private long m_SRCompleteTime;
    private String m_Native;
    private String m_InputAudioFormat;
    private long m_UtteranceLength;
    private long m_NetworkDelay;
    private String m_SpeechDataFileName;
    private DataType m_DataType;
    private long m_RecievedTimeOnServlet;
    private String m_AcousticModelName;
    private String m_NICTlatticeModel;
    private String m_NICTrescoreModel;

    // ------------------------------------------
    // constructer
    // ------------------------------------------
    ASRCorpusLogInfo() {

        super();

        m_InputData = new ArrayList<byte[]>();
        m_SRCompleteTime = 0;
        m_Native = "";
        m_InputAudioFormat = "";
        m_UtteranceLength = 0;
        m_NetworkDelay = 0;
        m_SpeechDataFileName = "";
        m_DataType = null;
        m_RecievedTimeOnServlet = 0;
    }

    // ------------------------------------------
    // setter and getter
    // ------------------------------------------
    /**
     * Gets InputData.
     * 
     * @return m_InputData
     */
    public ArrayList<byte[]> getInputData() {
        return m_InputData;
    }

    /**
     * Sets InputData.
     * 
     * @param inputData
     */
    public void setInputData(ArrayList<byte[]> inputData) {
        m_InputData.addAll(inputData);
    }

    /**
     * Gets InputAudioFormat.
     * 
     * @return m_InputAudioFormat
     */
    public String getInputAudioFormat() {
        return m_InputAudioFormat;
    }

    /**
     * Sets InputAudioFormat.
     * 
     * @param inputAudioFormat
     */
    public void setInputAudioFormat(String inputAudioFormat) {
        m_InputAudioFormat = inputAudioFormat;
    }

    /**
     * Gets Native.
     * 
     * @return m_Native
     */
    public String getNative() {
        return m_Native;
    }

    /**
     * Sets Native.
     * 
     * @param nativeVal
     */
    public void setNative(String nativeVal) {
        m_Native = nativeVal;
    }

    /**
     * Gets NetworkDelay.
     * 
     * @return m_NetworkDelay
     */
    public long getNetworkDelay() {
        return m_NetworkDelay;
    }

    /**
     * Sets NetworkDelay.
     * 
     * @param networkDelay
     */
    public void setNetworkDelay(long networkDelay) {
        m_NetworkDelay = networkDelay;
    }

    /**
     * Gets SpeechDataFileName.
     * 
     * @return m_SpeechDataFileName
     */
    public String getSpeechDataFileName() {
        return m_SpeechDataFileName;
    }

    /**
     * Sets SpeechDataFileName.
     * 
     * @param speechDataFileName
     */
    public void setSpeechDataFileName(String speechDataFileName) {
        m_SpeechDataFileName = speechDataFileName;
    }

    /**
     * Gets SRCompleteTime.
     * 
     * @return m_SRCompleteTime
     */
    public long getSRCompleteTime() {
        return m_SRCompleteTime;
    }

    /**
     * Sets SRCompleteTime.
     * 
     * @param completeTime
     */
    public void setSRCompleteTime(long completeTime) {
        m_SRCompleteTime = completeTime;
    }

    /**
     * Gets UtteranceLength.
     * 
     * @return m_UtteranceLength
     */
    public long getUtteranceLength() {
        return m_UtteranceLength;
    }

    /**
     * Sets UtteranceLength.
     * 
     * @param utteranceLength
     */
    public void setUtteranceLength(long utteranceLength) {
        m_UtteranceLength = utteranceLength;
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
     * Gets RecievedTimeOnServlet.
     * 
     * @return m_RecievedTimeOnServlet
     */
    public long getRecievedTimeOnServlet() {
        return m_RecievedTimeOnServlet;
    }

    /**
     * Sets RecievedTimeOnServlet.
     * 
     * @param recievedTimeOnServlet
     */
    public void setRecievedTimeOnServlet(long recievedTimeOnServlet) {
        m_RecievedTimeOnServlet = recievedTimeOnServlet;
    }

    /**
     * Gets AcousticModelName.
     * 
     * @return m_AcousticModelName
     */
    public String getAcousticModelName() {
        return m_AcousticModelName;
    }

    /**
     * Sets AcousticModelName.
     * 
     * @param acousticModelName
     */
    public void setAcousticModelName(String acousticModelName) {
        m_AcousticModelName = acousticModelName;
    }

    /**
     * Gets NICTlatticeModel.
     * 
     * @return m_NICTlatticeModel
     */
    public String getNICTlatticeModel() {
        return m_NICTlatticeModel;
    }

    /**
     * Sets NICTlatticeModel.
     * 
     * @param rlatticeModel
     */
    public void setNICTlatticeModel(String rlatticeModel) {
        m_NICTlatticeModel = rlatticeModel;
    }

    /**
     * Gets NICTrescoreModel.
     * 
     * @return m_NICTrescoreModel
     */
    public String getNICTrescoreModel() {
        return m_NICTrescoreModel;
    }

    /**
     * Sets NICTrescoreModel.
     * 
     * @param rrescoreModel
     */
    public void setNICTrescoreModel(String rrescoreModel) {
        m_NICTrescoreModel = rrescoreModel;
    }
}
