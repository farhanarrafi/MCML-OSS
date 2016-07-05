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

// ------------------------------
// Newly created for unification.
// ------------------------------
package jp.go.nict.mcml.exception;

/**
 * MCML exception class.
 * 
 */
public class MCMLException extends Exception {

    private static final long serialVersionUID = 2135443728753487130L;
    // ------------------------------------------
    // public member constant
    // ------------------------------------------
    /** Fatal */
    public static final String FATAL = "F";
    /** Error */
    public static final String ERROR = "E";
    /** Warning */
    public static final String WARNING = "W";
    /** Information */
    public static final String INFO = "I";

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
        /** DM */
        DM
    }

    /** COMMON */
    public static final int COMMON = 0;
    /** ASR */
    public static final int ASR = 1;
    /** MT */
    public static final int MT = 2;
    /** TTS */
    public static final int TTS = 3;
    /** DM */
    public static final int DM = 4;

    // 1000 - (NEW) -------------------------------------------------
    /** Server initialization error */
    public static final int SERVER_INITIALIZE_ERROR = 1000;
    /** Server error */
    public static final int SERVER_DESTINATION_ERROR = 1001;
    /** ControlServer generic error */
    public static final int CS_COMPREHENSIVE_ERROR = 1100;
    /** ControlServer2 down */
    public static final int CS2_DOWN_IO = 1101;
    /** ControlServer2 null pointer */
    public static final int CS2_DOWN_NULLPOINTER = 1102;
    /** ControlServer2 URL error */
    public static final int CS2_MALFORMED_URL = 1103;
    /** PartyRegistrationServer down */
    public static final int PARTY_REG_SERVER_DOWN = 1104;
    /** PartyRegistrationServer response error */
    public static final int PARTY_REG_SERVER_RESPONSE_ERROR = 1105;
    /** PartyRegistrationServer request error */
    public static final int PARTY_REG_SERVER_REQUEST_ERROR = 1106;
    /** RoutingServer down */
    public static final int ROUTING_SERVER_DOWN = 1107;
    /** RoutingServer response error */
    public static final int ROUTING_SERVER_RESPONSE_ERROR = 1108;
    /** RoutingServer request error */
    public static final int ROUTING_SERVER_REQUEST_ERROR = 1109;

    /** HTTP session error */
    public static final int HTTP_SESSION_ERROR = 1201;
    /** HTTP session ID error */
    public static final int HTTP_SESSION_ID_ERROR = 1202;
    /** HTTP attribute error */
    public static final int HTTP_ATTRIBUTE_ERROR = 1203;
    /** HTTP socket error  */
    public static final int HTTP_SOCKET_ERROR = 1204;
    /** HTTP socket timeout */
    public static final int REQUEST_SOCKET_TIME_OUT = 1205;
    /** Host not found error */
    public static final int UNKNOWN_HOST_ERROR = 1206;
    /** Connection error */
    public static final int CONNECT_ERROR = 1207;
    /** Socket  error */
    public static final int SOCKET_ERROR = 1208;
    /** File termination error */
    public static final int EOF_ERROR = 1209;
    /** Engine communication error */
    public static final int ENGINE_COMMUNICATION_ERROR = 1210;
    /** Request message error */
    public static final int REQUEST_MESSAGING_ERROR = 1211;

    /** XML analysis error */
    public static final int XML_PARSE_ERROR = 1300;
    /** XML format error (MCML) */
    public static final int XML_FORMAT_ERROR_MCML = 1301;
    /** XML format error (Server) */
    public static final int XML_FORMAT_ERROR_SERVER = 1302;
    /** XML format error (Service) */
    public static final int XML_FORMAT_ERROR_SERVICE = 1303;
    /** XML format error (InputUserProfile element) */
    public static final int XML_FORMAT_ERROR_INPUT_USER_PROF = 1304;
    /** XML format error (Source language) */
    public static final int XML_FORMAT_ERROR_SRC_LANGUAGE = 1305; // Source
                                                                  // language
    /** XML format error (Target language) */
    public static final int XML_FORMAT_ERROR_DST_LANGUAGE = 1306; // Destination
                                                                  // (Target)
                                                                  // language
    /** XML format error (Target output) */
    public static final int XML_FORMAT_ERROR_TARGET_OUTPUT = 1307;
    /** XML format error (Version) */
    public static final int XML_FORMAT_ERROR_VERSION = 1308;
    /** XML format error (ID) */
    public static final int XML_FORMAT_ERROR_ID = 1309;
    /** XML format error (SentenceSequence element) */
    public static final int XML_FORMAT_ERROR_SENTENCE_SEQ = 1310;
    /** XML combination error */
    public static final int XML_COMBINATION_ERROR = 1311;
    /** XML creation error */
    public static final int XML_CREATION_ERROR = 1312;
    /** Audio conversion error */
    public static final int AUDIO_CONVERSION_ERROR = 1400;
    /** Audio byte size error */
    public static final int AUDIO_BYTE_LENGTH_ERROR = 1401;

    /** Pivot request error */
    public static final int PIVOT_REQUEST_ERROR = 1500;
    /** Pivot request timeout */
    public static final int PIVOT_REQUEST_TIME_OUT = 1501;

    /** MCML response data is null */
    public static final int MCML_RESPONSE_DATA_EMPTY = 1600;
    /** MCML response data body is null */
    public static final int MCML_RESPONSE_DATA_BODY_EMPTY = 1601;
    /** Frame data is null */
    public static final int FRAME_DATA_EMPTY = 1602;
    /** Frame data body is null */
    public static final int FRAME_DATA_BODY_EMPTY = 1603;
    /** Frame data order error */
    public static final int FRAME_DATA_SEQUENCE_ERROR = 1604;
    /** MCML document error */
    public static final int MCMLDOC_ERROR = 1605;

    // ------------------------------------------
    // private member variable
    // ------------------------------------------
    private String m_ErrorCode;
    private String m_Service; // <--- setService()
    private String m_Explanation;

    // ------------------------------------------
    // public member function
    // ------------------------------------------

    /**
     * constructor
     * 
     * @param message
     *            Message
     * @param err
     *             Error
     * @param proc
     * @param mcod
     */
    public MCMLException(String message, String err, int proc, int mcod) {
        super(message);

        m_ErrorCode = String.format("%s-%04d", err, mcod);
        System.out.println("@@@ proc=" + proc);
        setService(proc);
        System.out.println("@@@ m_Service=" + m_Service);

        switch (mcod) {
        default:
            m_Explanation = message;
            break;
        }
    }

    /**
     * New constructor Created
     * 
     * @param level
     *            Level
     * @param service
     *            Service
     * @param errorCode
     *            Error code
     */
    public MCMLException(String level, int service, int errorCode) {
        m_ErrorCode = String.format("%s-%04d", level, errorCode);
        setService(service);
        switch (errorCode) {
        case SERVER_INITIALIZE_ERROR:
            m_Explanation = "Properties file for Server List Data is missing, or failed to initialize ServerDatabase with it.";
            break;
        case SERVER_DESTINATION_ERROR:
            m_Explanation = "Failed to create server connection or destination containers.";
            break;
        case CS_COMPREHENSIVE_ERROR:
            m_Explanation = "Failed to process request.";
            break;
        case CS2_DOWN_IO:
            m_Explanation = "Failed to communicate with ControlServer2.";
            break;
        case CS2_DOWN_NULLPOINTER: // ASR only
            m_Explanation = "Failed to communicate with ControlServer2.";
            break;
        case CS2_MALFORMED_URL:
            m_Explanation = "The URL for ControlServer2 is invalid.";
            break;
        case PARTY_REG_SERVER_DOWN:
            m_Explanation = "Failed to communicate with PartyRegistrationServer.";
            break;
        case PARTY_REG_SERVER_RESPONSE_ERROR:
            m_Explanation = "No XML data found in the response from PartyRegistrationServer.";
            break;
        case PARTY_REG_SERVER_REQUEST_ERROR:
            m_Explanation = "Failed to send request to PartyRegistrationServer.";
            break;
        case ROUTING_SERVER_DOWN:
            m_Explanation = "Failed to communicate with RoutingServer.";
            break;
        case ROUTING_SERVER_RESPONSE_ERROR:
            m_Explanation = "No XML data found in the response from RoutingServer.";
            break;
        case ROUTING_SERVER_REQUEST_ERROR:
            m_Explanation = "Failed to send request to RoutingServer.";
            break;
        case HTTP_SESSION_ERROR:
            m_Explanation = "The request has no valid HTTP session.";
            break;
        case HTTP_SESSION_ID_ERROR:
            m_Explanation = "Failed to get the unique identifier assigned to this session.";
            break;
        case HTTP_ATTRIBUTE_ERROR:
            m_Explanation = "No object was bound with the session attribute name.";
            break;
        case HTTP_SOCKET_ERROR:
            m_Explanation = "Failed to get socket and stream.";
            break;
        case REQUEST_SOCKET_TIME_OUT:
            m_Explanation = "ConnectorByClientComCtrl.request() has failed (SocketTimeoutException).";
            break;
        case UNKNOWN_HOST_ERROR:
            m_Explanation = "The IP address of the host could not be determined.";
            break;
        case CONNECT_ERROR:
            m_Explanation = "The connection was refused remotely (e.g., no process is listening on the remote address/port).";
            break;
        case SOCKET_ERROR:
            m_Explanation = "There is an error in the underlying protocol, such as a TCP error.";
            break;
        case EOF_ERROR:
            m_Explanation = "An end of file or end of stream has been reached unexpectedly during input.";
            break;
        case ENGINE_COMMUNICATION_ERROR:
            m_Explanation = "Failed to communicate with the engine (IOException).";
            break;
        case REQUEST_MESSAGING_ERROR:
            m_Explanation = "Failed to communicate with the engine MessagingException).";
            break;
        case XML_PARSE_ERROR:
            m_Explanation = "Failed to parse XML.";
            break;
        case XML_FORMAT_ERROR_MCML:
            m_Explanation = "'MCML' element is missing in MCML.";
            break;
        case XML_FORMAT_ERROR_SERVER:
            m_Explanation = "'Server' element is missing in MCML (MCML/).";
            break;
        case XML_FORMAT_ERROR_SERVICE:
            m_Explanation = "'Service' attribute is missing in MCML, or has an invalid value (MCML/Server/Request/).";
            break;
        case XML_FORMAT_ERROR_INPUT_USER_PROF:
            m_Explanation = "'InputUserProfile' element is missing in MCML (MCML/Server/Request/).";
            break;
        case XML_FORMAT_ERROR_SRC_LANGUAGE:
            m_Explanation = "Source language is empty.";
            break;
        case XML_FORMAT_ERROR_DST_LANGUAGE:
            m_Explanation = "Target language is empty.";
            break;
        case XML_FORMAT_ERROR_TARGET_OUTPUT:
            m_Explanation = "'TargetOutput' element is missing in MCML (MCML/Server/Request/).";
            break;
        case XML_FORMAT_ERROR_VERSION:
            m_Explanation = "'Version' attribute is empty (MCML/).";
            break;
        case XML_FORMAT_ERROR_ID:
            m_Explanation = "'ID' attribute is missing in MCML, or has an invalid value (MCML/User/Transmitter/UserProfile/).";
            break;
        case XML_FORMAT_ERROR_SENTENCE_SEQ:
            m_Explanation = "'SentenceSequence' element is missing in MCML (MCML/Server/Request/Input/Data/Text/).";
            break;
        case XML_COMBINATION_ERROR:
            m_Explanation = "'URI' element and 'ID' attribute in MCML/User/Transmitter do not match with those in MCML/User/Receiver.";
            break;
        case XML_CREATION_ERROR:
            m_Explanation = "Failed to create XML data due to empty request.";
            break;
        case AUDIO_CONVERSION_ERROR:
            m_Explanation = "Failed to convert audio.";
            break;
        case AUDIO_BYTE_LENGTH_ERROR:
            m_Explanation = "Failed to convert audio due to abnormal byte length.";
            break;
        case PIVOT_REQUEST_ERROR:
            m_Explanation = "ConnectorBySocketAndStream.request() has failed.";
            break;
        case PIVOT_REQUEST_TIME_OUT:
            m_Explanation = "ConnectorBySocketAndStream.request() has timed out.";
            break;
        case MCML_RESPONSE_DATA_EMPTY:
            m_Explanation = "MCML response data was empty.";
            break;
        case MCML_RESPONSE_DATA_BODY_EMPTY:
            m_Explanation = "MCML response data body was body.";
            break;
        case FRAME_DATA_EMPTY:
            m_Explanation = "Frame data was empty.";
            break;
        case FRAME_DATA_BODY_EMPTY:
            m_Explanation = "Frame data body was empty.";
            break;
        case FRAME_DATA_SEQUENCE_ERROR:
            m_Explanation = "An unexpected order was detected in the frame data.";
            break;
        case MCMLDOC_ERROR:
            m_Explanation = "Failed to create MCML document at ThreadPerRequest.run().";
            break;
        default:
            break;
        }
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
