package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

import javax.swing.JPanel;

import planner.Configuration;
import planner.MyObject;
import planner.NF1;
import planner.Obstacle;
import planner.Robot;
import tool.CanvasToPlanner;
import tool.DPair;

public class GraphPanel extends JPanel {
	final public static double PLANNER = 128, CANVAS = 500;

	private Planner P;

	private MyObject o;
	private DPair dP;
	private DPair mP = new DPair(0, 0);
	private double initAngle;
	protected boolean paintAllPath = false;
	protected boolean paintLast = false;
	private boolean goal = false;
	boolean animation = false;
	private int clip = 0;
	

	public GraphPanel(Planner in_P){
		P = in_P;

		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				DPair initPair = new CanvasToPlanner((double) e.getX(),
						(double) e.getY());
				o = null;
				for (int i = 0; i < P.robots.size() && o == null; i++){
					o = P.robots.get(i).isInside(initPair);
					if(o != null && P.currentRobot!=i){
						P.currentRobot = i;
						P.reset();
					}
				}
				for (int i = 0; i < P.robots.size() && o == null; i++) {
					o = P.robots.get(i).isInsideGoal(initPair);
					if (o != null){
						if(P.currentRobot != i){
							P.currentRobot = i;
							P.reset();
						}
						goal = true;
					}
				}
				for (int i = 0; i < P.obstacles.size() && o == null; i++)
					o = P.obstacles.get(i).isInside(initPair);
				
				if (o != null && !goal) {
					dP = new DPair(initPair.x
							- o.getReferenceConfig().getX(), initPair.y
							- o.getReferenceConfig().getY());
					if (e.getModifiers() == 4)
						initAngle = o.getReferenceConfig().getAngle();
				} else if (o != null && goal) {
					dP = new DPair(initPair.x
							- ((Robot) o).getGoalConfig().getX(),
							initPair.y - ((Robot) o).getGoalConfig().getY());
					if (e.getModifiers() == 4)
						initAngle = ((Robot) o).getGoalConfig().getAngle();
				}
			}

			public void mouseReleased(MouseEvent e) {
				goal = false;
				repaint();
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				DPair endP = new CanvasToPlanner((double) e.getX(),
						(double) e.getY());
				if (e.getModifiers() == 16 && o != null) {
					if (!goal) {
						o.Move(new DPair(endP.x - dP.x, endP.y - dP.y));
						o.updateInitialConfig();
					} else
						((Robot) o).MoveGoal(new DPair(endP.x - dP.x,
								endP.y - dP.y));
				} else if (e.getModifiers() == 4 && o != null) {
					double angle = 0;
					if (!goal)
						angle = Math.atan2(endP.y
								- o.getReferenceConfig().getY(), endP.x
								- o.getReferenceConfig().getX())
								- Math.atan2(dP.y, dP.x);
					else
						angle = Math
								.atan2(endP.y
										- ((Robot) o).getGoalConfig()
												.getY(), endP.x
										- ((Robot) o).getGoalConfig()
												.getX())
								- Math.atan2(dP.y, dP.x);
					double setAngle = initAngle + angle * 180 / Math.PI;
					while (setAngle < 0)
						setAngle += 360;
					while (setAngle >= 360)
						setAngle -= 360;
					if (!goal) {
						o.Rotate(setAngle);
						o.updateInitialConfig();
					} else
						((Robot) o).RotateGoal(setAngle);
				}
				P.reset();
				mP = new CanvasToPlanner((double) e.getX(), (double) e
						.getY());
				repaint();
			}

			public void mouseMoved(MouseEvent e) {
				mP = new CanvasToPlanner((double) e.getX(), (double) e
						.getY());
				repaint();
			}
		});
	}

	public Dimension getPreferredSize() {
		return new Dimension((int) CANVAS, (int) CANVAS);
	}

	protected void paintLastPosition() {
		paintLast = true;
		//repaint();
	}

	protected Thread paintPath() {
		Thread T = new Thread(new Animation());
		T.start();
		return T;
	}

	protected void paintAllPath() {
		paintAllPath = true;
		repaint();
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.setColor(Color.white);
		g.fillRect(0, 0, (int)CANVAS, (int)CANVAS);
		
		if(!animation)
			for (int i = 0; i < P.robots.size(); i++) {
				Robot r = P.robots.get(i).copyRobot(P.robots.get(i).getGoalConfig());
				if(i==P.currentRobot)
					g.setColor(Color.green);
				else
					g.setColor(Color.lightGray);
				r.show(g, true);
			}

		if (!animation)
			for (int i = 0; i < P.robots.size(); i++){
				if(i==P.currentRobot)
					g.setColor(Color.orange);
				else
					g.setColor(Color.lightGray);
				P.robots.get(i).show(g, true);
			}

		g.setColor(Color.darkGray);
		for (int i = 0; i < P.obstacles.size(); i++)
			P.obstacles.get(i).show(g, true);

		if (animation && P.path != null) {
			g.setColor(Color.orange);
			Robot r = P.robots.get(P.currentRobot).copyRobot(P.path.get(clip));
			r.show(g, true);
		}

		if (paintLast && P.path != null) {
			g.setColor(Color.red);
			Robot r = P.robots.get(P.currentRobot).copyRobot(P.path.get(P.path.size() - 1));
			r.show(g, false);
		}

		if (paintAllPath && P.path != null) {
			g.setColor(Color.red);
			for (int i = 0; i < P.path.size(); i++) {
				Robot r = P.robots.get(P.currentRobot).copyRobot(P.path.get(i));
				r.show(g, false);
			}
			
		}

		g.setColor(Color.black);
		String s = new String("x:" + mP.x + ",y:" + mP.y);
		g.drawString(s, 0, 10);
	}

	class Animation implements Runnable {
		boolean isPaintAllPath;
		public Animation() {
			clip = 0;
			isPaintAllPath = paintAllPath;
		}

		public void run() {
			try {
				paintLast = false;
				paintAllPath = false;
				animation = true;
				for (clip = 0; clip < P.path.size(); clip++) {
					repaint();
					P.CP.msg.setText("<Animation>\ncurrent step: "+(clip+1));
					Thread.sleep(20);
				}
				animation = false;
				paintLast = true;
				paintAllPath = isPaintAllPath;
				repaint();
				P.CP.PP.jbtAnimation.setText("Animation");
				P.CP.PP.T = null;
				P.CP.setAllEnabled(true);
			} catch (InterruptedException ex) {
			}
		}
	}
}