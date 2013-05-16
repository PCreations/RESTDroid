package fr.pcreations.labs.RESTDroid.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * <b>Holder class for request listeners</b>
 * 
 * <p>
 * You can create an inner class in your Activity which extends this class to fit your needs.
 * 
 * <pre>
public class TestRequestListeners extends RequestListeners {
	private OnStartedRequestListener onStart = new OnStartedRequestListener() {

		public void onStartedRequest() {
			//TODO
		}
		
	};
	
	private OnSucceededRequestListener onSucceeded = new OnSucceededRequestListener() {

		public void onSucceededRequest(int resultCode) {
			//TODO
		}
		
	};
	
	private OnFailedRequestListener onFailed = new OnFailedRequestListener() {
		
		public void onFailedRequest(int resultCode) {
			//TODO
		}
		
	};
	
	private OnFinishedRequestListener onFinished = new OnFinishedRequestListener() {
		
		public void onFinishedRequest(int resultCode) {
			//TODO
		}
		
	};
	
	public TestRequestListeners() {
		super();
		addOnStartedRequestListener(onStart);
		addOnSucceedRequestListener(onSucceed);
		addOnFailedRequestListener(onFailed);
		addOnFinishedRequestListener(onFinished);
	}
}
 * </pre>
 * </p>
 * 
 * @author Pierre Criulanscy
 * 
 * @version 0.8
 *
 */
public class RequestListeners {

	/**
	 * HashMap of onStartedRequestListener. Fires if value is set to true.
	 * 
	 * @see OnStartedRequestListener
	 * @see ListenerState
	 * @see RequestListeners#getOnStartedRequestListeners()
	 * @see RequestListeners#setOnStartedRequestListeners(OnStartedRequestListeners)
	 */
	protected transient HashMap<OnStartedRequestListener, ListenerState> mOnStartedRequestListeners;
	
	/**
	 * HashMap of onSucceedRequestListener. Fires if value is set to true.
	 * 
	 * @see OnSucceededRequestListener
	 * @see ListenerState
	 * @see RequestListeners#getOnSucceededRequestListeners()
	 * @see RequestListeners#setOnFinishedRequestListeners(OnFinishedRequestListeners)
	 */
	protected transient HashMap<OnSucceededRequestListener, ListenerState> mOnSucceededRequestListeners;
	
	/**
	 * HashMap of onFailedRequestListener. Fires if value is set to true.
	 * 
	 * @see OnFailedRequestListener
	 * @see ListenerState
	 * @see RequestListeners#getOnFailedRequestListeners()
	 * @see RequestListeners#setOnFailedRequestListeners(OnFailedRequestListeners)
	 */
	protected transient HashMap<OnFailedRequestListener, ListenerState> mOnFailedRequestListeners;
	
	/**
	 * HashMap of onFinishedRequestListener. Fires if value is set to true.
	 * 
	 * @see OnFinishedRequestListener
	 * @see ListenerState
	 * @see RequestListeners#getOnFinishedRequestListeners()
	 * @see RequestListeners#setOnFinishedRequestListeners(OnFinishedRequestListeners)
	 */
	protected transient HashMap<OnFinishedRequestListener, ListenerState> mOnFinishedRequestListeners;
	
	/**
	 * {@link RESTRequest} holding by this {@link RequestListeners} class
	 * 
	 * @see RequestListeners#setRequest(RESTRequest);
	 */
	protected transient RESTRequest<? extends Resource> mRequest;
	
	public RequestListeners() {
		mOnFailedRequestListeners = new HashMap<OnFailedRequestListener, ListenerState>();
		mOnSucceededRequestListeners = new HashMap<OnSucceededRequestListener, ListenerState>();
		mOnStartedRequestListeners = new HashMap<OnStartedRequestListener, ListenerState>();
		mOnFinishedRequestListeners = new HashMap<OnFinishedRequestListener, ListenerState>();
	}
	
	/**
	 * Setter for {@link RequestListeners#mRequest}
	 * 
	 * @param r
	 * 		Instance of {@link RESTRequest}
	 */
	public void setRequest(RESTRequest<? extends Resource> r) {
		mRequest = r;
	}
	
	/**
	 * Add {@link OnStartedRequestListener} listener
	 * 
	 * @param listener
	 * 		Instance of {@link OnStartedRequestListener}
	 * 
	 * @see OnStartedRequestListener
	 * @see RequestListeners#mOnStartedRequestListeners
	 * @see RequestListeners#getOnStartedRequestListeners()
	 */
	public void addOnStartedRequestListener(OnStartedRequestListener listener) {
		if(!mOnStartedRequestListeners.containsKey(listener))
			mOnStartedRequestListeners.put(listener, ListenerState.SET);
	}
	
	/**
	 * Add {@link OnSucceededRequestListener} listener
	 * 
	 * @param listener
	 * 		Instance of {@link OnSucceededRequestListener}
	 * 
	 * @see OnSucceededRequestListener
	 * @see RequestListeners#mOnSucceededRequestListeners
	 * @see RequestListeners#getOnSucceedRequestListeners()
	 */
	public void addOnSucceededRequestListener(OnSucceededRequestListener listener) {		
		if(!mOnSucceededRequestListeners.containsKey(listener))
			mOnSucceededRequestListeners.put(listener, ListenerState.SET);
	}
	
