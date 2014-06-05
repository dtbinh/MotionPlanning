package util;

import java.awt.Polygon;

import model.MyPolygon;

import View.GraphPanel;


public class Transformer extends Point {
	public static Point LocalToPlanner(double x, double y, double angle, double dx, double dy){
		return new Point((Math.cos(angle/180*Math.PI)*x - Math.sin(angle/180*Math.PI)*y + dx), 
						(Math.sin(angle/180 * Math.PI)*x + Math.cos(angle/180* Math.PI)*y+dy));
	}
	
	public static Point CanvasToPlanner(double x, double y){
		return new Point(x*GraphPanel.PLANNER/GraphPanel.CANVAS, 
						(GraphPanel.CANVAS-y)*GraphPanel.PLANNER/GraphPanel.CANVAS);
	}
	
	public static Polygon PlannerToCanvas(MyPolygon myPoly){
		Polygon intPoly = new Polygon();
		for (int i = 0; i < myPoly.npoints; i++) {
			intPoly.addPoint((int)Math.floor((myPoly.xpoints[i] * GraphPanel.CANVAS / GraphPanel.PLANNER)),
					(int)Math.floor((GraphPanel.CANVAS - myPoly.ypoints[i] * GraphPanel.CANVAS / GraphPanel.PLANNER)));
		}
		return intPoly;
	}
	
	public static Point PlannerToCanvas(double x, double y){
		return new Point(x*GraphPanel.CANVAS / GraphPanel.PLANNER, GraphPanel.CANVAS - y*GraphPanel.CANVAS / GraphPanel.PLANNER);
	}
}
