package planner;

import java.util.ArrayList;

import util.CollisionDetector;

import model.Configuration;
import model.Data;
import model.Robot;

public abstract class LocalPlanner implements Runnable{
	protected Data data;
	protected CollisionDetector CD;
	public enum LPTypes{WalkPlanner(), ClimbPlanner()};
	protected Configuration config1, config2;
	
	public LocalPlanner(){
		data = Data.getInstance();
		CD = new CollisionDetector();
	}
	
	public boolean isCollide(Robot robot, Configuration config){
		return CD.isCollide(robot, config, false);
	}
	
	public void setFrameConfigs(Configuration config1, Configuration config2){
		this.config1 = config1;
		this.config2 = config2;
	}
	
	public void run(){
		DynamicMotionPlanner DMPlanner = data.getDynamicMotionPlanner();
		ArrayList<Configuration> pathConfigs = getPath(config1, config2, false);
		for(int i=0; i<pathConfigs.size()-1; i++){
			try {
				DMPlanner.framePipe.put(pathConfigs.get(i));
			} catch (InterruptedException e) {
				//e.printStackTrace();
				return;
			}
		}
		DMPlanner.goNextSequence();
	}
	
	public ArrayList<Configuration> getPath(Configuration config1, Configuration config2){
		return getPath(config1, config2, true);
	}
	
	abstract protected ArrayList<Configuration> getPath(Configuration config1, Configuration config2, boolean checkCollide);
}
