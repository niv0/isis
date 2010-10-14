/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */


package org.apache.isis.metamodel.facets.collections.validate;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import org.apache.isis.metamodel.adapter.ObjectAdapter;
import org.apache.isis.metamodel.facets.FacetHolder;
import org.apache.isis.metamodel.java5.ImperativeFacet;
import org.apache.isis.metamodel.util.ObjectInvokeUtils;


public class CollectionValidateRemoveFromFacetViaMethod extends CollectionValidateRemoveFromFacetAbstract implements
        ImperativeFacet {

    private final Method method;

    public CollectionValidateRemoveFromFacetViaMethod(
    		final Method method, 
    		final FacetHolder holder) {
        super(holder);
        this.method = method;
    }

    /**
     * Returns a singleton list of the {@link Method} provided in the constructor. 
     */
    public List<Method> getMethods() {
    	return Collections.singletonList(method);
    }

	public boolean impliesResolve() {
		return true;
	}

	public boolean impliesObjectChanged() {
		return false;
	}

	public String invalidReason(final ObjectAdapter owningAdapter, final ObjectAdapter proposedAdapter) {
        return (String) ObjectInvokeUtils.invoke(method, owningAdapter, proposedAdapter);
    }

    @Override
    protected String toStringValues() {
        return "method=" + method;
    }

}

