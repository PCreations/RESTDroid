package fr.pcreations.labs.RESTDroid.core;

import java.util.HashMap;

/**
 * <b>Base class for DaoFactory that handle creation and access of Persistable classes as Singleton</b>
 * 
 * @author Pierre Criulanscy
 * 
 * @version 0.6.1
 */
abstract public class PersistableFactory {
	
	/**
	 * HashMap to store Persistable as Singleton
	 * <p>
	 * <ul>
	 * <li><b>key</b> : {@link ResourceRepresentation} Class object</li>
	 * <li><b>value</b> : {@link Persistable} instance</li>
	 * </ul>
	 * </p>
	 */
	protected HashMap<Class<? extends ResourceRepresentation<?>>, Persistable<? extends ResourceRepresentation<?>>> mPersistables;

	/**
	 * Constructor
	 */
	public PersistableFactory() {
		mPersistables = new HashMap<Class<? extends ResourceRepresentation<?>>, Persistable<? extends ResourceRepresentation<?>>>();
	}
	
	/**
	 * Method to retrieve Persistable. Persistable should be store in mDaos in order to store Persistable as singleton :
	 * 
	 * <p><pre>
	 * if(mPersistables.containsKey(clazz)) {
	return (P) mPersistables.get(clazz);
}
persistable = (P) //You're logic to retrieve specific Persistable
mPersistables.put(clazz, persistable);
return persistable;
	</pre></p>
	 * 
	 * @param clazz
	 * 		Class object of the {@link ResourceRepresentation} in order to get the corresponding Persistable
	 * 
	 * @return
	 * 		The Persistable instance
	 * 
	 * @see Persistable
	 * @see ResourceRepresentation
	 */
	public abstract <P extends Persistable<T>, T extends ResourceRepresentation<?>> P getPersistable(Class<T> clazz);
	
}
