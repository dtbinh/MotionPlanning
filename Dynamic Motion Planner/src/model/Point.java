package model;


public class Point {
	public double x, y;

	public Point(){
		this.x = 0;
		this.y = 0;
	}
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Point(Point p){
		this.x = p.x;
		this.y = p.y;
	}
	
	public Point add(double x, double y){
		return new Point(this.x+x, this.y+y);
	}
	
	public Point add(Point p){
		return add(p.x, p.y);
	}	
	
	public Point sub(double x, double y){
		return new Point(this.x-x, this.y-y);
	}

	public Point sub(Point p){
		return sub(p.x, p.y);
	}
	
	public Point rotate(double theta){
		theta = Angle.normalized(theta) * Math.PI / 180;
		return new Point(Math.cos(theta)*x - Math.sin(theta)*y, Math.sin(theta)*x + Math.cos(theta)*y);
	}
	
	public String toString(){
		return "Point X: " + x +" Y: " + y;
	}
}
