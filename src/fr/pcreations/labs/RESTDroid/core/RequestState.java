package fr.pcreations.labs.RESTDroid.core;

/**
 * <b>Constant class which defined request state. Used to mirror the server state of {@link ResourceRepresentation}</b>
 * 
 * @author Pierre Criulanscy
 * 
 * @version 0.5
 *
 */
public class RequestState {
	
	/**
	 * Request has succeed
	 */
	
	public final static int STATE_OK = 0;
	
	/**
	 * Request is updating data to the server
	 */
	public final static int STATE_UPDATING = 1;
	
	/**
	 * Request is deleting data to the server
	 */
	public final static int STATE_DELETING = 2;
	
	/**
	 * Request is posting data to the server
	 */
	public final static int STATE_POSTING = 3;
	
}