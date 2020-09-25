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

package org.wso2.carbon.identity.organization.user.role.mgt.core.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.database.utils.jdbc.JdbcTemplate;
import org.wso2.carbon.identity.core.persistence.UmPersistenceManager;
import org.wso2.carbon.identity.organization.user.role.mgt.core.constant.OrganizationUserRoleMgtConstants;
import org.wso2.carbon.identity.organization.user.role.mgt.core.exception.OrganizationUserRoleMgtClientException;
import org.wso2.carbon.identity.organization.user.role.mgt.core.exception.OrganizationUserRoleMgtServerException;

import java.util.UUID;

public class Utils {

    private static final Log log = LogFactory.getLog(org.wso2.carbon.identity.organization.mgt.core.util.Utils.class);

    public static OrganizationUserRoleMgtClientException handleClientException(
            OrganizationUserRoleMgtConstants.ErrorMessages error, String data) {

        String message;
        if (StringUtils.isNotBlank(data)) {
            message = String.format(error.getMessage(), data);
        } else {
            message = error.getMessage();
        }
        return new OrganizationUserRoleMgtClientException(message, error.getCode());
    }

    public static OrganizationUserRoleMgtServerException handleServerException(
            OrganizationUserRoleMgtConstants.ErrorMessages error, String data, Throwable e) {

        String message;
        if (StringUtils.isNotBlank(data)) {
            message = String.format(error.getMessage(), data);
        } else {
            message = error.getMessage();
        }
        return new OrganizationUserRoleMgtServerException(message, error.getCode(), e);
    }


    public static OrganizationUserRoleMgtServerException handleServerException(
            OrganizationUserRoleMgtConstants.ErrorMessages error, String data) {

        String message;
        if (StringUtils.isNotBlank(data)) {
            message = String.format(error.getMessage(), data);
        } else {
            message = error.getMessage();
        }
        return new OrganizationUserRoleMgtServerException(message, error.getCode());
    }

    public static JdbcTemplate getNewTemplate() {
        return new JdbcTemplate(UmPersistenceManager.getInstance().getDataSource());
    }

    public static String generateUniqueID() {

        return UUID.randomUUID().toString();
    }
}
