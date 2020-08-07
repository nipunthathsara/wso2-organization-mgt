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

package org.wso2.carbon.identity.organization.mgt.core.dao;

import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementException;
import org.wso2.carbon.identity.organization.mgt.core.model.Organization;
import org.wso2.carbon.identity.organization.mgt.core.model.UserStoreConfig;

import java.util.List;
import java.util.Map;

/**
 * Interface to perform CRUD operations on {@link Organization}
 */
public interface OrganizationMgtDao {

    /**
     * Create new {@link Organization} in the database.
     * @param tenantId
     * @param organization
     * @throws OrganizationManagementException
     */
    void addOrganization(int tenantId, Organization organization) throws OrganizationManagementException;

    /**
     * Delete {@link Organization} by ID.
     * @param tenantId
     * @param organizationId
     * @throws OrganizationManagementException
     */
    void deleteOrganization(int tenantId, String organizationId) throws OrganizationManagementException;

    /**
     * Check if the {@link Organization} exists by name in a given tenant.
     * @param tenantId
     * @param name
     * @return
     * @throws OrganizationManagementException
     */
    boolean isOrganizationExistByName(int tenantId, String name) throws OrganizationManagementException;

    /**
     * Check if the {@link Organization} exists by organization Id in a given tenant.
     * @param tenantId
     * @param id
     * @return
     * @throws OrganizationManagementException
     */
    boolean isOrganizationExistById(int tenantId, String id) throws OrganizationManagementException;

    /**
     * Retrieve {@link Organization} by ID in the given tenant.
     * @param tenantId
     * @param organizationId
     * @return
     * @throws OrganizationManagementException
     */
    Organization getOrganization(int tenantId, String organizationId) throws OrganizationManagementException;

    /**
     *
     * @param tenantId
     * @param offset
     * @param limit
     * @param sortBy
     * @param sortOrder
     * @return
     * @throws OrganizationManagementException
     */
    List<Organization> getOrganizations(int tenantId, int offset, int limit, String sortBy, String sortOrder)
            throws OrganizationManagementException;

    /**
     * Returns user store configs of the {@link Organization} identified by the given ID.
     * @param tenantId
     * @param organizationId
     * @return
     * @throws OrganizationManagementException
     */
    Map<String, UserStoreConfig> getUserStoreConfigsByOrgId(int tenantId, String organizationId) throws OrganizationManagementException;
}
