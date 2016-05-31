package com.dronet.frontend;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;

import com.dronet.mas.agents.ControllerAgent;
import com.dronet.mas.entities.Drone;
import com.dronet.mas.entities.Task;

import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JTextArea;

public class Scenario extends JFrame {

	private static final long serialVersionUID = -2634266721255900116L;
	
	private static Scenario instance; 
	
	private JFrame frame;
	private JTabbedPane tabbedPane;
	private JTextArea txtConsole;

	private JTextField txtDroneName;
	private JTextField txtDroneLocationLatitude;
	private JTextField txtDroneLocationLongitude;
	private JSpinner spinnerDroneRole;
	private JTextField txtDroneBattery;
	private JTextField txtDroneCenterLatitude;
	private JTextField txtDroneCenterLongitude;
	private JTextField txtDroneRatio;
	private JTextField txtDroneSpeed;
	private JTextField txtDronePayload;

	private JTextField txtTaskName;
	private JTextField txtTaskLocationLatitude;
	private JTextField txtTaskLocationLongitude;
	private JSpinner spinnerTaskPriority;
	private JSpinner spinnerTaskAction;
	private JTextField txtTaskPayload;
	
	private SceneDrawer canvas;

	/**
	 * Create the application.
	 */
	private Scenario() {
		initialize();
	}
	
	/**
	 * Launch the application.
	 */
	static {
        instance = new Scenario();
    }
	
	public static Scenario getInstance() {
        return instance;
    }

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		setTitle("DRONET Task Allocation Quick Interface v1.1");
		setMinimumSize(new Dimension(1280, 800));
		setPreferredSize(new Dimension(1280, 800));
		setResizable(false);
		
		frame = this;
		frame.setBounds(100, 100, 1280, 800);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		
		txtConsole = new JTextArea();
		txtConsole.setFont(new Font("Consolas", Font.PLAIN, 12));
		txtConsole.setEditable(false);
		txtConsole.setBounds(10, 616, 620, 140);
		JScrollPane scrollPane = new JScrollPane(txtConsole);
		scrollPane.setBounds(10, 616, 720, 140);
		PrintStream printStream = new PrintStream(new CustomOutputStream(txtConsole));
		System.setOut(printStream);//*/
		//System.setErr(printStream);
		frame.getContentPane().add(scrollPane);

		canvas = new SceneDrawer();
		canvas.setBounds(10, 10, 1255, 600);
		//canvas.setBounds(10, 10, 1060, 600);
		frame.getContentPane().add(canvas);

		//tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		//tabbedPane.setBounds(1080, 10, 180, 600);
		//frame.getContentPane().add(tabbedPane);

		//setNewDroneForm();
		//setNewTaskForm();
		
