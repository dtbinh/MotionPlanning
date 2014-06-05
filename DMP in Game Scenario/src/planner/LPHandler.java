package planner;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.Image;

import model.Configuration;
import model.Robot;

public class LPHandler {
	public static LocalPlanner[] LPs = {new WalkPlanner(), new ClimbPlanner()};
	private static int[] LPCost = {1,3};
	private static Color[] LPColor = {Color.red, Color.blue}; 
	private static Image[] LPImg = {Toolkit.getDefaultToolkit().createImage("img/rabbit_swing1.gif"),
									Toolkit.getDefaultToolkit().createImage("img/rabbit_climb1.gif")};
	private int LPNo=0;

	public LPHandler(){
		LPNo = 0;
	}
	
	public LPHandler(LocalPlanner LP){
		for(int i=0; i<LPs.length; i++){
			if(LPs[i] == LP){
				LPNo = i;
				break;
			}
		}	
	}
	
	public static LocalPlanner getDefaultLP(){
		return LPs[0];
	}
	
	public static Color getLPColor(LocalPlanner LP){
		for(int i=0; i < LPs.length; i++){
			if(LP == LPs[i]){
				return LPColor[i];
			}
		}
		return LPColor[0];
	}
	
	public static Image getLPImg(LocalPlanner LP){
		for(int i=0; i < LPs.length; i++){
			if(LP == LPs[i]){
				return LPImg[i];
			}
		}
		return LPImg[0];
	}
	
	public static Image getDefaultImg(){
		return LPImg[0];
	}
	
	
	public static int getLPCost(LocalPlanner LP){
		for(int i=0; i<LPs.length; i++){
			if(LP == LPs[i]){
				return LPCost[i];
			}
		}
		return 0;
	}

	public boolean isCollide(Robot robot, Configuration config, boolean checkInside){
		while(LPNo<LPs.length){
			if(!LPs[LPNo].CD.isCollide(robot, config, checkInside)){
				return false;
			}
			LPNo++;
		}
		return true;
	}
	
	public boolean hasPath(Configuration config1, Configuration config2){
		while(LPNo<LPs.length){
			if(LPs[LPNo].getPath(config1, config2) != null){
				return true;
			}
			LPNo++;
		}
		return false;
	}
	
	public LocalPlanner getLocalPlanner(){
		if(LPNo >= LPs.length){
			System.err.println("ERROR: LPNo out of bound index:"+LPNo+" size:"+LPs.length+"\n\tat LocalPlanner.getLocalPlanner()");
			return null;
		}
		return LPs[LPNo];
	}
}
