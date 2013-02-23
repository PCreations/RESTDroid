package fr.pcreations.labs.RESTDroid.core;

import java.sql.SQLException;
import java.util.List;


/**
 * <b>Interface which represents methods to implement for object persistency</b>
 * 
 * @author Pierre Criulanscy
 *
 * @param <T>
 * 		A class derivated from {@link ResourceRepresentation}
 * 
 * @version 0.6.1
 * 
 * @see ResourceRepresentation
 */
public interface Persistable<T extends ResourceRepresentation<?>> {

	/**
	 * This method has to create a new resource or update it if already exists.
	 * 
	 * @param resource
	 * 		The resource to create or update
	 * 
	 * @throws SQLException if a sql error occurs.
	 * 
	 */
	abstract public void updateOrCreate(T resource)throws Exception;
	
	/**
	 * This method has to return the resource corresponding to the given ID. ID is a parametrized type corresponding to the {@link ResourceRepresentation} id field type
	 * 
	 * @param resourceId
	 * 		The id of the resource
	 * 
	 * @return
	 * 		The resource corresponding to the given ID
	 * 
	 * @throws SQLException if a sql error occurs
	 */
	abstract public <ID> T findById(ID resourceId) throws Exception;
	
	/**
	 * This method has to return all resources of the parameterized type defined by the interface
	 * 
	 * @return
	 * 		A list of all the resources
	 * 
	 * @throws SQLException if a sql error occurs
	 */
	abstract public List<T> queryForAll() throws Exception;
	
	/**
	 * This method has to delete the specified resource
	 * 
	 * @param resource
	 * 		The resource to delete
	 * 
	 * @return
	 * 		Number of resource actually deleted (should be 1)
	 * 
	 * @throws SQLException if a sql error occurs
	 */
	abstract public int deleteResource(T resource) throws Exception;
	
	/**
	 * This method has to update the specified resource
	 * 
	 * @param resource
	 * 		The resource to update
	 * 
	 * @return
	 * 		Number of resource actually updated (should be 1)
	 * 
	 * @throws SQLException if a sql error occurs
	 */
	abstract public int updateResource(T resource) throws Exception;
	
}
