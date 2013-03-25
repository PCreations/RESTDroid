package fr.pcreations.labs.RESTDroid.exceptions;

import android.util.Log;
import fr.pcreations.labs.RESTDroid.core.RestService;

public class ParserFactoryNotInitializedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3549902032941972617L;

	public ParserFactoryNotInitializedException() {
		super();
		Log.e(RestService.TAG, "ParserFactory not initialized");
	}
	
}
