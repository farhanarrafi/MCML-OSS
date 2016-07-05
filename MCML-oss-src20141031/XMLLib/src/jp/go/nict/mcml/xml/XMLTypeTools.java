package jp.go.nict.mcml.xml;

import java.util.ArrayList;
import java.util.regex.Pattern;

import com.MCML.AudioType;
import com.MCML.DataType;
import com.MCML.DeviceType;
import com.MCML.ErrorType;
import com.MCML.GlobalPositionType;
import com.MCML.HistoryType;
import com.MCML.InputModalityType;
import com.MCML.InputType;
import com.MCML.InputUserProfileType;
import com.MCML.LanguageType;
import com.MCML.LanguageTypeType;
import com.MCML.LocationType;
import com.MCML.MCMLDoc;
import com.MCML.MCMLType;
import com.MCML.ModelTypeType;
import com.MCML.OptionType;
import com.MCML.OutputType;
import com.MCML.PersonalityType;
import com.MCML.ReceiverType;
import com.MCML.RequestType;
import com.MCML.ResponseType;
import com.MCML.RoutingType;
import com.MCML.SentenceSequenceType;
import com.MCML.SentenceType;
import com.MCML.ServerType;
import com.MCML.SurfaceType2;
import com.MCML.TargetOutputType;
import com.MCML.TextType;
import com.MCML.TransmitterType;
import com.MCML.UserProfileType;
import com.MCML.UserType;

/**
 * Common function MCML elements
 *
 * @author knozaki
 * @version 4.0
 * @since 20120921
 */
public class XMLTypeTools {
	private final static String		SCHEMA_LOCATON		= "MCML4ITU_Sep6-12.xsd";
	private static final String		URI_DELIMETER		= "_";
	private static final int		URI_GRP_NO_INDEX	= 2;
	private static final Exception	Exception			= null;

	// ------------------------------------------
	// public member functions
	// ------------------------------------------
	/**
	 * @param mcmlDoc
	 * @return
	 * @throws Exception
	 */
	public static String generate(MCMLDoc mcmlDoc) throws Exception {
		mcmlDoc.setSchemaLocation(SCHEMA_LOCATON);
		return mcmlDoc.saveToString(true);
	}

	/**
	 * @param mcmlDoc
	 * @return
	 * @throws Exception
	 */
	public static MCMLType generateMCMLType(MCMLDoc mcmlDoc) throws Exception {
		MCMLType mcmlType = mcmlDoc.MCML.append();
		mcmlType.Version.setValue(MCMLStatics.VERSION);
		return mcmlType;
	}

	/**
	 * @param mcmlType
	 * @return
	 * @throws Exception
	 */
	public static ReceiverType getReceiverType(MCMLType mcmlType) throws Exception {
		return (mcmlType.User.exists() && mcmlType.User.first().Receiver.exists()) ? mcmlType.User.first().Receiver
				.first() : null;
	}

	/**
	 * @param mcmlDoc
	 * @return
	 * @throws Exception
	 */
	public static String getURI(MCMLDoc mcmlDoc) throws Exception {
		if (mcmlDoc.MCML.exists()) {
			MCMLType mcmlType = mcmlDoc.MCML.first();
			return getURI(mcmlType);
		}
		return "";
	}

	/**
	 * @param mcmlType
	 * @return
	 * @throws Exception
	 */
	public static String getURI(MCMLType mcmlType) throws Exception {
		return (mcmlType.User.exists() && mcmlType.User.first().Transmitter.exists()
				&& mcmlType.User.first().Transmitter.first().Device.exists()
				&& mcmlType.User.first().Transmitter.first().Device.first().Location.exists() && mcmlType.User.first().Transmitter
				.first().Device.first().Location.first().URI.exists())
				? mcmlType.User.first().Transmitter.first().Device.first().Location.first().URI.first().getValue() : "";
	}

	/**
	 * @param receiverType
	 * @return
	 * @throws Exception
	 */
	public static String getURI(ReceiverType receiverType) throws Exception {
		return (receiverType.Device.exists() && receiverType.Device.first().Location.exists() && receiverType.Device
				.first().Location.first().URI.exists()) ? receiverType.Device.first().Location.first().URI.first()
				.getValue() : "";
	}

	/**
	 * @param mcmlDoc
	 * @return
	 * @throws Exception
	 */
	public static double getLatitude(MCMLDoc mcmlDoc) throws Exception {
		return mcmlDoc.MCML.first().User.first().Transmitter.first().Device.first().Location.first().GlobalPosition
				.first().Latitude.getValue();
	}

	/**
	 * @param mcmlDoc
	 * @return
	 * @throws Exception
	 */
	public static double getLongitude(MCMLDoc mcmlDoc) throws Exception {
		return mcmlDoc.MCML.first().User.first().Transmitter.first().Device.first().Location.first().GlobalPosition
				.first().Longitude.getValue();
	}

	/**
	 * @param mcmlDoc
	 * @return
	 * @throws Exception
	 */
	public static UserProfileType getUserProfileType(MCMLDoc mcmlDoc) throws Exception {
		if (mcmlDoc.MCML.exists()) {
			MCMLType mcmlType = mcmlDoc.MCML.first();
			return getUserProfileType(mcmlType);
		}
		return null;
	}

	/**
	 * @param mcmlType
	 * @return
	 * @throws Exception
	 */
	public static UserProfileType getUserProfileType(MCMLType mcmlType) throws Exception {
		return (mcmlType.User.exists() && mcmlType.User.first().Transmitter.exists() && mcmlType.User.first().Transmitter
				.first().UserProfile.exists()) ? mcmlType.User.first().Transmitter.first().UserProfile.first() : null;
	}

	/**
	 * @param receiverType
	 * @return
	 * @throws Exception
	 */
	public static UserProfileType getUserProfileType(ReceiverType receiverType) throws Exception {
		return (receiverType.UserProfile.exists()) ? receiverType.UserProfile.first() : null;
	}

	/**
	 * @param mcmlDoc
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<String> getTransmitterIdList(MCMLDoc mcmlDoc) throws Exception {
		if (mcmlDoc.MCML.exists()) {
			return getTransmitterIdList(mcmlDoc.MCML.first());
		}
		return null;
	}

	/**
	 * @param mcmlType
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<String> getTransmitterIdList(MCMLType mcmlType) throws Exception {
		ArrayList<String> idList = new ArrayList<String>();
		if (mcmlType.User.exists() && mcmlType.User.first().Transmitter.exists()) {
			int tranCnt = mcmlType.User.first().Transmitter.count();
			for (int i = 0; i < tranCnt; i++) {
				TransmitterType transmitterType = mcmlType.User.first().Transmitter.at(i);
				if (transmitterType.UserProfile.exists()) {
					idList.add(transmitterType.UserProfile.first().ID.getValue());
				}
			}
		}

		return idList;
	}

	/**
	 * @param userProfileType
	 * @return
	 * @throws Exception
	 */
	public static String getID(UserProfileType userProfileType) throws Exception {
		return (userProfileType != null && userProfileType.ID.exists()) ? userProfileType.ID.getValue() : "";
	}

	/**
	 * @param mcmlDoc
	 * @return
	 * @throws Exception
	 */
	public static String getGender(MCMLDoc mcmlDoc) throws Exception {
		if (mcmlDoc.MCML.exists() && mcmlDoc.MCML.first().Server.exists()
				&& mcmlDoc.MCML.first().Server.first().Request.exists()) {
//			return getGender(mcmlDoc.MCML.first().User.first().Transmitter.first().UserProfile.first());
			return getGender(mcmlDoc.MCML.first().Server.first().Request.first().InputUserProfile.first());
		} else if (mcmlDoc.MCML.exists() && mcmlDoc.MCML.first().Server.exists()
				&& mcmlDoc.MCML.first().Server.first().Response.exists()) {
			if (serviceIsTTS(mcmlDoc.MCML.first().Server.first().Response.first().Service.getValue())) {
				return mcmlDoc.MCML.first().Server.first().Response.first().Output.first().Data.first().Audio.first().ModelType
						.first().Personality.first().Gender.getValue();
			} else {
				return mcmlDoc.MCML.first().Server.first().Response.first().Output.first().Data.first().Text.first().ModelType
						.first().Personality.first().Gender.getValue();
			}
		} else {
			throw new MCMLException("Can not get Gender.", MCMLException.SYSTEM, MCMLException.COMMON,
					MCMLException.ABNORMAL_XML_DATA_FORMAT);
		}
	}

	/**
	 * @param inputUserProfileType
	 * @return
	 * @throws Exception
	 */
	public static String getGender(InputUserProfileType inputUserProfileType) throws Exception {
		return (inputUserProfileType != null && inputUserProfileType.Gender.exists()) ? inputUserProfileType.Gender.getValue() : "";
	}

	/**
	 * @param userProfileType
	 * @return
	 * @throws Exception
	 */
	public static String getGender(UserProfileType userProfileType) throws Exception {
		return (userProfileType != null && userProfileType.Gender.exists()) ? userProfileType.Gender.getValue() : "";
	}

