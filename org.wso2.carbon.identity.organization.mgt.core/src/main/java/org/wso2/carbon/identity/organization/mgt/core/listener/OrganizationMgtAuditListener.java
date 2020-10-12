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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.organization.mgt.core.listener;

import org.apache.commons.logging.Log;
import org.wso2.carbon.CarbonConstants;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementException;
import org.wso2.carbon.identity.organization.mgt.core.model.Operation;
import org.wso2.carbon.identity.organization.mgt.core.model.Organization;
import org.wso2.carbon.identity.organization.mgt.core.model.OrganizationAdd;
import org.wso2.carbon.identity.organization.mgt.core.model.UserStoreConfig;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * This class implements the OrganizationMgtListener interface to provide auditing capability.
 */
public class OrganizationMgtAuditListener implements OrganizationMgtListener {

    private static final Log AUDIT = CarbonConstants.AUDIT_LOG;
    private static final String AUDIT_MESSAGE =
            "Initiator : %s | Action : %s | Target : %s | Data : { %s } | Result : %s ";
    private static final String SUCCESS = "Success";

    @Override
    public int getExecutionOrderId() {

        return 1;
    }

    @Override
    public int getDefaultOrderId() {

        return 1;
    }

    @Override
    public boolean isEnable() {

        return true;
    }

    @Override
    public boolean doPreCreateOrganization(OrganizationAdd organizationAdd, String tenantDomain, String username)
            throws OrganizationManagementException {

        return false;
    }

    @Override
    public boolean doPreImportOrganization(OrganizationAdd organizationAdd, String tenantDomain, String username)
            throws OrganizationManagementException {

        return false;
    }

    @Override
    public boolean doPreGetOrganization(String organizationId, String tenantDomain, String username)
            throws OrganizationManagementException {

        return false;
    }

    @Override
    public boolean doPreGetOrganizations(String tenantDomain, String username) throws OrganizationManagementException {

        return false;
    }

    @Override
    public boolean doPrePatchOrganization(String organizationId, Operation operation, String tenantDomain,
            String username) throws OrganizationManagementException {

        return false;
    }

    @Override
    public boolean doPreDeleteOrganization(String organizationId, String tenantDomain, String username)
            throws OrganizationManagementException {

        return false;
    }

    @Override
    public boolean doPreGetUserStoreConfigs(String organizationId, String tenantDomain, String username)
            throws OrganizationManagementException {

        return false;
    }

    @Override
    public boolean doPrePatchUserStoreConfigs(String organizationId, Operation operation, String tenantDomain,
            String username) throws OrganizationManagementException {

        return false;
    }

    @Override
    public boolean doPreGetChildOrganizationIds(String organizationId, String tenantDomain, String username)
            throws OrganizationManagementException {

        return false;
    }

    @Override
    public boolean doPostCreateOrganization(Organization organization, String tenantDomain, String username)
            throws OrganizationManagementException {

        String orgId = organization.getId();
        String orgName = organization.getName();
        AUDIT.info(String.format(AUDIT_MESSAGE, username, "create", orgId, orgName, SUCCESS));
        return true;
    }

    @Override
    public boolean doPostImportOrganization(Organization organization, String tenantDomain, String username)
            throws OrganizationManagementException {

        String orgId = organization.getId();
        String orgName = organization.getName();
        AUDIT.info(String.format(AUDIT_MESSAGE, username, "import", orgId, orgName, SUCCESS));
        return true;
    }

    @Override
    public boolean doPostGetOrganization(Organization organization, String tenantDomain, String username)
            throws OrganizationManagementException {

        String orgId = organization.getId();
        String orgName = organization.getName();
        AUDIT.info(String.format(AUDIT_MESSAGE, username, "retrieve", orgId, orgName, SUCCESS));
        return true;
    }

    @Override
    public boolean doPostGetOrganizations(List<Organization> organizations, String tenantDomain, String username)
            throws OrganizationManagementException {

        StringJoiner orgIds = new StringJoiner(",");
        for (Organization organization : organizations) {
            orgIds.add(organization.getId());
        }
        AUDIT.info(String.format(AUDIT_MESSAGE, username, "retrieve", "organizations", orgIds.toString(), SUCCESS));
        return true;
    }

    @Override
    public boolean doPostPatchOrganization(String organizationId, Operation operation, String tenantDomain,
            String username) throws OrganizationManagementException {

        StringBuilder data = new StringBuilder();
        data.append("Operation : " + operation.getOp());
        data.append(", Path : " + operation.getPath());
        data.append(", Value : " + operation.getValue());
        AUDIT.info(String.format(AUDIT_MESSAGE, username, "patch", organizationId, data.toString(), SUCCESS));
        return true;
    }

    @Override
    public boolean doPostDeleteOrganization(String organizationId, String tenantDomain, String username)
            throws OrganizationManagementException {

        AUDIT.info(String.format(AUDIT_MESSAGE, username, "delete", organizationId, organizationId, SUCCESS));
        return true;
    }

    @Override
    public boolean doPostGetUserStoreConfigs(String organizationId, Map<String, UserStoreConfig> userStoreConfigs,
            String tenantDomain, String username) throws OrganizationManagementException {

        AUDIT.info(String.format(AUDIT_MESSAGE, username, "retrieve user store configs", organizationId, organizationId,
                SUCCESS));
        return true;
    }

    @Override
    public boolean doPostPatchUserStoreConfigs(String organizationId, Operation operation, String tenantDomain,
            String username) throws OrganizationManagementException {

        StringBuilder data = new StringBuilder();
        data.append("Operation : " + operation.getOp());
        data.append(", Path : " + operation.getPath());
        data.append(", Value : " + operation.getValue());
        AUDIT.info(String.format(AUDIT_MESSAGE, username, "patch user store configs", organizationId, data.toString(),
                SUCCESS));
        return true;
    }

    @Override
    public boolean doPostGetChildOrganizationIds(String organizationId, List<String> childIds, String tenantDomain,
            String username) throws OrganizationManagementException {

        AUDIT.info(String.format(AUDIT_MESSAGE, username, "retrieve child organizations", organizationId,
                Arrays.toString(childIds.toArray()), SUCCESS));
        return true;
    }
}
