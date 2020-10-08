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

import java.util.ArrayList;

/**
 * This class holds both sql query, data (and the operator) to be injected for a prepared statement.
 */
public class PlaceholderSQL {

    private String query;
    private ArrayList<Object> data;
    private ArrayList<ConditionType.PrimitiveOperator> operators;

    public String getQuery() {

        return query;
    }

    public void setQuery(String query) {

        this.query = query;
    }

    public ArrayList<Object> getData() {

        return data;
    }

    public void setData(ArrayList<Object> data) {

        this.data = data;
    }

    public ArrayList<ConditionType.PrimitiveOperator> getOperators() {

        return operators;
    }

    public void setOperator(ArrayList<ConditionType.PrimitiveOperator> operators) {

        this.operators = operators;
    }
}
