package org.wso2.carbon.identity.organization.mgt.endpoint;

import org.wso2.carbon.identity.organization.mgt.endpoint.*;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.*;

import org.wso2.carbon.identity.organization.mgt.endpoint.dto.BasicOrganizationDTO;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.ErrorDTO;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.OrganizationDTO;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.OrganizationAddDTO;

import java.util.List;

import java.io.InputStream;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;

import javax.ws.rs.core.Response;

public abstract class OrganizationsApiService {
    public abstract Response organizationsGet(Integer offset, Integer limit, String sortBy, String sortOrder);

    public abstract Response organizationsOrganizationIdDelete(String organizationId);

    public abstract Response organizationsOrganizationIdGet(String organizationId);

    public abstract Response organizationsOrganizationIdPut(String organizationId, OrganizationAddDTO organization);

    public abstract Response organizationsPost(OrganizationAddDTO organization);
}
