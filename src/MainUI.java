import javax.swing.*;


public class MainUI extends JFrame {
	private App app;
	private Model model;
	
	MainUI(Model model_, App app_){
		model = model_;
		app = app_;
		
		setSize(200,200);

		SessionManager sessionManager = new SessionManager(this, app, model);
		int res = sessionManager.open();
		U.log("MainUI: res: " + res);
		setVisible(true);
	}
}
