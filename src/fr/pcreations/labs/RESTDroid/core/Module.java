package fr.pcreations.labs.RESTDroid.core;

abstract public class Module {

	protected Processor mProcessor;
	
	public void init() {
		mProcessor = setProcessor();
		mProcessor.setParserFactory(setParserFactory());
		mProcessor.setDaoFactory(setDaoFactory());
	}
	
	abstract public Processor setProcessor();
	abstract public ParserFactory setParserFactory();
	abstract public DaoFactory setDaoFactory();

	public Processor getProcessor() {
		return mProcessor;
	}
	
	
	
}
