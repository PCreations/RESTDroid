package fr.pcreations.labs.RESTDroid.core;

import java.util.HashMap;

/**
 * <b>Base class for DaoFactory that handle creation and access of Dao as Singleton</b>
 * 
 * @author Pierre Criulanscy
 * 
 * @version 0.5
 */
abstract public class DaoFactory {
	
	/**
	 * HashMap to store Dao as Singleton
	 * <p>
	 * <ul>
	 * <li><b>key</b> : {@link ResourceRepresentation} Class object</li>
	 * <li><b>value</b> : {@link DaoAccess} instance</li>
	 * </ul>
	 * </p>
	 */
	protected HashMap<Class<? extends ResourceRepresentation<?>>, DaoAccess<? extends ResourceRepresentation<?>>> mDaos;

	/**
	 * Constructor
	 */
	public DaoFactory() {
		mDaos = new HashMap<Class<? extends ResourceRepresentation<?>>, DaoAccess<? extends ResourceRepresentation<?>>>();
	}
	
	/**
	 * Method to retrieve Dao. Dao should be store in mDaos in order to store daos as singleton :
	 * 
	 * <p><pre>
	 * if(mDaos.containsKey(clazz)) {
	return (D) mDaos.get(clazz);
}
dao = (D) //You're logic to retrieve specific Dao
mDaos.put(clazz, dao);
return dao;
	</pre></p>
	 * 
	 * @param clazz
	 * 		Class object of the {@link ResourceRepresentation} in order to get the corresponding dao
	 * 
	 * @return
	 * 		The Dao instance
	 * 
	 * @see DaoAccess
	 * @see ResourceRepresentation
	 */
	public abstract <D extends DaoAccess<T>, T extends ResourceRepresentation<?>> D getDao(Class<T> clazz);
	
}
