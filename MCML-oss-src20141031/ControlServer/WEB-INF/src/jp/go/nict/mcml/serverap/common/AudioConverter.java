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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import jp.go.nict.mcml.exception.MCMLException;
import jp.go.nict.speechcoding.SignalAdpcm;

import org.apache.log4j.Logger;

/**
 * AudioConverterクラスです。
 *
 */
public class AudioConverter {
    private static final Logger LOG = Logger.getLogger(AudioConverter.class
            .getName());

    // ------------------------------------------
    // public member variable
    // ------------------------------------------
    // AudioFormat Type
    /**
     * AudioTypeの種別列挙型クラスです。
     *
     */
    public enum AudioType {
        /** 予期しない */
        Unexpected,
        /** RAW */
        Raw,
        /** ADPCM */
        ADPCM,
        /** DSR */
        DSR
    }

    // ------------------------------------------
    // protected member variable
    // ------------------------------------------
    protected boolean m_SrcIsRawData;
    protected ByteOrder m_SrcOrder;

    protected boolean m_TrgIsRawData;
    protected ByteOrder m_TrgOrder;

    protected boolean m_IsLastData;

    protected SignalAdpcm m_Codec;
    private int m_ServerType = 0;

    // ------------------------------------------
    // public member function
    // ------------------------------------------
    /**
     * constructor
     *
     * @param srcIsRawData
     * @param srcIsBigEndian
     * @param trgIsRawData
     * @param trgIsBigEndian
     * @param serverType
     */
    public AudioConverter(boolean srcIsRawData, boolean srcIsBigEndian,
            boolean trgIsRawData, boolean trgIsBigEndian, int serverType) {
        m_SrcIsRawData = srcIsRawData;
        m_SrcOrder = (srcIsBigEndian) ? ByteOrder.BIG_ENDIAN
                : ByteOrder.LITTLE_ENDIAN;

        m_TrgIsRawData = trgIsRawData;
        m_TrgOrder = (trgIsBigEndian) ? ByteOrder.BIG_ENDIAN
                : ByteOrder.LITTLE_ENDIAN;

        if (m_SrcIsRawData && !m_TrgIsRawData) {
            // convert RAW to ADPCM
            m_Codec = new SignalAdpcm(m_SrcOrder);
        } else if (!m_SrcIsRawData && m_TrgIsRawData) {
            // convert ADPCM to RAW
            m_Codec = new SignalAdpcm(m_TrgOrder);
        } else {
            m_Codec = null;
        }
        m_ServerType = serverType;

        m_IsLastData = false;
    }

    /**
     * convertします。
     *
     * @param inputData
     * @return byte[]
     * @throws MCMLException
     */
    public byte[] convert(byte[] inputData) throws MCMLException {
        if (m_IsLastData) {
            LOG.error("Failed to convert audio due to abnormal byte length.");
            throw new MCMLException(MCMLException.ERROR, m_ServerType,
                    MCMLException.AUDIO_BYTE_LENGTH_ERROR);
        }

        byte[] outputData = null;

        if (m_SrcIsRawData) {
            if (m_TrgIsRawData) {
                // endian convert (RAW to RAW)
                outputData = changeRawOrder(inputData);
            } else {
                // convert RAW to ADPCM
                if ((inputData.length % 4) != 0) {
                    m_IsLastData = true;
                }
                outputData = m_Codec.Encode(inputData, m_IsLastData);
            }
        } else {
            if (m_TrgIsRawData) {
                // convert ADPCM to RAW
                outputData = m_Codec.Decode(inputData);
            } else {
                // unnecessary convert (ADPCM to ADPCM)
                outputData = inputData;
            }
        }

        if (outputData == null) {
            LOG.error("Failed to convert audio.");
            throw new MCMLException(MCMLException.ERROR, m_ServerType,
                    MCMLException.AUDIO_CONVERSION_ERROR);
        }

        return outputData;
    }

    /**
     * RawDataかどうかの判定を行います。
     *
     * @return m_SrcIsRawData
     */
    public boolean srcIsRawData() {
        return m_SrcIsRawData;
    }

    /**
     * BigEndianかどうかの判定を行います。
     *
     * @return BIG_ENDIANの場合は{@code true}、それ以外は{@code false}
     */
    public boolean srcIsBigEndian() {
        return (m_SrcOrder == ByteOrder.BIG_ENDIAN) ? true : false;
    }

    /**
     * RawDataかどうかの判定を行います。
     *
     * @return boolean
     */
    public boolean trgIsRawData() {
        return m_TrgIsRawData;
    }

    /**
     * BigEndianかどうかの判定を行います。
     *
     * @return BIG_ENDIANの場合は{@code true}、それ以外は{@code false}
     */
    public boolean trgIsBigEndian() {
        return (m_TrgOrder == ByteOrder.BIG_ENDIAN) ? true : false;
    }

    // ------------------------------------------
    // private member function
    // ------------------------------------------

    private byte[] changeRawOrder(byte[] rawData) {
        ByteBuffer inputBuf = ByteBuffer.wrap(rawData);
        inputBuf.order(m_SrcOrder);

        byte[] workData = new byte[rawData.length];
        ByteBuffer outputBuf = ByteBuffer.wrap(workData);
        outputBuf.order(m_TrgOrder);

        while (inputBuf.hasRemaining()) {
            short sample = inputBuf.getShort();
            outputBuf.putShort(sample);
        }
        outputBuf.flip();

        return outputBuf.array();
    }
}
