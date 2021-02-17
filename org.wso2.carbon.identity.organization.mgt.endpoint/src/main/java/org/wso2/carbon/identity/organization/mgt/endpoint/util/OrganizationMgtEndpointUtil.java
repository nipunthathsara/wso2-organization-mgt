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

package org.wso2.carbon.identity.organization.mgt.endpoint.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.jaxrs.ext.search.PrimitiveStatement;
import org.apache.cxf.jaxrs.ext.search.SearchCondition;
import org.apache.cxf.jaxrs.ext.search.SearchContext;
import org.apache.log4j.MDC;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.organization.mgt.core.OrganizationManager;
import org.wso2.carbon.identity.organization.mgt.core.constant.ConditionType;
import org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementClientException;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementException;
import org.wso2.carbon.identity.organization.mgt.core.model.Attribute;
import org.wso2.carbon.identity.organization.mgt.core.model.Organization;
import org.wso2.carbon.identity.organization.mgt.core.model.OrganizationAdd;
import org.wso2.carbon.identity.organization.mgt.core.model.UserStoreConfig;
import org.wso2.carbon.identity.organization.mgt.core.search.ComplexCondition;
import org.wso2.carbon.identity.organization.mgt.core.search.Condition;
import org.wso2.carbon.identity.organization.mgt.core.search.PrimitiveCondition;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.AttributeDTO;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.BasicOrganizationDTO;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.ErrorDTO;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.MetaDTO;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.OrganizationAddDTO;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.OrganizationDTO;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.ParentDTO;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.UserStoreConfigDTO;
import org.wso2.carbon.identity.organization.mgt.endpoint.exceptions.BadRequestException;
import org.wso2.carbon.identity.organization.mgt.endpoint.exceptions.ConflictRequestException;
import org.wso2.carbon.identity.organization.mgt.endpoint.exceptions.ForbiddenException;
import org.wso2.carbon.identity.organization.mgt.endpoint.exceptions.InternalServerErrorException;
import org.wso2.carbon.identity.organization.mgt.endpoint.exceptions.NotFoundException;
import org.wso2.carbon.identity.organization.mgt.endpoint.odata.MethodSearchCondition;
import org.wso2.carbon.identity.organization.user.role.mgt.core.OrganizationUserRoleManager;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response;

import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_UNEXPECTED;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.LIST_REQUEST_BAD_FILTER;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.LIST_REQUEST_INVALID_DATE_FILTER;
import static org.wso2.carbon.identity.organization.mgt.endpoint.constants.OrganizationMgtEndpointConstants.CORRELATION_ID_MDC;
import static org.wso2.carbon.identity.organization.mgt.endpoint.constants.OrganizationMgtEndpointConstants.CREATED;
import static org.wso2.carbon.identity.organization.mgt.endpoint.constants.OrganizationMgtEndpointConstants.DATE_SEARCH_FORMAT;
import static org.wso2.carbon.identity.organization.mgt.endpoint.constants.OrganizationMgtEndpointConstants.ENDS_WITH;
import static org.wso2.carbon.identity.organization.mgt.endpoint.constants.OrganizationMgtEndpointConstants.LAST_MODIFIED;
import static org.wso2.carbon.identity.organization.mgt.endpoint.constants.OrganizationMgtEndpointConstants.STARTS_WITH;
import static org.wso2.carbon.identity.organization.mgt.endpoint.constants.OrganizationMgtEndpointConstants.SUBSTRING;

/**
 * This class provides util functions to the Organization Management endpoint.
 */
public class OrganizationMgtEndpointUtil {

    private static final Log log = LogFactory.getLog(OrganizationMgtEndpointUtil.class);

    public static OrganizationManager getOrganizationManager() {

        return (OrganizationManager) PrivilegedCarbonContext.getThreadLocalCarbonContext()
                .getOSGiService(OrganizationManager.class, null);
    }

