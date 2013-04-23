package fr.pcreations.labs.RESTDroid.core;

import java.util.List;

/**
 * Interface which represents a list of {@link ResourceRepresentation}
 * 
 * @author Pierre Criulanscy
 * 
 * @version 0.8
 *
 * @see Resource
 * @see ResourceRepresentation
 */
public interface ResourcesList extends Resource {

	/**
	 * Getter for the {@link ResourceRepresentation} list holding by this {@link ResourcesList}
	 * 
	 * @return
	 * 		List of {@link ResourceRepresentation}
	 */
	abstract public List<? extends ResourceRepresentation<?>> getResourcesList(); 
	
	/**
	 * Getter for the {@link ResourceRepresentation} list holding by this {@link ResourcesList}
	 * 
	 * @param list
	 * 		List of {@link ResourceRepresentation}
	 */
	abstract public void setResourcesList(List<? extends ResourceRepresentation<?>> list);
	
	/**
	 * Add a {@link ResourceRepresentation} in the list of {@link ResourceRepresentation}
	 * 
	 * @param resourceRepresentation
	 * 		The {@link ResourceRepresentation} to add in list
	 * 
	 * @return
	 * 		True if the {@link ResourceRepresentation} has been added, false otherwise
	 */
	abstract public boolean addInList(ResourceRepresentation<?> resourceRepresentation);
}
