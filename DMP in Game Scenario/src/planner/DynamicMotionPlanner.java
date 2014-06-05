package planner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Observable;
import java.util.concurrent.SynchronousQueue;

import model.Configuration;
import model.Data;
import model.Obstacle;
import model.RRTNode;
import model.Robot;

public class DynamicMotionPlanner extends Observable implements Runnable{
	public int sequence = 0;
	public SynchronousQueue<Configuration> framePipe;
	public boolean test = false;
	
	private static final int MAX_OBSERVE_ROUND = 10;
	private Data data = null;
	private HashSet<Obstacle> observedObstacles = null;
	public int frameNo = 0;
	private RRTNode goalNode = null;
	private Thread frameProducer;
	private RRTNode node1, node2;
	LocalPlanner node2LP;
	private int blockCount = 0;
	
	public DynamicMotionPlanner(){
		data = Data.getInstance();
		observedObstacles = new HashSet<Obstacle>();
		framePipe = new SynchronousQueue<Configuration>();
		goalNode = data.getPath().get(data.getPath().size()-1);
	}
	
	public void addObservedObstacle(Obstacle obstacle){
		synchronized(observedObstacles){
			observedObstacles.add(obstacle);
		}
	}
	
	public boolean isNextThreeSequenceNodesMarked(){
		for(int i=sequence+1; i <= sequence+3 && i<data.getPath().size(); i++){
			if(data.getPath().get(i).isPath() && data.getPath().get(i).getMarkNo() > 0){
				return true;
			}
		}
		return false;
	}
	
	public ArrayList<RRTNode> getNextThreeSequenceMarkedNodes(){
		ArrayList<RRTNode> candidates = new ArrayList<RRTNode>();
		for(int i=sequence+1; i <= sequence+3 && i<data.getPath().size(); i++){
			if(data.getPath().get(i).isPath() && data.getPath().get(i).getMarkNo() > 0){
				candidates.add(data.getPath().get(i));
			}
		}
		return candidates;
	}
	
	public ArrayList<RRTNode> findNewPath(){
		//用目前所在的位置
		if(!data.getPath().get(data.getPath().size()-1).isPath()){
			//System.out.println(new CollisionDetector().isCollide(data.getRobot(), data.getRobot().getReferenceConfig()));
			return data.getRRF().RRF_CONNECT(data.getRobot().getReferenceConfig(), goalNode, 50);
		}

		return data.getRRF().RRF_CONNECT(data.getPath().get(data.getPath().size()-1), goalNode, 50);
	}
	
	public boolean goNextSequence(){
		if(sequence+1 < data.getPath().size()-1){
			sequence++;
			blockCount = 0;
			node1 = data.getPath().get(sequence);
			node1.setIsPath(false);
			node2 = data.getPath().get(sequence+1);
			node2LP = node2.getLocalPlanner();
			node2LP.setFrameConfigs(node1.config, node2.config);
			//data.getRobot().setRobotColor(LPHandler.getLPColor(node2LP));
			data.getRobot().setRobotImg(LPHandler.getLPImg(node2LP));
			frameProducer = new Thread(node2LP);
			frameProducer.start();
			//System.out.println("Sequence:"+sequence);
			return true;
		}
		return false;
	}
	
	public void run() {
		long timer;
		HashSet<RRTNode> previousMarkedNodes = null;
		HashSet<RRTNode> currentMarkedNodes = new HashSet<RRTNode>();
		HashSet<RRTNode> unvalidateNodes = new HashSet<RRTNode>();

		sequence = -1;
		goNextSequence();

		while(true){
			timer = System.currentTimeMillis();
			frameNo++;

			//把受到改變的obstacles影響的RRTNodes標記起來
			synchronized(observedObstacles){
				for(Obstacle obstacle : observedObstacles){
					obstacle.increaseObserveRound();
					currentMarkedNodes.addAll(data.getRRF().getCandidates(obstacle));
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
			}
			
			//更新下一個動畫格的Robot configuration
			if(data.getPath().get(data.getPath().size()-1)==goalNode){
			//if(node2.isPath()){
				if(node2 == goalNode && !frameProducer.isAlive()/* && data.getPath().get(data.getPath().size()-1)==goalNode*/){
					data.getPath().get(data.getPath().size()-1).setIsPath(false);
					data.getRobot().setReferenceConfig(data.getPath().get(data.getPath().size()-1).config, LPHandler.getLPCost(node2LP));
					data.getRobot().setRobotImg(Robot.defaultImg);
					setChanged();
					notifyObservers();
					System.out.println("You Lose!!! Robot has arrived his goal!");
					return;
				}
				else{
					try {
						if(!data.getRobot().setReferenceConfig(framePipe.take(), LPHandler.getLPCost(node2LP))){
							System.out.println("the Rabbit is out of Strength! YOU WIN!");
							setChanged();
							notifyObservers();
							return;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					setChanged();
				}
			}
			else{
				if(goNextSequence()){
					try {
						if(!data.getRobot().setReferenceConfig(framePipe.take(), LPHandler.getLPCost(node2LP))){
							System.out.println("the Rabbit is out of Strength! YOU WIN!");
							setChanged();
							notifyObservers();
							return;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					setChanged();
				}
				else if(frameProducer.isAlive()){
					Configuration newConfig = null;
					try {
						newConfig = framePipe.take();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if(!node2LP.isCollide(data.getRobot(), newConfig)){
						if(!data.getRobot().setReferenceConfig(newConfig, LPHandler.getLPCost(node2LP))){
							System.out.println("the Rabbit is out of Strength! YOU WIN!");
							setChanged();
							notifyObservers();
							return;
						}
						setChanged();
					}
					else{
						frameProducer.interrupt();
						if(++blockCount > 100){
							System.out.println("the Rabbit is Blocked! YOU WIN!");
							setChanged();
							notifyObservers();
							return;
						}
						data.getRobot().setRobotImg(Robot.blockImg);
						setChanged();
					}
				}
				else{
					if(++blockCount > 100){
						System.out.println("the Rabbit is Blocked! YOU WIN!");
						return;
					}
					data.getRobot().setRobotImg(Robot.blockImg);
					setChanged();
				}
			}
			notifyObservers();

			//利用空檔的33ms(30fps)做背後的資料計算
			while(System.currentTimeMillis()-timer <= 33){
				//if the path is invalid then try to find a new one and update the path

				if(isNextThreeSequenceNodesMarked()){
					//System.out.println(frameNo + " update path node");
					data.getRRF().updateRRF(getNextThreeSequenceMarkedNodes());
				}
				//Verify next three nodes in the path if one of the nodes has been marked
				
				else if(!data.getPath().isPathValid()){
					//System.out.println(frameNo + " replan");
					ArrayList<RRTNode> newPath = findNewPath();

					if(newPath != null){
						//System.out.println("Replain SUCCESS!");
						data.getPath().connectNewPath(newPath);
					}
				}
				//update the those candidates have more then 10 rounds mark
				else if(unvalidateNodes.size() > 0){
					//System.out.println(frameNo + " update");
					RRTNode node = (RRTNode)unvalidateNodes.iterator().next();
					unvalidateNodes.remove(node);
					data.getRRF().updateRRF(node);
				}
			}
		}
	}

}
