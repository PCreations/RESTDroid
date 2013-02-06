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
import android.util.Log;
import fr.pcreations.labs.RESTDroid.core.RESTRequest.OnFinishedRequestListener;
import fr.pcreations.labs.RESTDroid.exceptions.RequestNotFoundException;

public abstract class WebService implements RestResultReceiver.Receiver{

	public static final boolean FLAG_RESOURCE = true;
	protected RestResultReceiver mReceiver;
	protected Context mContext;
	protected Processor mProcessor;
	protected OnFinishedRequestListener onFinishedRequestListener;
	protected List<RESTRequest<?>> mRequestCollection;
	
	protected WebService() {}
	
	public WebService(Context context) {
		super();
		mContext = context;
		setProcessor();
		RestService.setProcessor(mProcessor);
		mReceiver = new RestResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        mRequestCollection = new ArrayList<RESTRequest<?>>();
	}
	
	public void registerModule(Module m) {
		m.init();
		RestService.setProcessor(m.getProcessor());
	}
	
	public <T extends ResourceRepresentation<?>> RESTRequest<T> newRequest(Class<T> clazz) {
		RESTRequest<T> r = new RESTRequest<T>(generateID(), clazz);
		mRequestCollection.add(r);
		Log.i(RestService.TAG, "Requête ajoutée ID = " + String.valueOf(r.getID()));
		return r;
	}
	
	protected abstract void setProcessor();
	
	protected void get(RESTRequest<?> r, String uri) {
		Log.e(RestService.TAG, "WebService.get("+uri+")");
		initRequest(r, HTTPVerb.GET,  uri);
		initAndStartService(r);
	}
	
	protected void get(RESTRequest<?> r, String uri, Bundle extraParams) {
		Log.d(RestService.TAG, "WebService.get()");
		initRequest(r, HTTPVerb.GET, uri, extraParams);
		initAndStartService(r);
	}
	
	protected void post(RESTRequest<?> r, String string, ResourceRepresentation<?> resource) {
		//initPostHeaders(r);
		Log.e(RestService.TAG, "WebService.post("+string+")");
		r.setResourceRepresentation(resource);
		initRequest(r, HTTPVerb.POST,  string);
		initAndStartService(r);
	}
	
	protected void put(RESTRequest<?> r, String string, ResourceRepresentation<?> resource) {
		Log.e(RestService.TAG, "WebService.put("+string+")");
		r.setResourceRepresentation(resource);
		initRequest(r, HTTPVerb.PUT,  string);
		initAndStartService(r);
	}
	
	protected void delete(RESTRequest<?> r, String string, ResourceRepresentation<?> resource) {
		Log.e(RestService.TAG, "WebService.delete("+string+")");
		r.setResourceRepresentation(resource);
		initRequest(r, HTTPVerb.DELETE, string);
		initAndStartService(r);
	}
	
	protected void initRequest(RESTRequest<?> r, HTTPVerb verb, String uri) {
		r.setVerb(verb);
		r.setUrl(uri);
	}
	
	protected void initRequest(RESTRequest<?> r, HTTPVerb verb, String uri, Bundle extraParams) {
		r.setVerb(verb);
		r.setUrl(uri);
		r.setExtraParams(extraParams);
	}
	
	protected void initAndStartService(RESTRequest<?> request){
		Log.i(RestService.TAG, "Init service request id = " + String.valueOf(request.getID()));
		boolean proceedRequest = true;
		if(FLAG_RESOURCE && request.getVerb() != HTTPVerb.GET)
			proceedRequest = mProcessor.checkRequest(request);
		if(proceedRequest) {
			Intent i = new Intent(mContext, RestService.class);
			i.setData(Uri.parse(request.getUrl()));
			i.putExtra(RestService.REQUEST_KEY, request);
			i.putExtra(RestService.RECEIVER_KEY, mReceiver);
			Log.d(RestService.TAG, "startService");
			
			/* Fire OnStartedRequest listener */
			for(Iterator<RESTRequest<?>> it = mRequestCollection.iterator(); it.hasNext();) {
				RESTRequest<?> r = it.next();
				Log.e(RestService.TAG, "R == null " + String.valueOf(null == r));
				Log.e(RestService.TAG, "R = " + r.toString());
				if(request.getID().equals(r.getID())) {
					if(r.getOnStartedRequestListener() != null)
						r.getOnStartedRequestListener().onStartedRequest();
				}
			}
			mContext.startService(i);
		}
	}
	
	protected void initPostHeaders(RESTRequest<?> r) {
		//TODO Make clean header class
		/*r.getHeaders().add(r.new SerializableHeader("Accept", "application/json"));
		r.getHeaders().add(r.new SerializableHeader("Content-type", "application/json"));*/
	}
	
	protected UUID generateID() {
		return UUID.randomUUID();
	}
	

	public void setOnFinishedRequestListener(OnFinishedRequestListener listener) {
		onFinishedRequestListener = listener;
	}

	
	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		Log.d(RestService.TAG, "onReceiveResult");
		RESTRequest<?> r = (RESTRequest<?>) resultData.getSerializable(RestService.REQUEST_KEY);
		//Log.w(RestService.TAG, "dans onReceiveResult" + r.getResourceRepresentation().toString());
		for(Iterator<RESTRequest<?>> it = mRequestCollection.iterator(); it.hasNext();) {
			RESTRequest<?> request = it.next();
			if(request.getID().equals(r.getID())) {
				if(resultCode == 200) {
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
				if(resultCode == 200)
					it.remove();
			}
		}
		Log.e(RestService.TAG, "onReceiveResult : mRequestCollection.size() = " + String.valueOf(mRequestCollection.size()));
	}
	
	@SuppressWarnings("unchecked")
	public <T extends ResourceRepresentation<?>> RESTRequest<T> getRequest(UUID requestID, Class<T> clazz) throws RequestNotFoundException {
		for(RESTRequest<? extends ResourceRepresentation<?>> r : mRequestCollection) {
			if(r.getID().equals(requestID))
				return (RESTRequest<T>) r;
		}
		throw new RequestNotFoundException(requestID);
	}
}
