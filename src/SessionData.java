import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


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
