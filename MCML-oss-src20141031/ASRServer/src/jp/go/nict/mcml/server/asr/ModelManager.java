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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import jp.go.nict.mcml.serverap.common.DataQueue;
import jp.go.nict.mcml.serverap.common.FrameData;
import jp.go.nict.mcml.serverap.common.ServerApLogger;
import jp.go.nict.mcml.serverap.common.ServerApThread;
import jp.go.nict.mcml.serverap.common.ServerSocketForModel;
import jp.go.nict.mcml.servlet.MCMLStatics;

/**
 * ModelManagerクラスです。
 *
 */
public class ModelManager extends ServerApThread {

    /**
     * CommandLineInfoインナークラスです。
     *
     */
    private class CommandLineInfo {
        // ------------------------------------------
        // private member variable(instance field)
        // ------------------------------------------
        private int m_LineNo;
        private String m_CommandLine;

        // ------------------------------------------
        // public member functions
        // ------------------------------------------
        public CommandLineInfo(String commandLine, int lineNo) {
            m_LineNo = lineNo;
            m_CommandLine = commandLine;
        }

        public String getCommandLine() {
            return m_CommandLine;
        }

        public int getLineNo() {
            return m_LineNo;
        }
    }

    // ------------------------------------------
    // private member constant(class field)
    // ------------------------------------------
    private static final String EQUAL_MARK = "=";
    private static final String SPACE_MARK = " ";
    private static final String ID_MARK = "ID:";
    private static final String RECEIVER_STR = "-receiver=";
    private static final String OPTIONS_STR = "-command=options";
    private static final String AMNAME_STR = "amname=";
    private static final String ACTIVE_STR = "active_model=all";
    private static final String COMMAND_DELIMITER = "&";
    private static final String COMMENT_MARK = "#";

    // ------------------------------------------
    // private member variable(instance field)
    // ------------------------------------------
    private ServerSocketForModel m_ServerSocket;
    private int m_Port;
    private DataQueue<FrameData<byte[]>> m_FrameDataQueue;
    private ArrayList<ModelChanger> m_ModelChangerList;
    private File m_ModelOutputDirectry;
    private String m_RpcCommandReceiver;
    private ArrayList<String> m_CommandLineList;
    private HashMap<String, CommandLineInfo> m_IdAndCommandMap;
    private int m_LastLineNo;
    private String m_AMFileName;

    // ------------------------------------------
    // public member functions
    // ------------------------------------------
    /**
     * デフォルトコンストラクタ
     */
    public ModelManager() {
        super("ModelManager");
        m_ServerSocket = null;
        m_Port = -1;
        m_FrameDataQueue = null;
        m_ModelChangerList = new ArrayList<ModelChanger>();
        m_CommandLineList = new ArrayList<String>();
        m_IdAndCommandMap = new HashMap<String, CommandLineInfo>();
        m_LastLineNo = 0;
        m_AMFileName = "";
    }

