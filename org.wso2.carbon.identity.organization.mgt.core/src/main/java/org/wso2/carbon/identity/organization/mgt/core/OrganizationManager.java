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

package org.wso2.carbon.identity.organization.mgt.core;

import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementException;
import org.wso2.carbon.identity.organization.mgt.core.model.Operation;
import org.wso2.carbon.identity.organization.mgt.core.model.Organization;
import org.wso2.carbon.identity.organization.mgt.core.model.OrganizationAdd;
import org.wso2.carbon.identity.organization.mgt.core.model.UserStoreConfig;
import org.wso2.carbon.identity.organization.mgt.core.search.Condition;

import java.util.List;
import java.util.Map;

/**
 * Organization manager service interface.
 */
public interface OrganizationManager {

    /**
     * Add a new organization
     *
     * @param organizationAdd
     * @param isImport 'true' if you want to create an OU in the underlying user store.
     * @return
     * @throws OrganizationManagementException
     */
    Organization addOrganization(OrganizationAdd organizationAdd, boolean isImport)
            throws OrganizationManagementException;

    /**
     * Retrieve the organization identified by the provided ID, if such exists within the tenant.
     *
     * @param organizationId
     * @return
     * @throws OrganizationManagementException
     */
    Organization getOrganization(String organizationId) throws OrganizationManagementException;

    /**
     * Retrieve the ID of the organization identified by the provided name, if such exists within the tenant.
     *
     * @param organizationName
     * @return
     * @throws OrganizationManagementException
     */
    String getOrganizationIdByName(String organizationName) throws OrganizationManagementException;

    /**
     * List or search organizations with pagination and sorting.
     *
     * @param searchCondition Search condition
     * @param offset Number of items to be skipped
     * @param limit Number of items to be retrieved
     * @param sortBy Attribute to be sorted by
     * @param sortOrder Order to be sorted by
     * @return
     * @throws OrganizationManagementException
     */
    List<Organization> getOrganizations(Condition searchCondition, int offset, int limit, String sortBy, String sortOrder)
            throws OrganizationManagementException;

    /**
     * Check if the provided organization name exists within the tenant.
     *
     * @param organizationName
     * @return
     * @throws OrganizationManagementException
     */
    boolean isOrganizationExistByName(String organizationName) throws OrganizationManagementException;

    /**
     * Check if the provided organization ID exists within the tenant.
     *
     * @param id
     * @return
     * @throws OrganizationManagementException
     */
    boolean isOrganizationExistById(String id) throws OrganizationManagementException;

    /**
     * Patch organization and its attributes.
     *
     * @param organizationId
     * @param operations
     * @throws OrganizationManagementException
     */
    void patchOrganization(String organizationId, List<Operation> operations)
            throws OrganizationManagementException;

    /**
     * Delete the organization identified by the provided ID.
     *
     * @param organizationId
     * @throws OrganizationManagementException
     */
    void deleteOrganization(String organizationId) throws OrganizationManagementException;

    /**
     * Retrieve user store configurations of the organization identified by the provided ID.
     *
     * @param organizationId
     * @return
     * @throws OrganizationManagementException
     */
    Map<String, UserStoreConfig> getUserStoreConfigs(String organizationId) throws OrganizationManagementException;

    /**
     * Patch user store configurations of the organization identified by the provided ID.
     *
     * @param organizationId
     * @param operations
     * @throws OrganizationManagementException
     */
    void patchUserStoreConfigs(String organizationId, List<Operation> operations) throws OrganizationManagementException;

    /**
     * Retrieve children organization IDs of the organization identified by the given ID.
     *
     * @param organizationId
     * @return
     * @throws OrganizationManagementException
     */
    List<String> getChildOrganizationIds(String organizationId) throws OrganizationManagementException;
}
