package View;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import model.Data;

public class Project_MAIN extends JFrame {
	private static final long serialVersionUID = 1L;
	public final static int MAX_X = 128;
	public final static int MAX_Y = 128;
	
	Data data = Data.getInstance();
	public static GraphPanel GP;
	public static ControlPanel CP;
	
	public static void main(String[] args) throws Exception {
		Project_MAIN program = new Project_MAIN();
		program.setLocationRelativeTo(null);
		program.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		program.pack();
		program.setVisible(true);
	}

	public Project_MAIN() throws Exception {
		setTitle("Dynamic Motion Planner");

		data.readInRobots("dat/Robots/robot02.dat");
		data.readInObstacles("dat/env2.dat");
		data.initialize();
		//data.readInObstacles("dat/Obstacles/map04.dat");
		GP = new GraphPanel();
		add(GP, BorderLayout.CENTER);
		CP = new ControlPanel();
		add(CP, BorderLayout.EAST);

	}
}
