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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import jp.go.nict.S2SSample.Event.CompleteEvent;
import jp.go.nict.S2SSample.Event.CompleteEventAdapter;
import jp.go.nict.S2SSample.Event.VoiceRecorderEvent;
import jp.go.nict.S2SSample.Event.VoiceRecorderEventAdapter;
import jp.go.nict.common.file.Endian.EndianConverter;
import jp.go.nict.mcml.signal.SignalAdpcm;
import jp.go.nict.mcml.util.PublicCreateID;
import jp.go.nict.mcml.xml.types.GlobalPositionType;
import jp.go.nict.mcml.xml.types.PersonalityType;
import jp.go.nict.mcml.xml.types.SentenceSequenceType;
import jp.go.nict.mcml.xml.types.SentenceType;
import jp.go.nict.mcml.xml.types.ServerType;
import jp.go.nict.mcml.xml.types.TextType;
import jp.go.nict.mcml.xml.types.UserType;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Speech translation sample application
 * 
 */
public class PublicS2SSample extends Activity {
    /** Screen color combination */
    int color_normal = Color.BLACK;
    int color_recording = Color.rgb(255, 210, 210);
    int color_recognizeing = Color.rgb(255, 190, 150);
    int color_translating = Color.rgb(210, 255, 150);
    int color_synthesising = Color.rgb(190, 230, 255);
    int color_soundplay = Color.rgb(150, 190, 255);

    /** FONT color combination */
    int color_font_message = Color.BLACK;
    int color_font_normal = Color.WHITE;

    static final int STATE_PRE_SR = 0; // Before speech recognition processing
    static final int STATE_START_SR = 1; // Currently recognizing speech
    static final int STATE_START_MT = 2; // Currently translating
    static final int STATE_START_BMT_SS = 3; // Currently implementing back translation and speech synthesis
    static final int STATE_START_BMTCOMP_SS = 4; // Completed back translation and currently implementing speech synthesis
    static final int STATE_START_SSCOMP_BMT = 5; // Completed speech synthesis and currently implementing back translation
    static final int STATE_START_BMTERR_SS = 10; // Failed in back translation and currently implementing speech synthesis
    static final int STATE_START_SSERR_BMT = 11; // Failed in speech synthesis and currently implementing back translation

    int miS2SState = STATE_PRE_SR;

    /** Loop wait time (msec) */
    private static final int THREAD_ROOP_WAIT = 100;

    /** Screen item */
    Spinner mSpinnerUpper = null;
    Spinner mSpinnerLower = null;
    TextView mTextViewUpper = null;
    TextView mTextViewLower = null;

    /** Level meter display control */
    LevelMeterControl mLevelMeterControl = null;

    /** Screen item listener  */
    View.OnTouchListener mTextViewOnTouchListener = null;
    AdapterView.OnItemSelectedListener mSpinnerOnItemSelectedListener = null;

    /** Screen display handler */
    Handler mHandler = new Handler();

    /** Language code conversion */
    ISO639Language mISO639Language = new ISO639Language();

    /** Instance for executing speech translation */
    PublicSpeechTranslator mSpeechTranslator = null;

    /** Voice player  */
    SoundPlayer mSoundPlayer = new SoundPlayer();

    /** ADPCM dividing decode size */
    int adpcmDivideDecodeSize = 6000;

    /** ADPCM Converter */
    SignalAdpcm adpcmConverter = new SignalAdpcm(ByteOrder.LITTLE_ENDIAN);

    /** true when current is top TextView */
    boolean mFlagCurrentUpper = true;

    /** Input language */
    String msSourceLanguage = null;

    /** Translation language */
    String msTargetLanguage = null;

    /** VoiceFont table(ID, Age, Gender, FORMAT are key) */
    Hashtable<String, Hashtable<String, Object>> mVoiceFontTable = new Hashtable<String, Hashtable<String, Object>>();

    /**
     * initIDCreateor
     */
    public void initIDCreateor() {

        mSpeechTranslator = new PublicSpeechTranslator(this);
    }

