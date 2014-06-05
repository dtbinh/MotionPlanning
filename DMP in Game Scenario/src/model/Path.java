package model;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import planner.LPHandler;
import util.Point;
import util.Transformer;

import View.GraphPanel;

public class Path extends ArrayList<RRTNode> {

	private static final long serialVersionUID = 2105739787130720079L;
	private boolean pathValid = false;
	
	public Path(){
		pathValid = true;
		
	}
	
	public Path(ArrayList<RRTNode> pathNodes){
		pathValid = true;
		addAll(pathNodes);
	}
	
	public synchronized boolean add(RRTNode newRRTNode){
		return super.add(newRRTNode);
	}
	
	public synchronized int sequenceCompare(RRTNode node1, RRTNode node2){
		int i=0;
		if(Data.getInstance().getDynamicMotionPlanner()!=null){
			i = Data.getInstance().getDynamicMotionPlanner().sequence+1;
		}
		for(; i<size()-1; i++){
			if((get(i)==node1 && get(i+1)==node2 || get(i)==node2 && get(i+1)==node1)){
				return i+1;
			}
		}
		return -1;
	}
	
	public synchronized void connectNewPath(ArrayList<RRTNode> connectPath){
		for(RRTNode node:connectPath){
			node.setIsPath(true);
		}
		if(get(size()-1) == connectPath.get(0)){
			get(size()-1).setIsPath(false);
			remove(size()-1);
		}
		super.addAll(connectPath);
		pathValid = true;
	}
	
	public synchronized void invalidatePath(RRTNode breakNode){
		int index = -1;
		int i=0;
		if(Data.getInstance().getDynamicMotionPlanner()!=null){
			i = Data.getInstance().getDynamicMotionPlanner().sequence+1;
		}
		for(; i<size(); i++){
			if(get(i)==breakNode && get(i).isPath()){
				index = i;
				break;
			}
		}
		invalidatePath(index);
	}
	
	public synchronized void invalidatePath(int index){
		if(index<0){
			//要被invalidate的node可能因為在上一次invalidePath時，因為排序在後被remove了，而不存在於path中
			return;
		}
		pathValid = false;
		while(size() > index){
			get(index).setIsPath(false);
			remove(index);
		}
	}
	
	public void refreshPath(){
		for(RRTNode node:this){
			node.setIsPath(true);
		}
	}
	
	public boolean isPathValid(){
		return pathValid;
	}
	
	public synchronized void show(Graphics g){
		double canvasScale = GraphPanel.CANVAS/GraphPanel.PLANNER;
			
		g.setColor(Color.pink);
		for(int i=size()-1; i>=0; i--){
			if(get(i).isPath()){
				g.setColor(LPHandler.getLPColor(get(i).getLocalPlanner()));
				Data.getInstance().getRobot().getMirrorRobot(get(i).config).show(g, true);
				//g.setColor(Color.cyan);
				//Point p = Transformer.PlannerToCanvas(get(i).config.x, get(i).config.y);
				//g.drawString(String.valueOf(i), (int)p.x, (int)p.y);
			}
		}
		
		for(int i=size()-1; i>=1; i--){
			if(get(i-1).isPath() && get(i).isPath()){
				g.setColor(LPHandler.getLPColor(get(i).getLocalPlanner()));

				Configuration config1 = get(i-1).config;
				Configuration config2 = get(i).config;
				g.drawLine((int)(config1.x * canvasScale), (int)(GraphPanel.CANVAS - config1.y * canvasScale),
						   (int)(config2.x * canvasScale), (int)(GraphPanel.CANVAS - config2.y * canvasScale));
			}
		}
	}
}
