/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.plugin.errorhandler.client;

import java.util.ArrayList;

import org.cruxframework.crux.plugin.errorhandler.client.remote.ErrorHandlerService;
import org.cruxframework.crux.plugin.errorhandler.client.remote.ErrorHandlerServiceAsync;
import org.cruxframework.crux.plugin.errorhandler.client.resource.SuperErrorHandlerResource;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 *<pre>
   {@code
   //In order to make it work, insert this in a client pom project: 
   <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>aspectj-maven-plugin</artifactId>
        <version>1.5</version>
        <configuration>
            <weaveDependencies>
                <weaveDependency>
                     <groupId>org.cruxframework</groupId>
                     <artifactId>super-error-handler</artifactId>
                </weaveDependency>
                <weaveDependency>
                    <groupId>com.google.gwt</groupId>
                    <artifactId>gwt-dev</artifactId>
                </weaveDependency>
            </weaveDependencies>
            <complianceLevel>1.6</complianceLevel>
            <source>1.6</source>
            <target>1.6</target>
            <Xlint>ignore</Xlint>
        </configuration>
        <executions>
            <execution>
                <phase>process-sources</phase>
                <goals>
                    <goal>compile</goal>
                </goals>
            </execution>
        </executions>
    </plugin>
    }
    </pre>
 * 
 * @author Samuel Cardoso
 *
 */
public class SuperErrorHandler implements EntryPoint
{
	@Override
    public void onModuleLoad()
    {
		final ErrorHandlerServiceAsync errorHandlerService = GWT.create(ErrorHandlerService.class);
		final SuperErrorHandlerResource resources = GWT.create(SuperErrorHandlerResource.class);
		resources.css().ensureInjected();
		
		Scheduler.get().scheduleFixedDelay(new RepeatingCommand()
		{
			@Override
			public boolean execute()
			{
				checkErrorsOnServer(errorHandlerService, resources);
				return true;
			}
		}, 10000);
    }

	private void checkErrorsOnServer(final ErrorHandlerServiceAsync errorHandlerService, final SuperErrorHandlerResource resources)
    {
        errorHandlerService.getError(new AsyncCallback<ArrayList<Throwable>>() 
		{
			@Override
			public void onSuccess(ArrayList<Throwable> result) 
			{
				if(result == null || result.isEmpty())
				{
					return;
				}
				
				for(Throwable throwable : result)
				{
					FlowPanel errorMsgContainer = new FlowPanel();
					errorMsgContainer.setStyleName(resources.css().errorMsgContainer());
					
					FlowPanel errorMsgContainerHeader = new FlowPanel();
					errorMsgContainerHeader.setStyleName(resources.css().errorMsgContainerHeader());
					Label labelHeader = new Label();
					labelHeader.setText(throwable.getMessage());
					errorMsgContainerHeader.add(labelHeader);
					errorMsgContainer.add(errorMsgContainerHeader);
					
					StringBuffer sb = new StringBuffer();
					if(throwable.getStackTrace() != null)
					{
						for(StackTraceElement stackTraceElement : throwable.getStackTrace())
						{
							sb.append(stackTraceElement.toString() + "\n");
						}

						FlowPanel errorMsgContainerBody = new FlowPanel();
						errorMsgContainerBody.setStyleName(resources.css().errorMsgContainerBody());
						Label labelStack = new Label();
						labelStack.setText(sb.toString());
						errorMsgContainerBody.add(labelStack);
						errorMsgContainer.add(errorMsgContainerBody);
					}
					Document.get().getBody().appendChild(errorMsgContainer.getElement());
				}
			}
			
			@Override
			public void onFailure(Throwable exception) 
			{
				Window.alert("CRITICAL ERROR: failed to connect service to get log: " + exception.getMessage());
			}
		});
    }
}
