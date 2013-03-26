package fr.pcreations.labs.RESTDroid.exceptions;

import android.util.Log;
import fr.pcreations.labs.RESTDroid.core.RestService;

public class PersistableFactoryNotInitializedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -828011978093234679L;

	public PersistableFactoryNotInitializedException() {
		super();
		Log.e(RestService.TAG, "DaoFactory not initialized");
	}
}
