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

package jp.go.nict.mcml.server.mt;

import java.util.ArrayList;
import java.util.HashMap;

import jp.go.nict.mcml.serverap.common.ServerApProperties;
import jp.go.nict.mcml.servlet.MCMLStatics;

/**
 * MT property class.
 *
 */
public class MTProperties extends ServerApProperties {
    // ------------------------------------------
    // private member constant
    // ------------------------------------------
    private static final String KEY_ENGINE_MT = KEY_ENGINE + "mt";
    private static final String KEY_STARTUPCOMMAND = "startupcommand";
    private static final String KEY_CONTROLLERCOMMAND = "controllercommand";

    private static final String KEY_INPUTWORDID = KEY_ENGINE + "inputwordid";

    private static final String KEY_ENGINE_LANGUAGE = KEY_ENGINE + "language";
    private static final String KEY_ENGINE_LANGUAGE_MAXNUMBER = KEY_ENGINE_LANGUAGE
            + "." + "maxnumber";
    private static final String KEY_ENGINE_DIRECTION = KEY_ENGINE
            + "direction.";
    private static final String KEY_ENGINE_DIRECTION_COMMANDLISTFILE = KEY_ENGINE_DIRECTION
            + "commandlistfile";

    private static final String KEY_DELIMITER = ".delimiter";
    private static final String KEY_STRINGCODE = ".stringcode";
    private static final String KEY_TEXTFILTERFILE = ".textfilterfile";

    private static final String KEY_NBEST = "nbest";
    private static final String KEY_PARTOFSPEECH = "partofspeech";
    private boolean m_WordTagPartOfSpeech = false;

    /**
     * Gets WordTagPartOfSpeech.
     *
     * @return WordTagPartOfSpeech
     */
    public boolean getWordTagPartOfSpeech() {
        return m_WordTagPartOfSpeech;
    }

    private boolean readPartOfSpeech(String paramSection) {
        String temp = m_Properties.getProperty(paramSection);

        if (temp == null || temp.isEmpty()) {
            // framesyncsize is invalid.
            return false;
        }

        if (temp.toLowerCase().indexOf(MCMLStatics.STRING_YES) >= 0) {
            return true;
        }

        return false;
    }

    private static final String KEY_ENGINE_NULLRESPONSE_SLEEPMSEC = KEY_ENGINE
            + "nullresponse.sleepmsec";
    private static final String KEY_ENGINE_NULLRESPONSE_COUNTER = KEY_ENGINE
            + "nullresponse.counter";

    // ------------------------------------------
    // protected member variable(class field)
    // ------------------------------------------
    protected static final MTProperties M_INSTANCE = new MTProperties();

    // ------------------------------------------
    // private member variable(instance field)
    // ------------------------------------------
    private ArrayList<MTParam> m_MTParamList;
    private boolean m_InputWordID;

    private int m_LanguageMaxNumber;
    private MTLanguageInfoManager m_LanguageInfoManager;

    private int m_NullResponseSleepMSec = 10;
    private int m_NullResponseeCounter = 10;

    // ------------------------------------------
    // public member function
    // ------------------------------------------
    /**
     * Gets instance.
     *
     * @return Instance
     */
    public static MTProperties getInstance() {
        return M_INSTANCE;
    }

    /** Reads property. */
    @Override
    public void readProperties(String fileName) throws Exception {
        super.readProperties(fileName);

        // read LanguageParameters
        readLanguageParameters();

        m_WordTagPartOfSpeech = readPartOfSpeech(KEY_ENGINE + KEY_NBEST + "."
                + KEY_PARTOFSPEECH);

        // Loop by Max Engine number
        for (int i = 1; i <= m_EngineMaxNumber; i++) {
            String paramSection = KEY_ENGINE_MT + String.valueOf(i);

            // Parameter Manager Class Create
            MTParam params = new MTParam();

            // Engine(MT startup) Start up Command
            String temp = m_Properties.getProperty(paramSection + "."
                    + KEY_STARTUPCOMMAND);
            if (temp == null || temp.isEmpty()) {
                // mtX.startupcommand is invalid.
                System.out.println("Invalid Parameter: " + paramSection + "."
                        + KEY_STARTUPCOMMAND);
                continue;
            }
            params.setStartUpCommand(temp);

            // Engine(MT controller) Start up Command
            temp = m_Properties.getProperty(paramSection + "."
                    + KEY_CONTROLLERCOMMAND);
            if (temp == null || temp.isEmpty()) {
                // mtX.controllercommand is "".
                System.out.println("Invalid Parameter: " + paramSection + "."
                        + KEY_CONTROLLERCOMMAND);
                continue;
            }
            params.setControllerCommand(temp);

            m_MTParamList.add(params);
        }

        // read inputwordid property.
        String sInputWordID = m_Properties.getProperty(KEY_INPUTWORDID);
        if (sInputWordID == null) {
            // engine.inputwordid is "".
            System.out.println("Invalid Parameter: " + KEY_INPUTWORDID);
            throw new Exception("Invalid Parameter: " + KEY_INPUTWORDID);
        }
        if (sInputWordID.equalsIgnoreCase(MCMLStatics.STRING_YES)) {
            m_InputWordID = true;
        } else if (sInputWordID.equalsIgnoreCase(MCMLStatics.STRING_NO)) {
            m_InputWordID = false;
        } else {
            // engine.inputwordid is bad.
            System.out.println("Invalid Parameter: " + KEY_INPUTWORDID);
            throw new Exception("Invalid Parameter: " + KEY_INPUTWORDID);
        }

        readNullResponseParameter();

        // normal end
        return;
    }

