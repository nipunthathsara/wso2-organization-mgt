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

import org.wso2.carbon.identity.application.common.cache.CacheKey;

/**
 * Cache key for lookup organization's children from the cache.
 */
public class OrganizationCacheKey extends CacheKey {

    private static final long serialVersionUID = 8263255365985309443L;

    private String organizationKey;

    public OrganizationCacheKey(String organizationId) {

        this.organizationKey = organizationId;
    }

    public String getOrganizationKey() {

        return organizationKey;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        OrganizationCacheKey that = (OrganizationCacheKey) o;

        if (!organizationKey.equals(that.organizationKey)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {

        int result = super.hashCode();
        result = 31 * result + organizationKey.hashCode();
        return result;
    }

}
