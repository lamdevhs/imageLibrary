
public class App {
	Model model;
	
	App(String rootpath) {
		Locator locator = new Locator(rootpath);
		model = new Model(locator);
		new MainFrame(model, this);
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
