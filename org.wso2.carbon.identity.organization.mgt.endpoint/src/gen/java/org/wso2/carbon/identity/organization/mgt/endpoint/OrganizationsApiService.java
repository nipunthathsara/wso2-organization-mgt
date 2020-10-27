/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.identity.organization.mgt.endpoint;

import org.apache.cxf.jaxrs.ext.search.SearchContext;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.OrganizationAddDTO;

import java.util.List;

import org.wso2.carbon.identity.organization.mgt.endpoint.dto.OperationDTO;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.UserRoleMappingDTO;

import javax.ws.rs.core.Response;

public abstract class OrganizationsApiService {
    public abstract Response organizationsGet(SearchContext searchContext, Integer offset, Integer limit, String sortBy,
            String sortOrder, String attributes, Boolean includePermissions);

    public abstract Response organizationsImportPost(OrganizationAddDTO organization);

    public abstract Response organizationsOrganizationIdChildrenGet(String organizationId);

    public abstract Response organizationsOrganizationIdDelete(String organizationId);

    public abstract Response organizationsOrganizationIdGet(String organizationId);

    public abstract Response organizationsOrganizationIdPatch(String organizationId, List<OperationDTO> operations);

    public abstract Response organizationsOrganizationIdRolesPost(String organizationId, UserRoleMappingDTO userRoles);

    public abstract Response organizationsOrganizationIdRolesRoleIdUsersGet(String organizationId, String roleId, Integer offset, Integer limit, String attributes);

    public abstract Response organizationsOrganizationIdRolesRoleIdUsersUserIdDelete(String organizationId, String roleId, String userId, Boolean includeSubOrgs);

    public abstract Response organizationsOrganizationIdUsersUserIdRolesGet(String organizationId, String userId);

    public abstract Response organizationsOrganizationIdUserstoreConfigsGet(String organizationId);

    public abstract Response organizationsOrganizationIdUserstoreConfigsPatch(String organizationId, List<OperationDTO> operations);

    public abstract Response organizationsPost(OrganizationAddDTO organization);
}
