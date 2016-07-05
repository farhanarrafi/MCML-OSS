////////////////////////////////////////////////////////////////////////
//
// UserProfileType.java
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

public class UserProfileType extends com.altova.xml.TypeBase
{
	public static com.altova.xml.meta.ComplexType getStaticInfo() { return new com.altova.xml.meta.ComplexType(com.MCML.MCML_TypeInfo.binder.getTypes()[com.MCML.MCML_TypeInfo._altova_ti_altova_UserProfileType]); }
	
	public UserProfileType(org.w3c.dom.Node init)
	{
		super(init);
		instantiateMembers();
	}

	private void instantiateMembers()
	{
		ID = new MemberAttribute_ID (this, com.MCML.MCML_TypeInfo.binder.getMembers()[com.MCML.MCML_TypeInfo._altova_mi_altova_UserProfileType._ID]);
		Age = new MemberAttribute_Age (this, com.MCML.MCML_TypeInfo.binder.getMembers()[com.MCML.MCML_TypeInfo._altova_mi_altova_UserProfileType._Age]);
		Gender = new MemberAttribute_Gender (this, com.MCML.MCML_TypeInfo.binder.getMembers()[com.MCML.MCML_TypeInfo._altova_mi_altova_UserProfileType._Gender]);
		Email = new MemberAttribute_Email (this, com.MCML.MCML_TypeInfo.binder.getMembers()[com.MCML.MCML_TypeInfo._altova_mi_altova_UserProfileType._Email]);
		Password = new MemberAttribute_Password (this, com.MCML.MCML_TypeInfo.binder.getMembers()[com.MCML.MCML_TypeInfo._altova_mi_altova_UserProfileType._Password]);
		AccessCode = new MemberAttribute_AccessCode (this, com.MCML.MCML_TypeInfo.binder.getMembers()[com.MCML.MCML_TypeInfo._altova_mi_altova_UserProfileType._AccessCode]);

	}
	// Attributes
	public MemberAttribute_ID ID;
		public static class MemberAttribute_ID
		{
			private com.altova.xml.TypeBase owner;
			private com.altova.typeinfo.MemberInfo info; 
			public MemberAttribute_ID (com.altova.xml.TypeBase owner, com.altova.typeinfo.MemberInfo info) {this.owner = owner; this.info = info;}
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
	public MemberAttribute_Age Age;
		public static class MemberAttribute_Age
		{
			private com.altova.xml.TypeBase owner;
			private com.altova.typeinfo.MemberInfo info; 
			public MemberAttribute_Age (com.altova.xml.TypeBase owner, com.altova.typeinfo.MemberInfo info) {this.owner = owner; this.info = info;}
			public int getValue() {
				return (int)com.altova.xml.XmlTreeOperations.castToInt(com.altova.xml.XmlTreeOperations.findAttribute(owner.getNode(), info), info);
			}
			public void setValue(int value) {
				com.altova.xml.XmlTreeOperations.setValue(owner.getNode(), info, value);		
			}
			public boolean exists() {return owner.getAttribute(info) != null;}
			public void remove() {owner.removeAttribute(info);} 
			public com.altova.xml.meta.Attribute getInfo() {return new com.altova.xml.meta.Attribute(info);}

		}
	public MemberAttribute_Gender Gender;
		public static class MemberAttribute_Gender
		{
			private com.altova.xml.TypeBase owner;
			private com.altova.typeinfo.MemberInfo info; 
			public MemberAttribute_Gender (com.altova.xml.TypeBase owner, com.altova.typeinfo.MemberInfo info) {this.owner = owner; this.info = info;}
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
	public MemberAttribute_Email Email;
		public static class MemberAttribute_Email
		{
			private com.altova.xml.TypeBase owner;
			private com.altova.typeinfo.MemberInfo info; 
			public MemberAttribute_Email (com.altova.xml.TypeBase owner, com.altova.typeinfo.MemberInfo info) {this.owner = owner; this.info = info;}
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
	public MemberAttribute_Password Password;
		public static class MemberAttribute_Password
		{
			private com.altova.xml.TypeBase owner;
			private com.altova.typeinfo.MemberInfo info; 
			public MemberAttribute_Password (com.altova.xml.TypeBase owner, com.altova.typeinfo.MemberInfo info) {this.owner = owner; this.info = info;}
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
	public MemberAttribute_AccessCode AccessCode;
		public static class MemberAttribute_AccessCode
		{
			private com.altova.xml.TypeBase owner;
			private com.altova.typeinfo.MemberInfo info; 
			public MemberAttribute_AccessCode (com.altova.xml.TypeBase owner, com.altova.typeinfo.MemberInfo info) {this.owner = owner; this.info = info;}
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
