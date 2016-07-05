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

package jp.go.nict.mcml.com.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import jp.go.nict.mcml.servlet.control.ControlServerProperties;

import org.apache.log4j.Logger;

/**
 * DBConnector class.
 * 
 */
public class DBConnector {
    private static final Logger LOG = Logger.getLogger(DBConnector.class
            .getName());

    private static final DBConnector DB_CONNECTOR = new DBConnector();

    /** Connecting DB URL */
    private String dbUrl = null;

    /** DB driver */
    private String driver = null;

    /** User name */
    private String user = null;

    /** Password */
    private String password = null;

    /** DB connection */
    private Connection conn = null;

    /**
     * Get DBConnector instance
     * 
     * @return DB connector
     */
    public static DBConnector getInstance() {
        return DB_CONNECTOR;
    }

    /**
     * Connect DB
     * 
     */
    public void connect() {
        if (dbUrl.equals("")) {
            return;
        }

        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(dbUrl, user, password);
            LOG.info("Connected DB at " + dbUrl);
        } catch (SQLException e) {
            e.printStackTrace();
            LOG.error("Connected DB at " + dbUrl);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Disconnect DB
     * 
     */
    public void disconnect() {
        if (conn != null) {
            try {
                conn.close();
                LOG.info("Connected DB at " + dbUrl);
            } catch (SQLException e) {
                e.printStackTrace();
                LOG.error("Connected DB at " + dbUrl);
            }
        }
    }

    /**
     * Get a DB connection
     * 
     * @return  Connection
     */
    public Connection getConnection() {
        return conn;
    }

    /**
     * Constructor Initialize DB informations
     */
    private DBConnector() {
        dbUrl = ControlServerProperties.getInstance().getDbUrl();
        user = ControlServerProperties.getInstance().getDbUser();
        password = ControlServerProperties.getInstance().getDbPassword();
        driver = ControlServerProperties.getInstance().getDbDriver();
    }
}
