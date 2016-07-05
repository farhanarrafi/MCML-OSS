////////////////////////////////////////////////////////////////////////
//
// RoutingType.java
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

public class RoutingType extends com.altova.xml.TypeBase
{
	public static com.altova.xml.meta.ComplexType getStaticInfo() { return new com.altova.xml.meta.ComplexType(com.MCML.MCML_TypeInfo.binder.getTypes()[com.MCML.MCML_TypeInfo._altova_ti_altova_RoutingType]); }
	
	public RoutingType(org.w3c.dom.Node init)
	{
		super(init);
		instantiateMembers();
	}

	private void instantiateMembers()
	{

		From= new MemberElement_From (this, com.MCML.MCML_TypeInfo.binder.getMembers()[com.MCML.MCML_TypeInfo._altova_mi_altova_RoutingType._From]);
		To= new MemberElement_To (this, com.MCML.MCML_TypeInfo.binder.getMembers()[com.MCML.MCML_TypeInfo._altova_mi_altova_RoutingType._To]);
	}
	// Attributes


	// Elements
	
	public MemberElement_From From;

		public static class MemberElement_From
		{
			public static class MemberElement_From_Iterator implements java.util.Iterator
			{
				private org.w3c.dom.Node nextNode;
				private MemberElement_From member;
				public MemberElement_From_Iterator(MemberElement_From member) {this.member=member; nextNode=member.owner.getElementFirst(member.info);}
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
					FromType nx = new FromType(nextNode);
					nextNode = nextNode.getNextSibling();
					return nx;
				}
				
				public void remove () {}
			}
			protected com.altova.xml.TypeBase owner;
			protected com.altova.typeinfo.MemberInfo info;
			public MemberElement_From (com.altova.xml.TypeBase owner, com.altova.typeinfo.MemberInfo info) { this.owner = owner; this.info = info;}
			public FromType at(int index) {return new FromType(owner.getElementAt(info, index));}
			public FromType first() {return new FromType(owner.getElementFirst(info));}
			public FromType last(){return new FromType(owner.getElementLast(info));}
			public FromType append(){return new FromType(owner.createElement(info));}
			public boolean exists() {return count() > 0;}
			public int count() {return owner.countElement(info);}
			public void remove() {owner.removeElement(info);}
			public void removeAt(int index) {owner.removeElementAt(info, index);}
			public java.util.Iterator iterator() {return new MemberElement_From_Iterator(this);}
			public com.altova.xml.meta.Element getInfo() { return new com.altova.xml.meta.Element(info); }
		}
	
	public MemberElement_To To;

		public static class MemberElement_To
		{
			public static class MemberElement_To_Iterator implements java.util.Iterator
			{
				private org.w3c.dom.Node nextNode;
				private MemberElement_To member;
				public MemberElement_To_Iterator(MemberElement_To member) {this.member=member; nextNode=member.owner.getElementFirst(member.info);}
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
					ToType nx = new ToType(nextNode);
					nextNode = nextNode.getNextSibling();
					return nx;
				}
				
				public void remove () {}
			}
			protected com.altova.xml.TypeBase owner;
			protected com.altova.typeinfo.MemberInfo info;
			public MemberElement_To (com.altova.xml.TypeBase owner, com.altova.typeinfo.MemberInfo info) { this.owner = owner; this.info = info;}
			public ToType at(int index) {return new ToType(owner.getElementAt(info, index));}
			public ToType first() {return new ToType(owner.getElementFirst(info));}
			public ToType last(){return new ToType(owner.getElementLast(info));}
			public ToType append(){return new ToType(owner.createElement(info));}
			public boolean exists() {return count() > 0;}
			public int count() {return owner.countElement(info);}
			public void remove() {owner.removeElement(info);}
			public void removeAt(int index) {owner.removeElementAt(info, index);}
			public java.util.Iterator iterator() {return new MemberElement_To_Iterator(this);}
			public com.altova.xml.meta.Element getInfo() { return new com.altova.xml.meta.Element(info); }
		}
}
