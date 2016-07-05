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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ResultWriter abstract class.
 * 
 * @version 4.0
 * @since 20120921
 */
public abstract class ResultWriter {
    // ------------------------------------------
    // protected member variables(instance field)
    // ------------------------------------------
    protected File m_OutputDirectory;
    private String m_OutputFileName;

    // ------------------------------------------
    // public member functions
    // ------------------------------------------

    /**
     * constructor
     */
    public ResultWriter() {
        m_OutputDirectory = null;
    }

    /**
     * create output directory of result file
     * 
     * @param parentdirecotryPath
     * @return {@code true} Always returns {@code true} when this method is called.
     */
    public boolean createDirectory(String parentdirecotryPath) {
        return true;
    }

    /**
     * create Result File and write Result in it
     * 
     * @param prefix
     * @param languageInfo
     * @param clientIP
     * @param userURI
     * @param userID
     * @param utteranceID
     * @param date
     * @param result
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public void writeFile(String prefix, String languageInfo, String clientIP,
            String userURI, String userID, int utteranceID, Date date,
            String result) throws UnsupportedEncodingException, IOException {
        if (m_OutputDirectory != null) {
            // select Output File's Extension.
            String extension = selectExtension();

            SimpleDateFormat dataFormat = new SimpleDateFormat(
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
            String fileName = prefix + "_" + languageInfo + "_"
                    + fileNameClientIP + "_" + userURI + "_" + userID + "_"
                    + Integer.toString(utteranceID) + "_"
                    + dataFormat.format(date) + extension;

            // create File.
            File textResultFile = new File(m_OutputDirectory.getPath(),
                    fileName);
            FileOutputStream textResultStream = new FileOutputStream(
                    textResultFile);

            // write Result File.
            String charset = selectCharset(languageInfo);
            textResultStream.write(result.getBytes(charset));
            textResultStream.flush();
            textResultStream.close();

            // set OutputFileName.
            m_OutputFileName = textResultFile.getPath();
        }
        return;
    }

    /**
     * Gets ResultLogFileName.
     * 
     * @return m_OutputFileName
     */
    public String getResultLogFileName() {
        return m_OutputFileName;
    }

    // ------------------------------------------
    // protected member function
    // ------------------------------------------
    protected abstract String selectExtension();

    protected abstract String selectCharset(String languageinfo);
}