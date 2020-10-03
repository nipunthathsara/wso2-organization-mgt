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

package org.wso2.carbon.identity.organization.mgt.core.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.core.util.IdentityUtil;
import org.wso2.carbon.identity.organization.mgt.core.exception.OrganizationManagementException;
import org.wso2.carbon.identity.organization.mgt.core.model.Attribute;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ATTRIBUTE_VALIDATOR_PROPERTIES_FILE_NAME;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_ATTRIBUTE_VALIDATION_ERROR;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ErrorMessages.ERROR_CODE_INVALID_ATTRIBUTES;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.handleClientException;
import static org.wso2.carbon.identity.organization.mgt.core.util.Utils.handleServerException;

/**
 * Organization attribute validator.
 */
public class AttributeValidatorImpl implements AttributeValidator {

    private static final Log log = LogFactory.getLog(AttributeValidatorImpl.class);
    private static Properties properties = loadAttributeValidatorProperties();

    @Override
    public void validateAttribute(Attribute attribute) throws OrganizationManagementException {

        if (attribute == null) {
            throw handleServerException(ERROR_CODE_ATTRIBUTE_VALIDATION_ERROR, "Can not validate null attribute.");
        }
        if (properties.getProperty(attribute.getKey()) != null) {
            String criteria = properties.getProperty(attribute.getKey());
            String[] allowedValues = criteria.split(",");
            boolean isValid = Arrays.stream(allowedValues).anyMatch(s -> s.trim().equals(attribute.getValue()));
            if (!isValid) {
                String errorMsg =
                        "attribute key : " + attribute.getKey() + ", attribute value : " + attribute.getValue()
                                + ", validation criteria : " + criteria;
                throw handleClientException(ERROR_CODE_INVALID_ATTRIBUTES, errorMsg);
            }
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
                log.error(ERROR_CODE_ATTRIBUTE_VALIDATION_ERROR + "Error loading attribute validation properties : "
                        + ATTRIBUTE_VALIDATOR_PROPERTIES_FILE_NAME, e);
            } finally {
                if (inStream != null) {
                    try {
                        inStream.close();
                    } catch (IOException e) {
                        log.error(ERROR_CODE_ATTRIBUTE_VALIDATION_ERROR + "Error while closing the stream ", e);
                    }
                }
            }
        }
        return properties;
    }
}
