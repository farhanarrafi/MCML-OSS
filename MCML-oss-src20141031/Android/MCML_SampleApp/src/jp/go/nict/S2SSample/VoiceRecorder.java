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

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.Enumeration;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;

import jp.go.nict.S2SSample.Event.VoiceRecorderEvent;
import jp.go.nict.S2SSample.Event.VoiceRecorderEventListener;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

/**
 * Implementation class of voice recorder recording voice.
 * 
 */
public class VoiceRecorder {
    /** Flag adding voice to Q voice data queue */
    private static boolean mFlagAddQueue = false;

    /** Recording block size */
    private int mBlockSize = -1;

    /** Voice data queue  */
    private LinkedBlockingQueue<byte[]> mDataQueue = new LinkedBlockingQueue<byte[]>();;

    /** Recorder body */
    private AudioRecord mAudioRecord = null;

    /** Event listener notifying volume */
    protected Vector<VoiceRecorderEventListener> mvArrivalEventListeners = new Vector<VoiceRecorderEventListener>();

    /**
     * Starts recording
     * 
     * @return False if already recorded, otherwise true.
     */
    public boolean startRecord() {
        final int sampleRate = 16000; // Sampling rate 16kHz
        final int notifyFreq = 10; // Number of notifications performed per second.
        final int notifyPeriod = sampleRate / notifyFreq; // Cycle for performing notifications
        final int blockSize = notifyPeriod * 2; // Processed size per notification (Byte count)
        byte[] buffer = new byte[blockSize];
        mBlockSize = blockSize;
        if (isRecording()) {
            return false; // Returns false if recording has already started
        }
      mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
      sampleRate, AudioFormat.CHANNEL_IN_MONO, // Monaural
      AudioFormat.ENCODING_PCM_16BIT, // PCM 16 bit/sample
      sampleRate * 2); // Number of bytes per second.
      
        mAudioRecord.setPositionNotificationPeriod(notifyPeriod); // Notification point setting
        mAudioRecord
                .setRecordPositionUpdateListener(new AudioRecord.OnRecordPositionUpdateListener() {
                    @Override
                    public void onMarkerReached(AudioRecord audiorecord) {
                    }

                    @Override
                    public void onPeriodicNotification(AudioRecord audiorecord) {
                        // Processing when set passage point is reached
                        readData(blockSize);
                    }
                });

        mAudioRecord.startRecording(); // Recording start
        mAudioRecord.read(buffer, 0, blockSize);
        mAudioRecord.read(buffer, 0, blockSize);

        return true;
    }

    /**
     * Reads and voice data and processes
     * 
     * @param length
     *            Size of read voice data
     */
    private void readData(int length) {
        if (isRecording()) {
            try {
                byte[] buffer = new byte[length];
                mAudioRecord.read(buffer, 0, length);

                // Notifies voice volume by event.
                sendEventVoiceVolue(buffer);

                if (mFlagAddQueue) {
                    mDataQueue.put(buffer);
                }
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }

        }
    }

    /**
     * Recording stop
     */
    public void stopRecord() {
        if (isRecording()) {
            mAudioRecord.stop();
            mAudioRecord.release();
        }
    }

    /**
     * Determines whether currently recording or not.
     * 
     * @return True if currently recording, otherwise returns false.
     */
    private boolean isRecording() {
        return mAudioRecord != null
                && mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING;
    }

    /**
     * Inserts recorded voice in queue.
     */
    public void startAddQueue() {
        mDataQueue.clear();
        mFlagAddQueue = true;
    }

    /**
     * Does not insert recorded voice in queue.
     */
    public void stopAddQueue() {
        mFlagAddQueue = false;
    }

    /**
     * Gets BlockSize during recording.
     * 
     * @return BlockSize
     */
    public int getBlockSize() {
        return mBlockSize;

    }

    /**
     * Checks whether queue is empty.
     * 
     * @return {@code true} if queue is empty, otherwise {@code false}
     */
    public boolean isRecordDataEmpty() {
        return mDataQueue.isEmpty();
    }

    /**
     * Gets voice data in queue.
     * 
     * @return retVal
     */
    public byte[] getRecordData() {
        byte[] retVal = null;

        if (!mDataQueue.isEmpty()) {
            retVal = mDataQueue.poll();
        }

        return retVal;
    }

    /**
     * Sends voice volume event.
     * 
     * @param sound
     */
    private void sendEventVoiceVolume(short[] sound) {
        int iToTalVolue = 0;
        int iAverageVolume = 0;

        for (int i = 0; i < sound.length; i++) {
            try {
                iToTalVolue += Math.abs(sound[i]);
            } catch (BufferUnderflowException bue) {
                break;
            }
        }

        iAverageVolume = iToTalVolue / sound.length;

        voiceVolumeEvent(new VoiceRecorderEvent(this, iAverageVolume));

    }

    /**
     * Sends voice volume event.
     * 
     * @param bBuffer
     */
    private void sendEventVoiceVolue(byte[] bBuffer) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bBuffer);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        ShortBuffer soundBuffer = byteBuffer.asShortBuffer();
        short[] sound = new short[bBuffer.length / 2];

        soundBuffer.get(sound);
        sendEventVoiceVolume(sound);
    }

    /**
     * Registers event executed when MCML request completes.
     * 
     * @param voiceRecorderEventListener
     */
    public void addActionListener(
            VoiceRecorderEventListener voiceRecorderEventListener) {
        mvArrivalEventListeners.addElement(voiceRecorderEventListener);
    }

    /**
     * Deletes event executed when MCML request completes.
     * 
     * @param voiceRecorderEventListener
     */
    public void removeActionListener(
            VoiceRecorderEventListener voiceRecorderEventListener) {
        mvArrivalEventListeners.removeElement(voiceRecorderEventListener);
    }

    /**
     * Notifies event when MCML request completes.
     * 
     * @param CompleteEvent
     *            CompleteEvent Completion event
     */
    protected void voiceVolumeEvent(VoiceRecorderEvent voiceRecorderEvent) {
        Vector cloneArrivalEventListeners = (Vector) mvArrivalEventListeners
                .clone();
        Enumeration arrivalEventListenersElements = cloneArrivalEventListeners
                .elements();
        while (arrivalEventListenersElements.hasMoreElements()) {
            VoiceRecorderEventListener listener = (VoiceRecorderEventListener) arrivalEventListenersElements
                    .nextElement();
            listener.voiceVolume(voiceRecorderEvent);
        }
    }

}
