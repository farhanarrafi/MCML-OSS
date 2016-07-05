// Copyright 2013, NICT
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are
// met:
//
//     * Redistributions of source code must retain the above copyright
// notice, this list of conditions and the following disclaimer.
//     * Redistributions in binary form must reproduce the above
// copyright notice, this list of conditions and the following disclaimer
// in the documentation and/or other materials provided with the
// distribution.
//     * Neither the name of NICT nor the names of its
// contributors may be used to endorse or promote products derived from
// this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package jp.go.nict.mcml.util;

import java.util.Calendar;

import jp.go.nict.common.util.android.Information;
import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

/**
 * SDK version management
 * 
 * @version 2.15
 */
public class CreateID {
    // Translation request type code
    /** SR results */
    public static final int REQUEST_TYPE_SR_RESULT = 1; // When translating speech recognition results.
    /** History of use */
    public static final int REQUEST_TYPE_HISTORY_TRANSLATION = 2; // When translating using history of use.
    /** Back translation */
    public static final int REQUEST_TYPE_REVERSE_TRANSLATION = 3; // When performing back translation
    /** Collective multiple translations */
    public static final int REQUEST_TYPE_ONETIME_TRANSLATION = 4; // When translating multiple languages collectively
    /**  Text input translation */
    public static final int REQUEST_TYPE_EDIT_TRANSLATION = 5; // When translating text inputs
    /** Other than translation */
    public static final int REQUEST_TYPE_NOTTRANSLATE = 6; // For requests other than translation

    // Speech recognition control type code
    /** Button */
    public static final int CONTROL_TYPE_BUTTON = 1; // Speech input by button operation
    /** Proximity sensor */
    public static final int CONTROL_TYPE_SENSOR = 2; // Speech input by proximity sensor
    /** Other than above  */
    public static final int CONTROL_TYPE_OTHER = 3; // Other than above

    // Request type character
    private static final String REQUEST_TYPE_SR_RESULT_SYMBOL = "T";
    private static final String REQUEST_TYPE_HISTORY_TRANSLATION_SYMBOL = "H";
    private static final String REQUEST_TYPE_REVERSE_TRANSLATION_SYMBOL = "R";
    private static final String REQUEST_TYPE_ONETIME_TRANSLATION_SYMBOL = "O";
    private static final String REQUEST_TYPE_UNKNOWN_SYMBOL = "U";
    private static final String REQUEST_TYPE_EDIT_TRANSLATION_SYMBOL = "E";
    private static final String REQUEST_TYPE_NOTTRANSLATE_SYMBOL = "N";

    // Control type character
    private static final String CONTROL_TYPE_BUTTON_SYMBOL = "B";
    private static final String CONTROL_TYPE_SENSOR_SYMBOL = "S";
    private static final String CONTROL_TYPE_OTHER_SYMBOL = "N";
    private static final String CONTROL_TYPE_UNKNOWN_SYMBOL = "U";

    // OS type character
    private static final String OS_ANDROID = "A";

    // ID type character
    private static final String ID_SOURCE_IMEI = "I";
    private static final String ID_SOURCE_MACADDRESS = "M";
    private static final String ID_SOURCE_TIMEID = "T";

    // Objective type character
    private static final String PURPOSE_DEMO = "D";
    private static final String PURPOSE_PUBLIC = "P";

    // Network type character
    private static final String NETWORK_WIFI = "W";
    private static final String NETWORK_3G = "G";

    // UserID value with default setting
    private String mApplicationName = "UU";
    private String mRequestPrefix = "U";
    private String mPurpose = "U";
    private String mUserIDSource = "U";
    private String mApplicationVersion = "0000";
    private String mOS = "U";
    private String mOSVersion = "0000";
    private String mInputController = "U";
    private String mNetwork = "U";
    private String mSDKVersion = "0000";
    private String mOrganizationID = "##########";
    private String mReserve = "####################";
    private String mID = "";
    private String mTerminalName = "";

    // Activities to get terminal information
    private Activity mActivity = null;

    private String msFixedTimeID = null;

