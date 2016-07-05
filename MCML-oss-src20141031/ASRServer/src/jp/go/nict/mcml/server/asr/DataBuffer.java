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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * DataBuffer class.
 * 
 */
public class DataBuffer {
    // ------------------------------------------
    // private member variable(instance field)
    // ------------------------------------------
    private ByteOrder m_SourceOrder;
    private ByteOrder m_TargetOrder;
    private ByteArrayOutputStream m_Buffer;

    // ------------------------------------------
    // public member functions
    // ------------------------------------------
    /**
     * Default constructor
     */
    public DataBuffer() {
        m_SourceOrder = ByteOrder.BIG_ENDIAN;
        m_TargetOrder = ByteOrder.BIG_ENDIAN;
        m_Buffer = new ByteArrayOutputStream();
        m_Buffer.reset();
    }

    /**
     * Sets SourceOrder.
     * 
     * @param order
     */
    public void setSourceOrder(ByteOrder order) {
        m_SourceOrder = order;
    }

    /**
     * Sets TargetOrder.
     * 
     * @param order
     */
    public void setTargetOrder(ByteOrder order) {
        m_TargetOrder = order;
    }

    /**
     * Sets Order.
     * 
     * @param sourceOrder
     * @param targetOrder
     */
    public void setOrder(ByteOrder sourceOrder, ByteOrder targetOrder) {
        setSourceOrder(sourceOrder);
        setTargetOrder(targetOrder);
    }

    /**
     * Sets Data.
     * 
     * @param data
     * @throws IOException
     */
    public void setData(byte[] data) throws IOException {
        m_Buffer.write(data);
    }

    /**
     * Gets DataSize.
     * 
     * @return Buffer size
     */
    public int getDataSize() {
        return m_Buffer.size();
    }

    /**
     * Gets Data.
     * 
     * @return byte[]
     */
    public byte[] getData() {
        byte[] data = null;
        if (m_SourceOrder != m_TargetOrder) {
            data = convertEndian(m_Buffer.toByteArray());
        } else {
            data = m_Buffer.toByteArray();
        }

        return data;
    }

    /**
     * Clear
     */
    public void clear() {
        m_Buffer.reset();
    }

    // ------------------------------------------
    // private member function
    // ------------------------------------------
    private byte[] convertEndian(byte[] data) {
        ByteBuffer inputBuf = ByteBuffer.wrap(data);
        inputBuf.order(m_SourceOrder);

        byte[] workData = new byte[data.length];
        ByteBuffer outputBuf = ByteBuffer.wrap(workData);
        outputBuf.order(m_TargetOrder);

        while (inputBuf.hasRemaining()) {
            short sample = inputBuf.getShort();
            outputBuf.putShort(sample);
        }
        outputBuf.flip();

        return outputBuf.array();
    }
}
