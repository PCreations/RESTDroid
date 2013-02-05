package fr.pcreations.labs.RESTDroid.exceptions;

import android.util.Log;
import fr.pcreations.labs.RESTDroid.RestService;

public class ProcessorNotInitializedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4499377368099149904L;

	public ProcessorNotInitializedException() {
		Log.e(RestService.TAG, "Processor not ininitialized in RestService");
	}
}
