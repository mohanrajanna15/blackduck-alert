/**
 * alert-common
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
package com.synopsys.integration.alert.common.rest;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.synopsys.integration.alert.common.descriptor.config.field.errors.AlertFieldStatus;

// TODO make this a Utils class
@Component
public class ResponseFactory {
    public static final String EMPTY_ID = "-1L";
    public static final String MISSING_REQUEST_BODY = "Required request body is missing";
    public static final String UNAUTHORIZED_REQUEST_MESSAGE = "User not authorized to perform the request";

    // Static methods

    public static ResponseStatusException createBadRequestException(@Nullable String customMessage) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, customMessage);
    }

    public static ResponseStatusException createGoneException(@Nullable String customMessage) {
        return new ResponseStatusException(HttpStatus.GONE, customMessage);
    }

    public static ResponseStatusException createUnauthorizedException() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    public static ResponseStatusException createForbiddenException() {
        return createForbiddenException(null);
    }

    public static ResponseStatusException createForbiddenException(@Nullable String customMessage) {
        return new ResponseStatusException(HttpStatus.FORBIDDEN, customMessage);
    }

    public static ResponseStatusException createNotFoundException(@Nullable String customMessage) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, customMessage);
    }

    public static ResponseStatusException createInternalServerErrorException(@Nullable String customMessage) {
        return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, customMessage);
    }

    public static ResponseStatusException createNotImplementedException(@Nullable String customMessage) {
        return new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, customMessage);
    }

    // Unnecessarily stateful methods:

    public ResponseEntity<String> createMessageResponse(HttpStatus status, String id, String message) {
        String responseBody = new ResponseBodyBuilder(id, message).build();
        return new ResponseEntity<>(responseBody, status);
    }

    public ResponseEntity<String> createMessageResponse(HttpStatus status, Long id, String message) {
        return createMessageResponse(status, String.valueOf(id), message);
    }

    public ResponseEntity<String> createMessageResponse(HttpStatus status, String message) {
        return createMessageResponse(status, EMPTY_ID, message);
    }

    public ResponseEntity<String> createEmptyResponse(HttpStatus status) {
        return new ResponseEntity<>(status);
    }

    public ResponseEntity<String> createNotFoundResponse(String message) {
        return createMessageResponse(HttpStatus.NOT_FOUND, message);
    }

    public ResponseEntity<String> createForbiddenResponse() {
        return createForbiddenResponse(UNAUTHORIZED_REQUEST_MESSAGE);
    }

    public ResponseEntity<String> createForbiddenResponse(String message) {return createMessageResponse(HttpStatus.FORBIDDEN, message);}

    public ResponseEntity<String> createCreatedResponse(String id, String message) {
        return createMessageResponse(HttpStatus.CREATED, id, message);
    }

    public ResponseEntity<String> createAcceptedResponse(String id, String message) {
        return createMessageResponse(HttpStatus.ACCEPTED, id, message);
    }

    public ResponseEntity<String> createNoContentResponse() {
        return createEmptyResponse(HttpStatus.NO_CONTENT);
    }

    public ResponseEntity<String> createOkResponse(String id, String message) {
        return createMessageResponse(HttpStatus.OK, id, message);
    }

    public ResponseEntity<String> createGoneResponse(String id, String message) {
        return createMessageResponse(HttpStatus.GONE, id, message);
    }

    public ResponseEntity<String> createMethodNotAllowedResponse(String message) {
        return createMessageResponse(HttpStatus.METHOD_NOT_ALLOWED, message);
    }

    public ResponseEntity<String> createBadRequestResponse(String id, String message) {
        return createMessageResponse(HttpStatus.BAD_REQUEST, id, message);
    }

    public ResponseEntity<String> createInternalServerErrorResponse(String id, String message) {
        return createMessageResponse(HttpStatus.INTERNAL_SERVER_ERROR, id, message);
    }

    public ResponseEntity<String> createConflictResponse(String id, String message) {
        return createMessageResponse(HttpStatus.CONFLICT, id, message);
    }

    public ResponseEntity<String> createFieldErrorResponse(String id, String message, List<AlertFieldStatus> fieldErrors) {
        ResponseBodyBuilder responseBody = new ResponseBodyBuilder(id, message);
        responseBody.putErrors(fieldErrors);
        return new ResponseEntity<>(responseBody.build(), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<String> createResponse(HttpStatus status, HttpHeaders headers, String message) {
        return new ResponseEntity<>(message, headers, status);
    }

    public ResponseEntity<String> createContentResponse(HttpStatus status, String jsonContent) {
        return new ResponseEntity<>(jsonContent, status);
    }

    public ResponseEntity<String> createCreatedContentResponse(String jsonContent) {
        return createContentResponse(HttpStatus.CREATED, jsonContent);
    }

    public ResponseEntity<String> createOkContentResponse(String jsonContent) {
        return createContentResponse(HttpStatus.OK, jsonContent);
    }

    public ResponseEntity<String> createFoundRedirectResponse(String location) {
        HttpHeaders header = new HttpHeaders();
        header.add("Location", location);
        return new ResponseEntity<>(header, HttpStatus.FOUND);
    }

}
