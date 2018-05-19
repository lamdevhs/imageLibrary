import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observable;



public class Model extends Observable {
	public static void log(String string) {
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
				SessionData sessionData;
				String sessionPath = locator.sessionFile(sessionName);
				try {
					sessionData = (SessionData) U.fromXML(sessionPath);
				} catch (ClassCastException cce) {
					log("read: cast impossible for sessionData " + sessionName);
					sessionData = null;
				}
				if (sessionData != null) {
					allSessions.add(Session.fromData(sessionData));
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

	// this function should never be called without first
	// having checked the validity of name and folder.
	public int addNewSession(String name, File folder) {
		// creating new session
		Session s = new Session(name, folder);

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
		int report = checkNewSessionName(name, s);
		
		if (report == U.OK) { // valid new name
			s.name = name; // we do the renaming
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

		// may gods above and below have mercy on your
		// computer if this function isn't called before
		// (re)naming a session...
	public int checkNewSessionName(String name, Session s) {
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
		
		return U.OK;
	}
	
	public void deleteSession(int sessionIndex) {
		Session s = allSessions.get(sessionIndex);
		allSessions.remove(sessionIndex);
		File sessionFile = new File(locator.sessionFile(s.name));
		if (sessionFile.exists()) {
			// deleting session datafile
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
			U.toXML(session.data(), locator.sessionFile(session.name));
		}
	}
}
