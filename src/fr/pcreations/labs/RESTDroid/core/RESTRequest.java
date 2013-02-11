package fr.pcreations.labs.RESTDroid.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import android.os.Bundle;

/**
 * <b>Holder class for all request stuff. Provides some listener to handle specific logic in Activity when request starts, succeed or failed</b>
 * 
 * @author Pierre Criulanscy
 *
 * @param <T>
 * 		The {@link ResourceRepresentation} class that request deals with
 * 
 * @version 0.5
 */
public class RESTRequest<T extends ResourceRepresentation<?>> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3975518541858101876L;
	
	/**
	 * Unique ID of the request
	 * 
	 * @see RESTRequest#getID()
	 */
	private UUID mID;
	
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
	 * List of onStartedRequestListener. Fires if key is set to true.
	 * 
	 * @see OnStartedRequestListener
	 * @see RESTRequest#getOnStartedRequestListener()
	 * @see RESTRequest#setOnStartedRequestListener(OnStartedRequestListener)
	 */
	protected transient HashMap<Boolean, OnStartedRequestListener> mOnStartedRequestListeners;
	
	/**
	 * List of onFinishedRequestListener. Fires if key is set to true.
	 * 
	 * @see OnFinishedRequestListener
	 * @see RESTRequest#getOnFinishedRequestListener()
	 * @see RESTRequest#setOnFinishedRequestListener(OnFinishedRequestListener)
	 */
	protected transient HashMap<Boolean, OnFinishedRequestListener> mOnFinishedRequestListeners;
	
	/**
	 * List of onFailedRequestListener. Fires if key is set to true.
	 * 
	 * @see OnFailedRequestListener
	 * @see RESTRequest#getOnFailedRequestListener()
	 * @see RESTRequest#setOnFailedRequestListener(OnFailedRequestListener)
	 */
	protected transient HashMap<Boolean, OnFailedRequestListener> mOnFailedRequestListeners;
	
	/**
	 * Constructor
	 * 
	 * @param id
	 * 		Unique ID of the request
	 * 
	 * @param clazz
	 * 		The Class object of the {@link ResourceRepresentation} attached to this request
	 */
	public RESTRequest(UUID id, Class<T> clazz) {
		mID = id;
		mResourceClass = clazz;
		mHeaders = new ArrayList<SerializableHeader>();
	}
	
	/**
	 * Constructor
	 * 
	 * @param verb
	 * @param id
	 * @param url
	 * @param extraParams
	 */
	public RESTRequest(HTTPVerb verb, UUID id, String url, Bundle extraParams) {
		super();
		mVerb = verb;
		mID = id;
		mUrl = url;
		mExtraParams = extraParams;
		mHeaders = new ArrayList<SerializableHeader>();
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
		mOnStartedRequestListeners.put(false, listener);
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
		mOnFinishedRequestListeners.put(false, listener);
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
		mOnFailedRequestListeners.put(false, listener);
	}

	/**
	 * Getter for the unique ID of the request
	 * 
	 * @return
	 * 		The unique ID of the request
	 * 
	 * @see RESTRequest#mID
	 */
	public UUID getID() {
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
	public HashMap<Boolean, OnStartedRequestListener> getOnStartedRequestListeners() {
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
	public HashMap<Boolean, OnFinishedRequestListener> getOnFinishedRequestListener() {
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
	public HashMap<Boolean, OnFailedRequestListener> getOnFailedRequestListener() {
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
