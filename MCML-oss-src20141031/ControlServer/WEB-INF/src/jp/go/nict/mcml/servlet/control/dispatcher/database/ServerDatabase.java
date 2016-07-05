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

package jp.go.nict.mcml.servlet.control.dispatcher.database;

import java.io.File;
import java.io.FileInputStream;
//import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Properties;

import jp.go.nict.mcml.servlet.control.ControlServerProperties;
//import jp.go.nict.mcml.servlet.MCMLLogger;
import jp.go.nict.mcml.xml.MCMLStatics;

import org.apache.log4j.Logger;

/**
 * ServerDatabase class.
 * 
 * @version 4.0
 * @serial 20120921
 * 
 */
public class ServerDatabase {
    private static final Logger LOG = Logger.getLogger(ServerDatabase.class
            .getName());
    // ------------------------------------------
    // private member constants
    // ------------------------------------------
    // list file keys
    private static final String KEY_SERVER = "server.";
    private static final String KEY_SERVERX = "server";
    private static final String KEY_MAXNUMBER = "maxnumber";
    private static final String KEY_DESTINATION = "destination";
    private static final String KEY_SERVICE = "service";
    private static final String KEY_LANGUAGES = "languages";
    private static final String KEY_COEFFICIENT = "coefficient.";
    private static final String KEY_PRIORITY = "priority";
    private static final String KEY_ASR = "asr.";
    private static final String KEY_TIMEOUT = "timeout";
    private static final ServerDatabase INSTANCE = new ServerDatabase();

    private static final String KEY_COMPANY1 = "company1";
    private static final String KEY_COMPANY2 = "company2";

    private static final String KEY_DOMAIN = "domain";

    private static final String KEY_VIA_SERVER = "via.";
    private static final String KEY_VIA_SERVERX = "via";
    private static final String KEY_CONNECT = "connect";
    private static final String VIA_SERVER_CONNECT_ON = "on";

    // ------------------------------------------
    // private member variable
    // ------------------------------------------
    private ArrayList<ServerRecord> serverRecords; // array for server list data
    private static final Object SERVER_RECORDS_LOCK = new Object(); // for lock
    private boolean timeoutLimitSwitch; // timeout limit switch
    private int timeoutLimitNumber; // timeout limit number

    private boolean viaConnect;
    private ArrayList<ServerRecord> viaRecords;

    // ------------------------------------------
    // public member function
    // ------------------------------------------
    /**
     * Gets instance.
     * 
     * @return  instance
     */
    public static ServerDatabase getInstance() {
        return INSTANCE;
    }

    /**
     * initialize
     * 
     * @param listFileName
     * @return {@code true}: Initialization successful; {@code false}: Initialization failed
     */
    public boolean initialize(String listFileName) {
        if (listFileName.equals("")) {
            return false;
        }
        Properties properties;
        try {
            // read list file
            properties = new Properties();
            InputStream inputStream = new FileInputStream(
                    new File(listFileName));
            properties.load(inputStream);
        } catch (Exception e) {
            LOG.fatal("Can't read: " + listFileName, e);
            return false;
        }

        String temp;
        String key;
        int maxServerNumber;

        // get list.maxnumber(required)
        key = KEY_SERVER + KEY_MAXNUMBER;
        temp = properties.getProperty(key, "0");
        try {
            maxServerNumber = Integer.parseInt(temp);
            if (maxServerNumber <= 0) {
                LOG.error("Invalid parameter for: " + key
                        + " (This value can't be less than 1.)");
                return false;
            }
        } catch (NumberFormatException e) {
            LOG.error("Invalid parameter for: " + key, e);
            return false;
        }

        if (!getServerXParameters(maxServerNumber, properties)) {
            serverRecords.clear();
            return false;
        }

        // normal end
        return true;
    }

