package fr.pcreations.labs.RESTDroid.exceptions;

import android.util.Log;
import fr.pcreations.labs.RESTDroid.RestService;

public class DaoFactoryNotInitializedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -828011978093234679L;

	public DaoFactoryNotInitializedException() {
		super();
		Log.e(RestService.TAG, "DaoFactory not initialized");
	}
}
