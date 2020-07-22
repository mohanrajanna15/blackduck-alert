/**
 * azure-boards-common
 *
 * Copyright (c) 2020 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.azure.boards.common.http;

import java.net.Proxy;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.gson.Gson;

public class AzureHttpServiceFactory {
    public static final String DEFAULT_BASE_URL = "https://dev.azure.com";

    public static AzureHttpService withCredentialNoProxy(Credential oAuthCredential, Gson gson) {
        return withCredentialNoProxy(DEFAULT_BASE_URL, oAuthCredential, gson);
    }

    public static AzureHttpService withCredentialNoProxy(String baseUrl, Credential oAuthCredential, Gson gson) {
        return withCredential(baseUrl, Proxy.NO_PROXY, oAuthCredential, gson);
    }

    public static AzureHttpService withCredential(Proxy proxy, Credential oAuthCredential, Gson gson) {
        return withCredential(DEFAULT_BASE_URL, proxy, oAuthCredential, gson);
    }

    public static AzureHttpService withCredential(String baseUrl, Proxy proxy, Credential oAuthCredential, Gson gson) {
        NetHttpTransport netHttpTransport = new NetHttpTransport.Builder()
                                                .setProxy(proxy)
                                                .build();
        return new AzureHttpService(baseUrl, netHttpTransport.createRequestFactory(oAuthCredential), gson);
    }

}