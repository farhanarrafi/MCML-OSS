//-------------------------------------------------------------------
// Ver.1.0
// 2011/01/25
//-------------------------------------------------------------------

package jp.go.nict.mcml.com.server;

import jp.go.nict.mcml.com.ComData;

public class RequestData extends ComData
{
	
	//------------------------------------------
	// public member function
	//------------------------------------------
	
	public boolean hasXML()
	{
		if(!m_XML.isEmpty()){;
			return true;
		}
		return false;
	}
	
	public boolean hasBinary()
	{
		if(!m_BinaryList.isEmpty()){;
			return true;
		}
		return false;
	}	
}
