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

import java.util.ArrayList;
import java.util.regex.Pattern;

import jp.go.nict.mcml.xml.altova.types.SchemaFloat;
import jp.go.nict.mcml.xml.altova.types.SchemaInt;
import jp.go.nict.mcml.xml.types.AudioType;
import jp.go.nict.mcml.xml.types.DataType;
import jp.go.nict.mcml.xml.types.DeviceType;
import jp.go.nict.mcml.xml.types.ErrorType;
import jp.go.nict.mcml.xml.types.FromType;
import jp.go.nict.mcml.xml.types.GlobalPositionType;
import jp.go.nict.mcml.xml.types.InputModalityType;
import jp.go.nict.mcml.xml.types.InputType;
import jp.go.nict.mcml.xml.types.InputUserProfileType;
import jp.go.nict.mcml.xml.types.LanguageType;
import jp.go.nict.mcml.xml.types.LocationType;
import jp.go.nict.mcml.xml.types.MCMLType;
import jp.go.nict.mcml.xml.types.ModelTypeType;
import jp.go.nict.mcml.xml.types.OutputType;
import jp.go.nict.mcml.xml.types.PersonalityType;
import jp.go.nict.mcml.xml.types.ReceiverType;
import jp.go.nict.mcml.xml.types.RequestType;
import jp.go.nict.mcml.xml.types.ResponseType;
import jp.go.nict.mcml.xml.types.RoutingType;
import jp.go.nict.mcml.xml.types.SentenceSequenceType;
import jp.go.nict.mcml.xml.types.SentenceType;
import jp.go.nict.mcml.xml.types.ServerType;
import jp.go.nict.mcml.xml.types.SpeakingType;
import jp.go.nict.mcml.xml.types.TargetOutputType;
import jp.go.nict.mcml.xml.types.TextType;
import jp.go.nict.mcml.xml.types.ToType;
import jp.go.nict.mcml.xml.types.TransmitterType;
import jp.go.nict.mcml.xml.types.UserProfileType;
import jp.go.nict.mcml.xml.types.UserType;

/**
 * XMLTypeTools class.
 * 
 */
public class XMLTypeTools {
    // ------------------------------------------
    // public member functions
    // ------------------------------------------
    /**
     * Generates MCMLType.
     * 
     * @return MCMLType
     * @throws Exception
     */
    public static MCMLType generateMCMLType() throws Exception {
        MCMLType mcmlType = new MCMLType();
        mcmlType.addVersion(MCMLStatics.VERSION);
        return mcmlType;
    }

    /**
     * Gets ReceiverType.
     * 
     * @param mcmlType
     * @return ReceiverType
     * @throws Exception
     */
    public static ReceiverType getReceiverType(MCMLType mcmlType)
            throws Exception {
        return (mcmlType.hasUser() && mcmlType.getUser().hasReceiver()) ? mcmlType
                .getUser().getReceiver() : null;
    }

    /**
     * Gets URI.
     * 
     * @param mcmlType
     * @return URI character string
     * @throws Exception
     */
    public static String getURI(MCMLType mcmlType) throws Exception {
        return (mcmlType.hasUser()
                && mcmlType.getUser().hasTransmitter()
                && mcmlType.getUser().getTransmitter().hasDevice()
                && mcmlType.getUser().getTransmitter().getDevice()
                        .hasLocation() && mcmlType.getUser().getTransmitter()
                .getDevice().getLocation().hasURI()) ? mcmlType.getUser()
                .getTransmitter().getDevice().getLocation().getURI().getValue()
                : "";
    }

    /**
     * Get GlobalPositionType
     * 
     * @param mcmlType
     * @return GlobalPositionType
     * @throws Exception
     */
    public static GlobalPositionType getGlobalPosition(MCMLType mcmlType)
            throws Exception {
        GlobalPositionType result = null;
        if (mcmlType.hasUser()
                && mcmlType.getUser().hasTransmitter()
                && mcmlType.getUser().getTransmitter().hasDevice()
                && mcmlType.getUser().getTransmitter().getDevice()
                        .hasLocation()
                && mcmlType.getUser().getTransmitter().getDevice()
                        .getLocation().hasGlobalPosition()) {
            result = mcmlType.getUser().getTransmitter().getDevice()
                    .getLocation().getGlobalPosition();
        }
        return result;
    }

    /**
     * Get Longitude
     * 
     * @param globalPositionType
     * @return Longitude
     * @throws Exception
     */
    public static String getLongitude(GlobalPositionType globalPositionType)
            throws Exception {
        String result = null;
        if (globalPositionType != null && globalPositionType.hasLongitude()) {
            result = globalPositionType.getLongitude().toString();
        }
        return result;
    }

    /**
     * Get Latitude
     * 
     * @param globalPositionType
     * @return Latitude
     * @throws Exception
     */
    public static String getLatitude(GlobalPositionType globalPositionType)
            throws Exception {
        String result = null;
        if (globalPositionType != null && globalPositionType.hasLatitude()) {
            result = globalPositionType.getLatitude().toString();
        }
        return result;
    }

    /**
     * Gets URI.
     * 
     * @param receiverType
     * @return URI character string
     * @throws Exception
     */
    public static String getURI(ReceiverType receiverType) throws Exception {
        return (receiverType.hasDevice()
                && receiverType.getDevice().hasLocation() && receiverType
                .getDevice().getLocation().hasURI()) ? receiverType.getDevice()
                .getLocation().getURI().getValue() : "";
    }

    /**
     * Gets UserProfileType.
     * 
     * @param mcmlType
     * @return UserProfileType
     * @throws Exception
     */
    public static UserProfileType getUserProfileType(MCMLType mcmlType)
            throws Exception {
        return (mcmlType.hasUser() && mcmlType.getUser().hasTransmitter() && mcmlType
                .getUser().getTransmitter().hasUserProfile()) ? mcmlType
                .getUser().getTransmitter().getUserProfile() : null;
    }

    /**
     * Gets UserProfileType.
     * 
     * @param receiverType
     * @return UserProfileType
     * @throws Exception
     */
    public static UserProfileType getUserProfileType(ReceiverType receiverType)
            throws Exception {
        return (receiverType.hasUserProfile()) ? receiverType.getUserProfile()
                : null;
    }

