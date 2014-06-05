package planner;

import java.util.ArrayList;

import model.MyPolygon;
import model.Obstacle;
import model.Point;


import tool.IPair;

public class MyBitMap {
	final public static int SIZE = 128;
	
	int[][] map =  new int[SIZE][SIZE];
	private ArrayList<Obstacle> obstacles;
	
	public MyBitMap(ArrayList<Obstacle> obstacles){
		this.obstacles = obstacles;
		for(int i=0; i<SIZE; i++)
			for(int j=0; j<SIZE; j++)
				map[i][j] = 254;
		drawObstacles(this.obstacles);
	}

	public int[][] getMap(){
		return map;
	}
	
	private void drawObstacles(ArrayList<Obstacle> obstacles){
		for(int i=0; i<obstacles.size(); i++){
			ArrayList<MyPolygon> plannerReferencePolygons = obstacles.get(i).getPlannerReferencePolygons();
			for(int j=0; j<plannerReferencePolygons.size(); j++){
				BitPolygon bitPoly = new BitPolygon(plannerReferencePolygons.get(j));
				for(int m=bitPoly.getMinX(); m<=bitPoly.getMaxX(); m++)
					for(int n=bitPoly.getMinY_in_Xaxis(m); n<=bitPoly.getMaxY_in_Xaxis(m); n++)
						if(m<SIZE && m>=0 && n<SIZE && n>=0)//
							map[m][n] = 255;
				}
		}
	}
	
	private class BitPolygon{
		ArrayList<IPair> p = new ArrayList<IPair>();
		int minX=SIZE-1, maxX=0;
		
		public BitPolygon(MyPolygon poly){
			for(int i=0; i<poly.npoints; i++){
				Point p1 = new Point(poly.xpoints[i], poly.ypoints[i]);
				Point p2;
				if(i+1 == poly.npoints)
					p2 = new Point(poly.xpoints[0], poly.ypoints[0]);
				else
					p2 = new Point(poly.xpoints[i+1], poly.ypoints[i+1]);
				int d = (int)Math.ceil(Math.max(Math.abs(p2.x-p1.x), Math.abs(p2.y-p1.y)));
				double dx = (p2.x-p1.x)/(double)d;
				double dy = (p2.y-p1.y)/(double)d;
				for(int j=0; j<d; j++){
					p.add(new IPair((int)(p1.x+j*dx),(int)(p1.y+j*dy)));
					if(p.get(p.size()-1).x>maxX)
						maxX = p.get(p.size()-1).x;
					if(p.get(p.size()-1).x<minX)
						minX = p.get(p.size()-1).x;
				}
			}
		}
		
		int getMinX(){
			return minX;
		}
		
		int getMaxX(){
			return maxX;
		}
		
		int getMinY_in_Xaxis(int Xaxis){
			int minY = SIZE-1;
			for(int i=0; i<p.size(); i++)
				if(p.get(i).x==Xaxis)
					if(p.get(i).y < minY)
						minY = p.get(i).y;
			return minY;
		}
		
		int getMaxY_in_Xaxis(int Xaxis){
			int maxY = 0;
			for(int i=0; i<p.size(); i++)
				if(p.get(i).x==Xaxis)
					if(p.get(i).y > maxY)
						maxY = p.get(i).y;
			return maxY;
		}
		
		public String toString(){
			return p.toString();
		}
	}
}
