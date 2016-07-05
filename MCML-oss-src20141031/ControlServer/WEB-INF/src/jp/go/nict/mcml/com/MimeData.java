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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.http.HttpSession;

import jp.go.nict.mcml.serverap.common.AudioConverter;
import jp.go.nict.mcml.serverap.common.WaveLogWriter;
import jp.go.nict.mcml.xml.MCMLException;
import jp.go.nict.security.crypto.Code;

import org.apache.log4j.Logger;

/**
 * MimeData class.
 * 
 */
public class MimeData {
    private static final Logger LOG = Logger
            .getLogger(MimeData.class.getName());
    // ------------------------------------------
    // protected member constant
    // ------------------------------------------
    /** Character set name */
    public static final String CHARSET_NAME = "UTF-8";
    /** Content type (Binary) */
    public static final String CONTENT_TYPE_BINARY = "application/octet-stream";
    /** Content type (Multipart) */
    public static final String CONTENT_TYPE_MULTIPART_MIXED = "multipart/mixed";
    /** Content type (Text) */
    public static final String CONTENT_TYPE_TEXT = "text/xml;charset=utf-8";
    /** Encoding (7bit) */
    public static final String ENCODING_7BIT = "7bit";
    /** Encoding (8bit) */
    public static final String ENCODING_8BIT = "8bit";
    /** Encoding (Binary) */
    public static final String ENCODING_BINARY = "binary";
    /** Encryption type 1 */
    public static final String ENCRYPTION_TYPE_ENCTYPE01 = "enctype01";
    /** Encryption type 2 */
    public static final String ENCRYPTION_TYPE_ENCTYPE02 = "enctype02";
    /** Header content (Encryption) */
    public static final String HEADER_CONTENT_ENCRYPTION_TYPE = "X-Content-Encryption-Type";
    /** Header content (Length) */
    public static final String HEADER_CONTENT_LENGTH = "Content-Length";
    /** Header content (Transfer encoding) */
    public static final String HEADER_CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";
    /** HeaderXcontent (Transfer encoding) */
    public static final String HEADER_X_CONTENT_TRANSFER_ENCODING = "X-Content-Transfer-Encoding";
    /** Header content type */
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    /** Header transfer encoding */
    public static final String HEADER_TRANSFER_ENCODING = "Transfer-Encoding";
    /** Transfer encoding (Chunk) */
    public static final String TRANSFER_ENCODING_CHUNKED = "chunked";

    /** Encoding (BASE64) */
    public static final String ENCODING_BASE64 = "base64";

    private final String optionKeyBinary = "Binary";
    private final String optionValueBase64 = "Base64";
    private final String optionValueBase64Wave = "Base64Wave";

    protected static final int SAMPLINGFREQUENCY_16K = 16000;
    protected static final int SAMPLINGFREQUENCY_8K = 8000;
    protected static final int DEFAULT_SAMPLING_FREQUENCY = 16000;
    protected static final short DEFAULT_SAMPLING_BIT = 16;
    protected static final short DEFAULT_CHANNEL_NUM = 1;

    // ------------------------------------------
    // private member variable
    // ------------------------------------------
    private String m_Subject;

    // ------------------------------------------
    // protected member variable
    // ------------------------------------------
    protected MimeMultipart m_MultiPart;

    private Code encrypter = null;

    // ------------------------------------------
    // public member function
    // ------------------------------------------
    /** default constructor */
    public MimeData() {
        // generate MIME multi part object
        m_MultiPart = new MimeMultipart();
        m_Subject = null;
    }

    /**
     * Constructor with the parameter encrypter. Removes default constructor for
     * preventing coding errors.
     */
    public MimeData(Code encrypter) {
        // generate MIME multi part object
        m_MultiPart = new MimeMultipart();
        this.encrypter = encrypter;
    }

    /**
     * Checks if MultiPart data is null.
     * 
     * @return boolean
     */
    // handles content when it becomes "" in HTTPS communication.
    public boolean isMultiParisNull() {
        if (m_MultiPart == null) {
            return true;
        }
        return false;
    }

