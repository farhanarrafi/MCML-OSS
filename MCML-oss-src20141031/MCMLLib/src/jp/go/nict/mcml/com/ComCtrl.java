//-------------------------------------------------------------------
// Ver.1.1
// 2011/6/22
//-------------------------------------------------------------------

package jp.go.nict.mcml.com;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public class ComCtrl {
    // Constructor.
    public ComCtrl() {
    }

	//------------------------------------------
	// protected member function
	//------------------------------------------
	protected  void receive(InputStream stream, ComData comData) 
		throws MessagingException, IOException
	{
		// generate InputStream
		BufferedInputStream bufStream = new BufferedInputStream(stream) ;
        		 		
		// receive MIME data
		MimeData mimeData = new MimeData(bufStream) ;

		// close InputStream
		bufStream.close();
		
		// get MIME data contents
		String text = mimeData.getTextData(0) ;
		if(text != null){
			comData.setXML(text) ;
		}
		
		int count = mimeData.getCount() ;
		ArrayList<byte[]>  byteList = new ArrayList<byte[]>() ;
		
		// loop for number of MIME data body
		for(int no=1; no<count ; no++){
			byte[] bin = mimeData.getBinaryData(no) ;
			if(bin != null){
				// add binary data
				byteList.add(bin) ;
			}
		}
		
		comData.setBinaryList(byteList) ;
		
		// success
		return ;
	}
	
	protected void send(OutputStream stream, String stringData, ArrayList<byte[]> binaryList) 
		throws MessagingException, IOException
	{
		// make MIME data
		MimeData mime = new MimeData() ;

		// set string data part
		if(stringData != null){
			mime.addMimeBodyPart(stringData) ;
		}
		else{
			mime.addMimeBodyPart("") ;
		}
		
		if(binaryList != null){
			for(int i=0; i<binaryList.size(); i++){
				// set binary data part
				mime.addMimeBody(binaryList.get(i)) ;
			}
		}
		
		// get MIME message
		MimeMessage mmsg = mime.getMimeMessage() ;
		
		// generate OutputStream
		BufferedOutputStream bufStream = new BufferedOutputStream(stream) ;
			
		// send
		mmsg.writeTo(bufStream) ;
		
		// close OutputStream
		bufStream.flush() ;
		bufStream.close();
		
		// success
		return ;
	}
}
	
