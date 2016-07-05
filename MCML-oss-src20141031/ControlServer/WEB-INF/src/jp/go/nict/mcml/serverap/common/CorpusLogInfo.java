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

package jp.go.nict.mcml.serverap.common;

import java.util.ArrayList;

/**
 * CorpusLogInfoクラスです。
 * 
 * @version 4.0
 * @since 20120921
 */
public class CorpusLogInfo {
    // ------------------------------------------
    // private member variable
    // ------------------------------------------
    private String m_UserURI;
    private String m_UserID;
    private String m_ClientIP;
    private String m_VoiceID;
    private int m_ProcessOrder;
    private String m_Location;
    private String m_Domain;
    private String m_Language;
    private String m_InputSentence;
    private String m_InputChunk;
    private String m_Sentence;
    private String m_Chunk;
    private String m_Gender;
    private String m_Age;
    private String m_State;
    private long m_FirstFrameArrivedTime;
    private long m_LastFrameArrivedTime;
    private long m_ProcessTime;
    private double m_RTF;
    private boolean m_IsBigEndian;
    private ArrayList<String> m_DestinationURLList;

    // ------------------------------------------
    // constructer
    // ------------------------------------------
    public CorpusLogInfo() {

        m_UserURI = "";
        m_UserID = "";
        m_ClientIP = "";
        m_VoiceID = "";
        m_ProcessOrder = 0;
        m_Location = "";
        m_Domain = "";
        m_Language = "";
        m_InputSentence = "";
        m_InputChunk = "";
        m_Sentence = "";
        m_Chunk = "";
        m_Gender = "";
        m_Age = "";
        m_State = "";
        m_FirstFrameArrivedTime = 0;
        m_LastFrameArrivedTime = 0;
        m_ProcessTime = 0;
        m_RTF = 0;
        m_IsBigEndian = true;
        m_DestinationURLList = new ArrayList<String>();
    }

    // ------------------------------------------
    // setter and getter
    // ------------------------------------------

    /**
     * Ageを取得します。
     * 
     * @return Age
     */
    public String getAge() {
        return m_Age;
    }

    /**
     * Ageを設定します。
     * 
     * @param age
     */
    public void setAge(String age) {
        m_Age = age;
    }

    /**
     * ClientIPを取得します。
     * 
     * @return ClientIP
     */
    public String getClientIP() {
        return m_ClientIP;
    }

    /**
     * ClientIPを設定します。
     * 
     * @param clientIP
     */
    public void setClientIP(String clientIP) {
        m_ClientIP = clientIP;
    }

    /**
     * Domainを取得します。
     * 
     * @return Domain
     */
    public String getDomain() {
        return m_Domain;
    }

    /**
     * Domainを設定します。
     * 
     * @param domain
     */
    public void setDomain(String domain) {
        m_Domain = domain;
    }

    /**
     * FirstFrameArrivedTimeを取得します。
     * 
     * @return FirstFrameArrivedTime
     */
    public long getFirstFrameArrivedTime() {
        return m_FirstFrameArrivedTime;
    }

    /**
     * FirstFrameArrivedTimeを設定します。
     * 
     * @param firstFrameArrivedTime
     */
    public void setFirstFrameArrivedTime(long firstFrameArrivedTime) {
        m_FirstFrameArrivedTime = firstFrameArrivedTime;
    }

    /**
     * Genderを取得します。
     * 
     * @return Gender
     */
    public String getGender() {
        return m_Gender;
    }

    /**
     * Genderを設定します。
     * 
     * @param gender
     */
    public void setGender(String gender) {
        m_Gender = gender;
    }

    /**
     * InputSentenceを取得します。
     * 
     * @return InputSentence
     */
    public String getInputSentence() {
        return m_InputSentence;
    }

    /**
     * InputSentenceを設定します。
     * 
     * @param inputSentence
     */
    public void setInputSentence(String inputSentence) {
        m_InputSentence = inputSentence;
    }

    /**
     * InputChunkを取得します。
     * 
     * @return InputChunk
     */
    public String getInputChunk() {
        return m_InputChunk;
    }

    /**
     * InputChunkを設定します。
     * 
     * @param inputChunk
     */
    public void setInputChunk(String inputChunk) {
        m_InputChunk = inputChunk;
    }

    /**
     * IsBigEndianを取得します。
     * 
     * @return IsBigEndian
     */
    public boolean getIsBigEndian() {
        return m_IsBigEndian;
    }

