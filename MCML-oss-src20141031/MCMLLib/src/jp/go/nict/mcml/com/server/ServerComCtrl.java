//-------------------------------------------------------------------
// Ver.1.0
// 2011/01/25
//-------------------------------------------------------------------

package jp.go.nict.mcml.com.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.mail.MessagingException;
import jp.go.nict.mcml.com.ComCtrl;

public class ServerComCtrl extends ComCtrl {

	//------------------------------------------
	// public member function
	//------------------------------------------
	
	// constructor
	public ServerComCtrl()
	{
	}

	// receive request MIME data
	public RequestData receiveRequesData(InputStream stream) 
		throws MessagingException, IOException
	{
		// data receive
		RequestData retVal = new RequestData() ;
		super.receive(stream, retVal) ;
		
		// success
		return retVal ;
	}

	// send response MIME data (multi data version)
	public void sendResponseData(OutputStream stream, String xmlData, ArrayList<byte[]> binaryDataList)  
		throws MessagingException, IOException
	{
		// data send
		super.send(stream, xmlData, binaryDataList) ;
		
		// success
		return ;
	}
	
	// send response MIME data (single data version)
	public void sendResponseData(OutputStream stream, String xmlData, byte[] binaryData)
		throws MessagingException, IOException
	{
		ArrayList<byte[]> binaryDataList = new ArrayList<byte[]>() ;
		binaryDataList.add(binaryData) ;
		
		// data send
		sendResponseData(stream, xmlData, binaryDataList) ;
		
		// success
		return ;
	}
}