    /**
     * Event listener  initialization
     */
    private void initListener() {
        // TextView listener
        mTextViewOnTouchListener = new View.OnTouchListener() {

            // Gets spinner instance.
            Spinner spinnerUpper = (Spinner) findViewById(R.id.spinnerUpper);
            Spinner spinnerLower = (Spinner) findViewById(R.id.spinnerLower);

            boolean flagActionDown = false;

            Resources res = getResources();

            @Override
            public boolean onTouch(View parent, MotionEvent event) {
                TextView textview = (TextView) parent;
                mFlagCurrentUpper = (parent.getId() == R.id.textViewUpper);
                boolean flagSRError = false;
                SpeechTranslationData errorResult = new SpeechTranslationData();
                errorResult.setError();

                if (event.getAction() == MotionEvent.ACTION_DOWN) { // Action when TextView is pressed.
                    // Starts recording and speech recognition.
                    flagActionDown = true;
                    if (mFlagCurrentUpper) {
                        msSourceLanguage = mISO639Language.get(spinnerUpper
                                .getSelectedItem().toString());
                        msTargetLanguage = mISO639Language.get(spinnerLower
                                .getSelectedItem().toString());
                    } else {
                        msSourceLanguage = mISO639Language.get(spinnerLower
                                .getSelectedItem().toString());
                        msTargetLanguage = mISO639Language.get(spinnerUpper
                                .getSelectedItem().toString());
                    }
                    mSpeechTranslator.speechRecognitionStart(msSourceLanguage);
                    textview.setTextColor(color_font_message);
                    textview.setBackgroundColor(color_recording);
                    textview.setText(res.getString(R.string.proc_recording));
                } else if (event.getAction() == MotionEvent.ACTION_UP) { // Action when TextView is released.
                    if (flagActionDown) {
                        // Sends terminal of speech recognition.
                        textview.setTextColor(color_font_message);
                        textview.setBackgroundColor(color_recognizeing);
                        textview.setText(res
                                .getString(R.string.proc_recognizing));
                        flagSRError = !mSpeechTranslator.speechRecognitionEnd();
                        miS2SState = STATE_START_SR;

                        if (flagSRError) {
                            requestTaskCompleteEvent(new CompleteEvent(this,
                                    errorResult));
                        } else {
                            setTextViewEnable(false);
                        }
                    }
                    flagActionDown = false;
                }

                return false;
            }
        };

        // Spinner listener. Does not do anything in particular.
        mSpinnerOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) { // Operation when Item is selected.
                Spinner spinner = (Spinner) parent;
                String sLanguage = (String) spinner.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.s2s_sample);

        // ID initialization
        initIDCreateor();

        // Listener initialization
        initListener();
        addExecuterActionListener(mSpeechTranslator.getMainExecuter());
        addExecuterActionListener(mSpeechTranslator.getSubExecuter());

        addVoiceRecorderActionListener(mSpeechTranslator.getVoiceRecorder());
        mSpeechTranslator.start();

        // Spinner initialization
        Spinner mSpinnerUpper = (Spinner) findViewById(R.id.spinnerUpper);
        Spinner mSpinnerLower = (Spinner) findViewById(R.id.spinnerLower);
        mSpinnerUpper.setSelection(0);
        mSpinnerLower.setSelection(1);
        mSpinnerUpper.setOnItemSelectedListener(mSpinnerOnItemSelectedListener);
        mSpinnerLower.setOnItemSelectedListener(mSpinnerOnItemSelectedListener);

        // TextView initialization
        TextView mTextViewUpper = (TextView) findViewById(R.id.textViewUpper);
        TextView mTextViewLower = (TextView) findViewById(R.id.textViewLower);
        mTextViewUpper.setLongClickable(true);
        mTextViewLower.setLongClickable(true);
        mTextViewUpper.setOnTouchListener(mTextViewOnTouchListener);
        mTextViewLower.setOnTouchListener(mTextViewOnTouchListener);
        mTextViewUpper.setPadding(10, 5, 10, 5);
        mTextViewLower.setPadding(10, 5, 10, 5);

