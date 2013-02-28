package fr.pcreations.labs.RESTDroid.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import fr.pcreations.labs.RESTDroid.exceptions.RequestNotFoundException;

/**
 * <b>Helper which exposes simple asynchronous API to be used by the UI</b>
 * 
 * <p>
 * This class is used as singleton by {@link RESTDroid}. This class provides also a little factory for {@link RESTRequest}.
 * First of all a {@link Module} must be registered.
 * The role of this class is to prepare and to send the {@link RestService} request :
 * <ul>
 * <li>Check if the request has to be re-sent or not via {@link Processor#checkRequest(RESTRequest)}</li>
 * <li>Create the request Intent</li>
 * <li>Add the {@link RESTRequest} in {@link WebService#mRequestCollection}</li>
 * <li>Start the service</li>
 * </ul>
 * </p>
 * 
 * @author Pierre Criulanscy
 * 
 * @version 0.7.0
 * 
 * @see RESTDroid#getWebService(Class)
 * @see Processor#checkRequest(RESTRequest)
 * @see Module
 */
public abstract class WebService implements RestResultReceiver.Receiver{

	/**
	 * ResultReceiver to acts as a binder callback
	 */
	protected RestResultReceiver mReceiver;
	
	/**
	 * Current application context
	 */
	protected Context mContext;
	
	/**
	 * Collection of {@link RESTRequest}
	 */
	protected List<RESTRequest<?>> mRequestCollection;
	
	/**
	 * {@link Module} actually registered to this WebService instance
	 */
	protected Module mModule;
	
	/**
	 * Empty constructor
	 */
	protected WebService() {}
	
	/**
	 * Constructor
	 * 
	 * @param context
	 * 		Actual application context
	 * 
	 * @see WebService#mContext
	 */
	public WebService(Context context) {
		super();
		mContext = context;
		mReceiver = new RestResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        mRequestCollection = new ArrayList<RESTRequest<?>>();
	}
	
	/**
	 * Registers module for this instance of WebService and set {@link RestService} {@link Processor}
	 * 
	 * @param m
	 * 		Module to be registered
	 * 
	 * @see Module
	 * @see Module#init()
	 * @see RestService#setProcessor(Processor)
	 * @see WebService#mModule
	 */
	public void registerModule(Module m) {
		mModule = m;
		mModule.init();
		RestService.setProcessor(mModule.getProcessor());
	}
	
	/**
	 * Factory of {@link RESTRequest}. Adds {@link RESTRequest} instance in {@link WebService#mRequestCollection} or retrieve it
	 * 
	 * @param clazz
	 * 		Class object of the {@link ResourceRepresentation} which {@link RESTRequest} is dealing with
	 * 
	 * @return
	 * 		Instance of {@link RESTRequest}
	 * 
	 * @see RESTRequest
	 * @see WebService#mRequestCollection
	 * @see WebService#get(RESTRequest, String)
	 * 
	 * @since 0.6.0
	 */
	@SuppressWarnings("unchecked")
	public <T extends ResourceRepresentation<?>> RESTRequest<T> retrieveRequest(Class<T> clazz, String url) {
		RESTRequest<T> r;
		for(Iterator<RESTRequest<?>> it = mRequestCollection.iterator(); it.hasNext();) {
			r = (RESTRequest<T>) it.next();
			if(r.getUrl().equals(url) && clazz.equals(r.getResourceClass())) {
				return r;
			}
		}
		return null;
	}
	
	/**
	 * Pauses all request listeners
	 * 
	 * @see RESTRequest#mOnFailedRequestListeners
	 * @see RESTRequest#mOnFinishedRequestListeners
	 * @see RESTRequest#mOnStartedRequestListeners
	 */
	public void onPause() {
		for(Iterator<RESTRequest<?>> it = mRequestCollection.iterator(); it.hasNext();) {
			RESTRequest<?> r = it.next();
			r.pauseListeners();
		}
	}
	
	/**
	 * Resumes all request listeners and if a listener is triggered remove the request from {@link WebService#mRequestCollection}
	 * 
	 * @see RESTRequest#mOnFailedRequestListeners
	 * @see RESTRequest#mOnFinishedRequestListeners
	 * @see RESTRequest#mOnStartedRequestListeners
	 */
	public void onResume() {
		for(Iterator<RESTRequest<?>> it = mRequestCollection.iterator(); it.hasNext();) {
			RESTRequest<?> r = it.next();
			if(r.resumeListeners())
				it.remove();
		}
	}
	
