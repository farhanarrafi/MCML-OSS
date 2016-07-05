// TypeBase.java 
// This file contains generated code and will be overwritten when you rerun code generation.

package com.altova.xml;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import com.altova.typeinfo.MemberInfo;

public class TypeBase
{
	protected Node node;

	public TypeBase(Node node)
	{
		this.node = node;
	}

	public Node getNode()
	{
		return node;
	}

	public Node getAttribute(MemberInfo member)
	{
		Element element = (Element) node;
		if (member.getLocalName() == null)
		{
			for (Node child = element.getFirstChild(); child != null; child = child.getNextSibling())
			{
				if (child.getNodeType() == Node.TEXT_NODE || child.getNodeType() == Node.CDATA_SECTION_NODE)
					return child;
			}
			return null;
		}
		else
			return element.getAttributeNodeNS(member.getNamespaceURI(), member.getLocalName());
	}

	public Node createAttribute(MemberInfo member)
	{
		Element element = (Element) node;
		if (member.getLocalName() == null)
		{
			Node existing = getAttribute(member);
			if (existing == null)
			{
				Text text = element.getOwnerDocument().createTextNode("");
				element.appendChild(text);
				return text;
			}
			return existing;
		}
		else
		{
			Attr att = element.getOwnerDocument().createAttributeNS(member.getNamespaceURI(), member.getLocalName());
			element.setAttributeNode(att);
			return att;
		}
	}

	public void removeAttribute(MemberInfo member)
	{
		Element element = (Element) node;
		if (member.getLocalName() == null)
		{
			for (Node child = element.getFirstChild(); child != null;)
			{
				Node keep = child;
				child = child.getNextSibling();
				if (keep.getNodeType() == Node.TEXT_NODE || keep.getNodeType() == Node.CDATA_SECTION_NODE)
					element.removeChild(keep);
			}
		}
		else
		{
			element.removeAttributeNS(member.getNamespaceURI(), member.getLocalName());
		}
	}

	public Node getElementFirst(MemberInfo member)
	{
		return getElementAt(member, 0);
	}

	public Node getElementAt(MemberInfo member, int index)
	{
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling())
		{
			if (memberEqualsNode(member, child))
			{
				if (index-- == 0)
					return child;
			}
		}
		return null;
	}

	public Node getElementLast(MemberInfo member)
	{
		for (Node child = node.getLastChild(); child != null; child = child.getPreviousSibling())
		{
			if (memberEqualsNode(member, child))
			{
				return child;
			}
		}
		return null;
	}

	public Node createElement(MemberInfo member)
	{
		Document doc = node.getOwnerDocument();
		if (doc == null)
			doc = (Document) node;
		Node child = doc.createElementNS(member.getNamespaceURI(), member.getLocalName());
		node.appendChild(child);
		return child;
	}

	public int countElement(MemberInfo member)
	{
		int count = 0;
		for (Node child=node.getFirstChild(); child != null; child = child.getNextSibling())
		{
			if (memberEqualsNode(member, child))
				++count;
		}
		return count;
	}

	public void removeElement(MemberInfo member)
	{
		for (Node child = node.getFirstChild(); child != null;)
		{
			Node keep = child;
			child = child.getNextSibling();
			if (memberEqualsNode(member, keep))
				node.removeChild(keep);
		}
	}
	
	public void removeElementAt(MemberInfo member, int index)
	{
		for (Node child = node.getFirstChild(); child != null;)
		{
			Node keep = child;
			child = child.getNextSibling();
			if (memberEqualsNode(member, keep) && index-- == 0)
				node.removeChild(keep);
		}
	}
	
	public static boolean memberEqualsNode(MemberInfo member, Node node)
	{
		if (node == null)
			return false;
		String nodeURI = node.getNamespaceURI() == null ? "" : node.getNamespaceURI();
		String nodeLocalName = node.getLocalName() == null ? "" : node.getLocalName();
		String memberURI = member.getNamespaceURI() == null ? "" : member.getNamespaceURI();
		String memberLocalName = member.getLocalName() == null ? "" : member.getLocalName();
				
		if (nodeURI.equals(memberURI) && nodeLocalName.equals(memberLocalName))
			return true;
		return false;
	}
	
	public static int getEnumerationIndex( MemberInfo member, String value, int enumOffset, int enumCount)
	{
		for( int i = enumOffset; i < (enumOffset + enumCount); i++)
		{
			if( member.getDataType().facets[i].facetName.equals( "enumeration") &&
				member.getDataType().facets[i].stringValue.equals( value) )
					return i - enumOffset;
		}
		
		return -1;
	}
}
