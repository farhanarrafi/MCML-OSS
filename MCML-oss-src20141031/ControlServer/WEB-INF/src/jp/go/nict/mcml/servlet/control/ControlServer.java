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

package jp.go.nict.mcml.servlet.control;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jp.go.nict.mcml.com.MimeData;
import jp.go.nict.mcml.com.db.DBConnector;
import jp.go.nict.mcml.com.server.RequestData;
import jp.go.nict.mcml.com.server.ServerComCtrl;
import jp.go.nict.mcml.engine.socket.SocketEngineCtrl;
import jp.go.nict.mcml.exception.MCMLException;
import jp.go.nict.mcml.servlet.control.corpuslog.ControlServerCorpusLogger;
import jp.go.nict.mcml.servlet.control.dialog.DialogController;
import jp.go.nict.mcml.xml.XMLTypeTools;
import jp.go.nict.security.AccessManager;
import jp.go.nict.security.Constants;
import jp.go.nict.security.crypto.Code;

import org.apache.log4j.Logger;

import com.MCML.MCMLDoc;
import com.MCML.MCMLType;

/**
 * Modified for asynchronous divided wave data. Mutable integer class.
 * 
 * @version 4.0
 * @since 20120921
 */
class IntegerEx {

    private Integer myValue;

    public IntegerEx(int value) {
        myValue = new Integer(value);
    }

    public IntegerEx(Integer value) {
        myValue = new Integer(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return myValue.toString();
    }

    public int intValue() {
        return myValue.intValue();
    }

    public void setInt(int value) {
        myValue = new Integer(value);
    }
}

/**
 * The entrance servlet for connecting to MCML system.
 */
public class ControlServer extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(ControlServer.class
            .getName());
    // ------------------------------------------
    // private member constants
    // ------------------------------------------
    private static final long serialVersionUID = -7026553871114937884L;

    private final String frameCounterName = "/n";
    private final String lastMark = "last";
    private final String debugMark = "/debug";
    private final String refreshMark = "/refresh";
    private final String refreshKey = "refresh_key";
    private final String infoMark = "/information";
    private static Hashtable<String, IntegerEx> sequenceCounters = new Hashtable<String, IntegerEx>();
    private AccessManager accessManager = null;

    // ------------------------------------------
    // private member variables
    // ------------------------------------------
    private final SocketEngineCtrl m_EngineCtrl;

    /**
     * doGet
     */
    @Override
    public void doGet(HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) throws IOException,
            ServletException {

        httpResponse.setContentType("text/html");
        PrintWriter out = httpResponse.getWriter();

        out.println("<html>");
        out.println("<head>");
        out.println("<title>ControlServer</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>The ControlServer is working!</h1>");
        out.println("</body>");
        out.println("</html>");

        out.close();

    }

    // ------------------------------------------
    // public member functions
    // ------------------------------------------
    /** constructor */
    public ControlServer() {
        ControlServerProperties prop = ControlServerProperties.getInstance();

        m_EngineCtrl = new SocketEngineCtrl();
        m_EngineCtrl.initialize();

        // initialize CorpusLogger.
        ControlServerCorpusLogger.getInstance().initialize(prop);

        // Access control setting.
        this.accessManager = null;
        if (ControlServerProperties.getInstance().getAccessKeyControl()) {
            long minute = ControlServerProperties.getInstance()
                    .getAccessKeyUpdateMinute();
            if (minute > 0) {
                this.accessManager = new AccessManager(new Date(
                        minute * 1000 * 60));
                LOG.info("Access control is ON.");
            }
        }

        // Connected DB
        DBConnector.getInstance().connect();
    }

