////////////////////////////////////////////////////////////////////////
//
// LocationType.java
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

public class LocationType extends com.altova.xml.TypeBase
{
	public static com.altova.xml.meta.ComplexType getStaticInfo() { return new com.altova.xml.meta.ComplexType(com.Information.InformationTypeInfo.binder.getTypes()[com.Information.InformationTypeInfo._altova_ti_altova_LocationType]); }
	
	public LocationType(org.w3c.dom.Node init)
	{
		super(init);
		instantiateMembers();
	}

	private void instantiateMembers()
	{

		URI= new MemberElement_URI (this, com.Information.InformationTypeInfo.binder.getMembers()[com.Information.InformationTypeInfo._altova_mi_altova_LocationType._URI]);
		GlobalPosition= new MemberElement_GlobalPosition (this, com.Information.InformationTypeInfo.binder.getMembers()[com.Information.InformationTypeInfo._altova_mi_altova_LocationType._GlobalPosition]);
	}
	// Attributes


	// Elements
	
	public MemberElement_URI URI;

		public static class MemberElement_URI
		{
			public static class MemberElement_URI_Iterator implements java.util.Iterator
			{
				private org.w3c.dom.Node nextNode;
				private MemberElement_URI member;
				public MemberElement_URI_Iterator(MemberElement_URI member) {this.member=member; nextNode=member.owner.getElementFirst(member.info);}
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
					URIType nx = new URIType(nextNode);
					nextNode = nextNode.getNextSibling();
					return nx;
				}
				
				public void remove () {}
			}
			protected com.altova.xml.TypeBase owner;
			protected com.altova.typeinfo.MemberInfo info;
			public MemberElement_URI (com.altova.xml.TypeBase owner, com.altova.typeinfo.MemberInfo info) { this.owner = owner; this.info = info;}
			public URIType at(int index) {return new URIType(owner.getElementAt(info, index));}
			public URIType first() {return new URIType(owner.getElementFirst(info));}
			public URIType last(){return new URIType(owner.getElementLast(info));}
			public URIType append(){return new URIType(owner.createElement(info));}
			public boolean exists() {return count() > 0;}
			public int count() {return owner.countElement(info);}
			public void remove() {owner.removeElement(info);}
			public void removeAt(int index) {owner.removeElementAt(info, index);}
			public java.util.Iterator iterator() {return new MemberElement_URI_Iterator(this);}
			public com.altova.xml.meta.Element getInfo() { return new com.altova.xml.meta.Element(info); }
		}
	
	public MemberElement_GlobalPosition GlobalPosition;

		public static class MemberElement_GlobalPosition
		{
			public static class MemberElement_GlobalPosition_Iterator implements java.util.Iterator
			{
				private org.w3c.dom.Node nextNode;
				private MemberElement_GlobalPosition member;
				public MemberElement_GlobalPosition_Iterator(MemberElement_GlobalPosition member) {this.member=member; nextNode=member.owner.getElementFirst(member.info);}
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
					GlobalPositionType nx = new GlobalPositionType(nextNode);
					nextNode = nextNode.getNextSibling();
					return nx;
				}
				
				public void remove () {}
			}
			protected com.altova.xml.TypeBase owner;
			protected com.altova.typeinfo.MemberInfo info;
			public MemberElement_GlobalPosition (com.altova.xml.TypeBase owner, com.altova.typeinfo.MemberInfo info) { this.owner = owner; this.info = info;}
			public GlobalPositionType at(int index) {return new GlobalPositionType(owner.getElementAt(info, index));}
			public GlobalPositionType first() {return new GlobalPositionType(owner.getElementFirst(info));}
			public GlobalPositionType last(){return new GlobalPositionType(owner.getElementLast(info));}
			public GlobalPositionType append(){return new GlobalPositionType(owner.createElement(info));}
			public boolean exists() {return count() > 0;}
			public int count() {return owner.countElement(info);}
			public void remove() {owner.removeElement(info);}
			public void removeAt(int index) {owner.removeElementAt(info, index);}
			public java.util.Iterator iterator() {return new MemberElement_GlobalPosition_Iterator(this);}
			public com.altova.xml.meta.Element getInfo() { return new com.altova.xml.meta.Element(info); }
		}
}