package fr.pcreations.labs.RESTDroid;

import java.util.HashMap;
import java.util.UUID;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import fr.pcreations.labs.RESTDroid.Processor.RESTServiceCallback;
import fr.pcreations.labs.RESTDroid.exceptions.DaoFactoryNotInitializedException;

public class RestService extends IntentService{
	
	public final static String REQUEST_KEY = "com.pcreations.restclient.restservice.REQUEST_KEY";
	public final static String RECEIVER_KEY = "com.pcreations.restclient.restservice.RECEIVER_KEY";
	public final static String INTENT_KEY = "com.pcreations.restclient.restservice.INTENT_KEY";
	public final static String TAG = "com.pcreations.restclient.restservice";
	private static Processor processor = null;
	private HashMap<UUID, Intent> mIntentsMap;
	
	public RestService() {
		super("RestService");
		mIntentsMap = new HashMap<UUID, Intent>();
	}

	@Override
	protected void onHandleIntent(Intent intent){
		Bundle bundle = intent.getExtras();
		@SuppressWarnings("unchecked")
		RESTRequest<? extends ResourceRepresentation<?>> r = (RESTRequest<? extends ResourceRepresentation<?>>) bundle.getSerializable(RestService.REQUEST_KEY);
		mIntentsMap.put(r.getID(), intent);
		Log.e(RestService.TAG, "onHandleIntent() "+ String.valueOf(r.getID()));
		RestService.processor.setRESTServiceCallback(new RESTServiceCallback() {

			@Override
			public void callAction(int statusCode, RESTRequest<? extends ResourceRepresentation<?>> r) {
				// TODO Auto-generated method stub
				handleRESTServiceCallback(statusCode, r);
			}
     
        });
        try {
			RestService.processor.process(r);
		} catch (DaoFactoryNotInitializedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void handleRESTServiceCallback(int statusCode, RESTRequest<? extends ResourceRepresentation<?>> r) {
		Intent currentIntent = mIntentsMap.get(r.getID());
		Bundle bundle = currentIntent.getExtras();
		ResultReceiver receiver = bundle.getParcelable(RestService.RECEIVER_KEY);
		//Log.e(RestService.TAG, "resource dans handleRESTServiceCallback = " + r.getResourceRepresentation().toString());
		Bundle resultData = new Bundle();
        resultData.putSerializable(RestService.REQUEST_KEY, r);
        resultData.putParcelable(RestService.INTENT_KEY, currentIntent);
        Log.i(RestService.TAG, "send");
        receiver.send(statusCode, resultData);
	}
	
	public static void setProcessor(Processor processor) {
		RestService.processor = processor;
	}

}
