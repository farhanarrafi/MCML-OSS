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

package jp.go.nict.mcml.engine.socket;

/**
 * Pair class.
 * 
 * @param <F>
 * @param <S>
 */
public class Pair<F, S> {
    // ------------------------------------------
    // private member variables(instance field)
    // ------------------------------------------
    private F first;
    private S second;

    // ------------------------------------------
    // public member functions
    // ------------------------------------------
    /**
     * Default constructor
     */
    public Pair() {
    }

    /**
     * Constructor
     * 
     * @param first
     * @param second
     */
    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Gets First.
     * 
     * @return first
     */
    public F getFirst() {
        return first;
    }

    /**
     * Gets Second.
     * 
     * @return second
     */
    public S getSecond() {
        return second;
    }

    /**
     * Sets First.
     * 
     * @param first
     */
    public void setFirst(F first) {
        this.first = first;
    }

    /**
     * Sets Second.
     * 
     * @param second
     */
    public void setSecond(S second) {
        this.second = second;
    }
}
