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
import java.util.logging.Logger;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.errors.ErrorHandlerImpl;
import org.cruxframework.crux.plugin.errorhandler.client.remote.ErrorHandlerService;
import org.cruxframework.crux.plugin.errorhandler.client.remote.ErrorHandlerServiceAsync;
import org.cruxframework.crux.plugin.errorhandler.client.resource.SuperErrorHandlerResource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * @author Samuel Cardoso
 *
 */
public class SuperErrorHandler extends ErrorHandlerImpl
{
	private static Logger logger = Logger.getLogger(Crux.class.getName());
	
	private void checkErrorsOnServer(final ErrorHandlerServiceAsync errorHandlerService, final SuperErrorHandlerResource resources, final Throwable originalError)
    {
        errorHandlerService.getError(new AsyncCallback<ArrayList<Throwable>>() 
		{
			@Override
			public void onSuccess(ArrayList<Throwable> result) 
			{
				if(result == null)
				{
					result = new ArrayList<Throwable>();
				}
				
				if(originalError != null)
				{
					result.add(result.size(), originalError);
				}
				
				FlowPanel errorContainer = new FlowPanel();
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
					errorContainer.add(errorMsgContainer);
				}
				
				Document.get().getBody().appendChild(errorContainer.getElement());
			}
			
			@Override
			public void onFailure(Throwable exception) 
			{
				Window.alert("CRITICAL ERROR: failed to connect service to get log: " + exception.getMessage());
			}
		});
    }

	@Override
	public void handleError(String errorMessage, Throwable t) 
	{
		logger.info("error detected.");
		final ErrorHandlerServiceAsync errorHandlerService = GWT.create(ErrorHandlerService.class);
		final SuperErrorHandlerResource resources = GWT.create(SuperErrorHandlerResource.class);
		resources.css().ensureInjected();
		checkErrorsOnServer(errorHandlerService, resources, t);		
	}
}
