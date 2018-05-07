import java.io.File;
import java.util.ArrayList;
import java.util.Observable;



public class Model extends Observable {
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
			U.log("read: cast impossible");
			session_list = null;
		}
		if (session_list == null) {
			U.log("session_list null");
		}
		else {
			U.log("session_list not null");
			for (int i = 0; i < session_list.length; i++) {
				String sessionName = session_list[i];
				Session session;
				try {
					session = (Session) U.fromXML(locator.sessionFile(sessionName));
				} catch (ClassCastException cce) {
					U.log("read: cast impossible for session " + sessionName);
					session = null;
				}
				if (session != null) {
					allSessions.add(session);
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
				U.log("renameSession: old file exists");
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
			U.log("deleteSession: session file exists - deleting it");
			sessionFile.delete();
		}
		setChanged();
		notifyObservers();
	}
	

	public void save() {
		U.toXML(getSessionsNames(), locator.all_sessions);
		for (int i = 0; i < allSessions.size(); i++) {
			Session session = allSessions.get(i);
			U.toXML(session, locator.sessionFile(session.name));
		}
	}
}
