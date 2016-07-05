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

import java.util.ArrayList;

/**
 * AMNode abstract class.
 *
 */
public abstract class AMNode {
    // ------------------------------------------
    // private member variables(instance field)
    // ------------------------------------------
    private ArrayList<String> m_CommandList;
    private AMNode m_NextNode;

    // ------------------------------------------
    // public member functions
    // ------------------------------------------
    /**
     * Default constructor
     */
    public AMNode() {
        m_CommandList = new ArrayList<String>();
        m_CommandList.clear();
        m_NextNode = null;
    }

    /**
     * createTree
     *
     * @param amCommands
     * @throws Exception
     */
    public abstract void createTree(AMCommands amCommands) throws Exception;

    /**
     * Sets Node.
     *
     * @param amCommands
     * @throws Exception
     */
    public abstract void setNode(AMCommands amCommands) throws Exception;

    /**
     * searchAMCommands
     *
     * @param amCommands
     * @return ArrayList<String>
     */
    public abstract ArrayList<String> searchAMCommands(AMCommands amCommands);

    // ------------------------------------------
    // protected member functions
    // ------------------------------------------
    protected abstract void createNextNode(AMCommands amCommands)
            throws Exception;

    protected abstract boolean checkNodeType(AMCommands amCommands)
            throws Exception;

    protected abstract ArrayList<String> getCommands(AMCommands amCommands);

    protected ArrayList<String> getCommandList() {
        // copy to commandList.
        ArrayList<String> commandList = new ArrayList<String>(m_CommandList);
        return commandList;
    }

    protected void setCommandList(ArrayList<String> commandList) {
        // replace commandList.
        if (!m_CommandList.isEmpty()) {
            m_CommandList.clear();
        }
        m_CommandList.addAll(commandList);
    }

    protected AMNode getNextNode() {
        return m_NextNode;
    }

    protected void setNextNode(AMNode nextNode) {
        m_NextNode = nextNode;
    }
}
