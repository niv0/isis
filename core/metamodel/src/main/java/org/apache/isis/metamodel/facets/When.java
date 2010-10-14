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


package org.apache.isis.metamodel.facets;

import org.apache.isis.metamodel.adapter.ObjectAdapter;


public final class When extends EnumerationAbstract {

    public static When ALWAYS = new When(0, "ALWAYS", "Always");
    public static When ONCE_PERSISTED = new When(1, "ONCE_PERSISTED", "Once Persisted");
    public static When UNTIL_PERSISTED = new When(1, "UNTIL_PERSISTED", "Until Persisted");
    public static When NEVER = new When(1, "NEVER", "Never");

    private When(final int num, final String nameInCode, final String friendlyName) {
        super(num, nameInCode, friendlyName);
    }

    /**
     * Whether the state of the supplied {@link ObjectAdapter} corresponds to this 'when'.
     */
    public boolean isNowFor(final ObjectAdapter targetAdapter) {
        final boolean isTransient = targetAdapter.isTransient();
        return this == When.ALWAYS || 
               this == When.ONCE_PERSISTED && !isTransient || 
               this == When.UNTIL_PERSISTED && isTransient;
    }

}
