import java.util.ArrayList;


public class Model {
	//String[] allSessions = {"a", "b", "c"};
	ArrayList<String> allSessions = new ArrayList<String>();
	// public String[] getSessions() {
	
	Model() {
		allSessions.add("a");
		allSessions.add("b");
		allSessions.add("c");
		allSessions.add("d");
	}
	
	public ArrayList<String> getSessions() {
		return allSessions;
	}

	public int createSession(String name) {
		// creating new session
		int report = this.renameSession(name, 0);
		if (report != U.OK) return report;
		// else: add new session
		return 42; // return session index
	}

	public int renameSession(String name, int sessionIndex) {
		// TODO Auto-generated method stub
		if (name == null || name.compareTo("") == 0) return U.INVALID;
		return U.OK;
	}
	
	public void deleteSession(int sessionIndex) {
		// deleting session file and data
	}
}
