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

package jp.go.nict.S2SSample;

import jp.go.nict.mcml.xml.XMLProcessor;
import jp.go.nict.mcml.xml.types.MCMLType;

/**
 * Voice translation request, results data
 * 
 */
public class SpeechTranslationData {
    /** XML server */
    protected XMLProcessor mXMLProcessor = new XMLProcessor();
    /** XML */
    private MCMLType mMCML = null;
    /**  Binary data */
    private byte[] mBinaryData = null;

    /**  Flag: error */
    private boolean mFlagError = false;
    /**  Flag: SR_IN */
    private boolean mFlagSR_IN = false;
    /**  Flag: SR_IN */
    private boolean mFlagMT_IN = false;
    /**  Flag: SR_IN */
    private boolean mFlagSS_IN = false;
    /**  Flag: Reverse translation */
    private boolean mFlagBackTranslation = false;
    /**  Flag: Head of asynchronous divided transmission */
    private boolean mFlagSRDivFirstResponse = false;

    /** Optional URL */
    private String msOptionURL = null;

    /**
     * Optional URL setting
     * 
     * @param sOptionURL
     */
    public void setOptionURL(String sOptionURL) {
        msOptionURL = sOptionURL;
    }

    /**
     * Sets MCML
     * 
     * @param mcml
     */
    public void setMCML(MCMLType mcml) throws Exception {
        mMCML = new MCMLType(mcml);

        mFlagError = mMCML.hasServer() && mMCML.getServer().hasResponse()
                && mMCML.getServer().getResponse().hasError();
        mFlagSR_IN = mMCML.hasServer()
                && mMCML.getServer().hasRequest()
                && mMCML.getServer().getRequest().hasService()
                && mMCML.getServer().getRequest().getService().getValue()
                        .equals("ASR");
        mFlagMT_IN = mMCML.hasServer()
                && mMCML.getServer().hasRequest()
                && mMCML.getServer().getRequest().hasService()
                && mMCML.getServer().getRequest().getService().getValue()
                        .equals("MT");
        mFlagSS_IN = mMCML.hasServer()
                && mMCML.getServer().hasRequest()
                && mMCML.getServer().getRequest().hasService()
                && mMCML.getServer().getRequest().getService().getValue()
                        .equals("TTS");
    }

    /**
     * Sets Binary
     * 
     * @param binaryData
     */
    public void setBinaryData(byte[] binaryData) {
        if (binaryData == null) {
            binaryData = null;
        } else {
            mBinaryData = new byte[binaryData.length];
            System.arraycopy(binaryData, 0, mBinaryData, 0, binaryData.length);
        }
    }

    /**
     * Sets error
     */
    public void setError() {
        mFlagError = true;
    }

    /**
     * Sets SR_IN
     */
    public void setSR_IN() {
        mFlagSR_IN = true;
    }

    /**
     * Sets MT_IN
     */
    public void setMT_IN() {
        mFlagMT_IN = true;
    }

    /**
     * Sets SS_IN
     */
    public void setSS_IN() {
        mFlagSS_IN = true;
    }

    /**
     * Sets asynchronous divided transmission head
     */
    public void setSRDivFirstResponse() {
        mFlagSRDivFirstResponse = true;
    }

    /**
     * Sets as reverse translation.
     */
    public void setBackTranslation() {
        mFlagBackTranslation = true;
    }

    /**
     * Gets MCML.
     * 
     * @return MCML
     */
    public MCMLType getMCML() {
        return mMCML;
    }

    /**
     * Gets optional URL.
     * 
     * @return Optional URL
     */
    public String getOptionURL() {
        return msOptionURL;
    }

    /**
     * Gets binary data
     * 
     * @return  binary data
     */
    public byte[] getBinaryData() {
        return mBinaryData;
    }

    /**
     * Determines whether there is an error or not.
     * 
     * @return mFlagError
     */
    public boolean isError() {
        return mFlagError;
    }

