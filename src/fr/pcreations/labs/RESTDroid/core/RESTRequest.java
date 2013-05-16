package fr.pcreations.labs.RESTDroid.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import fr.pcreations.labs.RESTDroid.core.RequestListeners.OnFailedRequestListener;
import fr.pcreations.labs.RESTDroid.core.RequestListeners.OnFinishedRequestListener;
import fr.pcreations.labs.RESTDroid.core.RequestListeners.OnStartedRequestListener;
import fr.pcreations.labs.RESTDroid.core.RequestListeners.OnSucceededRequestListener;
/**
 * <b>Holder class for all request stuff. Provides some listener to handle specific logic in Activity when request starts, succeed or failed</b>
 * 
 * @author Pierre Criulanscy
 *
 * @param <T>
 * 		The {@link ResourceRepresentation} class that request deals with
 * 
 * @version 0.7.2
 */
public class RESTRequest<T extends Resource> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3975518541858101876L;
	
	/**
	 * ID of the request. Should be unique
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
	 * The server response
	 * 
	 * @see RESTRequest#getResultStream()
	 * @see RESTRequest#setResultStream(InputStream)
	 * 
	 * @since 0.7.1
	 */
	private transient ByteArrayOutputStream mByteArrayResultStream;
	
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
	private T mResource;
	
	/**
	 * The Class object of {@link ResourceRepresentation} attached to this request. Useful when {@link RESTRequest#mResource} is null
	 * 
	 * @see RESTRequest#getResourceClass()
	 */
	private Class<T> mResourceClass;
	
	/**
	 *  Instance of {@link RequestListeners}
	 *  
	 *  @see RESTRequest#setRequestListeners(Activity, Class)
	 *  @see RESTRequest#getRequestListeners()
	 *  
	 *  @since 0.7.1
	 */
	private transient RequestListeners mRequestListeners;
	
	private Class<? extends FailBehavior> mFailBehaviorClass;
	
	private long mExpirationTime;
	
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
		mPending = false;
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
		mPending = false;
		mExtraParams = extraParams;
		mHeaders = new ArrayList<SerializableHeader>();
	}

	
	
	/**
	 * Pauses the listeners by setting their state to {@link ListenerState#UNSET}
	 * 
	 * @see RESTRequest#resumeListeners()
	 * @see RequestListeners
	 */
	public void pauseListeners() {
		if(mRequestListeners != null) {
			for(Entry<OnStartedRequestListener, ListenerState> listener : mRequestListeners.getOnStartedRequestListeners().entrySet()) {
				listener.setValue(ListenerState.UNSET);
			}
			for(Entry<OnFailedRequestListener, ListenerState> listener : mRequestListeners.getOnFailedRequestListeners().entrySet()) {
				listener.setValue(ListenerState.UNSET);
			}
			for(Entry<OnSucceededRequestListener, ListenerState> listener : mRequestListeners.getOnSucceedRequestListeners().entrySet()) {
				listener.setValue(ListenerState.UNSET);
			}
		}
	}
	
	/**
	 * Resumes the listeners. If listener state is set to {@link ListenerState#UNSET}, state is updated to {@link ListenerState#SET}. If the state is {@link ListenerState#TRIGGER_ME} the listener is triggered and is state updated to {@link ListenerState#TRIGGERED}
	 * 
	 * @return
	 * 		True if listener(s) was triggered, false otherwise
	 * 
	 * @see RESTRequest#pauseListeners()
	 * @see RequestListeners
	 */
	public boolean resumeListeners() {
		boolean listenerTriggered = false;
		if(mRequestListeners != null) {
			for(Entry<OnStartedRequestListener, ListenerState> listener : mRequestListeners.getOnStartedRequestListeners().entrySet()) {
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
			for(Entry<OnFailedRequestListener, ListenerState> listener : mRequestListeners.getOnFailedRequestListeners().entrySet()) {
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
			for(Entry<OnSucceededRequestListener, ListenerState> listener : mRequestListeners.getOnSucceedRequestListeners().entrySet()) {
				switch(listener.getValue()) {
					case TRIGGER_ME:
						listener.setValue(ListenerState.TRIGGERED);
						listener.getKey().onSucceededRequest(mResultCode);
						listenerTriggered = true;
						break;
					case UNSET:
						listener.setValue(ListenerState.SET);
						break;
					default:
						break;
				}
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
	 * Triggers {@link OnStartedRequestListener} if state is set to {@link ListenerState#SET} and updates state to {@link ListenerState#TRIGGERED}. If the listener is unset, state is updated to {@link ListenerState#TRIGGER_ME}
	 * 
	 * @return
	 * 		True if a listener was triggered, false otherwise
	 * 
	 * @see RequestListeners#mOnStartedRequestListeners
	 */
	public boolean triggerOnStartedRequestListeners() {
		boolean listenerFired = false;
		if(mRequestListeners != null) {
			for(Entry<OnStartedRequestListener, ListenerState> listener : mRequestListeners.getOnStartedRequestListeners().entrySet()) {
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
						Log.e("CAN'T TRIGGER STARTED REQUEST LISTENER", listener.getValue().toString());
				}
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
	 * @see RequestListeners#mOnSucceededRequestListeners
	 */
	public boolean triggerOnSucceededRequestListeners() {
		boolean listenerFired = false;
		if(mRequestListeners != null) {
			for(Entry<OnSucceededRequestListener, ListenerState> listener : mRequestListeners.getOnSucceedRequestListeners().entrySet()) {
				switch(listener.getValue()) {
					case SET:
						listener.setValue(ListenerState.TRIGGERED);
						listenerFired = true;
						listener.getKey().onSucceededRequest(mResultCode);
						break;
					case UNSET:
						listener.setValue(ListenerState.TRIGGER_ME);
						break;
					default:
						break;
				}
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
	 * @see RequestListeners#mOnFailedRequestListeners
	 */
	public boolean triggerOnFailedRequestListeners() {
		boolean listenerFired = false;
		if(mRequestListeners != null) {
			for(Entry<OnFailedRequestListener, ListenerState> listener : mRequestListeners.getOnFailedRequestListeners().entrySet()) {
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
		}
		return listenerFired;
	}
	
	/**
	 * Triggers {@link OnFinishedRequestListener} if state is set to {@link ListenerState#SET} and updates state to {@link ListenerState#TRIGGERED}. If the listener is unset, state is updated to {@link ListenerState#TRIGGER_ME}
	 * 
	 * @return
	 * 		True if a listener was triggered, false otherwise
	 * 
	 * @see RequestListeners#mOnFinishedRequestListeners
	 */
	public boolean triggerOnFinishedRequestListeners() {
		boolean listenerFired = false;
		if(mRequestListeners != null) {
			for(Entry<OnFinishedRequestListener, ListenerState> listener : mRequestListeners.getOnFinishedRequestListeners().entrySet()) {
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
	 * Getter for {@link RESTRequest#mByteArrayResultStream}. Returns a new InputStream each time
	 * 
	 * @return
	 * 		The server's result stream
	 * 
	 * @see RESTRequest#mByteArrayResultStream
	 * @see RESTRequest#setResultStream(InputStream)
	 * 
	 * @since 0.7.1
	 */
	public InputStream getResultStream() {
		if(null == mByteArrayResultStream)
			return null;
		InputStream is = new ByteArrayInputStream(mByteArrayResultStream.toByteArray());
		return is;
	}

	/**
	 * Setter for {@link RESTRequest#mByteArrayResultStream}
	 * 
	 * @param mResultStream
	 * 		The server's result stream
	 * @throws IOException 
	 * 
	 * @see RESTRequest#mByteArrayResultStream
	 * @see RESTRequest#getResultStream()
	 * 
	 * @since 0.7.1
	 */
	public void setResultStream(InputStream mResultStream) throws IOException {
		if(null != mResultStream) {
			mByteArrayResultStream = new ByteArrayOutputStream();
		    byte[] buffer = new byte[1024];
		    int len;
		    while ((len = mResultStream.read(buffer)) > -1 ) {
		    	mByteArrayResultStream.write(buffer, 0, len);
		    }
		    mByteArrayResultStream.flush();
		}
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
	 * @see RESTRequest#mResource
	 * @see RESTRequest#setResourceRepresentation(ResourceRepresentation)
	 */
	public T getResource() {
		return mResource;
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
	 * @see RESTRequest#mResource
	 * @see RESTRequest#getResourceRepresentation()
	 */
	@SuppressWarnings("unchecked")
	public void setResource(Resource mResourceRepresentation) {
		this.mResource = (T) mResourceRepresentation;
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
	 * Getter for {@link RESTRequest#mRequestListeners}
	 * 
	 * @return
	 * 		{@link RESTRequest#mRequestListeners}
	 * 
	 * @see RESTRequest#mRequestListeners
	 * @see RequestListeners
	 * @see RESTRequest#setRequestListeners(Activity, Class)
	 * 
	 * @since 0.7.1
	 */
	public RequestListeners getRequestListeners() {
		return mRequestListeners;
	}
	
	/**
	 * Setter for {@link RESTRequest#mRequestListeners}. Dynamically instanciate new instance of the specified {@link RequestListeners} class.
	 * 
	 * @param a
	 * 		Context activity where the {@link RequestListeners} inner class is defined
	 * @param clazz
	 * 		{@link RESTRequest} Class object holding by this {@link RequestListeners} class
	 * 
	 * @since 0.7.1
	 */
	public <L> void setRequestListeners(Activity a, Class<L> clazz) {
		
		try {
			Constructor<L> ctor = clazz.getConstructor(a.getClass());
			mRequestListeners = (RequestListeners) ctor.newInstance(a);
			mRequestListeners.setRequest(this);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
	public void setExpirationTime(long expirationTime) {
		mExpirationTime = expirationTime;
	}
	
	public long getExpirationTime() {
		return mExpirationTime;
	}
	
	public Class<? extends FailBehavior> getFailBehaviorClass() {
		return mFailBehaviorClass;
	}

	public void setFailBehaviorClass(Class<? extends FailBehavior> failBehaviorClass) {
		this.mFailBehaviorClass = failBehaviorClass;
	}

	public String toString() {
		String str = "";
		str += null != mID ? "Request[id] = " +mID.toString() : "Request[id] = null";
		str += null != mVerb ? "[verb] = " + mVerb.name() : "[verb] = null";
		str += null != mUrl ? "[url] = " + mUrl : "[url] = null";
		str += null != mResource ? mResource.toString() : "[ResourceRepresentation] = null";
		return str;
	}

}
