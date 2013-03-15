package fr.pcreations.labs.RESTDroid.core;

import java.util.List;

public interface ResourcesList extends Resource {

	abstract public List<? extends ResourceRepresentation<?>> getResourcesList(); 
	
	abstract public void setResourcesList(List<? extends ResourceRepresentation<?>> list);
	
	abstract public boolean addInList(ResourceRepresentation<?> resourceRepresentation);
}
