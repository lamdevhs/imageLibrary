import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;


public class Session {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String name;
	public File folder;
	
	// new empty session
	public Session() {}

	public Session(SessionData data) {
		name = data.name;
		if (data.folder != null)
			folder = new File(data.folder);
	}

	public SessionData data() {
		SessionData data = new SessionData();
		data.name = name;
		if (folder != null)
			data.folder = folder.getAbsolutePath();
		else
			data.folder = null;
		return data;
	}

	public int setFolder(File folder) {
		return 0;
		// test if exists,
		// if folder
		// if already is in list
		// returns warning accordingly
	}

}
