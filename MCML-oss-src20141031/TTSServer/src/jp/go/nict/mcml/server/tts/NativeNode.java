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
 * Native node class.
 *
 *
 */
public class NativeNode extends VoiceFontNode {
    // ------------------------------------------
    // private member variables(instance field)
    // ------------------------------------------
    private NativeNode m_Native;
    private NativeNode m_NonNative;
    private VoiceFontAttributes.Native m_NodeType;

    // ------------------------------------------
    // public member functions
    // ------------------------------------------
    /** Default constructor */
    public NativeNode() {
        m_Native = null;
        m_NonNative = null;
        m_NodeType = VoiceFontAttributes.Native.None;
    }

    /**
     * Constructor
     *
     * @param nativeType
     */
    public NativeNode(VoiceFontAttributes.Native nativeType) {
        m_Native = null;
        m_NonNative = null;
        m_NodeType = nativeType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createTree(VoiceFontAttributes voiceFontAttributes)
            throws Exception {
        if (voiceFontAttributes.getNative() == VoiceFontAttributes.Native.Native) {
            if (m_Native == null) {
                // create ChildNode.
                m_Native = new NativeNode(VoiceFontAttributes.Native.Native);
            }
            m_Native.setNode(voiceFontAttributes);
        } else if (voiceFontAttributes.getNative() == VoiceFontAttributes.Native.NonNative) {
            if (m_NonNative == null) {
                // create ChildNode.
                m_NonNative = new NativeNode(
                        VoiceFontAttributes.Native.NonNative);
            }
            m_NonNative.setNode(voiceFontAttributes);
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

        // set VoiceFontID
        // no more Search Tree.
        setVoiceFontID(voiceFontAttributes.getVoiceFontID());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String searchVoiceFontID(VoiceFontAttributes voiceFontAttributes) {
        String voiceFontID = null;
        if (m_NodeType == VoiceFontAttributes.Native.None) {
            // root Node.
            if (voiceFontAttributes.getNative() == VoiceFontAttributes.Native.Native) {
                if (m_Native == null) {
                    // no match voiceFontID.
                    return null;
                }
                voiceFontID = m_Native.searchVoiceFontID(voiceFontAttributes);
            } else if (voiceFontAttributes.getNative() == VoiceFontAttributes.Native.NonNative) {
                if (m_NonNative == null) {
                    // no match voiceFontID.
                    return null;
                }
                voiceFontID = m_NonNative
                        .searchVoiceFontID(voiceFontAttributes);
            } else {

                voiceFontID = getVoiceFontID(voiceFontAttributes);
            }
        } else {
            // child Nodes(Native,NonNative).
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
    protected boolean checkNodeType(VoiceFontAttributes voiceFontAttributes)
            throws Exception {
        if (voiceFontAttributes.getNative() != m_NodeType) {
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
        // no more Create Search Tree.
        return;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getVoiceFontID(VoiceFontAttributes voiceFontAttributes) {
        String voiceFontID = getVoiceFontID();
        return voiceFontID;
    }
}
