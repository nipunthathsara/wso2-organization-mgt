package org.wso2.carbon.identity.organization.mgt.endpoint.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementClientException;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementException;
import org.wso2.carbon.identity.organization.mgt.core.model.Organization;
import org.wso2.carbon.identity.organization.mgt.endpoint.*;

import static org.wso2.carbon.identity.organization.mgt.endpoint.constants.OrganizationMgtEndpointConstants.ORGANIZATION_PATH;

import org.wso2.carbon.identity.organization.mgt.endpoint.dto.*;


import org.wso2.carbon.identity.organization.mgt.endpoint.dto.BasicOrganizationDTO;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.ErrorDTO;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.OrganizationDTO;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.OrganizationAddDTO;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import java.io.InputStream;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;

import static org.wso2.carbon.identity.organization.mgt.endpoint.util.OrganizationMgtEndpointUtil.getOrganizationAddFromDTO;
import static org.wso2.carbon.identity.organization.mgt.endpoint.util.OrganizationMgtEndpointUtil.getOrganizationDTOFromModel;
import static org.wso2.carbon.identity.organization.mgt.endpoint.util.OrganizationMgtEndpointUtil.getOrganizationManager;
import static org.wso2.carbon.identity.organization.mgt.endpoint.util.OrganizationMgtEndpointUtil.handleBadRequestResponse;
import static org.wso2.carbon.identity.organization.mgt.endpoint.util.OrganizationMgtEndpointUtil.handleServerErrorResponse;
import static org.wso2.carbon.identity.organization.mgt.endpoint.util.OrganizationMgtEndpointUtil.handleUnexpectedServerError;

import javax.ws.rs.core.Response;

public class OrganizationsApiServiceImpl extends OrganizationsApiService {

    private static final Log LOG = LogFactory.getLog(OrganizationsApiServiceImpl.class);

    @Override
    public Response organizationsGet(Integer offset, Integer limit, String sortBy, String sortOrder) {
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
    public Response organizationsOrganizationIdPut(String organizationId, OrganizationAddDTO organization) {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }

    @Override
    public Response organizationsPost(OrganizationAddDTO organizationAddDTO) {
        try {
            Organization organization = getOrganizationManager().addOrganization(getOrganizationAddFromDTO(organizationAddDTO));
            return Response.created(getResourceURI(organization)).entity(getOrganizationDTOFromModel(organization)).build();
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
