
public class Paths {
	public String root;
	public String sessionsFolder = "sessions";
	public String sessionsList = "all_sessions.xml";
	
	Paths(String root_) {
		root = root_;
		sessionsFolder = U.catPaths(root, sessionsFolder);
		sessionsList = U.catPaths(sessionsFolder, sessionsList);
	}
	
	// returns the path of the file of the session whose
	// name was given in input
	public String sessionFile(String name) {
		return U.catPaths(sessionsFolder, name + ".xml"); 
	}
}