    /**
     * get request destination form server list
     * 
     * @param service
     * @param sourceLanguage
     * @param targetLanguage
     * @return ServerRecord list
     */
    public ArrayList<ServerRecord> getDestinationFromServerList(String service,
            String sourceLanguage, String targetLanguage) {
        synchronized (SERVER_RECORDS_LOCK) {
            ArrayList<ServerRecord> serverRecords = new ArrayList<ServerRecord>();
            // search loop
            for (int i = 0; i < this.serverRecords.size(); i++) {
                ServerRecord serverRecord = this.serverRecords.get(i);
                // compare service

                if (serverRecord.getService().equals(service)) {
                    ArrayList<String> languages = new ArrayList<String>(
                            ServerRecord.MAX_LANGUAGES);
                    languages = serverRecord.getLanguages();

                    // DM
                    if (service.equals(MCMLStatics.SERVICE_DIALOG)) {
                        serverRecords.add(serverRecord);

                        // TTS

                    } else if (service.equals(MCMLStatics.SERVICE_TTS)) {
                        // compare source language
                        if (targetLanguage.equals(languages.get(0).toString())) {
                            serverRecords.add(serverRecord);
                        }
                    } else {
                        // compare source language
                        if (sourceLanguage.equals(languages.get(0).toString())) {
                            // ASR
                            if ((service.equals(MCMLStatics.SERVICE_ASR))) {
                                serverRecords.add(serverRecord);

                                // MT
                            } else {
                                String compareTargetLanguage;
                                if (languages.size() < ServerRecord.MAX_LANGUAGES) {
                                    compareTargetLanguage = languages.get(1)
                                            .toString();
                                } else {
                                    compareTargetLanguage = languages.get(2)
                                            .toString();
                                }
                                // compare Target Language
                                if (targetLanguage
                                        .equals(compareTargetLanguage)) {
                                    serverRecords.add(serverRecord);
                                }
                            }
                        }
                    }
                }
            }
            if (serverRecords.size() == 0) {
                return null; // not found request destination
            }
            // normal end
            return serverRecords;
        }
    }

    /**
     * get destinations by id
     * 
     * @param getListID
     * @return <ul>
     *        List containing the capacity of number of list ID destinations corresponding to server records.
     *         </ul>
     *         <ul>
     *         If server records do not exist and there is no corrsponding list ID, returns {@code null}.
     *         </ul>
     */
    public ArrayList<String> getDestinationsByID(int getListID) {
        synchronized (SERVER_RECORDS_LOCK) {
            ArrayList<String> destinations = null;
            for (int i = 0; i < serverRecords.size(); i++) {
                if (serverRecords.get(i).getListID() == getListID) {
                    destinations = new ArrayList<String>(serverRecords.get(i)
                            .getDestinations());
                    break;
                }
            }
            return destinations;
        }
    }

    /**
     * update timeout counter
     * 
     * @param updateListID
     */
    public void updateTimeoutCounter(int updateListID) {
        synchronized (SERVER_RECORDS_LOCK) {
            if (timeoutLimitSwitch) {
                for (int i = 0; i < serverRecords.size(); i++) {
                    if (serverRecords.get(i).getListID() == updateListID) {
                        if (serverRecords.get(i).incrementTimeoutCounter() >= timeoutLimitNumber) {
                            // delete from server list
                            deleteDestinationFromServerList(updateListID);
                        }
                        break;
                    }
                }
            }
        }
        return; // normal end
    }

    //
    /**
     * reset timeout counter
     * 
     * @param resetListID
     */
    public void resetTimeoutCounter(int resetListID) {
        synchronized (SERVER_RECORDS_LOCK) {
            if (timeoutLimitSwitch) {
                for (int i = 0; i < serverRecords.size(); i++) {
                    if (serverRecords.get(i).getListID() == resetListID) {
                        serverRecords.get(i).resetTimeoutCounter();
                        break;
                    }
                }
            }
        }
        return; // normal end
    }

    /**
     * Empty Check
     * 
     * @return {@code true}
     *         <ul>
     *         If there is no server record size.
     *         </ul>
     *         {@code false}
     *         <ul>
     *         If there is server record size.
     *         </ul>
     */
    public boolean isEmptyServerRecords() {
        boolean isEmpty = false;
        if (serverRecords.size() == 0) {
            isEmpty = true;
        }
        return isEmpty;
    }

