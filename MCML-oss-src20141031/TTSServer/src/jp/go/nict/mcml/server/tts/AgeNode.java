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

package jp.go.nict.mcml.server.tts;

/**
 * AgeNode class.
 *
 *
 */
public class AgeNode extends VoiceFontNode {
    // ------------------------------------------
    // private member variables(instance field)
    // ------------------------------------------
    private AgeNode m_Child;
    private AgeNode m_Adult;
    private AgeNode m_Elder;
    private VoiceFontAttributes.Age m_NodeType;

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
        m_NodeType = VoiceFontAttributes.Age.None;
    }

    /**
     * Constructor
     *
     * @param age
     *            Age type enumeration
     */
    public AgeNode(VoiceFontAttributes.Age age) {
        m_Child = null;
        m_Adult = null;
        m_Elder = null;
        m_NodeType = age;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createTree(VoiceFontAttributes voiceFontAttributes)
            throws Exception {
        if (voiceFontAttributes.getAge() == VoiceFontAttributes.Age.Child) {
            if (m_Child == null) {
                // create ChildNode.
                m_Child = new AgeNode(VoiceFontAttributes.Age.Child);
            }
            m_Child.setNode(voiceFontAttributes);
        } else if (voiceFontAttributes.getAge() == VoiceFontAttributes.Age.Adult) {
            if (m_Adult == null) {
                // create ChildNode.
                m_Adult = new AgeNode(VoiceFontAttributes.Age.Adult);
            }
            m_Adult.setNode(voiceFontAttributes);
        } else if (voiceFontAttributes.getAge() == VoiceFontAttributes.Age.Elder) {
            if (m_Elder == null) {
                // create ChildNode.
                m_Elder = new AgeNode(VoiceFontAttributes.Age.Elder);
            }
            m_Elder.setNode(voiceFontAttributes);
        } else {
            setNode(voiceFontAttributes);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNode(VoiceFontAttributes voiceFontAttributes)
            throws Exception {
        // voiceFontAttributes is matched to check.
        if (!checkNodeType(voiceFontAttributes)) {
            throw new Exception("voiceFontAttributes unmatched NodeType.");
        }

        if ((voiceFontAttributes.getGender() == VoiceFontAttributes.Gender.None)
                && (voiceFontAttributes.getNative() == VoiceFontAttributes.Native.None)) { //
            // set voiceFontID
            setVoiceFontID(voiceFontAttributes.getVoiceFontID());
        } else {
            // create Next Tree.
            createNextNode(voiceFontAttributes);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String searchVoiceFontID(VoiceFontAttributes voiceFontAttributes) {
        String voiceFontID = null;
        if (m_NodeType == VoiceFontAttributes.Age.None) {
            // root Node.
            if (voiceFontAttributes.getAge() == VoiceFontAttributes.Age.Child) {
                if (m_Child == null) {
                    // no match voiceFontID.
                    return null;
                }
                voiceFontID = m_Child.searchVoiceFontID(voiceFontAttributes);
            } else if (voiceFontAttributes.getAge() == VoiceFontAttributes.Age.Adult) {
                if (m_Adult == null) {
                    // no match voiceFontID.
                    return null;
                }
                voiceFontID = m_Adult.searchVoiceFontID(voiceFontAttributes);
            } else if (voiceFontAttributes.getAge() == VoiceFontAttributes.Age.Elder) {
                if (m_Elder == null) {
                    // no match voiceFontID.
                    return null;
                }
                voiceFontID = m_Elder.searchVoiceFontID(voiceFontAttributes);
            } else {
                voiceFontID = getVoiceFontID(voiceFontAttributes);
            }
        } else {
            // child Nodes(Child,Adult,Elder).
            voiceFontID = getVoiceFontID(voiceFontAttributes);
        }

        return voiceFontID;
    }

    // ------------------------------------------
    // protected member functions
    // ------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkNodeType(VoiceFontAttributes voiceFontAttributes) {
        if (voiceFontAttributes.getAge() != m_NodeType) {
            // inputed voiceFontAttributes are unmatch This Node.
            return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createNextNode(VoiceFontAttributes voiceFontAttributes)
            throws Exception {
        // create NextNode.
        if (getNextNode() == null) {
            GenderNode gender = new GenderNode();

            // set NextNode.
            setNextNode(gender);
        }

        // create GenderTree.
        getNextNode().createTree(voiceFontAttributes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getVoiceFontID(VoiceFontAttributes voiceFontAttributes) {
        String voiceFontID = null;
        if (voiceFontAttributes.getGender() == VoiceFontAttributes.Gender.None
                && voiceFontAttributes.getNative() == VoiceFontAttributes.Native.None) { // no
                                                                                         // more
                                                                                         // search.
            voiceFontID = getVoiceFontID();
        } else {
            voiceFontID = getNextNode().searchVoiceFontID(voiceFontAttributes);
        }
        return voiceFontID;
    }
}
