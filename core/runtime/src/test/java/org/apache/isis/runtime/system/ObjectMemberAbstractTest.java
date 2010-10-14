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


package org.apache.isis.runtime.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.metamodel.adapter.Instance;
import org.apache.isis.metamodel.adapter.ObjectAdapter;
import org.apache.isis.metamodel.authentication.AuthenticationSession;
import org.apache.isis.metamodel.consent.Consent;
import org.apache.isis.metamodel.consent.InteractionInvocationMethod;
import org.apache.isis.metamodel.facets.When;
import org.apache.isis.metamodel.facets.disable.DisableForSessionFacetAbstract;
import org.apache.isis.metamodel.facets.hide.HiddenFacetAbstract;
import org.apache.isis.metamodel.facets.hide.HiddenFacetAlways;
import org.apache.isis.metamodel.facets.hide.HiddenFacetImpl;
import org.apache.isis.metamodel.facets.hide.HiddenFacetNever;
import org.apache.isis.metamodel.facets.hide.HideForContextFacetNone;
import org.apache.isis.metamodel.facets.hide.HideForSessionFacetAbstract;
import org.apache.isis.metamodel.facets.naming.describedas.DescribedAsFacetAbstract;
import org.apache.isis.metamodel.facets.naming.named.NamedFacetAbstract;
import org.apache.isis.metamodel.interactions.PropertyUsabilityContext;
import org.apache.isis.metamodel.interactions.PropertyVisibilityContext;
import org.apache.isis.metamodel.interactions.UsabilityContext;
import org.apache.isis.metamodel.interactions.VisibilityContext;
import org.apache.isis.metamodel.runtimecontext.RuntimeContext;
import org.apache.isis.metamodel.runtimecontext.noruntime.RuntimeContextNoRuntime;
import org.apache.isis.metamodel.runtimecontext.spec.feature.ObjectMemberAbstract;
import org.apache.isis.metamodel.spec.ObjectSpecification;
import org.apache.isis.metamodel.spec.identifier.IdentifiedImpl;
import org.apache.isis.runtime.testsystem.TestProxySystem;


public class ObjectMemberAbstractTest {

    private TestProxySystem system;
    private ObjectMemberAbstractImpl testMember;
    private ObjectAdapter testAdapter;

    @Before
    public void setUp() throws Exception {
        Logger.getRootLogger().setLevel(Level.OFF);

        system = new TestProxySystem();
        system.init();

        testAdapter = system.createPersistentTestObject();

        testMember = new ObjectMemberAbstractImpl("id");
    }
    
    
    @After
    public void tearDown() throws Exception {
        
    }
    

    @Test
    public void testToString() throws Exception {
        testMember.addFacet(new NamedFacetAbstract("", testMember) {});
        assertTrue(testMember.toString().length() > 0);
    }

    @Test
    public void testAvailableForUser() throws Exception {
        testMember.addFacet(new DisableForSessionFacetAbstract(testMember) {
            public String disabledReason(final AuthenticationSession session) {
                return null;
            }
        });
        final Consent usable = testMember.isUsable(null, testAdapter);
        boolean allowed = usable.isAllowed();
        assertTrue(allowed);
    }

    @Test
    public void testVisibleWhenHiddenFacetSetToAlways() {
        testMember.addFacet(new HideForContextFacetNone(testMember));
        testMember.addFacet(new HiddenFacetAbstract(When.ALWAYS, testMember) {
            public String hiddenReason(final ObjectAdapter target) {
                return null;
            }
        });
        final Consent visible = testMember.isVisible(null, testAdapter);
        assertTrue(visible.isAllowed());
    }

    @Test
    public void testVisibleWhenTargetPersistentAndHiddenFacetSetToOncePersisted() {
        testMember.addFacet(new HideForContextFacetNone(testMember));
        testMember.addFacet(new HiddenFacetImpl(When.ONCE_PERSISTED, testMember));
        assertFalse(testMember.isVisible(null, testAdapter).isAllowed());
    }

