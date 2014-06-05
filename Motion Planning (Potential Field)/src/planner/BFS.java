package planner;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

import tool.Angle;
import tool.DPair;
import tool.LocalToPlanner;

public class BFS {
	ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
	NF1 PF;
	ArrayList<Configuration> path = new ArrayList<Configuration>();
	ArrayList<Configuration> visited = new ArrayList<Configuration>();
	ArrayList<LocalToPlanner> PObstacles = new ArrayList<LocalToPlanner>();

	public BFS(Robot in_robot, ArrayList<Obstacle> in_obstacles, NF1 in_PF) {
		obstacles = in_obstacles;
		PF = in_PF;
		for (int i = 0; i < obstacles.size(); i++)
			PObstacles.add(new LocalToPlanner(obstacles.get(i)));
		Robot robot = in_robot;

		// initialize the Tree
		LinkedList<Node>[] T = new LinkedList[512];
		for (int i = 0; i < MyBitMap.SIZE * 4; i++)
			T[i] = new LinkedList<Node>();

		// add initial Configuration
		LocalToPlanner PRobot = new LocalToPlanner(robot);
		int index = 0;
		for (int i = 0; i < robot.getControlPoints().size(); i++)
			index += PF.getMap(i)[(int) Math.floor(PRobot
					.getReferenceControlPoint(i).x)][(int) Math.floor(PRobot
					.getReferenceControlPoint(i).y)];
		T[index].add(new Node(robot.getInitialConfig(), null));
		visited.add(robot.getInitialConfig());

		boolean SUCCESS = false;
		while (!T[index].isEmpty() && !SUCCESS) {
			int local = 0;
			// get the first Node
			Node N = T[index].getFirst();
			if (robot.isGoal(N.getConfig())) {
				SUCCESS = true;
				//System.out.println(SUCCESS);
				break;
			}
			// get the neighbors
			Configuration[] nextSteps = getNextSteps(N.getConfig());
			for (int i = 0; i < 6; i++) {
				LocalToPlanner PR = new LocalToPlanner(robot
						.copyRobot(nextSteps[i]));
				// remove visited neighbors //remove collision and out of bounds
				if (detectVisited(nextSteps[i]) || detectCollision(PR, robot)) {
					local++;
					continue;
				}

				// evaluate the potential
				int potential = 0;
				for (int j = 0; j < robot.getControlPoints().size(); j++) {
					potential += PF.getMap(j)[(int) Math.ceil(PR
							.getReferenceControlPoint(j).x)][(int) Math.ceil(PR
							.getReferenceControlPoint(j).y)];
				}
				T[potential].add(0, new Node(nextSteps[i], N));
				visited.add(T[potential].getFirst().getConfig());

				if (potential < index)
					index = potential;
			}
			if (local == 6) {
				T[index].remove();

				for (int i = 0; i < MyBitMap.SIZE * 4; i++)
					if (!T[i].isEmpty()) {
						index = i;
						break;
					}
			}

		}
		if (SUCCESS) {
			Stack<Configuration> path1 = new Stack<Configuration>();
			Node previous = T[index].get(0);
			path1.push(previous.getConfig());
			while (previous.getFather() != null) {
				previous = previous.getFather();
				path1.push(previous.getConfig());
			}
			while (!path1.isEmpty())
				path.add(path1.pop());
			//System.out.println("size:" + path.size());
		} else {
			//System.out.println("failure");
			path = null;
		}
	}
	
	public boolean isSuccess(){
		return !(path==null);
	}
	
	public int getSteps(){
		return path.size();
	}

	private Configuration[] getNextSteps(Configuration here) {
		Configuration[] next = new Configuration[6];

		next[0] = new Configuration(here.getX() + 1, here.getY(), here
				.getAngle());
		next[1] = new Configuration(here.getX() - 1, here.getY(), here
				.getAngle());
		next[2] = new Configuration(here.getX(), here.getY() - 1, here
				.getAngle());
		next[3] = new Configuration(here.getX(), here.getY() + 1, here
				.getAngle());
		next[4] = new Configuration(here.getX(), here.getY(), Angle
				.normalized(here.getAngle() + 5));
		next[5] = new Configuration(here.getX(), here.getY(), Angle
				.normalized(here.getAngle() - 5));

		return next;
	}

	private boolean detectCollision(LocalToPlanner PR, Robot robot) {
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

	private boolean detectVisited(Configuration c) {
		for (int i = visited.size() - 1; i >= 0; i--)
			if (visited.get(i).isEqual(c))
				return true;
		return false;
	}

	public ArrayList<Configuration> getPath() {
		return path;
	}

	
	
	private class Node {
		Configuration c;
		Node father;

		public Node(Configuration in_c, Node in_father) {
			c = in_c;
			father = in_father;
		}

		public Configuration getConfig() {
			return c;
		}

		public Node getFather() {
			return father;
		}
	}
}