    /**
     * Constructor. Initializes values that are fixed thereafter.
     * 
     * @param activity
     *            Gets activities to obtain terminal information.
     * @param sOrganizationName
     *            Organization ID: within 10 one-byte characters.
     * @param sApplicationName
     *            Application name: 2 one-byte characters
     * @param isDistribution
     *            true: Distribution version false: Demo version
     * @param iVersion
     *            Specifies version number between 0 and 9999.
     */
    public CreateID(Activity activity, String sOrganizationName,
            String sApplicationName, boolean isDistribution, int iVersion) {
        this(activity, sOrganizationName, sApplicationName, isDistribution,
                iVersion, null);
    }

    /**
     * Constructor. Initializes values that are fixed thereafter.
     * 
     * @param activity
     *            Gets Activity to obtain terminal information.
     * @param sOrganizationName
     *            Organization ID: within 10 one-byte characters.
     * @param sApplicationName
     *            Application name: 2 one-byte characters
     * @param isDistribution
     *            true:Distribution version false: Demo version
     * @param iVersion
     *            Specifies version number between 0 and 9999.
     * @param sFixedTimeID
     *            If there already exists a fixed time ID, specify it.
     */
    public CreateID(Activity activity, String sOrganizationName,
            String sApplicationName, boolean isDistribution, int iVersion,
            String sFixedTimeID) {
        mActivity = activity;

        // set ID
        if (sFixedTimeID == null) {
            String sIMEI = getIMEI();
            String sMacAddress = getMacAddress();
            String sTimeID = getTimeID();

            if (sIMEI != null) {
                mUserIDSource = ID_SOURCE_IMEI;
                mID = sIMEI;
            } else if (sMacAddress != null) {
                mUserIDSource = ID_SOURCE_MACADDRESS;
                mID = sMacAddress;
            } else if (sTimeID != null) {
                mUserIDSource = ID_SOURCE_TIMEID;
                mID = sTimeID;
                msFixedTimeID = sTimeID;
            }
        } else {
            mUserIDSource = ID_SOURCE_TIMEID;
            mID = sFixedTimeID;
            msFixedTimeID = sFixedTimeID;
        }

        // set OS
        mOS = OS_ANDROID;
        mOSVersion = getOSVersion();

        // set Network
        mNetwork = getNetworkType();

        // set SDKVersion
        mSDKVersion = getSDKVersion();

        // set TerminalName
        mTerminalName = getTerminalName();

        // set OrganizationName
        setOrganizationID(sOrganizationName);

        // set Application Info
        setApplicationName(sApplicationName);
        mApplicationVersion = getApplicationVersion(iVersion);

        // set purpose
        if (isDistribution) {
            mPurpose = PURPOSE_PUBLIC;
        } else {
            mPurpose = PURPOSE_DEMO;
        }
    }

    /**
     * Gets UserID.
     * 
     * @param requestType
     *            Request type code
     * @param controlType
     *           Control type code
     * @return UserID
     */
    public String getUserID(int requestType, int controlType) {
        // set Network
        mNetwork = getNetworkType();

        // set Request Type
        switch (requestType) {
        case REQUEST_TYPE_SR_RESULT:
            mRequestPrefix = REQUEST_TYPE_SR_RESULT_SYMBOL;
            break;
        case REQUEST_TYPE_HISTORY_TRANSLATION:
            mRequestPrefix = REQUEST_TYPE_HISTORY_TRANSLATION_SYMBOL;
            break;
        case REQUEST_TYPE_REVERSE_TRANSLATION:
            mRequestPrefix = REQUEST_TYPE_REVERSE_TRANSLATION_SYMBOL;
            break;
        case REQUEST_TYPE_ONETIME_TRANSLATION:
            mRequestPrefix = REQUEST_TYPE_ONETIME_TRANSLATION_SYMBOL;
            break;
        case REQUEST_TYPE_EDIT_TRANSLATION:
            mRequestPrefix = REQUEST_TYPE_EDIT_TRANSLATION_SYMBOL;
            break;
        case REQUEST_TYPE_NOTTRANSLATE:
            mRequestPrefix = REQUEST_TYPE_NOTTRANSLATE_SYMBOL;
            break;
        default:
            mRequestPrefix = REQUEST_TYPE_UNKNOWN_SYMBOL;
            break;
        }

        // set Control Type
        switch (controlType) {
        case CONTROL_TYPE_BUTTON:
            mInputController = CONTROL_TYPE_BUTTON_SYMBOL;
            break;
        case CONTROL_TYPE_SENSOR:
            mInputController = CONTROL_TYPE_SENSOR_SYMBOL;
            break;
        case CONTROL_TYPE_OTHER:
            mInputController = CONTROL_TYPE_OTHER_SYMBOL;
            break;
        default:
            mInputController = CONTROL_TYPE_UNKNOWN_SYMBOL;
            break;
        }

        String sUserID = "";
        sUserID += mApplicationName; // 1-2
        sUserID += mRequestPrefix; // 3
        sUserID += mPurpose; // 4
        sUserID += mUserIDSource; // 5
        sUserID += mApplicationVersion; // 6-9
        sUserID += mOS; // 10
        sUserID += mOSVersion; // 11-14
        sUserID += mInputController; // 15
        sUserID += mNetwork; // 16
        sUserID += mSDKVersion; // 17-20
        sUserID += mOrganizationID; // 21-30
        sUserID += mReserve; // 31-50
        sUserID += mID; // 51-
        sUserID += "-" + mTerminalName; // -Thereafter

        return sUserID;
    }

