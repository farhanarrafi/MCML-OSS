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

package jp.go.nict.mcml.engine.socket;

import java.io.Serializable;
import java.util.ArrayList;

import jp.go.nict.mcml.servlet.control.dispatcher.container.DispatchContainer;

import com.MCML.MCMLDoc;

/**
 * Session attribute class.
 * 
 */
public class SessionAttribute implements Serializable {

    private static final long serialVersionUID = 1L;

    // ------------------------------------------
    // public member statics
    // ------------------------------------------
    /** Session attribute */
    public static final String SESSION_ATTRIBUTE = "SESSION_ATTRIBUTE";

    // ------------------------------------------
    // private member variable
    // ------------------------------------------
    private MCMLDoc mcmlDoc;
    private ArrayList<Integer> corpusLogInfoIDList;
    private ArrayList<DispatchContainer> dispatchContainers;
    private boolean isEnd;

    // ------------------------------------------
    // public member function
    // ------------------------------------------
    /**
     * Default constructor
     * 
     */
    public SessionAttribute() {
        mcmlDoc = null;
        corpusLogInfoIDList = new ArrayList<Integer>();
        dispatchContainers = new ArrayList<DispatchContainer>();
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
     * Sets McmlDoc.
     * 
     * @param mcmlDoc
     */
    public void setMcmlDoc(MCMLDoc mcmlDoc) {
        this.mcmlDoc = mcmlDoc;
    }

    /**
     * Gets CorpusLogInfoIDList.
     * 
     * @return corpusLogInfoIDList
     */
    public ArrayList<Integer> getCorpusLogInfoIDList() {
        return corpusLogInfoIDList;
    }

    /**
     * Gets CorpusLogInfoIDList.
     * 
     * @param corpusLogInfoIDList
     */
    public void setCorpusLogInfoIDList(ArrayList<Integer> corpusLogInfoIDList) {
        this.corpusLogInfoIDList = corpusLogInfoIDList;
    }

    /**
     * Gets DispatchContainers.
     * 
     * @return dispatchContainers
     */
    public ArrayList<DispatchContainer> getDispatchContainers() {
        return dispatchContainers;
    }

    /**
     * Sets DispatchContainers.
     * 
     * @param dispatchContainers
     */
    public void setDispatchContainers(
            ArrayList<DispatchContainer> dispatchContainers) {
        this.dispatchContainers = dispatchContainers;
    }

    /**
     * Determines isEnd.
     * 
     * @return isEnd
     */
    public boolean isEnd() {
        return isEnd;
    }

    /**
     * Sets End to {@code true}.
     */
    public void setEnd() {
        this.isEnd = true;
    }

}
