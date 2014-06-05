package planner;

import tool.Angle;

public class Configuration {
	private double x, y, theta;
	
	public Configuration(double x1, double y1, double theta1){
		this.x = x1;
		this.y = y1;
		this.theta = Angle.normalized(theta1);
	}
	public Configuration(Configuration c){
		this.x = c.x;
		this.y = c.y;
		this.theta = Angle.normalized(c.theta);
	}
	
	public String toString(){
		return "Configuration X: " + this.x + " Y: " + this.y	+ " Theta: " + this.theta;
	}
	
	public double getX(){
		return x;
	}
	
	public double getY(){
		return y;
	}
	
	public double getAngle(){
		return theta;
	}
	
	public boolean isEqual(Configuration c){
		if(c.x == this.x && c.y == this.y && c.theta == this.theta)
			return true;
		return false;		
	}
	
}
