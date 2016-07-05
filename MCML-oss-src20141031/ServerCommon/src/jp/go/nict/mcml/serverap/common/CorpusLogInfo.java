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

/**
 * CorpusLogInfo class.
 * 
 */
public class CorpusLogInfo {

    // ------------------------------------------
    // private member variable
    // ------------------------------------------
    private String m_UserURI;
    private String m_UserID;
    private String m_ClientIP;
    private String m_VoiceID;
    private String m_ProcessOrder;
    private String m_Location;
    private String m_Domain;
    private String m_Language;
    private String m_InputSentence;
    private String m_InputChunk;
    private String m_Sentence;
    private String m_Chunk;
    private String m_Longitude;
    private String m_Latitude;
    private String m_Gender;
    private String m_Age;
    private String m_NativeLanguage;
    private String m_FirstForeignLanguage;
    private String m_SecondForeignLanguage;

    private String m_State;
    private long m_FirstFrameArrivedTime;
    private long m_LastFrameArrivedTime;
    private long m_ProcessTime;
    private double m_RTF;
    private boolean m_IsBigEndian;

    // ------------------------------------------
    // constructer
    // ------------------------------------------
    public CorpusLogInfo() {

        m_UserURI = "";
        m_UserID = "";
        m_ClientIP = "";
        m_VoiceID = "";
        m_ProcessOrder = "";
        m_Location = "";
        m_Domain = "";
        m_Language = "";
        m_InputSentence = "";
        m_InputChunk = "";
        m_Sentence = "";
        m_Chunk = "";
        m_Longitude = "";
        m_Latitude = "";
        m_Gender = "";
        m_Age = "";
        m_NativeLanguage = "";
        m_FirstForeignLanguage = "";
        m_SecondForeignLanguage = "";
        m_State = "";
        m_FirstFrameArrivedTime = 0;
        m_LastFrameArrivedTime = 0;
        m_ProcessTime = 0;
        m_RTF = 0;
        m_IsBigEndian = true;
    }

    // ------------------------------------------
    // setter and getter
    // ------------------------------------------

    /**
     * Gets Longitude.
     * 
     * @return Longitude
     */
    public String getLongitude() {
        return m_Longitude;
    }

    /**
     * Sets Longitude.
     * 
     * @param longitude
     */
    public void setLongitude(String longitude) {
        m_Longitude = longitude;
    }

    /**
     * Gets Latitude.
     * 
     * @return Latitude
     */
    public String getLatitude() {
        return m_Latitude;
    }

    /**
     * Sets Latitude.
     * 
     * @param latitude
     */
    public void setLatitude(String latitude) {
        m_Latitude = latitude;
    }

    /**
     * Gets Age.
     * 
     * @return Age
     */
    public String getAge() {
        return m_Age;
    }

    /**
     * Sets Age.
     * 
     * @param age
     */
    public void setAge(String age) {
        m_Age = age;
    }

    /**
     * Gets NativeLanguage.
     * 
     * @return NativeLanguage
     */
    public String getNativeLanguage() {
        return m_NativeLanguage;
    }

    /**
     * Sets NativeLanguage.
     * 
     * @param nativeLanguage
     */
    public void setNativeLanguage(String nativeLanguage) {
        m_NativeLanguage = nativeLanguage;
    }

    /**
     * Gets FirstForeignLanguage.
     * 
     * @return FirstForeignLanguage
     */
    public String getFirstForeignLanguage() {
        return m_FirstForeignLanguage;
    }

    /**
     * Sets FirstForeignLanguage.
     * 
     * @param firstForeignLanguage
     */
    public void setFirstForeignLanguage(String firstForeignLanguage) {
        m_FirstForeignLanguage = firstForeignLanguage;
    }

    /**
     * Gets SecondForeignLanguage.
     * 
     * @return SecondForeignLanguage
     */
    public String getSecondForeignLanguage() {
        return m_SecondForeignLanguage;
    }

    /**
     * Sets SecondForeignLanguage.
     * 
     * @param secondForeignLanguage
     */
    public void setSecondForeignLanguage(String secondForeignLanguage) {
        m_SecondForeignLanguage = secondForeignLanguage;
    }

    /**
     * Gets ClientIP.
     * 
     * @return ClientIP
     */
    public String getClientIP() {
        return m_ClientIP;
    }

    /**
     * Sets ClientIP.
     * 
     * @param clientIP
     */
    public void setClientIP(String clientIP) {
        m_ClientIP = clientIP;
    }

    /**
     * Gets Domain.
     * 
     * @return Domain
     */
    public String getDomain() {
        return m_Domain;
    }

    /**
     * Sets Domain.
     * 
     * @param domain
     */
    public void setDomain(String domain) {
        m_Domain = domain;
    }

    /**
     * Gets FirstFrameArrivedTime.
     * 
     * @return FirstFrameArrivedTime
     */
    public long getFirstFrameArrivedTime() {
        return m_FirstFrameArrivedTime;
    }

