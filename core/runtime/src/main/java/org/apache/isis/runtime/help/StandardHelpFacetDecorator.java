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


package org.apache.isis.runtime.help;

import org.apache.isis.applib.Identifier;
import org.apache.isis.metamodel.facetdecorator.FacetDecoratorAbstract;
import org.apache.isis.metamodel.facets.Facet;
import org.apache.isis.metamodel.facets.FacetHolder;
import org.apache.isis.metamodel.facets.help.HelpFacet;
import org.apache.isis.metamodel.spec.identifier.Identified;


public class StandardHelpFacetDecorator  extends FacetDecoratorAbstract implements HelpFacetDecorator {
    private final HelpManager helpManager;

    public StandardHelpFacetDecorator(final HelpManager manager) {
        helpManager = manager;
    }

    public Facet decorate(final Facet facet, FacetHolder requiredHolder) {
    	if (!(requiredHolder instanceof Identified)) {
    		return null;
    	} 

    	Identified identified = (Identified) requiredHolder;
		final Identifier identifier = identified.getIdentifier();
		
        if (facet.facetType() == HelpFacet.class) {
            final String lookupHelp = helpManager.help(identifier);
            if (lookupHelp != null) {
                HelpFacetLookup decoratingFacet = new HelpFacetLookup(lookupHelp, facet.getFacetHolder());
				return replaceFacetWithDecoratingFacet(facet, decoratingFacet, requiredHolder);
            }
        }

        return facet;
    }

    public Class<? extends Facet>[] getFacetTypes() {
        return new Class[] { HelpFacet.class };
    }
}

