package fr.pcreations.labs.RESTDroid;

import java.io.Serializable;

import org.apache.http.message.BasicHeader;

public class SerializableHeader extends BasicHeader implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3589739936804187767L;
	
	private String mName;
	private String mValue;
	
	public SerializableHeader(String name, String value) {
		super(name, value);
		// TODO Auto-generated constructor stub
	}
	
	public void setName(String name) {
		mName = name;
	}
	
	public void setValue(String value) {
		mValue = value;
	}
	
	public String getName() {
		return mName;
	}
	
	public String getValue() {
		return mValue;
	}
	
}
