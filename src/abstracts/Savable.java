package abstracts;

public interface Savable {
	/*
	 * return a string representation of object data.
	 * data should be delimited with "delims[dIndex]" and 
	 * other savable data objects should be saved with
	 * x.save(saver, delims, dIndex+1);
	 */
	String save(Saver saver, char[] delims, int dIndex);
	
	/*
	 * Given the string representation of an object's data
	 * parse the object and return a new instance of this object's class.
	 * data is delimited by delims[dIndex]. Nested savable object data can
	 * be rebuilt into objects by calling x.load(saver, delims, dIndex+1, z)
	 * where "z" represents a scanner chunk of that object's string-represented
	 * data.
	 */
	Object load(Saver saver, char[] delims, int dIndex, String data);
}
