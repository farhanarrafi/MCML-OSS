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

//-------------------------------------------------------------------
//Ver.3.0
//2011/12/06
//-------------------------------------------------------------------

package jp.go.nict.mcml.serverap.common;

import java.util.ArrayList;

/**
 * FrameData class.
 * 
 * @param <T>
 */
public class FrameData<T> extends ServerApObject {
    // ------------------------------------------
    // private member variables
    // ------------------------------------------
    private ArrayList<T> m_FrameDataList;

    // ------------------------------------------
    // public member functions
    // ------------------------------------------
    /**
     * Default constructor
     */
    public FrameData() {
        m_FrameDataList = new ArrayList<T>();
    }

    /**
     * Gets FrameData.
     * 
     * @return FrameData
     */
    public ArrayList<T> getFrameDataList() {
        return m_FrameDataList;
    }

    /**
     *Sets FrameData list.
     * 
     * @param frameDataList
     */
    public void setFrameDataList(ArrayList<T> frameDataList) {
        m_FrameDataList = frameDataList;
    }

    /**
     * Gets FrameData list head.
     * 
     * @return FrameData list head
     */
    public T getFrameData() {
        return m_FrameDataList.get(0);
    }

    /**
     * Gets value corresponding to index of FrameData list parameter.
     * 
     * @param index
     * @return Value corresponding to index of FrameData list parameter.
     */
    public T getFrameDataAt(int index) {
        return m_FrameDataList.get(index);
    }

    /**
     * Adds data to FrameData list.
     * 
     * @param data
     */
    public void addFrameData(T data) {
        m_FrameDataList.add(data);
    }

    /**
     * Gets FrameData list size.
     * 
     * @return FrameData list size
     */
    public int size() {
        return m_FrameDataList.size();
    }

    /**
     * Clears FrameData list.
     */
    public void clearFrameData() {
        m_FrameDataList.clear();
    }
}
