package planner;

import java.util.ArrayList;

import tool.Angle;
import tool.DPair;
import tool.LocalToPlanner;

public class Smooth {
	private Robot robot;
	private ArrayList<Obstacle> obstacles;
	private ArrayList<LocalToPlanner> PObstacles = new ArrayList<LocalToPlanner>();
	private ArrayList<Configuration> path;
	private int times = 2;

	public Smooth(Robot in_r, ArrayList<Configuration> in_p, ArrayList<Obstacle> in_os) {
		robot = in_r;
		obstacles = in_os;
		path = in_p;
		for (int i = 0; i < obstacles.size(); i++)
			PObstacles.add(new LocalToPlanner(obstacles.get(i)));
	}

	public ArrayList<Configuration> doSmooth() {
		times = (int)(Math.random()*8+2);
		return doRandSmooth(0, path.size()-1);
	}
	
	public int getDivisor(){
		return times;
	}
	
	public int getSteps(){
		return path.size();
	}
	
	private ArrayList<Configuration> doRandSmooth(
			int head, int last) {
		if (last-head <= times){
			ArrayList<Configuration> p = new ArrayList<Configuration>();
			for(int i=head; i<=last; i++)
				p.add(path.get(i));
			return p;
		}

		ArrayList<Configuration> newPath = interpolation(path.get(head),path.get(last));
		if (newPath == null) {
			int mid = (last-head) / times;

			newPath = doRandSmooth(head, head+mid);
			for(int i=1; i<times-1; i++)
				newPath.addAll(doRandSmooth(head+i*mid + 1, head+(i+1)*mid));
			newPath.addAll(doRandSmooth(head+(times-1)*mid + 1, last));

		}
		return newPath;
	}

	private ArrayList<Configuration> interpolation(Configuration c1,
			Configuration c2) {
		ArrayList<Configuration> subPath = new ArrayList<Configuration>();
		double dx = c2.getX() - c1.getX();
		double dy = c2.getY() - c1.getY();
		double dAngle = Angle.getDAngle(c2.getAngle() - c1.getAngle());

		double n = Math.abs(dx);
		if (Math.abs(dy) > n)
			n = Math.abs(dy);
		if (Math.abs(dAngle) / 5 > n)
			n = Math.abs(dAngle) / Angle.ARange;

		double eachX = dx / n;
		double eachY = dy / n;
		double eachAngle = (dAngle / n);

		for (int i = 0; i < n; i++) {
			subPath.add(new Configuration(c1.getX() + eachX * i, c1.getY()
					+ eachY * i, Angle
					.normalized(c1.getAngle() + eachAngle * i)));
			if (detectCollision(subPath.get(i)))
				return null;
		}
		subPath.add(c2);

		return subPath;
	}

	private boolean detectCollision(Configuration c) {
		LocalToPlanner PR = new LocalToPlanner(robot.copyRobot(c));
		for (int i = 0; i < PR.getReferenceMyPolygon().size(); i++)
			for (int j = 0; j < PR.getReferenceMyPolygon().get(i).npoints; j++) {
				DPair p1 = new DPair(
						PR.getReferenceMyPolygon().get(i).xpoints[j], PR
								.getReferenceMyPolygon().get(i).ypoints[j]);
				DPair p2;
				if (j + 1 == PR.getReferenceMyPolygon().get(i).npoints)
					p2 = new DPair(
							PR.getReferenceMyPolygon().get(i).xpoints[0], PR
									.getReferenceMyPolygon().get(i).ypoints[0]);
				else
					p2 = new DPair(
							PR.getReferenceMyPolygon().get(i).xpoints[j + 1],
							PR.getReferenceMyPolygon().get(i).ypoints[j + 1]);

				if (detectIntersectWithObstacles(p1, p2))
					return true;

				if (p1.x < 0 || p1.y < 0 || p2.x < 0 || p2.y < 0
						|| p1.x >= MyBitMap.SIZE || p1.y >= MyBitMap.SIZE
						|| p2.x >= MyBitMap.SIZE || p2.y >= MyBitMap.SIZE)
					return true;
			}
		for (int i = 0; i < robot.getControlPoints().size(); i++) {
			DPair pc = PR.getReferenceControlPoint(i);
			if (Math.ceil(pc.x) < 0 || Math.ceil(pc.x) >= MyBitMap.SIZE
					|| Math.ceil(pc.y) < 0 || Math.ceil(pc.y) >= MyBitMap.SIZE)
				return true;
		}
		return false;
	}

	private boolean detectIntersectWithObstacles(DPair p1, DPair p2) {
		for (int i = 0; i < PObstacles.size(); i++)
			for (int j = 0; j < PObstacles.get(i).getReferenceMyPolygon()
					.size(); j++)
				for (int k = 0; k < PObstacles.get(i).getReferenceMyPolygon()
						.get(j).npoints; k++) {
					DPair p3 = new DPair(
							PObstacles.get(i).getReferenceMyPolygon().get(j).xpoints[k],
							PObstacles.get(i).getReferenceMyPolygon().get(j).ypoints[k]);
					DPair p4;
					if (k + 1 == PObstacles.get(i).getReferenceMyPolygon().get(
							j).npoints)
						p4 = new DPair(PObstacles.get(i)
								.getReferenceMyPolygon().get(j).xpoints[0],
								PObstacles.get(i).getReferenceMyPolygon()
										.get(j).ypoints[0]);
					else
						p4 = new DPair(PObstacles.get(i)
								.getReferenceMyPolygon().get(j).xpoints[k + 1],
								PObstacles.get(i).getReferenceMyPolygon()
										.get(j).ypoints[k + 1]);

					if (((p1.y - p2.y) * (p3.x - p1.x) + (p2.x - p1.x)
							* (p3.y - p1.y))
							* ((p1.y - p2.y) * (p4.x - p1.x) + (p2.x - p1.x)
									* (p4.y - p1.y)) < 0
							&& ((p3.y - p4.y) * (p1.x - p3.x) + (p4.x - p3.x)
									* (p1.y - p3.y))
									* ((p3.y - p4.y) * (p2.x - p3.x) + (p4.x - p3.x)
											* (p2.y - p3.y)) < 0) {
						return true;
					}
				}
		return false;
	}
}
