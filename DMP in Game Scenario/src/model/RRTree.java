package model;

import java.util.ArrayList;
import java.util.Stack;

import planner.LPHandler;

import util.SortedList;
import util.Vector;

public class RRTree {
	public RRTNode root;
	private Robot robot;
	public static final double e_distance = 30;
	public static RRTNode newRoot = null;
	
	
	public RRTree(RRTNode root){//you should maintain the root's parent is null by your own
		this.root = root;
		robot = Data.getInstance().getRobot();
	}
	
	public void changeRoot(RRTNode newRoot){
		root = newRoot;
		newRoot.changeToRoot();
	}
	
	public ArrayList<RRTNode> getAllNodsbyDFS(){
		RRTNode tmp = null;
		Stack<RRTNode> dfs = new Stack<RRTNode>();
		ArrayList<RRTNode> array = new ArrayList<RRTNode>();
		dfs.add(root);
		while(!dfs.isEmpty()){
			tmp = dfs.pop();
			ArrayList<RRTNode> children = tmp.getChildren();
			for(RRTNode child:children){
				dfs.push(child);
			}
			array.add(tmp);
		}
		return array;
	}
	
	public RRTNode extend(RRTNode randNode){
		
		SortedList nearestNodes = getNearestNodes(randNode);
		RRTNode nearNode, newNode;
		Vector vector;
		for(int i=0; i<20 && i<nearestNodes.getSize(); i++){//only consider 20 nearest nodes
			LPHandler LPH = new LPHandler();
			nearNode = nearestNodes.getNode(i);
			vector = new Vector(nearNode.config, randNode.config).getUnitVector().scaleVector(e_distance);
			newNode = new RRTNode(new Configuration(nearNode.config.x+vector.vx, nearNode.config.y+vector.vy, Angle.normalized(nearNode.config.theta+vector.vtheta)));
			if((!LPH.isCollide(robot, newNode.config, true)) && LPH.hasPath(newNode.config, nearNode.config)){
				newNode.setLocalPlanner(LPH.getLocalPlanner());
				nearNode.addChild(newNode);
				return newNode;
			}
		}
		return null;
	}
	
	public SortedList getNearestNodes(RRTNode refNode){
		RRTNode tmp = null;
		Stack<RRTNode> dfs = new Stack<RRTNode>();
		SortedList distanceList = new SortedList();
		
		dfs.add(root);
		while(!dfs.isEmpty()){
			tmp = dfs.pop();
			ArrayList<RRTNode> children = tmp.getChildren();
			for(RRTNode child:children){
				dfs.push(child);
			}
			distanceList.insert(tmp, RRTNode.distance(tmp, refNode));
		}
		
		return distanceList;
	}
}
