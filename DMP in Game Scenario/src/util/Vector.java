package util;

import model.Configuration;

public class Vector {
	public double vx;
	public double vy;
	public double vtheta;
	
	public Vector(Configuration config1, Configuration config2){
		vx = config2.x - config1.x;
		vy = config2.y - config1.y;
		vtheta = config2.theta - config1.theta;		
	}
	
	public Vector(double vx, double vy, double vtheta){
		this.vx = vx;
		this.vy = vy;
		this.vtheta = vtheta;
	}
	
	public Vector getUnitVector(){
		double unitLength = Math.sqrt(Math.pow(vx, 2) + Math.pow(vy, 2) + Math.pow(vtheta, 2));
		return new Vector(vx/unitLength, vy/unitLength, vtheta/unitLength);
	}
	
	public Vector scaleVector(double scale){
		vx *= scale;
		vy *= scale;
		vtheta *= scale;
		return this;
	}
}
