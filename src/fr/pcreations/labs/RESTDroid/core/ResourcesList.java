package fr.pcreations.labs.RESTDroid.core;

import java.util.List;

public interface ResourcesList {

	abstract public List<ResourceRepresentation<?>> getResourcesList(); 
	
	abstract public void setResourcesList(List<ResourceRepresentation<?>> list);
	
	abstract public boolean addInList(ResourceRepresentation<?> resourceRepresentation);
}
