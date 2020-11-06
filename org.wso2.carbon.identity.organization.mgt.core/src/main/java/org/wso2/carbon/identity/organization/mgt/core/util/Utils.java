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

package org.wso2.carbon.identity.organization.mgt.core.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.database.utils.jdbc.JdbcTemplate;
import org.wso2.carbon.identity.core.persistence.UmPersistenceManager;
import org.wso2.carbon.identity.core.util.IdentityDatabaseUtil;
import org.wso2.carbon.identity.core.util.IdentityUtil;
import org.wso2.carbon.identity.organization.mgt.core.OrganizationManager;
import org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementClientException;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementException;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementServerException;
import org.wso2.carbon.identity.organization.mgt.core.internal.OrganizationMgtDataHolder;
import org.wso2.carbon.identity.organization.mgt.core.model.Organization;
import org.wso2.carbon.identity.organization.mgt.core.model.OrganizationAdd;
import org.wso2.carbon.user.api.RealmConfiguration;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.UserStoreManager;
import org.wso2.carbon.user.core.common.AbstractUserStoreManager;
import org.wso2.carbon.user.core.ldap.UniqueIDReadOnlyLDAPUserStoreManager;
import org.wso2.carbon.user.core.ldap.UniqueIDReadWriteLDAPUserStoreManager;
import org.wso2.carbon.user.core.model.ExpressionCondition;
import org.wso2.carbon.user.core.model.OperationalCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;

import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ACCOUNT_DISABLED_CLAIM_URI;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ADMIN_MANAGE_IDENTITY_PERMISSION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ADMIN_MANAGE_PERMISSION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ADMIN_PERMISSION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_INVALID_ORGANIZATION_USER_STORE_CONFIGURATIONS;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_ORGANIZATION_PATCH_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_UNCLASSIFIED_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_UNEXPECTED;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_USER_STORE_CONFIGURATIONS_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_USER_STORE_OPERATIONS_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_BASE_PERMISSION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_CREATE_PERMISSION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_DELETE_PERMISSION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_EDIT_PERMISSION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_ID_CLAIM_URI;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_ID_DEFAULT_CLAIM_URI;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_VIEW_PERMISSION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ROLE_MGT_BASE_PERMISSION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ROLE_MGT_CREATE_PERMISSION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ROLE_MGT_DELETE_PERMISSION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ROLE_MGT_EDIT_PERMISSION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ROLE_MGT_VIEW_PERMISSION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.USER_MGT_BASE_PERMISSION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.USER_MGT_CREATE_PERMISSION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.USER_MGT_DELETE_PERMISSION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.USER_MGT_EDIT_PERMISSION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.USER_MGT_LIST_PERMISSION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.USER_MGT_VIEW_PERMISSION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.USER_STORE_DOMAIN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.MAX_QUERY_LENGTH_IN_BYTES_SQL;
import static org.wso2.carbon.user.core.UserStoreConfigConstants.DOMAIN_NAME;
import static org.wso2.carbon.user.core.UserStoreConfigConstants.userSearchBase;

/**
 * This class provides utility functions for the Organization Management.
 */
public class Utils {

    private static final Log log = LogFactory.getLog(Utils.class);

    public static OrganizationManagementClientException handleClientException(
            OrganizationMgtConstants.ErrorMessages error, String data) {

        String message;
        if (StringUtils.isNotBlank(data)) {
            message = String.format(error.getMessage(), data);
        } else {
            message = error.getMessage();
        }
        return new OrganizationManagementClientException(message, error.getCode());
    }

    public static OrganizationManagementServerException handleServerException(
            OrganizationMgtConstants.ErrorMessages error, String data, Throwable e) {

        String message;
        if (StringUtils.isNotBlank(data)) {
            message = String.format(error.getMessage(), data);
        } else {
            message = error.getMessage();
        }
        return new OrganizationManagementServerException(message, error.getCode(), e);
    }

    public static OrganizationManagementServerException handleServerException(
            OrganizationMgtConstants.ErrorMessages error, String data) {

        String message;
        if (StringUtils.isNotBlank(data)) {
            message = String.format(error.getMessage(), data);
        } else {
            message = error.getMessage();
        }
        return new OrganizationManagementServerException(message, error.getCode());
    }

