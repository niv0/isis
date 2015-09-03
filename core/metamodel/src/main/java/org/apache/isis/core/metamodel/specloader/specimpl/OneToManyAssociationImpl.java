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

package org.apache.isis.core.metamodel.specloader.specimpl;

import org.apache.isis.applib.annotation.Where;
import org.apache.isis.core.commons.authentication.AuthenticationSession;
import org.apache.isis.core.commons.debug.DebugString;
import org.apache.isis.core.commons.exceptions.IsisException;
import org.apache.isis.core.commons.util.ToString;
import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.consent.Consent;
import org.apache.isis.core.metamodel.consent.InteractionInitiatedBy;
import org.apache.isis.core.metamodel.consent.InteractionResult;
import org.apache.isis.core.metamodel.deployment.DeploymentCategory;
import org.apache.isis.core.metamodel.facetapi.FeatureType;
import org.apache.isis.core.metamodel.facets.FacetedMethod;
import org.apache.isis.core.metamodel.facets.propcoll.accessor.PropertyOrCollectionAccessorFacet;
import org.apache.isis.core.metamodel.facets.collections.modify.CollectionAddToFacet;
import org.apache.isis.core.metamodel.facets.collections.modify.CollectionClearFacet;
import org.apache.isis.core.metamodel.facets.collections.modify.CollectionFacet;
import org.apache.isis.core.metamodel.facets.collections.modify.CollectionRemoveFromFacet;
import org.apache.isis.core.metamodel.interactions.CollectionAddToContext;
import org.apache.isis.core.metamodel.interactions.CollectionRemoveFromContext;
import org.apache.isis.core.metamodel.interactions.CollectionUsabilityContext;
import org.apache.isis.core.metamodel.interactions.CollectionVisibilityContext;
import org.apache.isis.core.metamodel.interactions.InteractionUtils;
import org.apache.isis.core.metamodel.interactions.UsabilityContext;
import org.apache.isis.core.metamodel.interactions.ValidityContext;
import org.apache.isis.core.metamodel.interactions.VisibilityContext;
import org.apache.isis.core.metamodel.spec.Instance;
import org.apache.isis.core.metamodel.spec.ObjectSpecification;
import org.apache.isis.core.metamodel.spec.feature.ObjectMemberContext;
import org.apache.isis.core.metamodel.spec.feature.OneToManyAssociation;

public class OneToManyAssociationImpl extends ObjectAssociationAbstract implements OneToManyAssociation {

    public OneToManyAssociationImpl(
            final FacetedMethod facetedMethod, 
            final ObjectMemberContext objectMemberContext) {
        this(facetedMethod, getSpecification(objectMemberContext.getSpecificationLoader(), facetedMethod.getType()), objectMemberContext);
    }

    protected OneToManyAssociationImpl(
            final FacetedMethod facetedMethod, 
            final ObjectSpecification objectSpec, 
            final ObjectMemberContext objectMemberContext) {
        super(facetedMethod, FeatureType.COLLECTION, objectSpec, objectMemberContext);
    }

    @Override
    public CollectionSemantics getCollectionSemantics() {
        final Class<?> underlyingClass = getSpecification().getCorrespondingClass();
        return getCollectionTypeRegistry().semanticsOf(underlyingClass);
    }

    // /////////////////////////////////////////////////////////////
    // Hidden (or visible)
    // /////////////////////////////////////////////////////////////

    @Override
    public VisibilityContext<?> createVisibleInteractionContext(
            final ObjectAdapter ownerAdapter, final InteractionInitiatedBy interactionInitiatedBy,
            Where where) {
        final AuthenticationSession session = getAuthenticationSession();
        return new CollectionVisibilityContext(getDeploymentCategory(), session, interactionInitiatedBy, ownerAdapter, getIdentifier(), where);
    }

    // /////////////////////////////////////////////////////////////
    // Disabled (or enabled)
    // /////////////////////////////////////////////////////////////

