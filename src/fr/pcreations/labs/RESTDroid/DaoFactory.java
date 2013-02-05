package fr.pcreations.labs.RESTDroid;

abstract public class DaoFactory {

	public abstract <D extends DaoAccess<T>, T extends ResourceRepresentation<?>> D getDao(Class<T> clazz);
	
}
