//-------------------------------------------------------------------
// Ver.1.0
// 2011/01/25
//-------------------------------------------------------------------

package jp.go.nict.mcml.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import jp.go.nict.mcml.xml.types.MCMLDoc;
import jp.go.nict.mcml.xml.types.MCMLType;


public class XMLProcessor {
	//------------------------------------------
	// private member constant
	//------------------------------------------
    private final String SCHEMA_LOCATON		= "MCML4ITU_Sep6-12.xsd";
    private final String CHARSET_NAME			= "UTF-8";
    private final static String SYNCHRONIZER	= "ForSynchronize" ;
    private final String ROOT_ELEMENT_NAME		= "MCML";
    
	//------------------------------------------
	// public member function
	//------------------------------------------
	// constructor
	public XMLProcessor()
	{
		// no process
	}
	
	public String generate(MCMLType mcmlType) 
		throws UnsupportedEncodingException, IOException 
	{
		String retVal ;
		
		synchronized(SYNCHRONIZER){
			MCMLDoc doc = new MCMLDoc();
			
			// set root node
			doc.setRootElement(null, ROOT_ELEMENT_NAME,mcmlType);
			doc.setSchemaLocation(SCHEMA_LOCATON) ;
			
			// output node information to stream
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream() ;
			doc.save(outputStream, mcmlType) ;
			
			retVal = outputStream.toString(CHARSET_NAME) ;
			outputStream.close() ;
		}
		
		// return text
		return retVal ;
	}
	
	public MCMLType parse(String xmlData) 
		throws UnsupportedEncodingException, IOException 
	{
		MCMLType retVal ;
		
		synchronized(SYNCHRONIZER){
			MCMLDoc doc = new MCMLDoc();
			
			// generate stream from text
			ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlData.getBytes(CHARSET_NAME)) ;
			
			retVal = new MCMLType(doc.load(inputStream));
				
			inputStream.close() ;
		}
			
		// return node information
		return retVal ;
	}	
}
