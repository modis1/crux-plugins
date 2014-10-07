/*
 * Copyright 2011 cruxframework.org
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.cruxframework.crux.plugin.gadget.launcher;


import java.io.File;
import java.lang.reflect.Constructor;

import org.cruxframework.crux.core.server.launcher.CruxJettyLauncher;
import org.eclipse.jetty.server.AbstractConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.webapp.WebAppContext;

import com.google.gwt.core.ext.ServletContainer;
import com.google.gwt.core.ext.TreeLogger;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class GadgetJettyLauncher extends CruxJettyLauncher
{
	private int shindigJettyPort = 8080;
	private String shindigBindAddress = "localhost";
	
	public void setShindigBindAddress(String bindAddress) 
	{
		this.shindigBindAddress = bindAddress;
	}

	@Override
	public ServletContainer start(TreeLogger logger, int port, File appRootDir) throws Exception
	{
		ServletContainer servletContainer = super.start(logger, port, appRootDir);
		
		TreeLogger branch = logger.branch(TreeLogger.TRACE,
				"Starting Shindig Jetty on port " + shindigJettyPort, null);

		// Setup our branch logger during startup.
		Log.setLog(new JettyTreeLogger(branch));

		AbstractConnector connector = getConnector(logger);
		if (shindigBindAddress != null) {
			connector.setHost(shindigBindAddress);
		}
		connector.setPort(shindigJettyPort);

		// Don't share ports with an existing process.
		connector.setReuseAddress(false);

		// Linux keeps the port blocked after shutdown if we don't disable this.
		connector.setSoLingerTime(0);

		Server server = new Server();
		server.addConnector(connector);

		RequestLogHandler logHandler = new RequestLogHandler();
		logHandler.setRequestLog(new JettyRequestLogger(logger, getBaseLogLevel()));
		logHandler.setHandler(getShindigApplication(logger, new File(".", "shindig.war")));
		server.setHandler(logHandler);
		server.start();
		server.setStopAtShutdown(true);
		
		// Now that we're started, log to the top level logger.
		Log.setLog(new JettyTreeLogger(logger));
		
		return servletContainer;
	}
	
	/**
	 * Create a jetty handler for each application located on the appDir directory.
	 * <p>
	 * Applications must be deployed as an .war package.
	 * @param logger devMode logger.
	 * @param appFile directory that contains the application to be installed.
	 * @return
	 */
	protected WebAppContext getShindigApplication(TreeLogger logger, File appFile) throws Exception
	{
		String webappPath = appFile.getCanonicalPath();
		
		@SuppressWarnings("unchecked")
		Class<WebAppContextWithReload> c = (Class<WebAppContextWithReload>) Class.forName("com.google.gwt.dev.shell.jetty.JettyLauncher.WebAppContextWithReload");//full package name
		//note: getConstructor() can return only public constructors,
		//you need to use 
		Constructor<WebAppContextWithReload> constructor = c.getDeclaredConstructor();

		constructor.setAccessible(true);
		WebAppContext webAppContext = constructor.newInstance(logger, webappPath, "/");
		
		//WebAppContext webAppContext = new WebAppContextWithReload(logger, webappPath, "/");
	    webAppContext.setConfigurationClasses(__dftConfigurationClasses);
		return webAppContext;
	}

	private TreeLogger.Type baseLogLevel = TreeLogger.INFO;

	private final Object privateInstanceLock = new Object();


	public void setBaseRequestLogLevel(TreeLogger.Type baseLogLevel) {
		synchronized (privateInstanceLock) {
			this.baseLogLevel = baseLogLevel;
		}
	}

	private TreeLogger.Type getBaseLogLevel() {
		synchronized (privateInstanceLock) {
			return this.baseLogLevel;
		}
	}	  
}
