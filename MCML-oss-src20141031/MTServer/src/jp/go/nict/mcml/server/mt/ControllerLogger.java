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

package jp.go.nict.mcml.server.mt;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import jp.go.nict.mcml.serverap.common.ServerApThread;

/**
 * ControllerLogger class.
 *
 */
public class ControllerLogger extends ServerApThread {
    private BufferedReader m_StdErrorReader; // Standard Error reader for
                                             // Controller.
    private String m_EngineName;

    /**
     * Constructor
     *
     * @param errorStream
     * @param charset
     * @param engineName
     * @throws UnsupportedEncodingException
     */
    public ControllerLogger(InputStream errorStream, String charset,
            String engineName) throws UnsupportedEncodingException {
        super("ControllerLogger");

        // create Standard Error reader for Controller.
        InputStreamReader stdErrorReader = new InputStreamReader(errorStream,
                charset);
        m_StdErrorReader = new BufferedReader(stdErrorReader, 1024);

        m_EngineName = engineName;
    }

    /**
     * processMain
     */
    @Override
    protected void processMain() throws Exception {
        while (true) {
            String stdErrorString = null;

            // get string from Standard Error.
            if (m_StdErrorReader.ready()) {
                stdErrorString = m_StdErrorReader.readLine();
            }

            // output Standard Error string.
            if (stdErrorString != null) {
                System.out.println(m_EngineName + " : " + stdErrorString);
            }

            // wait(Sleep) 1 sec.
            if (stdErrorString == null) {
                sleep(1000);
            }
        }
    }

    @Override
    protected void processTermination() throws Exception {
        // interrupt for waiting.
        interrupt();

        // wait for thread termination
        join();

        // close stream.
        if (m_StdErrorReader != null) {
            m_StdErrorReader.close();
            m_StdErrorReader = null;
        }

        // normal end
        return;
    }

}
