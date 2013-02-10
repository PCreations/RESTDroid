package fr.pcreations.labs.RESTDroid.core;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * <b>Specific implementation of {@link ResultReceiver} for {@link RESTDroid}</b>
 * 
 * @author Pierre Criulanscy
 * 
 * @version 0.5
 *
 */
public class RestResultReceiver extends ResultReceiver {
	private Receiver mReceiver;

    public RestResultReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle resultData);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }
}