    public static OrganizationUserRoleManager getOrganizationUserRoleManager() {

        return (OrganizationUserRoleManager) PrivilegedCarbonContext.getThreadLocalCarbonContext()
                .getOSGiService(OrganizationUserRoleManager.class, null);
    }

    public static OrganizationAdd getOrganizationAddFromDTO(OrganizationAddDTO organizationAddDTO) {

        OrganizationAdd organizationAdd = new OrganizationAdd();
        organizationAdd.setName(organizationAddDTO.getName());
        organizationAdd.setDisplayName(organizationAddDTO.getDisplayName());
        organizationAdd.setDescription(organizationAddDTO.getDescription());
        organizationAdd.getParent().setId(organizationAddDTO.getParentId());
        organizationAdd.setAttributes(
                organizationAddDTO.getAttributes().stream().map(OrganizationMgtEndpointUtil::getAttributeFromDTO)
                        .collect(Collectors.toList()));
        organizationAdd.setUserStoreConfigs(organizationAddDTO.getUserStoreConfigs().stream()
                .map(OrganizationMgtEndpointUtil::getUserStoreConfigsFromDTO).collect(Collectors.toList()));
        return organizationAdd;
    }

    public static BasicOrganizationDTO getBasicOrganizationDTOFromOrganization(Organization organization) {

        BasicOrganizationDTO basicOrganizationDTO = new BasicOrganizationDTO();
        basicOrganizationDTO.setId(organization.getId());
        basicOrganizationDTO.setName(organization.getName());
        basicOrganizationDTO.setDisplayName(organization.getDisplayName());
        basicOrganizationDTO.setDescription(organization.getDescription());
        basicOrganizationDTO.setStatus(BasicOrganizationDTO.StatusEnum.valueOf(organization.getStatus().toString()));
        // Set parent
        ParentDTO parentDTO = new ParentDTO();
        parentDTO.setId(organization.getParent().getId());
        parentDTO.setName(organization.getParent().getName());
        parentDTO.setDisplayName(organization.getParent().getDisplayName());
        parentDTO.setRef(organization.getParent().getRef());
        basicOrganizationDTO.setParent(parentDTO);
        // Set metadata
        MetaDTO metaDTO = new MetaDTO();
        metaDTO.setCreated(organization.getMetadata().getCreated());
        metaDTO.setLastModified(organization.getMetadata().getLastModified());
        basicOrganizationDTO.setMeta(metaDTO);
        return basicOrganizationDTO;
    }

    public static OrganizationDTO getOrganizationDTOFromOrganization(Organization organization) {

        OrganizationDTO organizationDTO = new OrganizationDTO();
        organizationDTO.setId(organization.getId());
        organizationDTO.setName(organization.getName());
        organizationDTO.setDisplayName(organization.getDisplayName());
        organizationDTO.setDescription(organization.getDescription());
        organizationDTO.setStatus(OrganizationDTO.StatusEnum.valueOf(organization.getStatus().toString()));
        // Set parent
        ParentDTO parentDTO = new ParentDTO();
        parentDTO.setId(organization.getParent().getId());
        parentDTO.setName(organization.getParent().getName());
        parentDTO.setDisplayName(organization.getParent().getDisplayName());
        parentDTO.setRef(organization.getParent().getRef());
        organizationDTO.setParent(parentDTO);
        // Set metadata
        MetaDTO metaDTO = new MetaDTO();
        metaDTO.setCreated(organization.getMetadata().getCreated());
        metaDTO.setLastModified(organization.getMetadata().getLastModified());
        organizationDTO.setMeta(metaDTO);
        organizationDTO.setAttributes(organization.getAttributes().values().stream()
                .map(OrganizationMgtEndpointUtil::getAttributeDTOFromAttribute).collect(Collectors.toList()));
        // Set permissions
        organizationDTO.setPermissions(organization.getPermissions());
        return organizationDTO;
    }

