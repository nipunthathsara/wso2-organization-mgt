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
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.GET_ALL_ORGANIZATION_IDS_MULTI_ATTR_SEARCH_CLAUSE;
import static org.wso2.carbon.identity.organization.mgt.core.constant.SQLConstants.VIEW_ATTR_KEY_COLUMN;

/**
 * This class represent a complex condition with a {@link ConditionType}. A complex condition can contain a list of
 * another complex conditions or a primitive condition. Ex: A sample complex condition with two complex conditions
 * as a list can represent the form, {@link ComplexCondition}[1] {@link ConditionType}[2] {@link ComplexCondition}[2].
 */
public class ComplexCondition implements Condition {

    private List<Condition> conditions;
    private ConditionType.ComplexOperator operator;
    private int attrSearchConditionNumber = 0;

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
        ArrayList<String> attrSearchJoins = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        // Identify attribute search conditions (attributeKey eq 'Type' and attributeValue eq 'partner')
        boolean isAttrSearch = false;
        for (Condition condition : conditions) {
            isAttrSearch = false;
            if (condition instanceof PrimitiveCondition) {
                isAttrSearch = true;
                PrimitiveCondition pCondition = (PrimitiveCondition) condition;
                if (!ConditionType.ComplexOperator.AND.equals(operator) &&
                        !(ORGANIZATION_SEARCH_BEAN_FIELD_ATTRIBUTE_KEY.equals(pCondition.getProperty())
                        || ORGANIZATION_SEARCH_BEAN_FIELD_ATTRIBUTE_VALUE.equals(pCondition.getProperty()))) {
                    // Not an attribute search condition
                    isAttrSearch = false;
                    break;
                }
            } else {
                break;
            }
        }
        System.out.println("*************************************" + isAttrSearch);
        // Build join statement for this attribute search complex condition. (Includes both attrKey and attrValue)
        if (isAttrSearch) {
            ++attrSearchConditionNumber;
            String attrSearchJoin = GET_ALL_ORGANIZATION_IDS_MULTI_ATTR_SEARCH_CLAUSE;
            attrSearchJoins.add(attrSearchJoin);
        }

        boolean first = true;
        for (Condition condition : conditions) {

            if (isAttrSearch) {
                // Build SQLs for primitive conditions - lower(V1.ATTR_KEY) = lower(?), lower(V1.ATTR_VALUE) = lower(?)
                PlaceholderSQL eachPlaceholderSQL = condition.buildQuery(primitiveConditionValidator);
                // Replace place holders
                String join = attrSearchJoins.get(attrSearchJoins.size() - 1);
                String query = eachPlaceholderSQL.getQuery();
                if (eachPlaceholderSQL.getQuery().contains(VIEW_ATTR_KEY_COLUMN)) {
                    attrSearchJoins.set(
                            attrSearchJoins.size() - 1,
                            join.replace("{attrKey}", query)
                                    .replaceAll("\\{N}", "V" + attrSearchConditionNumber)
                    );
                } else {
                    attrSearchJoins.set(
                            attrSearchJoins.size() - 1,
                            join.replace("{attrValue}", query)
                                    .replaceAll("\\{N}", "V" + attrSearchConditionNumber)
                    );
                }
                data.addAll(eachPlaceholderSQL.getData());
                continue;
            }

            if (!first) {
                sb.append(" ").append(operator.toSQL()).append(" ");
            } else {
                first = false;
            }
            sb.append("(");
            PlaceholderSQL eachPlaceholderSQL = condition.buildQuery(primitiveConditionValidator);
            sb.append(eachPlaceholderSQL.getQuery());
            data.addAll(eachPlaceholderSQL.getData());
            operators.addAll(eachPlaceholderSQL.getOperators());
            attrSearchJoins.addAll(eachPlaceholderSQL.getAttrSearchJoins());
            sb.append(")");
        }

        placeholderSQL.setAttrSearchJoins(attrSearchJoins);
        placeholderSQL.setQuery(sb.toString());
        placeholderSQL.setData(data);
        placeholderSQL.setOperator(operators);
        return placeholderSQL;
    }
}