    @Override
    public UsabilityContext<?> createUsableInteractionContext(
            final ObjectAdapter ownerAdapter, final InteractionInitiatedBy interactionInitiatedBy,
            Where where) {
        final AuthenticationSession session = getAuthenticationSession();
        return new CollectionUsabilityContext(getDeploymentCategory(), session, interactionInitiatedBy, ownerAdapter, getIdentifier(), where);
    }

    // /////////////////////////////////////////////////////////////
    // Validate Add
    // /////////////////////////////////////////////////////////////

    // Not API
    private ValidityContext<?> createValidateAddInteractionContext(
            final InteractionInitiatedBy interactionInitiatedBy,
            final ObjectAdapter ownerAdapter,
            final ObjectAdapter proposedToAddAdapter) {
        final AuthenticationSession authenticationSession = getAuthenticationSession();
        return new CollectionAddToContext(getDeploymentCategory(), authenticationSession, interactionInitiatedBy, ownerAdapter, getIdentifier(), proposedToAddAdapter);
    }

    @Override
    public Consent isValidToAdd(
            final ObjectAdapter ownerAdapter,
            final ObjectAdapter proposedToAddAdapter,
            final InteractionInitiatedBy interactionInitiatedBy) {
        return isValidToAddResult(ownerAdapter, proposedToAddAdapter, interactionInitiatedBy).createConsent();
    }

    private InteractionResult isValidToAddResult(
            final ObjectAdapter ownerAdapter,
            final ObjectAdapter proposedToAddAdapter,
            final InteractionInitiatedBy interactionInitiatedBy) {
        final ValidityContext<?> validityContext = createValidateAddInteractionContext(
                interactionInitiatedBy, ownerAdapter, proposedToAddAdapter);
        return InteractionUtils.isValidResult(this, validityContext);
    }

    // /////////////////////////////////////////////////////////////
    // Validate Remove
    // /////////////////////////////////////////////////////////////

    private ValidityContext<?> createValidateRemoveInteractionContext(
            final ObjectAdapter ownerAdapter,
            final ObjectAdapter proposedToRemoveAdapter,
            final InteractionInitiatedBy interactionInitiatedBy) {
        return new CollectionRemoveFromContext(
                getDeploymentCategory(), getAuthenticationSession(),
                interactionInitiatedBy,
                ownerAdapter,
                getIdentifier(),
                proposedToRemoveAdapter);
    }

    @Override
    public Consent isValidToRemove(
            final ObjectAdapter ownerAdapter,
            final ObjectAdapter proposedToRemoveAdapter,
            final InteractionInitiatedBy interactionInitiatedBy) {
        return isValidToRemoveResult(
                ownerAdapter, proposedToRemoveAdapter, interactionInitiatedBy).createConsent();
    }

    private InteractionResult isValidToRemoveResult(
            final ObjectAdapter ownerAdapter,
            final ObjectAdapter proposedToRemoveAdapter,
            final InteractionInitiatedBy interactionInitiatedBy) {
        final ValidityContext<?> validityContext = createValidateRemoveInteractionContext(
                ownerAdapter, proposedToRemoveAdapter, interactionInitiatedBy);
        return InteractionUtils.isValidResult(this, validityContext);
    }

    private boolean readWrite() {
        return !isNotPersisted();
    }

    // /////////////////////////////////////////////////////////////
    // get, isEmpty, add, clear
    // /////////////////////////////////////////////////////////////

    @Override
    public ObjectAdapter get(
            final ObjectAdapter ownerAdapter,
            final InteractionInitiatedBy interactionInitiatedBy) {

        final PropertyOrCollectionAccessorFacet accessor = getFacet(PropertyOrCollectionAccessorFacet.class);
        final Object collection = accessor.getProperty(ownerAdapter, getAuthenticationSession(), getDeploymentCategory(),
                interactionInitiatedBy);
        if (collection == null) {
            return null;
        }
        return getAdapterManager().adapterFor(collection, ownerAdapter, this);
    }

    @Override
    public boolean isEmpty(final ObjectAdapter parentAdapter, final InteractionInitiatedBy interactionInitiatedBy) {
        // REVIEW should we be able to determine if a collection is empty
        // without loading it?
        final ObjectAdapter collection = get(parentAdapter, interactionInitiatedBy);
        final CollectionFacet facet = CollectionFacet.Utils.getCollectionFacetFromSpec(collection);
        return facet.size(collection) == 0;
    }

