package View;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import model.Data;

public class Project_MAIN extends JFrame {
	private static final long serialVersionUID = 1L;
	public final static int MAX_X = 128;
	public final static int MAX_Y = 128;
	public final static String rFile = new String("dat/robot.dat");
	public final static String oFile = new String("dat/env3.dat");
	public final static String ocFile = new String("dat/ObstacleCandidates.dat");
	
	Data data;
	public static GraphPanel GP;
	public static ControlPanel CP;
	public static RobotPanel RP;
	public static PlayerPanel PP;
	
	public static void main(String[] args) throws Exception {
		Project_MAIN program = new Project_MAIN();
		program.setLocationRelativeTo(null);
		program.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		program.pack();
		program.setVisible(true);
	}

	public Project_MAIN() throws Exception {
		setTitle("DMP in Game Scenario");
		data = Data.getInstance();

		GP = new GraphPanel();
		add(GP, BorderLayout.CENTER);
		CP = new ControlPanel();
		add(CP, BorderLayout.SOUTH);
		RP = new RobotPanel();
		add(RP, BorderLayout.WEST);
		PP = new PlayerPanel();
		add(PP, BorderLayout.EAST);

	}
}
