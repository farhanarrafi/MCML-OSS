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

/**
 * SpeexEncoder class.
 * 
 */
public class SpeexEncoder {
    static {
        System.loadLibrary("speex4j");
    }

    private byte[] state;
    private byte[] buf;
    private int buf_in;
    private int frame_size;
    private boolean active;

    /**
     * Constructor
     * 
     * @param quality
     * @param complexity
     * @param vbr
     */
    public SpeexEncoder(int quality, int complexity, int vbr) {
        state = createx(quality, complexity, vbr);
        frame_size = get_frame_size(); // NOTE: must call it AFTER createx()
        buf = new byte[frame_size];
        buf_in = 0;
        active = true;
    }

    /**
     * Default constructor
     */
    public SpeexEncoder() {
        this(8, 3, 1);
    }

    @Override
    protected void finalize() {
        if (active) {
            destroyx(state);
            active = false;
        }
    }

    /**
     * encode
     * 
     * @param raw
     * @param len
     * @return byte[]
     */
    public byte[] encode(byte[] raw, int len) {
        int pos = 0;
        int nframes = 0;
        byte[] head = null;

        if (buf_in > 0) {
            if (buf_in + len < frame_size) {
                System.arraycopy(raw, 0, buf, buf_in, len);
                buf_in += len;
                return new byte[0];
            }
            pos = frame_size - buf_in;
            System.arraycopy(raw, 0, buf, buf_in, pos);
            head = buf;
            buf_in = 0;
            nframes = 1;
        }

        int end = (len - pos) / frame_size;
        nframes += end;

        end = pos + end * frame_size;
        if (end < len) {
            buf_in = len - end;
            System.arraycopy(raw, end, buf, 0, buf_in);
        }

        return encodex(state, head, raw, pos, nframes);
    }

    /**
     * encode
     * 
     * @param raw
     * @return byte[]
     */
    public byte[] encode(byte[] raw) {
        return encode(raw, raw.length);
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

    private native byte[] createx(int q, int c, int v);

    private native void destroyx(byte[] state);

    private native int get_frame_size();

    private native byte[] encodex(byte[] state, byte[] head, byte[] rest,
            int start, int nframes);
}