    // ------------------------------------------
    // private member function
    // ------------------------------------------
    //
    /**
     * constructor
     */
    private ServerDatabase() {
        serverRecords = new ArrayList<ServerRecord>();
        ControlServerProperties properties = ControlServerProperties
                .getInstance();
        timeoutLimitSwitch = properties.getTimeoutLimitSwitch();
        timeoutLimitNumber = properties.getTimeoutLimitNumber();

        viaRecords = new ArrayList<ServerRecord>();
    }

    /**
     * get serverX parameters from list file
     * 
     * @param maxServerNumber
     * @param properties
     * @return boolean
     */
    private boolean getServerXParameters(int maxServerNumber,
            Properties properties) {
        // get a via server information
        getViaRecords(properties);

        // loop listX
        for (int i = 0; i < maxServerNumber; i++) {
            // create server list data
            ServerRecord serverRecord = new ServerRecord();

            // create "serverX." key
            String keyServerX = KEY_SERVERX + Integer.toString(i + 1) + ".";

            String key = keyServerX + KEY_DESTINATION + Integer.toString(1);
            String temp = properties.getProperty(key);
            if (temp == null) {
                // skip serverX + 1
                LOG.warn("'" + KEY_SERVERX + Integer.toString(i + 1)
                        + "' was skipped.");
                continue;
            }

            // get serverX.request.destinationY
            String requestDestination;
            ArrayList<String> requestDestinations = new ArrayList<String>();
            for (int j = 0; j < ServerRecord.MAX_DESTINATIONS; j++) {
                key = keyServerX + KEY_DESTINATION + Integer.toString(j + 1);
                requestDestination = properties.getProperty(key);
                if (requestDestination != null) {
                    requestDestinations.add(requestDestination);
                }
            }
            serverRecord.setDestinations(requestDestinations);

            // get serverX.service
            key = keyServerX + KEY_SERVICE;
            temp = properties.getProperty(key);
            if (!checkValidityService(temp)) {
                LOG.error("Invalid parameter for: " + key
                        + " (Unsupported service.)");
                return false;
            }
            serverRecord.setService(temp);

            // get serverX.company1
            key = keyServerX + KEY_COMPANY1;
            temp = properties.getProperty(key);
            serverRecord.setCompany1(temp);

            // get serverX.company2
            key = keyServerX + KEY_COMPANY2;
            temp = properties.getProperty(key);
            serverRecord.setCompany2(temp);

            // get serverX.domain
            key = keyServerX + KEY_DOMAIN;
            temp = properties.getProperty(key);
            serverRecord.setDomain(temp);

            // get serverX.language
            key = keyServerX + KEY_LANGUAGES;
            temp = properties.getProperty(key);
            if (temp == null) {
                LOG.error("Invalid parameter for: " + key
                        + " (This value can't be null.)");
                return false;
            }
            // split ","
            String[] tempArray = temp.split(",", -1);
            if (tempArray.length > ServerRecord.MAX_LANGUAGES) {
                LOG.error("Invalid parameter for: "
                        + key
                        + " (Array length can't be greater than 'ServerRecord.MAX_LANGUAGES'.)");
                return false;
            }
            ArrayList<String> languages = new ArrayList<String>(
                    Arrays.asList(tempArray));
            serverRecord.setLanguages(languages);

            // get serverX.coefficient.priority
            key = keyServerX + KEY_COEFFICIENT + KEY_PRIORITY;
            temp = properties.getProperty(key);
            try {
                if (temp != null) {
                    float ftemp = Float.parseFloat(temp);
                    if (ftemp < (float) (0.0)) {
                        LOG.error("Invalid parameter for: " + key
                                + " (This value can't be less than 0.0.)");
                        return false;
                    }
                    serverRecord.setCoefficientPriority(ftemp);
                }
            } catch (NumberFormatException e) {
                LOG.error("Invalid parameter for: " + key, e);
                return false;
            }

            // ASR service
            if (serverRecord.getService().equals(MCMLStatics.SERVICE_ASR)) {
                // get serverX.coefficient.asr.timeout
                key = keyServerX + KEY_COEFFICIENT + KEY_ASR + KEY_TIMEOUT;
                temp = properties.getProperty(key);
                try {
                    if (temp != null) {
                        float ftemp = Float.parseFloat(temp);
                        if (ftemp < (float) (0.0)) {
                            LOG.error("Invalid parameter for: " + key
                                    + " (This value can't be less than 0.0.)");
                            return false;
                        }
                        serverRecord.setCoefficientASRTimeout(ftemp);
                    }
                } catch (NumberFormatException e) {
                    LOG.error("Invalid parameter for: " + key, e);
                    return false;
                }
            }

            // check Validity MT service parameter
            if (serverRecord.getService().equals(MCMLStatics.SERVICE_MT)) {
                if (!checkValidityLanguage(keyServerX, serverRecord)) {

                    return false; // invalid parameter
                }
                if (!checkValidityRequestDestination(keyServerX, serverRecord)) {

                    return false; // invalid parameter
                }
            }

            // set list ID
            serverRecord.setListID(i);

            // add server list array
            serverRecords.add(serverRecord);
        }

        return true; // normal end
    }

