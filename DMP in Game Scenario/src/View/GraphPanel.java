package View;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import model.Player;
import model.Data;

import util.Point;

public class GraphPanel extends JPanel implements Observer{
	public static final double PLANNER = 128, CANVAS = 500;	
	private static final long serialVersionUID = 1L;
	
	private Data data;
	private boolean showPath = false, showRRF = false;
	private Point mouseP = new Point();

	public void update(Observable o, Object arg) {
		if(o instanceof Player){
			mouseP = data.getPlayer().getMouseP();
		}
		repaint();
	}
	
	public GraphPanel() {
		data = Data.getInstance();
		data.addObserver(this);
		data.getPlayer().addObserver(this);
		//TODO 新增成為controlPanel 以及 path的observer
		
		addMouseListener(data.getPlayer());
		addMouseMotionListener(data.getPlayer());
		
		/*Icon icon = new ImageIcon("gif/Blender3D_NormalWalkCycle.gif");
		JLabel label = new JLabel(icon);
		add(label);*/
		/*addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				Point initPair = Transformer.CanvasToPlanner((double) e.getX(), (double) e.getY());
				focusedObject = null;
				
				if(data.getRobot().isInside(initPair) && focusedObject == null){
					focusedObject = data.getRobot();
					dP = new Point(initPair.x - focusedObject.getReferenceConfig().x, initPair.y - focusedObject.getReferenceConfig().y);
					if (e.getModifiers() == 4){
						initAngle = focusedObject.getReferenceConfig().theta;
					}
				}
				
				if(data.getRobot().isInsideGoal(initPair) != null && focusedObject == null){
					goal = true;
					focusedObject = data.getRobot();
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
				Point endP = Transformer.CanvasToPlanner((double) e.getX(), (double) e.getY());
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
				mouseP = Transformer.CanvasToPlanner((double)e.getX(), (double)e.getY());
				repaint();
			}
		});*/
	}

	public Dimension getPreferredSize() {
		return new Dimension((int) CANVAS, (int) CANVAS);
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.setColor(Color.white);
		g.fillRect(0, 0, (int) CANVAS, (int) CANVAS);

		g.setColor(Color.green);
		data.getRobot().getMirrorRobot(data.getRobot().getGoalConfig()).show(g, true);
		//data.getRobot().getMirrorRobot(data.getRobot().getGoalConfig()).showGIF(g);
		
		if(showRRF && data.getRRF() != null){
			data.getRRF().show(g);
		}
		
		if(showPath && data.getPath() != null){
			data.getPath().show(g);
		}
		
		for (int i = 0; i < data.obstacles.size(); i++)
			data.obstacles.get(i).show(g, true);
		/*
		g.setColor(Color.cyan);
		for(int i=0; i< data.obstacles.size(); i++){
			Point p = Transformer.PlannerToCanvas(data.obstacles.get(i).getReferenceConfig().x, data.obstacles.get(i).getReferenceConfig().y);
			g.drawString(String.valueOf(i), (int)p.x, (int)p.y);
		}*/
		
		//g.setColor(data.getRobot().getRobotColor());
		//data.getRobot().show(g, true);
		data.getRobot().showGIF(g);
		
		data.getPlayer().showMirrorObstacle(g);

		
		
		g.setColor(Color.white);
		g.drawString(new String("x:"+mouseP.x+" y:"+mouseP.y), 30, 13);
	}
	
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