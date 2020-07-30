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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants;
import org.wso2.carbon.identity.organization.mgt.core.dao.OrganizationMgtDao;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementException;
import org.wso2.carbon.identity.organization.mgt.core.internal.OrganizationMgtDataHolder;
import org.wso2.carbon.identity.organization.mgt.core.model.Attribute;
import org.wso2.carbon.identity.organization.mgt.core.model.Organization;
import org.wso2.carbon.identity.organization.mgt.core.model.OrganizationAdd;

import java.util.ArrayList;

import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.generateUniqueID;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.handleClientException;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.logOrganizationAddObject;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.logOrganizationObject;

/**
 * This class implements the {@link OrganizationManager} interface.
 */
public class OrganizationManagerImpl implements OrganizationManager {

    private static final Log log = LogFactory.getLog(OrganizationManagerImpl.class);
    private OrganizationMgtDao organizationMgtDao = OrganizationMgtDataHolder.getInstance().getOrganizationMgtDao();

    @Override
    public Organization addOrganization(OrganizationAdd organizationAdd)
            throws OrganizationManagementException {

        int tenantId = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId();
        logOrganizationAddObject(organizationAdd);
        validateAddOrganizationRequest(tenantId, organizationAdd);
        Organization organization = generateOrganizationFromRequest(organizationAdd);
        organization.setId(generateUniqueID());
        organization.setTenantId(tenantId);
        organization.setChildren(new ArrayList());
        logOrganizationObject(organization);
        organizationMgtDao.addOrganization(tenantId, organization);
        return organization;
    }

    @Override
    public Organization getOrganization(String organizationId) throws OrganizationManagementException {
        return null;
    }

    @Override
    public Organization updateOrganization(String organizationId, OrganizationAdd organizationAdd)
            throws OrganizationManagementException {

        return null;
    }

    @Override
    public void deleteOrganization(String organizationId) throws OrganizationManagementException {

    }

    @Override
    public boolean isOrganizationExistByName(int tenantId, String organizationName) throws OrganizationManagementException {

        return organizationMgtDao.isOrganizationExistByName(tenantId, organizationName);
    }

    @Override
    public boolean isOrganizationExistById(int tenantId, String id) throws OrganizationManagementException {

        return organizationMgtDao.isOrganizationExistById(tenantId, id);
    }

    private void validateAddOrganizationRequest(int tenantId, OrganizationAdd organizationAdd)
            throws OrganizationManagementException {

        // Check required fields.
        if (StringUtils.isEmpty(organizationAdd.getName()) || StringUtils.isEmpty(organizationAdd.getRdn()) ||
                StringUtils.isEmpty(organizationAdd.getRdn())) {
            throw handleClientException(OrganizationMgtConstants.ErrorMessages.ERROR_CODE_ORGANIZATION_ADD_REQUEST_INVALID,
                    "Required fields are empty");
        }
        // Attribute keys can't be empty
        for (Attribute attribute : organizationAdd.getAttributes()) {
            if (StringUtils.isEmpty(attribute.getKey())) {
                throw handleClientException(OrganizationMgtConstants.ErrorMessages.ERROR_CODE_ORGANIZATION_ADD_REQUEST_INVALID,
                        "Attribute keys cannot be empty.");
            }
        }
        // Check if the organization name already exists for the given tenant
        if (isOrganizationExistByName(tenantId, organizationAdd.getName())) {
            throw handleClientException(OrganizationMgtConstants.ErrorMessages.ERROR_CODE_ORGANIZATION_ALREADY_EXISTS_ERROR,
                    "Organization name " + organizationAdd.getName() + " already exists in this tenant.");
        }
        // Check if parent org exists
        if (StringUtils.isNotEmpty(organizationAdd.getParentId()) &&
                !organizationMgtDao.isOrganizationExistById(tenantId, organizationAdd.getParentId())) {
            throw handleClientException(OrganizationMgtConstants.ErrorMessages.ERROR_CODE_ORGANIZATION_ADD_REQUEST_INVALID,
                    "Defined parent organization doesn't exist " + organizationAdd.getParentId());
        }
    }

    private Organization generateOrganizationFromRequest(OrganizationAdd organizationAdd) {

        Organization organization = new Organization();
        organization.setName(organizationAdd.getName());
        organization.setParentId(organizationAdd.getParentId());
        organization.setStatus(organizationAdd.getStatus());
        organization.setAttributes(organizationAdd.getAttributes());
        // TODO make sure no null could happen in the endpoint layer
        organization.setHasAttribute(!organizationAdd.getAttributes().isEmpty());
        organization.setRdn(organizationAdd.getRdn());
        return organization;
    }
}
