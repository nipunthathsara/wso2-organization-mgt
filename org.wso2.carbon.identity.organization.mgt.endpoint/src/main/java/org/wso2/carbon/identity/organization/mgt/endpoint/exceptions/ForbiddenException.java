/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
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

package org.wso2.carbon.identity.organization.mgt.endpoint.exceptions;

import org.apache.http.HttpHeaders;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.ErrorDTO;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Forbidden Exception
 */
public class ForbiddenException extends WebApplicationException {

    private String message;

    public ForbiddenException(ErrorDTO errorDTO) {

        super(Response.status(Response.Status.FORBIDDEN).entity(errorDTO)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).build());
        message = errorDTO.getDescription();
    }

    public ForbiddenException() {

        super(Response.Status.FORBIDDEN);
    }

    @Override
    public String getMessage() {

        return message;
    }
}
