package fr.pcreations.labs.RESTDroid.core;

import java.io.InputStream;
import java.sql.SQLException;

import android.util.Log;
import fr.pcreations.labs.RESTDroid.core.HttpRequestHandler.ProcessorCallback;
import fr.pcreations.labs.RESTDroid.exceptions.DaoFactoryNotInitializedException;
import fr.pcreations.labs.RESTDroid.exceptions.ParsingException;

/**
 * <b>Processor class handle request call via {@link HttpRequestHandler}. It manages pre-process and post-process logic . Pre-process and post-process are defined as hooks in order to defined specific logics for you're application</b>
 * 
 * @author Pierre Criulanscy
 * 
 * @version 0.5
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
	 * Instance of {@link DaoFactory}
	 */
	
	protected DaoFactory mDaoFactory;
	
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
	abstract protected void preRequestProcess(RESTRequest<ResourceRepresentation<?>> r) throws Exception;
	
	/**
	 * Hook for logic just before a GET request
	 * 
	 * @param r
	 * 		The {@link RESTRequest} instance
	 */
	abstract protected void preGetRequest(RESTRequest<ResourceRepresentation<?>> r);
	
	/**
	 * Hook for logic just before a DELETE request
	 * 
	 * @param r
	 * 		The {@link RESTRequest} instance
	 */
	abstract protected void preDeleteRequest(RESTRequest<ResourceRepresentation<?>> r);
	
	/**
	 * Hook for logic just before a POST request
	 * 
	 * @param r
	 * 		The {@link RESTRequest} instance
	 * 
	 * @return
	 * 		This method must returns an InputStream (typically result of {@link ResourceRepresentation} parsing. See {@link Parser})
	 */
	abstract protected InputStream prePostRequest(RESTRequest<ResourceRepresentation<?>> r);
	
	/**
	 * Hook for logic just before a PUT request
	 * 
	 * @param r
	 * 		The {@link RESTRequest} instance
	 * 
	 * @return
	 * 		This method must returns an InputStream (typically result of {@link ResourceRepresentation} parsing. See {@link Parser})
	 */
	abstract protected InputStream prePutRequest(RESTRequest<ResourceRepresentation<?>> r);
	
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
	abstract protected int postRequestProcess(int statusCode, RESTRequest<ResourceRepresentation<?>> r, InputStream resultStream);
	
	/**
	 * Calls {@link Processor#preRequestProcess(RESTRequest)} and {@link Processor#process(RESTRequest)}
	 * 
	 * @param r
	 * 		The actual {@link RESTRequest}
	 * 
	 * @throws Exception
	 */
	protected void process(RESTRequest<ResourceRepresentation<?>> r) throws Exception {
		preRequestProcess(r);
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
	protected void processRequest(RESTRequest<ResourceRepresentation<?>> r) {
		Log.i(RestService.TAG, "processRequest start");
		mHttpRequestHandler.setProcessorCallback(new ProcessorCallback() {

			@Override
			public void callAction(int statusCode, RESTRequest<ResourceRepresentation<?>> request, InputStream resultStream) {
				// TODO Auto-generated method stub
				handleHttpRequestHandlerCallback(statusCode, request, resultStream);
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
		Log.i(RestService.TAG, "processRequest end");
	}
	
	/**
	 * Handles the binder callback from {@link HttpRequestHandler} by updating status code calling {@link Processor#preRequestProcess(RESTRequest)} hook and fires {@link RESTServiceCallback}
	 * 
	 * @param statusCode
	 *		Status code returned by {@link HttpRequestHandler}
	 *
	 * @param request
	 *		The actual {@link ResourceRepresentation}
	 *
	 * @param resultStream
	 * 		The server response
	 */
	protected void handleHttpRequestHandlerCallback(int statusCode, RESTRequest<ResourceRepresentation<?>> request, InputStream resultStream) {
		Log.i(RestService.TAG, "handleHTTpREquestHandlerCallback start");
        statusCode = postRequestProcess(statusCode, request, resultStream);
        mRESTServiceCallback.callAction(statusCode, request);
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
	 * Set the {@link DaoFactory}
	 * 
	 * @param d
	 * 		Instance of {@link DaoFactory}
	 * 
	 * @see Processor#mDaoFactory
	 */
	public void setDaoFactory(DaoFactory d) {
		mDaoFactory = d;
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
		abstract public void callAction(int statusCode, RESTRequest<ResourceRepresentation<?>> r);
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
	protected void mirrorServerState(RESTRequest<ResourceRepresentation<?>> r){
        if(r.getVerb() != HTTPVerb.GET) {
            if(null == mDaoFactory) {
                try {
					throw new DaoFactoryNotInitializedException();
				} catch (DaoFactoryNotInitializedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            if(null != r.getResourceRepresentation()) {
                ResourceRepresentation<?> resource = r.getResourceRepresentation();
                resource.setTransactingFlag(true);
                Log.e(RestService.TAG, "resource dans preProcessRequest = " + r.getResourceRepresentation().toString());
                switch(r.getVerb()) {
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
                try {
                    DaoAccess<ResourceRepresentation<?>> dao = mDaoFactory.getDao(resource.getClass());
                    dao.updateOrCreate(resource);
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        Log.i(RestService.TAG, "preRequestProcess end");
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
	protected int updateLocalResource(int statusCode, RESTRequest<ResourceRepresentation<?>> r, InputStream resultStream) {
		try {
			if(statusCode >= 200 && statusCode <= 210) {
	            if(r.getVerb() == HTTPVerb.DELETE) {
	            	ResourceRepresentation<?> resource = r.getResourceRepresentation();
	                Log.i(RestService.TAG, "AFTER DELETE RESOURCE AND BEFORE DELETE LOCAL DB RESOURCE = " + resource.toString());
	                DaoAccess<ResourceRepresentation<?>> dao = getResourceDao(resource);
	                dao.deleteResource(resource);
	            }
	            else if(r.getVerb() == HTTPVerb.GET) {
	                Log.i("debug", "Delete old resource !");
	                try {
	    				r.setResourceRepresentation(parseToObject(resultStream, r.getResourceClass()));
	    			} catch (ParsingException e) {
	    				statusCode = -10;
	    				e.printStackTrace();
	    			}
	                ResourceRepresentation<?> resource = r.getResourceRepresentation();
	                DaoAccess<ResourceRepresentation<?>> dao = getResourceDao(resource);
	                ResourceRepresentation<?> oldResource = dao.findById(resource.getId());
	                dao.deleteResource(oldResource);
	            }
			}
            if(r.getResourceRepresentation() != null) { //POST PUT GET
            	DaoAccess<ResourceRepresentation<?>> dao = getResourceDao(r.getResourceRepresentation());
            	r.getResourceRepresentation().setResultCode(statusCode);
            	r.getResourceRepresentation().setTransactingFlag(false);
            	if(statusCode >= 200 && statusCode <= 210)
            		r.getResourceRepresentation().setState(RequestState.STATE_OK);
                dao.updateOrCreate(r.getResourceRepresentation());
                Log.d(RestService.TAG, "handleHttpRequestHandlerCallback");
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		return statusCode;
	}
	
	/**
	 * Shortcut to retrieve Dao from Processor via {@link DaoFactory}
	 * 
	 * @param r
	 * 		The {@link ResourceRepresentation} you want the dao
	 * 
	 * @return
	 * 		Instance of {@link DaoAccess}
	 */
	protected DaoAccess<ResourceRepresentation<?>> getResourceDao(ResourceRepresentation<?> r) {
		return mDaoFactory.getDao(r.getClass());
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
	 * Handles caching logic. TODO Has to be delegate to a CacheManager
	 * 
	 * @param request
	 * 		The actual {@link ResourceRepresentation}
	 * 
	 * @return
	 * 		False if the request has to be resent, true otherwise
	 */
	public boolean checkRequest(RESTRequest<? extends ResourceRepresentation<?>> request) {
		/*Log.e(RestService.TAG, "LISTE RESOURCES = ");
		List<ResourceRepresentation<?>> resourcesList;
		ResourceRepresentation<?> requestResource = request.getResourceRepresentation();
		DaoAccess<ResourceRepresentation<?>> dao = mDaoFactory.getDao(requestResource.getClass());
		try {
			resourcesList = dao.queryForAll();
			for(ResourceRepresentation<?> r : resourcesList) {
				Log.e(RestService.TAG, r.toString());
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Log.e(RestService.TAG, "FIN LISTE RESOURCES");
		try {
			ResourceRepresentation<?> resource = dao.findById(request.getResourceRepresentation().getId());
			if(null != resource) {
				Log.w(RestService.TAG, resource.toString());
				if(!resource.getTransactingFlag()) {
					if(resource.getResultCode() == 200) {
						Log.e(RestService.TAG, "La requête s'est bien déroulée : je ne fait rien et je renvoie false");
						return false;
					}
					Log.e(RestService.TAG, "La requête s'est mal déroulée : je la relance et je renvoie true");
					return true;
				}
				Log.e(RestService.TAG, "La requête est en cours : j'attends et je renvoie false");
				return false;
			}
			Log.e(RestService.TAG, "Je ne manipule pas la même resource et je renvoie true");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		return true;
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
	protected <R extends ResourceRepresentation<?>> R parseToObject(InputStream content, Class<R> clazz) throws ParsingException {
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
	protected <R extends ResourceRepresentation<?>> InputStream parseToInputStream(R resource) throws ParsingException {
		Parser<R> p = mParserFactory.getParser(resource.getClass());
		return p.parseToInputStream(resource);
	}

}
