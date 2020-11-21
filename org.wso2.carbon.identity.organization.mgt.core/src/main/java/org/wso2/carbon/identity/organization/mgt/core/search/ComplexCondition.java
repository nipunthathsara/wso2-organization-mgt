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
import java.util.List;

import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_SEARCH_BEAN_FIELD_ATTRIBUTE_KEY;
import static org.wso2.carbon.identity.organization.mgt.core.constant.OrganizationMgtConstants.ORGANIZATION_SEARCH_BEAN_FIELD_ATTRIBUTE_VALUE;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.GET_ALL_ORGANIZATION_IDS;

/**
 * This class represent a complex condition with a {@link ConditionType}. A complex condition can contain a list of
 * another complex conditions or a primitive condition. Ex: A sample complex condition with two complex conditions
 * as a list can represent the form, {@link ComplexCondition}[1] {@link ConditionType}[2] {@link ComplexCondition}[2].
 */
public class ComplexCondition implements Condition {

    private List<Condition> conditions;
    private ConditionType.ComplexOperator operator;

    public ComplexCondition(ConditionType.ComplexOperator operator, List<Condition> conditions) {

        this.operator = operator;
        this.conditions = conditions;
    }

    public List<Condition> getConditions() {

        return conditions;
    }

    public PlaceholderSQL buildQuery(PrimitiveConditionValidator primitiveConditionValidator)
            throws PrimitiveConditionValidationException {

        PlaceholderSQL placeholderSQL = new PlaceholderSQL();
        ArrayList<Object> data = new ArrayList<>();
        ArrayList<ConditionType.PrimitiveOperator> operators = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        // Identify complex conditions with attribute search (attributeKey eq 'Type' and attributeValue eq 'partner')
        boolean isAttrSearch = false;
        for (Condition condition : conditions) {
            isAttrSearch = false;
            if (condition instanceof PrimitiveCondition) {
                isAttrSearch = true;
                PrimitiveCondition pCondition = (PrimitiveCondition) condition;
                if (!ConditionType.ComplexOperator.AND.equals(operator) &&
                        !(ORGANIZATION_SEARCH_BEAN_FIELD_ATTRIBUTE_KEY.equals(pCondition.getProperty())
                                || ORGANIZATION_SEARCH_BEAN_FIELD_ATTRIBUTE_VALUE.equals(pCondition.getProperty()))) {
                    isAttrSearch = false;
                    break;
                }
            } else {
                break;
            }
        }

        boolean first = true;
        for (Condition condition : conditions) {

            if (isAttrSearch) {
                PlaceholderSQL eachPlaceholderSQL = condition.buildQuery(primitiveConditionValidator);
                if (first) {
                    sb.append(GET_ALL_ORGANIZATION_IDS);
                    sb.append(eachPlaceholderSQL.getQuery());
                    first = false;
                } else {
                    sb.append(" AND ");
                    sb.append(eachPlaceholderSQL.getQuery());
                }
                data.addAll(eachPlaceholderSQL.getData());
                operators.addAll(eachPlaceholderSQL.getOperators());
                continue;
            }

            // For non-attribute search scenarios
            if (!first) {
                sb.append("\n").append(operator.toSQL()).append("\n");
            } else {
                first = false;
            }
            PlaceholderSQL eachPlaceholderSQL = condition.buildQuery(primitiveConditionValidator);
            sb.append(eachPlaceholderSQL.getQuery());
            data.addAll(eachPlaceholderSQL.getData());
            operators.addAll(eachPlaceholderSQL.getOperators());
        }

        placeholderSQL.setQuery(sb.toString());
        placeholderSQL.setData(data);
        placeholderSQL.setOperator(operators);
        return placeholderSQL;
    }
}
