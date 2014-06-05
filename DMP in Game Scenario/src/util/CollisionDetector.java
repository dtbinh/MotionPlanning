package util;

import java.util.ArrayList;

import View.GraphPanel;

import model.Configuration;
import model.Data;
import model.MyPolygon;
import model.Obstacle;
import model.Robot;
import model.Obstacle.ObstacleType;

public class CollisionDetector {
	private ArrayList<Obstacle> obstacles;
	public ArrayList<ObstacleType> exclusiveTypes;
	
	public CollisionDetector(){
		obstacles = Data.getInstance().obstacles;
		exclusiveTypes = new ArrayList<ObstacleType>();
	}

	public boolean isCollide(Robot robot, Configuration config){
		return isCollide(robot, config, false);
	}
	
	public boolean isCollide(Robot robot, Configuration config, boolean checkInside) {
		ArrayList<MyPolygon> plannerPolygons = robot.getMirrorRobot(config).getPlannerReferencePolygons();
		for (int i = 0; i < plannerPolygons.size(); i++)
			for (int j = 0; j < plannerPolygons.get(i).npoints; j++) {
				Point p1 = new Point(
						plannerPolygons.get(i).xpoints[j], plannerPolygons.get(i).ypoints[j]);
				Point p2;
				if (j + 1 == plannerPolygons.get(i).npoints)
					p2 = new Point(
							plannerPolygons.get(i).xpoints[0], plannerPolygons.get(i).ypoints[0]);
				else
					p2 = new Point(
							plannerPolygons.get(i).xpoints[j + 1],
							plannerPolygons.get(i).ypoints[j + 1]);

				if (p1.x < 0 || p1.y < 0 || p2.x < 0 || p2.y < 0
						|| p1.x >= GraphPanel.PLANNER || p1.y >= GraphPanel.PLANNER
						|| p2.x >= GraphPanel.PLANNER || p2.y >= GraphPanel.PLANNER)
					return true;
				
				if (isIntersectWithObstacles(p1, p2))
					return true;
				
				if (checkInside && isInsideObstacles(p1)){
					return true;
				}
		}
		for (int i = 0; i < robot.getControlPoints().size(); i++) {
			Point pc = robot.getMirrorRobot(config).getPlannerReferenceControlPoint(i);
			if (Math.ceil(pc.x) < 0 || Math.ceil(pc.x) >= GraphPanel.PLANNER
					|| Math.ceil(pc.y) < 0 || Math.ceil(pc.y) >= GraphPanel.PLANNER)
				return true;
		}
		return false;
	}

	private boolean isInsideObstacles(Point p1){
		for(Obstacle obstacle:obstacles){
			if(!exclusiveTypes.contains(obstacle.getType()) && obstacle.isInside(p1)){
				return true;
			}
		}
		return false;
	}
	
	private boolean isIntersectWithObstacles(Point p1, Point p2) {
		for (int i = 0; i < obstacles.size(); i++){
			if(exclusiveTypes.contains(obstacles.get(i).getType())){
				continue;
			}
			
			ArrayList<MyPolygon> plannerReferencePolygons = obstacles.get(i).getPlannerReferencePolygons();
			for (int j = 0; j < plannerReferencePolygons.size(); j++){
				for (int k = 0; k < plannerReferencePolygons.get(j).npoints; k++) {
					Point p3 = new Point(
							plannerReferencePolygons.get(j).xpoints[k],
							plannerReferencePolygons.get(j).ypoints[k]);
					Point p4;
					if (k + 1 == plannerReferencePolygons.get(j).npoints)
						p4 = new Point(plannerReferencePolygons.get(j).xpoints[0],
								plannerReferencePolygons.get(j).ypoints[0]);
					else
						p4 = new Point(plannerReferencePolygons.get(j).xpoints[k + 1],
								plannerReferencePolygons.get(j).ypoints[k + 1]);

					if (((p1.y-p2.y)*(p3.x-p1.x)+(p2.x-p1.x)*(p3.y-p1.y))*((p1.y-p2.y)*(p4.x-p1.x)+(p2.x-p1.x)*(p4.y-p1.y))<0
						&&((p3.y-p4.y)*(p1.x-p3.x)+(p4.x-p3.x)*(p1.y-p3.y))*((p3.y-p4.y)*(p2.x-p3.x)+(p4.x-p3.x)*(p2.y-p3.y))<0){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static boolean isCollideWithRobot(Obstacle obstacle){
		synchronized(Data.getInstance().getRobot()){
		ArrayList<MyPolygon> RobotPlannerPolygons = Data.getInstance().getRobot().getPlannerReferencePolygons();
		ArrayList<MyPolygon> ObstaclePlannerPolygons = obstacle.getPlannerReferencePolygons();
		Point p1=null, p2=null, p3=null, p4=null;
		
		for (int i = 0; i < RobotPlannerPolygons.size(); i++){
			for (int j = 0; j < RobotPlannerPolygons.get(i).npoints; j++) {
				p1 = new Point(RobotPlannerPolygons.get(i).xpoints[j], RobotPlannerPolygons.get(i).ypoints[j]);
				if (j + 1 == RobotPlannerPolygons.get(i).npoints){
					p2 = new Point(RobotPlannerPolygons.get(i).xpoints[0], RobotPlannerPolygons.get(i).ypoints[0]);
				}
				else{
					p2 = new Point(RobotPlannerPolygons.get(i).xpoints[j+1],RobotPlannerPolygons.get(i).ypoints[j+1]);
				}

				for (int m = 0; m < ObstaclePlannerPolygons.size(); m++){
					for (int n = 0; n < ObstaclePlannerPolygons.get(m).npoints; n++) {
						p3 = new Point(ObstaclePlannerPolygons.get(m).xpoints[n], ObstaclePlannerPolygons.get(m).ypoints[n]);
						if (n + 1 == ObstaclePlannerPolygons.get(m).npoints){
							p4 = new Point(ObstaclePlannerPolygons.get(m).xpoints[0], ObstaclePlannerPolygons.get(m).ypoints[0]);
						}
						else{
							p4 = new Point(ObstaclePlannerPolygons.get(m).xpoints[n+1], ObstaclePlannerPolygons.get(m).ypoints[n+1]);
						}
						if (((p1.y-p2.y)*(p3.x-p1.x)+(p2.x-p1.x)*(p3.y-p1.y))*((p1.y-p2.y)*(p4.x-p1.x)+(p2.x-p1.x)*(p4.y-p1.y))<0
								&&((p3.y-p4.y)*(p1.x-p3.x)+(p4.x-p3.x)*(p1.y-p3.y))*((p3.y-p4.y)*(p2.x-p3.x)+(p4.x-p3.x)*(p2.y-p3.y))<0){
								return true;
						}
					}
				}
			}
		}		
		return false;
		}
	}
}
