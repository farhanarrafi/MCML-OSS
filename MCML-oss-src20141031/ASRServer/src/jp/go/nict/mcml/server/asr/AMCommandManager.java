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

package jp.go.nict.mcml.server.asr;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import jp.go.nict.mcml.serverap.common.ServerApLogger;
import jp.go.nict.mcml.servlet.MCMLStatics;
import jp.go.nict.mcml.servlet.XMLTypeTools;
import jp.go.nict.mcml.xml.types.InputUserProfileType;
import jp.go.nict.mcml.xml.types.MCMLType;

/**
 * AMCommandManager class.
 *
 */
public class AMCommandManager {
    // ------------------------------------------
    // private member constant(class field)
    // ------------------------------------------
    private static final String COMMENT_MARK = "#";
    private static final String EQUAL_MARK = "=";
    private static final String COMMAND_DELIMITER = "&";
    private static final String SPACE_MARK = " ";
    private static final String COMMA_MARK = ",";
    private static final String OPTION_MARK = "Option:";
    private static final String ID_MARK = "ID:";
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
    private static final String RECEIVER_STRING = "-receiver";

    // ------------------------------------------
    // private member Enumration(instance field)
    // ------------------------------------------
    /**
     * Gender switch type enumeration class.
     *
     */
    private enum GenderSwitch {
        /** ON */
        On,
        /** OFF */
        Off,
        /** None */
        None
    }

    /**
     * Age switch type enumeration class.
     *
     *
     */
    private enum AgeSwitch {
        /** ON */
        On,
        /** OFF */
        Off,
        /** None */
        None
    }

    /**
     * Native switch enumeration class.
     *
     *
     */
    private enum NativeSwitch {
        /** ON */
        On,
        /** OFF */
        Off,
        /** None */
        None
    }

    // ------------------------------------------
    // private member variables(instance field)
    // ------------------------------------------
    private HashMap<String, ArrayList<String>> m_CommandMap;
    private GenderSwitch m_GenderSwitch;
    private AgeSwitch m_AgeSwitch;
    private NativeSwitch m_NativeSwitch;
    private int m_AdultThreshold;
    private int m_ElderThreshold;
    private AgeNode m_CommandTree;
    private String m_AppliedAcousticModelID;
    private AMCommands m_AppliedAMCommands;

    // ------------------------------------------
    // public member function
    // ------------------------------------------
    /**
     * Default constructor
     */
    public AMCommandManager() {
        m_CommandMap = new HashMap<String, ArrayList<String>>();
        m_CommandMap.clear();
        m_GenderSwitch = GenderSwitch.None;
        m_AgeSwitch = AgeSwitch.None;
        m_NativeSwitch = NativeSwitch.None;
        m_AdultThreshold = -1;
        m_ElderThreshold = Integer.MAX_VALUE;
        m_CommandTree = null;
        clearAppliedAcousticModelID();
        clearAppliedAMCommands();
    }

    /**
     * getCommands
     *
     * @param inputMCML
     * @param inputUserProfile
     * @return commandList
     * @throws Exception
     */
    public ArrayList<String> getCommands(MCMLType inputMCML,
            InputUserProfileType inputUserProfile) throws Exception {
        ArrayList<String> commandList = null;

        // try to get ID Commands.
        if (inputUserProfile.hasID()) {
            String key;
            key = inputUserProfile.getID().toString();

            // check applied acoustic model ID is same as input ID.
            if (key.equalsIgnoreCase(m_AppliedAcousticModelID)) {
                // unnecessary change Acousitc Model.
                return commandList;
            }

            // get Acousitc Model Change Command.
            if (m_CommandMap.containsKey(key.toLowerCase())) {
                // save apply Acoustic Model ID.
                m_AppliedAcousticModelID = key;
                clearAppliedAMCommands();
                return m_CommandMap.get(m_AppliedAcousticModelID.toLowerCase());
            }
        }

        // try to get Combination Commands.
        commandList = searchCommandTree(inputMCML, inputUserProfile);
        if (commandList != null) {
            if (commandList.isEmpty()) {
                commandList = null;
            }
            return commandList;
        }

        // try to get Default Commands.
        if (m_CommandMap.containsKey(DEFAULT_STRING.toLowerCase())
                && !m_AppliedAcousticModelID.equalsIgnoreCase(DEFAULT_STRING)) {
            // save apply Acoustic Model ID.
            m_AppliedAcousticModelID = DEFAULT_STRING;
            clearAppliedAMCommands();
            commandList = m_CommandMap.get(m_AppliedAcousticModelID
                    .toLowerCase());
        }

        return commandList;
    }

