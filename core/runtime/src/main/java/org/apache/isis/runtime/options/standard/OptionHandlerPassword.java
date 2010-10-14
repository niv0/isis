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


package org.apache.isis.runtime.options.standard;

import static org.apache.isis.runtime.runner.Constants.PASSWORD_LONG_OPT;
import static org.apache.isis.runtime.runner.Constants.PASSWORD_OPT;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.isis.metamodel.config.ConfigurationBuilder;
import org.apache.isis.runtime.runner.BootPrinter;
import org.apache.isis.runtime.runner.Constants;
import org.apache.isis.runtime.runner.options.OptionHandlerAbstract;
import org.apache.isis.runtime.system.SystemConstants;

public class OptionHandlerPassword extends OptionHandlerAbstract {

	private String password;

	public OptionHandlerPassword() {
		super();
	}

	@SuppressWarnings("static-access")
	public void addOption(Options options) {
		Option option = OptionBuilder.withArgName("password").hasArg().withLongOpt(PASSWORD_LONG_OPT).withDescription(
        "password to automatically log in with").create(PASSWORD_OPT);
		options.addOption(option);		
	}

	public boolean handle(CommandLine commandLine, BootPrinter bootPrinter, Options options) {
		password = commandLine.getOptionValue(Constants.PASSWORD_OPT);
		return true;		
	}
	
	public void primeConfigurationBuilder(
			ConfigurationBuilder configurationBuilder) {
		configurationBuilder.add(SystemConstants.PASSWORD_KEY, password);
	}


	public String getPassword() {
		return password;
	}
	
}
