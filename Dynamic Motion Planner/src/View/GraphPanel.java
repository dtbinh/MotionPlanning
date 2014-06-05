package View;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import model.Configuration;
import model.MyObject;
import model.Point;
import model.RRTNode;
import model.Robot;
import model.Data;

import tool.CanvasToPlanner;

public class GraphPanel extends JPanel implements Observer{
	public static final double PLANNER = 128, CANVAS = 500;	
	private static final long serialVersionUID = 1L;
	
	private Data data;
	private Observable observable;
	private MyObject focusedObject;
	private Point dP;
	private double initAngle;
	private boolean goal = false;
	private Color[] rrfColor = {new Color(118,181,211), new Color(118,211,150), new Color(215,220,110), new Color(210,120,120), new Color(187,116,214), new Color(251,181,130), new Color(150,243,250), new Color(255,100,180), new Color(100,100,255), new Color(255,128,128)};
	private boolean showPath = false, showRRF = false;
	
	@Override
	public void update(Observable o, Object arg) {
		repaint();		
	}
	
	public GraphPanel() {
		data = Data.getInstance();
		observable = data;
		observable.addObserver(this);
		
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				Point initPair = new CanvasToPlanner((double) e.getX(), (double) e.getY());
				focusedObject = null;
				
				if(data.robot.isInside(initPair) && focusedObject == null){
					focusedObject = data.robot;
					dP = new Point(initPair.x - focusedObject.getReferenceConfig().x, initPair.y - focusedObject.getReferenceConfig().y);
					if (e.getModifiers() == 4){
						initAngle = focusedObject.getReferenceConfig().theta;
					}
				}
				
				if(data.robot.isInsideGoal(initPair) != null && focusedObject == null){
					goal = true;
					focusedObject = data.robot;
					dP = new Point(initPair.x - ((Robot) focusedObject).getGoalConfig().x, initPair.y - ((Robot) focusedObject).getGoalConfig().y);
					if (e.getModifiers() == 4)
						initAngle = ((Robot) focusedObject).getGoalConfig().theta;
				}
								
				for (int i = 0; i < data.obstacles.size() && focusedObject == null; i++){
					if(data.obstacles.get(i).isInside(initPair)){
						focusedObject = data.obstacles.get(i);
						dP = new Point(initPair.x - focusedObject.getReferenceConfig().x, initPair.y - focusedObject.getReferenceConfig().y);
						if (e.getModifiers() == 4){
							initAngle = focusedObject.getReferenceConfig().theta;
						}
						break;
					}
				}
			}

			public void mouseReleased(MouseEvent e) {
				goal = false;
				repaint();
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				Point endP = new CanvasToPlanner((double) e.getX(), (double) e.getY());
				if (e.getModifiers() == 16 && focusedObject != null) {//LMB
					if (!goal) {
						focusedObject.Move(new Point(endP.x - dP.x, endP.y - dP.y));
						focusedObject.updateInitialConfig();
					} else
						((Robot) focusedObject).MoveGoal(new Point(endP.x - dP.x, endP.y - dP.y));
				} else if (e.getModifiers() == 4 && focusedObject != null) {//RMB
					double angle = 0;
					if (!goal)
						angle = Math.atan2(endP.y - focusedObject.getReferenceConfig().y, endP.x - focusedObject.getReferenceConfig().x) - Math.atan2(dP.y, dP.x);
					else
						angle = Math.atan2(endP.y - ((Robot) focusedObject).getGoalConfig().y, endP.x - ((Robot) focusedObject).getGoalConfig().x) - Math.atan2(dP.y, dP.x);
					
					double setAngle = initAngle + angle * 180 / Math.PI;
					while (setAngle < 0)
						setAngle += 360;
					while (setAngle >= 360)
						setAngle -= 360;
					
					if (!goal) {
						focusedObject.Rotate(setAngle);
						focusedObject.updateInitialConfig();
					} else
						((Robot) focusedObject).RotateGoal(setAngle);
				}
				repaint();
			}

			public void mouseMoved(MouseEvent e) {
				repaint();
			}
		});
	}

	public Dimension getPreferredSize() {
		return new Dimension((int) CANVAS, (int) CANVAS);
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.setColor(Color.white);
		g.fillRect(0, 0, (int) CANVAS, (int) CANVAS);

		g.setColor(Color.green);
		data.robot.copyRobot(data.robot.getGoalConfig()).show(g, true);

		g.setColor(Color.orange);
		data.robot.show(g, true);

		if(showRRF && data.rrf != null){
			for(int k=0; k<data.rrf.getNumOfRRTs(); k++){
				ArrayList<RRTNode> nodes = data.rrf.getTree(k+1).getAllNodsbyDFS();
				for(int i=0; i<nodes.size(); i++){
					if(k<10)
						g.setColor(rrfColor[k]);
					else
						g.setColor(Color.lightGray);
					
					Configuration config1 = nodes.get(i).config;
					data.robot.copyRobot(config1).show(g, true);
					ArrayList<RRTNode> children = nodes.get(i).getChildren();
					g.setColor(Color.black);
					for(int j=0; j<children.size(); j++){
						Configuration config2 = children.get(j).config;
						g.drawLine((int)(config1.x * CANVAS/PLANNER), (int)(CANVAS - config1.y * CANVAS/PLANNER),
							   (int)(config2.x * CANVAS/PLANNER), (int)(CANVAS - config2.y * CANVAS/PLANNER));
					}
				}
				g.setColor(Color.red);
				for(RRTNode n:nodes){
					if(n.getMarkNo() != 0){
						g.drawString(String.valueOf(n.getMarkNo()), (int)(n.config.x * CANVAS/PLANNER), (int)(CANVAS - n.config.y * CANVAS/PLANNER));
					}
				}
			}
		}
		
		if(showPath && data.path != null){
			for(int i=0; i<data.path.size(); i++){
				if(data.path.get(i).isPath()){
					g.setColor(Color.red);
				}
				else{
					g.setColor(Color.pink);
				}
				data.robot.copyRobot(data.path.get(i).config).show(g, true);
			}
			for(int i=0; i<data.path.size()-1; i++){
				if(data.path.get(i).isPath()){
					g.setColor(Color.red);
				}
				else{
					g.setColor(Color.pink);
				}
				Configuration config1 = data.path.get(i).config;
				Configuration config2 = data.path.get(i+1).config;
				g.drawLine((int)(config1.x * CANVAS/PLANNER), (int)(CANVAS - config1.y * CANVAS/PLANNER),
						   (int)(config2.x * CANVAS/PLANNER), (int)(CANVAS - config2.y * CANVAS/PLANNER));
			}
			
		}
		
		g.setColor(Color.darkGray);
		for (int i = 0; i < data.obstacles.size(); i++)
			data.obstacles.get(i).show(g, true);
		
		if(data.isRunning()){
			g.setColor(Color.blue);
			data.robot.copyRobot(data.getDynamicMotionPlanner().getCurrentFrameConfig()).show(g, true);
		}
	}
	
	/*public void animation_init(){
		runningData = data.getRunningData();
	}
*/
	public void setShowPath(boolean b){
		showPath = b;
		repaint();
	}
	
	public boolean isShowPath(){
		return showPath;
	}
	
	public void setShowRRF(boolean b){
		showRRF = b;
		repaint();
	}
	
	public boolean isShowRRF(){
		return showRRF;
	}
}