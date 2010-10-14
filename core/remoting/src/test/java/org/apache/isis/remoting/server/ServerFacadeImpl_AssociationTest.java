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


package org.apache.isis.remoting.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.apache.isis.commons.exceptions.IsisException;
import org.apache.isis.metamodel.adapter.ObjectAdapter;
import org.apache.isis.metamodel.adapter.oid.Oid;
import org.apache.isis.metamodel.authentication.AuthenticationSession;
import org.apache.isis.metamodel.spec.feature.ObjectAssociation;
import org.apache.isis.metamodel.testspec.TestProxySpecification;
import org.apache.isis.remoting.data.DummyReferenceData;
import org.apache.isis.remoting.data.common.ObjectData;
import org.apache.isis.remoting.exchange.ClearAssociationRequest;
import org.apache.isis.remoting.exchange.ClearAssociationResponse;
import org.apache.isis.remoting.exchange.SetAssociationRequest;
import org.apache.isis.remoting.exchange.SetAssociationResponse;
import org.apache.isis.remoting.facade.impl.ServerFacadeImpl;
import org.apache.isis.remoting.protocol.encoding.internal.ObjectEncoderDecoder;
import org.apache.isis.runtime.authentication.AuthenticationManager;
import org.apache.isis.runtime.context.IsisContext;
import org.apache.isis.runtime.persistence.ConcurrencyException;
import org.apache.isis.runtime.testsystem.ProxyJunit4TestCase;
import org.apache.isis.runtime.testsystem.TestProxyAssociation;
import org.apache.isis.runtime.testsystem.TestProxyVersion;


@RunWith(JMock.class)
public class ServerFacadeImpl_AssociationTest extends ProxyJunit4TestCase {

    private Mockery mockery = new JUnit4Mockery();


    private ServerFacadeImpl server;
    private AuthenticationSession authenticationSession;
    private ObjectAdapter movieAdapter;
    private DummyReferenceData movieData;
    private DummyReferenceData personData;
    private TestProxyAssociation nameField;
    private ObjectAdapter personAdapter;

    private AuthenticationManager mockAuthenticationManager;
    private ObjectEncoderDecoder mockObjectEncoder;

    /*
     * Testing the Distribution implementation ServerDistribution. This uses the encoder to unmarshall objects
     * and then calls the persistor and reflector; all of which should be mocked.
     */
    @Before
    public void setUp() throws Exception {

        mockAuthenticationManager = mockery.mock(AuthenticationManager.class);
        mockObjectEncoder = mockery.mock(ObjectEncoderDecoder.class);
        
        server = new ServerFacadeImpl(mockAuthenticationManager);
        server.setEncoder(mockObjectEncoder);
        server.init();

        authenticationSession = IsisContext.getAuthenticationSession();

        movieAdapter = system.createPersistentTestObject();
        final Oid movieOid = movieAdapter.getOid();

        movieData = new DummyReferenceData(movieOid, "none", movieAdapter.getVersion());

        final TestProxySpecification spec = (TestProxySpecification) movieAdapter.getSpecification();
        nameField = new TestProxyAssociation("director", system.getSpecification(String.class));
        spec.setupFields(new ObjectAssociation[] { nameField });

        personAdapter = system.createPersistentTestObject();
        final Oid personOid = personAdapter.getOid();

        personData = new DummyReferenceData(personOid, "none", personAdapter.getVersion());
    }

    @After
    public void tearDown() throws Exception {
        system.shutdown();
    }

    @Test
    public void testClearAssociation() {
        /*
         * other tests for clear: - clear collection element - fails if unauthorised - fails if unavailable
         * 
         * could place all these clear test in one class; test other methods in other classes
         */
        IsisContext.getTransactionManager().startTransaction();
        ClearAssociationRequest request = new ClearAssociationRequest(authenticationSession, "director", movieData, personData);
		ClearAssociationResponse response = server.clearAssociation(request);
		final ObjectData[] updatesData = response.getUpdates();
        IsisContext.getTransactionManager().endTransaction();

        nameField.assertFieldEmpty(movieAdapter);
        assertEquals(0, updatesData.length);
    }

    @Test
    public void testSetAssociation() {
        IsisContext.getTransactionManager().startTransaction();
        SetAssociationRequest request = new SetAssociationRequest(authenticationSession, "director", movieData, personData);
        SetAssociationResponse response = server.setAssociation(request);
		final ObjectData[] updates = response.getUpdates();
        IsisContext.getTransactionManager().endTransaction();

        nameField.assertField(movieAdapter, personAdapter.getObject());
        assertEquals(0, updates.length);
    }

    @Test
    public void testSetAssociationFailsWithNonCurrentTarget() {
        // version should be different, causing concurrency exception
        movieAdapter.setOptimisticLock(new TestProxyVersion(6));
        try {
        	SetAssociationRequest request = new SetAssociationRequest(authenticationSession, "director", movieData, personData);
            server.setAssociation(request);
            fail();
        } catch (final ConcurrencyException expected) {}
    }

    @Test
    public void testSetAssociationFailsWhenInvisible() {
        nameField.setUpIsVisible(false);
        try {
            SetAssociationRequest request = new SetAssociationRequest(authenticationSession, "director", movieData, personData);
			server.setAssociation(request);
            fail();
        } catch (final IsisException expected) {
            assertEquals("can't modify field as not visible or editable", expected.getMessage());
        }
    }

    @Test
    public void testSetAssociationFailsWhenUnavailable() {
        nameField.setUpIsUnusableFor(movieAdapter);
        try {
        	SetAssociationRequest request = new SetAssociationRequest(authenticationSession, "director", movieData, personData);
            server.setAssociation(request);
            fail();
        } catch (final IsisException expected) {
            assertEquals("can't modify field as not visible or editable", expected.getMessage());
        }
    }

    @Test
    public void testSetAssociationFailsWithNonCurrentAssociate() {
        // version should be different, causing concurrency exception
        personAdapter.setOptimisticLock(new TestProxyVersion(6));
        try {
        	SetAssociationRequest request = new SetAssociationRequest(authenticationSession, "director", movieData, personData);
            server.setAssociation(request);
            fail();
        } catch (final ConcurrencyException expected) {}
    }

}