    public static Attribute getAttributeFromDTO(AttributeDTO attributeDTO) {

        return new Attribute(attributeDTO.getKey(), attributeDTO.getValue());
    }

    public static UserStoreConfig getUserStoreConfigsFromDTO(UserStoreConfigDTO userStoreConfigDTO) {

        return new UserStoreConfig(userStoreConfigDTO.getKey().toString(), userStoreConfigDTO.getValue());
    }

    public static List<UserStoreConfigDTO> getUserStoreConfigDTOsFromUserStoreConfigs(
            Collection<UserStoreConfig> configs) {

        List<UserStoreConfigDTO> configDTOs = new ArrayList<>();
        for (UserStoreConfig config : configs) {
            UserStoreConfigDTO configDTO = new UserStoreConfigDTO();
            configDTO.setKey(UserStoreConfigDTO.KeyEnum.valueOf(config.getKey()));
            configDTO.setValue(config.getValue());
            configDTOs.add(configDTO);
        }
        return configDTOs;
    }

    public static List<OrganizationDTO> getOrganizationDTOsFromOrganizations(List<Organization> organizations) {

        List<OrganizationDTO> organizationDTOs = new ArrayList<>();
        for (Organization org : organizations) {
            OrganizationDTO organizationDTO = new OrganizationDTO();
            organizationDTO.setId(org.getId());
            organizationDTO.setName(org.getName());
            organizationDTO.setDisplayName(org.getDisplayName());
            organizationDTO.setDescription(org.getDescription());
            organizationDTO.setStatus(OrganizationDTO.StatusEnum.valueOf(org.getStatus().toString()));
            // Set parent
            ParentDTO parentDTO = new ParentDTO();
            parentDTO.setId(org.getParent().getId());
            parentDTO.setName(org.getParent().getName());
            parentDTO.setDisplayName(org.getParent().getDisplayName());
            parentDTO.setRef(org.getParent().getRef());
            organizationDTO.setParent(parentDTO);
            // Set metadata
            MetaDTO metaDTO = new MetaDTO();
            metaDTO.setCreated(org.getMetadata().getCreated());
            metaDTO.setLastModified(org.getMetadata().getLastModified());
            organizationDTO.setMeta(metaDTO);
            // Set attributes if any
            if (org.hasAttributes()) {
                organizationDTO.setAttributes(org.getAttributes().values().stream()
                        .map(OrganizationMgtEndpointUtil::getAttributeDTOFromAttribute).collect(Collectors.toList()));
            }
            // Set permissions if required
            if (org.getPermissions() != null && !org.getPermissions().isEmpty()) {
                organizationDTO.setPermissions(org.getPermissions());
            }
            organizationDTOs.add(organizationDTO);
        }
        return organizationDTOs;
    }

    public static AttributeDTO getAttributeDTOFromAttribute(Attribute attribute) {

        AttributeDTO attributeDTO = new AttributeDTO();
        attributeDTO.setKey(attribute.getKey());
        attributeDTO.setValue(attribute.getValue());
        return attributeDTO;
    }

    public static Response handleBadRequestResponse(OrganizationManagementClientException e, Log log) {

        if (isNotFoundError(e)) {
            throw OrganizationMgtEndpointUtil.buildNotFoundRequestException(e.getDescription(), e.getMessage(),
                    e.getErrorCode(), log, e);
        }

        if (isConflictError(e)) {
            throw OrganizationMgtEndpointUtil.buildConflictRequestException(e.getDescription(), e.getMessage(),
                    e.getErrorCode(), log, e);
        }

        if (isForbiddenError(e)) {
            throw OrganizationMgtEndpointUtil.buildForbiddenException(e.getDescription(), e.getMessage(),
                    e.getErrorCode(), log, e);
        }
        throw OrganizationMgtEndpointUtil.buildBadRequestException(e.getDescription(), e.getMessage(),
                e.getErrorCode(), log, e);
    }

