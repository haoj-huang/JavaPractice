package jsonparser;

public class NotJsonStringException extends Exception {

	private static final long serialVersionUID = 1L;
	public NotJsonStringException(){
		System.err.println("Not A JsonString!");
	}
}
