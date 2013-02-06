package fr.pcreations.labs.RESTDroid.exceptions;

import android.util.Log;
import fr.pcreations.labs.RESTDroid.core.RestService;

public class DatabaseManagerNotInitializedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3189957130125110077L;

	public DatabaseManagerNotInitializedException() {
		super();
		Log.e(RestService.TAG, "DatabaseManager not initialized");
	}

}
