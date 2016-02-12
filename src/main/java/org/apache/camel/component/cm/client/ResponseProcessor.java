package org.apache.camel.component.cm.client;


public interface ResponseProcessor {

	public void processResponse(CMResponse cmResponse);
}
