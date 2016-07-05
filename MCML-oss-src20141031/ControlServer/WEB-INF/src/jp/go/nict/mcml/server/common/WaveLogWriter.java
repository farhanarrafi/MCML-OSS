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

package jp.go.nict.mcml.server.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import jp.go.nict.mcml.xml.MCMLStatics;

/**
 * WaveLogWriter class.
 * 
 * @version 4.0
 * @since 20120921
 */
public class WaveLogWriter {
    /**
     * RIFFHeader inner class.
     * 
     */
    private class RIFFHeader {
        // ------------------------------------------
        // private member constants
        // ------------------------------------------
        private static final String RIFF_STRING = "RIFF";
        private static final String WAVE_STRING = "WAVE";

        // ------------------------------------------
        // private member variables(instance field)
        // ------------------------------------------
        byte[] m_RiffHeader = new byte[4]; // RIFF Header
        int m_PayloadSize; // pay load Size
        byte[] m_WaveHeader = new byte[4]; // WAVE Header

        // ------------------------------------------
        // public member functions
        // ------------------------------------------
        RIFFHeader(int payloadSize) {
            m_RiffHeader = RIFF_STRING.getBytes();
            m_PayloadSize = payloadSize;
            m_WaveHeader = WAVE_STRING.getBytes();
        }

        public byte[] getByteStream() {
            // ByteBuffer memory allocate.
            int riffHeaderSize = m_RiffHeader.length + (Integer.SIZE / 8)
                    + m_WaveHeader.length;
            ByteBuffer buff = ByteBuffer.allocate(riffHeaderSize);

            // calculate PaylordSize.
            int paylordSize = m_PayloadSize + m_WaveHeader.length;

            // merge parameters.
            buff.put(m_RiffHeader);
            buff.putInt(Integer.reverseBytes(paylordSize));
            buff.put(m_WaveHeader);

            return buff.array();
        }
    }

    /**
     * PCMfmtChunk inner class.
     * 
     */
    private class PCMfmtChunk {
        // ------------------------------------------
        // private member constants
        // ------------------------------------------
        private static final String FMTCHUNKHEADER_STRING = "fmt ";
        private static final short FORMAT_TAG_PCM = 0x0001;

        // ------------------------------------------
        // private member variables(instance field)
        // ------------------------------------------
        private byte[] m_FmtHeader = new byte[4]; // fmtCHUNK Header
        private int m_FmtSize; // fmtCHUNK Size
        private short m_FmtTag; // fmtCHUNK Tag
        private short m_Channels; // number of channels.
        private int m_SamplingFrequency; // Sampling Frequency
        private int m_AverageBytePerSec; // WaveData Speed.
        private short m_BlockSize; // Block Size
        private short m_SamplingBitRate; // SamplingBitRate

        // ------------------------------------------
        // public member functions
        // ------------------------------------------
        PCMfmtChunk(short channels, int samplingFrequency,
                int averageBytePerSec, short blockSize, short samplingBitRate) {
            // set "fmt " string.
            m_FmtHeader = FMTCHUNKHEADER_STRING.getBytes();

            // PCMfmtChunk size is constant.
            m_FmtSize = 16;

            m_FmtTag = FORMAT_TAG_PCM;
            m_Channels = channels;
            m_SamplingFrequency = samplingFrequency;
            m_AverageBytePerSec = averageBytePerSec;
            m_BlockSize = blockSize;
            m_SamplingBitRate = samplingBitRate;
        }

        public byte[] getByteStream() {
            // ByteBuffer memory allocate.
            int pcmfmtChunkSize = m_FmtHeader.length + (Integer.SIZE / 8) * 3
                    + (Short.SIZE / 8) * 4;
            ByteBuffer buff = ByteBuffer.allocate(pcmfmtChunkSize);

            // marge params.
            buff.put(m_FmtHeader);
            buff.putInt(Integer.reverseBytes(m_FmtSize));
            buff.putShort(Short.reverseBytes(m_FmtTag));
            buff.putShort(Short.reverseBytes(m_Channels));
            buff.putInt(Integer.reverseBytes(m_SamplingFrequency));
            buff.putInt(Integer.reverseBytes(m_AverageBytePerSec));
            buff.putShort(Short.reverseBytes(m_BlockSize));
            buff.putShort(Short.reverseBytes(m_SamplingBitRate));

            return buff.array();
        }
    }

