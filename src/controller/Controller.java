package controller;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import exception.InvalidStudentIdException;
import exception.StudentIdAlreadyInUseException;
import model.Student;
import networking.ObserverService;
import networking.SecretaryService;
import view.StudentListView;

public class Controller implements Observer{
	private static Controller instance;
	private SecretaryService service;
	private ObserverService ob_service;
	private StudentListView mainView;

	private Controller() {
		service = new SecretaryService();
		ob_service = new ObserverService();
	}

	public static Controller getInstance() {
		if (instance == null) {
			instance = new Controller();
		}

		return instance;
	}

	public boolean login(String username, String password) {
		if (service.login(username, password)) {
			mainView = new StudentListView();
			mainView.setVisible(true);
			return true;
		}

		return false;
	}

	@Override
	public void update(Observable arg0, Object students) {
		System.out.println("Update in controller");
		mainView.update(null, students);
	}

	public ArrayList<Student> getFilteredStudents(String group) {
		return service.getFilteredStudents(group);
	}

	public void addStudent(String id, String name, String group) throws StudentIdAlreadyInUseException {
		service.addStudent(id, name, group);
	}

	public void runUpdateListener() {
		instance.ob_service.listenForUpdates();
	}

	public void updateStudent(Student s) throws InvalidStudentIdException {
		service.updateStudent(s);
	}

	public void getAllStudents() {
		service.requestUpdate();
		
	}
}
