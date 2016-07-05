/**
 * PronunciationType.java
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

public class PronunciationType extends jp.go.nict.mcml.xml.altova.xml.Node {
	public PronunciationType() {
		super();
	}

	public PronunciationType(PronunciationType node) {
		super(node);
	}

	public PronunciationType(org.w3c.dom.Node node) {
		super(node);
	}

	public PronunciationType(org.w3c.dom.Document doc) {
		super(doc);
	}
	public void adjustPrefix() {
		int count;
		count = getDomChildCount(Attribute, null, "DictionaryID");
		for (int i = 0; i < count; i++) {
			org.w3c.dom.Node tmpNode = getDomChildAt(Attribute, null, "DictionaryID", i);
			internalAdjustPrefix(tmpNode, false);
		}
		count = getDomChildCount(Attribute, null, "EntryID");
		for (int i = 0; i < count; i++) {
			org.w3c.dom.Node tmpNode = getDomChildAt(Attribute, null, "EntryID", i);
			internalAdjustPrefix(tmpNode, false);
		}
	}

	public int getDictionaryIDMinCount() {
		return 0;
	}

	public int getDictionaryIDMaxCount() {
		return 1;
	}

	public int getDictionaryIDCount() {
		return getDomChildCount(Attribute, null, "DictionaryID");
	}

	public boolean hasDictionaryID() {
		return hasDomChild(Attribute, null, "DictionaryID");
	}

	public SchemaString getDictionaryIDAt(int index) throws Exception {
		return new SchemaString(getDomNodeValue(getDomChildAt(Attribute, null, "DictionaryID", index)));
	}

	public SchemaString getDictionaryID() throws Exception {
		return getDictionaryIDAt(0);
	}

	public void removeDictionaryIDAt(int index) {
		removeDomChildAt(Attribute, null, "DictionaryID", index);
	}

	public void removeDictionaryID() {
		while (hasDictionaryID())
			removeDictionaryIDAt(0);
	}

	public void addDictionaryID(SchemaString value) {
		appendDomChild(Attribute, null, "DictionaryID", value.toString());
	}

	public void addDictionaryID(String value) throws Exception {
		addDictionaryID(new SchemaString(value));
	}

	public void insertDictionaryIDAt(SchemaString value, int index) {
		insertDomChildAt(Attribute, null, "DictionaryID", index, value.toString());
	}

	public void insertDictionaryIDAt(String value, int index) throws Exception {
		insertDictionaryIDAt(new SchemaString(value), index);
	}

	public void replaceDictionaryIDAt(SchemaString value, int index) {
		replaceDomChildAt(Attribute, null, "DictionaryID", index, value.toString());
	}

	public void replaceDictionaryIDAt(String value, int index) throws Exception {
		replaceDictionaryIDAt(new SchemaString(value), index);
	}

	public int getEntryIDMinCount() {
		return 0;
	}

	public int getEntryIDMaxCount() {
		return 1;
	}

	public int getEntryIDCount() {
		return getDomChildCount(Attribute, null, "EntryID");
	}

	public boolean hasEntryID() {
		return hasDomChild(Attribute, null, "EntryID");
	}

	public SchemaString getEntryIDAt(int index) throws Exception {
		return new SchemaString(getDomNodeValue(getDomChildAt(Attribute, null, "EntryID", index)));
	}

	public SchemaString getEntryID() throws Exception {
		return getEntryIDAt(0);
	}

	public void removeEntryIDAt(int index) {
		removeDomChildAt(Attribute, null, "EntryID", index);
	}

	public void removeEntryID() {
		while (hasEntryID())
			removeEntryIDAt(0);
	}

	public void addEntryID(SchemaString value) {
		appendDomChild(Attribute, null, "EntryID", value.toString());
	}

	public void addEntryID(String value) throws Exception {
		addEntryID(new SchemaString(value));
	}

	public void insertEntryIDAt(SchemaString value, int index) {
		insertDomChildAt(Attribute, null, "EntryID", index, value.toString());
	}

	public void insertEntryIDAt(String value, int index) throws Exception {
		insertEntryIDAt(new SchemaString(value), index);
	}

	public void replaceEntryIDAt(SchemaString value, int index) {
		replaceDomChildAt(Attribute, null, "EntryID", index, value.toString());
	}

	public void replaceEntryIDAt(String value, int index) throws Exception {
		replaceEntryIDAt(new SchemaString(value), index);
	}
}
