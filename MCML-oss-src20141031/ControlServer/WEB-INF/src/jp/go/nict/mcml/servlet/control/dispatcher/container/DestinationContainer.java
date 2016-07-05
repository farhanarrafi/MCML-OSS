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

package jp.go.nict.mcml.servlet.control.dispatcher.container;

import java.util.ArrayList;

import jp.go.nict.mcml.servlet.control.dispatcher.connector.Connector;

/**
 * DestinationContainer class.
 * 
 */
public class DestinationContainer {
    // ------------------------------------------
    // private member variables
    // ------------------------------------------
    private int id;
    private float priorityCoefficient;
    private ArrayList<String> languages;
    private ArrayList<Connector> connectors;
    private String company1;
    private String company2;

    // ------------------------------------------
    // public member functions
    // ------------------------------------------
    /**
     * Constructor
     * 
     * @param id
     * @param priorityCoefficient
     * @param languages
     * @param connectors
     * @param company1
     * @param company2
     */
    public DestinationContainer(int id, float priorityCoefficient,
            ArrayList<String> languages, ArrayList<Connector> connectors,
            String company1, String company2) {
        this.id = id;
        this.priorityCoefficient = priorityCoefficient;
        this.languages = languages;
        this.connectors = connectors;
        this.company1 = company1;
        this.company2 = company2;
    }

    /**
     * Gets Company1.
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
     * Gets Id.
     * 
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Gets PriorityCoefficient.
     * 
     * @return priorityCoefficient
     */
    public float getPriorityCoefficient() {
        return priorityCoefficient;
    }

    /**
     * Gets Languages.
     * 
     * @return languages
     */
    public ArrayList<String> getLanguages() {
        return languages;
    }

    /**
     * Gets Connectors.
     * 
     * @return connectors
     */
    public ArrayList<Connector> getConnectors() {
        return connectors;
    }
}
