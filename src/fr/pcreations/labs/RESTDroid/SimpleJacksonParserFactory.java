package fr.pcreations.labs.RESTDroid;


public class SimpleJacksonParserFactory extends ParserFactory{

	@SuppressWarnings("unchecked")
	@Override
	public <P extends Parser<T>, T extends ResourceRepresentation<?>> P getParser(Class<T> clazz) {
		return (P) new SimpleJacksonParser(clazz);
	}

}
