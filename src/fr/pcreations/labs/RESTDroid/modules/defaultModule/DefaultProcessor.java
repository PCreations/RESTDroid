package fr.pcreations.labs.RESTDroid.modules.defaultModule;

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
import fr.pcreations.labs.RESTDroid.core.WebService;
import fr.pcreations.labs.RESTDroid.exceptions.DaoFactoryNotInitializedException;
import fr.pcreations.labs.RESTDroid.exceptions.ParsingException;

public class DefaultProcessor extends Processor{

	@Override
	public void setDaoFactory() {
		// TODO Auto-generated method stub
		mDaoFactory = new ORMLiteDaoFactory();
	}

	@Override
	public void setParserFactory() {
		// TODO Auto-generated method stub
		
	}

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
		try {
			ResourceRepresentation<?> resource = r.getResourceRepresentation();
			InputStream is = mParserFactory.getParser(resource.getClass()).parseToInputStream(resource);
			//TODO afficher is
			//Log.i(RestService.TAG, "INPUT STREAM NOTE = " + inputStreamToString(is));
			return is;
		} catch (ParsingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected InputStream prePutRequest(RESTRequest<? extends ResourceRepresentation<?>> r) {
		return prePostRequest(r);
	}

	@Override
	protected <T extends ResourceRepresentation<?>> int postRequestProcess(
			int statusCode, RESTRequest<T> r, InputStream resultStream) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void preRequestProcess(
			RESTRequest<? extends ResourceRepresentation<?>> r)
			throws DaoFactoryNotInitializedException {
		 //GESTION BDD
        Log.i(RestService.TAG, "preRequestProcess start");
        if(WebService.FLAG_RESOURCE && r.getVerb() != HTTPVerb.GET) {
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
                                processRequest(r);
                        } catch (SQLException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                        }
                }
        }
        else
                processRequest(r);
        Log.i(RestService.TAG, "preRequestProcess end");
		
	}

}
