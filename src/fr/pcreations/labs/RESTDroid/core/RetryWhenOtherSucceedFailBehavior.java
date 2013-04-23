package fr.pcreations.labs.RESTDroid.core;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * <b>Behavior to retry a request when other request has succeed</b>
 * 
 * @author Pierre Criulanscy
 * 
 * @version 0.8
 */
public class RetryWhenOtherSucceedFailBehavior extends FailBehavior {

	
	public RetryWhenOtherSucceedFailBehavior() {
		super();
	}
	
	/**
	 * @see FailBehavior#failAction(WebService, ArrayList)
	 */
	@Override
	public void failAction(WebService context, ArrayList<RESTRequest<? extends Resource>> failedRequests) {
		for(Iterator<RESTRequest<?>> it = failedRequests.iterator(); it.hasNext();) {
			RESTRequest<?> request = it.next();
			context.retryRequest(request);
		}
	}
	
}
