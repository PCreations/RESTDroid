package fr.pcreations.labs.RESTDroid.core;

import java.io.InputStream;

import fr.pcreations.labs.RESTDroid.exceptions.ParsingException;

public interface Parser<T extends ResourceRepresentation<?>> {

	public T parseToObject(InputStream content) throws ParsingException;

	public <R extends ResourceRepresentation<?>> InputStream parseToInputStream(R resource) throws ParsingException;
	
}
