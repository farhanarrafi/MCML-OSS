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

import java.io.UnsupportedEncodingException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Calendar;
import java.util.Date;

import jp.go.nict.mcml.servlet.MCMLException;

/**
 * TTSFrameData class.
 * 
 * 
 */
public class TTSFrameData {
    // ------------------------------------------
    // public member constant
    // ------------------------------------------
    /** TTS frame header byte length */
    public static final int TTS_FRAME_HEADER_BYTE_LENGTH = 24;
    /** TTS conversion request header byte length */
    public static final int TTS_CONVERT_REQUEST_HEADER_BYTE_LENGTH = 88;
    /** TTS frame time byte length */
    public static final int TTS_FRAME_TIME_BYTE_LENGTH = 8;
    /** TTS frame locale byte length */
    public static final int TTS_FRAME_LOCALE_BYTE_LENGTH = 8;
    /** TTS frame voice font name byte length */
    public static final int TTS_FRAME_VOICEFONT_NAME_BYTE_LENGTH = 64;
    /** TTS frame suffix */
    public static final ByteOrder TTS_FRAME_ENDIAN = ByteOrder.LITTLE_ENDIAN;
    /** TTS command conversion request */
    public static final int TTS_COMMAND_CONVERT_REQUEST = 0x01001000;
    /** TTS command conversion response  */
    public static final int TTS_COMMAND_CONVERT_RESPONSE = 0x01001001;
    /** TTS character set */
    public static final String TTS_CHARSET = "UTF-8";
    /** TTS results code (No error) */
    public static final int TTS_RESULT_CODE_NO_ERROR = 0x00000000;
    /** TTS WAVE data start position */
    public static final int TTS_WAVE_DATA_START_POSITION = 52; // contents data
                                                               // size field(4)

    // + Lipsync info field(4)
    // + RIFF header field(44)

    // ------------------------------------------
    // public member functions
    // ------------------------------------------
    /**
     * Gets frame packet with byte array.
     * 
     * @param commandNo
     *             Command number
     * @param sequenceNo
     *             Sequence number
     * @param numerator
     * @param denominator
     * @param date
     * @param isLipSync
     * @param partitionDataSize
     * @param speakRate
     * @param locale
     * @param voiceFontName
     * @param contents
     * @param charset
     * @return  Frame packet
     * @throws MCMLException
     */
    public byte[] setSendFrame(int commandNo, short sequenceNo, byte numerator,
            byte denominator, Date date, boolean isLipSync,
            int partitionDataSize, float speakRate, String locale,
            String voiceFontName, String contents, String charset)
            throws MCMLException {

        byte[] framePacket = null;
        if (contents == null || contents.isEmpty()) {
            return null;
        }
        try {
            byte[] contentsData = contents.getBytes(charset);
            int contentsDataSize = contentsData.length;
            int frameSize = TTS_FRAME_HEADER_BYTE_LENGTH
                    + TTS_CONVERT_REQUEST_HEADER_BYTE_LENGTH + contentsDataSize;
            framePacket = new byte[frameSize];

            ByteBuffer buf = ByteBuffer.wrap(framePacket);
            buf.order(TTS_FRAME_ENDIAN);
            buf.put(setFrameHeader(commandNo, frameSize, sequenceNo, numerator,
                    denominator, date), 0, TTS_FRAME_HEADER_BYTE_LENGTH);
            buf.put(setFrameData(contentsDataSize, isLipSync,
                    partitionDataSize, speakRate, locale, voiceFontName,
                    contentsData, charset), 0,
                    TTS_CONVERT_REQUEST_HEADER_BYTE_LENGTH + contentsDataSize);
            framePacket = buf.array();
        } catch (UnsupportedEncodingException uee) {
            // Unsupported StringCode.
            throw new MCMLException("Unsupported StringCode.",
                    MCMLException.ERROR, MCMLException.TTS,
                    MCMLException.ABNORMAL_DATA_FORMAT);
        } catch (BufferOverflowException boe) {
            // Buffer Overflow.
            throw new MCMLException("Buffer Overflow.", MCMLException.ERROR,
                    MCMLException.TTS, MCMLException.ABNORMAL_DATA_FORMAT);
        } catch (IndexOutOfBoundsException ie) {
            // Index Out Of Bounds.
            throw new MCMLException("Index Out Of Bounds.",
                    MCMLException.ERROR, MCMLException.TTS,
                    MCMLException.ABNORMAL_DATA_FORMAT);
        }

        return framePacket;
    }

