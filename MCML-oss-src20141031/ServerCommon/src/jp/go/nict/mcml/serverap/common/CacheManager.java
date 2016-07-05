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

//-------------------------------------------------------------------
//Ver.3.0
//2011/12/06
//-------------------------------------------------------------------

package jp.go.nict.mcml.serverap.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * CacheManager class.
 * 
 */
public class CacheManager {
    // ------------------------------------------
    // private member constants(class field)
    // ------------------------------------------
    private static final String CACHEOUTPUTDIRECTRYNAME = "cache";
    private static final String STRINGCODE_UTF8 = "UTF-8";

    // ------------------------------------------
    // private member variable
    // ------------------------------------------
    private ArrayList<CacheFileInfo> m_CacheFileList;
    private HashMap<String, CacheFileReader> m_CacheFileSearchMap;
    private File m_CacheDataListOutputDirectry;
    private File m_CacheFileOutputDirectry;
    private int m_MaxCacheCount;
    private int m_DeleteCount;
    private String m_Language;
    private int m_FileID;
    private String m_ChacheDataListFileName;

    // ------------------------------------------
    // public member functions
    // ------------------------------------------
    /** Default constructor */
    public CacheManager() {
        // create CacheFile List
        m_CacheFileList = new ArrayList<CacheFileInfo>();

        // create CacheFile Search Map
        m_CacheFileSearchMap = new HashMap<String, CacheFileReader>();

        m_FileID = 0;
    }

    /**
     * Initialization
     * 
     * @param baseDirectryName
     * @param language
     * @param cacheDataListFileName
     * @param maxCacheCount
     * @param deleteCount
     * @return boolean
     */
    public boolean initialize(String baseDirectryName, String language,
            String cacheDataListFileName, int maxCacheCount, int deleteCount) {
        synchronized (m_CacheFileSearchMap) {
            // save MaxCacheCount.
            m_MaxCacheCount = maxCacheCount;

            // save DeletCount(The number of the cache files to delete).
            if (m_MaxCacheCount < deleteCount) {
                ServerApLogger.getInstance().writeWarning(
                        "DeletCount is over MaxCacheCount.");
                m_DeleteCount = maxCacheCount;
            } else {
                m_DeleteCount = deleteCount;
            }

            // baseDirectry is not specified
            if (baseDirectryName == null || baseDirectryName.isEmpty()) {
                // use current directry as baseDirectry
                baseDirectryName = "./";

                // Is setting BaseDirectryName?
            } else { // if(!baseDirectryName.isEmpty()){
                     // create BaseDirectry.
                if (createDirectory(baseDirectryName, "") == null) {

                    // creating BaseDirectry failed.
                    ServerApLogger.getInstance().writeWarning(
                            "creating BaseDirectry failed.");
                    return false;
                }
            }

            // create ChacheDataListOutputDirectry.
            m_CacheDataListOutputDirectry = createDirectory(baseDirectryName,
                    language);

            if (m_CacheDataListOutputDirectry == null) {
                // creating ChacheDataListOutputDirectry failed.
                ServerApLogger.getInstance().writeWarning(
                        "creating ChacheDataListOutputDirectry failed.");
                return false;
            }

            // create CacheFileOutputDirectry.
            m_CacheFileOutputDirectry = createDirectory(
                    m_CacheDataListOutputDirectry.getAbsolutePath(),
                    CACHEOUTPUTDIRECTRYNAME);
            if (m_CacheFileOutputDirectry == null) {
                // creating CacheFileOutputDirectry failed.
                ServerApLogger.getInstance().writeWarning(
                        "creating CacheFileOutputDirectry failed.");
                return false;
            }

            // can read cacheDataListFile?
            File chacheDataListFile = new File(m_CacheDataListOutputDirectry,
                    cacheDataListFileName);
            if (chacheDataListFile.canRead()) {
                // read CacheDataListFile.
                if (readCacheDataListFile(chacheDataListFile)) {
                    // reset FileID.
                    if (!resetFileID()) {
                        return false;
                    }
                }
            } else {
                // delete unnecessarily files in CacheDataListOutputDirectry
                clearCacheDataListOutputDirectry();
            }

            // save Language
            m_Language = language;

            // save CacheDataListFileName.
            m_ChacheDataListFileName = cacheDataListFileName;
        }
        return true;
    }

