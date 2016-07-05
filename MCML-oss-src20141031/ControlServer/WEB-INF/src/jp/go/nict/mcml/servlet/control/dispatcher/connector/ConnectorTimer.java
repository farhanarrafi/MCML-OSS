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

import java.util.Timer;

/**
 * ConnectorTimer class.
 * 
 */
public class ConnectorTimer {
    // ------------------------------------------
    // private member variables
    // ------------------------------------------
    private Timer timer; // timer
    private Connector connector; // server connection
    private TimerTask task; // timer task

    // ------------------------------------------
    // public member functions
    // ------------------------------------------
    /** constructor */
    public ConnectorTimer(Connector connector) {
        this.connector = connector;
        timer = new Timer();
        task = null;
    }

    /** start timer */
    public void start(long timeoutMilliSeconds) {
        if (timeoutMilliSeconds != 0) {
            if (task == null) {
                // set timeout task and start timer
                task = new TimerTask(connector);
                timer.schedule(task, timeoutMilliSeconds);
            }
        }
    }

    /** stop timer */
    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    // ------------------------------------------
    // private internal class
    // ------------------------------------------
    /**
     * TimerTask inner class.
     * 
     * @see java.util.TimerTask
     * 
     */
    private class TimerTask extends java.util.TimerTask {
        // ------------------------------------------
        // private member variables
        // ------------------------------------------
        private Connector connector; // server connection
        private boolean executed; // check executed timeout task

        // ------------------------------------------
        // public member functions
        // ------------------------------------------
        // constructor
        public TimerTask(Connector connector) {
            this.connector = connector;
            executed = false;
        }

        // timeout task
        @Override
        public void run() {
            synchronized (this) {
                if (executed) {
                    // no more process
                    return;
                }
                try {
                    // close connection
                    connector.closeConnection();
                    // occurred time out
                    connector.setTimeoutTimerTask();
                    // update counter
                    connector.updateTimeoutCounter();
                } finally {
                    // occurred timeout
                    executed = true;
                }
            }
            return;
        }
    }
}