    @Test
    public void testVisibleWhenTargetPersistentAndHiddenFacetSetToUntilPersisted() {
        testMember.addFacet(new HideForContextFacetNone(testMember));
        testMember.addFacet(new HiddenFacetImpl(When.UNTIL_PERSISTED, testMember));
        final Consent visible = testMember.isVisible(null, testAdapter);
        assertTrue(visible.isAllowed());
    }

    @Test
    public void testVisibleWhenTargetTransientAndHiddenFacetSetToUntilPersisted() {
        testMember.addFacet(new HideForContextFacetNone(testMember));
        testMember.addFacet(new HiddenFacetImpl(When.UNTIL_PERSISTED, testMember));
        final ObjectAdapter transientTestAdapter = system.createTransientTestObject();
        assertFalse(testMember.isVisible(null, transientTestAdapter).isAllowed());
    }

    @Test
    public void testVisibleDeclarativelyByDefault() {
        testMember.addFacet(new HiddenFacetNever(testMember) {});
        assertTrue(testMember.isVisible(null, testAdapter).isAllowed());
    }

    @Test
    public void testVisibleDeclaratively() {
        testMember.addFacet(new HiddenFacetAlways(testMember) {});
        assertFalse(testMember.isVisible(null, testAdapter).isAllowed());
    }

    @Test
    public void testVisibleForSessionByDefault() {
        final Consent visible = testMember.isVisible(null, testAdapter);
        assertTrue(visible.isAllowed());
    }

    @Test
    public void testVisibleForSession() {
        testMember.addFacet(new HideForSessionFacetAbstract(testMember) {
            public String hiddenReason(final AuthenticationSession session) {
                return "Hidden";
            }
        });
        assertFalse(testMember.isVisible(null, testAdapter).isAllowed());
    }

    @Test
    public void testVisibleForSessionFails() {
        testMember.addFacet(new HideForSessionFacetAbstract(testMember) {
            public String hiddenReason(final AuthenticationSession session) {
                return "hidden";
            }
        });
        assertFalse(testMember.isVisible(null, testAdapter).isAllowed());
    }

    @Test
    public void testName() throws Exception {
        final String name = "action name";
        testMember.addFacet(new NamedFacetAbstract(name, testMember) {});
        assertEquals(name, testMember.getName());
    }

    @Test
    public void testDescription() throws Exception {
        final String name = "description text";
        testMember.addFacet(new DescribedAsFacetAbstract(name, testMember) {});
        assertEquals(name, testMember.getDescription());
    }
}

class ObjectMemberAbstractImpl extends ObjectMemberAbstract {

    protected ObjectMemberAbstractImpl(final String id) {
        super(id, new IdentifiedImpl(), MemberType.ONE_TO_ONE_ASSOCIATION, new RuntimeContextNoRuntime());
    }

    protected ObjectMemberAbstractImpl(final String id, final RuntimeContext runtimeContext) {
        super(id, new IdentifiedImpl(), MemberType.ONE_TO_ONE_ASSOCIATION, runtimeContext);
    }


    public String debugData() {
        return null;
    }

    public Consent isUsable(final ObjectAdapter target) {
        return null;
    }

    public ObjectSpecification getSpecification() {
        return null;
    }

    public UsabilityContext<?> createUsableInteractionContext(
            final AuthenticationSession session,
            final InteractionInvocationMethod invocationMethod,
            final ObjectAdapter target) {
        return new PropertyUsabilityContext(session, invocationMethod, target, getIdentifier());
    }

    public VisibilityContext<?> createVisibleInteractionContext(
            final AuthenticationSession session,
            final InteractionInvocationMethod invocationMethod,
            final ObjectAdapter targetObjectAdapter) {
        return new PropertyVisibilityContext(session, invocationMethod, targetObjectAdapter, getIdentifier());
    }

    // /////////////////////////////////////////////////////////////
    // getInstance
    // /////////////////////////////////////////////////////////////
    
    public Instance getInstance(ObjectAdapter adapter) {
        return null;
    }

}

