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
 * NativeNode class.
 *
 */
public class NativeNode extends AMNode {
    // ------------------------------------------
    // private member variables(instance field)
    // ------------------------------------------
    private NativeNode m_Native;
    private NativeNode m_NonNative;
    private AMCommands.Native m_NodeType;

    // ------------------------------------------
    // public member functions
    // ------------------------------------------
    /**
     * Default constructor
     */
    public NativeNode() {
        m_Native = null;
        m_NonNative = null;
        m_NodeType = AMCommands.Native.None;
    }

    /**
     * Constructor
     *
     * @param nativeType
     */
    public NativeNode(AMCommands.Native nativeType) {
        m_Native = null;
        m_NonNative = null;
        m_NodeType = nativeType;
    }

    /**
     * createTree
     */
    @Override
    public void createTree(AMCommands amCommands) throws Exception {
        if (amCommands.getNative() == AMCommands.Native.Native) {
            if (m_Native == null) {
                // create ChildNode.
                m_Native = new NativeNode(AMCommands.Native.Native);
            }
            m_Native.setNode(amCommands);
        } else if (amCommands.getNative() == AMCommands.Native.NonNative) {
            if (m_NonNative == null) {
                // create ChildNode.
                m_NonNative = new NativeNode(AMCommands.Native.NonNative);
            }
            m_NonNative.setNode(amCommands);
        } else {

            setNode(amCommands);
        }
    }

    /**
     * setNode
     */
    @Override
    public void setNode(AMCommands amCommands) throws Exception {
        // Commands is matched to check.
        if (!checkNodeType(amCommands)) {
            throw new Exception("Commands unmatched NodeType.");
        }

        // set Acoustic Model Change Commands
        // no more Search Tree.
        setCommandList(amCommands.getCommands());
    }

    /**
     * searchAMCommands
     */
    @Override
    public ArrayList<String> searchAMCommands(AMCommands amCommands) {
        ArrayList<String> commandList = null;
        if (m_NodeType == AMCommands.Native.None) {
            // root Node.
            if (amCommands.getNative() == AMCommands.Native.Native) {
                if (m_Native == null) {
                    // no match Change Command.
                    return null;
                }
                commandList = m_Native.searchAMCommands(amCommands);
            } else if (amCommands.getNative() == AMCommands.Native.NonNative) {
                if (m_NonNative == null) {
                    // no match Change Command.
                    return null;
                }
                commandList = m_NonNative.searchAMCommands(amCommands);
            } else {

                commandList = getCommands(amCommands);
            }
        } else {
            // child Nodes(Native,NonNative).
            commandList = getCommands(amCommands);
        }

        return commandList;
    }

    // ------------------------------------------
    // protected member functions
    // ------------------------------------------
    @Override
    protected boolean checkNodeType(AMCommands amCommands) throws Exception {
        if (amCommands.getNative() != m_NodeType) {
            // inputed Commdands are unmatch This Node.
            return false;
        }

        return true;
    }

    @Override
    protected void createNextNode(AMCommands amCommands) throws Exception {
        // no more Create Search Tree.
        return;
    }

    @Override
    protected ArrayList<String> getCommands(AMCommands amCommands) {
        ArrayList<String> commandList = getCommandList();
        return commandList;
    }
}
