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
import org.wso2.carbon.custom.userstore.manager.internal.CustomUserStoreDataHolder;
import org.wso2.carbon.database.utils.jdbc.JdbcTemplate;
import org.wso2.carbon.identity.core.persistence.UmPersistenceManager;
import org.wso2.carbon.identity.core.util.IdentityDatabaseUtil;
import org.wso2.carbon.identity.core.util.IdentityUtil;
import org.wso2.carbon.identity.organization.mgt.core.OrganizationManager;
import org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants;
import org.wso2.carbon.identity.organization.mgt.core.dao.OrganizationAuthorizationDao;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementClientException;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementException;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementServerException;
import org.wso2.carbon.identity.organization.mgt.core.internal.OrganizationMgtDataHolder;
import org.wso2.carbon.identity.organization.mgt.core.model.Organization;
import org.wso2.carbon.identity.organization.mgt.core.model.OrganizationAdd;
import org.wso2.carbon.identity.organization.mgt.core.model.OrganizationMgtRole;
import org.wso2.carbon.user.api.RealmConfiguration;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.UserStoreManager;
import org.wso2.carbon.user.core.common.AbstractUserStoreManager;
import org.wso2.carbon.user.core.ldap.UniqueIDReadOnlyLDAPUserStoreManager;
import org.wso2.carbon.user.core.ldap.UniqueIDReadWriteLDAPUserStoreManager;
import org.wso2.carbon.user.core.model.ExpressionCondition;
import org.wso2.carbon.user.core.model.OperationalCondition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.regex.Pattern;

