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


package org.apache.isis.metamodel.facets.hide;

import org.apache.isis.metamodel.adapter.ObjectAdapter;
import org.apache.isis.metamodel.facets.Facet;
import org.apache.isis.metamodel.interactions.HidingInteractionAdvisor;


/**
 * Hide a property, collection or action based on the state of the target {@link ObjectAdapter object}.
 * 
 * <p>
 * In the standard [[NAME]] Programming Model, corresponds to invoking the <tt>hideXxx</tt> support
 * method for the member.
 */
public interface HideForContextFacet extends Facet, HidingInteractionAdvisor {

    public String hiddenReason(ObjectAdapter object);

}

