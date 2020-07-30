package org.wso2.carbon.identity.organization.mgt.endpoint.impl;

import org.wso2.carbon.identity.organization.mgt.endpoint.*;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.*;


import org.wso2.carbon.identity.organization.mgt.endpoint.dto.ErrorDTO;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.OrganizationAddDTO;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.OrganizationDTO;

import java.util.List;

import java.io.InputStream;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;

import javax.ws.rs.core.Response;

public class ImportApiServiceImpl extends ImportApiService {
    @Override
    public Response importPost(OrganizationAddDTO organization){
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
}
