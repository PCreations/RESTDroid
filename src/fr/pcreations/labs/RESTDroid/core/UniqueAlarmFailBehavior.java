package fr.pcreations.labs.RESTDroid.core;

import java.util.ArrayList;
import java.util.Iterator;

import android.os.Handler;

public class UniqueAlarmFailBehavior extends FailBehavior {

	@Override
	public void failAction(WebService context, ArrayList<RESTRequest<? extends Resource>> failedRequests) {
		final ArrayList<RESTRequest<? extends Resource>> finalFailedRequests = failedRequests;
		final WebService finalContext = context;
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				for(Iterator<RESTRequest<?>> it = finalFailedRequests.iterator(); it.hasNext();) {
					RESTRequest<?> request = it.next();
					finalContext.retryRequest(request);
				}
			}
			
		}, CacheManager.DURATION_ONE_MINUTE);
	}

}
