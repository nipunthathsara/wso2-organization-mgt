package org.wso2.carbon.identity.organization.mgt.endpoint;

import org.apache.cxf.jaxrs.ext.search.SearchContext;
import org.wso2.carbon.identity.organization.mgt.endpoint.*;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.*;

import org.wso2.carbon.identity.organization.mgt.endpoint.dto.BasicOrganizationDTO;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.ErrorDTO;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.OrganizationAddDTO;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.OrganizationDTO;
import java.util.List;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.OperationDTO;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.UserstoreConfigDTO;

import java.util.List;

import java.io.InputStream;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;

import javax.ws.rs.core.Response;

public abstract class OrganizationsApiService {
    public abstract Response organizationsGet(SearchContext searchContext, Integer offset, Integer limit, String sortBy, String sortOrder);
    public abstract Response organizationsImportPost(OrganizationAddDTO organization);
    public abstract Response organizationsOrganizationIdChildrenGet(String organizationId);
    public abstract Response organizationsOrganizationIdDelete(String organizationId);
    public abstract Response organizationsOrganizationIdGet(String organizationId);
    public abstract Response organizationsOrganizationIdPatch(String organizationId,List<OperationDTO> operations);
    public abstract Response organizationsOrganizationIdUserstoreConfigsGet(String organizationId);
    public abstract Response organizationsOrganizationIdUserstoreConfigsPatch(String organizationId,List<OperationDTO> operations);
    public abstract Response organizationsPost(OrganizationAddDTO organization);
}

