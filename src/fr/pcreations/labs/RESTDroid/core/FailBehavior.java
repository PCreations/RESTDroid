package fr.pcreations.labs.RESTDroid.core;

import java.util.ArrayList;

/**
 * <b>FailBehavior lets you implement a behavior for request failure. This behavior is managed by {@link FailBehaviorManager}</b>
 * 
 * @author Pierre Criulanscy
 *
 * @version 0.8
 */
public abstract class FailBehavior {

	/**
	 * Action to perform for all requests which defined this FailBehavior as behavior of failure
	 * 
	 * @param context
	 * 		Instance of {@link WebService} within which the specified request is running
	 * 
	 * @param failedRequests
	 * 		List of all failed requests
	 */
	abstract public void failAction(WebService context, ArrayList<RESTRequest<? extends Resource>> failedRequests);
	
}
