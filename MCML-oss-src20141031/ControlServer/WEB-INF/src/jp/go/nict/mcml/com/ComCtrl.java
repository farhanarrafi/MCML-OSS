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

package jp.go.nict.mcml.com;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;

import jp.go.nict.security.crypto.Code;

import org.apache.log4j.Logger;

/**
 * ComCtrl class.
 * 
 * @version 4.0
 * @since 20120921
 */
public class ComCtrl {
    private static final Logger LOG = Logger.getLogger(ComCtrl.class.getName());

    // Encryption flag only for Response.
    private Code code;
    private MimeData mimeData;

    /**
     * Constructor.
     */
    public ComCtrl() {
    }

    /**
     * Constructor with encryption.
     */
    public ComCtrl(boolean encryption) {

        if (encryption) {
            code = new Code();
        }

    }

    // ------------------------------------------
    // protected member function
    // ------------------------------------------
    protected void receive(InputStream stream, ComData comData, String url,
            HttpSession session) throws MessagingException, IOException {
        // generate InputStream
        BufferedInputStream bufStream = new BufferedInputStream(stream);

        LOG.info("[(yano)" + session.getId() + "]\n URL: " + url + "\n");

        // receive MIME data
        MimeData mimeData = new MimeData(bufStream);

        // close InputStream
        bufStream.close();

        // get MIME data contents
        String text = mimeData.getTextData(0);
        if (text != null) {
            comData.setXML(text);
            LOG.info("[" + session.getId() + "]\n URL: " + url + "\n Receive: "
                    + text);
        }

        int count = mimeData.getCount();
        ArrayList<byte[]> byteList = new ArrayList<byte[]>();

        // loop for number of MIME data body
        for (int no = 1; no < count; no++) {
            byte[] bin = mimeData.getBinaryData(no);
            if (bin != null) {
                // add binary data
                byteList.add(bin);
            }
        }

        comData.setBinaryList(byteList);

        // success
        return;
    }

    protected boolean receive(InputStream stream, ComData comData)
            throws MessagingException, IOException {

        // generate InputStream
        BufferedInputStream bufStream = new BufferedInputStream(stream);

        // receive MIME data
        this.mimeData = new MimeData(bufStream);

        // Handles content when it becomes "" in HTTPS communication.
        if (this.mimeData.isMultiParisNull()) {
            return false;
        }

        // close InputStream
        bufStream.close();

        // get MIME data contents
        String text = this.mimeData.getTextData(0);
        if (text != null) {
            comData.setXML(text);
        } else {
            // The encrypted string data is binary of type.
            byte[] bin = this.mimeData.getBinaryData(0);
            if (bin != null) {
                String string = new String(bin, 0, bin.length,
                        MimeData.CHARSET_NAME);
                comData.setXML(string);
            }
        }

        int count = this.mimeData.getCount();
        ArrayList<byte[]> byteList = new ArrayList<byte[]>();

        // loop for numberr of MIME data body
        for (int no = 1; no < count; no++) {
            byte[] bin = this.mimeData.getBinaryData(no);
            if (bin != null) {
                // add binary data
                byteList.add(bin);
            }
        }

        comData.setBinaryList(byteList);

        // Handles content when it becomes "" in success HTTPS communication.
        return true;
    }

    protected void send(OutputStream stream, String stringData,
            ArrayList<byte[]> binaryList, String url, HttpSession session)
            throws MessagingException, IOException {
        // make MIME data
        MimeData mime = new MimeData();

        // set string data part
        if (stringData != null) {
            mime.addMimeBodyPart(stringData);
            LOG.info("[" + session.getId() + "]\n URL: " + url + "\n Send: "
                    + stringData);
        } else {
            mime.addMimeBodyPart("");
        }

        if (binaryList != null) {
            for (int i = 0; i < binaryList.size(); i++) {
                // set binary data part
                mime.addMimeBody(binaryList.get(i), session);
            }
        }

        // get MIME message
        MimeMessage mmsg = mime.getMimeMessage();

        // generate OutputStream
        BufferedOutputStream bufStream = new BufferedOutputStream(stream);

        // send
        mmsg.writeTo(bufStream);

        // close OutputStream
        bufStream.flush();
        bufStream.close();

        // success
        return;
    }

    protected void send(OutputStream stream, String stringData,
            ArrayList<byte[]> binaryList) throws MessagingException,
            IOException {
        // make MIME data
        // If this.mimeData is created in receive method, use it.
        if (this.mimeData == null) {
            this.mimeData = new MimeData(this.code);
        } else {
            this.mimeData.resetMultiPart();
        }
        try {

            // set string data part
            if (stringData != null) {
                this.mimeData.addMimeBodyPart(stringData);
            } else {
                this.mimeData.addMimeBodyPart("");
            }

            for (int i = 0; i < binaryList.size(); i++) {
                // set binary data part
                this.mimeData.addMimeBody(binaryList.get(i), null);
            }

            // get MIME message
            MimeMessage mmsg = this.mimeData.getMimeMessage();

            // generate OutputStream
            BufferedOutputStream bufStream = new BufferedOutputStream(stream);

            // send
            mmsg.writeTo(bufStream);

            // close OutputStream
            bufStream.flush();
            bufStream.close();

            // success
            return;

        } finally {
            // Resets this.mimeData.
            // The MimeData used in send method is the MimeData created in the
            // last call of receive method.
            this.mimeData = null;
        }
    }

    protected void sendLogServer(OutputStream stream, String stringData,
            String url, HttpSession session, ArrayList<byte[]> binaryList)
            throws MessagingException, IOException {
        // make MIME data
        MimeData mime = new MimeData();

        // set string data part
        if (stringData != null) {
            mime.addMimeBodyPart(stringData);
            LOG.info("[" + session.getId() + "]\n URL: " + url + "\n Send: "
                    + stringData);
        } else {
            mime.addMimeBodyPart("");
        }

        if (binaryList != null) {
            for (int i = 0; i < binaryList.size(); i++) {
                // set binary data part
                mime.addMimeBody(binaryList.get(i), null);
            }
        }

        // get MIME message
        MimeMessage mmsg = mime.getMimeMessage();

        // generate OutputStream
        BufferedOutputStream bufStream = new BufferedOutputStream(stream);

        // send
        mmsg.writeTo(bufStream);

        // close OutputStream
        bufStream.flush();
        bufStream.close();

        // success
        return;
    }

    /**
     * send No Mime
     * 
     * @param stream
     * @param stringData
     * @throws MessagingException
     * @throws IOException
     */
    protected void sendNoMime(OutputStream stream, String stringData)
            throws MessagingException, IOException {
        LOG.info("Send: " + stringData);
        // generate OutputStream
        BufferedOutputStream bufStream = new BufferedOutputStream(stream);

        // send
        bufStream.write(stringData.getBytes());

        // close OutputStream
        bufStream.flush();
        bufStream.close();

        // success
        return;
    }

    /**
     * receive No Mime
     * 
     * @param stream
     * @param comData
     * @return {@code true} at completion of processing normally, {@code false} at the occurrence of exceptions
     * @throws MessagingException
     * @throws IOException
     */
    protected boolean receiveNoMime(InputStream stream, ComData comData)
            throws MessagingException, IOException {
        // generate InputStream
        BufferedReader stringReader = new BufferedReader(new InputStreamReader(
                stream, "UTF8"));

        String text = stringReader.readLine();

        if (text != null) {
            comData.setXML(text);
        }

        return true;
    }

}
