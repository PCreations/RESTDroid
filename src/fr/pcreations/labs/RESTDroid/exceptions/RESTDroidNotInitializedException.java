package fr.pcreations.labs.RESTDroid.exceptions;

import android.util.Log;
import fr.pcreations.labs.RESTDroid.core.RestService;

public class RESTDroidNotInitializedException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2360362049434847399L;

	public RESTDroidNotInitializedException() {
		Log.e(RestService.TAG, "RESTDroid is not initialized");
	}
	
}
