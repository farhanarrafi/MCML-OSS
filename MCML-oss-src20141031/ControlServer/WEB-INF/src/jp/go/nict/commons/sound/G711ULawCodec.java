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

/**
 * STMD G711 u-Law Codec. Title: STMD G711 u-Law Codec.
 * 
 */
class G711ULawCodec extends G711Codec {
    /**
     * Default Constructor.
     */
    G711ULawCodec() {
        super();
    }

    /**
     * Compress method Implementation.
     */
    @Override
    public ByteBuffer compress(ByteBuffer targetByteBuffer) {
        /* Input Buffer */
        ByteBuffer inputByteBuffer = targetByteBuffer.duplicate()
                .asReadOnlyBuffer();
        inputByteBuffer.order(targetByteBuffer.order());

        /* Allocation Output Buffer for Expanded Samples */
        ByteBuffer outputByteBuffer = ByteBuffer.allocate(inputByteBuffer
                .remaining());
        outputByteBuffer.order(targetByteBuffer.order());
        outputByteBuffer.clear();

        while (inputByteBuffer.hasRemaining()) {
            /*
             * ------------------------------------------------------------------
             * --
             */
            /* Change from 14 bit left justified to 14 bit right justified */
            /* Compute absolute value; adjust for easy processing */
            /*
             * ------------------------------------------------------------------
             * --
             */

            /* A Sample Value extracted from the Buffer */
            short inSample = inputByteBuffer.getShort();

            /* absolute value of linear (input) sample */
            short absno;
            if (inSample < 0) { /* compute 1's complement in case of */
                /* negative samples */
                absno = (short) (((~inSample) >> 2) + 33);
            } else {
                /* NB: 33 is the difference value */
                absno = (short) (((inSample) >> 2) + 33);
            }

            /* between the thresholds for */
            /* A-law and u-law. */
            if (absno > (0x1FFF)) { /* limitation to "absno" < 8192 */
                absno = (0x1FFF);
            }

            /* Determination of sample's segment */
            short segno = 1;
            for (short i = (short) (absno >> 6); (i != 0); i >>= 1) {
                segno++;
            }

            /* Mounting the high-nibble of the log-PCM sample */
            short high_nibble = (short) ((0x0008) - segno);

            /* Mounting the low-nibble of the log PCM sample */
            short low_nibble = (short) ((absno >> segno) /*
                                                          * right shift of
                                                          * mantissa and
                                                          */
            & (0x000F)); /* masking away leading '1' */
            low_nibble = (short) ((0x000F) - low_nibble);

            /* Joining the high-nibble and the low-nibble of the log PCM sample */
            short outSample = (short) ((high_nibble << 4) | low_nibble);

            /* Add sign bit */
            if (inSample >= 0) {
                outSample = (short) (outSample | (0x0080));
            }

            outputByteBuffer.putShort(outSample);
        }

        outputByteBuffer.flip();

        return outputByteBuffer;
    }

    /**
     * Expand method Implementation.
     */
    @Override
    public ByteBuffer expand(ByteBuffer targetByteBuffer) {
        /* Input Buffer */
        ByteBuffer inputByteBuffer = targetByteBuffer.duplicate()
                .asReadOnlyBuffer();
        inputByteBuffer.order(targetByteBuffer.order());

        /* Allocation Output Buffer for Expanded Samples */
        ByteBuffer outputByteBuffer = ByteBuffer.allocate(inputByteBuffer
                .remaining());
        outputByteBuffer.order(targetByteBuffer.order());

        outputByteBuffer.clear();

        while (inputByteBuffer.hasRemaining()) {
            /* A Sample Value extracted from the Buffer */
            short sampleValue = inputByteBuffer.getShort();
            short sign = (sampleValue < 0x0080) ? (short) -1 : (short) 1;
            /* 1's complement of input value */
            short mantissa = (short) ~sampleValue;
            /* extract exponent */
            short exponent = (short) ((mantissa >> 4) & (0x0007));
            /* compute segment number */
            short segment = (short) (exponent + 1);
            /* extract mantissa */
            mantissa = (short) (mantissa & (0x000F));

            /* Compute Quantized Sample (14 bit left justified!) */
            short step = (short) (4 << segment); /* position of the LSB */
            /* = 1 quantization step) */
            outputByteBuffer.putShort((short)
            /* sign */
            (sign
            /* '1', preceding the mantissa */
            * (((0x0080) << exponent)
            /* left shift of mantissa */
            + step * mantissa
            /* 1/2 quantization step */
            + step / 2 - 4 * 33)));
        }

        outputByteBuffer.flip();

        return outputByteBuffer;
    }
}
