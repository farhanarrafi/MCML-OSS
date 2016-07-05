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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.go.nict.security.crypto;

/**
 * This class encrypts and decrypts data.
 */
public class Code {

    /**
     * The table for code translation.
     */
    private static int[][] codeTable = {
            { 0x00, 0x90, 0xd6, 0xf6, 0xb2, 0x3d, 0x34, 0x42, 0xc6, 0x09, 0x0a,
                    0xe2, 0xb0, 0x0d, 0x78, 0x08, 0x51, 0xa3, 0x02, 0x35, 0xfa,
                    0x18, 0x97, 0x29, 0x3b, 0x85, 0x1a, 0x6a, 0xd2, 0x21, 0xc1,
                    0x75, 0x20, 0x1f, 0x06, 0xdc, 0xae, 0x60, 0x0c, 0x10, 0x30,
                    0xf1, 0xb1, 0xde, 0x19, 0xce, 0x32, 0xcf, 0xf2, 0x1d, 0x3a,
                    0xeb, 0xd4, 0xf5, 0xb7, 0xc9, 0x4e, 0xd9, 0x50, 0xf4, 0x8c,
                    0x23, 0x7e, 0x56, 0xc8, 0xdd, 0x4a, 0x66, 0x04, 0x01, 0xf3,
                    0x3e, 0x62, 0x27, 0x55, 0x38, 0xe0, 0x53, 0x2c, 0x13, 0x91,
                    0xfe, 0xa2, 0x37, 0xc4, 0xcd, 0xc0, 0x79, 0x1c, 0x5e, 0xbc,
                    0x28, 0xbb, 0xef, 0x95, 0x41, 0xd1, 0x25, 0x9d, 0xa1, 0xe5,
                    0x67, 0x54, 0x86, 0x07, 0x57, 0x12, 0x5d, 0xe4, 0x9c, 0x4b,
                    0x6c, 0x03, 0xa9, 0xa6, 0x64, 0x6b, 0x69, 0x7f, 0x2d, 0xc7,
                    0xec, 0x3c, 0x44, 0xa5, 0xba, 0xb3, 0x0f, 0xa7, 0x48, 0x71,
                    0x11, 0x1e, 0x31, 0xd8, 0x96, 0x49, 0x9b, 0x58, 0x14, 0xda,
                    0x72, 0xc5, 0x8b, 0xab, 0x9e, 0x77, 0x87, 0xbd, 0xd5, 0x84,
                    0x9a, 0x4d, 0xb5, 0xe1, 0xe8, 0x2b, 0x46, 0x92, 0xbe, 0x1b,
                    0x98, 0x33, 0xbf, 0x83, 0x36, 0x3f, 0x88, 0xa0, 0x94, 0xfd,
                    0xed, 0x8d, 0xb9, 0x80, 0xf0, 0x0e, 0x89, 0x5b, 0x59, 0x2f,
                    0xb4, 0xfb, 0x93, 0x0b, 0x24, 0xdb, 0x45, 0x43, 0xf9, 0x76,
                    0xe9, 0x5a, 0x5c, 0x39, 0x65, 0xcc, 0xaa, 0xd7, 0xf7, 0x7d,
                    0x9f, 0xaf, 0xa4, 0x99, 0x2a, 0x6d, 0xe6, 0x6f, 0x81, 0x8e,
                    0xd3, 0x5f, 0x74, 0x82, 0xd0, 0x15, 0xea, 0x4c, 0xa8, 0xac,
                    0x7a, 0x8a, 0xad, 0xc3, 0x61, 0x73, 0xfc, 0xb8, 0x63, 0x05,
                    0x2e, 0xf8, 0xee, 0x4f, 0xca, 0x16, 0xe3, 0x47, 0xdf, 0x17,
                    0x70, 0x22, 0x40, 0xc2, 0xcb, 0xe7, 0x6e, 0x52, 0x68, 0x8f,
                    0x26, 0x7c, 0xb6, 0x7b, 0xff },
            { 0x00, 0x45, 0x12, 0x70, 0x44, 0xe6, 0x22, 0x68, 0x0f, 0x09, 0x0a,
                    0xb8, 0x26, 0x0d, 0xb0, 0x7f, 0x27, 0x83, 0x6a, 0x4f, 0x8b,
                    0xd8, 0xec, 0xf0, 0x15, 0x2c, 0x1a, 0xa0, 0x58, 0x31, 0x84,
                    0x21, 0x20, 0x1d, 0xf2, 0x3d, 0xb9, 0x61, 0xfb, 0x49, 0x5b,
                    0x17, 0xcd, 0x9c, 0x4e, 0x77, 0xe7, 0xb4, 0x28, 0x85, 0x2e,
                    0xa2, 0x06, 0x13, 0xa5, 0x53, 0x4b, 0xc2, 0x32, 0x18, 0x7a,
                    0x05, 0x47, 0xa6, 0xf3, 0x5f, 0x07, 0xbc, 0x7b, 0xbb, 0x9d,
                    0xee, 0x81, 0x88, 0x42, 0x6e, 0xda, 0x98, 0x38, 0xea, 0x3a,
                    0x10, 0xf8, 0x4d, 0x66, 0x4a, 0x3f, 0x69, 0x8a, 0xb3, 0xc0,
                    0xb2, 0xc1, 0x6b, 0x59, 0xd4, 0x25, 0xe1, 0x48, 0xe5, 0x73,
                    0xc3, 0x43, 0x65, 0xf9, 0x75, 0x1b, 0x74, 0x6f, 0xce, 0xf7,
                    0xd0, 0xf1, 0x82, 0x8d, 0xe2, 0xd5, 0x1f, 0xbe, 0x92, 0x0e,
                    0x57, 0xdd, 0xfe, 0xfc, 0xc8, 0x3e, 0x76, 0xae, 0xd1, 0xd6,
                    0xa4, 0x96, 0x19, 0x67, 0x93, 0xa7, 0xb1, 0xde, 0x8f, 0x3c,
                    0xac, 0xd2, 0xfa, 0x01, 0x50, 0x9e, 0xb7, 0xa9, 0x5e, 0x87,
                    0x16, 0xa1, 0xcc, 0x97, 0x89, 0x6d, 0x62, 0x91, 0xc9, 0xa8,
                    0x63, 0x52, 0x11, 0xcb, 0x7c, 0x72, 0x80, 0xdb, 0x71, 0xc5,
                    0x90, 0xdc, 0xdf, 0x24, 0xca, 0x0c, 0x2a, 0x04, 0x7e, 0xb5,
                    0x99, 0xfd, 0x36, 0xe4, 0xad, 0x7d, 0x5c, 0x5a, 0x94, 0x9f,
                    0xa3, 0x56, 0x1e, 0xf4, 0xe0, 0x54, 0x8e, 0x08, 0x78, 0x40,
                    0x37, 0xeb, 0xf5, 0xc4, 0x55, 0x2d, 0x2f, 0xd7, 0x60, 0x1c,
                    0xd3, 0x34, 0x95, 0x02, 0xc6, 0x86, 0x39, 0x8c, 0xba, 0x23,
                    0x41, 0x2b, 0xef, 0x4c, 0x9a, 0x0b, 0xed, 0x6c, 0x64, 0xcf,
                    0xf6, 0x9b, 0xbf, 0xd9, 0x33, 0x79, 0xab, 0xe9, 0x5d, 0xaf,
                    0x29, 0x30, 0x46, 0x3b, 0x35, 0x03, 0xc7, 0xe8, 0xbd, 0x14,
                    0xb6, 0xe3, 0xaa, 0x51, 0xff } };

