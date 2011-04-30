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
package net.sf.restfulobjects.viewer.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.junit.Test;


public class StringUtilTest {

    @Test
    public void shouldReturnEmptyArrayWhenNull() {
        final List<String> args = StringUtil.getArgs(null);
        assertThat(args.size(), is(equalTo(0)));
    }

    @Test
    public void shouldReturnEmptyArrayWhenEmptyString() {
        final List<String> args = StringUtil.getArgs(newInputStream(""));
        assertThat(args.size(), is(equalTo(0)));
    }

    @Test
    public void shouldReturnArrayOfCorrectSizeWhenTwoArgs() {
        final List<String> args = StringUtil.getArgs(newInputStream("arg0=foo&arg1=bar"));
        assertThat(args.size(), is(equalTo(2)));
    }

    private ByteArrayInputStream newInputStream(final String string) {
        ByteArrayInputStream bais;
        bais = new ByteArrayInputStream(string.getBytes());
        return bais;
    }

    @Test
    public void shouldReturnFirstArg() {
        final ByteArrayInputStream bais = new ByteArrayInputStream("arg0=foo&arg1=bar".getBytes());
        final List<String> args = StringUtil.getArgs(bais);
        assertThat(args.get(0), is(equalTo("foo")));
    }

    @Test
    public void shouldReturnSecondArg() {
        final ByteArrayInputStream bais = new ByteArrayInputStream("arg0=foo&arg1=bar".getBytes());
        final List<String> args = StringUtil.getArgs(bais);
        assertThat(args.get(1), is(equalTo("bar")));
    }

}
