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

package jp.go.nict.mcml.servlet.control.dispatcher.connector;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import javax.servlet.http.HttpSession;

import jp.go.nict.mcml.com.client.ResponseData;
import jp.go.nict.mcml.servlet.control.ControlServerProperties;
import jp.go.nict.mcml.servlet.control.dispatcher.database.ServerDatabase;
//import jp.go.nict.mcml.servlet.MCMLLogger;
import jp.go.nict.mcml.xml.MCMLStatics;
import jp.go.nict.mcml.xml.XMLTypeTools;

import org.apache.log4j.Logger;

import com.MCML.AudioType;
import com.MCML.MCMLDoc;

/**
 * Connector class.
 * 
 * @version 4.0
 * @since 20120921
 */
public abstract class Connector {
    private static final Logger LOG = Logger.getLogger(Connector.class
            .getName());
    // ------------------------------------------
    // private member constants
    // ------------------------------------------
    private static final long SAMPLINGFREQUENCY_16K = 16000;
    private static final long SAMPLINGFREQUENCY_8K = 8000;
    private static final short DEFAULT_SAMPLING_BIT = 16;
    private static final short DEFAULT_CHANNEL_QTY = 1;
    private static final int DSR_SPEECH_LENGTH_PER_ONE_FRAME = 240;
    private static final int DSR_PACKET_LENGTH = 168;
    private static final int COFFICIENT_CONVERT_ADPCM_TO_PCM = 4;

    // ------------------------------------------
    // protected member variables
    // ------------------------------------------
    protected int listID; // server list data ID
    protected float coefficientASRTimeout; // coefficient ASR timeout
    protected int timeoutMilliSeconds; // timeout milliseconds
    protected boolean isTimeoutTimerTask; // time out(socket only)

    // ------------------------------------------
    // private member variables
    // ------------------------------------------
    private int totalTimeoutMilliSeconds; // total timeout milliseconds
    private String audioFormatValue; // audio format
    private long samplingRateValue; // sampling rate
    private short channelQtyValue; // number of channels

    // ------------------------------------------
    // public member function
    // ------------------------------------------
    /**
     * constructor
     * 
     * @param listID
     * @param coefficientASRTimeout
     */
    public Connector(int listID, float coefficientASRTimeout) {
        this.listID = listID;
        this.coefficientASRTimeout = coefficientASRTimeout;
        timeoutMilliSeconds = ControlServerProperties.getInstance()
                .getTimeoutMilliSeconds();

        // for ASR timeout
        totalTimeoutMilliSeconds = -1;
        audioFormatValue = null;
        samplingRateValue = SAMPLINGFREQUENCY_16K;
        channelQtyValue = DEFAULT_CHANNEL_QTY;

        // for timeout task
        isTimeoutTimerTask = false;
    }

    /**
     * send request to server and receive response from server
     * 
     * @param mcmlDoc
     * @param binariesList
     * @param session
     * @return ResponseData
     * @throws Exception
     */
    public abstract ResponseData request(MCMLDoc mcmlDoc,
            ArrayList<byte[]> binariesList, HttpSession session)
            throws Exception;

    /** close connection to server */
    public abstract void closeConnection();

