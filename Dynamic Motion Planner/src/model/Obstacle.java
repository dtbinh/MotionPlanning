package model;

import java.util.Scanner;
import java.util.StringTokenizer;

import tool.PlannerObject;

public class Obstacle extends MyObject{
	private static int numOfObstacle = 0;
	private int observeRound = 0;
	
	public Obstacle(Configuration initial){
		this.initial = initial;
		reference = initial;
		plannerObject = new PlannerObject(this);
	}
	
	public Obstacle(Scanner input) throws Exception {
		numOfObstacle++;
		
		int poly = Integer.parseInt(new StringTokenizer(ReadInNext(input)).nextToken());
		for (int p = 0; p < poly; p++)
			CreatePolygons(p, input);
		
		SetInitialConfig(input);
		
		plannerObject = new PlannerObject(this);
	}

	public MyObject setReferenceConfig(double x, double y, double theta){
		Data data = Data.getInstance();
		super.setReferenceConfig(x, y, theta);
		if(data.isRunning()){
			data.getDynamicMotionPlanner().addObservedObstacle(this);
		}
		clearObserveRound();
		return this;
	}
	
	public int getObserveRound(){
		return observeRound;
	}
	
	public int increaseObserveRound(){
		return ++observeRound;
	} 
	
	public int clearObserveRound(){
		observeRound = 0;
		return observeRound;
	}
	
	public String toString() {
		return "Polygons: \n" + polygons.toString() + "\nInitial "
				+ initial.toString();
	}

}
