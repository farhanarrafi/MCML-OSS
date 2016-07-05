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

import com.MCML.MCMLDoc;

/**
 * ResponseContainer class.
 * 
 * @version 4.0
 * @since 20120921
 */
public class ResponseContainer {
    // ------------------------------------------
    // private member variable
    // ------------------------------------------
    private int id;
    private float priorityCoefficient;
    private MCMLDoc mcmlDoc;
    private ArrayList<byte[]> binaryList;

    // ------------------------------------------
    // public member functions
    // ------------------------------------------
    /**
     * Constructor
     * 
     * @param id
     * @param priorityCoefficient
     * @param mcmlDoc
     * @param binaryList
     */
    public ResponseContainer(int id, float priorityCoefficient,
            MCMLDoc mcmlDoc, ArrayList<byte[]> binaryList) {
        this.id = id;
        this.priorityCoefficient = priorityCoefficient;
        this.mcmlDoc = mcmlDoc;
        this.binaryList = binaryList;
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
     * Gets McmlDoc.
     * 
     * @return mcmlDoc
     */
    public MCMLDoc getMcmlDoc() {
        return mcmlDoc;
    }

    /**
     *Gets  BinaryList.
     * 
     * @return binaryList
     */
    public ArrayList<byte[]> getBinaryList() {
        return binaryList;
    }
}