    /**
     * Gets CommandNo.
     * 
     * @param frameHeader
     * @return CommandNo
     * @throws MCMLException
     */
    public int getCommandNo(byte[] frameHeader) throws MCMLException {
        int commandNo;
        try {
            ByteBuffer inputBuf = ByteBuffer.wrap(frameHeader);
            inputBuf.order(TTS_FRAME_ENDIAN);
            commandNo = inputBuf.getInt();
        } catch (BufferUnderflowException bue) {
            // Buffer Underflow.
            throw new MCMLException("Buffer Underflow.", MCMLException.ERROR,
                    MCMLException.TTS, MCMLException.ABNORMAL_DATA_FORMAT);
        }

        return commandNo;
    }

    /**
     * Gets ErrorCode.
     * 
     * @param frameHeader
     * @return ErrorCode
     * @throws MCMLException
     */
    public int getErrorCode(byte[] frameHeader) throws MCMLException {
        int errorCode;
        try {
            ByteBuffer inputBuf = ByteBuffer.wrap(frameHeader);
            inputBuf.order(TTS_FRAME_ENDIAN);
            errorCode = inputBuf.getInt(4);
        } catch (BufferUnderflowException bue) {
            // Buffer Underflow.
            throw new MCMLException("Buffer Underflow.", MCMLException.ERROR,
                    MCMLException.TTS, MCMLException.ABNORMAL_DATA_FORMAT);
        }

        return errorCode;
    }

    /**
     * Gets FrameDataSize.
     * 
     * @param frameHeader
     * @return FrameDataSize
     * @throws MCMLException
     */
    public int getFrameDataSize(byte[] frameHeader) throws MCMLException {
        int dataSize;
        try {
            ByteBuffer inputBuf = ByteBuffer.wrap(frameHeader);
            inputBuf.order(TTS_FRAME_ENDIAN);
            dataSize = inputBuf.getInt(8) - TTS_FRAME_HEADER_BYTE_LENGTH;
        } catch (BufferUnderflowException bue) {
            // Buffer Underflow.
            throw new MCMLException("Buffer Underflow.", MCMLException.ERROR,
                    MCMLException.TTS, MCMLException.ABNORMAL_DATA_FORMAT);
        }

        return dataSize;
    }

    /**
     * Gets WaveDataSize.
     * 
     * @param frameData
     * @return WaveDataSize
     * @throws MCMLException
     */
    public int getWaveDataSize(byte[] frameData) throws MCMLException {
        int waveDataSize;
        try {
            ByteBuffer inputBuf = ByteBuffer.wrap(frameData);
            inputBuf.order(TTS_FRAME_ENDIAN);
            waveDataSize = inputBuf.getInt() - TTS_WAVE_DATA_START_POSITION;
        } catch (BufferUnderflowException bue) {
            // Buffer Underflow.
            throw new MCMLException("Buffer Underflow.", MCMLException.ERROR,
                    MCMLException.TTS, MCMLException.ABNORMAL_DATA_FORMAT);
        }

        return waveDataSize;
    }

