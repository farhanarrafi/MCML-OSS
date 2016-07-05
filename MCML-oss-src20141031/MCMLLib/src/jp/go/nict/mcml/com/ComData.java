//-------------------------------------------------------------------
// Ver.1.0
// 2011/01/25
//-------------------------------------------------------------------

package jp.go.nict.mcml.com;

import java.util.ArrayList;

public class ComData {
	//------------------------------------------
	// protected member variable
	//------------------------------------------
	protected String m_XML ;
	protected ArrayList<byte[]>	m_BinaryList ;
	
	//------------------------------------------
	// public member function
	//------------------------------------------
	// constructor
	public ComData() {
		m_XML = null ;
		m_BinaryList = new ArrayList<byte[]>() ;
	}
	
	public String getXML() 
	{
		return m_XML;
	}
	
	public void setXML(String string) 
	{
		m_XML = string;
	}
	
	public byte[] getBinary() 
	{
		if(m_BinaryList.size() == 0){
			return null ;
		}
		return m_BinaryList.get(0);
	}
	
	public void setBinary(byte[] binary) 
	{
		m_BinaryList.clear() ;
		m_BinaryList.add(binary) ;
	}
	
	public ArrayList<byte[]> getBinaryList() 
	{
		return m_BinaryList;
	}

	public void setBinaryList(ArrayList<byte[]> binaryList) 
	{
		m_BinaryList = binaryList;
	}
	
	public boolean hasXML()
	{
		if(m_XML != null && !m_XML.isEmpty()){;
			return true;
		}
		return false;
	}
	
	public boolean hasBinary()
	{
		if(m_BinaryList != null && !m_BinaryList.isEmpty()){;
			return true;
		}
		return false;
	}	
}
