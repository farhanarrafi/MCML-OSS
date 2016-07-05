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

import java.util.Date;

/**
 * ServerApThread abstract thread class.
 * 
 * 
 */
public abstract class ServerApThread extends Thread {
    // ------------------------------------------
    // public member function
    // ------------------------------------------
    /**
     * Constructor
     * 
     * @param name
     */
    public ServerApThread(String name) {
        super(name);
    }

    /**
     * run
     */
    @Override
    public final void run() {
        writeLog("started");

        Date processStartTime = new Date();

        try {
            processMain();
        } catch (Exception exp) {
            ServerApLogger.getInstance().writeError(
                    "ServerApThread processMain Error"); // KDL debug
            ServerApLogger.getInstance().writeException(exp);
        }

        writeLog("stopped");

        Date processEndTime = new Date();
        writeLog("[ProcessTime]ProcessStart->ProcessEnd: "
                + (processEndTime.getTime() - processStartTime.getTime())
                + "msec ");

        // normal end
        return;
    }

    /**
     * Ends.
     * 
     * @throws Exception
     */
    public final void terminate() throws Exception {
        writeLog("terminating");
        processTermination();
    }

    // ------------------------------------------
    // protected member function
    // ------------------------------------------
    protected final void writeLog(String message) {
        ServerApLogger.getInstance().writeDebug(message + ": " + getName());
    }

    // ------------------------------------------
    // abstract protected member function
    // ------------------------------------------
    protected abstract void processMain() throws Exception;

    protected abstract void processTermination() throws Exception;
}
