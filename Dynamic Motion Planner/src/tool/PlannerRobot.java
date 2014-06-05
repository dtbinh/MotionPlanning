package tool;

import java.util.ArrayList;

import model.MyObject;
import model.MyPolygon;
import model.Point;
import model.Robot;

public class PlannerRobot extends PlannerObject {
	private Robot robot;
	private boolean goalChanged;
	private ArrayList<MyPolygon> goalPolygons ;
	
	public PlannerRobot(MyObject object){
		super(object);
		robot = (Robot)object;
		setPlannerGoalPolygons();
	}
	
	public ArrayList<MyPolygon> getPlannerGoalPolygons(){
		if(goalChanged || polygonChanged){
			setPlannerGoalPolygons();
		}
		return goalPolygons;
	}
	
	public void setGoalChanged(){
		goalChanged = true;
	}
	
	public void setPlannerGoalPolygons() {
		goalPolygons = new ArrayList<MyPolygon>();
		for (int i = 0; i < robot.getMyPolygon().size(); i++) {
			goalPolygons.add(new MyPolygon());
			for (int j = 0; j < robot.getMyPolygon().get(i).npoints; j++) {
				Point pair = new Transformer(
						robot.getMyPolygon().get(i).xpoints[j], 
						robot.getMyPolygon().get(i).ypoints[j],
						robot.getGoalConfig().theta, 
						robot.getGoalConfig().x,
						robot.getGoalConfig().y);
				goalPolygons.get(i).addPoint(pair.x, pair.y);
			}
		}
		goalChanged = false;
		polygonChanged = false;
	}

	public Point getReferenceControlPoint(int i){
		Point pair = new Transformer(
				robot.getControlPoints().get(i).x,
				robot.getControlPoints().get(i).y,
				robot.getReferenceConfig().theta,
				robot.getReferenceConfig().x,
				robot.getReferenceConfig().y);
		return pair;
	}
	
	public Point getGoalControlPoint(int i){
		Point pair = new Transformer(
				robot.getControlPoints().get(i).x,
				robot.getControlPoints().get(i).y,
				robot.getGoalConfig().theta,
				robot.getGoalConfig().x,
				robot.getGoalConfig().y);
		return pair;
	}
}
