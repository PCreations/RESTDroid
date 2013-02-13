package fr.pcreations.labs.RESTDroid.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import android.os.Bundle;
import android.util.Log;
import fr.pcreations.labs.RESTDroid.core.RESTRequest.OnFailedRequestListener;
import fr.pcreations.labs.RESTDroid.core.RESTRequest.OnFinishedRequestListener;
import fr.pcreations.labs.RESTDroid.core.RESTRequest.OnStartedRequestListener;

/**
 * <b>Holder class for all request stuff. Provides some listener to handle specific logic in Activity when request starts, succeed or failed</b>
 * 
 * @author Pierre Criulanscy
 *
 * @param <T>
 * 		The {@link ResourceRepresentation} class that request deals with
 * 
 * @version 0.6.0
 */
public class RESTRequest<T extends ResourceRepresentation<?>> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3975518541858101876L;
	
	/**
	 * ID of the request. Should be unique
	 * 
	 * @see RESTRequest#getID()
	 */
	private String mID;
	
	/**
	 * The particular {@link HTTPVerb} for this request
	 * 
	 * @see RESTRequest#getVerb()
	 * @see RESTRequest#setVerb(HTTPVerb)
	 */
	private HTTPVerb mVerb;
	
	
	/**
	 * Url for this request
	 * 
	 * @see RESTRequest#getUrl()
	 * @see RESTRequest#setUrl(String)
	 */
	private String mUrl;
	
	/**
	 * Request result code
	 * 
	 * @see RESTRequest#getResultCode()
	 * @see RESTRequest#setResultCode(int)
	 */
	private int mResultCode;
	
	/**
	 * Boolean to know if request is pending
	 * 
	 * @see RESTRequest#isPending()
	 * @see RESTRequest#setPending(boolean)
	 * 
	 * @since 0.6.0
	 */
	private boolean mPending;
	
	/**
	 * Defines extra parameters for request
	 * 
	 */
	private Bundle mExtraParams;
	
	/**
	 * List of {@link SerializableHeader} for this request
	 * 
	 * @see RESTRequest#getHeaders()
	 * @see RESTRequest#addHeader(SerializableHeader)
	 * @see RESTRequest#addHeader(String, String)
	 */
	private List<SerializableHeader> mHeaders;
	
	/**
	 * Instance of {@link ResourceRepresentation} attached to this request
	 * 
	 * @see RESTRequest#getResourceRepresentation()
	 * @see RESTRequest#setResourceRepresentation(ResourceRepresentation)
	 */
	private T mResourceRepresentation;
	
	/**
	 * The Class object of {@link ResourceRepresentation} attached to this request. Useful when {@link RESTRequest#mResourceRepresentation} is null
	 * 
	 * @see RESTRequest#getResourceClass()
	 */
	private Class<T> mResourceClass;
	
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
	
	/**
	 * Constructor
	 * 
	 * @param id
	 * 		Unique ID of the request
	 * 
	 * @param clazz
	 * 		The Class object of the {@link ResourceRepresentation} attached to this request
	 */
	public RESTRequest(String id, Class<T> clazz) {
		mID = id;
		mResourceClass = clazz;
		mPending = false;
		mHeaders = new ArrayList<SerializableHeader>();
		mOnFailedRequestListeners = new HashMap<OnFailedRequestListener, ListenerState>();
		mOnFinishedRequestListeners = new HashMap<OnFinishedRequestListener, ListenerState>();
		mOnStartedRequestListeners = new HashMap<OnStartedRequestListener, ListenerState>();
	}
	
	/**
	 * Constructor
	 * 
	 * @param verb
	 * @param id
	 * @param url
	 * @param extraParams
	 */
	public RESTRequest(HTTPVerb verb, String id, String url, Bundle extraParams) {
		super();
		mVerb = verb;
		mID = id;
		mUrl = url;
		mPending = false;
		mExtraParams = extraParams;
		mHeaders = new ArrayList<SerializableHeader>();
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
		if(!mOnFinishedRequestListeners.containsKey(listener))
			mOnFinishedRequestListeners.put(listener, ListenerState.SET);
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
	 * Pauses the listeners by setting their state to {@link ListenerState#UNSET}
	 * 
	 * @see RESTRequest#resumeListeners()
	 * @see RESTRequest#mOnFailedRequestListeners
	 * @see RESTRequest#mOnFinishedRequestListeners
	 * @see RESTRequest#mOnStartedRequestListeners
	 */
	public void pauseListeners() {
		for(Entry<OnStartedRequestListener, ListenerState> listener : mOnStartedRequestListeners.entrySet()) {
			listener.setValue(ListenerState.UNSET);
		}
		for(Entry<OnFailedRequestListener, ListenerState> listener : mOnFailedRequestListeners.entrySet()) {
			listener.setValue(ListenerState.UNSET);
		}
		for(Entry<OnFinishedRequestListener, ListenerState> listener : mOnFinishedRequestListeners.entrySet()) {
			listener.setValue(ListenerState.UNSET);
		}
	}
	
	/**
	 * Resumes the listeners. If listener state is set to {@link ListenerState#UNSET}, state is updated to {@link ListenerState#SET}. If the state is {@link ListenerState#TRIGGER_ME} the listener is triggered and is state updated to {@link ListenerState#TRIGGERED}
	 * 
	 * @return
	 * 		True if listener(s) was triggered, false otherwise
	 * 
	 * @see RESTRequest#pauseListeners()
	 * @see RESTRequest#mOnFailedRequestListeners
	 * @see RESTRequest#mOnFinishedRequestListeners
	 * @see RESTRequest#mOnStartedRequestListeners
	 */
	public boolean resumeListeners() {
		boolean listenerTriggered = false;
		for(Entry<OnStartedRequestListener, ListenerState> listener : mOnStartedRequestListeners.entrySet()) {
			switch(listener.getValue()) {
				case TRIGGER_ME:
					listener.setValue(ListenerState.TRIGGERED);
					listener.getKey().onStartedRequest();
					listenerTriggered = true;
					break;
				case UNSET:
					listener.setValue(ListenerState.SET);
					break;
				default:
					break;
			}
		}
		for(Entry<OnFailedRequestListener, ListenerState> listener : mOnFailedRequestListeners.entrySet()) {
			switch(listener.getValue()) {
				case TRIGGER_ME:
					listener.setValue(ListenerState.TRIGGERED);
					listener.getKey().onFailedRequest(mResultCode);
					listenerTriggered = true;
					break;
				case UNSET:
					listener.setValue(ListenerState.SET);
					break;
				default:
					break;
			}
		}
		for(Entry<OnFinishedRequestListener, ListenerState> listener : mOnFinishedRequestListeners.entrySet()) {
			switch(listener.getValue()) {
				case TRIGGER_ME:
					listener.setValue(ListenerState.TRIGGERED);
					listener.getKey().onFinishedRequest(mResultCode);
					listenerTriggered = true;
					break;
				case UNSET:
					listener.setValue(ListenerState.SET);
					break;
				default:
					break;
			}
		}
		return listenerTriggered;
	}

	/**
	 * Getter for the unique ID of the request
	 * 
	 * @return
	 * 		The unique ID of the request
	 * 
	 * @see RESTRequest#mID
	 */
	public String getID() {
		return mID;
	}
	
	/**
	 * Getter for request's list of {@link SerializableHeader}
	 * 
	 * @return
	 * 		All the {@link SerializableHeader} of the request
	 * 
	 * @see RESTRequest#mHeaders
	 * @see RESTRequest#addHeader(SerializableHeader)
	 * @see RESTRequest#addHeader(String, String)
	 * @see SerializableHeader
	 */
	public List<SerializableHeader> getHeaders() {
		return mHeaders;
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
	public HashMap<OnFinishedRequestListener, ListenerState> getOnFinishedRequestListener() {
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
	public HashMap<OnFailedRequestListener, ListenerState> getOnFailedRequestListener() {
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

	/**
	 * Triggers {@link OnStartedRequestListener} if state is set to {@link ListenerState#SET} and updates state to {@link ListenerState#TRIGGERED}. If the listener is unset, state is updated to {@link ListenerState#TRIGGER_ME}
	 * 
	 * @return
	 * 		True if a listener was triggered, false otherwise
	 * 
	 * @see RESTRequest#mOnStartedRequestListeners
	 */
	public boolean triggerOnStartedRequestListeners() {
		boolean listenerFired = false;
		for(Entry<OnStartedRequestListener, ListenerState> listener : mOnStartedRequestListeners.entrySet()) {
			switch(listener.getValue()) {
				case SET:
					listener.setValue(ListenerState.TRIGGERED);
					listenerFired = true;
					listener.getKey().onStartedRequest();
					break;
				case UNSET:
					listener.setValue(ListenerState.TRIGGER_ME);
					break;
				default:
					break;
			}
		}
		return listenerFired;
	}
	
	/**
	 * Triggers {@link OnFinishedRequestListener} if state is set to {@link ListenerState#SET} and updates state to {@link ListenerState#TRIGGERED}. If the listener is unset, state is updated to {@link ListenerState#TRIGGER_ME}
	 * 
	 * @return
	 * 		True if a listener was triggered, false otherwise
	 * 
	 * @see RESTRequest#mOnFinishedRequestListeners
	 */
	public boolean triggerOnFinishedRequestListeners() {
		boolean listenerFired = false;
		for(Entry<OnFinishedRequestListener, ListenerState> listener : mOnFinishedRequestListeners.entrySet()) {
			switch(listener.getValue()) {
				case SET:
					listener.setValue(ListenerState.TRIGGERED);
					listenerFired = true;
					listener.getKey().onFinishedRequest(mResultCode);
					break;
				case UNSET:
					listener.setValue(ListenerState.TRIGGER_ME);
					break;
				default:
					break;
			}
		}
		return listenerFired;
	}
	
	/**
	 * Triggers {@link OnFailedRequestListener} if state is set to {@link ListenerState#SET} and updates state to {@link ListenerState#TRIGGERED}. If the listener is unset, state is updated to {@link ListenerState#TRIGGER_ME}
	 * 
	 * @return
	 * 		True if a listener was triggered, false otherwise
	 * 
	 * @see RESTRequest#mOnFailedRequestListeners
	 */
	public boolean triggerOnFailedRequestListeners() {
		boolean listenerFired = false;
		for(Entry<OnFailedRequestListener, ListenerState> listener : mOnFailedRequestListeners.entrySet()) {
			switch(listener.getValue()) {
				case SET:
					listener.setValue(ListenerState.TRIGGERED);
					listenerFired = true;
					listener.getKey().onFailedRequest(mResultCode);
					break;
				case UNSET:
					listener.setValue(ListenerState.TRIGGER_ME);
					break;
				default:
					break;
			}
		}
		return listenerFired;
	}
	
	/**
	 * Getter for url
	 * 
	 * @return
	 * 		The {@link RESTRequest}'s url
	 * 
	 * @see RESTRequest#mUrl
	 * @see RESTRequest#setUrl(String)
	 */
	public String getUrl() {
		return mUrl;
	}

	/**
	 * Setter for url
	 * 
	 * @param url
	 * 		The {@link RESTRequest}'s url
	 * 
	 * @see RESTRequest#mUrl
	 * @see RESTRequest#getUrl()
	 */
	public void setUrl(String url) {
		this.mUrl = url;
	}

	/**
	 * Getter for {@link RESTRequest#mResultCode}
	 * 
	 * @return
	 * 		The request result code
	 * 
	 * @see RESTRequest#mResultCode
	 * @see RESTRequest#setResultCode(int)
	 * 
	 */
	public int getResultCode() {
		return mResultCode;
	}

	/**
	 * Set the request's result code
	 * 
	 * @param resultCode
	 * 		The result code to set
	 * 
	 * @see RESTRequest#mResultCode
	 * @see RESTRequest#getResultCode()
	 * 
	 */
	public void setResultCode(int resultCode) {
		mResultCode = resultCode;
	}
	
	/**
	 * Getter for {@link RESTRequest#mPending}
	 * 
	 * @return
	 * 		True if the request is pending, false otherwise
	 * 
	 * @see RESTRequest#mPending
	 * @see RESTRequest#setPending(boolean)
	 * 
	 * @since 0.6.0
	 */
	public boolean isPending() {
		return mPending;
	}

	/**
	 * Setter for {@link RESTRequest#mPending}
	 * 
	 * @param pending
	 * 		The pending state
	 * 
	 * @see RESTRequest#mPending
	 * @see RESTRequest#isPending()
	 * 
	 * @since 0.6.0
	 */
	public void setPending(boolean pending) {
		mPending = pending;
	}

	/**
	 * Getter for {@link HTTPVerb}
	 * 
	 * @return
	 * 		The {@link RESTRequest}'s {@link HTTPVerb}
	 * 
	 * @see HTTPVerb
	 * @see RESTRequest#mVerb
	 * @see RESTRequest#setVerb(HTTPVerb)
	 */
	public HTTPVerb getVerb() {
		return mVerb;
	}

	/**
	 * Setter for {@link HTTPVerb}
	 * 
	 * @param mVerb
	 * 		The {@link RESTRequest}'s {@link HTTPVerb}
	 * 
	 * @see HTTPVerb
	 * @see RESTRequest#mVerb
	 * @see RESTRequest#getVerb()
	 */
	public void setVerb(HTTPVerb mVerb) {
		this.mVerb = mVerb;
	}

	/**
	 * Getter for {@link ResourceRepresentation}
	 * 
	 * @return
	 * 		The {@link RESTRequest}'s {@link ResourceRepresentation}
	 * 
	 * @see ResourceRepresentation
	 * @see RESTRequest#mResourceRepresentation
	 * @see RESTRequest#setResourceRepresentation(ResourceRepresentation)
	 */
	public T getResourceRepresentation() {
		return mResourceRepresentation;
	}
	
	/**
	 * Getter for Class object of {@link RESTRequest}'s {@link ResourceRepresentation}
	 * 
	 * @return
	 * 		Class object of {@link RESTRequest}'s {@link ResourceRepresentation}
	 * 
	 * @see ResourceRepresentation
	 * @see RESTRequest#mResourceClass
	 */
	public Class<T> getResourceClass() {
		return mResourceClass;
	}
	
	/**
	 * Setter for {@link ResourceRepresentation}
	 *  
	 * @param mResourceRepresentation
	 * 		Instance of {@link ResourceRepresentation}
	 * 
	 * @see ResourceRepresentation
	 * @see RESTRequest#mResourceRepresentation
	 * @see RESTRequest#getResourceRepresentation()
	 */
	@SuppressWarnings("unchecked")
	public void setResourceRepresentation(ResourceRepresentation<?> mResourceRepresentation) {
		this.mResourceRepresentation = (T) mResourceRepresentation;
	}

	/**
	 * Getter for extra parameters
	 * 
	 * @return
	 * 		{@link RESTRequest}'s extra	parameters
	 * 
	 * @see RESTRequest#mExtraParams
	 * @see RESTRequest#setExtraParams(Bundle)
	 */
	public Bundle getExtraParams() {
		return mExtraParams;
	}
	
	/**
	 * Setter for extra parameters
	 * 
	 * @param extraParams
	 * 		Extra parameters to store in {@link RESTRequest}
	 * 
	 * @see RESTRequest#mExtraParams
	 */
	public void setExtraParams(Bundle extraParams) {
		mExtraParams = extraParams;
	}
	
	/**
	 * Add {@link SerializableHeader} to {@link RESTRequest}
	 * 
	 * @param h
	 * 		Instance of {@link SerializableHeader}
	 * 
	 * @see SerializableHeader
	 * @see RESTRequest#mHeaders
	 * @see RESTRequest#getHeaders()
	 * @see RESTRequest#addHeader(String, String)
	 */
	public void addHeader(SerializableHeader h) {
		mHeaders.add(h);
	}
	
	/**
	 * Add {@link SerializableHeader} from name and value
	 *  
	 * @param name
	 * 		Header's name
	 * 
	 * @param value
	 * 		Header's value
	 * 
	 * @see SerializableHeader
	 * @see RESTRequest#mHeaders
	 * @see RESTRequest#getHeaders()
	 * @see RESTRequest#addHeader(SerializableHeader)
	 */
	public void addHeader(String name, String value) {
		mHeaders.add(new SerializableHeader(name, value));
	}
	
	public String toString() {
		String str = "";
		str += null != mID ? "Request[id] = " +mID.toString() : "Request[id] = null";
		str += null != mVerb ? "[verb] = " + mVerb.name() : "[verb] = null";
		str += null != mUrl ? "[url] = " + mUrl : "[url] = null";
		str += null != mResourceRepresentation ? mResourceRepresentation.toString() : "[ResourceRepresentation] = null";
		return str;
	}

}
