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

package org.wso2.carbon.identity.organization.mgt.core.handler;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.wso2.carbon.CarbonConstants;
import org.wso2.carbon.identity.core.bean.context.MessageContext;
import org.wso2.carbon.identity.event.IdentityEventException;
import org.wso2.carbon.identity.event.event.Event;
import org.wso2.carbon.identity.event.handler.AbstractEventHandler;
import org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.Status;
import org.wso2.carbon.identity.organization.mgt.core.model.Operation;
import org.wso2.carbon.identity.organization.mgt.core.model.Organization;
import org.wso2.carbon.identity.organization.mgt.core.model.OrganizationUserRoleMappingForEvent;
import org.wso2.carbon.identity.organization.mgt.core.model.UserRoleInheritance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.DATA;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.ORGANIZATION_ID;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.POST_ASSIGN_ORGANIZATION_USER_ROLE;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.POST_CREATE_ORGANIZATION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.POST_DELETE_ORGANIZATION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.POST_GET_CHILD_ORGANIZATIONS;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.POST_GET_ORGANIZATION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.POST_GET_USER_STORE_CONFIGS;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.POST_IMPORT_ORGANIZATION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.POST_LIST_ORGANIZATIONS;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.POST_PATCH_ORGANIZATION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.POST_PATCH_USER_STORE_CONFIGS;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.POST_REVOKE_ORGANIZATION_USER_ROLE;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.STATUS;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.USER_NAME;

/**
 * This class provides auditing capability to the Organization Management
 * using the Identity Eventing framework.
 */
public class OrganizationMgtAuditHandler extends AbstractEventHandler {

    private static final Log AUDIT = CarbonConstants.AUDIT_LOG;
    private static final String AUDIT_MESSAGE =
            "Initiator : %s | Action : %s | Target : %s | Data : { %s } | Result : %s ";

    @Override
    public String getName() {

        return "organizationMgtAuditHandler";
    }

    @Override
    public int getPriority(MessageContext messageContext) {

        return 51;
    }

    @Override
    public void handleEvent(Event event) throws IdentityEventException {

        // Common or most common data
        Map<String, Object> eventProperties = event.getEventProperties();
        String status = eventProperties.get(STATUS) instanceof Status ?
                ((Status) eventProperties.get(STATUS)).getStatus() : null;
        Organization organization = eventProperties.get(DATA) instanceof Organization ?
                (Organization) eventProperties.get(DATA) : new Organization();
        String username = eventProperties.get(USER_NAME) instanceof String ?
                eventProperties.get(USER_NAME).toString() : null;
        String organizationId = eventProperties.get(ORGANIZATION_ID) instanceof String ?
                eventProperties.get(ORGANIZATION_ID).toString() : null;
        Object data = eventProperties.get(DATA);

        switch (event.getEventName()) {
            case POST_CREATE_ORGANIZATION:
                AUDIT.info(String.format(AUDIT_MESSAGE, username, "create organization",
                        organization.getId(), organization.getName(), status));
                break;
            case POST_IMPORT_ORGANIZATION:
                AUDIT.info(String.format(AUDIT_MESSAGE, username, "import organization",
                        organization.getId(), organization.getName(), status));
                break;
            case POST_GET_ORGANIZATION:
                AUDIT.info(String.format(AUDIT_MESSAGE, username, "retrieve organization",
                        organization.getId(), organization.getName(), status));
                break;
            case POST_LIST_ORGANIZATIONS:
                List<Organization> organizations = (data instanceof List && !((List) data).isEmpty() && ((List) data)
                        .get(0) instanceof Organization) ? (List<Organization>) data : new ArrayList<>();
                AUDIT.info(String.format(AUDIT_MESSAGE, username, "list organizations",
                        organizations.stream().map(Organization::getId).collect(Collectors.toList()),
                        organizations.stream().map(Organization::getName).collect(Collectors.toList()),
                        status));
                break;
            case POST_DELETE_ORGANIZATION:
                AUDIT.info(String.format(AUDIT_MESSAGE, username, "delete organization",
                        organizationId, null, status));
                break;
            case POST_PATCH_ORGANIZATION:
                AUDIT.info(String.format(AUDIT_MESSAGE, username, "patch organization", organizationId,
                        formatOperationData(data), status));
                break;
            case POST_GET_CHILD_ORGANIZATIONS:
                List<String> children = (data instanceof List && !((List) data).isEmpty() && ((List) data)
                        .get(0) instanceof String) ? (List<String>) data : new ArrayList<>();
                AUDIT.info(String.format(AUDIT_MESSAGE, username, "list child organizations", organizationId,
                        StringUtils.join(children, ", "), status));
                break;
            case POST_GET_USER_STORE_CONFIGS:
                AUDIT.info(String.format(AUDIT_MESSAGE, username, "retrieve organization user store configs",
                        organizationId, null, status));
                break;
            case POST_PATCH_USER_STORE_CONFIGS:
                AUDIT.info(
                        String.format(AUDIT_MESSAGE, username, "patch organization user store config", organizationId,
                                formatOperationData(data), status));
                break;
            case POST_ASSIGN_ORGANIZATION_USER_ROLE:
                AUDIT.warn(String.format(AUDIT_MESSAGE, username, "assign organization user roles", organizationId,
                        formatRoleMappingAssignmentData(data), status));
                break;
            case POST_REVOKE_ORGANIZATION_USER_ROLE:
                AUDIT.warn(String.format(AUDIT_MESSAGE, username, "revoke organization user roles", organizationId,
                        formatRoleMappingRevokeData(data), status));
                break;
            default:
                return;
        }
    }

