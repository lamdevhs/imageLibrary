import java.io.File;
import java.util.ArrayList;
import java.util.Observable;



public class Model extends Observable {
	public void log(String string) {
		U.log("(Model) " + string);
	}

	Locator locator;
	ArrayList<Session> allSessions = new ArrayList<Session>();
	
	Model(Locator loc) {
		locator = loc;
		read();
	}
	
	
	private void read() {
		String[] session_list;
		try {
			session_list = (String[])U.fromXML(locator.all_sessions);
		} catch (ClassCastException cce) {
			log("read: cast impossible");
			session_list = null;
		}
		if (session_list == null) {
			log("session_list null");
		}
		else {
			log("session_list not null");
			for (int i = 0; i < session_list.length; i++) {
				String sessionName = session_list[i];
				SessionFile sessionFile;
				String sessionPath = locator.sessionFile(sessionName);
				try {
					sessionFile = (SessionFile) U.fromXML(sessionPath);
				} catch (ClassCastException cce) {
					log("read: cast impossible for sessionFile " + sessionName);
					sessionFile = null;
				}
				if (sessionFile != null) {
					allSessions.add(new Session(sessionFile));
				}
			}
		}
	}
	
	public String[] getSessionsNames() {
		int size = allSessions.size();
		String[] names = new String[size];
		for (int i = 0; i < size; i++) {
			names[i] = allSessions.get(i).name;
		}
		return names;
	}

	public int createSession(String name) {
		// creating new session
		Session s = new Session();
		int report = this.nameSession(name, s);
		if (report != U.OK)
			// name given is not usable, aborting
			return report;
		
		// else: add new session
		int index = allSessions.size();
		allSessions.add(s);
		
		setChanged();
		notifyObservers();
		
		return index; // return new session index
	}

	// The output is a report on potential failure.
	public int renameSession(String name, int sessionIndex) {
		Session s = allSessions.get(sessionIndex);
		String oldName = s.name;
		int report = nameSession(name, s);
		
		if (report == U.OK) {
			File oldSessionFile = new File(locator.sessionFile(oldName));
			if (oldSessionFile.exists()) {
				log("renameSession: old file exists");
				oldSessionFile.delete();
			}
			setChanged();
			notifyObservers();
		}
		
		return report;
	}
	
	// The output is a report on potential failure.
	private int nameSession(String name, Session s) {
		if (name == null || name.compareTo("") == 0)
			return U.INVALID;
		
		// Checks the name isn't taken already
		int i;
		for (i = 0; i < allSessions.size(); i++) {
			Session other = allSessions.get(i);
			if (s != other && name.compareTo(other.name) == 0) {
				return U.IMPOSSIBLE;
			}
		}
		
		s.name = name;
		
		return U.OK;
	}
	
	public void deleteSession(int sessionIndex) {
		// deleting session file and data
		Session s = allSessions.get(sessionIndex);
		allSessions.remove(sessionIndex);
		File sessionFile = new File(locator.sessionFile(s.name));
		if (sessionFile.exists()) {
			log("deleteSession: session file exists - deleting it");
			sessionFile.delete();
		}
		setChanged();
		notifyObservers();
	}
	

	public void save() {
		U.toXML(getSessionsNames(), locator.all_sessions);
		for (int i = 0; i < allSessions.size(); i++) {
			Session session = allSessions.get(i);
			U.toXML(session.toFile(), locator.sessionFile(session.name));
		}
	}
}
