import java.io.File;
import java.util.ArrayList;
import java.util.Observable;



public class Model extends Observable {
	Paths path;
	//String[] allSessions = {"a", "b", "c"};
	ArrayList<String> sessionsList = new ArrayList<String>();
	ArrayList<Session> allSessions = new ArrayList<Session>();
	// public String[] getSessions() {
	
	Model(Paths p) {
		path = p;
		readSessionsList();
		//createSession("a");
		//createSession("b");
		//createSession("c");
		//createSession("d");
	}
	
	
	private void readSessionsList() {
		ArrayList<String> list;
		try {
			list = (ArrayList<String>)U.fromXML(path.sessionsList);
		} catch (ClassCastException cce) {
			U.log("readSessionsList: cast impossible");
			list = null;
		}
		if (list == null) {
			U.log("sessionsList null");
			//sessionsList = new ArrayList<String>();
		}
		else {
			U.log("sessionsList not null");
			//sessionsList = o;
			for (int i = 0; i < list.size(); i++) {
				String name = list.get(i);
				Session session;
				try {
					session = (Session) U.fromXML(path.sessionFile(name));
				} catch (ClassCastException cce) {
					U.log("readSessionsList: cast impossible for session " + name);
					session = null;
				}
				if (session != null) {
					sessionsList.add(name);
					allSessions.add(session);
				}
			}
		}
	}
	
	public ArrayList<String> getSessionsList() {
		return sessionsList;
	}

	public int createSession(String name) {
		// creating new session
		Session s = new Session();
		int report = this.nameSession(name, s, -1);
		if (report != U.OK)
			// name given is not usable, aborting
			return report;
		
		// else: add new session
		sessionsList.add(name);
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
		int report = nameSession(name, s, sessionIndex);
		
		if (report == U.OK) {
			File oldSessionFile = new File(path.sessionFile(oldName));
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
	public int nameSession(String name, Session s, int sessionIndex) {
		if (name == null || name.compareTo("") == 0)
			return U.INVALID;
		
		// Checks the name isn't taken already
		int i;
		for (i = 0; i < sessionsList.size(); i++) {
			String other = sessionsList.get(i);
			if (i != sessionIndex && name.compareTo(other) == 0) {
				return U.IMPOSSIBLE;
			}
		}
		
		s.name = name;
		if (sessionIndex >= 0) // when renaming
			sessionsList.set(sessionIndex, name);
		
		return U.OK;
	}
	
	public void deleteSession(int sessionIndex) {
		// deleting session file and data
		Session s = allSessions.get(sessionIndex);
		allSessions.remove(sessionIndex);
		sessionsList.remove(sessionIndex);
		File sessionFile = new File(path.sessionFile(s.name));
		if (sessionFile.exists()) {
			U.log("deleteSession: file exists");
			sessionFile.delete();
		}
		setChanged();
		notifyObservers();
	}
	

	public void save() {
		U.toXML(sessionsList, path.sessionsList);
		for (int i = 0; i < sessionsList.size(); i++) {
			Session session = allSessions.get(i);
			U.toXML(session, path.sessionFile(session.name));
		}
	}
}
