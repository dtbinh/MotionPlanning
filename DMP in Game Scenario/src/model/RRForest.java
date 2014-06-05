package model;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;

import View.GraphPanel;

import planner.LPHandler;
import planner.LocalPlanner;
import planner.WalkPlanner;
import util.Point;
import util.SortedList;


public class RRForest {
	private Hashtable<Integer, RRTree> rrts = null;
	private Data data = Data.getInstance();
	private static Color[] rrfColor = {new Color(118,181,211), new Color(118,211,150), new Color(215,220,110), new Color(210,120,120), new Color(187,116,214), new Color(251,181,130), new Color(150,243,250), new Color(255,100,180), new Color(100,100,255), new Color(255,128,128)};
	
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
	
	
	public ArrayList<RRTree> getRRTs(){
		synchronized(rrts){
		ArrayList<RRTree> arrayRRTs = new ArrayList<RRTree>();
		Enumeration<RRTree> enume = rrts.elements();
		while(enume.hasMoreElements()){
			arrayRRTs.add((RRTree)enume.nextElement());
		}
		if(arrayRRTs.size() == 0)
			return null;
		return arrayRRTs;
		}
	}
	
	public synchronized Path RRF_FirstCONNECT(Configuration config1, Configuration config2, int K){
		LocalPlanner LP = new WalkPlanner();
		if(LP.isCollide(data.getRobot(), config1) || LP.isCollide(data.getRobot(), config2)){
			System.out.println("RRF_CONNECT input configs collide");
			return null;
		}
		RRTNode node1 = new RRTNode(config1);
		RRTNode node2 = new RRTNode(config2);
		return new Path(RRF_CONNECT_core(node1, node2, K));
	}
	
	public synchronized ArrayList<RRTNode> RRF_CONNECT(RRTNode node1, RRTNode node2, int K){
			RRTree rrt1 = getRRTbyNode(node1);
			RRTree rrt2 = getRRTbyNode(node2);
			if(rrt1 == null) System.err.println("RRF_CONNECT: rrt1 == null");
			if(rrt2 == null) System.err.println("RRF_CONNECT: rrt2 == null");
			if(rrt1 == rrt2){
				rrt1.changeRoot(node1);
				return getPath(node1, node2, rrt1.root);
			}
			else{
				rrts.remove(rrt1.hashCode());
				rrts.remove(rrt2.hashCode());
				node1.changeToRoot();
				node2.changeToRoot();
				return RRF_CONNECT_core(node1, node2, K);
			}
	}
	
	public synchronized ArrayList<RRTNode> RRF_CONNECT(Configuration config1, RRTNode node2, int K){
		RRTNode node1 = new RRTNode(config1);
		RRTree rrt2 = getRRTbyNode(node2);
		
		if(rrt2 == null){
			System.err.println("ERROR: rrt2 == null\n\tat RRForest.RRF_CONNECT(Configuration, RRTNode, int");
			return null;
		}
		else{
			rrts.remove(rrt2.hashCode());
			node2.changeToRoot();
			return RRF_CONNECT_core(node1, node2, K);
		}
	}
	
