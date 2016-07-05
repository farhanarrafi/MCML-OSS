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

package jp.go.nict.security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * This class magages user access control to web services.
 */
public class AccessManager {

    /**
     * Inner utility class.
     */
    protected static class Pair {
        public Pair(String key, Long value) {
            this.key = key;
            this.value = value;
        }

        public String key;
        public Long value;
    }

    private static final int KEY_MAX_LENGTH = 10;
    private final boolean bDebug = false;
    private boolean accessControl;
    private long updateTime;
    private LinkedList<Pair> keyAndDate = new LinkedList<Pair>();

    /**
     * AccessManager Constructor.
     * 
     * @param updateTime
     *            the effective
     */
    public AccessManager(Date updateTime) {

        if (bDebug) {
            System.out.println("@@ Access control: " + updateTime);
        }

        if (updateTime != null) {
            accessControl = true;
        } else {
            accessControl = false;
        }

        if (updateTime != null) {
            this.updateTime = updateTime.getTime();
        } else {
            this.updateTime = Long.MAX_VALUE;
        }
    }

    /**
     * Creates an access key.
     * 
     * @param seed
     *            is the hint for key generation, and must not be null.
     * @return access key.
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public synchronized String generateAccessKey(String seed)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {

        // If the key exists already, return it.

        if (!keyAndDate.isEmpty()) {
            Pair k = keyAndDate.getFirst();
            if (checkValidity(k.key, true)) {
                if (bDebug) {
                    System.out.println("@@ Previous key is valid. returns it.");
                }
                return k.key;
            }
        }

        // Generates a key.

        Date today = GregorianCalendar.getInstance().getTime();
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] b = (Long.toString(today.getTime()) + ":" + seed)
                .getBytes("8859_1");
        md.update(b);
        byte[] d = md.digest();

        StringBuffer sb = new StringBuffer();
        int length = (d.length > AccessManager.KEY_MAX_LENGTH) ? AccessManager.KEY_MAX_LENGTH
                : d.length;
        for (int i = 0; i < length; i++) {
            String hex = Integer.toHexString(d[i] & 0xff);
            sb.append(hex.length() == 1 ? ("0" + hex) : hex);
        }
        String ds = sb.toString();

        if (bDebug) {
            System.out.println("@@ AccessManager MD5 digest: " + ds);
        }

        if (keyAndDate.size() >= 2) {
            keyAndDate.removeLast();
        }

        keyAndDate.addFirst(new Pair(ds, new Long(today.getTime())));

        if (bDebug) {
            System.out.println("@@ AccessManager key count: "
                    + keyAndDate.size());
        }

        return ds;
    }

    /**
     * Checks key.
     * 
     * @param key
     *            access key.
     * @param bStrict
     * @return true, if key is valid. false, otherwise.
     */
    public synchronized boolean checkValidity(String key, boolean bStrict) {

        if (!accessControl) {
            return true;
        }

        int nExists = 0;
        int i = 0;
        Pair target = null;
        Iterator it = keyAndDate.iterator();
        while (it.hasNext()) {
            i++;
            target = (Pair) it.next();
            if (key.equals(target.key)) {
                nExists = i;
                break;
            }
        }
        if (nExists == 0) {
            return false;
        }

        long time = bStrict ? updateTime : (updateTime * 2);
        long past = target.value.longValue();
        long now = GregorianCalendar.getInstance().getTimeInMillis();

        if (bDebug) {
            System.out.println("@@ key: " + key + ", now: " + now + ", past: "
                    + past + ", updateTime: " + time);
        }
        if ((now - past) <= time) {
            return true;
        }
        return false;
    }

    /**
     * Checks key to be updated at this time.
     * 
     * @param key
     *            access key.
     * @return true, it is update duration. false, otherwise.
     */
    public synchronized boolean checkUpdate(String key) {
        return (checkValidity(key, false) && !checkValidity(key, true)) ? true
                : false;
    }

    /**
     * Clears all access key.
     */
    public synchronized void clear() {
        keyAndDate.clear();
    }
}
