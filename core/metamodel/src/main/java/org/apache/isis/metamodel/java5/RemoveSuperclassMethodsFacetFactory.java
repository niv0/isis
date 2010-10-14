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


package org.apache.isis.metamodel.java5;

import java.lang.reflect.Method;

import org.apache.isis.commons.lang.JavaClassUtils;
import org.apache.isis.metamodel.facets.Facet;
import org.apache.isis.metamodel.facets.FacetFactoryAbstract;
import org.apache.isis.metamodel.facets.FacetHolder;
import org.apache.isis.metamodel.facets.MethodRemover;
import org.apache.isis.metamodel.spec.feature.ObjectFeatureType;


/**
 * Removes all superclass methods of the class, but doesn't add any {@link Facet}s.
 */
public class RemoveSuperclassMethodsFacetFactory extends FacetFactoryAbstract {

    @SuppressWarnings("unused")
	private static final String JAVA_CLASS_PREFIX = "java.";

    public RemoveSuperclassMethodsFacetFactory() {
        super(ObjectFeatureType.OBJECTS_ONLY);
    }

    @Override
    public boolean process(final Class<?> type, final MethodRemover methodRemover, final FacetHolder holder) {
        removeSuperclassMethods(type, methodRemover);
        return false;
    }

    private void removeSuperclassMethods(final Class<?> type, final MethodRemover methodRemover) {
        if (type == null) {
            return;
        }

        if (!JavaClassUtils.isJavaClass(type)) {
            removeSuperclassMethods(type.getSuperclass(), methodRemover);
            return;
        }

        final Method[] methods = type.getMethods();
        for (int j = 0; j < methods.length; j++) {
            final Method method = methods[j];
            methodRemover.removeMethod(method);
        }

    }

}

