package org.wso2.carbon.identity.organization.mgt.endpoint.impl;

import org.wso2.carbon.identity.organization.mgt.endpoint.ApiResponseMessage;
import org.wso2.carbon.identity.organization.mgt.endpoint.ImportApiService;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.OrganizationAddDTO;

import javax.ws.rs.core.Response;

public class ImportApiServiceImpl extends ImportApiService {

    @Override
    public Response importPost(OrganizationAddDTO organization) {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
}
