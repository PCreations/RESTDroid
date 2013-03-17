package fr.pcreations.labs.RESTDroid.core;

import java.util.Iterator;
import java.util.LinkedList;


public class RequestQueue extends LinkedList<RESTRequest<? extends ResourceRepresentation<?>>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -221334223986850243L;
	
	public void addInTail(RESTRequest<? extends ResourceRepresentation<?>> request) throws NullPointerException, NoSuchFieldException {
		if(null == request)
			throw new NullPointerException("Request is null");
		if(null == request.getUrl())
			throw new NoSuchFieldException("Request url is null");
		for(Iterator<RESTRequest<? extends ResourceRepresentation<?>>> it = iterator(); it.hasNext();) {
			if(request.getUrl().equals(it.next().getUrl())) {
				it.remove();
				break;
			}
		}
		add(request);
	}
	

}
