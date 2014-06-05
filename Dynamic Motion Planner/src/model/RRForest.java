package model;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;

import planner.WalkPlanner;
import tool.CollisionDetector;


public class RRForest {
	private Hashtable<Integer, RRTree> rrts = null;
	private Data data = Data.getInstance();
	
	public RRForest(){
		rrts = new Hashtable<Integer, RRTree>();
	}
	
	public RRTree getTree(int index){
		Enumeration<RRTree> enume = rrts.elements();
		RRTree rrt = null;
		for(int i=0; i<index; i++){
			if(enume.hasMoreElements()){
				rrt = (RRTree)enume.nextElement();
			}
			else 
				return null;
		}
		return rrt;
	}

	public int getNumOfRRTs(){
		return rrts.size();
	}

	private RRTree getRRT(RRTNode rootNode){
		Enumeration<RRTree> enume = rrts.elements();
		RRTree rrt = null;
		//find the rrt by the root node first
		while(enume.hasMoreElements()){
			rrt = (RRTree)enume.nextElement();
			if(rrt.root == rootNode){
				return rrt;
			}
		}
		return null;
	}
	
	/*
	private RRTree getRRTbyNode(RRTNode node){
		//find the rrt by all of the nodes in all rrts
		Enumeration<RRTree> enume = rrts.elements();
		RRTree rrt = null;
		while(enume.hasMoreElements()){
			rrt = (RRTree)enume.nextElement();
			RRTNode tmp = null;
			Stack<RRTNode> dfs = new Stack<RRTNode>();
			dfs.add(rrt.root);
			while(!dfs.isEmpty()){
				tmp = dfs.pop();
				ArrayList<RRTNode> children = tmp.getChildren();
				for(RRTNode child:children){
					dfs.push(child);
				}
				if(tmp == node){
					return rrt;
				}
			}
		}
		return null;
	}
	*/
	
	public ArrayList<RRTree> getRRTs(){
		ArrayList<RRTree> arrayRRTs = new ArrayList<RRTree>();
		Enumeration<RRTree> enume = rrts.elements();
		while(enume.hasMoreElements()){
			arrayRRTs.add((RRTree)enume.nextElement());
		}
		if(arrayRRTs.size() == 0)
			return null;
		return arrayRRTs;
	}
	
	public ArrayList<RRTNode> RRF_FirstCONNECT(Configuration config1, Configuration config2, int K){
		CollisionDetector CD = data.getCollisionDetector();
		if(CD.isCollide(data.robot, config1) || CD.isCollide(data.robot, config2)){
			System.out.println("RRF_CONNECT input configs collide");
			return null;
		}
		RRTNode node1 = new RRTNode(config1);
		RRTNode node2 = new RRTNode(config2);
		data.setGoalNode(node2);
		return RRF_CONNECT(node1, node2, K);
	}
	
	public ArrayList<RRTNode> RRF_CONNECT(RRTNode node1, RRTNode node2, int K){
		RRTree rrt1 = new RRTree(node1);
		RRTree rrt2 = new RRTree(node2);
		rrts.put(rrt1.hashCode(), rrt1);
		rrts.put(rrt2.hashCode(), rrt2);

		if(isInOneTree(rrt1, mergeRRTs(rrt2, node2)))
			return getPath(node1, node2, rrt2.root);
		if(isInOneTree(rrt2, mergeRRTs(rrt1, node1)))
			return getPath(node1, node2, rrt1.root);
		
		for(int k=0; k<K; k++){
			RRTNode newNode = rrt1.extend(new RRTNode(Configuration.getRandomConfig()));
			if(newNode != null){
				if(isInOneTree(rrt2, mergeRRTs(rrt1, newNode)))
					return getPath(node1, node2, rrt1.root);
			}			
			//SWAP
			RRTree tmp = rrt1;
			rrt1 = rrt2;
			rrt2 = tmp;
		}
		
		//only fail to get the path after K rounds will go to here returning null
		return null;
	}

	public void updateRRF(Obstacle obstacle){
		ArrayList<RRTNode> candidates = getCandidates(obstacle);
		updateRRF(candidates);		
	}
	
	public void updateRRF(RRTNode node){
		ArrayList<RRTNode> candidate = new ArrayList<RRTNode>();
		candidate.add(node);
		updateRRF(candidate);
	}
	