	/**
	 * @param mcmlDoc
	 * @return
	 * @throws Exception
	 */
	public static String getAge(MCMLDoc mcmlDoc) throws Exception {
		if (mcmlDoc.MCML.exists() && mcmlDoc.MCML.first().Server.exists()
				&& mcmlDoc.MCML.first().Server.first().Request.exists()) {
//			return getAge(mcmlDoc.MCML.first().User.first().Transmitter.first().UserProfile.first());
			return getAge(mcmlDoc.MCML.first().Server.first().Request.first().InputUserProfile.first());
		} else if (mcmlDoc.MCML.exists() && mcmlDoc.MCML.first().Server.exists()
				&& mcmlDoc.MCML.first().Server.first().Response.exists()) {
			if (serviceIsTTS(mcmlDoc.MCML.first().Server.first().Response.first().Service.getValue())) {
				return Integer
						.toString(mcmlDoc.MCML.first().Server.first().Response.first().Output.first().Data.first().Audio
								.first().ModelType.first().Personality.first().Age.getValue());
			} else {
				return Integer
						.toString(mcmlDoc.MCML.first().Server.first().Response.first().Output.first().Data.first().Text
								.first().ModelType.first().Personality.first().Age.getValue());
			}
		} else {
			throw new MCMLException("Can not get Gender.", MCMLException.SYSTEM, MCMLException.COMMON,
					MCMLException.ABNORMAL_XML_DATA_FORMAT);
		}
	}

	/**
	 * @param userProfileType
	 * @return
	 * @throws Exception
	 */
	public static String getAge(InputUserProfileType inputUserProfileType) throws Exception {
		return (inputUserProfileType != null && inputUserProfileType.Age.exists()) ? new Integer(inputUserProfileType.Age.getValue())
				.toString() : "";
	}

	/**
	 * @param userProfileType
	 * @return
	 * @throws Exception
	 */
	public static String getAge(UserProfileType userProfileType) throws Exception {
		return (userProfileType != null && userProfileType.Age.exists()) ? new Integer(userProfileType.Age.getValue())
				.toString() : "";
	}

	/**
	 * @param userProfileType
	 * @return
	 * @throws Exception
	 */
	public static String getEMail(UserProfileType userProfileType) throws Exception {
		return (userProfileType != null && userProfileType.Email.exists()) ? userProfileType.Email.getValue() : "";
	}

	/**
	 * @param userProfileType
	 * @return
	 * @throws Exception
	 */
	public static String getAccessCode(UserProfileType userProfileType) throws Exception {
		return (userProfileType != null && userProfileType.AccessCode.exists()) ? userProfileType.AccessCode.getValue()
				: "";
	}

	/**
	 * @param userProfileType
	 * @return
	 * @throws Exception
	 */
	public static String getPassword(UserProfileType userProfileType) throws Exception {
		return (userProfileType != null && userProfileType.Password.exists()) ? userProfileType.Password.getValue()
				: "";
	}

	/**
	 * @param mcmlType
	 * @return
	 * @throws Exception
	 */
	public static String getUserID(MCMLType mcmlType) throws Exception {
		UserProfileType userProfileType = getUserProfileType(mcmlType);
		String id = getID(userProfileType);

		return (!id.isEmpty()) ? id : "";
	}

	/**
	 * @param mcmlDoc
	 * @return
	 * @throws Exception
	 */
	public static String getUserID(MCMLDoc mcmlDoc) throws Exception {
		if (mcmlDoc.MCML.exists()) {
			MCMLType mcmlType = mcmlDoc.MCML.first();
			return getUserID(mcmlType);
		}
		return "";
	}

	/**
	 * @param receiverType
	 * @return
	 * @throws Exception
	 */
	public static String getUserID(ReceiverType receiverType) throws Exception {
		UserProfileType userProfileType = getUserProfileType(receiverType);
		String id = getID(userProfileType);

		return (!id.isEmpty()) ? id : "";
	}

	/**
	 * Get UserID List with out Transmitter ID
	 *
	 * @param mcmlDoc
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<String> getUserIdListWOMe(MCMLDoc mcmlDoc) throws Exception {
		ArrayList<String> userIdList = new ArrayList<String>();

		String userTransmitter = getUserID(mcmlDoc);
		for (int i = 0; i < getReceiverCount(mcmlDoc); i++) {
			String userId = mcmlDoc.MCML.first().User.first().Receiver.at(i).UserProfile.first().ID.getValue();
			if (!userTransmitter.equals(userId)) {
				userIdList.add(userId);
			}
		}
		return userIdList;
	}

	/**
	 * Get count of Receivers
	 *
	 * @param mcmlDoc
	 * @return
	 * @throws Exception
	 */
	public static int getReceiverCount(MCMLDoc mcmlDoc) throws Exception {
		return mcmlDoc.MCML.first().User.first().Receiver.count();
	}

	/**
	 * @param uniqueUserID
	 * @return
	 */
	public static String[] splitUniqueUserID(String uniqueUserID) {
		return uniqueUserID.split(MCMLStatics.UNIQUE_USER_ID_SPLITTER);
	}

	/**
	 * @param mcmlDoc
	 * @return
	 * @throws Exception
	 */
	public static int getProcessOrder(MCMLDoc mcmlDoc) throws Exception {
		if (mcmlDoc.MCML.exists()) {
			MCMLType mcmlType = mcmlDoc.MCML.first();
			return getProcessOrder(mcmlType);
		}
		return 0;
	}

	/**
	 * @param mcmlType
	 * @return
	 * @throws Exception
	 */
	public static int getProcessOrder(MCMLType mcmlType) throws Exception {
	// public static String getProcessOrder(MCMLType mcmlType) throws Exception
		int processOrder;

		if (hasRequest(mcmlType) && mcmlType.Server.first().Request.first().ProcessOrder.exists()) {
			// processOrder = new
			// Integer(mcmlType.Server.first().Request.first().ProcessOrder.getValue()).toString()
			// ;
			processOrder = mcmlType.Server.first().Request.first().ProcessOrder.getValue();
		} else if (hasResponse(mcmlType) && mcmlType.Server.first().Response.first().ProcessOrder.exists()) {
			// processOrder = new
			// Integer(mcmlType.Server.first().Response.first().ProcessOrder.getValue()).toString()
			// ;
			processOrder = mcmlType.Server.first().Response.first().ProcessOrder.getValue();
		} else {
			processOrder = 0;
		}

		return processOrder;
	}

	/**
	 * @param mcmlDoc
	 * @return
	 * @throws Exception
	 */
	public static String getService(MCMLDoc mcmlDoc) throws Exception {
		if (mcmlDoc.MCML.exists()) {
			MCMLType mcmlType = mcmlDoc.MCML.first();
			return getService(mcmlType);
		}
		return "";
	}

	/**
	 * @param mcmlType
	 * @return
	 * @throws Exception
	 */
	public static String getService(MCMLType mcmlType) throws Exception {
		String service;

		if (hasRequest(mcmlType) && mcmlType.Server.first().Request.first().Service.exists()) {
			service = mcmlType.Server.first().Request.first().Service.getValue();
		} else if (hasResponse(mcmlType) && mcmlType.Server.first().Response.first().Service.exists()) {
			service = mcmlType.Server.first().Response.first().Service.getValue();
		} else {
			service = "";
		}

		return service;
	}

	/**
	 * get Source Language
	 *
	 * @param mcmlDoc
	 * @return String
	 * @throws Exception
	 */
	public static String getSourceLanguage(MCMLDoc mcmlDoc) throws Exception {
		String service = getService(mcmlDoc);
		if (mcmlDoc.MCML.exists()
				&& mcmlDoc.MCML.first().Server.exists()
				&& mcmlDoc.MCML.first().Server.first().Request.exists()) {
			if (service.equals("ASR")) {
				return mcmlDoc.MCML.first().Server.first().Request.first().Input.first().AttachedBinary.first().DataID
				.getValue();
			} else if (service.equals("MT")) {
				return mcmlDoc.MCML.first().Server.first().Request.first().Input.first().Data.first().Text.first().ModelType
				.first().Language.first().ID.getValue();
			} else if (service.equals("TTS")) {
				return mcmlDoc.MCML.first().Server.first().Request.first().TargetOutput.first().LanguageType2.first().ID
				.getValue();
			}
		} else if (mcmlDoc.MCML.exists()
				&& mcmlDoc.MCML.first().Server.exists()
				&& mcmlDoc.MCML.first().Server.first().Response.exists()) {
			if (service.equals("ASR")) {
				return getModelLanguage(mcmlDoc.MCML.first().Server.first().Response.first().Output.first().Data.first());
			} else if (service.equals("TTS")) {
				return mcmlDoc.MCML.first().Server.first().Response.first().Output.first().AttachedBinary.first().DataID
				.getValue();
			}
		}
		throw new MCMLException("Can not get Source Language.", MCMLException.SYSTEM, MCMLException.COMMON,
				MCMLException.ABNORMAL_XML_DATA_FORMAT);
	}

	/**
	 * get Target Language
	 *
	 * @param mcmlDoc
	 * @return
	 * @throws Exception
	 */
	public static String getTargetLanguage(MCMLDoc mcmlDoc) throws Exception {
		String service = getService(mcmlDoc);
		if (mcmlDoc.MCML.exists() && mcmlDoc.MCML.first().Server.exists()
				&& mcmlDoc.MCML.first().Server.first().Request.exists()) {
			if (service.equals("ASR")) {
				return mcmlDoc.MCML.first().Server.first().Request.first().Input.first().AttachedBinary.first().DataID
				.getValue();
			} else if (service.equals("MT")
					|| service.equals("TTS")) {
				return mcmlDoc.MCML.first().Server.first().Request.first().TargetOutput.first().LanguageType2.first().ID
				.getValue();
			}
		} else if (mcmlDoc.MCML.exists() && mcmlDoc.MCML.first().Server.exists()
				&& mcmlDoc.MCML.first().Server.first().Response.exists()) {
			if (service.equals("ASR")) {
				return getModelLanguage(mcmlDoc.MCML.first().Server.first().Response.first().Output.first().Data.first());
			} else if (service.equals("MT")) {
				return getModelLanguage(mcmlDoc.MCML.first().Server.first().Response.first().Output.first().Data.first());
			} else if (service.equals("DM")) {
				// nothing to do
			} else if (service.equals("TTS")) {
				return mcmlDoc.MCML.first().Server.first().Response.first().Output.first().AttachedBinary.first().DataID
				.getValue();
			}
		}
			throw new MCMLException("Can not get Target language.", MCMLException.SYSTEM, MCMLException.COMMON,
					MCMLException.ABNORMAL_XML_DATA_FORMAT);
}

