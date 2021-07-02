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

package org.wso2.carbon.identity.organization.mgt.core.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.organization.mgt.core.cache.OrganizationCache;
import org.wso2.carbon.identity.organization.mgt.core.cache.OrganizationCacheEntry;
import org.wso2.carbon.identity.organization.mgt.core.cache.OrganizationCacheKey;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementException;
import org.wso2.carbon.identity.organization.mgt.core.model.Metadata;
import org.wso2.carbon.identity.organization.mgt.core.model.Operation;
import org.wso2.carbon.identity.organization.mgt.core.model.Organization;
import org.wso2.carbon.identity.organization.mgt.core.model.UserStoreConfig;
import org.wso2.carbon.identity.organization.mgt.core.search.Condition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.PATCH_PATH_ORG_PARENT_ID;

/**
 * Cached organization tree nodes for organization management.
 */
public class CacheBackedOrganizationMgtDAO implements OrganizationMgtDao {

    private static final Log log = LogFactory.getLog(CacheBackedOrganizationMgtDAO.class);

    private OrganizationMgtDao organizationMgtDao;
    private OrganizationCache organizationCache;

    public CacheBackedOrganizationMgtDAO(OrganizationMgtDao organizationMgtDao) {

        this.organizationMgtDao = organizationMgtDao;
        organizationCache = OrganizationCache.getInstance();

    }

    @Override
    public void addOrganization(int tenantId, Organization organization) throws OrganizationManagementException {

        // Remove cache for the parent organization of the adding org
        String parentOrgId = organization.getParent().getId();
        // Parent id can be null if it is the root. We are adding root as an org (verify later)
        if (log.isDebugEnabled()) {
            log.debug("Removing entry for  Organization: " + parentOrgId + " from cache");
        }
        clearOrganizationCache(tenantId, parentOrgId);
        organizationMgtDao.addOrganization(tenantId, organization);
    }

    @Override
    public void deleteOrganization(int tenantId, String organizationId) throws OrganizationManagementException {

        Organization orgToBeDeleted = organizationMgtDao.getOrganization(tenantId, organizationId, null, false);
        if (orgToBeDeleted != null) {
            String parentId = orgToBeDeleted.getParent().getId();
            // Clear the children cache for this orgId.
            if (log.isDebugEnabled()) {
                log.debug("Removing entry for  Organization: " + organizationId + " from cache");
            }
            clearOrganizationCache(tenantId, organizationId);
            // Clear the children cache for parent of this orgId.
            if (log.isDebugEnabled()) {
                log.debug("Removing entry for  Organization: " + parentId + " from cache");
            }
            clearOrganizationCache(tenantId, parentId);
        }
        organizationMgtDao.deleteOrganization(tenantId, organizationId);
    }

    @Override
    public boolean isOrganizationExistByName(int tenantId, String name) throws OrganizationManagementException {

        return organizationMgtDao.isOrganizationExistByName(tenantId, name);
    }

    @Override
    public boolean isOrganizationExistById(int tenantId, String id) throws OrganizationManagementException {

        OrganizationCacheKey cacheKey = new OrganizationCacheKey(id);
        OrganizationCacheEntry entry = organizationCache.getValueFromCache(cacheKey);

        if (entry != null) {
            return true;
        }
        return organizationMgtDao.isOrganizationExistById(tenantId, id);
    }

    @Override
    public Organization getOrganization(int tenantId, String organizationId, String userId, boolean getAsAdmin)
            throws OrganizationManagementException {

        return organizationMgtDao.getOrganization(tenantId, organizationId, userId, getAsAdmin);
    }

    @Override
    public String getOrganizationIdByName(int tenantId, String organizationName)
            throws OrganizationManagementException {

        return organizationMgtDao.getOrganizationIdByName(tenantId, organizationName);
    }

    @Override
    public List<Organization> getOrganizations(Condition condition, int tenantId, int offset, int limit, String sortBy,
                                               String sortOrder, List<String> requestedAttributes, String userId,
                                               boolean includePermissions,
                                               boolean listAsAdmin)
            throws OrganizationManagementException {

        return organizationMgtDao
                .getOrganizations(condition, tenantId, offset, limit, sortBy, sortOrder, requestedAttributes, userId,
                        includePermissions, listAsAdmin);
    }

    @Override
    public Map<String, UserStoreConfig> getUserStoreConfigsByOrgId(int tenantId, String organizationId)
            throws OrganizationManagementException {

        return organizationMgtDao.getUserStoreConfigsByOrgId(tenantId, organizationId);
    }

