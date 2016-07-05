/**
 * LocationType.java
 *
 * This file was generated by XMLSPY 2004 Enterprise Edition.
 *
 * YOU SHOULD NOT MODIFY THIS FILE, BECAUSE IT WILL BE
 * OVERWRITTEN WHEN YOU RE-RUN CODE GENERATION.
 *
 * Refer to the XMLSPY Documentation for further details.
 * http://www.altova.com/xmlspy
 */


package jp.go.nict.mcml.xml.types;

import jp.go.nict.mcml.xml.altova.types.*;

public class LocationType extends jp.go.nict.mcml.xml.altova.xml.Node {
	public LocationType() {
		super();
	}

	public LocationType(LocationType node) {
		super(node);
	}

	public LocationType(org.w3c.dom.Node node) {
		super(node);
	}

	public LocationType(org.w3c.dom.Document doc) {
		super(doc);
	}
	public void adjustPrefix() {
		int count;
		count = getDomChildCount(Element, null, "URI");
		for (int i = 0; i < count; i++) {
			org.w3c.dom.Node tmpNode = getDomChildAt(Element, null, "URI", i);
			internalAdjustPrefix(tmpNode, true);
		}
		count = getDomChildCount(Element, null, "GlobalPosition");
		for (int i = 0; i < count; i++) {
			org.w3c.dom.Node tmpNode = getDomChildAt(Element, null, "GlobalPosition", i);
			internalAdjustPrefix(tmpNode, true);
			new GlobalPositionType(tmpNode).adjustPrefix();
		}
	}

	public int getURIMinCount() {
		return 1;
	}

	public int getURIMaxCount() {
		return 1;
	}

	public int getURICount() {
		return getDomChildCount(Element, null, "URI");
	}

	public boolean hasURI() {
		return hasDomChild(Element, null, "URI");
	}

	public SchemaString getURIAt(int index) throws Exception {
		return new SchemaString(getDomNodeValue(getDomChildAt(Element, null, "URI", index)));
	}

	public SchemaString getURI() throws Exception {
		return getURIAt(0);
	}

	public void removeURIAt(int index) {
		removeDomChildAt(Element, null, "URI", index);
	}

	public void removeURI() {
		while (hasURI())
			removeURIAt(0);
	}

	public void addURI(SchemaString value) {
		appendDomChild(Element, null, "URI", value.toString());
	}

	public void addURI(String value) throws Exception {
		addURI(new SchemaString(value));
	}

	public void insertURIAt(SchemaString value, int index) {
		insertDomChildAt(Element, null, "URI", index, value.toString());
	}

	public void insertURIAt(String value, int index) throws Exception {
		insertURIAt(new SchemaString(value), index);
	}

	public void replaceURIAt(SchemaString value, int index) {
		replaceDomChildAt(Element, null, "URI", index, value.toString());
	}

	public void replaceURIAt(String value, int index) throws Exception {
		replaceURIAt(new SchemaString(value), index);
	}

	public int getGlobalPositionMinCount() {
		return 0;
	}

	public int getGlobalPositionMaxCount() {
		return 1;
	}

	public int getGlobalPositionCount() {
		return getDomChildCount(Element, null, "GlobalPosition");
	}

	public boolean hasGlobalPosition() {
		return hasDomChild(Element, null, "GlobalPosition");
	}

	public GlobalPositionType getGlobalPositionAt(int index) throws Exception {
		return new GlobalPositionType(getDomChildAt(Element, null, "GlobalPosition", index));
	}

	public GlobalPositionType getGlobalPosition() throws Exception {
		return getGlobalPositionAt(0);
	}

	public void removeGlobalPositionAt(int index) {
		removeDomChildAt(Element, null, "GlobalPosition", index);
	}

	public void removeGlobalPosition() {
		while (hasGlobalPosition())
			removeGlobalPositionAt(0);
	}

	public void addGlobalPosition(GlobalPositionType value) {
		appendDomElement(null, "GlobalPosition", value);
	}

	public void insertGlobalPositionAt(GlobalPositionType value, int index) {
		insertDomElementAt(null, "GlobalPosition", index, value);
	}

	public void replaceGlobalPositionAt(GlobalPositionType value, int index) {
		replaceDomElementAt(null, "GlobalPosition", index, value);
	}
}