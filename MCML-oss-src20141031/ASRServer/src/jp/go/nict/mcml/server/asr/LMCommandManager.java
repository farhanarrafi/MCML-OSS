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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import jp.go.nict.mcml.serverap.common.ServerApLogger;

/**
 * LMCommandManager class.
 * 
 */
public class LMCommandManager {
    // ------------------------------------------
    // private member constant(class field)
    // ------------------------------------------
    private static String defaultTask = "Default";

    // ------------------------------------------
    // private member variable(instance field)
    // ------------------------------------------
    private Properties m_LMCommandList;
    private String m_AppliedLangageModelTask;

    LMCommandManager() {
        m_LMCommandList = new Properties();
        m_AppliedLangageModelTask = "";
    }

    /**
     * readLMCommandTableFile
     * 
     * @param fileName
     * @throws IOException
     */
    public void readLMCommandTableFile(String fileName) throws IOException {
        if (fileName == null || fileName.isEmpty()) {
            return;
        }

        FileInputStream inputFile = new FileInputStream(fileName);
        InputStreamReader reader = new InputStreamReader(inputFile, "UTF-8");
        BufferedReader fileReader = new BufferedReader(reader);

        // load Language Model CommandTable File
        m_LMCommandList.load(fileReader);

        return;
    }

    /**
     * get Language Model Command from the list corresponds to task string
     * 
     * @param task
     * @return lmCommand
     */
    public String getLMCommand(String task) {
        String lmCommand = null;
        String keyTask = task;

        // already setting Language Model for input task.
        if (!task.equalsIgnoreCase(m_AppliedLangageModelTask)) {
            lmCommand = m_LMCommandList.getProperty(keyTask);

            // if the specified task is not found in the LMCommandList
            if (lmCommand == null) {
                keyTask = defaultTask;
                if (!m_AppliedLangageModelTask.equalsIgnoreCase(keyTask)) {
                    lmCommand = m_LMCommandList.getProperty(keyTask);
                }
            } else if (lmCommand.isEmpty()) {
                // Language Model Command Table is wrong.
                ServerApLogger.getInstance().writeWarning(
                        "Language Model Command Table is wrong.:Task="
                                + keyTask);
                keyTask = defaultTask;
                if (!m_AppliedLangageModelTask.equalsIgnoreCase(keyTask)) {
                    lmCommand = m_LMCommandList.getProperty(keyTask);
                }
            }

        }

        // save apply Language Model Task Name.
        if (lmCommand != null && !lmCommand.isEmpty()) {
            m_AppliedLangageModelTask = keyTask;
        }

        return lmCommand;
    }
}