	public void updateRRF(Iterable<RRTNode> candidates){
		ArrayList<RRTNode> collideNodes = new ArrayList<RRTNode>();
		ArrayList<RRTNode> freeNodes = new ArrayList<RRTNode>();
		
		CollisionDetector CD = data.getCollisionDetector();
		WalkPlanner LP = new WalkPlanner();
		
		//Separate illegal and free nodes
		for(RRTNode node:candidates){
			node.clearMark();
			if(CD.isCollide(Data.getInstance().robot, node.config, true)){
				collideNodes.add(node);
			}
			else{
				freeNodes.add(node);
			}
		}
		
		//deals with collide nodes: remove these nodes
		for(RRTNode node:collideNodes){
			//remove the links between collide nodes and it's children
			for(RRTNode child:node.getChildren()){
				if(!collideNodes.contains(child)){
					RRTree newRRT = new RRTree(child);
					child.setParent(null);
					rrts.put(newRRT.hashCode(), newRRT);
				}
			}
			//remove the links between collide nodes and it's parent
			if(node.getParent() == null){
				//TODO test
				RRTree rrt = getRRT(node);
				if(rrt != null){
					//rrts.remove(getRRT(node).hashCode());
					rrts.remove(rrt.hashCode());
				}
				else{
					System.out.println("ERROR: rrt == null");
				}
			}
			else{
				node.getParent().removeChild(node);	
				//if the node which has been removed is one node in the path then notify the data
				if(node.isPath()){
					data.invalidatePath(node);
				}
			}
		}
		
		//deals with free nodes: remove the illegal links between them and their children
		for(RRTNode node:freeNodes){
			for(RRTNode child:node.getChildren()){
				if(!LP.hasPath(node.config, child.config)){
					RRTree newRRT = new RRTree(child);
					child.setParent(null);
					rrts.put(newRRT.hashCode(), newRRT);
					node.removeChild(child);
					//if both the node and the child are parts of path then the link is a section of the path
					if(node.isPath() && child.isPath()){
						//set only the latter node as not path
						if(data.path.indexOf(node) <= data.path.indexOf(child)){
							data.invalidatePath(child);
						}
						else{
							data.invalidatePath(node);
						}
					}
					else if(node.isPath()){
						data.invalidatePath(node);
					}
					else if(child.isPath()){
						data.invalidatePath(child);
					}
				}
			}
		}
	}
	
	public ArrayList<RRTNode> getCandidates(Obstacle obstacle){
		ArrayList<RRTNode> candidates = new ArrayList<RRTNode>();
		Point[] boundingBox = obstacle.getBoundingBox(RRTree.e_distance/2 + data.robot.getBoundingDiameter()/2);
		Enumeration<RRTree> enume = rrts.elements();
		while(enume.hasMoreElements()){
			RRTree rrt = (RRTree)enume.nextElement();
			RRTNode tmp = null;
			Stack<RRTNode> dfs = new Stack<RRTNode>();
			dfs.add(rrt.root);
			while(!dfs.isEmpty()){
				tmp = dfs.pop();
				ArrayList<RRTNode> children = tmp.getChildren();
				for(RRTNode child:children){
					dfs.push(child);
				}
				//if inside the bounding box
				if(tmp.config.x < boundingBox[0].x && tmp.config.y < boundingBox[0].y
						&& tmp.config.x > boundingBox[1].x && tmp.config.y > boundingBox[1].y){
					candidates.add(tmp);
				}
			}
		}
		return candidates;
	}
	
	private boolean connect(RRTree rrt, RRTNode newNode){
		WalkPlanner LP = new WalkPlanner();
		SortedList nearestNodes = rrt.getNearestNodes(newNode);
		RRTNode nearNode = null;
		
		for(int i=0; i<20 && i<nearestNodes.getSize(); i++){//only consider 20 nearest nodes
			nearNode = nearestNodes.getNode(i);
			if(LP.hasPath(newNode.config, nearNode.config) && Configuration.distance(newNode.config, nearNode.config) <= RRTree.e_distance){
				nearNode.changeParent(newNode);
				return true;
			}	
		}
		return false;
	}
	
	private ArrayList<RRTree> mergeRRTs(RRTree rrtA, RRTNode newNode){
		Enumeration<RRTree> enume = rrts.elements();
		ArrayList<RRTree> mergedTrees = new ArrayList<RRTree>();
		while(enume.hasMoreElements()){
			RRTree rrt = (RRTree)enume.nextElement();
			if(rrt != rrtA){
				if(connect(rrt, newNode)){
					rrts.remove(rrt.hashCode());
					mergedTrees.add(rrt);
				}
			}
		}
		return mergedTrees;
	}

	private boolean isInOneTree(RRTree childTree, ArrayList<RRTree> mergedTrees){
		for(RRTree rrt:mergedTrees){
			if(rrt == childTree)
				return true;
		}
		return false;
	}
	
	private ArrayList<RRTNode> getPath(RRTNode initNode, RRTNode goalNode, RRTNode rootNode){
		ArrayList<RRTNode> path = new ArrayList<RRTNode>();
		RRTNode tmp = null;
		if(initNode == rootNode){
			tmp = goalNode;
			Stack<RRTNode> tmpPath = new Stack<RRTNode>();
			while(tmp != null){
				tmpPath.push(tmp);
				tmp = tmp.getParent();
			}
			for(int i=0; tmpPath.size() != 0; i++){
				tmp = tmpPath.pop();
				tmp.setIsPath(true);
				path.add(tmp);
			}
			if(path.size() <= 0 || path.get(0) != initNode){
				System.out.println("RRForest.getPath problem 1");
			}
			
		}
		else if(goalNode == rootNode){
			tmp = initNode;
			for(int i=0; tmp != null; i++){
				tmp.setIsPath(true);
				path.add(tmp);
				tmp = tmp.getParent();
			}
			if(path.size() <= 0 || path.get(path.size()-1) != goalNode){
				System.out.println("RRForest.getPath problem 2");
			}
		}
		else{
			return null;
		}
		
		return path;
	}
}