    /**
     * Gets TransmitterIdList.
     * 
     * @param mcmlType
     * @return idList
     * @throws Exception
     */
    public static ArrayList<String> getTransmitterIdList(MCMLType mcmlType)
            throws Exception {
        ArrayList<String> idList = new ArrayList<String>();
        if (mcmlType.hasUser() && mcmlType.getUser().hasTransmitter()) {
            int tranCnt = mcmlType.getUser().getTransmitterCount();
            for (int i = 0; i < tranCnt; i++) {
                TransmitterType transmitterType = mcmlType.getUser()
                        .getTransmitterAt(i);
                if (transmitterType.hasUserProfile()) {
                    idList.add(transmitterType.getUserProfile().getID()
                            .getValue());
                }
            }
        }

        return idList;
    }

    /**
     * Gets ID.
     * 
     * @param userProfileType
     * @return ID
     * @throws Exception
     */
    public static String getID(UserProfileType userProfileType)
            throws Exception {
        return (userProfileType != null && userProfileType.hasID()) ? userProfileType
                .getID().getValue() : "";
    }

    /**
     * Gets Gender.
     * 
     * @param userProfileType
     * @return Gender
     * @throws Exception
     */
    public static String getGender(UserProfileType userProfileType)
            throws Exception {
        return (userProfileType != null && userProfileType.hasGender()) ? userProfileType
                .getGender().getValue() : "";
    }

    /**
     * Gets Age.
     * 
     * @param userProfileType
     * @return Age
     * @throws Exception
     */
    public static String getAge(UserProfileType userProfileType)
            throws Exception {
        return (userProfileType != null && userProfileType.hasAge()) ? new Integer(
                userProfileType.getAge().getValue()).toString() : "";
    }

    /**
     * Gets EMail.
     * 
     * @param userProfileType
     * @return EMail
     * @throws Exception
     */
    public static String getEMail(UserProfileType userProfileType)
            throws Exception {
        return (userProfileType != null && userProfileType.hasEmail()) ? userProfileType
                .getEmail().getValue() : "";
    }

    /**
     * Gets AccessCode.
     * 
     * @param userProfileType
     * @return String
     * @throws Exception
     */
    public static String getAccessCode(UserProfileType userProfileType)
            throws Exception {
        return (userProfileType != null && userProfileType.hasAccessCode()) ? userProfileType
                .getAccessCode().getValue() : "";
    }

    /**
     * Gets Password.
     * 
     * @param userProfileType
     * @return String
     * @throws Exception
     */
    public static String getPassword(UserProfileType userProfileType)
            throws Exception {
        return (userProfileType != null && userProfileType.hasPassword()) ? userProfileType
                .getPassword().getValue() : "";
    }

    /**
     * Gets UserID.
     * 
     * @param mcmlType
     * @return String
     * @throws Exception
     */
    public static String getUserID(MCMLType mcmlType) throws Exception {
        UserProfileType userProfileType = getUserProfileType(mcmlType);
        String id = getID(userProfileType);

        return (!id.isEmpty()) ? id : "";
    }

    /**
     * Gets UserID.
     * 
     * @param receiverType
     * @return String
     * @throws Exception
     */
    public static String getUserID(ReceiverType receiverType) throws Exception {
        UserProfileType userProfileType = getUserProfileType(receiverType);
        String id = getID(userProfileType);

        return (!id.isEmpty()) ? id : "";
    }

    /**
     * Gets splitUniqueUserIDD.
     * 
     * @param uniqueUserID
     * @return String[]
     */
    public static String[] splitUniqueUserID(String uniqueUserID) {
        return uniqueUserID.split(MCMLStatics.UNIQUE_USER_ID_SPLITTER);
    }

    /**
     * Gets ProcessOrder.
     * 
     * @param mcmlType
     * @return String
     * @throws Exception
     */
    public static String getProcessOrder(MCMLType mcmlType) throws Exception {
        String processOrder;

        if (hasRequest(mcmlType)
                && mcmlType.getServer().getRequest().hasProcessOrder()) {
            processOrder = new Integer(mcmlType.getServer().getRequest()
                    .getProcessOrder().getValue()).toString();
        } else if (hasResponse(mcmlType)
                && mcmlType.getServer().getResponse().hasProcessOrder()) {
            processOrder = new Integer(mcmlType.getServer().getResponse()
                    .getProcessOrder().getValue()).toString();
        } else {
            processOrder = "";
        }

        return processOrder;
    }

    /**
     * Gets Service.
     * 
     * @param mcmlType
     * @return String
     * @throws Exception
     */
    public static String getService(MCMLType mcmlType) throws Exception {
        String service;

        if (hasRequest(mcmlType)
                && mcmlType.getServer().getRequest().hasService()) {
            service = mcmlType.getServer().getRequest().getService().getValue();
        } else if (hasResponse(mcmlType)
                && mcmlType.getServer().getResponse().hasService()) {
            service = mcmlType.getServer().getResponse().getService()
                    .getValue();
        } else {
            service = "";
        }

        return service;
    }

    /**
     * Determines if Service is {@link MCMLStatics#SERVICE_USER_REGISTRATION} or not.
     * 
     * @param service
     * @return {@code true} if Service  is {@link MCMLStatics#SERVICE_USER_REGISTRATION}, otherwise
     *         {@code false}
     */
    public static boolean serviceIsUserRegistration(String service) {
        return service.equals(MCMLStatics.SERVICE_USER_REGISTRATION);
    }

    /**
     * Determines if Service is {@link MCMLStatics#SERVICE_USER_UNREGISTRATION}or not.
     * 
     * @param service
     * @return {@code true} if Service is {@link MCMLStatics#SERVICE_USER_UNREGISTRATION}, otherwise
     *        {@code false}
     */
    public static boolean serviceIsUserUnRegistration(String service) {
        return service.equals(MCMLStatics.SERVICE_USER_UNREGISTRATION);
    }

    /**
     * Determines if Service is {@link MCMLStatics#SERVICE_PARTY_REGISTRATION}or not.
     * 
     * @param service
     * @return {@code true} if Service is{@link MCMLStatics#SERVICE_PARTY_REGISTRATION}, otherwise
     *        {@code false}
     */
    public static boolean serviceIsPartyRegistration(String service) {
        return service.equals(MCMLStatics.SERVICE_PARTY_REGISTRATION);
    }

