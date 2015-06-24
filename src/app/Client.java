package app;

import controller.Controller;
import view.LoginView;

public class Client {
	static LoginView v = new LoginView();
	
	public static void main(String[] args) {
		v.setVisible(true);
		
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				Controller.getInstance().runUpdateListener();
			}
		});
		t.start();
		
	}
}