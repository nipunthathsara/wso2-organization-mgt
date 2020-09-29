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

package org.wso2.carbon.identity.organization.mgt.core.model;

import org.wso2.carbon.identity.organization.mgt.core.exception.PrimitiveConditionValidationException;
import org.wso2.carbon.identity.organization.mgt.core.search.PrimitiveCondition;
import org.wso2.carbon.identity.organization.mgt.core.search.SearchBean;

import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_SEARCH_BEAN_FIELD_CREATED;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_SEARCH_BEAN_FIELD_CREATED_BY_ID;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_SEARCH_BEAN_FIELD_DISPLAY_NAME;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_SEARCH_BEAN_FIELD_LAST_MODIFIED;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_SEARCH_BEAN_FIELD_LAST_MODIFIED_BY_ID;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_SEARCH_BEAN_FIELD_PARENT_DISPLAY_NAME;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_SEARCH_BEAN_FIELD_PARENT_NAME;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_SEARCH_BEAN_FIELD_STATUS;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_SEARCH_BEAN_FIELD_ATTRIBUTE_KEY;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_SEARCH_BEAN_FIELD_ATTRIBUTE_VALUE;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_SEARCH_BEAN_FIELD_DESCRIPTION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_SEARCH_BEAN_FIELD_NAME;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_SEARCH_BEAN_FIELD_PARENT_ID;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_SEARCH_BEAN_FIELD_USER_STORE_CONFIG_KEY;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_SEARCH_BEAN_FIELD_USER_STORE_CONFIG_VALUE;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_CREATED_BY_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_CREATED_TIME_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_DISPLAY_NAME_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_LAST_MODIFIED_BY_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_LAST_MODIFIED_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_PARENT_DISPLAY_NAME_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_PARENT_NAME_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_STATUS_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_ATTR_KEY_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_ATTR_VALUE_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_CONFIG_KEY_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_CONFIG_VALUE_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_DESCRIPTION_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_NAME_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_PARENT_ID_COLUMN;

public class OrganizationSearchBean implements SearchBean {

    // Allowed search criteria
    private String name;
    private String displayName;
    private String description;
    private String status;
    private String parentId;
    private String parentName;
    private String parentDisplayName;
    //TODO fix time search : Caused by: java.sql.SQLDataException: ORA-01843: not a valid month
    private String created;
    private String lastModified;
    private String createdBy;
    private String lastModifiedBy;
    private String attributeKey;
    private String attributeValue;
    private String userStoreConfigKey;
    private String userStoreConfigValue;

    /**
     * Map field name to the DB table identifier.
     *
     * @param fieldName
     * @return
     */
    @Override
    public String getDBQualifiedFieldName(String fieldName) {

        String dbQualifiedFieldName = null;
        switch (fieldName) {
        case ORGANIZATION_SEARCH_BEAN_FIELD_NAME:
            dbQualifiedFieldName = VIEW_NAME_COLUMN;
            break;
        case ORGANIZATION_SEARCH_BEAN_FIELD_DISPLAY_NAME:
            dbQualifiedFieldName = VIEW_DISPLAY_NAME_COLUMN;
            break;
        case ORGANIZATION_SEARCH_BEAN_FIELD_DESCRIPTION:
            dbQualifiedFieldName = VIEW_DESCRIPTION_COLUMN;
            break;
        case ORGANIZATION_SEARCH_BEAN_FIELD_STATUS:
            dbQualifiedFieldName = VIEW_STATUS_COLUMN;
            break;
        case ORGANIZATION_SEARCH_BEAN_FIELD_PARENT_ID:
            dbQualifiedFieldName = VIEW_PARENT_ID_COLUMN;
            break;
        case ORGANIZATION_SEARCH_BEAN_FIELD_PARENT_NAME:
            dbQualifiedFieldName = VIEW_PARENT_NAME_COLUMN;
            break;
        case ORGANIZATION_SEARCH_BEAN_FIELD_PARENT_DISPLAY_NAME:
            dbQualifiedFieldName = VIEW_PARENT_DISPLAY_NAME_COLUMN;
            break;
        case ORGANIZATION_SEARCH_BEAN_FIELD_CREATED:
            dbQualifiedFieldName = VIEW_CREATED_TIME_COLUMN;
            break;
        case ORGANIZATION_SEARCH_BEAN_FIELD_LAST_MODIFIED:
            dbQualifiedFieldName = VIEW_LAST_MODIFIED_COLUMN;
            break;
        case ORGANIZATION_SEARCH_BEAN_FIELD_CREATED_BY_ID:
            dbQualifiedFieldName = VIEW_CREATED_BY_COLUMN;
            break;
        case ORGANIZATION_SEARCH_BEAN_FIELD_LAST_MODIFIED_BY_ID:
            dbQualifiedFieldName = VIEW_LAST_MODIFIED_BY_COLUMN;
            break;
        case ORGANIZATION_SEARCH_BEAN_FIELD_ATTRIBUTE_KEY:
            dbQualifiedFieldName = VIEW_ATTR_KEY_COLUMN;
            break;
        case ORGANIZATION_SEARCH_BEAN_FIELD_ATTRIBUTE_VALUE:
            dbQualifiedFieldName = VIEW_ATTR_VALUE_COLUMN;
            break;
        case ORGANIZATION_SEARCH_BEAN_FIELD_USER_STORE_CONFIG_KEY:
            dbQualifiedFieldName = VIEW_CONFIG_KEY_COLUMN;
            break;
        case ORGANIZATION_SEARCH_BEAN_FIELD_USER_STORE_CONFIG_VALUE:
            dbQualifiedFieldName = VIEW_CONFIG_VALUE_COLUMN;
            break;
        }
        return dbQualifiedFieldName;
    }

    /**
     * This method allow mapping of {@link PrimitiveCondition}.
     *
     * @param primitiveCondition Primitive search expression to be mapped.
     * @return
     * @throws PrimitiveConditionValidationException
     */
    @Override
    public PrimitiveCondition mapPrimitiveCondition(PrimitiveCondition primitiveCondition)
            throws PrimitiveConditionValidationException {

        //TODO Convert '2020-09-01 15:54:52.905' to '01-SEP-20 10.06.17.867000000 AM'
        if (ORGANIZATION_SEARCH_BEAN_FIELD_CREATED.equals(primitiveCondition.getProperty())
                || ORGANIZATION_SEARCH_BEAN_FIELD_LAST_MODIFIED.equals(primitiveCondition.getProperty())
                && primitiveCondition.getValue() != null) {
            primitiveCondition
                    .setValue(java.sql.Timestamp.valueOf(primitiveCondition.getValue() + "0000000").toString());
        }
        return primitiveCondition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentDisplayName() {
        return parentDisplayName;
    }

    public void setParentDisplayName(String parentDisplayName) {
        this.parentDisplayName = parentDisplayName;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public String getAttributeKey() {
        return attributeKey;
    }

    public void setAttributeKey(String attributeKey) {
        this.attributeKey = attributeKey;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    public String getUserStoreConfigKey() {
        return userStoreConfigKey;
    }

    public void setUserStoreConfigKey(String userStoreConfigKey) {
        this.userStoreConfigKey = userStoreConfigKey;
    }

    public String getUserStoreConfigValue() {
        return userStoreConfigValue;
    }

    public void setUserStoreConfigValue(String userStoreConfigValue) {
        this.userStoreConfigValue = userStoreConfigValue;
    }
}