    // ------------------------------------------
    // private member functions
    // ------------------------------------------
    private void processRequest(HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) throws Exception {

        MCMLDoc inputMCMLDoc = MCMLDoc.createDocument();
        MCMLDoc outputMCMLDoc = MCMLDoc.createDocument();

        ArrayList<byte[]> outputBinaryList = new ArrayList<byte[]>();
        HttpSession session = httpRequest.getSession();
        String sessionId = session.getId();
        ServerComCtrl com = new ServerComCtrl();

        try {
            // receive MCML request
            ServletInputStream in = httpRequest.getInputStream();
            String url = httpRequest.getRemoteAddr();
            RequestData requestData = com.receiveRequesData(in, url, session);
            // set ArrivedTime First Frame.
            if (!requestData.hasXML() && !requestData.hasBinary()) {
                // Last Data Frame arrived(Multi Data send).
                ControlServerCorpusLogger.getInstance()
                        .setArrivedTimeForRequest(sessionId);
            }
            in.close();

            // get input MCML data
            if (requestData.hasXML()) {
                // set ArrivedTime First Frame.
                ControlServerCorpusLogger.getInstance()
                        .setArrivedTimeForBinary(sessionId);

                String xmlData = requestData.getXML();

                inputMCMLDoc = MCMLDoc.loadFromString(xmlData);

                // set IP address in the
                // <User><Transmitter><Device><Location><URI>
                ipSetInURI(inputMCMLDoc, httpRequest.getRemoteAddr());

                // set ClientIP address.
                ControlServerCorpusLogger.getInstance().setClientIP(sessionId,
                        httpRequest.getRemoteAddr());
                // create new session
                // httpRequest.getSession(true);
                httpRequest.getSession();
            }

            // get input binary data
            ArrayList<byte[]> inputBinaryList = requestData.getBinaryList();

            try {
                byte[] binarySize = requestData.getBinary();
                int iBinarySize = binarySize.length;
                LOG.debug("Binary size: " + iBinarySize);
            } catch (NullPointerException e) {
                LOG.debug("No binary (Header or footer)");
            }

            processMCMLRequest(httpRequest, httpResponse, inputMCMLDoc,
                    inputBinaryList, outputMCMLDoc, outputBinaryList);

        } catch (NullPointerException e) {
            LOG.error("ResponseContainer.getMcmlDoc() has failed.");
            MCMLException mcmlException = new MCMLException(
                    MCMLException.ERROR, MCMLException.ASR,
                    MCMLException.CS2_DOWN_NULLPOINTER);

            XMLTypeTools
                    .generateErrorResponse(mcmlException.getErrorCode(),
                            mcmlException.getExplanation(), inputMCMLDoc,
                            outputMCMLDoc);
        } catch (Exception exception) {
            LOG.error(exception.getMessage(), exception);

            // generate error response
            MCMLException mcmlException = new MCMLException(
                    MCMLException.ERROR, MCMLException.COMMON,
                    MCMLException.CS_COMPREHENSIVE_ERROR);

            XMLTypeTools
                    .generateErrorResponse(mcmlException.getErrorCode(),
                            mcmlException.getExplanation(), inputMCMLDoc,
                            outputMCMLDoc);
            // clear response binary data
            outputBinaryList.clear();

        } finally {
            String url = httpRequest.getRemoteAddr();
            sendMCMLResponse(httpResponse, com, outputMCMLDoc,
                    outputBinaryList, url, session);
        }

        return;
    }

    /**
     * Returns response processing to client
     * 
     * @param httpResponse
     * @param comCtrl
     * @param outputMCMLDoc
     * @param outputBinaryList
     * @param url
     * @param session
     * @throws IOException
     */
    private void sendMCMLResponse(HttpServletResponse httpResponse,
            ServerComCtrl comCtrl, MCMLDoc outputMCMLDoc,
            ArrayList<byte[]> outputBinaryList, String url, HttpSession session)
            throws IOException {

        try {
            String xmlData = outputMCMLDoc.saveToString(true);

            ServletOutputStream outputStream = httpResponse.getOutputStream();
            comCtrl.sendResponseData(outputStream, xmlData, outputBinaryList,
                    url, session);
            outputStream.close();

        } catch (Exception exception) {
            LOG.error(exception.getMessage(), exception);
        }
        return;
    }

