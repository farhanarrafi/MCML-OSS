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
 * STMD G711 Codec. Title: STMD G711 Codec.
 * 
 */
public abstract class G711Codec extends Object {
    /* Kinds of Law on Codec Algorism */
    /** u-law algorism. */
    public static final int U_LAW = 0;
    /** A-law algorism. */
    public static final int A_LAW = 1;

    /**
     * Default Constructor.
     */
    G711Codec() {
        super();
    }

    /**
     * Instance Factory method.
     * 
     * @param law
     * @return G711Codec
     */
    public static G711Codec getInstance(int law) {
        G711Codec instance = null;

        /* u-law */
        if (law == U_LAW) {
            instance = (G711Codec) new G711ULawCodec();

            /* Specified law is not supported. */
        } else {
            return null;
        }

        return (G711Codec) instance;
    }

    /**
     * Compress method for Override.
     * 
     * @param targetByteBuffer
     * @return ByteBuffer
     */
    public ByteBuffer compress(ByteBuffer targetByteBuffer) {
        return null;
    }

    /**
     * Expand method for Override.
     * 
     * @param targetByteBuffer
     * @return ByteBuffer
     */
    public ByteBuffer expand(ByteBuffer targetByteBuffer) {
        return null;
    }
}
