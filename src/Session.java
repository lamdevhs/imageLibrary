import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;


public class Session {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String name;
	public ArrayList<File> folders;
	
	// new empty session
	public Session() {}

	public Session(SessionFile file) {
		name = file.name;
	}

	public SessionFile toFile() {
		SessionFile file = new SessionFile();
		file.name = name;
		return file;
	}

	public int addFolder(File folder) {
		return 0;
		// test if exists,
		// if folder
		// if already is in list
		// returns warning accordingly
	}

}