    /**
     * IsBigEndianを設定します。
     * 
     * @param isBigEndian
     */
    public void setIsBigEndian(boolean isBigEndian) {
        m_IsBigEndian = isBigEndian;
    }

    /**
     * Languageを取得します。
     * 
     * @return Language
     */
    public String getLanguage() {
        return m_Language;
    }

    /**
     * Languageを設定します。
     * 
     * @param language
     */
    public void setLanguage(String language) {
        m_Language = language;
    }

    /**
     * LastFrameArrivedTimeを取得します。
     * 
     * @return LastFrameArrivedTime
     */
    public long getLastFrameArrivedTime() {
        return m_LastFrameArrivedTime;
    }

    /**
     * LastFrameArrivedTimeを設定します。
     * 
     * @param lastFrameArrivedTime
     */
    public void setLastFrameArrivedTime(long lastFrameArrivedTime) {
        m_LastFrameArrivedTime = lastFrameArrivedTime;
    }

    /**
     * Locationを取得します。
     * 
     * @return Location
     */
    public String getLocation() {
        return m_Location;
    }

    /**
     * Locationを設定します。
     * 
     * @param location
     */
    public void setLocation(String location) {
        m_Location = location;
    }

    /**
     * ProcessTimeを取得します。
     * 
     * @return ProcessTime
     */
    public long getProcessTime() {
        return m_ProcessTime;
    }

    /**
     * ProcessTimeを設定します。
     * 
     * @param processTime
     */
    public void setProcessTime(long processTime) {
        m_ProcessTime = processTime;
    }

    /**
     * RTFを取得します。
     * 
     * @return RTF
     */
    public double getRTF() {
        return m_RTF;
    }

    /**
     * RTFを設定します。
     * 
     * @param rtf
     */
    public void setRTF(double rtf) {
        m_RTF = rtf;
    }

    /**
     * Sentenceを取得します。
     * 
     * @return Sentence
     */
    public String getSentence() {
        return m_Sentence;
    }

    /**
     * Sentenceを設定します。
     * 
     * @param sentence
     */
    public void setSentence(String sentence) {
        m_Sentence = sentence;
    }

    /**
     * Stateを取得します。
     * 
     * @return State
     */
    public String getState() {
        return m_State;
    }

    /**
     * Stateを設定します。
     * 
     * @param state
     */
    public void setState(String state) {
        m_State = state;
    }

    /**
     * UserURIを取得します。
     * 
     * @return UserURI
     */
    public String getUserURI() {
        return m_UserURI;
    }

    /**
     * UserURIを設定します。
     * 
     * @param userURI
     */
    public void setUserURI(String userURI) {
        m_UserURI = userURI;
    }

    /**
     * UserIDを取得します。
     * 
     * @return UserID
     */
    public String getUserID() {
        return m_UserID;
    }

    /**
     * UserIDを設定します。
     * 
     * @param userID
     */
    public void setUserID(String userID) {
        m_UserID = userID;
    }

    /**
     * ProcessOrderを取得します。
     * 
     * @return ProcessOrder
     */
    public int getProcessOrder() {
        return m_ProcessOrder;
    }

    /**
     * ProcessOrderを設定します。
     * 
     * @param processOrder
     */
    public void setProcessOrder(int processOrder) {
        m_ProcessOrder = processOrder;
    }

    /**
     * VoiceIDを取得します。
     * 
     * @return VoiceID
     */
    public String getVoiceID() {
        return m_VoiceID;
    }

    /**
     * VoiceIDを設定します。
     * 
     * @param voiceID
     */
    public void setVoiceID(String voiceID) {
        m_VoiceID = voiceID;
    }

    /**
     * Chunkを取得します。
     * 
     * @return Chunk
     */
    public String getChunk() {
        return m_Chunk;
    }

    /**
     * Chunkを設定します。
     * 
     * @param chunk
     */
    public void setChunk(String chunk) {
        m_Chunk = chunk;
    }

    /**
     * DestinationURLを取得します。
     * 
     * @return DestinationURLリスト
     */
    public ArrayList<String> getDestinationURL() {
        return m_DestinationURLList;
    }

    /**
     * DestinationURLを設定します。
     * 
     * @param urlList
     */
    public void setDestinationURL(ArrayList<String> urlList) {
        m_DestinationURLList = urlList;
    }
}
