package model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Observable;

import javax.swing.event.MouseInputListener;

import util.Point;
import util.Transformer;

public class Player extends Observable implements MouseInputListener, Runnable{
	public static enum PANEL{GraphPanel, PlayerPanel};
	private int strength=400;
	private Data data;
	private Obstacle focusedObstacle = null;
	private Obstacle mirrorObstacle = null;
	private Configuration targetConfig = null;
	private Point dP;
	private double initAngle;
	private Point mouseP = new Point();
	private boolean lock = false;
	private boolean isMouseInside = false;
	private ArrayList<Obstacle> oCandidates = null;
	private PPMouseInputListener mouseInputListener;
	private boolean addObstacle = false;
	private Color color = null;
	
	public Player(){
		data = Data.getInstance();
		mouseInputListener = new PPMouseInputListener();
	}
	
	public boolean isMouseInside(){
		return isMouseInside;
	}
	
	public PPMouseInputListener getPPMouseInputListener(){
		return mouseInputListener;
	}
	
	public int getStrength(){
		return strength;
	}
	
	public void setOCandidates(ArrayList<Obstacle> oCandidates){
		this.oCandidates = oCandidates;
	}
	
	public void mouseClicked(MouseEvent e) {

	}

	public void mouseEntered(MouseEvent e) {
		isMouseInside = true;
	}

	public void mouseExited(MouseEvent e) {
		if(!lock){
			isMouseInside = false;
			mirrorObstacle = null;
			targetConfig = null;
			setChanged();
			notifyObservers();
		}
	}

	public void mousePressed(MouseEvent e) {
		if(focusedObstacle == null){
			Point initPair = Transformer.CanvasToPlanner((double) e.getX(), (double) e.getY());
			dP = null;
	
			for (int i = 0; i < data.obstacles.size(); i++){
				if(data.obstacles.get(i).isInside(initPair)){
					focusedObstacle = data.obstacles.get(i);
					dP = new Point(initPair.x - focusedObstacle.getReferenceConfig().x, initPair.y - focusedObstacle.getReferenceConfig().y);
					if (e.getModifiers() == 4){
						initAngle = focusedObstacle.getReferenceConfig().theta;
					}
					break;
				}
			}
		}
	}

	public void mouseReleased(MouseEvent e) {
		if(!lock && focusedObstacle != null){
			if(targetConfig != null && mirrorObstacle != null){
				lock = true;
				new Thread(this).start();
			}
			else{
				focusedObstacle = null;
			}
		}
	}

	public void mouseDragged(MouseEvent e) {
		if(focusedObstacle != null && !lock){
			Point endP = Transformer.CanvasToPlanner((double) e.getX(), (double) e.getY());
			mouseP = endP;
			if (e.getModifiers() == 16) {//LMB
				targetConfig = new Configuration(endP.x - dP.x, endP.y - dP.y, focusedObstacle.getReferenceConfig().theta);
				mirrorObstacle = focusedObstacle.getMirrorObstacle(targetConfig);
			}else if (e.getModifiers() == 4) {//RMB
				double angle = Math.atan2(endP.y - focusedObstacle.getReferenceConfig().y, endP.x - focusedObstacle.getReferenceConfig().x) - Math.atan2(dP.y, dP.x);
				targetConfig = new Configuration(focusedObstacle.getReferenceConfig().x, focusedObstacle.getReferenceConfig().y, Angle.normalized(initAngle + angle * 180 / Math.PI));
				mirrorObstacle = focusedObstacle.getMirrorObstacle(targetConfig);
			}
			setChanged();
			notifyObservers();
		}
	}

	public void mouseMoved(MouseEvent e) {
		mouseP = Transformer.CanvasToPlanner((double)e.getX(), (double)e.getY());
		setChanged();
		notifyObservers();
	}
	
	public Point getMouseP(){
		return mouseP;
	}
	
	public void crossDraged(Obstacle focusedObstacle, Point dP, MouseEvent e){
		this.focusedObstacle = focusedObstacle;
		this.dP = dP;
		//TODO mouseDragged(e);
		if(focusedObstacle != null && !lock){
			Point endP = Transformer.CanvasToPlanner((double) e.getX(), (double) e.getY());
			endP.x += 128;
			mouseP = endP;
			targetConfig = new Configuration(endP.x - dP.x, endP.y - dP.y, focusedObstacle.getReferenceConfig().theta);
			mirrorObstacle = focusedObstacle.getMirrorObstacle(targetConfig);

			setChanged();
			notifyObservers();
		}
	}
	
	public void crossReleased(){
		focusedObstacle = null;
		mirrorObstacle = null;
		targetConfig = null;
		setChanged();
		notifyObservers();
	}
	
	/*public Obstacle getMirrorObstacle(){
		return mirrorObstacle;
	}*/
	
	public void showMirrorObstacle(Graphics g){
		if(mirrorObstacle != null && isMouseInside){
			if(addObstacle){
				//System.out.println("show1");
				mirrorObstacle.show(g, true, color);
			}
			else{
				//System.out.println("show2");
				g.setColor(Color.black);
				mirrorObstacle.show(g, false);
			}
		}
	}
	
