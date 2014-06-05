package tool;

import model.Point;
import View.GraphPanel;

public class CanvasToPlanner extends Point{
	public CanvasToPlanner(double x, double y){
		super(x*GraphPanel.PLANNER/GraphPanel.CANVAS, (GraphPanel.CANVAS-y)*GraphPanel.PLANNER/GraphPanel.CANVAS);
	}
}
