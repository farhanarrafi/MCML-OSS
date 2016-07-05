/**
 * InputModalityType.java
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

public class InputModalityType extends jp.go.nict.mcml.xml.altova.xml.Node {
	public InputModalityType() {
		super();
	}

	public InputModalityType(InputModalityType node) {
		super(node);
	}

	public InputModalityType(org.w3c.dom.Node node) {
		super(node);
	}

	public InputModalityType(org.w3c.dom.Document doc) {
		super(doc);
	}
	public void adjustPrefix() {
		int count;
		count = getDomChildCount(Element, null, "Speaking");
		for (int i = 0; i < count; i++) {
			org.w3c.dom.Node tmpNode = getDomChildAt(Element, null, "Speaking", i);
			internalAdjustPrefix(tmpNode, true);
			new SpeakingType(tmpNode).adjustPrefix();
		}
		count = getDomChildCount(Element, null, "Writing");
		for (int i = 0; i < count; i++) {
			org.w3c.dom.Node tmpNode = getDomChildAt(Element, null, "Writing", i);
			internalAdjustPrefix(tmpNode, true);
			new WritingType(tmpNode).adjustPrefix();
		}
		count = getDomChildCount(Element, null, "Signing");
		for (int i = 0; i < count; i++) {
			org.w3c.dom.Node tmpNode = getDomChildAt(Element, null, "Signing", i);
			internalAdjustPrefix(tmpNode, true);
			new SigningType(tmpNode).adjustPrefix();
		}
	}

	public int getSpeakingMinCount() {
		return 0;
	}

	public int getSpeakingMaxCount() {
		return 1;
	}

	public int getSpeakingCount() {
		return getDomChildCount(Element, null, "Speaking");
	}

	public boolean hasSpeaking() {
		return hasDomChild(Element, null, "Speaking");
	}

	public SpeakingType getSpeakingAt(int index) throws Exception {
		return new SpeakingType(getDomChildAt(Element, null, "Speaking", index));
	}

	public SpeakingType getSpeaking() throws Exception {
		return getSpeakingAt(0);
	}

	public void removeSpeakingAt(int index) {
		removeDomChildAt(Element, null, "Speaking", index);
	}

	public void removeSpeaking() {
		while (hasSpeaking())
			removeSpeakingAt(0);
	}

	public void addSpeaking(SpeakingType value) {
		appendDomElement(null, "Speaking", value);
	}

	public void insertSpeakingAt(SpeakingType value, int index) {
		insertDomElementAt(null, "Speaking", index, value);
	}

	public void replaceSpeakingAt(SpeakingType value, int index) {
		replaceDomElementAt(null, "Speaking", index, value);
	}

	public int getWritingMinCount() {
		return 0;
	}

	public int getWritingMaxCount() {
		return 1;
	}

	public int getWritingCount() {
		return getDomChildCount(Element, null, "Writing");
	}

	public boolean hasWriting() {
		return hasDomChild(Element, null, "Writing");
	}

	public WritingType getWritingAt(int index) throws Exception {
		return new WritingType(getDomChildAt(Element, null, "Writing", index));
	}

	public WritingType getWriting() throws Exception {
		return getWritingAt(0);
	}

	public void removeWritingAt(int index) {
		removeDomChildAt(Element, null, "Writing", index);
	}

	public void removeWriting() {
		while (hasWriting())
			removeWritingAt(0);
	}

	public void addWriting(WritingType value) {
		appendDomElement(null, "Writing", value);
	}

	public void insertWritingAt(WritingType value, int index) {
		insertDomElementAt(null, "Writing", index, value);
	}

	public void replaceWritingAt(WritingType value, int index) {
		replaceDomElementAt(null, "Writing", index, value);
	}

	public int getSigningMinCount() {
		return 0;
	}

	public int getSigningMaxCount() {
		return 1;
	}

	public int getSigningCount() {
		return getDomChildCount(Element, null, "Signing");
	}

	public boolean hasSigning() {
		return hasDomChild(Element, null, "Signing");
	}

	public SigningType getSigningAt(int index) throws Exception {
		return new SigningType(getDomChildAt(Element, null, "Signing", index));
	}

	public SigningType getSigning() throws Exception {
		return getSigningAt(0);
	}

	public void removeSigningAt(int index) {
		removeDomChildAt(Element, null, "Signing", index);
	}

	public void removeSigning() {
		while (hasSigning())
			removeSigningAt(0);
	}

	public void addSigning(SigningType value) {
		appendDomElement(null, "Signing", value);
	}

	public void insertSigningAt(SigningType value, int index) {
		insertDomElementAt(null, "Signing", index, value);
	}

	public void replaceSigningAt(SigningType value, int index) {
		replaceDomElementAt(null, "Signing", index, value);
	}
}