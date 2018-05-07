
public class App {
	Model model;
	
	App(String rootpath) {
		Paths path = new Paths(rootpath);
		model = new Model(path);
		new MainUI(model, this);
	}
	
	public void quit() {
		model.save();
		U.log("quit app");
		System.exit(0);
	}
	
	public static void main(String[] args) {
		new App(".");
	}
}
