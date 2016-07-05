//-------------------------------------------------------------------
//Ver.3.0
//2011/12/06
//-------------------------------------------------------------------
package jp.go.nict.mcml.xml;

public class MCMLStatics {
	// ------------------------------------------
	// public member constants
	// ------------------------------------------
	public static final String	VERSION							= "1.0";
	public static final String	SERVICE_ASR						= "ASR";
	public static final String	SERVICE_MT						= "MT";
	public static final String	SERVICE_TTS						= "TTS";
	public static final String	SERVICE_DIALOG_CONNECT			= "DM_CONNECT";
	public static final String	SERVICE_DIALOG_DISCONNECT		= "DM_DISCONNECT";
	public static final String	SERVICE_DIALOG					= "DM";
	public static final String	SERVICE_USER_REGISTRATION		= "UserRegistration";
	public static final String	SERVICE_USER_UNREGISTRATION		= "UserUnregistration";
	public static final String	SERVICE_PARTY_REGISTRATION		= "PartyRegistration";
	public static final String	SERVICE_PARTY_UNREGISTRATION	= "PartyUnregistration";
	public static final String	SERVICE_USER_SEARCH				= "UserSearch";
	public static final String	SERVICE_POLLING					= "Polling";
	public static final String	SERVICE_GROUPINFORMATION		= "GroupInformation";
	public static final String	SERVICE_INVITE					= "Invite";
/** Service code for individual assembling requests between RoutingServers */
	public static final String	SERVICE_INDIVIDUAL_INVITE		= "IndividualInvite";
/** Service code for acquiring URL of servers used by assembling users */
	public static final String	SERVICE_GET_USERS_SERVER_URL	= "GetUsersServerUrl";
	public static final String	SERVICE_ACCEPT					= "Accept";
	public static final String	SERVICE_REJECT					= "Reject";
	public static final String	SERVICE_BYE						= "Bye";
	public static final String	SERVICE_ALIVE					= "Alive";
	public static final String	SERVICE_WORKCHECK				= "WorkCheck";
	public static final String	SERVICE_LEAVEGROUP				= "LeaveGroup";
/** Service code for server switch automatic recovery */
	public static final String	SERVICE_SERVER					= "ServerChange";
/** Service code for server switch notification requests to RoutingServer */
	public static final String	SERVICE_CREATE_OR_UPDATE_MAP	= "CreateOrUpdateMap";
/** Service code for MAP update request to RoutingServer */
	public static final String	MAP_UPDATE						= "MapUpdate";
	public static final String	SERVICE_GROUP_BACK				= "GroupBack";
	public static final String	SERVICE_ASR_MODIFICATION		= "ASRModification";
	public static final String	SERVICE_MT_MODIFICATION			= "MTModification";

	public static final String	AUDIO_ADPCM						= "ADPCM";
	public static final String	AUDIO_RAW						= "raw PCM";
	public static final String	AUDIO_DSR						= "DSR";
	public static final String	AUDIO_MP3						= "MP3";
	public static final String	ENDIAN_LITTLE					= "little";
	public static final String	ENDIAN_BIG						= "big";
	public static final int		SAMPLING_FREQUENCY_8K			= 8000;
	public static final int		SAMPLING_FREQUENCY_16K			= 16000;
	public static final String	UNIQUE_USER_ID_SPLITTER			= "\t";
	// public static final String MODEL_CHANNEL_ID_IMAGE = "0";
	public static final int		MODEL_CHANNEL_ID_IMAGE			= 0;
	public static final String	MODEL_CHANNEL_ID_TEXT			= "1";
	public static final String	MODEL_CHANNEL_ID_AUDIO			= "1";
	public static final String	BINARY_CHANNEL_ID_AUDIO			= "1";
	public static final String	BINARY_DATA_TYPE_IMAGE			= "image";
	public static final String	SENTENCE_FUNCTION				= "text";
	public static final String	SIGNAL_VALUE_TYPE_INTEGER		= "integer";
	public static final String	SIGNAL_VALUE_TYPE_FLOAT			= "float";
	public static final String	SIGNAL_BIT_RATE					= "16";
	public static final String	SIGNAL_CHANNEL_QTY				= "1";
	public static final String	GENDER_MALE						= "Male";
	public static final String	GENDER_FEMALE					= "Female";
	public static final String	GENDER_UNKNOWN					= "Unknown";
	public static final String	WFSTDM							= "WFSTDM";

	// NBest Parser used Return Code.
	public static final String	RETURN_CODE						= "\n";

	// use for creating corpus log file
	public static final String	LANGUAGE_JAPANESE				= "Ja";
	public static final String	LANGUAGE_ENGLISH				= "En";
	public static final String	LANGUAGE_CHINESE				= "Zh";
	public static final String	LANGUAGE_KOREAN					= "Ko";

	public static final String	CHARSET_NAME					= "UTF-8";
	public static final String	CHARSET_UTF_8					= "UTF8";
	public static final String	CHARSET_EUC_JP					= "EUC-JP";
	public static final String	CHARSET_EUC_CN					= "EUC-CN";
	public static final String	CHARSET_EUC_KR					= "EUC-KR";

	public static final String	PROCESS_STATE_SUCCESS			= "Success";
	public static final String	PROCESS_STATE_FAIL				= "Fail=";
	public static final String	STRING_YES						= "yes";
	public static final String	STRING_NO						= "no";

	// Add the contents of the log by Nozaki 2012/02/27
	public static final int		NATIVE_LANGUAGE			= 5;
	public static final int		FOREIGN_LANGUAGE_1ST	= 4;
	public static final int		FOREIGN_LANGUAGE_2ND	= 3;
}
