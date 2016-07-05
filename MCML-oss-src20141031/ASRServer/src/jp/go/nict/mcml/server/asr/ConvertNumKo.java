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
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * ConvertNumKo class.
 * 
 */
public class ConvertNumKo {

    /** {@value} */
    public static final String STR_CODE_UTF8 = "UTF-8";

    private String m_fileConvert;
    private String m_fileFigureList;
    private String m_fileNumberList;
    private String m_fileUnConvert;

    private HashMap<String, String> m_numberlist;
    private HashMap<String, String> m_numkurailist;
    private HashMap<String, String> m_kurailist;
    private ArrayList<String> m_nextlist;
    private ArrayList<String> m_unconvlist;

    /**
     * Default constructor
     */
    public ConvertNumKo() {
        m_fileConvert = "";
        m_fileFigureList = "";
        m_fileNumberList = "";
        m_fileUnConvert = "";
    }

    /**
     * Initialization
     * 
     * @param sFiles
     * @return {@literal -1} when exception occurs, otherwise {@literal 0}
     */
    public int initialize(String sFiles) {

        String[] fileAry = sFiles.split(",");
        if (fileAry.length != 4) {
            System.err.println("[ERROR] bad files=" + sFiles);
            return -1;
        }

        m_numberlist = new HashMap<String, String>();
        m_numkurailist = new HashMap<String, String>();
        m_kurailist = new HashMap<String, String>();
        m_nextlist = new ArrayList<String>();
        m_unconvlist = new ArrayList<String>();

        m_fileNumberList = fileAry[0];
        m_fileFigureList = fileAry[1];
        m_fileConvert = fileAry[2];
        m_fileUnConvert = fileAry[3];

        if (readFiles() < 0) {
            System.err.println("[ERROR] read KO convertfile.");
            return -1;
        }

        return 0;
    }

    /**
     * convert
     * 
     * @param sSentence
     * @return  sentence
     */
    public String convert(String sSentence) {
        String[] eojeol = sSentence.split(" ");

        // nothing to do for only 1 word.
        if (eojeol.length <= 1) {
            return sSentence;
        }

        String sNewSentence = "";

        int numflag = 0;
        int totalflag = 0;
        for (int i = 0; i < eojeol.length - 1; i++) {
            String sWord = eojeol[i];
            if (m_unconvlist.contains(eojeol[i])) {
                if (!sNewSentence.isEmpty()) {
                    sNewSentence += " ";
                }
                sNewSentence += sWord;
                continue;
            }

            numflag = 1;
            totalflag = 1;

            ArrayList<String> numlist = new ArrayList<String>();

            for (int k = 0; k < eojeol[i].length(); k++) {
                if (totalflag != 0) {
                    numflag = 1;
                    Iterator it = m_numkurailist.keySet().iterator();
                    while (it.hasNext()) {
                        Object o = it.next();
                        String key = o.toString();
                        if (eojeol[i].substring(k).indexOf(key) == 0) {
                            numflag = 0;
                            numlist.add(m_numkurailist.get(o));
                        }

                        if (numflag == 0) {
                            totalflag = 1;
                        } else {
                            totalflag = 0;
                        }
                    }
                }
            }

            // check the next word for short eojeol
            if (eojeol[i].length() < 2 && totalflag == 1) {
                if (m_nextlist.contains(eojeol[i + 1])) {
                    sWord = str2num(numlist);
                }

                // do not check the next word for long eojeol
            } else if (eojeol[i].length() > 1 && totalflag == 1) {
                sWord = str2num(numlist);
            }

            if (!sNewSentence.isEmpty()) {
                sNewSentence += " ";
            }
            sNewSentence += sWord;
        }

        if (!sNewSentence.isEmpty()) {
            sNewSentence += " ";
        }
        sNewSentence += eojeol[eojeol.length - 1];

        return sNewSentence;
    }

    private String str2num(ArrayList<String> numstr) {
        long total = 0;
        long c_kurai = 1; // current figure

        Iterator it;

        if (numstr.size() == 1) {
            total = Long.parseLong(numstr.get(0));
            return Long.toString(total);
        }

        int iFlg = 0;
        for (int i = 0; i < numstr.size() - 1; i++) {
            iFlg = 0;
            it = m_kurailist.keySet().iterator();
            while (it.hasNext()) {
                Object o = it.next();
                if (numstr.get(numstr.size() - i - 1)
                        .equals(m_kurailist.get(o))) {
                    iFlg = 1;
                    break;
                }
            }
            if (iFlg == 1) {
                c_kurai = Long.parseLong(numstr.get(numstr.size() - i - 1));
            } else {
                if (c_kurai != 1) {
                    total = total
                            + Long.parseLong(numstr.get(numstr.size() - i - 1))
                            * c_kurai;
                } else {
                    total = Long.parseLong(numstr.get(numstr.size() - i - 1));
                }
            }
        }

        iFlg = 0;
        it = m_kurailist.keySet().iterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (numstr.get(0).equals(m_kurailist.get(o))) {
                iFlg = 1;
                break;
            }
        }
        if (iFlg == 1) {
            total = Long.parseLong(numstr.get(0)) + total;
        } else {
            total = Long.parseLong(numstr.get(0)) * c_kurai + total;
        }

        return Long.toString(total);
    }

    /**
     * Reads file.
     * 
     * @return {@literal -1} when exception occurs, otherwise {@literal 0}
     */
    public int readFiles() {
        try {
            // read numberlist
            FileInputStream fis = new FileInputStream(m_fileNumberList);
            InputStreamReader in = new InputStreamReader(fis, STR_CODE_UTF8);
            BufferedReader br = new BufferedReader(in);
            String line;
            while ((line = br.readLine()) != null) {
                String[] strs = line.split(",");
                if (strs.length != 2) {
                    continue;
                }

                m_numberlist.put(strs[1], strs[0]);
                m_numkurailist.put(strs[1], strs[0]);
            }
            br.close();
            in.close();
            fis.close();
        } catch (IOException e) {
            System.out.println(e);
            return -1;
        }

        try {
            // read figurelist
            FileInputStream fis = new FileInputStream(m_fileFigureList);
            InputStreamReader in = new InputStreamReader(fis, STR_CODE_UTF8);
            BufferedReader br = new BufferedReader(in);
            String line;
            while ((line = br.readLine()) != null) {
                String[] strs = line.split(",");
                if (strs.length != 2) {
                    continue;
                }

                m_kurailist.put(strs[1], strs[0]);
                m_numkurailist.put(strs[1], strs[0]);
            }
            br.close();
            in.close();
            fis.close();
        } catch (IOException e) {
            System.out.println(e);
            return -1;
        }

        try {
            // read nextword
            FileInputStream fis = new FileInputStream(m_fileConvert);
            InputStreamReader in = new InputStreamReader(fis, STR_CODE_UTF8);
            BufferedReader br = new BufferedReader(in);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }

                m_nextlist.add(line);
            }
            br.close();
            in.close();
            fis.close();
        } catch (IOException e) {
            System.out.println(e);
            return -1;
        }

        try {
            // read unconv
            FileInputStream fis = new FileInputStream(m_fileUnConvert);
            InputStreamReader in = new InputStreamReader(fis, STR_CODE_UTF8);
            BufferedReader br = new BufferedReader(in);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }

                m_unconvlist.add(line);
            }
            br.close();
            in.close();
            fis.close();
        } catch (IOException e) {
            System.out.println(e);
            return -1;
        }

        return 0;
    }
}
