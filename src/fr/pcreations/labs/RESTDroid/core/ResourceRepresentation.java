package fr.pcreations.labs.RESTDroid.core;

import java.io.Serializable;

/**
 * <b>Interface which represents application items that have to be synchronized with the server</b>
 * 
 * @author Pierre Criulanscy
 *
 * @param <ID>
 * 		The type of the id field of the item
 * 
 * @version 0.5
 */
public interface ResourceRepresentation<ID> extends Serializable {
	
	/**
	 * Getter for id field
	 * 
	 * @return
	 * 		The item's id
	 * 
	 * @see ResourceRepresentation#setId(Object)
	 */
	abstract public ID getId();
	
	/**
	 * Getter for {@link RequestState}. Used by {@link Processor} to handle server mirroring
	 * 
	 * @return
	 * 		The item's {@link RequestState}
	 * 
	 * @see ResourceRepresentation#setState(int)
	 * @see Processor#mirrorServerState(RESTRequest)
	 * @see Processor#updateLocalResource(int, RESTRequest, java.io.InputStream)
	 */
	abstract public int getState();
	
	/**
	 * Getter for result code. Used by {@link Processor} to handle server mirroring
	 * 
	 * @return
	 * 		The item's result code
	 * 
	 * @see ResourceRepresentation#setResultCode(int)
	 * @see Processor#mirrorServerState(RESTRequest)
	 * @see Processor#updateLocalResource(int, RESTRequest, java.io.InputStream)
	 */
	abstract public int getResultCode();
	
	/**
	 * Getter for transacting flag. Used by {@link Processor} to handle server mirroring
	 *
	 * @return
	 * 		The item's transacting flag
	 * 
	 * @see ResourceRepresentation#setTransactingFlag(boolean)
	 * @see Processor#mirrorServerState(RESTRequest)
	 * @see Processor#updateLocalResource(int, RESTRequest, java.io.InputStream)
	 */
	abstract public boolean getTransactingFlag();
	
	/**
	 * Setter for item's id
	 * 
	 * @param id
	 * 		Id to store in this item
	 * 
	 * @see ResourceRepresentation#getId()
	 * @see Processor#mirrorServerState(RESTRequest)
	 * @see Processor#updateLocalResource(int, RESTRequest, java.io.InputStream)
	 */
	abstract public void setId(ID id);
	
	/**
	 * Setter for item's {@link RequestState}. Used by {@link Processor} to handle server mirroring
	 * 
	 * @param state
	 * 		The {@link RequestState} to store in this item
	 * 
	 * @see ResourceRepresentation#getState()
	 * @see Processor#mirrorServerState(RESTRequest)
	 * @see Processor#updateLocalResource(int, RESTRequest, java.io.InputStream)
	 */
	abstract public void setState(int state);
	
	/**
	 * Setter for transacting flag. Used by {@link Processor} to handle server mirroring
	 * 
	 * @param transacting
	 * 		The transacting flag to store in this item
	 * 
	 * @see ResourceRepresentation#getTransactingFlag()
	 * @see Processor#mirrorServerState(RESTRequest)
	 * @see Processor#updateLocalResource(int, RESTRequest, java.io.InputStream)
	 */
	abstract public void setTransactingFlag(boolean transacting);
	
	/**
	 * Setter for item's result code. Used by {@link Processor} to handle server mirroring
	 * 
	 * @param resultCode
	 * 		The result code to store in this item
	 * 
	 * @see ResourceRepresentation#getResultCode()
	 * @see Processor#mirrorServerState(RESTRequest)
	 * @see Processor#updateLocalResource(int, RESTRequest, java.io.InputStream)
	 */
	abstract public void setResultCode(int resultCode);
}