    public static Response handleServerErrorResponse(OrganizationManagementException e, Log log) {

        throw buildInternalServerErrorException(e.getErrorCode(), log, e);
    }

    public static Response handleUnexpectedServerError(Throwable e, Log log) {

        throw buildInternalServerErrorException(ERROR_CODE_UNEXPECTED.getCode(), log, e);
    }

    private static boolean isNotFoundError(OrganizationManagementClientException e) {

        for (OrganizationMgtConstants.NotFoundErrorMessages notFoundError :
                OrganizationMgtConstants.NotFoundErrorMessages
                .values()) {
            if (notFoundError.toString().replace('_', '-').equals(e.getErrorCode())) {
                return true;
            }
        }
        return false;
    }

    private static boolean isConflictError(OrganizationManagementClientException e) {

        for (OrganizationMgtConstants.ConflictErrorMessages conflictError :
                OrganizationMgtConstants.ConflictErrorMessages
                        .values()) {
            if (conflictError.toString().replace('_', '-').equals(e.getErrorCode())) {
                return true;
            }
        }
        return false;
    }

    private static boolean isForbiddenError(OrganizationManagementClientException e) {

        for (OrganizationMgtConstants.ForbiddenErrorMessages forbiddenError :
                OrganizationMgtConstants.ForbiddenErrorMessages
                .values()) {
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

    public static ForbiddenException buildForbiddenException(String description, String message,
            String code, Log log, Throwable e) {

        ErrorDTO errorDTO = getErrorDTO(message, description, code);
        logDebug(log, e);
        return new ForbiddenException(errorDTO);
    }

    public static BadRequestException buildBadRequestException(String description, String message,
            String code, Log log, Throwable e) {

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

    public static <T> Condition getSearchCondition(SearchContext searchContext, Class<T> reference)
            throws OrganizationManagementClientException {

        SearchCondition<T> searchCondition;
        try {
            searchCondition = searchContext.getCondition(reference);
        } catch (RuntimeException e) {
            throw new OrganizationManagementClientException(
                    LIST_REQUEST_BAD_FILTER.getMessage(),
                    LIST_REQUEST_BAD_FILTER.getDescription(),
                    LIST_REQUEST_BAD_FILTER.getCode(),
                    e);
        }
        return buildSearchCondition(searchCondition);
    }

    private static Condition buildSearchCondition(SearchCondition searchCondition)
            throws OrganizationManagementClientException {

        // No search condition defined
        if (searchCondition == null) {
            return null;
        }
        if (searchCondition.getStatement() != null) {
            PrimitiveStatement primitiveStatement = searchCondition.getStatement();
            // If 'startswith', 'endswith' or 'substring'(contains) search operation...
            String method = null;
            if (org.apache.cxf.jaxrs.ext.search.ConditionType.CUSTOM.equals(primitiveStatement.getCondition())) {
                method = ((MethodSearchCondition) searchCondition).getMethod();
            }
            if (primitiveStatement.getProperty() != null) {
                if (CREATED.equals(primitiveStatement.getProperty()) || LAST_MODIFIED
                        .equals(primitiveStatement.getProperty())) {
                    SimpleDateFormat format = new SimpleDateFormat(DATE_SEARCH_FORMAT);
                    Date date;
                    try {
                        date = format.parse(primitiveStatement.getValue().toString());
                    } catch (ParseException e) {
                        throw new OrganizationManagementClientException(
                            LIST_REQUEST_INVALID_DATE_FILTER.getMessage(),
                            String.format(LIST_REQUEST_INVALID_DATE_FILTER.getDescription(),
                                    "'created' and 'lastModified' search criteria should be of : "
                                            + DATE_SEARCH_FORMAT + " format."),
                            LIST_REQUEST_INVALID_DATE_FILTER.getCode(),
                            e);
                    }
                    PrimitiveStatement statement = new PrimitiveStatement(primitiveStatement.getProperty(),
                            new Timestamp(date.getTime()), Timestamp.class, primitiveStatement.getCondition());
                    return new PrimitiveCondition(statement.getProperty(),
                            getPrimitiveOperatorFromOdata(statement.getCondition(), method), statement.getValue());
                }
                return new PrimitiveCondition(primitiveStatement.getProperty(),
                        getPrimitiveOperatorFromOdata(primitiveStatement.getCondition(), method),
                        primitiveStatement.getValue());
            }
            return null;
        } else {
            List<Condition> conditions = new ArrayList<>();
            for (Object condition : searchCondition.getSearchConditions()) {
                Condition buildCondition = buildSearchCondition((SearchCondition) condition);
                if (buildCondition != null) {
                    conditions.add(buildCondition);
                }
            }
            return new ComplexCondition(getComplexOperatorFromOdata(searchCondition.getConditionType()), conditions);
        }
    }

    private static ConditionType.PrimitiveOperator getPrimitiveOperatorFromOdata(
            org.apache.cxf.jaxrs.ext.search.ConditionType odataConditionType, String method) {

        ConditionType.PrimitiveOperator primitiveConditionType = null;
        switch (odataConditionType) {
        case EQUALS:
            primitiveConditionType = ConditionType.PrimitiveOperator.EQUALS;
            break;
        case GREATER_OR_EQUALS:
            primitiveConditionType = ConditionType.PrimitiveOperator.GREATER_OR_EQUALS;
            break;
        case LESS_OR_EQUALS:
            primitiveConditionType = ConditionType.PrimitiveOperator.LESS_OR_EQUALS;
            break;
        case GREATER_THAN:
            primitiveConditionType = ConditionType.PrimitiveOperator.GREATER_THAN;
            break;
        case NOT_EQUALS:
            primitiveConditionType = ConditionType.PrimitiveOperator.NOT_EQUALS;
            break;
        case LESS_THAN:
            primitiveConditionType = ConditionType.PrimitiveOperator.LESS_THAN;
            break;
        case CUSTOM:
            if (STARTS_WITH.equals(method)) {
                primitiveConditionType = ConditionType.PrimitiveOperator.STARTS_WITH;
            } else if (ENDS_WITH.equals(method)) {
                primitiveConditionType = ConditionType.PrimitiveOperator.ENDS_WITH;
            } else if (SUBSTRING.equals(method)) {
                primitiveConditionType = ConditionType.PrimitiveOperator.SUBSTRING;
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Unsupported function found : " + method);
                }
            }
            break;
        default:
            if (log.isDebugEnabled()) {
                log.debug("Invalid primitive operator found: " + odataConditionType);
            }
        }
        return primitiveConditionType;
    }

    private static ConditionType.ComplexOperator getComplexOperatorFromOdata(
            org.apache.cxf.jaxrs.ext.search.ConditionType odataConditionType) {

        ConditionType.ComplexOperator complexConditionType = null;
        switch (odataConditionType) {
        case OR:
            complexConditionType = ConditionType.ComplexOperator.OR;
            break;
        case AND:
            complexConditionType = ConditionType.ComplexOperator.AND;
            break;
        default:
            if (log.isDebugEnabled()) {
                log.debug("Invalid complex operator found: " + odataConditionType);
            }
        }
        return complexConditionType;
    }

    private static ErrorDTO getErrorDTO(String message, String description, String code) {

        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setCode(code);
        errorDTO.setMessage(message);
        errorDTO.setDescription(description);
        errorDTO.setTraceID(getCorrelation());
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

    public static String getCorrelation() {
        String ref;
        if (isCorrelationIDPresent()) {
            ref = MDC.get(CORRELATION_ID_MDC).toString();
        } else {
            ref = UUID.randomUUID().toString();

        }
        return ref;
    }

    public static boolean isCorrelationIDPresent() {
        return MDC.get(CORRELATION_ID_MDC) != null;
    }
}
