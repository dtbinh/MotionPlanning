package tool;

import java.util.ArrayList;

import model.Configuration;
import model.Data;
import model.MyPolygon;
import model.Obstacle;
import model.Point;
import model.Robot;

import planner.MyBitMap;

public class CollisionDetector {
	//ArrayList<LocalToPlanner> PObstacles;
	ArrayList<Obstacle> obstacles;
	
	public CollisionDetector(){
		obstacles = Data.getInstance().obstacles;
		//PObstacles = new ArrayList<LocalToPlanner>();
		//for (int i = 0; i < obstacles.size(); i++)
		//	PObstacles.add(new LocalToPlanner(obstacles.get(i)));
	}
	/*
	public void update(){
		ArrayList<LocalToPlanner> newPObstacles = new ArrayList<LocalToPlanner>();
		for (int i = 0; i < obstacles.size(); i++)
			newPObstacles.add(new LocalToPlanner(obstacles.get(i)));
		PObstacles = newPObstacles;
	}*/
	
	public boolean isCollide(Robot robot, Configuration config){
		return isCollide(robot, config, false);
	}
	
	public boolean isCollide(Robot robot, Configuration config, boolean checkInside) {
		ArrayList<MyPolygon> plannerPolygons = robot.copyRobot(config).getPlannerReferencePolygons();
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
						|| p1.x >= MyBitMap.SIZE || p1.y >= MyBitMap.SIZE
						|| p2.x >= MyBitMap.SIZE || p2.y >= MyBitMap.SIZE)
					return true;
				if (isIntersectWithObstacles(p1, p2))
					return true;
				if (checkInside){
					if (isInsideObstacles(p1))
						return true;
				}
		}
		for (int i = 0; i < robot.getControlPoints().size(); i++) {
			Point pc = robot.copyRobot(config).getPlannerReferenceControlPoint(i);
			if (Math.ceil(pc.x) < 0 || Math.ceil(pc.x) >= MyBitMap.SIZE
					|| Math.ceil(pc.y) < 0 || Math.ceil(pc.y) >= MyBitMap.SIZE)
				return true;
		}
		return false;
	}

	private boolean isInsideObstacles(Point p1){
		for(Obstacle obstacle:obstacles){
			if(obstacle.isInside(p1)){
				return true;
			}
		}
		return false;
	}
	
	private boolean isIntersectWithObstacles(Point p1, Point p2) {
		for (int i = 0; i < obstacles.size(); i++){
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

					if (((p1.y - p2.y) * (p3.x - p1.x) + (p2.x - p1.x)
							* (p3.y - p1.y))
							* ((p1.y - p2.y) * (p4.x - p1.x) + (p2.x - p1.x)
									* (p4.y - p1.y)) < 0
							&& ((p3.y - p4.y) * (p1.x - p3.x) + (p4.x - p3.x)
									* (p1.y - p3.y))
									* ((p3.y - p4.y) * (p2.x - p3.x) + (p4.x - p3.x)
											* (p2.y - p3.y)) < 0) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
