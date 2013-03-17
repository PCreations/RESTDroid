package fr.pcreations.labs.RESTDroid.core;

import java.util.AbstractMap.SimpleEntry;

public class StringRequestPair extends SimpleEntry<String, RESTRequest<? extends ResourceRepresentation<?>>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8527649241125190025L;

	public StringRequestPair(String url,
			RESTRequest<? extends ResourceRepresentation<?>> request) {
		super(url, request);
		// TODO Auto-generated constructor stub
	}
	
}