    /** constructor for parser */
    public MimeData(InputStream inputStream) throws MessagingException,
            IOException {
        // generate MIME message object
        Session session = getSession();
        MimeMessage mimeMsg = new MimeMessage(session, inputStream);

        byte[] fbytes = new byte[1024];
        while ((inputStream.read(fbytes)) >= 0) {
            System.out.print("1: " + new String(fbytes));
        }

        m_MultiPart = (MimeMultipart) mimeMsg.getContent();
        m_Subject = mimeMsg.getSubject();
    }

    /** resetMultiPart. */
    public void resetMultiPart() {
        m_MultiPart = new MimeMultipart();
    }

    /** add body(text) */
    public void addMimeBodyPart(String textData) throws MessagingException,
            UnsupportedEncodingException {

        if (this.encrypter != null) {
            addMimeBody(textData.getBytes("UTF-8"), null);
        } else {
            MimeBodyPart bp = new MimeBodyPart();
            bp.setText(textData, CHARSET_NAME);
            bp.setHeader(HEADER_CONTENT_TYPE, CONTENT_TYPE_TEXT);
            bp.setHeader(HEADER_CONTENT_TRANSFER_ENCODING, ENCODING_8BIT); // Bugfix,
                                                                           // 7bit(http
                                                                           // base64)
                                                                           // ->
                                                                           // 8bit
                                                                           // (https
                                                                           // raw)
            bp.setHeader(HEADER_CONTENT_LENGTH, "" + textData.length());
            m_MultiPart.addBodyPart(bp);
        }

        // success
        return;
    }

    /** add body(binary) */
    public void addMimeBody(byte[] binData, HttpSession session)
            throws MessagingException {
        MimeBodyPart bp = new MimeBodyPart();

        // base64,
        if (session != null
                && session.getAttribute(this.optionKeyBinary) != null) {
            String binaryOption = (String) session
                    .getAttribute(this.optionKeyBinary);
            if (binaryOption != null
                    && binaryOption.equals(this.optionValueBase64)) {
                // Add wave header, encode Base64 & text
                bp.setHeader(HEADER_X_CONTENT_TRANSFER_ENCODING,
                        ENCODING_BASE64);
                bp.setHeader(HEADER_CONTENT_TRANSFER_ENCODING, ENCODING_8BIT);
                String strData = encodeStream(binData);
                LOG.debug(strData);
                bp.setText(strData, CHARSET_NAME);
                session.setAttribute(this.optionKeyBinary, "");
            } else if (binaryOption != null
                    && binaryOption.equals(this.optionValueBase64)) {
                // Add wave header, encode Base64 & text
                bp.setHeader(HEADER_X_CONTENT_TRANSFER_ENCODING,
                        ENCODING_BASE64);
                bp.setHeader(HEADER_CONTENT_TRANSFER_ENCODING, ENCODING_8BIT);
                byte[] pcmbyteData = null;

                // convert Little Endian to Big Endian (isPCM, isBigEndian,
                // trgIsPCM, trgIsBigEndian, MCMLException)
                AudioConverter audioConv = new AudioConverter(true, true, true,
                        false, MCMLException.TTS);
                try {
                    pcmbyteData = audioConv.convert(binData);
                } catch (jp.go.nict.mcml.exception.MCMLException e) {
                    LOG.error(e.getMessage(), e);
                }
                // add WAV Header
                WaveLogWriter waveLogWriter = new WaveLogWriter(true);
                byte[] wavData = waveLogWriter.generateWavData(pcmbyteData,
                        DEFAULT_CHANNEL_NUM, DEFAULT_SAMPLING_FREQUENCY,
                        DEFAULT_SAMPLING_BIT);
                String strData = encodeStream(wavData);
                LOG.debug(strData);
                bp.setText(strData, CHARSET_NAME);
                session.setAttribute(this.optionKeyBinary, "");
            } else {
                bp.setDataHandler(new DataHandler(new ByteArrayDataSource(
                        binData, CONTENT_TYPE_BINARY)));

            }

        } else {
            bp.setDataHandler(new DataHandler(new ByteArrayDataSource(binData,
                    CONTENT_TYPE_BINARY)));

        }

        bp.setHeader(HEADER_CONTENT_TRANSFER_ENCODING, ENCODING_BINARY);
        if (this.encrypter != null) {
            this.encrypter.encode(binData);
            bp.setHeader(HEADER_CONTENT_ENCRYPTION_TYPE,
                    ENCRYPTION_TYPE_ENCTYPE02);
        }
        bp.setHeader(HEADER_CONTENT_LENGTH, "" + binData.length);

        m_MultiPart.addBodyPart(bp);

        // success
        return;
    }