    /**
     * Gets speech ID. Character string where month, day, hour, minute, second and millisecond are connected. 2011/05/17 18:59:345 -> 5171859345.
     * 
     * @return Speech ID
     */
    public String getUtteranceID() {
        String sRetVal = null;

        Calendar cal = Calendar.getInstance();
        String sMonth = Integer.toString(cal.get(Calendar.MONTH) + 1);

        String sDate = Integer.toString(cal.get(Calendar.DATE));
        if (sDate.length() == 1) {
            sDate = "0" + sDate;
        }
        String sHour = Integer.toString(cal.get(Calendar.HOUR_OF_DAY));
        if (sHour.length() == 1) {
            sHour = "0" + sHour;
        }
        String sMinute = Integer.toString(cal.get(Calendar.MINUTE));
        if (sMinute.length() == 1) {
            sMinute = "0" + sMinute;
        }
        String sSecond = Integer.toString(cal.get(Calendar.SECOND));
        if (sSecond.length() == 1) {
            sSecond = "0" + sSecond;
        }
        String sMilliSecond = Integer.toString(cal.get(Calendar.MILLISECOND));
        if (sMilliSecond.length() == 1) {
            sMilliSecond = "00" + sMilliSecond;
        } else if (sMilliSecond.length() == 2) {
            sMilliSecond = "0" + sMilliSecond;
        }

        sRetVal = sMonth + sDate + sHour + sMinute + sSecond; // + sMilliSecond;

        return sRetVal;
    }

    /**
     * Gets fixed TimeID.
     * 
     * @return Fixed TimeID. Values exist only when ID acquired was TimeID or when values were set at initialization. Otherwise {@code null} .
     */
    public String getFixedTimeID() {
        return msFixedTimeID;
    }

    /**
     * Sets application name.
     * 
     * @param sValue
     * @return
     */
    private boolean setApplicationName(String sValue) {
        if (sValue.length() != 2) {
            return false;
        }
        mApplicationName = sValue;
        return true;
    }

    /**
     * Sets organization ID.
     * 
     * @param sValue
     * @return
     */
    private boolean setOrganizationID(String sValue) {
        if (sValue.length() > 10) {
            return false;
        }
        mOrganizationID = sValue;

        for (int i = 0; i < 10 - sValue.length(); i++) {
            mOrganizationID += "#";
        }

        return true;
    }

    /**
     * Gets application version.
     * 
     * @param iValue
     * @return
     */
    private String getApplicationVersion(int iValue) {
        String sVersion = "0000";

        if ((iValue < 0) || (iValue >= 10000)) {
            return sVersion;
        }

        sVersion = Integer.toString(iValue);
        if (iValue < 10) {
            sVersion = "000" + sVersion;
        } else if (iValue < 100) {
            sVersion = "00" + sVersion;
        } else if (iValue < 1000) {
            sVersion = "0" + sVersion;
        }

        return sVersion;
    }