    /**
     * check validity service parameter
     * 
     * @param service
     * @return {@code true}
     *         <ul>
     *        <li> If other than {@code null}, and the parameter is ASR, MT, TTS, or DIALOG</li>
     *         </ul>
     *         {@code false}
     *         <ul>
     *        <li> {@code null}, or the parameter is other than ASR and other than MT and other than TTS and other than DIALOG</li>
     *         </ul>
     * 
     */
    private boolean checkValidityService(String service) {
        if (service == null) {
            return false; // invalid
        }

        // check parameter
        if ((!service.equals(MCMLStatics.SERVICE_ASR))
                && (!service.equals(MCMLStatics.SERVICE_MT))
                && (!service.equals(MCMLStatics.SERVICE_TTS))
                && (!service.equals(MCMLStatics.SERVICE_DIALOG))) {

            return false; // invalid
        }

        // valid
        return true;
    }

    /**
     * check validity language
     * 
     * @param keyServerX
     * @param serverRecord
     * @return {@code true} if the number of server record language is 2 or more. {@code false} if the number of server record languages is 1 or less
     */
    private boolean checkValidityLanguage(String keyServerX,
            ServerRecord serverRecord) {
        ArrayList<String> languages = serverRecord.getLanguages();
        if (languages.size() < 2) {
            // not exist target language
            LOG.error("Invalid parameter for: " + keyServerX + KEY_LANGUAGES
                    + " (Target language is empty.)");
            return false; // invalid
        }
        // valid
        return true;
    }

    /**
     * check validity request destination
     * 
     * @param keyServerX
     * @param serverRecord
     * @return {@code true}
     *         <ul>
     *        <li> If the number of server record languages is other than the maximum number of languages</li>
     *        <li> or is the maximum number of languages and the number of destinations is more than the maximum number of destinations </li>
     *         </ul>
     *         {@code false}
     *         <ul>
     *        If the number of server code languages is the maximum number of languages and the number of destinations is less than the maximum number of destinations
     *         </ul>
     */
    private boolean checkValidityRequestDestination(String keyServerX,
            ServerRecord serverRecord) {
        ArrayList<String> languages = serverRecord.getLanguages();
        if (languages.size() == ServerRecord.MAX_LANGUAGES) {
            ArrayList<String> destinations = serverRecord.getDestinations();
            // not exist request destination2
            if (destinations.size() < ServerRecord.MAX_DESTINATIONS) {
                LOG.error("Invalid parameter for: "
                        + keyServerX
                        + KEY_DESTINATION
                        + "2"
                        + " (Array length can't be less than 'ServerRecord.MAX_DESTINATIONS'.)");
                return false; // invalid
            }
        }
        // valid
        return true;
    }