	/**
	 * get Sentence Surface
	 *
	 * @param mcmlDoc
	 * @return
	 * @throws Exception
	 */
	public static String getRequestSentenceSurface(MCMLDoc mcmlDoc) throws Exception {
		String service = getService(mcmlDoc);
		if (mcmlDoc.MCML.exists() && mcmlDoc.MCML.first().Server.exists()
				&& mcmlDoc.MCML.first().Server.first().Request.exists()) {
			if (service.equals("MT") || service.equals("TTS")) {
				return mcmlDoc.MCML.first().Server.first().Request.first().Input.first().Data.first().Text.first().SentenceSequence
				.first().Sentence.first().Surface.first().getValue();
			}
//		} else if (mcmlDoc.MCML.exists() && mcmlDoc.MCML.first().Server.exists()
//				&& mcmlDoc.MCML.first().Server.first().Response.exists()) {
//			if (service.equals("ASR")) {
//				return mcmlDoc.MCML.first().Server.first().Response.first().Output.first().Data.first().Text.first().SentenceSequence
//				.first().Sentence.first().Surface.first().getValue();
//			}
		}
		throw new MCMLException("Can not get Surface of under Sentence.", MCMLException.SYSTEM, MCMLException.COMMON,
				MCMLException.ABNORMAL_XML_DATA_FORMAT);
	}

	/**
	 * get Sentence Surface
	 *
	 * @param mcmlDoc
	 * @return
	 * @throws Exception
	 */
	public static String getResultSentenceSurface(MCMLDoc mcmlDoc) throws Exception {
		String service = getService(mcmlDoc);
		if (mcmlDoc.MCML.exists() && mcmlDoc.MCML.first().Server.exists()
				&& mcmlDoc.MCML.first().Server.first().Response.exists()) {
			if (service.equals("MT")) {
				return mcmlDoc.MCML.first().Server.first().Response.first().Output.first().Data.first().Text.first().SentenceSequence
				.first().Sentence.first().Surface.first().getValue();
			}
		}
		if (mcmlDoc.MCML.exists() && mcmlDoc.MCML.first().Server.exists()
				&& mcmlDoc.MCML.first().Server.first().Response.exists()) {
			if (service.equals("ASR")) {
				return mcmlDoc.MCML.first().Server.first().Response.first().Output.first().Data.first().Text.first().SentenceSequence
				.first().Sentence.first().Surface.first().getValue();
			}
		}
		throw new MCMLException("Can not get Surface of under Sentence.", MCMLException.SYSTEM, MCMLException.COMMON,
				MCMLException.ABNORMAL_XML_DATA_FORMAT);
	}

	/**
	 * @param mcmlDoc
	 * @return
	 * @throws Exception
	 */
	public static String getResponseURI(MCMLDoc mcmlDoc) throws Exception {
		return mcmlDoc.MCML.first().Server.first().Response.first().Routing.first().From.first().URI.getValue();
	}

	/**
	 * @param mcmlDoc
	 * @return
	 * @throws Exception
	 */
	public static String getResponseToURI(MCMLDoc mcmlDoc) throws Exception {
		return mcmlDoc.MCML.first().Server.first().Response.first().Routing.first().To.first().URI.getValue();
	}

	/**
	 * @param mcmlDoc
	 * @return
	 * @throws Exception
	 */
	public static String getRequestFromURI(MCMLDoc mcmlDoc) throws Exception {
		return mcmlDoc.MCML.first().Server.first().Request.first().Routing.first().From.first().URI.getValue();
	}

	/**
	 * @param mcmlDoc
	 * @return
	 * @throws Exception
	 */
	public static String getRequestToURI(MCMLDoc mcmlDoc) throws Exception {
		return mcmlDoc.MCML.first().Server.first().Request.first().Routing.first().To.first().URI.getValue();
	}

	/**
	 *
	 * @param service
	 * @return
	 */
	public static boolean serviceIsUserRegistration(String service) {
		return service.equals(MCMLStatics.SERVICE_USER_REGISTRATION);
	}

	/**
	 *
	 * @param service
	 * @return
	 */
	public static boolean serviceIsUserUnRegistration(String service) {
		return service.equals(MCMLStatics.SERVICE_USER_UNREGISTRATION);
	}

	/**
 * Determines if it is a service code used to get a server URL used by assembling users
	 * @param service
	 * @return
	 */
	public static boolean serviceIsGetUsersServerUrl(String service) {
		return MCMLStatics.SERVICE_GET_USERS_SERVER_URL.equals(service);
	}

	/**
	 * @param service
	 * @return
	 */
	public static boolean serviceIsPartyRegistration(String service) {
		return service.equals(MCMLStatics.SERVICE_PARTY_REGISTRATION);
	}

	/**
	 * @param service
	 * @return
	 */
	public static boolean serviceIsPartyUnRegistration(String service) {
		return service.equals(MCMLStatics.SERVICE_PARTY_UNREGISTRATION);
	}

	/**
	 * @param service
	 * @return
	 */
	public static boolean serviceIsUserSearch(String service) {
		return service.equals(MCMLStatics.SERVICE_USER_SEARCH);
	}

	/**
	 * @param service
	 * @return
	 */
	public static boolean serviceIsASR(String service) {
		return service.equals(MCMLStatics.SERVICE_ASR);
	}

	/**
	 * @param service
	 * @return
	 */
	public static boolean serviceIsMT(String service) {
		return service.equals(MCMLStatics.SERVICE_MT);
	}

	/**
	 * @param service
	 * @return
	 */
	public static boolean serviceIsTTS(String service) {
		return service.equals(MCMLStatics.SERVICE_TTS);
	}

	/**
	 * @param service
	 * @return
	 */
	public static boolean serviceIsDialogConnect(String service) {
		return service.equals(MCMLStatics.SERVICE_DIALOG_CONNECT);
	}

	/**
	 * @param service
	 * @return
	 */
	public static boolean serviceIsDialogDisconnect(String service) {
		return service.equals(MCMLStatics.SERVICE_DIALOG_DISCONNECT);
	}

	/**
	 * @param service
	 * @return
	 */
	public static boolean serviceIsDialog(String service) {
		return service.equals(MCMLStatics.SERVICE_DIALOG);
	}

	/**
	 * @param service
	 * @return
	 */
	public static boolean serviceIsPolling(String service) {
		return service.equals(MCMLStatics.SERVICE_POLLING);
	}

	/**
	 * @param service
	 * @return
	 */
	public static boolean serviceIsInvite(String service) {
		return service.equals(MCMLStatics.SERVICE_INVITE);
	}

	/**
 * Returns information on whether service code is individual assembling request.
	 * @param service
	 * @return
	 */
	public static boolean serviceIsIndividualInvite(String service) {
		return MCMLStatics.SERVICE_INDIVIDUAL_INVITE.equals(service);
	}

	/**
	 * @param service
	 * @return
	 */
	public static boolean serviceIsBye(String service) {
		return service.equals(MCMLStatics.SERVICE_BYE);
	}

	/**
	 * @param service
	 * @return
	 */
	public static boolean serviceIsAccept(String service) {
		return service.equals(MCMLStatics.SERVICE_ACCEPT);
	}

	/**
	 * @param service
	 * @return
	 */
	public static boolean serviceIsReject(String service) {
		return service.equals(MCMLStatics.SERVICE_REJECT);
	}

	/**
	 * @param service
	 * @return
	 */
	public static boolean serviceIsGroupInfomation(String service) {
		return service.equals(MCMLStatics.SERVICE_GROUPINFORMATION);
	}

	/**
	 * @param service
	 * @return
	 */
	public static boolean serviceIsAlive(String service) {
		return service.equals(MCMLStatics.SERVICE_ALIVE);
	}

	/**
	 * @param service
	 * @return
	 */
	public static boolean serviceIsWorkCheck(String service) {
		return service.equals(MCMLStatics.SERVICE_WORKCHECK);
	}

	/**
	 * @param service
	 * @return
	 */
	public static boolean serviceIsLeaveGroup(String service) {
		return service.equals(MCMLStatics.SERVICE_LEAVEGROUP);
	}
	/**
 * Returns information on whether service code is server switch automatic recovery.
	 * @param service
	 * @return
	 */
	public static boolean serviceIsChangeServer(String service) {
		return service.equals(MCMLStatics.SERVICE_SERVER);
	}
	/**
 * Returns information on whether server map creation or update request after switching of service code
	 * @param service
	 * @return
	 */
	public static boolean serviceIsCreateOrUpdateMap(String service) {
		return service.equals(MCMLStatics.SERVICE_CREATE_OR_UPDATE_MAP);
	}

	/**
 * Returns information on whether service code is map information update request.
	 * @param service
	 * @return
	 */
	public static boolean serviceIsMapUpdate(String service) {
		return service.equals(MCMLStatics.MAP_UPDATE);
	}

	/**
	 *
	 * @param service
	 * @return
	 */
	public static boolean serviceIsGroupBack(String service) {
		return service.equals(MCMLStatics.SERVICE_GROUP_BACK);
	}

	/**
	 *
	 * @param service
	 * @return
	 */
	public static boolean serviceIsASRModification(String service) {
		return service.equals(MCMLStatics.SERVICE_ASR_MODIFICATION);
	}

