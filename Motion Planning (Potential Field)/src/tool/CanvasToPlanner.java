package tool;

import gui.GraphPanel;

public class CanvasToPlanner extends DPair{
	public CanvasToPlanner(double x, double y){
		super(x*GraphPanel.PLANNER/GraphPanel.CANVAS, (GraphPanel.CANVAS-y)*GraphPanel.PLANNER/GraphPanel.CANVAS);
	}
}
