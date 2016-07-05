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
 * Gender node class.
 *
 *
 */
public class GenderNode extends VoiceFontNode {
    // ------------------------------------------
    // private member variables(instance field)
    // ------------------------------------------
    private GenderNode m_Male;
    private GenderNode m_Female;
    private GenderNode m_Unknown;
    private VoiceFontAttributes.Gender m_NodeType;

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
        m_NodeType = VoiceFontAttributes.Gender.None;
    }

    /**
     * Constructor
     *
     * @param gender
     *            Gender enumeration
     */
    public GenderNode(VoiceFontAttributes.Gender gender) {
        m_Male = null;
        m_Female = null;
        m_Unknown = null;
        m_NodeType = gender;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createTree(VoiceFontAttributes voiceFontAttributes)
            throws Exception {
        if (voiceFontAttributes.getGender() == VoiceFontAttributes.Gender.Male) {
            if (m_Male == null) {
                // create ChildNode.
                m_Male = new GenderNode(VoiceFontAttributes.Gender.Male);
            }
            m_Male.setNode(voiceFontAttributes);
        } else if (voiceFontAttributes.getGender() == VoiceFontAttributes.Gender.Female) {
            if (m_Female == null) {
                // create ChildNode.
                m_Female = new GenderNode(VoiceFontAttributes.Gender.Female);
            }
            m_Female.setNode(voiceFontAttributes);
        } else if (voiceFontAttributes.getGender() == VoiceFontAttributes.Gender.Unknown) {
            if (m_Unknown == null) {
                // create ChildNode.
                m_Unknown = new GenderNode(VoiceFontAttributes.Gender.Unknown);
            }
            m_Unknown.setNode(voiceFontAttributes);
        } else {

            // rootNode
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

        if ((voiceFontAttributes.getNative() == VoiceFontAttributes.Native.None)) {
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
        if (m_NodeType == VoiceFontAttributes.Gender.None) {
            // root Node.
            if (voiceFontAttributes.getGender() == VoiceFontAttributes.Gender.Male) {
                if (m_Male == null) {
                    // no match voiceFontID.
                    return null;
                }
                voiceFontID = m_Male.searchVoiceFontID(voiceFontAttributes);
            } else if (voiceFontAttributes.getGender() == VoiceFontAttributes.Gender.Female) {
                if (m_Female == null) {
                    // no match voiceFontID.
                    return null;
                }
                voiceFontID = m_Female.searchVoiceFontID(voiceFontAttributes);
            } else if (voiceFontAttributes.getGender() == VoiceFontAttributes.Gender.Unknown) {
                if (m_Unknown == null) {
                    // no match voiceFontID.
                    return null;
                }
                voiceFontID = m_Unknown.searchVoiceFontID(voiceFontAttributes);
            } else {

                voiceFontID = getVoiceFontID(voiceFontAttributes);
            }
        } else {
            // child Nodes(Male,Female,Unknown).
            voiceFontID = getVoiceFontID(voiceFontAttributes);
        }

        return voiceFontID;
    }

    // ------------------------------------------
    // protected member functions
    // ------------------------------------------
    @Override
    protected boolean checkNodeType(VoiceFontAttributes voiceFontAttributes)
            throws Exception {
        if (voiceFontAttributes.getGender() != m_NodeType) {
            // inputed voiceFontAttributes are unmatch This Node.
            return false;
        }

        return true;
    }

    @Override
    protected void createNextNode(VoiceFontAttributes voiceFontAttributes)
            throws Exception {
        // create NextNode.
        if (getNextNode() == null) {
            NativeNode nativeNode = new NativeNode();

            // set NextNode.
            setNextNode(nativeNode);
        }

        // create NativeTree.
        getNextNode().createTree(voiceFontAttributes);
    }

    @Override
    protected String getVoiceFontID(VoiceFontAttributes voiceFontAttributes) {
        String voiceFontID = null;
        if (voiceFontAttributes.getNative() == VoiceFontAttributes.Native.None) { // no
                                                                                  // more
                                                                                  // search.
            voiceFontID = getVoiceFontID();
        } else {
            voiceFontID = getNextNode().searchVoiceFontID(voiceFontAttributes);
        }
        return voiceFontID;
    }
}
