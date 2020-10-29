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

package org.wso2.carbon.identity.organization.mgt.core.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.core.bean.context.MessageContext;
import org.wso2.carbon.identity.core.util.IdentityUtil;
import org.wso2.carbon.identity.event.IdentityEventException;
import org.wso2.carbon.identity.event.event.Event;
import org.wso2.carbon.identity.event.handler.AbstractEventHandler;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementException;
import org.wso2.carbon.identity.organization.mgt.core.model.Attribute;
import org.wso2.carbon.identity.organization.mgt.core.model.Operation;
import org.wso2.carbon.identity.organization.mgt.core.model.OrganizationAdd;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_EVENTING_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_INVALID_EVENT;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.PATCH_OP_REMOVE;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.PATCH_PATH_ORG_ATTRIBUTES;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.DATA;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.PRE_CREATE_ORGANIZATION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.PRE_IMPORT_ORGANIZATION;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtEventConstants.PRE_PATCH_ORGANIZATION;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.handleClientException;

/**
 * This handler validates the attribute values of an organization
 */
public class OrganizationMgtValidationHandler extends AbstractEventHandler {

    private static final Log log = LogFactory.getLog(OrganizationMgtValidationHandler.class);
    public static final String ATTRIBUTE_VALIDATOR_PROPERTIES_FILE_NAME = "org-mgt-attributes.properties";
    private static Properties properties = loadAttributeValidatorProperties();

    @Override
    public String getName() {

        return "organizationMgtValidationHandler";
    }

    @Override
    public int getPriority(MessageContext messageContext) {

        return 52;
    }

    @Override
    public void handleEvent(Event event) throws IdentityEventException {

        Object data = event.getEventProperties().get(DATA);
        List<Attribute> attributes;

        switch (event.getEventName()) {
        case PRE_CREATE_ORGANIZATION:
        case PRE_IMPORT_ORGANIZATION:
            attributes = data instanceof OrganizationAdd ? ((OrganizationAdd) data).getAttributes() :
                    new OrganizationAdd().getAttributes();
            break;
        case PRE_PATCH_ORGANIZATION:
            List<Operation> patchOperations = (data instanceof List && !((List) data).isEmpty() && ((List) data)
                    .get(0) instanceof Operation) ? (List<Operation>) data : new ArrayList<>();
            attributes = populateAttributesFromPatchOperations(patchOperations);
            break;
        default:
            return;
        }
        // Validate attributes
        for (Attribute attribute : attributes) {
            validateAttribute(attribute);
        }
    }

    private static Properties loadAttributeValidatorProperties() {

        InputStream inStream = null;
        Properties properties = new Properties();
        if (log.isDebugEnabled()) {
            log.debug("Loading attribute validator properties : " + ATTRIBUTE_VALIDATOR_PROPERTIES_FILE_NAME);
        }
        File attributeProperties = new File(IdentityUtil.getIdentityConfigDirPath(),
                ATTRIBUTE_VALIDATOR_PROPERTIES_FILE_NAME);
        if (attributeProperties.exists()) {
            try {
                inStream = new FileInputStream(attributeProperties);
                properties.load(inStream);
            } catch (IOException e) {
                log.error(ERROR_CODE_EVENTING_ERROR + "Error loading attribute validation properties : "
                        + ATTRIBUTE_VALIDATOR_PROPERTIES_FILE_NAME, e);
            } finally {
                if (inStream != null) {
                    try {
                        inStream.close();
                    } catch (IOException e) {
                        log.error(ERROR_CODE_EVENTING_ERROR + "Error while closing the stream ", e);
                    }
                }
            }
        }
        return properties;
    }

    private List<Attribute> populateAttributesFromPatchOperations(List<Operation> operations) {

        List<Attribute> attributes = new ArrayList<>();
        for (Operation operation : operations) {
            // Consider all attribute addition and replacement operations
            if (operation.getPath().startsWith(PATCH_PATH_ORG_ATTRIBUTES) && !PATCH_OP_REMOVE
                    .equals(operation.getOp())) {
                Attribute attribute = new Attribute();
                attribute.setKey(operation.getPath().replace(PATCH_PATH_ORG_ATTRIBUTES, "").trim());
                attribute.setValue(operation.getValue());
                attributes.add(attribute);
            }
        }
        return attributes;
    }

    public void validateAttribute(Attribute attribute) throws OrganizationManagementException {

        if (properties.getProperty(attribute.getKey()) != null) {
            String criteria = properties.getProperty(attribute.getKey());
            String[] allowedValues = criteria.split(",");
            boolean isValid = Arrays.stream(allowedValues).anyMatch(s -> s.trim().equals(attribute.getValue()));
            if (!isValid) {
                String errorMsg = "Invalid attribute. attribute key : " + attribute.getKey() + ", attribute value : "
                        + attribute.getValue() + ", validation criteria : " + criteria;
                throw handleClientException(ERROR_CODE_INVALID_EVENT, errorMsg);
            }
        }
    }
}
