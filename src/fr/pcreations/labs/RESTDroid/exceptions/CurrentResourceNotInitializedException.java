package fr.pcreations.labs.RESTDroid.exceptions;

import android.util.Log;
import fr.pcreations.labs.RESTDroid.core.RestService;

public class CurrentResourceNotInitializedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2708793938890322023L;
	
	public CurrentResourceNotInitializedException() {
		super();
		Log.e(RestService.TAG, "FLAG_RESOURCE is set to ON but current resource is not initialized");
	}
	
}
