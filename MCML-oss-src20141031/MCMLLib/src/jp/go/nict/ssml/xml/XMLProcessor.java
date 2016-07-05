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

package jp.go.nict.ssml.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import jp.go.nict.common.LicenseManager.SimpleLicenseCertification;
import jp.go.nict.ssml.xml.types.ssml_nxDoc;
import jp.go.nict.ssml.xml.types.speakType;


public class XMLProcessor {
	//------------------------------------------
	// private member constant
	//------------------------------------------
    private final String ROOT_ELEMENT_NAME		= "speak";
    private final String SCHEMA_LOCATON		= "synthesis.xsd";
    private final String CHARSET_NAME			= "UTF-8";

    private final static String SYNCHRONIZER	= "ForSynchronize" ;


	//------------------------------------------
	// public member function
	//------------------------------------------
	// constructor
	public XMLProcessor()
	{
		// no process
                SimpleLicenseCertification.isCertification();
	}

	public String generate(speakType speakType)
		throws UnsupportedEncodingException, IOException
	{
		String retVal ;

		synchronized(SYNCHRONIZER){
			ssml_nxDoc doc = new ssml_nxDoc();

			// set root node
			doc.setRootElement("", ROOT_ELEMENT_NAME, speakType) ;
			doc.setSchemaLocation(SCHEMA_LOCATON) ;

			// output node information to stream
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream() ;
			doc.save(outputStream, speakType) ;

			retVal = outputStream.toString(CHARSET_NAME) ;
			outputStream.close() ;
		}

		// return text
		return retVal ;
	}

	public speakType parse(String xmlData)
		throws UnsupportedEncodingException, IOException
	{
		speakType retVal ;

		synchronized(SYNCHRONIZER){
			ssml_nxDoc doc = new ssml_nxDoc();

			// generate stream from text
			ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlData.getBytes(CHARSET_NAME)) ;

			retVal = new speakType(doc.load(inputStream));

			inputStream.close() ;
		}

		// return node information
		return retVal ;
	}
}
