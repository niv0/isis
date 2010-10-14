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
import java.util.List;

import org.apache.isis.commons.filters.AbstractFilter;
import org.apache.isis.commons.lang.CastUtils;
import org.apache.isis.metamodel.facets.DecoratingFacet;
import org.apache.isis.metamodel.facets.Facet;
import org.apache.isis.metamodel.spec.feature.ObjectMember;


public final class ImperativeFacetUtils {

    private ImperativeFacetUtils() {}

    /**
     * Returns the provided {@link Facet facet} as an {@link ImperativeFacet} if it either is one or if it is
     * a {@link DecoratingFacet} that in turn wraps an {@link ImperativeFacet}.
     * 
     * <p>
     * Otherwise, returns <tt>null</tt>.
     */
    public static ImperativeFacet getImperativeFacet(final Facet facet) {
        if (facet instanceof ImperativeFacet) {
            return (ImperativeFacet) facet;
        }
        if (facet instanceof DecoratingFacet) {
            final DecoratingFacet<?> decoratingFacet = CastUtils.cast(facet);
            return getImperativeFacet(decoratingFacet.getDecoratedFacet());
        }
        return null;

    }

    public static boolean isImperativeFacet(final Facet facet) {
        return getImperativeFacet(facet) != null;
    }

    public static class ImperativeFacetFlags {
        private boolean impliesResolve;
        private boolean impliesObjectChanged;

        public void apply(ImperativeFacet imperativeFacet) {
            this.impliesResolve |= imperativeFacet.impliesResolve();
            this.impliesObjectChanged |= imperativeFacet.impliesObjectChanged();
        }

        public boolean bothSet() {
            return impliesResolve && impliesObjectChanged;
        }

        public boolean impliesResolve() {
            return impliesResolve;
        }

        public boolean impliesObjectChanged() {
            return impliesObjectChanged;
        }
    }

    public static ImperativeFacetFlags getImperativeFacetFlags(final ObjectMember member, final Method method) {
        ImperativeFacetFlags flags = new ImperativeFacetFlags();
        if (member == null) {
            return flags;
        }
        Facet[] allFacets = member.getFacets(AbstractFilter.noop(Facet.class));
        for (Facet facet : allFacets) {
            ImperativeFacet imperativeFacet = ImperativeFacetUtils.getImperativeFacet(facet);
            if (imperativeFacet == null) {
                continue;
            }
            List<Method> methods = imperativeFacet.getMethods();
            if (!methods.contains(method)) {
                continue;
            }
            flags.apply(imperativeFacet);

            // no need to search further
            if (flags.bothSet()) {
                break;
            }
        }
        return flags;
    }

}
