package fr.pcreations.labs.RESTDroid.core;

import java.util.HashMap;
import java.util.UUID;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import fr.pcreations.labs.RESTDroid.core.Processor.RESTServiceCallback;

/**
 * <b>Service class which hold all process populating an Intent map and calling {@link Processor#process(RESTRequest)}</b>
 * 
 * <p>
 * On the forward path the service receives the Intent sent by {@link WebService} and starts the corresponding REST method.
 * On the return path the service handles the callback fires by {@link Processor} and sends the result to the {@link RestResultReceiver}
 * </p>
 * @author Pierre Criulanscy
 * 
 * @version 0.5
 */
public class RestService extends IntentService{
	
	/**
	 * {@link RESTRequest} key for intent
	 */
	public final static String REQUEST_KEY = "com.pcreations.restclient.restservice.REQUEST_KEY";
	
	/**
	 * Receiver key for intent
	 */
	public final static String RECEIVER_KEY = "com.pcreations.restclient.restservice.RECEIVER_KEY";
	
	/**
	 * Intent key
	 */
	public final static String INTENT_KEY = "com.pcreations.restclient.restservice.INTENT_KEY";
	
	public final static String TAG = "com.pcreations.restclient.restservice";
	
	/**
	 * {@link Processor} to call
	 * 
	 * @see RestService#setProcessor(Processor)
	 */
	private static Processor processor = null;
	
	/**
	 * HashMap which stores intent generated for specific {@link RESTRequest}
	 * 
	 * <p>
	 * <ul>
	 * <li><b>key</b> : ID of {@link RESTRequest}</li>
	 * <li><b>value</b> : Instance of Intent</li>
	 * </ul>
	 * </p>
	 */
	private HashMap<UUID, Intent> mIntentsMap;
	
	/**
	 * Constructor
	 */
	public RestService() {
		super("RestService");
		mIntentsMap = new HashMap<UUID, Intent>();
	}

	/**
	 * Receives the intent and starts the request by calling {@link Processor#process(RESTRequest)}
	 * 
	 * @see Processor
	 * @see RESTServiceCallback
	 */
	@Override
	protected void onHandleIntent(Intent intent){
		Bundle bundle = intent.getExtras();
		@SuppressWarnings("unchecked")
		RESTRequest<ResourceRepresentation<?>> r = (RESTRequest<ResourceRepresentation<?>>) bundle.getSerializable(RestService.REQUEST_KEY);
		mIntentsMap.put(r.getID(), intent);
		RestService.processor.setRESTServiceCallback(new RESTServiceCallback() {

			@Override
			public void callAction(int statusCode, RESTRequest<ResourceRepresentation<?>> r) {
				// TODO Auto-generated method stub
				handleRESTServiceCallback(statusCode, r);
			}
     
        });
		try {
			RestService.processor.process(r);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Handles the binder callback fires by the Processor in {@link Processor#postRequestProcess(int, RESTRequest, java.io.InputStream)}
	 * Current intent is retrieved in {@link RestService#mIntentsMap} in order to send results to {@link RestResultReceiver}
	 * 
	 * @param statusCode
	 * 		The status code resulting of all process
	 * 
	 * @param r
	 * 		The actual {@link RESTRequest}
	 * 
	 * @see Processor#postRequestProcess(int, RESTRequest, java.io.InputStream)
	 * @see RestResultReceiver
	 * @see RestService#mIntentsMap
	 */
	private void handleRESTServiceCallback(int statusCode, RESTRequest<ResourceRepresentation<?>> r) {
		Intent currentIntent = mIntentsMap.get(r.getID());
		Bundle bundle = currentIntent.getExtras();
		ResultReceiver receiver = bundle.getParcelable(RestService.RECEIVER_KEY);
		//Log.e(RestService.TAG, "resource dans handleRESTServiceCallback = " + r.getResourceRepresentation().toString());
		Bundle resultData = new Bundle();
        resultData.putSerializable(RestService.REQUEST_KEY, r);
        resultData.putParcelable(RestService.INTENT_KEY, currentIntent);
        receiver.send(statusCode, resultData);
	}
	
	/**
	 * Setter for the {@link Processor}
	 * 
	 * @param processor
	 * 		The {@link Processor} to set
	 * 
	 * @see RestService#processor
	 */
	public static void setProcessor(Processor processor) {
		RestService.processor = processor;
	}

}
