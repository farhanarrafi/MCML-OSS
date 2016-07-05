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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/**
 * MTLanguageInfoManager class.
 *
 */
public class MTLanguageInfoManager {

    // ------------------------------------------
    // private member variable
    // ------------------------------------------
    private HashMap<String, MTLanguageInfo> m_LanguageInfoMap;
    private ArrayList<String> m_LanguageList;
    private Properties m_AllDirectionCommandList;

    // ------------------------------------------
    // Constructor
    // ------------------------------------------
    /**
     * Default constructor
     */
    public MTLanguageInfoManager() {

        m_LanguageList = new ArrayList<String>();
        m_LanguageInfoMap = new HashMap<String, MTLanguageInfo>();
        m_AllDirectionCommandList = new Properties();
    }

    /**
     * load DirectionCommandListFile and get all command from file
     *
     * @param fileName
     * @throws Exception
     */
    public void setDirectionCommandListFile(String fileName) throws Exception {

        try {
            // check File exists.
            File directionCommandListFile = new File(fileName);

            if (!directionCommandListFile.exists()
                    || !directionCommandListFile.isFile()
                    || !directionCommandListFile.canRead()) {

                System.out.println("not found direction command list file");
                throw new Exception("not found direction command list file");
            }

            FileInputStream inputFile = new FileInputStream(
                    directionCommandListFile);
            InputStreamReader reader = new InputStreamReader(inputFile, "UTF-8");
            BufferedReader fileReader = new BufferedReader(reader);

            // load directionCommandListFile
            m_AllDirectionCommandList.load(fileReader);

            // close stream
            inputFile.close();
            reader.close();
            fileReader.close();

        } catch (IOException e) {

            // failed to read file
            System.out.println("failed to read direction command list file");
            throw new Exception("failed to read direction command list file");
        }
        return;
    }

    /**
     * check is valid language
     *
     * @param language
     * @return boolean
     */
    public boolean isValidLanguage(String language) {

        if (m_LanguageList.contains(language)) {

            return true;
        }

        return false;
    }

    /**
     * get language list
     *
     * @return m_LanguageList
     */
    public ArrayList<String> getLanguageList() {

        return m_LanguageList;
    }

    /**
     * set all language to list
     *
     * @param language
     */
    public void setLanguageList(String language) {

        // make language string lower

        m_LanguageList.add(language);

        return;
    }

    /**
     * get direction command map of a sourcelanguage
     *
     * @param sourceLanguage
     * @return directionCommandMap
     */
    public HashMap<String, String> getDirectionCommandMap(String sourceLanguage) {

        HashMap<String, String> directionCommandMap = new HashMap<String, String>();

        // make sourceLanguage string lower

        for (int i = 0; i < m_LanguageList.size(); i++) {

            String directionKey = "";
            String targetLanguage = "";
            String directionCommand = "";

            if (!m_LanguageList.get(i).equalsIgnoreCase(sourceLanguage)) {

                // get direction command from commandlistfile
                targetLanguage = m_LanguageList.get(i);
                directionKey = sourceLanguage + "," + targetLanguage;
                directionCommand = m_AllDirectionCommandList
                        .getProperty(directionKey);

                if (directionCommand == null || directionCommand.isEmpty()) {

                    directionCommand = "";
                }

                // set diretion command to map
                directionCommandMap.put(targetLanguage, directionCommand);
            }
        }

        return directionCommandMap;
    }

    /**
     * set language information to LanguageInfo
     *
     * @param language
     * @param delimiter
     * @param stringcode
     * @param textfilterfile
     * @param directionCommandMap
     */
    public void setLanguageInfo(String language, String delimiter,
            String stringcode, String textfilterfile,
            HashMap<String, String> directionCommandMap) {

        MTLanguageInfo langinfo = new MTLanguageInfo();

        // make string lower

        // set delimiter
        langinfo.setDelimiter(delimiter);

        // set stringcode
        langinfo.setStringcode(stringcode);

        // set textfilterfile
        langinfo.setTextfilterfile(textfilterfile);

        // set direction command map
        langinfo.setDirectionCommandMap(directionCommandMap);

        // set all information to languageInfoMAp
        m_LanguageInfoMap.put(language, langinfo);
    }

    /**
     * get information of a language
     *
     * @param language
     * @return languageInfo
     */
    public MTLanguageInfo getLanguageInfo(String language) {

        // make string lower

        MTLanguageInfo languageInfo = m_LanguageInfoMap.get(language);

        return languageInfo;
    }
}
