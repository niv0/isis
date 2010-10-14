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

import org.apache.isis.metamodel.facets.FacetFactory;
import org.apache.isis.metamodel.facets.PropertyOrCollectionIdentifyingFacetFactory;
import org.apache.isis.metamodel.spec.feature.ObjectFeatureType;
import org.apache.isis.metamodel.specloader.collectiontyperegistry.CollectionTypeRegistry;
import org.apache.isis.metamodel.specloader.collectiontyperegistry.CollectionTypeRegistryAware;


public abstract class PropertyOrCollectionIdentifyingFacetFactoryAbstract extends MethodPrefixBasedFacetFactoryAbstract implements
        PropertyOrCollectionIdentifyingFacetFactory, CollectionTypeRegistryAware {

    private CollectionTypeRegistry collectionTypeRegistry;

    public PropertyOrCollectionIdentifyingFacetFactoryAbstract(
            final String[] prefixes,
            final ObjectFeatureType[] featureTypes) {
        super(prefixes, featureTypes);
    }

    protected boolean isCollectionOrArray(final Class<?> cls) {
        return getCollectionTypeRepository().isCollectionType(cls) || getCollectionTypeRepository().isArrayType(cls);
    }

    // /////////////////////////////////////////////////////////////////////////
    // Injected: CollectionTypeRegistry
    // /////////////////////////////////////////////////////////////////////////

    protected CollectionTypeRegistry getCollectionTypeRepository() {
        return collectionTypeRegistry;
    }

    /**
     * Injected so can propogate to any {@link #registerFactory(FacetFactory) registered} {@link FacetFactory}
     * s that are also {@link CollectionTypeRegistryAware}.
     */
    public void setCollectionTypeRegistry(final CollectionTypeRegistry collectionTypeRegistry) {
        this.collectionTypeRegistry = collectionTypeRegistry;
    }

}

