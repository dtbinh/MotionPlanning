package planner;

import java.util.ArrayList;

import tool.IPair;
import tool.LocalToPlanner;

public class NF1 extends MyBitMap{
	Robot robot;
	public NF1(Robot in_robot, ArrayList<Obstacle> in_obstacles){
		super(in_obstacles);
		robot = in_robot;
		
			for(int i=0; i<robot.getControlPoints().size(); i++){
				int [][] newMap = new int[SIZE][SIZE];
				for(int m=0; m<SIZE; m++)
					for(int n=0; n<SIZE; n++)
						newMap[m][n] = map[m][n];
				makeNF1(newMap, i);
			}
	}
	
	private void makeNF1(int [][] newMap, int c){
		ArrayList<IPair>[] potential = new ArrayList[255];
		for(int i=0; i<255; i++)
			potential[i] = new ArrayList<IPair>();
		
		LocalToPlanner PR = new LocalToPlanner(robot);
		potential[0].add(new IPair((int)Math.ceil(PR.getGoalControlPoint(c).x), (int)Math.ceil(PR.getGoalControlPoint(c).y)));
		newMap[potential[0].get(0).x][potential[0].get(0).y]=0;
		
		for(int i=0; i<255; i++){
			for(int j=0; j<potential[i].size(); j++){
				IPair[] n = oneNeighbor(potential[i].get(j));
				for(int k=0; k<4; k++){
					if(n[k] != null && newMap[n[k].x][n[k].y]==254){
						potential[i+1].add(n[k]);
						newMap[n[k].x][n[k].y]=i+1;
					}
				}
			}
			if(potential[i]==null)
				continue;
		}
		addMap(newMap);
	}
	
	private IPair[] oneNeighbor(IPair center){
		IPair[] neighbor = new IPair[4];
		neighbor[0] = new IPair(center.x-1, center.y);
		neighbor[1] = new IPair(center.x+1, center.y);
		neighbor[2] = new IPair(center.x, center.y-1);
		neighbor[3] = new IPair(center.x, center.y+1);
		for(int i=0; i<4; i++)
			if(neighbor[i].x<0 || neighbor[i].x>=SIZE || neighbor[i].y<0 || neighbor[i].y>=SIZE)
				neighbor[i] = null;
		return neighbor;
	}
}
