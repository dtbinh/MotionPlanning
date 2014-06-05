package gui;

import java.awt.Graphics;
import java.awt.Polygon;
import java.util.ArrayList;

import planner.MyObject;
import planner.MyPolygon;

public class ShowPolygons {	
	public ShowPolygons(Graphics g, MyObject o, boolean fill) {
		ArrayList<Polygon> s = toCanvas(o.getPlannerRefMyPoly());
		for (int i = 0; i < s.size(); i++)
			if(fill)
				g.fillPolygon(s.get(i));
			else
				g.drawPolygon(s.get(i));
	}
	
	private ArrayList<Polygon> toCanvas(ArrayList<MyPolygon> p) {
		ArrayList<Polygon> intP = new ArrayList<Polygon>();
		for (int j = 0; j < p.size(); j++) {
			intP.add(new Polygon());
			for (int i = 0; i < p.get(j).npoints; i++) {
				intP.get(j).addPoint(
						(int)Math.floor((p.get(j).xpoints[i] * GraphPanel.CANVAS / GraphPanel.PLANNER)),
						(int)Math.floor((GraphPanel.CANVAS - p.get(j).ypoints[i] * GraphPanel.CANVAS / GraphPanel.PLANNER)));
			}
		}
		return intP;
	}
}
