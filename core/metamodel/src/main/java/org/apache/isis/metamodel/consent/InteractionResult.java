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


package org.apache.isis.metamodel.consent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.isis.applib.events.InteractionEvent;


public class InteractionResult {

    /**
     * Initially {@link #ADVISING}; when call {@link InteractionResult#getInteractionEvent()}, flips over into
     * {@link #ADVISED}.
     * 
     * <p>
     * Subsequent attempts to {@link InteractionResult#advise(String, InteractionAdvisorFacet)} will then be
     * disallowed.
     */
    enum State {
        ADVISING, ADVISED
    }

    private final InteractionEvent interactionEvent;
    private final StringBuilder reasonBuf = new StringBuilder();
    private final List<InteractionAdvisorFacet> facets = new ArrayList<InteractionAdvisorFacet>();

    private State state = State.ADVISING;

    public InteractionResult(final InteractionEvent interactionEvent) {
        this.interactionEvent = interactionEvent;
    }

    /**
     * Returns the contained {@link InteractionEvent}, if necessary updated with the
     * {@link #advise(String, InteractionAdvisorFacet) advice} of the interactions.
     * 
     * <p>
     * That is, if still {@link State#ADVISING advising}, then copies over the details from this result into
     * the contained {@link InteractionEvent}, and flips into {@link State#ADVISED advised (done)}.
     * 
     * @return
     */
    public InteractionEvent getInteractionEvent() {
        if (state == State.ADVISING) {
            interactionEvent.advised(getReason(), getAdvisorFacetClass());
            state = State.ADVISED;
        }
        return interactionEvent;
    }

    private Class<?> getAdvisorFacetClass() {
        final InteractionAdvisorFacet advisorFacet = getAdvisorFacet();
        return advisorFacet != null ? advisorFacet.getClass() : null;
    }

    public void advise(final String reason, final InteractionAdvisorFacet facet) {
        if (state == State.ADVISED) {
            throw new IllegalStateException("Cannot append since have called getInteractionEvent");
        }
        if (reason == null) {
            return;
        }
        if (isVetoing()) {
            reasonBuf.append("; ");
        }
        facets.add(facet);
        reasonBuf.append(reason);
    }

    public boolean isVetoing() {
        return !isNotVetoing();
    }

    public boolean isNotVetoing() {
        return reasonBuf.length() == 0;
    }

    /**
     * Returns the first of the {@link #getAdvisorFacets()} that has been
     * {@link #advise(String, InteractionAdvisorFacet) advised}, or <tt>null</tt> if none yet.
     * 
     * @see #getAdvisorFacets()
     */
    public InteractionAdvisorFacet getAdvisorFacet() {
        return facets.size() >= 1 ? facets.get(0) : null;
    }

    /**
     * Returns all {@link InteractionAdvisorFacet facet}s that have
     * {@link #advise(String, InteractionAdvisorFacet) append}ed reasons to the buffer.
     * 
     * @see #getAdvisorFacet()
     */
    public List<InteractionAdvisorFacet> getAdvisorFacets() {
        return Collections.unmodifiableList(facets);
    }

    public Consent createConsent() {
        if (isNotVetoing()) {
            return new Allow(this);
        } else {
            return new Veto(this);
        }
    }

    /**
     * Gets the reason as currently known, but does not change the state.
     * 
     * <p>
     * If {@link #isNotVetoing()}, then returns <tt>null</tt>.  Otherwise will be a non-empty string.
     */
    public String getReason() {
        return isNotVetoing()? null : reasonBuf.toString();
    }

    @Override
    public String toString() {
        return String.format("%s: %s: %s (%d facets advised)", interactionEvent, state, toStringInterpret(reasonBuf), facets
                .size());
    }

    private String toStringInterpret(final StringBuilder reasonBuf) {
        if (getReason().length() == 0) {
            return "allowed";
        } else {
            return "vetoed";
        }
    }

}

