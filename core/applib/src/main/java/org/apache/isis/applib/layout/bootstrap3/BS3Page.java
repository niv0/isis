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
package org.apache.isis.applib.layout.bootstrap3;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This is the top-level region for the domain object's properties, collections and actions.  It simply consists
 * of a number of rows.
 *
 * <p>
 *     Note that the title's
 * </p>
 */
@XmlRootElement(
        name = "page"
)
@XmlType(
        name = "page"
        , propOrder = {
            "rows"
        }
)
public class BS3Page {

    private List<BS3Row> rows = new ArrayList<BS3Row>(){{
        add(new BS3Row());
    }};

    // no wrapper
    @XmlElement(name = "row", required = true)
    public List<BS3Row> getRows() {
        return rows;
    }

    public void setRows(final List<BS3Row> rows) {
        this.rows = rows;
    }



}