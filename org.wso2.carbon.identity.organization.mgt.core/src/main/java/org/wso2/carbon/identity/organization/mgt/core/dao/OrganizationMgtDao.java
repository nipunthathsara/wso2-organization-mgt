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
import org.wso2.carbon.identity.organization.mgt.core.model.Metadata;
import org.wso2.carbon.identity.organization.mgt.core.model.Operation;
import org.wso2.carbon.identity.organization.mgt.core.model.Organization;
import org.wso2.carbon.identity.organization.mgt.core.model.UserStoreConfig;
import org.wso2.carbon.identity.organization.mgt.core.search.Condition;

import java.util.List;
import java.util.Map;

/**
 * Interface to perform CRUD operations on {@link Organization}
 */
public interface OrganizationMgtDao {

    /**
     * Create new {@link Organization} in the database.
     *
     * @param tenantId
     * @param organization
     * @throws OrganizationManagementException
     */
    void addOrganization(int tenantId, Organization organization) throws OrganizationManagementException;

    /**
     * Delete {@link Organization} by ID.
     *
     * @param tenantId
     * @param organizationId
     * @throws OrganizationManagementException
     */
    void deleteOrganization(int tenantId, String organizationId) throws OrganizationManagementException;

    /**
     * Check if the {@link Organization} exists by name in a given tenant.
     *
     * @param tenantId
     * @param name
     * @return
     * @throws OrganizationManagementException
     */
    boolean isOrganizationExistByName(int tenantId, String name) throws OrganizationManagementException;

    /**
     * Check if the {@link Organization} exists by organization Id in a given tenant.
     *
     * @param tenantId
     * @param id
     * @return
     * @throws OrganizationManagementException
     */
    boolean isOrganizationExistById(int tenantId, String id) throws OrganizationManagementException;

    /**
     * Retrieve {@link Organization} by ID in the given tenant.
     *
     * @param tenantId
     * @param organizationId
     * @param userId
     * @param getAsAdmin
     * @return
     * @throws OrganizationManagementException
     */
    Organization getOrganization(int tenantId, String organizationId, String userId, boolean getAsAdmin)
            throws OrganizationManagementException;

    /**
     * Retrieve Organization ID if the given organization name exists for the tenant.
     *
     * @param tenantId
     * @param organizationName
     * @return
     * @throws OrganizationManagementException
     */
    String getOrganizationIdByName(int tenantId, String organizationName) throws OrganizationManagementException;

    /**
     * @param condition
     * @param tenantId
     * @param offset
     * @param limit
     * @param sortBy
     * @param sortOrder
     * @param requestedAttributes
     * @param userId
     * @param includePermissions
     * @param listAsAdmin
     * @return
     * @throws OrganizationManagementException
     */
    List<Organization> getOrganizations(Condition condition, int tenantId, int offset, int limit, String sortBy,
            String sortOrder, List<String> requestedAttributes, String userId, boolean includePermissions,
            boolean listAsAdmin)
            throws OrganizationManagementException;

    /**
     * Returns user store configs of the {@link Organization} identified by the given ID.
     *
     * @param tenantId
     * @param organizationId
     * @return
     * @throws OrganizationManagementException
     */
    Map<String, UserStoreConfig> getUserStoreConfigsByOrgId(int tenantId, String organizationId)
            throws OrganizationManagementException;

    /**
     * @param organizationId
     * @param userId
     * @return
     * @throws OrganizationManagementException
     */
    List<String> getChildOrganizationIds(String organizationId, String userId) throws OrganizationManagementException;

    /**
     * Add, remove or replace organization field, attribute or user store configuration.
     *
     * @param organizationId
     * @param operation
     * @throws OrganizationManagementException
     */
    void patchOrganization(String organizationId, Operation operation) throws OrganizationManagementException;

    /**
     * Add, remove or replace multiple organization fields, attributes or user store configurations with single DB statement
     *
     * @param organizationId
     * @param operations
     * @throws OrganizationManagementException
     */
    void patchOrganizationMultipleAttributes(String organizationId, List<Operation> operations)
            throws OrganizationManagementException;

    /**
     * Patch user store configurations of the organization identified by the provided ID.
     *
     * @param organizationId
     * @param operation
     * @throws OrganizationManagementException
     */
    void patchUserStoreConfigs(String organizationId, Operation operation) throws OrganizationManagementException;

    /**
     * @param tenantId
     * @param organizationId
     * @param attributeKey
     * @return
     * @throws OrganizationManagementException
     */
    boolean isAttributeExistByKey(int tenantId, String organizationId, String attributeKey)
            throws OrganizationManagementException;

    /**
     * Update lastModified and lastModifiedBy fields of the organization identified by the provided ID.
     *
     * @param organizationId
     * @param metadata
     * @throws OrganizationManagementException
     */
    void modifyOrganizationMetadata(String organizationId, Metadata metadata) throws OrganizationManagementException;

    /**
     * Check if the RDN is already taken for a given parent (or ROOT)
     *
     * @param rdn
     * @param parentId
     * @param tenantId
     * @return
     * @throws OrganizationManagementException
     */
    boolean isRdnAvailable(String rdn, String parentId, int tenantId) throws OrganizationManagementException;
}
