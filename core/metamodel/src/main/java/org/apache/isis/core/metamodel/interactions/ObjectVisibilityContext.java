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

package org.apache.isis.core.metamodel.interactions;

import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.events.ObjectVisibilityEvent;
import org.apache.isis.core.commons.authentication.AuthenticationSession;
import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.consent.InteractionContextType;
import org.apache.isis.core.metamodel.consent.InteractionInitiatedBy;
import org.apache.isis.core.metamodel.deployment.DeploymentCategory;

import static org.apache.isis.core.metamodel.adapter.ObjectAdapter.Util.unwrap;

/**
 * See {@link org.apache.isis.core.metamodel.interactions.InteractionContext} for overview; analogous to
 * {@link org.apache.isis.applib.events.ObjectVisibilityEvent}.
 */
public class ObjectVisibilityContext extends VisibilityContext<ObjectVisibilityEvent> implements ProposedHolder {

    public ObjectVisibilityContext(
            final DeploymentCategory deploymentCategory,
            final AuthenticationSession session,
            final InteractionInitiatedBy invocationMethod,
            final ObjectAdapter target,
            final Identifier identifier,
            final Where where) {
        super(InteractionContextType.OBJECT_VISIBILITY, deploymentCategory, session, invocationMethod, identifier, target, where);
    }

    @Override
    public ObjectVisibilityEvent createInteractionEvent() {
        return new ObjectVisibilityEvent(unwrap(getTarget()), getIdentifier());
    }

    @Override
    public ObjectAdapter getProposed() {
        return getTarget();
    }

}
