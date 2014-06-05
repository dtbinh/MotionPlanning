package gui;

import java.awt.BorderLayout;

import java.io.*;
import java.util.*;

import javax.swing.JFrame;

import planner.BFS;
import planner.Configuration;
import planner.MyObject;
import planner.NF1;
import planner.Obstacle;
import planner.Robot;

public class Planner extends JFrame {
	ArrayList<Robot> robots = null;
	ArrayList<Obstacle> obstacles = null;
	ArrayList<Configuration> path = null;
	GraphPanel GP;
	ControlPanel CP;
	NF1 PF = null;
	String rFile, oFile;
	int currentRobot;

	public static void main(String[] args) throws Exception {
		// create the motion planner main frame
		Planner planner = new Planner();
		planner.setLocationRelativeTo(null);
		planner.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		planner.pack();
		planner.setVisible(true);
	}

	public Planner() throws Exception {
		setTitle("Planner");

		rFile = new String("dat/Robots/robot00.dat");
		oFile = new String("dat/Obstacles/map00.dat");
		readInRobots();
		readInObstacles();
		
		
		GP = new GraphPanel(this);
		add(GP, BorderLayout.CENTER);
		CP = new ControlPanel(this);
		add(CP, BorderLayout.EAST);
		
	}

	void readInRobots() throws Exception {
		robots = new ArrayList<Robot>();

		File file = new File(rFile);
		Scanner input = new Scanner(file);

		int numOfRobot = Integer.parseInt(new StringTokenizer(MyObject
				.ReadInNext(input)).nextToken());
		for (int i = 0; i < numOfRobot; i++)
			robots.add(new Robot(input));
		
		currentRobot = 0;

	}

	void readInObstacles() throws Exception {
		obstacles = new ArrayList<Obstacle>();

		File file = new File(oFile);
		Scanner input = new Scanner(file);

		int numOfObstacle = Integer.parseInt(new StringTokenizer(MyObject
				.ReadInNext(input)).nextToken());
		for (int i = 0; i < numOfObstacle; i++)
			obstacles.add(new Obstacle(input));
	}
	void reset() {
		CP.PP.jbtSmooth.setEnabled(false);
		CP.PP.jbtAnimation.setEnabled(false);
		CP.InP.jbtShowPath.setEnabled(false);
		CP.InP.jbtShowPath.setText("Hide Path");
		CP.msg.setText("");
		path = null;
		PF = null;
	}
	
	Thread Plan(){
		Thread T = new Thread(new FindPath());
		T.start();
		return T;
	}
	
	class FindPath implements Runnable{
		public FindPath(){
			
		}
		public void run(){
			PF = new NF1(robots.get(currentRobot), obstacles);

			long t = new Date().getTime();
			BFS MP = new BFS(robots.get(currentRobot), obstacles, PF);
			path = MP.getPath();
			t = new Date().getTime() - t;
			double time = (double)t/1000;
			if(MP.isSuccess())
			CP.msg.setText("<Planner>SUCCESS!\n"+
					       "Spent Time: "+Double.toString(time)+"(s)\n"+
					       "No. of Steps: "+Integer.toString(MP.getSteps()));
			else
				CP.msg.setText("<Planner>FAIL!\nSorry! There is no path to reach \nthe Goal.\n"+
						"Spent Time: "+Double.toString(time)+"(s)\n");
			GP.paintLastPosition();
			GP.paintAllPath();
			CP.PP.T1 = null;
			CP.PP.jbtPlanPath.setText("Find the Path");
			CP.setAllEnabled(true);
		}
	}
}
