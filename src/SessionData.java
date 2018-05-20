import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


// Intermediate class used to store the long-term important
// data of a Session before writing it to its datafile.
// Reversely, is also the class used to read the data from a
// session datafile before creating a Session object from it,
// using Session.fromData()
public class SessionData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public SessionData() {}

	public String name;
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public String folder;
	public String getFolder() { return folder; }
	public void setFolder(String folder) { this.folder = folder; }
	
	public HashMap<String, ArrayList<String>> tags = new HashMap<String, ArrayList<String>>();

	public HashMap<String, ArrayList<String>> getTags() {
		return tags;
	}
	public void setTags(HashMap<String, ArrayList<String>> tags) {
		this.tags = tags;
	}
}