	/**
	 * Add {@link OnFailedRequestListener} listener
	 * 
	 * @param listener
	 * 		Instance of {@link OnFailedRequestListener}
	 * 
	 * @see OnFailedRequestListener
	 * @see RequestListeners#mOnFailedRequestListeners
	 * @see RequestListeners#getOnFailedRequestListeners()
	 */
	public void addOnFailedRequestListener(OnFailedRequestListener listener) {
		if(!mOnFailedRequestListeners.containsKey(listener))
			mOnFailedRequestListeners.put(listener, ListenerState.SET);
	}
	
	/**
	 * Add {@link OnFinishedRequestListener} listener
	 * 
	 * @param listener
	 * 		Instance of {@link OnFinishedRequestListener}
	 * 
	 * @see OnFinishedRequestListener
	 * @see RequestListeners#mOnFinishedRequestListeners
	 * @see RequestListeners#getOnFinishedRequestListeners()
	 */
	public void addOnFinishedRequestListener(OnFinishedRequestListener listener) {
		if(!mOnFinishedRequestListeners.containsKey(listener))
			mOnFinishedRequestListeners.put(listener, ListenerState.SET);
	}
	
	/**
	 * @return
	 * 		{@link RequestListeners#mOnStartedRequestListeners}
	 * 
	 * @see OnStartedRequestListeners
	 * @see RequestListeners#mOnStartedRequestListeners
	 * @see RequestListeners#addOnStartedRequestListener(OnStartedRequestListener)
	 */
	public HashMap<OnStartedRequestListener, ListenerState> getOnStartedRequestListeners() {
		return mOnStartedRequestListeners;
	}
	
	/**
	 * @return
	 * 		{@link RequestListeners#mOnSucceededRequestListeners}
	 * 
	 * @see OnFinishedRequestListeners
	 * @see RequestListeners#mOnSucceededRequestListeners
	 * @see RequestListeners#addOnSucceededRequestListener(OnSucceededRequestListener)
	 */
	public HashMap<OnSucceededRequestListener, ListenerState> getOnSucceedRequestListeners() {
		return mOnSucceededRequestListeners;
	}
	
	/**
	 * @return
	 * 		{@link RequestListeners#mOnFailedRequestListeners}
	 * 
	 * @see OnFailedRequestListeners
	 * @see RequestListeners#mOnFailedRequestListeners
	 * @see RequestListeners#addOnFailedRequestListener(OnFailedRequestListener)
	 */
	public HashMap<OnFailedRequestListener, ListenerState> getOnFailedRequestListeners() {
		return mOnFailedRequestListeners;
	}
	
	/**
	 * @return
	 * 		{@link RequestListeners#mOnFinishedRequestListeners}
	 * 
	 * @see OnFinishedRequestListeners
	 * @see RequestListeners#mOnFinishedRequestListeners
	 * @see RequestListeners#addOnFinishedRequestListener(OnFinishedRequestListener)
	 */
	public HashMap<OnFinishedRequestListener, ListenerState> getOnFinishedRequestListeners() {
		return mOnFinishedRequestListeners;
	}
	
	/**
	 * <b>Listener for {@link RESTRequest} started state</b>
	 * 
	 * @author Pierre Criulanscy
	 * 
	 * @version 0.7.0
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
	 * @version 0.8.0
	 *
	 */
	public interface OnSucceededRequestListener {
		
		/**
		 * Logic to executes when a {@link RESTRequest} finished
		 * 
		 * @param resultCode
		 * 		The result code resulting of all process
		 */
        public abstract void onSucceededRequest(int resultCode);
    }
	
	/**
	 * <b>Listener for {@link RESTRequest} failed state</b>
	 * 
	 * @author Pierre Criulanscy
	 * 
	 * @version 0.7.0
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
	
	/**
	 * <b>Listener for {@link RESTRequest} finished state (whether the request succeeded or failed)</b>
	 * 
	 * @author Pierre Criulanscy
	 * 
	 * @version 0.8.0
	 */
	public interface OnFinishedRequestListener {
		
		/**
		 * Logic to executes when {@link RESTRequest} is finished
		 * 
		 * @param resultCode
		 * 		The result code resulting of all process
		 */
		public abstract void onFinishedRequest(int resultCode);
	}
	
	/**
	 * Helper method to display server's response stream as String
	 * 
	 * @param is
	 * 		The InputStream to display as String
	 * 
	 * @return
	 * 		The String created from InputStream
	 */
	protected String inputStreamToString(InputStream is) {
        BufferedReader bufferedReader;
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			StringBuilder inputStringBuilder = new StringBuilder();
	        String line;
			try {
				line = bufferedReader.readLine();
				while(line != null){
		            inputStringBuilder.append(line);inputStringBuilder.append('\n');
		            try {
						line = bufferedReader.readLine();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }
				return inputStringBuilder.toString();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return null;
	}

	/**
	 * Resets all listeners by setting the state of all listeners to {@link ListenerState#SET}
	 */
	public void resetAllListeners() {
		for(Entry<OnFinishedRequestListener, ListenerState> listener : mOnFinishedRequestListeners.entrySet()) {
			listener.setValue(ListenerState.SET);
		}
		for(Entry<OnFailedRequestListener, ListenerState> listener : mOnFailedRequestListeners.entrySet()) {
			listener.setValue(ListenerState.SET);
		}
		for(Entry<OnSucceededRequestListener, ListenerState> listener : mOnSucceededRequestListeners.entrySet()) {
			listener.setValue(ListenerState.SET);
		}
		for(Entry<OnStartedRequestListener, ListenerState> listener : mOnStartedRequestListeners.entrySet()) {
			listener.setValue(ListenerState.SET);
		}
	}
	
}
