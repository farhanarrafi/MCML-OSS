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
 * MCML exception class.
 * 
 */
public class MCMLException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = 2135443728753487130L;
    // ------------------------------------------
    // public member constant
    // ------------------------------------------
    /** System */
    public static final String SYSTEM = "S";
    /**  Error */
    public static final String ERROR = "E";

    /**
     * Service type enumeration class.
     * 
     */
    public enum Service {
        /** COMMON */
        COMMON,
        /** ASR */
        ASR,
        /** MT */
        MT,
        /** TTS */
        TTS,
    }

    /** COMMON */
    public static final int COMMON = 0;
    /** ASR */
    public static final int ASR = 1;
    /** MT */
    public static final int MT = 2;
    /** TTS */
    public static final int TTS = 3;

    /** System timeout */
    public static final int SYSTEM_TIME_OUT = 50;
    /** Internal error  */
    public static final int INTERNAL_ABNORMALITY = 60;
    /**  Connection CLOSED */
    public static final int CONNECTION_CLOSED = 70;
    /**  Data format error  */
    public static final int ABNORMAL_DATA_FORMAT = 80;
    /** XML data format error  */
    public static final int ABNORMAL_XML_DATA_FORMAT = 90;
    /** No communication parameter */
    public static final int NON_CORESSPONDANCE_PARAM = 100;
    /** Engine CLOSED */
    public static final int ENGINE_CLOSED = 110;
    /** Timeout */
    public static final int TIME_OUT = 200;
    /** Engine down */
    public static final int ENGINE_DOWN = 500;
    /** No support language */
    public static final int NOT_SUPPORT_LANGUAGE = 510;
    /** Unexpected input */
    public static final int NOT_EXPECTED_INPUT = 520;
    /** Internal error */
    public static final int INTERNAL_ERROR = 530;
    /** Unrecoverable error */
    public static final int IRREPARABLE_ERROR = 540;
    /** Cannot go further */
    public static final int DESTINATION_IS_NOT_FOUND = 550;
    /** Abnormal ID */
    public static final int ABNORMAL_ID = 600;
    /** UnusableID */
    public static final int UNAVAILABLE_ID = 610;
    /** Abnormal email */
    public static final int ABNORMAL_EMAIL = 620;
    /** Unusable email */
    public static final int UNAVAILABLE_EMAIL = 630;
    /** Abnormal  password */
    public static final int ABNORMAL_PASSWORD = 640;
    /** Unusable password */
    public static final int UNAVAILABLE_PASSWORD = 650;
    /** Abnormal access code*/
    public static final int ABNORMAL_ACCESS_CODE = 660;
    /** Unusable access code*/
    public static final int UNAVAILABLE_ACCESS_CODE = 670;
    /** Nonexistent user */
    public static final int NOT_EXIST_USER = 700;
    /**  User invalid number */
    public static final int INVALID_NUMBER_OF_USERS = 710;
    /** Call failed  */
    public static final int CALL_FAILED = 720;
    /** Currently using engine server */
    public static final int ENGINE_SERVER_BUSY = 800;
    /** Engineer server request timeout */
    public static final int ENGINE_SERVER_REQUEST_TIME_OUT = 810;
    /** Other errors */
    public static final int OTHER_ERROR = 900;
    /** Other messages */
    public static final int OTHER_MESSAGE = 910;

    // ------------------------------------------
    // private member variable
    // ------------------------------------------
    private String m_ErrorCode;
    private String m_Service;
    private String m_Explanation;

    // ------------------------------------------
    // public member function
    // ------------------------------------------

    // constructor
    public MCMLException(String message, String err, int proc, int mcod) {
        super(message);

        m_ErrorCode = String.format("%s-20%02d%04d", err, proc, mcod);
        setService(proc);

        switch (mcod) {
        case SYSTEM_TIME_OUT:
            m_Explanation = "System time out";
            break;
        case INTERNAL_ABNORMALITY:
            m_Explanation = "Internal abnormality";
            break;
        case CONNECTION_CLOSED:
            m_Explanation = "Connection closed";
            break;
        case ABNORMAL_DATA_FORMAT:
            m_Explanation = "Abnormal data format";
            break;
        case ABNORMAL_XML_DATA_FORMAT:
            m_Explanation = "Abnormal XML data format";
            break;
        case NON_CORESSPONDANCE_PARAM:
            m_Explanation = "Non-correspondence parameter";
            break;
        case ENGINE_CLOSED:
            m_Explanation = "Engine closed";
            break;
        case TIME_OUT:
            m_Explanation = "Time out";
            break;
        case ENGINE_DOWN:
            m_Explanation = "Engine down";
            break;
        case NOT_SUPPORT_LANGUAGE:
            m_Explanation = "Not support language";
            break;
        case NOT_EXPECTED_INPUT:
            m_Explanation = "Not expected input";
            break;
        case INTERNAL_ERROR:
            m_Explanation = "Internal error";
            break;
        case IRREPARABLE_ERROR:
            m_Explanation = "irreparable error";
            break;
        case DESTINATION_IS_NOT_FOUND:
            m_Explanation = "Specified Destination is not found";
            break;
        case ENGINE_SERVER_BUSY:
            m_Explanation = "Engine server busy";
            break;
        case ENGINE_SERVER_REQUEST_TIME_OUT:
            m_Explanation = "Engine Server Request Time out";
            break;
        case ABNORMAL_ID:
            m_Explanation = "Abnormal ID ";
            break;
        case UNAVAILABLE_ID:
            m_Explanation = "Unavailable ID ";
            break;
        case ABNORMAL_EMAIL:
            m_Explanation = "Abnormal EMail address";
            break;
        case UNAVAILABLE_EMAIL:
            m_Explanation = "Unavailable EMail address";
            break;
        case ABNORMAL_PASSWORD:
            m_Explanation = "Abnormal password";
            break;
        case UNAVAILABLE_PASSWORD:
            m_Explanation = "Unavailable password";
            break;
        case ABNORMAL_ACCESS_CODE:
            m_Explanation = "Abnormal access code";
            break;
        case UNAVAILABLE_ACCESS_CODE:
            m_Explanation = "Unavailable access code";
            break;
        case NOT_EXIST_USER:
            m_Explanation = "Not exist User";
            break;
        case INVALID_NUMBER_OF_USERS:
            m_Explanation = "Invalid number of Users";
            break;
        case CALL_FAILED:
            m_Explanation = "Call failed";
            break;
        case OTHER_ERROR:
            m_Explanation = "Other errors";
            break;
        case OTHER_MESSAGE:
            m_Explanation = message;
            break;
        default:
            m_Explanation = "";
            break;
        }
    }

    /**
     * Constructor
     * 
     * @param message
     * @param explanation
     * @param errorCode
     */
    public MCMLException(String message, String explanation, String errorCode) {
        super(message);
        m_Explanation = explanation;
        m_ErrorCode = errorCode;
    }

    /**
     * Gets Service.
     * 
     * @return Service
     */
    public String getService() {
        return m_Service;
    }

    /**
     * Gets ErrorCode.
     * 
     * @return ErrorCode
     */
    public String getErrorCode() {
        return m_ErrorCode;
    }

    /**
     * Gets Explanation.
     * 
     * @return Explanation
     */
    public String getExplanation() {
        return m_Explanation;
    }

    private void setService(int proc) {
        if (proc == COMMON) {
            m_Service = "Common";
        } else if (proc == ASR) {
            m_Service = "ASR";
        } else if (proc == MT) {
            m_Service = "MT";
        } else if (proc == TTS) {
            m_Service = "TTS";
        }
    }

}
