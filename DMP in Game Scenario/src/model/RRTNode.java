package model;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import planner.LPHandler;
import planner.LocalPlanner;


public class RRTNode {
	public Configuration config = null;
	private Hashtable<Integer, RRTNode> children = null;
	private RRTNode parent = null;
	private int mark = 0;
	private int isPathFlag = 0;
	private LocalPlanner LP= null; //從parent到this node所用到的localPlanner
	
	public RRTNode(Configuration config){
		this.config = config;
		children = new Hashtable<Integer, RRTNode>();
	}
	
	public void addChild(RRTNode child){
		if(child == null){
			return;
		}
		children.put(child.hashCode(), child);
		child.parent = this;
	}
	
	public void removeChild(RRTNode child){
		children.remove(child.hashCode());
	}
	
	public void setParent(RRTNode newParent, LocalPlanner thisLP){
		this.parent = newParent;
		this.LP = thisLP;
		if(newParent != null){
			newParent.children.put(this.hashCode(), this);
		}
	}
	
	public static float distance(RRTNode q1,RRTNode q2){
		return Configuration.distance(q1.config, q2.config);  
	}
	
	public RRTNode getParent(){
		return parent;
	}
	
	public int getNumOfChildren(){
		return children.size();
	}
	
	public ArrayList<RRTNode> getChildren(){
		ArrayList<RRTNode> arrayChildren = new ArrayList<RRTNode>();
		Enumeration<Integer> enumb = children.keys();
		while(enumb.hasMoreElements()){
			Object key = enumb.nextElement();
			arrayChildren.add(children.get(key));
		}
		return arrayChildren;
	}
	
	public void changeToRoot(){
		changeParent(null, LPHandler.getDefaultLP());
	}
	
	public void changeParent(RRTNode newParent, LocalPlanner thisLP){
		if(this == newParent){
			return;
		}
		
		RRTNode newChild = this.parent;
		LocalPlanner newChildLP = this.getLocalPlanner();
		
		if(newParent != null){
			children.remove(newParent.hashCode());
			newParent.children.put(this.hashCode(), this);
			this.parent = newParent;
			this.LP = thisLP;
		}
		else{//this node is the new root
			this.parent = null;
			//root node 不需要紀錄local planner 現在先用預設的walk planner代替
			this.setLocalPlanner(LPHandler.getDefaultLP());
		}
		
		if(newChild != null){
			//newChild.setLocalPlanner(newChildLP);
			newChild.children.remove(this.hashCode());
			this.children.put(newChild.hashCode(), newChild);
			newChild.changeParent(this, newChildLP);
		}
	}
	
	public int increaseMark(){
		mark ++;
		return mark;
	}
	
	public int getMarkNo(){
		return mark;
	}
	
	public void clearMark(){
		mark = 0;
	}
	
	public boolean isPath(){
		if(isPathFlag > 0){
			return true;
		}
		else if(isPathFlag < 0){
			System.err.println("RRTNode:" + this.hashCode() + " PathFlag < 0");
		}
		return false;
	}
	
	public void setIsPath(boolean flag){
		if(flag == true){
			isPathFlag ++;
		}
		else{
			isPathFlag --;
		}
	}
	
	public void setLocalPlanner(LocalPlanner LP){
		this.LP = LP;
	}
	
	public LocalPlanner getLocalPlanner(){
		return LP;
	}
}
