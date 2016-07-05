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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Server tag class.
 * 
 * 
 */
public class ServerTag {

    /** request tag */
    public Map<String, String> requestTag;
    /** request InputUserProfile tag list. */
    public List<InputUserProfileTag> requestTagInputUserProfileTagList;
    /** request TargetOutput HypothesisFormat */
    public Map<String, String> requestTagTargetOutputHypothesisFormat;
    /** request TargetOutput LanguageType */
    public Map<String, String> requestTagTargetOutputLanguageType;

    /** ASR */
    /** request Input Data Audio */
    public Map<String, String> requestTagInputDataAudio;
    /** request Input Data Audio ModelType Domain */
    public String requestTagInputDataAudioModelTypeDomain;
    /** request Input Data Audio ModelType Task */
    public String requestTagInputDataAudioModelTypeTask;
    /** request Input Data Audio Signal */
    public Map<String, String> requestTagInputDataAudioSignal;
    /** request Input AttachedBinary */
    public Map<String, String> requestTagInputAttachedBinary;

    /** MT */
    /** request Input Data Text */
    public Map<String, String> requestTagInputDataText;
    /** request Input Data Text */
    public Map<String, String> requestTagInputDataTextModelTypeLanguage;
    /** request Input Data ModelType Domain */
    public String requestTagInputDataTextModelTypeDomain;
    /** request Input Data ModelType Task */
    public String requestTagInputDataTextModelTypeTask;
    /** request Input Data ModelType Personality */
    public Map<String, String> requestTagInputDataTextModelTypePersonality;
    /** request Input Data SentenceSequence */
    public Map<String, String> requestTagInputDataTextSentenceSequence;
    /** request Input Data SentenceSequence Sentence */
    public Map<String, String> requestTagInputDataTextSentenceSequenceSentence;
    /** request Input Data SentenceSequence Sentence Function */
    public String requestTagInputDataTextSentenceSequenceSentenceFunction;
    /** request Input Data SentenceSequence Sentence SurfaceMap */
    public Map<String, String> requestTagInputDataTextSentenceSequenceSentenceSurfaceMap;
    /** request Input Data Text SentenceSequence Sentence Surface */
    public String requestTagInputDataTextSentenceSequenceSentenceSurface;
    /** request Input Data Text SentenceSequence Sentence Chunk tag list */
    public List<ChunkTag> requestTagInputDataTextSentenceSequenceSentenceChunkTagList;

    /** Constructor */
    ServerTag() {
        requestTag = new HashMap<String, String>();
        requestTagInputUserProfileTagList = new ArrayList<InputUserProfileTag>();
        requestTagTargetOutputHypothesisFormat = new HashMap<String, String>();
        requestTagTargetOutputLanguageType = new HashMap<String, String>();

        // ASR
        requestTagInputDataAudio = new HashMap<String, String>();
        requestTagInputDataAudioSignal = new HashMap<String, String>();
        requestTagInputAttachedBinary = new HashMap<String, String>();

        // MT
        requestTagInputDataText = new HashMap<String, String>();
        requestTagInputDataTextModelTypeLanguage = new HashMap<String, String>();
        requestTagInputDataTextModelTypePersonality = new HashMap<String, String>();
        requestTagInputDataTextSentenceSequence = new HashMap<String, String>();
        requestTagInputDataTextSentenceSequenceSentence = new HashMap<String, String>();
        requestTagInputDataTextSentenceSequenceSentenceSurfaceMap = new HashMap<String, String>();
        requestTagInputDataTextSentenceSequenceSentenceChunkTagList = new ArrayList<ChunkTag>();
    }
}
