package fr.pcreations.labs.RESTDroid.core;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
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
	 * Constructor
	 */
	public RestService() {
		super("RestService");
	}
	
	private Intent mCurrentIntent;
	
	/**
	 * Receives the intent and starts the request by calling {@link Processor#process(RESTRequest)}
	 * 
	 * @see Processor
	 * @see RESTServiceCallback
	 */
	@Override
	protected void onHandleIntent(Intent intent){
		mCurrentIntent = intent;
		Bundle bundle = intent.getExtras();
		@SuppressWarnings("unchecked")
		RESTRequest<? extends Resource> r = (RESTRequest<? extends Resource>) bundle.getSerializable(RestService.REQUEST_KEY);
		RestService.processor.setRESTServiceCallback(new RESTServiceCallback() {

			@Override
			public void callAction(int statusCode, RESTRequest<? extends Resource> r) {
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
	 * Current intent is retrieved in {@link RestService#intentsMap} in order to send results to {@link RestResultReceiver}
	 * 
	 * @param statusCode
	 * 		The status code resulting of all process
	 * 
	 * @param r
	 * 		The actual {@link RESTRequest}
	 * 
	 * @see Processor#postRequestProcess(int, RESTRequest, java.io.InputStream)
	 * @see RestResultReceiver
	 * @see RestService#intentsMap
	 */
	private void handleRESTServiceCallback(int statusCode, RESTRequest<? extends Resource> r) {
		Bundle bundle = mCurrentIntent.getExtras();
		ResultReceiver receiver = bundle.getParcelable(RestService.RECEIVER_KEY);
		//Log.e(RestService.TAG, "resource dans handleRESTServiceCallback = " + r.getResourceRepresentation().toString());
		Bundle resultData = new Bundle();
        resultData.putSerializable(RestService.REQUEST_KEY, r);
        resultData.putParcelable(RestService.INTENT_KEY, mCurrentIntent);
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
