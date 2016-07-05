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
 * Voice font node abstract class.
 * 
 */
public abstract class VoiceFontNode {
    // ------------------------------------------
    // private member variables(instance field)
    // ------------------------------------------
    private String m_VoiceFontID;
    private VoiceFontNode m_NextNode;

    // ------------------------------------------
    // public member functions
    // ------------------------------------------
    /** Default constructor */
    public VoiceFontNode() {
        m_VoiceFontID = "";
        m_NextNode = null;
    }

    /**
     * Creates tree from voice font attribute class parameter.
     * 
     * @param voiceFontAttributes
     * @throws Exception
     */
    public abstract void createTree(VoiceFontAttributes voiceFontAttributes)
            throws Exception;

    /**
     * Sets voice font attribute class to node.
     * 
     * @param voiceFontAttributes
     * @throws Exception
     */
    public abstract void setNode(VoiceFontAttributes voiceFontAttributes)
            throws Exception;

    /**
     * Searches voice font ID from voice font attribute class.
     * 
     * @param voiceFontAttributes
     * @return Voice font ID
     */
    public abstract String searchVoiceFontID(
            VoiceFontAttributes voiceFontAttributes);

    // ------------------------------------------
    // protected member functions
    // ------------------------------------------
    /**
     * Creates next node from voice font attribute class.
     * 
     * @param voiceFontAttributes
     *            Voice font attribute class
     */
    protected abstract void createNextNode(
            VoiceFontAttributes voiceFontAttributes) throws Exception;

    /**
     * Determine node type from voice font attribute class.
     * 
     * @param voiceFontAttributes
     *            Voice font attribute class
     */
    protected abstract boolean checkNodeType(
            VoiceFontAttributes voiceFontAttributes) throws Exception;

    /**
     * Gets voice font ID from voice font attribute class.
     * 
     * @param voiceFontAttributes
     *            Voice font attribute class
     */
    protected abstract String getVoiceFontID(
            VoiceFontAttributes voiceFontAttributes);

    /**
     * Gets voice font ID.
     * 
     */
    protected String getVoiceFontID() {
        // copy to VoiceFontID.
        return m_VoiceFontID;
    }

    /**
     * Sets voice font ID.
     * 
     * @param voiceFontID
     */
    protected void setVoiceFontID(String voiceFontID) {
        // replace VoiceFontID.
        m_VoiceFontID = voiceFontID;
    }

    /**
     * Gets next node.
     * 
     */
    protected VoiceFontNode getNextNode() {
        return m_NextNode;
    }

    /**
     * Sets next node.
     * 
     * @param nextNode
     *             Next node
     */
    protected void setNextNode(VoiceFontNode nextNode) {
        m_NextNode = nextNode;
    }
}