	/**
	 *
	 * @param service
	 * @return
	 */
	public static boolean serviceIsMTModification(String service) {
		return service.equals(MCMLStatics.SERVICE_MT_MODIFICATION);
	}

	/**
	 * @param mcmlDoc
	 * @return
	 * @throws Exception
	 */
	public static boolean hasRequest(MCMLDoc mcmlDoc) throws Exception {
		if (mcmlDoc.MCML.exists()) {
			MCMLType mcmlType = mcmlDoc.MCML.first();
			return hasRequest(mcmlType);
		}
		return false;
	}

	/**
	 * @param mcmlType
	 * @return
	 * @throws Exception
	 */
	public static boolean hasRequest(MCMLType mcmlType) throws Exception {
		return (mcmlType.Server.exists() && mcmlType.Server.first().Request.exists());
	}

	/**
	 * @param mcmlDoc
	 * @return
	 * @throws Exception
	 */
	public static boolean hasResponse(MCMLDoc mcmlDoc) throws Exception {
		if (mcmlDoc.MCML.exists()) {
			MCMLType mcmlType = mcmlDoc.MCML.first();
			return hasResponse(mcmlType);
		}
		return false;
	}

	/**
	 * @param mcmlType
	 * @return
	 * @throws Exception
	 */
	public static boolean hasResponse(MCMLType mcmlType) throws Exception {
		return (mcmlType.Server.exists() && mcmlType.Server.first().Response.exists());
	}

	/**
	 * @param mcmlDoc
	 * @return
	 * @throws Exception
	 */
	public static boolean hasError(MCMLDoc mcmlDoc) throws Exception {
		if (mcmlDoc.MCML.exists()) {
			MCMLType mcmlType = mcmlDoc.MCML.first();
			return hasError(mcmlType);
		}
		return false;
	}

	/**
	 * @param mcmlType
	 * @return
	 * @throws Exception
	 */
	public static boolean hasError(MCMLType mcmlType) throws Exception {
		return (hasResponse(mcmlType) && mcmlType.Server.first().Response.first().Error.exists());
	}

	/**
	 * @param mcmlDoc
	 * @return
	 * @throws Exception
	 */
	public static InputUserProfileType getInputUserProfileType(MCMLDoc mcmlDoc) throws Exception {
		if (mcmlDoc.MCML.exists()) {
			MCMLType mcmlType = mcmlDoc.MCML.first();
			return getInputUserProfileType(mcmlType);
		}
		return null;

	}

	/**
	 * @param mcmlType
	 * @return
	 * @throws Exception
	 */
	public static InputUserProfileType getInputUserProfileType(MCMLType mcmlType) throws Exception {
		return (hasRequest(mcmlType) && mcmlType.Server.first().Request.first().InputUserProfile.exists())
				? mcmlType.Server.first().Request.first().InputUserProfile.first() : null;
	}

	/**
	 * @param inputUserProfileType
	 * @return
	 * @throws Exception
	 */
	public static LanguageType getLanguageType(InputUserProfileType inputUserProfileType) throws Exception {
		// process LanguageTyp
		return (inputUserProfileType.InputModality.exists()
				&& inputUserProfileType.InputModality.first().Speaking.exists() && inputUserProfileType.InputModality
					.first().Speaking.first().Language.exists()) ? inputUserProfileType.InputModality.first().Speaking
				.first().Language.first() : null;
	}

	/**
	 * @param mcmlDoc
	 * @return
	 * @throws Exception
	 */
	public static String getInputLanguageID(MCMLDoc mcmlDoc) throws Exception {
		if (mcmlDoc.MCML.exists()) {
			MCMLType mcmlType = mcmlDoc.MCML.first();
			return getInputLanguageID(mcmlType);
		}
		return "";
	}

	/**
	 * @param mcmlType
	 * @return
	 * @throws Exception
	 */
	public static String getInputLanguageID(MCMLType mcmlType) throws Exception {
		String lang = "";
		if (hasRequest(mcmlType)) {
			RequestType request = mcmlType.Server.first().Request.first();

			if (request.Input.first().AttachedBinary.exists()) { // ASR
				lang = XMLTypeTools.getDataID(request);
			} else { // MT or TTS
				DataType dataType = XMLTypeTools.getDataTypeFromRequest(request);
				lang = XMLTypeTools.getModelLanguage(dataType);
			}
		}
		if (lang.isEmpty()) { // get UserProfile
			InputUserProfileType inputUserProfileType = getInputUserProfileType(mcmlType);
			lang = getInputLanguageID(inputUserProfileType);
		}
		return lang;
	}

	/**
	 * @param mcmlDoc
	 * @return
	 * @throws Exception
	 */
	public static String getInputLanguageFluency(MCMLDoc mcmlDoc) throws Exception {
		if (mcmlDoc.MCML.exists()) {
			MCMLType mcmlType = mcmlDoc.MCML.first();
			return getInputLanguageFluency(mcmlType);
		}
		return null;
	}

	/**
	 * @param mcmlType
	 * @return
	 * @throws Exception
	 */
	public static String getInputLanguageFluency(MCMLType mcmlType) throws Exception {
		InputUserProfileType inputUserProfileType = getInputUserProfileType(mcmlType);
		return getInputLanguageFluency(inputUserProfileType);
	}

	/**
	 * @param inputUserProfileType
	 * @return
	 * @throws Exception
	 */
	public static String getInputLanguageID(InputUserProfileType inputUserProfileType) throws Exception {
		return (inputUserProfileType != null && getLanguageType(inputUserProfileType) != null && getLanguageType(inputUserProfileType).ID
				.exists()) ? getLanguageType(inputUserProfileType).ID.getValue() : "";
	}

	/**
	 * @param inputUserProfileType
	 * @return
	 * @throws Exception
	 */
	public static String getInputLanguageFluency(InputUserProfileType inputUserProfileType) throws Exception {
		return (inputUserProfileType != null && getLanguageType(inputUserProfileType) != null && getLanguageType(inputUserProfileType).Fluency
				.exists()) ? String.valueOf(getLanguageType(inputUserProfileType).Fluency.getValue()) : "";
	}

	/**
	 * @param mcmlDoc
	 * @return
	 * @throws Exception
	 */
	public static AudioType getAudioType(MCMLDoc mcmlDoc) throws Exception {
		if (mcmlDoc.MCML.exists()) {
			MCMLType mcmlType = mcmlDoc.MCML.first();
			return getAudioType(mcmlType);
		}
		return null;
	}

	/**
	 * @param mcmlType
	 * @return
	 * @throws Exception
	 */
	public static AudioType getAudioType(MCMLType mcmlType) throws Exception {
		return (hasRequest(mcmlType) && mcmlType.Server.first().Request.first().Input.exists()
				&& mcmlType.Server.first().Request.first().Input.first().Data.exists() && mcmlType.Server.first().Request
				.first().Input.first().Data.first().Audio.exists()) ? mcmlType.Server.first().Request.first().Input
				.first().Data.first().Audio.first() : null;
	}

	/**
	 * @param requestType
	 * @return
	 * @throws Exception
	 */
	public static AudioType getAudioType(RequestType requestType) throws Exception {
		return (requestType.Input.exists() && requestType.Input.first().Data.exists() && requestType.Input.first().Data
				.first().Audio.exists()) ? requestType.Input.first().Data.first().Audio.first() : null;
	}

	/**
	 * @param responseType
	 * @return
	 * @throws Exception
	 */
	public static AudioType getAudioType(ResponseType responseType) throws Exception {
		return (responseType.Output.exists() && responseType.Output.first().Data.exists() && responseType.Output
				.first().Data.first().Audio.exists()) ? responseType.Output.first().Data.first().Audio.first() : null;
	}

	/**
	 * @param requestType
	 * @return
	 * @throws Exception
	 */
	public static String getDataID(RequestType requestType) throws Exception {
		return (requestType.Input.exists() && requestType.Input.first().AttachedBinary.exists() && requestType.Input
				.first().AttachedBinary.first().DataID.exists())
				? requestType.Input.first().AttachedBinary.first().DataID.getValue() : "";
	}

	/**
	 * @param responceType
	 * @return
	 * @throws Exception
	 */
	public static String getDataID(ResponseType responceType) throws Exception {
		return (responceType.Output.exists() && responceType.Output.first().AttachedBinary.exists() && responceType.Output
				.first().AttachedBinary.first().DataID.exists())
				? responceType.Output.first().AttachedBinary.first().DataID.getValue() : "";
	}

	/**
	 * @param mcmlType
	 * @return
	 * @throws Exception
	 */
	public static String getNofN_best(MCMLType mcmlType) throws Exception {
		TargetOutputType targetOutputType = getTargetOutputType(mcmlType);
		return (targetOutputType != null && targetOutputType.HypothesisFormat.exists())
				? targetOutputType.HypothesisFormat.first().NofN_best.getValue() : "";
	}

	/**
	 * @param mcmlDoc
	 * @return
	 * @throws Exception
	 */
	public static String getLanguageTypeID(MCMLDoc mcmlDoc) throws Exception {
		if (mcmlDoc.MCML.exists()) {
			MCMLType mcmlType = mcmlDoc.MCML.first();
			return getLanguageTypeID(mcmlType);
		}
		return "";
	}

	/**
	 * @param mcmlType
	 * @return
	 * @throws Exception
	 */
	public static String getLanguageTypeID(MCMLType mcmlType) throws Exception {
		TargetOutputType targetOutputType = getTargetOutputType(mcmlType);
		return (targetOutputType != null && targetOutputType.LanguageType2.exists() && targetOutputType.LanguageType2
				.first().ID.exists()) ? targetOutputType.LanguageType2.first().ID.getValue() : "";
	}

