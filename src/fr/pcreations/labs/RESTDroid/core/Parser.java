package fr.pcreations.labs.RESTDroid.core;

import java.io.InputStream;

import fr.pcreations.labs.RESTDroid.exceptions.ParsingException;

/**
 * <b>Interface which represents Parser</b>
 * 
 * @author Pierre Criulanscy
 *
 * @param <T>
 * 		The Class object of {@link ResourceRepresentation} which is parsed with this parser
 * 
 * @version 0.5
 */
public interface Parser<T extends ResourceRepresentation<?>> {

	/**
	 * Use this method to return parsed {@link ResourceRepresentation} from InputStream
	 * 
	 * @param content
	 * 		The InputStream representing {@link ResourceRepresentation}
	 * 
	 * @return
	 * 		Parsed {@link ResourceRepresentation}
	 * 
	 * @throws ParsingException
	 */
	public T parseToObject(InputStream content) throws ParsingException;

	/**
	 * Use this method to return InputStream parsed from {@link ResourceRepresentation}
	 * 
	 * @param resource
	 * 		The {@link ResourceRepresentation} which will be parsed to InputStream
	 * 
	 * @return
	 * 		InputStream representing the {@link ResourceRepresentation}
	 * 
	 * @throws ParsingException
	 */
	public <R extends ResourceRepresentation<?>> InputStream parseToInputStream(R resource) throws ParsingException;
	
}