    /**
     * This class encode data, and decode them.
     */
    public Code() {
    }

    /**
     * エンコードを行い、バイト配列を取得します。
     * 
     * @param data
     * @return バイト配列
     */
    public byte[] encode(byte[] data) {

        if (data == null) {
            return null;
        }

        // Encodes data

        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) Code.codeTable[0][data[i] & 0xff];
        }

        return data;
    }

    /**
     * <p>
     * エンコードされたデータからデコードしたバイト配列を返します。
     * </p>
     * <p>
     * エンコードされたデータが{@code null}の場合は{@code null}を返します。
     * </p>
     * 
     * @param encodedData
     *            エンコードされたデータ
     * @return デコードしたバイト配列
     */
    public byte[] decode(byte[] encodedData) {

        if (encodedData == null) {
            return null;
        }

        // Decodes data.

        for (int i = 0; i < encodedData.length; i++) {
            encodedData[i] = (byte) Code.codeTable[1][(int) encodedData[i] & 0xff];
        }

        return encodedData;
    }

    /**
     * For developer.
     */
    public static void makeTheOtherTable() {
        for (int j = 0; j < 256; j++) {
            for (int i = 0; i < 256; i++) {
                if (codeTable[0][i] == j) {
                    System.out
                            .print((Integer.toHexString(i).length() == 1 ? "0x0"
                                    : "0x")
                                    + Integer.toHexString(i) + ", ");
                    break;
                }
            }
            if (((j + 1) % 16) == 0) {
                System.out.println("");
            }
        }
        System.out.println("");
    }
}