    /** get MIME message */
    public MimeMessage getMimeMessage() throws MessagingException {
        // generate MIME message
        MimeMessage retVal = new MimeMessage(getSession());
        retVal.setContent(m_MultiPart);

        if (m_Subject != null) {
            // set Subject on MIME message
            retVal.setSubject(m_Subject, CHARSET_NAME);
        }

        // success
        return retVal;
    }

    /** get number of body part */
    public int getCount() throws MessagingException {
        return m_MultiPart.getCount();
    }

    /** get text data part(for parser) */
    public String getTextData(int no) throws MessagingException, IOException {
        // get OutputStream for text data
        ByteArrayOutputStream outStream = getBodyPartOutputStream(no,
                CONTENT_TYPE_TEXT);
        if (outStream == null) {
            return null; // no data
        }

        String retVal = null;

        String[] enctype = this.getHeader(no,
                MimeData.HEADER_CONTENT_ENCRYPTION_TYPE);
        if (enctype != null
                && enctype.length > 0
                && enctype[0]
                        .equalsIgnoreCase(MimeData.ENCRYPTION_TYPE_ENCTYPE02)) {
            throw new IllegalStateException("Invalid encoding.");

        } else {
            // set return value
            retVal = outStream.toString(CHARSET_NAME);
        }

        // close OutputStream
        outStream.close();

        // success
        return retVal;
    }

    /** get binary data part(for parser) */
    public byte[] getBinaryData(int no) throws MessagingException, IOException {
        // get OutputStream for binary data
        ByteArrayOutputStream outStream = getBodyPartOutputStream(no,
                CONTENT_TYPE_BINARY);
        if (outStream == null) {
            return null; // no data
        }

        // set return value
        byte[] retVal = null;
        byte[] bin = outStream.toByteArray();
        retVal = bin;

        String[] enctype = this.getHeader(no,
                MimeData.HEADER_CONTENT_ENCRYPTION_TYPE);
        if (enctype != null
                && enctype.length > 0
                && enctype[0]
                        .equalsIgnoreCase(MimeData.ENCRYPTION_TYPE_ENCTYPE02)) {

            if (bin != null) {
                if (this.encrypter == null) {
                    this.encrypter = new Code();
                }
                retVal = this.encrypter.decode(bin);
            } else {
            }
        } else {
        }

        // close OutputStream
        outStream.close();

        // success
        return retVal;
    }

    /**
     * get OutputStream for MIME body part
     * 
     * @param no
     * @param contentType
     * @return ByteArrayOutputStream
     * @throws MessagingException
     * @throws IOException
     */
    public ByteArrayOutputStream getBodyPartOutputStream(int no,
            String contentType) throws MessagingException, IOException {
        if (no < 0 || no >= m_MultiPart.getCount()) {
            return null; // wrong parameter
        }

        // get MIME body part
        BodyPart bp = m_MultiPart.getBodyPart(no);
        if (!bp.getContentType().equals(contentType)) {
            return null; // no match contentType
        }

        // generate stream
        InputStream inStream = bp.getInputStream();
        ByteArrayOutputStream retVal = new ByteArrayOutputStream();

        int data;
        // get data
        while ((data = inStream.read()) != -1) {
            retVal.write(data);
        }

        // close stream
        inStream.close();

        String[] xContentTransferEncoding = bp
                .getHeader(HEADER_X_CONTENT_TRANSFER_ENCODING);
        if (xContentTransferEncoding != null
                && xContentTransferEncoding[0].equals(ENCODING_BASE64)) {
            LOG.debug("recived base64 request");
            String sttBase64 = retVal.toString();
            return decodeByteArray(sttBase64);
        }

        // success
        return retVal;
    }

