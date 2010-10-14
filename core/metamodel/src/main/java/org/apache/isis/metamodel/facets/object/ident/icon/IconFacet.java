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


package org.apache.isis.metamodel.facets.object.ident.icon;

import org.apache.isis.metamodel.adapter.ObjectAdapter;
import org.apache.isis.metamodel.facets.Facet;
import org.apache.isis.metamodel.facets.object.bounded.BoundedFacet;
import org.apache.isis.metamodel.facets.object.ident.plural.PluralFacet;
import org.apache.isis.metamodel.facets.object.ident.title.TitleFacet;


/**
 * Mechanism for obtaining the name of the icon for <i>this instance</i> of a class.
 * 
 * <p>
 * Typically a single icon is used for every instance of a class (for example, by placing an appropriately
 * named image file into a certain directory). This facet allows the icon to be changed on an
 * instance-by-instance basis. For example, the icon might be adapted with an overlay to represent its state
 * through some well-defined lifecycle (eg pending approval, approved, rejected). Alternatively a
 * {@link BoundedFacet bounded} class might have completely different icons for its instances (eg Visa,
 * Mastercard, Amex).
 * 
 * <p>
 * In the standard [[NAME]] Programming Model, typically corresponds to a method named <tt>iconName</tt>.
 * 
 * @see TitleFacet
 * @see PluralFacet
 */
public interface IconFacet extends Facet {

    public String iconName(final ObjectAdapter object);
}
