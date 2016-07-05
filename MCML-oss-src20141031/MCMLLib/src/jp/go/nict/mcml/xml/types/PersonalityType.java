/**
 * PersonalityType.java
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

public class PersonalityType extends jp.go.nict.mcml.xml.altova.xml.Node {
	public PersonalityType() {
		super();
	}

	public PersonalityType(PersonalityType node) {
		super(node);
	}

	public PersonalityType(org.w3c.dom.Node node) {
		super(node);
	}

	public PersonalityType(org.w3c.dom.Document doc) {
		super(doc);
	}
	public void adjustPrefix() {
		int count;
		count = getDomChildCount(Attribute, null, "ID");
		for (int i = 0; i < count; i++) {
			org.w3c.dom.Node tmpNode = getDomChildAt(Attribute, null, "ID", i);
			internalAdjustPrefix(tmpNode, false);
		}
		count = getDomChildCount(Attribute, null, "Age");
		for (int i = 0; i < count; i++) {
			org.w3c.dom.Node tmpNode = getDomChildAt(Attribute, null, "Age", i);
			internalAdjustPrefix(tmpNode, false);
		}
		count = getDomChildCount(Attribute, null, "Gender");
		for (int i = 0; i < count; i++) {
			org.w3c.dom.Node tmpNode = getDomChildAt(Attribute, null, "Gender", i);
			internalAdjustPrefix(tmpNode, false);
		}
	}

	public int getIDMinCount() {
		return 1;
	}

	public int getIDMaxCount() {
		return 1;
	}

	public int getIDCount() {
		return getDomChildCount(Attribute, null, "ID");
	}

	public boolean hasID() {
		return hasDomChild(Attribute, null, "ID");
	}

	public SchemaString getIDAt(int index) throws Exception {
		return new SchemaString(getDomNodeValue(getDomChildAt(Attribute, null, "ID", index)));
	}

	public SchemaString getID() throws Exception {
		return getIDAt(0);
	}

	public void removeIDAt(int index) {
		removeDomChildAt(Attribute, null, "ID", index);
	}

	public void removeID() {
		while (hasID())
			removeIDAt(0);
	}

	public void addID(SchemaString value) {
		appendDomChild(Attribute, null, "ID", value.toString());
	}

	public void addID(String value) throws Exception {
		addID(new SchemaString(value));
	}

	public void insertIDAt(SchemaString value, int index) {
		insertDomChildAt(Attribute, null, "ID", index, value.toString());
	}

	public void insertIDAt(String value, int index) throws Exception {
		insertIDAt(new SchemaString(value), index);
	}

	public void replaceIDAt(SchemaString value, int index) {
		replaceDomChildAt(Attribute, null, "ID", index, value.toString());
	}

	public void replaceIDAt(String value, int index) throws Exception {
		replaceIDAt(new SchemaString(value), index);
	}

	public int getAgeMinCount() {
		return 1;
	}

	public int getAgeMaxCount() {
		return 1;
	}

	public int getAgeCount() {
		return getDomChildCount(Attribute, null, "Age");
	}

	public boolean hasAge() {
		return hasDomChild(Attribute, null, "Age");
	}

	public SchemaInt getAgeAt(int index) throws Exception {
		return new SchemaInt(getDomNodeValue(getDomChildAt(Attribute, null, "Age", index)));
	}

	public SchemaInt getAge() throws Exception {
		return getAgeAt(0);
	}

	public void removeAgeAt(int index) {
		removeDomChildAt(Attribute, null, "Age", index);
	}

	public void removeAge() {
		while (hasAge())
			removeAgeAt(0);
	}

	public void addAge(SchemaInt value) {
		appendDomChild(Attribute, null, "Age", value.toString());
	}

	public void addAge(String value) throws Exception {
		addAge(new SchemaInt(value));
	}

	public void insertAgeAt(SchemaInt value, int index) {
		insertDomChildAt(Attribute, null, "Age", index, value.toString());
	}

	public void insertAgeAt(String value, int index) throws Exception {
		insertAgeAt(new SchemaInt(value), index);
	}

	public void replaceAgeAt(SchemaInt value, int index) {
		replaceDomChildAt(Attribute, null, "Age", index, value.toString());
	}

	public void replaceAgeAt(String value, int index) throws Exception {
		replaceAgeAt(new SchemaInt(value), index);
	}

	public int getGenderMinCount() {
		return 1;
	}

	public int getGenderMaxCount() {
		return 1;
	}

	public int getGenderCount() {
		return getDomChildCount(Attribute, null, "Gender");
	}

	public boolean hasGender() {
		return hasDomChild(Attribute, null, "Gender");
	}

	public SchemaString getGenderAt(int index) throws Exception {
		return new SchemaString(getDomNodeValue(getDomChildAt(Attribute, null, "Gender", index)));
	}

	public SchemaString getGender() throws Exception {
		return getGenderAt(0);
	}

	public void removeGenderAt(int index) {
		removeDomChildAt(Attribute, null, "Gender", index);
	}

	public void removeGender() {
		while (hasGender())
			removeGenderAt(0);
	}

	public void addGender(SchemaString value) {
		appendDomChild(Attribute, null, "Gender", value.toString());
	}

	public void addGender(String value) throws Exception {
		addGender(new SchemaString(value));
	}

	public void insertGenderAt(SchemaString value, int index) {
		insertDomChildAt(Attribute, null, "Gender", index, value.toString());
	}

	public void insertGenderAt(String value, int index) throws Exception {
		insertGenderAt(new SchemaString(value), index);
	}

	public void replaceGenderAt(SchemaString value, int index) {
		replaceDomChildAt(Attribute, null, "Gender", index, value.toString());
	}

	public void replaceGenderAt(String value, int index) throws Exception {
		replaceGenderAt(new SchemaString(value), index);
	}
}