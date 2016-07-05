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
 *Age node class.
 *
 */
public class AgeNode extends AMNode {
    // ------------------------------------------
    // private member variables(instance field)
    // ------------------------------------------
    private AgeNode m_Child;
    private AgeNode m_Adult;
    private AgeNode m_Elder;
    private AMCommands.Age m_NodeType;

    // ------------------------------------------
    // public member functions
    // ------------------------------------------
    /**
     * Default constructor
     */
    public AgeNode() {
        m_Child = null;
        m_Adult = null;
        m_Elder = null;
        m_NodeType = AMCommands.Age.None;
    }

    /**
     * Constructor
     *
     * @param age
     */
    public AgeNode(AMCommands.Age age) {
        m_Child = null;
        m_Adult = null;
        m_Elder = null;
        m_NodeType = age;
    }

    /**
     * createTree
     */
    @Override
    public void createTree(AMCommands amCommands) throws Exception {
        if (amCommands.getAge() == AMCommands.Age.Child) {
            if (m_Child == null) {
                // create ChildNode.
                m_Child = new AgeNode(AMCommands.Age.Child);
            }
            m_Child.setNode(amCommands);
        } else if (amCommands.getAge() == AMCommands.Age.Adult) {
            if (m_Adult == null) {
                // create ChildNode.
                m_Adult = new AgeNode(AMCommands.Age.Adult);
            }
            m_Adult.setNode(amCommands);
        } else if (amCommands.getAge() == AMCommands.Age.Elder) {
            if (m_Elder == null) {
                // create ChildNode.
                m_Elder = new AgeNode(AMCommands.Age.Elder);
            }
            m_Elder.setNode(amCommands);
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

        if ((amCommands.getGender() == AMCommands.Gender.None)
                && (amCommands.getNative() == AMCommands.Native.None)) { //
                                                                         // set
                                                                         // Acoustic
                                                                         // Model
                                                                         // Change
                                                                         // Commands
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
        if (m_NodeType == AMCommands.Age.None) {
            // root Node.
            if (amCommands.getAge() == AMCommands.Age.Child) {
                if (m_Child == null) {
                    // no match Change Command.
                    return null;
                }
                commandList = m_Child.searchAMCommands(amCommands);
            } else if (amCommands.getAge() == AMCommands.Age.Adult) {
                if (m_Adult == null) {
                    // no match Change Command.
                    return null;
                }
                commandList = m_Adult.searchAMCommands(amCommands);
            } else if (amCommands.getAge() == AMCommands.Age.Elder) {
                if (m_Elder == null) {
                    // no match Change Command.
                    return null;
                }
                commandList = m_Elder.searchAMCommands(amCommands);
            } else {

                commandList = getCommands(amCommands);
            }
        } else {
            // child Nodes(Child,Adult,Elder).
            commandList = getCommands(amCommands);
        }

        return commandList;
    }

    // ------------------------------------------
    // protected member functions
    // ------------------------------------------
    @Override
    protected boolean checkNodeType(AMCommands amCommands) {
        if (amCommands.getAge() != m_NodeType) {
            // inputed Commdands are unmatch This Node.
            return false;
        }

        return true;
    }

    @Override
    protected void createNextNode(AMCommands amCommands) throws Exception {
        // create NextNode.
        if (getNextNode() == null) {
            GenderNode gender = new GenderNode();

            // set NextNode.
            setNextNode(gender);
        }

        // create GenderTree.
        getNextNode().createTree(amCommands);
    }

    @Override
    protected ArrayList<String> getCommands(AMCommands amCommands) {
        ArrayList<String> commandList = null;
        if (amCommands.getGender() == AMCommands.Gender.None
                && amCommands.getNative() == AMCommands.Native.None) { // no
                                                                       // more
                                                                       // search.
            commandList = getCommandList();
        } else {
            commandList = getNextNode().searchAMCommands(amCommands);
        }
        return commandList;
    }
}