    /**
     * readAMCommandTableFile
     *
     * @param fileName
     * @throws Exception
     */
    public void readAMCommandTableFile(String fileName) throws Exception {
        FileInputStream inputFile = null;
        InputStreamReader reader = null;
        BufferedReader fileReader = null;

        try {
            // no Acoustic Model Change Command Table.
            if (fileName == null || fileName.isEmpty()) {
                return;
            }
            // file open.
            inputFile = new FileInputStream(fileName);
            reader = new InputStreamReader(inputFile, "UTF-8");
            fileReader = new BufferedReader(reader);

            // read AcousticModel change Command Table.
            while (true) {
                String line = fileReader.readLine();
                if (line == null) {
                    // End of File
                    break;
                }
                if (!line.isEmpty()) {
                    parseAMCommand(line);
                }
            }
        } finally {
            if (fileReader != null) {
                fileReader.close();
            }
        }
    }

    /**
     * replaceAMCommand
     *
     * @param amCommand
     * @throws Exception
     */
    public void replaceAMCommand(String amCommand) throws Exception {
        parseAMCommand(amCommand);
    }

    // ------------------------------------------
    // private member function
    // ------------------------------------------
    private void parseAMCommand(String amCommand) throws Exception {
        // check commandLine or comment.
        String commandLines = checkAndDeleteCommentMark(amCommand);

        if (commandLines.isEmpty()) {
            // comment only.
            return;
        }

        // split by "=". ID:XX=XXXX to ID:XX XXXX or Default=XXXX to Default
        // XXXX.
        String[] elements = commandLines.split(EQUAL_MARK, 2);
        if (elements.length == 1) {
            // fail to split(worng format).
            ServerApLogger
                    .getInstance()
                    .writeWarning(
                            "fail to split by \"=\". ID:XX=XXXX => ID:XX XXXX or Default=XXXX => Default XXXX.");
            return;
        }

        // Option line?
        if (elements[0].startsWith(OPTION_MARK)) {
            // parse Option line.
            parseOption(elements);
            // nomal end.
            return;
        }

        // ID line?
        if (elements[0].startsWith(ID_MARK)) {
            // parse ID line.
            parseID(elements);
            // nomal end.
            return;
        }

        // Default line?
        if (elements[0].startsWith(DEFAULT_STRING)) {
            // parse Default line.
            parseID(elements);
            // nomal end.
            return;
        }

        // Combination(Gender,Age,Native) line.
        parseCombination(elements);
    }