    private String formatOperationData(Object data) {

        Operation operation = data instanceof Operation ? (Operation) data : new Operation();
        StringBuilder builder = new StringBuilder();
        builder.append("Operation : " + operation.getOp());
        builder.append(", Path : " + operation.getPath());
        builder.append(", Value : " + operation.getValue());
        return builder.toString();
    }

    private String formatRoleMappingRevokeData(Object data) {

        OrganizationUserRoleMappingForEvent organizationUserRoleMappingForRevokeEvent =
                data instanceof OrganizationUserRoleMappingForEvent ? (OrganizationUserRoleMappingForEvent) data :
                        new OrganizationUserRoleMappingForEvent();
        StringBuilder builder = new StringBuilder();
        builder.append("OrganizationId : " + organizationUserRoleMappingForRevokeEvent.getOrganizationId());
        builder.append(", RoleId : " + organizationUserRoleMappingForRevokeEvent.getRoleId());
        builder.append(", UserId : " + organizationUserRoleMappingForRevokeEvent.getUserId());
        builder.append(", includeSubOrgs : " + organizationUserRoleMappingForRevokeEvent.isIncludeSubOrgs());
        return builder.toString();
    }

    private String formatRoleMappingAssignmentData(Object data) {

        OrganizationUserRoleMappingForEvent organizationUserRoleMappingForRevokeEvent =
                data instanceof OrganizationUserRoleMappingForEvent ? (OrganizationUserRoleMappingForEvent) data :
                        new OrganizationUserRoleMappingForEvent();
        StringBuilder builder = new StringBuilder();
        builder.append("OrganizationId : " + organizationUserRoleMappingForRevokeEvent.getOrganizationId());
        builder.append(", RoleId : " + organizationUserRoleMappingForRevokeEvent.getRoleId());
        for (UserRoleInheritance userRoleInheritance : organizationUserRoleMappingForRevokeEvent
                .getUsersRoleInheritance()) {
            builder.append(", { UserId : " + userRoleInheritance.getUserId());
            builder.append(", includeSubOrgs : " + userRoleInheritance.isCascadedRole());
            builder.append(" }");
        }
        return builder.toString();
    }
}
