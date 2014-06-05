package planner;

import java.util.Scanner;
import java.util.StringTokenizer;

public class Obstacle extends MyObject{
	private static int numOfObstacle = 0;

	public Obstacle(Scanner input1) throws Exception {
		input = input1;
		numOfObstacle++;
		
		int poly = Integer.parseInt(new StringTokenizer(ReadInNext(input)).nextToken());
		for (int p = 0; p < poly; p++)
			CreatePolygons(p);
		
		SetInitialConfig();

	}

	public String toString() {
		return "Polygons: \n" + polygons.toString() + "\nInitial "
				+ initial.toString();
	}

}
