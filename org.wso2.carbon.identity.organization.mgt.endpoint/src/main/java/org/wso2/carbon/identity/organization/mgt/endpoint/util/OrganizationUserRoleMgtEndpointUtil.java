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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.organization.mgt.endpoint.util;

import org.apache.commons.logging.Log;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.ErrorDTO;
import org.wso2.carbon.identity.organization.mgt.endpoint.exceptions.BadRequestException;
import org.wso2.carbon.identity.organization.mgt.endpoint.exceptions.ConflictRequestException;
import org.wso2.carbon.identity.organization.mgt.endpoint.exceptions.ForbiddenException;
import org.wso2.carbon.identity.organization.mgt.endpoint.exceptions.InternalServerErrorException;
import org.wso2.carbon.identity.organization.mgt.endpoint.exceptions.NotFoundException;
import org.wso2.carbon.identity.organization.user.role.mgt.core.constant.OrganizationUserRoleMgtConstants;
import org.wso2.carbon.identity.organization.user.role.mgt.core.exception.OrganizationUserRoleMgtClientException;
import org.wso2.carbon.identity.organization.user.role.mgt.core.exception.OrganizationUserRoleMgtException;

import javax.ws.rs.core.Response;

import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_UNEXPECTED;

/**
 * Organization User Role Mgt Endpoint Util.
 */
public class OrganizationUserRoleMgtEndpointUtil {

    public static Response handleBadRequestResponse(OrganizationUserRoleMgtClientException e, Log log) {

        if (isNotFoundError(e)) {
            throw OrganizationUserRoleMgtEndpointUtil.buildNotFoundRequestException(e.getDescription(), e.getMessage(),
                    e.getErrorCode(), log, e);
        }

        if (isConflictError(e)) {
            throw OrganizationUserRoleMgtEndpointUtil.buildConflictRequestException(e.getDescription(), e.getMessage(),
                    e.getErrorCode(), log, e);
        }

        if (isForbiddenError(e)) {
            throw OrganizationUserRoleMgtEndpointUtil.buildForbiddenException(e.getDescription(), e.getMessage(),
                    e.getErrorCode(), log, e);
        }
        throw OrganizationUserRoleMgtEndpointUtil.buildBadRequestException(e.getDescription(), e.getMessage(),
                e.getErrorCode(), log, e);
    }

    public static Response handleServerErrorResponse(OrganizationUserRoleMgtException e, Log log) {

        throw buildInternalServerErrorException(e.getErrorCode(), log, e);
    }

    public static Response handleUnexpectedServerError(Throwable e, Log log) {

        throw buildInternalServerErrorException(ERROR_CODE_UNEXPECTED.getCode(), log, e);
    }

    private static boolean isNotFoundError(OrganizationUserRoleMgtClientException e) {

        for (OrganizationUserRoleMgtConstants.NotFoundErrorMessages notFoundError :
                OrganizationUserRoleMgtConstants.NotFoundErrorMessages.values()) {
            if (notFoundError.toString().replace('_', '-').equals(e.getErrorCode())) {
                return true;
            }
        }
        return false;
    }

    private static boolean isConflictError(OrganizationUserRoleMgtClientException e) {

        for (OrganizationUserRoleMgtConstants.ConflictErrorMessages conflictError :
                OrganizationUserRoleMgtConstants.ConflictErrorMessages.values()) {
            if (conflictError.toString().replace('_', '-').equals(e.getErrorCode())) {
                return true;
            }
        }
        return false;
    }

    private static boolean isForbiddenError(OrganizationUserRoleMgtClientException e) {

        for (OrganizationUserRoleMgtConstants.ForbiddenErrorMessages forbiddenError :
                OrganizationUserRoleMgtConstants.ForbiddenErrorMessages.values()) {
            if (forbiddenError.toString().replace('_', '-').equals(e.getErrorCode())) {
                return true;
            }
        }
        return false;
    }

    public static NotFoundException buildNotFoundRequestException(String description, String message, String code,
            Log log, Throwable e) {

        ErrorDTO errorDTO = getErrorDTO(message, description, code);
        logDebug(log, e);
        return new NotFoundException(errorDTO);
    }

    public static ConflictRequestException buildConflictRequestException(String description, String message,
            String code, Log log, Throwable e) {

        ErrorDTO errorDTO = getErrorDTO(message, description, code);
        logDebug(log, e);
        return new ConflictRequestException(errorDTO);
    }

    public static ForbiddenException buildForbiddenException(String description, String message, String code, Log log,
            Throwable e) {

        ErrorDTO errorDTO = getErrorDTO(message, description, code);
        logDebug(log, e);
        return new ForbiddenException(errorDTO);
    }

    public static BadRequestException buildBadRequestException(String description, String message, String code, Log log,
            Throwable e) {

        ErrorDTO errorDTO = getErrorDTO(message, description, code);
        logDebug(log, e);
        return new BadRequestException(errorDTO);
    }

    public static InternalServerErrorException buildInternalServerErrorException(String code, Log log, Throwable e) {

        ErrorDTO errorDTO = getErrorDTO(Response.Status.INTERNAL_SERVER_ERROR.toString(),
                Response.Status.INTERNAL_SERVER_ERROR.toString(), code);
        logError(log, e);
        return new InternalServerErrorException(errorDTO);
    }

    private static ErrorDTO getErrorDTO(String message, String description, String code) {

        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setCode(code);
        errorDTO.setMessage(message);
        errorDTO.setDescription(description);
        return errorDTO;
    }

    private static void logDebug(Log log, Throwable throwable) {

        if (log.isDebugEnabled()) {
            log.debug(Response.Status.BAD_REQUEST, throwable);
        }
    }

    private static void logError(Log log, Throwable throwable) {

        log.error(throwable.getMessage(), throwable);
    }
}
