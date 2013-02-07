package fr.pcreations.labs.RESTDroid.modules.ORMliteJacksonModule;

import java.io.InputStream;
import java.sql.SQLException;

import android.util.Log;
import fr.pcreations.labs.RESTDroid.core.DaoAccess;
import fr.pcreations.labs.RESTDroid.core.HTTPVerb;
import fr.pcreations.labs.RESTDroid.core.Processor;
import fr.pcreations.labs.RESTDroid.core.RESTRequest;
import fr.pcreations.labs.RESTDroid.core.RequestState;
import fr.pcreations.labs.RESTDroid.core.ResourceRepresentation;
import fr.pcreations.labs.RESTDroid.core.RestService;
import fr.pcreations.labs.RESTDroid.exceptions.DaoFactoryNotInitializedException;
import fr.pcreations.labs.RESTDroid.exceptions.ParsingException;

public class ORMliteJacksonProcessor extends Processor{

	@Override
	protected void preGetRequest(RESTRequest<? extends ResourceRepresentation<?>> r) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void preDeleteRequest(RESTRequest<? extends ResourceRepresentation<?>> r) {
		// TODO Auto-generated method stub
	}

	@Override
	protected InputStream prePostRequest(RESTRequest<? extends ResourceRepresentation<?>> r) {
		InputStream is = null;
		try {
			is =  parseToInputStream(r.getResourceRepresentation());
		} catch (ParsingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return is;
	}

	@Override
	protected InputStream prePutRequest(RESTRequest<? extends ResourceRepresentation<?>> r) {
		return prePostRequest(r);
	}

	@Override
	protected <T extends ResourceRepresentation<?>> int postRequestProcess(
		int statusCode, RESTRequest<T> r, InputStream resultStream) {
        //By default store object
        try {
            if(r.getVerb() == HTTPVerb.DELETE) {
                ResourceRepresentation<?> resource = r.getResourceRepresentation();
                Log.i(RestService.TAG, "AFTER DELETE RESOURCE AND BEFORE DELETE LOCAL DB RESOURCE = " + resource.toString());
                DaoAccess<ResourceRepresentation<?>> dao = mDaoFactory.getDao(r.getResourceClass());
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
                DaoAccess<ResourceRepresentation<?>> dao = mDaoFactory.getDao(resource.getClass());
                ResourceRepresentation<?> oldResource = dao.findById(resource.getId());
                dao.deleteResource(oldResource);
            }
            if(r.getResourceRepresentation() != null) { //POST PUT GET
                DaoAccess<ResourceRepresentation<?>> dao = mDaoFactory.getDao(r.getResourceClass());
                dao.updateOrCreate(r.getResourceRepresentation());
                Log.d(RestService.TAG, "handleHttpRequestHandlerCallback");
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.i(RestService.TAG, "handleHTTpREquestHandlerCallback end");
        return statusCode;
	}

	@Override
	protected void preRequestProcess(
			RESTRequest<? extends ResourceRepresentation<?>> r)
			throws DaoFactoryNotInitializedException {
		 //GESTION BDD
        Log.i(RestService.TAG, "preRequestProcess start");
        if(r.getVerb() != HTTPVerb.GET) {
                if(null == mDaoFactory) {
                        throw new DaoFactoryNotInitializedException();
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

}