    /**
     * Gets WaveData.
     * 
     * @param frameData
     * @param contentsDataSize
     * @return byte[]
     * @throws MCMLException
     */
    public byte[] getWaveData(byte[] frameData, int contentsDataSize)
            throws MCMLException {
        byte[] waveData = new byte[contentsDataSize];
        try {
            ByteBuffer inputBuf = ByteBuffer.wrap(frameData);
            inputBuf.order(TTS_FRAME_ENDIAN);
            inputBuf.position(TTS_WAVE_DATA_START_POSITION);
            inputBuf.get(waveData, 0, contentsDataSize);
        } catch (BufferUnderflowException bue) {
            // Buffer Underflow.
            throw new MCMLException("Buffer Underflow.", MCMLException.ERROR,
                    MCMLException.TTS, MCMLException.ABNORMAL_DATA_FORMAT);
        } catch (IndexOutOfBoundsException ie) {
            // Index Out Of Bounds.
            throw new MCMLException("Index Out Of Bounds.",
                    MCMLException.ERROR, MCMLException.TTS,
                    MCMLException.ABNORMAL_DATA_FORMAT);
        }

        return waveData;
    }

    // ------------------------------------------
    // private member function
    // ------------------------------------------
    private byte[] setFrameHeader(int commandNo, int frameSize,
            short sequenceNo, byte numerator, byte denominator, Date date)
            throws MCMLException {
        byte[] frameHeader = new byte[TTS_FRAME_HEADER_BYTE_LENGTH];
        try {
            ByteBuffer buf = ByteBuffer.wrap(frameHeader);
            buf.order(TTS_FRAME_ENDIAN);
            buf.putInt(commandNo);
            buf.putInt(0);
            buf.putInt(frameSize);
            buf.putShort(sequenceNo);
            buf.put(numerator);
            buf.put(denominator);
            buf.put(setTime(date), 0, TTS_FRAME_TIME_BYTE_LENGTH);
            frameHeader = buf.array();
        } catch (BufferOverflowException boe) {
            // Buffer Overflow.
            throw new MCMLException("Buffer Overflow.", MCMLException.ERROR,
                    MCMLException.TTS, MCMLException.ABNORMAL_DATA_FORMAT);
        } catch (IndexOutOfBoundsException ie) {
            // Index Out Of Bounds.
            throw new MCMLException("Index Out Of Bounds.",
                    MCMLException.ERROR, MCMLException.TTS,
                    MCMLException.ABNORMAL_DATA_FORMAT);
        }

        return frameHeader;
    }

    private byte[] setFrameData(int contentsDataSize, boolean isLipSync,
            int partitionDataSize, float speakRate, String locale,
            String voiceFontName, byte[] contentsData, String charset)
            throws MCMLException {
        byte[] frameData = new byte[TTS_CONVERT_REQUEST_HEADER_BYTE_LENGTH
                + contentsDataSize];
        try {
            byte lipSync = (byte) ((isLipSync) ? 1 : 0);
            byte[] localeData = setLocale(locale, charset);
            byte[] voiceFontNameData = setVoiceFontName(voiceFontName, charset);

            ByteBuffer buf = ByteBuffer.wrap(frameData);
            buf.order(TTS_FRAME_ENDIAN);
            buf.putInt(contentsDataSize);
            buf.put(lipSync);
            buf.position(8);
            buf.putInt(0);
            buf.putFloat(speakRate);
            buf.put(localeData, 0, TTS_FRAME_LOCALE_BYTE_LENGTH);
            buf.put(voiceFontNameData, 0, TTS_FRAME_VOICEFONT_NAME_BYTE_LENGTH);
            buf.put(contentsData, 0, contentsDataSize);
            frameData = buf.array();
        } catch (BufferOverflowException boe) {
            // Buffer Overflow.
            throw new MCMLException("Buffer Overflow.", MCMLException.ERROR,
                    MCMLException.TTS, MCMLException.ABNORMAL_DATA_FORMAT);
        } catch (IndexOutOfBoundsException ie) {
            // Index Out Of Bounds.
            throw new MCMLException("Index Out Of Bounds.",
                    MCMLException.ERROR, MCMLException.TTS,
                    MCMLException.ABNORMAL_DATA_FORMAT);
        }

        return frameData;
    }

