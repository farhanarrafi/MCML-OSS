//-------------------------------------------------------------------
//Ver.3.0
//2011/12/06
//-------------------------------------------------------------------
package jp.go.nict.mcml.xml;

/**
 * MCML exception
 * @author
 *
 */
public class MCMLException extends Exception{

	/**  */
	private static final long serialVersionUID = 2135443728753487130L;

	//------------------------------------------
	// public member constant
	//------------------------------------------
    public static final String SYSTEM	= "S";
    public static final String ERROR	= "E";

	public enum Service{
		COMMON,
		ASR,
		MT,
		TTS,
	}

    public static final int	COMMON	= 0 ;
    public static final int	ASR		= 1 ;
    public static final int	MT		= 2 ;
    public static final int	TTS		= 3 ;

	public static final int	SYSTEM_TIME_OUT				=  50 ;
	public static final int	INTERNAL_ABNORMALITY		=  60 ;
	public static final int	CONNECTION_CLOSED			=  70 ;
	public static final int	ABNORMAL_DATA_FORMAT		=  80 ;
	public static final int	ABNORMAL_XML_DATA_FORMAT	=  90 ;
	public static final int	NON_CORESSPONDANCE_PARAM	= 100 ;
	public static final int	ENGINE_CLOSED				= 110 ;
	public static final int	TIME_OUT					= 200 ;
	public static final int	ENGINE_DOWN					= 500 ;
	public static final int	NOT_SUPPORT_LANGUAGE		= 510 ;
	public static final int	NOT_EXPECTED_INPUT			= 520 ;
	public static final int	INTERNAL_ERROR				= 530 ;
	public static final int	IRREPARABLE_ERROR			= 540 ;
    public static final int	DESTINATION_IS_NOT_FOUND	= 550 ;
	public static final int	ABNORMAL_ID					= 600 ;
	public static final int	UNAVAILABLE_ID				= 610 ;
	public static final int	ABNORMAL_EMAIL				= 620 ;
	public static final int	UNAVAILABLE_EMAIL			= 630 ;
	public static final int	ABNORMAL_PASSWORD			= 640 ;
	public static final int	UNAVAILABLE_PASSWORD		= 650 ;
	public static final int	ABNORMAL_ACCESS_CODE		= 660 ;
	public static final int	UNAVAILABLE_ACCESS_CODE		= 670 ;
	public static final int	NOT_EXIST_USER				= 700 ;
	public static final int	INVALID_NUMBER_OF_USERS		= 710 ;
	public static final int	CALL_FAILED					= 720 ;
    public static final int	ENGINE_SERVER_BUSY				= 800 ;
    public static final int	ENGINE_SERVER_REQUEST_TIME_OUT	= 810 ;
	public static final int	OTHER_ERROR					= 900 ;

	//------------------------------------------
	// private member variable
	//------------------------------------------
	private String m_ErrorCode ;
	private String m_Service ;
	private String m_Explanation ;

	//------------------------------------------
	// public member function
	//------------------------------------------

