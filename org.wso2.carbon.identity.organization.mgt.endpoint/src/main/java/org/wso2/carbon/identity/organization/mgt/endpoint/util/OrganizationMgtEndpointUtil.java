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
import org.apache.cxf.jaxrs.ext.search.SearchParseException;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.organization.mgt.core.OrganizationManager;
import org.wso2.carbon.identity.organization.mgt.core.constant.ConditionType;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementClientException;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementException;
import org.wso2.carbon.identity.organization.mgt.core.model.Attribute;
import org.wso2.carbon.identity.organization.mgt.core.model.Organization;
import org.wso2.carbon.identity.organization.mgt.core.model.OrganizationAdd;
import org.wso2.carbon.identity.organization.mgt.core.model.OrganizationSearchBean;
import org.wso2.carbon.identity.organization.mgt.core.model.UserStoreConfig;
import org.wso2.carbon.identity.organization.mgt.core.search.ComplexCondition;
import org.wso2.carbon.identity.organization.mgt.core.search.Condition;
import org.wso2.carbon.identity.organization.mgt.core.search.PrimitiveCondition;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.AttributeDTO;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.BasicOrganizationDTO;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.ErrorDTO;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.OrganizationAddDTO;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.OrganizationDTO;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.UserstoreConfigDTO;
import org.wso2.carbon.identity.organization.mgt.endpoint.exceptions.BadRequestException;
import org.wso2.carbon.identity.organization.mgt.endpoint.exceptions.ConflictRequestException;
import org.wso2.carbon.identity.organization.mgt.endpoint.exceptions.ForbiddenException;
import org.wso2.carbon.identity.organization.mgt.endpoint.exceptions.InternalServerErrorException;
import org.wso2.carbon.identity.organization.mgt.endpoint.exceptions.NotFoundException;
import org.wso2.carbon.identity.organization.mgt.endpoint.exceptions.SearchConditionException;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_SEARCH_ORGANIZATION_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_UNEXPECTED;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_SEARCH_BEAN_FIELD_ACTIVE;

/**
 * This class provides util functions to the Organization Management endpoint.
 */
public class OrganizationMgtEndpointUtil {

    private static final Log LOG = LogFactory.getLog(OrganizationMgtEndpointUtil.class);

    public static OrganizationManager getOrganizationManager() {

        return (OrganizationManager) PrivilegedCarbonContext.getThreadLocalCarbonContext()
                .getOSGiService(OrganizationManager.class, null);
    }

    public static OrganizationAdd getOrganizationAddFromDTO(OrganizationAddDTO organizationAddDTO) {

        OrganizationAdd organizationAdd = new OrganizationAdd();
        organizationAdd.setName(organizationAddDTO.getName());
        organizationAdd.setDescription(organizationAddDTO.getDescription());
        organizationAdd.setParentId(organizationAddDTO.getParentId());
        organizationAdd.setAttributes(organizationAddDTO.getAttributes()
                .stream().map(OrganizationMgtEndpointUtil::getAttributeFromDTO).collect(Collectors.toList()));
        organizationAdd.setUserStoreConfigs(organizationAddDTO.getUserstoreConfigs()
                .stream().map(OrganizationMgtEndpointUtil::getUserStoreConfigsFromDTO).collect(Collectors.toList()));
        return organizationAdd;
    }

    public static BasicOrganizationDTO getBasicOrganizationDTOFromOrganization(Organization organization) {

        BasicOrganizationDTO basicOrganizationDTO = new BasicOrganizationDTO();
        basicOrganizationDTO.setId(organization.getId());
        basicOrganizationDTO.setName(organization.getName());
        basicOrganizationDTO.setDescription(organization.getDescription());
        basicOrganizationDTO.setParentId(organization.getParentId());
        basicOrganizationDTO.setActive(organization.isActive());
        basicOrganizationDTO.setCreated(organization.getCreated());
        basicOrganizationDTO.setLastModified(organization.getLastModified());
        return basicOrganizationDTO;
    }

    public static OrganizationDTO getOrganizationDTOFromOrganization(Organization organization) {

        OrganizationDTO organizationDTO = new OrganizationDTO();
        organizationDTO.setId(organization.getId());
        organizationDTO.setName(organization.getName());
        organizationDTO.setDescription(organization.getDescription());
        organizationDTO.setParentId(organization.getParentId());
        organizationDTO.setActive(organization.isActive());
        organizationDTO.setCreated(organization.getCreated());
        organizationDTO.setLastModified(organization.getLastModified());
        organizationDTO.setAttributes(organization.getAttributes().values().stream()
                .map(OrganizationMgtEndpointUtil::getAttributeDTOFromAttribute).collect(Collectors.toList()));
        return organizationDTO;
    }

    public static Attribute getAttributeFromDTO(AttributeDTO attributeDTO) {

        return new Attribute(attributeDTO.getKey(), attributeDTO.getValue());
    }

    public static UserStoreConfig getUserStoreConfigsFromDTO(UserstoreConfigDTO userStoreConfigDTO) {

        return new UserStoreConfig(userStoreConfigDTO.getKey(), userStoreConfigDTO.getValue());
    }

    public static List<UserstoreConfigDTO> getUserStoreConfigDTOsFromUserStoreConfigs(Collection<UserStoreConfig> configs) {

        List<UserstoreConfigDTO> configDTOs = new ArrayList<>();
        for (UserStoreConfig config : configs) {
            UserstoreConfigDTO configDTO = new UserstoreConfigDTO();
            configDTO.setKey(config.getKey());
            configDTO.setValue(config.getValue());
            configDTOs.add(configDTO);
        }
        return configDTOs;
    }

