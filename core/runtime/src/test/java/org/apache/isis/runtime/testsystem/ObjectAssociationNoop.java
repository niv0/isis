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


package org.apache.isis.runtime.testsystem;

import org.apache.isis.applib.Identifier;
import org.apache.isis.metamodel.adapter.Instance;
import org.apache.isis.metamodel.adapter.ObjectAdapter;
import org.apache.isis.metamodel.authentication.AuthenticationSession;
import org.apache.isis.metamodel.consent.InteractionInvocationMethod;
import org.apache.isis.metamodel.interactions.UsabilityContext;
import org.apache.isis.metamodel.interactions.VisibilityContext;
import org.apache.isis.metamodel.runtimecontext.RuntimeContext;
import org.apache.isis.metamodel.runtimecontext.spec.feature.ObjectAssociationAbstract;
import org.apache.isis.metamodel.spec.ObjectSpecification;
import org.apache.isis.metamodel.spec.identifier.IdentifiedImpl;


public class ObjectAssociationNoop extends ObjectAssociationAbstract {

    public ObjectAssociationNoop(
    		final String name, 
    		final ObjectSpecification spec, 
    		final RuntimeContext runtimeContext) {
        super(name, spec, MemberType.ONE_TO_ONE_ASSOCIATION, new IdentifiedImpl(), runtimeContext);
    }

    public String debugData() {
        return "";
    }

    @Override
    public ObjectAdapter get(final ObjectAdapter fromObject) {
        return ((TestProxyAdapter) fromObject).getField(this); // contentObject;
    }

    @Override
    public String getBusinessKeyName() {
        return null;
    }

    @Override
    public final boolean isNotPersisted() {
        return false;
    }

    @Override
    public boolean isEmpty(final ObjectAdapter adapter) {
        return false;
    }

    @Override
    public boolean hasChoices() {
        return false;
    }

    public ObjectAdapter getDefault(final ObjectAdapter adapter) {
        return null;
    }

    public void toDefault(final ObjectAdapter target) {}

    @Override
    public Identifier getIdentifier() {
        return null;
    }

    public ObjectAdapter[] getChoices(final ObjectAdapter object) {
        return new ObjectAdapter[] {};
    }

    public UsabilityContext<?> createUsableInteractionContext(
            final AuthenticationSession session,
            final InteractionInvocationMethod invocationMethod,
            final ObjectAdapter target) {
        return null;
    }

    public VisibilityContext<?> createVisibleInteractionContext(
            final AuthenticationSession session,
            final InteractionInvocationMethod invocationMethod,
            final ObjectAdapter targetObjectAdapter) {
        return null;
    }

    // /////////////////////////////////////////////////////////////
    // getInstance
    // /////////////////////////////////////////////////////////////
    
    public Instance getInstance(ObjectAdapter adapter) {
        return null;
    }


}
