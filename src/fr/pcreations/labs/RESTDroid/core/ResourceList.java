package fr.pcreations.labs.RESTDroid.core;

import java.util.ArrayList;

public class ResourceList<T> implements ResourceRepresentation<T>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -156700486459176529L;
	
	private ArrayList<T> mResourcesList;
	
	public ResourceList() {
		mResourcesList = new ArrayList<T>();
	}

	public ArrayList<T> getResourcesList() {
		return mResourcesList;
	}

	public void setResourcesList(ArrayList<T> mResourcesList) {
		this.mResourcesList = mResourcesList;
	}

	@Override
	public T getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getState() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getResultCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean getTransactingFlag() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setId(T id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setState(int state) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTransactingFlag(boolean transacting) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setResultCode(int resultCode) {
		// TODO Auto-generated method stub
		
	}
	
	

}
