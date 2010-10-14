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


package org.apache.isis.metamodel.facets.object.value;

import org.apache.isis.applib.adapters.ValueSemanticsProvider;
import org.apache.isis.commons.lang.StringUtils;
import org.apache.isis.metamodel.config.IsisConfiguration;
import org.apache.isis.metamodel.facets.FacetHolder;
import org.apache.isis.metamodel.util.ClassUtil;


public final class ValueSemanticsProviderUtil {

    private ValueSemanticsProviderUtil() {}

    public static final String SEMANTICS_PROVIDER_NAME_KEY_PREFIX = "isis.reflector.java.facets.value.";
    public static final String SEMANTICS_PROVIDER_NAME_KEY_SUFFIX = ".semanticsProviderName";

    static String semanticsProviderNameFromConfiguration(final Class<?> type, final IsisConfiguration configuration) {
        final String key = SEMANTICS_PROVIDER_NAME_KEY_PREFIX + type.getCanonicalName() + SEMANTICS_PROVIDER_NAME_KEY_SUFFIX;
        final String semanticsProviderName = configuration.getString(key);
        return !StringUtils.isEmpty(semanticsProviderName) ? semanticsProviderName : null;
    }

    @SuppressWarnings("unchecked")
	public static Class<? extends ValueSemanticsProvider<?>> valueSemanticsProviderOrNull(final Class<?> candidateClass, final String classCandidateName) {
        final Class clazz = candidateClass != null ? ClassUtil.implementingClassOrNull(candidateClass.getName(),
                ValueSemanticsProvider.class, FacetHolder.class) : null;
        return clazz != null ? clazz : ClassUtil.implementingClassOrNull(classCandidateName, ValueSemanticsProvider.class, FacetHolder.class);
    }

}

