package org.wso2.carbon.identity.organization.mgt.endpoint.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.taskdefs.condition.Or;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementClientException;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementException;
import org.wso2.carbon.identity.organization.mgt.core.model.Organization;
import org.wso2.carbon.identity.organization.mgt.core.model.UserStoreConfig;
import org.wso2.carbon.identity.organization.mgt.endpoint.*;

import static org.wso2.carbon.identity.organization.mgt.endpoint.constants.OrganizationMgtEndpointConstants.ORGANIZATION_PATH;
import static org.wso2.carbon.identity.organization.mgt.endpoint.util.OrganizationMgtEndpointUtil.getBasicOrganizationDTOFromOrganization;
import static org.wso2.carbon.identity.organization.mgt.endpoint.util.OrganizationMgtEndpointUtil.getBasicOrganizationDTOsFromOrganizations;
import static org.wso2.carbon.identity.organization.mgt.endpoint.util.OrganizationMgtEndpointUtil.getOrganizationDTOFromOrganization;
import static org.wso2.carbon.identity.organization.mgt.endpoint.util.OrganizationMgtEndpointUtil.getOrganizationManager;
import static org.wso2.carbon.identity.organization.mgt.endpoint.util.OrganizationMgtEndpointUtil.getOrganizationAddFromDTO;
import static org.wso2.carbon.identity.organization.mgt.endpoint.util.OrganizationMgtEndpointUtil.getUserStoreConfigDTOsFromUserStoreConfigs;
import static org.wso2.carbon.identity.organization.mgt.endpoint.util.OrganizationMgtEndpointUtil.handleBadRequestResponse;
import static org.wso2.carbon.identity.organization.mgt.endpoint.util.OrganizationMgtEndpointUtil.handleServerErrorResponse;
import static org.wso2.carbon.identity.organization.mgt.endpoint.util.OrganizationMgtEndpointUtil.handleUnexpectedServerError;

import org.wso2.carbon.identity.organization.mgt.endpoint.dto.OrganizationAddDTO;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.wso2.carbon.identity.organization.mgt.endpoint.dto.OperationDTO;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.OrganizationDTO;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.UserstoreConfigDTO;
import org.wso2.carbon.identity.organization.mgt.endpoint.util.OrganizationMgtEndpointUtil;

import javax.ws.rs.core.Response;

public class OrganizationsApiServiceImpl extends OrganizationsApiService {

    private static final Log LOG = LogFactory.getLog(OrganizationsApiServiceImpl.class);

    @Override
    public Response organizationsPost(OrganizationAddDTO organizationAddDTO) {

        try {
            Organization organization = getOrganizationManager()
                    .addOrganization(getOrganizationAddFromDTO(organizationAddDTO), false);
            return Response.created(getResourceURI(organization))
                    .entity(getBasicOrganizationDTOFromOrganization(organization)).build();
        } catch (OrganizationManagementClientException e) {
            return handleBadRequestResponse(e, LOG);
        } catch (OrganizationManagementException e) {
            return handleServerErrorResponse(e, LOG);
        } catch (Throwable throwable) {
            return handleUnexpectedServerError(throwable, LOG);
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
            return handleBadRequestResponse(e, LOG);
        } catch (OrganizationManagementException e) {
            return handleServerErrorResponse(e, LOG);
        } catch (Throwable throwable) {
            return handleUnexpectedServerError(throwable, LOG);
        }
    }

    @Override
    public Response organizationsGet(String filter, Integer offset, Integer limit, String sortBy, String sortOrder) {

        try {
            List<Organization> organizations = getOrganizationManager()
                    .getOrganizations(filter, offset, limit, sortBy, sortOrder);
            return Response.ok().entity(getBasicOrganizationDTOsFromOrganizations(organizations)).build();
        } catch (OrganizationManagementClientException e) {
            return handleBadRequestResponse(e, LOG);
        } catch (OrganizationManagementException e) {
            return handleServerErrorResponse(e, LOG);
        } catch (Throwable throwable) {
            return handleUnexpectedServerError(throwable, LOG);
        }
    }

    @Override
    public Response organizationsOrganizationIdGet(String organizationId) {

        try {
            Organization organization = getOrganizationManager().getOrganization(organizationId);
            return Response.ok().entity(getOrganizationDTOFromOrganization(organization)).build();
        } catch (OrganizationManagementClientException e) {
            return handleBadRequestResponse(e, LOG);
        } catch (OrganizationManagementException e) {
            return handleServerErrorResponse(e, LOG);
        } catch (Throwable throwable) {
            return handleUnexpectedServerError(throwable, LOG);
        }
    }

    @Override
    public Response organizationsOrganizationIdChildrenGet(String organizationId) {

        try {
            return Response.ok().entity(getOrganizationManager().getChildOrganizationIds(organizationId)).build();
        } catch (OrganizationManagementClientException e) {
            return handleBadRequestResponse(e, LOG);
        } catch (OrganizationManagementException e) {
            return handleServerErrorResponse(e, LOG);
        } catch (Throwable throwable) {
            return handleUnexpectedServerError(throwable, LOG);
        }
    }

    @Override
    public Response organizationsOrganizationIdDelete(String organizationId) {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }

    @Override
    public Response organizationsOrganizationIdPatch(String organizationId, List<OperationDTO> operations) {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }

    @Override
    public Response organizationsOrganizationIdUserstoreConfigsGet(String organizationId) {

        try {
            Map<String, UserStoreConfig> userStoreConfigs = getOrganizationManager().getUserStoreConfigs(organizationId);
            return Response.ok(getUserStoreConfigDTOsFromUserStoreConfigs(userStoreConfigs.values())).build();
        } catch (OrganizationManagementClientException e) {
            return handleBadRequestResponse(e, LOG);
        } catch (OrganizationManagementException e) {
            return handleServerErrorResponse(e, LOG);
        } catch (Throwable throwable) {
            return handleUnexpectedServerError(throwable, LOG);
        }
    }

    @Override
    public Response organizationsOrganizationIdUserstoreConfigsPatch(String organizationId,
                                                                     List<OperationDTO> operations) {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }

    private URI getResourceURI(Organization organization) throws URISyntaxException {

        return new URI(ORGANIZATION_PATH + '/' + organization.getId());
    }
}
