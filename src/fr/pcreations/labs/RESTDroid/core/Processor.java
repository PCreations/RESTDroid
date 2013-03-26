package fr.pcreations.labs.RESTDroid.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import android.content.Context;
import android.util.Log;
import fr.pcreations.labs.RESTDroid.core.HttpRequestHandler.ProcessorCallback;
import fr.pcreations.labs.RESTDroid.exceptions.ParsingException;
import fr.pcreations.labs.RESTDroid.exceptions.PersistableFactoryNotInitializedException;

/**
 * <b>Processor class handle request call via {@link HttpRequestHandler}. It manages pre-process and post-process logic . Pre-process and post-process are defined as hooks in order to defined specific logics for you're application</b>
 * 
 * @author Pierre Criulanscy
 * 
 * @version 0.7.2
 *
 */
public abstract class Processor {

	/**
	 * Instance of {@link HttpRequestHandler}
	 */
	protected HttpRequestHandler mHttpRequestHandler;
	
	/**
	 * Instance of {@link RESTServiceCallback}
	 */
	
	protected RESTServiceCallback mRESTServiceCallback;
	
	/**
	 * Instance of {@link PersistableFactory}
	 */
	
	protected PersistableFactory mPersistableFactory;
	
	/**
	 * Instance of {@link ParserFactory}
	 */
	protected ParserFactory mParserFactory;
	
	/**
	 * Constructor
	 */
	public Processor() {
		mHttpRequestHandler = new HttpRequestHandler();
	}

	/**
	 * Hook for logic just before the request is executed (regardless of the request's HTTP verb)
	 * 
	 * @param r
	 * 		The {@link RESTRequest} instance
	 * 
	 * @throws Exception
	 */
	abstract protected void preRequestProcess(RESTRequest<? extends Resource> r) throws Exception;
	
	/**
	 * Hook for logic just before a GET request
	 * 
	 * @param r
	 * 		The {@link RESTRequest} instance
	 */
	abstract protected void preGetRequest(RESTRequest<? extends Resource> r);
	
	/**
	 * Hook for logic just before a DELETE request
	 * 
	 * @param r
	 * 		The {@link RESTRequest} instance
	 */
	abstract protected void preDeleteRequest(RESTRequest<? extends Resource> r);
	
	/**
	 * Hook for logic just before a POST request
	 * 
	 * @param r
	 * 		The {@link RESTRequest} instance
	 * 
	 * @return
	 * 		This method must returns an InputStream (typically result of {@link ResourceRepresentation} parsing. See {@link Parser})
	 */
	abstract protected InputStream prePostRequest(RESTRequest<? extends Resource> r);
	
	/**
	 * Hook for logic just before a PUT request
	 * 
	 * @param r
	 * 		The {@link RESTRequest} instance
	 * 
	 * @return
	 * 		This method must returns an InputStream (typically result of {@link ResourceRepresentation} parsing. See {@link Parser})
	 */
	abstract protected InputStream prePutRequest(RESTRequest<? extends Resource> r);
	
	/**
	 * Hook for logic just after the request is executed (regardless of the request's HTTP verb)
	 * 
	 * @param statusCode
	 * 		The request status code
	 * 
	 * @param r
	 * 		The actual {@link ResourceRepresentation}
	 * 
	 * @param resultStream
	 * 		The server response
	 * 
	 * @return
	 * 		The status code
	 */
	abstract protected int postRequestProcess(int statusCode, RESTRequest<? extends Resource> r, InputStream resultStream);
	
	/**
	 * Calls {@link Processor#preRequestProcess(RESTRequest)} and {@link Processor#process(RESTRequest)}
	 * 
	 * @param r
	 * 		The actual {@link RESTRequest}
	 * 
	 * @throws Exception
	 */
	protected void process(RESTRequest<? extends Resource> r) throws Exception {
		preRequestProcess(r);
		if(r.getVerb() == HTTPVerb.GET) {
			final File file = new File(CacheManager.getCacheDir(), String.valueOf(r.getUrl().hashCode()));
			Log.e(RestService.TAG, "LAST MODIFIED BEFORE UDPATE = " + String.valueOf(file.lastModified()));
			InputStream cacheStream = CacheManager.getRequestFromCache(r);
			if(cacheStream != null) {
				r.setResultStream(cacheStream);
				r.setResource(parseToObject(r.getResultStream(), r.getResourceClass()));
				r.setResultCode(210);
				mRESTServiceCallback.callAction(210, r);
			}
			else
				processRequest(r);
		}
		else
			processRequest(r);
	}
	