    /**
     * Sets FirstFrameArrivedTime.
     * 
     * @param firstFrameArrivedTime
     */
    public void setFirstFrameArrivedTime(long firstFrameArrivedTime) {
        m_FirstFrameArrivedTime = firstFrameArrivedTime;
    }

    /**
     * Gets Gender.
     * 
     * @return Gender
     */
    public String getGender() {
        return m_Gender;
    }

    /**
     * Sets Gender.
     * 
     * @param gender
     */
    public void setGender(String gender) {
        m_Gender = gender;
    }

    /**
     * Gets InputSentence.
     * 
     * @return InputSentence
     */
    public String getInputSentence() {
        return m_InputSentence;
    }

    /**
     * Sets InputSentence.
     * 
     * @param inputSentence
     */
    public void setInputSentence(String inputSentence) {
        m_InputSentence = inputSentence;
    }

    /**
     * Gets InputChunk.
     * 
     * @return InputChunk
     */
    public String getInputChunk() {
        return m_InputChunk;
    }

    /**
     * Sets InputChunk.
     * 
     * @param inputChunk
     */
    public void setInputChunk(String inputChunk) {
        m_InputChunk = inputChunk;
    }

    /**
     * Gets IsBigEndian.
     * 
     * @return IsBigEndian
     */
    public boolean getIsBigEndian() {
        return m_IsBigEndian;
    }

    /**
     * Sets IsBigEndian.
     * 
     * @param isBigEndian
     */
    public void setIsBigEndian(boolean isBigEndian) {
        m_IsBigEndian = isBigEndian;
    }

    /**
     * Gets Language.
     * 
     * @return Language
     */
    public String getLanguage() {
        return m_Language;
    }

    /**
     * Sets Language.
     * 
     * @param language
     */
    public void setLanguage(String language) {
        m_Language = language;
    }

    /**
     * Gets LastFrameArrivedTime.
     * 
     * @return LastFrameArrivedTime
     */
    public long getLastFrameArrivedTime() {
        return m_LastFrameArrivedTime;
    }

    /**
     * Sets LastFrameArrivedTime.
     * 
     * @param lastFrameArrivedTime
     */
    public void setLastFrameArrivedTime(long lastFrameArrivedTime) {
        m_LastFrameArrivedTime = lastFrameArrivedTime;
    }

    /**
     * Gets Location.
     * 
     * @return Location
     */
    public String getLocation() {
        return m_Location;
    }

    /**
     * Sets Location.
     * 
     * @param location
     */
    public void setLocation(String location) {
        m_Location = location;
    }

    /**
     * Gets ProcessTime.
     * 
     * @return ProcessTime
     */
    public long getProcessTime() {
        return m_ProcessTime;
    }

    /**
     * Sets ProcessTime.
     * 
     * @param processTime
     */
    public void setProcessTime(long processTime) {
        m_ProcessTime = processTime;
    }

    /**
     * Gets RTF.
     * 
     * @return RTF
     */
    public double getRTF() {
        return m_RTF;
    }

    /**
     * Sets RTF.
     * 
     * @param rtf
     */
    public void setRTF(double rtf) {
        m_RTF = rtf;
    }

    /**
     * Gets Sentence.
     * 
     * @return Sentence
     */
    public String getSentence() {
        return m_Sentence;
    }

    /**
     * Sets Sentence.
     * 
     * @param sentence
     */
    public void setSentence(String sentence) {
        m_Sentence = sentence;
    }

    /**
     * Gets State.
     * 
     * @return State
     */
    public String getState() {
        return m_State;
    }

    /**
     * Sets State.
     * 
     * @param state
     */
    public void setState(String state) {
        m_State = state;
    }

    /**
     * Gets UserURI.
     * 
     * @return UserURI
     */
    public String getUserURI() {
        return m_UserURI;
    }

    /**
     * Sets UserURI.
     * 
     * @param userURI
     */
    public void setUserURI(String userURI) {
        m_UserURI = userURI;
    }

    /**
     * Gets UserID.
     * 
     * @return UserID
     */
    public String getUserID() {
        return m_UserID;
    }

    /**
     * Sets UserID.
     * 
     * @param userID
     */
    public void setUserID(String userID) {
        m_UserID = userID;
    }

    /**
     * Gets ProcessOrder.
     * 
     * @return ProcessOrder
     */
    public String getProcessOrder() {
        return m_ProcessOrder;
    }

    /**
     * Sets ProcessOrde.
     * 
     * @param processOrder
     */
    public void setProcessOrder(String processOrder) {
        m_ProcessOrder = processOrder;
    }

    /**
     * Gets VoiceID.
     * 
     * @return VoiceID
     */
    public String getVoiceID() {
        return m_VoiceID;
    }

    /**
     * Sets VoiceID.
     * 
     * @param voiceID
     */
    public void setVoiceID(String voiceID) {
        m_VoiceID = voiceID;
    }

    /**
     * Gets Chunk.
     * 
     * @return Chunk
     */
    public String getChunk() {
        return m_Chunk;
    }

    /**
     * Sets chunk.
     * 
     * @param chunk
     */
    public void setChunk(String chunk) {
        m_Chunk = chunk;
    }

}
