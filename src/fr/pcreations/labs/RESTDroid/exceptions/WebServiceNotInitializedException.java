package fr.pcreations.labs.RESTDroid.exceptions;

import android.util.Log;
import fr.pcreations.labs.RESTDroid.core.RestService;

public class WebServiceNotInitializedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3189957130125110077L;

	public WebServiceNotInitializedException() {
		super();
		Log.e(RestService.TAG, "Web service not initialized");
	}

}
