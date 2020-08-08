package org.wso2.carbon.identity.organization.mgt.endpoint.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementClientException;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementException;
import org.wso2.carbon.identity.organization.mgt.core.model.Organization;
import org.wso2.carbon.identity.organization.mgt.core.model.UserStoreConfig;
import org.wso2.carbon.identity.organization.mgt.endpoint.*;

import static org.wso2.carbon.identity.organization.mgt.endpoint.constants.OrganizationMgtEndpointConstants.ORGANIZATION_PATH;
import static org.wso2.carbon.identity.organization.mgt.endpoint.util.OrganizationMgtEndpointUtil.getBasicOrganizationDTOFromModel;
import static org.wso2.carbon.identity.organization.mgt.endpoint.util.OrganizationMgtEndpointUtil.getOrganizationManager;
import static org.wso2.carbon.identity.organization.mgt.endpoint.util.OrganizationMgtEndpointUtil.getOrganizationAddFromDTO;
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

import javax.ws.rs.core.Response;

public class OrganizationsApiServiceImpl extends OrganizationsApiService {

    private static final Log LOG = LogFactory.getLog(OrganizationsApiServiceImpl.class);

    @Override
    public Response organizationsGet(String filter, Integer offset, Integer limit, String sortBy, String sortOrder) {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }

    @Override
    public Response organizationsImportPost(OrganizationAddDTO organization) {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }

    @Override
    public Response organizationsOrganizationIdChildrenGet(String organizationId) {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }

    @Override
    public Response organizationsOrganizationIdDelete(String organizationId) {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }

    @Override
    public Response organizationsOrganizationIdGet(String organizationId) {
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
            return Response.ok(userStoreConfigs.values().stream().collect(Collectors.toList())).build();
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

    @Override
    public Response organizationsPost(OrganizationAddDTO organizationAddDTO) {

        try {
            Organization organization = getOrganizationManager()
                    .addOrganization(getOrganizationAddFromDTO(organizationAddDTO));
            return Response.created(getResourceURI(organization))
                    .entity(getBasicOrganizationDTOFromModel(organization)).build();
        } catch (OrganizationManagementClientException e) {
            return handleBadRequestResponse(e, LOG);
        } catch (OrganizationManagementException e) {
            return handleServerErrorResponse(e, LOG);
        } catch (Throwable throwable) {
            return handleUnexpectedServerError(throwable, LOG);
        }
    }

    private URI getResourceURI(Organization organization) throws URISyntaxException {

        return new URI(ORGANIZATION_PATH + '/' + organization.getId());
    }
}
