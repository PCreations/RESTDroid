package fr.pcreations.labs.RESTDroid.modules.defaultModule;

import java.io.InputStream;

import fr.pcreations.labs.RESTDroid.Processor;
import fr.pcreations.labs.RESTDroid.RESTRequest;
import fr.pcreations.labs.RESTDroid.ResourceRepresentation;

public class DefaultProcessor extends Processor{

	@Override
	public void setDaoFactory() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setParserFactory() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void preRequestProcessLogic(
			RESTRequest<? extends ResourceRepresentation<?>> r) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean preGetRequest() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean preDeleteRequest() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected InputStream prePostRequest() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected InputStream prePutRequest() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected <T extends ResourceRepresentation<?>> int postProcess(
			int statusCode, RESTRequest<T> r, InputStream resultStream) {
		// TODO Auto-generated method stub
		return 0;
	}

}
