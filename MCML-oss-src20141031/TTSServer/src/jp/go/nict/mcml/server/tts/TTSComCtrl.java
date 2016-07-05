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

package jp.go.nict.mcml.server.tts;

import java.io.IOException;

import jp.go.nict.mcml.servlet.MCMLException;

/**
 * TTSComCtrl class.
 * 
 * 
 */
public class TTSComCtrl {

    // ------------------------------------------
    // private member variable(instance field)
    // ------------------------------------------
    private TTSClientSocketForEngine m_Socket;

    // ------------------------------------------
    // public member function
    // ------------------------------------------
    TTSComCtrl() {
        m_Socket = null;
    }

    /**
     * connect
     * 
     * @param ipAddress
     * @param port
     * @param clientHost
     * @param clientPost
     * @param retryTimes
     * @param retryInterval
     * @param engineNo
     * @return boolean
     * @throws IOException
     * @throws IllegalStateException
     * @throws InterruptedException
     */
    public boolean connect(String ipAddress, int port, String clientHost,
            int clientPost, int retryTimes, int retryInterval, int engineNo)
            throws IOException, IllegalStateException, InterruptedException {
        TTSProperties prop = TTSProperties.getInstance();

        // check InputSocket already opened.
        if (m_Socket != null) {
            throw new IllegalStateException("InputSocket already opened.");
        }

        // create Socket Class.
        m_Socket = new TTSClientSocketForEngine(MCMLException.TTS);

        // ttsX is 1 origin
        engineNo++;

        // connect start.
        return m_Socket.connect(ipAddress, port, clientHost, clientPost,
                retryTimes, retryInterval, "TTS_" + prop.getLanguage1()
                        + engineNo + ".port");
    }

    /**
     * Sends data.
     * 
     * @param data
     * @throws MCMLException
     */
    public void sendData(byte[] data) throws MCMLException {
        try {
            m_Socket.sendData(data);
        } catch (IOException exp) {
            throw new MCMLException("connection with the engine is closed",
                    MCMLException.ERROR, MCMLException.TTS,
                    MCMLException.ENGINE_CLOSED);
        }
        // normal end
        return;
    }

    /**
     * Receives data.
     * 
     * @return byte[]
     * @throws MCMLException
     */
    public byte[] recieveData() throws MCMLException {
        byte[] recievedData = null;
        TTSFrameData ttsData = new TTSFrameData();

        try {
            byte[] frameHeader = m_Socket
                    .receiveData(TTSFrameData.TTS_FRAME_HEADER_BYTE_LENGTH);
            // check command no
            if (ttsData.getCommandNo(frameHeader) != TTSFrameData.TTS_COMMAND_CONVERT_RESPONSE) {
                // error case
                throw new MCMLException("wrong command no",
                        MCMLException.ERROR, MCMLException.TTS,
                        MCMLException.ABNORMAL_DATA_FORMAT);
            }
            // check error code
            int errorCode = ttsData.getErrorCode(frameHeader);
            if (errorCode != TTSFrameData.TTS_RESULT_CODE_NO_ERROR) {
                // error case
                throw new MCMLException(
                        "New XIMERA engine responsed error code:0x"
                                + String.format("%08x", errorCode),
                        MCMLException.ERROR, MCMLException.TTS,
                        MCMLException.OTHER_ERROR);
            }
            // check frame data size
            int dataSize = ttsData.getFrameDataSize(frameHeader);
            if (dataSize <= 0) {
                // error case
                throw new MCMLException("wrong frame data size",
                        MCMLException.ERROR, MCMLException.TTS,
                        MCMLException.ABNORMAL_DATA_FORMAT);
            }

            byte[] frameData = m_Socket.receiveData(dataSize);
            // check wave data size
            int waveDataSize = ttsData.getWaveDataSize(frameData);
            if (waveDataSize <= 0) {
                // error case
                throw new MCMLException("wrong wave data size",
                        MCMLException.ERROR, MCMLException.TTS,
                        MCMLException.ABNORMAL_DATA_FORMAT);
            }
            recievedData = ttsData.getWaveData(frameData, waveDataSize);
        } catch (IOException exp) {
            throw new MCMLException("connection with the engine is closed",
                    MCMLException.ERROR, MCMLException.TTS,
                    MCMLException.ENGINE_CLOSED);
        }

        // normal end
        return recievedData;
    }

    /**
     * closeEngineSocket
     * 
     * @throws IOException
     */
    public void closeEngineSocket() throws IOException {
        m_Socket.disconnect();
        m_Socket = null;
        return;
    }

}
