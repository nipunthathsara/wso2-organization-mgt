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

package org.wso2.carbon.identity.organization.mgt.core.search;

import org.wso2.carbon.identity.organization.mgt.core.constant.ConditionType;
import org.wso2.carbon.identity.organization.mgt.core.exception.PrimitiveConditionValidationException;

import java.util.ArrayList;

import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.GET_ALL_ORGANIZATION_IDS;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VALUE_LOWER_WRAPPER;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_ATTR_KEY_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_ATTR_VALUE_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_CREATED_TIME_COLUMN;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_LAST_MODIFIED_COLUMN;

/**
 * Represents a primitive search expression. Ex: 'a > 5' where property is 'a', operator is '>' and value is '5'.
 */
public class PrimitiveCondition implements Condition {

    private String property;
    private Object value;
    private ConditionType.PrimitiveOperator operator;

    public PrimitiveCondition(String property, ConditionType.PrimitiveOperator operator, Object value) {

        this.property = property;
        this.value = value;
        this.operator = operator;
    }

    public ConditionType.PrimitiveOperator getOperator() {

        return operator;
    }

    public void setOperator(ConditionType.PrimitiveOperator operator) {

        this.operator = operator;
    }

    public String getProperty() {

        return property;
    }

    public void setProperty(String property) {

        this.property = property;
    }

    public Object getValue() {

        return value;
    }

    public void setValue(Object value) {

        this.value = value;
    }

    public PlaceholderSQL buildQuery(PrimitiveConditionValidator primitiveConditionValidator, boolean isAttrSearch)
            throws PrimitiveConditionValidationException {

        PlaceholderSQL placeholderSQL = new PlaceholderSQL();
        PrimitiveCondition dbQualifiedPrimitiveCondition = primitiveConditionValidator.validate(this);
        // No need to perform a case insensitive search for 'CREATED' or 'LAST_MODIFIED' columns.
        String valuePlaceHolder = (VIEW_CREATED_TIME_COLUMN.equals(dbQualifiedPrimitiveCondition.getProperty())
                || VIEW_LAST_MODIFIED_COLUMN.equals(dbQualifiedPrimitiveCondition.getProperty())) ?
                " ?" :
                VALUE_LOWER_WRAPPER;
        String base = GET_ALL_ORGANIZATION_IDS;
        if (isAttrSearch && (dbQualifiedPrimitiveCondition.getProperty().contains(VIEW_ATTR_KEY_COLUMN)
                || dbQualifiedPrimitiveCondition.getProperty().contains(VIEW_ATTR_VALUE_COLUMN))) {
            base = "";
        }
        placeholderSQL.setQuery(
                base +
                dbQualifiedPrimitiveCondition.getProperty() +
                " " +
                dbQualifiedPrimitiveCondition.getOperator().toSQL() +
                valuePlaceHolder
        );
        ArrayList<Object> data = new ArrayList<>();
        data.add(dbQualifiedPrimitiveCondition.getValue());
        placeholderSQL.setData(data);
        ArrayList<ConditionType.PrimitiveOperator> operators = new ArrayList<>();
        operators.add(dbQualifiedPrimitiveCondition.getOperator());
        placeholderSQL.setOperator(operators);
        return placeholderSQL;
    }
}
