package fr.pcreations.labs.RESTDroid.core;

import java.io.Serializable;

/**
 * <b>Simple class to represents serializable header</b>
 * 
 * @author Pierre Criulanscy
 * 
 * @version 0.5
 */
public class SerializableHeader implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3589739936804187767L;
	
	/**
	 * Header's name
	 * 
	 * @see SerializableHeader#getName()
	 * @see SerializableHeader#setName(String)
	 */
	private String mName;
	
	/**
	 * Header's value
	 * 
	 * @see SerializableHeader#getValue()
	 * @see SerializableHeader#setValue(String)
	 */
	private String mValue;
	
	/**
	 * Constructor
	 * 
	 * @param name
	 * 		The header's name
	 * @param value
	 * 		The header's value
	 * 
	 * @see SerializableHeader#mName
	 * @see SerializableHeader#mValue
	 */
	public SerializableHeader(String name, String value) {
		mName = name;
		mValue = value;
	}
	
	/**
	 * Set the header's name
	 * 
	 * @param name
	 * 		Header's name
	 * 
	 * @see SerializableHeader#mName
	 * @see SerializableHeader#getName()
	 */
	public void setName(String name) {
		mName = name;
	}
	
	/**
	 * Set the header's value
	 * 
	 * @param value
	 * 		Header's value
	 * 
	 * @see SerializableHeader#mValue
	 * @see SerializableHeader#getValue()
	 */
	public void setValue(String value) {
		mValue = value;
	}
	
	/**
	 * Getter for header's name
	 * 
	 * @return
	 * 		Header's name
	 * 
	 * @see SerializableHeader#mName
	 * @see SerializableHeader#setName(String)
	 */
	public String getName() {
		return mName;
	}
	
	/**
	 * Getter for header's value
	 * 
	 * @return
	 * 		Header's value
	 * 
	 * @see SerializableHeader#mValue
	 * @see SerializableHeader#setValue(String)
	 */
	public String getValue() {
		return mValue;
	}
	
}
