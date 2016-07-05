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
/**
 * @file Define.h
 * @brief  Constant definition
 */

/**
 * @brief  For audio control
 */
#define AUDIO_RECORDING_UNIT_BYTES  8000            //!< @brief Recording data unit (Byte count)
#define AUDIO_PLAYBACK_UNIT_BYTES   4000            //!< @brief  Played data unit (Byte count)
#define AUDIO_SAMPLING_FREQUENCY    16000.0         //!< @brief  Sampling frequency
#define AUDIO_RECORDING_TIME_LIMIT  0.0             //!< @brief Recording time limit (Seconds) *0=Infinite

/**
 * @brief  Background color of results display area
 */
#define COLOR_RECORDING     [UIColor colorWithRed:1.0 green:0.9 blue:0.9 alpha:1.0] //!< @brief  Currently recording
#define COLOR_RECOGNIZING   [UIColor colorWithRed:1.0 green:0.9 blue:0.7 alpha:1.0] //!< @brief  Currently speech recognition
#define COLOR_TRANSLATING   [UIColor colorWithRed:0.7 green:1.0 blue:0.7 alpha:1.0] //!< @brief  Currently translating
#define COLOR_PLAYING       [UIColor colorWithRed:0.7 green:0.9 blue:1.0 alpha:1.0] //!< @brief  Currently playing
#define COLOR_IDLING        [UIColor whiteColor]                                    //!< @brief  Idling state

/**
 * @brief  Message in results display area
 */
#define MESSAGE_RECORDING               @"Recording..."                             //!< @brief  Currently recording
#define MESSAGE_RECOGNIZING             @"Recognizing..."                           //!< @brief  Currently recording
#define MESSAGE_TRANSLATING             @"Translating..."                           //!< @brief  Currently translating
#define MESSAGE_ERROR_RECIGNITION       @"<<There is no recognition result.>>"      //!< @brief  Speech recognition failed
#define MESSAGE_ERROR_TRANSLATION       @"<<There is no translation result.>>"      //!< @brief  Translation failed
#define MESSAGE_ERROR_BACK_TRANSLATION  @"<<There is no back translation result.>>" //!< @brief  Reverse translation failed

/**
 * @brief MCML
 */

#define MCML_SERVER                 @"http://MyServer/ControlServer/ControlServer"      //!< @brief  Server address. Change to the URL of the server environment created here.
#define MCML_VERSION                @"1.1"                                              //!< @brief  MCML version
#define MCML_SEND_ID                @"iPhoneMCMLClient"                                 //!< @brief  Sender ID
#define MCML_RECEIVE_ID             @"iPhoneMCMLClient"                                 //!< @brief  Receiver ID
#define MCML_GENDER                 @"male"                                             //!< @brief  Gender
#define MCML_AGE                    @"33"                                               //!< @brief  Age
#define MCML_RECOGNITION_SERVICE    @"ASR"                                              //!< @brief  Speech recognition service name
#define MCML_TRANSLATION_SERVICE    @"MT"                                               //!< @brief  Voice translation/reverse translation service name
#define MCML_SYNTHESIS_SERVICE      @"TTS"                                              //!< @brief  Voice synthesis service name
#define MCML_DEVICE                 @"iPhone5"                                          //!< @brief Device
#define MCML_RECORD_AUDIO           @"raw PCM"                                          //!< @brief Recording voice format
#define MCML_RECORD_ENDIAN          @"Little"                                           //!< @brief  (Recording) Byte order
#define MCML_PLAYBACK_AUDIO         @"raw PCM"                                          //!< @brief Recording voice format
#define MCML_PLAYBACK_ENDIAN        @"Big"                                              //!< @brief  (Play)  Byte order
#define MCML_BITRATE                16                                                  //!< @brief (Recording)Voice bit rate
#define MCML_SAMPLING_FREQUENCY     16000                                               //!< @brief (Recording)Voice sampling frequency
#define MCML_LANGUAGE_JAPANESE      "ja"                                                //!< @brief  language type (Japanese)
#define MCML_LANGUAGE_ENGLISH       "en"                                                //!< @brief  language type (English)
#define COMMUNICATION_TIMEOUT       10 * 1000                                           //!< @brief Communication timeout�FMiliseconds
#define APPLOADING_TIMEOUT          6                                                   //!< @brief Timeout at start: Seconds