	/**
 * Constructor
	 * @param message
	 * @param err
	 * @param proc
	 * @param mcod
	 */
	public MCMLException(String message, String err, int proc, int mcod)
	{
		super(message) ;

//		m_ErrorCode			= String.format("%s-20%02d%04d", err, proc, mcod) ;
		m_ErrorCode			= String.format("%s-%04d", proc, mcod) ;	// Modified by Y. Ishii (2013/02/21)
		setService(proc);


		// 2013/03/25 mod hayashi start
//		switch(mcod){
//		case SYSTEM_TIME_OUT:
//			m_Explanation = "System time out" ;
//			break ;
//		case INTERNAL_ABNORMALITY:
//			m_Explanation = "Internal abnormality" ;
//			break ;
//		case CONNECTION_CLOSED:
//			m_Explanation = "Connection closed" ;
//			break ;
//		case ABNORMAL_DATA_FORMAT:
//			m_Explanation = "Abnormal data format" ;
//			break ;
//		case ABNORMAL_XML_DATA_FORMAT:
//			m_Explanation = "Abnormal XML data format" ;
//			break ;
//		case NON_CORESSPONDANCE_PARAM:
//			m_Explanation = "Non-correspondence parameter" ;
//			break ;
//		case ENGINE_CLOSED:
//			m_Explanation = "Engine closed" ;
//			break ;
//		case TIME_OUT:
//			m_Explanation = "Time out" ;
//			break ;
//		case ENGINE_DOWN:
//			m_Explanation = "Engine down" ;
//			break ;
//		case NOT_SUPPORT_LANGUAGE:
//			m_Explanation = "Not support language" ;
//			break ;
//		case NOT_EXPECTED_INPUT:
//			m_Explanation = "Not expected input" ;
//			break ;
//		case INTERNAL_ERROR:
//			m_Explanation = "Internal error" ;
//			break ;
//		case IRREPARABLE_ERROR:
//			m_Explanation = "irreparable error" ;
//			break ;
//		case DESTINATION_IS_NOT_FOUND:
//			m_Explanation = "Specified Destination is not found";
//			break;
//		case ENGINE_SERVER_BUSY :
//			m_Explanation = "Engine server busy" ;
//			break ;
//		case ENGINE_SERVER_REQUEST_TIME_OUT :
//			m_Explanation = "Engine Server Request Time out" ;
//			break ;
//		case ABNORMAL_ID:
//			m_Explanation = "Abnormal ID " ;
//			break ;
//		case UNAVAILABLE_ID:
//			m_Explanation = "Unavailable ID " ;
//			break ;
//		case ABNORMAL_EMAIL:
//			m_Explanation = "Abnormal EMail address" ;
//			break ;
//		case UNAVAILABLE_EMAIL:
//			m_Explanation = "Unavailable EMail address" ;
//			break ;
//		case ABNORMAL_PASSWORD:
//			m_Explanation = "Abnormal password" ;
//			break ;
//		case UNAVAILABLE_PASSWORD:
//			m_Explanation = "Unavailable password" ;
//			break ;
//		case ABNORMAL_ACCESS_CODE:
//			m_Explanation = "Abnormal access code" ;
//			break ;
//		case UNAVAILABLE_ACCESS_CODE:
//			m_Explanation = "Unavailable access code" ;
//			break ;
//		case NOT_EXIST_USER:
//			m_Explanation = "Not exist User";
//			break;
//		case INVALID_NUMBER_OF_USERS:
//			m_Explanation = "Invalid number of Users";
//			break;
//		case CALL_FAILED:
//			m_Explanation = "Call failed";
//			break;
//		case OTHER_ERROR:
//			m_Explanation = "Other errors" ;
//			break ;
//		default:
//			m_Explanation = "" ;
//			break ;
//		}

		//  method
		// update start by nozaki at 20130326
//		setExplanation(mcod);
		setExplanation(mcod, message);
		// update start by nozaki at 20130326
		// 2013/03/25 mod hayashi end

	}

	/**
 * Constructor
	 * @param message
	 * @param explanation
	 * @param errorCode
	 */
	public MCMLException(String message, String explanation, String errorCode){
		super(message);
		m_Explanation = explanation;
		m_ErrorCode = errorCode;
	}

	/**
 * Constructor
	 * 2013/03/25 add hayashi
	 * @param message
	 * @param err
	 * @param proc
	 * @param mcod
	 * @param e
	 */
	public MCMLException(String message, String err, int proc, int mcod, Throwable e) {

		super(message, e);
		m_ErrorCode = String.format("%s-%04d", proc, mcod) ;
		setService(proc);
// update start by nozaki at 20130326
//		setExplanation(mcod);
		setExplanation(mcod, message);
// update end by nozaki at 20130326

	}

