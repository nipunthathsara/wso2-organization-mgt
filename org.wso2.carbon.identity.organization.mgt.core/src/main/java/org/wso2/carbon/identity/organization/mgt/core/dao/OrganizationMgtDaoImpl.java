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

public class OrganizationMgtDaoImpl implements OrganizationMgtDao {

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void addOrganization(int tenantId, Organization organization) throws OrganizationManagementException {

    }

    @Override
    public void deleteOrganization(int tenantId, String organizationId) throws OrganizationManagementException {

    }

    @Override
    public boolean isOrganizationExist(int tenantId, String name) throws OrganizationManagementException {


        return false;
    }

    @Override
    public Organization getOrganization(int tenantId, String organizationId) throws OrganizationManagementException {
        return null;
    }
}
