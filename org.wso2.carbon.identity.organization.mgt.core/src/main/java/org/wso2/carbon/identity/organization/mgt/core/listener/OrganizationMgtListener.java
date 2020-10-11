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

import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementException;
import org.wso2.carbon.identity.organization.mgt.core.model.Operation;
import org.wso2.carbon.identity.organization.mgt.core.model.Organization;
import org.wso2.carbon.identity.organization.mgt.core.model.OrganizationAdd;
import org.wso2.carbon.identity.organization.mgt.core.model.UserStoreConfig;

import java.util.List;
import java.util.Map;

/**
 * This interface defines pre and post listeners for the organization management API.
 */
public interface OrganizationMgtListener {

    /**
     * Get the execution order identifier for this listener.
     *
     * @return The execution order identifier integer value.
     */
    int getExecutionOrderId();

    /**
     * Get the default order identifier for this listener.
     *
     * @return default order id
     */
    int getDefaultOrderId();

    /**
     * Check whether the listener is enabled or not
     *
     * @return true if enabled
     */
    boolean isEnable();

    boolean doPreCreateOrganization(OrganizationAdd organizationAdd, String tenantDomain, String username)
            throws OrganizationManagementException;

    boolean doPreImportOrganization(OrganizationAdd organizationAdd, String tenantDomain, String username)
            throws OrganizationManagementException;

    boolean doPreGetOrganization(String organizationId, String tenantDomain, String username)
            throws OrganizationManagementException;

    boolean doPreGetOrganizations(String tenantDomain, String username) throws OrganizationManagementException;

    boolean doPrePatchOrganization(String organizationId, Operation operation, String tenantDomain, String username)
            throws OrganizationManagementException;

    boolean doPreDeleteOrganization(String organizationId, String tenantDomain, String username)
            throws OrganizationManagementException;

    boolean doPreGetUserStoreConfigs(String organizationId, String tenantDomain, String username)
            throws OrganizationManagementException;

    boolean doPrePatchUserStoreConfigs(String organizationId, Operation operation, String tenantDomain, String username)
            throws OrganizationManagementException;

    boolean doPreGetChildOrganizationIds(String organizationId, String tenantDomain, String username)
            throws OrganizationManagementException;

    boolean doPostCreateOrganization(Organization organization, String tenantDomain, String username)
            throws OrganizationManagementException;

    boolean doPostImportOrganization(Organization organization, String tenantDomain, String username)
            throws OrganizationManagementException;

    boolean doPostGetOrganization(Organization organization, String tenantDomain, String username)
            throws OrganizationManagementException;

    boolean doPostGetOrganizations(List<Organization> organizations, String tenantDomain, String username)
            throws OrganizationManagementException;

    boolean doPostPatchOrganization(String organizationId, Operation operation, String tenantDomain, String username)
            throws OrganizationManagementException;

    boolean doPostDeleteOrganization(String organizationId, String tenantDomain, String username)
            throws OrganizationManagementException;

    boolean doPostGetUserStoreConfigs(String organizationId, Map<String, UserStoreConfig> userStoreConfigs,
            String tenantDomain,
            String username) throws OrganizationManagementException;

    boolean doPostPatchUserStoreConfigs(String organizationId, Operation operation, String tenantDomain, String username)
            throws OrganizationManagementException;

    boolean doPostGetChildOrganizationIds(String organizationId, List<String> childIds, String tenantDomain,
            String username) throws OrganizationManagementException;
}
