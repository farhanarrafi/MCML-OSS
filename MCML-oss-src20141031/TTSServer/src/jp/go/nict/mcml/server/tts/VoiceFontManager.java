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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import jp.go.nict.mcml.serverap.common.ServerApLogger;
import jp.go.nict.mcml.servlet.MCMLStatics;
import jp.go.nict.mcml.servlet.XMLTypeTools;
import jp.go.nict.mcml.xml.types.InputUserProfileType;
import jp.go.nict.mcml.xml.types.MCMLType;

/**
 * VoiceFontManager class.
 *
 *
 */
public class VoiceFontManager {
    // ------------------------------------------
    // public member class
    // ------------------------------------------
    /**
     * VoiceFont inner class.
     *
     */
    public class VoiceFont {
        private String m_VoiceFontName;
        private String m_F0Mean;

        /** Inner class default constructor */
        public VoiceFont() {
            m_VoiceFontName = "";
            m_F0Mean = Float.toString(SSMLStatics.F0_MEAN);
        }

        /**
         * Gets Voicefont name.
         *
         * @return Voicefont name
         */
        public String getVoiceFontName() {
            return m_VoiceFontName;
        }

        /**
         * Gets F0Mean.
         *
         * @return F0Mean
         */
        public String getF0Mean() {
            return m_F0Mean;
        }

        /**
         * Sets voice font names.
         *
         * @param voiceFontName
         */
        public void setVoiceFontName(String voiceFontName) {
            m_VoiceFontName = voiceFontName;
        }

        /**
         * f0Mean.
         *
         * @param f0Mean
         */
        public void setF0Mean(String f0Mean) {
            m_F0Mean = f0Mean;
        }

    }

    // ------------------------------------------
    // private member constant(class field)
    // ------------------------------------------
    private static final String COMMENT_MARK = "#";
    private static final String EQUAL_MARK = "=";
    private static final String SPACE_MARK = " ";
    private static final String COMMA_MARK = ",";
    private static final String COLON_MARK = ":";
    private static final String OPTION_MARK = "Option:";
    private static final String DEFAULT_STRING = "Default";
    private static final String GENDER_STRING = "Gender";
    private static final String AGE_STRING = "Age";
    private static final String NATIVE_STRING = "Native";
    private static final String AGETHRESHOLD_STRING = "AgeThreshold";
    private static final String ON_STRING = "On";
    private static final String OFF_STRING = "Off";
    private static final String MALE_STRING = "Male";
    private static final String FEMALE_STRING = "Female";
    private static final String UNKNOWN_STRING = "Unknown";
    private static final String CHILD_STRING = "Child";
    private static final String ADULT_STRING = "Adult";
    private static final String ELDER_STRING = "Elder";
    private static final String NONNATIVE_STRING = "NonNative";
    private static final String INPUT_STRING = "Input";

    // ------------------------------------------
    // private member Enumration(instance field)
    // ------------------------------------------
    /**
     * Gender switch type enumeration class.
     *
     *
     */
    private enum GenderSwitch {
        On, Off, None
    }

    /**
     * Age switch type enumeration class.
     *
     *
     */
    private enum AgeSwitch {
        On, Off, None
    }

    /**
     * Native switch type enumeration class
     *
     */
    private enum NativeSwitch {
        On, Off, None
    }

    // ------------------------------------------
    // private member variables(instance field)
    // ------------------------------------------
    private String m_DefaultVoiceFontID;
    private GenderSwitch m_GenderSwitch;
    private AgeSwitch m_AgeSwitch;
    private NativeSwitch m_NativeSwitch;
    private int m_AdultThreshold;
    private int m_ElderThreshold;
    private AgeNode m_VoiceFontTree;

