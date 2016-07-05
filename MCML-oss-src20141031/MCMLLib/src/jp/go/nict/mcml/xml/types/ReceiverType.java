/**
 * ReceiverType.java
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

public class ReceiverType extends jp.go.nict.mcml.xml.altova.xml.Node {
	public ReceiverType() {
		super();
	}

	public ReceiverType(ReceiverType node) {
		super(node);
	}

	public ReceiverType(org.w3c.dom.Node node) {
		super(node);
	}

	public ReceiverType(org.w3c.dom.Document doc) {
		super(doc);
	}
	public void adjustPrefix() {
		int count;
		count = getDomChildCount(Element, null, "Device");
		for (int i = 0; i < count; i++) {
			org.w3c.dom.Node tmpNode = getDomChildAt(Element, null, "Device", i);
			internalAdjustPrefix(tmpNode, true);
			new DeviceType(tmpNode).adjustPrefix();
		}
		count = getDomChildCount(Element, null, "UserProfile");
		for (int i = 0; i < count; i++) {
			org.w3c.dom.Node tmpNode = getDomChildAt(Element, null, "UserProfile", i);
			internalAdjustPrefix(tmpNode, true);
			new UserProfileType(tmpNode).adjustPrefix();
		}
	}

	public int getDeviceMinCount() {
		return 1;
	}

	public int getDeviceMaxCount() {
		return 1;
	}

	public int getDeviceCount() {
		return getDomChildCount(Element, null, "Device");
	}

	public boolean hasDevice() {
		return hasDomChild(Element, null, "Device");
	}

	public DeviceType getDeviceAt(int index) throws Exception {
		return new DeviceType(getDomChildAt(Element, null, "Device", index));
	}

	public DeviceType getDevice() throws Exception {
		return getDeviceAt(0);
	}

	public void removeDeviceAt(int index) {
		removeDomChildAt(Element, null, "Device", index);
	}

	public void removeDevice() {
		while (hasDevice())
			removeDeviceAt(0);
	}

	public void addDevice(DeviceType value) {
		appendDomElement(null, "Device", value);
	}

	public void insertDeviceAt(DeviceType value, int index) {
		insertDomElementAt(null, "Device", index, value);
	}

	public void replaceDeviceAt(DeviceType value, int index) {
		replaceDomElementAt(null, "Device", index, value);
	}

	public int getUserProfileMinCount() {
		return 1;
	}

	public int getUserProfileMaxCount() {
		return Integer.MAX_VALUE;
	}

	public int getUserProfileCount() {
		return getDomChildCount(Element, null, "UserProfile");
	}

	public boolean hasUserProfile() {
		return hasDomChild(Element, null, "UserProfile");
	}

	public UserProfileType getUserProfileAt(int index) throws Exception {
		return new UserProfileType(getDomChildAt(Element, null, "UserProfile", index));
	}

	public UserProfileType getUserProfile() throws Exception {
		return getUserProfileAt(0);
	}

	public void removeUserProfileAt(int index) {
		removeDomChildAt(Element, null, "UserProfile", index);
	}

	public void removeUserProfile() {
		while (hasUserProfile())
			removeUserProfileAt(0);
	}

	public void addUserProfile(UserProfileType value) {
		appendDomElement(null, "UserProfile", value);
	}

	public void insertUserProfileAt(UserProfileType value, int index) {
		insertDomElementAt(null, "UserProfile", index, value);
	}

	public void replaceUserProfileAt(UserProfileType value, int index) {
		replaceDomElementAt(null, "UserProfile", index, value);
	}
}