    private String checkAndDeleteCommentMark(String amCommand) {
        String commandLines = "";

        // search Comment Mark("#").
        int commentIndex = amCommand.indexOf(COMMENT_MARK);
        if (commentIndex == -1) {
            // no comment=commandLines
            commandLines = amCommand;
        } else if (0 < commentIndex) {
            // comment delete(split by "XXXX=XXXX # XXXX").
            commandLines = amCommand.substring(0, commentIndex);
        }
        return commandLines;
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

    private void parseID(String[] idLine) {
        // delete "ID:".
        String id = idLine[0].replace(ID_MARK, "");

        // delete space.
        id = id.replace(SPACE_MARK, "");

        // no ID(parse Next Command Line).
        if (id.isEmpty()) {
            return;
        }

        // get Command
        ArrayList<String> commandList = getCommandList(idLine[1]);

        // no CommandList(parse Next Command Line).
        if (commandList.isEmpty()) {
            return;
        }

        // replace command
        m_CommandMap.put(id.toLowerCase(), commandList);
    }

    private ArrayList<String> getCommandList(String commandStr) {
        // get Command
        String[] commandElement = commandStr.split(COMMAND_DELIMITER);
        Iterator<String> commandIte = Arrays.asList(commandElement).iterator();
        ArrayList<String> commandList = new ArrayList<String>();
        while (commandIte.hasNext()) {
            //
            String command;
            String temp = commandIte.next();
            int i = temp.indexOf(RECEIVER_STRING);
            if (i > 0) {
                command = temp.substring(i);
            } else {
                command = temp;
            }
            if (!command.isEmpty()) {
                commandList.add(command);
            }
        }
        return commandList;
    }

    private void parseCombination(String[] elements) throws Exception {
        // split by "," to combination Element.
        String[] comb = elements[0].split(COMMA_MARK);

        AMCommands amCommands = new AMCommands();
        for (int i = 0; i < comb.length; i++) {
            // delete space.
            String param = comb[i].replace(SPACE_MARK, "");

            // Gender.
            if (!setGender(param, amCommands)) {
                // worng format
                ServerApLogger.getInstance().writeWarning(
                        "duplicate Gender parameter.");
                return;
            }

            // Age
            if (!setAge(param, amCommands)) {
                // worng format.
                ServerApLogger.getInstance().writeWarning(
                        "duplicate Age parameter.");
                return;
            }

            // Native
            if (!setNative(param, amCommands)) {
                // worng format.
                ServerApLogger.getInstance().writeWarning(
                        "duplicate Native parameter.");
                return;
            }
        }

        // is Combination Command?
        if (amCommands.isEmpty()) {
            // no Combination Command.
            return;
        }

        // delete Space.
        String commands;
        int recieverIndex = elements[1].indexOf(RECEIVER_STRING);
        if (recieverIndex > 0) {
            commands = elements[1].substring(recieverIndex);
        } else if (recieverIndex == 0) {
            commands = elements[1];
        } else {
            // worng format.
            ServerApLogger.getInstance().writeWarning("Command Line is worng.");
            return;
        }
        if (commands.isEmpty()) {
            // worng format(no Command Line).
            ServerApLogger.getInstance().writeWarning("Command Line is empty.");
            return;
        }

        // get Command List.
        ArrayList<String> commandList = getCommandList(commands);
        if (commandList.isEmpty()) {
            // worng format.
            ServerApLogger.getInstance().writeWarning("Command Line is empty.");
            return;
        }
        amCommands.setCommands(commandList);
        // create Combination Command Tree.
        createCommandTree(amCommands);
    }

    private boolean setGender(String element, AMCommands amCommands) {
        AMCommands.Gender setGender = AMCommands.Gender.None;

        // Male
        if (element.equalsIgnoreCase(MALE_STRING)) {
            setGender = AMCommands.Gender.Male;

            // Female
        } else if (element.equalsIgnoreCase(FEMALE_STRING)) {
            setGender = AMCommands.Gender.Female;

            // Unknown
        } else if (element.equalsIgnoreCase(UNKNOWN_STRING)) {
            setGender = AMCommands.Gender.Unknown;
        }

        // match Gender Element
        if (setGender != AMCommands.Gender.None) {
            if (amCommands.getGender() == AMCommands.Gender.None) {
                // no setting Gender.
                amCommands.setGender(setGender);
            } else {
                // Gender Element is duplicated.
                return false;
            }
        }

        return true;
    }

    private boolean setAge(String element, AMCommands amCommands) {
        AMCommands.Age setAge = AMCommands.Age.None;

        // Child
        if (element.equalsIgnoreCase(CHILD_STRING)) {
            setAge = AMCommands.Age.Child;

            // Adult
        } else if (element.equalsIgnoreCase(ADULT_STRING)) {
            setAge = AMCommands.Age.Adult;

            // Elder
        } else if (element.equalsIgnoreCase(ELDER_STRING)) {
            setAge = AMCommands.Age.Elder;
        }

        // match Age Element
        if (setAge != AMCommands.Age.None) {
            if (amCommands.getAge() == AMCommands.Age.None) {
                // no setting Age.
                amCommands.setAge(setAge);
            } else {
                // Age Element is duplicated.
                return false;
            }
        }
        return true;
    }

    private boolean setNative(String element, AMCommands amCommands) {
        AMCommands.Native setNative = AMCommands.Native.None;

        // Native
        if (element.equalsIgnoreCase(NATIVE_STRING)) {
            setNative = AMCommands.Native.Native;

            // NonNative
        } else if (element.equalsIgnoreCase(NONNATIVE_STRING)) {
            setNative = AMCommands.Native.NonNative;
        }

        // match Native Element
        if (setNative != AMCommands.Native.None) {
            if (amCommands.getNative() == AMCommands.Native.None) {
                // no setting Native.
                amCommands.setNative(setNative);
            } else {
                // Native Element is duplicated.
                return false;
            }
        }
        return true;
    }

    private void createCommandTree(AMCommands amcommands) throws Exception {
        if (m_CommandTree == null) {
            m_CommandTree = new AgeNode();
        }

        m_CommandTree.createTree(amcommands);
    }

    private ArrayList<String> searchCommandTree(MCMLType inputMCML,
            InputUserProfileType inputUserProfile) throws Exception {

        AMCommands amCommands = parseVoiceType(inputMCML, inputUserProfile);
        if (amCommands == null) {
            return null;
        } else if (amCommands.equals(m_AppliedAMCommands)) {
            // return Empty List.
            return new ArrayList<String>();
        }

        ArrayList<String> commands = m_CommandTree.searchAMCommands(amCommands);
        if (commands != null) {
            m_AppliedAMCommands = null;
            // save Acoustic Model Attributes.
            m_AppliedAMCommands = new AMCommands(amCommands);
            clearAppliedAcousticModelID();
        }

        return commands;
    }

    private AMCommands parseVoiceType(MCMLType inputMCML,
            InputUserProfileType inputUserProfile) throws Exception {
        AMCommands amCommands = new AMCommands();

        // parse Age.
        if (inputUserProfile.hasAge() && m_AgeSwitch == AgeSwitch.On) {
            AMCommands.Age setAge = AMCommands.Age.None;
            int age = inputUserProfile.getAge().getValue();

            if (age <= m_AdultThreshold) {
                // Child
                setAge = AMCommands.Age.Child;
            } else if (age < m_ElderThreshold) {
                // Adult
                setAge = AMCommands.Age.Adult;
            } else {
                // Elder
                setAge = AMCommands.Age.Elder;
            }
            amCommands.setAge(setAge);
        }

        // parse Gender.
        if (inputUserProfile.hasGender() && m_GenderSwitch == GenderSwitch.On) {
            AMCommands.Gender setGender = AMCommands.Gender.None;
            if (inputUserProfile.getGender().getValue()
                    .equalsIgnoreCase(MALE_STRING)) {
                // Male
                setGender = AMCommands.Gender.Male;
            } else if (inputUserProfile.getGender().getValue()
                    .equalsIgnoreCase(FEMALE_STRING)) {
                // Female
                setGender = AMCommands.Gender.Female;
            } else if (inputUserProfile.getGender().getValue()
                    .equalsIgnoreCase(UNKNOWN_STRING)) {
                // Unknown
                setGender = AMCommands.Gender.Unknown;
            } else {
                // MCML/Server/Request/InputUserProfile@Gender is wrong data.
                ServerApLogger
                        .getInstance()
                        .writeWarning(
                                "MCML/Server/Request/InputUserProfile@Gender is wrong data.");
            }
            amCommands.setGender(setGender);
        }

        String isNative = getIsNative(inputMCML);
        if (!isNative.isEmpty() && m_NativeSwitch == NativeSwitch.On) {
            AMCommands.Native setNative = AMCommands.Native.None;
            if (isNative.equalsIgnoreCase(MCMLStatics.STRING_YES)) {
                // Native
                setNative = AMCommands.Native.Native;
            } else if (isNative.equalsIgnoreCase(MCMLStatics.STRING_NO)) {
                // NonNative
                setNative = AMCommands.Native.NonNative;
            } else {
                // MCML/Server/Request/InputUserProfile/InputModality/Speaking/Language@Fluency
                // is wrong data.
                ServerApLogger
                        .getInstance()
                        .writeWarning(
                                "MCML/Server/Request/InputUserProfile/InputModality/Speaking/Language@Fluency is wrong data.");
            }
            amCommands.setNative(setNative);
        }

        if (amCommands.isEmpty()) {
            // no Combination Elements.
            ServerApLogger.getInstance().writeWarning("Command Line is empty.");
            return null;
        }

        return amCommands;
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

    private void clearAppliedAcousticModelID() {
        m_AppliedAcousticModelID = "";
    }

    private void clearAppliedAMCommands() {
        m_AppliedAMCommands = null;
    }
}
