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
package org.apache.isis.viewer.bdd.common.fixtures.perform.checkthat;



/**
 * Parameterizes {@link ThatSubcommand}s.
 */
public enum AssertsContainment {

    CONTAINS("contains", true, "(does not contain)"),
    DOES_NOT_CONTAIN(
            "does not contain", false, "(contains)");

    private final String key;
    private final boolean contains;
    private final String errorMsgIfNotSatisfied;

    private AssertsContainment(String key, boolean contains, String errorMsgIfNotSatisfied) {
        this.key = key;
        this.contains = contains;
        this.errorMsgIfNotSatisfied = errorMsgIfNotSatisfied;
    }

    public String getKey() {
        return key;
    }

    public boolean doesContain() {
        return contains;
    }

    public boolean isSatisfiedBy(final boolean contains) {
        return this.contains == contains;
    }

    public String getErrorMsgIfNotSatisfied() {
        return errorMsgIfNotSatisfied;
    }
}