    // ------------------------------------------
    // public member function
    // ------------------------------------------
    /**
     * Default constructor
     */
    public VoiceFontManager() {
        m_DefaultVoiceFontID = "";
        m_GenderSwitch = GenderSwitch.None;
        m_AgeSwitch = AgeSwitch.None;
        m_NativeSwitch = NativeSwitch.None;
        m_AdultThreshold = -1;
        m_ElderThreshold = Integer.MAX_VALUE;
        m_VoiceFontTree = null;
    }

    /**
     * Gets Voicefont.
     *
     * @param inputMCML
     *            Used when inputUserProfile is not {@code null}
     * @param inputUserProfile
     * @return voiceFont Voice Font inner class information
     * @throws Exception
     */

    public VoiceFont getVoiceFontID(MCMLType inputMCML,
            InputUserProfileType inputUserProfile) throws Exception {
        VoiceFont voiceFont = new VoiceFont();
        String voiceFontID = "";
        String voiceFontName = "";
        String f0_mean = "";

        // try to get ID.
        if (inputUserProfile != null && inputUserProfile.hasID()) {
            voiceFontID = inputUserProfile.getID().toString();
            if (!voiceFontID.isEmpty()) {
                voiceFontName = getVoiceFontName(voiceFontID);
                f0_mean = getF0Mean(voiceFontID);
            }
        }

        if (inputUserProfile == null || voiceFontName.isEmpty()
                || voiceFontName.equalsIgnoreCase(INPUT_STRING)) {
            voiceFontName = "";
            if (inputUserProfile != null) {
                // try to get Combination.

                voiceFontID = searchVoiceFontTree(inputMCML, inputUserProfile);
            }
            if (voiceFontID == null || voiceFontID.isEmpty()) {
                // try to get Default.
                voiceFontID = m_DefaultVoiceFontID;
            }
            if (voiceFontID != null && !voiceFontID.isEmpty()) {
                // string format is already checked
                // (read from VoiceFontTableFile "voiceFontName:f0" )
                String[] elements = voiceFontID.split(COLON_MARK);
                if (elements.length == 2) {
                    voiceFontName = elements[0];
                    f0_mean = elements[1];
                }
            }
        }

        if (!voiceFontName.isEmpty()) {
            voiceFont.setVoiceFontName(voiceFontName);
            voiceFont.setF0Mean(f0_mean);
        } else {
            ServerApLogger.getInstance().writeWarning(
                    "voiceFontName cannot get.");
        }

        return voiceFont;
    }

    /**
     * Reads voice font table file from filename.
     * <p>
     * Nothing is performed if filename is null.
     * </p>
     *
     * @param fileName
     *            Filename
     * @throws Exception
     *             <ul>
     *             <li>Filename not found</li>
     *             <li>Reading failed </li>
     *             <li>Voice font table analysis failure </li>
     *             <li>If voice font table related creation failed</li>
     *             </ul>
     */
    public void readVoiceFontTableFile(String fileName) throws Exception {
        FileInputStream inputFile = null;
        InputStreamReader reader = null;
        BufferedReader fileReader = null;

        try {
            // no VoiceFontTable.
            if (fileName == null || fileName.isEmpty()) {
                return;
            }
            // file open.
            inputFile = new FileInputStream(fileName);
            reader = new InputStreamReader(inputFile, "UTF-8");
            fileReader = new BufferedReader(reader);

            // read VoiceFontTable.
            while (true) {
                String line = fileReader.readLine();
                if (line == null) {
                    // End of File
                    break;
                }
                if (!line.isEmpty()) {
                    parseVoiceFontTable(line);
                }
            }
        } finally {
            if (fileReader != null) {
                fileReader.close();
            }
        }
    }

