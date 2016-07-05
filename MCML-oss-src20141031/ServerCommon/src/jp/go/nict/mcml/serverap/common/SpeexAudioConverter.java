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

import java.nio.ByteOrder;

import jp.go.nict.mcml.servlet.MCMLException;
import jp.go.nict.speex4j.SpeexDecoder;
import jp.go.nict.speex4j.SpeexEncoder;

/**
 * SpeexAudioConverter class.
 * 
 */
public class SpeexAudioConverter extends AudioConverter {

    private int quality = 8;
    private int complexity = 3;
    private int vbr = 1;

    private SpeexEncoder m_Encoder;
    private SpeexDecoder m_Decoder;

    /**
     * Constructor
     * 
     * @param srcIsRawData
     * @param srcIsBigEndian
     * @param trgIsRawData
     * @param trgIsBigEndian
     * @param serverType
     */
    public SpeexAudioConverter(boolean srcIsRawData, boolean srcIsBigEndian,
            boolean trgIsRawData, boolean trgIsBigEndian, int serverType) {

        m_SrcIsRawData = srcIsRawData;
        m_SrcOrder = (srcIsBigEndian) ? ByteOrder.BIG_ENDIAN
                : ByteOrder.LITTLE_ENDIAN;

        m_TrgIsRawData = trgIsRawData;
        m_TrgOrder = (trgIsBigEndian) ? ByteOrder.BIG_ENDIAN
                : ByteOrder.LITTLE_ENDIAN;

        if (m_SrcIsRawData && !m_TrgIsRawData) {
            // convert Speex to ADPCM
            m_Encoder = new SpeexEncoder(quality, complexity, vbr);
            m_Decoder = null;
            m_Codec = null;
        } else if (!m_SrcIsRawData && m_TrgIsRawData) {
            // convert Speex to RAW
            // m_Codec = new SignalAdpcm(m_TrgOrder) ;
            m_Decoder = new SpeexDecoder();
            m_Encoder = null;
            m_Codec = null;

        } else {
            m_Codec = null;
        }

        m_ServerType = serverType;

        m_IsLastData = false;
    }

    /**
     * convert
     */
    @Override
    public byte[] convert(byte[] inputData) {
        return changeRawOrder(inputData);
    }

    /**
     * pre_convert
     * 
     * @param inputData
     * @return byte[]
     * @throws MCMLException
     */
    public byte[] pre_convert(byte[] inputData) throws MCMLException {
        if (m_IsLastData) {
            throw new MCMLException("wrong inputData length",
                    MCMLException.ERROR, m_ServerType,
                    MCMLException.ABNORMAL_DATA_FORMAT);
        }

        byte[] outputData = null;

        try {
            if (m_SrcIsRawData) {
                if (m_TrgIsRawData) {
                    // endian convert (RAW to RAW)
                    ServerApLogger.getInstance().writeDebug(
                            "pre_convert.changeRawOrder");
                    outputData = changeRawOrder(inputData);
                } else {
                    // convert RAW to Speex
                    outputData = m_Encoder.encode(inputData, inputData.length);
                    System.out.println("Speex encode!");
                }
            } else {
                if (m_TrgIsRawData) {
                    // convert Speex to RAW
                    try {
                        outputData = m_Decoder.decode(inputData);
                    } catch (Exception e) {
                        ServerApLogger.getInstance().writeError(
                                "wrong input speex data");
                        throw new MCMLException("wrong input speex data",
                                MCMLException.SYSTEM, m_ServerType,
                                MCMLException.ABNORMAL_DATA_FORMAT);
                    }
                } else {
                    outputData = inputData;
                }
            }

            if (outputData == null) {
                throw new MCMLException("AudioConverter.convert()",
                        MCMLException.SYSTEM, m_ServerType,
                        MCMLException.INTERNAL_ABNORMALITY);
            }
        } catch (MCMLException e) {
            throw e;
        } catch (Exception e) {
            throw new MCMLException(e.getMessage(), MCMLException.SYSTEM,
                    m_ServerType, MCMLException.INTERNAL_ABNORMALITY);
        }

        return outputData;
    }

    /**
     * destroy
     */
    public void destroy() {
        // JNI release
        if (m_Encoder != null) {
            m_Encoder.destroy();
        }
        if (m_Decoder != null) {
            m_Decoder.destroy();
        }
    }

}
