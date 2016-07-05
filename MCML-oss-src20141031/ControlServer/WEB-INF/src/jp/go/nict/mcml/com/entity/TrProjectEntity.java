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

package jp.go.nict.mcml.com.entity;

/**
 * TrProjectEntity class.
 * 
 */
public class TrProjectEntity {
    /** Project name */
    private String project = null;

    /** Owner */
    private String owner = null;

    /** Port number */
    private String port = null;

    /** Domain */
    private String domain = null;

    /**
     * Gets project name
     * 
     * @return Project name
     */
    public String getProject() {
        return project;
    }

    /**
     * Set project
     * 
     * @param project
     */
    public void setProject(String project) {
        this.project = project;
    }

    /**
     * Gets owner
     * 
     * @return Owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Set owner
     * 
     * @param owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Gets port number
     * 
     * @return port
     */
    public String getPort() {
        return port;
    }

    /**
     * Set port number
     * 
     * @param port
     */
    public void setPort(String port) {
        this.port = port;
    }

    /**
     * Gets Domain
     * 
     * @return Domain
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Set domain
     * 
     * @param domain
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }
}