    // ------------------------------------------
    // private member function
    // ------------------------------------------
    private void parseVoiceFontTable(String inputLine) throws Exception {
        // check inputLine or comment.
        inputLine = checkAndDeleteCommentMark(inputLine);

        if (inputLine.isEmpty()) {
            // comment only.
            return;
        }

        // split by "=". Default=XXXX to Default XXXX.
        String[] elements = inputLine.split(EQUAL_MARK, 2);
        if (elements.length == 1) {
            // fail to split(worng format).
            ServerApLogger.getInstance().writeWarning(
                    "fail to split by \"=\". Default=XXXX => Default XXXX.");
            return;
        }

        // Option line?
        if (elements[0].startsWith(OPTION_MARK)) {
            // parse Option line.
            parseOption(elements);
            // nomal end.
            return;
        }

        // Default line?
        if (elements[0].equalsIgnoreCase(DEFAULT_STRING)) {
            // parse Default line.
            parseDefault(elements[1]);
            // nomal end.
            return;
        }

        // Combination(Gender,Age,Native) line.
        parseCombination(elements);
    }

    private String checkAndDeleteCommentMark(String inputLine) {
        // search Comment Mark("#").
        int commentIndex = inputLine.indexOf(COMMENT_MARK);
        if (commentIndex == -1) {
            // no comment=inputLine
        } else if (0 < commentIndex) {
            // comment delete(split by "XXXX=XXXX # XXXX").
            inputLine = inputLine.substring(0, commentIndex);
        }

        return inputLine;
    }

    private void parseOption(String[] optionLine) throws Exception {
        // delete "Option:"
        String option = optionLine[0].replace(OPTION_MARK, "");

        // delete space.
        option = option.replace(SPACE_MARK, "");

        // Gender?
        if (option.equalsIgnoreCase(GENDER_STRING)) {
            // get Gender Switch.
            String genderSwitch = optionLine[1].replace(SPACE_MARK, "");
            if (genderSwitch.equalsIgnoreCase(ON_STRING)) {
                m_GenderSwitch = GenderSwitch.On;
            } else if (genderSwitch.equalsIgnoreCase(OFF_STRING)) {
                m_GenderSwitch = GenderSwitch.Off;
            } else {
                // worng Line.
                ServerApLogger.getInstance().writeWarning(
                        "Gender Option Switch is worng");
                return;
            }
            // nomal end.
            return;
        }

        // Age?
        if (option.equalsIgnoreCase(AGE_STRING)) {
            // get Age Switch.
            String ageSwitch = optionLine[1].replace(SPACE_MARK, "");
            if (ageSwitch.equalsIgnoreCase(ON_STRING)) {
                m_AgeSwitch = AgeSwitch.On;
            } else if (ageSwitch.equalsIgnoreCase(OFF_STRING)) {
                m_AgeSwitch = AgeSwitch.Off;
            } else {
                // worng Line.
                ServerApLogger.getInstance().writeWarning(
                        "Age Option Switch is worng");
                return;
            }
            // nomal end.
            return;
        }

        // Native?
        if (option.equalsIgnoreCase(NATIVE_STRING)) {
            // get Native Switch.
            String nativeSwitch = optionLine[1].replace(SPACE_MARK, "");
            if (nativeSwitch.equalsIgnoreCase(ON_STRING)) {
                m_NativeSwitch = NativeSwitch.On;
            } else if (nativeSwitch.equalsIgnoreCase(OFF_STRING)) {
                m_NativeSwitch = NativeSwitch.Off;
            } else {
                // worng Line.
                ServerApLogger.getInstance().writeWarning(
                        "Native Option Switch is worng");
                return;
            }
            // nomal end.
            return;
        }

        // AgeThreshold?
        if (option.equalsIgnoreCase(AGETHRESHOLD_STRING)) {
            // get AgeThreshold.
            String ageThreshold = optionLine[1].replace(SPACE_MARK, "");
            getAgeThreshold(ageThreshold);
        }
        // nomal end.
        return;
    }

