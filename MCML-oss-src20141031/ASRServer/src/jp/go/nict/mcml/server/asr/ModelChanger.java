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

package jp.go.nict.mcml.server.asr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import jp.go.nict.mcml.serverap.common.ServerApLogger;
import jp.go.nict.mcml.servlet.MCMLException;
import jp.go.nict.mcml.xml.types.InputUserProfileType;
import jp.go.nict.mcml.xml.types.MCMLType;

/**
 * ModelChangerクラスです。
 *
 */
public class ModelChanger {
    // ------------------------------------------
    // private member constants
    // ------------------------------------------
    private static final String FLAG_HOST = "-host=";
    private static final String FLAG_RPCNUMBER = "-rpcNumber=";

    // ------------------------------------------
    // private member variables(instance field)
    // ------------------------------------------
    private LMCommandManager m_LMCommandManager;
    private AMCommandManager m_AMCommandManager;
    private int m_ServerType;
    private String m_RPCCommandSender;
    private String m_EngineHost;
    private String m_RPCNumber;

    // ------------------------------------------
    // public member functions
    // ------------------------------------------
    public ModelChanger() {
        m_LMCommandManager = null;
        m_AMCommandManager = null;
        m_ServerType = 0;
        m_RPCCommandSender = "";
        m_EngineHost = "";
        m_RPCNumber = "";
    }

    /**
     * 初期化
     *
     * @param serverType
     * @param rpcCommandSender
     * @param engineHost
     * @param rpcNumber
     * @param lmCommandFileName
     * @param amCommandFileName
     * @throws Exception
     */
    public void initialize(int serverType, String rpcCommandSender,
            String engineHost, int rpcNumber, String lmCommandFileName,
            String amCommandFileName) throws Exception {
        // check Model Change Function On/Off.
        if (rpcCommandSender == null || rpcCommandSender.isEmpty()) {
            return;
        }

        // check input Argument.
        if (engineHost.isEmpty()) {
            throw new Exception("Engine Host or RPC Number is worng.");
        }

        // save Parameters.
        m_ServerType = serverType;
        m_RPCCommandSender = rpcCommandSender;
        m_EngineHost = engineHost;
        m_RPCNumber = Integer.toString(rpcNumber);

        // create Language Model Change Command Manager.
        if (m_LMCommandManager == null) {
            m_LMCommandManager = new LMCommandManager();
            m_LMCommandManager.readLMCommandTableFile(lmCommandFileName);
        }

        // create Acoustic Model Change Command Manager.
        if (m_AMCommandManager == null) {
            m_AMCommandManager = new AMCommandManager();
            m_AMCommandManager.readAMCommandTableFile(amCommandFileName);
        }
    }

    /**
     * changeLanguageModel
     *
     * @param task
     * @throws MCMLException
     */
    public void changeLanguageModel(String task) throws MCMLException {
        try {
            // get Language Model Change Command.
            String command = null;
            if (m_LMCommandManager != null) {
                command = m_LMCommandManager.getLMCommand(task);
            }

            // set Language Model Change Command
            if (command != null) {
                ArrayList<String> commandList = new ArrayList<String>();
                commandList.add(command);
                sendRPCCommand(commandList);
            }
        } catch (IOException e) {
            writeLog("Language Model failed to change.");
            throw new MCMLException("Language Model failed to change.",
                    MCMLException.ERROR, m_ServerType,
                    MCMLException.OTHER_ERROR);
        } catch (InterruptedException e) {
            writeLog("Language Model failed to change.");
            throw new MCMLException("Language Model failed to change.",
                    MCMLException.ERROR, m_ServerType,
                    MCMLException.OTHER_ERROR);
        }

        return;
    }

    /**
     * changeAcousticModel
     *
     * @param inputMCML
     * @param inputUserProfile
     * @throws MCMLException
     */
    public void changeAcousticModel(MCMLType inputMCML,
            InputUserProfileType inputUserProfile) throws MCMLException {
        try {
            if (inputUserProfile == null) {
                return;
            }

            // try to get Acoustic Model Change Command by ID.
            ArrayList<String> commands = null;
            if (m_AMCommandManager != null) {
                synchronized (m_AMCommandManager) {
                    commands = m_AMCommandManager.getCommands(inputMCML,
                            inputUserProfile);
                }
            }
            if (commands != null) {
                // set Language Model Change Command
                ArrayList<String> commandList = new ArrayList<String>();
                commandList.addAll(commands);
                sendRPCCommand(commandList);
            }
        } catch (IOException e) {
            writeLog("Acoustic Model failed to change.");
            throw new MCMLException("Acoustic Model failed to change.",
                    MCMLException.ERROR, m_ServerType,
                    MCMLException.OTHER_ERROR);
        } catch (InterruptedException e) {
            writeLog("Acoustic Model failed to change.");
            throw new MCMLException("Acoustic Model failed to change.",
                    MCMLException.ERROR, m_ServerType,
                    MCMLException.OTHER_ERROR);
        } catch (Exception e) {
            writeLog(e.getMessage());
            throw new MCMLException(e.getMessage(), MCMLException.ERROR,
                    m_ServerType, MCMLException.OTHER_ERROR);
        }
    }

    /**
     * replaceAMCommand
     *
     * @param amCommand
     * @throws Exception
     */
    public void replaceAMCommand(String amCommand) throws Exception {
        if (m_AMCommandManager != null) {
            synchronized (m_AMCommandManager) {
                m_AMCommandManager.replaceAMCommand(amCommand);
            }
        }
    }

    // ------------------------------------------
    // private member functions
    // ------------------------------------------
    private void sendRPCCommand(ArrayList<String> commandList)
            throws IOException, InterruptedException {
        if (commandList == null || commandList.isEmpty()) {
            // no Command List.
            return;
        }

        for (int i = 0; i < commandList.size(); i++) {
            if (commandList.get(i).isEmpty()) {
                // Command line is empty.
                ServerApLogger.getInstance().writeWarning(
                        "Command line is empty.");
                continue;
            }
            // split reciever and -command=options
            String[] elements = commandList.get(i).split(" ", 2);

            String[] sendCommands = new String[5];
            sendCommands[0] = m_RPCCommandSender;
            sendCommands[1] = FLAG_HOST + m_EngineHost;
            sendCommands[2] = FLAG_RPCNUMBER + m_RPCNumber;
            sendCommands[3] = elements[0];
            sendCommands[4] = elements[1];
            Process rpcProc = Runtime.getRuntime().exec(sendCommands);
            BufferedReader rpcState = new BufferedReader(new InputStreamReader(
                    rpcProc.getErrorStream()));
            writeLog(rpcState.readLine());

            // RPC Process wait.
            rpcProc.waitFor();

            // RPC Process terminate.
            rpcProc.destroy();
        }
    }

    private void writeLog(String message) {
        ServerApLogger.getInstance().writeDebug(message);
    }
}