    /**
     * Get Engine(MT) Parameter Manager class
     *
     * @param engineNo
     * @return  Values of list corresponding to parameter engineNo key.
     */
    public MTParam getMTParam(int engineNo) {
        return m_MTParamList.get(engineNo);
    }

    /** Gets EngineNumber. */
    @Override
    public int getEngineNumber() {
        return m_MTParamList.size();
    }

    /**
     * Determines if InputWordID is true or not.
     *
     * @return m_InputWordID
     */
    public boolean isInputWordID() {
        return m_InputWordID;
    }

    /**
     * get LanguageInfo manager
     *
     * @return m_LanguageInfoManager
     */
    public MTLanguageInfoManager getLanguageInfoManager() {
        return m_LanguageInfoManager;
    }

    /**
     * Gets LanguageMaxNumber.
     *
     * @return LanguageMaxNumber
     */
    public int getLanguageMaxNumber() {
        return m_LanguageMaxNumber;
    }

    /**
     * Gets NullResponseSleepMSec.
     *
     * @return NullResponseSleepMSec
     */
    public int getNullResponseSleepMSec() {
        return m_NullResponseSleepMSec;
    }

    /**
     * Gets NullResponseCounter.
     *
     * @return NullResponseCounter
     */
    public int getNullResponseCounter() {
        return m_NullResponseeCounter;
    }

    // ------------------------------------------
    // private member function
    // ------------------------------------------
    // constructor
    private MTProperties() {
        super();
        m_MTParamList = new ArrayList<MTParam>();

        m_LanguageInfoManager = new MTLanguageInfoManager();
    }