    private void getAgeThreshold(String ageThreshold) throws Exception {
        // split ","
        String[] thresholds = ageThreshold.split(COMMA_MARK);

        // check format.
        if (thresholds.length == 2) {
            if (!thresholds[0].isEmpty()) {
                m_AdultThreshold = Integer.parseInt(thresholds[0]);
            }

            if (!thresholds[1].isEmpty()) {
                m_ElderThreshold = Integer.parseInt(thresholds[1]);
            }
        } else if (thresholds.length == 1) {
            // Age Threshold is wrong.
            ServerApLogger.getInstance().writeError("Age Threshold is wrong.");
            throw new Exception("Age Threshold is wrong.");
        }
    }

    private void parseDefault(String inputStr) {
        // get voiceFontID
        String voiceFontID = getVoiceFontID(inputStr);

        // no voiceFontID(parse Next).
        if (voiceFontID.isEmpty()) {
            return;
        }

        // replace voiceFontID
        m_DefaultVoiceFontID = voiceFontID;
    }

    private String getVoiceFontID(String inputStr) {
        // expect to input format is "voiceFontName:f0"
        String voiceFontID = "";
        String[] elements = inputStr.split(COLON_MARK, 2);
        String voiceFontName = elements[0].replace(SPACE_MARK, "");
        if (voiceFontName.isEmpty()) {
            // worng format.
            ServerApLogger.getInstance()
                    .writeWarning("VoiceFontName is empty.");
            return "";
        }

        String f0_mean = "";
        if (elements.length == 2) {
            try {
                f0_mean = elements[1].replace(SPACE_MARK, "");
                if (f0_mean.isEmpty()) {
                    throw new Exception("F0 is empty");
                }
                float f0 = new Float(f0_mean);
                if (f0 < 0.0f) {
                    throw new Exception(
                            "F0 is necessary to not negative number");
                }
            } catch (NumberFormatException e) {
                ServerApLogger.getInstance()
                        .writeWarning("F0 format is wrong.");
                f0_mean = "";
            } catch (Exception e) {
                ServerApLogger.getInstance().writeWarning(e.getMessage());
                f0_mean = "";
            }
        }

        voiceFontID = createVoiceFontIDString(voiceFontName, f0_mean);
        return voiceFontID;
    }

    private String getVoiceFontName(String voiceId) throws Exception {
        // input format is "voiceFontName,f0" or "voiceFontName"
        String voiceFontName = "";
        voiceFontName = voiceId.split(COMMA_MARK, 2)[0];

        // normal end
        return voiceFontName;
    }

    private String getF0Mean(String voiceId) throws Exception {
        // input format is "voiceFontName,f0"
        String f0_mean = Float.toString(SSMLStatics.F0_MEAN);
        String[] temp = voiceId.split(COMMA_MARK, 2);
        if (temp.length >= 2) {
            try {
                float f0 = new Float(temp[1]);
                if (f0 > 0.0f) {
                    f0_mean = Float.toString(f0);
                }
            } catch (Exception e) {
                ServerApLogger.getInstance()
                        .writeWarning("F0 format is wrong.");
            }
        }

        // normal end
        return f0_mean;
    }

    private String createVoiceFontIDString(String voiceFontName, String f0Mean) {
        if (f0Mean.isEmpty()) {
            f0Mean = Float.toString(SSMLStatics.F0_MEAN);
        }
        // create format is "voiceFontName,f0"
        String voiceFontID = voiceFontName + COLON_MARK + f0Mean;
        return voiceFontID;
    }

    private void parseCombination(String[] elements) throws Exception {
        // split by "," to combination Element.
        String[] comb = elements[0].split(COMMA_MARK);

        VoiceFontAttributes voiceFontAttributes = new VoiceFontAttributes();
        for (int i = 0; i < comb.length; i++) {
            // delete space.
            String param = comb[i].replace(SPACE_MARK, "");

            // Gender.
            if (!setGender(param, voiceFontAttributes)) {
                // worng format
                ServerApLogger.getInstance().writeWarning(
                        "duplicate Gender parameter.");
                return;
            }

            // Age
            if (!setAge(param, voiceFontAttributes)) {
                // worng format.
                ServerApLogger.getInstance().writeWarning(
                        "duplicate Age parameter.");
                return;
            }

            // Native
            if (!setNative(param, voiceFontAttributes)) {
                // worng format.
                ServerApLogger.getInstance().writeWarning(
                        "duplicate Native parameter.");
                return;
            }
        }

        if (voiceFontAttributes.isEmpty()) {
            return;
        }

        voiceFontAttributes.setVoiceFontID(getVoiceFontID(elements[1]));

        // create VoiceFontTree.
        createVoiceFontTree(voiceFontAttributes);
    }