	/**
	 * Initializes and prepares a GET request or retrieve it if already pending.
	 * 
	 * @param clazz
	 * 		Class of ResourceRepresentation holds by request
	 * 
	 * @param uri
	 * 		Uri to fetch
	 * 
	 * @see WebService#get(RESTRequest, String, Bundle)
	 */
	protected <R extends ResourceRepresentation<?>> RESTRequest<R> get(Class<R> clazz, String uri) {
		RESTRequest<R> request = requestRoutine(clazz, uri);
		initRequest(request, HTTPVerb.GET,  uri);
		Log.w("fr.pcreations.labs.RESTDROID.sample.DebugWebService.TAG", "SIZE = " + String.valueOf(mRequestCollection.size()));
		return request;
	}
	
	/**
	 * Initializes and prepares a GET request or retrieve it if already pending.
	 * 
	 * @param clazz
	 * 		Class of ResourceRepresentation holds by request
	 * 
	 * @param uri
	 * 		Uri to fetch
	 * 
	 * @param extraParams
	 * 		Extra parameters
	 * 
	 * @see WebService#get(RESTRequest, String)
	 */
	protected <R extends ResourceRepresentation<?>> RESTRequest<R> get(Class<R> clazz, String uri, Bundle extraParams) {
		RESTRequest<R> request = requestRoutine(clazz, uri);
		initRequest(request, HTTPVerb.GET, uri, extraParams);
		return request;
	}
	
	/**
	 * Initializes and prepares a POST request or retrieve it if already pending.
	 * 
	 * @param clazz
	 * 		Class of ResourceRepresentation holds by request
	 * @param uri
	 * 		URI to retrieve
	 * @param resource
	 * 		Resource to send
	 * 
	 */
	protected <R extends ResourceRepresentation<?>> RESTRequest<R> post(Class<R> clazz, String uri, ResourceRepresentation<?> resource) {
		RESTRequest<R> request = requestRoutine(clazz, uri);
		request.setResourceRepresentation(resource);
		return request;
	}
	
	/**
	 * Initializes and prepares a PUT request or retrieve it if already pending.
	 * 
	 * @param clazz
	 * 		Class of ResourceRepresentation holds by request
	 * @param uri
	 * 		URI to fetch
	 * @param resource
	 * 		Resource to send
	 * 
	 */
	protected <R extends ResourceRepresentation<?>> RESTRequest<R> put(Class<R> clazz, String uri, ResourceRepresentation<?> resource) {
		RESTRequest<R> request = requestRoutine(clazz, uri);
		request.setResourceRepresentation(resource);
		initRequest(request, HTTPVerb.PUT,  uri);
		return request;
	}
	
	/**
	 * Initializes and prepares a DELETE request or retrieve it if already pending.
	 * 
	 * @param clazz
	 * 		Class of ResourceRepresentation holds by request
	 * @param uri
	 * 		Uri to fetch
	 * @param resource
	 * 		Resource to send
	 */
	protected <R extends ResourceRepresentation<?>> RESTRequest<R> delete(Class<R> clazz, String uri, ResourceRepresentation<?> resource) {
		RESTRequest<R> request = requestRoutine(clazz, uri);
		request.setResourceRepresentation(resource);
		initRequest(request, HTTPVerb.DELETE, uri);
		return request;
	}
	
	/**
	 * Create or retrieve {@link RESTRequest} if it is already pending
	 * 
	 * @param clazz
	 * 		Class of ResourceRepresentation holds by request
	 * @param uri
	 * 		Uri to fetch
	 * @return
	 * 		New instance of {@link RESTRequest} or instance already pending
	 */
	protected <R extends ResourceRepresentation<?>> RESTRequest<R> requestRoutine(Class<R> clazz, String uri) {
		RESTRequest<R> request = retrieveRequest(clazz, uri);
		if(null != request) {
			return request;
		}
		request = new RESTRequest<R>(generateID(), clazz);
		mRequestCollection.add(request);
		return request;
	}
	/**
	 * Initializes a request by setting verb and uri
	 * 
	 * @param r
	 * 		Instance of {@link RESTRequest}
	 * @param verb
	 * 		Instance of {@link HTTPVerb}
	 * @param uri
	 * 		Uri to fetch
	 * 
	 * @see WebService#initRequest(RESTRequest, HTTPVerb, String, Bundle)
	 */
	protected void initRequest(RESTRequest<? extends ResourceRepresentation<?>> r, HTTPVerb verb, String uri) {
		r.setVerb(verb);
		r.setUrl(uri);
	}
	
