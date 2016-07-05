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

import java.nio.ByteOrder;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import jp.go.nict.S2SSample.Event.CompleteEvent;
import jp.go.nict.S2SSample.Event.CompleteEventAdapter;
import jp.go.nict.mcml.signal.SignalAdpcm;
import jp.go.nict.mcml.util.PublicCreateID;
import jp.go.nict.mcml.xml.types.AttachedBinaryType;
import jp.go.nict.mcml.xml.types.AudioType;
import jp.go.nict.mcml.xml.types.ChunkType;
import jp.go.nict.mcml.xml.types.DataType;
import jp.go.nict.mcml.xml.types.DeviceType;
import jp.go.nict.mcml.xml.types.GlobalPositionType;
import jp.go.nict.mcml.xml.types.HypothesisFormatType;
import jp.go.nict.mcml.xml.types.InputModalityType;
import jp.go.nict.mcml.xml.types.InputType;
import jp.go.nict.mcml.xml.types.InputUserProfileType;
import jp.go.nict.mcml.xml.types.LanguageType;
import jp.go.nict.mcml.xml.types.LanguageTypeType;
import jp.go.nict.mcml.xml.types.LocationType;
import jp.go.nict.mcml.xml.types.MCMLType;
import jp.go.nict.mcml.xml.types.ModelTypeType;
import jp.go.nict.mcml.xml.types.PersonalityType;
import jp.go.nict.mcml.xml.types.ReceiverType;
import jp.go.nict.mcml.xml.types.RequestType;
import jp.go.nict.mcml.xml.types.SentenceSequenceType;
import jp.go.nict.mcml.xml.types.SentenceType;
import jp.go.nict.mcml.xml.types.ServerType;
import jp.go.nict.mcml.xml.types.SignalType;
import jp.go.nict.mcml.xml.types.SpeakingType;
import jp.go.nict.mcml.xml.types.SurfaceType;
import jp.go.nict.mcml.xml.types.SurfaceType2;
import jp.go.nict.mcml.xml.types.TargetOutputType;
import jp.go.nict.mcml.xml.types.TextType;
import jp.go.nict.mcml.xml.types.TransmitterType;
import jp.go.nict.mcml.xml.types.UserProfileType;
import jp.go.nict.mcml.xml.types.UserType;
import android.content.Context;
import android.util.Log;

/**
 * Class executing VoiceTranslation. In thread processing, Gets voice data for speech recognition.
 *
 */
public class PublicSpeechTranslator extends Thread {
    /** Maximum number of results acquired */
    private static final int MAX_NBEST = 1;
    /** Loop waiting time (msec) */
    private static final int THREAD_ROOP_WAIT = 100;

    /** Number of data sent per time to speech recognition */
    private static final int SEND_DATA_SIZE = 5;

    /** Main thread sending voice translation request to MCML server. */
    private PublicS2SSendThread mExecuteMainThread = null;
    /** Sub thread sending voice translation request to MCML server.*/
    private PublicS2SSendThread mExecuteSubThread = null;

    /** Arrangement which collectively manages main and sub threads for asynchronous divided transmission */
    private PublicS2SSendThread[] mExecuter = new PublicS2SSendThread[2];
    /** Number of threads sending asynchronous divided data next */
    private int mExecuteThreadNumber = 1; // Sent from sub thread at beginning of data.
    /** Counter for transmission order management in asynchronous divided transmission */
    private int miSRCounter = 0;

    /** Recorder class acquiring voice */
    private VoiceRecorder mVoiceRecorder = null;

    /** Flag which can send voice data */
    private boolean mFlagSendRecordData = false;
    /** Flag currently recording */
    private boolean mFlagRecording = false;

    boolean mFlagLiving = true;

    private static final int SR_SEND_STATE_PRE = 0;
    private static final int SR_SEND_STATE_HEADER_SEND = 1;
    private static final int SR_SEND_STATE_HEADER_RECV = 2;
    private static final int SR_SEND_STATE_DATA_SEND = 3;
    private static final int SR_SEND_STATE_DATA_RECV = 4;
    private static final int SR_SEND_STATE_TAIL_SEND = 5;
    private static final int SR_SEND_STATE_TAIL_RECV = 6;
    private static final int SR_SEND_STATE_ERROR = 999;

    private int mSRSendState = SR_SEND_STATE_PRE;

    /** MCML version information (For creating XML) */
    private static final String VERSION = "1.0";

    /** utteranceID(Dummy) */
    private static final String UTTERANCE_ID = "dummyUtteranceID";

    /** userID(Dummy) */
    private static final String USER_ID = "dummyUserID";

    /**
     * Constructor
     *
     * @param context
     *            Recorder
     */
    public PublicSpeechTranslator(Context context) {
        mVoiceRecorder = new VoiceRecorder();
        mVoiceRecorder.startRecord();

        mExecuteMainThread = new PublicS2SSendThread(context);
        mExecuteSubThread = new PublicS2SSendThread(
                mExecuteMainThread.getClientComCtrl());


        mExecuter[0] = mExecuteMainThread;
        mExecuter[1] = mExecuteSubThread;

        // Adds event listener as need to check results of asynchronous divided head only for main thread.
        addExecuterActionListener(mExecuteMainThread);

        mExecuteMainThread.start();
        mExecuteSubThread.start();
    }

    /**
     * destroyExecute
     */
    public void destroyExecute() {
        mVoiceRecorder.stopRecord();
        mExecuteMainThread.destroyExecute();
        mExecuteSubThread.destroyExecute();
        mFlagLiving = false;
    }

    /**
     * Gets main thread sending voice translation request to MCML server.
     * # Must get the thread instance in order to get the events at completion of request.
     *
     * @return Main thread sending voice translation request.
     */
    public PublicS2SSendThread getMainExecuter() {
        return mExecuteMainThread;
    }

    /**
     * Gets sub thread for sending voice translation request to MCML server.
     * # Must get the thread instance in order to get the events at completion of request.
     *
     * @return Sub thread sending voice translation request.
     */
    public PublicS2SSendThread getSubExecuter() {
        return mExecuteSubThread;
    }

    /**
     * Gets VoiceRecorder.
     *
     * @return mVoiceRecorder
     */
    public VoiceRecorder getVoiceRecorder() {
        return mVoiceRecorder;
    }