    private boolean setGender(String element,
            VoiceFontAttributes voiceFontAttributes) {
        VoiceFontAttributes.Gender setGender = VoiceFontAttributes.Gender.None;

        // Male
        if (element.equalsIgnoreCase(MALE_STRING)) {
            setGender = VoiceFontAttributes.Gender.Male;

            // Female
        } else if (element.equalsIgnoreCase(FEMALE_STRING)) {
            setGender = VoiceFontAttributes.Gender.Female;

            // Unknown
        } else if (element.equalsIgnoreCase(UNKNOWN_STRING)) {
            setGender = VoiceFontAttributes.Gender.Unknown;
        }

        // match Gender Element
        if (setGender != VoiceFontAttributes.Gender.None) {
            if (voiceFontAttributes.getGender() == VoiceFontAttributes.Gender.None) {
                // no setting Gender.
                voiceFontAttributes.setGender(setGender);
            } else {
                // Gender Element is duplicated.
                return false;
            }
        }

        return true;
    }

    private boolean setAge(String element,
            VoiceFontAttributes voiceFontAttributes) {
        VoiceFontAttributes.Age setAge = VoiceFontAttributes.Age.None;

        // Child
        if (element.equalsIgnoreCase(CHILD_STRING)) {
            setAge = VoiceFontAttributes.Age.Child;

            // Adult
        } else if (element.equalsIgnoreCase(ADULT_STRING)) {
            setAge = VoiceFontAttributes.Age.Adult;

            // Elder
        } else if (element.equalsIgnoreCase(ELDER_STRING)) {
            setAge = VoiceFontAttributes.Age.Elder;
        }

        // match Age Element
        if (setAge != VoiceFontAttributes.Age.None) {
            if (voiceFontAttributes.getAge() == VoiceFontAttributes.Age.None) {
                // no setting Age.
                voiceFontAttributes.setAge(setAge);
            } else {
                // Age Element is duplicated.
                return false;
            }
        }
        return true;
    }

    private boolean setNative(String element,
            VoiceFontAttributes voiceFontAttributes) {
        VoiceFontAttributes.Native setNative = VoiceFontAttributes.Native.None;

        // Native
        if (element.equalsIgnoreCase(NATIVE_STRING)) {
            setNative = VoiceFontAttributes.Native.Native;

            // NonNative
        } else if (element.equalsIgnoreCase(NONNATIVE_STRING)) {
            setNative = VoiceFontAttributes.Native.NonNative;
        }

        // match Native Element
        if (setNative != VoiceFontAttributes.Native.None) {
            if (voiceFontAttributes.getNative() == VoiceFontAttributes.Native.None) {
                // no setting Native.
                voiceFontAttributes.setNative(setNative);
            } else {
                // Native Element is duplicated.
                return false;
            }
        }
        return true;
    }

    private void createVoiceFontTree(VoiceFontAttributes voiceFontAttributes)
            throws Exception {
        if (m_VoiceFontTree == null) {
            m_VoiceFontTree = new AgeNode();
        }

        m_VoiceFontTree.createTree(voiceFontAttributes);
    }

    private String searchVoiceFontTree(MCMLType inputMCML,
            InputUserProfileType inputUserProfile) throws Exception {

        VoiceFontAttributes voiceFontAttributes = parseVoiceType(inputMCML,
                inputUserProfile);
        if (voiceFontAttributes == null) {
            return null;
        }

        String voiceFontID = m_VoiceFontTree
                .searchVoiceFontID(voiceFontAttributes);

        return voiceFontID;
    }