    /**
     * delete destination from server list (occurred request timeout server)
     * 
     * @param deleteListID
     */
    private void deleteDestinationFromServerList(int deleteListID) {
        synchronized (SERVER_RECORDS_LOCK) {
            // delete record
            for (int i = 0; i < serverRecords.size(); i++) {
                if (serverRecords.get(i).getListID() == deleteListID) {
                    serverRecords.remove(i);
                    break;
                }
            }
        }
        return; // normal end
    }

    /**
     * get a MT server list
     * 
     * @param sourceLanguage
     * @param targetLanguage
     * @return list of type ServerRecord
     */
    public ArrayList<ServerRecord> getMTServerList(String sourceLanguage,
            String targetLanguage) {
        // check a connection via server
        if (!viaConnect) {
            return getDestinationFromServerList("MT", sourceLanguage,
                    targetLanguage);
        }

        ArrayList<ServerRecord> serverMTRecords = new ArrayList<ServerRecord>();

        Calendar cal1 = Calendar.getInstance();
        int year = cal1.get(Calendar.YEAR); // (2) Gets current year
        int month = cal1.get(Calendar.MONTH) + 1; // (3) Gets current month
        int day = cal1.get(Calendar.DATE); // (4) Gets current day
        int hour = cal1.get(Calendar.HOUR_OF_DAY); // (5) Gets current hour
        int minute = cal1.get(Calendar.MINUTE); // (6) Gets current minute
        int second = cal1.get(Calendar.SECOND); // (7) Gets current second
        LOG.info(year + "/" + month + "/" + day + " " + " " + hour + ":"
                + minute + ":" + second);

        for (int i = 0; i < viaRecords.size(); i++) {
            ServerRecord viaRecord = viaRecords.get(i);
            String viaLanguage = viaRecord.getLanguages().get(0);

            if (viaLanguage.equals(sourceLanguage)
                    || viaLanguage.equals(targetLanguage)) {
                break;
            }

            // get from source to via server list
            ArrayList<ServerRecord> toViaRecords = new ArrayList<ServerRecord>();
            toViaRecords = getDestinationFromServerList("MT", sourceLanguage,
                    viaLanguage);
            if (toViaRecords.size() == 0) {
                continue;
            }
            for (int via = 0; via < toViaRecords.size(); via++) {
                ServerRecord toViaRecord = toViaRecords.get(via);
                LOG.info("toViaRecord \n" + "list id = "
                        + toViaRecord.getListID() + "\n" + "destinations = "
                        + toViaRecord.getDestinations() + "\n" + "languages = "
                        + toViaRecord.getLanguages() + "\n"
                        + "coefficient.priority = "
                        + toViaRecord.getCoefficientPriority() + "\n"
                        + "company1 = " + toViaRecord.getCompany1() + "\n"
                        + "company2 = " + toViaRecord.getCompany2());
            }

            // get from via to target server list
            ArrayList<ServerRecord> toTargetRecords = new ArrayList<ServerRecord>();
            toTargetRecords = getDestinationFromServerList("MT", viaLanguage,
                    targetLanguage);
            if (toTargetRecords.size() == 0) {
                continue;
            }
            for (int target = 0; target < toTargetRecords.size(); target++) {
                ServerRecord toTargetRecord = toTargetRecords.get(target);
                LOG.info("toTargetRecord \n" + "list id = "
                        + toTargetRecord.getListID() + "\n" + "destinations = "
                        + toTargetRecord.getDestinations() + "\n"
                        + "languages = " + toTargetRecord.getLanguages() + "\n"
                        + "coefficient.priority = "
                        + toTargetRecord.getCoefficientPriority() + "\n"
                        + "company1 = " + toTargetRecord.getCompany1() + "\n"
                        + "company2 = " + toTargetRecord.getCompany2());
            }

            // merge server lists
            mergeMTServerRecords(toViaRecords, toTargetRecords, serverMTRecords);
        }

        ArrayList<ServerRecord> orgRecords = getDestinationFromServerList("MT",
                sourceLanguage, targetLanguage);
        if (orgRecords != null) {
            serverMTRecords.addAll(orgRecords);
        }

        for (int i = 0; i < serverMTRecords.size(); i++) {
            ServerRecord serverMTRecord = serverMTRecords.get(i);
            LOG.info("MTRecord \n" + "list id = " + serverMTRecord.getListID()
                    + "\n" + "destinations = "
                    + serverMTRecord.getDestinations() + "\n" + "languages = "
                    + serverMTRecord.getLanguages() + "\n"
                    + "coefficient.priority = "
                    + serverMTRecord.getCoefficientPriority() + "\n"
                    + "company1 = " + serverMTRecord.getCompany1() + "\n"
                    + "company2 = " + serverMTRecord.getCompany2());
        }

        return serverMTRecords;
    }

