package model;

import java.awt.Graphics;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import View.Project_MAIN;
import View.ShowPolygons;

import tool.PlannerObject;

public abstract class MyObject {
	protected Configuration initial = null, reference = null;
	protected ArrayList<MyPolygon> polygons = new ArrayList<MyPolygon>();
	protected PlannerObject plannerObject = null;

	public abstract String toString();
	
	public static String ReadInNext(Scanner in){
		String s = in.nextLine();
		while(s.isEmpty()|| s.charAt(0) == '#')
			s = in.nextLine();
		return s;
	}
	
	protected void CreatePolygons(int p, Scanner input) {
		this.polygons.add(new MyPolygon());
		int vertices = Integer.parseInt(new StringTokenizer(MyObject
				.ReadInNext(input)).nextToken());

		for (; vertices > 0; vertices--) {
			StringTokenizer token = new StringTokenizer(ReadInNext(input));
			this.polygons.get(p).addPoint(Double.parseDouble(token.nextToken()),
					Double.parseDouble(token.nextToken()));
		}
	}
	

	protected void SetInitialConfig(Scanner input) {
		StringTokenizer token = new StringTokenizer(ReadInNext(input));
		this.initial = new Configuration(Double.parseDouble(token.nextToken()),
				Double.parseDouble(token.nextToken()), Double.parseDouble(token.nextToken()));
		this.reference = new Configuration(initial.x, initial.y, initial.theta);
	}

	public void addPolygon(MyPolygon myPoly){
		plannerObject.setPolygonChanged();
		polygons.add(myPoly);
	}
	
	public ArrayList<MyPolygon> getMyPolygon(){
		return polygons;
	}
	
	public Configuration getInitialConfig(){
		return initial;
	}
	
	public Configuration getReferenceConfig(){
		return reference;
	}
	
	public void show(Graphics g, boolean fill){
		new ShowPolygons(g, this, fill);
	}
	
	public ArrayList<MyPolygon> getPlannerReferencePolygons(){
		return plannerObject.getPlannerReferencePolygons();
	}
	
	
	public boolean isInside(Point p){
		int in;
		ArrayList<MyPolygon> plannerReferencePolygons = this.getPlannerReferencePolygons();
		for(int i=0; i<plannerReferencePolygons.size(); i++){
			in = 0;
			for(int j=0; j<plannerReferencePolygons.get(i).npoints; j++){
				Point p1 = new Point(plannerReferencePolygons.get(i).xpoints[j], plannerReferencePolygons.get(i).ypoints[j]);
				Point p2;
				if(j+1 == plannerReferencePolygons.get(i).npoints)
					p2 = new Point(plannerReferencePolygons.get(i).xpoints[0], plannerReferencePolygons.get(i).ypoints[0]);
				else
					p2 = new Point(plannerReferencePolygons.get(i).xpoints[j+1], plannerReferencePolygons.get(i).ypoints[j+1]);
				
				if((p.x-p1.x)*(p1.y-p2.y)+(p.y-p1.y)*(p2.x-p1.x)>0)
					in++;
			}
			if(in == plannerReferencePolygons.get(i).npoints)
				return true;
		}
		return false;
	}
	
	public Point[] getBoundingBox(double expand){
		Point maxP = new Point(0,0);
		Point minP = new Point(Project_MAIN.MAX_X,Project_MAIN.MAX_Y);
		Point[] boundingBox = {maxP, minP};
		ArrayList<MyPolygon> plannerReferencePolygons = this.getPlannerReferencePolygons();
		
		for(int i=0; i<plannerReferencePolygons.size(); i++){
			for(int j=0; j<plannerReferencePolygons.get(i).npoints; j++){
				if(minP.x > plannerReferencePolygons.get(i).xpoints[j])
					minP.x = plannerReferencePolygons.get(i).xpoints[j];
				if(minP.y > plannerReferencePolygons.get(i).ypoints[j])
					minP.y = plannerReferencePolygons.get(i).ypoints[j];
				if(maxP.x < plannerReferencePolygons.get(i).xpoints[j])
					maxP.x = plannerReferencePolygons.get(i).xpoints[j];
				if(maxP.y < plannerReferencePolygons.get(i).ypoints[j])
					maxP.y = plannerReferencePolygons.get(i).ypoints[j];
			}
		}
		
		maxP.x += expand;
		maxP.y += expand;
		minP.x -= expand;
		minP.y -= expand;
		
		if(maxP.x > Project_MAIN.MAX_X)
			maxP.x = Project_MAIN.MAX_X;
		if(maxP.y > Project_MAIN.MAX_Y)
			maxP.y = Project_MAIN.MAX_Y;
		if(minP.x < 0)
			minP.x = 0;
		if(minP.y < 0)
			minP.y = 0;
		
		return boundingBox;
	}
	
	public double getBoundingDiameter(){
		Point[] boundingBox = getBoundingBox(0);
		double diameter = 0;
		if(diameter < boundingBox[0].x - boundingBox[1].x)
			diameter = boundingBox[0].x - boundingBox[1].x;
		if(diameter < boundingBox[0].y - boundingBox[1].y)
			diameter = boundingBox[0].y - boundingBox[1].y;
		return diameter;
	}
	
	public MyObject Move(Point p){
		return setReferenceConfig(p.x, p.y, reference.theta);
	}
	
	public MyObject Rotate(double theta){
		return setReferenceConfig(reference.x, reference.y, theta);
	}
	
	public MyObject setReferenceConfig(double x, double y, double theta){
		reference = new Configuration(x, y, theta);
		plannerObject.setReferenceChanged();
		return this;
	}
	
	
	
	public void updateInitialConfig(){
		initial = new Configuration(reference.x, reference.y, reference.theta);
	}
	

}