    /**
     * Determines if Service is {@link MCMLStatics#SERVICE_PARTY_UNREGISTRATION}or not.
     * 
     * @param service
     * @return {@code true} if Service is{@link MCMLStatics#SERVICE_PARTY_UNREGISTRATION},
     *         {@code true}, otherwise{@code false}
     */
    public static boolean serviceIsPartyUnRegistration(String service) {
        return service.equals(MCMLStatics.SERVICE_PARTY_UNREGISTRATION);
    }

    /**
     * Determines if Service is {@link MCMLStatics#SERVICE_USER_SEARCH}or not.
     * 
     * @param service
     * @return {@code true} if Service is{@link MCMLStatics#SERVICE_USER_SEARCH}, otherwise
     *       {@code false}
     */
    public static boolean serviceIsUserSearch(String service) {
        return service.equals(MCMLStatics.SERVICE_USER_SEARCH);
    }

    /**
     * Determines if Service is {@link MCMLStatics#SERVICE_ASR}or not.
     * 
     * @param service
     * @return {@code true} if Service is{@link MCMLStatics#SERVICE_ASR},  otherwise {@code false}
     *         {@code false}
     */
    public static boolean serviceIsASR(String service) {
        return service.equals(MCMLStatics.SERVICE_ASR);
    }

    /**
     * Determines if Service is {@link MCMLStatics#SERVICE_MT}or not.
     * 
     * @param service
     * @return {@code true} if Service is {@link MCMLStatics#SERVICE_MT}, otherwise
     *         {@code false}
     */
    public static boolean serviceIsMT(String service) {
        return service.equals(MCMLStatics.SERVICE_MT);
    }

    /**
     * Determines if Service is {@link MCMLStatics#SERVICE_TTS}or not.
     * 
     * @param service
     * @return {@code true} if Service is{@link MCMLStatics#SERVICE_TTS}, otherwise
     *         {@code false}
     */
    public static boolean serviceIsTTS(String service) {
        return service.equals(MCMLStatics.SERVICE_TTS);
    }

    /**
     *Determines if Service is {@link MCMLStatics#SERVICE_DIALOG_CONNECT}or not.
     * 
     * @param service
     * @return {@code true} if Service is {@link MCMLStatics#SERVICE_DIALOG_CONNECT},
     *         , otherwise{@code false}
     */
    public static boolean serviceIsDialogConnect(String service) {
        return service.equals(MCMLStatics.SERVICE_DIALOG_CONNECT);
    }

    /**
     * Determines if Service is {@link MCMLStatics#SERVICE_DIALOG}or not.
     * 
     * @param service
     * @return {@code true} if Service is {@link MCMLStatics#SERVICE_DIALOG}, otherwise
     *         {@code false}
     */
    public static boolean serviceIsDialog(String service) {
        return service.equals(MCMLStatics.SERVICE_DIALOG);
    }

    /**
     * Determines if Service is {@link MCMLStatics#SERVICE_POLLING}or not.
     * 
     * @param service
     * @return {@code true} if Service is {@link MCMLStatics#SERVICE_POLLING}, otherwise
     *         {@code false}
     */
    public static boolean serviceIsPolling(String service) {
        return service.equals(MCMLStatics.SERVICE_POLLING);
    }

    /**
     * Determines if Service is {@link MCMLStatics#SERVICE_INVITE}or not.
     * 
     * @param service
     * @return {@code true} if Service is {@link MCMLStatics#SERVICE_INVITE}, otherwise
     *         {@code false}
     */
    public static boolean serviceIsInvite(String service) {
        return service.equals(MCMLStatics.SERVICE_INVITE);
    }

    /**
     * Determines if Service is {@link MCMLStatics#SERVICE_BYE}or not.
     * 
     * @param service
     * @return {@code true} if Service is {@link MCMLStatics#SERVICE_BYE}, otherwise
     *         {@code false}
     */
    public static boolean serviceIsBye(String service) {
        return service.equals(MCMLStatics.SERVICE_BYE);
    }

    /**
     * Determines if Service is {@link MCMLStatics#SERVICE_ACCEPT}or not.
     * 
     * @param service
     * @return {@code true} if Service is{@link MCMLStatics#SERVICE_ACCEPT, otherwise
     *         {@code false}
     */
    public static boolean serviceIsAccept(String service) {
        return service.equals(MCMLStatics.SERVICE_ACCEPT);
    }

    /**
     * Determines if Service is {@link MCMLStatics#SERVICE_REJECT}or not.
     * 
     * @param service
     * @return {@code true} if Service is {@link MCMLStatics#SERVICE_REJECT}, {@code true}, otherwise
     *         {@code false}
     */
    public static boolean serviceIsReject(String service) {
        return service.equals(MCMLStatics.SERVICE_REJECT);
    }

    /**
     * Determines if Service is {@link MCMLStatics#SERVICE_GROUPINFORMATION}or not.
     * 
     * @param service
     * @return {@code true} if Service is{@link MCMLStatics#SERVICE_GROUPINFORMATION}
     *         , otherwise {@code false}
     */
    public static boolean serviceIsGroupInfomation(String service) {
        return service.equals(MCMLStatics.SERVICE_GROUPINFORMATION);
    }

    /**
     * Determines if request exists.
     * 
     * @param mcmlType
     * @return boolean
     * @throws Exception
     */
    public static boolean hasRequest(MCMLType mcmlType) throws Exception {
        return (mcmlType.hasServer() && mcmlType.getServer().hasRequest());
    }

    /**
     * Determines if that requests exists.
     * 
     * @param mcmlType
     * @return boolean
     * @throws Exception
     */
    public static boolean hasResponse(MCMLType mcmlType) throws Exception {
        return (mcmlType.hasServer() && mcmlType.getServer().hasResponse());
    }

    /**
     * Determines if error exsists.
     * 
     * @param mcmlType
     * @return boolean
     * @throws Exception
     */
    public static boolean hasError(MCMLType mcmlType) throws Exception {
        return (hasResponse(mcmlType) && mcmlType.getServer().getResponse()
                .hasError());
    }

