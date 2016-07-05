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

package jp.go.nict.speex4j;

import jp.go.nict.mcml.serverap.common.ServerApLogger;

/**
 * SpeexDecoder class.
 * 
 */
public class SpeexDecoder {
    static {
        System.loadLibrary("speex4j");
    }

    private byte[] state; // Saves speex library state
    private byte[] buf; // Buffer for decoding
    private int buf_in; // Data volume in buf
    private int frame_size; // byte size
    private boolean active;

    /**
     * Default constructor
     */
    public SpeexDecoder() {
        buf = new byte[256];
        buf_in = 0;
        state = createx();
        frame_size = get_frame_size(); // NOTE: must call it AFTER createx()
        active = true;
    }

    @Override
    protected void finalize() {
        if (active) {
            destroyx(state);
            active = false;
        }
    }

    /**
     * decode
     * 
     * @param encdata
     * @param len
     * @return byte[]
     */
    public byte[] decode(byte[] encdata, int len) throws Exception {
        byte[] result = null;
        //
        // N bytes
        // +---+---------------------------+---+-----
        // | N | encoded data | N | ... encdata[]
        // +---+---------------------------+---+------
        // N should be <= 110  (Note that as Java byte is signed, can only express to 127)
        //
        try {
            int nframes = 0;
            int pos = 0;
            byte[] head = null;

            if (buf_in > 0) { // Some parts were not completely decoded the last time
                int nb = buf[0];
                if (buf_in - 1 + len < nb) { // Cannot decode even if added this time
                    System.arraycopy(encdata, 0, buf, buf_in, len);
                    buf_in += len;
                    return new byte[0];
                }

                // Moves remaining part to buf
                pos = nb - buf_in + 1;
                System.arraycopy(encdata, 0, buf, buf_in, pos);
                nframes = 1;
                head = buf;
            }

            // Counts data of how many frames came this time.
            int end = pos;
            while (end < len) {
                // an unexpected format
                if (encdata[end] + 1 <= 0) {
                    ServerApLogger.getInstance().writeError(
                            "Bad speex data format");
                    throw new Exception();
                }
                // KDL#13 end
                if (encdata[end] + 1 > len - end) {
                    break;
                }
                ++nframes;
                end += encdata[end] + 1;
            }

            buf_in = 0;

            if (end < len) { // Saves amount which could not be decoded this time in buf.
                buf_in = len - end;
                System.arraycopy(encdata, end, buf, 0, buf_in);
            }

            result = decodex(state, head, encdata, pos, nframes);
        } catch (Exception e) {
            ServerApLogger.getInstance().writeDebug("decode error");
            throw e;
        }
        return result;
    }

    /**
     * decode
     * 
     * @param encdata
     * @return byte[]
     */
    public byte[] decode(byte[] encdata) throws Exception {
        return decode(encdata, encdata.length);
    }

    /**
     * destroy
     */
    public void destroy() {
        if (active) {
            destroyx(state);
            active = false;
        }
    }

    private native byte[] createx();

    private native void destroyx(byte[] state);

    private native int get_frame_size();

    // enc[] is stored with encode data of one frame (nb bute) and decode data of one frame from raw[start].
    private native byte[] decodex(byte[] state, byte[] head, byte[] encdata,
            int pos, int nframes);
}