	public void run() {
		Configuration originalConfig = focusedObstacle.getReferenceConfig();
		int movingCost = focusedObstacle.getMovingCost();
		//double difference = Math.floor(Math.sqrt(Math.pow(targetConfig.x - originalConfig.x, 2) + Math.pow(targetConfig.y - originalConfig.y, 2)));
		double difference = Math.max(Math.abs(targetConfig.x - originalConfig.x), Math.abs(targetConfig.y - originalConfig.y));
		difference *= movingCost;
		
		double eachX = (targetConfig.x - originalConfig.x) / difference;
		double eachY = (targetConfig.y - originalConfig.y) / difference;
		//double eachA = (targetConfig.theta - originalConfig.theta) / difference;
		
		for(int i=1; i<difference; i++){
			if(!focusedObstacle.Move(new Point(originalConfig.x+eachX*i, originalConfig.y+eachY*i))){
				targetConfig = null;
				focusedObstacle = null;
				mirrorObstacle = null;
				lock = false;
				setChanged();
				notifyObservers();
				return;
			}
			strength --;
			setChanged();
			notifyObservers();
			try {
				Thread.sleep(33);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		focusedObstacle.Move(new Point(targetConfig.x, targetConfig.y));
		
		targetConfig = null;
		focusedObstacle = null;
		mirrorObstacle = null;
		lock = false;
		setChanged();
		notifyObservers();
	}
	
	
	
	
	
	public class PPMouseInputListener implements MouseInputListener, Runnable{
		//private Obstacle mirrorObstacle;
		private static final int EFFECTIVE_TIME = 30;
		private boolean isMouseInside = false;

		
		public Obstacle getMirrorObstacle(){
			return mirrorObstacle;
		}
		
		public void mouseClicked(MouseEvent e) {
			
		}

		public void mouseEntered(MouseEvent e) {
			isMouseInside = true;
		}

		public void mouseExited(MouseEvent e) {
			isMouseInside = false;
		}

		public void mousePressed(MouseEvent e) {
			if(focusedObstacle == null){
				Point initPair = Transformer.CanvasToPlanner((double) e.getX(), (double) e.getY());
				dP = null;
				
				for(int i=0; i<oCandidates.size(); i++){
					if(oCandidates.get(i).isInside(initPair)){
						focusedObstacle = oCandidates.get(i);
						dP = new Point(initPair.x - focusedObstacle.getReferenceConfig().x, initPair.y - focusedObstacle.getReferenceConfig().y);
						if (e.getModifiers() == 4){
							initAngle = focusedObstacle.getReferenceConfig().theta;
						}
						break;
					}
				}
			}			
		}

		public void mouseReleased(MouseEvent e) {
			if(!lock && focusedObstacle != null){
				if(isMouseInside()){
					lock = true;
					new Thread(this).start();
				}
				else{				
					mirrorObstacle = null;
					crossReleased();
				}
			}
		}

		public void mouseDragged(MouseEvent e) {
			if(focusedObstacle != null && !lock){
				Point endP = Transformer.CanvasToPlanner((double) e.getX(), (double) e.getY());
				if (e.getModifiers() == 16) {//LMB
					targetConfig = new Configuration(endP.x - dP.x, endP.y - dP.y, focusedObstacle.getReferenceConfig().theta);
					mirrorObstacle = focusedObstacle.getMirrorObstacle(targetConfig);
				}else if (e.getModifiers() == 4) {//RMB
					double angle = Math.atan2(endP.y - focusedObstacle.getReferenceConfig().y, endP.x - focusedObstacle.getReferenceConfig().x) - Math.atan2(dP.y, dP.x);
					targetConfig = new Configuration(focusedObstacle.getReferenceConfig().x, focusedObstacle.getReferenceConfig().y, Angle.normalized(initAngle + angle * 180 / Math.PI));
					mirrorObstacle = focusedObstacle.getMirrorObstacle(targetConfig);
				}
				setChanged();
				notifyObservers();
				if(isMouseInside()){
					crossDraged(focusedObstacle, dP, e);
				}
			}			
		}

		public void mouseMoved(MouseEvent e) {
			
		}
		
		public void showMirrorObstacle(Graphics g){
			if(mirrorObstacle != null && isMouseInside){
				g.setColor(Color.black);
				mirrorObstacle.show(g, false);
			}
		}
		
		public void run() {
			int targetColor = mirrorObstacle.getColor().getRGB();
			int baseColor = targetColor & 0x00ffffff;
			int eachAlpha = mirrorObstacle.getColor().getAlpha()/EFFECTIVE_TIME;
			addObstacle = true;

			for(int i=0; i<EFFECTIVE_TIME; i++){
				int newColor = baseColor | ((eachAlpha*i)<<24);
				color = new Color(newColor, true);
				setChanged();
				notifyObservers();
				try {
					Thread.sleep(33);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}	
			}
			while(!focusedObstacle.setReferenceConfig(targetConfig)){
			}
			data.obstacles.add(focusedObstacle);
			oCandidates.remove(focusedObstacle);
			strength -= focusedObstacle.getMovingCost()*50;
			
			targetConfig = null;
			focusedObstacle = null;
			mirrorObstacle = null;
			addObstacle = false;
			lock = false;
			setChanged();
			notifyObservers();
		}
	}
}