    /**
     * Gets InputUserProfileType.
     * 
     * @param mcmlType
     * @return InputUserProfileType
     * @throws Exception
     */
    public static InputUserProfileType getInputUserProfileType(MCMLType mcmlType)
            throws Exception {
        return (hasRequest(mcmlType) && mcmlType.getServer().getRequest()
                .hasInputUserProfile()) ? mcmlType.getServer().getRequest()
                .getInputUserProfile() : null;
    }

    /**
     * Get LanguageType List
     * 
     * 
     * 
     * @param mcmlType
     * @return LanguageType List
     * @throws Exception
     */
    public static ArrayList<LanguageType> getLanguageList(MCMLType mcmlType)
            throws Exception {
        ArrayList<LanguageType> resultList = new ArrayList<LanguageType>();

        if (hasRequest(mcmlType)
                && mcmlType.getServer().getRequest().hasInputUserProfile()) {
            RequestType requestType = mcmlType.getServer().getRequest();

            int inputUserProfileTypeCount = requestType
                    .getInputUserProfileCount();
            for (int i = 0; i < inputUserProfileTypeCount; i++) {
                InputUserProfileType inputUserProfileType = requestType
                        .getInputUserProfileAt(i);
                if (inputUserProfileType.hasInputModality()
                        && inputUserProfileType.getInputModality()
                                .hasSpeaking()
                        && inputUserProfileType.getInputModality()
                                .getSpeaking().hasLanguage()
                        && inputUserProfileType.getInputModality()
                                .getSpeaking().getLanguage().hasID()
                        && inputUserProfileType.getInputModality()
                                .getSpeaking().getLanguage().hasFluency()) {
                    resultList.add(inputUserProfileType.getInputModality()
                            .getSpeaking().getLanguage());
                }
            }
        }
        return resultList;
    }

    /**
     * Native Check
     * 
     * @param mcmlType
     * @param sorceLanguage
     * @return Native: true UnNative: false
     * @throws Exception
     */
    public static boolean hasNative(MCMLType mcmlType, String sorceLanguage)
            throws Exception {
        boolean resultBoolean = false;

        if (hasRequest(mcmlType)
                && mcmlType.getServer().getRequest().hasInputUserProfile()) {
            RequestType requestType = mcmlType.getServer().getRequest();

            int inputUserProfileTypeCount = requestType
                    .getInputUserProfileCount();
            for (int i = 0; i < inputUserProfileTypeCount; i++) {
                InputUserProfileType inputUserProfileType = requestType
                        .getInputUserProfileAt(i);
                if (inputUserProfileType.hasInputModality()
                        && inputUserProfileType.getInputModality()
                                .hasSpeaking()
                        && inputUserProfileType.getInputModality()
                                .getSpeaking().hasLanguage()
                        && inputUserProfileType.getInputModality()
                                .getSpeaking().getLanguage().hasID()
                        && inputUserProfileType.getInputModality()
                                .getSpeaking().getLanguage().hasFluency()
                        && inputUserProfileType.getInputModality()
                                .getSpeaking().getLanguage().getID().getValue()
                                .equals(sorceLanguage)
                        && inputUserProfileType.getInputModality()
                                .getSpeaking().getLanguage().getFluency()
                                .getValue() == MCMLStatics.NATIVE_LANGUAGE) {
                    resultBoolean = true;
                }
            }
        }
        return resultBoolean;
    }

    /**
     * Get Source Language ID for ASR <Input><AttachedBinary DataID>
     * 
     * 
     * 
     * @param mcmlType
     * @return Returns results character string if conditions are set. If not met, {@code null}.
     * @throws Exception
     */
    public static String getInputAttachedBinaryID(MCMLType mcmlType)
            throws Exception {
        String resultString = null;
        if (hasRequest(mcmlType)
                && mcmlType.getServer().getRequest().hasInput()
                && mcmlType.getServer().getRequest().getInput()
                        .hasAttachedBinary()
                && mcmlType.getServer().getRequest().getInput()
                        .getAttachedBinary().hasDataID()) {
            resultString = mcmlType.getServer().getRequest().getInput()
                    .getAttachedBinary().getDataID().getValue();
        }
        return resultString;
    }

    /**
     * Get Source Language ID for TTS <TargetOutput><LanguageType ID>
     * 
     * 
     * 
     * @param mcmlType
     * @return Returns results character string if conditions are set. If not met, {@code null}.
     * @throws Exception
     */
    public static String getTargetOutputLanguageType(MCMLType mcmlType)
            throws Exception {
        String resultString = null;
        if (hasRequest(mcmlType)
                && mcmlType.getServer().getRequest().hasTargetOutput()
                && mcmlType.getServer().getRequest().getTargetOutput()
                        .hasLanguageType()) {
            resultString = mcmlType.getServer().getRequest().getTargetOutput()
                    .getLanguageType().getID().getValue();
        }

        return resultString;
    }

    /**
     * Get Source Language ID for MT <Input><Data><Text><ModelType><Language
     * ID=>
     * 
     * 
     * 
     * @param mcmlType
     * @return Returns results character string if conditions are set. If not met, {@code null}.
     * @throws Exception
     */
    public static String getInputDataTextModelTypeLanguageType(MCMLType mcmlType)
            throws Exception {
        String resultString = null;
        if (hasRequest(mcmlType)
                && mcmlType.getServer().getRequest().hasInput()
                && mcmlType.getServer().getRequest().getInput().hasData()
                && mcmlType.getServer().getRequest().getInput().getData()
                        .hasText()
                && mcmlType.getServer().getRequest().getInput().getData()
                        .getText().hasModelType()
                && mcmlType.getServer().getRequest().getInput().getData()
                        .getText().getModelType().hasLanguage()
                && mcmlType.getServer().getRequest().getInput().getData()
                        .getText().getModelType().getLanguage().hasID()) {
            resultString = mcmlType.getServer().getRequest().getInput()
                    .getData().getText().getModelType().getLanguage().getID()
                    .getValue();
        }

        return resultString;
    }

