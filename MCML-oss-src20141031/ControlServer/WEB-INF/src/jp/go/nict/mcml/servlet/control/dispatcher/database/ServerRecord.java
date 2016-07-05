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

import java.util.ArrayList;

/**
 * ServerRecord class.
 * 
 */
public class ServerRecord {
    // ------------------------------------------
    // public member constants
    // ------------------------------------------
    /** Maximum number of DESTINATIONS */
    public static final int MAX_DESTINATIONS = 2; // max destinations
    /** Maximum number of languages */
    public static final int MAX_LANGUAGES = 3; // max languages

    // ------------------------------------------
    // private member constants
    // ------------------------------------------
    private static final float DEFAULT_COEFFICIENT_PRIORITY = (float) (1.0);
    private static final float DEFAULT_COEFFICIENT_ASR_TIMEOUT = (float) (1.0);

    // ------------------------------------------
    // private member variable
    // ------------------------------------------
    private int listID; // list id
    private ArrayList<String> destinations; // destinations
    private String service; // service
    private ArrayList<String> languages; // languages
    private float coefficientPriority; // coefficient priority
    private float coefficientASRTimeout; // coefficient ASR timeout
    private int timeoutCounter; // timeout counter
    private String company1; // company1
    private String company2; // company2

    private String domain; // domain

    // ------------------------------------------
    // public member function
    // ------------------------------------------
    // constructor
    /**
     * Default constructor
     */
    public ServerRecord() {
        listID = -1;
        destinations = null;
        service = null;
        languages = null;
        coefficientPriority = DEFAULT_COEFFICIENT_PRIORITY;
        coefficientASRTimeout = DEFAULT_COEFFICIENT_ASR_TIMEOUT;
        timeoutCounter = 0;
        company1 = null;
        company2 = null;

        domain = null;

    }

    // setter member variables
    /**
     * Sets ListID
     * 
     * @param listID
     */
    public void setListID(int listID) {
        this.listID = listID;
    }

    /**
     * Sets Destinations.
     * 
     * @param destinations
     */
    public void setDestinations(ArrayList<String> destinations) {
        if (destinations.size() > MAX_DESTINATIONS) {
            return; // out of range
        }
        this.destinations = new ArrayList<String>(destinations);
        return; // normal end
    }

    /**
     * Sets Service.
     * 
     * @param service
     */
    public void setService(String service) {
        this.service = service;
    }

    /**
     * Sets Languages.
     * 
     * @param languages
     */
    public void setLanguages(ArrayList<String> languages) {
        if (languages.size() > MAX_LANGUAGES) {
            return; // out of range
        }
        this.languages = new ArrayList<String>(languages);
        return; // normal end
    }

    /**
     * Sets CoefficientPriority.
     * 
     * @param coefficientPriority
     */
    public void setCoefficientPriority(float coefficientPriority) {
        this.coefficientPriority = coefficientPriority;
    }

    /**
     * Sets CoefficientASRTimeout.
     * 
     * @param coefficientASRTimeout
     */
    public void setCoefficientASRTimeout(float coefficientASRTimeout) {
        this.coefficientASRTimeout = coefficientASRTimeout;
    }

    /**
     * Sets Company1.
     * 
     * @param company1
     */
    public void setCompany1(String company1) {
        this.company1 = company1;
    }

    /**
     * Sets Company2.
     * 
     * @param company2
     */
    public void setCompany2(String company2) {
        this.company2 = company2;
    }

    /**
     * Sets Domain.
     * 
     * @param domain
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    // getter member variables
    /**
     * Gets ListID.
     * 
     * @return listID
     */
    public int getListID() {
        return listID;
    }

    /**
     * Gets Destinations.
     * 
     * @return destinations
     */
    public ArrayList<String> getDestinations() {
        return destinations;
    }

    /**
     * Gets Service.
     * 
     * @return service
     */
    public String getService() {
        return service;
    }

    /**
     * Gets Languages.
     * 
     * @return Language list
     */
    public ArrayList<String> getLanguages() {
        return languages;
    }

    /**
     * Gets CoefficientPriority.
     * 
     * @return coefficientPriority
     */
    public float getCoefficientPriority() {
        return coefficientPriority;
    }

    /**
     * Gets CoefficientASRTimeout.
     * 
     * @return coefficientASRTimeout
     */
    public float getCoefficientASRTimeout() {
        return coefficientASRTimeout;
    }

    // reset timeout counter
    /**
     * Resets TimeoutCounter.
     * 
     */
    public void resetTimeoutCounter() {
        timeoutCounter = 0;
    }

    // increment timeout counter
    /**
     * Adds TimeoutCounter and Gets TimeoutCounter.
     * 
     * @return timeoutCounter
     */
    public int incrementTimeoutCounter() {
        timeoutCounter++;
        return timeoutCounter;
    }

    /**
     * Gets Company 1.
     * 
     * @return company1
     */
    public String getCompany1() {
        return company1;
    }

    /**
     * Gets Company2.
     * 
     * @return company2
     */
    public String getCompany2() {
        return company2;
    }

    /**
     * Gets Domain.
     * 
     * @return domain
     */
    public String getDomain() {
        return domain;
    }

}
