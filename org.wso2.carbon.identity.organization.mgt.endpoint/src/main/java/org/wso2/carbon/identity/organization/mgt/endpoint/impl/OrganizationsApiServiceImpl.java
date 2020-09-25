package org.wso2.carbon.identity.organization.mgt.endpoint.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.jaxrs.ext.search.SearchContext;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementClientException;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementException;
import org.wso2.carbon.identity.organization.mgt.core.model.Operation;
import org.wso2.carbon.identity.organization.mgt.core.model.Organization;
import org.wso2.carbon.identity.organization.mgt.core.model.OrganizationSearchBean;
import org.wso2.carbon.identity.organization.mgt.core.model.UserStoreConfig;
import org.wso2.carbon.identity.organization.mgt.endpoint.*;

import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_INVALID_ORGANIZATION_GET_REQUEST;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.handleClientException;
import static org.wso2.carbon.identity.organization.mgt.endpoint.constants.OrganizationMgtEndpointConstants.ORGANIZATION_PATH;
import static org.wso2.carbon.identity.organization.mgt.endpoint.util.OrganizationMgtEndpointUtil.getBasicOrganizationDTOFromOrganization;
import static org.wso2.carbon.identity.organization.mgt.endpoint.util.OrganizationMgtEndpointUtil.getOrganizationDTOsFromOrganizations;
import static org.wso2.carbon.identity.organization.mgt.endpoint.util.OrganizationMgtEndpointUtil.getOrganizationDTOFromOrganization;
import static org.wso2.carbon.identity.organization.mgt.endpoint.util.OrganizationMgtEndpointUtil.getOrganizationManager;
import static org.wso2.carbon.identity.organization.mgt.endpoint.util.OrganizationMgtEndpointUtil.getOrganizationAddFromDTO;
import static org.wso2.carbon.identity.organization.mgt.endpoint.util.OrganizationMgtEndpointUtil.getOrganizationUserRoleManager;
import static org.wso2.carbon.identity.organization.mgt.endpoint.util.OrganizationMgtEndpointUtil.getSearchCondition;
import static org.wso2.carbon.identity.organization.mgt.endpoint.util.OrganizationMgtEndpointUtil.getUserStoreConfigDTOsFromUserStoreConfigs;
import static org.wso2.carbon.identity.organization.mgt.endpoint.util.OrganizationMgtEndpointUtil.handleBadRequestResponse;
import static org.wso2.carbon.identity.organization.mgt.endpoint.util.OrganizationMgtEndpointUtil.handleServerErrorResponse;
import static org.wso2.carbon.identity.organization.mgt.endpoint.util.OrganizationMgtEndpointUtil.handleUnexpectedServerError;

import org.wso2.carbon.identity.organization.mgt.endpoint.dto.OrganizationAddDTO;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.wso2.carbon.identity.organization.mgt.endpoint.dto.OperationDTO;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.UserRoleMappingDTO;
import org.wso2.carbon.identity.organization.mgt.endpoint.util.OrganizationUserRoleMgtEndpointUtil;
import org.wso2.carbon.identity.organization.user.role.mgt.core.exception.OrganizationUserRoleMgtClientException;
import org.wso2.carbon.identity.organization.user.role.mgt.core.exception.OrganizationUserRoleMgtException;
import org.wso2.carbon.identity.organization.user.role.mgt.core.exception.OrganizationUserRoleMgtServerException;
import org.wso2.carbon.identity.organization.user.role.mgt.core.model.OrganizationUserRoleMapping;
import org.wso2.carbon.identity.organization.user.role.mgt.core.model.UserRoleMapping;

import javax.ws.rs.core.Response;

public class OrganizationsApiServiceImpl extends OrganizationsApiService {

    private static final Log log = LogFactory.getLog(OrganizationsApiServiceImpl.class);

    @Override
    public Response organizationsPost(OrganizationAddDTO organizationAddDTO) {
        // TODO remove 'null' fields from the response
        try {
            Organization organization = getOrganizationManager().addOrganization(
                    getOrganizationAddFromDTO(organizationAddDTO),
                    false
            );
            return Response.created(getResourceURI(organization))
                    .entity(getBasicOrganizationDTOFromOrganization(organization)).build();
        } catch (OrganizationManagementClientException e) {
            return handleBadRequestResponse(e, log);
        } catch (OrganizationManagementException e) {
            return handleServerErrorResponse(e, log);
        } catch (Throwable throwable) {
            return handleUnexpectedServerError(throwable, log);
        }
    }

