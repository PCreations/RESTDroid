package fr.pcreations.labs.RESTDroid.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * <b>Manages triggering of request's {@link FailBehavior}</b>
 * 
 * @author Pierre Criulanscy
 *
 * @version 0.8
 */
public class FailBehaviorManager {
	
	/**
	 * HashMap to store instance of FailBehavior as singletons
	 * 
	 * <p><ul>
	 * 	<li><b>key</b> : Class object of {@link FailBehavior} class</li>
	 * 	<li><b>value</b> : Instance of {@link FailBehavior} store as singleton</li>
	 * </ul></p>
	 */
	private static HashMap<Class<? extends FailBehavior>, FailBehavior> failBehaviors = new HashMap<Class<? extends FailBehavior>, FailBehavior>();

	private FailBehaviorManager() {}
	
	/**
	 * Triggers the specified {@link FailBehavior} for each failed request which defines it as behavior of failure
	 *   
	 * @param context
	 * 		Instance of {@link WebService} within which the specified request is running
	 * 
	 * @param failBehaviorClass
	 * 		The {@link FailBehavior} Class object to trigger
	 * 
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static void trigger(WebService context, Class<? extends FailBehavior> failBehaviorClass) throws NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		CopyOnWriteArrayList<RESTRequest<? extends Resource>> failedRequests = WebService.getFailedRequests();
		
		/* HashMap used to manage polymorphism if user decided to extend basic FailBehavior classes */
		HashMap<Class<? extends FailBehavior>, ArrayList<RESTRequest<? extends Resource>>> requestsToRetry = new HashMap<Class<? extends FailBehavior>, ArrayList<RESTRequest<? extends Resource>>>();
		
		for(Iterator<RESTRequest<?>> it = failedRequests.iterator(); it.hasNext();) {
			RESTRequest<?> r = it.next();
			if(r.getFailBehaviorClass().equals(failBehaviorClass) || (r.getFailBehaviorClass().getSuperclass().equals(failBehaviorClass))) {
				if(!requestsToRetry.containsKey(r.getFailBehaviorClass())) {
					requestsToRetry.put(r.getFailBehaviorClass(), new ArrayList<RESTRequest<? extends Resource>>());
				}
				requestsToRetry.get(r.getFailBehaviorClass()).add(r);
			}
		}
		
		for(Entry<Class<? extends FailBehavior>, ArrayList<RESTRequest<? extends Resource>>> entry : requestsToRetry.entrySet()) {
			/* Iteration to initialize new instance of FailBehavior if needed */
			for(Iterator<RESTRequest<? extends Resource>> it = entry.getValue().iterator(); it.hasNext();) {
				RESTRequest<? extends Resource> r = it.next();
				if(!failBehaviors.containsKey(entry.getKey())) {
					Constructor<? extends FailBehavior> ctor = r.getFailBehaviorClass().getConstructor();
					failBehaviors.put(entry.getKey(), ctor.newInstance());
				}
			}
			
			failBehaviors.get(entry.getKey()).failAction(context, entry.getValue());
		}
		
	}
	
}
