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


package org.apache.isis.remoting.data;

import org.apache.isis.metamodel.adapter.oid.Oid;
import org.apache.isis.metamodel.adapter.version.Version;
import org.apache.isis.remoting.data.common.CollectionData;
import org.apache.isis.remoting.data.common.CollectionDataImpl;
import org.apache.isis.remoting.data.common.EncodableObjectData;
import org.apache.isis.remoting.data.common.EncodableObjectDataImpl;
import org.apache.isis.remoting.data.common.IdentityData;
import org.apache.isis.remoting.data.common.IdentityDataImpl;
import org.apache.isis.remoting.data.common.NullData;
import org.apache.isis.remoting.data.common.NullDataImpl;
import org.apache.isis.remoting.data.common.ObjectData;
import org.apache.isis.remoting.data.common.ObjectDataImpl;
import org.apache.isis.remoting.data.common.ReferenceData;

public class DataFactoryDefault implements DataFactory {

    public NullData createNullData(final String type) {
        return new NullDataImpl(type);
    }

    public IdentityData createIdentityData(final String type, final Oid oid, final Version version) {
    	return new IdentityDataImpl(type, oid, version);
    }
    
    public EncodableObjectData createValueData(final String type, final String encodedValue) {
    	return new EncodableObjectDataImpl(type, encodedValue);
    }
    
    public ObjectData createObjectData(final String type, final Oid oid, final boolean hasCompleteData, final Version version) {
        return new ObjectDataImpl(oid, type, hasCompleteData, version);
    }

    public CollectionData createCollectionData(
    		final String collectionType,
    		final String elementType,
    		final Oid oid,
    		final ReferenceData[] elements,
    		final boolean hasAllElements,
    		final Version version) {
    	return new CollectionDataImpl(oid, collectionType, elementType, elements, hasAllElements, version);
    }
    

}