    static final String CHARSET = "utf-8";

    /**
     * Encodes character strings sent in BASE64 format.
     * 
     * @param src
     * @return ByteArrayOutputStream.toString()
     */
    private String encodeString(String src) {
        String res = null;
        OutputStream out = null;
        ByteArrayOutputStream outStream = null;
        try {
            outStream = new ByteArrayOutputStream();
            out = MimeUtility.encode(outStream, "base64");
            out.write(src.getBytes(CHARSET));
        } catch (Exception e) {
            LOG.error(e.getMessage() + "error encode by base64.", e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (outStream != null) {
            res = outStream.toString();
        }
        return res;
    }

    /**
     * Encodes character streams sent in BASE64 format
     * 
     * 
     * @param src
     * @return String
     */
    private String encodeStream(byte[] src) {
        String res = null;
        OutputStream out = null;
        ByteArrayOutputStream outStream = null;
        try {
            outStream = new ByteArrayOutputStream();
            out = MimeUtility.encode(outStream, "base64");
            out.write(src);
        } catch (Exception e) {
            LOG.error(e.getMessage() + "error encode by base64.", e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (outStream != null) {
            res = outStream.toString();
        }
        res = res.replaceAll("\\n", "");
        res = res.replaceAll("\\r", "");
        return res;
    }

    /**
     * Decodes character strings sent in BASE64 format.
     * 
     * @param src
     * @return String
     */
    private String decodeString(String src) {
        ByteArrayInputStream inputStream = null;
        InputStream in = null;
        String res = null;
        try {
            inputStream = new ByteArrayInputStream(src.getBytes(CHARSET));
            in = MimeUtility.decode(inputStream, "base64");

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int len;
            byte[] buf = new byte[512];
            while ((len = in.read(buf)) >= 0) {
                outputStream.write(buf, 0, len);
            }
            res = new String(outputStream.toByteArray(), CHARSET);
        } catch (Exception e) {
            LOG.error(e.getMessage() + "error decode by base64.", e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    /**
     * Decodes character strings encoded in BASE64 format.
     * 
     * @param src
     * @return ByteArrayOutputStream
     */
    private ByteArrayOutputStream decodeByteArray(String src) {
        ByteArrayInputStream inputStream = null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        InputStream in = null;
        try {
            inputStream = new ByteArrayInputStream(src.getBytes(CHARSET));
            in = MimeUtility.decode(inputStream, "base64");

            int len;
            byte[] buf = new byte[512];
            while ((len = in.read(buf)) >= 0) {
                outputStream.write(buf, 0, len);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage() + "error decode by base64.", e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return outputStream;
    }

    // set subject
    public void setSubject(String subject) {
        m_Subject = subject;
    }

    // get subject
    public String getSubject() {
        return m_Subject;
    }

    // ------------------------------------------
    // protected member function
    // ------------------------------------------
    // get SessionInstance
    protected Session getSession() {
        Properties prop = System.getProperties();
        return Session.getDefaultInstance(prop);
    }

    // Gets the value of the specified header in the partbody specified by no.
    private String[] getHeader(int no, String header) throws MessagingException {
        if (m_MultiPart == null) {
            return null;
        }
        if (m_MultiPart.getCount() <= no) {
            return null;
        }
        BodyPart bp = m_MultiPart.getBodyPart(no);
        if (bp == null) {
            return null;
        }
        return bp.getHeader(header);
    }
}