    /**
     * Sends request processing to MCML
     * 
     * @param httpRequest
     * @param httpResponse
     * @param inputMCMLDoc
     * @param inputBinaryList
     * @param outputMCMLDoc
     * @param outputBinaryList
     * @throws Exception
     */
    private void processMCMLRequest(HttpServletRequest httpRequest,
            HttpServletResponse httpResponse, MCMLDoc inputMCMLDoc,
            ArrayList<byte[]> inputBinaryList, MCMLDoc outputMCMLDoc,
            ArrayList<byte[]> outputBinaryList) throws Exception {

        HttpSession session = httpRequest.getSession();

        try {
            String service = "";
            if (inputMCMLDoc != null && inputMCMLDoc.MCML.exists()) {
                service = XMLTypeTools.getService(inputMCMLDoc);
                session.setAttribute("Service", service);
            }

            if (XMLTypeTools.serviceIsDialogConnect(service)) {
                DialogController.doDialogConnect(httpRequest, inputMCMLDoc,
                        outputMCMLDoc);

            } else if (XMLTypeTools.serviceIsDialogDisconnect(service)) {
                DialogController.doDialogDisconnect(httpRequest, inputMCMLDoc,
                        outputMCMLDoc);

            } else if (XMLTypeTools.serviceIsDialog(service)) {
                DialogController.doDialog(httpRequest, inputMCMLDoc,
                        outputMCMLDoc);

            } else {
                m_EngineCtrl.processRequest(httpRequest, httpResponse,
                        inputMCMLDoc, inputBinaryList, outputMCMLDoc,
                        outputBinaryList);

                session = httpRequest.getSession(false);
                if (session != null) {
                    if (inputMCMLDoc != null && inputMCMLDoc.MCML.exists()) {
                        // save MCML request data
                        session.setAttribute("MCML_REQUEST", inputMCMLDoc);
                    } else {
                        // load MCML request data
                        inputMCMLDoc = (MCMLDoc) session
                                .getAttribute("MCML_REQUEST");
                    }
                }
            }
        } catch (MCMLException exception) {
            LOG.error(exception.getMessage(), exception);
            XMLTypeTools.generateErrorResponse(exception.getErrorCode(),
                    exception.getExplanation(), inputMCMLDoc, outputMCMLDoc);
            // clear response binary data
            outputBinaryList.clear();
            // output error log
            // oputputErrorLog(exception.);
        }
        return;
    }

    /**
     * set IP address in the <User><Transmitter><Device><Location><URI>
     * 
     * @param inputMCMLDoc
     * @param ipAddress
     * @throws Exception
     */
    private void ipSetInURI(MCMLDoc inputMCMLDoc, String ipAddress)
            throws Exception {

        MCMLType outputMCML = inputMCMLDoc.MCML.first();

        String transmitterURI = "";

        if (outputMCML.User.exists()) {

            if (outputMCML.User.first().Transmitter.exists()
                    && outputMCML.User.first().Transmitter.first().Device
                            .exists()
                    && outputMCML.User.first().Transmitter.first().Device
                            .first().Location.exists()
                    && outputMCML.User.first().Transmitter.first().Device
                            .first().Location.first().URI.exists()) {

                String stURI = outputMCML.User.first().Transmitter.first().Device
                        .first().Location.first().URI.first().getValue();
                transmitterURI = stURI;

                // already set IP address ?
                stURI = ipSetURI(stURI, ipAddress);

                outputMCML.User.first().Transmitter.first().Device.first().Location
                        .first().URI.first().setValue(stURI);
            }

            if (outputMCML.User.first().Receiver.exists()
                    && outputMCML.User.first().Receiver.first().Device.exists()
                    && outputMCML.User.first().Receiver.first().Device.first().Location
                            .exists()
                    && outputMCML.User.first().Receiver.first().Device.first().Location
                            .first().URI.exists()) {

                String stURI = outputMCML.User.first().Receiver.first().Device
                        .first().Location.first().URI.first().getValue();
                if (transmitterURI.equals(stURI)) {
                    // already set IP address ?
                    stURI = ipSetURI(stURI, ipAddress);

                    outputMCML.User.first().Receiver.first().Device.first().Location
                            .first().URI.first().setValue(stURI);
                }
            }
        }
        return;
    }

