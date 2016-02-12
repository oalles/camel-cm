package org.apache.camel.component.cm.client;

public interface Translator<T> {

	public SMSMessage translate(T t);

}
