package fr.pcreations.labs.RESTDroid.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;
import fr.pcreations.labs.RESTDroid.exceptions.RESTDroidNotInitializedException;

/**
 * <b>RESTDroid is an open source library to handle calls to REST webservice with data caching and persistence features</b>
 * @author Pierre Criulanscy
 * @version 0.5
 */
public class RESTDroid {

	/**
	 * Unique instance of RESTDroid class
	 * 
	 * @see RESTDroid#init(Context)
	 * @see RESTDroid#getInstance()
	 */
	private static RESTDroid instance;
	
	/**
	 * Actual application context
	 * @see RESTDroid#init(Context)
	 */
	private Context mContext;
	
	/**
	 * HashMap to store WebService class as singleton
	 * 
	 * <p>
	 * <ul>
	 * <li><b>key</b> : Class<? extends WebService></li>
	 * <li><b>value</b> : WebService instance</li>
	 * </ul>
	 * </p>
	 * 
	 * @see WebService
	 * @see RESTDroid#getWebService(Class)
	 */
	private HashMap<Class<? extends WebService>, WebService> mWebServices;
	
	
	/**
	 * Private constructor for singleton
	 * 
	 * @param context
	 * 
	 * @see RESTDroid#init(Context)
	 * @see RESTDroid#getInstance()
	 */
	private RESTDroid(Context context) {
		mContext = context;
		mWebServices = new HashMap<Class<? extends WebService>, WebService>();
	}
	
	
	/**
	 * Initializer for RESTDroid singleton. Call it in Activity.onStart()
	 *  
	 * @param context
	 * 		The actual application context
	 * 
	 * @see RESTDroid#getInstance()
	 */
	static public void init(Context context) {
		if(null == instance)
			instance = new RESTDroid(context);
	}
	
	/**
	 * Gives the unique RESTDroid instance
	 * 
	 * @see RESTDroid#init(Context)
	 * @see RESTDroid#instance
	 * 
	 * @throws RESTDroidNotInitializedException if RESTDroid hasn't been initialized with {@link RESTDroid#init(Context)}
	 */
	static public RESTDroid getInstance() throws RESTDroidNotInitializedException {
		if(instance != null)
			return instance;
		throw new RESTDroidNotInitializedException();
	}
	
	/**
	 * 
	 * @param clazz
	 * 		Class object of WebService class to retrieve
	 * 
	 * @return Instance of WebService class
	 * 
	 * @throws RESTDroidNotInitializedException
	 * 
	 * @see WebService
	 */
	@SuppressWarnings("unchecked")
	public <W extends WebService> WebService getWebService(Class<W> clazz) throws RESTDroidNotInitializedException {
		if(null == instance)
			throw new RESTDroidNotInitializedException();
		if(instance.mWebServices.containsKey(clazz)) {
			Log.i("debug", "webservice ALREADY exists");
			return instance.mWebServices.get(clazz);
		}
		Log.i("debug", "webservice doesn't exists");
		Class<W> _tempClass;
		try {
			_tempClass = (Class<W>) Class.forName(clazz.getName());
			Constructor<W> ctor = _tempClass.getDeclaredConstructor(Context.class);  
			instance.mWebServices.put(clazz, ctor.newInstance(instance.mContext));
			return instance.mWebServices.get(clazz);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
}
