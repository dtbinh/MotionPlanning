package model;

import java.io.File;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;
import java.util.StringTokenizer;

import View.Project_MAIN;

import planner.DynamicMotionPlanner;

public class Data extends Observable implements Observer{
	public ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();


	private volatile static Data uniqueInstance = null;
	private Robot robot = null;
	private Player player = null;
	private RRForest rrf = null;
	private Path path = null;
	private DynamicMotionPlanner DMPlanner = null;
	private Thread PlannerThread = null;
	
	private Data(){
		try {
			readInRobots(Project_MAIN.rFile);
			readInObstacles(Project_MAIN.oFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Data getInstance(){
		if(uniqueInstance == null){
			synchronized(Data.class){
				if(uniqueInstance == null){
					uniqueInstance = new Data();
					uniqueInstance.initialize();
				}
			}
		}
		return uniqueInstance;
	}
	
	public void initialize(){
		rrf = new RRForest();
		player = new Player();
		setChanged();
		notifyObservers();
		
		/*for(int i=0; i<obstacles.size(); i++){
			System.out.println("obstacle"+i+" "+obstacles.get(i).getType());
		}*/
	}
	
	private void readInRobots(String rFile) throws Exception {
		File file = new File(rFile);
		Scanner input = new Scanner(file);

		int numOfRobot = Integer.parseInt(new StringTokenizer(MyObject.ReadInNext(input)).nextToken());
		if(numOfRobot > 1){
			System.err.println("We Only support ONE Robot for now!");
			System.exit(0);
		}
		robot = new Robot(input);
	}
	
	private void readInObstacles(String oFile) throws Exception {
		File file = new File(oFile);
		Scanner input = new Scanner(file);

		int numOfObstacle = Integer.parseInt(new StringTokenizer(MyObject.ReadInNext(input)).nextToken());
		for (int i = 0; i < numOfObstacle; i++)
			obstacles.add(new Obstacle(input));
	}

	public synchronized void start(){
		if(PlannerThread != null && PlannerThread.isAlive()){
			return;
		}
		path = rrf.RRF_FirstCONNECT(robot.getInitialConfig(), robot.getGoalConfig(), 1000);
		path.refreshPath();
		//TODO
		robot.setStrength(1000);
		DMPlanner = new DynamicMotionPlanner();
		DMPlanner.addObserver(this);
		PlannerThread = new Thread(DMPlanner);
		PlannerThread.start();
	}
	
	public void pause(){
		PlannerThread.interrupt();
	}
	
	public void stop(){
		//TODO й|е╝з╣жи
		//runningFlag = false;
	}
	
	public void addObstacle(Obstacle newObstacle){
		obstacles.add(newObstacle);
		if(isRunning()){
			DMPlanner.addObservedObstacle(newObstacle);
		}
		newObstacle.clearObserveRound();
	}
	
	public DynamicMotionPlanner getDynamicMotionPlanner(){
		return DMPlanner;
	}
	
	public Path getPath(){
		return path;
	}
	
	public Robot getRobot(){
		return robot;
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public RRForest getRRF(){
		return rrf;
	}
	
	public boolean isRunning(){
		return (PlannerThread != null && PlannerThread.isAlive());
	}
	
	public void update(Observable o, Object arg) {
		setChanged();
		notifyObservers();
	}
}
