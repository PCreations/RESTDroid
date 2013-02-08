package fr.pcreations.labs.RESTDroid.core;

import java.io.InputStream;
import java.sql.SQLException;

import android.util.Log;
import fr.pcreations.labs.RESTDroid.core.HttpRequestHandler.ProcessorCallback;
import fr.pcreations.labs.RESTDroid.exceptions.DaoFactoryNotInitializedException;
import fr.pcreations.labs.RESTDroid.exceptions.ParsingException;

public abstract class Processor {

	protected HttpRequestHandler mHttpRequestHandler;
	protected RESTServiceCallback mRESTServiceCallback;
	protected DaoFactory mDaoFactory; //could be a DatabaseHelper;
	protected ParserFactory mParserFactory;
	
	public Processor() {
		mHttpRequestHandler = new HttpRequestHandler();
	}

	abstract protected void preRequestProcess(RESTRequest<ResourceRepresentation<?>> r) throws Exception;
	abstract protected void preGetRequest(RESTRequest<ResourceRepresentation<?>> r);
	abstract protected void preDeleteRequest(RESTRequest<ResourceRepresentation<?>> r);
	abstract protected InputStream prePostRequest(RESTRequest<ResourceRepresentation<?>> r);
	abstract protected InputStream prePutRequest(RESTRequest<ResourceRepresentation<?>> r);
	
	abstract protected int postRequestProcess(int statusCode, RESTRequest<ResourceRepresentation<?>> r, InputStream resultStream);
	
	protected void process(RESTRequest<ResourceRepresentation<?>> r) throws Exception {
		preRequestProcess(r);
		processRequest(r);
	}
	
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
	
	protected void handleHttpRequestHandlerCallback(int statusCode, RESTRequest<ResourceRepresentation<?>> request, InputStream resultStream) {
		Log.i(RestService.TAG, "handleHTTpREquestHandlerCallback start");
        /*Log.i(RestService.TAG, "RESPONSE SERVER JSON = " + inputStreamToString(resultStream));
        Log.i(RestService.TAG, "Breakpoint");*/
        statusCode = postRequestProcess(statusCode, request, resultStream);
        mRESTServiceCallback.callAction(statusCode, request);
	}
	
	public void setRESTServiceCallback(RESTServiceCallback callback) {
		mRESTServiceCallback = callback;
	}
	
	public void setDaoFactory(DaoFactory d) {
		mDaoFactory = d;
	}
	public void setParserFactory(ParserFactory p) {
		mParserFactory = p;
	}
	
	public interface RESTServiceCallback {
		abstract public void callAction(int statusCode, RESTRequest<ResourceRepresentation<?>> r);
	}
	
	protected void mirrorServerState(RESTRequest<ResourceRepresentation<?>> r){
		Log.i(RestService.TAG, "preRequestProcess start");
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
                    case GET:
                            resource.setState(RequestState.STATE_RETRIEVING);
                            break;
                    case POST:
                            resource.setState(RequestState.STATE_POSTING);
                            break;
                    case PUT:
                            resource.setState(RequestState.STATE_UPDATING);
                            break;
                    case DELETE:
                            resource.setState(RequestState.STATE_DELETING);
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
	
	protected int updateLocalResource(int statusCode, RESTRequest<ResourceRepresentation<?>> r, InputStream resultStream) {
		try {
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
            if(r.getResourceRepresentation() != null) { //POST PUT GET
            	DaoAccess<ResourceRepresentation<?>> dao = getResourceDao(r.getResourceRepresentation());
                dao.updateOrCreate(r.getResourceRepresentation());
                Log.d(RestService.TAG, "handleHttpRequestHandlerCallback");
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		return statusCode;
	}
	
	protected DaoAccess<ResourceRepresentation<?>> getResourceDao(ResourceRepresentation<?> r) {
		return mDaoFactory.getDao(r.getClass());
	}

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
	
	protected <R extends ResourceRepresentation<?>> R parseToObject(InputStream content, Class<R> clazz) throws ParsingException {
		Parser<R> p = mParserFactory.getParser(clazz);
		return p.parseToObject(content);
	}
	
	protected <R extends ResourceRepresentation<?>> InputStream parseToInputStream(R resource) throws ParsingException {
		Parser<R> p = mParserFactory.getParser(resource.getClass());
		return p.parseToInputStream(resource);
	}

}
