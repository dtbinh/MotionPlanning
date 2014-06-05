package planner;

import gui.ShowPolygons;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import tool.LocalToPlanner;
import tool.DPair;

public abstract class MyObject {
	protected Configuration initial, reference;
	protected ArrayList<MyPolygon> polygons = new ArrayList<MyPolygon>();
	protected Scanner input;

	MyObject() {

	}
	
	public static String ReadInNext(Scanner in){
		String s = in.nextLine();
		while(s.isEmpty()|| s.charAt(0) == '#')
			s = in.nextLine();
		return s;
	}
	
	protected void CreatePolygons(int p) {
		this.polygons.add(new MyPolygon());
		int vertices = Integer.parseInt(new StringTokenizer(MyObject
				.ReadInNext(input)).nextToken());

		for (; vertices > 0; vertices--) {
			StringTokenizer token = new StringTokenizer(ReadInNext(input));
			this.polygons.get(p).addPoint(Double.parseDouble(token.nextToken()),
					Double.parseDouble(token.nextToken()));
		}
	}

	protected void SetInitialConfig() {
		StringTokenizer token = new StringTokenizer(ReadInNext(input));
		this.initial = new Configuration(Double.parseDouble(token.nextToken()),
				Double.parseDouble(token.nextToken()), Double.parseDouble(token.nextToken()));
		this.reference = new Configuration(initial.getX(), initial.getY(), initial.getAngle());
	}

	public abstract String toString();
	
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
	
	public ArrayList<MyPolygon> getPlannerRefMyPoly(){
		LocalToPlanner n = new LocalToPlanner(this);
		return n.getReferenceMyPolygon();
	}
	
	
	public MyObject isInside(DPair p){
		int in;
		ArrayList<MyPolygon> NormalPoly = this.getPlannerRefMyPoly();
		for(int i=0; i<NormalPoly.size(); i++){
			in = 0;
			for(int j=0; j<NormalPoly.get(i).npoints; j++){
				DPair p1 = new DPair(NormalPoly.get(i).xpoints[j], NormalPoly.get(i).ypoints[j]);
				DPair p2;
				if(j+1 == NormalPoly.get(i).npoints)
					p2 = new DPair(NormalPoly.get(i).xpoints[0], NormalPoly.get(i).ypoints[0]);
				else
					p2 = new DPair(NormalPoly.get(i).xpoints[j+1], NormalPoly.get(i).ypoints[j+1]);
				
				if((p.x-p1.x)*(p1.y-p2.y)+(p.y-p1.y)*(p2.x-p1.x)>0)
					in++;
			}
			if(in == NormalPoly.get(i).npoints)
				return this;
		}
		return null;
	}
	
	public MyObject Move(DPair p){
		reference = new Configuration(p.x, p.y, reference.getAngle());
		return this;
	}
	
	public MyObject Rotate(double a){
		reference = new Configuration(reference.getX(), reference.getY(), a);
		return this;
	}
	
	public void updateInitialConfig(){
		initial = new Configuration(reference.getX(), reference.getY(), reference.getAngle());
	}
}
