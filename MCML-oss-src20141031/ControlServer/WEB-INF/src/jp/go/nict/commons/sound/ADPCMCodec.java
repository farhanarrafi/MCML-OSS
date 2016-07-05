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

package jp.go.nict.commons.sound;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * STMD ADPCM Codec.
 * 
 * Title: STMD ADPCM Codec.
 * 
 */
public class ADPCMCodec extends Object {

    /**
     * a number of ADPCM bits per sample. 2 bits per sample (16 kbit/s).
     */
    public static final short BITRATE_16K = 2;

    /**
     * a number of ADPCM bits per sample. 3 bits per sample (24 kbit/s).
     */
    public static final short BITRATE_24K = 3;

    /**
     * a number of ADPCM bits per sample. 4 bits per sample (32 kbit/s).
     */
    public static final short BITRATE_32K = 4;

    /**
     * a number of ADPCM bits per sample. 5 bits per sample (40 kbit/s).
     */
    public static final short BITRATE_40K = 5;

    /**
     * G726 Codec instance.
     */
    private G726Codec g726Codec = null;

    /** a Law on log-PCM(g.711) algorism. */
    private int law;

    /**
     * Default Constructor.
     */
    public ADPCMCodec() {
        super();
        /* Setting Default Values */
        this.law = G711Codec.U_LAW;
        this.g726Codec = new G726Codec(this.law);
    }

    /**
     * Constructor.
     * 
     * @param logPcmLaw
     */
    public ADPCMCodec(int logPcmLaw) {
        super();
        this.law = logPcmLaw;
        this.g726Codec = new G726Codec(this.law);
    }

    /**
     * Setter of a Law on log-PCM(g.711).
     * 
     * @param logPcmLaw
     */
    public void setLaw(int logPcmLaw) {
        this.law = logPcmLaw;
        this.g726Codec.setLaw(this.law);
    }

    /**
     * Getter of a Law on log-PCM(g.711).
     * 
     * @return law
     */
    public int getLaw() {
        return this.law;
    }

    /**
     * ADPCM Encoder (Byte array version). This method call encode() of
     * ByteBuffer version in the process.
     * 
     * @param inputByteArray
     * @param endian
     * @param bitrate
     * @param doReset
     * @return バイト配列
     */
    public byte[] encode(byte[] inputByteArray, ByteOrder endian,
            short bitrate, boolean doReset) {
        ByteBuffer outputByteBuffer = ByteBuffer.wrap(inputByteArray);
        outputByteBuffer = outputByteBuffer.order(endian);
        outputByteBuffer = this.encode(outputByteBuffer, bitrate, doReset);

        return outputByteBuffer.array();
    }

    /**
     * ADPCM Encoder (ByteBuffer version).
     * 
     * @param targetByteBuffer
     * @param bitrate
     * @param doReset
     * @return ByteBuffer
     */
    public ByteBuffer encode(ByteBuffer targetByteBuffer, short bitrate,
            boolean doReset) {
        /* Initialize a ByteBuffer for output */
        ByteBuffer byteBuffer = targetByteBuffer.duplicate();
        byteBuffer.order(targetByteBuffer.order());

        /* Initialize Codecs */
        G711Codec g711Codec = G711Codec.getInstance(this.law);

        /* Compless by G.711 */
        byteBuffer = g711Codec.compress(byteBuffer);

        /* Encode by G.726 */
        byteBuffer = g726Codec.encode(byteBuffer, this.law, bitrate, doReset);

        /* Pack data */
        byteBuffer = this.pack(byteBuffer, bitrate);

        return byteBuffer;
    }

    /**
     * ADPCM Encoder (G.711 ByteBuffer version).
     * 
     * @param targetByteBuffer
     * @param bitrate
     * @param doReset
     * @return ByteBuffer
     */
    public ByteBuffer encodeForG711(ByteBuffer targetByteBuffer, short bitrate,
            boolean doReset) {
        /* Initialize a ByteBuffer for output */
        ByteBuffer byteBuffer = targetByteBuffer.duplicate();
        byteBuffer.order(targetByteBuffer.order());

        /* Encode by G.726 */
        byteBuffer = g726Codec.encode(byteBuffer, this.law, bitrate, doReset);

        /* Pack data */
        byteBuffer = this.pack(byteBuffer, bitrate);

        return byteBuffer;
    }

    /**
     * ADPCM Decoder (Byte array version). This method call decode() of
     * ByteBuffer version in the process.
     * 
     * @param inputByteArray
     * @param endian
     * @param bitrate
     * @param doReset
     * @return byte[]
     */
    public byte[] decode(byte[] inputByteArray, ByteOrder endian,
            short bitrate, boolean doReset) {
        ByteBuffer outputByteBuffer = ByteBuffer.wrap(inputByteArray);

        outputByteBuffer = this.decode(outputByteBuffer.order(endian), bitrate,
                doReset);

        return outputByteBuffer.array();
    }