	private synchronized ArrayList<RRTNode> RRF_CONNECT_core(RRTNode node1, RRTNode node2, int K){
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
				if(isInOneTree(rrt2, mergeRRTs(rrt1, newNode))){
					return getPath(node1, node2, rrt1.root);
				}
			}			
			//SWAP
			RRTree tmp = rrt1;
			rrt1 = rrt2;
			rrt2 = tmp;
		}
		
		//only fail to get the path after K rounds will go to here returning null
		return null;
	}

	public synchronized void updateRRF(Obstacle obstacle){
		ArrayList<RRTNode> candidates = getCandidates(obstacle);
		updateRRF(candidates);		
	}
	
	public synchronized void updateRRF(RRTNode node){
		ArrayList<RRTNode> candidate = new ArrayList<RRTNode>();
		candidate.add(node);
		updateRRF(candidate);
	}
	
	public synchronized void updateRRF(Iterable<RRTNode> candidates){
		ArrayList<RRTNode> freeNodes = new ArrayList<RRTNode>();
		
		//Separate illegal and free nodes
		for(RRTNode node:candidates){
			LPHandler LPH = new LPHandler();
			node.clearMark();
			if(LPH.isCollide(Data.getInstance().getRobot(), node.config, true)){
				//deals with collide nodes: remove these nodes
				//remove the links between collide nodes and it's children
				for(RRTNode child:node.getChildren()){
					child.setParent(null, LPHandler.getDefaultLP());
					RRTree newRRT = new RRTree(child);
					rrts.put(newRRT.hashCode(), newRRT);
				}
				//remove the links between collide nodes and it's parent
				if(node.getParent() == null){
					rrts.remove(getRRT(node).hashCode());
				}
				else{
					node.getParent().removeChild(node);
				}
				//if the node which has been removed is one node in the path then notify the data
				if(node.isPath()){
					data.getPath().invalidatePath(node);
				}
			}
			else{
				freeNodes.add(node);
			}
		}
		
		//deals with free nodes: remove the illegal links between them and their children
		for(RRTNode node:freeNodes){
			for(RRTNode child:node.getChildren()){
				LPHandler LPH = new LPHandler();
				if(!LPH.hasPath(node.config, child.config)){
					node.removeChild(child);
					child.setParent(null, LPHandler.getDefaultLP());
					RRTree newRRT = new RRTree(child);
					rrts.put(newRRT.hashCode(), newRRT);
					
					//if both the node and the child are parts of path then the link is a section of the path
					if(node.isPath() && child.isPath()){
						//invalidate only the latter node
						int compare = data.getPath().sequenceCompare(node, child);
						if(compare == -1){
							System.err.println("ERROR: can't find the sequence of node & child\n\tat RRForest.updateRRF(Iterable<RRTNode> )");
						}
						else {
							data.getPath().invalidatePath(compare);
						}
					}
				}
				else{
					child.setLocalPlanner(LPH.getLocalPlanner());
				}
			}
		}
	}
	
	public ArrayList<RRTNode> getCandidates(Obstacle obstacle){
		ArrayList<RRTNode> candidates = new ArrayList<RRTNode>();
		Point[] boundingBox = obstacle.getBoundingBox(RRTree.e_distance/2 + data.getRobot().getBoundingDiameter()/2);
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
		LPHandler LPH = new LPHandler();
		SortedList nearestNodes = rrt.getNearestNodes(newNode);
		RRTNode nearNode = null;
		
		for(int i=0; i<20 && i<nearestNodes.getSize(); i++){//only consider 20 nearest nodes
			nearNode = nearestNodes.getNode(i);
			if(LPH.hasPath(newNode.config, nearNode.config) && Configuration.distance(newNode.config, nearNode.config) <= RRTree.e_distance){
				nearNode.changeParent(newNode, LPH.getLocalPlanner());
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
	
/*	private ArrayList<RRTNode> getPath(RRTNode initNode, RRTNode goalNode, RRTNode rootNode){
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
				path.add(tmp);
			}
		}
		else if(goalNode == rootNode){
			tmp = initNode;
			for(int i=0; tmp != null; i++){
				path.add(tmp);
				tmp = tmp.getParent();
			}
		}
		else{
			System.err.println("ERROR: getPath Problem");
			return null;
		}
		
		return path;
	}*/
	
	private ArrayList<RRTNode> getPath(RRTNode initNode, RRTNode goalNode, RRTNode rootNode){
		ArrayList<RRTNode> path = new ArrayList<RRTNode>();
		RRTNode tmp = null;
		if(goalNode != rootNode && initNode != rootNode){
			System.err.println("ERROR: goalNode != rootNode && initNode != rootNode\n\tat RRForest.getPath()");
			return null;
		}
		
		if(goalNode == rootNode){
			RRTree rrt = getRRT(rootNode);
			rrt.changeRoot(initNode);
		}
		
		tmp = goalNode;
		Stack<RRTNode> tmpPath = new Stack<RRTNode>();
		while(tmp != null){
			tmpPath.push(tmp);
			tmp = tmp.getParent();
		}
		for(int i=0; tmpPath.size() != 0; i++){
			tmp = tmpPath.pop();
			path.add(tmp);
		}

		return path;
	}
	
	public void show(Graphics g){
		synchronized(rrts){
		Enumeration<RRTree> enume = rrts.elements();
		int k = 0;
		double canvasScale = GraphPanel.CANVAS/GraphPanel.PLANNER;
		
		while(enume.hasMoreElements()){
			ArrayList<RRTNode> nodes = enume.nextElement().getAllNodsbyDFS();
			ArrayList<RRTNode> children;
			for(int i=0; i<nodes.size(); i++){
				if(k<10)
					g.setColor(rrfColor[k]);
				else 
					g.setColor(Color.lightGray);
				Configuration config1 = nodes.get(i).config;
				data.getRobot().getMirrorRobot(config1).show(g, true);
				
				
				
				children = nodes.get(i).getChildren();
				g.setColor(Color.black);
				
				for(int j=0; j<children.size(); j++){
					Configuration config2 = children.get(j).config;
					g.drawLine((int)(config1.x * canvasScale), (int)(GraphPanel.CANVAS - config1.y * canvasScale),
							   (int)(config2.x * canvasScale), (int)(GraphPanel.CANVAS - config2.y * canvasScale));
					}
			}
			
			g.setColor(Color.red);
			for(RRTNode n:nodes){
				if(n.getMarkNo() != 0){
					g.drawString(String.valueOf(n.getMarkNo()), (int)(n.config.x * canvasScale), (int)(GraphPanel.CANVAS - n.config.y * canvasScale));
				}

			}
			
			g.setColor(Color.yellow);
			g.drawString(String.valueOf("H"), (int)((nodes.get(0).config.x-2) * canvasScale), (int)(GraphPanel.CANVAS - nodes.get(0).config.y * canvasScale));
			
			k++;
		}
		
	}
	}
}
