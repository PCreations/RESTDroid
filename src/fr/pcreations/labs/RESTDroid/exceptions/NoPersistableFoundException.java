package fr.pcreations.labs.RESTDroid.exceptions;

import android.util.Log;
import fr.pcreations.labs.RESTDroid.core.RestService;

public class NoPersistableFoundException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7319543087703918355L;

	public NoPersistableFoundException(String msg) {
		Log.e(RestService.TAG, msg);
	}
	
}