    private VoiceFontAttributes parseVoiceType(MCMLType inputMCML,
            InputUserProfileType inputUserProfile) throws Exception {
        VoiceFontAttributes voiceFontAttributes = new VoiceFontAttributes();

        // parse Age.
        if (inputUserProfile.hasAge() && m_AgeSwitch == AgeSwitch.On) {
            VoiceFontAttributes.Age setAge = VoiceFontAttributes.Age.None;
            int age = inputUserProfile.getAge().getValue();

            if (age <= m_AdultThreshold) {
                // Child
                setAge = VoiceFontAttributes.Age.Child;
            } else if (age < m_ElderThreshold) {
                // Adult
                setAge = VoiceFontAttributes.Age.Adult;
            } else {
                // Elder
                setAge = VoiceFontAttributes.Age.Elder;
            }
            voiceFontAttributes.setAge(setAge);
        }

        // parse Gender.
        if (inputUserProfile.hasGender() && m_GenderSwitch == GenderSwitch.On) {
            VoiceFontAttributes.Gender setGender = VoiceFontAttributes.Gender.None;
            if (inputUserProfile.getGender().getValue()
                    .equalsIgnoreCase(MALE_STRING)) {
                // Male
                setGender = VoiceFontAttributes.Gender.Male;
            } else if (inputUserProfile.getGender().getValue()
                    .equalsIgnoreCase(FEMALE_STRING)) {
                // Female
                setGender = VoiceFontAttributes.Gender.Female;
            } else if (inputUserProfile.getGender().getValue()
                    .equalsIgnoreCase(UNKNOWN_STRING)) {
                // Unknown
                setGender = VoiceFontAttributes.Gender.Unknown;
            } else {
                // MCML/Server/Request/InputUserProfile@Gender is wrong data.
                ServerApLogger
                        .getInstance()
                        .writeWarning(
                                "MCML/Server/Request/InputUserProfile@Gender is wrong data.");
            }
            voiceFontAttributes.setGender(setGender);
        }

        // parse Native

        String isNative = getIsNative(inputMCML);
        if (!isNative.isEmpty() && m_NativeSwitch == NativeSwitch.On) {
            VoiceFontAttributes.Native setNative = VoiceFontAttributes.Native.None;
            if (isNative.equalsIgnoreCase(MCMLStatics.STRING_YES)) {
                // Native
                setNative = VoiceFontAttributes.Native.Native;
            } else if (isNative.equalsIgnoreCase(MCMLStatics.STRING_NO)) {
                // NonNative
                setNative = VoiceFontAttributes.Native.NonNative;
            } else {
                // MCML/Server/Request/InputUserProfile/InputModality/Speaking/Language@Fluency
                // is wrong data.
                ServerApLogger
                        .getInstance()
                        .writeWarning(
                                "MCML/Server/Request/InputUserProfile/InputModality/Speaking/Language@Fluency is wrong data.");
            }
            voiceFontAttributes.setNative(setNative);
        }

        if (voiceFontAttributes.isEmpty()) {
            // no Combination Elements.
            ServerApLogger
                    .getInstance()
                    .writeWarning(
                            "MCML/Server/Request/InputUserProfile(Age,Gender,Native) is empty.");
            return null;
        }

        return voiceFontAttributes;
    }

    private String getIsNative(MCMLType inputMCML) throws Exception {
        // get nativeVal
        String nativeVal = "";

        String language = XMLTypeTools.getInputAttachedBinaryID(inputMCML);

        if (XMLTypeTools.hasNative(inputMCML, language)) {
            nativeVal = MCMLStatics.STRING_YES;
        } else {
            nativeVal = MCMLStatics.STRING_NO;
        }
        return nativeVal;
    }

}