    @Override
    public Response organizationsImportPost(OrganizationAddDTO organizationAddDTO) {

        try {
            Organization organization = getOrganizationManager()
                    .addOrganization(getOrganizationAddFromDTO(organizationAddDTO), true);
            return Response.created(getResourceURI(organization))
                    .entity(getBasicOrganizationDTOFromOrganization(organization)).build();
        } catch (OrganizationManagementClientException e) {
            return handleBadRequestResponse(e, log);
        } catch (OrganizationManagementException e) {
            return handleServerErrorResponse(e, log);
        } catch (Throwable throwable) {
            return handleUnexpectedServerError(throwable, log);
        }
    }

    @Override
    public Response organizationsGet(SearchContext searchContext, Integer offset, Integer limit, String sortBy,
                                     String sortOrder, String attributes) {

        try {
            if ((limit != null && limit < 1) || (offset != null && offset < 0)) {
                throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_GET_REQUEST,
                        "Invalid pagination arguments. 'limit' should be greater than 0 and 'offset' should be greater than -1");
            }
            // If pagination parameters not defined in the request, set them to -1
            limit = (limit == null) ? -1 : limit;
            offset = (offset == null) ? -1 : offset;
            List<String> requestedAttributes = attributes == null ? new ArrayList<>()
                    : Arrays.stream(attributes.split(",")).map(String::trim).collect(Collectors.toList());
            List<Organization> organizations = getOrganizationManager()
                    .getOrganizations(
                            getSearchCondition(searchContext, OrganizationSearchBean.class),
                            offset,
                            limit,
                            sortBy,
                            sortOrder,
                            requestedAttributes);
            return Response.ok().entity(getOrganizationDTOsFromOrganizations(organizations)).build();
        } catch (OrganizationManagementClientException e) {
            return handleBadRequestResponse(e, log);
        } catch (OrganizationManagementException e) {
            return handleServerErrorResponse(e, log);
        } catch (Throwable throwable) {
            return handleUnexpectedServerError(throwable, log);
        }
    }

    @Override
    public Response organizationsOrganizationIdGet(String organizationId) {

        try {
            Organization organization = getOrganizationManager().getOrganization(organizationId);
            return Response.ok().entity(getOrganizationDTOFromOrganization(organization)).build();
        } catch (OrganizationManagementClientException e) {
            return handleBadRequestResponse(e, log);
        } catch (OrganizationManagementException e) {
            return handleServerErrorResponse(e, log);
        } catch (Throwable throwable) {
            return handleUnexpectedServerError(throwable, log);
        }
    }

    @Override
    public Response organizationsOrganizationIdChildrenGet(String organizationId) {

        try {
            return Response.ok().entity(getOrganizationManager().getChildOrganizationIds(organizationId)).build();
        } catch (OrganizationManagementClientException e) {
            return handleBadRequestResponse(e, log);
        } catch (OrganizationManagementException e) {
            return handleServerErrorResponse(e, log);
        } catch (Throwable throwable) {
            return handleUnexpectedServerError(throwable, log);
        }
    }

    @Override
    public Response organizationsOrganizationIdDelete(String organizationId) {

        try {
            getOrganizationManager().deleteOrganization(organizationId);
            return Response.ok().build();
        } catch (OrganizationManagementClientException e) {
            return handleBadRequestResponse(e, log);
        } catch (OrganizationManagementException e) {
            return handleServerErrorResponse(e, log);
        } catch (Throwable throwable) {
            return handleUnexpectedServerError(throwable, log);
        }
    }

    @Override
    public Response organizationsOrganizationIdPatch(String organizationId, List<OperationDTO> operations) {

        try {
            getOrganizationManager().patchOrganization(
                    organizationId,
                    operations.stream().map(op -> new Operation(op.getOp(), op.getPath(), op.getValue()))
                            .collect(Collectors.toList())
            );
            return Response.ok().build();
        } catch (OrganizationManagementClientException e) {
            return handleBadRequestResponse(e, log);
        } catch (OrganizationManagementException e) {
            return handleServerErrorResponse(e, log);
        } catch (Throwable throwable) {
            return handleUnexpectedServerError(throwable, log);
        }
    }

    @Override
    public Response organizationsOrganizationIdRolesPatch(String organizationId, List<OperationDTO> operations) {

        try {
            getOrganizationUserRoleManager().patchOrganizationAndUserRoleMapping(organizationId,
                    operations.stream()
                            .map(op -> new org.wso2.carbon.identity.organization.user.role.mgt.core.model.Operation(
                                    op.getOp(), op.getPath(), op.getValue()))
                            .collect(Collectors.toList()));
            return Response.ok().build();
        } catch (OrganizationUserRoleMgtClientException e) {
            return OrganizationUserRoleMgtEndpointUtil.handleBadRequestResponse(e, log);
        } catch (OrganizationUserRoleMgtException e) {
            return OrganizationUserRoleMgtEndpointUtil.handleServerErrorResponse(e, log);
        } catch (Throwable throwable) {
            return OrganizationUserRoleMgtEndpointUtil.handleUnexpectedServerError(throwable, log);
        }
    }

    @Override
    public Response organizationsOrganizationIdRolesPost(String organizationId, List<UserRoleMappingDTO> userRoles) {

        try {
            getOrganizationUserRoleManager().addOrganizationAndUserRoleMappings(organizationId,
            userRoles.stream()
                    .map(mapping -> new UserRoleMapping(mapping.getRoleId(), mapping.getUsers()))
                    .collect(Collectors.toList()));
            return Response.ok().build();
        } catch (OrganizationUserRoleMgtClientException e) {
            return OrganizationUserRoleMgtEndpointUtil.handleBadRequestResponse(e, log);
        } catch (OrganizationUserRoleMgtException e) {
            return OrganizationUserRoleMgtEndpointUtil.handleServerErrorResponse(e, log);
        } catch (Throwable throwable) {
            return OrganizationUserRoleMgtEndpointUtil.handleUnexpectedServerError(throwable, log);
        }
    }

    @Override
    public Response organizationsOrganizationIdRolesRoleIdUsersUserIdDelete(String organizationId, String roleId,
                                                                            String userId) {

        try {
            getOrganizationUserRoleManager()
                    .deleteOrganizationAndUserRoleMapping(organizationId, userId, roleId);
            return Response.accepted().build();
        } catch (OrganizationUserRoleMgtClientException e) {
            return OrganizationUserRoleMgtEndpointUtil.handleBadRequestResponse(e, log);
        } catch (OrganizationUserRoleMgtException e) {
            return OrganizationUserRoleMgtEndpointUtil.handleServerErrorResponse(e, log);
        } catch (Throwable throwable) {
            return OrganizationUserRoleMgtEndpointUtil.handleUnexpectedServerError(throwable, log);
        }
    }

    @Override
    public Response organizationsOrganizationIdUserstoreConfigsGet(String organizationId) {

        try {
            Map<String, UserStoreConfig> userStoreConfigs = getOrganizationManager().getUserStoreConfigs(organizationId);
            return Response.ok(getUserStoreConfigDTOsFromUserStoreConfigs(userStoreConfigs.values())).build();
        } catch (OrganizationManagementClientException e) {
            return handleBadRequestResponse(e, log);
        } catch (OrganizationManagementException e) {
            return handleServerErrorResponse(e, log);
        } catch (Throwable throwable) {
            return handleUnexpectedServerError(throwable, log);
        }
    }

    @Override
    public Response organizationsOrganizationIdUserstoreConfigsPatch(String organizationId,
                                                                     List<OperationDTO> operations) {

        try {
            getOrganizationManager().patchUserStoreConfigs(
                    organizationId,
                    operations.stream().map(op -> new Operation(op.getOp(), op.getPath(), op.getValue()))
                            .collect(Collectors.toList())
            );
            return Response.ok().build();
        } catch (OrganizationManagementClientException e) {
            return handleBadRequestResponse(e, log);
        } catch (OrganizationManagementException e) {
            return handleServerErrorResponse(e, log);
        } catch (Throwable throwable) {
            return handleUnexpectedServerError(throwable, log);
        }
    }

    private URI getResourceURI(Organization organization) throws URISyntaxException {

        return new URI(ORGANIZATION_PATH + '/' + organization.getId());
    }
}