    /**
     * Gets AudioType.
     * 
     * @param mcmlType
     * @return AudioType
     * @throws Exception
     */
    public static AudioType getAudioType(MCMLType mcmlType) throws Exception {
        return (hasRequest(mcmlType)
                && mcmlType.getServer().getRequest().hasInput()
                && mcmlType.getServer().getRequest().getInput().hasData() && mcmlType
                .getServer().getRequest().getInput().getData().hasAudio()) ? mcmlType
                .getServer().getRequest().getInput().getData().getAudio()
                : null;
    }

    /**
     * Gets AudioType.
     * 
     * @param requestType
     * @return AudioType
     * @throws Exception
     */
    public static AudioType getAudioType(RequestType requestType)
            throws Exception {
        return (requestType.hasInput() && requestType.getInput().hasData() && requestType
                .getInput().getData().hasAudio()) ? requestType.getInput()
                .getData().getAudio() : null;
    }

    /**
     * Gets AudioType.
     * 
     * @param responseType
     * @return AudioType
     * @throws Exception
     */
    public static AudioType getAudioType(ResponseType responseType)
            throws Exception {
        return (responseType.hasOutput() && responseType.getOutput().hasData() && responseType
                .getOutput().getData().hasAudio()) ? responseType.getOutput()
                .getData().getAudio() : null;
    }

    /**
     * Gets NofN_best.
     * 
     * @param mcmlType
     * @return String
     * @throws Exception
     */
    public static String getNofN_best(MCMLType mcmlType) throws Exception {
        TargetOutputType targetOutputType = getTargetOutputType(mcmlType);
        return (targetOutputType != null && targetOutputType
                .hasHypothesisFormat()) ? targetOutputType
                .getHypothesisFormat().getNofN_best().getValue() : "";
    }

    /**
     * Gets LanguageType ID.
     * 
     * @param mcmlType
     * @return String
     * @throws Exception
     */
    public static String getLanguageTypeID(MCMLType mcmlType) throws Exception {
        TargetOutputType targetOutputType = getTargetOutputType(mcmlType);
        return (targetOutputType != null && targetOutputType.hasLanguageType() && targetOutputType
                .getLanguageType().hasID()) ? targetOutputType
                .getLanguageType().getID().getValue() : "";
    }

    /**
     * Gets TargetOutputLanguageID.
     * 
     * @param targetOutputType
     * @return String
     * @throws Exception
     */
    public static String getTargetOutputLanguageID(
            TargetOutputType targetOutputType) throws Exception {
        return (targetOutputType != null && targetOutputType.hasLanguageType() && targetOutputType
                .getLanguageType().hasID()) ? targetOutputType
                .getLanguageType().getID().getValue() : "";
    }

    /**
     * Gets TargetOutputType
     * 
     * @param mcmlType
     * @return TargetOutputType
     * @throws Exception
     */
    public static TargetOutputType getTargetOutputType(MCMLType mcmlType)
            throws Exception {
        return (hasRequest(mcmlType) && mcmlType.getServer().getRequest()
                .hasTargetOutput()) ? mcmlType.getServer().getRequest()
                .getTargetOutput() : null;
    }

    /**
     * Gets TextType.
     * 
     * @param mcmlType
     * @return TextType
     * @throws Exception
     */
    public static TextType getTextType(MCMLType mcmlType) throws Exception {
        return (hasRequest(mcmlType)
                && mcmlType.getServer().getRequest().hasInput()
                && mcmlType.getServer().getRequest().getInput().hasData() && mcmlType
                .getServer().getRequest().getInput().getData().hasText()) ? mcmlType
                .getServer().getRequest().getInput().getData().getText()
                : null;
    }

    /**
     * Gets TextType.
     * 
     * @param requestType
     * @return TextType
     * @throws Exception
     */
    public static TextType getTextType(RequestType requestType)
            throws Exception {
        return (requestType.hasInput() && requestType.getInput().hasData() && requestType
                .getInput().getData().hasText()) ? requestType.getInput()
                .getData().getText() : null;
    }

    /**
     * Gets TextType.
     * 
     * @param responseType
     * @return TextType
     * @throws Exception
     */
    public static TextType getTextType(ResponseType responseType)
            throws Exception {
        return (responseType.hasOutput() && responseType.getOutput().hasData() && responseType
                .getOutput().getData().hasText()) ? responseType.getOutput()
                .getData().getText() : null;
    }

    /**
     * Determines if RelayRequetor not.
     * 
     * @param mcmlType
     * @return boolean
     * @throws Exception
     */
    public static boolean isRelayRequet(MCMLType mcmlType) throws Exception {
        if (!mcmlType.hasServer() || !mcmlType.getServer().hasRequest()) {
            return false;
        }

        int requestCnt = mcmlType.getServer().getRequestCount();
        if (1 < requestCnt) {
            return true;
        }
        return false;
    }

    /**
     * Generates RequestTypeFromBaGenderML.
     * 
     * @param sourceRequest
     * @param mcmlType
     * @return RequestType
     * @throws Exception
     */
    public static RequestType generateRequestTypeFromBaseXML(
            RequestType sourceRequest, MCMLType mcmlType) throws Exception {
        // set service name
        RequestType outputRequest = new RequestType();
        outputRequest.addService(sourceRequest.getService());
        outputRequest.addProcessOrder(sourceRequest.getProcessOrder());

        // set RoutingType
        if (sourceRequest.hasRouting()) {
            outputRequest.addRouting(sourceRequest.getRouting());
        }

        // get DataType from base XML
        DataType dataType = getDataTypeFromMCML(mcmlType);

        // generate InputUserProfile from base XML data(DataType)
        InputUserProfileType inputUserProfileType = generateUsrProfileTypeFromDataType(dataType);

        // add InputUserProfile to dmRequest
        outputRequest.addInputUserProfile(inputUserProfileType);

        // add TargetOutput to ttsRequest
        outputRequest.addTargetOutput(sourceRequest.getTargetOutput());

        // get InputType from DataType
        InputType inputType = new InputType();
        inputType.addData(dataType);

        // get DataType from last time log
        outputRequest.addInput(inputType);

        return outputRequest;
    }

