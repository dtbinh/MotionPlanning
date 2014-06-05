package model;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Scanner;
import java.util.StringTokenizer;

import util.CollisionDetector;
import util.PlannerObject;

public class Obstacle extends MyObject{
	public static enum ObstacleType{HIGH_WALL, SHORT_WALL};
	public static final int MovingCost[] = {2,1};
	public static final Color[] ObstacleColor = {Color.black, Color.lightGray};
	private static int numOfObstacle = 0;
	private int number = -1;
	private int observeRound = 0;
	private ObstacleType type = ObstacleType.HIGH_WALL;;
	
	public Obstacle(){
		
	}
	
	public Obstacle(Configuration reference){
		this.reference = reference;
		plannerObject = new PlannerObject(this);
	}
	
	public Obstacle(Scanner input) throws Exception {
		number = numOfObstacle++;
		type = ObstacleType.valueOf(new StringTokenizer(ReadInNext(input)).nextToken());	
		int poly = Integer.parseInt(new StringTokenizer(ReadInNext(input)).nextToken());
		for (int p = 0; p < poly; p++)
			CreatePolygons(p, input);
		
		setReferenceConfig(input);
		
		plannerObject = new PlannerObject(this);
	}

	public boolean setReferenceConfig(Configuration newConfig){
		if(CollisionDetector.isCollideWithRobot(getMirrorObstacle(newConfig))){
			return false;
		}
		
		clearObserveRound();
		if(Data.getInstance().isRunning()){
			Data.getInstance().getDynamicMotionPlanner().addObservedObstacle(this);
		}
		
		return super.setReferenceConfig(newConfig);
	}
	
	public int getObserveRound(){
		return observeRound;
	}
	
	public Obstacle getMirrorObstacle(Configuration ref){
		Obstacle newO = new Obstacle();
		
		newO.reference = new Configuration(ref.x, ref.y, ref.theta);
		newO.polygons = this.polygons;
		newO.type = this.type;
		newO.plannerObject = new PlannerObject(newO);
		return newO;
	}
	
	public ObstacleType getType(){
		return type;
	}
	
	public int increaseObserveRound(){
		return ++observeRound;
	} 
	
	public int clearObserveRound(){
		observeRound = 0;
		return observeRound;
	}
	
	public void show(Graphics g, boolean fill){
		g.setColor(ObstacleColor[type.ordinal()]);
		super.show(g, fill);
	}
	
	public Color getColor(){
		return ObstacleColor[type.ordinal()];
	}
	
	public int getMovingCost(){
		return MovingCost[type.ordinal()];
	}
	
	public void show(Graphics g, boolean fill, Color c){
		g.setColor(c);
		super.show(g, fill);
	}
	
	public String toString() {
		return "Polygons: \n" + polygons.toString() + "\nReference "
				+ reference.toString();
	}

	public int getNumber(){
		return number;
	}
}