        // Sets LevelMeter
        LinearLayout layoutLevelMeter = (LinearLayout) findViewById(R.id.linearLayoutLevelMeter);
        layoutLevelMeter.setPadding(5, 0, 5, 0);
        TextView[] lelveView = new TextView[20];
        lelveView[0] = (TextView) findViewById(R.id.L0);
        lelveView[1] = (TextView) findViewById(R.id.L1);
        lelveView[2] = (TextView) findViewById(R.id.L2);
        lelveView[3] = (TextView) findViewById(R.id.L3);
        lelveView[4] = (TextView) findViewById(R.id.L4);
        lelveView[5] = (TextView) findViewById(R.id.L5);
        lelveView[6] = (TextView) findViewById(R.id.L6);
        lelveView[7] = (TextView) findViewById(R.id.L7);
        lelveView[8] = (TextView) findViewById(R.id.L8);
        lelveView[9] = (TextView) findViewById(R.id.L9);
        lelveView[10] = (TextView) findViewById(R.id.L10);
        lelveView[11] = (TextView) findViewById(R.id.L11);
        lelveView[12] = (TextView) findViewById(R.id.L12);
        lelveView[13] = (TextView) findViewById(R.id.L13);
        lelveView[14] = (TextView) findViewById(R.id.L14);
        lelveView[15] = (TextView) findViewById(R.id.L15);
        lelveView[16] = (TextView) findViewById(R.id.L16);
        lelveView[17] = (TextView) findViewById(R.id.L17);
        lelveView[18] = (TextView) findViewById(R.id.L18);
        lelveView[19] = (TextView) findViewById(R.id.L19);
        mLevelMeterControl = new LevelMeterControl(lelveView);
        mLevelMeterControl.setLevelMeter(0);