import static org.wso2.carbon.custom.userstore.manager.Constants.ORGANIZATION_ID_CLAIM_URI;
import static org.wso2.carbon.custom.userstore.manager.Constants.ORGANIZATION_ID_DEFAULT_CLAIM_URI;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ACCOUNT_DISABLED_CLAIM_URI;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.OrganizationMgtRoles;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_INVALID_ORGANIZATION_USER_STORE_CONFIGURATIONS;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_ORGANIZATION_PATCH_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_ORG_MGT_SERVER_CONFIG_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_UNEXPECTED;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_USER_STORE_CONFIGURATIONS_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_USER_STORE_OPERATIONS_ERROR;
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
        sb.append("\nParent $ref : " + organization.getParent().get$ref());
        sb.append("\nStatus : " + organization.getStatus().toString());
        sb.append("\nCreated time : " + organization.getMetadata().getCreated());
        sb.append("\nLast modified time : " + organization.getMetadata().getLastModified());
        sb.append("\nCreated by id : " + organization.getMetadata().getCreatedBy().getId());
        sb.append("\nCreated by username : " + organization.getMetadata().getCreatedBy().getUsername());
        sb.append("\nCreated by $ref : " + organization.getMetadata().getCreatedBy().get$ref());
        sb.append("\nLast modified by id : " + organization.getMetadata().getLastModifiedBy().getId());
        sb.append("\nLast modified by username : " + organization.getMetadata().getLastModifiedBy().getUsername());
        sb.append("\nLast modified by $ref : " + organization.getMetadata().getLastModifiedBy().get$ref());
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

        return StringUtils.isBlank(MAX_QUERY_LENGTH_IN_BYTES_SQL) ?
                4194304 :
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
                    .getInstance().
                            getRealmService().getTenantUserRealm(tenantId).getUserStoreManager();
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

    public static boolean checkForActiveUsers(String organizationId, int tenantId)
            throws OrganizationManagementException {

        String userStoreDomain = getOrganizationManager().getUserStoreConfigs(organizationId).get(USER_STORE_DOMAIN)
                .getValue();
        // Find realmConfigurations for the user store domain
        List<RealmConfiguration> realmConfigurations = getRealmConfigurations(tenantId);
        RealmConfiguration matchingRealmConfig = null;
        for (RealmConfiguration realmConfig : realmConfigurations) {
            if (realmConfig.getUserStoreProperties().get(DOMAIN_NAME).equalsIgnoreCase(userStoreDomain)) {
                matchingRealmConfig = realmConfig;
                break;
            }
        }
        if (matchingRealmConfig == null) {
            throw handleServerException(ERROR_CODE_ORGANIZATION_PATCH_ERROR,
                    "Couldn't find realm configurations for the user store domain : " + userStoreDomain);
        }
        String orgIdClaimUri = !StringUtils.isBlank(IdentityUtil.getProperty(ORGANIZATION_ID_CLAIM_URI)) ?
                IdentityUtil.getProperty(ORGANIZATION_ID_CLAIM_URI).trim() :
                ORGANIZATION_ID_DEFAULT_CLAIM_URI;
        try {
            // Get user store manager for the domain
            UserStoreManager userStoreManager = OrganizationMgtDataHolder.getInstance().getRealmService()
                    .getUserRealm(matchingRealmConfig).getUserStoreManager();
            // Get tenant user realm
            org.wso2.carbon.user.api.UserRealm tenantUserRealm = CustomUserStoreDataHolder.getInstance()
                    .getRealmService().getTenantUserRealm(tenantId);
            org.wso2.carbon.user.api.ClaimManager claimManager = tenantUserRealm.getClaimManager();
            // Find attribute name for the 'accountDisabled' claim
            String accDisabledAttribute = claimManager.getAttributeName(userStoreDomain, ACCOUNT_DISABLED_CLAIM_URI);
            String orgIdAttribute = claimManager.getAttributeName(userStoreDomain, orgIdClaimUri);
            ExpressionCondition accDisabledCondition = new ExpressionCondition("EQ", accDisabledAttribute, "false");
            ExpressionCondition orgIdCondition = new ExpressionCondition("EQ", orgIdAttribute, organizationId);
            OperationalCondition opCondition = new OperationalCondition("and", orgIdCondition, accDisabledCondition);
            String[] userList = ((AbstractUserStoreManager) userStoreManager)
                    .getUserList(opCondition, userStoreDomain, null, 1, 0, null, null);
            return userList.length > 0;
        } catch (org.wso2.carbon.user.api.UserStoreException e) {
            throw handleServerException(ERROR_CODE_ORGANIZATION_PATCH_ERROR, "Error while checking for active users",
                    e);
        }
    }

    public static OrganizationManager getOrganizationManager() {

        return (OrganizationManager) PrivilegedCarbonContext.getThreadLocalCarbonContext()
                .getOSGiService(OrganizationManager.class, null);
    }

    public static Map<String, OrganizationMgtRole> populateManagementRoles(int tenantId)
            throws OrganizationManagementException {

        Map<String, OrganizationMgtRole> organizationMgtRoles = new HashMap<>();
        for (OrganizationMgtRoles mgtRole : OrganizationMgtRoles.values()) {
            String role = IdentityUtil.getProperty(mgtRole.getPropertyName());
            if (StringUtils.isBlank(role)) {
                throw handleServerException(ERROR_CODE_ORG_MGT_SERVER_CONFIG_ERROR,
                        "Organization Management roles can not be empty : " + mgtRole.getPropertyName());
            }
            role = role.trim();
            // If the domain is defined, it should be 'internal'
            if (role.contains("/") && !"Internal".equalsIgnoreCase(role.substring(0, role.indexOf("/")))) {
                throw handleServerException(ERROR_CODE_ORG_MGT_SERVER_CONFIG_ERROR,
                        "Management roles should be 'INTERNAL' roles : " + role);
            } else {
                // Remove the 'internal/' prefix from the role name
                role = role.replaceAll("(?i)" + Pattern.quote("Internal/"), "");
            }
            OrganizationAuthorizationDao authorizationDao = OrganizationMgtDataHolder.getInstance()
                    .getOrganizationAuthDao();
            // Find hybrid role id of the internal role
            int hybridRoleId = authorizationDao.findHybridRoleIdFromRoleName(role, tenantId);
            // Find SCIM group id
            role = "Internal/".concat(role);
            String groupId = authorizationDao.findGroupIdFromRoleName(role, tenantId);
            organizationMgtRoles.put(mgtRole.toString(), new OrganizationMgtRole(role, groupId, hybridRoleId));
        }
        return organizationMgtRoles;
    }
}
