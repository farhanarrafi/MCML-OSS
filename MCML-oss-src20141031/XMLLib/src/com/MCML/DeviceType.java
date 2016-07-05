////////////////////////////////////////////////////////////////////////
//
// DeviceType.java
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

public class DeviceType extends com.altova.xml.TypeBase
{
	public static com.altova.xml.meta.ComplexType getStaticInfo() { return new com.altova.xml.meta.ComplexType(com.MCML.MCML_TypeInfo.binder.getTypes()[com.MCML.MCML_TypeInfo._altova_ti_altova_DeviceType]); }
	
	public DeviceType(org.w3c.dom.Node init)
	{
		super(init);
		instantiateMembers();
	}

	private void instantiateMembers()
	{

		Location= new MemberElement_Location (this, com.MCML.MCML_TypeInfo.binder.getMembers()[com.MCML.MCML_TypeInfo._altova_mi_altova_DeviceType._Location]);
	}
	// Attributes


	// Elements
	
	public MemberElement_Location Location;

		public static class MemberElement_Location
		{
			public static class MemberElement_Location_Iterator implements java.util.Iterator
			{
				private org.w3c.dom.Node nextNode;
				private MemberElement_Location member;
				public MemberElement_Location_Iterator(MemberElement_Location member) {this.member=member; nextNode=member.owner.getElementFirst(member.info);}
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
					LocationType nx = new LocationType(nextNode);
					nextNode = nextNode.getNextSibling();
					return nx;
				}
				
				public void remove () {}
			}
			protected com.altova.xml.TypeBase owner;
			protected com.altova.typeinfo.MemberInfo info;
			public MemberElement_Location (com.altova.xml.TypeBase owner, com.altova.typeinfo.MemberInfo info) { this.owner = owner; this.info = info;}
			public LocationType at(int index) {return new LocationType(owner.getElementAt(info, index));}
			public LocationType first() {return new LocationType(owner.getElementFirst(info));}
			public LocationType last(){return new LocationType(owner.getElementLast(info));}
			public LocationType append(){return new LocationType(owner.createElement(info));}
			public boolean exists() {return count() > 0;}
			public int count() {return owner.countElement(info);}
			public void remove() {owner.removeElement(info);}
			public void removeAt(int index) {owner.removeElementAt(info, index);}
			public java.util.Iterator iterator() {return new MemberElement_Location_Iterator(this);}
			public com.altova.xml.meta.Element getInfo() { return new com.altova.xml.meta.Element(info); }
		}
}