    public static List<BasicOrganizationDTO> getBasicOrganizationDTOsFromOrganizations(List<Organization> organizations) {

        List<BasicOrganizationDTO> basicOrganizationDTOs = new ArrayList<>();
        for (Organization organization : organizations) {
            BasicOrganizationDTO basicOrganization = new BasicOrganizationDTO();
            basicOrganization.setId(organization.getId());
            basicOrganization.setName(organization.getName());
            if (organization.getDescription() != null) {
                basicOrganization.setDescription(organization.getDescription());
            }
            basicOrganization.setParentId(organization.getParentId());
            basicOrganization.setActive(organization.isActive());
            basicOrganization.setLastModified(organization.getLastModified());
            basicOrganization.setCreated(organization.getCreated());
            basicOrganizationDTOs.add(basicOrganization);
        }
        return basicOrganizationDTOs;
    }

    public static AttributeDTO getAttributeDTOFromAttribute(Attribute attribute) {

        AttributeDTO attributeDTO = new AttributeDTO();
        attributeDTO.setKey(attribute.getKey());
        attributeDTO.setValue(attribute.getValue());
        return attributeDTO;
    }

    public static Response handleBadRequestResponse(OrganizationManagementClientException e, Log LOG) {

        if (isNotFoundError(e)) {
            throw OrganizationMgtEndpointUtil.buildNotFoundRequestException(e.getMessage(), e.getErrorCode(), LOG, e);
        }

        if (isConflictError(e)) {
            throw OrganizationMgtEndpointUtil.buildConflictRequestException(e.getMessage(), e.getErrorCode(), LOG, e);
        }

        if (isForbiddenError(e)) {
            throw OrganizationMgtEndpointUtil.buildForbiddenException(e.getMessage(), e.getErrorCode(), LOG, e);
        }
        throw OrganizationMgtEndpointUtil.buildBadRequestException(e.getMessage(), e.getErrorCode(), LOG, e);
    }

    public static Response handleServerErrorResponse(OrganizationManagementException e, Log LOG) {

        throw buildInternalServerErrorException(e.getErrorCode(), LOG, e);
    }

    public static Response handleUnexpectedServerError(Throwable e, Log LOG) {

        throw buildInternalServerErrorException(ERROR_CODE_UNEXPECTED.getCode(), LOG, e);
    }

    private static boolean isNotFoundError(OrganizationManagementClientException e) {

        //TODO implement once error codes are finalized
        return false;
    }

    private static boolean isConflictError(OrganizationManagementClientException e) {

        //TODO implement once error codes are finalized
        return false;
    }

    private static boolean isForbiddenError(OrganizationManagementClientException e) {

        //TODO implement once error codes are finalized
        return false;
    }

    public static NotFoundException buildNotFoundRequestException(String description, String code,
                                                                  Log log, Throwable e) {

        ErrorDTO errorDTO = getErrorDTO(Response.Status.NOT_FOUND.toString(), description, code);
        logDebug(log, e);
        return new NotFoundException(errorDTO);
    }

    public static ConflictRequestException buildConflictRequestException(String description, String code,
                                                                         Log log, Throwable e) {

        ErrorDTO errorDTO = getErrorDTO(Response.Status.BAD_REQUEST.toString(), description, code);
        logDebug(log, e);
        return new ConflictRequestException(errorDTO);
    }

    public static ForbiddenException buildForbiddenException(String description, String code,
                                                             Log log, Throwable e) {

        ErrorDTO errorDTO = getErrorDTO(Response.Status.BAD_REQUEST.toString(), description, code);
        logDebug(log, e);
        return new ForbiddenException(errorDTO);
    }

    public static BadRequestException buildBadRequestException(String description, String code,
                                                               Log log, Throwable e) {

        ErrorDTO errorDTO = getErrorDTO(Response.Status.BAD_REQUEST.toString(), description, code);
        logDebug(log, e);
        return new BadRequestException(errorDTO);
    }

    public static InternalServerErrorException buildInternalServerErrorException(String code,
                                                                                 Log log, Throwable e) {

        ErrorDTO errorDTO = getErrorDTO(
                Response.Status.INTERNAL_SERVER_ERROR.toString(),
                Response.Status.INTERNAL_SERVER_ERROR.toString(),
                code
        );
        logError(log, e);
        return new InternalServerErrorException(errorDTO);
    }

    public static <T> Condition getSearchCondition(SearchContext searchContext, Class<T> reference) {

        SearchCondition<T> searchCondition = searchContext.getCondition(reference);
        return buildSearchCondition(searchCondition);
    }

    private static Condition buildSearchCondition(SearchCondition searchCondition) {

        // No search condition defined
        if (searchCondition == null) {
            return null;
        }
        if (!(searchCondition.getStatement() == null)) {
            PrimitiveStatement primitiveStatement = searchCondition.getStatement();
            if (!(primitiveStatement.getProperty() == null)) {
                    return new PrimitiveCondition(
                            primitiveStatement.getProperty(),
                            getPrimitiveOperatorFromOdata(primitiveStatement.getCondition()),
                            // By default the class tye would be String. Hence, explicit casting for Boolean 'active' field
                            primitiveStatement.getProperty().equals(ORGANIZATION_SEARCH_BEAN_FIELD_ACTIVE) ?
                                    Boolean.parseBoolean(primitiveStatement.getValue().toString()) :
                                    primitiveStatement.getValue()
                    );
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
            return new ComplexCondition(
                    getComplexOperatorFromOdata(searchCondition.getConditionType()),
                    conditions
            );
        }
    }

    private static ConditionType.PrimitiveOperator getPrimitiveOperatorFromOdata(
            org.apache.cxf.jaxrs.ext.search.ConditionType odataConditionType) {

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
        }
        return complexConditionType;
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