    /**
     * Executes speech recognition.
     */
    @Override
    public void run() {
        /** Recorded data */
        byte[] recordData = null;
        /** Block size during recording.*/
        int iBlockSize = mVoiceRecorder.getBlockSize();

        /** Data sent by speech recognition */
        byte[] sendData = null;
        /** Counter of number of data acquiring speech recognition */
        int iDataCount = 0;

        /** ADPCM Converter */
        SignalAdpcm adpcmConverter = new SignalAdpcm(ByteOrder.LITTLE_ENDIAN);

        byte[] temp = null;

        // Sent data buffer creation
        sendData = new byte[mVoiceRecorder.getBlockSize() * SEND_DATA_SIZE];
        //  Resetting of data count
        iDataCount = 0;

        try {
            while (mFlagLiving) {

                if (!mFlagSendRecordData) { //  Before sending data
                    if (mFlagRecording) { // When currently recording flag is set
                    } else {
                        if (mSRSendState == SR_SEND_STATE_HEADER_SEND) {
                            //  Even though divided transmission head has been sent  before sending data,
                            // recording time is short, and the currently recording flag is not set.

                            // If there is no voice data in queue, sends speech recognition termination.
                            if (mVoiceRecorder.isRecordDataEmpty()) {
                                speechRecognitionSendLast();
                            }
                        }
                    }

                    // Insert WAIT in loop.
                    try {
                        Thread.sleep(THREAD_ROOP_WAIT);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }

                if (mFlagSendRecordData) { // Data sendable state.
                    // Gets voice from recorder and sends when amount equivalent to DEND_DATA_SIZE has accumulated.
                    recordData = mVoiceRecorder.getRecordData();
                    if (recordData != null) {
                        System.arraycopy(recordData, 0, sendData, iDataCount
                                * iBlockSize, iBlockSize);
                        iDataCount++;
                        if (iDataCount == SEND_DATA_SIZE) {
                            // Encodes data to ADPCM and Sends it.
                            speechRecognitionSendData(adpcmConverter
                                    .Encode(sendData));

                            sendData = new byte[iBlockSize * SEND_DATA_SIZE];
                            iDataCount = 0;
                        }
                    } else {
                        if (!mFlagRecording) { // End of recording
                            //  When data is null and recording end flag is set, sends remaining data and sends SR_IN termination.
                            if (iDataCount > 0) {
                                temp = sendData;
                                sendData = new byte[iBlockSize * iDataCount];
                                System.arraycopy(temp, 0, sendData, 0,
                                        sendData.length);
                                //
                                speechRecognitionSendData(adpcmConverter
                                        .Encode(sendData, true));
                                sendData = new byte[iBlockSize * SEND_DATA_SIZE];
                                iDataCount = 0;
                            }

                            // Sends speech recognition termination.
                            speechRecognitionSendLast();
                        }

                        try {
                            Thread.sleep(THREAD_ROOP_WAIT);
                        } catch (InterruptedException ie) {
                            ie.printStackTrace();
                        }
                    }
                } else {
                    try {
                        Thread.sleep(THREAD_ROOP_WAIT);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Starts speech recognition. Sends asynchronous divided transmission head.
     *
     * @param sLanguage
     *            Language (Conforms to iso639-1 2 character description )
     * @param sUserID
     *             userID
     * @param sUtteranceID
     *            Speech ID
     * @param voiceFont
     */
    public void speechRecognitionStart(String sLanguage, String sUserID,
            String sUtteranceID, Hashtable<String, Object> voiceFont) {
        mFlagRecording = true;
        // Starts addition to recorded voice queue.
        mVoiceRecorder.startAddQueue();

        SpeechTranslationData request = new SpeechTranslationData();
        try {
            MCMLType mcml = new MCMLType();

            mcml.addVersion(VERSION);
            // System.out.println("ASR execution test");

            // User tag creation
            createUserTag(mcml, getUserTagDataForASR(sUserID, sUtteranceID),
                    ServerTypeEnum.ASR);
            // Server tag creation
            final String gender = "Male";
            final String age = "29";
            createServerTag(mcml,
                    getServerTagDataForASR(sUserID, gender, age, sLanguage),
                    ServerTypeEnum.ASR);

            request.setMCML(mcml);

            // As assigning n1 to nXlast, next is 2.
            miSRCounter = 2;

            // Stops sub thread until there is response to  asynchronous speech recognition head.
            mExecuteSubThread.suspendExecute();

            // Adds request
            mExecuteMainThread.addRequest(request);

            // Transmission state: Head transmission
            mSRSendState = SR_SEND_STATE_HEADER_SEND;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates under  {@code <User>}tag.
     *
     * @param mcml
     * @throws Exception
     */
    private void createUserTag(MCMLType mcml, UserTag userTag,
            ServerTypeEnum serverTypeEnum) throws Exception {
        mcml.addUser(new UserType());
        mcml.getUser().addTransmitter(new TransmitterType());
        mcml.getUser().getTransmitter().addDevice(new DeviceType());
        mcml.getUser().getTransmitter().getDevice()
                .addLocation(new LocationType());
        mcml.getUser().getTransmitter().getDevice().getLocation()
                .addURI(userTag.transMitterDeviceLocationUri);
        GlobalPositionType globalPositionType = new GlobalPositionType();
        globalPositionType
                .addLongitude(userTag.transMitterDeviceLocationGlobalPosition
                        .get("Longitude"));
        globalPositionType
                .addLatitude(userTag.transMitterDeviceLocationGlobalPosition
                        .get("Latitude"));
        mcml.getUser().getTransmitter().getDevice().getLocation()
                .addGlobalPosition(globalPositionType);

        UserProfileType userProfileType = new UserProfileType();
        userProfileType.addID(userTag.transMitterUserProfile.get("ID"));
        if (ServerTypeEnum.MT == serverTypeEnum
                || ServerTypeEnum.TTS == serverTypeEnum) {
            userProfileType.addGender(userTag.transMitterUserProfile
                    .get("Gender"));
            userProfileType.addAge(userTag.transMitterUserProfile.get("Age"));
        }
        mcml.getUser().getTransmitter().addUserProfile(userProfileType);

        // Receiver tag
        final int receiverTagSize = userTag.receiverTagList.size();
        for (int index = 0; index < receiverTagSize; index++) {
            mcml.getUser().addReceiver(new ReceiverType());
            mcml.getUser().getReceiverAt(index).addDevice(new DeviceType());
            mcml.getUser().getReceiverAt(index).getDevice()
                    .addLocation(new LocationType());
            mcml.getUser()
                    .getReceiverAt(index)
                    .getDevice()
                    .getLocation()
                    .addURI(userTag.receiverTagList.get(index).deviceLocationUri);

            UserProfileType userProfileTypeForReceiver = new UserProfileType();
            userProfileTypeForReceiver
                    .addID(userTag.receiverTagList.get(index).userProfile
                            .get("ID"));
            if (ServerTypeEnum.MT == serverTypeEnum
                    || ServerTypeEnum.TTS == serverTypeEnum) {
                userProfileTypeForReceiver.addGender(userTag.receiverTagList
                        .get(index).userProfile.get("Gender"));
                userProfileTypeForReceiver.addAge(userTag.receiverTagList
                        .get(index).userProfile.get("Age"));
            }
            mcml.getUser().getReceiverAt(index)
                    .addUserProfile(userProfileTypeForReceiver);
        }
    }

    /** For ASR */
    private UserTag getUserTagDataForASR(final String userId,
            final String utteranceId) {
        UserTag userTag = new UserTag();
        userTag.transMitterDeviceLocationUri = utteranceId;
        userTag.transMitterDeviceLocationGlobalPosition.put("Longitude",
                "135.754807");
        userTag.transMitterDeviceLocationGlobalPosition.put("Latitude",
                "35.009129");
        userTag.transMitterUserProfile.put("ID", "test07");
        // Receiver tag(1)
        ReceiverTag receiverTag = new ReceiverTag();
        receiverTag.deviceLocationUri = utteranceId;
        receiverTag.userProfile.put("ID", "test07");
        userTag.receiverTagList.add(receiverTag);
        // Receiver tag(2)
        ReceiverTag receiverTag2 = new ReceiverTag();
        receiverTag2.deviceLocationUri = utteranceId;
        receiverTag2.userProfile.put("ID", "nict-dev-111");
        userTag.receiverTagList.add(receiverTag2);
        return userTag;
    }

    /** For MT(Sets ASR results to MT) */
    private UserTag getUserTagDataForMT(final String userId,
            final String utteranceId, final UserTag paramUserTag,
            final Map<String, String> personalityTypeMap) {

        UserTag userTag = getUserTagData(userId, utteranceId, paramUserTag,
                personalityTypeMap);

        final String gender = personalityTypeMap.get("Gender");
        final String age = personalityTypeMap.get("Age");

        // Receiver tag(1)
        ReceiverTag receiverTag = new ReceiverTag();
        receiverTag.deviceLocationUri = utteranceId;
        receiverTag.userProfile.put("ID", userId);
        receiverTag.userProfile.put("Gender", gender);
        receiverTag.userProfile.put("Age", age);
        userTag.receiverTagList.add(receiverTag);
        // Receiver tag(2)
        ReceiverTag receiverTag2 = new ReceiverTag();
        receiverTag2.deviceLocationUri = utteranceId;
        receiverTag2.userProfile.put("ID", userId);
        receiverTag2.userProfile.put("Gender", gender);
        receiverTag2.userProfile.put("Age", age);
        userTag.receiverTagList.add(receiverTag2);
        return userTag;
    }

    /** For TTS */
    private UserTag getUserTagDataForTTS(final String userId,
            final String utteranceId, UserTag paramUserTag,
            Map<String, String> personalityTypeMap) {

        UserTag userTag = getUserTagData(userId, utteranceId, paramUserTag,
                personalityTypeMap);

        final String gender = personalityTypeMap.get("Gender");
        final String age = personalityTypeMap.get("Age");

        // Receiver tag
        ReceiverTag receiverTag = new ReceiverTag();
        receiverTag.deviceLocationUri = utteranceId;
        receiverTag.userProfile.put("ID", userId);
        receiverTag.userProfile.put("Gender", gender);
        receiverTag.userProfile.put("Age", age);
        userTag.receiverTagList.add(receiverTag);
        return userTag;
    }

    /** Gets User tag data. */
    private UserTag getUserTagData(final String userId,
            final String utteranceId, UserTag paramUserTag,
            Map<String, String> personalityTypeMap) {

        UserTag userTag = new UserTag();
        userTag.transMitterDeviceLocationUri = utteranceId;

        Map<String, String> globalPositionMap = paramUserTag.transMitterDeviceLocationGlobalPosition;
        userTag.transMitterDeviceLocationGlobalPosition.put("Longitude",
                globalPositionMap.get("Longitude"));
        userTag.transMitterDeviceLocationGlobalPosition.put("Latitude",
                globalPositionMap.get("Latitude"));

        final String gender = personalityTypeMap.get("Gender");
        final String age = personalityTypeMap.get("Age");

        userTag.transMitterUserProfile.put("ID", userId);
        userTag.transMitterUserProfile.put("Gender", gender);
        userTag.transMitterUserProfile.put("Age", age);

        return userTag;
    }

    /**
     * Generates under {@code <Server>}tag.
     *
     * @param mcml
     * @throws Exception
     */
    private void createServerTag(MCMLType mcml, ServerTag serverTag,
            ServerTypeEnum serverTypeEnum) throws Exception {
        mcml.addServer(new ServerType());
        RequestType requestType = new RequestType();
        requestType.addService(serverTag.requestTag.get("Service"));
        requestType.addProcessOrder(serverTag.requestTag.get("ProcessOrder"));
        mcml.getServer().addRequest(requestType);

        // InputUserProfile
        final int inputUserProfileSize = 3;
        for (int index = 0; index < inputUserProfileSize; index++) {
            final String inputUserProfile = serverTag.requestTagInputUserProfileTagList
                    .get(index).inputUserProfile.get("ID");
            final String gender = serverTag.requestTagInputUserProfileTagList
                    .get(index).inputUserProfile.get("Gender");
            final String age = serverTag.requestTagInputUserProfileTagList
                    .get(index).inputUserProfile.get("Age");
            mcml.getServer()
                    .getRequest()
                    .addInputUserProfile(
                            createInputUserProfileTypeTag(inputUserProfile,
                                    gender, age));
            mcml.getServer().getRequest().getInputUserProfileAt(index)
                    .addInputModality(new InputModalityType());
            mcml.getServer().getRequest().getInputUserProfileAt(index)
                    .getInputModality().addSpeaking(new SpeakingType());

            final String languageId = serverTag.requestTagInputUserProfileTagList
                    .get(index).inputModalitySpeakingLanguage.get("ID");
            final String fluency = serverTag.requestTagInputUserProfileTagList
                    .get(index).inputModalitySpeakingLanguage.get("Fluency");
            mcml.getServer().getRequest().getInputUserProfileAt(index)
                    .getInputModality().getSpeaking()
                    .addLanguage(createLanguageTypeTag(languageId, fluency));
        }

        // TargetOutput
        mcml.getServer().getRequest().addTargetOutput(new TargetOutputType());

        if (ServerTypeEnum.TTS != serverTypeEnum) {
            HypothesisFormatType hypothesisFormatType = new HypothesisFormatType();
            hypothesisFormatType
                    .addNofN_best(serverTag.requestTagTargetOutputHypothesisFormat
                            .get("NofN-best"));
            mcml.getServer().getRequest().getTargetOutput()
                    .addHypothesisFormat(hypothesisFormatType);
        }

        if (ServerTypeEnum.MT == serverTypeEnum
                || ServerTypeEnum.TTS == serverTypeEnum) {
            LanguageTypeType languageTypeType = new LanguageTypeType();
            languageTypeType.addID(serverTag.requestTagTargetOutputLanguageType
                    .get("ID"));
            mcml.getServer().getRequest().getTargetOutput()
                    .addLanguageType(languageTypeType);
        }

        // Input
        mcml.getServer().getRequest().addInput(new InputType());
        mcml.getServer().getRequest().getInput().addData(new DataType());

        // For ASR
        if (ServerTypeEnum.ASR == serverTypeEnum) {
            AudioType audioType = new AudioType();
            audioType.addChannelID(serverTag.requestTagInputDataAudio
                    .get("ChannelID"));
            mcml.getServer().getRequest().getInput().getData()
                    .addAudio(audioType);
            mcml.getServer().getRequest().getInput().getData().getAudio()
                    .addModelType(new ModelTypeType());

            final String domain = serverTag.requestTagInputDataAudioModelTypeDomain;
            mcml.getServer().getRequest().getInput().getData().getAudio()
                    .getModelType().addDomain(domain);

            final String task = serverTag.requestTagInputDataAudioModelTypeTask;
            mcml.getServer().getRequest().getInput().getData().getAudio()
                    .getModelType().addTask(task);

            SignalType signalType = new SignalType();
            signalType.addSamplingRate(serverTag.requestTagInputDataAudioSignal
                    .get("SamplingRate"));
            signalType.addValueType(serverTag.requestTagInputDataAudioSignal
                    .get("ValueType"));
            signalType.addAudioFormat(serverTag.requestTagInputDataAudioSignal
                    .get("AudioFormat"));
            signalType.addBitRate(serverTag.requestTagInputDataAudioSignal
                    .get("BitRate"));
            signalType.addEndian(serverTag.requestTagInputDataAudioSignal
                    .get("Endian"));
            signalType.addChannelQty(serverTag.requestTagInputDataAudioSignal
                    .get("ChannelQty"));
            mcml.getServer().getRequest().getInput().getData().getAudio()
                    .addSignal(signalType);
            AttachedBinaryType attachedBinaryType = new AttachedBinaryType();
            attachedBinaryType
                    .addChannelID(serverTag.requestTagInputAttachedBinary
                            .get("ChannelID"));
            attachedBinaryType
                    .addDataID(serverTag.requestTagInputAttachedBinary
                            .get("DataID"));
            mcml.getServer().getRequest().getInput()
                    .addAttachedBinary(attachedBinaryType);

            // For MT, TTS
        } else if (ServerTypeEnum.MT == serverTypeEnum
                || ServerTypeEnum.TTS == serverTypeEnum) {

            TextType textType = new TextType();
            textType.addChannelID(serverTag.requestTagInputDataText
                    .get("ChannelID"));
            mcml.getServer().getRequest().getInput().getData()
                    .addText(textType);

            // ModelTypetag
            mcml.getServer().getRequest().getInput().getData().getText()
                    .addModelType(new ModelTypeType());
            LanguageType languageType = new LanguageType();
            languageType
                    .addID(serverTag.requestTagInputDataTextModelTypeLanguage
                            .get("ID"));
            mcml.getServer().getRequest().getInput().getData().getText()
                    .getModelType().addLanguage(languageType);

            final String domain = serverTag.requestTagInputDataTextModelTypeDomain;
            mcml.getServer().getRequest().getInput().getData().getText()
                    .getModelType().addDomain(domain);

            final String task = serverTag.requestTagInputDataTextModelTypeTask;
            mcml.getServer().getRequest().getInput().getData().getText()
                    .getModelType().addTask(task);

            PersonalityType personalityType = new PersonalityType();
            personalityType
                    .addID(serverTag.requestTagInputDataTextModelTypePersonality
                            .get("ID"));
            personalityType
                    .addGender(serverTag.requestTagInputDataTextModelTypePersonality
                            .get("Gender"));
            personalityType
                    .addAge(serverTag.requestTagInputDataTextModelTypePersonality
                            .get("Age"));
            mcml.getServer().getRequest().getInput().getData().getText()
                    .getModelType().addPersonality(personalityType);

            // SentenceSequencetag
            SentenceSequenceType sentenceSequenceType = new SentenceSequenceType();
            sentenceSequenceType
                    .addOrder(serverTag.requestTagInputDataTextSentenceSequence
                            .get("Order"));
            if (ServerTypeEnum.TTS == serverTypeEnum) {
                sentenceSequenceType
                        .addScore(serverTag.requestTagInputDataTextSentenceSequence
                                .get("Score"));
            }
            sentenceSequenceType
                    .addN_bestRank(serverTag.requestTagInputDataTextSentenceSequence
                            .get("N-bestRank"));
            mcml.getServer().getRequest().getInput().getData().getText()
                    .addSentenceSequence(sentenceSequenceType);

            SentenceType sentenceType = new SentenceType();
            sentenceType
                    .addOrder(serverTag.requestTagInputDataTextSentenceSequenceSentence
                            .get("Order"));
            mcml.getServer().getRequest().getInput().getData().getText()
                    .getSentenceSequence().addSentence(sentenceType);

            final String function = serverTag.requestTagInputDataTextSentenceSequenceSentenceFunction;
            mcml.getServer().getRequest().getInput().getData().getText()
                    .getSentenceSequence().getSentence().addFunction(function);

            SurfaceType2 surfaceType2 = new SurfaceType2();
            surfaceType2
                    .addDelimiter(serverTag.requestTagInputDataTextSentenceSequenceSentenceSurfaceMap
                            .get("Delimiter"));
            final String surfaceText = serverTag.requestTagInputDataTextSentenceSequenceSentenceSurface;
            if (null != surfaceText) {
                surfaceType2.setValue(surfaceText);
            }
            mcml.getServer().getRequest().getInput().getData().getText()
                    .getSentenceSequence().getSentence()
                    .addSurface(surfaceType2);

            final int chunkListSize = serverTag.requestTagInputDataTextSentenceSequenceSentenceChunkTagList
                    .size();

            for (int index = 0; index < chunkListSize; index++) {
                ChunkType chunkType = new ChunkType();

                final String order = serverTag.requestTagInputDataTextSentenceSequenceSentenceChunkTagList
                        .get(index).chunk.get("Order");
                chunkType.addOrder(order);

                SurfaceType surfaceType = new SurfaceType();
                final String surface = serverTag.requestTagInputDataTextSentenceSequenceSentenceChunkTagList
                        .get(index).surface;
                surfaceType.setValue(surface);

                chunkType.addSurface(surfaceType);

                mcml.getServer().getRequest().getInput().getData().getText()
                        .getSentenceSequence().getSentence()
                        .addChunk(chunkType);
            }
        }
    }

    /** server ASR */
    private ServerTag getServerTagDataForASR(final String userId,
            final String gender, final String age, final String sLanguage) {
        ServerTag serverTag = new ServerTag();
        serverTag.requestTag.put("Service", "ASR");
        serverTag.requestTag.put("ProcessOrder", "101");

        // InputUserProfile
        InputUserProfileTag inputUserProfileTag = new InputUserProfileTag();
        inputUserProfileTag.inputUserProfile.put("ID", userId);
        inputUserProfileTag.inputUserProfile.put("Gender", gender);
        inputUserProfileTag.inputUserProfile.put("Age", age);
        inputUserProfileTag.inputModalitySpeakingLanguage.put("ID", "en");
        inputUserProfileTag.inputModalitySpeakingLanguage.put("Fluency", "5");

        InputUserProfileTag inputUserProfileTag2 = new InputUserProfileTag();
        inputUserProfileTag2.inputUserProfile.put("ID", userId);
        inputUserProfileTag2.inputUserProfile.put("Gender", gender);
        inputUserProfileTag2.inputUserProfile.put("Age", age);
        inputUserProfileTag2.inputModalitySpeakingLanguage.put("ID", "ko");
        inputUserProfileTag2.inputModalitySpeakingLanguage.put("Fluency", "4");

        InputUserProfileTag inputUserProfileTag3 = new InputUserProfileTag();
        inputUserProfileTag3.inputUserProfile.put("ID", userId);
        inputUserProfileTag3.inputUserProfile.put("Gender", gender);
        inputUserProfileTag3.inputUserProfile.put("Age", age);
        inputUserProfileTag3.inputModalitySpeakingLanguage.put("ID", "zh");
        inputUserProfileTag3.inputModalitySpeakingLanguage.put("Fluency", "3");

        serverTag.requestTagInputUserProfileTagList.add(inputUserProfileTag);
        serverTag.requestTagInputUserProfileTagList.add(inputUserProfileTag2);
        serverTag.requestTagInputUserProfileTagList.add(inputUserProfileTag3);

        // TargetOutput
        serverTag.requestTagTargetOutputHypothesisFormat.put("NofN-best", "5");

        // Input
        serverTag.requestTagInputDataAudio.put("ChannelID", "1");
        serverTag.requestTagInputDataAudioModelTypeDomain = "Travel";
        serverTag.requestTagInputDataAudioModelTypeTask = "Dictation";
        serverTag.requestTagInputDataAudioSignal.put("SamplingRate", "16000");
        serverTag.requestTagInputDataAudioSignal.put("ValueType", "integer");
        // serverTag.requestTagInputDataAudioSignal.put("AudioFormat",
        // "raw PCM");
        serverTag.requestTagInputDataAudioSignal.put("AudioFormat", "ADPCM");
        serverTag.requestTagInputDataAudioSignal.put("BitRate", "16");
        serverTag.requestTagInputDataAudioSignal.put("Endian", "Little");
        serverTag.requestTagInputDataAudioSignal.put("ChannelQty", "0");

        serverTag.requestTagInputAttachedBinary.put("ChannelID", "1");
        serverTag.requestTagInputAttachedBinary.put("DataID", sLanguage);

        return serverTag;
    }

    /** server MT */
    private ServerTag getServerTagDataForMT(final String userId,
            final String languageId, final String srcLanguageId,
            ServerTag paramServerTag, Map<String, String> personalityTypeMap,
            List<String> surfaceList) {
        ServerTag serverTag = new ServerTag();
        serverTag.requestTag.put("Service", "MT");
        serverTag.requestTag.put("ProcessOrder",
                paramServerTag.requestTag.get("ProcessOrder"));

        final String gender = personalityTypeMap.get("Gender");
        final String age = personalityTypeMap.get("Age");

        // InputUserProfile
        InputUserProfileTag inputUserProfileTag = new InputUserProfileTag();
        inputUserProfileTag.inputUserProfile.put("ID", userId);
        inputUserProfileTag.inputUserProfile.put("Gender", gender);
        inputUserProfileTag.inputUserProfile.put("Age", age);
        inputUserProfileTag.inputModalitySpeakingLanguage.put("ID", "zh");
        inputUserProfileTag.inputModalitySpeakingLanguage.put("Fluency", "5");

        InputUserProfileTag inputUserProfileTag2 = new InputUserProfileTag();
        inputUserProfileTag2.inputUserProfile.put("ID", userId);
        inputUserProfileTag2.inputUserProfile.put("Gender", gender);
        inputUserProfileTag2.inputUserProfile.put("Age", age);
        inputUserProfileTag2.inputModalitySpeakingLanguage.put("ID", "ko");
        inputUserProfileTag2.inputModalitySpeakingLanguage.put("Fluency", "4");

        InputUserProfileTag inputUserProfileTag3 = new InputUserProfileTag();
        inputUserProfileTag3.inputUserProfile.put("ID", userId);
        inputUserProfileTag3.inputUserProfile.put("Gender", gender);
        inputUserProfileTag3.inputUserProfile.put("Age", age);
        inputUserProfileTag3.inputModalitySpeakingLanguage.put("ID", "vi");
        inputUserProfileTag3.inputModalitySpeakingLanguage.put("Fluency", "3");

        serverTag.requestTagInputUserProfileTagList.add(inputUserProfileTag);
        serverTag.requestTagInputUserProfileTagList.add(inputUserProfileTag2);
        serverTag.requestTagInputUserProfileTagList.add(inputUserProfileTag3);

        // TargetOutput
        serverTag.requestTagTargetOutputHypothesisFormat.put("NofN-best",
                String.valueOf(MAX_NBEST));
        serverTag.requestTagTargetOutputLanguageType.put("ID", languageId);

        // Input
        serverTag.requestTagInputDataText.put("ChannelID",
                paramServerTag.requestTagInputDataText.get("ChannelID"));

        serverTag.requestTagInputDataTextModelTypeLanguage.put("ID",
                srcLanguageId);
        serverTag.requestTagInputDataTextModelTypeDomain = paramServerTag.requestTagInputDataTextModelTypeDomain;
        serverTag.requestTagInputDataTextModelTypeTask = paramServerTag.requestTagInputDataTextModelTypeTask;
        serverTag.requestTagInputDataTextModelTypePersonality.put("ID",
                "EF050A,12.345");
        serverTag.requestTagInputDataTextModelTypePersonality.put("Gender",
                gender);
        serverTag.requestTagInputDataTextModelTypePersonality.put("Age", age);

        serverTag.requestTagInputDataTextSentenceSequence.put("Order",
                paramServerTag.requestTagInputDataTextSentenceSequence
                        .get("Order"));
        serverTag.requestTagInputDataTextSentenceSequence.put("N-bestRank",
                paramServerTag.requestTagInputDataTextSentenceSequence
                        .get("N-bestRank"));
        serverTag.requestTagInputDataTextSentenceSequenceSentence.put("Order",
                paramServerTag.requestTagInputDataTextSentenceSequenceSentence
                        .get("Order"));

        serverTag.requestTagInputDataTextSentenceSequenceSentenceFunction = paramServerTag.requestTagInputDataTextSentenceSequenceSentenceFunction;
        serverTag.requestTagInputDataTextSentenceSequenceSentenceSurface = paramServerTag.requestTagInputDataTextSentenceSequenceSentenceSurface;
        serverTag.requestTagInputDataTextSentenceSequenceSentenceSurfaceMap
                .put("Delimiter", "|");

        setSerface(surfaceList, serverTag);

        return serverTag;
    }

    /** server TTS */
    private ServerTag getServerTagDataForTTS(final String userId,
            final String targetLanguage, ServerTag paramServerTag,
            Map<String, String> personalityTypeMap, List<String> surfaceList) {
        ServerTag serverTag = new ServerTag();
        serverTag.requestTag.put("Service", "TTS");
        serverTag.requestTag.put("ProcessOrder",
                paramServerTag.requestTag.get("ProcessOrder"));

        final String gender = personalityTypeMap.get("Gender");
        final String age = personalityTypeMap.get("Age");

        // InputUserProfile
        InputUserProfileTag inputUserProfileTag = new InputUserProfileTag();
        inputUserProfileTag.inputUserProfile.put("ID", "owner");
        inputUserProfileTag.inputUserProfile.put("Gender", gender);
        inputUserProfileTag.inputUserProfile.put("Age", age);
        inputUserProfileTag.inputModalitySpeakingLanguage.put("ID", "en");
        inputUserProfileTag.inputModalitySpeakingLanguage.put("Fluency", "5");

        InputUserProfileTag inputUserProfileTag2 = new InputUserProfileTag();
        inputUserProfileTag2.inputUserProfile.put("ID", "owner");
        inputUserProfileTag2.inputUserProfile.put("Gender", gender);
        inputUserProfileTag2.inputUserProfile.put("Age", age);
        inputUserProfileTag2.inputModalitySpeakingLanguage.put("ID", "ko");
        inputUserProfileTag2.inputModalitySpeakingLanguage.put("Fluency", "4");

        InputUserProfileTag inputUserProfileTag3 = new InputUserProfileTag();
        inputUserProfileTag3.inputUserProfile.put("ID", "owner");
        inputUserProfileTag3.inputUserProfile.put("Gender", gender);
        inputUserProfileTag3.inputUserProfile.put("Age", age);
        inputUserProfileTag3.inputModalitySpeakingLanguage.put("ID", "zh");
        inputUserProfileTag3.inputModalitySpeakingLanguage.put("Fluency", "3");

        serverTag.requestTagInputUserProfileTagList.add(inputUserProfileTag);
        serverTag.requestTagInputUserProfileTagList.add(inputUserProfileTag2);
        serverTag.requestTagInputUserProfileTagList.add(inputUserProfileTag3);

        // TargetOutput
        serverTag.requestTagTargetOutputLanguageType.put("ID", targetLanguage);

        // Input
        serverTag.requestTagInputDataText.put("ChannelID",
                paramServerTag.requestTagInputDataText.get("ChannelID"));

        serverTag.requestTagInputDataTextModelTypeLanguage.put("ID",
                targetLanguage);

        serverTag.requestTagInputDataTextModelTypeDomain = paramServerTag.requestTagInputDataTextModelTypeDomain;
        serverTag.requestTagInputDataTextModelTypeTask = paramServerTag.requestTagInputDataTextModelTypeTask;
        serverTag.requestTagInputDataTextModelTypePersonality.put("ID",
                "EF050A,12.345");
        serverTag.requestTagInputDataTextModelTypePersonality.put("Gender",
                gender);
        serverTag.requestTagInputDataTextModelTypePersonality.put("Age", age);

        serverTag.requestTagInputDataTextSentenceSequence.put("Order",
                paramServerTag.requestTagInputDataTextSentenceSequence
                        .get("Order"));
        serverTag.requestTagInputDataTextSentenceSequence.put("Score",
                paramServerTag.requestTagInputDataTextSentenceSequence
                        .get("Score"));
        serverTag.requestTagInputDataTextSentenceSequence.put("N-bestRank",
                paramServerTag.requestTagInputDataTextSentenceSequence
                        .get("N-bestRank"));
        serverTag.requestTagInputDataTextSentenceSequenceSentence.put("Order",
                paramServerTag.requestTagInputDataTextSentenceSequenceSentence
                        .get("Order"));

        serverTag.requestTagInputDataTextSentenceSequenceSentenceFunction = "text";
        serverTag.requestTagInputDataTextSentenceSequenceSentenceSurfaceMap
                .put("Delimiter", "|");

        serverTag.requestTagInputDataTextSentenceSequenceSentenceSurface = paramServerTag.requestTagInputDataTextSentenceSequenceSentenceSurface;
        setSerface(surfaceList, serverTag);

        return serverTag;
    }

    private void setSerface(final List<String> surfaceList, ServerTag serverTag) {

        final int surfaceListSize = surfaceList.size();
        int index = 0;
        while (index < surfaceListSize) {
            String surface = surfaceList.get(index);
            if (null == surface || 0 == surface.length()) {
                index++;
                continue;
            }
            ChunkTag chunkTag = new ChunkTag();
            index++; // Indexing starts at 1, so increment in advance
            chunkTag.chunk.put("Order", String.valueOf(index));
            chunkTag.surface = surface;
            serverTag.requestTagInputDataTextSentenceSequenceSentenceChunkTagList
                    .add(chunkTag);
        }
    }

    /**
     * Creates InputUserProfile type tag.
     *
     * @return
     * @throws Exception
     */
    private InputUserProfileType createInputUserProfileTypeTag(final String id,
            final String gender, final String age) throws Exception {
        InputUserProfileType inputUserProfileType = new InputUserProfileType();
        inputUserProfileType.addID(id);
        inputUserProfileType.addGender(gender);
        inputUserProfileType.addAge(age);
        return inputUserProfileType;
    }

    /**
     * Creates Language type tag.
     *
     * @param id
     * @param fluency
     * @return
     * @throws Exception
     */
    private LanguageType createLanguageTypeTag(final String id,
            final String fluency) throws Exception {
        LanguageType languageType = new LanguageType();
        languageType.addID(id);
        languageType.addFluency(fluency);
        return languageType;
    }

    /**
     * Starts speech recognition
     *
     * @param sLanguage
     *            Language as iso639-1
     * @param sUserID
     *            User ID
     * @param sUtteranceID
     *            Speech ID
     */
    public void speechRecognitionStart(String sLanguage, String sUserID,
            String sUtteranceID) {
        speechRecognitionStart(sLanguage, sUserID, sUtteranceID, null);
    }


    /**
     * Ends speech recognition.
     *
     * @return True when ended normally. False means that error occurred halfway through and data has not been sent.
     */
    public boolean speechRecognitionEnd() {
        mVoiceRecorder.stopAddQueue();
        mFlagRecording = false;
        if (mSRSendState == SR_SEND_STATE_ERROR) {
            // As an error occurred in data sent earlier, transmission is no longer being carried out.
            // To update screen, returns false as recognition failure.
            return false;
        }

        return true;
    }

    /**
     * Speech recognition Sends asynchronous divided transmission data.
     *
     * @param binary
     *            Voice data(ADPCM)
     */
    private void speechRecognitionSendData(byte[] binary) {
        SpeechTranslationData request = new SpeechTranslationData();
        try {
            if (miSRCounter > 1) { // Data may be sent after sending termination depending on timing.
                request.setBinaryData(binary);
                request.setSR_IN();
                request.setOptionURL("n" + miSRCounter);
                miSRCounter++;
                mExecuter[mExecuteThreadNumber].addRequest(request);
                mExecuteThreadNumber++;
                if (mExecuteThreadNumber == mExecuter.length) {
                    mExecuteThreadNumber = 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends speech recognition last data.
     */
    private void speechRecognitionSendLast() {
        SpeechTranslationData request = new SpeechTranslationData();
        try {
            if (miSRCounter > 1) { // As two calls may be made depending on timing, don't allow calling after sending head.
                request.setSR_IN();
                request.setOptionURL("n" + miSRCounter + "last");
                miSRCounter = 0;

                // Sendable flag OFF
                mFlagSendRecordData = false;

                mExecuteMainThread.addRequest(request);

                // Transmission state�FTermination transmission
                mSRSendState = SR_SEND_STATE_TAIL_SEND;
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * Executes translation.
     *
     * @param sentences
     *            Multiple specification by sentence type translation  source text arrangement
     * @param sSourceLanguage
     *            Translation source language (Conforms  to iso639-1 2 character description.)
     * @param sTargetLanguage
     *            Translation target language (Conforms  to iso639-1 2 character description.)
     * @param sUserID
     *             User ID
     * @param sUtteranceID
     *            Speech ID
     * @param iTranslationMode
     */
    public void translate(SentenceType[] sentences, String sSourceLanguage,
            String sTargetLanguage, String sUserID, String sUtteranceID,
            int iTranslationMode, UserTag userTag, ServerTag serverTag,
            Map<String, String> personalityTypeMap, List<String> surfaceList) {
        SpeechTranslationData request = new SpeechTranslationData();
        try {
            MCMLType mcml = new MCMLType();

            mcml.addVersion(VERSION);

            // System.out.println("MT execution");
            createUserTag(
                    mcml,
                    getUserTagDataForMT(sUserID, sUtteranceID, userTag,
                            personalityTypeMap), ServerTypeEnum.MT);
            createServerTag(
                    mcml,
                    getServerTagDataForMT(sUserID, sTargetLanguage,
                            sSourceLanguage, serverTag, personalityTypeMap,
                            surfaceList), ServerTypeEnum.MT);

            request.setMCML(mcml);

            // Sets reverse translation marking.
            if (iTranslationMode == PublicCreateID.REQUEST_TYPE_REVERSE_TRANSLATION) {
                request.setBackTranslation();
            }

            mExecuteMainThread.addRequest(request);

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * Executes voice synthesis.
     *
     * @param sentences
     *            Multiple specification by sentence type voice synthesis  source text arrangement
     * @param sLanguage
     *            Synthesized language
     * @param sUserID
     *             User ID
     * @param sUtteranceID
     * @param voiceFont
     *
     */
    public void speechSynthesis(SentenceType[] sentences, String sLanguage,
            String sUserID, String sUtteranceID,
            Hashtable<String, Object> voiceFont, UserTag userTag,
            ServerTag serverTag, Map<String, String> personalityType,
            List<String> surfaceList) {
        SpeechTranslationData request = new SpeechTranslationData();

        try {
            MCMLType mcml = new MCMLType();
            mcml.addVersion(VERSION);

            // System.out.println ("TTS  execution");
            createUserTag(
                    mcml,
                    getUserTagDataForTTS(sUserID, sUtteranceID, userTag,
                            personalityType), ServerTypeEnum.TTS);
            createServerTag(
                    mcml,
                    getServerTagDataForTTS(sUserID, sLanguage, serverTag,
                            personalityType, surfaceList), ServerTypeEnum.TTS);

            request.setMCML(mcml);
            mExecuteMainThread.addRequest(request);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *Adds completed event listener to request.
     *
     * @param executer
     */
    protected void addExecuterActionListener(PublicS2SSendThread executer) {
        executer.addActionListener(new CompleteEventAdapter() {
            @Override
            public void requestTaskComplete(CompleteEvent event) {
                try {
                    requestTaskCompleteEvent(event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Receives response to asynchronous divided transmission head request, and resumes sub-thread for asynchronous transmission.
     *
     * @param completeEvent
     * @throws Exception
     */
    protected void requestTaskCompleteEvent(CompleteEvent completeEvent)
            throws Exception {
        SpeechTranslationData result = completeEvent.getContainerData();

        if (result.isSRDivFirstResponse()) {
            if (result.isError()) {
                // Error towards recognition head
                mSRSendState = SR_SEND_STATE_ERROR;
                mFlagSendRecordData = false;
                mFlagRecording = false;
                mExecuteMainThread.clearRequest();
                mExecuteSubThread.clearRequest();

                if (result.isExistMCML()) {
                    Log.i("S2SSample", result.generate());
                }
            } else {
                // Data sendable flag ON
                mFlagSendRecordData = true;
                mExecuteSubThread.resumeExecute();

                // Transmission state�FHead reception
                mSRSendState = SR_SEND_STATE_HEADER_RECV;
            }
        } else if (result.isError()) {
            if (result.isSR_IN()) {
                if (result.isExistMCML()) {
                    // Error towards recognition termination
                    // Data sendable flag OFF
                    mFlagSendRecordData = false;
                    mSRSendState = SR_SEND_STATE_PRE;
                } else {
                    // Error during data transmission halfway through
                    mSRSendState = SR_SEND_STATE_ERROR;
                    mFlagSendRecordData = false;
                    mFlagRecording = false;
                    mExecuteMainThread.clearRequest();
                    mExecuteSubThread.clearRequest();

                }
            }
        } else {
            if (result.isSR_OUT()) {
                mSRSendState = SR_SEND_STATE_PRE;
            }
        }
    }

    /**
     * Executes translation.
     *
     * @param sentences
     *           Sentence type translation  source text
     * @param sSourceLanguage
     *            Translation source language (Conforms to iso639-1 2 character description.)
     * @param sTargetLanguage
     *            Translation target language (Conforms to iso639-1 2 character description.)
     * @param sUtteranceID
     *            Speech ID
     * @param iTranslationMode
     *
     */
    public void translate(SentenceType[] sentences, String sSourceLanguage,
            String sTargetLanguage, String sUtteranceID, int iTranslationMode,
            UserTag userTag, ServerTag serverTag,
            Map<String, String> personalityTypeMap, List<String> surfaceList) {

        translate(sentences, sSourceLanguage, sTargetLanguage, USER_ID,
                sUtteranceID, iTranslationMode, userTag, serverTag,
                personalityTypeMap, surfaceList);

    }

    /**
     * Executes translation.
     *
     * @param sText
     *            Translation source text
     * @param sSourceLanguage
     *            Translation source language (Conforms  to iso639-1 2 character description.)
     * @param sTargetLanguage
     *                 Translation target language (Conforms  to iso639-1 2 character description.)
     * @param sUtteranceID
     *            Speech ID
     * @param iTranslationMode
     */
    public void translate(String sText, String sSourceLanguage,
            String sTargetLanguage, String sUtteranceID, int iTranslationMode,
            UserTag userTag, ServerTag serverTag,
            Map<String, String> personalityTypeMap, List<String> surfaceList) {
        SentenceType[] sentences = new SentenceType[1];
        sentences[0] = new SentenceType();
        sentences[0].setValue(sText);

        translate(sentences, sSourceLanguage, sTargetLanguage, sUtteranceID,
                iTranslationMode, userTag, serverTag, personalityTypeMap,
                surfaceList);
    }

    /**
     * Executes voice synthesis.
     *
     * @param sentence
     *           Sentence type voice synthesis source text
     * @param sLanguage
     *            Synthesized language
     * @param sUtteranceID
     *            Speech ID
     * @param voiceFont
     *            VoiceFont
     */
    public void speechSynthesis(SentenceType sentence, String sLanguage,
            String sUtteranceID, Hashtable<String, Object> voiceFont,
            UserTag userTag, ServerTag serverTag,
            Map<String, String> personalityType, List<String> surfaceList) {
        SentenceType[] sentences = new SentenceType[1];
        sentences[0] = sentence;

        speechSynthesis(sentences, sLanguage, USER_ID, sUtteranceID, voiceFont,
                userTag, serverTag, personalityType, surfaceList);

    }

    /**
     * Speech Synthesis
     *
     * @param sText
     *            Text
     * @param sLanguage
     *            Language
     * @param sUtteranceID
     *            Speech ID
     * @param voiceFont
     *            VoiceFont
     */
    public void speechSynthesis(String sText, String sLanguage,
            String sUtteranceID, Hashtable<String, Object> voiceFont,
            UserTag userTag, ServerTag serverTag,
            Map<String, String> personalityType, List<String> surfaceList) {
        SentenceType sentence = new SentenceType();
        sentence.setValue(sText);

        speechSynthesis(sentence, sLanguage, sUtteranceID, voiceFont, userTag,
                serverTag, personalityType, surfaceList);
    }

    /**
     * Executes voice synthesis.
     *
     * @param sentence
     *            Voice synthesis  source text
     * @param sLanguage
     *            Synthesized language
     * @param sUtteranceID
     *            Speech ID
     */
    public void speechSynthesis(SentenceType sentence, String sLanguage,
            String sUtteranceID, UserTag userTag, ServerTag serverTag,
            Map<String, String> personalityType, List<String> surfaceList) {
        speechSynthesis(sentence, sLanguage, sUtteranceID, null, userTag,
                serverTag, personalityType, surfaceList);

    }

    /**
     * Executes voice synthesis.
     *
     * @param sText
     *            Voice synthesis  source text
     * @param sLanguage
     *            Synthesized language
     * @param sUtteranceID
     *            Speech ID
     */
    public void speechSynthesis(String sText, String sLanguage,
            String sUtteranceID, UserTag userTag, ServerTag serverTag,
            Map<String, String> personalityType, List<String> surfaceList) {
        speechSynthesis(sText, sLanguage, sUtteranceID, null, userTag,
                serverTag, personalityType, surfaceList);
    }

    /**
     * Starts speech recognition. Sends asynchronous divided transmission head.
     *
     * @param sLanguage
     * @param voice
     * @return UtteranceID
     */
    public String speechRecognitionStart(String sLanguage,
            Hashtable<String, Object> voice) {

        speechRecognitionStart(sLanguage, USER_ID, UTTERANCE_ID, voice);

        return UTTERANCE_ID;
    }

    /**
     * Starts speech recognition
     *
     * @param sLanguage
     * @return UtteranceID
     */
    public String speechRecognitionStart(String sLanguage) {
        return speechRecognitionStart(sLanguage, null);
    }

}
