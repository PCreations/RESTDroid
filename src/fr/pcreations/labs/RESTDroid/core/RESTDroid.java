package fr.pcreations.labs.RESTDroid.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import android.content.Context;
import android.os.Bundle;
import fr.pcreations.labs.RESTDroid.exceptions.RESTDroidNotInitializedException;

public class RESTDroid {

	private static RESTDroid instance;
	private Context mContext;
	private HashMap<Class<? extends WebService>, WebService> mWebServices;
	
	private RESTDroid(Context context) {
		mContext = context;
		mWebServices = new HashMap<Class<? extends WebService>, WebService>();
	}
	
	static public void init(Context context) {
		if(null == instance)
			instance = new RESTDroid(context);
	}
	
	static public RESTDroid getInstance() throws RESTDroidNotInitializedException {
		if(instance != null)
			return instance;
		throw new RESTDroidNotInitializedException();
	}
	
	@SuppressWarnings("unchecked")
	public <W extends WebService> WebService getWebService(Class<W> clazz) throws RESTDroidNotInitializedException {
		if(null == instance)
			throw new RESTDroidNotInitializedException();
		if(instance.mWebServices.containsKey(clazz))
			return instance.mWebServices.get(clazz);
		
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