    /**
     * Gets CacheData.
     * 
     * @param inputString
     * @return byte[]
     */
    public byte[] getCacheData(String inputString) {
        synchronized (m_CacheFileSearchMap) {
            if (m_CacheFileSearchMap.isEmpty()) {
                return null;
            }

            // search CacheData.
            if (!m_CacheFileSearchMap.containsKey(inputString)) {
                return null;
            }

            // get CacheData.
            byte[] cacheData = null;
            try {
                cacheData = m_CacheFileSearchMap.get(inputString)
                        .getCacheData();
            } catch (IOException e) {
                ServerApLogger.getInstance().writeError(
                        "getCacheData() failed.");
                cacheData = null;
            }
            return cacheData;
        }
    }

    /**
     * Sets CacheData.
     * 
     * @param inputString
     * @param synthesizedData
     * @return boolean
     */
    public boolean setCacheData(String inputString, byte[] synthesizedData) {
        synchronized (m_CacheFileSearchMap) {
            try {
                if (m_CacheFileList.size() >= m_MaxCacheCount) {
                    // delete Cache Files.
                    deleteCacheFiles();
                }

                // create CacheFile.
                createCacheFile(inputString, synthesizedData);
            } catch (IOException e) {
                ServerApLogger.getInstance().writeError(
                        "setCacheData() failed.");
                ServerApLogger.getInstance().writeError(e.getMessage());
                return false;
            }
        }
        return true;
    }

    /**
     * writeCacheDataListFile
     */
    public void writeCacheDataListFile() {
        synchronized (m_CacheFileSearchMap) {
            try {
                // Is set CacheDataListFileName?
                if (m_ChacheDataListFileName == null
                        || m_ChacheDataListFileName.isEmpty()) {
                    return;
                }

                // Is empty CacheFileList?
                if (m_CacheFileList == null || m_CacheFileList.isEmpty()) {
                    return;
                }

                // create CacheDataListFileName(Full Path).
                File cacheDataListFile = new File(
                        m_CacheDataListOutputDirectry, m_ChacheDataListFileName);

                // CacheDataListFile open.
                FileOutputStream cacheDataListWriter;
                cacheDataListWriter = new FileOutputStream(cacheDataListFile);

                // write thisTimeMaxCacheCount
                String thisTimeMaxCacheCount = String.valueOf(m_MaxCacheCount)
                        + "\n";
                cacheDataListWriter.write(thisTimeMaxCacheCount
                        .getBytes("UTF-8"));

                // write CacheDataList.
                int cacheCount = m_CacheFileList.size();
                for (int i = 0; i < cacheCount; i++) {
                    CacheFileInfo cacheFileInfo = m_CacheFileList.get(i);

                    // create Output String.
                    String outputString = cacheFileInfo.getInputString() + ":"
                            + cacheFileInfo.getCacheFileName() + "\n";

                    // write.
                    cacheDataListWriter.write(outputString.getBytes("UTF-8"));
                }
            } catch (FileNotFoundException e) {
                ServerApLogger.getInstance().writeWarning(
                        "writeCacheDataListFile() file not found.");
                ServerApLogger.getInstance().writeWarning(e.getMessage());
            } catch (UnsupportedEncodingException e) {
                ServerApLogger.getInstance().writeWarning(
                        "writeCacheDataListFile() unsupported Encoding.");
                ServerApLogger.getInstance().writeWarning(e.getMessage());
            } catch (IOException e) {
                ServerApLogger.getInstance().writeWarning(e.getMessage());
            } finally {
                // clear CacheFileLists.
                m_CacheFileList.clear();
                m_CacheFileSearchMap.clear();
            }
        }
    }

    // ------------------------------------------
    // private member functions
    // ------------------------------------------
    private File createDirectory(String parentDirectry, String childDirectry) {
        // exists Directry?
        File directry = new File(parentDirectry, childDirectry);
        if (!directry.exists()) {
            // not exists.
            if (!directry.mkdirs()) {
                // creating Directry failed.
                return null;
            }
        }

        return directry;
    }

