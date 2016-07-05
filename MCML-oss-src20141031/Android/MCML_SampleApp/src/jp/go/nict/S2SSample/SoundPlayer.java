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

package jp.go.nict.S2SSample;

import java.util.Vector;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

/**
 * Plays sound.
 * 
 */
public class SoundPlayer extends Thread {
    /** Buffer size */
    private int bufferSize = 8000;
    private int zerodataSize = bufferSize / 2;

    /** Play data */
    private Vector<byte[]> mvSpeechData = new Vector<byte[]>();

    /** Currently playing flag */
    private boolean mFlagPlaying = false;

    /** Player */
    private AudioTrack mAudioTrack = null;

    /** Player operation flag */
    boolean runFlag = false;

    /**
     * Checks whether currently playing
     * 
     * @return true Currently playing
     */
    public boolean isPlaying() {
        return mFlagPlaying;
    }

    /** Play data addition */
    public void addSpeechData(byte[] speechData) {
        mvSpeechData.add(speechData);
    }

    /**
     * Initialization
     */
    public void initialize() {
        if (mAudioTrack == null) {
            mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 16000, // Sampling frequency
                    AudioFormat.CHANNEL_CONFIGURATION_MONO, // Monaural
                    AudioFormat.ENCODING_DEFAULT, // 16bitPCM
                    bufferSize, //  Buffer size
                    AudioTrack.MODE_STREAM);
            mAudioTrack.play();
        }
        runFlag = true;
    }

    /**
     * run
     */
    @Override
    public void run() {
        byte[] speechData = null;
        byte[] zeroData = new byte[zerodataSize];
        java.util.Arrays.fill(zeroData, (byte) 0);

        int iPlayingCheckCounter = 0;
        while (runFlag) {
            if (!mvSpeechData.isEmpty()) {
                mFlagPlaying = true;
                iPlayingCheckCounter = 0;
                speechData = mvSpeechData.remove(0);
                mAudioTrack.write(speechData, 0, speechData.length); // Sets data to be played here.
            } else {
                if (!isPlaying()) {
                    mvSpeechData.add(zeroData);
                } else {
                    iPlayingCheckCounter++;
                    if (iPlayingCheckCounter > 10) {
                        mFlagPlaying = false;
                        iPlayingCheckCounter = 0;
                    }
                }

                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Stops play.
     */
    public void stopAudio() {
        if (mAudioTrack != null) {
            // Stops play.
            mAudioTrack.stop();
            mAudioTrack.release();
            this.runFlag = false;
        }
    }

}
