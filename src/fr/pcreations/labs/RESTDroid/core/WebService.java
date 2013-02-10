package fr.pcreations.labs.RESTDroid.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
 * @version 0.5
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
	 * Factory of {@link RESTRequest}. Adds {@link RESTRequest} instance in {@link WebService#mRequestCollection}
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
	 */
	public <T extends ResourceRepresentation<?>> RESTRequest<T> newRequest(Class<T> clazz) {
		RESTRequest<T> r = new RESTRequest<T>(generateID(), clazz);
		mRequestCollection.add(r);
		return r;
	}
	
	/**
	 * Initializes and prepares a GET request
	 * 
	 * @param r
	 * 		Instance of {@link RESTRequest}
	 * 
	 * @param uri
	 * 		Uri to fetch
	 * 
	 * @see WebService#get(RESTRequest, String, Bundle)
	 */
	protected void get(RESTRequest<? extends ResourceRepresentation<?>> r, String uri) {
		initRequest(r, HTTPVerb.GET,  uri);
		initAndStartService(r);
	}
	
	/**
	 * Initializes and prepares a GET request with extra parameters
	 * 
	 * @param r
	 * 		Instance of {@link RESTRequest}
	 * 
	 * @param uri
	 * 		Uri to fetch
	 * 
	 * @param extraParams
	 * 		Extra parameters
	 * 
	 * @see WebService#get(RESTRequest, String)
	 */
	protected void get(RESTRequest<? extends ResourceRepresentation<?>> r, String uri, Bundle extraParams) {
		initRequest(r, HTTPVerb.GET, uri, extraParams);
		initAndStartService(r);
	}
	
	/**
	 * Initializes and prepares a POST request
	 * 
	 * @param r
	 * 		Instance of {@link RESTRequest}
	 * @param uri
	 * 		URI to retrieve
	 * @param resource
	 * 		Resource to send
	 * 
	 */
	protected void post(RESTRequest<? extends ResourceRepresentation<?>> r, String uri, ResourceRepresentation<?> resource) {
		//initPostHeaders(r);
		r.setResourceRepresentation(resource);
		initRequest(r, HTTPVerb.POST,  uri);
		initAndStartService(r);
	}
	
	/**
	 * Initializes and prepares a PUT request
	 * 
	 * @param r
	 * 		Instance of {@link RESTRequest}
	 * @param uri
	 * 		URI to fetch
	 * @param resource
	 * 		Resource to send
	 * 
	 */
	protected void put(RESTRequest<? extends ResourceRepresentation<?>> r, String uri, ResourceRepresentation<?> resource) {
		r.setResourceRepresentation(resource);
		initRequest(r, HTTPVerb.PUT,  uri);
		initAndStartService(r);
	}
	
	/**
	 * Initializes and prepares a DELETE request
	 * 
	 * @param r
	 * 		Instance of {@link RESTRequest}
	 * @param uri
	 * 		Uri to fetch
	 * @param resource
	 * 		Resource to send
	 */
	protected void delete(RESTRequest<? extends ResourceRepresentation<?>> r, String uri, ResourceRepresentation<?> resource) {
		r.setResourceRepresentation(resource);
		initRequest(r, HTTPVerb.DELETE, uri);
		initAndStartService(r);
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
		if(proceedRequest) {
			Intent i = new Intent(mContext, RestService.class);
			i.setData(Uri.parse(request.getUrl()));
			i.putExtra(RestService.REQUEST_KEY, request);
			i.putExtra(RestService.RECEIVER_KEY, mReceiver);
			
			/* Fire OnStartedRequest listener */
			for(Iterator<RESTRequest<?>> it = mRequestCollection.iterator(); it.hasNext();) {
				RESTRequest<?> r = it.next();
				if(request.getID().equals(r.getID())) {
					if(r.getOnStartedRequestListener() != null)
						r.getOnStartedRequestListener().onStartedRequest();
				}
			}
			mContext.startService(i);
		}
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
	 */
	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		RESTRequest<?> r = (RESTRequest<?>) resultData.getSerializable(RestService.REQUEST_KEY);
		//Log.w(RestService.TAG, "dans onReceiveResult" + r.getResourceRepresentation().toString());
		for(Iterator<RESTRequest<?>> it = mRequestCollection.iterator(); it.hasNext();) {
			RESTRequest<?> request = it.next();
			if(request.getID().equals(r.getID())) {
				if(resultCode >= 200 && resultCode <= 210) {
					if(request.getOnFinishedRequestListener() != null) {
						request.setResourceRepresentation(r.getResourceRepresentation());
						request.getOnFinishedRequestListener().onFinishedRequest(resultCode);
					}
				}
				else {
					if(request.getOnFailedRequestListener() != null) {
						request.setResourceRepresentation(r.getResourceRepresentation());
						request.getOnFailedRequestListener().onFailedRequest(resultCode);
					}
				}
				
				Intent i = resultData.getParcelable(RestService.INTENT_KEY);
				mContext.stopService(i);
				if(resultCode >= 200 && resultCode <= 210)
					it.remove();
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
	public <T extends ResourceRepresentation<?>> RESTRequest<T> getRequest(UUID requestID, Class<T> clazz) throws RequestNotFoundException {
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
