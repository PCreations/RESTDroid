package fr.pcreations.labs.RESTDroid.core;

import java.util.List;

public interface ResourcesList {

	abstract public List<? extends ResourceRepresentation<?>> getResourcesList(); 
	
	abstract public void setResourcesList(List<? extends ResourceRepresentation<?>> list);
}
