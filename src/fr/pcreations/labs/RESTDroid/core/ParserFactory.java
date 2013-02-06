package fr.pcreations.labs.RESTDroid.core;

abstract public class ParserFactory {

	public abstract <P extends Parser<T>, T extends ResourceRepresentation<?>> P getParser(Class<T> clazz); 
	
}
