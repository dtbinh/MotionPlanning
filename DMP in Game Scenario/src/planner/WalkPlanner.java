package planner;

import java.util.ArrayList;

import model.Angle;
import model.Configuration;


public class WalkPlanner extends LocalPlanner{
	public WalkPlanner(){
		
	}

	protected ArrayList<Configuration> getPath(Configuration config1, Configuration config2, boolean checkCollide){
		if(checkCollide && (CD.isCollide(data.getRobot(), config1, true) || CD.isCollide(data.getRobot(), config2, true))){
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
			if(checkCollide && CD.isCollide(data.getRobot(), config, true)){
				return null;
			}
			else{
				path.add(config);
			}
		}
		path.add(config2);
		return path;
	}
}