		setLeyend();

	}

	private void setLeyend() {
		
		ImageIcon icon1 = new ImageIcon("assets/drone-search.png");
		ImageIcon icon2 = new ImageIcon("assets/drone-deliver-no-camera.png");
		ImageIcon icon3 = new ImageIcon("assets/drone-deliver-with-camera.png");
		ImageIcon icon4 = new ImageIcon("assets/waypoint-deliver.png");
		ImageIcon icon5 = new ImageIcon("assets/waypoint-search.png");
		ImageIcon icon6 = new ImageIcon("assets/waypoint-video.png");
		
		icon1.setImage(icon1.getImage().getScaledInstance(30, 30, Image.SCALE_AREA_AVERAGING));
		icon2.setImage(icon2.getImage().getScaledInstance(30, 30, Image.SCALE_AREA_AVERAGING));
		icon3.setImage(icon3.getImage().getScaledInstance(30, 30, Image.SCALE_AREA_AVERAGING));
		icon4.setImage(icon4.getImage().getScaledInstance(13, 18, Image.SCALE_AREA_AVERAGING));
		icon5.setImage(icon5.getImage().getScaledInstance(13, 18, Image.SCALE_AREA_AVERAGING));
		icon6.setImage(icon6.getImage().getScaledInstance(13, 18, Image.SCALE_AREA_AVERAGING));

		JLabel label1 = new JLabel("Search Drone",icon1, JLabel.RIGHT);
		JLabel label2 = new JLabel("Deliver Drone without camera",icon2, JLabel.RIGHT);
		JLabel label3 = new JLabel("Deliver Drone with camera",icon3, JLabel.RIGHT);
		JLabel label4 = new JLabel("Deliver task",icon4, JLabel.RIGHT);
		JLabel label5 = new JLabel("Search task",icon5, JLabel.RIGHT);
		JLabel label6 = new JLabel("Video task.",icon6, JLabel.RIGHT);

		label1.setVerticalTextPosition(JLabel.CENTER);
		label1.setHorizontalTextPosition(JLabel.RIGHT);
		label2.setVerticalTextPosition(JLabel.CENTER);
		label2.setHorizontalTextPosition(JLabel.RIGHT);
		label3.setVerticalTextPosition(JLabel.CENTER);
		label3.setHorizontalTextPosition(JLabel.RIGHT);
		label4.setVerticalTextPosition(JLabel.CENTER);
		label4.setHorizontalTextPosition(JLabel.RIGHT);
		label5.setVerticalTextPosition(JLabel.CENTER);
		label5.setHorizontalTextPosition(JLabel.RIGHT);
		label6.setVerticalTextPosition(JLabel.CENTER);
		label6.setHorizontalTextPosition(JLabel.RIGHT);
		
		JPanel droneLeyend = new JPanel();
		droneLeyend.setLayout(new FlowLayout(FlowLayout.LEFT));
		droneLeyend.setBounds(730, 616, 220, 200);
		
		droneLeyend.add(label1);
		droneLeyend.add(label2);
		droneLeyend.add(label3);
		
		JPanel taskLeyend = new JPanel();
		taskLeyend.setLayout(new FlowLayout(FlowLayout.LEFT));
		taskLeyend.setBounds(980, 616, 100, 200);

		taskLeyend.add(label5);
		taskLeyend.add(label4);
		taskLeyend.add(label6);
		
		frame.getContentPane().add(droneLeyend);
		frame.getContentPane().add(taskLeyend);
		
	}

	@SuppressWarnings("unused")
	private void setNewDroneForm() {

		JPanel tabDrone = new JPanel();
		tabbedPane.addTab("New Drone", null, tabDrone, null);

		tabbedPane.addTab("New Drone", null, tabDrone, null);
		tabDrone.setLayout(new GridLayout(0, 1, 0, 0));

		JLabel lblDroneName = new JLabel("Name:");
		tabDrone.add(lblDroneName);

		txtDroneName = new JTextField();
		txtDroneName.setColumns(10);
		tabDrone.add(txtDroneName);

		JLabel lblDroneLocationLatitude = new JLabel("Latitude:");
		tabDrone.add(lblDroneLocationLatitude);

		txtDroneLocationLatitude = new JTextField();
		txtDroneLocationLatitude.setColumns(10);
		tabDrone.add(txtDroneLocationLatitude);

		JLabel lblDroneLocationLongitude = new JLabel("Longitude:");
		tabDrone.add(lblDroneLocationLongitude);

		txtDroneLocationLongitude = new JTextField();
		txtDroneLocationLongitude.setColumns(10);
		tabDrone.add(txtDroneLocationLongitude);		

		JLabel lblDroneRole = new JLabel("Role:");
		tabDrone.add(lblDroneRole);

		spinnerDroneRole = new JSpinner(new SpinnerListModel(Drone.Role.values()));
		spinnerDroneRole.setAlignmentX(Component.LEFT_ALIGNMENT);
		spinnerDroneRole.setEditor(new JSpinner.DefaultEditor(spinnerDroneRole));
		tabDrone.add(spinnerDroneRole);

		JLabel lblDroneBattery = new JLabel("Battery:");
		tabDrone.add(lblDroneBattery);

		txtDroneBattery = new JTextField();
		txtDroneBattery.setColumns(10);
		tabDrone.add(txtDroneBattery);		

		JLabel lblDroneCenterLatitude = new JLabel("Center latitude:");
		tabDrone.add(lblDroneCenterLatitude);

		txtDroneCenterLatitude = new JTextField();
		txtDroneCenterLatitude.setColumns(10);
		tabDrone.add(txtDroneCenterLatitude);		

		JLabel lblDroneCenterLongitude = new JLabel("Center longitude:");
		tabDrone.add(lblDroneCenterLongitude);

		txtDroneCenterLongitude = new JTextField();
		tabDrone.add(txtDroneCenterLongitude);		

		JLabel lblDroneRatio = new JLabel("Ratio:");
		tabDrone.add(lblDroneRatio);

		txtDroneRatio = new JTextField();
		tabDrone.add(txtDroneRatio);		

		JLabel lblDroneSpeed = new JLabel("Max. speed:");
		tabDrone.add(lblDroneSpeed);

		txtDroneSpeed = new JTextField();
		tabDrone.add(txtDroneSpeed);		

		JLabel lblDronePayload = new JLabel("Max. payload:");
		tabDrone.add(lblDronePayload);

		txtDronePayload = new JTextField();
		tabDrone.add(txtDronePayload);

		JButton btnCreateDrone = new JButton("Create Drone");
		
		btnCreateDrone.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {

				try {
					
					String droneName = txtDroneName.getText();
					String droneLatitude = txtDroneLocationLatitude.getText();
					String droneLongitude = txtDroneLocationLongitude.getText();
					String droneCenterLatitude = txtDroneCenterLongitude.getText();
					String droneCenterLongitude = txtDroneCenterLatitude.getText();
					String droneRatio = txtDroneRatio.getText();
					String droneSpeed = txtDroneSpeed.getText();
					String dronePayload = txtDronePayload.getText();
					String droneBattery = txtDroneBattery.getText();
					String droneRole = spinnerDroneRole.getModel().getValue().toString();
					
					ControllerAgent.addDrone(droneName, new String[] {
							droneLatitude,			// Latitude
							droneLongitude,			// Longitude
							droneRole,				// Role
							droneBattery,			// Battery
							droneCenterLatitude,	// Center Latitude
							droneCenterLongitude,	// Center Longitude
							droneRatio,				// Ratio
							droneSpeed,				// Max. Speed
							dronePayload			// Max. Payload 
							});
					
					txtDroneName.setText(null);
					txtDroneLocationLatitude.setText(null);
					txtDroneLocationLongitude.setText(null);
					txtDroneCenterLongitude.setText(null);
					txtDroneCenterLatitude.setText(null);
					txtDroneRatio.setText(null);
					txtDroneSpeed.setText(null);
					txtDronePayload.setText(null);
					txtDroneBattery.setText(null);
					
					
				} catch (Exception exc) {
					System.out.println(exc.getMessage());
				}


				
			}
		});
		
		tabDrone.add(btnCreateDrone);
	}

	@SuppressWarnings("unused")
	private void setNewTaskForm() {

		JPanel tabTask = new JPanel();
		tabbedPane.addTab("New Task", null, tabTask, null);

		JLabel lblTaskName = new JLabel("Name: ");

		txtTaskName = new JTextField();
		lblTaskName.setLabelFor(txtTaskName);
		
		JLabel lblTaskLocationLatitude = new JLabel("Latitude:");

		txtTaskLocationLatitude = new JTextField();
		txtTaskLocationLatitude.setColumns(10);
		lblTaskLocationLatitude.setLabelFor(txtTaskLocationLatitude);

		JLabel lblTaskLocationLongitude = new JLabel("Longitude:");

		txtTaskLocationLongitude = new JTextField();
		txtTaskLocationLongitude.setColumns(10);
		lblTaskLocationLongitude.setLabelFor(txtTaskLocationLatitude);

		JLabel lblTaskPriority = new JLabel("Priority:");

		spinnerTaskPriority = new JSpinner(new SpinnerListModel(Task.Priority.values()));
		spinnerTaskPriority.setAlignmentX(Component.LEFT_ALIGNMENT);
		spinnerTaskPriority.setEditor(new JSpinner.DefaultEditor(spinnerTaskPriority));
		lblTaskPriority.setLabelFor(spinnerTaskPriority);

		JLabel lblTaskAction = new JLabel("Action:");

		spinnerTaskAction = new JSpinner(new SpinnerListModel(Task.Action.values()));
		spinnerTaskAction.setAlignmentX(Component.LEFT_ALIGNMENT);
		spinnerTaskAction.setEditor(new JSpinner.DefaultEditor(spinnerTaskAction));
		lblTaskAction.setLabelFor(spinnerTaskAction);
		
		JLabel lblTaskPayload = new JLabel("Payload:");

		txtTaskPayload = new JTextField();
		txtTaskPayload.setColumns(10);
		lblTaskPayload.setLabelFor(txtTaskPayload);

		JButton btnCreateTask = new JButton("Create Task");
		
		btnCreateTask.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					
					String taskName = txtTaskName.getText();
					String taskLatitude = txtTaskLocationLatitude.getText();
					String taskLongitude = txtTaskLocationLongitude.getText();
					String taskPriority = spinnerTaskPriority.getModel().getValue().toString();
					String taskAction = spinnerTaskAction.getModel().getValue().toString();
					String taskPayload = txtTaskPayload.getText();
					
					ControllerAgent.addTask(taskName, new String[] {
						taskLatitude,		// Latitude
						taskLongitude,		// Longitude
						taskPriority,		// Priority
						taskAction,			// Action
						taskPayload			// Payload
						});
					
					txtTaskName.setText(null);
					txtTaskLocationLatitude.setText(null);
					txtTaskLocationLongitude.setText(null);
					txtTaskPayload.setText(null);
					
				} catch (Exception exc) {
					System.out.println(exc.getMessage());
				}

			}
		});
		
		GroupLayout gl_tabTask = new GroupLayout(tabTask);
		gl_tabTask.setHorizontalGroup(
			gl_tabTask.createParallelGroup(Alignment.LEADING)
				.addComponent(lblTaskName, GroupLayout.PREFERRED_SIZE, 167, GroupLayout.PREFERRED_SIZE)
				.addComponent(txtTaskName, GroupLayout.PREFERRED_SIZE, 167, GroupLayout.PREFERRED_SIZE)
				.addComponent(lblTaskLocationLatitude, GroupLayout.PREFERRED_SIZE, 167, GroupLayout.PREFERRED_SIZE)
				.addComponent(txtTaskLocationLatitude, GroupLayout.PREFERRED_SIZE, 167, GroupLayout.PREFERRED_SIZE)
				.addComponent(lblTaskLocationLongitude, GroupLayout.PREFERRED_SIZE, 167, GroupLayout.PREFERRED_SIZE)
				.addComponent(txtTaskLocationLongitude, GroupLayout.PREFERRED_SIZE, 167, GroupLayout.PREFERRED_SIZE)
				.addComponent(lblTaskPriority, GroupLayout.PREFERRED_SIZE, 167, GroupLayout.PREFERRED_SIZE)
				.addComponent(spinnerTaskPriority, GroupLayout.PREFERRED_SIZE, 167, GroupLayout.PREFERRED_SIZE)
				.addComponent(lblTaskAction, GroupLayout.PREFERRED_SIZE, 167, GroupLayout.PREFERRED_SIZE)
				.addComponent(spinnerTaskAction, GroupLayout.PREFERRED_SIZE, 167, GroupLayout.PREFERRED_SIZE)
				.addComponent(lblTaskPayload, GroupLayout.PREFERRED_SIZE, 167, GroupLayout.PREFERRED_SIZE)
				.addComponent(txtTaskPayload, GroupLayout.PREFERRED_SIZE, 167, GroupLayout.PREFERRED_SIZE)
				.addComponent(btnCreateTask, GroupLayout.PREFERRED_SIZE, 167, GroupLayout.PREFERRED_SIZE)
		);
		gl_tabTask.setVerticalGroup(
			gl_tabTask.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_tabTask.createSequentialGroup()
					.addComponent(lblTaskName, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
					.addComponent(txtTaskName, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
					.addComponent(lblTaskLocationLatitude, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
					.addComponent(txtTaskLocationLatitude, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
					.addComponent(lblTaskLocationLongitude, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
					.addComponent(txtTaskLocationLongitude, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
					.addComponent(lblTaskPriority, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
					.addComponent(spinnerTaskPriority, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
					.addComponent(lblTaskAction, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
					.addComponent(spinnerTaskAction, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
					.addComponent(lblTaskPayload, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
					.addComponent(txtTaskPayload, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
					.addComponent(btnCreateTask, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE))
		);
		tabTask.setLayout(gl_tabTask);
	}

	public SceneDrawer getCanvas() { return canvas; }
	
	public class CustomOutputStream extends OutputStream {
	    private JTextArea textArea;
	     
	    public CustomOutputStream(JTextArea textArea) {
	        this.textArea = textArea;
	    }
	     
	    @Override
	    public void write(int b) throws IOException {
	        // redirects data to the text area
	        textArea.append(String.valueOf((char)b));
	        // scrolls the text area to the end of data
	        textArea.setCaretPosition(textArea.getDocument().getLength());
	    }
	}
}
