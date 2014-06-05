package tool;

import java.util.ArrayList;

import model.MyObject;
import model.MyPolygon;
import model.Point;
import model.Robot;



public class LocalToPlanner {
	MyObject o;
	ArrayList<MyPolygon> n = null;

	public LocalToPlanner(MyObject o) {
		this.o = o;
	}

	public MyObject getObject(){
		return o;
	}
	
	public ArrayList<MyPolygon> ReferenceMyPolygon() {
		ArrayList<MyPolygon> normal = new ArrayList<MyPolygon>();
		for (int i = 0; i < o.getMyPolygon().size(); i++) {
			normal.add(new MyPolygon());
			for (int j = 0; j < o.getMyPolygon().get(i).npoints; j++) {
				Point pair = new Transformer(o.getMyPolygon().get(i).xpoints[j],
											o.getMyPolygon().get(i).ypoints[j],
											o.getReferenceConfig().theta,
											o.getReferenceConfig().x,
											o.getReferenceConfig().y);
				normal.get(i).addPoint(pair.x, pair.y);
			}
		}
		return normal;
	}
	
	public ArrayList<MyPolygon> getReferenceMyPolygon() {
		if(n == null){
			n = ReferenceMyPolygon();
		}
		return n;
	}
	
	public ArrayList<MyPolygon> getGoalMyPolygon() {
		if(o instanceof Robot){
		ArrayList<MyPolygon> goal = new ArrayList<MyPolygon>();
			for (int i = 0; i < o.getMyPolygon().size(); i++) {
				goal.add(new MyPolygon());
				for (int j = 0; j < o.getMyPolygon().get(i).npoints; j++) {
					Point pair = new Transformer(
							o.getMyPolygon().get(i).xpoints[j], o
									.getMyPolygon().get(i).ypoints[j],
									((Robot)o).getGoalConfig().theta, 
									((Robot)o).getGoalConfig().x,
									((Robot)o).getGoalConfig().y);
					goal.get(i).addPoint(pair.x, pair.y);
				}
			}
			return goal;
		}
		return null;
	}

	public Point getReferenceControlPoint(int c){
		if(o instanceof Robot){
			Point pair = new Transformer(((Robot) o).getControlPoints().get(c).x,
										((Robot) o).getControlPoints().get(c).y,
										((Robot) o).getReferenceConfig().theta,
										((Robot) o).getReferenceConfig().x,
										((Robot) o).getReferenceConfig().y);
			return pair;
		}
		return null;
	}
	
	public Point getGoalControlPoint(int c){
		if(o instanceof Robot){
			Point pair = new Transformer(((Robot) o).getControlPoints().get(c).x,
										((Robot) o).getControlPoints().get(c).y,
										((Robot) o).getGoalConfig().theta,
										((Robot) o).getGoalConfig().x,
										((Robot) o).getGoalConfig().y);
			return pair;
		}
		return null;
	}
}

