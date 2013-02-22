package fr.pcreations.labs.RESTDroid.core;

import java.util.HashMap;
import java.util.Map.Entry;

import android.util.Log;

public class RequestListeners {

	/**
	 * HashMap of onStartedRequestListener. Fires if value is set to true.
	 * 
	 * @see OnStartedRequestListener
	 * @see ListenerState
	 * @see RESTRequest#getOnStartedRequestListener()
	 * @see RESTRequest#setOnStartedRequestListener(OnStartedRequestListener)
	 */
	protected transient HashMap<OnStartedRequestListener, ListenerState> mOnStartedRequestListeners;
	
	/**
	 * HashMap of onFinishedRequestListener. Fires if value is set to true.
	 * 
	 * @see OnFinishedRequestListener
	 * @see ListenerState
	 * @see RESTRequest#getOnFinishedRequestListener()
	 * @see RESTRequest#setOnFinishedRequestListener(OnFinishedRequestListener)
	 */
	protected transient HashMap<OnFinishedRequestListener, ListenerState> mOnFinishedRequestListeners;
	
	/**
	 * HashMap of onFailedRequestListener. Fires if value is set to true.
	 * 
	 * @see OnFailedRequestListener
	 * @see ListenerState
	 * @see RESTRequest#getOnFailedRequestListener()
	 * @see RESTRequest#setOnFailedRequestListener(OnFailedRequestListener)
	 */
	protected transient HashMap<OnFailedRequestListener, ListenerState> mOnFailedRequestListeners;
	
	public RequestListeners() {
		mOnFailedRequestListeners = new HashMap<OnFailedRequestListener, ListenerState>();
		mOnFinishedRequestListeners = new HashMap<OnFinishedRequestListener, ListenerState>();
		mOnStartedRequestListeners = new HashMap<OnStartedRequestListener, ListenerState>();
	}
	
	/**
	 * Add {@link OnStartedRequestListener} listener
	 * 
	 * @param listener
	 * 		Instance of {@link OnStartedRequestListener}
	 * 
	 * @see OnStartedRequestListener
	 * @see RESTRequest#mOnStartedRequestListener
	 * @see RESTRequest#getOnStartedRequestListener()
	 */
	public void addOnStartedRequestListener(OnStartedRequestListener listener) {
		if(!mOnStartedRequestListeners.containsKey(listener))
			mOnStartedRequestListeners.put(listener, ListenerState.SET);
	}
	
	/**
	 * Add {@link OnFinishedRequestListener} listener
	 * 
	 * @param listener
	 * 		Instance of {@link OnFinishedRequestListener}
	 * 
	 * @see OnFinishedRequestListener
	 * @see RESTRequest#mOnFinishedRequestListener
	 * @see RESTRequest#getOnFinishedRequestListener()
	 */
	public void addOnFinishedRequestListener(OnFinishedRequestListener listener) {
		for(Entry<OnFinishedRequestListener, ListenerState> l : mOnFinishedRequestListeners.entrySet()) {
			Log.e("fr.pcreations.labs.RESTDROID.sample.DebugWebService.TAG", l.toString());
			Log.e("fr.pcreations.labs.RESTDROID.sample.DebugWebService.TAG", String.valueOf(l == listener));
		}
		Log.e("fr.pcreations.labs.RESTDROID.sample.DebugWebService.TAG", listener.toString());
		
		if(!mOnFinishedRequestListeners.containsKey(listener)) {
			Log.w("fr.pcreations.labs.RESTDROID.sample.DebugWebService.TAG", "add listeners");
			mOnFinishedRequestListeners.put(listener, ListenerState.SET);
		}
	}
	
	/**
	 * Add {@link OnFailedRequestListener} listener
	 * 
	 * @param listener
	 * 		Instance of {@link OnFailedRequestListener}
	 * 
	 * @see OnFailedRequestListener
	 * @see RESTRequest#mOnFailedRequestListener
	 * @see RESTRequest#getOnFailedRequestListener()
	 */
	public void addOnFailedRequestListener(OnFailedRequestListener listener) {
		if(!mOnFailedRequestListeners.containsKey(listener))
			mOnFailedRequestListeners.put(listener, ListenerState.SET);
	}
	
	/**
	 * @return
	 * 		{@link RESTRequest#mOnStartedRequestListeners}
	 * 
	 * @see OnStartedRequestListeners
	 * @see RESTRequest#mOnStartedRequestListeners
	 * @see RESTRequest#addOnStartedRequestListener(OnStartedRequestListener)
	 */
	public HashMap<OnStartedRequestListener, ListenerState> getOnStartedRequestListeners() {
		return mOnStartedRequestListeners;
	}
	
	/**
	 * @return
	 * 		{@link RESTRequest#mOnFinishedRequestListeners}
	 * 
	 * @see OnFinishedRequestListeners
	 * @see RESTRequest#mOnFinishedRequestListeners
	 * @see RESTRequest#addOnFinishedRequestListener(OnFinishedRequestListener)
	 */
	public HashMap<OnFinishedRequestListener, ListenerState> getOnFinishedRequestListeners() {
		return mOnFinishedRequestListeners;
	}
	
	/**
	 * @return
	 * 		{@link RESTRequest#mOnFailedRequestListeners}
	 * 
	 * @see OnFailedRequestListeners
	 * @see RESTRequest#mOnFailedRequestListeners
	 * @see RESTRequest#addOnFailedRequestListener(OnFailedRequestListener)
	 */
	public HashMap<OnFailedRequestListener, ListenerState> getOnFailedRequestListeners() {
		return mOnFailedRequestListeners;
	}
	
	/**
	 * <b>Listener for {@link RESTRequest} started state</b>
	 * 
	 * @author Pierre Criulanscy
	 * 
	 * @version 0.5
	 *
	 */
	public interface OnStartedRequestListener {
		
		/**
		 * Logic to executes when a {@link RESTRequest} starts
		 */
		public abstract void onStartedRequest();
	}
	
	/**
	 * <b>Listener for {@link RESTRequest} finished state</b>
	 * 
	 * @author Pierre Criulanscy
	 * 
	 * @version 0.5
	 *
	 */
	public interface OnFinishedRequestListener {
		
		/**
		 * Logic to executes when a {@link RESTRequest} finished
		 * 
		 * @param resultCode
		 * 		The result code resulting of all process
		 */
        public abstract void onFinishedRequest(int resultCode);
    }
	
	/**
	 * <b>Listener for {@link RESTRequest} failed state</b>
	 * 
	 * @author Pierre Criulanscy
	 * 
	 * @version 0.5
	 */
	public interface OnFailedRequestListener {
		
		/**
		 * Logic to executes when {@link RESTRequest} failed
		 * 
		 * @param resultCode
		 * 		The result code resulting of all process
		 */
		public abstract void onFailedRequest(int resultCode);
	}
	
}