    // read parameter relation to language
    private void readLanguageParameters() throws Exception {
        String temp = "";

        try {

            // get language max number
            temp = m_Properties.getProperty(KEY_ENGINE_LANGUAGE_MAXNUMBER);

            if (temp == null || temp.isEmpty()) {
                System.out.println("Invalid Parameter: "
                        + KEY_ENGINE_LANGUAGE_MAXNUMBER);
                throw new Exception("Invalid Parameter: "
                        + KEY_ENGINE_LANGUAGE_MAXNUMBER);
            }

            // set language Max number
            m_LanguageMaxNumber = Integer.parseInt(temp);

            if (m_LanguageMaxNumber < 2) {
                // if language max number is less than 2,failed to start
                System.out.println("Invalid Parameter: " + "short of "
                        + KEY_ENGINE_LANGUAGE_MAXNUMBER);
                throw new Exception("Invalid Parameter: " + "short of "
                        + KEY_ENGINE_LANGUAGE_MAXNUMBER);
            }

        } catch (NumberFormatException e) {
            // if language max number is abnormal format
            System.out.println("Invalid Parameter: "
                    + KEY_ENGINE_LANGUAGE_MAXNUMBER + " is abnormal format");
            throw new Exception("Invalid Parameter: "
                    + KEY_ENGINE_LANGUAGE_MAXNUMBER + " is abnormal format");
        }

        // get direction command list filename
        temp = m_Properties.getProperty(KEY_ENGINE_DIRECTION_COMMANDLISTFILE);

        if (temp == null || temp.isEmpty()) {
            System.out.println("Invalid Parameter: "
                    + KEY_ENGINE_DIRECTION_COMMANDLISTFILE);
            throw new Exception("Invalid Parameter: "
                    + KEY_ENGINE_DIRECTION_COMMANDLISTFILE);
        }

        // set direction command list file to LanguageInfoManager
        m_LanguageInfoManager.setDirectionCommandListFile(temp);

        // set target language list to LanguageInfoManager
        for (int i = 1; i <= m_LanguageMaxNumber; i++) {

            temp = m_Properties.getProperty(KEY_ENGINE_LANGUAGE
                    + String.valueOf(i));

            if (temp != null && !temp.isEmpty()) {

                m_LanguageInfoManager.setLanguageList(temp);
            }
        }

        // check valid language number
        if (m_LanguageInfoManager.getLanguageList().size() < 2) {
            // if valid language number is less than 2,failed to start
            System.out.println("Invalid Parameter: "
                    + "count of language too short");
            throw new Exception("Invalid Parameter: "
                    + "count of language too short");
        }

        // set language information
        for (int i = 1; i <= m_LanguageMaxNumber; i++) {

            String language = "";
            String delimiter = "";
            String stringcode = "";
            String textfilterfile = "";

            String languageNumStr = String.valueOf(i);

            // get language from property file
            language = m_Properties.getProperty(KEY_ENGINE_LANGUAGE
                    + languageNumStr);

            if (language != null && !language.isEmpty()) {

                // get StringCode from property file
                stringcode = m_Properties.getProperty(KEY_ENGINE_LANGUAGE
                        + languageNumStr + KEY_STRINGCODE);

                if (stringcode == null || stringcode.isEmpty()) {

                    System.out.println("Invalid Parameter: "
                            + KEY_ENGINE_LANGUAGE + languageNumStr
                            + KEY_STRINGCODE);

                    throw new Exception("Invalid Parameter: " + languageNumStr
                            + KEY_STRINGCODE);
                }

                // get delimiter from property file
                delimiter = m_Properties.getProperty(KEY_ENGINE_LANGUAGE
                        + languageNumStr + KEY_DELIMITER);

                if (delimiter == null) {

                    System.out.println("Invalid Parameter: "
                            + KEY_ENGINE_LANGUAGE + languageNumStr
                            + KEY_DELIMITER);

                    throw new Exception("Invalid Parameter: " + languageNumStr
                            + KEY_DELIMITER);
                }

                // get text filter file from property file
                textfilterfile = m_Properties.getProperty(KEY_ENGINE_LANGUAGE
                        + languageNumStr + KEY_TEXTFILTERFILE);

                if (textfilterfile == null || textfilterfile.isEmpty()) {

                    textfilterfile = "";
                }

                // get directionCommandMap of the language
                HashMap<String, String> directionCommandMap = m_LanguageInfoManager
                        .getDirectionCommandMap(language);

                // set information of the language
                m_LanguageInfoManager.setLanguageInfo(language, delimiter,
                        stringcode, textfilterfile, directionCommandMap);

            } else if (language == null) {
                // if language + KEY_ENGINE_LANGUAGE is null,ignore this number
            } else if (language.isEmpty()) {
                // if language + KEY_ENGINE_LANGUAGE is empty
                System.out.println("Invalid Parameter: " + KEY_ENGINE_LANGUAGE
                        + languageNumStr + " is empty");
            }
        }
        return;
    }

    private void readNullResponseParameter() {
        String temp = null;
        // NullResponseSleepMSec
        try {
            temp = m_Properties.getProperty(KEY_ENGINE_NULLRESPONSE_SLEEPMSEC,
                    "10");
            if (temp == null || temp.isEmpty()) {
                System.out.println("Invalid Parameter: "
                        + KEY_ENGINE_NULLRESPONSE_SLEEPMSEC);
            } else {
                int tempVal = Integer.parseInt(temp);
                if (tempVal > 0) {
                    m_NullResponseSleepMSec = tempVal;
                } else {
                    System.out.println("Invalid Parameter: "
                            + KEY_ENGINE_NULLRESPONSE_SLEEPMSEC + " <= 0");
                }
            }
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid Parameter: "
                    + KEY_ENGINE_NULLRESPONSE_SLEEPMSEC);
        }

        // NullResponseCounter
        try {
            temp = m_Properties.getProperty(KEY_ENGINE_NULLRESPONSE_COUNTER,
                    "10");
            if (temp == null || temp.isEmpty()) {
                System.out.println("Invalid Parameter: "
                        + KEY_ENGINE_NULLRESPONSE_COUNTER);
            } else {
                int tempVal = Integer.parseInt(temp);
                if (tempVal > 0) {
                    m_NullResponseeCounter = tempVal;
                } else {
                    System.out.println("Invalid Parameter: "
                            + KEY_ENGINE_NULLRESPONSE_COUNTER + " <= 0");
                }
            }
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid Parameter: "
                    + KEY_ENGINE_NULLRESPONSE_COUNTER);
        }
    }

}
