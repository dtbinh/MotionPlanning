package planner;

import java.util.ArrayList;

import model.Configuration;

public interface LocalPlanner {
	public boolean hasPath(Configuration config1, Configuration config2);
	public ArrayList<Configuration> getPath(Configuration config1, Configuration config2, boolean debug);
	public ArrayList<Configuration> getPath(Configuration config1, Configuration config2, boolean debug, boolean addLast);
}