	/**
	 * Calls {@link HttpRequestHandler} API based on {@link RESTRequest} {@link HTTPVerb} after firing hooks
	 * 
	 * @param r
	 * 		The actual {@link ResourceRepresentation}
	 * 
	 * @see Processor#preGetRequest(RESTRequest)
	 * @see Processor#prePostRequest(RESTRequest)
	 * @see Processor#prePutRequest(RESTRequest)
	 * @see Processor#preDeleteRequest(RESTRequest)
	 * @see ProcessorCallback
	 */
	protected void processRequest(RESTRequest<? extends Resource> r) {
		mHttpRequestHandler.setProcessorCallback(new ProcessorCallback() {

			@Override
			public void callAction(int statusCode, RESTRequest<? extends Resource> request) {
				// TODO Auto-generated method stub
				handleHttpRequestHandlerCallback(statusCode, request);
			}
			
		});
		InputStream is;
		switch(r.getVerb()) {
			case GET:
				preGetRequest(r);
				mHttpRequestHandler.get(r);
				break;
			case POST:
				is = prePostRequest(r);
				mHttpRequestHandler.post(r, is);
				break;
			case PUT:
				is = prePutRequest(r);
				mHttpRequestHandler.put(r, is);
				break;
			case DELETE:
				preDeleteRequest(r);
				mHttpRequestHandler.delete(r);
				
		}
	}
	
	/**
	 * Handles the binder callback from {@link HttpRequestHandler} by updating status code calling {@link Processor#postRequestProcess(int, RESTRequest, InputStream)} hook, set the result stream in {@link RESTRequest} and fires {@link RESTServiceCallback}
	 * 
	 * @param statusCode
	 *		Status code returned by {@link HttpRequestHandler}
	 *
	 * @param request
	 *		The actual {@link ResourceRepresentation}
	 *
	 */
	protected void handleHttpRequestHandlerCallback(int statusCode, RESTRequest<? extends Resource> request) {
        statusCode = postRequestProcess(statusCode, request, request.getResultStream());
		mRESTServiceCallback.callAction(statusCode, request);
	}
	
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
	 * Set the {@link RESTServiceCallback}
	 * 
	 * @param callback
	 * 		Instance of {@link RESTServiceCallback}
	 * 
	 * @see Processor#mRESTServiceCallback
	 */
	public void setRESTServiceCallback(RESTServiceCallback callback) {
		mRESTServiceCallback = callback;
	}
	
	/**
	 * Set the {@link PersistableFactory}
	 * 
	 * @param d
	 * 		Instance of {@link PersistableFactory}
	 * 
	 * @see Processor#mPersistableFactory
	 */
	public void setPersistableFactory(PersistableFactory d) {
		mPersistableFactory = d;
	}
	
	/**
	 * Set the {@link ParserFactory}
	 * 
	 * @param p
	 * 		Instance of {@link ParserFactory}
	 * 
	 * @see Processor#mParserFactory
	 */
	public void setParserFactory(ParserFactory p) {
		mParserFactory = p;
	}
	
	/**
	 * <b>Binder callback for {@link RestService}</b>
	 * 
	 * @author Pierre Criulanscy
	 * 
	 * @version 0.5
	 */
	public interface RESTServiceCallback {
		
		/**
		 * Logic to executes
		 * 
		 * @param statusCode
		 * 		The status code returned by {@link Processor}
		 * 
		 * @param r
		 * 		The actual {@link ResourceRepresentation}
		 */
		abstract public void callAction(int statusCode, RESTRequest<? extends Resource> r);
	}
	
