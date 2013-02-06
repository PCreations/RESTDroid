package fr.pcreations.labs.RESTDroid.exceptions;

@SuppressWarnings("serial")
public class ProcessorNotInitializedException extends Exception {

	public ProcessorNotInitializedException() {
		System.out.println("Processor not ininitialized in RestService");
	}
}