    /**
     * Determines if MCML exists.
     * 
     * @return {@code true} if MCMLType is other than {@code null}, otherwise {@code false}
     */
    public boolean isExistMCML() {
        if (mMCML != null) {
            return true;
        }
        return false;
    }

    /**
     * Determines whether it is SR_IN or not.
     * 
     * @return mFlagSR_IN
     */
    public boolean isSR_IN() {
        return mFlagSR_IN;
    }

    /**
     * Determines whether it is MT_IN or not.
     * 
     * @return mFlagMT_IN
     */
    public boolean isMT_IN() {
        return mFlagMT_IN;
    }

    /**
     * Determines whether it is SS_IN or not.
     * 
     * @return mFlagSS_IN
     */
    public boolean isSS_IN() {
        return mFlagSS_IN;
    }

    /**
     * Determines whether it is SRDivFirstResponse or not.
     * 
     * @return mFlagSRDivFirstResponse
     */
    public boolean isSRDivFirstResponse() {
        return mFlagSRDivFirstResponse;
    }

    /**
     * Determines whether it is SR_OUT or not.
     * 
     * @return boolean
     * @throws Exception
     */
    public boolean isSR_OUT() throws Exception {
        if (mMCML != null) {
            return mMCML.hasServer()
                    && mMCML.getServer().hasResponse()
                    && mMCML.getServer().getResponse().hasService()
                    && mMCML.getServer().getResponse().getService().getValue()
                            .equals("ASR");
        }
        return false;
    }

    /**
     * Determines whether it is MT_OUT or not.
     * 
     * @return boolean
     * @throws Exception
     */
    public boolean isMT_OUT() throws Exception {
        if (mMCML != null) {
            return mMCML.hasServer()
                    && mMCML.getServer().hasResponse()
                    && mMCML.getServer().getResponse().hasService()
                    && mMCML.getServer().getResponse().getService().getValue()
                            .equals("MT");
        }
        return false;
    }

    /**
     * Determines whether it is BackTranslation or not.
     * 
     * @return boolean
     */
    public boolean isBackTranslation() {
        return mFlagBackTranslation;
    }

    /**
     * Determines whether it is SS_OUT or not.
     * 
     * @return boolean
     * @throws Exception
     */
    public boolean isSS_OUT() throws Exception {
        if (mMCML != null) {
            return mMCML.hasServer()
                    && mMCML.getServer().hasResponse()
                    && mMCML.getServer().getResponse().hasService()
                    && mMCML.getServer().getResponse().getService().getValue()
                            .equals("TTS");
        }
        return false;
    }

    /**
     * Sets flag here to determine if  error/SR_IN which passes XML character string to MCML type.
     * 
     * @param xmlString
     */
    public void parse(String xmlString) {
        try {
            if (xmlString != null) {
                mMCML = mXMLProcessor.parse(xmlString);
                mFlagError = mMCML.hasServer()
                        && mMCML.getServer().hasResponse()
                        && mMCML.getServer().getResponse().hasError();
                mFlagSR_IN = mMCML.hasServer()
                        && mMCML.getServer().hasRequest()
                        && mMCML.getServer().getRequest().hasService()
                        && mMCML.getServer().getRequest().getService()
                                .getValue().equals("ASR");
                mFlagMT_IN = mMCML.hasServer()
                        && mMCML.getServer().hasRequest()
                        && mMCML.getServer().getRequest().hasService()
                        && mMCML.getServer().getRequest().getService()
                                .getValue().equals("MT");
                mFlagSS_IN = mMCML.hasServer()
                        && mMCML.getServer().hasRequest()
                        && mMCML.getServer().getRequest().hasService()
                        && mMCML.getServer().getRequest().getService()
                                .getValue().equals("TTS");
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * Sets character string from MCML to XML.
     * 
     * @return XML character string
     */
    public String generate() {
        String sRetVal = null;
        try {
            if (mMCML != null) {
                sRetVal = mXMLProcessor.generate(mMCML);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sRetVal;
    }

}
