//-------------------------------------------------------------------
// Ver.1.0
// 2011/01/25
//-------------------------------------------------------------------

package jp.go.nict.mcml.com.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.net.HttpURLConnection;
import javax.mail.MessagingException;

import jp.go.nict.mcml.com.ComCtrl;
import jp.go.nict.mcml.xml.types.ErrorType;
import jp.go.nict.mcml.xml.types.MCMLType;
import jp.go.nict.mcml.xml.types.ResponseType;
import jp.go.nict.mcml.xml.types.ServerType;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class ClientComCtrl extends ComCtrl {
	//------------------------------------------
	// private member constant
	//------------------------------------------
	private static final String	HEADER_SEND_COOKIE		= "Cookie" ;
	private static final String	HEADER_RECEIVE_COOKIE	= "Set-Cookie" ;
	private static final String STRING_URL_HTTP = "http://" ;
	private static final String STRING_URL_HTTPS = "https://" ;

	//-------------------------------------------
	// for Error messsage
	//-------------------------------------------
	private static final String Service					= "Common";
	private static final String Error					= "E-00000010";

	//------------------------------------------
	// protected member variable
	//------------------------------------------
	protected String m_Cookie;
	protected Proxy	m_Proxy ;
	protected int m_TimeoutMilliSeconds ;
	protected boolean m_IsUsingSeifSignedCertification ;

	//------------------------------------------
	// public member function
	//------------------------------------------
	// constructor
	public ClientComCtrl()
	{
		m_Cookie				= null ;
		m_Proxy					= null ;
		m_TimeoutMilliSeconds	= 0 ;
		m_IsUsingSeifSignedCertification = false ;
	}

	// constructor for use proxy
	public ClientComCtrl(String httpProxyHost,int httpProxyPort) {
		SocketAddress addr = new InetSocketAddress(httpProxyHost, httpProxyPort);
		m_Proxy = new Proxy(Proxy.Type.HTTP, addr);
	}

	// execute request (multi data version)
	public ResponseData request(String url, String xmlData, ArrayList<byte[]> binaryDataList)
	//throws MalformedURLException, IOException, MessagingException
			throws MessagingException
	{

		// select URL connection
		URLConnection connection ;
		try{
			connection = selectURLConnection(url) ;
			if(connection==null){
				return null ;
			}
		}
		catch(IOException e){
			return null;
		}
		catch(NoSuchAlgorithmException e){
			return null ;
		}
		catch(KeyManagementException e){
			return null ;
		}

		// specify output
		connection.setDoOutput(true) ;

		if(m_TimeoutMilliSeconds > 0){
			// set timeout
			connection.setConnectTimeout(m_TimeoutMilliSeconds) ;
			connection.setReadTimeout(m_TimeoutMilliSeconds) ;
		}

		if(m_Cookie != null){
			connection.setRequestProperty(HEADER_SEND_COOKIE, m_Cookie) ;
		}

		ResponseData retVal = new ResponseData() ;
		try{
			// send request MIME message
			OutputStream out = connection.getOutputStream() ;
			super.send(out, xmlData, binaryDataList) ;
			out.close() ;

			// receive response MIME message
			InputStream in = connection.getInputStream() ;
			super.receive(in, retVal) ;
			in.close() ;

		}catch(IOException e){
// 2012/05/28 Modified by Yohei Saga
//			retVal = createErrorResponce("<!CDATA[[" + e.getMessage() + "]]>");
			retVal = createErrorResponce(e.getMessage());
			return retVal;
		}

		String cookie = connection.getHeaderField(HEADER_RECEIVE_COOKIE) ; ;
		if(cookie != null && !cookie.isEmpty() && !cookie.equals("")){ //add ktakai
			m_Cookie = cookie ;
		}

		/*
		if (m_Connection != null) {
			synchronized (m_Connection) {
				m_Connection.disconnect();
				m_Connection = null;
			}
		}*/


		// success
		return retVal ;
	}



	/**
    @brief    create message, when error occur on HTTP
    @return   ResponseData : HTTP responce
    @param    str : String : message detail
    @see
    @date     2012/05/25:(takai) initial version
    @throws   Exception
    @bug
    @warning
    @note
    */
	private ResponseData createErrorResponce(String mes)
	{
		ResponseData res = new ResponseData();

		MCMLType outputMCML = new MCMLType();
		try {
			// create error instance
			ErrorType errorType = new ErrorType();
			errorType.addCode(Error);

			if(mes != null && !mes.isEmpty()){
				errorType.addMessage(mes);
			}
			errorType.addService(Service);

			//error = XMLTypeTools.generateErrorType(exp.getErrorCode(),exp.getExplanation(),exp.getService());

			ResponseType responseType = new ResponseType();
			responseType.addService(Service);
			responseType.addProcessOrder("0");
			responseType.addError(errorType);
			ServerType serverType = new ServerType();
			serverType.addResponse(responseType);

			// set mcml instance
			outputMCML.addServer(serverType) ;

			// create mcml string
			jp.go.nict.mcml.xml.XMLProcessor xml = new jp.go.nict.mcml.xml.XMLProcessor();
			String mcmlString = xml.generate(outputMCML);

			res.setXML(mcmlString);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return res;

	}


	// execute request (single data version)
	public ResponseData request(String url, String xmlData, byte[] binaryData)
	throws MalformedURLException, IOException, MessagingException
	{
		ArrayList<byte[]> binaryDataList = new ArrayList<byte[]>() ;
		if(binaryData != null){
			binaryDataList.add(binaryData) ;
		}
		return request(url, xmlData, binaryDataList) ;
	}

	// execute request (not use binary data)
	public ResponseData request(String url, String xmlData)
	throws MalformedURLException, IOException, MessagingException
	{
		return request(url, xmlData, (byte[])null) ;
	}

	// execute request (not use xml data & multi data version)
	public ResponseData request(String url, ArrayList<byte[]> binaryDataList)
	throws MalformedURLException, IOException, MessagingException
	{
		return request(url, null, binaryDataList) ;
	}

	// execute request (not use xml data & single data version)
	public ResponseData request(String url, byte[] binaryData)
	throws MalformedURLException, IOException, MessagingException
	{
		return request(url, null, binaryData) ;
	}

	// execute request (not use xml data & not use binary data)
	public ResponseData request(String url)
	throws MalformedURLException, IOException, MessagingException
	{
		return request(url, null, (byte[])null) ;
	}

	// set time out value
	public boolean setTimeout(int timeoutMilliSeconds){
		if(timeoutMilliSeconds < 0){
			// illegal argument
			return false ;
		}

		m_TimeoutMilliSeconds = timeoutMilliSeconds ;

		// success
		return true ;
	}

	// using self signed certification on https
	public void setUsingSelfSignedCertification(){
		m_IsUsingSeifSignedCertification = true ;
	}

	//------------------------------------------
	// private member function
	//------------------------------------------
	// select URL Connection
	private URLConnection selectURLConnection(String url)
		throws IOException,NoSuchAlgorithmException, KeyManagementException
	{
		// analyze url
		if(url.startsWith(STRING_URL_HTTP)){
			// select http
			HttpURLConnection httpConnection =
				(HttpURLConnection)openURLConnection(url) ;
			return httpConnection ;
		}
		else if(url.startsWith(STRING_URL_HTTPS)){
			// select https
			HttpsURLConnection httpsConnection =
				(HttpsURLConnection)openURLConnection(url) ;
			// use self signed certification
			if(m_IsUsingSeifSignedCertification){
				// correct authentication result of certificate
				correctAuthenticationResultofCertificate(httpsConnection,url);
			}
			return httpsConnection ;
		}
		else{
			// no such protocol
			return null ;
		}
	}

	// open URL connection
	private URLConnection openURLConnection(String url) throws IOException{
		// connection to servlet
		URL urlObj = new URL(url) ;
		URLConnection connection ;

		if(m_Proxy != null){
			connection = urlObj.openConnection(m_Proxy);
		}
		else{
			connection = urlObj.openConnection();
		}
		return connection ;
	}

	// correct authentication result of certificate
	private void correctAuthenticationResultofCertificate(
		HttpsURLConnection httpsConnection, String url)
		throws NoSuchAlgorithmException, KeyManagementException{
		// get hostName
		final String hostName = getHostNameFromUrl(url) ;
		// destination host name is unknown
		if(hostName==null){
			return ;
		}

		// correct authentication result
		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
			public boolean verify(String host,SSLSession SSL){
				// Is destination host name equals authentication host name?
				if(hostName.equals(host)){
					// correct result(fail->success)
					return true ;
				}
				// not correct result
				return false ;
			}
		});

		// authentication result notify to java
		KeyManager[] km = null ;
		TrustManager[] tm = {
				new X509TrustManager(){
					public void checkClientTrusted(X509Certificate[] arg0,
						String arg1) throws CertificateException {}
					public void checkServerTrusted(X509Certificate[] arg0,
						String arg1) throws CertificateException {}
					public X509Certificate[] getAcceptedIssuers() {
						return null;
					}
				}
		};
		// setting HTTPS context
		SSLContext sslcontext = SSLContext.getInstance("SSL");
		sslcontext.init(km,tm,new SecureRandom() );
		httpsConnection.setSSLSocketFactory(sslcontext.getSocketFactory());
	}

	// get host name from URL
	private String getHostNameFromUrl(String url){
		String hostName = null ;

		if(url.startsWith(STRING_URL_HTTPS)){
			int hostNameStartIndex = STRING_URL_HTTPS.length();
			int hostNameEndIndex1 ;
			int hostNameEndIndex2 ;
			int hostNameEndIndex = -1 ;

			hostNameEndIndex1 = url.indexOf(":",hostNameStartIndex);
			hostNameEndIndex2 = url.indexOf("/",hostNameStartIndex);

			// On the specification of URL, ":" must be earlier than "/"
			// checked.
			// URL is "https://hostname:portNumber/..." or "https://hostname/"
			if(hostNameEndIndex1 != -1){
				hostNameEndIndex = hostNameEndIndex1 ;
			}
			else if(hostNameEndIndex2 != -1){
				hostNameEndIndex = hostNameEndIndex2 ;
			}
			if(hostNameEndIndex != -1){
				hostName = url.substring(hostNameStartIndex,hostNameEndIndex);
			}
		}
		return hostName ;
	}
}