    /**
     * DataChunk inner class.
     * 
     */
    private class DataChunk {
        // ------------------------------------------
        // private member constants
        // ------------------------------------------
        private static final String DATACHUNKHEADER_STRING = "data";

        // ------------------------------------------
        // private member variables(instance field)
        // ------------------------------------------
        byte[] m_DataHeader = new byte[4]; // DATACHUNK Header
        int m_DataSize; // DATACHUNK Size

        // ------------------------------------------
        // public member functions
        // ------------------------------------------
        DataChunk(int dataSize) {
            // set "data" string.
            m_DataHeader = DATACHUNKHEADER_STRING.getBytes();

            // set data size.
            m_DataSize = dataSize;
        }

        public byte[] getByteStream() {
            // ByteBuffer memory allocate.
            ByteBuffer buff = ByteBuffer.allocate(m_DataHeader.length
                    + (Integer.SIZE / 8));

            // marge params.
            buff.put(m_DataHeader);
            buff.putInt(Integer.reverseBytes(m_DataSize));

            return buff.array();
        }
    }

    // ------------------------------------------
    // private member constants
    // ------------------------------------------
    private static final String DIRECTORY_WAV = "WAV";
    private static final String DIRECTORY_BS = "BS";
    private static final String EXTENSION_WAV = ".wav";
    private static final String EXTENSION_BS = ".bs";

    // ------------------------------------------
    // private member variables(instance field)
    // ------------------------------------------
    private File m_OutputDirectory;
    private String m_OutputFileName;
    private boolean m_IsWAV;

    // ------------------------------------------
    // public member functions
    // ------------------------------------------
    /**
     * Default constructor
     * 
     * @param isWAV
     */
    public WaveLogWriter(boolean isWAV) {
        m_IsWAV = isWAV;
        m_OutputFileName = "";
    }

    /**
     * create output directory of wave log file
     * 
     * @param directoryPath
     * @return boolean
     */
    public boolean createDirectory(String directoryPath) {

        // choose directory name according to audioformat
        if (m_IsWAV) {
            m_OutputDirectory = new File(directoryPath, DIRECTORY_WAV);
        } else {
            m_OutputDirectory = new File(directoryPath, DIRECTORY_BS);
        }

        // create directory
        if (!m_OutputDirectory.exists()) {
            return m_OutputDirectory.mkdirs();
        }

        return true;
    }

    /**
     * create wave log file and write data in it
     * 
     * @param prefix
     * @param language
     * @param clientIP
     * @param userURI
     * @param userID
     * @param utteranceID
     * @param date
     * @param audioFormat
     * @param data
     * @param channels
     * @param samplingFrequency
     * @param samplingBitRate
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void writeFile(String prefix, String language, String clientIP,
            String userURI, String userID, int utteranceID, Date date,
            String audioFormat, byte[] data, short channels,
            int samplingFrequency, short samplingBitRate)
            throws FileNotFoundException, IOException {

        // choose extension according to audioformat
        String extension = "";
        if (m_IsWAV) {
            extension = EXTENSION_WAV;
        } else {
            extension = EXTENSION_BS;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyyMMdd_HHmmss_SSS");

        // create Client IP Address
        String fileNameClientIP = "";

        if (clientIP != null && !clientIP.isEmpty()) {
            String[] ipParts = clientIP.split("\\.");
            int partsCnt = ipParts.length;
            for (int i = 0; i < partsCnt; i++) {
                fileNameClientIP += String.format("%03d",
                        Integer.parseInt(ipParts[i]));
            }
        }

        if (date == null) {
            date = new Date();
        }

        // create File Name.
        String fileName = prefix + "_" + language + "_" + fileNameClientIP
                + "_" + userURI + "_" + userID + "_"
                + Integer.toString(utteranceID) + "_" + dateFormat.format(date)
                + extension;

        // create file
        File waveLogFile = new File(m_OutputDirectory.getPath(), fileName);

        // create Stream
        FileOutputStream waveLogStream = new FileOutputStream(waveLogFile);

        // write data in file as it is,when Audioformat is DSR
        if (audioFormat.equalsIgnoreCase(MCMLStatics.AUDIO_DSR)) {
            waveLogStream.write(data);
        } else {
            // append Wavheader to data when Audioformat is PCM
            byte[] wavData = generateWavData(data, channels, samplingFrequency,
                    samplingBitRate);

            waveLogStream.write(wavData);
        }

        // close stream
        waveLogStream.close();

        // set OutputFileName.
        m_OutputFileName = waveLogFile.getPath();

        return;
    }

    /**
     * Gets WaveLogFileName.
     * 
     * @return m_OutputFileName
     */
    public String getWaveLogFileName() {
        return m_OutputFileName;
    }