    private byte[] setLocale(String locale, String charset)
            throws MCMLException {
        byte[] localeData = new byte[TTS_FRAME_LOCALE_BYTE_LENGTH];
        try {
            if (locale != null && !locale.isEmpty()) {
                byte[] tmpData = locale.getBytes(charset);
                ByteBuffer buf = ByteBuffer.wrap(localeData);
                buf.order(TTS_FRAME_ENDIAN);
                buf.put(tmpData, 0, tmpData.length);
                localeData = buf.array();
            }
        } catch (UnsupportedEncodingException exp) {
            // Unsupported StringCode.
            throw new MCMLException("Unsupported StringCode.",
                    MCMLException.ERROR, MCMLException.TTS,
                    MCMLException.ABNORMAL_DATA_FORMAT);
        } catch (BufferOverflowException boe) {
            // Buffer Overflow.
            throw new MCMLException("Buffer Overflow.", MCMLException.ERROR,
                    MCMLException.TTS, MCMLException.ABNORMAL_DATA_FORMAT);
        } catch (IndexOutOfBoundsException ie) {
            // Index Out Of Bounds.
            throw new MCMLException("Index Out Of Bounds.",
                    MCMLException.ERROR, MCMLException.TTS,
                    MCMLException.ABNORMAL_DATA_FORMAT);
        }

        return localeData;
    }

    private byte[] setVoiceFontName(String voiceFontName, String charset)
            throws MCMLException {
        byte[] voiceFontNameData = new byte[TTS_FRAME_VOICEFONT_NAME_BYTE_LENGTH];
        try {
            if (voiceFontName != null && !voiceFontName.isEmpty()) {
                byte[] tmpData = voiceFontName.getBytes(charset);
                ByteBuffer buf = ByteBuffer.wrap(voiceFontNameData);
                buf.order(TTS_FRAME_ENDIAN);
                buf.put(tmpData, 0, tmpData.length);
                voiceFontNameData = buf.array();
            }
        } catch (UnsupportedEncodingException exp) {
            // Unsupported StringCode.
            throw new MCMLException("Unsupported StringCode.",
                    MCMLException.ERROR, MCMLException.TTS,
                    MCMLException.ABNORMAL_DATA_FORMAT);
        } catch (BufferOverflowException boe) {
            // Buffer Overflow.
            throw new MCMLException("Buffer Overflow.", MCMLException.ERROR,
                    MCMLException.TTS, MCMLException.ABNORMAL_DATA_FORMAT);
        } catch (IndexOutOfBoundsException ie) {
            // Index Out Of Bounds.
            throw new MCMLException("Index Out Of Bounds.",
                    MCMLException.ERROR, MCMLException.TTS,
                    MCMLException.ABNORMAL_DATA_FORMAT);
        }

        return voiceFontNameData;
    }

    private byte[] setTime(Date date) throws MCMLException {
        byte[] time = new byte[TTS_FRAME_TIME_BYTE_LENGTH];
        try {
            ByteBuffer buf = ByteBuffer.wrap(time);
            buf.order(TTS_FRAME_ENDIAN);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            byte month = (byte) (calendar.get(Calendar.MONTH) + 1);
            byte day = (byte) calendar.get(Calendar.DAY_OF_MONTH);
            byte hour = (byte) calendar.get(Calendar.HOUR_OF_DAY);
            byte minute = (byte) calendar.get(Calendar.MINUTE);
            byte second = (byte) calendar.get(Calendar.SECOND);
            short milliSecond = (short) calendar.get(Calendar.MILLISECOND);

            buf.put(month);
            buf.put(day);
            buf.put(hour);
            buf.put(minute);
            buf.put(second);
            buf.put((byte) 0x00);
            buf.putShort(milliSecond);
            time = buf.array();
        } catch (ArrayIndexOutOfBoundsException ae) {
            // Array Index Out Of Bounds.
            throw new MCMLException("Array Index Out Of Bounds.",
                    MCMLException.ERROR, MCMLException.TTS,
                    MCMLException.ABNORMAL_DATA_FORMAT);
        } catch (BufferOverflowException boe) {
            // Buffer Overflow.
            throw new MCMLException("Buffer Overflow.", MCMLException.ERROR,
                    MCMLException.TTS, MCMLException.ABNORMAL_DATA_FORMAT);
        }

        return time;
    }
}
