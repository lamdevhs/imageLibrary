import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;


public class Session extends Observable {
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

	public int setFolder(File folder_) {
		if (folder_ == null)
			// should never happen...
			return U.INVALID;
		if (!folder_.exists() || !folder_.isDirectory())
			// should never happen...
			return U.INVALID;

		folder = folder_;
		
		setChanged();
		notifyObservers();

		return U.OK;
	}

}
