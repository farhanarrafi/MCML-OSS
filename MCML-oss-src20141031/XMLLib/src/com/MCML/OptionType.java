////////////////////////////////////////////////////////////////////////
//
// OptionType.java
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

package com.MCML;

public class OptionType extends com.altova.xml.TypeBase
{
	public static com.altova.xml.meta.ComplexType getStaticInfo() { return new com.altova.xml.meta.ComplexType(com.MCML.MCML_TypeInfo.binder.getTypes()[com.MCML.MCML_TypeInfo._altova_ti_altova_OptionType]); }
	
	public OptionType(org.w3c.dom.Node init)
	{
		super(init);
		instantiateMembers();
	}

	private void instantiateMembers()
	{
		Key = new MemberAttribute_Key (this, com.MCML.MCML_TypeInfo.binder.getMembers()[com.MCML.MCML_TypeInfo._altova_mi_altova_OptionType._Key]);
		Value2 = new MemberAttribute_Value2 (this, com.MCML.MCML_TypeInfo.binder.getMembers()[com.MCML.MCML_TypeInfo._altova_mi_altova_OptionType._Value2]);

	}
	// Attributes
	public MemberAttribute_Key Key;
		public static class MemberAttribute_Key
		{
			private com.altova.xml.TypeBase owner;
			private com.altova.typeinfo.MemberInfo info; 
			public MemberAttribute_Key (com.altova.xml.TypeBase owner, com.altova.typeinfo.MemberInfo info) {this.owner = owner; this.info = info;}
			public String getValue() {
				return (String)com.altova.xml.XmlTreeOperations.castToString(com.altova.xml.XmlTreeOperations.findAttribute(owner.getNode(), info), info);
			}
			public void setValue(String value) {
				com.altova.xml.XmlTreeOperations.setValue(owner.getNode(), info, value);		
			}
			public boolean exists() {return owner.getAttribute(info) != null;}
			public void remove() {owner.removeAttribute(info);} 
			public com.altova.xml.meta.Attribute getInfo() {return new com.altova.xml.meta.Attribute(info);}

		}
	public MemberAttribute_Value2 Value2;
		public static class MemberAttribute_Value2
		{
			private com.altova.xml.TypeBase owner;
			private com.altova.typeinfo.MemberInfo info; 
			public MemberAttribute_Value2 (com.altova.xml.TypeBase owner, com.altova.typeinfo.MemberInfo info) {this.owner = owner; this.info = info;}
			public String getValue() {
				return (String)com.altova.xml.XmlTreeOperations.castToString(com.altova.xml.XmlTreeOperations.findAttribute(owner.getNode(), info), info);
			}
			public void setValue(String value) {
				com.altova.xml.XmlTreeOperations.setValue(owner.getNode(), info, value);		
			}
			public boolean exists() {return owner.getAttribute(info) != null;}
			public void remove() {owner.removeAttribute(info);} 
			public com.altova.xml.meta.Attribute getInfo() {return new com.altova.xml.meta.Attribute(info);}

		}


	// Elements
}