        mSoundPlayer.initialize();
        mSoundPlayer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSpeechTranslator.destroyExecute();
        mSoundPlayer.stopAudio();
        finish();
        System.exit(0);
    }

    /**
     * Adds event listener for completion of request
     * 
     * @param executer
     */
    protected void addExecuterActionListener(PublicS2SSendThread executer) {
        executer.addActionListener(new CompleteEventAdapter() {
            @Override
            public void requestTaskComplete(CompleteEvent event) {
                requestTaskCompleteEvent(event);
            }
        });
    }

    /**
     * Gets recognition, translation, and synthesis results and sends next request. Screen transition is also carried out here.
     * 
     * @param completeEvent
     */
    protected void requestTaskCompleteEvent(CompleteEvent completeEvent) {
        final SpeechTranslationData result = completeEvent.getContainerData();

        mHandler.post(new Runnable() {
            public void run() {
                TextView sr_TextView = null;
                TextView mt_TextView = null;
                String sSR_Result = null;
                String sMT_Result = null;
                String sUtteranceID = null;
                byte[] bBinaryData = null;

                Resources res = getResources();

                if (mFlagCurrentUpper) {
                    sr_TextView = (TextView) findViewById(R.id.textViewUpper);
                    mt_TextView = (TextView) findViewById(R.id.textViewLower);
                } else {
                    sr_TextView = (TextView) findViewById(R.id.textViewLower);
                    mt_TextView = (TextView) findViewById(R.id.textViewUpper);
                }

                SentenceType[] sentences = null;

                try {
                    if (result.isSRDivFirstResponse()) { // When asynchronous division transmission head arrives
                        if (result.isError()) { // When error occurs with asynchronous head
                            // Changes to error event and resends.
                            switch (miS2SState) {
                            case STATE_PRE_SR:
                                // Although currently sending data, an error has occurred
                                // Processes as an error from start of speech recognition processing.
                                break;
                            case STATE_START_SR:
                                // As error was generated during recognition, processes as a normal error.
                                SpeechTranslationData errorResult = new SpeechTranslationData();
                                errorResult.setError();
                                requestTaskCompleteEvent(new CompleteEvent(
                                        this, errorResult));
                                break;
                            default:
                            }
                        }
                    } else if (result.isSR_OUT()) { // speech recognition results
                        setTextViewColor(sr_TextView, color_font_normal,
                                color_normal);
                        Log.i("S2SSample", "Speech recognition result begin");
                        Log.i("S2SSample", "Result  " + result.generate());
                        Log.i("S2SSample", "Speech recognition result end");

                        // User tag
                        UserTag userTag = new UserTag();
                        setUserTag(result, userTag);

                        // Server tag
                        ServerTag serverTag = new ServerTag();
                        ServerType serverType = result.getMCML().getServer();

                        PersonalityType personalityType = serverType
                                .getResponse().getOutput().getData().getText()
                                .getModelType().getPersonality();
                        Map<String, String> personalityTypeMap = getPersonalityType(personalityType);

                        // SentenceSequenceType
                        SentenceSequenceType sentenceSequenceType = serverType
                                .getResponse().getOutput().getData().getText()
                                .getSentenceSequence();

                        final int chunkCount = sentenceSequenceType
                                .getSentence().getChunkCount();

                        List<String> surfaceList = new ArrayList<String>(
                                chunkCount);
                        for (int index = 0; index < chunkCount; index++) {
                            final String surface = sentenceSequenceType
                                    .getSentence().getChunkAt(index)
                                    .getSurface().getValue().toString();
                            surfaceList.add(surface);
                        }
                        sSR_Result = "";
                        // Sets Surface tag character string as is on screen display.
                        sSR_Result = sentenceSequenceType.getSentence()
                                .getSurface().getValue().toString();
                        sr_TextView.setText(sSR_Result);

                        setServerTag(serverType, serverTag, sSR_Result);

                        sUtteranceID = result.getMCML().getServer()
                                .getResponse().getProcessOrder().toString();

                        // Gets VoiceTag
                        Hashtable<String, Object> voiceFont = null;
                        String sVoiceFontLanguage = null;
                        TextType sTextType = result.getMCML().getServer()
                                .getResponse().getOutput().getData().getText();
                        PersonalityType sPersonaryType = sTextType
                                .getModelType().getPersonality();

                        if (sTextType.hasModelType()
                                && sTextType.getModelType().hasLanguage()
                                && sTextType.getModelType().hasPersonality()) {
                            sVoiceFontLanguage = sTextType.getModelType()
                                    .getLanguage().getID().toString();
                            voiceFont = new Hashtable<String, Object>();
                            voiceFont.put("ID", sPersonaryType.getID());
                            voiceFont.put("Age", sPersonaryType.getAge());
                            voiceFont.put("Gender", sPersonaryType.getGender());
                            voiceFont.put("FORMAT", sVoiceFontLanguage);
                            mVoiceFontTable.put(sVoiceFontLanguage, voiceFont);
                        }

                        // Translates recognition results.
                        setTextViewColor(mt_TextView, color_font_message,
                                color_translating);
                        mt_TextView.setText(res
                                .getString(R.string.proc_translating));
                        miS2SState = STATE_START_MT;

                        mSpeechTranslator.translate(sentences,
                                msSourceLanguage, msTargetLanguage,
                                sUtteranceID,
                                PublicCreateID.REQUEST_TYPE_SR_RESULT, userTag,
                                serverTag, personalityTypeMap, surfaceList);
                    } else if (result.isMT_OUT()) { // Translation results
                        if (result.isBackTranslation()) { // Reverse translation

                            Log.i("S2SSample", "Back translation result begin");
                            Log.i("S2SSample", "Result  " + result.generate());
                            Log.i("S2SSample", "Back translation result end");

                            setTextViewColor(sr_TextView, color_font_normal,
                                    color_normal);

                            sMT_Result = "";
                            SentenceSequenceType sSentenceSequenceType = result
                                    .getMCML().getServer().getResponse()
                                    .getOutput().getData().getText()
                                    .getSentenceSequence();
                            sMT_Result = getSurfaceText(sSentenceSequenceType);

                            sMT_Result = sr_TextView.getText() + "\n" + "<<"
                                    + sMT_Result + ">>";
                            sr_TextView.setText(sMT_Result);

                            switch (miS2SState) {
                            case STATE_START_BMT_SS:
                                // Sets state to currently synthesizing.
                                miS2SState = STATE_START_BMTCOMP_SS;
                                break;
                            case STATE_START_SSCOMP_BMT:
                            case STATE_START_SSERR_BMT:
                                // Sets state back to before recognition.
                                miS2SState = STATE_PRE_SR;
                                break;
                            default:
                            }

                        } else { // Normal translation

                            Log.i("S2SSample", "Translation result begin");
                            Log.i("S2SSample", "Result  " + result.generate());
                            Log.i("S2SSample", "Translation result end");

                            UserTag userTag = new UserTag();
                            setUserTag(result, userTag);

                            // Server tag
                            ServerTag serverTag = new ServerTag();
                            ServerType serverType = result.getMCML()
                                    .getServer();

                            PersonalityType personalityType = serverType
                                    .getResponse().getOutput().getData()
                                    .getText().getModelType().getPersonality();
                            Map<String, String> personalityTypeMap = getPersonalityType(personalityType);

                            SentenceSequenceType sSentenceSequenceType = result
                                    .getMCML().getServer().getResponse()
                                    .getOutput().getData().getText()
                                    .getSentenceSequence();

                            // Sets translation results on screen.
                            sMT_Result = "";
                            final int chunkCount = sSentenceSequenceType
                                    .getSentence().getChunkCount();

                            List<String> surfaceList = new ArrayList<String>(
                                    chunkCount);
                            for (int index = 0; index < chunkCount; index++) {
                                final String surface = sSentenceSequenceType
                                        .getSentence().getChunkAt(index)
                                        .getSurface().getValue().toString();
                                surfaceList.add(surface);
                            }

                            sMT_Result = getSurfaceText(sSentenceSequenceType);

                            mt_TextView.setText(sMT_Result);

                            setServerTag(serverType, serverTag, sMT_Result);

                            sUtteranceID = result.getMCML().getUser()
                                    .getReceiver().getDevice().getLocation()
                                    .getURI().getValue();

                            miS2SState = STATE_START_BMT_SS;
                            setTextViewColor(sr_TextView, color_font_message,
                                    color_translating);
                            setTextViewColor(mt_TextView, color_font_message,
                                    color_synthesising);

                            Hashtable<String, Object> voiceFont = null;
                            String sVoiceFontLanguage = msTargetLanguage;
                            voiceFont = mVoiceFontTable.get(sVoiceFontLanguage);

                            if (voiceFont == null) {
                                voiceFont = new Hashtable<String, Object>();
                            }

                            // Voice-synthesizes translation results.
                            mSpeechTranslator.speechSynthesis(result.getMCML()
                                    .getServer().getResponse().getOutput()
                                    .getData().getText().getSentenceSequence()
                                    .getSentence(), msTargetLanguage,
                                    sUtteranceID, voiceFont, userTag,
                                    serverTag, personalityTypeMap, surfaceList);
                            // Reverse-translates translation results.
                            mSpeechTranslator
                                    .translate(
                                            sentences,
                                            msTargetLanguage,
                                            msSourceLanguage,
                                            sUtteranceID,
                                            PublicCreateID.REQUEST_TYPE_REVERSE_TRANSLATION,
                                            userTag, serverTag,
                                            personalityTypeMap, surfaceList);

                        }
                    } else if (result.isSS_OUT()) { // Voice synthesis
                        // Plays synthesized sound.
                        Log.i("S2SSample", "Speech synthesis result begin");
                        Log.i("S2SSample", "Result  " + result.generate());
                        Log.i("S2SSample", "Speech synthesis result end");

                        bBinaryData = result.getBinaryData();
                        if (bBinaryData != null) {
                            playSpeechSynthesis(bBinaryData, result.getMCML()
                                    .getServer().getResponse().getOutput()
                                    .getData().getAudio().getSignal()
                                    .getAudioFormat().getValue()
                                    .equals("ADPCM"));
                        }
                        switch (miS2SState) {
                        case STATE_START_BMT_SS:
                            // Sets state to currently implementing back translation
                            miS2SState = STATE_START_SSCOMP_BMT;
                            break;
                        case STATE_START_BMTCOMP_SS:
                        case STATE_START_BMTERR_SS:
                            // Sets state back to before recognition.
                            miS2SState = STATE_PRE_SR;
                            break;
                        default:
                        }
                        setTextViewColor(mt_TextView, color_font_normal,
                                color_normal);
                        setTextViewEnable(true);
                    } else if (result.isError()) { // Error
                        setTextViewColor(sr_TextView, color_font_normal,
                                color_normal);
                        setTextViewColor(mt_TextView, color_font_normal,
                                color_normal);
                        setTextViewEnable(true);

                        if (result.isExistMCML()) {
                            Log.i("S2SSample", "Result  " + result.generate());
                        }

                        switch (miS2SState) {
                        case STATE_PRE_SR:
                            // Error does not occur in this state.
                            break;
                        case STATE_START_SR:
                            // Recognition failure.
                            sr_TextView.setText(res
                                    .getString(R.string.err_recognition));
                            mt_TextView.setText("");
                            // Sets state back to before recognition.
                            miS2SState = STATE_PRE_SR;
                            break;
                        case STATE_START_MT:
                            // Translation failure. Produces recognition results.

                            mt_TextView.setText(res
                                    .getString(R.string.err_translation));
                            // Sets state back to before recognition.
                            miS2SState = STATE_PRE_SR;
                            break;
                        case STATE_START_SSCOMP_BMT:
                        case STATE_START_SSERR_BMT:
                            // Error while waiting for reverse translation after completion of synthesis
                            sr_TextView.setText(sr_TextView.getText()
                                    + "\n"
                                    + res.getString(R.string.err_backtranslation));
                            // Sets state back to before recognition.
                            miS2SState = STATE_PRE_SR;
                            break;
                        case STATE_START_BMTCOMP_SS:
                        case STATE_START_BMTERR_SS:
                            // Error while waiting after completion of reverse translation.
                            // Display not affected.
                            // Sets state back to before recognition.
                            miS2SState = STATE_PRE_SR;
                            break;
                        case STATE_START_BMT_SS:
                            // Error while waiting for reverse translation and synthesis.

                            if (result.isSR_IN()) {
                                // Recognition error
                                // This does not occur in this state.
                            } else if (result.isMT_IN()) {
                                // Translation error ( Reverse translation )
                                miS2SState = STATE_START_BMTERR_SS;
                                sr_TextView.setText(sr_TextView.getText()
                                        + "\n"
                                        + res.getString(R.string.err_backtranslation));
                                setTextViewColor(mt_TextView,
                                        color_font_message, color_synthesising);
                            } else if (result.isSS_IN()) {
                                // Synthesis error
                                miS2SState = STATE_START_SSERR_BMT;
                                setTextViewColor(sr_TextView,
                                        color_font_message, color_translating);
                            }
                            break;
                        default:
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
    }

    /** Sets GlobalPositionType(Latitude, Longitude).*/
    private void setGlobalPositionType(GlobalPositionType globalPositionType,
            UserTag userTag) throws Exception {

        userTag.transMitterDeviceLocationGlobalPosition.put("Latitude",
                globalPositionType.getLatitude().toString());
        userTag.transMitterDeviceLocationGlobalPosition.put("Longitude",
                globalPositionType.getLongitude().toString());
    }

    /**
     * Gets results from PersonalityType by MAP.
     * 
     * @throws Exception
     */
    private Map<String, String> getPersonalityType(
            PersonalityType personalityType) throws Exception {

        Map<String, String> personalityTypeMap = new HashMap<String, String>(2,
                2);
        personalityTypeMap.put("Age", personalityType.getAge().toString());
        personalityTypeMap
                .put("Gender", personalityType.getGender().getValue());
        return personalityTypeMap;
    }

    private void setUserTag(final SpeechTranslationData result, UserTag userTag)
            throws Exception {

        UserType userType = result.getMCML().getUser();

        GlobalPositionType globalPositionType = userType.getTransmitter()
                .getDevice().getLocation().getGlobalPosition();

        setGlobalPositionType(globalPositionType, userTag);
    }

    /** */
    private void setServerTag(ServerType serverType, ServerTag serverTag,
            final String result) throws Exception {

        // <Response>tag
        // ProcessOrder
        serverTag.requestTag.put("ProcessOrder", serverType.getResponse()
                .getProcessOrder().toString());

        // <Text>tag
        // ChannelID
        serverTag.requestTagInputDataText.put("ChannelID", serverType
                .getResponse().getOutput().getData().getText().getChannelID()
                .toString());

        // <ModelType>tag
        // Domain
        serverTag.requestTagInputDataTextModelTypeDomain = serverType
                .getResponse().getOutput().getData().getText().getModelType()
                .getDomain().toString();
        // Task
        serverTag.requestTagInputDataTextModelTypeTask = serverType
                .getResponse().getOutput().getData().getText().getModelType()
                .getTask().toString();

        // <SentenceSequence>tag
        // N-bestRank
        serverTag.requestTagInputDataTextSentenceSequence.put("N-bestRank",
                serverType.getResponse().getOutput().getData().getText()
                        .getSentenceSequence().getN_bestRank().toString());

        // Order
        serverTag.requestTagInputDataTextSentenceSequence.put("Order",
                serverType.getResponse().getOutput().getData().getText()
                        .getSentenceSequence().getOrder().toString());

        // Score
        serverTag.requestTagInputDataTextSentenceSequence.put("Score",
                serverType.getResponse().getOutput().getData().getText()
                        .getSentenceSequence().getScore().toString());

        // <Sentence>tag
        serverTag.requestTagInputDataTextSentenceSequenceSentence.put("Order",
                serverType.getResponse().getOutput().getData().getText()
                        .getSentenceSequence().getSentence().getOrder()
                        .toString());

        // Serface
        serverTag.requestTagInputDataTextSentenceSequenceSentenceSurface = result;

    }

    /**
     * Gets Surface text displayed on screen.
     * 
     * @param sentenceSequenceType
     * @return
     * @throws Exception
     */
    private String getSurfaceText(
            final SentenceSequenceType sentenceSequenceType) throws Exception {
        return sentenceSequenceType.getSentence().getSurface().getValue()
                .toString();
    }

    /**
     * Changes TextView background and font color.
     * 
     * @param textView
     * @param iFontColor
     * @param iBackGroundColor
     */
    private void setTextViewColor(TextView textView, int iFontColor,
            int iBackGroundColor) {
        textView.setBackgroundColor(iBackGroundColor);
        textView.setTextColor(iFontColor);
    }

    private void setTextViewEnable(boolean flagEnable) {
        TextView mTextViewUpper = (TextView) findViewById(R.id.textViewUpper);
        TextView mTextViewLower = (TextView) findViewById(R.id.textViewLower);

        mTextViewUpper.setEnabled(flagEnable);
        mTextViewLower.setEnabled(flagEnable);
    }

    /**
     * Adds event listener for voice volume notification.
     * 
     * @param Executer
     */
    protected void addVoiceRecorderActionListener(VoiceRecorder voiceRecorder) {
        voiceRecorder.addActionListener(new VoiceRecorderEventAdapter() {
            @Override
            public void voiceVolume(VoiceRecorderEvent event) {
                voiceVolumeEvent(event);
            }
        });
    }

    /**
     * Moves level meter when voice volume is notified.
     * 
     * @param CompleteEvent
     */
    protected void voiceVolumeEvent(VoiceRecorderEvent event) {
        mLevelMeterControl.setLevelMeter(event.getLogVolumePerPeek());
    }

    private void playSpeechSynthesis(byte[] binaryData, boolean isADPCM) {
        if (binaryData != null) {
            // When binary datas is ADPCM, they convert to Raw PCM.
            if (isADPCM) {
                byte[] temp = new byte[adpcmDivideDecodeSize];
                int iCount = 0;
                int iLastLength = binaryData.length;
                boolean lastFlag = false;

                while (true) {
                    if (iLastLength <= adpcmDivideDecodeSize) {
                        temp = new byte[iLastLength];
                        System.arraycopy(binaryData, iCount
                                * adpcmDivideDecodeSize, temp, 0, iLastLength);
                        lastFlag = true;
                        iLastLength = 0;
                    } else {
                        temp = new byte[adpcmDivideDecodeSize];
                        System.arraycopy(binaryData, iCount
                                * adpcmDivideDecodeSize, temp, 0,
                                adpcmDivideDecodeSize);
                        iLastLength -= adpcmDivideDecodeSize;
                    }
                    temp = adpcmConverter.Decode(temp);

                    mSoundPlayer.addSpeechData(temp);

                    if (lastFlag) {
                        break;
                    }
                    iCount++;
                }
            } else {

                // Converts audio data to binary.
                byte[] soundData = EndianConverter.ConvertShort(binaryData,
                        ByteOrder.BIG_ENDIAN);

                mSoundPlayer.addSpeechData(soundData);
            }
        }
    }
}