	/**
 * Constructor
	 * @param message
	 * @param explanation
	 * @param errorCode
	 * @param e
	 */
	public MCMLException(String message, String explanation, String errorCode, Throwable e) {

		super(message, e);
		m_Explanation = explanation;
		m_ErrorCode = errorCode;

	}


	/**
	 *
	 * @param mcod
	 */
// update start by nozaki at 20130326
//	private void setExplanation(int mcod) {
	private void setExplanation(int mcod, String message) {
// update end by nozaki at 20130326

		switch(mcod){
		case SYSTEM_TIME_OUT:
			m_Explanation = "System time out" ;
			break ;
		case INTERNAL_ABNORMALITY:
			m_Explanation = "Internal abnormality" ;
			break ;
		case CONNECTION_CLOSED:
			m_Explanation = "Connection closed" ;
			break ;
		case ABNORMAL_DATA_FORMAT:
			m_Explanation = "Abnormal data format" ;
			break ;
		case ABNORMAL_XML_DATA_FORMAT:
			m_Explanation = "Abnormal XML data format" ;
			break ;
		case NON_CORESSPONDANCE_PARAM:
			m_Explanation = "Non-correspondence parameter" ;
			break ;
		case ENGINE_CLOSED:
			m_Explanation = "Engine closed" ;
			break ;
		case TIME_OUT:
			m_Explanation = "Time out" ;
			break ;
		case ENGINE_DOWN:
			m_Explanation = "Engine down" ;
			break ;
		case NOT_SUPPORT_LANGUAGE:
			m_Explanation = "Not support language" ;
			break ;
		case NOT_EXPECTED_INPUT:
			m_Explanation = "Not expected input" ;
			break ;
		case INTERNAL_ERROR:
			m_Explanation = "Internal error" ;
			break ;
		case IRREPARABLE_ERROR:
			m_Explanation = "irreparable error" ;
			break ;
		case DESTINATION_IS_NOT_FOUND:
			m_Explanation = "Specified Destination is not found";
			break;
		case ENGINE_SERVER_BUSY :
			m_Explanation = "Engine server busy" ;
			break ;
		case ENGINE_SERVER_REQUEST_TIME_OUT :
			m_Explanation = "Engine Server Request Time out" ;
			break ;
		case ABNORMAL_ID:
			m_Explanation = "Abnormal ID " ;
			break ;
		case UNAVAILABLE_ID:
			m_Explanation = "Unavailable ID " ;
			break ;
		case ABNORMAL_EMAIL:
			m_Explanation = "Abnormal EMail address" ;
			break ;
		case UNAVAILABLE_EMAIL:
			m_Explanation = "Unavailable EMail address" ;
			break ;
		case ABNORMAL_PASSWORD:
			m_Explanation = "Abnormal password" ;
			break ;
		case UNAVAILABLE_PASSWORD:
			m_Explanation = "Unavailable password" ;
			break ;
		case ABNORMAL_ACCESS_CODE:
			m_Explanation = "Abnormal access code" ;
			break ;
		case UNAVAILABLE_ACCESS_CODE:
			m_Explanation = "Unavailable access code" ;
			break ;
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
			m_Explanation = "Other errors" ;
			break ;
		default:
			m_Explanation = "" ;
			break ;
		}

		// update start by nozaki at 20130326
		m_Explanation = m_Explanation + " [" + message + "]";
		// update end by nozaki at 20130326

	}

	/**
	 *
	 * @return
	 */
	public String getService()
	{
		return m_Service ;
	}

	/**
	 *
	 * @return
	 */
	public String getErrorCode()
	{
		return m_ErrorCode ;
	}

	/**
	 *
	 * @return
	 */
	public String getExplanation()
	{
		return m_Explanation ;
	}

	/**
	 *
	 * @param proc
	 */
	private void setService(int proc)
	{
		if(proc == COMMON){
			m_Service = "Common";
		}else if(proc == ASR){
			m_Service = "ASR";
		}else if(proc == MT){
			m_Service = "MT";
		}else if(proc == TTS){
			m_Service = "TTS";
		}
	}


}

