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

package jp.go.nict.mcml.serverap.common;

/**
 * @version 4.0
 * @since 20120921
 */
import java.util.ArrayList;

import com.MCML.MCMLDoc;

/**
 * MCMLDataクラスです。
 * 
 */
public class MCMLData extends ServerApObject {
    // ------------------------------------------
    // private member variable
    // ------------------------------------------
    private MCMLDoc m_MCMLDoc;
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
     * デフォルトコンストラクタ
     */
    public MCMLData() {
        m_MCMLDoc = null;
        m_BinaryDataList = new ArrayList<byte[]>();
        m_EngineInfo = null;
        m_IsErrorOccured = false;
        m_BinaryDataQueue = null;
        m_ReceiveTime = -1;
        m_IsRequestTimeoutOccurred = false;
    }

    /**
     * コンストラクタ
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
     * McmlDocを取得します。
     * 
     * @return m_MCMLDoc
     */
    public MCMLDoc getMcmlDoc() {
        return m_MCMLDoc;
    }

    /**
     * McmlDocを設定します。
     * 
     * @param mcmlDoc
     */
    public void setMcmlDoc(MCMLDoc mcmlDoc) {
        m_MCMLDoc = mcmlDoc;
    }

    /**
     * BinaryDataを追加します。
     * 
     * @param binaryData
     */
    public void addBinaryData(byte[] binaryData) {
        m_BinaryDataList.add(binaryData);
    }

    /**
     * BinaryDataをクリアします。
     */
    public void clearBinaryData() {
        m_BinaryDataList.clear();
    }

    /**
     * BinaryDataListを取得します。
     * 
     * @return m_BinaryDataList
     */
    public ArrayList<byte[]> getBinaryDataList() {
        return m_BinaryDataList;
    }

    /**
     * EngineInfoを設定します。
     * 
     * @param engineInfo
     */
    public void setEngineInfo(EngineInfo engineInfo) {
        m_EngineInfo = engineInfo;
    }

    /**
     * EngineInfoを取得します。
     * 
     * @return m_EngineInfo
     */
    public EngineInfo getEngineInfo() {
        return m_EngineInfo;
    }

    /**
     * BinaryQueueを設定します。
     * 
     * @param data
     */
    public void putBinaryQueue(byte[] data) {
        if (m_BinaryDataQueue != null) {
            m_BinaryDataQueue.putData(data);
        }
    }

    /**
     * BinaryQueueを取得します。
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
     * IsErrorOccuredを設定します。
     * 
     * @param isErrorOccured
     */
    public void setIsErrorOccured(boolean isErrorOccured) {
        synchronized (this) {
            m_IsErrorOccured = isErrorOccured;
        }
    }

    /**
     * ErrorOccuredの判定を行います。
     * 
     * @return m_IsErrorOccured
     */
    public boolean isErrorOccured() {
        synchronized (this) {
            return m_IsErrorOccured;
        }
    }

    /**
     * ReceiveTimeを設定します。
     */
    public void setReceiveTime() {
        synchronized (this) {
            m_ReceiveTime = System.currentTimeMillis();
        }
    }

    /**
     * ReceiveTimeを取得します。
     * 
     * @return m_ReceiveTime
     */
    public long getReceiveTime() {
        synchronized (this) {
            return m_ReceiveTime;
        }
    }

    /**
     * IsRequestTimeoutOccurredを設定します。
     * 
     * @param isRequestTimeoutOccurred
     */
    public void setIsRequestTimeoutOccurred(boolean isRequestTimeoutOccurred) {
        synchronized (this) {
            m_IsRequestTimeoutOccurred = isRequestTimeoutOccurred;
        }
    }

    /**
     * IsRequestTimeoutOccurredを取得します。
     * 
     * @return m_IsRequestTimeoutOccurred
     */
    public boolean getIsRequestTimeoutOccurred() {
        synchronized (this) {
            return m_IsRequestTimeoutOccurred;
        }
    }
}
