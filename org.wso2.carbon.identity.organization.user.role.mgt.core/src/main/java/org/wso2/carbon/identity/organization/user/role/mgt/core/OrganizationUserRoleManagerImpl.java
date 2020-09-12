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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.organization.user.role.mgt.core;

import org.apache.commons.lang.StringUtils;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.core.util.IdentityTenantUtil;
import org.wso2.carbon.identity.organization.mgt.core.model.Organization;
import org.wso2.carbon.identity.organization.user.role.mgt.core.dao.OrganizationUserRoleMgtDAO;
import org.wso2.carbon.identity.organization.user.role.mgt.core.dao.OrganizationUserRoleMgtDAOImpl;
import org.wso2.carbon.identity.organization.user.role.mgt.core.exception.OrganizationUserRoleMgtServerException;
import org.wso2.carbon.identity.organization.user.role.mgt.core.model.Operation;
import org.wso2.carbon.identity.organization.user.role.mgt.core.model.OrganizationUserRoleMapping;
import org.wso2.carbon.identity.organization.user.role.mgt.core.model.UserRoleMapping;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import java.util.ArrayList;
import java.util.List;

public class OrganizationUserRoleManagerImpl implements OrganizationUserRoleManager {

    @Override
    public void addOrganizationAndUserRoleMappings(String organizationId, List<UserRoleMapping> userRoleMappings)
            throws OrganizationUserRoleMgtServerException {

//        validateAddRoleMappingRequest(organizationUserRoleMappings);
        //@TODO check for mapping existance
        List<OrganizationUserRoleMapping> organizationUserRoleMappings = new ArrayList<>();
        for (UserRoleMapping userRoleMapping: userRoleMappings) {
            for (String userID: userRoleMapping.getUserIds()) {
                OrganizationUserRoleMapping organizationUserRoleMapping = new OrganizationUserRoleMapping();
                organizationUserRoleMapping.setOrganizationId(organizationId);
                organizationUserRoleMapping.setRoleId(userRoleMapping.getRoleId());
                organizationUserRoleMapping.setUserId(userID);
                organizationUserRoleMappings.add(organizationUserRoleMapping);
            }
        }
        OrganizationUserRoleMgtDAO organizationUserRoleMgtDAO = new OrganizationUserRoleMgtDAOImpl();
        organizationUserRoleMgtDAO.addOrganizationAndUserRoleMappings(organizationUserRoleMappings, getTenantId());
    }

    @Override
    public void patchOrganizationAndUserRoleMapping(String organizationId, List<Operation> operations)
            throws OrganizationUserRoleMgtServerException {

    }

    @Override
    public List<Organization> getOrganizationsByUserAndRole(String userId, Integer roleId) {

        return null;
    }

    @Override
    public List<String> getUserIdsByOrganizationAndRole(String organizationID, Integer roleId) {

        return null;
    }

    @Override
    public void deleteOrganizationAndUserRoleMapping(String organizationId, String userId, Integer roleId) {

        OrganizationUserRoleMgtDAO organizationUserRoleMgtDAO = new OrganizationUserRoleMgtDAOImpl();
        organizationUserRoleMgtDAO.deleteOrganizationAndUserRoleMapping(organizationId, userId, roleId, getTenantId());
    }

    @Override
    public boolean isOrganizationAndUserRoleMappingExists(String organizationId, String userId, Integer roleId) {

        OrganizationUserRoleMgtDAO organizationUserRoleMgtDAO = new OrganizationUserRoleMgtDAOImpl();
        return organizationUserRoleMgtDAO
                .isOrganizationAndUserRoleMappingExists(organizationId, userId, roleId, getTenantId());
    }

//    private void validateAddRoleMappingRequest(UserRoleMapping organizationUserRoleMapping) {
//
//        // Check required fields.
//        if (StringUtils.isBlank(organizationUserRoleMapping.getUserId())) {
//
//        }
//        if (StringUtils.isBlank(String.valueOf(organizationUserRoleMapping.getRoleId()))) {
//
//        }
//    }

    private int getTenantId() {

        return PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId();
    }
}
