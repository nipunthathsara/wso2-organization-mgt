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
import org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementClientException;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementServerException;
import org.wso2.carbon.identity.organization.mgt.core.model.Organization;
import org.wso2.carbon.identity.organization.mgt.core.model.OrganizationAdd;

import java.util.Arrays;
import java.util.UUID;

import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.MAX_QUERY_LENGTH_IN_BYTES_SQL;

/**
 * This class provides util functions for the Organization Management.
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

    public static String generateUniqueID() {

        return UUID.randomUUID().toString();
    }

    public static String getLdapRootDn() {

        //TODO implement logic.
        return null;
    }

    public static void logOrganizationAddObject(OrganizationAdd organizationAdd) {

        if (!log.isDebugEnabled()) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Logging OrganizationAdd object");
        sb.append("\nName : " + organizationAdd.getName());
        sb.append("\nParentId : " + organizationAdd.getParentId());
        sb.append("\nStatus : " + organizationAdd.getStatus());
        sb.append("\nRDN : " + organizationAdd.getRdn());
        if (organizationAdd.getAttributes() != null) {
            sb.append("\nAttributes : " + organizationAdd.getAttributes().toString());
        }
        log.debug(sb.toString());
    }

    public static void logOrganizationObject(Organization organization) {

        if (!log.isDebugEnabled()) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Logging Organization object");
        sb.append("\nID : " + organization.getId());
        sb.append("\nName : " + organization.getName());
        sb.append("\nTenantId : " + organization.getTenantId());
        sb.append("\nParentId : " + organization.getParentId());
        sb.append("\nStatus : " + organization.getStatus());
        sb.append("\nRDN : " + organization.getRdn());
        sb.append("\nDN : " + organization.getDn());
        sb.append("\nCreated Time : " + organization.getCreated());
        sb.append("\nLast Modified Time : " + organization.getLastModified());
        if (organization.getAttributes() != null) {
            sb.append("\nAttributes : " + organization.getAttributes().toString());
        }
        if (organization.getChildren() != null) {
            sb.append("\nChild Organizations : " + Arrays.toString(organization.getChildren().toArray()));
        }
        log.debug(sb.toString());
    }

    public static int getMaximumQueryLengthInBytes() {

        return StringUtils.isBlank(MAX_QUERY_LENGTH_IN_BYTES_SQL) ? 4194304 : Integer.parseInt(MAX_QUERY_LENGTH_IN_BYTES_SQL);
    }
}