	/**
	 * @param targetOutputType
	 * @return
	 * @throws Exception
	 */
	public static String getTargetOutputLanguageID(TargetOutputType targetOutputType) throws Exception {
		return (targetOutputType != null && targetOutputType.LanguageType2.exists() && targetOutputType.LanguageType2
				.first().ID.exists()) ? targetOutputType.LanguageType2.first().ID.getValue() : "";
	}

	/**
	 * @param mcmlDoc
	 * @return
	 * @throws Exception
	 */
	public static OptionType getOptionType(MCMLDoc mcmlDoc) throws Exception {
		if (mcmlDoc.MCML.exists()) {
			MCMLType mcmlType = mcmlDoc.MCML.first();
			if  (getTargetOutputType(mcmlType).Option.exists()) {
				return getTargetOutputType(mcmlType).Option.first();
			}
		}
		return null;
	}

	/**
	 * @param mcmlDoc
	 * @return
	 * @throws Exception
	 */
	public static int getOptionCont(MCMLDoc mcmlDoc) throws Exception {
		if (mcmlDoc.MCML.exists()) {
			MCMLType mcmlType = mcmlDoc.MCML.first();
			if  (getTargetOutputType(mcmlType).Option.exists()) {
				return getTargetOutputType(mcmlType).Option.count();
			}
		}
		return 0;
	}

	/**
	 * @param mcmlDoc
	 * @return
	 * @throws Exception
	 */
	public static TargetOutputType getTargetOutputType(MCMLDoc mcmlDoc) throws Exception {
		if (mcmlDoc.MCML.exists()) {
			MCMLType mcmlType = mcmlDoc.MCML.first();
			return getTargetOutputType(mcmlType);
		}
		return null;
	}

	/**
	 * @param mcmlType
	 * @return
	 * @throws Exception
	 */
	public static TargetOutputType getTargetOutputType(MCMLType mcmlType) throws Exception {
		return (hasRequest(mcmlType) && mcmlType.Server.first().Request.first().TargetOutput.exists())
				? mcmlType.Server.first().Request.first().TargetOutput.first() : null;
	}

	/**
	 * @param mcmlDoc
	 * @return
	 * @throws Exception
	 */
	public static TextType getTextType(MCMLDoc mcmlDoc) throws Exception {
		if (mcmlDoc.MCML.exists()) {
			MCMLType mcmlType = mcmlDoc.MCML.first();
			return getTextType(mcmlType);
		}
		return null;
	}

	/**
	 * @param mcmlType
	 * @return
	 * @throws Exception
	 */
	public static TextType getTextType(MCMLType mcmlType) throws Exception {
		return (hasRequest(mcmlType) && mcmlType.Server.first().Request.first().Input.exists()
				&& mcmlType.Server.first().Request.first().Input.first().Data.exists() && mcmlType.Server.first().Request
				.first().Input.first().Data.first().Text.exists()) ? mcmlType.Server.first().Request.first().Input
				.first().Data.first().Text.first() : null;
	}

	/**
	 * @param responseType
	 * @return
	 * @throws Exception
	 */
	public static TextType getTextType(ResponseType responseType) throws Exception {
		return (responseType.Output.exists() && responseType.Output.first().Data.exists() && responseType.Output
				.first().Data.first().Text.exists()) ? responseType.Output.first().Data.first().Text.first() : null;
	}

	/**
	 * @param mcmlDoc
	 * @return
	 * @throws Exception
	 */
	public static boolean isRelayRequet(MCMLDoc mcmlDoc) throws Exception {
		if (mcmlDoc.MCML.exists()) {
			MCMLType mcmlType = mcmlDoc.MCML.first();
			return isRelayRequet(mcmlType);
		}
		return false;
	}

	/**
	 * @param mcmlType
	 * @return
	 * @throws Exception
	 */
	public static boolean isRelayRequet(MCMLType mcmlType) throws Exception {
		if (!mcmlType.Server.exists() || !mcmlType.Server.first().Request.exists()) {
			return false;
		}

		int requestCnt = mcmlType.Server.first().Request.count();
		if (1 < requestCnt) {
			return true;
		}
		return false;
	}

	/**
	 * @param code
	 * @param explanation
	 * @param requestDoc
	 * @param outputMCMLDoc
	 * @throws Exception
	 */
	public static void generateErrorResponse(String code, String explanation, MCMLDoc requestDoc, MCMLDoc outputMCMLDoc)
					throws Exception {
		// get parameters
		String service =
				(requestDoc != null && requestDoc.MCML.exists()) ? XMLTypeTools.getService(requestDoc.MCML.first())
						: "";
		int processOrder =
				(requestDoc != null && requestDoc.MCML.exists()) ? XMLTypeTools
						.getProcessOrder(requestDoc.MCML.first()) : 0;

		// if(outputMCMLDoc == null) {
		// outputMCMLDoc = MCMLDoc.createDocument();
		// }
		if (!outputMCMLDoc.MCML.exists()) {
			outputMCMLDoc.MCML.append();
		}

		MCMLType mcmlType = outputMCMLDoc.MCML.first();

		// generate MCMLType
		generateEmptyData(mcmlType);

		ResponseType responseType = mcmlType.Server.append().Response.append();
		responseType.Service.setValue(service);
		responseType.ProcessOrder.setValue(processOrder);
		ErrorType errorType = responseType.Error.append();
		errorType.Code.setValue(code);
		errorType.Message.setValue(explanation);
		errorType.Service.setValue(service);

		// return mcmlType ;
		return;
	}

	/**
	 * @param mcmlType
	 * @throws Exception
	 */
	public static void generateEmptyData(MCMLType mcmlType) throws Exception {
		mcmlType.Version.setValue(MCMLStatics.VERSION);
		// return mcmlType ;
		return;
	}

	/**
	 * @param errorCode
	 * @param message
	 * @param service
	 * @param errorType
	 * @throws Exception
	 */
	public static void generateErrorType(String errorCode, String message, String service, ErrorType errorType)
			throws Exception {
		if ((errorCode == null || errorCode.isEmpty()) || (service == null || service.isEmpty())) {
			return;
		}
		errorType.Code.setValue(errorCode);

		if (message != null && !message.isEmpty()) {
			errorType.Message.setValue(message);
		}
		errorType.Service.setValue(service);

		return;
	}

	/**
	 * @param mcmlDoc
	 * @return
	 * @throws Exception
	 */
	public static String getErrorCode(MCMLDoc mcmlDoc) throws Exception {
		if (mcmlDoc.MCML.exists()) {
			MCMLType mcmlType = mcmlDoc.MCML.first();
			return getErrorCode(mcmlType);
		}
		return "";
	}

	/**
	 * @param mcmlType
	 * @return
	 * @throws Exception
	 */
	public static String getErrorCode(MCMLType mcmlType) throws Exception {
		return (hasError(mcmlType) && mcmlType.Server.first().Response.first().Error.first().Code.exists())
				? mcmlType.Server.first().Response.first().Error.first().Code.getValue() : "";
	}

	/**
	 * @param mcmlDoc
	 * @return
	 * @throws Exception
	 */
	public static String getErrorMessage(MCMLDoc mcmlDoc) throws Exception {
		if (mcmlDoc.MCML.exists()) {
			MCMLType mcmlType = mcmlDoc.MCML.first();
			return getErrorMessage(mcmlType);
		}
		return "";
	}

	/**
	 * @param mcmlType
	 * @return
	 * @throws Exception
	 */
	public static String getErrorMessage(MCMLType mcmlType) throws Exception {
		return (hasError(mcmlType) && mcmlType.Server.first().Response.first().Error.first().Message.exists())
				? mcmlType.Server.first().Response.first().Error.first().Message.getValue() : "";
	}

	/**
	 * @param mcmlType
	 * @return
	 * @throws Exception
	 */
	public static String getErrorService(MCMLType mcmlType) throws Exception {
		return (hasError(mcmlType) && mcmlType.Server.first().Response.first().Error.first().Service.exists())
				? mcmlType.Server.first().Response.first().Error.first().Service.getValue() : "";
	}

	/**
	 * @param requestType
	 * @return
	 * @throws Exception
	 */
	public static TextType getTextTypeFromRequest(RequestType requestType) throws Exception {
		return (requestType.Input.exists() && requestType.Input.first().Data.exists() && requestType.Input.first().Data
				.first().Text.exists()) ? requestType.Input.first().Data.first().Text.first() : null;
	}

	/**
	 * @param responseType
	 * @return
	 * @throws Exception
	 */
	public static TextType getTextTypeFromResponse(ResponseType responseType) throws Exception {
		return (responseType.Output.exists() && responseType.Output.first().Data.exists() && responseType.Output
				.first().Data.first().Text.exists()) ? responseType.Output.first().Data.first().Text.first() : null;
	}

	/**
	 * @param mcmlDoc
	 * @return
	 * @throws Exception
	 */
	public static DataType getDataTypeFromMCML(MCMLDoc mcmlDoc) throws Exception {
		if (mcmlDoc.MCML.exists()) {
			MCMLType mcmlType = mcmlDoc.MCML.first();
			return getDataTypeFromMCML(mcmlType);
		}
		return null;
	}

	/**
	 * @param mcmlType
	 * @return
	 * @throws Exception
	 */
	public static DataType getDataTypeFromMCML(MCMLType mcmlType) throws Exception {
		DataType data = null;
		if (hasRequest(mcmlType)) {
			// get data from request
			data = getDataTypeFromRequest(mcmlType.Server.first().Request.first());
		} else if (hasResponse(mcmlType)) {
			// get data from response
			data = getDataTypeFromResponse(mcmlType.Server.first().Response.first());
		} else {
			return null;
		}

		return data;
	}

