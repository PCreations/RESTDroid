package fr.pcreations.labs.RESTDroid.core;

import java.util.ArrayList;
import java.util.Iterator;

import android.util.Log;

public class RetryWhenOtherSucceedFailBehavior extends FailBehavior {

	
	public RetryWhenOtherSucceedFailBehavior() {
		super();
	}
	
	@Override
	public void failAction(WebService context, ArrayList<RESTRequest<? extends Resource>> failedRequests) {
		for(Iterator<RESTRequest<?>> it = failedRequests.iterator(); it.hasNext();) {
			RESTRequest<?> request = it.next();
			context.retryRequest(request);
		}
	}
	
}
