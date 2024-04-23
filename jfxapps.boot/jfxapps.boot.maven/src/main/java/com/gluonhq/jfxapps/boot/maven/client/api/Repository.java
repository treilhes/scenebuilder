/*
 * Copyright (c) 2016, 2024, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2024, Pascal Treilhes and/or its affiliates.
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation and Gluon nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gluonhq.jfxapps.boot.maven.client.api;

public class Repository {

    public enum Content {
        SNAPSHOT,
        RELEASE,
        SNAPSHOT_RELEASE
    }
    private String id;
    private Class<? extends RepositoryType> type;
    private String url;
    private String user;
    private String password;
    private Content contentType = Content.SNAPSHOT_RELEASE;

    private Repository(Builder builder) {
        this.id = builder.id;
        this.type = builder.type;
        this.url = builder.url;
        this.user = builder.user;
        this.password = builder.password;
        this.contentType = builder.contentType;
    }

    public String getId() {
        return id;
    }

    public Class<? extends RepositoryType> getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public Content getContentType() {
        return contentType;
    }

    public String getName() {
        return id + (contentType == Content.SNAPSHOT_RELEASE ? "" : " (" + contentType.name().toLowerCase() + ")");
    }
    @Override
    public String toString() {
        return "Repository [id=" + id + ", type=" + type + ", URL=" + url + ", user=" + user + ", password=" + password
                + ", contentType=" + contentType + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Repository other = (Repository) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    public static Builder builder() {
        return new Builder();
    }


    public static class Builder {
        private String id;
        private Class<? extends RepositoryType> type;
        private String url;
        private String user;
        private String password;
        private Content contentType = Content.SNAPSHOT_RELEASE;


        private Builder() {
            super();
        }

        public Repository build() {
            return new Repository(this);
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder type(Class<? extends RepositoryType> type) {
            this.type = type;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder user(String user) {
            this.user = user;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder contentType(Content contentType) {
            this.contentType = contentType;
            return this;
        }

    }

}
