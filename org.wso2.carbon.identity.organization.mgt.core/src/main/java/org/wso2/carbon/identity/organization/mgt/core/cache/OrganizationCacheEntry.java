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

package org.wso2.carbon.identity.organization.mgt.core.cache;

import org.wso2.carbon.identity.application.common.cache.CacheEntry;

import java.util.List;

/**
 * Cache entry which is kept in the children organizations.
 */
public class OrganizationCacheEntry extends CacheEntry {

    private static final long serialVersionUID = 3112605038259278777L;

    private List<String> childrenOrganizations;

    public OrganizationCacheEntry(List<String> childrenOrganizations) {

        this.childrenOrganizations = childrenOrganizations;
    }

    public List<String> getChildrenOrganizations() {

        return childrenOrganizations;
    }

    public void setChildrenOrganizations(List<String> childrenOrganizations) {

        this.childrenOrganizations = childrenOrganizations;
    }
}
