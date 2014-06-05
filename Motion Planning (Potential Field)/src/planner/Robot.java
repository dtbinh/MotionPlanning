package planner;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import tool.Angle;
import tool.DPair;
import tool.LocalToPlanner;

public class Robot extends MyObject{
	private static int numOfRobot = 0;
	
	private ArrayList<DPair> controlP = new ArrayList<DPair>();
	private Configuration goal;


	public Robot(){
		
	}
	
	public Robot(Scanner input1) throws Exception {
		input = input1;
		numOfRobot++;
		
		int poly = Integer.parseInt(new StringTokenizer(ReadInNext(input)).nextToken());
		for (int p = 0; p < poly; p++)
			CreatePolygons(p);
		
		SetInitialConfig();
		
		SetGoalConfig();
		
		int pts = Integer.parseInt(new StringTokenizer(ReadInNext(input)).nextToken());
		for (int x = 0; x < pts; x++)
			CreateControlPts();
	}


	private void SetGoalConfig() {
		StringTokenizer token = new StringTokenizer(ReadInNext(input));
		this.goal = new Configuration(Double.parseDouble(token.nextToken()), Double.parseDouble(token.nextToken()),
				Double.parseDouble(token.nextToken()));
		this.input.nextLine();
	}

	private void CreateControlPts() {
		StringTokenizer token = new StringTokenizer(ReadInNext(input));
		controlP.add(new DPair(Double.parseDouble(token.nextToken()), Double.parseDouble(token.nextToken())));
	}

	public String toString() {
		return polygons.toString() + "\nInitial " + initial.toString()
				+ "\nGoal " + goal.toString() + "\n" + controlP.toString();
	}

	public Configuration getGoalConfig(){
		return goal;
	}
	
	public ArrayList<MyPolygon> getPlannerGoalMyPoly(){
		LocalToPlanner n = new LocalToPlanner(this);
		return n.getGoalMyPolygon();
	}
	
	public ArrayList<DPair> getControlPoints(){
		return controlP;
	}
	
	public Robot copyRobot(Configuration ref){
		Robot newR = new Robot();
		
		newR.reference = new Configuration(ref.getX(), ref.getY(), ref.getAngle());
		newR.initial = this.initial;
		newR.goal = this.goal;
		newR.input = null;
		newR.controlP = this.controlP;
		newR.polygons = this.polygons;
		
		return newR;
	}
	
	public boolean isGoal(Configuration c){
		if(c.getX() > goal.getX()-1 && c.getX() < goal.getX()+1 && 
			c.getY() > goal.getY()-1 && c.getY() < goal.getY()+1 &&
			Angle.isBetweenA(c.getAngle(), Angle.normalized(goal.getAngle()-5), Angle.normalized(goal.getAngle()+5)))
				return true;

		return false;		
	}
	
	public Robot isInsideGoal(DPair p){
		int in;
		ArrayList<MyPolygon> NormalPoly = this.getPlannerGoalMyPoly();
		for(int i=0; i<NormalPoly.size(); i++){
			in = 0;
			for(int j=0; j<NormalPoly.get(i).npoints; j++){
				DPair p1 = new DPair(NormalPoly.get(i).xpoints[j], NormalPoly.get(i).ypoints[j]);
				DPair p2;
				if(j+1 == NormalPoly.get(i).npoints)
					p2 = new DPair(NormalPoly.get(i).xpoints[0], NormalPoly.get(i).ypoints[0]);
				else
					p2 = new DPair(NormalPoly.get(i).xpoints[j+1], NormalPoly.get(i).ypoints[j+1]);
				
				if((p.x-p1.x)*(p1.y-p2.y)+(p.y-p1.y)*(p2.x-p1.x)>0)
					in++;
			}
			if(in == NormalPoly.get(i).npoints)
				return this;
		}
		return null;
	}
	
	public MyObject MoveGoal(DPair p){
		goal = new Configuration(p.x, p.y, goal.getAngle());
		return this;
	}
	
	public MyObject RotateGoal(double a){
		goal = new Configuration(goal.getX(), goal.getY(), a);
		return this;
	}
}
