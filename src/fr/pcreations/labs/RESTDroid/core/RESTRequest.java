package fr.pcreations.labs.RESTDroid.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.os.Bundle;

public class RESTRequest<T extends ResourceRepresentation<?>> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3975518541858101876L;
	
	private UUID mID;
	private HTTPVerb mVerb;
	private String mUrl;
	private Bundle mExtraParams;
	private List<SerializableHeader> mHeaders;
	private T mResourceRepresentation;
	private Class<T> mResourceClass;
	protected transient OnStartedRequestListener mOnStartedRequestListener;
	protected transient OnFinishedRequestListener mOnFinishedRequestListener;
	protected transient OnFailedRequestListener mOnFailedRequestListener;
	
	public RESTRequest(UUID id, Class<T> clazz) {
		mID = id;
		mResourceClass = clazz;
		mHeaders = new ArrayList<SerializableHeader>();
	}
	
	public RESTRequest(HTTPVerb verb, UUID id, String url, Bundle extraParams) {
		super();
		mVerb = verb;
		mID = id;
		mUrl = url;
		mExtraParams = extraParams;
		mHeaders = new ArrayList<SerializableHeader>();
	}

	
	public void setOnStartedRequestListener(OnStartedRequestListener listener) {
		mOnStartedRequestListener = listener;
	}
	
	public void setOnFinishedRequestListener(OnFinishedRequestListener listener) {
		mOnFinishedRequestListener = listener;
	}
	
	public void setOnFailedRequestListener(OnFailedRequestListener listener) {
		mOnFailedRequestListener = listener;
	}

	public UUID getID() {
		return mID;
	}
	
	public List<SerializableHeader> getHeaders() {
		return mHeaders;
	}

	public OnStartedRequestListener getOnStartedRequestListener() {
		return mOnStartedRequestListener;
	}
	
	public OnFinishedRequestListener getOnFinishedRequestListener() {
		return mOnFinishedRequestListener;
	}
	
	public OnFailedRequestListener getOnFailedRequestListener() {
		return mOnFailedRequestListener;
	}
	
	public interface OnStartedRequestListener {
		public abstract void onStartedRequest();
	}
	
	public interface OnFinishedRequestListener {
        public abstract void onFinishedRequest(int resultCode);
    }
	
	public interface OnFailedRequestListener {
		public abstract void onFailedRequest(int resultCode);
	}

	public String getUrl() {
		return mUrl;
	}

	public void setUrl(String url) {
		this.mUrl = url;
	}

	public HTTPVerb getVerb() {
		return mVerb;
	}

	public void setVerb(HTTPVerb mVerb) {
		this.mVerb = mVerb;
	}
	
	public T getResourceRepresentation() {
		return mResourceRepresentation;
	}
	
	public Class<T> getResourceClass() {
		return mResourceClass;
	}

	@SuppressWarnings("unchecked")
	public void setResourceRepresentation(ResourceRepresentation<?> mResourceRepresentation) {
		this.mResourceRepresentation = (T) mResourceRepresentation;
	}

	public void setExtraParams(Bundle extraParams) {
		mExtraParams = extraParams;
	}
	
	public void addHeader(SerializableHeader h) {
		mHeaders.add(h);
	}
	
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
