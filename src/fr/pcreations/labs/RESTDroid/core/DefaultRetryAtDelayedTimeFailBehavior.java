package fr.pcreations.labs.RESTDroid.core;

import java.util.ArrayList;
import java.util.Iterator;

import android.os.Handler;

/**
 * <b>Behavior to retry a request every minute by default until the request is successful</b>
 * 
 * <p>
 * If you want to change the default time value just extend this class and call {@link DefaultRetryAtDelayedTimeFailBehavior#setDelayedTime(long)} in {@link DefaultRetryAtDelayedTimeFailBehavior#failAction(WebService, ArrayList)} to set the time you want
 * </p>
 * 
 * @author Pierre Criulanscy
 * 
 * @version 0.8
 */
public class DefaultRetryAtDelayedTimeFailBehavior extends FailBehavior {

	/**
	 * Time after which the request will be re-sent
	 * 
	 * @see DefaultRetryAtDelayedTimeFailBehavior#getDelayedTime()
	 * @see DefaultRetryAtDelayedTimeFailBehavior#setDelayedTime(long)
	 */
	private long mDelayedTime = CacheManager.DURATION_ONE_MINUTE;
	
	/**
	 * @see FailBehavior#failAction(WebService, ArrayList)
	 */
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
			
		}, mDelayedTime);
	}

	/**
	 * Getter for {@link DefaultRetryAtDelayedTimeFailBehavior#mDelayedTime}
	 * 
	 * @return
	 * 		{@link DefaultRetryAtDelayedTimeFailBehavior#mDelayedTime}
	 * 
	 * @see DefaultRetryAtDelayedTimeFailBehavior#mDelayedTime
	 * @see DefaultRetryAtDelayedTimeFailBehavior#setDelayedTime(long)
	 */
	protected long getDelayedTime() {
		return mDelayedTime;
	}

	/**
	 * Setter for {@link DefaultRetryAtDelayedTimeFailBehavior#mDelayedTime}
	 * 
	 * @param delayedTime
	 * 		The new delayed time to set
	 * 
	 * @see DefaultRetryAtDelayedTimeFailBehavior#mDelayedTime
	 * @see DefaultRetryAtDelayedTimeFailBehavior#getDelayedTime()
	 */
	protected void setDelayedTime(long delayedTime) {
		mDelayedTime = delayedTime;
	}
	
	

}
