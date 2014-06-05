package planner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.SynchronousQueue;

import model.Configuration;
import model.Data;
import model.Obstacle;
import model.RRTNode;
import View.Project_MAIN;

public class DynamicMotionPlanner implements Runnable {
	public int sequence = 0;
	public SynchronousQueue<Configuration> framePipe;
	public RRTNode lastValidNode = null;
	
	private static final int MAX_OBSERVE_ROUND = 10;
	private Data data = null;
	private HashSet<Obstacle> observedObstacles = null;
	private Configuration currentFrameConfig = null;
	
	public DynamicMotionPlanner(){
		data = Data.getInstance();
		observedObstacles = new HashSet<Obstacle>();
		framePipe = new SynchronousQueue<Configuration>();
		/*Iterator<RRTNode> i = data.path.iterator();
		while(i.hasNext()){
			i.next().setIsPath(true);
		}*/
	}
	
	public void addObservedObstacle(Obstacle obstacle){
		observedObstacles.add(obstacle);
	}
	
	public Configuration getCurrentFrameConfig(){
		return currentFrameConfig;
	}
	
	public boolean isNextThreeSequenceNodesMarked(){
		for(int i=sequence+1; i <= sequence+3 && i<data.path.size(); i++){
			if(data.path.get(i).isPath() && data.path.get(i).getMarkNo() > 0){
				return true;
			}
		}
		return false;
	}
	
	public ArrayList<RRTNode> getNextThreeSequenceMarkedNodes(){
		ArrayList<RRTNode> candidates = new ArrayList<RRTNode>();
		for(int i=sequence+1; i <= sequence+3 && i<data.path.size(); i++){
			if(data.path.get(i).isPath() && data.path.get(i).getMarkNo() > 0){
				candidates.add(data.path.get(i));
			}
		}
		return candidates;
	}
	
	private boolean goNextFrame(){
		Configuration nextFrameConfig = null;
		try {
			nextFrameConfig = framePipe.take();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		currentFrameConfig = nextFrameConfig;
		Project_MAIN.GP.repaint();
		return true;
	}
	
	public void run() {
		long timer;
		HashSet<RRTNode> previousMarkedNodes = null;
		HashSet<RRTNode> currentMarkedNodes = new HashSet<RRTNode>();
		HashSet<RRTNode> unvalidateNodes = new HashSet<RRTNode>();
		
		//Project_MAIN.GP.animation_init();
		
		while(true){
			timer = System.currentTimeMillis();

			for(Obstacle obstacle : observedObstacles){
				obstacle.increaseObserveRound();
				currentMarkedNodes.addAll(data.rrf.getCandidates(obstacle));
				if(obstacle.getObserveRound() > MAX_OBSERVE_ROUND){
					observedObstacles.remove(obstacle);
					obstacle.clearObserveRound();
				}
			}
			
			if(previousMarkedNodes == null){
				for(RRTNode n:currentMarkedNodes){
					n.increaseMark();
				}
			}
			else{
				for(RRTNode n:previousMarkedNodes){
					if(currentMarkedNodes.contains(n)){
						if(n.increaseMark()>=10){
							unvalidateNodes.add(n);
						}
					}
					else{
						n.clearMark();
					}
				}
			}
			previousMarkedNodes = currentMarkedNodes;
			currentMarkedNodes = new HashSet<RRTNode>();

			if(!goNextFrame()){
				data.stop();
				return;//end of this thread
			}
			
			while(System.currentTimeMillis()-timer <= 40){
				//if the path is invalid then try to find a new one and update the path
				if(!data.isPathValid()){
					System.out.println("replan");
					ArrayList<RRTNode> newPath = data.findNewPath();
					if(newPath != null){
						System.out.println("Replain SUCCESS!");
						data.connectNewPath(newPath);
					}
				}
				//Verify next three nodes in the path if one of the nodes has been marked
				else if(isNextThreeSequenceNodesMarked()){
					System.out.println("update path node");
					data.rrf.updateRRF(getNextThreeSequenceMarkedNodes());
				}
				//update the those candidates have more then 10 rounds mark
				else if(unvalidateNodes.size() > 0){
					System.out.println("update");
					RRTNode node = (RRTNode)unvalidateNodes.iterator().next();
					unvalidateNodes.remove(node);
					data.rrf.updateRRF(node);
				}
			}
		}	
	}

}
