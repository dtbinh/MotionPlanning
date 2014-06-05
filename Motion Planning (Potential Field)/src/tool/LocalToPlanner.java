package tool;

import java.util.ArrayList;

import planner.MyObject;
import planner.MyPolygon;
import planner.Robot;

public class LocalToPlanner {
	MyObject o;
	ArrayList<MyPolygon> n;

	public LocalToPlanner(MyObject in_o) {
		o = in_o;
		n = ReferenceMyPolygon();
	}

	public ArrayList<MyPolygon> ReferenceMyPolygon() {
		ArrayList<MyPolygon> normal = new ArrayList<MyPolygon>();
		for (int i = 0; i < o.getMyPolygon().size(); i++) {
			normal.add(new MyPolygon());
			for (int j = 0; j < o.getMyPolygon().get(i).npoints; j++) {
				DPair pair = new Transformer(o.getMyPolygon().get(i).xpoints[j],
											o.getMyPolygon().get(i).ypoints[j],
											o.getReferenceConfig().getAngle(),
											o.getReferenceConfig().getX(),
											o.getReferenceConfig().getY());
				normal.get(i).addPoint(pair.x, pair.y);
			}
		}
		return normal;
	}
	
	public ArrayList<MyPolygon> getReferenceMyPolygon() {
		return n;
	}
	
	public ArrayList<MyPolygon> getGoalMyPolygon() {
		if(o instanceof Robot){
		ArrayList<MyPolygon> goal = new ArrayList<MyPolygon>();
			for (int i = 0; i < o.getMyPolygon().size(); i++) {
				goal.add(new MyPolygon());
				for (int j = 0; j < o.getMyPolygon().get(i).npoints; j++) {
					DPair pair = new Transformer(
							o.getMyPolygon().get(i).xpoints[j], o
									.getMyPolygon().get(i).ypoints[j],
									((Robot)o).getGoalConfig().getAngle(), 
									((Robot)o).getGoalConfig().getX(),
									((Robot)o).getGoalConfig().getY());
					goal.get(i).addPoint(pair.x, pair.y);
				}
			}
			return goal;
		}
		return null;
	}

	public DPair getReferenceControlPoint(int c){
		if(o instanceof Robot){
			DPair pair = new Transformer(((Robot) o).getControlPoints().get(c).x,
										((Robot) o).getControlPoints().get(c).y,
										((Robot) o).getReferenceConfig().getAngle(),
										((Robot) o).getReferenceConfig().getX(),
										((Robot) o).getReferenceConfig().getY());
			return pair;
		}
		return null;
	}
	
	public DPair getGoalControlPoint(int c){
		if(o instanceof Robot){
			DPair pair = new Transformer(((Robot) o).getControlPoints().get(c).x,
										((Robot) o).getControlPoints().get(c).y,
										((Robot) o).getGoalConfig().getAngle(),
										((Robot) o).getGoalConfig().getX(),
										((Robot) o).getGoalConfig().getY());
			return pair;
		}
		return null;
	}
}

