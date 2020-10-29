/*
 *   Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.wso2.carbon.identity.organization.scope.handler;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.core.util.IdentityTenantUtil;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.authz.OAuthAuthzReqMessageContext;
import org.wso2.carbon.identity.oauth2.token.OAuthTokenReqMessageContext;
import org.wso2.carbon.identity.oauth2.validators.OAuth2TokenValidationMessageContext;
import org.wso2.carbon.identity.oauth2.validators.scope.ScopeValidator;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementException;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementServerException;
import org.wso2.carbon.identity.organization.mgt.core.util.Utils;
import org.wso2.carbon.identity.organization.scope.handler.internal.OrganizationScopeHandlerDataHolder;
import org.wso2.carbon.utils.CarbonUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Custom scope handler to include all available scopes of the user, if the organization system scope is requested.
 */
public class OrganizationScopeHandler implements ScopeValidator {

    private static final Log log = LogFactory.getLog(OrganizationScopeHandler.class);
    private Map<String, String> scopeBindings;
    private static final String ORG_SCOPE_BINDING_PATH = "identity/org-mgt-scope-bindings.xml";
    private static final String ORG_SYSTEM_SCOPE = "ORGANIZATION_SYSTEM";

    @Override
    public boolean validateScope(OAuthAuthzReqMessageContext authzReqMessageContext) throws IdentityOAuth2Exception {

        return true;
    }

    @Override
    public boolean validateScope(OAuthTokenReqMessageContext tokenReqMessageContext) throws IdentityOAuth2Exception {

        String[] scopes = tokenReqMessageContext.getScope();
        if (Arrays.asList(scopes).contains(ORG_SYSTEM_SCOPE)) {
            // Populate scope bindings if not already done.
            if (scopeBindings == null) {
                scopeBindings = readScopeBindingsFromFile();
            }

            List<String> orgSystemScopes = new ArrayList<>();
            // Find org permissions of the user.
            int tenantId = IdentityTenantUtil.getTenantId(tokenReqMessageContext.getAuthorizedUser().getTenantDomain());
            try {
                String userId = Utils.getUserIDFromUserName(tokenReqMessageContext.getAuthorizedUser().getUserName(),
                        tenantId);
                List<String> permissions = OrganizationScopeHandlerDataHolder.getInstance().
                        getOrganizationAuthDao().findUserPermissions(Utils.getNewTemplate(), userId);
                for (String permission : permissions) {
                    if (scopeBindings.containsKey(permission) &&
                            !orgSystemScopes.contains(scopeBindings.get(permission))) {
                        orgSystemScopes.add(scopeBindings.get(permission));
                    } else {
                        // Check for higher level permission entries.
                        for (Map.Entry<String, String> entry : scopeBindings.entrySet()) {
                            if (entry.getKey().startsWith(permission) && !orgSystemScopes.contains(entry.getValue())) {
                                orgSystemScopes.add(entry.getValue());
                            }
                        }
                    }
                }
                String[] scopesToReturn = (String[]) ArrayUtils.addAll(scopes, orgSystemScopes.toArray());
                tokenReqMessageContext.setScope(scopesToReturn);
            } catch (OrganizationManagementServerException e) {
                throw new IdentityOAuth2Exception("Error while obtaining user ID for the user: " +
                        tokenReqMessageContext.getAuthorizedUser().getUserName());
            } catch (OrganizationManagementException e) {
                throw new IdentityOAuth2Exception("Error while obtaining user permissions for the user: " +
                        tokenReqMessageContext.getAuthorizedUser().getUserName());
            }
        }

        return true;
    }

    @Override
    public boolean validateScope(OAuth2TokenValidationMessageContext messageContext) throws IdentityOAuth2Exception {

        return true;
    }

    @Override
    public String getName() {

        return null;
    }

    /**
     * Read Org Mgt scope bindings from file and return as a hash map.
     *
     * @return Scope to permission mapping.
     * @throws IdentityOAuth2Exception If any errors occurred.
     */
    private Map<String, String> readScopeBindingsFromFile() throws IdentityOAuth2Exception {

        String configDirPath = CarbonUtils.getCarbonConfigDirPath();
        String confXml = Paths.get(configDirPath, ORG_SCOPE_BINDING_PATH).toString();
        File configFile = new File(confXml);
        if (!configFile.exists()) {
            log.warn("Organization management scope bindings file is not present at: " + confXml);
            return new HashMap<>();
        }

        Map<String, String> scopeBindings = new HashMap<>();
        XMLStreamReader parser;

        try (InputStream stream = new FileInputStream(configFile)) {

            parser = XMLInputFactory.newInstance().createXMLStreamReader(stream);
            StAXOMBuilder builder = new StAXOMBuilder(parser);
            OMElement documentElement = builder.getDocumentElement();
            Iterator bindingIterator = documentElement.getChildElements();
            while (bindingIterator.hasNext()) {

                OMElement bindingElement = (OMElement) bindingIterator.next();
                Iterator childIterator = bindingElement.getChildElements();
                OMElement permissionElement = (OMElement) childIterator.next();
                String permission = permissionElement.getText();
                OMElement scopeElement = (OMElement) childIterator.next();
                String scope = scopeElement.getText();

                scopeBindings.put(permission, scope);
            }
        } catch (XMLStreamException e) {
            throw new IdentityOAuth2Exception("Parsing error occurred while reading the organization management " +
                    "scope bindings file.");
        } catch (IOException e) {
            throw new IdentityOAuth2Exception("Couldn't find the organization management scope bindings " +
                    "file in path: " + confXml);
        }
        return scopeBindings;
    }
}