    /**
     * @param stURI
     * @param ipAddress
     * @return DeviceType
     * @throws Exception
     */
    private String ipSetURI(String stURI, String ipAddress) throws Exception {
        // IP Address Format
        String regex = "^(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[01]?\\d\\d|2[0-4]\\d|25[0-5])$";

        // already set IP address ?
        String[] splitURI = stURI.split("_");
        // if already set IP address, don't do anything.
        for (int i = 0; i < splitURI.length; i++) {
            // Regular expression: IP address format
            Pattern pattern = Pattern.compile(regex);

            if (pattern.matcher(splitURI[i]).matches()) {
                // Nothing
                return stURI;
            }
        }

        stURI = stURI + "_" + ipAddress;
        return stURI;
    }

    /**
     * This is the overridden doPost method.
     * 
     * @param httpRequest
     * @param httpResponse
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) throws ServletException,
            IOException {

        boolean bLast = false;
        boolean bDebug = false;
        boolean bRefresh = false;
        boolean bInfo = false;
        String sessionId = "";

        try {
            // Gets session id.
            HttpSession session = httpRequest.getSession(true);
            sessionId = session.getId();

            if (session.getAttribute("ProcessingTime") == null) {
                session.setAttribute("ProcessingTime", new Date());
            }

            // Gets URI.
            String value = (String) httpRequest.getRequestURI();
            value = value.toLowerCase();

            // Gets debug mark.

            // For band control.
            bRefresh = value.endsWith(this.refreshMark);
            if (bRefresh) {
                session.setAttribute(this.refreshKey, new IntegerEx(0));

                return;
            }

            bInfo = value.endsWith(this.infoMark);
            if (bInfo) {
                RequestDispatcher rd = getServletContext()
                        .getRequestDispatcher("/InformationServlet");

                if (rd != null) {
                    if (this.accessManager != null) {
                        String path = httpRequest.getServletPath();
                        String key = this.accessManager
                                .generateAccessKey(sessionId);
                        path = path + "/" + key;
                        httpRequest.setAttribute(Constants.ACCESS_KEY, path);

                        if (bDebug) {
                            System.out.println("@@ ACCESS_KEY: " + path);
                        }
                    } else {
                    }

                    rd.forward(httpRequest, httpResponse);
                    return;
                } else {
                    // Coding error.
                    session.invalidate();
                    throw new IllegalStateException(
                            "Information request dispatch error.");
                }
            }

            // Access privilege check.
            boolean bValidKey = false;
            try {
                if (this.accessManager == null) {
                    bValidKey = true;

                } else {
                    StringBuffer sb = httpRequest.getRequestURL();
                    String url = sb.toString();
                    String cpath = httpRequest.getContextPath();
                    String spath = httpRequest.getServletPath();

                    String key = url.substring(url.indexOf(cpath + spath)
                            + (cpath + spath).length());

                    if (key.length() != 0) {

                        // Key must exist between "/"s.
                        int ind = key.indexOf("/", 1);
                        if (ind == -1) {
                            key = key.substring(1);
                        } else {
                            key = key.substring(1, ind);
                        }

                        if (this.accessManager.checkValidity(key, false)) {
                            bValidKey = true;
                        }
                    }
                }
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);

                bValidKey = false;

            } finally {
                if (!bValidKey) {

                    sequenceCounters.remove(sessionId);
                    session.invalidate();
                    httpResponse.sendError(800);
                    return;
                }
            }

            if (bDebug) {
                System.out.println("@@ Key check passed.");
            }

            // If this is chunked transfer encoding, jumps to the method
            // processChunkedMultipartRequest.
            String transferEncoding = httpRequest
                    .getHeader(MimeData.HEADER_TRANSFER_ENCODING);
            if (transferEncoding == null) {
                transferEncoding = httpRequest.getHeader("X-"
                        + MimeData.HEADER_TRANSFER_ENCODING);
            }
            if (transferEncoding != null) {
                if (transferEncoding.equals(MimeData.TRANSFER_ENCODING_CHUNKED)) {

                    if (bDebug) {
                        System.out.println("@@ Chunked data received.");
                    }
                    try {
                        processChunkedMultipartRequest(httpRequest,
                                httpResponse);
                    } catch (Exception ex) {
                        session.invalidate();
                        ex.printStackTrace();
                        throw new IllegalStateException(ex);
                    }
                    return;
                }
            }

            int index = value.lastIndexOf(this.frameCounterName);
            if (index != -1) {

                value = value.substring(index + 2);

                bLast = value.endsWith(this.lastMark);

                if (bLast) {
                    value = value.replaceAll(this.lastMark, "");
                }

            } else {

                processRequest(httpRequest, httpResponse);
                return;
            }

            long start = System.currentTimeMillis();
            int currentFrame = Integer.parseInt(value);

            while (true) {
                try {
                    // Checks timeout.
                    long current = System.currentTimeMillis();
                    if ((current - start) > 15000) {
                        throw new IllegalStateException("Time out. (data: "
                                + currentFrame + ")");
                    }

                    // Checks turn number.
                    IntegerEx previousFrame = sequenceCounters.get(sessionId);
                    if (previousFrame == null) { // First data comes.
                        previousFrame = new IntegerEx(0);
                        sequenceCounters.put(sessionId, previousFrame);
                    }

                    synchronized (previousFrame) {

                        if ((previousFrame.intValue() + 1) == currentFrame) {
                            // It is my turn.

                            if (bLast) { // All data was received.
                                sequenceCounters.remove(sessionId);

                            } else {
                                previousFrame.setInt(currentFrame);
                                sequenceCounters.put(sessionId, previousFrame);
                            }

                            // Do the service.
                            processRequest(httpRequest, httpResponse);

                            break;

                        } else {

                            if (currentFrame - previousFrame.intValue() > 2) {
                                throw new IllegalStateException(
                                        "Timeout discarding of frame.");
                            } else if (currentFrame - previousFrame.intValue() <= 0) {
                                throw new IllegalStateException(
                                        "Timeout discarding of frame.");
                            }

                            // Wait until my turn come.
                            previousFrame.wait(200);

                        }
                    }
                } catch (Exception e) {
                    sequenceCounters.remove(sessionId);
                    session.invalidate();

                    e.printStackTrace();
                    throw new IllegalStateException(e);
                }
            }
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);

        } catch (Exception exception) {
            // output error log
            LOG.error(exception.getMessage(), exception);
        } finally {
            if (bDebug) {
                System.out.println("@@ Servlet end. <"
                        + Thread.currentThread().getId() + ":" + sessionId
                        + ">");
            }
        }
    }

    // Finds the target binary data in the source binary data.
    private int binaryIndexOf(String target, ByteBuffer data)
            throws UnsupportedEncodingException {

        byte[] targetData = target.getBytes();
        int targetLength = targetData.length;

        int i, j = 0;
        for (i = data.position(); i < data.limit();) {
            if (data.get(i) == targetData[j]) {
                j++;
                i++;
                if (j == targetLength) {
                    break;
                }
            } else {
                if (j == 0) {
                    i++;
                } else {
                    i = i - j + 1;
                    j = 0;
                }
            }
        }
        if (i == data.limit() && j != targetLength) {
            return -1;
        }
        return i - targetLength;
    }

    // Processes chunked multipart HTTP request.
    private void processChunkedMultipartRequest(HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) throws IOException,
            MessagingException, Exception { // overlapped enumeration on
                                            // purpose.

        final Boolean bDebug = false; // test flag.
        final String boundaryStart = "boundary=\"";
        final String boundaryEnd = "\"";
        final String emptyLine = "\r\n\r\n";

        int startIndex;
        int endIndex;

        httpRequest.getSession(true);

        String contentType = httpRequest
                .getHeader(MimeData.HEADER_CONTENT_TYPE);
        if (contentType == null) {
            throw new IllegalStateException("No Content-Type.");
        }
        if (contentType.indexOf(MimeData.CONTENT_TYPE_MULTIPART_MIXED) == -1) {
            throw new IllegalStateException("Unsupported Content-Type.");
        }
        startIndex = contentType.indexOf(boundaryStart);
        if (startIndex == -1) {
            throw new IllegalStateException(
                    "No boundary for multipart delimiter.");
        }
        startIndex += boundaryStart.length();
        if (startIndex >= contentType.length()) {
            throw new IllegalStateException("Invalid boundary.");
        }
        endIndex = contentType.indexOf(boundaryEnd, startIndex);
        if (endIndex == -1) {
            throw new IllegalStateException(
                    "No boundary for multipart delimiter.");
        }

        String boundary = "\r\n--"
                + contentType.substring(startIndex, endIndex); // get it from
                                                               // contentType.

        final InputStream is = httpRequest.getInputStream();
        final int defaultSize = 128 * 1024;

        // receive request
        int readLength;
        byte[] readBuffer = new byte[4 * 1024];

        boolean isFirstPart;
        boolean isLastPart;
        ByteBuffer bodyData;
        int currentParsingStage;

        ByteBuffer bb = ByteBuffer.allocate(defaultSize);
        HashMap<String, String> map = new HashMap<String, String>();
        Code code = new Code();

        final String firstLine = "\r\n";
        final String lineEnd = "\r\n";

        bb.put(firstLine.getBytes());

        isFirstPart = true;
        isLastPart = false;
        currentParsingStage = 0;
        bodyData = null;
        byte[] bodyBytes = null;
        int previousLength = 0;
        boolean bFirstLoop = true;

        while (true) {
            // Gets a part.
            // Tomcat removes chunk header number.
            readLength = is.read(readBuffer);
            if (readLength != -1) {
                if (bDebug) {
                    System.out.println("\n▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽▽");
                    for (int i = 0; i < readLength; i++) {
                        System.out.print(""
                                + Character.toString((char) readBuffer[i]));
                    }
                    System.out.println("△ Read bytes: " + readLength);
                    System.out.println("△△△△△△△△△△△△△△△△△△");
                }

                if (bFirstLoop) {
                    bFirstLoop = false;
                    if (!(readBuffer[0] == '-' && readBuffer[1] == '-')) {
                        String prefix = boundary + "\r\n";
                        if (bb.limit() - bb.position() >= prefix.length()) {
                            bb.put(prefix.getBytes(MimeData.CHARSET_NAME), 0,
                                    prefix.length());
                        } else {
                            ByteBuffer newBb = ByteBuffer.allocate(bb
                                    .capacity() * 2);
                            newBb.put(prefix.getBytes(MimeData.CHARSET_NAME),
                                    0, prefix.length());
                            bb = newBb;
                        }
                    }
                }

                if (bb.limit() - bb.position() >= readLength) {
                    System.out
                            .println("bb.limit() - bb.position() >= readLength");
                    bb.put(readBuffer, 0, readLength);
                } else {
                    System.out
                            .println("!(bb.limit() - bb.position() >= readLength)");
                    ByteBuffer newBb = ByteBuffer.allocate(bb.capacity() * 2);
                    bb.limit(bb.position());
                    bb.position(0);
                    newBb.put(bb);
                    newBb.put(readBuffer, 0, readLength);
                    bb = newBb;
                }
            } else {
                if (bDebug) {
                    System.out.println("read end.");
                }
                if (bb.limit() - bb.position() == previousLength) {
                    break;
                } else {
                    previousLength = bb.limit() - bb.position();
                }
            }

            // Finds bondary in body.
            boolean waitForData = false;
            boolean isEncrypted = false;

            while (true) {
                if (currentParsingStage == 0) {
                    if (bDebug) {
                        System.out
                                .println("currentParsingStage = 0, Finding first boundary.");
                    }
                    // Init.
                    isLastPart = false;
                    bodyData = null;
                    bodyBytes = null;
                    map.clear();

                    // Find the boundary for the beginning of a part.
                    ByteBuffer bb2 = bb.duplicate();
                    bb2.position(0);
                    bb2.limit(bb.position());
                    int tempIndex;
                    tempIndex = binaryIndexOf(boundary, bb2);
                    if (tempIndex == -1) {
                        waitForData = true;
                        break;
                    }
                    startIndex = tempIndex;
                    startIndex += boundary.length(); // The beginning of a part
                                                     // which contains CRLF.
                    currentParsingStage = 1;

                    if (bb2.limit() - startIndex >= 2) {
                        bb2.position(startIndex);
                        if (bb2.get(startIndex) == '-'
                                && bb2.get(startIndex + 1) == '-') {
                            if (bDebug) {
                                System.out.println("Last -- found.");
                            }
                            throw new IllegalStateException(
                                    "Invalid last part.");
                        }
                    }

                } else if (currentParsingStage == 1) {
                    if (bDebug) {
                        System.out
                                .println("currentParsingStage = 1, Finding the boundary between header and body.");
                    }

                    // Find an empty line.
                    ByteBuffer bb2 = bb.duplicate();
                    bb2.limit(bb2.position());
                    bb2.position(startIndex);
                    int tempIndex;
                    tempIndex = binaryIndexOf(emptyLine, bb2);
                    if (tempIndex == -1) {
                        waitForData = true;
                        break;
                    }
                    startIndex = tempIndex;
                    if (bDebug) {
                        System.out.println("CRLF index: " + startIndex);
                    }
                    bb2.limit(startIndex);

                    String headerFields = new String(bb2.array(),
                            bb2.position(), bb2.limit() - bb2.position()); // Entity
                                                                           // headers.
                    if (bDebug) {
                        System.out.println("headerData : " + headerFields);
                    }

                    String[] fields = headerFields.split(lineEnd);

                    for (int i = 0; i < fields.length; i++) {
                        if (fields[i].trim().isEmpty()) {
                            continue; // for
                        }
                        String[] keyValue = fields[i].split(":");
                        if (keyValue.length < 2) {
                            continue;
                        }
                        String key = keyValue[0].trim();
                        if (key.isEmpty()) {
                            continue; // for
                        }
                        String value = keyValue[1].trim();
                        map.put(key, value);
                    }

                    // entity body.
                    currentParsingStage = 2;

                } else if (currentParsingStage == 2) {
                    if (bDebug) {
                        System.out
                                .println("currentParsingStage = 2, finding the body.");
                    }
                    int length = 0;
                    int orgStartIndex = startIndex;

                    String contentLength = map
                            .get(MimeData.HEADER_CONTENT_LENGTH);
                    if (contentLength != null) {
                        length = Integer.parseInt(contentLength);
                        // The beginning of an entity body.
                        if (length != 0) {
                            startIndex += emptyLine.length();
                        } else {
                            startIndex += lineEnd.length();
                        }
                    }
                    // Find the boundary for the end of a part.
                    ByteBuffer bb2 = bb.duplicate();
                    bb2.limit(bb2.position());
                    bb2.position(startIndex); // The beginning of an entity
                                              // body.
                    int tempIndex;
                    tempIndex = binaryIndexOf(boundary, bb2);
                    if (tempIndex == -1) {
                        waitForData = true;
                        startIndex = orgStartIndex;
                        break;
                    }
                    endIndex = tempIndex; // The end of the body.
                    bb2.limit(endIndex); // bb2 is a part.
                    bodyData = bb2.slice(); // The entity body has been cut out.

                    int bodyLength = endIndex - startIndex;
                    if (bDebug) {
                        System.out.println("Body bodylength: " + bodyLength
                                + " endIndex:" + endIndex + " startIndex:"
                                + startIndex);
                        System.out.println("Body length: " + length);
                    }
                    if (bodyLength != length) {
                        throw new IllegalStateException("Bad Content-Length.");
                    }
                    bodyBytes = new byte[bodyLength];
                    for (int c = 0, p = bodyData.position(); c < bodyData
                            .limit(); c++, p++) {
                        bodyBytes[c] = bodyData.get(p);
                    }
                    // Prepares next buffer with the remaining data.
                    bb.limit(bb.position());
                    bb.position(endIndex);
                    ByteBuffer nextBb = ByteBuffer.allocate(defaultSize);
                    nextBb.put(bb);
                    bb = nextBb;
                    currentParsingStage = 0;
                    break;
                    // Go to the next proccess below.

                } else {
                    throw new IllegalStateException("Coding error.");
                }
            } // while
            if (waitForData) {
                continue;
            }

            contentType = map.get(MimeData.HEADER_CONTENT_TYPE);
            String encoding = map
                    .get(MimeData.HEADER_CONTENT_TRANSFER_ENCODING);
            String xencType = map.get(MimeData.HEADER_CONTENT_ENCRYPTION_TYPE);
            String contentLength = map.get(MimeData.HEADER_CONTENT_LENGTH);
            if (bDebug) {
                System.out.println("Part#Content-Type: " + contentType); // application/octet-stream
                System.out.println("Part#Content-Transfer-Encoding: "
                        + encoding); // binary
                System.out.println("Part#X-Content-Encryption-Type: "
                        + xencType); // enctype02
                System.out.println("Part#Content-Length: " + contentLength); // <integer>
            }

            if (contentLength != null) {
                isLastPart = (Integer.parseInt(contentLength) == 0) ? true
                        : false;
            }

            if (bodyBytes == null) {
                throw new IllegalStateException("Coding error.");
            }
            if (xencType != null
                    && xencType
                            .equalsIgnoreCase(MimeData.ENCRYPTION_TYPE_ENCTYPE02)) {
                isEncrypted = true;
                code.decode(bodyBytes);
            }
            if (bDebug) {
                for (int i = 0; i < bodyBytes.length; i++) {
                    System.out.print(""
                            + Character.toString((char) bodyBytes[i])); // Debugging.
                }
            }
            if (bodyBytes == null) {
                throw new IllegalStateException("Coding error.");
            }

            MCMLDoc inputMCML = MCMLDoc.createDocument();
            MCMLDoc outputMCML = MCMLDoc.createDocument();

            ArrayList<byte[]> inputBinaryList = new ArrayList<byte[]>();

            final String sessionId = httpRequest.getSession().getId();

            if (isFirstPart) {
                ControlServerCorpusLogger.getInstance()
                        .setArrivedTimeForRequest(sessionId);

                isFirstPart = false;
                String xml = new String(bodyBytes, 0, bodyBytes.length,
                        MimeData.CHARSET_NAME);

                if (bDebug) {
                    System.out.println("INPUT XML: " + xml);
                }
                if (xml != null && !xml.isEmpty()) {
                    inputMCML = MCMLDoc.loadFromString(xml);
                } else {
                    throw new IllegalStateException("No MCML XML.");
                }
            } else {
                if (bodyBytes.length > 0) {
                    if (bDebug) {
                        System.out.println("isFirstPart == FALSE"
                                + bodyBytes.length + "byte");
                    }
                    inputBinaryList.add(bodyBytes);
                } else {
                    if (bDebug) {
                        System.out.println("isFirstPart == FALSE 0byte");
                    }
                }
            }

            // Send MCML to the server.

            // create output memory
            ArrayList<byte[]> outputBinaryList = new ArrayList<byte[]>();

            // Last Data Frame arrived(Multi Data send).
            ControlServerCorpusLogger.getInstance().setArrivedTimeForBinary(
                    sessionId);

            // pending:return value check
            m_EngineCtrl.processRequest(httpRequest, httpResponse, inputMCML,
                    inputBinaryList, outputMCML, outputBinaryList);

            if (isLastPart || XMLTypeTools.hasResponse(outputMCML)
                    || XMLTypeTools.hasError(outputMCML)) {
                final ServerComCtrl com = new ServerComCtrl();

                String xml = outputMCML.saveToString(true);
                if (bDebug) {
                    System.out.println("OUTPUT XML: " + xml);
                }

                // send response
                ServletOutputStream out = httpResponse.getOutputStream();
                String url = httpRequest.getRemoteAddr();
                HttpSession session = httpRequest.getSession();
                sendMCMLResponse(httpResponse, com, outputMCML,
                        outputBinaryList, url, session);
                out.close();

                // remove log information Map on session id
                if (XMLTypeTools.hasResponse(outputMCML)
                        || XMLTypeTools.hasError(outputMCML)) {
                    ControlServerCorpusLogger.getInstance().removeLogInfoMap(
                            sessionId);
                }

                break; // multipart loop.
            }

        } // while
    }

}
