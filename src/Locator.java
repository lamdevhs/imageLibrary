
// Used to get the location of the application's datafiles
// the root attribute is meant to be the folder containing
// all those datafiles. For now it's the folder appdata/
// at the root of the project.
public class Locator {
	public String root;
	public String sessionsFolder = "sessions";
	public String all_sessions = "sessions.xml";
	
	Locator(String root_) {
		root = root_;
		sessionsFolder = U.catPaths(root, sessionsFolder);
		all_sessions = U.catPaths(root, all_sessions);
	}
	
	// returns the path of the datafile of the session whose
	// name was given in input
	public String sessionFile(String name) {
		return U.catPaths(sessionsFolder, name + ".xml"); 
	}
}
