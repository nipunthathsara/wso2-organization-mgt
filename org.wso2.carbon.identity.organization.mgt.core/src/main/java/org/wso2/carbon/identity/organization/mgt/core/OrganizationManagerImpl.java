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

import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_INVALID_ORGANIZATION_ID_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.generateUniqueID;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.getLdapRootDn;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.handleClientException;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.logOrganizationAddObject;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.logOrganizationObject;

/**
 * This class implements the {@link OrganizationManager} interface.
 */
public class OrganizationManagerImpl implements OrganizationManager {

    private static final Log log = LogFactory.getLog(OrganizationManagerImpl.class);
    private OrganizationMgtDao organizationMgtDao = OrganizationMgtDataHolder.getInstance().getOrganizationMgtDao();
    private int tenantId = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId();

    @Override
    public Organization addOrganization(OrganizationAdd organizationAdd)
            throws OrganizationManagementException {

        logOrganizationAddObject(organizationAdd);
        validateAddOrganizationRequest(organizationAdd);
        Organization organization = generateOrganizationFromRequest(organizationAdd);
        organization.setId(generateUniqueID());
        organization.setTenantId(tenantId);
        // No children as this is a leaf organization at the moment
        logOrganizationObject(organization);
        organizationMgtDao.addOrganization(tenantId, organization);
        return organization;
    }

    @Override
    public Organization getOrganization(String organizationId) throws OrganizationManagementException {

        if (StringUtils.isBlank(organizationId)) {
            handleClientException(ERROR_CODE_INVALID_ORGANIZATION_ID_ERROR, "Provided organization ID is empty");
        }
        return organizationMgtDao.getOrganization(tenantId, organizationId.trim());
    }

    @Override
    public Organization updateOrganization(String organizationId, OrganizationAdd organizationAdd)
            throws OrganizationManagementException {

        return null;
    }

    @Override
    public void deleteOrganization(String organizationId) throws OrganizationManagementException {

        if (StringUtils.isBlank(organizationId)) {
            handleClientException(ERROR_CODE_INVALID_ORGANIZATION_ID_ERROR, "Provided organization ID is empty");
        }
        organizationMgtDao.deleteOrganization(tenantId, organizationId.trim());
    }

    @Override
    public boolean isOrganizationExistByName(String organizationName) throws OrganizationManagementException {

        return organizationMgtDao.isOrganizationExistByName(tenantId, organizationName);
    }

    @Override
    public boolean isOrganizationExistById(String id) throws OrganizationManagementException {

        return organizationMgtDao.isOrganizationExistById(tenantId, id);
    }

    private void validateAddOrganizationRequest(OrganizationAdd organizationAdd)
            throws OrganizationManagementException {

        // Check required fields.
        if (StringUtils.isBlank(organizationAdd.getName()) || StringUtils.isBlank(organizationAdd.getRdn()) ||
                StringUtils.isBlank(organizationAdd.getRdn())) {
            throw handleClientException(OrganizationMgtConstants.ErrorMessages.ERROR_CODE_ORGANIZATION_ADD_REQUEST_INVALID,
                    "Required fields are empty");
        }
        // Attribute keys can't be empty
        for (Attribute attribute : organizationAdd.getAttributes()) {
            if (StringUtils.isBlank(attribute.getKey())) {
                throw handleClientException(OrganizationMgtConstants.ErrorMessages.ERROR_CODE_ORGANIZATION_ADD_REQUEST_INVALID,
                        "Attribute keys cannot be empty.");
            }
            // Sanitize input
            attribute.setKey(attribute.getKey().trim());
        }
        // Check if the organization name already exists for the given tenant
        if (isOrganizationExistByName(organizationAdd.getName().trim())) {
            throw handleClientException(OrganizationMgtConstants.ErrorMessages.ERROR_CODE_ORGANIZATION_ALREADY_EXISTS_ERROR,
                    "Organization name " + organizationAdd.getName().trim() + " already exists in this tenant.");
        }
        // Check if parent org exists
        if (StringUtils.isNotBlank(organizationAdd.getParentId()) &&
                !isOrganizationExistById(organizationAdd.getParentId().trim())) {
            throw handleClientException(OrganizationMgtConstants.ErrorMessages.ERROR_CODE_ORGANIZATION_ADD_REQUEST_INVALID,
                    "Defined parent organization doesn't exist " + organizationAdd.getParentId().trim());
        }
        // Sanitize values
        organizationAdd.setName(organizationAdd.getName().trim());
        organizationAdd.setRdn(organizationAdd.getRdn().trim());
        organizationAdd.setParentId(
                StringUtils.isNotBlank(organizationAdd.getParentId()) ? organizationAdd.getParentId().trim() : null);
    }

    //TODO should this be public? No as this is not tenant aware.
    private String getDnByOrganizationId(String organizationId) throws OrganizationManagementException {

        return organizationMgtDao.getDnByOrganizationId(organizationId);
    }

    private String constructDn(String parentId, String rdn) throws OrganizationManagementException {

        if (parentId != null) {
            String parentDn = getDnByOrganizationId(parentId);
            return parentDn.concat("," + rdn);
        } else {
            return getLdapRootDn().concat("," + rdn);
        }
    }

    private Organization generateOrganizationFromRequest(OrganizationAdd organizationAdd)
            throws OrganizationManagementException {

        Organization organization = new Organization();
        organization.setName(organizationAdd.getName());
        organization.setParentId(organizationAdd.getParentId());
        organization.setStatus(organizationAdd.getStatus());
        if (organizationAdd.getAttributes() != null) {
            organization.setAttributes(organizationAdd.getAttributes());
        }
        organization.setHasAttribute(!organizationAdd.getAttributes().isEmpty());
        organization.setRdn(organizationAdd.getRdn());
        organization.setDn(constructDn(organization.getParentId(), organization.getRdn()));
        return organization;
    }
}
