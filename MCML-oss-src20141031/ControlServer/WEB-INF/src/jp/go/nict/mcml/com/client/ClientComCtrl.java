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

package jp.go.nict.mcml.com.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.mail.MessagingException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpSession;

import jp.go.nict.mcml.com.ComCtrl;

import org.apache.log4j.Logger;

/**
 * ClientComCtrl class.
 * 
 * @version 4.0
 * @since 20120921
 */
public class ClientComCtrl extends ComCtrl {
    private static final Logger LOG = Logger.getLogger(ClientComCtrl.class
            .getName());
    // ------------------------------------------
    // private member constant
    // ------------------------------------------
    private static final String HEADER_SEND_COOKIE = "Cookie";
    private static final String HEADER_RECEIVE_COOKIE = "Set-Cookie";
    private static final String STRING_URL_HTTP = "http://";
    private static final String STRING_URL_HTTPS = "https://";
    private URLConnection m_Connection;

    // ------------------------------------------
    // protected member variable
    // ------------------------------------------
    protected String m_Cookie;
    protected Proxy m_Proxy;
    protected int m_TimeoutMilliSeconds;
    protected boolean m_IsUsingSelfSignedCertification;

    // ------------------------------------------
    // public member function
    // ------------------------------------------

    /**
     * constructor
     */
    public ClientComCtrl() {
        m_Cookie = null;
        m_Proxy = null;
        m_TimeoutMilliSeconds = 0;
        m_IsUsingSelfSignedCertification = true; // false ;
        m_Connection = null;
    }

    /**
     * constructor for use proxy
     * 
     * @param httpProxyHost
     * @param httpProxyPort
     */
    public ClientComCtrl(String httpProxyHost, int httpProxyPort) {
        SocketAddress addr = new InetSocketAddress(httpProxyHost, httpProxyPort);
        m_Proxy = new Proxy(Proxy.Type.HTTP, addr);
    }

    /**
     * execute request (multi data version)
     * 
     * @param url
     * @param xmlData
     * @param binaryDataList
     * @param session
     * @return ResponseData
     * @throws MalformedURLException
     * @throws IOException
     * @throws MessagingException
     */
    public ResponseData request(String url, String xmlData,
            ArrayList<byte[]> binaryDataList, HttpSession session)
            throws MalformedURLException, IOException, MessagingException {

        // select URL connection
        try {
            m_Connection = selectURLConnection(url);
            if (m_Connection == null) {
                throw new MalformedURLException();
            }
        } catch (NoSuchAlgorithmException e) {
            LOG.error(e.getMessage(), e);
            return null;
        } catch (KeyManagementException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }

        // specify output
        m_Connection.setDoOutput(true);

        if (m_TimeoutMilliSeconds > 0) {
            // set timeout
            m_Connection.setConnectTimeout(m_TimeoutMilliSeconds);
            m_Connection.setReadTimeout(m_TimeoutMilliSeconds);
        }

        if (m_Cookie != null) {
            m_Connection.setRequestProperty(HEADER_SEND_COOKIE, m_Cookie);
        }

        // send request MIME message
        OutputStream out = m_Connection.getOutputStream();
        super.send(out, xmlData, binaryDataList, url, session);
        out.close();

        int threadCount = (Integer) session.getAttribute("ThreadCount");
        String[] arrayDataEnd = (String[]) session.getAttribute("DataEnd");
        if (xmlData == null && binaryDataList.size() == 0) {
            arrayDataEnd[threadCount] = "yes";
        } else if (xmlData != null && binaryDataList != null
                && binaryDataList.size() == 1) {
            arrayDataEnd[threadCount] = "yes";
        }
        if (session.getAttribute("Service") != null
                && !session.getAttribute("Service").equals("ASR")) {
            arrayDataEnd[threadCount] = "yes";
        }
        session.setAttribute("DataEnd", arrayDataEnd);

        String[] arrayDestination = (String[]) session
                .getAttribute("Destination");
        Date[] arrayRequestSendTime = (Date[]) session
                .getAttribute("RequestSendTime");

        arrayDestination[threadCount] = url;
        arrayRequestSendTime[threadCount] = new Date();

        session.setAttribute("Destination", arrayDestination);
        session.setAttribute("RequestSendTime", arrayRequestSendTime);

        if (arrayDataEnd[threadCount] != null
                && arrayDataEnd[threadCount].equals("yes")) {
            SimpleDateFormat sdf = new SimpleDateFormat(
                    "yyyy/MM/dd HH:mm:ss.SSS");
            Date requestProcessStartTime = (Date) session
                    .getAttribute("ProcessingTime");
            LOG.debug("[" + session.getId() + "] RequestProcessStartTime:"
                    + sdf.format(requestProcessStartTime));
            LOG.debug("[" + session.getId() + "] RequestSendTime["
                    + arrayDestination[threadCount] + "]: "
                    + sdf.format(arrayRequestSendTime[threadCount]));

            String service = "";
            if (session.getAttribute("Service") != null) {
                service = (String) session.getAttribute("Service");

            }

            String printText = "";
            printText = printText + "[" + session.getId() + "] ";
            printText = printText + "[ProcessTime][" + service
                    + "]ClientRequest->SendEngineSide["
                    + arrayDestination[threadCount] + "]: ";
            printText = printText
                    + (arrayRequestSendTime[threadCount].getTime() - requestProcessStartTime
                            .getTime()) + "msec ";
            LOG.info(printText);
        }
        // receive response MIME message
        ResponseData retVal = new ResponseData();
        InputStream in = m_Connection.getInputStream();
        super.receive(in, retVal, url, session);
        in.close();

        Date[] arrayResponseReceiveTime = (Date[]) session
                .getAttribute("ResponseReceiveTime");

        if (arrayResponseReceiveTime[threadCount] == null) {
            arrayResponseReceiveTime[threadCount] = new Date();
        }
        session.setAttribute("ResponseReceiveTime", arrayResponseReceiveTime);

        String cookie = m_Connection.getHeaderField(HEADER_RECEIVE_COOKIE);

        if (cookie != null && !cookie.isEmpty()) {
            m_Cookie = cookie;
        }

        // success
        return retVal;
    }

