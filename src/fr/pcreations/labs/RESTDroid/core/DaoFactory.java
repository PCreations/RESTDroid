package fr.pcreations.labs.RESTDroid.core;

import java.util.HashMap;

abstract public class DaoFactory {
	
	protected HashMap<Class<? extends ResourceRepresentation<?>>, DaoAccess<? extends ResourceRepresentation<?>>> mDaos;

	public DaoFactory() {
		mDaos = new HashMap<Class<? extends ResourceRepresentation<?>>, DaoAccess<? extends ResourceRepresentation<?>>>();
	}
	
	public abstract <D extends DaoAccess<T>, T extends ResourceRepresentation<?>> D getDao(Class<T> clazz);
	
}
