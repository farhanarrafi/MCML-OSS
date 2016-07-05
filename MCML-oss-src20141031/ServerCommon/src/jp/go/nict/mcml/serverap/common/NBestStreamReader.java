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

//-------------------------------------------------------------------
//Ver.3.0
//2011/12/06
//-------------------------------------------------------------------

package jp.go.nict.mcml.serverap.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

import jp.go.nict.mcml.servlet.MCMLStatics;

//NBest reader for ASR or MT.
/**
 * NBestStreamReader class.
 * 
 */
public class NBestStreamReader {
    // ------------------------------------------
    // private member variable(instance field)
    // ------------------------------------------
    private InputStream m_InputStream;
    private InputStreamReader m_InputStreamReader;
    private BufferedReader m_NBestReader;
    private int m_BufferSize;
    private String m_BeginningKeyword;
    private String m_EndingKeyword;
    private String m_EngineName;
    private int m_NullResponseSleepMSec;
    private int m_NullResponseCounter;

    // ------------------------------------------
    // public member functions
    // ------------------------------------------
    /**
     * Default Constructor. A buffer size is 32k bytes.
     * 
     * @param stream
     */
    public NBestStreamReader(InputStream stream) {
        m_InputStream = stream;
        m_BufferSize = 32768;
        m_InputStreamReader = null;
        m_NBestReader = null;
        m_BeginningKeyword = "";
        m_EndingKeyword = "";
        m_EngineName = "";
        m_NullResponseSleepMSec = 10;
        m_NullResponseCounter = 10;
    }

    /**
     * Initialization method.
     * 
     * @param start
     * @param end
     * @param charset
     * @param engineName
     * @param nullResponseSleepMSec
     * @param nullResponseCounter
     * @throws IOException
     */
    public void init(String start, String end, String charset,
            String engineName, int nullResponseSleepMSec,
            int nullResponseCounter) throws IOException {
        // create reader Class(InputStream).
        if (m_InputStream != null) {
            m_InputStreamReader = new InputStreamReader(m_InputStream, charset);
        } else {
            System.out.println("NBestStreamReader::m_InputStream is null.");
            writeLog("NBestStreamReader::m_InputStream is null.");
            throw new IOException("NBestStreamReader::m_InputStream is null.");
        }
        m_NBestReader = new BufferedReader(m_InputStreamReader, m_BufferSize);

        // set NBest Beginning Keyword and Ending Keyword.
        m_BeginningKeyword = start;
        m_EndingKeyword = end;

        // set EngineName(for Logger).
        m_EngineName = engineName;

        m_NullResponseSleepMSec = nullResponseSleepMSec;
        m_NullResponseCounter = nullResponseCounter;

        return;
    }

    /**
     * Closing method.
     * 
     * @throws IOException
     */
    public void close() throws IOException {
        // close streams.
        if (m_InputStreamReader != null) {
            m_InputStreamReader.close();
            m_InputStreamReader = null;
        }
        if (m_NBestReader != null) {
            m_NBestReader.close();
            m_NBestReader = null;
        }

        return;
    }

    /**
     * Nbest data reading method. This method reads raw Nbest data from an
     * engine.
     * 
     * @return stringBuffer.toString()
     * @throws IOException
     * @throws InterruptedException
     */
    public String read() throws IOException, InterruptedException {
        /* When this object is not initialized. */
        if (m_NBestReader == null) {
            String message = "NBestStreamReader::m_NBestReader is null.";
            writeLog(message);
            throw new IOException(message);
        }

        // Lines of raw Nbest Data read from an engine.
        ArrayList<String> lines = new ArrayList<String>();

        // Reading invalid Nbest data. Until a beginning keyword.
        int totalLength = readLines(lines, m_BeginningKeyword, true);

        // Reading valid Nbest data. From TOF to a ending keyword.
        totalLength += readLines(lines, m_EndingKeyword, false);

        // Combining valid data.
        StringBuffer stringBuffer = new StringBuffer(totalLength);
        Iterator<String> iterator = lines.iterator();
        while (iterator.hasNext()) {
            stringBuffer.append((String) iterator.next()
                    + MCMLStatics.RETURN_CODE);
        }

        return stringBuffer.toString();
    }

    // ------------------------------------------
    // protected member functions
    // ------------------------------------------
    protected void getEngineInfoFromNBest(String nbest) {
    }

    // ------------------------------------------
    // protected member functions
    // ------------------------------------------
    private void writeLog(String message) {
        ServerApLogger.getInstance().writeTrace(message);
    }

    private int readLines(ArrayList<String> lines, String keyword,
            boolean isBegining) throws IOException, InterruptedException {
        int totalLength = 0;
        int nullResponseCounter = 0;

        // Reading invalid Nbest data. Until a beginning keyword.
        while (true) {
            String readLine = m_NBestReader.readLine();

            if (readLine == null) {
                nullResponseCounter++;
                if (nullResponseCounter >= m_NullResponseCounter) {
                    nullResponseCounter = 0;
                    String message = m_EngineName + " does not respond "
                            + keyword;
                    ServerApLogger.getInstance().writeError(message);
                    throw new IOException(message);
                }
                Thread.sleep(m_NullResponseSleepMSec);
                continue;
            }

            // output ASR/MT(controller) Standard Output.
            System.out.println(m_EngineName + " : " + readLine);
            writeLog(m_EngineName + " : " + readLine);

            // get Engine Information. ex:Acoustic Model Name,Language Model
            // Name.
            getEngineInfoFromNBest(readLine);

            if (!isBegining) {
                lines.add(readLine);
                // "+ 1" is a size of '\n'.
                totalLength += readLine.length() + 1;
            }

            // check NBest StartKeyWord
            if (readLine.startsWith(keyword)) {
                if (isBegining) {
                    lines.add(readLine);
                    // "+ 1" is a size of '\n'.
                    totalLength += readLine.length() + 1;
                }
                break;
            }

            nullResponseCounter = 0;
        }

        return totalLength;

    }
}