    public static String generateUniqueID() {

        return UUID.randomUUID().toString();
    }

    public static String getLdapRootDn(String userStoreDomain, int tenantId) throws OrganizationManagementException {

        List<RealmConfiguration> realmConfigurations = getRealmConfigurations(tenantId);
        RealmConfiguration matchingRealmConfig = null;
        for (RealmConfiguration realmConfig : realmConfigurations) {
            if (realmConfig.getUserStoreProperties().get(DOMAIN_NAME).equalsIgnoreCase(userStoreDomain)) {
                matchingRealmConfig = realmConfig;
                break;
            }
        }
        // Check if user domain exists
        if (matchingRealmConfig == null) {
            throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_USER_STORE_CONFIGURATIONS,
                    "Provided user store domain is not valid : " + userStoreDomain);
        }
        // Check if the underlying user store supports
        Class className;
        try {
            className = Class.forName(matchingRealmConfig.getUserStoreClass());
        } catch (ClassNotFoundException e) {
            throw handleServerException(ERROR_CODE_UNEXPECTED, "Error while loading user store manager class", e);
        }
        if (!(UniqueIDReadWriteLDAPUserStoreManager.class.isAssignableFrom(className)
                || UniqueIDReadOnlyLDAPUserStoreManager.class.isAssignableFrom(className))) {
            throw handleClientException(ERROR_CODE_INVALID_ORGANIZATION_USER_STORE_CONFIGURATIONS,
                    "Provided user store domain does not support organization management : " + userStoreDomain);
        }
        return matchingRealmConfig.getUserStoreProperties().get(userSearchBase);
    }

    public static List<RealmConfiguration> getRealmConfigurations(int tenantId) throws OrganizationManagementException {

        RealmConfiguration realmConfig;
        List<RealmConfiguration> realmConfigurations = new ArrayList<>();
        try {
            // Add PRIMARY user store
            realmConfig = OrganizationMgtDataHolder.getInstance().getRealmService().getTenantUserRealm(tenantId)
                    .getRealmConfiguration();
            //            realmConfig = CarbonContext.getThreadLocalCarbonContext().getUserRealm()
            //            .getRealmConfiguration();
            realmConfigurations.add(realmConfig);
            do {
                // Check for the tenant's secondary user stores
                realmConfig = realmConfig.getSecondaryRealmConfig();
                if (realmConfig != null) {
                    realmConfigurations.add(realmConfig);
                }
            } while (realmConfig != null);
        } catch (UserStoreException e) {
            throw handleServerException(ERROR_CODE_USER_STORE_CONFIGURATIONS_ERROR,
                    "Error while obtaining realm configurations", e);
        }
        return realmConfigurations;
    }

    public static void logOrganizationAddObject(OrganizationAdd organizationAdd) {

        if (!log.isDebugEnabled()) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        StringJoiner attributesJoiner = new StringJoiner(",");
        StringJoiner configJoiner = new StringJoiner(",");
        sb.append("Logging OrganizationAdd object");
        sb.append("\nName : " + organizationAdd.getName());
        sb.append("\nDisplay name : " + organizationAdd.getDisplayName());
        sb.append("\nDescription : " + organizationAdd.getDescription());
        sb.append("\nParent id : " + organizationAdd.getParent().getId());
        // Attributes cannot be null
        organizationAdd.getAttributes().forEach(entry -> attributesJoiner.add(entry.toString()));
        sb.append("\nAttributes : " + attributesJoiner.toString());
        // User store configs can not be null
        organizationAdd.getUserStoreConfigs().forEach(entry -> configJoiner.add(entry.toString()));
        sb.append("\nUser Store Configs : " + configJoiner.toString());
        log.debug(sb.toString());
    }

    public static void logOrganizationObject(Organization organization) {

        if (!log.isDebugEnabled()) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Logging Organization object");
        sb.append("\nId : " + organization.getId());
        sb.append("\nTenant id : " + organization.getTenantId());
        sb.append("\nName : " + organization.getName());
        sb.append("\nDisplay name : " + organization.getDisplayName());
        sb.append("\nDescription : " + organization.getDescription());
        sb.append("\nParent id : " + organization.getParent().getId());
        sb.append("\nParent name : " + organization.getParent().getName());
        sb.append("\nParent display name : " + organization.getParent().getDisplayName());
        sb.append("\nParent $ref : " + organization.getParent().getRef());
        sb.append("\nStatus : " + organization.getStatus().toString());
        sb.append("\nCreated time : " + organization.getMetadata().getCreated());
        sb.append("\nLast modified time : " + organization.getMetadata().getLastModified());
        sb.append("\nCreated by id : " + organization.getMetadata().getCreatedBy().getId());
        sb.append("\nCreated by username : " + organization.getMetadata().getCreatedBy().getUsername());
        sb.append("\nCreated by $ref : " + organization.getMetadata().getCreatedBy().getRef());
        sb.append("\nLast modified by id : " + organization.getMetadata().getLastModifiedBy().getId());
        sb.append("\nLast modified by username : " + organization.getMetadata().getLastModifiedBy().getUsername());
        sb.append("\nLast modified by $ref : " + organization.getMetadata().getLastModifiedBy().getRef());
        sb.append("\nUser store configs : ");
        StringJoiner configJoiner = new StringJoiner(",");
        organization.getUserStoreConfigs().entrySet().stream()
                .forEach(entry -> configJoiner.add(entry.getValue().toString()));
        sb.append(configJoiner.toString());
        sb.append("\nAttributes : ");
        StringJoiner attributeJoiner = new StringJoiner(",");
        organization.getAttributes().entrySet().stream()
                .forEach(entry -> attributeJoiner.add(entry.getValue().toString()));
        sb.append(attributeJoiner.toString());
        log.debug(sb.toString());
    }

    public static int getMaximumQueryLengthInBytes() {

        return StringUtils.isBlank(MAX_QUERY_LENGTH_IN_BYTES_SQL) ? 4194304 :
                Integer.parseInt(MAX_QUERY_LENGTH_IN_BYTES_SQL);
    }

    public static JdbcTemplate getNewTemplate() {

        return new JdbcTemplate(UmPersistenceManager.getInstance().getDataSource());
    }

    public static JdbcTemplate getNewIdentityTemplate() {

        return new JdbcTemplate(IdentityDatabaseUtil.getDataSource());
    }

    public static String getUserIDFromUserName(String username, int tenantId)
            throws OrganizationManagementServerException {

        try {
            AbstractUserStoreManager userStoreManager = (AbstractUserStoreManager) OrganizationMgtDataHolder
                    .getInstance().getRealmService().getTenantUserRealm(tenantId).getUserStoreManager();
            return userStoreManager.getUserIDFromUserName(username);
        } catch (UserStoreException e) {
            throw handleServerException(ERROR_CODE_USER_STORE_OPERATIONS_ERROR,
                    "Error obtaining ID for the username : " + username + ", tenant id : " + tenantId);
        }
    }

    public static String getUserNameFromUserID(String userId, int tenantId)
            throws OrganizationManagementServerException {

        try {
            AbstractUserStoreManager userStoreManager = (AbstractUserStoreManager) OrganizationMgtDataHolder
                    .getInstance().
                            getRealmService().getTenantUserRealm(tenantId).getUserStoreManager();
            return userStoreManager.getUserNameFromUserID(userId);
        } catch (UserStoreException e) {
            throw handleServerException(ERROR_CODE_USER_STORE_OPERATIONS_ERROR,
                    "Error obtaining username for the user id : " + userId + ", tenant id : " + tenantId);
        }
    }

    /**
     * Checks whether a given organization has any active users. accountDisabled claim is used to determine the
     * active status of a user. If the claim value is false, the user is considered active.
     *
     * @param organizationId ID of the organization.
     * @param tenantId Tenant ID
     * @return True if at least one active user found, false otherwise.
     * @throws OrganizationManagementException If any errors occurred.
     */
    public static boolean hasActiveUsers(String organizationId, int tenantId) throws OrganizationManagementException {

        String userStoreDomain = getOrganizationManager().getUserStoreConfigs(organizationId).get(USER_STORE_DOMAIN)
                .getValue();
        RealmConfiguration matchingRealmConfig = Utils.getMatchingRealmConfiguration(tenantId, userStoreDomain);
        if (matchingRealmConfig == null) {
            throw handleServerException(ERROR_CODE_ORGANIZATION_PATCH_ERROR,
                    "Couldn't find realm configurations for the user store domain : " + userStoreDomain);
        }
        String orgIdClaimUri = StringUtils.isNotBlank(IdentityUtil.getProperty(ORGANIZATION_ID_CLAIM_URI)) ?
                IdentityUtil.getProperty(ORGANIZATION_ID_CLAIM_URI).trim() :
                ORGANIZATION_ID_DEFAULT_CLAIM_URI;
        try {
            // Get user store manager for the domain
            UserStoreManager userStoreManager = OrganizationMgtDataHolder.getInstance().getRealmService()
                    .getUserRealm(matchingRealmConfig).getUserStoreManager();
            // Get tenant user realm
            org.wso2.carbon.user.api.UserRealm tenantUserRealm = OrganizationMgtDataHolder.getInstance()
                    .getRealmService().getTenantUserRealm(tenantId);
            org.wso2.carbon.user.api.ClaimManager claimManager = tenantUserRealm.getClaimManager();
            // Find the attribute name for 'accountDisabled' claim
            String accDisabledAttribute = claimManager.getAttributeName(userStoreDomain, ACCOUNT_DISABLED_CLAIM_URI);
            String orgIdAttribute = claimManager.getAttributeName(userStoreDomain, orgIdClaimUri);

            // We assume the accountDisabled attribute is set with an appropriate value for all the users.
            ExpressionCondition orgIdCondition = new ExpressionCondition("EQ", orgIdAttribute, organizationId);
            ExpressionCondition accDisabledFalseCondition = new ExpressionCondition("EQ", accDisabledAttribute,
                    "false");
            OperationalCondition opCondition = new OperationalCondition("AND", orgIdCondition,
                    accDisabledFalseCondition);
            String[] users = ((AbstractUserStoreManager) userStoreManager)
                    .getUserList(opCondition, userStoreDomain, null, 1, 0, null, null);
            return users.length > 0;

        } catch (org.wso2.carbon.user.api.UserStoreException e) {
            throw handleServerException(ERROR_CODE_ORGANIZATION_PATCH_ERROR, "Error while checking for active users",
                    e);
        }
    }

    /**
     * Checks whether a given organization has any users.
     *
     * @param organizationId ID of the organization.
     * @param tenantId Tenant ID
     * @return True if at least one user found, false otherwise.
     * @throws OrganizationManagementException If any errors occurred.
     */
    public static boolean hasUsers(String organizationId, int tenantId) throws OrganizationManagementException {

        String userStoreDomain = getOrganizationManager().getUserStoreConfigs(organizationId).get(USER_STORE_DOMAIN)
                .getValue();
        RealmConfiguration matchingRealmConfig = Utils.getMatchingRealmConfiguration(tenantId, userStoreDomain);
        if (matchingRealmConfig == null) {
            throw handleServerException(ERROR_CODE_ORGANIZATION_PATCH_ERROR,
                    "Couldn't find realm configurations for the user store domain : " + userStoreDomain);
        }
        String orgIdClaimUri = StringUtils.isNotBlank(IdentityUtil.getProperty(ORGANIZATION_ID_CLAIM_URI)) ?
                IdentityUtil.getProperty(ORGANIZATION_ID_CLAIM_URI).trim() :
                ORGANIZATION_ID_DEFAULT_CLAIM_URI;
        try {
            // Get user store manager for the domain
            UserStoreManager userStoreManager = OrganizationMgtDataHolder.getInstance().getRealmService()
                    .getUserRealm(matchingRealmConfig).getUserStoreManager();
            // Get tenant user realm
            org.wso2.carbon.user.api.UserRealm tenantUserRealm = OrganizationMgtDataHolder.getInstance()
                    .getRealmService().getTenantUserRealm(tenantId);
            org.wso2.carbon.user.api.ClaimManager claimManager = tenantUserRealm.getClaimManager();
            // Find the attribute name for 'accountDisabled' claim
            String orgIdAttribute = claimManager.getAttributeName(userStoreDomain, orgIdClaimUri);

            ExpressionCondition orgIdCondition = new ExpressionCondition("EQ", orgIdAttribute, organizationId);
            String[] users = ((AbstractUserStoreManager) userStoreManager)
                    .getUserList(orgIdCondition, userStoreDomain, null, 1, 0, null, null);
            return users.length > 0;

        } catch (org.wso2.carbon.user.api.UserStoreException e) {
            throw handleServerException(ERROR_CODE_ORGANIZATION_PATCH_ERROR, "Error while checking for active users",
                    e);
        }
    }

    public static OrganizationManager getOrganizationManager() {

        return (OrganizationManager) PrivilegedCarbonContext.getThreadLocalCarbonContext()
                .getOSGiService(OrganizationManager.class, null);
    }

    public static List<String> dissemblePermissionString(String permission) throws OrganizationManagementException {

        List<String> leafPermissions = new ArrayList<>();
        switch (permission) {
        case ADMIN_PERMISSION:
        case ADMIN_MANAGE_PERMISSION:
        case ADMIN_MANAGE_IDENTITY_PERMISSION:
            leafPermissions.add(ORGANIZATION_CREATE_PERMISSION);
            leafPermissions.add(ORGANIZATION_VIEW_PERMISSION);
            leafPermissions.add(ORGANIZATION_EDIT_PERMISSION);
            leafPermissions.add(ORGANIZATION_DELETE_PERMISSION);
            leafPermissions.add(USER_MGT_CREATE_PERMISSION);
            leafPermissions.add(USER_MGT_VIEW_PERMISSION);
            leafPermissions.add(USER_MGT_LIST_PERMISSION);
            leafPermissions.add(USER_MGT_EDIT_PERMISSION);
            leafPermissions.add(USER_MGT_DELETE_PERMISSION);
            leafPermissions.add(ROLE_MGT_CREATE_PERMISSION);
            leafPermissions.add(ROLE_MGT_VIEW_PERMISSION);
            leafPermissions.add(ROLE_MGT_EDIT_PERMISSION);
            leafPermissions.add(ROLE_MGT_DELETE_PERMISSION);
            break;
        case ORGANIZATION_BASE_PERMISSION:
            leafPermissions.add(ORGANIZATION_CREATE_PERMISSION);
            leafPermissions.add(ORGANIZATION_VIEW_PERMISSION);
            leafPermissions.add(ORGANIZATION_EDIT_PERMISSION);
            leafPermissions.add(ORGANIZATION_DELETE_PERMISSION);
            break;
        case USER_MGT_BASE_PERMISSION:
            leafPermissions.add(USER_MGT_CREATE_PERMISSION);
            leafPermissions.add(USER_MGT_VIEW_PERMISSION);
            leafPermissions.add(USER_MGT_LIST_PERMISSION);
            leafPermissions.add(USER_MGT_EDIT_PERMISSION);
            leafPermissions.add(USER_MGT_DELETE_PERMISSION);
            break;
        case ROLE_MGT_BASE_PERMISSION:
            leafPermissions.add(ROLE_MGT_CREATE_PERMISSION);
            leafPermissions.add(ROLE_MGT_VIEW_PERMISSION);
            leafPermissions.add(ROLE_MGT_EDIT_PERMISSION);
            leafPermissions.add(ROLE_MGT_DELETE_PERMISSION);
            break;
        default:
            throw handleServerException(ERROR_CODE_UNCLASSIFIED_ERROR,
                    "Unknown base permission to dissemble : " + permission);
        }
        return leafPermissions;
    }

    /**
     * Return the matching realm config for the given tenant and the user store domain.
     *
     * @param tenantID Tenant ID
     * @param userStoreDomain User store domain name
     * @return Matching realm config, null if not found.
     * @throws OrganizationManagementException If any errors occurred.
     */
    public static RealmConfiguration getMatchingRealmConfiguration(int tenantID, String userStoreDomain)
            throws OrganizationManagementException {

        List<RealmConfiguration> realmConfigurations = Utils.getRealmConfigurations(tenantID);
        RealmConfiguration matchingRealmConfig = null;
        for (RealmConfiguration realmConfig : realmConfigurations) {
            if (realmConfig.getUserStoreProperties().get(DOMAIN_NAME).equalsIgnoreCase(userStoreDomain)) {
                matchingRealmConfig = realmConfig;
                break;
            }
        }
        return matchingRealmConfig;
    }
}
