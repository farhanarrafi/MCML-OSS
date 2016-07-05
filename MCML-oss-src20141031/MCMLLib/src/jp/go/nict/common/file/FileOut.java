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

package jp.go.nict.common.file;

import java.io.*;

/**
 */
public class FileOut {
    File OutFile;
    BufferedOutputStream ByteFile;
    BufferedWriter mBufferdWriter;

    public FileOut(String sFileName) {
        try {
            OutFile = new File(sFileName);
            ByteFile = new BufferedOutputStream((new FileOutputStream(OutFile)));
            mBufferdWriter = new BufferedWriter(new OutputStreamWriter(ByteFile,"EUC_JP"));
        } catch (IOException IOE) {
            IOE.printStackTrace();
        }
    }

    public FileOut(File writeFile) {
        try {
            OutFile = writeFile;
            ByteFile = new BufferedOutputStream((new FileOutputStream(OutFile)));
        } catch (IOException IOE) {
            IOE.printStackTrace();
        }

    }

    public FileOut(File writeFile,String sStrignCode) {
        try {
            OutFile = writeFile;
            ByteFile = new BufferedOutputStream((new FileOutputStream(OutFile)));
            mBufferdWriter = new BufferedWriter(new OutputStreamWriter(ByteFile,sStrignCode));
        } catch (IOException IOE) {
            IOE.printStackTrace();
        }

    }


    public FileOut(String sFileName,String sStrignCode) {
        try {
            OutFile = new File(sFileName);
            ByteFile = new BufferedOutputStream((new FileOutputStream(OutFile)));
            mBufferdWriter = new BufferedWriter(new OutputStreamWriter(ByteFile,sStrignCode));
        } catch (IOException IOE) {
            IOE.printStackTrace();
        }

    }


    public void ByteFileOut(byte[] bOutData) {
        try {
            ByteFile.write(bOutData);
            ByteFile.flush();
        } catch (IOException IOE) {
            IOE.printStackTrace();
        }

    }

    public void ByteFileOut(byte[] bOutData, int startpoint, int size) {
        try {
            ByteFile.write(bOutData, startpoint, size);
            ByteFile.flush();
        } catch (IOException IOE) {
            IOE.printStackTrace();
        }

    }

    public void WriteLine(String sString){
        try{
            mBufferdWriter.write(sString+"\n");
            mBufferdWriter.flush();
        }
        catch(IOException IOE){
            IOE.printStackTrace();
        }
    }

    public void Close() {
        try {
            ByteFile.flush();
            ByteFile.close();
        } catch (IOException IOE) {
            IOE.printStackTrace();
        }
    }

}
