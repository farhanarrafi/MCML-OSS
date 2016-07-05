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

import java.util.Hashtable;

/**
 * Class for converting language name to ISO639-1 format language code.
 * 
 */
public class ISO639Language {
    Hashtable<String, String> mLanguageHashTable = new Hashtable<String, String>();

    public ISO639Language() {
        mLanguageHashTable.put("japanese", "ja"); // Japanese
        mLanguageHashTable.put("english", "en"); // English
        mLanguageHashTable.put("mandarin", "zh"); // Chinese (Mandarin)
        mLanguageHashTable.put("taiwanese", "zh-taiwan"); // Chinese (Taiwanese Mandarin)
        mLanguageHashTable.put("korean", "ko"); // Korean
        mLanguageHashTable.put("french", "fr"); // French
        mLanguageHashTable.put("german", "de"); // German
        mLanguageHashTable.put("hindi", "hi"); // Hindi
        mLanguageHashTable.put("indonesian", "id"); // Indonesian
        mLanguageHashTable.put("italian", "it"); // Italian
        mLanguageHashTable.put("malay", "ms"); // Malay
        mLanguageHashTable.put("portuguese", "pt"); // Portuguese
        mLanguageHashTable.put("brazilian", "pt_brazil"); // Portuguese (Brazil)
        mLanguageHashTable.put("russian", "ru"); // Russian
        mLanguageHashTable.put("spanish", "es"); // Spanish
        mLanguageHashTable.put("tagalog", "tl"); // Tagalog
        mLanguageHashTable.put("thai", "th"); // Thai
        mLanguageHashTable.put("vietnamese", "vi"); // Vietnamese
        mLanguageHashTable.put("arabic", "ar"); // Arabic
        mLanguageHashTable.put("dutch", "nl"); // Dutch
        mLanguageHashTable.put("danish", "da"); // Danish
    }

    /**
     * Returns language code in ISO639-1 format according to character string.
     * 
     * @param sLanguage
     *            Language name
     * @return ISO639-1 format language code
     */
    public String get(String sLanguage) {
        String sRetVal = null;

        sRetVal = mLanguageHashTable.get(sLanguage.toLowerCase());

        return sRetVal;
    }

}