    /**
     * execute request (single data version)
     * 
     * @param url
     * @param xmlData
     * @param binaryData
     * @param session
     * @return ResponseData
     * @throws MalformedURLException
     * @throws IOException
     * @throws MessagingException
     */
    public ResponseData request(String url, String xmlData, byte[] binaryData,
            HttpSession session) throws MalformedURLException, IOException,
            MessagingException {
        ArrayList<byte[]> binaryDataList = new ArrayList<byte[]>();
        if (binaryData != null) {
            binaryDataList.add(binaryData);
        }
        throw new IOException();
    }

    /**
     * execute request (not use binary data)
     * 
     * @param url
     * @param xmlData
     * @param session
     * @return ResponseData
     * @throws MalformedURLException
     * @throws IOException
     * @throws MessagingException
     */
    public ResponseData request(String url, String xmlData, HttpSession session)
            throws MalformedURLException, IOException, MessagingException {
        return request(url, xmlData, (byte[]) null, session);
    }

    /**
     * execute request (not use xml data & multi data version)
     * 
     * @param url
     * @param binaryDataList
     * @param session
     * @return ResponseData
     * @throws MalformedURLException
     * @throws IOException
     * @throws MessagingException
     */
    public ResponseData request(String url, ArrayList<byte[]> binaryDataList,
            HttpSession session) throws MalformedURLException, IOException,
            MessagingException {
        return request(url, null, binaryDataList, session);
    }

    /**
     * execute request (not use xml data & single data version)
     * 
     * @param url
     * @param binaryData
     * @param session
     * @return ResponseData
     * @throws MalformedURLException
     * @throws IOException
     * @throws MessagingException
     */
    public ResponseData request(String url, byte[] binaryData,
            HttpSession session) throws MalformedURLException, IOException,
            MessagingException {
        return request(url, null, binaryData, session);
    }

