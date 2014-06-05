package planner;

import java.util.ArrayList;

import tool.CollisionDetector;

import model.Angle;
import model.Configuration;
import model.Data;
import model.RRTNode;
import model.Robot;

public class WalkPlanner implements Runnable {
	private Data data;
	private Robot robot;
	private CollisionDetector CD;
	public WalkPlanner(){
		data = Data.getInstance();
		robot = data.robot;
		CD = data.getCollisionDetector();
	}
	
	public boolean hasPath(Configuration config1, Configuration config2){
		if(getPath(config1, config2, false, true) == null)
			return false;
		return true;
	}

	public ArrayList<Configuration> getPath(Configuration config1, Configuration config2, boolean debug){
		return getPath( config1,  config2,  debug, true);
	}
	
	public ArrayList<Configuration> getPath(Configuration config1, Configuration config2, boolean debug, boolean addLast){
		if(CD.isCollide(robot, config1, true) || CD.isCollide(robot, config2, true)){
			if(debug){
				System.out.println("getPath problem 1");
			}
			return null;
		}
		
		ArrayList<Configuration> path = new ArrayList<Configuration>();
		double difference = Math.floor(Math.sqrt(Math.pow(config2.x - config1.x, 2) + Math.pow(config2.y - config1.y, 2)));
		double eachX = (config2.x - config1.x) / difference;
		double eachY = (config2.y - config1.y) / difference;
		double eachA = (config2.theta - config1.theta) / difference;
		
		path.add(config1);
		
		Configuration config;
		for(int i=1; i<difference; i++){
			config = new Configuration(config1.x+eachX*i, config1.y+eachY*i, Angle.normalized(config1.theta+eachA*i));
			if(CD.isCollide(robot, config, true)){
				if(debug){
					System.out.println("getPath problem 2\ncollide: " + config + " eachX:" + eachX+ " eachY:"+eachY+" eachTheta:" + eachA + " i:"+i);
				}
				return null;
			}
			else{
				path.add(config);
			}
		}
		
		if(addLast){
			path.add(config2);
		}
		
		return path;
	}
	
	public void run(){//produce the next frame when needed
		DynamicMotionPlanner DMPlanner = data.getDynamicMotionPlanner();
		RRTNode node2;
		Configuration config1, config2, interConfig;
		while(DMPlanner.sequence < data.path.size()-1){
			config1 = data.path.get(DMPlanner.sequence).config;
			node2 = data.path.get(DMPlanner.sequence+1);
			config2 = node2.config;
			double difference = Math.floor(Math.sqrt(Math.pow(config2.x - config1.x, 2) + Math.pow(config2.y - config1.y, 2)));
			double eachX = (config2.x - config1.x) / difference;
			double eachY = (config2.y - config1.y) / difference;
			double eachA = (config2.theta - config1.theta) / difference;
			
			try {
				DMPlanner.framePipe.put(config1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			for(int i=1; i<difference; i++){
				interConfig = new Configuration(config1.x+eachX*i, config1.y+eachY*i, Angle.normalized(config1.theta+eachA*i));
				if(node2.isPath()||!CD.isCollide(robot, interConfig)){
					//if the end node is legal, we don't need to detect collision on the interCofnig
					try {
						DMPlanner.framePipe.put(interConfig);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
			if(DMPlanner.sequence+1 == data.path.size()-1){//add the last configuration of the path
				try {
					DMPlanner.framePipe.put(config2);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			data.path.get(DMPlanner.sequence).setIsPath(false);
			DMPlanner.sequence ++;
		}
	}
}
