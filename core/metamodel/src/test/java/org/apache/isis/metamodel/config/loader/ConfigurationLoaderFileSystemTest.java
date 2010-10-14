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


package org.apache.isis.metamodel.config.loader;

import junit.framework.TestCase;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.isis.commons.exceptions.IsisException;
import org.apache.isis.metamodel.config.ConfigurationBuilderFileSystem;
import org.apache.isis.metamodel.config.IsisConfiguration;
import org.apache.isis.metamodel.config.NotFoundPolicy;


public class ConfigurationLoaderFileSystemTest extends TestCase {
    ConfigurationBuilderFileSystem loader;

    @Override
    protected void setUp() throws Exception {
        Logger.getRootLogger().setLevel(Level.OFF);
        loader = new ConfigurationBuilderFileSystem("src/test/config");
    }

    public void testDefaultConfiguration() {
        final IsisConfiguration configuration = loader.getConfiguration();
        assertEquals("one", configuration.getString("properties.example"));
    }

    public void testDefaultConfigurationTrailingWhitespace() {
        final IsisConfiguration configuration = loader.getConfiguration();
        assertEquals("in-memory", configuration.getString("properties.trailingWhitespace"));
    }

    public void testAddConfiguration() {
        loader.addConfigurationResource("another.properties", NotFoundPolicy.FAIL_FAST);
        final IsisConfiguration configuration = loader.getConfiguration();
        assertEquals("added", configuration.getString("additional.example"));
    }

    public void testAddedConfigurationOveridesEarlierProperties() {
        loader.addConfigurationResource("another.properties", NotFoundPolicy.FAIL_FAST);
        final IsisConfiguration configuration = loader.getConfiguration();
        assertEquals("two", configuration.getString("properties.example"));
    }

    public void testAddedConfigurationFailsWhenFileNotFound() {
        try {
            loader.addConfigurationResource("unfound.properties", NotFoundPolicy.FAIL_FAST);
            loader.getConfiguration();
            fail();
        } catch (final IsisException expected) {}
    }

    public void testAddedConfigurationIgnoreUnfoundFile() {
        loader.addConfigurationResource("unfound.properties", NotFoundPolicy.CONTINUE);
        loader.getConfiguration();
    }

    public void testAddProperty() throws Exception {
        loader.add("added.property", "added by code");
        final IsisConfiguration configuration = loader.getConfiguration();
        assertEquals("added by code", configuration.getString("added.property"));
    }

    public void testIncludeSystemProperty() throws Exception {
        loader.setIncludeSystemProperties(true);
        final IsisConfiguration configuration = loader.getConfiguration();
        assertEquals(System.getProperty("os.name"), configuration.getString("os.name"));
    }
}