    /**
     * execute request (not use xml data & not use binary data)
     * 
     * @param url
     * @param session
     * @return ResponseData
     * @throws MalformedURLException
     * @throws IOException
     * @throws MessagingException
     */
    public ResponseData request(String url, HttpSession session)
            throws MalformedURLException, IOException, MessagingException {
        return request(url, null, (byte[]) null, session);
    }

    /**
     * set time out value
     * 
     * @param timeoutMilliSeconds
     * @return {@code true}
     *         <ul>
     *         Set timeout time.
     *         </ul>
     *         {@code false}
     *         <ul>
     *         Time during which parameter was incorrect (smaller than {@literal 0})
     *         </ul>
     */
    public boolean setTimeout(int timeoutMilliSeconds) {
        if (timeoutMilliSeconds < 0) {
            // illegal argument
            return false;
        }

        m_TimeoutMilliSeconds = timeoutMilliSeconds;

        // success
        return true;
    }

    /**
     * using self signed certification on https
     */
    public void setUsingSelfSignedCertification() {
        m_IsUsingSelfSignedCertification = true;
    }

    /**
     * close URL connection
     */
    public void closeURLConnection() {
        if (m_Connection != null) {
            HttpURLConnection httpURLConnection = (HttpURLConnection) m_Connection;
            httpURLConnection.disconnect();
        }
    }

    // ------------------------------------------
    // private member function
    // ------------------------------------------

    /**
     * select URL Connection
     * 
     * @param url
     * @return URLConnection
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    private URLConnection selectURLConnection(String url) throws IOException,
            NoSuchAlgorithmException, KeyManagementException {
        // analyze url
        if (url.startsWith(STRING_URL_HTTP)) {
            // select http
            HttpURLConnection httpConnection = (HttpURLConnection) openURLConnection(url);
            return httpConnection;
        } else if (url.startsWith(STRING_URL_HTTPS)) {
            // select https
            HttpsURLConnection httpsConnection = (HttpsURLConnection) openURLConnection(url);
            // use self signed certification
            if (m_IsUsingSelfSignedCertification) {
                // correct authentication result of certificate
                correctAuthenticationResultofCertificate(httpsConnection, url);
            }
            return httpsConnection;
        } else {
            // no such protocol
            return null;
        }
    }

    /**
     * open URL connection
     * 
     * @param url
     * @return Connection
     * @throws IOException
     */
    private URLConnection openURLConnection(String url) throws IOException {
        // connection to servlet
        URL urlObj = new URL(url);
        URLConnection connection;

        if (m_Proxy != null) {
            connection = urlObj.openConnection(m_Proxy);
        } else {
            connection = urlObj.openConnection();
        }
        return connection;
    }

    /**
     * correct authentication result of certificate
     * 
     * @param httpsConnection
     * @param url
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    private void correctAuthenticationResultofCertificate(
            HttpsURLConnection httpsConnection, String url)
            throws NoSuchAlgorithmException, KeyManagementException {
        // get hostName
        final String hostName = getHostNameFromUrl(url);
        // destination host name is unknown
        if (hostName == null) {
            return;
        }

        // correct authentication result
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String host, SSLSession ssl) {
                // Is destination host name equals authentication host name?
                if (hostName.equals(host)) {
                    // correct result(fail->success)
                    return true;
                }
                // not correct result
                return false;
            }
        });

        // authentication result notify to java
        KeyManager[] km = null;
        TrustManager[] tm = { new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1)
                    throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1)
                    throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        } };
        // setting HTTPS context
        SSLContext sslcontext = SSLContext.getInstance("SSL");
        sslcontext.init(km, tm, new SecureRandom());
        httpsConnection.setSSLSocketFactory(sslcontext.getSocketFactory());
    }

    /**
     * get host name from URL
     * 
     * @param url
     * @return Host name
     */
    private String getHostNameFromUrl(String url) {
        String hostName = null;

        if (url.startsWith(STRING_URL_HTTPS)) {
            int hostNameStartIndex = STRING_URL_HTTPS.length();
            int hostNameEndIndex1;
            int hostNameEndIndex2;
            int hostNameEndIndex = -1;

            hostNameEndIndex1 = url.indexOf(":", hostNameStartIndex);
            hostNameEndIndex2 = url.indexOf("/", hostNameStartIndex);

            // On the specification of URL, ":" must be earlier than "/"
            // checked.
            // URL is "https://hostname:portNumber/..." or "https://hostname/"
            if (hostNameEndIndex1 != -1) {
                hostNameEndIndex = hostNameEndIndex1;
            } else if (hostNameEndIndex2 != -1) {
                hostNameEndIndex = hostNameEndIndex2;
            }
            if (hostNameEndIndex != -1) {
                hostName = url.substring(hostNameStartIndex, hostNameEndIndex);
            }
        }
        return hostName;
    }

