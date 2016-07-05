////////////////////////////////////////////////////////////////////////
//
// OutputType.java
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

public class OutputType extends com.altova.xml.TypeBase
{
	public static com.altova.xml.meta.ComplexType getStaticInfo() { return new com.altova.xml.meta.ComplexType(com.MCML.MCML_TypeInfo.binder.getTypes()[com.MCML.MCML_TypeInfo._altova_ti_altova_OutputType]); }
	
	public OutputType(org.w3c.dom.Node init)
	{
		super(init);
		instantiateMembers();
	}

	private void instantiateMembers()
	{

		Data= new MemberElement_Data (this, com.MCML.MCML_TypeInfo.binder.getMembers()[com.MCML.MCML_TypeInfo._altova_mi_altova_OutputType._Data]);
		AttachedBinary= new MemberElement_AttachedBinary (this, com.MCML.MCML_TypeInfo.binder.getMembers()[com.MCML.MCML_TypeInfo._altova_mi_altova_OutputType._AttachedBinary]);
	}
	// Attributes


	// Elements
	
	public MemberElement_Data Data;

		public static class MemberElement_Data
		{
			public static class MemberElement_Data_Iterator implements java.util.Iterator
			{
				private org.w3c.dom.Node nextNode;
				private MemberElement_Data member;
				public MemberElement_Data_Iterator(MemberElement_Data member) {this.member=member; nextNode=member.owner.getElementFirst(member.info);}
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
					DataType nx = new DataType(nextNode);
					nextNode = nextNode.getNextSibling();
					return nx;
				}
				
				public void remove () {}
			}
			protected com.altova.xml.TypeBase owner;
			protected com.altova.typeinfo.MemberInfo info;
			public MemberElement_Data (com.altova.xml.TypeBase owner, com.altova.typeinfo.MemberInfo info) { this.owner = owner; this.info = info;}
			public DataType at(int index) {return new DataType(owner.getElementAt(info, index));}
			public DataType first() {return new DataType(owner.getElementFirst(info));}
			public DataType last(){return new DataType(owner.getElementLast(info));}
			public DataType append(){return new DataType(owner.createElement(info));}
			public boolean exists() {return count() > 0;}
			public int count() {return owner.countElement(info);}
			public void remove() {owner.removeElement(info);}
			public void removeAt(int index) {owner.removeElementAt(info, index);}
			public java.util.Iterator iterator() {return new MemberElement_Data_Iterator(this);}
			public com.altova.xml.meta.Element getInfo() { return new com.altova.xml.meta.Element(info); }
		}
	
	public MemberElement_AttachedBinary AttachedBinary;

		public static class MemberElement_AttachedBinary
		{
			public static class MemberElement_AttachedBinary_Iterator implements java.util.Iterator
			{
				private org.w3c.dom.Node nextNode;
				private MemberElement_AttachedBinary member;
				public MemberElement_AttachedBinary_Iterator(MemberElement_AttachedBinary member) {this.member=member; nextNode=member.owner.getElementFirst(member.info);}
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
					AttachedBinaryType nx = new AttachedBinaryType(nextNode);
					nextNode = nextNode.getNextSibling();
					return nx;
				}
				
				public void remove () {}
			}
			protected com.altova.xml.TypeBase owner;
			protected com.altova.typeinfo.MemberInfo info;
			public MemberElement_AttachedBinary (com.altova.xml.TypeBase owner, com.altova.typeinfo.MemberInfo info) { this.owner = owner; this.info = info;}
			public AttachedBinaryType at(int index) {return new AttachedBinaryType(owner.getElementAt(info, index));}
			public AttachedBinaryType first() {return new AttachedBinaryType(owner.getElementFirst(info));}
			public AttachedBinaryType last(){return new AttachedBinaryType(owner.getElementLast(info));}
			public AttachedBinaryType append(){return new AttachedBinaryType(owner.createElement(info));}
			public boolean exists() {return count() > 0;}
			public int count() {return owner.countElement(info);}
			public void remove() {owner.removeElement(info);}
			public void removeAt(int index) {owner.removeElementAt(info, index);}
			public java.util.Iterator iterator() {return new MemberElement_AttachedBinary_Iterator(this);}
			public com.altova.xml.meta.Element getInfo() { return new com.altova.xml.meta.Element(info); }
		}
}
