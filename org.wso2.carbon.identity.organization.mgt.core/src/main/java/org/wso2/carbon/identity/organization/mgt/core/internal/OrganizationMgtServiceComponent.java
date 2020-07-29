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

package org.wso2.carbon.identity.organization.mgt.core.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.wso2.carbon.identity.organization.mgt.core.OrganizationManager;
import org.wso2.carbon.identity.organization.mgt.core.OrganizationManagerImpl;
import org.wso2.carbon.identity.organization.mgt.core.dao.OrganizationMgtDao;
import org.wso2.carbon.identity.organization.mgt.core.dao.OrganizationMgtDaoImpl;

/**
 * OSGI service component for organization management core bundle.
 */
@Component(
        name = "carbon.organization.mgt.component",
        immediate = true
)
public class OrganizationMgtServiceComponent {

    private static final Log log = LogFactory.getLog(OrganizationMgtServiceComponent.class);

    /**
     * Register Organization Manager service in the OSGI context.
     * @param componentContext
     */
    @Activate
    protected void activate(ComponentContext componentContext) {

        try {
            BundleContext bundleContext = componentContext.getBundleContext();
            //TODO Should I register DAO as a service?
            bundleContext.registerService(OrganizationMgtDao.class.getName(), new OrganizationMgtDaoImpl(),null);
            bundleContext.registerService(OrganizationManager.class.getName(),
                    new OrganizationManagerImpl(), null);
            OrganizationMgtDataHolder.getInstance().setOrganizationMgtDao(new OrganizationMgtDaoImpl());
            if (log.isDebugEnabled()) {
                log.debug("Organization Management component activated successfully.");
            }
            //TODO erase
            log.info("Organization Management component activated successfully.*********************************");
        } catch (Throwable e) {
            log.error("Error while activating Organization Management module.", e);
        }
    }
}
