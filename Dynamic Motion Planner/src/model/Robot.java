package model;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import tool.PlannerRobot;

public class Robot extends MyObject{
	private static int numOfRobot = 0;
	
	private ArrayList<Point> controlP = new ArrayList<Point>();
	private Configuration goal;
	private PlannerRobot plannerRobot;

	public Robot(){

	}
	
	public Robot(Scanner input) throws Exception {
		numOfRobot++;
		
		int poly = Integer.parseInt(new StringTokenizer(ReadInNext(input)).nextToken());
		for (int p = 0; p < poly; p++)
			CreatePolygons(p, input);
		
		SetInitialConfig(input);
		
		SetGoalConfig(input);
		
		int pts = Integer.parseInt(new StringTokenizer(ReadInNext(input)).nextToken());
		for (int x = 0; x < pts; x++)
			CreateControlPts(input);
		
		plannerRobot = new PlannerRobot(this);
		plannerObject = plannerRobot;
	}


	private void SetGoalConfig(Scanner input) {
		StringTokenizer token = new StringTokenizer(ReadInNext(input));
		this.goal = new Configuration(Double.parseDouble(token.nextToken()), Double.parseDouble(token.nextToken()),
				Double.parseDouble(token.nextToken()));
		input.nextLine();
	}

	private void CreateControlPts(Scanner input) {
		StringTokenizer token = new StringTokenizer(ReadInNext(input));
		controlP.add(new Point(Double.parseDouble(token.nextToken()), Double.parseDouble(token.nextToken())));
	}

	public String toString() {
		return polygons.toString() + "\nInitial " + initial.toString()
				+ "\nGoal " + goal.toString() + "\n" + controlP.toString();
	}

	public Configuration getGoalConfig(){
		return goal;
	}
	
	public ArrayList<MyPolygon> getPlannerGoalPolygons(){
		return plannerRobot.getPlannerGoalPolygons();
	}
	
	public Point getPlannerReferenceControlPoint(int index){
		return plannerRobot.getReferenceControlPoint(index);
	}
	
	public ArrayList<Point> getControlPoints(){
		return controlP;
	}
	
	public Robot copyRobot(Configuration ref){
		Robot newR = new Robot();
		
		newR.reference = new Configuration(ref.x, ref.y, ref.theta);
		newR.initial = this.initial;
		newR.goal = this.goal;
		newR.controlP = this.controlP;
		newR.polygons = this.polygons;
		newR.plannerRobot = new PlannerRobot(newR);
		newR.plannerObject = newR.plannerRobot;
		return newR;
	}
	
	public boolean isGoal(Configuration c){
		if(c.x > goal.x-1 && c.x < goal.x+1 && 
			c.y > goal.y-1 && c.y < goal.y+1 &&
			Angle.isBetweenA(c.theta, Angle.normalized(goal.theta-5), Angle.normalized(goal.theta+5)))
				return true;

		return false;		
	}
	
	public Robot isInsideGoal(Point p){
		int in;
		ArrayList<MyPolygon> plannerGoalPolygons = this.getPlannerGoalPolygons();
		for(int i=0; i<plannerGoalPolygons.size(); i++){
			in = 0;
			for(int j=0; j<plannerGoalPolygons.get(i).npoints; j++){
				Point p1 = new Point(plannerGoalPolygons.get(i).xpoints[j], plannerGoalPolygons.get(i).ypoints[j]);
				Point p2;
				if(j+1 == plannerGoalPolygons.get(i).npoints)
					p2 = new Point(plannerGoalPolygons.get(i).xpoints[0], plannerGoalPolygons.get(i).ypoints[0]);
				else
					p2 = new Point(plannerGoalPolygons.get(i).xpoints[j+1], plannerGoalPolygons.get(i).ypoints[j+1]);
				
				if((p.x-p1.x)*(p1.y-p2.y)+(p.y-p1.y)*(p2.x-p1.x)>0)
					in++;
			}
			if(in == plannerGoalPolygons.get(i).npoints)
				return this;
		}
		return null;
	}
	
	public MyObject MoveGoal(Point p){
		goal = new Configuration(p.x, p.y, goal.theta);
		plannerRobot.setGoalChanged();
		return this;
	}
	
	public MyObject RotateGoal(double a){
		goal = new Configuration(goal.x, goal.y, a);
		plannerRobot.setGoalChanged();
		return this;
	}
}