    @Override
    public List<String> getChildOrganizationIds(String organizationId, String userId)
            throws OrganizationManagementException {

        OrganizationCacheKey cacheKey = new OrganizationCacheKey(organizationId);
        OrganizationCacheEntry entry = organizationCache.getValueFromCache(cacheKey);

        if (entry != null) {
            if (log.isDebugEnabled()) {
                log.debug("Cache entry found for organization id: " + organizationId);
            }
            List<String> childrenOrgs = entry.getChildrenOrganizations();
            return childrenOrgs;
        } else {
            if (log.isDebugEnabled()) {
                log.debug(
                        "Fetching entry from DB because cache entry not found for organization id: " + organizationId);
            }
        }

        List<String> childrenOrgs = organizationMgtDao.getChildOrganizationIds(organizationId, null);
        if (childrenOrgs != null) {
            if (log.isDebugEnabled()) {
                log.debug("Entry fetched from the database for organization: " + organizationId + ". Updating cache");
            }
            organizationCache.addToCache(cacheKey, new OrganizationCacheEntry(childrenOrgs));
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Children organizations for organization with id " + organizationId +
                        " not found in cache or DB.");
            }
        }
        return childrenOrgs;
    }

    @Override
    public void patchOrganization(String organizationId, Operation operation) throws OrganizationManagementException {

        String path = operation.getPath();
        String op = operation.getOp();
        int tenantId = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId();
        if (path.equals(PATCH_PATH_ORG_PARENT_ID) && op.equalsIgnoreCase("REPLACE")) {
            String newParentId = operation.getValue();
            // Clear the children cache for the newParentId.
            if (log.isDebugEnabled()) {
                log.debug("Removing entry for Organization: " + newParentId + " from cache.");
            }
            clearOrganizationCache(tenantId, newParentId);

            Organization organizationToBePatched = organizationMgtDao.getOrganization(tenantId, organizationId, null,
                    false);
            if (organizationToBePatched != null) {
                String currentParentId = organizationToBePatched.getParent().getId();
                // Clear the children cache for the currentParentId.
                if (log.isDebugEnabled()) {
                    log.debug("Removing entry for Organization: " + currentParentId + " from cache.");
                }
                clearOrganizationCache(tenantId, currentParentId);
            }
        }
        organizationMgtDao.patchOrganization(organizationId, operation);
    }

    @Override
    public void patchOrganizationMultipleAttributes(String organizationId, List<Operation> operations) throws OrganizationManagementException {

        for (Operation operation : operations) {
            String path = operation.getPath();
            String op = operation.getOp();
            int tenantId = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId();
            if (path.equals(PATCH_PATH_ORG_PARENT_ID) && op.equalsIgnoreCase("REPLACE")) {
                String newParentId = operation.getValue();
                // Clear the children cache for the newParentId.
                if (log.isDebugEnabled()) {
                    log.debug("Removing entry for Organization: " + newParentId + " from cache.");
                }
                clearOrganizationCache(tenantId, newParentId);

                Organization organizationToBePatched = organizationMgtDao.getOrganization(tenantId, organizationId, null,
                        false);
                if (organizationToBePatched != null) {
                    String currentParentId = organizationToBePatched.getParent().getId();
                    // Clear the children cache for the currentParentId.
                    if (log.isDebugEnabled()) {
                        log.debug("Removing entry for Organization: " + currentParentId + " from cache.");
                    }
                    clearOrganizationCache(tenantId, currentParentId);
                }
            }
        }
        organizationMgtDao.patchOrganizationMultipleAttributes(organizationId, operations);
    }

    @Override
    public void patchUserStoreConfigs(String organizationId, Operation operation)
            throws OrganizationManagementException {

        organizationMgtDao.patchUserStoreConfigs(organizationId, operation);
    }

    @Override
    public boolean isAttributeExistByKey(int tenantId, String organizationId, String attributeKey)
            throws OrganizationManagementException {

        return organizationMgtDao.isAttributeExistByKey(tenantId, organizationId, attributeKey);
    }

    @Override
    public void modifyOrganizationMetadata(String organizationId, Metadata metadata)
            throws OrganizationManagementException {

        organizationMgtDao.modifyOrganizationMetadata(organizationId, metadata);
    }

    @Override
    public boolean isRdnAvailable(String rdn, String parentId, int tenantId) throws OrganizationManagementException {

        return organizationMgtDao.isRdnAvailable(rdn, parentId, tenantId);
    }

    private void clearOrganizationCache(int tenantId, String organizationId) throws OrganizationManagementException {

        // TODO whether we have to check children list ??
        Organization organization = this.getOrganization(tenantId, organizationId, null, false);
        if (organization != null) {
            if (log.isDebugEnabled()) {
                log.debug("Removing entry for organization with id " + organizationId + " from cache.");
            }
            OrganizationCacheKey cacheKey = new OrganizationCacheKey(organizationId);
            organizationCache.clearCacheEntry(cacheKey);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Entry for Organization with id " + organizationId + " not found in cache or DB");
            }
        }
    }
}