	/**
	 * @param requestType
	 * @return
	 * @throws Exception
	 */
	public static DataType getDataTypeFromRequest(RequestType requestType) throws Exception {
		DataType data = null;
		// get data from request
		if (requestType.Input.exists()) {
			if (requestType.Input.first().Data.exists()) {
				data = requestType.Input.first().Data.first();
			}
		}

		return data;
	}

	/**
	 * @param responseType
	 * @return
	 * @throws Exception
	 */
	public static DataType getDataTypeFromResponse(ResponseType responseType) throws Exception {
		DataType data = null;
		// get data from response
		if (responseType.Output.exists()) {
			if (responseType.Output.first().Data.exists()) {
				data = responseType.Output.first().Data.first();
			}
		}

		return data;
	}

	/**
	 * @param dataType
	 * @return
	 * @throws Exception
	 */
	public static SentenceSequenceType getSentenceSequenceTypeFromDataType(DataType dataType) throws Exception {
		// get SentenceSequence from dataType
		return (dataType != null && dataType.Text.exists() && dataType.Text.first().SentenceSequence.exists())
				? dataType.Text.first().SentenceSequence.first() : null;
	}

	/**
	 * @param dataType
	 * @return
	 * @throws Exception
	 */
	public static String getPersonalityID(DataType dataType) throws Exception {
		return (dataType != null && dataType.Text.exists() && dataType.Text.first().ModelType.exists()
				&& dataType.Text.first().ModelType.first().Personality.exists() && dataType.Text.first().ModelType
				.first().Personality.first().ID.exists())
				? dataType.Text.first().ModelType.first().Personality.first().ID.getValue() : "";
	}

	/**
	 * @param mcmlDoc
	 * @return
	 * @throws Exception
	 */
	public static String getModelLanguage(MCMLDoc mcmlDoc) throws Exception {
		return getModelLanguage(mcmlDoc.MCML.first().Server.first().Response.first().Output.first().Data.first());
	}

	/**
	 * @param dataType
	 * @return
	 * @throws Exception
	 */
	public static String getModelLanguage(DataType dataType) throws Exception {
		// get SentenceSequence from dataType
		return (dataType != null && dataType.Text.exists() && dataType.Text.first().ModelType.exists()
				&& dataType.Text.first().ModelType.first().Language.exists() && dataType.Text.first().ModelType.first().Language
				.first().ID.exists()) ? dataType.Text.first().ModelType.first().Language.first().ID.getValue() : "";
	}

