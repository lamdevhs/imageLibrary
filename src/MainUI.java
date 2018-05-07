import javax.swing.*;


public class MainUI extends JFrame {
	private Model model;
	
	MainUI(Model model_){
		model = model_;
		
		setSize(200,200);

		SessionManager sessionManager = new SessionManager(this, model);
		int res = sessionManager.open();
		U.log("MainUI: res: " + res);
		setVisible(true);
	}
}
