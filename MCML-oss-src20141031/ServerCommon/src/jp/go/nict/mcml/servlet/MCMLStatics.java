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
package jp.go.nict.mcml.servlet;

/**
 * MCMLStatics class.
 * 
 */
public class MCMLStatics {
    // ------------------------------------------
    // public member constants
    // ------------------------------------------
    /**  version */
    public static final String VERSION = "1.0";
    /** Service ({@value} ) */
    public static final String SERVICE_ASR = "ASR";
    /** Service ({@value} ) */
    public static final String SERVICE_MT = "MT";
    /** Service ({@value} ) */
    public static final String SERVICE_TTS = "TTS";
    /** Service dialogue connection */
    public static final String SERVICE_DIALOG_CONNECT = "DM_CONNECT";
    /** Service dialogue */
    public static final String SERVICE_DIALOG = "DM";
    /** Service user registration */
    public static final String SERVICE_USER_REGISTRATION = "UserRegistration";
    /** Service user unregistered*/
    public static final String SERVICE_USER_UNREGISTRATION = "UserUnregistration";
    /** Service party registration */
    public static final String SERVICE_PARTY_REGISTRATION = "PartyRegistration";
    /** Service party unregistered*/
    public static final String SERVICE_PARTY_UNREGISTRATION = "PartyUnregistration";
    /** Service user search */
    public static final String SERVICE_USER_SEARCH = "UserSearch";
    /** Service polling */
    public static final String SERVICE_POLLING = "Polling";
    /** Service Group information */
    public static final String SERVICE_GROUPINFORMATION = "GroupInformation";
    /** Service invitation */
    public static final String SERVICE_INVITE = "Invite";
    /** Service permission */
    public static final String SERVICE_ACCEPT = "Accept";
    /** Service rejection */
    public static final String SERVICE_REJECT = "Reject";
    /** Service BYE */
    public static final String SERVICE_BYE = "Bye";
    /** Voice(ADPCM) */
    public static final String AUDIO_ADPCM = "ADPCM";
    /** Voice(RAW) */
    public static final String AUDIO_RAW = "raw PCM";
    /** Voice(DSR) */
    public static final String AUDIO_DSR = "DSR";
    /** Voice(MP3) */
    public static final String AUDIO_MP3 = "MP3";
    /** Voice(SPEEX) */
    public static final String AUDIO_SPEEX = "Speex";
    /** Suffix (LITTLE) */
    public static final String ENDIAN_LITTLE = "little";
    /** Suffix (BIG) */
    public static final String ENDIAN_BIG = "big";
    /** Sampling (8K) */
    public static final String SAMPLING_FREQUENCY_8K = "8000";
    /** Sampling (16K) */
    public static final String SAMPLING_FREQUENCY_16K = "16000";
    /** Unique userID */
    public static final String UNIQUE_USER_ID_SPLITTER = "\t";
    /** MODEL CHANNEL ID (Image) */
    public static final String MODEL_CHANNEL_ID_IMAGE = "0";
    /** MODEL CHANNEL ID (text) */
    public static final String MODEL_CHANNEL_ID_TEXT = "1";
    /** MODEL CHANNEL ID (Voice) */
    public static final String MODEL_CHANNEL_ID_AUDIO = "1";
    /** BinaryCHANNEL ID (Voice) */
    public static final String BINARY_CHANNEL_ID_AUDIO = "1";
    /** Binary data type (Image) */
    public static final String BINARY_DATA_TYPE_IMAGE = "image";
    /** SENTENCE FUNCTION */
    public static final String SENTENCE_FUNCTION = "text";
    /** Signal value type (INTEGER) */
    public static final String SIGNAL_VALUE_TYPE_INTEGER = "integer";
    /** Signal value type (FLOAT) */
    public static final String SIGNAL_VALUE_TYPE_FLOAT = "float";
    /** Signal bit rate ({@value} ) */
    public static final String SIGNAL_BIT_RATE = "16";
    /** Signal CHANNEL QTY */
    public static final String SIGNAL_CHANNEL_QTY = "1";
    /** Gender (Male) */
    public static final String GENDER_MALE = "Male";
    /** Gender (Female) */
    public static final String GENDER_FEMALE = "Female";
    /** Gender (Unknown) */
    public static final String GENDER_UNKNOWN = "Unknown";
    /** {@value} */
    public static final String WFSTDM = "WFSTDM";

    // NBest Parser used Return Code.
    /** Line feed code*/
    public static final String RETURN_CODE = "\n";

    // use for creating corpus log file
    /** Language (Japanese) */
    public static final String LANGUAGE_JAPANESE = "Ja";
    /** Language (English) */
    public static final String LANGUAGE_ENGLISH = "En";
    /** Language (Chinese) */
    public static final String LANGUAGE_CHINESE = "Zh";
    /** Language (Korean) */
    public static final String LANGUAGE_KOREAN = "Ko";

    /** Character set name */
    public static final String CHARSET_NAME = "UTF-8";
    /** Character set ({@value} ) */
    public static final String CHARSET_UTF_8 = "UTF8";
    /** Character set ({@value} ) */
    public static final String CHARSET_EUC_JP = "EUC-JP";
    /** Character set ({@value} ) */
    public static final String CHARSET_EUC_CN = "EUC-CN";
    /** Character set ({@value} ) */
    public static final String CHARSET_EUC_KR = "EUC-KR";

    /** Progress state successful  */
    public static final String PROCESS_STATE_SUCCESS = "Success";
    /** Progress state failed  */
    public static final String PROCESS_STATE_FAIL = "Fail=";
    /** YES character string */
    public static final String STRING_YES = "yes";
    /** NO character string */
    public static final String STRING_NO = "no";

    /** NativeLanguage */
    public static final int NATIVE_LANGUAGE = 5;
    /** Foreign language (1) */
    public static final int FOREIGN_LANGUAGE_1ST = 4;
    /** Foreign language (2) */
    public static final int FOREIGN_LANGUAGE_2ND = 3;

}
