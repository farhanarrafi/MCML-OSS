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

import java.util.ArrayList;

import jp.go.nict.mcml.serverap.common.AudioConverter;
import jp.go.nict.mcml.serverap.common.EngineCtrl;
import jp.go.nict.mcml.serverap.common.MCMLData;
import jp.go.nict.mcml.serverap.common.ServerApLogger;
import jp.go.nict.mcml.serverap.common.ServerApTimer;

/**
 * SRTimer class.
 *
 */
public class SRTimer extends ServerApTimer {
    // ------------------------------------------
    // private member constants(class field)
    // ------------------------------------------
    private static final int DEFAULT_SAMPLING_FREQUENCY = 16000;
    private static final short DEFAULT_SAMPLING_BIT = 16;
    private static final short DEFAULT_CHANNEL_NUM = 1;
    private static final int DSR_SPEECH_LENGTH_PER_ONE_FRAME = 240;
    private static final int DSR_PACKET_LENGTH = 168;

    // ------------------------------------------
    // private member valuables(instance field)
    // ------------------------------------------
    private long m_SpeechDataSize;
    private long m_TimeoutTime;
    private long m_Ratio;

    // ------------------------------------------
    // public member functions
    // ------------------------------------------
    /**
     * Constructor
     *
     * @param engineCtrl
     * @param timeoutTime
     * @param ratio
     */
    public SRTimer(EngineCtrl engineCtrl, long timeoutTime, long ratio) {
        // create TimerTask.
        super(new SRTimerTask(engineCtrl));

        // create Scheduler.
        m_TimeoutTime = timeoutTime;
        m_Ratio = ratio;
    }

    /**
     * addSpeechDataSize
     *
     * @param speechDatas
     */
    public void addSpeechDataSize(ArrayList<byte[]> speechDatas) {
        for (int i = 0; i < speechDatas.size(); i++) {
            addSpeechDataSize(speechDatas.get(i));
        }
    }

    /**
     * addSpeechDataSize
     *
     * @param speechData
     */
    public void addSpeechDataSize(byte[] speechData) {
        m_SpeechDataSize += speechData.length;
    }

    /**
     * start
     *
     * @param audioType
     * @param data
     * @return boolean
     */
    public boolean start(AudioConverter.AudioType audioType, MCMLData data) {
        // set MCMLData for timeout.
        ((SRTimerTask) m_TimerTask).setMCMLData(data);

        // calculate UtteranceLength.
        double utteranceLength = calcUtteranceLength(audioType);
        if (utteranceLength < 0) {
            ServerApLogger.getInstance().writeError(
                    "Invalid utterance length.:" + utteranceLength);
            return false;
        }

        // calculate UtteranceLength ratio.
        double ratioTimeout = utteranceLength * (double) m_Ratio / (double) 100;

        // Timeout Timer start.
        if (m_TimeoutTime < ratioTimeout) {
            start(m_TimeoutTime);
        } else {
            start((long) ratioTimeout);
        }

        return true;
    }

    /**
     * stop
     */
    @Override
    public void stop() {
        super.stop();

        // Speech Data Size clear.
        m_SpeechDataSize = 0;
    }

    // ------------------------------------------
    // private member functions
    // ------------------------------------------
    private double calcUtteranceLength(AudioConverter.AudioType audioType) {
        double utteranceLength = 0;

        if (audioType == AudioConverter.AudioType.DSR) {
            utteranceLength = calcUtteranceLengthFromDSR();
        } else if (audioType == AudioConverter.AudioType.ADPCM) {
            utteranceLength = calcUtteranceLengthFromADPCM();
        } else if (audioType == AudioConverter.AudioType.Raw) {
            utteranceLength = calcUtteranceLengthFromPCM();
        } else if (audioType == AudioConverter.AudioType.Speex) {
            utteranceLength = calcUtteranceLengthFromSpeex();
        } else {
            ServerApLogger.getInstance().writeError("Unexpected AudioType.");
            return -1;
        }

        return utteranceLength;
    }

    private double calcUtteranceLengthFromDSR() {
        if (m_SpeechDataSize < 0) {
            ServerApLogger.getInstance().writeError(
                    "calcUtteranceLengthFromDSR() failed.");
            return -1;
        }

        return m_SpeechDataSize * DSR_SPEECH_LENGTH_PER_ONE_FRAME
                / DSR_PACKET_LENGTH;
    }

    private double calcUtteranceLengthFromADPCM() {
        if (m_SpeechDataSize < 0) {
            ServerApLogger.getInstance().writeError(
                    "calcUtteranceLengthFromADPCM() failed.");
            return -1;
        }
        return ((m_SpeechDataSize * 4) * 1000)
                / (DEFAULT_SAMPLING_FREQUENCY * (DEFAULT_SAMPLING_BIT / 8) * DEFAULT_CHANNEL_NUM);
    }

    private double calcUtteranceLengthFromSpeex() {
        if (m_SpeechDataSize < 0) {
            ServerApLogger.getInstance().writeError(
                    "calcUtteranceLengthFromSpeex() failed.");
            return -1;
        }
        // use 20 as a "high enough" value for the speex compression ratio.
        return ((m_SpeechDataSize * 20) * 1000)
                / (DEFAULT_SAMPLING_FREQUENCY * (DEFAULT_SAMPLING_BIT / 8) * DEFAULT_CHANNEL_NUM);
    }

    private double calcUtteranceLengthFromPCM() {
        if (m_SpeechDataSize < 0) {
            ServerApLogger.getInstance().writeError(
                    "calcUtteranceLengthFromPCM() failed.");
            return -1;
        }
        return (m_SpeechDataSize * 1000)
                / (DEFAULT_SAMPLING_FREQUENCY * (DEFAULT_SAMPLING_BIT / 8) * DEFAULT_CHANNEL_NUM);
    }
}
