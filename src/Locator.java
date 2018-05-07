
public class Locator {
	public String root;
	public String sessionsFolder = "sessions";
	public String all_sessions = "all_sessions.xml";
	
	Locator(String root_) {
		root = root_;
		sessionsFolder = U.catPaths(root, sessionsFolder);
		all_sessions = U.catPaths(sessionsFolder, all_sessions);
	}
	
	// returns the path of the file of the session whose
	// name was given in input
	public String sessionFile(String name) {
		return U.catPaths(sessionsFolder, name + ".xml"); 
	}
}
