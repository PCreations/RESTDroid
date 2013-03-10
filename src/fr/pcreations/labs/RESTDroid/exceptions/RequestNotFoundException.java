package fr.pcreations.labs.RESTDroid.exceptions;

import java.util.UUID;

import android.util.Log;
import fr.pcreations.labs.RESTDroid.core.RestService;

public class RequestNotFoundException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9174300205456940485L;

	public RequestNotFoundException(UUID requestID) {
		Log.e(RestService.TAG, "Request with id " + requestID + "not found");
	}
	
}
