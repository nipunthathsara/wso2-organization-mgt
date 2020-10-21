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

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.wso2.carbon.identity.organization.mgt.core.listener.OrganizationMgtEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * OSGI service component for {@link OrganizationMgtEventListener} interface.
 */
@Component(name = "org.wso2.carbon.identity.organization.mgt.listener",
           immediate = true)
public class OrganizationMgtListenerServiceComponent {

    private static List<OrganizationMgtEventListener> organizationMgtEventListeners = new ArrayList<>();

    @Reference(name = "org.wso2.carbon.organization.mgt.event.listener.service",
               service = OrganizationMgtEventListener.class,
               cardinality = ReferenceCardinality.MULTIPLE,
               policy = ReferencePolicy.DYNAMIC,
               unbind = "unsetOrganizationMgtListener")
    protected synchronized void setOrganizationMgtListener(OrganizationMgtEventListener organizationMgtEventListener) {

        organizationMgtEventListeners.add(organizationMgtEventListener);
        Collections.sort(organizationMgtEventListeners, orgMgtListenerComparator);
    }

    protected synchronized void unsetOrganizationMgtListener(
            OrganizationMgtEventListener organizationMgtEventListener) {

        organizationMgtEventListeners.remove(organizationMgtEventListener);
    }

    public static synchronized Collection getOrganizationMgtEventListeners() {

        return organizationMgtEventListeners;
    }

    private static Comparator<OrganizationMgtEventListener> orgMgtListenerComparator = (organizationMgtListener1,
            organizationMgtListener2) -> {
        if (organizationMgtListener1.getExecutionOrderId() > organizationMgtListener2.getExecutionOrderId()) {
            return 1;
        } else if (organizationMgtListener1.getExecutionOrderId() < organizationMgtListener2.getExecutionOrderId()) {
            return -1;
        } else {
            return 0;
        }
    };
}
