package fr.pcreations.labs.RESTDroid.modules.defaultModule;

import fr.pcreations.labs.RESTDroid.core.DaoFactory;
import fr.pcreations.labs.RESTDroid.core.Module;
import fr.pcreations.labs.RESTDroid.core.ParserFactory;
import fr.pcreations.labs.RESTDroid.core.Processor;

public class DefaultModule extends Module {

	@Override
	public Processor setProcessor() {
		return new DefaultProcessor();
	}

	@Override
	public ParserFactory setParserFactory() {
		return new SimpleJacksonParserFactory();
	}

	@Override
	public DaoFactory setDaoFactory() {
		return new ORMLiteDaoFactory();
	}

}