    /**
     * Gets ToUriList.
     * 
     * @param requestType
     * @return resultList
     * @throws Exception
     */
    public static ArrayList<String> getToUriList(RequestType requestType)
            throws Exception {
        ArrayList<String> resultList = null;
        if (!requestType.hasRouting()) {
            return resultList;
        }

        // create resultList
        resultList = new ArrayList<String>();

        // get To
        RoutingType routingType = requestType.getRouting();
        int cnt = routingType.getToCount();
        for (int i = 0; i < cnt; i++) {
            resultList.add(routingType.getToAt(i).getURI().getValue());
        }

        return resultList;
    }

    /**
     * Gets UserTransmitterURI.
     * 
     * @param mcmlType
     * @return String
     * @throws Exception
     */
    public static String getUserTransmitterURI(MCMLType mcmlType)
            throws Exception {
        if (!mcmlType.hasUser()
                || !mcmlType.getUser().hasTransmitter()
                || !mcmlType.getUser().getTransmitter().hasDevice()
                || !mcmlType.getUser().getTransmitter().getDevice()
                        .hasLocation()
                || !mcmlType.getUser().getTransmitter().getDevice()
                        .getLocation().hasURI()) {
            return "";
        }

        String uri = mcmlType.getUser().getTransmitter().getDevice()
                .getLocation().getURI().getValue();

        return uri;
    }

    /**
     * Gets SurfaceStringList.
     * 
     * @param mcmlType
     * @return resultList
     * @throws Exception
     */
    public static ArrayList<String> getSurfaceStringList(MCMLType mcmlType)
            throws Exception {
        ArrayList<String> resultList = new ArrayList<String>();

        if (hasRequest(mcmlType)) {
            int requestCnt = mcmlType.getServer().getRequestCount();
            for (int i = 0; i < requestCnt; i++) {
                RequestType requestType = mcmlType.getServer().getRequestAt(i);

                // next ResuestType(no InputType)
                if (!requestType.hasInput()) {
                    continue;
                }
                int inputCnt = requestType.getInputCount();
                for (int j = 0; j < inputCnt; j++) {
                    InputType inputType = requestType.getInputAt(j);

                    // next InputType(no DataType)
                    if (!inputType.hasData()) {
                        continue;
                    }
                    int dataCnt = inputType.getDataCount();
                    for (int k = 0; k < dataCnt; k++) {
                        resultList
                                .addAll(getSurfaceStringListInDataType(inputType
                                        .getDataAt(k)));
                    }
                }
            }
        } else if (hasResponse(mcmlType)) {
            int responseCnt = mcmlType.getServer().getResponseCount();
            for (int i = 0; i < responseCnt; i++) {
                ResponseType responseType = mcmlType.getServer().getResponseAt(
                        i);

                // next ResponseType(no OutputType)
                if (!responseType.hasOutput()) {
                    continue;
                }
                int outputCnt = responseType.getOutputCount();
                for (int j = 0; j < outputCnt; j++) {
                    OutputType outputType = responseType.getOutputAt(j);

                    // next OutputType(no DataType)
                    if (!outputType.hasData()) {
                        continue;
                    }
                    int dataCnt = outputType.getDataCount();
                    for (int k = 0; k < dataCnt; k++) {
                        resultList
                                .addAll(getSurfaceStringListInDataType(outputType
                                        .getDataAt(k)));
                    }
                }
            }
        } else {
            // no process
        }

        return resultList;
    }

    /**
     * Generates MCMLFromInfomations.
     * 
     * @param originalMCML
     * @param service
     * @param uri
     * @param dataMCML
     * @return MCMLType
     * @throws Exception
     */
    public static MCMLType generateMCMLFromInfomations(MCMLType originalMCML,
            String service, String uri, MCMLType dataMCML) throws Exception {
        // create output MCMLType
        MCMLType outputMCMLType = generateMCMLType();

        // get UserType form Response MCML
        UserType userType = originalMCML.getUser();
        outputMCMLType.addUser(userType);

        // create Response
        RequestType currentRequest = getRequestTypeAtService(originalMCML,
                service);
        ResponseType responseType = new ResponseType();
        if (currentRequest.hasProcessOrder()) {
            responseType.addProcessOrder(currentRequest.getProcessOrder());
        }
        responseType.addService(service);

        // create RoutingType
        RoutingType routingType = new RoutingType();

        // create FromType
        if (currentRequest.getRouting().hasFrom()) {
            FromType fromType = new FromType();
            fromType.addURI(currentRequest.getRouting().getFrom().getURI()
                    .getValue());
            // set FromType to routingType
            routingType.addFrom(fromType);
        }

        // create ToType
        ToType toType = new ToType();
        toType.addURI(uri);
        routingType.addTo(toType);

        // set RoutingType to responseType
        responseType.addRouting(routingType);

        // get DataType for Output
        DataType dataType = getDataTypeFromMCML(dataMCML);

        // create OutputType
        OutputType outputType = new OutputType();
        outputType.addData(dataType);
        responseType.addOutput(outputType);

        // create ServerType
        ServerType serverType = new ServerType();
        serverType.addResponse(responseType);
        outputMCMLType.addServer(serverType);

        return outputMCMLType;

    }

    /**
     * Generates DeviceType.
     * 
     * @param uri
     * @param globalPositions
     * @return DeviceType
     * @throws Exception
     */
    public static DeviceType generateDeviceType(String uri,
            ArrayList<Float> globalPositions) throws Exception {
        LocationType locationType = null;

        // add URI to LocationType
        if (uri != null && !uri.isEmpty()) {
            locationType = new LocationType();
            locationType.addURI(uri);
        }

        // add Global Position to LocationType
        if (globalPositions != null && !globalPositions.isEmpty()
                && (globalPositions.size() == 2)) {
            if (locationType == null) {
                locationType = new LocationType();
            }

            // set Global Position
            GlobalPositionType gType = new GlobalPositionType();
            gType.addLongitude(new SchemaFloat(globalPositions.get(0)));
            gType.addLatitude(new SchemaFloat(globalPositions.get(1)));

            locationType.addGlobalPosition(gType);
        }

        // create DeviceType
        DeviceType deviceType = null;
        if (locationType != null) {
            deviceType = new DeviceType();
            deviceType.addLocation(locationType);
        }

        return deviceType;
    }

