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

//-------------------------------------------------------------------
//Ver.3.0
//2011/12/06
//-------------------------------------------------------------------

package jp.go.nict.mcml.serverap.common;

import java.util.ArrayList;

import jp.go.nict.mcml.xml.types.MCMLType;

/**
 * MCMLData class.
 * 
 */
public class MCMLData extends ServerApObject {
    // ------------------------------------------
    // private member variable
    // ------------------------------------------
    private MCMLType m_MCMLType;
    private ArrayList<byte[]> m_BinaryDataList;
    private EngineInfo m_EngineInfo;
    private DataQueue<byte[]> m_BinaryDataQueue;
    private boolean m_IsErrorOccured;
    private long m_ReceiveTime;
    private boolean m_IsRequestTimeoutOccurred;

    // ------------------------------------------
    // public member function
    // ------------------------------------------
    /**
     * Default constructor
     */
    public MCMLData() {
        m_MCMLType = null;
        m_BinaryDataList = new ArrayList<byte[]>();
        m_EngineInfo = null;
        m_IsErrorOccured = false;
        m_BinaryDataQueue = null;
        m_ReceiveTime = -1;
        m_IsRequestTimeoutOccurred = false;
    }

    /**
     * Constructor
     * 
     * @param supportDivision
     */
    public MCMLData(boolean supportDivision) {
        this();
        if (supportDivision) {
            m_BinaryDataQueue = new DataQueue<byte[]>();
        }
    }

    /**
     * Gets MCMLType.
     * 
     * @return MCMLType
     */
    public MCMLType getMCMLType() {
        return m_MCMLType;
    }

    /**
     * Sets MCMLType.
     * 
     * @param mcmlType
     */
    public void setMCMLType(MCMLType mcmlType) {
        m_MCMLType = mcmlType;
    }

    /**
     * Adds binary data to BinaryData list.
     * 
     * @param binaryData
     */
    public void addBinaryData(byte[] binaryData) {
        m_BinaryDataList.add(binaryData);
    }

    /**
     * Clears binary data list.
     */
    public void clearBinaryData() {
        m_BinaryDataList.clear();
    }

    /**
     * Gets binary data list.
     * 
     * @return  Binary data list
     */
    public ArrayList<byte[]> getBinaryDataList() {
        return m_BinaryDataList;
    }

    /**
     * Sets EngineInfo.
     * 
     * @param engineInfo
     */
    public void setEngineInfo(EngineInfo engineInfo) {
        m_EngineInfo = engineInfo;
    }

    /**
     * Gets EngineInfo.
     * 
     * @return EngineInfo
     */
    public EngineInfo getEngineInfo() {
        return m_EngineInfo;
    }

    /**
     * Adds data to binary data Queue.
     * 
     * @param data
     */
    public void putBinaryQueue(byte[] data) {
        if (m_BinaryDataQueue != null) {
            m_BinaryDataQueue.putData(data);
        }
    }

    /**
     * takeBinaryQueue
     * 
     * @return byte[]
     */
    public byte[] takeBinaryQueue() {
        byte[] data = null;
        if (m_BinaryDataQueue != null) {
            data = m_BinaryDataQueue.takeData();
        }
        return data;
    }

    /**
     * Sets isErrorOccured.
     * 
     * @param isErrorOccured
     */
    public void setIsErrorOccured(boolean isErrorOccured) {
        synchronized (this) {
            m_IsErrorOccured = isErrorOccured;
        }
    }

    /**
     * Gets m_IsErrorOccured.
     * 
     * @return m_IsErrorOccured
     */
    public boolean isErrorOccured() {
        synchronized (this) {
            return m_IsErrorOccured;
        }
    }

    /**
     * Sets ReceiveTime.
     */
    public void setReceiveTime() {
        synchronized (this) {
            m_ReceiveTime = System.currentTimeMillis();
        }
    }

    /**
     * Gets ReceiveTime.
     * 
     * @return ReceiveTime
     */
    public long getReceiveTime() {
        synchronized (this) {
            return m_ReceiveTime;
        }
    }

    /**
     *Sets IsRequestTimeoutOccurred.
     * 
     * @param isRequestTimeoutOccurred
     */
    public void setIsRequestTimeoutOccurred(boolean isRequestTimeoutOccurred) {
        synchronized (this) {
            m_IsRequestTimeoutOccurred = isRequestTimeoutOccurred;
        }
    }

    /**
     * Gets IsRequestTimeoutOccurred.
     * 
     * @return m_IsRequestTimeoutOccurred
     */
    public boolean getIsRequestTimeoutOccurred() {
        synchronized (this) {
            return m_IsRequestTimeoutOccurred;
        }
    }
}
