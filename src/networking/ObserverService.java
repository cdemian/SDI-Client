package networking;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import controller.Controller;
import model.Student;

public class ObserverService {
	private Socket obs_s;
	private DataInputStream obs_is;

	public ObserverService() {
		try {
			obs_s = new Socket("127.0.0.1", 8001);
//			obs_s.setSoTimeout(0);
			obs_s.setKeepAlive(true);
			//obs_s.shutdownOutput();
			obs_is = new DataInputStream(obs_s.getInputStream());
		} catch (IOException e) {
			System.out.println("Cannot connect to server");
			e.printStackTrace();
		}
	}

	public void listenForUpdates() {
		while (true) {
			try {
				System.out.println("Listening for updates from server");
				ArrayList<Student> students = new ArrayList<Student>();
				int n = obs_is.readInt();
				System.out.println("Reading updates, nr of students: " + n);
				for (int i = 0; i < n; i++) {
					byte[] id = new byte[1024];
					byte[] name = new byte[1024];
					byte[] group = new byte[1024];
					
					int len = obs_is.readInt();
					
					System.out.println("Len of id: " + len);
					
					obs_is.readFully(id, 0, len);
					String sId = new String(id, "UTF-8");
					sId = sId.substring(0, len);
					
					System.out.println("Id: " + sId);
					
					len = obs_is.readInt();
					obs_is.readFully(name, 0, len);
					String sName = new String(name, "UTF-8");
					sName = sName.substring(0, len);
					
					System.out.println("Name: " + sName);
					
					len = obs_is.readInt();
					obs_is.readFully(group, 0, len);
					String sGroup = new String(group, "UTF-8");
					sGroup = sGroup.substring(0, len);
					
					System.out.println("Group: " + sGroup);
					
					Student s = new Student();
					s.id = sId;
					s.name = sName;
					s.group = sGroup;
					
					students.add(s);
				}
				
				Controller.getInstance().update(null, students);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