    private boolean readCacheDataListFile(File chacheDataListFile) {
        FileInputStream inputFile = null;
        InputStreamReader reader = null;
        BufferedReader fileReader = null;

        try {
            // file open.
            inputFile = new FileInputStream(chacheDataListFile);
            reader = new InputStreamReader(inputFile, STRINGCODE_UTF8);
            fileReader = new BufferedReader(reader);

            // read and parse CacheDataListFile's first line.
            if (!checkLastTimeMaxCacheCount(fileReader)) {
                // delete CacheFileList.
                ServerApLogger.getInstance().writeWarning(
                        "ChacheDataList format is invalid");
                fileReader.close();
                File chacheDataList = new File(
                        chacheDataListFile.getAbsolutePath());
                chacheDataList.delete();
                return false;
            }

            // read and parse CacheDataListFile.
            while (true) {
                String line = fileReader.readLine();
                if (line == null) {
                    // End of File
                    break;
                }
                if (!line.isEmpty()) {
                    if (!parseCacheDataList(line)) {
                        // delete CacheFiles and CacheFileList.
                        ServerApLogger.getInstance().writeWarning(
                                "ChacheFileName format is invalid.");
                        clearLastTimeCacheData();
                        fileReader.close();
                        File chacheDataList = new File(
                                chacheDataListFile.getAbsolutePath());
                        chacheDataList.delete();

                        // clear CacheDataLists.
                        m_CacheFileList.clear();
                        m_CacheFileSearchMap.clear();
                        return false;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            ServerApLogger.getInstance().writeWarning(
                    "readCacheDataListFile() file not found.");
            ServerApLogger.getInstance().writeWarning(e.getMessage());
            return false;
        } catch (UnsupportedEncodingException e) {
            ServerApLogger.getInstance().writeWarning(
                    "readCacheDataListFile() unsupported Encoding.");
            ServerApLogger.getInstance().writeWarning(e.getMessage());
            return false;
        } catch (IOException e) {
            ServerApLogger.getInstance().writeWarning(e.getMessage());
            return false;
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    ServerApLogger.getInstance().writeWarning(
                            "Failed to ChacheDataListFile Close.");
                    ServerApLogger.getInstance().writeWarning(e.getMessage());
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkLastTimeMaxCacheCount(BufferedReader reader) {
        try {
            // read FirstLine of ChacheDataListFile(Last Time MaxCacheCount).
            String firstLine = reader.readLine();

            // parse MaxCacheCount.
            int lastTimeMaxCacheCount = Integer.parseInt(firstLine);
            if (lastTimeMaxCacheCount != m_MaxCacheCount) {
                // lastTimeMaxCacheCount is not equals thisTimeMaxCacheCount
                clearLastTimeCacheData();
                return false;
            }
        } catch (NumberFormatException e) {
            // lastTimeMaxCacheCount is not written in firstLine
            clearLastTimeCacheData();
            return false;
        } catch (IOException e) {
            // can not read chacheDataListFile
            clearLastTimeCacheData();
            return false;
        }
        // normal end
        return true;
    }

    private void clearLastTimeCacheData() {
        // get CacheFilesName.
        String[] fileList = m_CacheFileOutputDirectry.list();

        // delete all cacheFile
        for (int i = 0; i < fileList.length; i++) {
            File cacheFile = new File(m_CacheFileOutputDirectry, fileList[i]);
            cacheFile.delete();
        }
    }

    private void clearCacheDataListOutputDirectry() {

        // get all files name in CacheDataListOutputDirectry
        String[] fileList = m_CacheDataListOutputDirectry.list();

        // clear directry
        for (int i = 0; i < fileList.length; i++) {

            File file = new File(m_CacheDataListOutputDirectry, fileList[i]);

            if (file.isDirectory()) {

                clearLastTimeCacheData();

            } else {

                file.delete();
            }
        }
    }

    private boolean parseCacheDataList(String line) {
        try {
            // split ReadLine.
            String[] readWords = line.split(":");
            if (readWords.length != 2) {
                return false;
            }
            if (readWords[0].isEmpty() || readWords[1].isEmpty()) {
                // InputString or CacheFileName is Empty.
                return false;
            }

            // convert FileName to LowerCase.
            String cacheFileName = readWords[1].toLowerCase();

            // check CacheFileName.
            if (!checkCacheFileName(cacheFileName)) {
                return false;
            }

            // exists CacheFile?
            File cacheFile = new File(m_CacheFileOutputDirectry, cacheFileName);
            if (!cacheFile.canRead()) {
                return false;
            }

            // update CacheFileList and CacheFileSearchMap.
            updateCacheDataLists(readWords[0], cacheFile);
        } catch (FileNotFoundException e) {
            // File Not Found.
            ServerApLogger.getInstance().writeWarning(
                    "parseCacheDataList() file not found.");
            ServerApLogger.getInstance().writeWarning(e.getMessage());
            return false;
        }
        return true;
    }

    private boolean checkCacheFileName(String cacheFileName) {
        try {
            // split CacheFileName("xx_00000.bin"=>xx,00000.bin).
            String[] temp1 = cacheFileName.split("_");

            // split CacheFileName("00000.bin"=>00000,bin).
            String[] temp2 = temp1[temp1.length - 1].split("\\.");

            // Check FileID.
            int fileID = Integer.parseInt(temp2[0]);
            if (fileID < 0 || m_MaxCacheCount <= fileID) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private boolean resetFileID() {
        try {
            // check CacheFileList.
            if (m_CacheFileList.isEmpty()) {
                ServerApLogger.getInstance().writeWarning(
                        "CacheFileList is empty.");
                return true;
            }

            // get last CacheFileName.
            String cacheFileName = m_CacheFileList.get(
                    m_CacheFileList.size() - 1).getCacheFileName();

            // split CacheFileName("xx_00000.bin"=>xx,00000.bin).
            String[] temp1 = cacheFileName.split("_");

            // split CacheFileName("00000.bin"=>00000,bin).
            String[] temp2 = temp1[temp1.length - 1].split("\\.");

            // get FileID.
            m_FileID = Integer.parseInt(temp2[0]);

            // check FileID to out of range.
            if (m_FileID < 0 || m_MaxCacheCount <= m_FileID) {
                ServerApLogger.getInstance().writeError(
                        "FileID is wrong number.");
                return false;
            }
            m_FileID++;
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private void deleteCacheFiles() throws IOException {
        for (int i = 0; i < m_DeleteCount; i++) {
            // close CacheFileStream.
            CacheFileInfo cacheFileInfo = m_CacheFileList.get(0);

            // remove CacheFileReader to CacheFileSearchMap.
            m_CacheFileSearchMap.remove(cacheFileInfo.getInputString());

            // delete CacheFile.
            File cacheFile = new File(m_CacheFileOutputDirectry,
                    cacheFileInfo.getCacheFileName());
            if (cacheFile.delete()) {
                ServerApLogger.getInstance().writeError(
                        "no CacheFile:" + cacheFileInfo.getCacheFileName());
            }

            // remove CacheFileReader to CacheFileList.
            m_CacheFileList.remove(0);
        }

        // reset FileID.
        if (m_FileID >= m_MaxCacheCount) {
            // clear FileID.
            m_FileID = 0;
        }
    }

    private void createCacheFile(String inputString, byte[] synthesizedData)
            throws IOException {
        // create CacheFileName.
        // It's bad for zh-TW, en-GB, pt-BR.
        // String cacheFileName = m_Language.toLowerCase() + "_" +
        // String.format("%05d",m_FileID) + ".bin";
        String cacheFileName = m_Language + "_"
                + String.format("%05d", m_FileID) + ".bin";

        // CacheFile open.
        File cacheFile = new File(m_CacheFileOutputDirectry, cacheFileName);
        FileOutputStream fileOutStream = new FileOutputStream(cacheFile);

        // write CacheFile.
        fileOutStream.write(synthesizedData);
        fileOutStream.flush();

        // CacheFile close.
        fileOutStream.close();

        // increment FileID
        m_FileID++;

        // update CacheFileList and CacheFileSearchMap.
        updateCacheDataLists(inputString, cacheFile);
    }

    private void updateCacheDataLists(String inputString, File cacheFile)
            throws FileNotFoundException {
        // create Instance for CacheFileInfomation class.
        CacheFileInfo cacheFileInfo = new CacheFileInfo(inputString,
                cacheFile.getName());

        // set Infomation to create CacheFileDataList to CacheFileList.
        m_CacheFileList.add(cacheFileInfo);

        // create CacheFileReader.
        CacheFileReader cacheFileReader = new CacheFileReader(
                (int) cacheFile.length(), cacheFile);

        // set Infomation to read CacheFile to CacheFileSearchMap.
        m_CacheFileSearchMap.put(inputString, cacheFileReader);
    }
}