    /**
     * Generates UserProfileType.
     * 
     * @param id
     * @param email
     * @param age
     * @param gender
     * @return UserProfileType
     * @throws Exception
     */
    public static UserProfileType generateUserProfileType(String id,
            String email, int age, String gender) throws Exception {
        // create UserProfileType
        UserProfileType userProfileType = null;

        // set ID
        if (id != null && !id.isEmpty()) {
            userProfileType = new UserProfileType();
            userProfileType.addID(id);
        }

        // set E-Mail Address
        if (email != null && !email.isEmpty()) {
            if (userProfileType == null) {
                userProfileType = new UserProfileType();
            }
            userProfileType.addEmail(email);
        }

        // set age
        if (0 <= age) {
            if (userProfileType == null) {
                userProfileType = new UserProfileType();
            }
            userProfileType.addAge(new SchemaInt(age));
        }

        // set gender
        if (gender != null && !gender.isEmpty()) {
            if (userProfileType == null) {
                userProfileType = new UserProfileType();
            }
            userProfileType.addGender(gender);
        }

        return userProfileType;
    }

    /**
     * Generates ErrorResponse.
     * 
     * @param code
     * @param explanation
     * @param requestData
     * @return MCMLType
     * @throws Exception
     */
    public static MCMLType generateErrorResponse(String code,
            String explanation, MCMLType requestData) throws Exception {
        // get parameters
        String service = (requestData != null) ? XMLTypeTools
                .getService(requestData) : "";
        String processOrder = (requestData != null) ? XMLTypeTools
                .getProcessOrder(requestData) : "";

        // generate ErrorType
        ErrorType errorType = new ErrorType();
        errorType.addCode(code);
        errorType.addMessage(explanation);
        errorType.addService(service);

        // generate ResponseType
        ResponseType responseType = new ResponseType();
        responseType.addService(service);
        responseType.addProcessOrder(processOrder);
        responseType.addError(errorType);

        // generate ServerType
        ServerType serverType = new ServerType();
        serverType.addResponse(responseType);

        // generate MCMLType
        MCMLType mcmlType = generateEmptyData();
        mcmlType.addServer(serverType);

        return mcmlType;
    }

    /**
     * Generates EmptyData.
     * 
     * @return MCMLType
     * @throws Exception
     */
    public static MCMLType generateEmptyData() throws Exception {
        // Version
        MCMLType mcmlType = new MCMLType();
        mcmlType.addVersion(MCMLStatics.VERSION);

        return mcmlType;
    }

    /**
     * Generates ErrorType.
     * 
     * @param errorCode
     * @param message
     * @param service
     * @return ErrorType
     * @throws Exception
     */
    public static ErrorType generateErrorType(String errorCode, String message,
            String service) throws Exception {
        if ((errorCode == null || errorCode.isEmpty())
                || (service == null || service.isEmpty())) {
            return null;
        }

        ErrorType errorType = new ErrorType();
        errorType.addCode(errorCode);

        if (message != null && !message.isEmpty()) {
            errorType.addMessage(message);
        }

        errorType.addService(service);

        return errorType;
    }

    /**
     * Gets ErrorCode.
     * 
     * @param mcmlType
     * @return String
     * @throws Exception
     */
    public static String getErrorCode(MCMLType mcmlType) throws Exception {
        return (hasError(mcmlType) && mcmlType.getServer().getResponse()
                .getError().hasCode()) ? mcmlType.getServer().getResponse()
                .getError().getCode().getValue() : "";
    }

    /**
     * Gets ErrorMessage.
     * 
     * @param mcmlType
     * @return String
     * @throws Exception
     */
    public static String getErrorMessage(MCMLType mcmlType) throws Exception {
        return (hasError(mcmlType) && mcmlType.getServer().getResponse()
                .getError().hasMessage()) ? mcmlType.getServer().getResponse()
                .getError().getMessage().getValue() : "";
    }

    /**
     * Gets ErrorService.
     * 
     * @param mcmlType
     * @return String
     * @throws Exception
     */
    public static String getErrorService(MCMLType mcmlType) throws Exception {
        return (hasError(mcmlType) && mcmlType.getServer().getResponse()
                .getError().hasService()) ? mcmlType.getServer().getResponse()
                .getError().getService().getValue() : "";
    }

    /**
     * Gets TextTypeFromRequest.
     * 
     * @param requestType
     * @return TextType
     * @throws Exception
     */
    public static TextType getTextTypeFromRequest(RequestType requestType)
            throws Exception {
        return (requestType.hasInput() && requestType.getInput().hasData() && requestType
                .getInput().getData().hasText()) ? requestType.getInput()
                .getData().getText() : null;
    }

    /**
     * Gets TextTypeFromResponse.
     * 
     * @param responseType
     * @return TextType
     * @throws Exception
     */
    public static TextType getTextTypeFromResponse(ResponseType responseType)
            throws Exception {
        return (responseType.hasOutput() && responseType.getOutput().hasData() && responseType
                .getOutput().getData().hasText()) ? responseType.getOutput()
                .getData().getText() : null;
    }

    /**
     * Gets DataTypeFromMCML.
     * 
     * @param mcmlType
     * @return DataType
     * @throws Exception
     */
    public static DataType getDataTypeFromMCML(MCMLType mcmlType)
            throws Exception {
        DataType data = null;
        if (hasRequest(mcmlType)) {
            // get data from request
            data = getDataTypeFromRequest(mcmlType.getServer().getRequest());
        } else if (hasResponse(mcmlType)) {
            // get data from response
            data = getDataTypeFromResponse(mcmlType.getServer().getResponse());
        } else {
            return null;
        }

        return data;
    }

    /**
     * Gets DataTypeFromRequest.
     * 
     * @param requestType
     * @return DataType
     * @throws Exception
     */
    public static DataType getDataTypeFromRequest(RequestType requestType)
            throws Exception {
        DataType data = null;
        // get data from request
        if (requestType.hasInput()) {
            if (requestType.getInput().hasData()) {
                data = requestType.getInput().getData();
            }
        }

        return data;
    }

    /**
     * Gets DataTypeFromResponse.
     * 
     * @param responseType
     * @return DataType
     * @throws Exception
     */
    public static DataType getDataTypeFromResponse(ResponseType responseType)
            throws Exception {
        DataType data = null;
        // get data from response
        if (responseType.hasOutput()) {
            if (responseType.getOutput().hasData()) {
                data = responseType.getOutput().getData();
            }
        }

        return data;
    }