    // ------------------------------------------
    // protected member function
    // ------------------------------------------
    // get timeout milliseconds
    protected int getTimeoutMilliSeconds(String xmlData, MCMLDoc mcmlDoc,
            ArrayList<byte[]> binariesList)
    // throws Exception
            throws IOException, Exception {

        String message;
        // has XML Data
        if (xmlData != null && !xmlData.isEmpty()) {
            String service = XMLTypeTools.getService(mcmlDoc.MCML.first());

            if (service.equals(MCMLStatics.SERVICE_ASR)) {
                // get AudioType parameters
                AudioType audioType = XMLTypeTools.getAudioType(mcmlDoc.MCML
                        .first());
                audioFormatValue = audioType.Signal.first().AudioFormat
                        .getValue();
                int samplingRate = audioType.Signal.first().SamplingRate
                        .getValue();
                samplingRateValue = SAMPLINGFREQUENCY_16K;
                if (samplingRate == MCMLStatics.SAMPLING_FREQUENCY_16K) {
                    samplingRateValue = SAMPLINGFREQUENCY_16K;
                } else if (samplingRate == MCMLStatics.SAMPLING_FREQUENCY_8K) {
                    samplingRateValue = SAMPLINGFREQUENCY_8K;
                }
                // channel
                int channelQty = audioType.Signal.first().ChannelQty.getValue();

                channelQtyValue = (short) channelQty;
                totalTimeoutMilliSeconds = 0;
            } else {
                // not ASR
                totalTimeoutMilliSeconds = -1;
            }
        }

        // calculate timeout
        if (totalTimeoutMilliSeconds >= 0) {
            // system timeout is infinite
            if (timeoutMilliSeconds == 0) {
                return timeoutMilliSeconds;
            }
            // request is not split
            // if (totalTimeoutMilliSeconds == 0 && mcmlType != null) {
            if (totalTimeoutMilliSeconds == 0 && mcmlDoc != null
                    && mcmlDoc.MCML.exists()) {
                if (binariesList != null && !binariesList.isEmpty()) {
                    totalTimeoutMilliSeconds = calculateTimeoutMilliseconds(binariesList);
                    message = "TRACE: Connector.getTimeoutMilliSeconds() calculateTimeoutMilliseconds() end";
                    LOG.debug(message);
                    if (totalTimeoutMilliSeconds > timeoutMilliSeconds) {
                        return totalTimeoutMilliSeconds; // return ASR timeout
                                                         // value
                    }
                }

                // request is split
            } else {
                // terminate
                if (binariesList == null || binariesList.isEmpty()) {
                    // if (mcmlType == null) {
                    if (mcmlDoc == null || !mcmlDoc.MCML.exists()) {
                        if (totalTimeoutMilliSeconds > timeoutMilliSeconds) {
                            return totalTimeoutMilliSeconds; // return ASR
                                                             // timeout value
                        }
                    }

                    // not terminate
                } else {
                    // add timeout value
                    totalTimeoutMilliSeconds += calculateTimeoutMilliseconds(binariesList);
                }
            }
        }

        // return common timeout milliseconds
        return this.timeoutMilliSeconds;
    }

    // reset timeout counter
    protected void resetTimeoutCounter() {
        ServerDatabase.getInstance().resetTimeoutCounter(listID);
    }

    // update timeout counter
    protected void updateTimeoutCounter() {
        ServerDatabase.getInstance().updateTimeoutCounter(listID);
    }

    // set time out timer task
    protected void setTimeoutTimerTask() {
        isTimeoutTimerTask = true;
    }

    // ------------------------------------------
    // private member function
    // ------------------------------------------
    // calculate timeout milliseconds
    private int calculateTimeoutMilliseconds(ArrayList<byte[]> binariesList) {
        long utteranceLength;

        // in case data format is DSR
        if (audioFormatValue.equalsIgnoreCase(MCMLStatics.AUDIO_DSR)) {
            ByteBuffer bb = ByteBuffer.wrap(binariesList.get(0));
            utteranceLength = calculateDSRUtteranceLength(bb);
        } else {
            int dataSize = binariesList.get(0).length;
            // in case data format is ADPCM
            if (audioFormatValue.equalsIgnoreCase(MCMLStatics.AUDIO_ADPCM)) {
                dataSize *= COFFICIENT_CONVERT_ADPCM_TO_PCM;
            }
            long channelQtyValueTemp = 1;
            if (channelQtyValue >= 1) {
                channelQtyValueTemp = channelQtyValue;
            }

            utteranceLength = (dataSize * 1000)
                    / (samplingRateValue * (DEFAULT_SAMPLING_BIT / 8) * channelQtyValueTemp);
        }
        return ((int) (coefficientASRTimeout * utteranceLength));
    }

    // calculate DSR data UtteranceLength
    private long calculateDSRUtteranceLength(ByteBuffer dsrData) {
        long utterancelength = 0;
        int dataSize = dsrData.array().length;
        if (dataSize > 0) {
            utterancelength = dataSize * DSR_SPEECH_LENGTH_PER_ONE_FRAME
                    / DSR_PACKET_LENGTH;
        }
        return utterancelength;
    }
}