    /**
     * 初期化
     *
     * @param port
     * @param modelOutPath
     * @param rpcCommandReceiver
     * @param lmCommandFileName
     * @param amCommandFileName
     * @return boolean
     */
    public boolean initialize(int port, String modelOutPath,
            String rpcCommandReceiver, String lmCommandFileName,
            String amCommandFileName) {
        m_Port = port;
        m_ModelOutputDirectry = new File(modelOutPath);

        // create ModelOutputDirectry.
        if (!m_ModelOutputDirectry.exists()) {
            m_ModelOutputDirectry.mkdirs();
        }

        // read AMCommandFile.
        try {
            readAMCommandFile(amCommandFileName);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        m_AMFileName = amCommandFileName;

        m_RpcCommandReceiver = rpcCommandReceiver;

        return true;
    }

    /**
     * addModelChanger
     *
     * @param modelChanger
     */
    public synchronized void addModelChanger(ModelChanger modelChanger) {
        if (modelChanger != null) {
            m_ModelChangerList.add(modelChanger);
        } else {
            System.out.println("addModelChanger() is failed.");
            ServerApLogger.getInstance().writeWarning(
                    "addModelChanger() is failed.");
        }
    }

    // ------------------------------------------
    // protected member functions
    // ------------------------------------------
    @Override
    protected void processMain() throws Exception {
        writeLog("ModelManager::processMain() start.");
        // create DataQueue(for receive FrameData).
        m_FrameDataQueue = new DataQueue<FrameData<byte[]>>();

        // create ServerSocket and Thread start.
        m_ServerSocket = new ServerSocketForModel(m_FrameDataQueue);
        m_ServerSocket.startAcceptation(m_Port);

        while (true) {
            // wait for Data receive.
            FrameData<byte[]> frameData = m_FrameDataQueue.takeData();
            if (frameData == null) {
                break;
            }

            // regist Model.
            registerModels(frameData);

            // complete.
            frameData.doNotification();
        }
        writeLog("ModelManager::processMain() end.");
        return;
    }

    @Override
    protected void processTermination() throws Exception {
        writeLog("ModelManager::processTermination() start.");
        m_FrameDataQueue.putData(null);
        writeAMCommandFile();
        m_ServerSocket.terminate();
        writeLog("ModelManager::processTermination() end.");
    }

    // ------------------------------------------
    // private member functions
    // ------------------------------------------
    private void readAMCommandFile(String fileName) throws IOException {
        FileInputStream inputFile = null;
        InputStreamReader reader = null;
        BufferedReader fileReader = null;

        try {
            // no Acoustic Model Change Command Table.
            if (fileName == null || fileName.isEmpty()) {
                System.out
                        .println("don't specify Acoustic Model Change Command Table");
                return;
            }

            // check File exists.
            File amCommandFile = new File(fileName);
            if (!amCommandFile.exists() || !amCommandFile.isFile()
                    || !amCommandFile.canRead() || !amCommandFile.canWrite()) {
                System.out
                        .println("not exists Acoustic Model Change Command Table File");
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
                    parseLine(line, m_LastLineNo);
                }
                m_CommandLineList.add(line);
                m_LastLineNo++;
            }
        } finally {
            if (fileReader != null) {
                fileReader.close();
            }
        }
    }

    private void writeAMCommandFile() throws IOException {
        FileOutputStream outputFile = null;
        OutputStreamWriter writer = null;
        BufferedWriter fileWriter = null;

        try {
            // no Acoustic Model Change Command Table.
            if (m_AMFileName == null || m_AMFileName.isEmpty()) {
                return;
            }
            // file open.
            outputFile = new FileOutputStream(m_AMFileName);
            writer = new OutputStreamWriter(outputFile, "UTF-8");
            fileWriter = new BufferedWriter(writer);

            // read AcousticModel change Command Table.
            for (int i = 0; i < m_CommandLineList.size(); i++) {
                fileWriter.write(m_CommandLineList.get(i));
                fileWriter.newLine();
            }
        } finally {
            if (fileWriter != null) {
                fileWriter.close();
            }
        }
    }

    private void parseLine(String line, int lineNo) {
        // ID line?
        if (!line.startsWith(ID_MARK)) {
            return;
        }

        String[] elements = line.split(EQUAL_MARK, 2);
        if (elements.length == 1) {
            // fail to split(worng format).
            ServerApLogger.getInstance().writeWarning(
                    "fail to split by \"=\". ID:XX=XXXX => ID:XX XXXX.");
            return;
        }
        // delete "ID:".
        String id = elements[0].replace(ID_MARK, "");

        // delete space.
        id = id.replace(SPACE_MARK, "");

        // no ID(parse Next Command Line).
        if (id.isEmpty()) {
            return;
        }

        // create CommandLineInfo.
        CommandLineInfo commandLineInfo = new CommandLineInfo(line, lineNo);

        // set id and CommandLineInfo.
        m_IdAndCommandMap.put(id.toLowerCase(), commandLineInfo);
    }

    private void registerModels(FrameData<byte[]> frameData) {
        // parse FrameData.
        ArrayList<String> modelNameList = new ArrayList<String>();
        ArrayList<byte[]> modelList = new ArrayList<byte[]>();
        String id = parseFrameData(frameData, modelNameList, modelList);
        if (id.isEmpty()) {
            return;
        }

        // save Models.
        if (!saveModels(modelNameList, modelList)) {
            return;
        }

        // create Command Line.
        String commandLine = createCommandLine(id, modelNameList);

        // regist Models.
        try {
            for (int i = 0; i < m_ModelChangerList.size(); i++) {
                if (m_ModelChangerList.get(i) != null) {
                    m_ModelChangerList.get(i).replaceAMCommand(commandLine);
                }
            }
        } catch (Exception e) {
            String message = "registerModels() is failed. :" + e.getMessage();
            System.out.println(message);
            e.printStackTrace();
            ServerApLogger.getInstance().writeWarning(message);
            ServerApLogger.getInstance().writeException(e);
            return;
        }

        // save Command Line.
        saveCommand(id, commandLine);

        return;
    }