    /**
     * ADPCM Decoder (ByteBuffer version).
     * 
     * @param targetByteBuffer
     *            指定されたバッファ内に残っているデータをデコードする.
     * @param bitrate
     * @param doReset
     * @return a decoded ByteBuffer. 新しいバッファの位置は 0,
     *         容量とリミットはこのバッファ内に残っているバイト数になります. マークは定義されません.
     * @see ByteBuffer
     */
    public ByteBuffer decode(ByteBuffer targetByteBuffer, short bitrate,
            boolean doReset) {

        /* Initialize a ByteBuffer for output */
        ByteBuffer byteBuffer = targetByteBuffer.duplicate();
        byteBuffer.order(targetByteBuffer.order());

        /* Initialize Codecs */
        G711Codec g711Codec = G711Codec.getInstance(this.law);

        /* Unpack data */
        byteBuffer = this.unpack(byteBuffer, bitrate);

        /* Decode by G.726 */
        byteBuffer = g726Codec.decode(byteBuffer, this.law, bitrate, doReset);

        /* Expand by G.711 */
        byteBuffer = g711Codec.expand(byteBuffer);

        return byteBuffer;
    }

    /**
     * ADPCM Decoder (G.711 ByteBuffer version).
     * 
     * @param targetByteBuffer
     * @param bitrate
     * @param doReset
     * @return ByteBuffer
     */
    public ByteBuffer decodeForG711(ByteBuffer targetByteBuffer, short bitrate,
            boolean doReset) {
        /* Initialize a ByteBuffer for output */
        ByteBuffer byteBuffer = targetByteBuffer.duplicate();
        byteBuffer.order(targetByteBuffer.order());

        /* Unpack data */
        byteBuffer = this.unpack(byteBuffer, bitrate);

        /* Decode by G.726 */
        byteBuffer = g726Codec.decode(byteBuffer, this.law, bitrate, doReset);

        return byteBuffer;
    }

    /**
     * ADPCM data Pack method
     * 
     * @param targetByteBuffer
     * @param bitrate
     * @return a packed ByteBuffer.
     * @see ByteBuffer
     */
    public ByteBuffer pack(ByteBuffer targetByteBuffer, short bitrate) {
        /* Input Buffer */
        ByteBuffer inputByteBuffer = targetByteBuffer.duplicate()
                .asReadOnlyBuffer();
        inputByteBuffer.order(targetByteBuffer.order());
        inputByteBuffer.clear();

        /* Output Buffer */
        ByteBuffer outputByteBuffer;

        if (bitrate == BITRATE_32K) {
            /* Allocation Output Buffer */
            outputByteBuffer = ByteBuffer
                    .allocate((inputByteBuffer.capacity() + 3) / 4);
            outputByteBuffer.order(inputByteBuffer.order());
            outputByteBuffer.clear();

            while (inputByteBuffer.hasRemaining()) {
                byte sampleByte1 = (byte) inputByteBuffer.getShort();
                byte sampleByte2 = 0;
                if (inputByteBuffer.hasRemaining()) {
                    sampleByte2 = (byte) (inputByteBuffer.getShort() << bitrate);
                }
                outputByteBuffer.put((byte) (sampleByte1 | sampleByte2));
            }
        } else {
            return null;
        }

        outputByteBuffer.clear();

        return outputByteBuffer;
    }

    /**
     * ADPCM data Unpack method
     * 
     * @param targetByteBuffer
     * @param bitrate
     * @return a unpacked ByteBuffer.
     * @see ByteBuffer
     */
    public ByteBuffer unpack(ByteBuffer targetByteBuffer, short bitrate) {
        /* Input Buffer */
        ByteBuffer inputByteBuffer = targetByteBuffer.duplicate()
                .asReadOnlyBuffer();
        inputByteBuffer.order(targetByteBuffer.order());

        /* Output Buffer */
        ByteBuffer outputByteBuffer;

        if (bitrate == BITRATE_32K) {
            /* Allocation Output Buffer */
            outputByteBuffer = ByteBuffer
                    .allocate(inputByteBuffer.remaining() * 4);
            outputByteBuffer.order(targetByteBuffer.order());
            outputByteBuffer.clear();

            while (inputByteBuffer.hasRemaining()) {
                byte sampleByte = inputByteBuffer.get();
                outputByteBuffer.putShort((short) (sampleByte & 0x000f));
                outputByteBuffer
                        .putShort((short) ((sampleByte & 0x00f0) >> bitrate));
            }
        } else if (bitrate == BITRATE_16K) {
            /* Allocation Output Buffer */
            outputByteBuffer = ByteBuffer
                    .allocate(inputByteBuffer.remaining() * 8);
            outputByteBuffer.order(targetByteBuffer.order());
            outputByteBuffer.clear();

            while (inputByteBuffer.hasRemaining()) {
                byte sampleByte = inputByteBuffer.get();
                outputByteBuffer.putShort((short) (sampleByte & 0x000f));
                outputByteBuffer
                        .putShort((short) ((sampleByte & 0x00f0) >> bitrate));
                outputByteBuffer
                        .putShort((short) ((sampleByte & 0x0f00) >> (bitrate * 2)));
                outputByteBuffer
                        .putShort((short) ((sampleByte & 0xf000) >> (bitrate * 3)));
            }
        } else {
            return null;
        }

        outputByteBuffer.flip();

        return outputByteBuffer;
    }
}
