package planner;

import java.util.ArrayList;

import tool.DPair;
import tool.IPair;

public class MyBitMap {
	final public static int SIZE = 128;
	
	int[][] map =  new int[SIZE][SIZE];
	ArrayList<int[][]> maps = new ArrayList<int[][]>();
	private int numberOfMaps = 0;
	
	ArrayList<Obstacle> o;
	
	public MyBitMap(ArrayList<Obstacle> in_o){
		o = in_o;
		for(int i=0; i<SIZE; i++)
			for(int j=0; j<SIZE; j++)
				map[i][j] = 254;
		drawObstacles(o);
	}
	
	protected void addMap(int [][] newMap){
		numberOfMaps ++;
		maps.add(newMap);
	}
	
	public int[][] getMap(int no){
		return maps.get(no);
	}
	
	public int getNumberOfMaps(){
		return numberOfMaps;
	}

	private void drawObstacles(ArrayList<Obstacle> o){
		for(int i=0; i<o.size(); i++)
			for(int j=0; j<o.get(i).getPlannerRefMyPoly().size(); j++){
				BitPolygon bitPoly = new BitPolygon(o.get(i).getPlannerRefMyPoly().get(j));
				for(int m=bitPoly.getMinX(); m<=bitPoly.getMaxX(); m++)
					for(int n=bitPoly.getMinY_in_Xaxis(m); n<=bitPoly.getMaxY_in_Xaxis(m); n++)
						if(m<128 && m>=0 &&n <128 && n>=0)//
							map[m][n] = 255;
				}			
	}
	
	private class BitPolygon{
		ArrayList<IPair> p = new ArrayList<IPair>();
		int minX=127, maxX=0;
		
		public BitPolygon(MyPolygon poly){
			for(int i=0; i<poly.npoints; i++){
				DPair p1 = new DPair(poly.xpoints[i], poly.ypoints[i]);
				DPair p2;
				if(i+1 == poly.npoints)
					p2 = new DPair(poly.xpoints[0], poly.ypoints[0]);
				else
					p2 = new DPair(poly.xpoints[i+1], poly.ypoints[i+1]);
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
			int minY = 127;
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
