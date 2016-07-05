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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ServerApLogger class.
 * 
 */
public class ServerApLogger {
    // ------------------------------------------
    // private member variable(class field)
    // ------------------------------------------
    private static final ServerApLogger M_INSTANCE = new ServerApLogger();
    private static final int LOG_LEVEL_ERROR = 1;
    private static final int LOG_LEVEL_WARNING = 2;
    private static final int LOG_LEVEL_TRACE = 3;
    private static final int LOG_LEVEL_DEBUG = 4;
    private static final String[] LOG_INDEX = { "", "ERR ", "WRN ", "TRC ",
            "DBG " };

    // ------------------------------------------
    // protected member variable(instance field)
    // ------------------------------------------
    protected String m_BaseFileName;
    protected int m_LogLevel;
    protected FileOutputStream m_Stream;
    protected SimpleDateFormat m_DateFormat;
    protected Date m_ErrorLogDate;

    // ------------------------------------------
    // private member function
    // ------------------------------------------
    // constructor
    private ServerApLogger() {
        m_BaseFileName = null;
        m_LogLevel = 0;
        m_Stream = null;
        m_DateFormat = null;
        m_ErrorLogDate = null;
    }

    // ------------------------------------------
    // public member function
    // ------------------------------------------
    /**
     * Gets instance.
     * 
     * @return Instance
     */
    public static ServerApLogger getInstance() {
        return M_INSTANCE;
    }

    /**
     * Initialization
     * 
     * @param baseFileName
     * @param logLevel
     */
    public synchronized void initialize(String baseFileName, int logLevel) {
        m_BaseFileName = baseFileName;
        m_LogLevel = logLevel;
        m_DateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss SSS ");
    }

    /**
     * writeError
     * 
     * @param message
     */
    public synchronized void writeError(String message) {
        write(LOG_LEVEL_ERROR, message);
    }

    /**
     * writeWarning
     * 
     * @param message
     */
    public synchronized void writeWarning(String message) {
        write(LOG_LEVEL_WARNING, message);
    }

    /**
     * writeTrace
     * 
     * @param message
     */
    public synchronized void writeTrace(String message) {
        write(LOG_LEVEL_TRACE, message);
    }

    /**
     * writeDebug
     * 
     * @param message
     */
    public synchronized void writeDebug(String message) {
        write(LOG_LEVEL_DEBUG, message);
    }

    /**
     * Writes Exception.
     * 
     * @param exception
     */
    public synchronized void writeException(Exception exception) {
        String message = exception.getMessage();
        if (message == null) {
            message = exception.getLocalizedMessage();
            if (message == null) {
                message = exception.toString();
            }
        }

        writeError(message);
        exception.printStackTrace();

        return;
    }

    // ------------------------------------------
    // protected member function
    // ------------------------------------------
    protected void write(int logLevel, String message) {
        try {
            if (m_BaseFileName == null || m_LogLevel < logLevel) {
                return;
            }

            Date date = new Date();

            if (m_ErrorLogDate != null) {
                SimpleDateFormat dtFmt = new SimpleDateFormat("yyyyMMdd");
                String s_nowDate = dtFmt.format(date);
                Date d_nowDate;
                try {
                    d_nowDate = dtFmt.parse(s_nowDate);
                } catch (ParseException e) {
                    d_nowDate = date;
                }

                // Is This file the day before?
                if (d_nowDate.compareTo(m_ErrorLogDate) > 0) {
                    // create new log file name
                    createFile(date);
                }
            }

            if (m_Stream == null) {
                createFile(date);
            }

            message = m_DateFormat.format(date) + LOG_INDEX[logLevel] + message
                    + "\n";
            m_Stream.write(message.getBytes("UTF-8"));
            m_Stream.flush();
        } catch (Exception exp) {
            // no process
        }

        return;
    }

    protected void createFile(Date date) throws FileNotFoundException {
        SimpleDateFormat dtFmt = new SimpleDateFormat("_yyyyMMddHHmmss");

        Date nowDate = date;

        String errorLogFileName = m_BaseFileName + dtFmt.format(nowDate)
                + ".log";
        m_Stream = new FileOutputStream(errorLogFileName);

        // Make log Date (no time)
        SimpleDateFormat logFmt = new SimpleDateFormat("yyyyMMdd");
        String s_logDate = logFmt.format(nowDate);
        try {
            m_ErrorLogDate = logFmt.parse(s_logDate);
        } catch (ParseException e) {
            m_ErrorLogDate = nowDate;
        }

        return;
    }
}
