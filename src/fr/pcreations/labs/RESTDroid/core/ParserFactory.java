package fr.pcreations.labs.RESTDroid.core;

import java.util.HashMap;

/**
 * <b>Base class for ParserFactory that handle creation and access of Parser as Singleton</b>
 * 
 * @author Pierre Criulanscy
 * 
 * @version 0.5
 */
abstract public class ParserFactory {

	/**
	 * HashMap to store Parser as Singleton
	 * <p>
	 * <ul>
	 * <li><b>key</b> : {@link ResourceRepresentation} Class object</li>
	 * <li><b>value</b> : {@link Parser} instance</li>
	 * </ul>
	 * </p>
	 */
	protected HashMap<Class<? extends ResourceRepresentation<?>>, Parser<? extends ResourceRepresentation<?>>> mParserMap;
	
	/**
	 * Constructor
	 */
	protected ParserFactory() {
		mParserMap = new HashMap<Class<? extends ResourceRepresentation<?>>, Parser<? extends ResourceRepresentation<?>>>();
	}
	
	/**
	 * Method to retrieve Parser. Parser should be store in mParserMap in order to store parser as singleton :
	 <p>
	 <pre>
if(mParserMap.containsKey(clazz))
	return (P) mParserMap.get(clazz);

mParserMap.put(clazz, //your specific parser here);
return (P) mParserMap.get(clazz);
	 </pre>
	 </p>
	 *
	 * @param clazz
	 * 		The {@link ResourceRepresentation} class
	 * 
	 * @return
	 * 		Instance of {@link Parser}
	 */
	public abstract <P extends Parser<T>, T extends ResourceRepresentation<?>> P getParser(Class<T> clazz); 
	
}
