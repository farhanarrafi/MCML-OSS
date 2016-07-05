//-------------------------------------------------------------------
// Ver.1.0
// 2011/01/25
//-------------------------------------------------------------------

package jp.go.nict.mcml.com;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

public class MimeData {
	//------------------------------------------
	// protected member constant
	//------------------------------------------
	protected final String HEADER_CONTENT_TYPE		= "Content-Type" ;
	protected final String HEADER_CONTENT_ENCODING	= "Content-Transfer-Encoding" ;
	protected final String CONTENT_TYPE_TEXT			= "text/xml;charset=utf-8" ;
	protected final String CONTENT_TYPE_BINARY		= "application/octet-stream" ;
	protected final String ENCODING_7BIT				= "7bit" ;
	protected final String ENCODING_BINARY			= "binary" ;
	protected final String CHARSET_NAME				= "UTF-8" ;

	//------------------------------------------
	// private member variable
	//------------------------------------------
	private String	m_Subject;
	
	//------------------------------------------
	// protected member variable
	//------------------------------------------
	protected MimeMultipart m_MultiPart ;

	//------------------------------------------
	// public member function
	//------------------------------------------
	// default constructor
	public MimeData()
	{
		// generate MIME multi part object
		m_MultiPart = new MimeMultipart() ;
		m_Subject = null ;
	}
	
	// constructor for parser
	public MimeData(InputStream inputStream) throws MessagingException, IOException
	{
		// generate MIME message object
		Session session = getSession() ;
		MimeMessage mimeMsg = new MimeMessage(session, inputStream) ;
		m_MultiPart =  (MimeMultipart)mimeMsg.getContent() ;
		m_Subject = mimeMsg.getSubject();
	}
	
	// add body(text)
	public void addMimeBodyPart(String textData) throws MessagingException
	{
		MimeBodyPart bp = new MimeBodyPart() ;
        bp.setText(textData, CHARSET_NAME) ;
		bp.setHeader(HEADER_CONTENT_TYPE, CONTENT_TYPE_TEXT) ;
		bp.setHeader(HEADER_CONTENT_ENCODING, ENCODING_7BIT) ;
		m_MultiPart.addBodyPart(bp) ;
		
		// success
		return ;
	}
	
	// add body(binary)
	public void addMimeBody(byte[] binData) throws MessagingException
	{
		MimeBodyPart bp = new MimeBodyPart() ;
        bp.setDataHandler(new DataHandler(new ByteArrayDataSource(binData, CONTENT_TYPE_BINARY)));
		bp.setHeader(HEADER_CONTENT_ENCODING, ENCODING_BINARY) ;
		m_MultiPart.addBodyPart(bp) ;
		
		// success
		return ;
	}
	
	// get MIME message
	public MimeMessage getMimeMessage() throws MessagingException
	{
		// generate MIME message
		MimeMessage retVal = new MimeMessage(getSession()) ;
		retVal.setContent(m_MultiPart) ;
		
		if(m_Subject != null){
			// set Subject on MIME message
			retVal.setSubject(m_Subject, CHARSET_NAME);
		}
		
		// success
		return retVal ;
	}
	
	// get number of body part
	public int getCount() throws MessagingException
	{
		return m_MultiPart.getCount() ;
	}
	
	// get text data part(for parser)
	public String getTextData(int no) throws MessagingException, IOException
	{
		// get OutputStream for text data
		ByteArrayOutputStream outStream = getBodyPartOutputStream(no, CONTENT_TYPE_TEXT) ;
		if(outStream == null){
			return null ; // no data
		}
		
		// set return value
		String retVal = outStream.toString(CHARSET_NAME) ;

		// close OutputStream
		outStream.close() ;
		
		// success
		return retVal ;
	}
	
	// get binary data part(for parser)
	public byte[] getBinaryData(int no) throws MessagingException, IOException
	{
		// get OutputStream for binary data
		ByteArrayOutputStream outStream = getBodyPartOutputStream(no, CONTENT_TYPE_BINARY) ;
		if(outStream == null){
			return null ; // no data
		}
		
		// set return value
		byte[] retVal = outStream.toByteArray() ;

		// close OutputStream
		outStream.close() ;
		
		// success
		return retVal ;
	}
	
	// get OutputStream for MIME body part
	public ByteArrayOutputStream getBodyPartOutputStream(int no, String contentType) throws MessagingException, IOException
	{
		if(no < 0 || no >= m_MultiPart.getCount()){
			return null ; // wrong parameter
		}

		// get MIME body part
		BodyPart bp = m_MultiPart.getBodyPart(no) ;
		if(!bp.getContentType().equals(contentType)){
			return null ; // no match contentType
		}
		
		// generate stream
		InputStream				inStream	= bp.getInputStream() ;
		ByteArrayOutputStream	retVal		= new ByteArrayOutputStream() ;
		
		// get data
		int data ;
        while ((data = inStream.read()) != -1) {
            retVal.write(data);
        }

		// close stream
		inStream.close() ;	
		
		// success
		return retVal ;
	}
	
	// set subject
	public void setSubject(String subject){
		m_Subject = subject;
	}
	
	// get subject
	public String getSubject(){
		return m_Subject;
	}
	
	//------------------------------------------
	// protected member function
	//------------------------------------------
	// get SessionInstance
	protected Session getSession()
	{
		Properties prop = System.getProperties() ;
		return Session.getDefaultInstance(prop) ;
	}
}