    /**
     * Generates WavData.
     * 
     * @param data
     * @param channels
     * @param samplingFrequency
     * @param samplingBitRate
     * @return byte[]
     */
    // ------------------------------------------
    // private member functions
    // ------------------------------------------

    public byte[] generateWavData(byte[] data, short channels,
            int samplingFrequency, short samplingBitRate) {

        // calculate waveDataBytes
        int waveDataBytes = data.length;

        // generate Wav Header
        byte[] wavHeader = generateWavHeader(waveDataBytes, channels,
                samplingFrequency, samplingBitRate);

        // ByteBuffer memory allocate
        ByteBuffer wavDataBuff = ByteBuffer.allocate(waveDataBytes
                + wavHeader.length);

        // put Wav Header to ByteBuffer
        wavDataBuff.put(wavHeader);

        // append data to Wav header
        wavDataBuff.put(data);

        return wavDataBuff.array();
    }

    private byte[] generateWavHeader(int waveDataBytes, short channels,
            int samplingFrequency, short samplingBitRate) {
        // generate Data Chunk.
        byte[] dataChunk = generateDataChunk(waveDataBytes);

        // generate PCM fmt Chunk.
        byte[] fmtChunk = generatePCMfmtChunk(channels, samplingFrequency,
                samplingBitRate);

        // generate RIFF Header.
        byte[] riffHeader = generateRIFFHeader(dataChunk.length,
                fmtChunk.length, waveDataBytes);

        // ByteBuffer memory allocate.
        ByteBuffer buff = ByteBuffer.allocate(riffHeader.length
                + fmtChunk.length + dataChunk.length);

        // marge WavHeader.
        buff.put(riffHeader);
        buff.put(fmtChunk);
        buff.put(dataChunk);

        return buff.array();
    }

    private byte[] generateDataChunk(int waveDataBytes) {
        // create instance and set DataChunk Parameter(Wave Data Size).
        DataChunk dataChunk = new DataChunk(waveDataBytes);

        // generate BinaryStream.
        return dataChunk.getByteStream();
    }

    private byte[] generatePCMfmtChunk(short channels, int samplingFrequency,
            short samplingBitRate) {
        // calculate data speed.
        int averageBytePerSec = samplingFrequency * (samplingBitRate / 8)
                * channels;

        // calculate block size.
        short blockSize = (short) ((samplingBitRate / 8) * channels);

        // create instance and set PCMfmtChunk Parameter.
        PCMfmtChunk pcmfmtChunk = new PCMfmtChunk(channels, samplingFrequency,
                averageBytePerSec, blockSize, samplingBitRate);

        // generate BinaryStream.
        return pcmfmtChunk.getByteStream();
    }

    private byte[] generateRIFFHeader(int dataChunkBytes, int fmtChunkBytes,
            int waveDataBytes) {
        // calculate pay load size.
        int paylordBytes = dataChunkBytes + fmtChunkBytes + waveDataBytes;

        // create instance and set PCMfmtChunk Parameter.
        RIFFHeader riffHeader = new RIFFHeader(paylordBytes);

        // generate BinaryStream.
        return riffHeader.getByteStream();
    }
}