    private String parseFrameData(FrameData<byte[]> frameData,
            ArrayList<String> modelNameList, ArrayList<byte[]> modelList) {
        String id = "";
        try {
            // parse ModelName part.
            String modelName = new String(frameData.getFrameData(),
                    MCMLStatics.CHARSET_NAME);
            String[] modelNames = modelName.split("\n");

            // set ModelNames to ModelNameList.
            for (int i = 0; i < modelNames.length; i++) {
                if (!modelNames[i].isEmpty()) {
                    modelNameList.add(modelNames[i]);
                }
            }
            // get id.
            String[] elements = modelNameList.get(0).split("_", 2);
            if (elements.length != 2) {
                return id;
            }
            id = elements[0];

            // parse Model part.
            ArrayList<byte[]> binaryList = frameData.getFrameDataList();
            // skip for first frame(attention ! first part is ModelName).
            for (int j = 1; j < binaryList.size(); j++) {
                modelList.add(binaryList.get(j));
            }
        } catch (UnsupportedEncodingException e) {
            String message = "parseFrameData() is failed. :" + e.getMessage();
            System.out.println(message);
            e.printStackTrace();
            ServerApLogger.getInstance().writeWarning(message);
            ServerApLogger.getInstance().writeException(e);
        }
        return id;
    }

    private boolean saveModels(ArrayList<String> modelNameList,
            ArrayList<byte[]> modelList) {
        // check Model count.
        if (modelNameList.size() != modelList.size()) {
            System.out
                    .println("ModelName's count and Model's count isn't same.");
            ServerApLogger.getInstance().writeWarning(
                    "ModelName's count and Model's count isn't same.");
            return false;
        }

        // save models.
        for (int i = 0; i < modelNameList.size(); i++) {
            File modelName = new File(modelNameList.get(i));
            File modelFile = new File(m_ModelOutputDirectry.getAbsolutePath(),
                    modelName.getName());
            // write ModelData.
            FileOutputStream outStream = null;
            try {
                outStream = new FileOutputStream(modelFile);
                outStream.write(modelList.get(i));
                outStream.flush();
                outStream.close();
                // reset ModelFileName.
                modelNameList.set(i, modelFile.getAbsolutePath());
            } catch (IOException e) {
                String message = "saveModels() is failed. :" + e.getMessage();
                System.out.println(message);
                e.printStackTrace();
                ServerApLogger.getInstance().writeWarning(message);
                ServerApLogger.getInstance().writeException(e);
                return false;
            }
        }
        return true;
    }

    private String createCommandLine(String id, ArrayList<String> modelNameList) {
        // create ID:XX = .
        String outString = ID_MARK + id + SPACE_MARK + EQUAL_MARK + SPACE_MARK;
        
        String receiverHeader = RECEIVER_STR + m_RpcCommandReceiver
                + SPACE_MARK + OPTIONS_STR + SPACE_MARK + AMNAME_STR;
        for (int i = 0; i < modelNameList.size(); i++) {
            String appendStr = "";
            if (0 < i) {
                appendStr = COMMAND_DELIMITER;
            }
            appendStr += receiverHeader + modelNameList.get(i) + SPACE_MARK
                    + ACTIVE_STR;
            outString += appendStr;
        }
        return outString;
    }

    private void saveCommand(String id, String commandLine) {
        // reset commandLine.
        if (m_IdAndCommandMap.containsKey(id.toLowerCase())) {
            CommandLineInfo lineInfo = m_IdAndCommandMap.get(id.toLowerCase());
            // comment out old commandline.
            String oldCommand = COMMENT_MARK
                    + m_CommandLineList.get(lineInfo.getLineNo());
            m_CommandLineList.set(lineInfo.getLineNo(), oldCommand);
            m_IdAndCommandMap.remove(id.toLowerCase());
        }

        // count up LastLine.
        m_LastLineNo++;

        // set command line map.
        CommandLineInfo lineInfo = new CommandLineInfo(commandLine,
                m_LastLineNo);
        m_IdAndCommandMap.put(id.toLowerCase(), lineInfo);

        // add CommandLineList.
        m_CommandLineList.add(commandLine);
    }
}
