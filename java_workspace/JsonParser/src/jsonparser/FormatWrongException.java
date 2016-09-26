package jsonparser;

public class FormatWrongException extends Exception {

	private static final long serialVersionUID = 1L;
	public FormatWrongException(String ex){
		System.err.println("JsonString Format Wrong! Exception happen at "+ex);
	}
}