	/**
	 * get Client IP address from MCMLDoc
	 *
	 * @param mcmlDoc
	 * @return
	 */
	public static String getIPAddressFromMCMLDoc(MCMLDoc mcmlDoc) {
		if(mcmlDoc.MCML.exists()
				&& mcmlDoc.MCML.first().User.exists()
				&& mcmlDoc.MCML.first().User.first().Transmitter.exists()
				&& mcmlDoc.MCML.first().User.first().Transmitter.first().Device.exists()
				&& mcmlDoc.MCML.first().User.first().Transmitter.first().Device.first().Location.exists()
				&& mcmlDoc.MCML.first().User.first().Transmitter.first().Device.first().Location.first().URI.exists()) {
			String userURI = mcmlDoc.MCML.first().User.first().Transmitter.first().Device.first().Location.first().URI.first().getValue();
			return getIPAddressFromUserURI(userURI);

		}
		return null;
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
		String regex =
				"^(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])$";

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

	/**
	 * @param mcmlDoc
	 * @return
	 * @throws java.lang.Exception
	 */
	public static String getGroupNoFromUserURI(MCMLDoc mcmlDoc) throws java.lang.Exception {
		String Uri =
				mcmlDoc.MCML.first().User.first().Receiver.first().Device.first().Location.first().URI.first()
						.getValue();
		String[] UriArray = Uri.split(URI_DELIMETER);
		if (UriArray.length == 3) {
			String GrpNo = UriArray[URI_GRP_NO_INDEX];
			return GrpNo;
		}
		throw new MCMLException("Can not get Group No.", MCMLException.SYSTEM, MCMLException.COMMON,
				MCMLException.ABNORMAL_XML_DATA_FORMAT);
	}

	/**
	 * To copy the element under <USER> without <USER> To copy <Transmitter> and <Receiver>s emove UserInfomation String(ClientIP and Recieved Time on Servlet).
	 *
	 * @param inputMCMLDoc
	 * @throws Exception
	 */
	public static void removeUserInfoStr(MCMLDoc inputMCMLDoc) throws Exception {
		if (inputMCMLDoc.MCML.first().User.exists()) {
			UserType userType = MCMLDoc.createDocument().MCML.append().User.append();
			if (inputMCMLDoc.MCML.first().User.first().Transmitter.exists()) {
				XMLTypeCopyTools.copyTransmitterType(userType.Transmitter.append(),
						inputMCMLDoc.MCML.first().User.first().Transmitter.first());
			}
			for (int i = 0; i < inputMCMLDoc.MCML.first().User.first().Receiver.count(); i++) {
				XMLTypeCopyTools.copyReceiverType(userType.Receiver.append(),
						inputMCMLDoc.MCML.first().User.first().Receiver.at(i));
			}
			inputMCMLDoc.MCML.first().User.remove();
			XMLTypeCopyTools.copyUserType(inputMCMLDoc.MCML.first().User.append(), userType);
		}
	}

	/**
	 * @param sourceRequest
	 * @param mcmlType
	 * @return RequestType
	 * @throws Exception
	 */
	public static RequestType generateRequestTypeFromBaseXML(RequestType sourceRequest, MCMLDoc mcmlDoc)
			throws Exception {
		// set service name
		RequestType outputRequest = new RequestType(null);

		// RequestType outputRequest = null;

		outputRequest.Service.setValue(sourceRequest.Service.getValue());
		// outputRequest.addProcessOrder(sourceRequest.getProcessOrder());
		outputRequest.ProcessOrder.setValue(sourceRequest.ProcessOrder.getValue());

		// set RoutingType
		if (sourceRequest.Routing.exists()) {
			XMLTypeCopyTools.copyRoutingType(outputRequest.Routing.append(), sourceRequest.Routing.first());
			// outputRequest.addRouting(sourceRequest.Routing.first());
		}

		// get DataType from base XML
		DataType dataType = getDataTypeFromMCML(mcmlDoc);

		// generate InputUserProfile from base XML data(DataType)
		InputUserProfileType inputUserProfileType = generateUsrProfileTypeFromDataType(dataType);

		// add InputUserProfile to dmRequest
		// outputRequest.addInputUserProfile(inputUserProfileType);
		XMLTypeCopyTools.copyInputUserProfileType(outputRequest.InputUserProfile.append(), inputUserProfileType);

		// add TargetOutput to ttsRequest
		// outputRequest.addTargetOutput(sourceRequest.getTargetOutput());
		XMLTypeCopyTools.copyTargetOutputType(outputRequest.TargetOutput.append(), sourceRequest.TargetOutput.first());

		// get InputType from DataType
		// InputType inputType = new InputType();
		// inputType.addData(dataType);
		XMLTypeCopyTools.copyDataType(outputRequest.Input.append().Data.append(), dataType);

		// get DataType from last time log
		// outputRequest.addInput(inputType);

		return outputRequest;
	}

	/**
	 * generate user type
	 *
	 * @param dataType
	 * @return InputUserProfileType
	 * @throws Exception
	 */
	private static InputUserProfileType generateUsrProfileTypeFromDataType(DataType dataType) throws Exception {
		// get ModelTypeType from DataType
		ModelTypeType modelType = dataType.Text.first().ModelType.first();
		// get personality from ModelTypeType
		PersonalityType personality = modelType.Personality.first();

		// add user ID
		InputUserProfileType inputUserProfileType = new InputUserProfileType(null);
		if (personality.ID.exists()) {
			inputUserProfileType.ID.setValue(personality.ID.getValue());
		}
		// add gender
		if (personality.Gender.exists()) {
			inputUserProfileType.Gender.setValue(personality.Gender.getValue());
		}
		// add age
		if (personality.Age.exists()) {
			inputUserProfileType.Age.setValue(personality.Age.getValue());
		}
		// add language Id to languageType
		LanguageType languageType = inputUserProfileType.InputModality.append().Speaking.append().Language.append();
		if (modelType.Language.first().ID.exists()) {
			languageType.ID.setValue(modelType.Language.first().ID.getValue());
		}

		// add fluency to language type
		if (modelType.Language.first().Fluency.exists()) {
			languageType.Fluency.setValue(modelType.Language.first().Fluency.getValue());
		}

		return inputUserProfileType;
	}

	/**
	 * Get "To" Uri List
	 *
	 * @param requestType
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<String> getToUriList(RequestType requestType) throws Exception {
		ArrayList<String> resultList = null;
		if (!requestType.Routing.exists()) {
			return resultList;
		}

		// create resultList
		resultList = new ArrayList<String>();

		// get To
		RoutingType routingType = requestType.Routing.first();
		int cnt = routingType.To.count();
		for (int i = 0; i < cnt; i++) {
			resultList.add(routingType.To.at(i).URI.getValue());
		}
		return resultList;
	}

	/**
	 * get User Transmitter URI
	 *
	 * @param mcmlType
	 * @return
	 * @throws Exception
	 */
	public static String getUserTransmitterURI(MCMLType mcmlType) throws Exception{
		if(!mcmlType.User.exists()
				|| !mcmlType.User.first().Transmitter.exists()
				|| !mcmlType.User.first().Transmitter.first().Device.exists()
				|| !mcmlType.User.first().Transmitter.first().Device.first().Location.exists()
				|| !mcmlType.User.first().Transmitter.first().Device.first().Location.first().URI.exists())
		{
			return "";
		}

		String uri = mcmlType.User.first().Transmitter.first().Device.first().Location.first().URI.first().getValue();

		return uri;
	}

	/**
	 * get Surface String List
	 *
	 * @param mcmlType
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<String> getSurfaceStringList(MCMLType mcmlType) throws Exception
	{
		ArrayList<String> resultList = new ArrayList<String>();

		if(hasRequest(mcmlType)){
			int requestCnt = mcmlType.Server.first().Request.count();
			for(int i = 0; i < requestCnt; i++){
				RequestType requestType = mcmlType.Server.first().Request.at(i);

				// next ResuestType(no InputType)
				if(!requestType.Input.exists()) continue;

				int inputCnt = requestType.Input.count();
				for(int j = 0; j < inputCnt; j++){
					InputType inputType = requestType.Input.at(j);

					// next InputType(no DataType)
					if(!inputType.Data.exists()) continue;

					int dataCnt = inputType.Data.count();
					for(int k = 0; k < dataCnt; k++){
						resultList.addAll(getSurfaceStringListInDataType(inputType.Data.at(k)));
					}
				}
			}
		}
		else if(hasResponse(mcmlType)){
			int responseCnt = mcmlType.Server.first().Response.count();
			for(int i = 0; i < responseCnt; i++){
				ResponseType responseType = mcmlType.Server.first().Response.at(i);

				// next ResponseType(no OutputType)
				if(!responseType.Output.exists()) continue;

				int outputCnt = responseType.Output.count();
				for(int j = 0; j < outputCnt; j++){
					OutputType outputType = responseType.Output.at(j);

					// next OutputType(no DataType)
					if(!outputType.Data.exists()) continue;

					int dataCnt = outputType.Data.count();
					for(int k = 0; k < dataCnt; k++){
						resultList.addAll(getSurfaceStringListInDataType(outputType.Data.at(k)));
					}
				}
			}
		}
		else {
			// no process
		}

		return resultList;
	}

	/**
	 * get Surface String List In DataType
	 *
	 * @param dataType
	 * @return
	 * @throws Exception
	 */
	private static ArrayList<String> getSurfaceStringListInDataType(DataType dataType)
	throws Exception
	{
		ArrayList<String> resultList = new ArrayList<String>();

		if(!dataType.Text.exists()){
			return resultList;
		}

		int textCnt = dataType.Text.count();
		for(int i = 0; i < textCnt; i++){
			TextType textType = dataType.Text.at(i);

			// next TextType(no SentenceSequenceType)
			if(!textType.SentenceSequence.exists()) continue;

			int sentenceSeqCnt = textType.SentenceSequence.count();
			for(int j = 0; j < sentenceSeqCnt; j++){
				SentenceSequenceType sentSeqType = textType.SentenceSequence.at(j);

				// next SentenceSequenceType(no SentenceType)
				if(!sentSeqType.Sentence.exists()) continue;

				int sentenceCnt = sentSeqType.Sentence.count();
				for(int k = 0; k < sentenceCnt; k++){
					SentenceType sentenceType = sentSeqType.Sentence.at(k);

					// next SentenceType(no SurfaceType)
					if(!sentenceType.Surface.exists()) continue;

					int surfaceCnt = sentenceType.Surface.count();
					for(int l = 0; l < surfaceCnt; l++){
						// add String
						resultList.add(sentenceType.Surface.at(l).getValue());
					}
				}
			}
		}
		return resultList;
	}

	/**
	 * @param originalMCMLDoc
	 * @param service
	 * @param uri
	 * @param dataMCMLDoc
	 * @param outputMCMLDoc
	 * @throws Exception
	 */
	public static void generateMCMLFromInfomations(MCMLDoc originalMCMLDoc, String service, String uri,
			MCMLDoc dataMCMLDoc, MCMLDoc outputMCMLDoc) throws Exception {
		// create output MCMLType
		MCMLType outputMCMLType = generateMCMLType(outputMCMLDoc);

		ResponseType outputResponseType = outputMCMLType.Server.append().Response.append();
		DataType outputDataType = outputResponseType.Output.append().Data.append();
		RoutingType outputRoutingType = outputResponseType.Routing.append();

		// get UserType form Response MCML
		XMLTypeCopyTools.copyUserType(outputMCMLType.User.append(), originalMCMLDoc.MCML.first().User.first());

		// create Response
		RequestType currentRequest = getRequestTypeAtService(originalMCMLDoc, service);
		// ResponseType responseType = new ResponseType();
		if (currentRequest.ProcessOrder.exists()) {
			outputResponseType.ProcessOrder.setValue(currentRequest.ProcessOrder.getValue());
		}
		outputResponseType.Service.setValue(service);

		// create RoutingType
		// RoutingType routingType = new RoutingType();

		// create FromType
		if (currentRequest.Routing.first().From.exists()) {
			// FromType fromType = new FromType();
			outputRoutingType.From.append().URI.setValue(currentRequest.Routing.first().From.first().URI.getValue());
			// set FromType to routingType
			// routingType.addFrom(fromType);
		}

		// create ToType
		// ToType toType = new ToType();
		// toType.addURI(uri);
		outputRoutingType.To.append().URI.setValue(uri);
		// routingType.addTo(toType);

		// set RoutingType to responseType
		// responseType.addRouting(routingType);

		// get DataType for Output
		// DataType dataType = getDataTypeFromMCML(dataMCMLDoc);
		XMLTypeCopyTools.copyDataType(outputDataType,
				dataMCMLDoc.MCML.first().Server.first().Response.first().Output.first().Data.first());

		// create OutputType
		// OutputType outputType = new OutputType();
		// outputType.addData(dataType);
		// responseType.addOutput(outputType);

		// create ServerType
		// ServerType serverType = new ServerType();
		// serverType.addResponse(responseType);
		// outputMCMLType.addServer(serverType);
	}

	/**
	 * @param mcmlDoc
	 * @param serviceName
	 * @return
	 * @throws Exception
	 */
	private static RequestType getRequestTypeAtService(MCMLDoc mcmlDoc, String serviceName) throws Exception {
		RequestType outputRequestType = null;
		if (!mcmlDoc.MCML.first().Server.exists() || !mcmlDoc.MCML.first().Server.first().Request.exists()) {
			return outputRequestType;
		}

		ServerType serverType = mcmlDoc.MCML.first().Server.first();
		int requestCnt = serverType.Request.count();
		for (int i = 0; i < requestCnt; i++) {
			if (serverType.Request.at(i).Service.getValue().equals(serviceName)) {
				outputRequestType = serverType.Request.at(i);
				break;
			}
		}

		return outputRequestType;
	}

	/**
	 * @param uri
	 * @param globalPositions
	 * @return
	 * @throws Exception
	 */
	public static DeviceType generateDeviceType(String uri, ArrayList<Float> globalPositions) throws Exception {
		MCMLDoc mcmlDoc = MCMLDoc.createDocument();

		DeviceType deviceType = mcmlDoc.MCML.append().User.append().Receiver.append().Device.append();
		LocationType locationType = deviceType.Location.append();

		// add URI to LocationType
		if (uri != null && !uri.isEmpty()) {
			locationType.URI.append().setValue(uri);
		}

		// add Global Position to LocationType
		if (globalPositions != null && !globalPositions.isEmpty() && (globalPositions.size() == 2)) {
			// set Global Position
			GlobalPositionType gType = locationType.GlobalPosition.append();
			// new GlobalPositionType();
			gType.Longitude.setValue(globalPositions.get(0));
			gType.Latitude.setValue(globalPositions.get(1));
		}

		// create DeviceType
		if (locationType != null) {
			XMLTypeCopyTools.copyLocationType(deviceType.Location.append(), locationType);
		}
		return deviceType;
	}

	/**
	 * @param id
	 * @param email
	 * @param age
	 * @param gender
	 * @return
	 * @throws Exception
	 */
	public static UserProfileType generateUserProfileType(String id, String email, int age, String gender)
			throws Exception {
		MCMLDoc mcmlDoc = MCMLDoc.createDocument();
		// create UserProfileType
		UserProfileType userProfileType = null;
		// set ID
		if (id != null && !id.isEmpty()) {
			userProfileType = mcmlDoc.MCML.append().User.append().Receiver.append().UserProfile.append();
			userProfileType.ID.setValue(id);
		}
		// set E-Mail Address
		if (email != null && !email.isEmpty()) {
			if (userProfileType == null) {
				userProfileType = mcmlDoc.MCML.append().User.append().Receiver.append().UserProfile.append();
			}
			userProfileType.Email.setValue(email);
		}
		// set age
		if (0 <= age) {
			if (userProfileType == null) {
				userProfileType = mcmlDoc.MCML.append().User.append().Receiver.append().UserProfile.append();
			}
			userProfileType.Age.setValue(age);
		}
		// set gender
		if (gender != null && !gender.isEmpty()) {
			if (userProfileType == null) {
				userProfileType = mcmlDoc.MCML.append().User.append().Receiver.append().UserProfile.append();
			}
			userProfileType.Gender.setValue(gender);
		}
		return userProfileType;
	}

	/**
	 * Get service from History
	 *
	 * @param mcmlDoc
	 * @return
	 * @throws Exception
	 */
	public static String getServiceOfHistory(MCMLDoc mcmlDoc) throws Exception {
		if (mcmlDoc.MCML.exists()) {
			MCMLType mcmlType = mcmlDoc.MCML.first();
			return getServiceOfHistory(mcmlType);
		}
		return "";
	}

	/**
	 * Get service from History
	 *
	 * @param mcmlType
	 * @return
	 * @throws Exception
	 */
	public static String getServiceOfHistory(MCMLType mcmlType) throws Exception {
		if (mcmlType.History.exists()) {
			return getServiceOfHistory(mcmlType.History.first());
		} else {
			return "";
		}
	}

	/**
	 * Get service from History
	 *
	 * @param historyType
	 * @return
	 * @throws Exception
	 */
	public static String getServiceOfHistory(HistoryType historyType) throws Exception {
		if (historyType.Response.exists()
				&& historyType.Response.first().Service.exists()) {
			return historyType.Response.first().Service.getValue();
		} else if (historyType.Request.exists()
				&& historyType.Request.first().Service.exists()) {
			return historyType.Request.first().Service.getValue();
		} else {
			return "";
		}
	}

	/**
	 * get Target Language from History
	 *
	 * @param mcmlDoc
	 * @return
	 * @throws Exception
	 */
	public static String getTargetLanguageOfHistory(MCMLDoc mcmlDoc) throws Exception {
		if (mcmlDoc.MCML.exists() && mcmlDoc.MCML.first().History.exists()) {
			return getTargetLanguageOfHistory(mcmlDoc.MCML.first().History.first());
		}
		throw new MCMLException("Can not get Target language.", MCMLException.SYSTEM, MCMLException.COMMON,
				MCMLException.ABNORMAL_XML_DATA_FORMAT);
	}

	/**
	 * get Target Language from History
	 *
	 * @param historyType
	 * @return
	 * @throws Exception
	 */
	public static String getTargetLanguageOfHistory(HistoryType historyType) throws Exception {
		String service = getServiceOfHistory(historyType);
		if (historyType.Request.exists()) {
			if (service.equals("MT") || service.equals("TTS")) {
				return historyType.Request.first().TargetOutput.first().LanguageType2.first().ID.getValue();
			}
		} else if (historyType.Response.exists()) {
			if (service.equals("ASR")) {
				return getModelLanguage(historyType.Response.first().Output.first().Data.first());
			} else if (service.equals("MT")) {
				return getModelLanguage(historyType.Response.first().Output.first().Data.first());
			} else if (service.equals("DM")) {
				// nothing to do
			} else if (service.equals("TTS")) {
				return historyType.Response.first().Output.first().AttachedBinary.first().DataID.getValue();
			}
		}
		throw new MCMLException("Can not get Target language.", MCMLException.SYSTEM, MCMLException.COMMON,
				MCMLException.ABNORMAL_XML_DATA_FORMAT);
	}

	/**
	 * Get Process order number from History
	 *
	 * @param mcmlDoc
	 * @return
	 * @throws Exception
	 */
	public static int getProcessOrderOfHistory(MCMLDoc mcmlDoc) throws Exception {
		if (mcmlDoc.MCML.exists()) {
			MCMLType mcmlType = mcmlDoc.MCML.first();
			return getProcessOrder(mcmlType);
		}
		return 0;
	}

	/**
	 * Get Process order number from History
	 *
	 * @param mcmlType
	 * @return
	 * @throws Exception
	 */
	public static int getProcessOrderOfHistory(MCMLType mcmlType) throws Exception {
		if (mcmlType.History.exists()) {
			return getProcessOrderOfHistory(mcmlType.History.first());
		} else {
			return 0;
		}
	}

	/**
	 * Get Process order number from History
	 *
	 * @param historyType
	 * @return
	 * @throws Exception
	 */
	public static int getProcessOrderOfHistory(HistoryType historyType) throws Exception {
		if (historyType.Request.exists()
				&& historyType.Request.first().ProcessOrder.exists()) {
			return historyType.Request.first().ProcessOrder.getValue();
		} else if (historyType.Response.exists()
				&&  historyType.Response.first().ProcessOrder.exists()) {
			return historyType.Response.first().ProcessOrder.getValue();
		} else {
			return 0;
		}
	}

	/**
	 * Get Source Language ID for MT
	 * <Input><Data><Text><ModelType><Language ID=>
	 *
	 * Add the contents of the log by Nozaki 2012/02/27
	 *
	 * @param mcmlType
	 * @return
	 * @throws Exception
	 */
	public static String getInputDataTextModelTypeLanguageType(MCMLType mcmlType) throws Exception {
		String resultString = null;
//		if (hasRequest(mcmlType)
//				&& mcmlType.getServer().getRequest().hasInput()
//				&& mcmlType.getServer().getRequest().getInput().hasData()
//				&& mcmlType.getServer().getRequest().getInput().getData().hasText()
//				&& mcmlType.getServer().getRequest().getInput().getData().getText().hasModelType()
//				&& mcmlType.getServer().getRequest().getInput().getData().getText().getModelType().hasLanguage()
//				&& mcmlType.getServer().getRequest().getInput().getData().getText().getModelType().getLanguage().hasID()) {
//			resultString = mcmlType.getServer().getRequest().getInput().getData().getText().getModelType().getLanguage().getID().getValue();
//		}
		if (hasRequest(mcmlType)
				&& mcmlType.Server.first().Request.exists()
				&& mcmlType.Server.first().Request.first().Input.exists()
				&& mcmlType.Server.first().Request.first().Input.first().Data.exists()
				&& mcmlType.Server.first().Request.first().Input.first().Data.first().Text.exists()
				&& mcmlType.Server.first().Request.first().Input.first().Data.first().Text.first().ModelType.exists()
				&& mcmlType.Server.first().Request.first().Input.first().Data.first().Text.first().ModelType.first().Language.exists()
				&& mcmlType.Server.first().Request.first().Input.first().Data.first().Text.first().ModelType.first().Language.first().ID.exists()) {
			resultString = mcmlType.Server.first().Request.first().Input.first().Data.first().Text.first().ModelType.first().Language.first().ID.getValue();
		}

		return resultString;
	}

	// Add the contents of the log by Nozaki 2012/02/27
	/**
	 * Get LanguageType List
	 *
	 * Add the contents of the log by Nozaki 2012/02/27
	 *
	 * @param mcmlType
	 * @return LanguageType List
	 * @throws Exception
	 */
	public static ArrayList<LanguageType> getLanguageList(MCMLType mcmlType)  throws Exception {
		ArrayList<LanguageType> resultList = new ArrayList<LanguageType>();

//		if (hasRequest(mcmlType)
//				&& mcmlType.getServer().getRequest().hasInputUserProfile()) {
//			RequestType requestType = mcmlType.getServer().getRequest();
//
//			int inputUserProfileTypeCount = requestType.getInputUserProfileCount();
//			for(int i = 0; i < inputUserProfileTypeCount; i++) {
//				InputUserProfileType inputUserProfileType = requestType.getInputUserProfileAt(i);
//				if (inputUserProfileType.hasInputModality()
//						&& inputUserProfileType.getInputModality().hasSpeaking()
//						&& inputUserProfileType.getInputModality().getSpeaking().hasLanguage()
//						&& inputUserProfileType.getInputModality().getSpeaking().getLanguage().hasID()
//						&& inputUserProfileType.getInputModality().getSpeaking().getLanguage().hasFluency()) {
//					resultList.add(inputUserProfileType.getInputModality().getSpeaking().getLanguage());
//				}
//			}
//		}
		if (hasRequest(mcmlType)
				&& mcmlType.Server.first().Request.first().InputUserProfile.exists()) {
			RequestType requestType = mcmlType.Server.first().Request.first();

			int inputUserProfileTypeCount = requestType.InputUserProfile.count();
			for(int i = 0; i < inputUserProfileTypeCount; i++) {
				InputUserProfileType inputUserProfileType = requestType.InputUserProfile.at(i);
				if (inputUserProfileType.InputModality.exists()
						&& inputUserProfileType.InputModality.first().Speaking.exists()
						&& inputUserProfileType.InputModality.first().Speaking.first().Language.exists()
						&& inputUserProfileType.InputModality.first().Speaking.first().Language.first().ID.exists()
						&& inputUserProfileType.InputModality.first().Speaking.first().Language.first().Fluency.exists()) {
//					resultList.add(inputUserProfileType.getInputModality().getSpeaking().getLanguage());
					XMLTypeCopyTools.copyLanguageType(resultList.get(i), inputUserProfileType.InputModality.first().Speaking.first().Language.first());
				}
			}
		}
		return resultList;
	}

	public static ModelTypeType getModelTypeType(MCMLDoc mcmlDoc) throws Exception {
		TextType textType = getTextType(mcmlDoc);
		return textType.ModelType.first();
	}

	public static LanguageType getLanguageType(ModelTypeType modelTypeType) {
		return modelTypeType.Language.first();
	}

	public static PersonalityType getPersonalityType(MCMLDoc mcmlDoc) throws Exception {
		return getModelTypeType(mcmlDoc).Personality.first();
	}

	public static SurfaceType2 getSurfaceType2(MCMLDoc mcmlDoc) throws Exception {
		TextType textType = getTextType(mcmlDoc);
		return textType.SentenceSequence.first().Sentence.first().Surface.first();
	}

	public static ServerType getServerType(MCMLDoc mcmlDoc) {
		return mcmlDoc.MCML.first().Server.first();
	}

	public static InputModalityType getInputModalityType(MCMLDoc mcmlDoc) {
		return getServerType(mcmlDoc).Request.first().InputUserProfile.first().InputModality.first();
	}

	public static LanguageType getLanguageType(InputModalityType inputModalityType) {
		return inputModalityType.Speaking.first().Language.first();
	}

	public static LanguageTypeType getLanguageType2(MCMLDoc mcmlDoc) throws Exception {
		return getTargetOutputType(mcmlDoc).LanguageType2.first();
	}
}
