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

import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_SEARCH_BEAN_FIELD_ACTIVE;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_SEARCH_BEAN_FIELD_ATTRIBUTE_KEY;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_SEARCH_BEAN_FIELD_ATTRIBUTE_VALUE;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_SEARCH_BEAN_FIELD_DESCRIPTION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_SEARCH_BEAN_FIELD_NAME;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_SEARCH_BEAN_FIELD_PARENT_ID;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_SEARCH_BEAN_FIELD_USER_STORE_CONFIG_KEY;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_SEARCH_BEAN_FIELD_USER_STORE_CONFIG_VALUE;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_STATUS_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_ATTR_KEY_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_ATTR_VALUE_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_CONFIG_KEY_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_CONFIG_VALUE_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_DESCRIPTION_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_NAME_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_PARENT_ID_COLUMN;

public class OrganizationSearchBean implements SearchBean {

    private String name;
    private String description;
    private String parentId;
    private Boolean active;
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
            case ORGANIZATION_SEARCH_BEAN_FIELD_DESCRIPTION:
                dbQualifiedFieldName = VIEW_DESCRIPTION_COLUMN;
                break;
            case ORGANIZATION_SEARCH_BEAN_FIELD_PARENT_ID:
                dbQualifiedFieldName = VIEW_PARENT_ID_COLUMN;
                break;
            case ORGANIZATION_SEARCH_BEAN_FIELD_ACTIVE:
                dbQualifiedFieldName = VIEW_STATUS_COLUMN;
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

        //TODO try to convert timestamps and boolean queries here.
        return primitiveCondition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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
