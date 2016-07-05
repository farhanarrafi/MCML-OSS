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

package jp.go.nict.mcml.com;

import java.util.ArrayList;

/**
 * ComData class.
 * 
 */
public class ComData {
    // ------------------------------------------
    // protected member variable
    // ------------------------------------------
    protected String m_XML;
    protected ArrayList<byte[]> m_BinaryList;

    // ------------------------------------------
    // public member function
    // ------------------------------------------
    /** constructor */
    public ComData() {
        m_XML = null;
        m_BinaryList = new ArrayList<byte[]>();
    }

    /**
     * Gets XML.
     * 
     * @return XML
     */
    public String getXML() {
        return m_XML;
    }

    /**
     * Sets XML.
     * 
     * @param string
     *            XML
     */
    public void setXML(String string) {
        m_XML = string;
    }

    /**
     * Gets binary.
     * 
     * @return byte[]
     */
    public byte[] getBinary() {
        if (m_BinaryList.size() == 0) {
            return null;
        }
        return m_BinaryList.get(0);
    }

    /**
     * Sets binary.
     * 
     * @param binary
     *            Binary
     */
    public void setBinary(byte[] binary) {
        m_BinaryList.clear();
        m_BinaryList.add(binary);
    }

    /**
     * Gets binary list.
     * 
     * @return Binary list
     */
    public ArrayList<byte[]> getBinaryList() {
        return m_BinaryList;
    }

    /**
     * Sets binary list.
     * 
     * @param binaryList
     *            Binary list
     */
    public void setBinaryList(ArrayList<byte[]> binaryList) {
        m_BinaryList = binaryList;
    }

    /**
     * Determines whether XML exists
     * 
     * @return {@code true} if XML exists, otherwise {@code false}
     */
    public boolean hasXML() {
        if (m_XML != null && !m_XML.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Determines if binary exists.
     * 
     * @return {@code true} if binary exists, otherwise {@code false}
     */
    public boolean hasBinary() {
        if (m_BinaryList != null && !m_BinaryList.isEmpty()) {
            return true;
        }
        return false;
    }
}
