package fr.pcreations.labs.RESTDroid.core;

import android.content.Context;
import fr.pcreations.labs.RESTDroid.exceptions.RESTDroidNotInitializedException;

public class RESTDroid {

	private static RESTDroid instance;
	private static Context mContext;
	
	private RESTDroid(Context context) {
		mContext = context;
	}
	
	static public void init(Context context) {
		if(null != instance)
			instance = new RESTDroid(context);
	}
	
	static public RESTDroid getInstance() throws RESTDroidNotInitializedException {
		if(instance != null)
			return instance;
		throw new RESTDroidNotInitializedException();
	}
	
}
