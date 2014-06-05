package model;

import java.io.File;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Scanner;
import java.util.StringTokenizer;

import planner.DynamicMotionPlanner;
import planner.WalkPlanner;

import tool.CollisionDetector;

public class Data extends Observable{
	
	public Robot robot = null;
	public ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
	public RRForest rrf = null;
	public ArrayList<RRTNode> path = null;
	
	private volatile static Data uniqueInstance = null;
	private boolean runningFlag = false;
	private DynamicMotionPlanner DMPlanner = null;
	private CollisionDetector CD = null;
	private boolean pathValid = false;
	private RRTNode goalNode = null;
	
	
	private Data(){
		
	}
	
	public static Data getInstance(){
		if(uniqueInstance == null){
			synchronized(Data.class){
				if(uniqueInstance == null){
					uniqueInstance = new Data();
				}
			}
		}
		return uniqueInstance;
	}
	
	public void initialize(){
		rrf = new RRForest();
		setPath(rrf.RRF_FirstCONNECT(robot.getInitialConfig(), robot.getGoalConfig(), 1000));
	}
	
	public void readInRobots(String rFile) throws Exception {
		File file = new File(rFile);
		Scanner input = new Scanner(file);

		int numOfRobot = Integer.parseInt(new StringTokenizer(MyObject.ReadInNext(input)).nextToken());
		if(numOfRobot > 1){
			System.out.println("We Only support ONE Robot for now!");
			System.exit(0);
		}
		robot = new Robot(input);
	}
	
	public void readInObstacles(String oFile) throws Exception {
		File file = new File(oFile);
		Scanner input = new Scanner(file);

		int numOfObstacle = Integer.parseInt(new StringTokenizer(MyObject.ReadInNext(input)).nextToken());
		for (int i = 0; i < numOfObstacle; i++)
			obstacles.add(new Obstacle(input));
	}
	
	public boolean isRunning(){
		return runningFlag;
	}
	
	public void start(){
		if(runningFlag){
			return;
		}
		synchronized(this){
			DMPlanner = new DynamicMotionPlanner();
			runningFlag = true;
			new Thread(DMPlanner).start();
			new Thread(new WalkPlanner()).start();//robot configuration producer
		}

	}
	
	public void stop(){
		//TODO й|е╝з╣жи
		runningFlag = false;
	}
	
	public DynamicMotionPlanner getDynamicMotionPlanner(){
		return DMPlanner;
	}
	
	private void setPath(ArrayList<RRTNode> newPath){
		path = newPath;
		pathValid = true;
		setChanged();
		notifyObservers();
	}
	
	public ArrayList<RRTNode> findNewPath(){
		//New initial node is the node previous to the break node
		RRTNode lastValidNode = DMPlanner.lastValidNode;
		
		//it is possible that there exists a node is illegal while hasn't been invalidated by updateRRF function
		if(lastValidNode.getMarkNo()>0){
			System.out.println("updata new Initial:" + lastValidNode.config);
			rrf.updateRRF(lastValidNode);
		}
		//TODO this checking is questionable
		if(lastValidNode.isPath()){
			//TODO RRF_CONNECT used nodes should be updated(MarkNo==0)
			return rrf.RRF_CONNECT(lastValidNode, goalNode, 100);
		}
		return null;
	}
	
	public void connectNewPath(ArrayList<RRTNode> connectPath){
		synchronized(path){
			path.addAll(connectPath);
			pathValid = true;
		}
	}
	
	public void addObstacle(Obstacle newObstacle){
		obstacles.add(newObstacle);
		if(isRunning()){
			DMPlanner.addObservedObstacle(newObstacle);
		}
		newObstacle.clearObserveRound();
	}

	
	public boolean isPathValid(){
		return pathValid;
	}
	
	public void invalidatePath(RRTNode breakNode){
		if(isRunning()){
			pathValid = false;		
			int index = path.indexOf(breakNode);
			DMPlanner.lastValidNode = path.get(index-1);
			synchronized(path){
				for(int i=path.size()-1; i>=index; i--){
					path.get(i).setIsPath(false);
					path.remove(i);
				}
			}
		}
	}
	
	public void setGoalNode(RRTNode goalNode){
		this.goalNode = goalNode;
	}
	
	public ArrayList<RRTNode> getPath(){
		return path;
	}
	
	public RRForest getRRF(){
		return rrf;
	}
	
	public CollisionDetector getCollisionDetector(){
		if(CD == null){
			CD = new CollisionDetector();
		}
		return CD;
	}
}