    /**
     * Gets TelephonyManager.
     * 
     * @return
     */
    private TelephonyManager getTelephonyManager() {
        TelephonyManager retVal = null;
        retVal = (TelephonyManager) mActivity
                .getSystemService(Context.TELEPHONY_SERVICE);

        return retVal;
    }

    /**
     * Gets WifiManager.
     * 
     * @return
     */
    private WifiManager getWifiManager() {
        WifiManager retVal = null;
        retVal = (WifiManager) mActivity.getSystemService(Context.WIFI_SERVICE);
        return retVal;
    }

    /**
     * Gets IMEI.
     * 
     * @return IMEI values. {@code null} if cannot get.
     */
    private String getIMEI() {
        String sIMEI = null;
        TelephonyManager telephonyManager = getTelephonyManager();
        if (telephonyManager != null) {
            sIMEI = telephonyManager.getDeviceId(); // Requires READ_PHONE_STATE
        }

        return sIMEI;
    }

    /**
     * Gets MacAddress.
     * 
     * @return MacAddress (without :). {@code null} if cannot get.
     */
    public String getMacAddress() {
        String sMacAddress = null;
        WifiManager wifiManager = getWifiManager();

        if (wifiManager != null) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            sMacAddress = wifiInfo.getMacAddress();
            if (sMacAddress != null) {
                sMacAddress = sMacAddress.replaceAll(":", "");
            }
        }

        return sMacAddress;
    }

    /**
     * Gets TimeID.
     * 
     * @return TIMEID
     */
    private String getTimeID() {
        String sTimeID = null;
        final Calendar calendar = Calendar.getInstance();
        sTimeID = Integer.toString(calendar.get(Calendar.YEAR)) + // Year/
                Integer.toString(calendar.get(Calendar.MONTH) + 1) + // Month/
                Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)) + // Day/
                Integer.toString(calendar.get(Calendar.HOUR_OF_DAY)) + // Hour/
                Integer.toString(calendar.get(Calendar.MINUTE)); // Minute/

        return sTimeID;
    }

    /**
     * Gets OS version. Gets with 4 digits.
     * 
     * @return OS version.
     */
    private String getOSVersion() {
        String sVersion = android.os.Build.VERSION.RELEASE;
        sVersion = sVersion.replaceAll("\\.", "");
        if (sVersion.length() == 1) {
            sVersion = "000" + sVersion;
        } else if (sVersion.length() == 2) {
            sVersion = "0" + sVersion + "0";
        } else if (sVersion.length() == 3) {
            sVersion = "0" + sVersion;
        } else if (sVersion.length() == 4) {

        } else {
            sVersion = "0000";
        }

        return sVersion;
    }

    /**
     * Gets network type.
     * 
     * @return 3G="G",Wifi="W"
     */
    private String getNetworkType() {
        WifiManager wifiManager = getWifiManager();
        int iWiFiState = wifiManager.getWifiState();
        WifiInfo info = wifiManager.getConnectionInfo();

        switch (iWiFiState) {
        case WifiManager.WIFI_STATE_ENABLED:
        case WifiManager.WIFI_STATE_ENABLING:
            if (info.getSSID() != null) {
                return NETWORK_WIFI;
            }
        default:
        }

        return NETWORK_3G;
    }

    /**
     * Gets SDK version.
     * 
     * @return SDK version
     */
    private String getSDKVersion() {

        String sVersion = "2.1.5";
        sVersion = sVersion.replaceAll("\\.", "");
        if (sVersion.length() == 1) {
            sVersion = "000" + sVersion;
        } else if (sVersion.length() == 2) {
            sVersion = "0" + sVersion + "0";
        } else if (sVersion.length() == 3) {
            sVersion = "0" + sVersion;
        } else if (sVersion.length() == 4) {

        } else {
            sVersion = "0000";
        }

        return sVersion;
    }

    /**
     * Gets terminal name.
     * 
     * @return Terminal name
     */
    private String getTerminalName() {
        String sTerminalName = Information.getModelName("");
        return sTerminalName;
    }

}
