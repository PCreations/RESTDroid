package fr.pcreations.labs.RESTDroid.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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
		setDaoFactory();
		setParserFactory();
	}

	abstract public void setDaoFactory();
	abstract public void setParserFactory();
	abstract protected void preRequestProcess(RESTRequest<? extends ResourceRepresentation<?>> r) throws DaoFactoryNotInitializedException;
	abstract protected void preGetRequest(RESTRequest<? extends ResourceRepresentation<?>> r);
	abstract protected void preDeleteRequest(RESTRequest<? extends ResourceRepresentation<?>> r);
	abstract protected InputStream prePostRequest(RESTRequest<? extends ResourceRepresentation<?>> r);
	abstract protected InputStream prePutRequest(RESTRequest<? extends ResourceRepresentation<?>> r);
	
	abstract protected <T extends ResourceRepresentation<?>> int postRequestProcess(int statusCode, RESTRequest<T> r, InputStream resultStream);
	
	protected void process(RESTRequest<? extends ResourceRepresentation<?>> r) throws DaoFactoryNotInitializedException {
		preRequestProcess(r);
		processRequest(r);
	}
	
	protected void processRequest(RESTRequest<? extends ResourceRepresentation<?>> r) {
		Log.i(RestService.TAG, "processRequest start");
		mHttpRequestHandler.setProcessorCallback(new ProcessorCallback() {

			@Override
			public void callAction(int statusCode, RESTRequest<? extends ResourceRepresentation<?>> request, InputStream resultStream) {
				// TODO Auto-generated method stub
				handleHttpRequestHandlerCallback(statusCode, request, resultStream);
			}
			
		});
		//TODO handle other verb
		ResourceRepresentation<?> resource = r.getResourceRepresentation();
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
	
	/*private InputStream stringToInputStream(String str) {
    	// convert String into InputStream
    	InputStream is = new ByteArrayInputStream(str.getBytes());
     
    	// read it with BufferedReader
    	BufferedReader br = new BufferedReader(new InputStreamReader(is));
     
    	String line;
    	try {
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
			try {
				br.close();
				return is;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return null;
    }*/
	
	
	protected void handleHttpRequestHandlerCallback(int statusCode, RESTRequest<? extends ResourceRepresentation<?>> request, InputStream resultStream) {
		Log.i(RestService.TAG, "handleHTTpREquestHandlerCallback start");
        /*Log.i(RestService.TAG, "RESPONSE SERVER JSON = " + inputStreamToString(resultStream));
        Log.i(RestService.TAG, "Breakpoint");*/
        if(postRequestProcess(statusCode, request, resultStream) != statusCode) {
                mRESTServiceCallback.callAction(statusCode, request);
        }
        else {
            //TODO setup StrategyProcess to decide what to do here
            //By default store object
            try {
                    if(WebService.FLAG_RESOURCE && request.getVerb() == HTTPVerb.DELETE) {
                            ResourceRepresentation<?> resource = request.getResourceRepresentation();
                            Log.i(RestService.TAG, "AFTER DELETE RESOURCE AND BEFORE DELETE LOCAL DB RESOURCE = " + resource.toString());
                            DaoAccess<ResourceRepresentation<?>> dao = mDaoFactory.getDao(resource.getClass());
                            dao.deleteResource(resource);
                    }
                    else if(WebService.FLAG_RESOURCE && request.getVerb() == HTTPVerb.GET) {
                            Log.i("debug", "Delete old resource !");
                            //TODO handle parsing here with ParserFactory
                            ResourceRepresentation<?> resource = request.getResourceRepresentation();
                            DaoAccess<ResourceRepresentation<?>> dao = mDaoFactory.getDao(resource.getClass());
                            ResourceRepresentation<?> oldResource = dao.findById(resource.getId());
                            dao.deleteResource(oldResource);
                    }
                    if(WebService.FLAG_RESOURCE && request.getResourceRepresentation() != null) { //POST PUT GET
                            ResourceRepresentation<?> resource = request.getResourceRepresentation();
                            DaoAccess<ResourceRepresentation<?>> dao = mDaoFactory.getDao(resource.getClass());
                            dao.updateOrCreate(request.getResourceRepresentation());
                            Log.d(RestService.TAG, "handleHttpRequestHandlerCallback");
                    }
                    mRESTServiceCallback.callAction(statusCode, request);
            } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            }
            Log.i(RestService.TAG, "handleHTTpREquestHandlerCallback end");
        }
	}
	
	public void setRESTServiceCallback(RESTServiceCallback callback) {
		mRESTServiceCallback = callback;
	}
	
	public interface RESTServiceCallback {
		abstract public void callAction(int statusCode, RESTRequest<? extends ResourceRepresentation<?>> r);
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
	
	private String inputStreamToString(InputStream is) {
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

}