    // /////////////////////////////////////////////////////////////
    // add, clear
    // /////////////////////////////////////////////////////////////

    @Override
    public void addElement(
            final ObjectAdapter ownerAdapter,
            final ObjectAdapter referencedAdapter,
            final InteractionInitiatedBy interactionInitiatedBy) {
        if (referencedAdapter == null) {
            throw new IllegalArgumentException("Can't use null to add an item to a collection");
        }
        if (readWrite()) {
            if (ownerAdapter.representsPersistent() && referencedAdapter.isTransient()) {
                throw new IsisException("can't set a reference to a transient object from a persistent one: " + ownerAdapter.titleString(null) + " (persistent) -> " + referencedAdapter.titleString() + " (transient)");
            }
            final CollectionAddToFacet facet = getFacet(CollectionAddToFacet.class);
            facet.add(ownerAdapter, referencedAdapter, interactionInitiatedBy);
        }
    }

    @Override
    public void removeElement(
            final ObjectAdapter ownerAdapter,
            final ObjectAdapter referencedAdapter,
            final InteractionInitiatedBy interactionInitiatedBy) {
        if (referencedAdapter == null) {
            throw new IllegalArgumentException("element should not be null");
        }
        if (readWrite()) {
            final CollectionRemoveFromFacet facet = getFacet(CollectionRemoveFromFacet.class);
            facet.remove(ownerAdapter, referencedAdapter, interactionInitiatedBy);
        }
    }

    public void removeAllAssociations(final ObjectAdapter ownerAdapter) {
        final CollectionClearFacet facet = getFacet(CollectionClearFacet.class);
        facet.clear(ownerAdapter);
    }

    @Override
    public void clearCollection(final ObjectAdapter ownerAdapter) {
        if (readWrite()) {
            final CollectionClearFacet facet = getFacet(CollectionClearFacet.class);
            facet.clear(ownerAdapter);
        }
    }

    // /////////////////////////////////////////////////////////////
    // defaults
    // /////////////////////////////////////////////////////////////

    @Override
    public ObjectAdapter getDefault(final ObjectAdapter ownerAdapter) {
        return null;
    }

    @Override
    public void toDefault(final ObjectAdapter ownerAdapter) {
    }

    // /////////////////////////////////////////////////////////////
    // choices & autoComplete
    // /////////////////////////////////////////////////////////////

    @Override
    public ObjectAdapter[] getChoices(
            final ObjectAdapter ownerAdapter,
            final InteractionInitiatedBy interactionInitiatedBy) {
        return new ObjectAdapter[0];
    }

    @Override
    public boolean hasChoices() {
        return false;
    }

    
    @Override
    public boolean hasAutoComplete() {
        return false;
    }

    @Override
    public ObjectAdapter[] getAutoComplete(
            ObjectAdapter object,
            String searchArg,
            final AuthenticationSession authenticationSession,
            final DeploymentCategory deploymentCategory,
            final InteractionInitiatedBy interactionInitiatedBy) {
        return new ObjectAdapter[0];
    }
    
    @Override
    public int getAutoCompleteMinLength() {
        return 0; // n/a
    }

    // /////////////////////////////////////////////////////////////
    // getInstance
    // /////////////////////////////////////////////////////////////

    @Override
    public Instance getInstance(final ObjectAdapter adapter) {
        final OneToManyAssociation specification = this;
        return adapter.getInstance(specification);
    }

    // /////////////////////////////////////////////////////////////
    // debug, toString
    // /////////////////////////////////////////////////////////////

    @Override
    public String debugData() {
        final DebugString debugString = new DebugString();
        debugString.indent();
        debugString.indent();
        getFacetedMethod().debugData(debugString);
        return debugString.toString();
    }

    @Override
    public String toString() {
        final ToString str = new ToString(this);
        str.append(super.toString());
        str.append(",");
        str.append("persisted", !isNotPersisted());
        str.append("type", getSpecification() == null ? "unknown" : getSpecification().getShortIdentifier());
        return str.toString();
    }


}
