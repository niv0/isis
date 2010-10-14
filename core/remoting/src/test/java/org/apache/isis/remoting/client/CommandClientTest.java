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


package org.apache.isis.remoting.client;

import org.easymock.MockControl;
import org.apache.isis.metamodel.authentication.AuthenticationSession;
import org.apache.isis.remoting.IsisRemoteException;
import org.apache.isis.remoting.client.ClientConnection;
import org.apache.isis.remoting.data.DummyIdentityData;
import org.apache.isis.remoting.data.common.IdentityData;
import org.apache.isis.remoting.data.common.ObjectData;
import org.apache.isis.remoting.exchange.ClearAssociationRequest;
import org.apache.isis.remoting.exchange.ClearAssociationResponse;
import org.apache.isis.remoting.exchange.HasInstancesRequest;
import org.apache.isis.remoting.exchange.HasInstancesResponse;
import org.apache.isis.remoting.exchange.OidForServiceRequest;
import org.apache.isis.remoting.exchange.OidForServiceResponse;
import org.apache.isis.remoting.exchange.Request;
import org.apache.isis.remoting.exchange.RequestAbstract;
import org.apache.isis.remoting.exchange.ResponseEnvelope;
import org.apache.isis.remoting.facade.ServerFacade;
import org.apache.isis.remoting.facade.proxy.ServerFacadeProxy;
import org.apache.isis.runtime.testsystem.ProxyJunit3TestCase;
import org.apache.isis.runtime.testsystem.TestProxyOid;
import org.apache.isis.runtime.testsystem.TestProxySession;


public class CommandClientTest extends ProxyJunit3TestCase {
    private MockControl control;
    private ServerFacade serverFacade;
    private ServerFacadeProxy serverFacadeProxy;
    private TestProxySession session;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        control = MockControl.createControl(ServerFacade.class);
        serverFacade = (ServerFacade) control.getMock();
        ClientConnection connection = new ClientConnection() {
            public ResponseEnvelope executeRemotely(final Request request) {
                request.execute(serverFacade);
                return new ResponseEnvelope(request);
            }

            public void init() {}

            public void shutdown() {}
        };
        serverFacadeProxy = new ServerFacadeProxy(connection);
        session = new TestProxySession();
    }

    public void testOidForService() {
        OidForServiceRequest request = new OidForServiceRequest(session, "domain.Service");
		serverFacade.oidForService(request );
        final IdentityData data = new DummyIdentityData();
        control.setReturnValue(new OidForServiceResponse(data));

        control.replay();
        OidForServiceResponse response = serverFacadeProxy.oidForService(request);
		final IdentityData ret = response.getOidData();
        control.verify();

        assertEquals(data, ret);
    }

    public void testHasInstances() {
        HasInstancesRequest request = new HasInstancesRequest(session, "pkg.Class");
		serverFacade.hasInstances(request);
        final boolean data = true;
        control.setReturnValue(new HasInstancesResponse(data));

        control.replay();
        HasInstancesResponse response = serverFacadeProxy.hasInstances(request);
		final boolean ret = response.hasInstances();
        control.verify();

        assertEquals(data, ret);
    }

    public void testOutOfSequence() {
        ClientConnection connection = new ClientConnection() {
            public ResponseEnvelope executeRemotely(final Request request) {
                // create a response based on another request so id is different
                return new ResponseEnvelope(new RequestAbstract((AuthenticationSession) null) {
                    private static final long serialVersionUID = 1L;

                    public void execute(final ServerFacade serverFacade) {}
                });
            }

            public void init() {}

            public void shutdown() {}
        };
		serverFacadeProxy = new ServerFacadeProxy(connection);

        try {
            OidForServiceRequest request = new OidForServiceRequest(session, "domain.Service");
            serverFacadeProxy.oidForService(request);
            fail();
        } catch (final IsisRemoteException e) {
            assertTrue(e.getMessage().startsWith("Response out of sequence"));
        }
    }

    public void testClearAssociation() {
        final DummyIdentityData target = new DummyIdentityData(new TestProxyOid(1), "class 1", null);
        final DummyIdentityData associate = new DummyIdentityData(new TestProxyOid(2), "class 2", null);
        ClearAssociationRequest request = new ClearAssociationRequest(session, "fieldname", target, associate);
		serverFacade.clearAssociation(request);
        final ObjectData[] data = new ObjectData[2];
        control.setReturnValue(new ClearAssociationResponse(data));

        control.replay();
        ClearAssociationResponse response = serverFacadeProxy.clearAssociation(request);
		final ObjectData[] ret = response.getUpdates();
        control.verify();

        assertEquals(data, ret);
    }

}

