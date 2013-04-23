package fr.pcreations.labs.RESTDroid.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import android.util.Log;

public class FailBehaviorManager {

	private FailBehaviorManager() {}
	
	public static void trigger(WebService context, Class<? extends FailBehavior> failBehaviorClass) throws NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		CopyOnWriteArrayList<RESTRequest<? extends Resource>> failedRequests = WebService.getFailedRequests();
		ArrayList<RESTRequest<? extends Resource>> requestToRetry = new ArrayList<RESTRequest<? extends Resource>>();
		for(Iterator<RESTRequest<?>> it = failedRequests.iterator(); it.hasNext();) {
			RESTRequest<?> r = it.next();
			if(r.getFailBehaviorClass().equals(failBehaviorClass)) {
				requestToRetry.add(r);
			}
		}
		
		Constructor<? extends FailBehavior> ctor = failBehaviorClass.getConstructor();
		
		ctor.newInstance().failAction(context, requestToRetry);
	}
	
}
