package fr.pcreations.labs.RESTDroid.core;

import java.util.ArrayList;


public abstract class FailBehavior {

	abstract public void failAction(WebService context, ArrayList<RESTRequest<? extends Resource>> failedRequests);
	
}
