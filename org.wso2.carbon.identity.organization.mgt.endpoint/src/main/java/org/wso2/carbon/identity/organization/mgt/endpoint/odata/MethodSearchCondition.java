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

package org.wso2.carbon.identity.organization.mgt.endpoint.odata;

import org.apache.cxf.jaxrs.ext.search.Beanspector;
import org.apache.cxf.jaxrs.ext.search.ConditionType;
import org.apache.cxf.jaxrs.ext.search.PrimitiveStatement;
import org.apache.cxf.jaxrs.ext.search.SearchBean;
import org.apache.cxf.jaxrs.ext.search.SearchCondition;
import org.apache.cxf.jaxrs.ext.search.SearchConditionVisitor;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

/**
 * Custom search condition class to support 'contains', 'startswith' and 'endswith' search methods.
 * @param <T>
 */
public class MethodSearchCondition<T> implements SearchCondition<T> {

    private String propertyName;
    private Object propertyValue;
    private Type propertyType;
    private T condition;
    private ConditionType cType;
    private Beanspector<T> beanspector;
    private String method;

    public MethodSearchCondition(String propertyName, Object propertyValue, ConditionType ct, T condition,
            String method) {

        this(propertyName, propertyValue, propertyValue.getClass(), ct, condition, method);
    }

    public MethodSearchCondition(String propertyName, Object propertyValue, Type propertyType, ConditionType ct,
            T condition, String method) {

        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
        this.propertyType = propertyType;
        this.condition = condition;
        this.cType = ct;
        this.method = method;
        if (propertyName != null) {
            this.beanspector = SearchBean.class.isAssignableFrom(condition.getClass()) ?
                    null :
                    new Beanspector(condition);
        }
    }

    @Override
    public boolean isMet(T t) {

        return false;
    }

    @Override
    public List<T> findAll(Collection<T> collection) {

        return null;
    }

    @Override
    public T getCondition() {

        return null;
    }

    @Override
    public PrimitiveStatement getStatement() {

        return new PrimitiveStatement(this.propertyName, this.propertyValue, this.propertyType, this.cType);
    }

    @Override
    public List<SearchCondition<T>> getSearchConditions() {

        return null;
    }

    @Override
    public ConditionType getConditionType() {

        return this.cType;
    }

    @Override
    public void accept(SearchConditionVisitor<T, ?> searchConditionVisitor) {

    }

    public String getMethod() {

        return method;
    }
}
