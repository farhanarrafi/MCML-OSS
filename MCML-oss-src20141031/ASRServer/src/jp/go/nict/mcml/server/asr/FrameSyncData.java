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

package jp.go.nict.mcml.server.asr;

import java.nio.ByteBuffer;

/**
 * FrameSyncData class.
 * 
 */
public class FrameSyncData {
    // ------------------------------------------
    // public member constant
    // ------------------------------------------
    // for Header setting.
    /** TOF */
    public static final int TOF = 9;
    /** STARTPU */
    public static final int STARTPU = 1;
    /** DATA */
    public static final int DATA = 0;
    /** ENDPU */
    public static final int ENDPU = 2;
    /** EOF */
    public static final int EOF = 10;
    /** HEADER_SIZE */
    public static final int HEADER_SIZE = 4;

    // ------------------------------------------
    // private member variable(instance field)
    // ------------------------------------------
    private int m_PayloadDataBytes;
    private int m_Header;
    private byte[] m_SpeechData;

    FrameSyncData(int payloadDataBytes) {
        m_Header = 0;
        m_SpeechData = new byte[payloadDataBytes];
        m_PayloadDataBytes = payloadDataBytes;
    }

    /**
     * Gets Header.
     * 
     * @return Header
     */
    public int getHeader() {
        return m_Header;
    }

    /**
     * Gets SpeechData.
     * 
     * @return SpeechData
     */
    public byte[] getSpeechData() {
        return m_SpeechData;
    }

    /**
     * Header value set.
     * 
     * @param header
     * @return boolean
     */
    public boolean setHeader(int header) {
        // check header value of the input arg.
        if (header == TOF || header == STARTPU || header == DATA
                || header == ENDPU || header == EOF) {
            m_Header = header;
            return true;
        }
        return false;

    }

    /**
     * Sets SpeechData.
     * 
     * @param speechdata
     * @throws IllegalArgumentException
     * @throws UnsupportedOperationException
     */
    public void setSpeechData(byte[] speechdata)
            throws IllegalArgumentException, UnsupportedOperationException {
        // check Header(FrameSyncType).
        if (m_Header != DATA) {
            throw new UnsupportedOperationException();
        }

        // check InputData Length.
        if (speechdata.length != m_PayloadDataBytes) {
            throw new IllegalArgumentException();
        }
        // set InputData
        m_SpeechData = speechdata;
    }

    /**
     * toBinary
     * 
     * @return byte[]
     */
    public byte[] toBinary() {
        byte[] headerBuf = new byte[4];

        // Header Value cast to ByteArray.
        headerBuf[3] = (byte) m_Header;

        ByteBuffer byteBuffer = ByteBuffer.allocate(HEADER_SIZE
                + m_PayloadDataBytes);
        byteBuffer.put(headerBuf);
        byteBuffer.put(m_SpeechData);

        // output FrameSyncData(Binary).
        return byteBuffer.array();
    }
}
