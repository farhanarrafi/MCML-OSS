////////////////////////////////////////////////////////////////////////
//
// LocationServer.java
//
// This file was generated by XMLSpy 2014 Enterprise Edition.
//
// YOU SHOULD NOT MODIFY THIS FILE, BECAUSE IT WILL BE
// OVERWRITTEN WHEN YOU RE-RUN CODE GENERATION.
//
// Refer to the XMLSpy Documentation for further details.
// http://www.altova.com/xmlspy
//
////////////////////////////////////////////////////////////////////////

package com.Information;

public class InformationDoc extends com.altova.xml.TypeBase
{
	public static com.altova.xml.meta.ComplexType getStaticInfo() { return new com.altova.xml.meta.ComplexType(com.Information.InformationTypeInfo.binder.getTypes()[com.Information.InformationTypeInfo._altova_ti_altova_LocationServer]); }

	public static InformationDoc loadFromFile(String filename) throws Exception
	{
		return new InformationDoc(com.altova.xml.XmlTreeOperations.loadDocument(filename));			
	}

	public static InformationDoc loadFromString(String xmlstring) throws Exception
	{
		return new InformationDoc(com.altova.xml.XmlTreeOperations.loadXml(xmlstring));			
	}

	public static InformationDoc loadFromBinary(byte[] binary) throws Exception
	{
		return new InformationDoc(com.altova.xml.XmlTreeOperations.loadXmlBinary(binary));
	}

	public void saveToFile(String filename, boolean prettyPrint) throws Exception
	{
		saveToFile(filename, prettyPrint, "UTF-8", false, false);
	}
	
	public void saveToFile(String filename, boolean prettyPrint, String encoding) throws Exception
	{
		saveToFile( filename, prettyPrint, encoding, encoding.compareToIgnoreCase("UTF-16BE") == 0, encoding.compareToIgnoreCase("UTF-16") == 0 );
	}

	public void saveToFile(String filename, boolean prettyPrint, String encoding, boolean bBigEndian, boolean bBOM) throws Exception
	{
		org.w3c.dom.Document doc = (org.w3c.dom.Document) getNode();
		com.altova.xml.XmlTreeOperations.saveDocument(doc, filename, encoding, bBigEndian, bBOM, prettyPrint);
	}

	public String saveToString(boolean prettyPrint) throws Exception
	{
		org.w3c.dom.Document doc = (org.w3c.dom.Document) getNode();
		return com.altova.xml.XmlTreeOperations.saveXml(doc, prettyPrint);
	}

	public byte[] saveToBinary(boolean prettyPrint) throws Exception
	{
		return saveToBinary(prettyPrint, "UTF-8", false, false);
	}
	
	public byte[] saveToBinary(boolean prettyPrint, String encoding) throws Exception
	{
		return saveToBinary( prettyPrint, encoding, encoding.compareToIgnoreCase("UTF-16BE") == 0, encoding.compareToIgnoreCase("UTF-16") == 0 );
	}

	public byte[] saveToBinary(boolean prettyPrint, String encoding, boolean bBigEndian, boolean bBOM) throws Exception
	{
		org.w3c.dom.Document doc = (org.w3c.dom.Document) getNode();
		return com.altova.xml.XmlTreeOperations.saveXmlBinary(doc, encoding, bBigEndian, bBOM, prettyPrint);
	}

	public static InformationDoc createDocument() throws Exception
	{
		org.w3c.dom.Document doc = com.altova.xml.XmlTreeOperations.createDocument();
		return new InformationDoc(doc);
	}