    /**
     * Gets SentenceSequenceTypeFromDataType.
     * 
     * @param dataType
     * @return SentenceSequenceType
     * @throws Exception
     */
    public static SentenceSequenceType getSentenceSequenceTypeFromDataType(
            DataType dataType) throws Exception {
        // get SentenceSequence from dataType
        return (dataType != null && dataType.hasText() && dataType.getText()
                .hasSentenceSequence()) ? dataType.getText()
                .getSentenceSequence() : null;
    }

    /**
     * Gets PersonalityID.
     * 
     * @param dataType
     * @return String
     * @throws Exception
     */
    public static String getPersonalityID(DataType dataType) throws Exception {
        return (dataType != null && dataType.hasText()
                && dataType.getText().hasModelType()
                && dataType.getText().getModelType().hasPersonality() && dataType
                .getText().getModelType().getPersonality().hasID()) ? dataType
                .getText().getModelType().getPersonality().getID().getValue()
                : "";
    }

    /**
     * Gets ModelLanguage.
     * 
     * @param dataType
     * @return String
     * @throws Exception
     */
    public static String getModelLanguage(DataType dataType) throws Exception {
        // get SentenceSequence from dataType
        return (dataType != null && dataType.hasText()
                && dataType.getText().hasModelType()
                && dataType.getText().getModelType().hasLanguage() && dataType
                .getText().getModelType().getLanguage().hasID()) ? dataType
                .getText().getModelType().getLanguage().getID().getValue() : "";
    }

    /**
     * get Client IP address from UserURI
     * 
     * @param userURI
     * @return
     */
    public static String getIPAddressFromUserURI(String userURI) {
        String[] splitUserURI = userURI.split("_");
        String lastSplit = splitUserURI[splitUserURI.length - 1];

        // IP Address Format
        String regex = "^(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])$";

        // Regular expression: IP address format
        Pattern pattern = Pattern.compile(regex);

        if (pattern.matcher(lastSplit).matches()) {
            return lastSplit;
        }
        if (splitUserURI.length > 2) {
            String secondSplit = splitUserURI[splitUserURI.length - 2];
            if (pattern.matcher(secondSplit).matches()) {
                return secondSplit;
            }
        }
        return "";
    }

    // ------------------------------------------
    // private member functions
    // ------------------------------------------
    private static RequestType getRequestTypeAtService(MCMLType mcmlType,
            String serviceName) throws Exception {
        RequestType outputRequestType = null;
        if (!mcmlType.hasServer() || !mcmlType.getServer().hasRequest()) {
            return outputRequestType;
        }

        ServerType serverType = mcmlType.getServer();
        int requestCnt = serverType.getRequestCount();
        for (int i = 0; i < requestCnt; i++) {
            if (serverType.getRequestAt(i).getService().getValue()
                    .equals(serviceName)) {
                outputRequestType = serverType.getRequestAt(i);
                break;
            }
        }

        return outputRequestType;
    }

    // generate user type
    private static InputUserProfileType generateUsrProfileTypeFromDataType(
            DataType dataType) throws Exception {
        // get ModelTypeType from DataType
        ModelTypeType modelType = dataType.getText().getModelType();

        // get personality from ModelTypeType
        PersonalityType personality = modelType.getPersonality();

        // add user ID
        InputUserProfileType inputUserProfileType = new InputUserProfileType();
        if (personality.hasID()) {
            String id = personality.getID().getValue();
            inputUserProfileType.addID(id);
        }
        // add gender
        if (personality.hasGender()) {
            String gender = personality.getGender().getValue();
            inputUserProfileType.addGender(gender);
        }
        // add age
        if (personality.hasAge()) {
            String age = personality.getAge().toString();
            inputUserProfileType.addAge(age);
        }
        // add language Id to languageType
        LanguageType languageType = new LanguageType();
        if (modelType.getLanguage().hasID()) {
            String languageId = modelType.getLanguage().getID().getValue();
            languageType.addID(languageId);
        }

        // add fluency to language type
        if (modelType.getLanguage().hasFluency()) {
            String languageFluency = String.valueOf(modelType.getLanguage()
                    .getFluency().getValue());
            languageType.addFluency(languageFluency);
        }

        // add language type to speaking type
        SpeakingType speakingType = new SpeakingType();
        speakingType.addLanguage(languageType);

        // add speaking type to input modality
        InputModalityType inputModalityType = new InputModalityType();
        inputModalityType.addSpeaking(speakingType);

        // add input Modality to input user profile
        inputUserProfileType.addInputModality(inputModalityType);

        return inputUserProfileType;
    }

    private static ArrayList<String> getSurfaceStringListInDataType(
            DataType dataType) throws Exception {
        ArrayList<String> resultList = new ArrayList<String>();

        if (!dataType.hasText()) {
            return resultList;
        }

        int textCnt = dataType.getTextCount();
        for (int i = 0; i < textCnt; i++) {
            TextType textType = dataType.getTextAt(i);

            // next TextType(no SentenceSequenceType)
            if (!textType.hasSentenceSequence()) {
                continue;
            }
            int sentenceSeqCnt = textType.getSentenceSequenceCount();
            for (int j = 0; j < sentenceSeqCnt; j++) {
                SentenceSequenceType sentSeqType = textType
                        .getSentenceSequenceAt(j);

                // next SentenceSequenceType(no SentenceType)
                if (!sentSeqType.hasSentence()) {
                    continue;
                }
                int sentenceCnt = sentSeqType.getSentenceCount();
                for (int k = 0; k < sentenceCnt; k++) {
                    SentenceType sentenceType = sentSeqType.getSentenceAt(k);

                    // next SentenceType(no SurfaceType)
                    if (!sentenceType.hasSurface()) {
                        continue;
                    }
                    int surfaceCnt = sentenceType.getSurfaceCount();
                    for (int l = 0; l < surfaceCnt; l++) {
                        // add String
                        resultList.add(sentenceType.getSurfaceAt(l).getValue()
                                .toString());
                    }
                }
            }
        }
        return resultList;
    }
}