	/**
	 * Initializes a request by setting verb, uri and extra paramaters
	 * 
	 * @param r
	 * 		Instance of {@link RESTRequest}
	 * @param verb
	 * 		Instance of {@link HTTPVerb}
	 * @param uri
	 * 		Uri to fetch
	 * @param extraParams
	 * 		Extra parameters
	 * 
	 * @see WebService#initRequest(RESTRequest, HTTPVerb, String)
	 */
	protected void initRequest(RESTRequest<? extends ResourceRepresentation<?>> r, HTTPVerb verb, String uri, Bundle extraParams) {
		r.setVerb(verb);
		r.setUrl(uri);
		r.setExtraParams(extraParams);
	}
	
	
	/**
	 * Executes a {@link RESTRequest}
	 * 
	 * @param r
	 * 		The request to execute
	 * 
	 * @since 0.7.0
	 */
	public void executeRequest(RESTRequest<? extends ResourceRepresentation<?>> r) {
		if(!r.isPending())
			initAndStartService(r);
	}
	
	/**
	 * Initializes and starts the service if the request has to be re-sent
	 * 
	 * @param request
	 * 		Instance of {@link RESTRequest}
	 * 
	 * @see Processor#checkRequest(RESTRequest)
	 */
	protected void initAndStartService(RESTRequest<? extends ResourceRepresentation<?>> request){
		boolean proceedRequest = true;
		if(request.getVerb() != HTTPVerb.GET)
			proceedRequest = mModule.getProcessor().checkRequest(request);
		if(proceedRequest && !request.isPending()) {
			request.setPending(true);
			Intent i = new Intent(mContext, RestService.class);
			i.setData(Uri.parse(request.getUrl()));
			i.putExtra(RestService.REQUEST_KEY, request);
			i.putExtra(RestService.RECEIVER_KEY, mReceiver);
			
			/* Trigger OnStartedRequest listener */
			for(Iterator<RESTRequest<?>> it = mRequestCollection.iterator(); it.hasNext();) {
				RESTRequest<?> r = it.next();
				if(request.getID().equals(r.getID())) {
					r.triggerOnStartedRequestListeners();
				}
			}
			mContext.startService(i);
		}
		else
			Toast.makeText(mContext, "Request already pending", Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * Generates a unique ID
	 * 
	 * @return
	 * 		Unique ID
	 */
	protected UUID generateID() {
		return UUID.randomUUID();
	}

	/**
	 * Receive result from {@link RestService} and fires callbacks corresponding to the request'state
	 * 
	 * @param resultCode
	 * 		The result code send by the {@link RestService}
	 * @param resultData
	 * 		Bundle with result data
	 */
	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		RESTRequest<?> r = (RESTRequest<?>) resultData.getSerializable(RestService.REQUEST_KEY);
		for(Iterator<RESTRequest<?>> it = mRequestCollection.iterator(); it.hasNext();) {
			RESTRequest<?> request = it.next();
			if(request.getID().equals(r.getID())) {
				try {
					request.setResultStream(r.getResultStream());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				request.setResultCode(resultCode);
				request.setPending(false);
				if(resultCode >= 200 && resultCode <= 210) {
					request.setResourceRepresentation(r.getResourceRepresentation());
					if(request.triggerOnFinishedRequestListeners())
						it.remove();
				}
				else {
					request.setResourceRepresentation(r.getResourceRepresentation());
					if(request.triggerOnFailedRequestListeners())
						it.remove();
				}
				
				Intent i = resultData.getParcelable(RestService.INTENT_KEY);
				mContext.stopService(i);
			}
		}
		/*if(resultCode >= 200 && resultCode <= 210)
			retryFailedRequest();*/
	}
	
	/**
	 * Getter for retrieve specific {@link RESTRequest} in {@link WebService#mRequestCollection}
	 * 
	 * FIXME !
	 * 
	 * @param requestID
	 * 		The {@link RESTRequest} unique ID
	 * @param clazz
	 * 		The {@link RESTRequest}'s {@link ResourceRepresentation} class
	 * 
	 * @return
	 * 		The {@link RESTRequest}
	 * 
	 * @throws RequestNotFoundException if {@link RESTRequest} is not found
	 */
	@SuppressWarnings("unchecked")
	public <T extends ResourceRepresentation<?>> RESTRequest<T> getRequest(String requestID, Class<T> clazz) throws RequestNotFoundException {
		for(RESTRequest<? extends ResourceRepresentation<?>> r : mRequestCollection) {
			if(r.getID().equals(requestID))
				return (RESTRequest<T>) r;
		}
		throw new RequestNotFoundException(requestID);
	}
	
	/**
	 * Provides a way to retry failed requests
	 */
	public void retryFailedRequest() {
		for(RESTRequest<? extends ResourceRepresentation<?>> r : mRequestCollection) {
			ResourceRepresentation<?> resource = r.getResourceRepresentation();
			if(!resource.getTransactingFlag() && resource.getState() != RequestState.STATE_OK && (resource.getResultCode() < 200 || resource.getResultCode() > 210)) {
				initAndStartService(r);
			}
		}
	}
}
