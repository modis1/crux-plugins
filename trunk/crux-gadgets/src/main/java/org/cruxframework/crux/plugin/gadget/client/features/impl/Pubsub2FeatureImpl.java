/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.plugin.gadget.client.features.impl;

import org.cruxframework.crux.plugin.gadget.client.features.Pubsub2Feature;

/**
 * @author Samuel Almeida Cardoso
 *
 */
public class Pubsub2FeatureImpl implements Pubsub2Feature
{
	private Pubsub2FeatureImpl()
	{
	}
	
	public native void publish(String channelName, String message)/*-{
	    $wnd.gadgets.Hub.publish(channelName, message);
    }-*/;

	public native void subscribe(String channelName, Callback callback)/*-{
	    $wnd.gadgets.Hub.subscribe(channelName, new function(topic, data, subscriberData){
	       callback.@org.cruxframework.crux.plugin.gadget.client.features.Pubsub2Feature.Callback::onMessage(Ljava/lang/String; Ljava/lang/String; Ljava/lang/String;)(topic, data, subscriberData);
	    });
    }-*/;

	public native void unsubscribe(String channelName)/*-{
	    $wnd.gadgets.Hub.unsubscribe(channelName);
    }-*/;
}
