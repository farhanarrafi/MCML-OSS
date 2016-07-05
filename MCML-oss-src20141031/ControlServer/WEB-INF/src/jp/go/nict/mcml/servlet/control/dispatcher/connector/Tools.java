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

import jp.go.nict.mcml.engine.socket.Pair;

import org.apache.log4j.Logger;

/**
 * Tools class.
 * 
 */
public class Tools {
    private static final Logger LOG = Logger.getLogger(Tools.class.getName());

    /**
     * generate server connection
     * 
     * @param listId
     * @param destination
     * @param coefficientASRTimeout
     * @return Connector
     */
    public static Connector generate(int listId, String destination,
            float coefficientASRTimeout) {
        Connector connector = null;
        // parse URL
        Pair<String, Integer> target = new Pair<String, Integer>();
        if (!parseURL(destination, target)) {
            // generate server connection ByClientComCtrl
            connector = new ConnectorByClientComCtrl(listId, destination, // URL
                    coefficientASRTimeout); // ASR timeout Coefficient
        } else {
            // server connection BySocketAndStream
            connector = new ConnectorBySocketAndStream(listId,
                    target.getFirst(), // host
                    target.getSecond().intValue(), // port number
                    coefficientASRTimeout); // ASR timeout Coefficient
        }
        return connector;
    }

    /**
     * parse URL return value:false (URL) true (IPAddress:port)
     * 
     * @param requestDestination
     * @param target
     * @return boolean
     */
    public static boolean parseURL(String requestDestination,
            Pair<String, Integer> target) {
        // judge URL
        if (requestDestination.contains("http://")
                || requestDestination.contains("https://")) {
            return false;
        }

        // split IPAddress(host) and port
        String[] parts = requestDestination.split(":");
        if (target == null) {
            target = new Pair<String, Integer>();
        }

        // set target information
        Integer port = 0;
        try {
            port = Integer.valueOf(parts[1]);
        } catch (NumberFormatException e) {
            // unexpected port number
            return false;
        }
        target.setFirst(parts[0]);
        target.setSecond(port);

        return true;
    }
}
