package networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import exception.InvalidStudentIdException;
import exception.StudentIdAlreadyInUseException;
import model.Student;

public class SecretaryService {
	private Socket s;
	private DataOutputStream os;
	private DataInputStream is;

	public SecretaryService() {
		connectToServer();
	}

	public void connectToServer() {
		try {
			s = new Socket("127.0.0.1", 8000);
			s.setKeepAlive(true);
			os = new DataOutputStream(s.getOutputStream());
			is = new DataInputStream(s.getInputStream());
		} catch (IOException e) {
			System.out.println("Cannot connect to server");
			e.printStackTrace();
		}
	}

	public void logout() {
		try {
			if (s.isConnected() == false) {
				// logout message: int of value -1
				os.writeInt(-1);
				s.close();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public boolean login(String username, String password) {
		try {
			int action = 0;
			os.writeInt(action);
			os.flush();

			// send username & password
			os.writeInt(username.length());
			os.flush();
			os.writeBytes(username);
			os.flush();
			os.writeInt(password.length());
			os.flush();
			os.writeBytes(password);
			int res = is.readInt();

			if (res == 1) {
				return true;
			}

			return false;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		return false;
	}

	public int addStudent(String id, String name, String group) throws StudentIdAlreadyInUseException {
		try {
			if (s.isConnected() == false) {
				connectToServer();
			}

			int act = 1;
			os.writeInt(act);
			os.flush();
			os.writeInt(id.length());
			os.flush();
			os.writeBytes(id);
			os.flush();
			os.writeInt(name.length());
			os.flush();
			os.writeBytes(name);
			os.flush();
			os.writeInt(group.length());
			os.flush();
			os.writeBytes(group);

			int response;
			response = is.readInt();

			if (response == -1) {
				throw new StudentIdAlreadyInUseException();
			}

			System.out.println("result for add student: " + response);
			return response;
		} catch (Exception e) {
			if (e.getClass().equals(StudentIdAlreadyInUseException.class)) {
				throw new StudentIdAlreadyInUseException();
			}
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		return -1;
	}

	public void updateStudent(Student s) throws InvalidStudentIdException {
		try {
			int act = 2;
			os.writeInt(act);
			os.flush();
			os.writeInt(s.id.length());
			os.flush();
			os.writeBytes(s.id);
			os.flush();
			os.writeInt(s.name.length());
			os.flush();
			os.writeBytes(s.name);
			os.flush();
			os.writeInt(s.group.length());
			os.flush();
			os.writeBytes(s.group);

			int response;
			response = is.readInt();

			if (response == -1) {
				throw new InvalidStudentIdException();
			}

			System.out.println("result for update: " + response);
		} catch (Exception e) {
			if (e.getClass().equals(InvalidStudentIdException.class)) {
				throw new InvalidStudentIdException();
			}
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public ArrayList<Student> getFilteredStudents(String filter) {
		try {
			System.out.println("Requesting filtered list");
			ArrayList<Student> students = new ArrayList<Student>();

			int act = 3;
			os.writeInt(act);
			os.flush();

			os.writeInt(filter.length());
			os.flush();
			os.writeBytes(filter);
			os.flush();

			System.out.println("Filter sent");
			int n = is.readInt();
			System.out.println("Whuttt");
			System.out.println("Reading updates, nr: " + n);
			for (int i = 0; i < n; i++) {
				byte[] id = new byte[1024];
				byte[] name = new byte[1024];
				byte[] group = new byte[1024];

				int len = is.readInt();
				is.readFully(id, 0, len);
				String sId = new String(id, "UTF-8");
				sId = sId.substring(0, len);

//				System.out.println("Read id: " + sId);

				len = is.readInt();
				is.readFully(name, 0, len);
				String sName = new String(name, "UTF-8");
				sName = sName.substring(0, len);

//				System.out.println("Read name: " + sName);

				len = is.readInt();
				is.readFully(group, 0, len);
				String sGroup = new String(group, "UTF-8");
				sGroup = sGroup.substring(0, len);

//				System.out.println("Read group: " + sGroup);

				Student s = new Student();
				s.id = sId;
				s.name = sName;
				s.group = sGroup;

				students.add(s);
				System.out.println("Finished reading student: " + s.toString());
			}

			return students;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public void requestUpdate() {
		try {
			int act = 4;
			os.writeInt(act);
			os.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
