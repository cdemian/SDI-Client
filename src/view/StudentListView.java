package view;

import java.awt.EventQueue;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;

import controller.Controller;
import exception.InvalidStudentIdException;

import javax.swing.JButton;
import javax.swing.JList;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ListSelectionModel;

import model.Student;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class StudentListView extends JFrame implements Observer{
	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	private JList<Student> list;
	private DefaultListModel<Student> listModel = new DefaultListModel<Student>();
	private JButton btnNewButton;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					StudentListView frame = new StudentListView();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public StudentListView() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 544, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"), FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC, }, new RowSpec[] { FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"), }));

		JButton btnAddStudent = new JButton("Add Student");
		btnAddStudent.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				addNewStudent();
			}
		});
		
		btnNewButton = new JButton("Show Student");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				getAllStudents();
			}
		});
		contentPane.add(btnNewButton, "2, 2");
		contentPane.add(btnAddStudent, "6, 2");

		JButton btnFilterStudents = new JButton("Filter Students");
		btnFilterStudents.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				filterStudents();
			}
		});
		contentPane.add(btnFilterStudents, "14, 2");

		list = new JList<Student>();
		list.setModel(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		contentPane.add(list, "2, 4, 13, 1, fill, fill");

		list.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3 && !list.isSelectionEmpty()) {
					JPopupMenu menu = new JPopupMenu();

					JMenuItem item;
					// update account
					item = new JMenuItem("Update Student");
					item.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							updateStudent();
						}
					});
					menu.add(item);

					menu.show(StudentListView.this, e.getPoint().x + 26, e.getY() + 79);
				}
			}
		});

		//getAllStudents();
	}

	protected void getAllStudents() {
		Controller.getInstance().getAllStudents();		
	}

	protected void filterStudents() {
		try {
			String group = JOptionPane.showInputDialog("Group: ");
			ArrayList<Student> studs = Controller.getInstance().getFilteredStudents(group);
			listModel.removeAllElements();
			for (Student e : studs) {
				listModel.addElement(e);
				System.out.println(e);
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage());
		}
	}

	protected void addNewStudent() {
		JTextField idField = new JTextField(20);
		JTextField nameField = new JTextField(20);
		JTextField groupField = new JTextField(20);

		JPanel myPanel = new JPanel();
		myPanel.add(new JLabel("id:"));
		myPanel.add(idField);
		myPanel.add(Box.createVerticalStrut(3)); // a spacer
		myPanel.add(new JLabel("name:"));
		myPanel.add(nameField);
		myPanel.add(Box.createVerticalStrut(3)); // a spacer
		myPanel.add(new JLabel("group:"));
		myPanel.add(groupField);

		int result = JOptionPane.showConfirmDialog(null, myPanel, "Please enter the student's data", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {

			try {
				Controller.getInstance().addStudent(idField.getText(), nameField.getText(), groupField.getText());
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, e.getClass());
			}
		}
	}

	protected JList<Student> getList() {
		return list;
	}

	private void updateStudent() {
		JTextField nameField = new JTextField(20);
		JTextField groupField = new JTextField(20);

		JPanel myPanel = new JPanel();
		myPanel.add(new JLabel("Name:"));
		myPanel.add(nameField);
		myPanel.add(Box.createVerticalStrut(3)); // a spacer
		myPanel.add(new JLabel("Group:"));
		myPanel.add(groupField);
		myPanel.add(Box.createVerticalStrut(3)); // a spacer

		int result = JOptionPane.OK_OPTION;
		while (result != JOptionPane.CANCEL_OPTION) {
			result = JOptionPane.showConfirmDialog(null, myPanel,
					"Please Enter the Grade", JOptionPane.OK_CANCEL_OPTION);
			if (result == JOptionPane.OK_OPTION) {
				Student s = list.getSelectedValue();
				s.name = nameField.getText();
				s.group = groupField.getText();
				
				try {
					Controller.getInstance().updateStudent(s);
				} catch (InvalidStudentIdException e) {
					JOptionPane.showMessageDialog(this, "Student ID is invalid");
					continue;
				}
				break;
			} else {
				break;
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update(Observable arg0, Object students) {
		System.out.println("Update in view");
		ArrayList<Student> studs = (ArrayList<Student>) students;
		listModel.removeAllElements();
		for (Student e : studs) {
			listModel.addElement(e);
		}
	}
}
