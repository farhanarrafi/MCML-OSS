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
 * Voice font attribute class.
 *
 *
 */
public class VoiceFontAttributes {
    // ------------------------------------------
    // public member Enumration(class field)
    // ------------------------------------------
    /**
     * Age type enumeration class.
     *
     *
     */
    public enum Age {
        /** Child */
        Child,
        /** Adult */
        Adult,
        /** Elderly */
        Elder,
        /** None */
        None
    }

    /**
     * Gender type enumeration class.
     *
     *
     */
    public enum Gender {

        /** Male */
        Male,
        /** Female */
        Female,
        /** Unknown */
        Unknown,
        /** None */
        None
    }

    /**
     * Native type enumeration class.
     *
     *
     */
    public enum Native {
        /** Native */
        Native,
        /** Not native */
        NonNative,
        /** None */
        None
    }

    // ------------------------------------------
    // private member variables(instance field)
    // ------------------------------------------
    private Age m_Age;
    private Gender m_Gender;
    private Native m_Native;
    private String m_VoiceFontID;

    // ------------------------------------------
    // public member functions
    // ------------------------------------------
    VoiceFontAttributes() {
        m_Age = Age.None;
        m_Gender = Gender.None;
        m_Native = Native.None;
        m_VoiceFontID = "";
    }

    VoiceFontAttributes(VoiceFontAttributes org) {
        m_Age = org.getAge();
        m_Gender = org.getGender();
        m_Native = org.getNative();
        m_VoiceFontID = org.getVoiceFontID();
    }

    /**
     * Determines if each member of VoiceFontAttributes is set.
     *
     * @return {@code true} conditions
     *         <ul>
     *         <li>Age(None) and</li>
     *         <li>Gender(None) and</li>
     *         <li>Native(None) and</li>
     *         <li>voicefontID(other than {@code null}) and</li>
     *         <li>voicefontID(null)</li>
     *         </ul> {@code false} conditions
     *         <ul>
     *         <li>Other than abvove</li>
     *         </ul>
     */
    public boolean isEmpty() {
        if (m_Age == Age.None && m_Gender == Gender.None
                && m_Native == Native.None && m_VoiceFontID != null
                && m_VoiceFontID.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * equals
     *
     * @param target
     * @return boolean
     */
    public boolean equals(VoiceFontAttributes target) {
        if (target == null) {
            return false;
        }
        if (target.getAge() != m_Age) {
            return false;
        }

        if (target.getGender() != m_Gender) {
            return false;
        }

        if (target.getNative() != m_Native) {
            return false;
        }

        // same attributes
        return true;
    }

    /**
     * Gets Age.
     *
     * @return Age
     */
    public Age getAge() {
        return m_Age;
    }

    /**
     * Sets Age.
     *
     * @param age
     *            Age
     */
    public void setAge(Age age) {
        m_Age = age;
    }

    /**
     * Gets gender.
     *
     * @return Gender
     */
    public Gender getGender() {
        return m_Gender;
    }

    /**
     * Sets Gender.
     *
     * @param gender
     */
    public void setGender(Gender gender) {
        m_Gender = gender;
    }

    /**
     * Gets Native.
     *
     * @return Native
     */
    public Native getNative() {
        return m_Native;
    }

    /**
     * Sets Native.
     *
     * @param native1
     *            Native
     */
    public void setNative(Native native1) {
        m_Native = native1;
    }

    /**
     * Gets Voice font ID.
     *
     * @return VoicefontID
     */
    public String getVoiceFontID() {
        return m_VoiceFontID;
    }

    /**
     * Sets VoicefontID.
     *
     * @param voiceFontID
     *            VoicefontID
     */
    public void setVoiceFontID(String voiceFontID) {
        m_VoiceFontID = voiceFontID;
    }

}
