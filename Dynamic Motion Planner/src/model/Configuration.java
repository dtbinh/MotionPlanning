package model;

import View.Project_MAIN;

public class Configuration {
	public double x, y, theta;
	
	public Configuration(double x, double y, double theta){
		this.x = x;
		this.y = y;
		this.theta = Angle.normalized(theta);
	}
	public Configuration(Configuration c){
		this.x = c.x;
		this.y = c.y;
		this.theta = Angle.normalized(c.theta);
	}
	
	public String toString(){
		return "X:" + this.x + " Y:" + this.y	+ " Theta:" + this.theta;
	}
	/*
	public double getX(){
		return x;
	}
	
	public double getY(){
		return y;
	}
	
	public double getAngle(){
		return theta;
	}
	*/
	public boolean isEqual(Configuration c){
		if(c.x == this.x && c.y == this.y && c.theta == this.theta)
			return true;
		return false;		
	}
	
	public static Configuration getRandomConfig(){
		return new Configuration(Math.random()*Project_MAIN.MAX_X, Math.random()*Project_MAIN.MAX_Y, Math.random()*360);
	}
	
	public static float distance(Configuration c1,Configuration c2){
		return (float)Math.sqrt(Math.pow((c1.x-c2.x),2)+Math.pow((c1.y-c2.y),2)+Math.pow((c1.theta-c2.theta),2));  
	}
}
