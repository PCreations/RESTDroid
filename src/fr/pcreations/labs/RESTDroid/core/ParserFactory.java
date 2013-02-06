package fr.pcreations.labs.RESTDroid.core;

import java.util.HashMap;

abstract public class ParserFactory {

	protected HashMap<Class<? extends ResourceRepresentation<?>>, Parser<? extends ResourceRepresentation<?>>> mParserMap;
	
	public abstract <P extends Parser<T>, T extends ResourceRepresentation<?>> P getParser(Class<T> clazz); 
	
}