	/**
	 * Mirrors the server state before the request is executed. Sets the transacting flag of {@link ResourceRepresentation} to true and the {@link RequestState} based on {@link HTTPVerb} :
	 * 
	 * <p>
	 * <ul>
	 * <li>{@link HTTPVerb#POST} : {@link RequestState#STATE_POSTING}</li>
	 * <li>{@link HTTPVerb#PUT} : {@link RequestState#STATE_UPDATING}</li>
	 * <li>{@link HTTPVerb#DELETE} : {@link RequestState#STATE_DELETING}</li>
	 * </ul>
	 * </p>
	 * 
	 * @param r
	 * 		The actual {@link RESTRequest}
	 */
	@SuppressWarnings("unchecked")
	protected void mirrorServerState(RESTRequest<? extends Resource> r){
        if(r.getVerb() != HTTPVerb.GET) {
            if(null == mPersistableFactory) {
                try {
					throw new PersistableFactoryNotInitializedException();
				} catch (PersistableFactoryNotInitializedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            if(null != r.getResource()) {
                Resource resource = r.getResource();
                try {
	                if(resource instanceof ResourcesList) {
	                	for(Iterator<ResourceRepresentation<?>> it = (Iterator<ResourceRepresentation<?>>) ( (ResourcesList) resource).getResourcesList().iterator(); it.hasNext();) {
	                		mirrorServerStateRoutine(r.getVerb(), it.next());
	                	}
	                }
	                else 
	                	mirrorServerStateRoutine(r.getVerb(), (ResourceRepresentation<?>)resource);
                } catch(Exception e) {
	        	   e.printStackTrace();
	           }
            }
        }
	}
	
	protected void mirrorServerStateRoutine(HTTPVerb verb, ResourceRepresentation<?> resource) throws Exception {
		resource.setTransactingFlag(true);
        switch(verb) {
            case POST:
                    resource.setState(RequestState.STATE_POSTING);
                    break;
            case PUT:
                    resource.setState(RequestState.STATE_UPDATING);
                    break;
            case DELETE:
                    resource.setState(RequestState.STATE_DELETING);
                    break;
			default:
				break;
        }
        Persistable<ResourceRepresentation<?>> persistable = mPersistableFactory.getPersistable(resource.getClass());
        persistable.updateOrCreate(resource);
	}
	
	/**
	 * Updates the local resource to mirror the server state after the request is executed. Sets the transacting flag to false, updates the result code. When deleting a resource, this resource is not deleted from local database until the DELETE request has succeed.
	 * When a GET request is finished and succeed, old local resource is first deleted before re-added in database. 
	 * 
	 * @param statusCode
	 * 		The status code resulting of request
	 * 
	 * @param r
	 * 		The actual {@link RESTRequest}
	 * 
	 * @param resultStream
	 * 		The server's response
	 * 
	 * @return
	 * 		The updated (or not) status code
	 */
	@SuppressWarnings("unchecked")
	protected int updateLocalResource(int statusCode, RESTRequest<? extends Resource> r, InputStream resultStream) {
		try {
			if(statusCode >= 200 && statusCode <= 210) {
	            if(r.getVerb() == HTTPVerb.DELETE) {
	            	Resource resource = r.getResource();
	            	if(resource instanceof ResourcesList) {
	                	for(Iterator<ResourceRepresentation<?>> it = (Iterator<ResourceRepresentation<?>>) ((ResourcesList) resource).getResourcesList().iterator(); it.hasNext();) {
	                		Persistable<Resource> persistable = getResourcePersistable(resource);
	    	                persistable.deleteResource(it.next());
	                	}
	                }
	                else {
	                	Persistable<Resource> persistable = getResourcePersistable(resource);
		                persistable.deleteResource((ResourceRepresentation<?>)resource);
	                }
	            }
	            else if(r.getVerb() == HTTPVerb.GET) {
	                try {
	    				r.setResource(parseToObject(resultStream, r.getResourceClass()));
	    			} catch (ParsingException e) {
	    				statusCode = -10;
	    				e.printStackTrace();
	    			}
	                Resource resource = r.getResource();
	                Persistable<Resource> persistable;
	                if(resource instanceof ResourcesList) {
	                	for(Iterator<ResourceRepresentation<?>> it = (Iterator<ResourceRepresentation<?>>) ((ResourcesList) resource).getResourcesList().iterator(); it.hasNext();) {
	                		ResourceRepresentation<?> current = it.next();
	                		persistable = getResourcePersistable(current);
	                		ResourceRepresentation<?> oldResource = (ResourceRepresentation<?>) persistable.findById(current.getId());
	    	                persistable.deleteResource(oldResource);
	                	}
	                }
	                else {
	                	persistable = getResourcePersistable(resource);
		                ResourceRepresentation<?> oldResource = (ResourceRepresentation<?>) persistable.findById(((ResourceRepresentation<?>) resource).getId());
		                persistable.deleteResource(oldResource);
	                }
	            }
			}
            if(r.getResource() != null) { //POST PUT GET
            	Resource resource = r.getResource();
            	if(resource instanceof ResourcesList) {
            		for(Iterator<ResourceRepresentation<?>> it = (Iterator<ResourceRepresentation<?>>) ((ResourcesList) resource).getResourcesList().iterator(); it.hasNext();) {
                		updateLocalResourceRoutine(statusCode, it.next());
                	}
            	}
            	else {
            		updateLocalResourceRoutine(statusCode, (ResourceRepresentation<?>)resource);
            	}
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		return statusCode;
	}
	
	protected void updateLocalResourceRoutine(int statusCode, ResourceRepresentation<?> resource) throws Exception {
		Log.i("UPDATE", "updateLocalResource");
		Persistable<Resource> persistable = getResourcePersistable(resource);
		resource.setResultCode(statusCode);
		resource.setTransactingFlag(false);
    	if(statusCode >= 200 && statusCode <= 210)
    		resource.setState(RequestState.STATE_OK);
    	persistable.updateOrCreate(resource);
	}
	
	/**
	 * Shortcut to retrieve Persistable class from Processor via {@link PersistableFactory}
	 * 
	 * @param resource
	 * 		The {@link ResourceRepresentation} you want the Persistable class
	 * 
	 * @return
	 * 		Instance of {@link Persistable}
	 */
	protected Persistable<Resource> getResourcePersistable(Resource resource) {
		return mPersistableFactory.getPersistable(resource.getClass());
	}

	/**
	 * Provides a way to know if a specific {@link ResourceRepresentation} is remotely sync or not.
	 * 
	 * @param r
	 * 		The {@link ResourceRepresentation} you wan to know if it's remotely synchronized
	 * 
	 * @return
	 * 		True if the resource is actually synchronized with the server, false otherwise
	 */
	public static boolean isRemotelySync(ResourceRepresentation<?> r) {
		if(r.getTransactingFlag())
			return false;
		if(r.getState() == RequestState.STATE_OK)
			return true;
		if(r.getResultCode() >= 200 && r.getResultCode() <= 210)
			return true;
		return false;
	}
	
	/**
	 * Handles caching logic. This method can be implemented in specific Processor to always return true in order to avoid caching. TODO Has to be delegate to a CacheManager and handle refresh with timestamp
	 * 
	 * @param request
	 * 		The actual {@link ResourceRepresentation}
	 * 
	 * @return
	 * 		True if the request has to be resent, false otherwise
	 */
	@SuppressWarnings("unchecked")
	public boolean checkRequest(RESTRequest<? extends Resource> request) {
		if(null == mPersistableFactory)
			return true;
		Resource requestResource = request.getResource();
		Persistable<ResourceRepresentation<?>> persistable = mPersistableFactory.getPersistable(requestResource.getClass());
		Resource resource = request.getResource();
		if(resource instanceof ResourceRepresentation) {
			try {
				ResourceRepresentation<?> r = persistable.findById(((ResourceRepresentation<?>) request.getResource()).getId());
				if(null != r) {
					if(!r.getTransactingFlag()) {
						if(r.getResultCode() == 200) {
							return true;
						}
						return true;
					}
					return false;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		else {
			int countResourcesNotSync = 0;
			for(Iterator<ResourceRepresentation<?>> it = (Iterator<ResourceRepresentation<?>>) ((ResourcesList) resource).getResourcesList().iterator(); it.hasNext();) {
        		ResourceRepresentation<?> current = it.next();
        		if(!current.getTransactingFlag()) {
        			if(!(current.getResultCode() >= 200 && current.getResultCode() <= 210))
        				countResourcesNotSync++;
        		}
        	}
			if(countResourcesNotSync == ((ResourcesList) resource).getResourcesList().size()) //request has failed
				return true;
			return false;
		}
	}
	
	/**
	 * Shortcut to parse an InputStream to object from Processor via retrieving the {@link Parser} thanks to {@link ParserFactory}
	 * 
	 * @param content
	 * 		The data you want to parse
	 * 
	 * @param clazz
	 * 		The Class object of the {@link ResourceRepresentation} you want to be mapped
	 * 
	 * @return
	 * 		An instance of {@link ResourceRepresentation}
	 * 
	 * @throws ParsingException
	 */
	protected <R extends Resource> R parseToObject(InputStream content, Class<R> clazz) throws ParsingException {
		Parser<R> p = mParserFactory.getParser(clazz);
		return p.parseToObject(content);
	}
	
	/**
	 * Shortcut to parse an instance of {@link ResourceRepresentation} to InputStream from Processor via retrieving the {@link Parser} thanks to {@link ParserFactory}
	 * 
	 * @param resource
	 * 		Instance of {@link ResourceRepresentation} you want to parse
	 * 
	 * @return
	 * 		InputStream holding data parsed from {@link ResourceRepresentation}
	 * 
	 * @throws ParsingException
	 */
	protected <R extends Resource> InputStream parseToInputStream(R resource) throws ParsingException {
		Parser<R> p = mParserFactory.getParser(resource.getClass());
		return p.parseToInputStream(resource);
	}

}
