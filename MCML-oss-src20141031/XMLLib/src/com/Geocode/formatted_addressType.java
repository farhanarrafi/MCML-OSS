////////////////////////////////////////////////////////////////////////
//
// formatted_addressType.java
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

package com.Geocode;

public class formatted_addressType extends com.Geocode.xs.stringType
{
	public static com.altova.xml.meta.ComplexType getStaticInfo() { return new com.altova.xml.meta.ComplexType(com.Geocode.GeocodeTypeInfo.binder.getTypes()[com.Geocode.GeocodeTypeInfo._altova_ti_altova_formatted_addressType]); }
	
	public formatted_addressType(org.w3c.dom.Node init)
	{
		super(init);
		instantiateMembers();
	}

	private void instantiateMembers()
	{

	}
	// Attributes


	// Elements

		public void setXsiType() {com.altova.xml.XmlTreeOperations.setAttribute(getNode(), "http://www.w3.org/2001/XMLSchema-instance", "xsi:type", "http://www.w3.org/2001/XMLSchema", "string");}
}