	public void setSchemaLocation(String schemaLocation) throws Exception
	{
		org.w3c.dom.Document doc = (org.w3c.dom.Document) node;
		if (doc.getDocumentElement() == null)
			throw new Exception("SetSchemaLocation requires a root element.");
		String namespaceuri = doc.getDocumentElement().getNamespaceURI();
		if (namespaceuri == null || namespaceuri.length() == 0)
			doc.getDocumentElement().setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "noNamespaceSchemaLocation", schemaLocation);
		else
			doc.getDocumentElement().setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation", namespaceuri + " " + schemaLocation);
	}
	
	public InformationDoc(org.w3c.dom.Node init)
	{
		super(init);
		instantiateMembers();
	}

	private void instantiateMembers()
	{

		Request= new MemberElement_Request (this, com.Information.InformationTypeInfo.binder.getMembers()[com.Information.InformationTypeInfo._altova_mi_altova_LocationServer._Request]);
		Response= new MemberElement_Response (this, com.Information.InformationTypeInfo.binder.getMembers()[com.Information.InformationTypeInfo._altova_mi_altova_LocationServer._Response]);
	}
	// Attributes


	// Elements
	
	public MemberElement_Request Request;

		public static class MemberElement_Request
		{
			public static class MemberElement_Request_Iterator implements java.util.Iterator
			{
				private org.w3c.dom.Node nextNode;
				private MemberElement_Request member;
				public MemberElement_Request_Iterator(MemberElement_Request member) {this.member=member; nextNode=member.owner.getElementFirst(member.info);}
				public boolean hasNext() 
				{
					while (nextNode != null)
					{
						if (com.altova.xml.TypeBase.memberEqualsNode(member.info, nextNode))
							return true;
						nextNode = nextNode.getNextSibling();
					}
					return false;
				}
				
				public Object next()
				{
					RequestType nx = new RequestType(nextNode);
					nextNode = nextNode.getNextSibling();
					return nx;
				}
				
				public void remove () {}
			}
			protected com.altova.xml.TypeBase owner;
			protected com.altova.typeinfo.MemberInfo info;
			public MemberElement_Request (com.altova.xml.TypeBase owner, com.altova.typeinfo.MemberInfo info) { this.owner = owner; this.info = info;}
			public RequestType at(int index) {return new RequestType(owner.getElementAt(info, index));}
			public RequestType first() {return new RequestType(owner.getElementFirst(info));}
			public RequestType last(){return new RequestType(owner.getElementLast(info));}
			public RequestType append(){return new RequestType(owner.createElement(info));}
			public boolean exists() {return count() > 0;}
			public int count() {return owner.countElement(info);}
			public void remove() {owner.removeElement(info);}
			public void removeAt(int index) {owner.removeElementAt(info, index);}
			public java.util.Iterator iterator() {return new MemberElement_Request_Iterator(this);}
			public com.altova.xml.meta.Element getInfo() { return new com.altova.xml.meta.Element(info); }
		}
	
	public MemberElement_Response Response;

		public static class MemberElement_Response
		{
			public static class MemberElement_Response_Iterator implements java.util.Iterator
			{
				private org.w3c.dom.Node nextNode;
				private MemberElement_Response member;
				public MemberElement_Response_Iterator(MemberElement_Response member) {this.member=member; nextNode=member.owner.getElementFirst(member.info);}
				public boolean hasNext() 
				{
					while (nextNode != null)
					{
						if (com.altova.xml.TypeBase.memberEqualsNode(member.info, nextNode))
							return true;
						nextNode = nextNode.getNextSibling();
					}
					return false;
				}
				
				public Object next()
				{
					ResponseType nx = new ResponseType(nextNode);
					nextNode = nextNode.getNextSibling();
					return nx;
				}
				
				public void remove () {}
			}
			protected com.altova.xml.TypeBase owner;
			protected com.altova.typeinfo.MemberInfo info;
			public MemberElement_Response (com.altova.xml.TypeBase owner, com.altova.typeinfo.MemberInfo info) { this.owner = owner; this.info = info;}
			public ResponseType at(int index) {return new ResponseType(owner.getElementAt(info, index));}
			public ResponseType first() {return new ResponseType(owner.getElementFirst(info));}
			public ResponseType last(){return new ResponseType(owner.getElementLast(info));}
			public ResponseType append(){return new ResponseType(owner.createElement(info));}
			public boolean exists() {return count() > 0;}
			public int count() {return owner.countElement(info);}
			public void remove() {owner.removeElement(info);}
			public void removeAt(int index) {owner.removeElementAt(info, index);}
			public java.util.Iterator iterator() {return new MemberElement_Response_Iterator(this);}
			public com.altova.xml.meta.Element getInfo() { return new com.altova.xml.meta.Element(info); }
		}

		public void setXsiType() {com.altova.xml.XmlTreeOperations.setAttribute(getNode(), "http://www.w3.org/2001/XMLSchema-instance", "xsi:type", "", "LocationServer");}
}