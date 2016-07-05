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
import java.util.*;

/**
 * @author Kimura Noriyuki
 * @version 2.00
 * @since 2011/06/10
 */
public class FileInput {
    File InFile;
    BufferedInputStream ByteFile;

    BufferedReader mStringReader;

    public FileInput(File file) throws Exception{
        this(file,"EUC_JP");
    }



    public FileInput(String sFileName) throws Exception{
        this(sFileName,"EUC_JP");
    }

    public FileInput(File file, String sStringCode) throws Exception {
        ByteFile = new BufferedInputStream((new FileInputStream(file)));
        if (sStringCode != null) {
            mStringReader = new BufferedReader(new InputStreamReader(ByteFile,
                    sStringCode));
        }
    }

    public FileInput(String sFileName, String sStringCode) throws Exception {
        File InputFile = new File(sFileName);
        ByteFile = new BufferedInputStream((new FileInputStream(InputFile)));
        if (sStringCode != null) {
            mStringReader = new BufferedReader(new InputStreamReader(ByteFile,
                    sStringCode));
        }
    }


    public byte[] ByteFileRead(int size) {
        byte[] buffer = new byte[size];
        try {
            ByteFile.read(buffer);
        } catch (IOException IOE) {
            IOE.printStackTrace();
        }
        return buffer;

    }

    public String readLine() {
        String sRetVal = null;
        try {
            sRetVal = mStringReader.readLine();
        } catch (IOException IOE) {
            IOE.printStackTrace();
        }
        return sRetVal;

    }


    public int ByteFileRead(byte[] buffer) {
        int RetVal = -1;
        try {
            RetVal = ByteFile.read(buffer);
        } catch (IOException IOE) {
            IOE.printStackTrace();
        }
        return RetVal;
    }

    public int ByteFileRead(byte[] buffer, int iReadPoint, int iReadSize) {
        int RetVal = -1;
        try {
            RetVal = ByteFile.read(buffer, iReadPoint, iReadSize);
        } catch (IOException IOE) {
            IOE.printStackTrace();
        }
        return RetVal;

    }

    public byte[] AllFileRead() {
        byte[] RetVal = null;
        int TotalReadSize = 0;
        int ReadSize = -1;
        Vector<byte[]> Temp = new Vector<byte[]>();

        try {
            while (true) {
                byte[] tempBuffer = new byte[1024];
                ReadSize = ByteFile.read(tempBuffer);
                if (ReadSize > 0) {
                    TotalReadSize += ReadSize;
                }

                if (ReadSize < 1024) {
                    if (ReadSize > 0) {
                        byte[] LastBuffer = new byte[ReadSize];
                        System.arraycopy(tempBuffer, 0, LastBuffer, 0, ReadSize);
                        Temp.add(LastBuffer);
                    }

                    RetVal = new byte[TotalReadSize];

                    byte[] SubBuffer = null;
                    int TotalWriteSize = 0;
                    for (int i = 0; i < Temp.size(); i++) {
                        SubBuffer = Temp.get(i);
                        System.arraycopy(SubBuffer, 0, RetVal, TotalWriteSize, SubBuffer.length);
                        TotalWriteSize += SubBuffer.length;
                    }
                    break;
                } else {
                    Temp.add(tempBuffer);
                }

            }
        } catch (IOException IOE) {
            IOE.printStackTrace();
        }
        return RetVal;
    }


    public void Close() {
        try {
            ByteFile.close();
        } catch (IOException IOE) {
            IOE.printStackTrace();
        }
    }

}
