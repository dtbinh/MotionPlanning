package model;

public class Angle extends Object{
	final public static double Range = 5;
	private double angle;
	public Angle(){
		
	}
	public Angle(double in_a){
		angle = normalized(in_a);
	}
	
	public double getAngle(){
		return angle;
	}
	
	public double getRadianAngle(){
		return angle*Math.PI/180;
	}
	
	public boolean equals(Angle a){
		System.out.println("equals");
		if(a.angle == this.angle)
			return true;
		return false;
	}
	
	static public double normalized(double a){
		while(a>=360)
			a-=360;
		while(a<0)
			a+=360;
		return a;
	}
	
	static public double getDAngle(double a){
		while(a>=180)
			a -= 360;
		while(a<-180)
			a += 360;
		return a;
	}
	
	static public boolean isBetweenA(double a, double a1, double a2){
		if(a1>a2){
			double a3 = a2;
			a2 = a1;
			a1 = a3;
		}
		//a2 is the bigger angle
		if(a2-a1 > 180 && (a>a2 || a<a1))
			return true;
		else if(a2-a1<=180 && (a>a1 && a<a2))
			return true;
		
		return false;
	}
	
	
}
