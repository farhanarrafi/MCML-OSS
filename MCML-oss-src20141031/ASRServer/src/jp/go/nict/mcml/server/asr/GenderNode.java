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
 * Gender node class.
 *
 */
public class GenderNode extends AMNode {
    // ------------------------------------------
    // private member variables(instance field)
    // ------------------------------------------
    private GenderNode m_Male;
    private GenderNode m_Female;
    private GenderNode m_Unknown;
    private AMCommands.Gender m_NodeType;

    // ------------------------------------------
    // public member functions
    // ------------------------------------------
    /**
     * Default constructor
     */
    public GenderNode() {
        m_Male = null;
        m_Female = null;
        m_Unknown = null;
        m_NodeType = AMCommands.Gender.None;
    }

    /**
     * Constructor
     *
     * @param gender
     */
    public GenderNode(AMCommands.Gender gender) {
        m_Male = null;
        m_Female = null;
        m_Unknown = null;
        m_NodeType = gender;
    }

    /**
     * createTree
     */
    @Override
    public void createTree(AMCommands amCommands) throws Exception {
        if (amCommands.getGender() == AMCommands.Gender.Male) {
            if (m_Male == null) {
                // create ChildNode.
                m_Male = new GenderNode(AMCommands.Gender.Male);
            }
            m_Male.setNode(amCommands);
        } else if (amCommands.getGender() == AMCommands.Gender.Female) {
            if (m_Female == null) {
                // create ChildNode.
                m_Female = new GenderNode(AMCommands.Gender.Female);
            }
            m_Female.setNode(amCommands);
        } else if (amCommands.getGender() == AMCommands.Gender.Unknown) {
            if (m_Unknown == null) {
                // create ChildNode.
                m_Unknown = new GenderNode(AMCommands.Gender.Unknown);
            }
            m_Unknown.setNode(amCommands);
        } else {

            // rootNode
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

        if ((amCommands.getNative() == AMCommands.Native.None)) {
            // set Acoustic Model Change Commands
            setCommandList(amCommands.getCommands());
        } else {
            // create Next Tree.
            createNextNode(amCommands);
        }
    }

    /**
     * searchAMCommands
     */
    @Override
    public ArrayList<String> searchAMCommands(AMCommands amCommands) {
        ArrayList<String> commandList = null;
        if (m_NodeType == AMCommands.Gender.None) {
            // root Node.
            if (amCommands.getGender() == AMCommands.Gender.Male) {
                if (m_Male == null) {
                    // no match Change Command.
                    return null;
                }
                commandList = m_Male.searchAMCommands(amCommands);
            } else if (amCommands.getGender() == AMCommands.Gender.Female) {
                if (m_Female == null) {
                    // no match Change Command.
                    return null;
                }
                commandList = m_Female.searchAMCommands(amCommands);
            } else if (amCommands.getGender() == AMCommands.Gender.Unknown) {
                if (m_Unknown == null) {
                    // no match Change Command.
                    return null;
                }
                commandList = m_Unknown.searchAMCommands(amCommands);
            } else {

                commandList = getCommands(amCommands);
            }
        } else {
            // child Nodes(Male,Female,Unknown).
            commandList = getCommands(amCommands);
        }

        return commandList;
    }

    // ------------------------------------------
    // protected member functions
    // ------------------------------------------
    @Override
    protected boolean checkNodeType(AMCommands amCommands) throws Exception {
        if (amCommands.getGender() != m_NodeType) {
            // inputed Commdands are unmatch This Node.
            return false;
        }

        return true;
    }

    @Override
    protected void createNextNode(AMCommands amCommands) throws Exception {
        // create NextNode.
        if (getNextNode() == null) {
            NativeNode nativeNode = new NativeNode();

            // set NextNode.
            setNextNode(nativeNode);
        }

        // create NativeTree.
        getNextNode().createTree(amCommands);
    }

    @Override
    protected ArrayList<String> getCommands(AMCommands amCommands) {
        ArrayList<String> commandList = null;
        if (amCommands.getNative() == AMCommands.Native.None) { // no more
                                                                // search.
            commandList = getCommandList();
        } else {
            commandList = getNextNode().searchAMCommands(amCommands);
        }
        return commandList;
    }
}
