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

package jp.go.nict.mcml.com.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import jp.go.nict.mcml.com.db.DBConnector;
import jp.go.nict.mcml.com.entity.TrProjectEntity;

/**
 * TrProjectDao class.
 * 
 */
public class TrProjectDao {
    private static final String COLUMN_PROJECT = "project";
    private static final String COLUMN_OWNER = "owner";
    private static final String COLUMN_PORT = "port";
    private static final String COLUMN_DOMAIN = "domain";

    /**
     * Constructor
     */
    public TrProjectDao() {
    }

    /**
     * select
     * 
     * @param project
     * @param owner
     * @param trProjectEntity
     * @throws SQLException
     */
    public void select(String project, String owner,
            TrProjectEntity trProjectEntity) throws SQLException {
        int retryCount = 5;
        boolean transactionCompleted = false;
        Statement stmt = null;
        ResultSet rs = null;
        do {
            try {
                Connection conn = DBConnector.getInstance().getConnection();

                // Select tr_project
                String sql = "SELECT * FROM tr_project ";
                sql += "WHERE project = '" + project + "' AND owner = '"
                        + owner + "'";
                stmt = conn.createStatement();
                rs = stmt.executeQuery(sql);

                // Get a port
                while (rs.next()) {
                    trProjectEntity.setProject(rs.getString(COLUMN_PROJECT));
                    trProjectEntity.setOwner(rs.getString(COLUMN_OWNER));
                    trProjectEntity.setPort(rs.getString(COLUMN_PORT));
                    trProjectEntity.setDomain(rs.getString(COLUMN_DOMAIN));
                }
                rs.close();
                rs = null;

                stmt.close();
                stmt = null;

                retryCount = 0;
                transactionCompleted = true;
            } catch (SQLException sqlEx) {
                String sqlState = sqlEx.getSQLState();
                if (("08S01".equals(sqlState)) || ("40001".equals(sqlState))) {
                    retryCount -= 1;
                } else {
                    retryCount = 0;
                }
            } finally {
                if (rs != null) {
                    rs.close();
                }

                if (stmt != null) {
                    stmt.close();
                }
            }
        } while (!transactionCompleted && (retryCount > 0));
    }
}