    /**
     * get a via server information
     * 
     * @param properties
     */
    private void getViaRecords(Properties properties) {
        // check a connecting via server
        viaConnect = checkViaConnect(properties);
        if (!viaConnect) {
            return;
        }

        ServerRecord viaRecord = new ServerRecord();

        // maxnumber
        String key = KEY_VIA_SERVER + KEY_MAXNUMBER;
        String tmp = properties.getProperty(key);
        int viaNumber = Integer.parseInt(tmp);

        for (int i = 0; i < viaNumber; i++) {
            String viaKey = KEY_VIA_SERVERX + Integer.toString(i + 1) + ".";

            // languages
            ArrayList<String> languages = new ArrayList<String>();
            key = viaKey + KEY_LANGUAGES;
            tmp = properties.getProperty(key);
            if ((tmp == null) || tmp.equals("")) {
                continue;
            }
            languages.add(tmp);
            viaRecord.setLanguages(languages);
            viaRecords.add(viaRecord);
        }
    }

    /**
     * check a connecting via server
     * 
     * @param properties
     * @return {@code true}
     *         <ul>
     *        When value acquired from parameter property is {@value #VIA_SERVER_CONNECT_ON}
     *         </ul>
     *         {@code false}
     *         <ul>
     *        When value is neither {@code null} nor {@value #VIA_SERVER_CONNECT_ON}
     *         </ul>
     */
    private boolean checkViaConnect(Properties properties) {
        String key = KEY_VIA_SERVER + KEY_CONNECT;
        String tmp = properties.getProperty(key);

        // check for null
        if (tmp == null) {
            // default is off
            return false;
        }

        if (tmp.equals(VIA_SERVER_CONNECT_ON)) {
            return true;
        }
        return false;
    }

    /**
     * merge a via server list and target server list
     * 
     * @param toViaRecords
     * @param toTargetRecords
     * @param serverMTRecords
     */
    private void mergeMTServerRecords(ArrayList<ServerRecord> toViaRecords,
            ArrayList<ServerRecord> toTargetRecords,
            ArrayList<ServerRecord> serverMTRecords) {
        int index = 0;
        for (int i = 0; i < toViaRecords.size(); i++) {
            ServerRecord toViaRecord = toViaRecords.get(i);

            // get a destination1
            String destination1 = toViaRecord.getDestinations().get(0);

            // get languages
            String language1 = toViaRecord.getLanguages().get(0);

            for (int j = 0; j < toTargetRecords.size(); j++) {
                ServerRecord serverMTRecord = new ServerRecord();
                ServerRecord toTargetRecord = toTargetRecords.get(j);

                // set a service
                serverMTRecord.setService("MT");

                // get a destination2
                ArrayList<String> destinations = new ArrayList<String>();
                destinations.add(destination1);
                destinations.add(toTargetRecord.getDestinations().get(0));

                // get languages
                ArrayList<String> languages = new ArrayList<String>();
                languages.add(language1);
                languages.addAll(toTargetRecord.getLanguages());

                // add
                serverMTRecord.setListID(index++);
                serverMTRecord.setDestinations(destinations);
                serverMTRecord.setLanguages(languages);
                serverMTRecord.setCoefficientPriority(toViaRecord
                        .getCoefficientPriority()
                        * toTargetRecord.getCoefficientPriority());
                serverMTRecord.setCompany1(toViaRecord.getCompany1());
                serverMTRecord.setCompany2(toTargetRecord.getCompany1());
                serverMTRecords.add(serverMTRecord);
            }
        }
    }
}
