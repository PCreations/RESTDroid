package fr.pcreations.labs.RESTDroid.core;

import fr.pcreations.labs.RESTDroid.core.RequestListeners.OnFailedRequestListener;
import fr.pcreations.labs.RESTDroid.core.RequestListeners.OnFinishedRequestListener;
import fr.pcreations.labs.RESTDroid.core.RequestListeners.OnStartedRequestListener;

/**
 * <b>Enum which represents ListenerState</b>
 * 
 * <p>
 * <ul>
 * <li><b>SET</b> : indicates that the listener is activate and ready to be triggered</li>
 * <li><b>UNSET</b> : indicates that the listener is deactivate. If listener has to be triggered, his state is updated to {@link ListenerState#TRIGGER_ME}</li>
 * <li><b>TRIGGER_ME</b> : indicates that the listener has to be triggered as soon as possible</li>
 * <li><b>TRIGGERED</b> : indicates that the listener has been triggered</li>
 * </ul>
 * </p>
 * 
 * @author Pierre Criulanscy
 * 
 * @version 0.5
 * 
 * @see OnStartedRequestListener
 * @see OnFailedRequestListener
 * @see OnFinishedRequestListener
 */
public enum ListenerState {
	SET,
	UNSET,
	TRIGGER_ME,
	TRIGGERED
}
