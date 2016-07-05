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

import jp.go.nict.mcml.servlet.MCMLException;
import jp.go.nict.mcml.servlet.MCMLStatics;
import jp.go.nict.mcml.servlet.XMLTypeTools;
import jp.go.nict.mcml.xml.types.ErrorType;
import jp.go.nict.mcml.xml.types.MCMLType;
import jp.go.nict.mcml.xml.types.ResponseType;
import jp.go.nict.mcml.xml.types.ServerType;
import jp.go.nict.mcml.xml.types.UserType;

/**
 * EngineCtrl abstract class.
 * 
 */
public abstract class EngineCtrl extends ServerApThread {
    // ------------------------------------------
    // private member variable(class field)
    // ------------------------------------------
    private static int mCounter = 0;

    // ------------------------------------------
    // protected member variable(instance field)
    // ------------------------------------------
    protected int m_ServerType;
    protected boolean m_IsSending;
    protected boolean m_IsTimeout;

    // ------------------------------------------
    // private member variable(instance field)
    // ------------------------------------------
    private boolean m_IsProcessing;

    // ------------------------------------------
    // pubric member variable
    // ------------------------------------------
    /**
     * Constructor
     * 
     * @param serverType
     */
    public EngineCtrl(int serverType) {
        super("EngineCtrl" + mCounter);
        mCounter++;
        m_ServerType = serverType;
        m_IsProcessing = true;
        m_IsSending = false;
        m_IsTimeout = false;
    }

    /**
     * isProcessing
     * 
     * @return boolean
     */
    public boolean isProcessing() {
        return m_IsProcessing;
    }

    /**
     * processTimeout
     * 
     * @param data
     */
    public void processTimeout(MCMLData data) {
    }

    // ------------------------------------------
    // protected member function
    // ------------------------------------------
    @Override
    protected void processMain() throws Exception {

        Date responceSendTime = null;
        Date responceStartTime = null;
        try {
            while (true) {
                writeLog("wait for request");

                // take data from queue
                MCMLData data = MCMLRequestQueue.getInstance().takeData();
                if (data == null) {
                    // terminated
                    break;
                }

                writeLog("processing request");
                responceStartTime = processRequest(data);

                // get Error Code.
                String errorCode = null;
                MCMLType mcmlType = data.getMCMLType();
                if (XMLTypeTools.hasError(mcmlType)) {
                    errorCode = mcmlType.getServer().getResponse().getError()
                            .getCode().getValue();
                }

                if (!m_IsSending && !m_IsTimeout) {
                    // notification for receiver thread
                    data.doNotification();
                }
                responceSendTime = new Date();

                if (responceSendTime != null && responceStartTime != null) {
                    writeLog("[ProcessTime]ReceiveResponse->SendResponse: "
                            + (responceSendTime.getTime() - responceStartTime
                                    .getTime()) + "msec ");
                }

                // check MCMLType(Is Engine Connection closed ?).
                postProcessRequest(errorCode);

                // clear Flags
                m_IsTimeout = false;
                m_IsSending = false;
            }
        } finally {
            // process End Flag on;
            m_IsProcessing = false;
        }

        // normal end
        return;
    }

    @Override
    protected void processTermination() throws Exception {
        // interrupt for waiting on MCMLRequestQueue
        interrupt();

        // wait for thread termination
        join();

        // normal end
        return;
    }

    protected void copyMCMLType(MCMLType original, MCMLType copy)
            throws Exception {
        // Version copy
        String version = original.getVersion().toString();
        if (!version.isEmpty()) {
            copy.addVersion(version);
        }

        // User copy
        UserType user = null;
        if (original.hasUser()) {
            user = original.getUser();
            if (user != null) {
                copy.addUser(user);
            }
        }

        // check required parameter
        if (version.isEmpty()) {
            throw new MCMLException("no Version", MCMLException.ERROR,
                    MCMLException.COMMON,
                    MCMLException.ABNORMAL_XML_DATA_FORMAT);
        }
        if (user == null) {
            throw new MCMLException("no User", MCMLException.ERROR,
                    MCMLException.COMMON,
                    MCMLException.ABNORMAL_XML_DATA_FORMAT);
        }

        // normal end
        return;
    }

    protected MCMLType doError(MCMLException isExp) throws Exception {
        ServerApLogger.getInstance().writeError(isExp.getMessage());

        // --------------------------------------------------------------------------------
        // MCML/Error
        // --------------------------------------------------------------------------------
        MCMLType outputMCML = new MCMLType();
        ErrorType error = XMLTypeTools.generateErrorType(isExp.getErrorCode(),
                isExp.getExplanation(), isExp.getService());

        ResponseType response = new ResponseType();
        response.addError(error);
        response.addService(isExp.getService());
        response.addProcessOrder("0");

        ServerType server = new ServerType();
        server.addResponse(response);

        outputMCML.addVersion(MCMLStatics.VERSION);
        outputMCML.addServer(server);

        // normal end
        return outputMCML;
    }

    // ------------------------------------------
    // abstract protected member function
    // ------------------------------------------
    protected abstract Date processRequest(MCMLData data) throws Exception;

    protected abstract void postProcessRequest(String errorCode)
            throws Exception;
}
