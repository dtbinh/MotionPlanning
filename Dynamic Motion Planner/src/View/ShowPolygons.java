package View;

import java.awt.Graphics;
import java.awt.Polygon;
import java.util.ArrayList;

import model.MyObject;
import model.MyPolygon;



public class ShowPolygons {	
	public ShowPolygons(Graphics g, MyObject o, boolean fill) {
		ArrayList<Polygon> canvasReferencePolygons = toCanvas(o.getPlannerReferencePolygons());
		for (int i = 0; i < canvasReferencePolygons.size(); i++)
			if(fill)
				g.fillPolygon(canvasReferencePolygons.get(i));
			else
				g.drawPolygon(canvasReferencePolygons.get(i));
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