    /**
     * Request not as MIME but as normal character string. Mainly for information.
     * 
     * @param url
     *            String
     * @param xmlData
     *            String
     * @return ResponseData
     * @throws MalformedURLException
     * @throws IOException
     * @throws MessagingException
     */
    public ResponseData requestNoMime(String url, String xmlData)
            throws MalformedURLException, IOException, MessagingException {
        HttpURLConnection connection = null;
        ResponseData retVal = new ResponseData();

        boolean isReceiveOK = false;

        while (true) {
            // connection to servlet
            URL urlObj = new URL(url);
            if (m_Proxy != null) {
                connection = (HttpURLConnection) urlObj.openConnection(m_Proxy);
            } else {
                connection = (HttpURLConnection) urlObj.openConnection();
            }

            // If using URLConnection, set 10 seconds for timeout.
            // Timeout period seems infinite.
            connection.setConnectTimeout(10 * 1000);
            connection.setReadTimeout(10 * 1000);

            // specify output
            connection.setDoOutput(true);

            if (m_Cookie != null) {
                connection.setRequestProperty(HEADER_SEND_COOKIE, m_Cookie);
            }

            // send request message
            OutputStream out = connection.getOutputStream();

            super.sendNoMime(out, xmlData);
            out.close();

            // receive response message
            InputStream in = null;
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                in = connection.getInputStream();
            } else {
                // Receives error message when error occurs.
                in = connection.getErrorStream();
            }

            isReceiveOK = super.receiveNoMime(in, retVal);
            in.close();

            if (!isReceiveOK) {
                System.out.println("Receive failed. Retry...");
                continue;
            } else {
                break;
            }

        }

        String cookie = connection.getHeaderField(HEADER_RECEIVE_COOKIE);
        if (cookie != null && cookie.length() != 0) {
            m_Cookie = cookie;
        }

        // success
        return retVal;
    }

    /**
     * execute request (multi data version)
     * 
     * @param url
     * @param xmlData
     * @param session
     * @param binaryList
     * @return ResponseData
     * @throws MalformedURLException
     * @throws IOException
     * @throws MessagingException
     */
    public ResponseData requestLogServer(String url, String xmlData,
            HttpSession session, ArrayList<byte[]> binaryList)
            throws MalformedURLException, IOException, MessagingException {

        // select URL connection
        try {
            m_Connection = selectURLConnection(url);
            if (m_Connection == null) {
                return null;
            }
        } catch (NoSuchAlgorithmException e) {
            LOG.error(e.getMessage(), e);
            return null;
        } catch (KeyManagementException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }

        // specify output
        m_Connection.setDoOutput(true);

        if (m_TimeoutMilliSeconds > 0) {
            // set timeout
            m_Connection.setConnectTimeout(m_TimeoutMilliSeconds);
            m_Connection.setReadTimeout(m_TimeoutMilliSeconds);
        }

        if (m_Cookie != null) {
            m_Connection.setRequestProperty(HEADER_SEND_COOKIE, m_Cookie);
        }

        // send request MIME message
        OutputStream out = m_Connection.getOutputStream();
        super.sendLogServer(out, xmlData, url, session, binaryList);
        out.close();

        // receive response MIME message
        ResponseData retVal = new ResponseData();
        InputStream in = m_Connection.getInputStream();
        super.receive(in, retVal, url, session);
        in.close();

        String cookie = m_Connection.getHeaderField(HEADER_RECEIVE_COOKIE);

        if (cookie != null && !cookie.isEmpty()) {
            m_Cookie = cookie;
        }

        // success
        return retVal;
    }

}
